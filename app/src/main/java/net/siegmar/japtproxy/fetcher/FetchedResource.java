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

import java.io.IOException;
import java.io.InputStream;

/**
 * The FetchedResource interface defines methods used to get information
 * about the fetched resource.
 *
 * @author Oliver Siegmar
 */
public interface FetchedResource {

    /**
     * @return Returns if the resource has been modified.
     */
    boolean isModified();

    /**
     * @return Returns the contentType (aka mime type).
     */
    String getContentType();

    /**
     * @return Returns the timestamp of the last modification.
     */
    long getLastModified();

    /**
     * @return Returns the length of the content.
     */
    long getContentLength();

    /**
     * @return Returns the {@code InputStream}.
     * @throws IOException is thrown if the operation fails.
     */
    InputStream getInputStream() throws IOException;

    /**
     * Closes open fetcher connection.
     */
    void close() throws IOException;

}
