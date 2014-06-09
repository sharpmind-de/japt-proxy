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

import java.io.IOException;
import java.net.URL;

/**
 * The Fetcher interface defines methods used to fetch external
 * resources.
 *
 * @author Oliver Siegmar
 */
public interface Fetcher {

    /**
     * Fetches an external resource.
     *
     * @param targetResource    the resource to fetch.
     * @param lastModified      the lastModified timestamp of the local object.
     * @param originalUserAgent the requesting user agent
     * @return the fetched resource.
     * @throws IOException                                                  is thrown if the fetch fails.
     * @throws net.siegmar.japtproxy.exception.ResourceUnavailableException is thrown if the resource was not
     *                                                                      found.
     */
    FetchedResource fetch(URL targetResource, long lastModified, String originalUserAgent)
        throws IOException, ResourceUnavailableException;

}
