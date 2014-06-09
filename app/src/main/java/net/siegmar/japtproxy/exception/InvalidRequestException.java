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
package net.siegmar.japtproxy.exception;

/**
 * Exception that is thrown on invalid requests.
 *
 * @author Oliver Siegmar
 */
public class InvalidRequestException extends HandlingException {

    /**
     * The serialization identifier.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor for InvalidRequestException.
     *
     * @param message the detail message.
     */
    public InvalidRequestException(final String message) {
        super(message);
    }

}
