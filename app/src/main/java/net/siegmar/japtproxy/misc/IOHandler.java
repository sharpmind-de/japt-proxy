/**
 * Japt-Proxy: The JAVA(TM) based APT-Proxy
 *
 * Copyright (C) 2006-2008  Oliver Siegmar <oliver@siegmar.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.siegmar.japtproxy.misc;

import net.siegmar.japtproxy.exception.InitializationException;
import net.siegmar.japtproxy.exception.ResourceUnavailableException;
import net.siegmar.japtproxy.fetcher.FetchedResource;
import net.siegmar.japtproxy.fetcher.Fetcher;
import net.siegmar.japtproxy.fetcher.FetcherPool;
import net.siegmar.japtproxy.poolobject.PoolObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.TeeOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * The IOHandler utility class is responsible for the IO
 * operation between fetchers and pools.
 *
 * @author Oliver Siegmar
 */
public class IOHandler {

    /**
     * The logger instance.
     */
    private static final Logger LOG = LoggerFactory.getLogger(IOHandler.class);

    /**
     * Map that contains the timestamp of the last check per resource.
     */
    private final Map<String, Date> resourcesLastCheckedMap = new HashMap<>();

    /**
     * Duration between new version check of mutable files.
     *
     * Default 1 minute.
     */
    private final int cacheDuration = 60_000;

    /**
     * The FetcherFactory instance.
     */
    private FetcherPool fetcherPool;

    @Required
    public void setFetcherPool(final FetcherPool fetcherPool) {
        this.fetcherPool = fetcherPool;
    }

    /**
     * Checks if a new version check is required for a specific resource.
     *
     * @param resourceName the resource name to check if a version check is
     *                     required for
     * @return if a new version check is required
     */
    protected boolean isNewVersionCheckRequired(final PoolObject poolObject, final String resourceName) {
        // If the resource is known to be immutable,
        // no checks are required at all
        if (poolObject.getRepoPackage() != null &&
            poolObject.getRepoPackage().isImmutable()) {
            LOG.debug("Resource '{}' is known to be immutable. No version check required.", resourceName);
            return false;
        }

        synchronized (resourcesLastCheckedMap) {
            final Date now = new Date();

            final Date d = resourcesLastCheckedMap.get(resourceName);

            // If the map doesn't contain an entry, add one
            if (d == null) {
                resourcesLastCheckedMap.put(resourceName, now);
                return true;
            }

            // If the map contains an entry, check if it is exceeded
            if (cacheDuration > now.getTime() - d.getTime()) {
                return false;
            }

            resourcesLastCheckedMap.remove(resourceName);
            return true;
        }
    }

    /**
     * Sends a locally stored pool object to the client. This method will
     * send HTTP status code 304 (not modified) if the client sent a
     * 'If-Modified-Since' header and the pool object wasn't modified since
     * that date.
     *
     * @param poolObject           the pool object to sent
     * @param requestModifiedSince the "If-Modified-Since" header
     * @param res                  the HttpServletResponse object
     * @throws IOException is thrown if a problem occured while sending data
     */
    protected void sendLocalFile(final PoolObject poolObject,
                                 final long requestModifiedSince,
                                 final HttpServletResponse res)
        throws IOException {
        final long poolModification = poolObject.getLastModified();

        if (requestModifiedSince != -1 &&
            poolModification <= requestModifiedSince) {
            LOG.debug("Requested resource wasn't modified since last request. " +
                "Returning status code 304 - not modified");
            res.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            return;
        }

        res.setContentType(poolObject.getContentType());
        res.setContentLength((int) poolObject.getSize());
        res.setDateHeader(HttpHeaderConstants.LAST_MODIFIED, poolObject.getLastModified());

        InputStream is = null;
        final OutputStream sendOs = res.getOutputStream();

        try {
            LOG.info("Sending locally cached object '{}'",
                poolObject.getName());

            is = poolObject.getInputStream();
            IOUtils.copy(is, sendOs);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    /**
     * This method is responsible for fetching remote data (if needed) and
     * sending the data (locally stored, or remotely fetched) to the client.
     *
     * @param requestedData  the requested data
     * @param poolObject     the pool object
     * @param targetResource the remote resource link
     * @param res            the HttpServletResponse object
     * @return true if the file was sent from cache, false otherwise
     * @throws IOException is thrown if a problem occured while sending data
     * @throws net.siegmar.japtproxy.exception.ResourceUnavailableException is thrown if the resource was not found
     */
    public boolean sendAndSave(final RequestedData requestedData,
                               final PoolObject poolObject,
                               final URL targetResource,
                               final HttpServletResponse res)
        throws IOException, ResourceUnavailableException, InitializationException {
        final String lockIdentifier = requestedData.getRequestedResource();
        final ReadWriteLock lock = ResourceLock.obtainLocker(lockIdentifier);
        final Lock readLock = lock.readLock();

        LockStatus lockStatus;

        readLock.lock();
        lockStatus = LockStatus.READ;

        LOG.debug("Obtained readLock for '{}'", lockIdentifier);

        final long poolModification = poolObject.getLastModified();

        FetchedResource fetchedResource = null;
        OutputStream saveOs = null;
        InputStream is = null;

        try {
            if (poolModification != 0) {
                if (!isNewVersionCheckRequired(poolObject,
                    requestedData.getRequestedResource())) {
                    LOG.debug("Local object exists and no need to do a version check - sending local object");
                    sendLocalFile(poolObject, requestedData.getRequestModifiedSince(), res);
                    return true;
                }

                LOG.debug("Local object exists but new version check is required");
            } else {
                LOG.debug("No local object exists - requesting remote host");
            }

            // Get a fetcher (http, ftp) for the current targetResource
            final Fetcher fetcher = fetcherPool.getInstance(targetResource);

            if (fetcher == null) {
                throw new InitializationException("No fetcher found for resource '" + targetResource + "'");
            }

            fetchedResource = fetcher.fetch(targetResource, poolModification,
                requestedData.getUserAgent());

            final String contentType = fetchedResource.getContentType();
            final long remoteModification = fetchedResource.getLastModified();
            final long contentLength = fetchedResource.getContentLength();

            if (remoteModification != 0 &&
                poolModification > remoteModification) {
                LOG.warn("Remote object is older than local pool object " +
                        "(Remote timestamp: {} - Local timestamp: {}). " +
                        "Object won't get updated! Check this manually!",
                    Util.getSimpleDateFromTimestamp(remoteModification),
                    Util.getSimpleDateFromTimestamp(poolModification));
            }

            setHeader(res, fetchedResource);

            if (!fetchedResource.isModified()) {
                LOG.debug("Remote resource has no new version - sending local object");
                sendLocalFile(poolObject, requestedData.getRequestModifiedSince(), res);
                return true;
            }

            if (LOG.isDebugEnabled()) {
                if (poolModification != 0) {
                    // Pool file exists, but it is out of date
                    LOG.debug("Newer version found (old Last-Modified: {}) - Request '{}', Last-Modified: {}, " +
                            "Content-Type: {}, Content-Length: {}", Util.getSimpleDateFromTimestamp(poolModification),
                        targetResource, Util.getSimpleDateFromTimestamp(remoteModification), contentType,
                        contentLength);
                } else {
                    // No pool file exists
                    LOG.debug("Request '{}', Last-Modified: {}, Content-Type: {}, Content-Length: {}", targetResource,
                        Util.getSimpleDateFromTimestamp(remoteModification), contentType, contentLength);
                }
            }

            readLock.unlock();
            lock.writeLock().lock();
            lockStatus = LockStatus.WRITE;

            LOG.debug("Obtained writeLock for '{}'", lockIdentifier);

            is = fetchedResource.getInputStream();

            LOG.info("Sending remote object '{}'", poolObject.getName());

            saveOs = new TeeOutputStream(poolObject.getOutputStream(), res.getOutputStream());
            final long bytesCopied = IOUtils.copyLarge(is, saveOs);

            LOG.debug("Data sent to file and client");

            poolObject.setLastModified(remoteModification);

            if (contentLength != 0 && bytesCopied != contentLength) {
                throw new IOException(String.format("Received file has invalid file size - " +
                    "only %d of %d were downloaded", bytesCopied, contentLength));
            }

            poolObject.store();

            return false;
        } catch (final IOException e) {
            // Remove pool file if it was created by this thread
            if (poolModification == 0) {
                poolObject.remove();
            }

            throw e;
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(saveOs);

            if (fetchedResource != null) {
                fetchedResource.close();
            }

            if (lockStatus == LockStatus.WRITE) {
                LOG.debug("Released writeLock for '{}'", lockIdentifier);
                lock.writeLock().unlock();
            } else {
                LOG.debug("Released readLock for '{}'", lockIdentifier);
                readLock.unlock();
            }
            ResourceLock.releaseLocker(lockIdentifier);
        }
    }

    protected void setHeader(final HttpServletResponse res, final FetchedResource fetchedResource) {
        final String contentType = fetchedResource.getContentType();
        final long contentLength = fetchedResource.getContentLength();
        final long remoteModification = fetchedResource.getLastModified();

        if (contentType != null) {
            res.setContentType(contentType);
        }

        if (contentLength != 0) {
            res.setContentLength((int) contentLength);
        }

        if (remoteModification != 0) {
            res.setDateHeader(HttpHeaderConstants.LAST_MODIFIED, remoteModification);
        }
    }

    private enum LockStatus {

        READ,
        WRITE

    }

}
