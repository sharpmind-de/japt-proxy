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
package net.siegmar.japtproxy.fetcher;

import net.siegmar.japtproxy.misc.Util;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * The FetchedResourceFtp provides access to the via FTP fetched resource.
 *
 * @author Oliver Siegmar
 */
public class FetchedResourceFtp implements FetchedResource {

    /**
     * The logger instance.
     */
    private static final Logger LOG = LoggerFactory.getLogger(FetchedResourceFtp.class);

    /**
     * The FTPClient instance.
     */
    private final FTPClient ftpClient;

    /**
     * The fetched resource.
     */
    private final FTPFile file;

    /**
     * The resource name to fetch.
     */
    private String resourceName;

    /**
     * If the resource has been modified.
     */
    private boolean modified;

    /**
     * Constructor.
     *
     * @param ftpClient the FTPClient to use.
     * @param file      the referenced file.
     */
    public FetchedResourceFtp(final FTPClient ftpClient, final FTPFile file) {
        if (ftpClient == null) {
            throw new IllegalArgumentException("ftpClient must not be null");
        }
        if (file == null) {
            throw new IllegalArgumentException("file must not be null");
        }

        this.ftpClient = ftpClient;
        this.file = file;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isModified() {
        return modified;
    }

    /**
     * Sets the modified field.
     *
     * @param modified the modified field.
     */
    public void setModified(final boolean modified) {
        this.modified = modified;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getContentType() {
        return Util.getMimetype(resourceName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getLastModified() {
        return file.getTimestamp().getTimeInMillis();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getContentLength() {
        return file.getSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getInputStream()
        throws IOException {
        return ftpClient.retrieveFileStream(resourceName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        if (!ftpClient.isConnected()) {
            return;
        }

        try {
            ftpClient.disconnect();
        } catch (final IOException e) {
            LOG.error("IOException occured", e);
        }
    }

}
