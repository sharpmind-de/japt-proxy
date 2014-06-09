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
package net.siegmar.japtproxy.poolobject;

import net.siegmar.japtproxy.packages.RepoPackage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Oliver Siegmar
 */
public interface PoolObject {

    /**
     * Get the timestamp of the final resource.
     *
     * @return the timestamp of the final resource.
     */
    long getLastModified();

    /**
     * Set a timestamp on the temp resource.
     *
     * @param lastModified a timestamp to set on the temp resource.
     */
    void setLastModified(long lastModified) throws IOException;

    /**
     * Get the size of the final resource.
     *
     * @return the size of the final resource.
     */
    long getSize();

    /**
     * Get the content type of the final resource.
     *
     * @return the content type of the final resource.
     */
    String getContentType();

    /**
     * Get the InputStream of the final resource.
     *
     * @return the InputStream of the final resource.
     * @throws IOException is thrown if this operation fails.
     */
    InputStream getInputStream() throws IOException;

    /**
     * Get the OutputStream of the temp resource.
     *
     * @return the OutputStream of the temp resource.
     * @throws IOException is thrown if this operation fails.
     */
    OutputStream getOutputStream() throws IOException;

    /**
     * Saves the temp resource to the final one.
     */
    void store() throws IOException;

    /**
     * Removes temp and final resources.
     */
    void remove();

    /**
     * Get the name of the pool object.
     *
     * @return the name of the pool object.
     */
    String getName();

    RepoPackage getRepoPackage();

}
