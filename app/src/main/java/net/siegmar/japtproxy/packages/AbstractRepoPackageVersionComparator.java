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

import org.apache.commons.lang3.CharUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;

/**
 * The VersionComparator is responsible for comparing package
 * versions. This is required for sorting a list of packages
 * from the same program with different versions.
 * <p/>
 * The algorithm used in this class was borrowed from {@code vercmp.c}
 * in {@code libdpkg} which is written by Ian Jackson.
 *
 * @author Oliver Siegmar
 */
public abstract class AbstractRepoPackageVersionComparator implements Comparator<RepoPackage> {

    /**
     * The logger instance.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(AbstractRepoPackageVersionComparator.class);

    /**
     * Non ASCII offset constant.
     */
    protected static final int NON_ASCII_OFFSET = 256;

    /**
     * Returns the order for a single character.
     *
     * @param ca  the character array
     * @param pos the position in the character array
     * @return the order for the given character
     */
    protected abstract int order(final char[] ca, final int pos);

    /**
     * Compares two version strings and returns and {@code int} that
     * specifies the order.
     *
     * @param s1 version string 1
     * @param s2 version string 2
     * @return the order
     */
    protected int strCompare(final String s1, final String s2) {
        final char[] c1 = s1 == null ? new char[0] : s1.toCharArray();
        final char[] c2 = s2 == null ? new char[0] : s2.toCharArray();

        int p1 = 0;
        int p2 = 0;

        while (p1 < c1.length || p2 < c2.length) {
            int firstDiff = 0;

            // compare leading non-digits
            for (; p1 < c1.length && !CharUtils.isAsciiNumeric(c1[p1]) ||
                p2 < c2.length && !CharUtils.isAsciiNumeric(c2[p2]); p1++, p2++) {
                final int diff = order(c1, p1) - order(c2, p2);

                if (diff != 0) {
                    return diff;
                }
            }

            // strip 0's
            while (p1 < c1.length && c1[p1] == '0') {
                p1++;
            }
            while (p2 < c2.length && c2[p2] == '0') {
                p2++;
            }

            // compare digits
            for (; p1 < c1.length && CharUtils.isAsciiNumeric(c1[p1]) &&
                p2 < c2.length && CharUtils.isAsciiNumeric(c2[p2]); p1++, p2++) {
                if (firstDiff == 0) {
                    firstDiff = c1[p1] - c2[p2];
                }
            }

            if (p1 < c1.length && CharUtils.isAsciiNumeric(c1[p1])) {
                return 1;
            }

            if (p2 < c2.length && CharUtils.isAsciiNumeric(c2[p2])) {
                return -1;
            }

            if (firstDiff != 0) {
                return firstDiff;
            }
        }

        return 0;
    }

    /**
     * Compares two packages based on the version and revision field.
     *
     * @param p1 package1
     * @param p2 package2
     * @return {@code 0} if both packages have the same version (should
     * not happen), {@code 1} if {@code p1} is more recent
     * than {@code p2}, otherwise {@code -1}
     */
    @Override
    public int compare(final RepoPackage p1, final RepoPackage p2) {
        LOG.debug("Comparing {} with {}", p1, p2);

        if (!p1.getBasename().equals(p2.getBasename())) {
            throw new IllegalStateException(String.format("Basenames are different - '%s' / '%s'", p1, p2));
        }

        if (!p1.getArch().equals(p2.getArch())) {
            throw new IllegalStateException(String.format("Architectures are different - '%s' / '%s'", p1, p2));
        }

        final int ret = strCompare(p1.getVersion(), p2.getVersion());
        return ret != 0 ? ret : strCompare(p1.getRevision(), p2.getRevision());
    }

}
