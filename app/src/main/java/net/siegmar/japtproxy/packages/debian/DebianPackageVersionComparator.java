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
package net.siegmar.japtproxy.packages.debian;

import net.siegmar.japtproxy.packages.AbstractRepoPackageVersionComparator;
import org.apache.commons.lang3.CharUtils;

/**
 * The VersionComparator is responsible for comparing debian package versions.
 * This is required for sorting a list of debian packages from the same program
 * with different versions.
 *
 * @author Oliver Siegmar
 */
public class DebianPackageVersionComparator extends AbstractRepoPackageVersionComparator {

    /**
     * Returns the order for a single character. The order is ~, then digits,
     * then alpha, then non-ascii. If {@code pos} &gt;=
     * {@code ca.length}, then the order is the same as for digits.
     *
     * @param ca  the character array
     * @param pos the position in the character array
     * @return the order for the given character
     */
    @Override
    protected int order(final char[] ca, final int pos) {
        if (pos >= ca.length) {
            return 0;
        }

        final char c = ca[pos];

        return c == '~' ? -1 : CharUtils.isAsciiNumeric(c) ? 0 : CharUtils.isAsciiAlpha(c) ? c : c + NON_ASCII_OFFSET;
    }

}
