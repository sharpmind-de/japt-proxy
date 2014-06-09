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

/**
 * Class that holds static String instances of HTTP header fields.
 *
 * @author Oliver Siegmar
 */
public final class HttpHeaderConstants {

    /**
     * The last-modified header name.
     */
    public static final String LAST_MODIFIED = "Last-Modified";

    /**
     * The if-modified-since header name.
     */
    public static final String IF_MODIFIED_SINCE = "If-Modified-Since";

    /**
     * The user-agent header name.
     */
    public static final String USER_AGENT = "User-Agent";

    /**
     * The content-type header name.
     */
    public static final String CONTENT_TYPE = "Content-Type";

    private HttpHeaderConstants() {
        // No public constructor for utility classes.
    }

}
