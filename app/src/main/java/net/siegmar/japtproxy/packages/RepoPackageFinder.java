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
package net.siegmar.japtproxy.packages;

import net.siegmar.japtproxy.exception.HandlingException;
import net.siegmar.japtproxy.exception.InitializationException;
import net.siegmar.japtproxy.exception.ResourceUnavailableException;
import net.siegmar.japtproxy.misc.Backend;
import net.siegmar.japtproxy.misc.Configuration;
import net.siegmar.japtproxy.misc.IOHandler;
import net.siegmar.japtproxy.misc.RequestedData;
import net.siegmar.japtproxy.packagepool.PackagePool;
import net.siegmar.japtproxy.poolobject.PoolObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

/**
 * @author Oliver Siegmar
 */
public class RepoPackageFinder {

    /**
     * The logger instance.
     */
    private static final Logger LOG = LoggerFactory.getLogger(RepoPackageFinder.class);

    /**
     * The Japt-Proxy configuration.
     */
    private Configuration configuration;

    private PackagePool<PoolObject> packagePool;

    private IOHandler ioHandler;

    @Required
    public void setConfiguration(final Configuration configuration) {
        this.configuration = configuration;
    }

    @Required
    public void setPackagePool(final PackagePool<PoolObject> packagePool) {
        this.packagePool = packagePool;
    }

    @Required
    public void setIoHandler(final IOHandler ioHandler) {
        this.ioHandler = ioHandler;
    }

    public void findSendSave(final RequestedData requestedData, final HttpServletResponse res)
        throws HandlingException, IOException {
        // repo specific prevalidation
        final String requestedTarget = requestedData.getRequestedTarget();

        final String requestedBackend = requestedData.getRequestedBackend();
        final Backend backend = configuration.getBackend(requestedData);

        final PoolObject poolObject = packagePool.getPoolObject(backend, requestedTarget);

        // Iterate over the resource locations for the requested resource
        // (and the requested backend)
        // If one resource location fails, try the next one (if exists).
        // This fails if the output buffer has already (auto-)flushed.
        for (final Iterator<URL> it = backend.getUrls().iterator(); it.hasNext();) {
            final URL baseURL = it.next();

            LOG.debug("Using backend '{}'", baseURL);

            final URL targetResource = new URL(baseURL + requestedTarget);

            if (handleBackend(requestedData, targetResource, poolObject, res)) {
                break;
            }

            if (!it.hasNext()) {
                throw new ResourceUnavailableException("No backend host provided the requested resource.");
            }

            LOG.info("Backend host '{}' failed, trying next one", baseURL);
        }

        LOG.info("Successfully handled request for '{}'", requestedData.getRequestedResource());
    }

    /**
     * Fetches an object from a specific backend.
     *
     * @param requestedData  the requested data.
     * @param targetResource the target resource.
     * @param poolObject     the pool object.
     * @param res            the HttpServletResponse object
     * @return true if the requested object was send successfully.
     * @throws IOException is thrown if an I/O error occurs.
     */
    protected boolean handleBackend(final RequestedData requestedData,
                                    final URL targetResource, final PoolObject poolObject,
                                    final HttpServletResponse res)
        throws IOException {
        try {
            // Send and save data
            final boolean locallyCached = ioHandler.sendAndSave(requestedData, poolObject, targetResource, res);

            // If a new version was downloaded and if the configuration
            // specifies a max-version parameter, remove old versions
            if (!locallyCached && configuration.getMaxVersions() != null) {
                packagePool.removeOldPackages(poolObject);
            }

            return true;
        } catch (final ResourceUnavailableException e) {
            LOG.info("Resource '{}' not found", targetResource);
        } catch (final IOException e) {
            LOG.warn(String.format("IOException while getting data from '%s'", targetResource), e);

            if (res.isCommitted()) {
                LOG.info("IOException occured but headers are already sent. Have to rethrow exception", e);
                throw e;
            }
            res.reset();
        } catch (final InitializationException e) {
            LOG.warn("Resource '{}' could not be fetched", targetResource, e);
        }

        return false;
    }

}
