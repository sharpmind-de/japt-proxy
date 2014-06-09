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

import net.siegmar.japtproxy.misc.HttpHeaderConstants;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.utils.DateUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * The FetchedResourceHttp provides access to the via HTTP fetched resource.
 *
 * @author Oliver Siegmar
 */
public class FetchedResourceHttp implements FetchedResource {

    /**
     * The httpGet instance.
     */
    private final CloseableHttpResponse httpGet;

    /**
     * If the resource has been modified.
     */
    private boolean modified;

    /**
     * Constructor.
     *
     * @param httpGet the GetMethod instance.
     */
    public FetchedResourceHttp(final CloseableHttpResponse httpGet) {
        if (httpGet == null) {
            throw new IllegalArgumentException("httpGet must not be null");
        }
        this.httpGet = httpGet;
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
        final Header header = httpGet.getFirstHeader(HttpHeaderConstants.CONTENT_TYPE);
        return header != null ? header.getValue() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getLastModified() {
        final Header lastModifiedHeader = httpGet.getFirstHeader(HttpHeaderConstants.LAST_MODIFIED);
        if (lastModifiedHeader == null) {
            return 0;
        }

        final Date parsedDate = DateUtils.parseDate(lastModifiedHeader.getValue());
        return parsedDate != null ? parsedDate.getTime() : 0L;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getContentLength() {
        return httpGet.getEntity().getContentLength();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getInputStream()
        throws IOException {
        return httpGet.getEntity().getContent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        httpGet.close();
    }

}
