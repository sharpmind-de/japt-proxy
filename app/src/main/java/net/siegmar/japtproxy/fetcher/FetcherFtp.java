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

import net.siegmar.japtproxy.exception.ResourceUnavailableException;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.io.IOException;
import java.net.URL;

/**
 * The FetcherFtp is responsible for fetching files from ftp resources.
 *
 * @author Oliver Siegmar
 */
public class FetcherFtp implements Fetcher {

    /**
     * The logger instance.
     */
    private static final Logger LOG = LoggerFactory.getLogger(FetcherFtp.class);

    private int socketTimeout;
    private int dataTimeout;

    @Required
    public void setSocketTimeout(final int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    @Required
    public void setDataTimeout(final int dataTimeout) {
        this.dataTimeout = dataTimeout;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FetchedResourceFtp fetch(final URL targetResource, final long lastModified, final String originalUserAgent)
        throws IOException, ResourceUnavailableException {

        final FTPClient ftpClient = new FTPClient();
        ftpClient.setSoTimeout(socketTimeout);
        ftpClient.setDataTimeout(dataTimeout);

        try {
            final String host = targetResource.getHost();
            final String resourceName = targetResource.getPath();

            LOG.debug("Configured FetcherFtp: Host '{}', Resource '{}'", host, resourceName);

            ftpClient.connect(host);
            ftpClient.enterLocalPassiveMode();

            if (!ftpClient.login("anonymous", "japt-proxy")) {
                throw new IOException("Can't login to FTP server");
            }

            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            final FTPFile[] files = ftpClient.listFiles(resourceName);

            if (files.length == 0) {
                throw new ResourceUnavailableException("Resource '" + resourceName + "' not found");
            }

            if (files.length > 1) {
                throw new IOException("Multiple files found");
            }

            final FTPFile file = files[0];

            final FetchedResourceFtp fetchedResourceFtp = new FetchedResourceFtp(ftpClient, file);
            fetchedResourceFtp.setModified(
                lastModified == 0 || lastModified < file.getTimestamp().getTimeInMillis()
            );

            return fetchedResourceFtp;
        } catch (final IOException e) {
            // Closing only in case of an exception - otherwise closed by FetchedResourceFtp
            if (ftpClient.isConnected()) {
                ftpClient.disconnect();
            }

            throw e;
        }
    }

}
