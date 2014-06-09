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
import net.siegmar.japtproxy.misc.HttpHeaderConstants;
import net.siegmar.japtproxy.misc.Util;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;

/**
 * The FetcherHttp is responsible for fetching files from http resources.
 *
 * @author Oliver Siegmar
 */
public class FetcherHttp implements Fetcher {

    /**
     * The logger instance.
     */
    private static final Logger LOG = LoggerFactory.getLogger(FetcherHttp.class);

    /**
     * The httpClient instance.
     */
    private CloseableHttpClient httpClient;

    @Required
    public void setHttpClient(final CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FetchedResourceHttp fetch(final URL targetResource, final long lastModified, final String originalUserAgent)
        throws IOException, ResourceUnavailableException {

        final HttpGet httpGet = new HttpGet(targetResource.toExternalForm());

        httpGet.addHeader(HttpHeaderConstants.USER_AGENT,
            StringUtils.trim(
                StringUtils.defaultString(originalUserAgent) + " " + Util.USER_AGENT
            )
        );

        if (lastModified != 0) {
            final String lastModifiedSince = Util.getRfc822DateFromTimestamp(lastModified);
            LOG.debug("Setting If-Modified-Since: {}", lastModifiedSince);
            httpGet.setHeader(HttpHeaderConstants.IF_MODIFIED_SINCE, lastModifiedSince);
        }

        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpGet);

            if (LOG.isDebugEnabled()) {
                logResponseHeader(httpResponse.getAllHeaders());
            }

            final int retCode = httpResponse.getStatusLine().getStatusCode();
            if (retCode == HttpServletResponse.SC_NOT_FOUND) {
                httpGet.releaseConnection();
                throw new ResourceUnavailableException("Resource '" + targetResource + " not found");
            }

            if (retCode != HttpServletResponse.SC_OK &&
                retCode != HttpServletResponse.SC_NOT_MODIFIED) {
                throw new IOException("Invalid status code returned: " + httpResponse.getStatusLine());
            }

            final FetchedResourceHttp fetchedResourceHttp = new FetchedResourceHttp(httpResponse);
            fetchedResourceHttp.setModified(
                lastModified == 0 || retCode != HttpServletResponse.SC_NOT_MODIFIED
            );

            if (LOG.isDebugEnabled()) {
                final long fetchedTimestamp = fetchedResourceHttp.getLastModified();
                if (fetchedTimestamp != 0) {
                    LOG.debug("Response status code: {}, Last modified: {}",
                        retCode, Util.getSimpleDateFromTimestamp(fetchedTimestamp));
                } else {
                    LOG.debug("Response status code: {}", retCode);
                }
            }

            return fetchedResourceHttp;
        } catch (final IOException e) {
            // Closing only in case of an exception - otherwise closed by FetchedResourceHttp
            if (httpResponse != null) {
                httpResponse.close();
            }

            throw e;
        }
    }

    private void logResponseHeader(final Header[] responseHeaders) {
        final StringBuilder sb = new StringBuilder();
        int i = 0;
        for (final Header header : responseHeaders) {
            sb.append(header.getName());
            sb.append('=');
            sb.append(header.getValue());
            if (++i < responseHeaders.length) {
                sb.append(',');
            }
        }
        LOG.debug("Response headers: {}", sb);
    }

}
