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

/**
 * @author Oliver Siegmar
 */
public interface RepoPackage {

    /**
     * Gets the arch part of this {@code repo package}.
     *
     * @return the arch part of this {@code repo package}.
     */
    String getArch();

    /**
     * Gets the basename part of this {@code repo package}.
     *
     * @return the basename part of this {@code repo package}.
     */
    String getBasename();

    /**
     * Gets the revision part of this {@code repo package}.
     *
     * @return the revision part of this {@code repo package}.
     */
    String getRevision();

    /**
     * Gets the version part of this {@code repo package}.
     *
     * @return the version part of this {@code repo package}.
     */
    String getVersion();

    /**
     * Gets the extension part of this {@code repo package}.
     *
     * @return the extension part of this {@code repo package}.
     */
    String getExtension();

    boolean isImmutable();

}
