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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

@Test
@ContextConfiguration(locations = { "classpath:test-master.xml" })
public class RpmPackageTest extends AbstractTestNGSpringContextTests {

    @Autowired
    @Qualifier("rpmRepoPackageFactory")
    private RepoPackageBuilder repoPackageBuilder;

    @Test
    public void testPackage() throws Exception {
        RepoPackage rpmPackage;

        rpmPackage = repoPackageBuilder.newPackage("qemu-0.9.0.cvs-35.i586.rpm");
        assertEquals(rpmPackage.getBasename(), "qemu");
        assertEquals(rpmPackage.getVersion(), "0.9.0.cvs");
        assertEquals(rpmPackage.getRevision(), "35");
        assertEquals(rpmPackage.getArch(), "i586");
        assertEquals(rpmPackage.getExtension(), "rpm");
        assertTrue(rpmPackage.isImmutable());

        rpmPackage = repoPackageBuilder.newPackage("qemu-0.9.0.cvs.i586.rpm");
        assertEquals(rpmPackage.getBasename(), "qemu");
        assertEquals(rpmPackage.getVersion(), "0.9.0.cvs");
        assertNull(rpmPackage.getRevision());
        assertEquals(rpmPackage.getArch(), "i586");
        assertEquals(rpmPackage.getExtension(), "rpm");
        assertTrue(rpmPackage.isImmutable());

        rpmPackage = repoPackageBuilder.newPackage("kdebase3-3.5.9-65.1_65.2.i586.delta.rpm");
        assertEquals(rpmPackage.getBasename(), "kdebase3");
        assertEquals(rpmPackage.getVersion(), "3.5.9");
        assertEquals(rpmPackage.getRevision(), "65.1_65.2");
        assertEquals(rpmPackage.getArch(), "i586");
        assertEquals(rpmPackage.getExtension(), "delta.rpm");
        assertTrue(rpmPackage.isImmutable());
    }

}
