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
import org.springframework.beans.factory.annotation.Required;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Oliver Siegmar
 */
public final class RepoPackageBuilder {

    private Pattern pattern;

    @Required
    public void setPattern(final Pattern pattern) {
        this.pattern = pattern;
    }

    /**
     * Constructs a {@code GenericRepoPackage} based on the given filename.
     *
     * @param filename the filename to construct a repo package object for.
     */
    public RepoPackage newPackage(final String filename) {
        final Matcher matcher = pattern.matcher(filename);

        if (!matcher.matches()) {
            throw new InvalidRepoPackageNameException("Package name '" + filename + "' has an illegal format");
        }

        final String basename = matcher.group(1);
        final String version = matcher.group(2);
        final String revision = matcher.group(3);
        final String arch = matcher.group(4);
        final String extension = matcher.group(5);

        return new GenericRepoPackage(basename, version, revision, arch, extension, true);
    }

    public RepoPackage[] newPackages(final String... filenames) {
        final RepoPackage[] packages = new RepoPackage[filenames.length];

        int i = 0;
        for (final String filename : filenames) {
            packages[i++] = newPackage(filename);
        }

        return packages;
    }

}
