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
package net.siegmar.japtproxy.packagepool;

import net.siegmar.japtproxy.packages.RepoPackage;
import net.siegmar.japtproxy.packages.RepoPackageBuilder;
import net.siegmar.japtproxy.packages.debian.DebianPackageVersionComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.testng.Assert.assertEquals;

@Test
@ContextConfiguration(locations = { "classpath:test-master.xml" })
public class VersionComparatorTest extends AbstractTestNGSpringContextTests {

    @Autowired
    @Qualifier("debianRepoPackageFactory")
    private RepoPackageBuilder repoPackageBuilder;

    @Autowired
    private DebianPackageVersionComparator debianPackageVersionComparator;

    @Test
    public void testComparator() {

        final String[] orderedFiles = {
            "dummy_1.0.9-2_i386.deb",
            "dummy_1.0.9-2a_i386.deb",
            "dummy_1.0.9-2b_i386.deb",
            "dummy_1.0.009-3_i386.deb",
            "dummy_1.0.9-5_i386.deb",
            "dummy_1.0.9-5a_i386.deb",
            "dummy_1.0.9-5c_i386.deb",
            "dummy_1.0.10-1_i386.deb",
            "dummy_1.4pre.20050518-0.3_i386.deb",
            "dummy_1.4pre.20050518-0.4_i386.deb",
            "dummy_1.4pre.20050519_i386.deb",
            "dummy_1.5.dfsg+1.5.0.6-4_i386.deb",
            "dummy_1.5.dfsg+1.5.0.7-1_i386.deb",
            "dummy_2.2.4~pre4-1_i386.deb",
            "dummy_2.2.4-1_i386.deb",
            "dummy_2.3.001_i386.deb",
            "dummy_2.3.005_i386.deb",
            "dummy_2.3.100_i386.deb",
            "dummy_2.3.101_i386.deb",
            "dummy_3.0.1a-3_i386.deb",
            "dummy_3.0.1a-4_i386.deb",
        };
        final RepoPackage[] orderedPackages = repoPackageBuilder.newPackages(orderedFiles);

        final String[] files = {
            "dummy_3.0.1a-4_i386.deb",
            "dummy_1.0.10-1_i386.deb",
            "dummy_1.0.9-2b_i386.deb",
            "dummy_1.4pre.20050518-0.3_i386.deb",
            "dummy_3.0.1a-3_i386.deb",
            "dummy_1.0.9-2_i386.deb",
            "dummy_2.2.4~pre4-1_i386.deb",
            "dummy_1.5.dfsg+1.5.0.6-4_i386.deb",
            "dummy_2.3.100_i386.deb",
            "dummy_1.0.009-3_i386.deb",
            "dummy_2.3.001_i386.deb",
            "dummy_1.0.9-2a_i386.deb",
            "dummy_2.2.4-1_i386.deb",
            "dummy_1.5.dfsg+1.5.0.7-1_i386.deb",
            "dummy_1.4pre.20050518-0.4_i386.deb",
            "dummy_1.0.9-5_i386.deb",
            "dummy_2.3.101_i386.deb",
            "dummy_2.3.005_i386.deb",
            "dummy_1.0.9-5c_i386.deb",
            "dummy_1.0.9-5a_i386.deb",
            "dummy_1.4pre.20050519_i386.deb"
        };
        final RepoPackage[] unorderedPackages = repoPackageBuilder.newPackages(files);

        Collections.sort(Arrays.asList(unorderedPackages), debianPackageVersionComparator);

        assertEquals(unorderedPackages, orderedPackages);
    }

}
