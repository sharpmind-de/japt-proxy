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

import net.siegmar.japtproxy.exception.InvalidRepoPackageNameException;
import org.apache.commons.io.filefilter.AbstractFileFilter;

import java.io.File;

/**
 * @author Oliver Siegmar
 */
public class RepoPackageFilter extends AbstractFileFilter {

    /**
     * The repo package this filter shoud use.
     */
    private final RepoPackage repoPackage;
    private final RepoPackageBuilder repoPackageBuilder;

    public RepoPackageFilter(final RepoPackage repoPackage, final RepoPackageBuilder repoPackageBuilder) {
        this.repoPackage = repoPackage;
        this.repoPackageBuilder = repoPackageBuilder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accept(final File dir, final String filename) {
        try {
            final RepoPackage dp = repoPackageBuilder.newPackage(filename);

            return
                repoPackage.getBasename().equals(dp.getBasename()) &&
                    repoPackage.getArch().equals(dp.getArch());
        } catch (final InvalidRepoPackageNameException e) {
            return false;
        }
    }

}
