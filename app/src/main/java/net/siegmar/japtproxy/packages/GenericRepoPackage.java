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

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * This class builds and holds all parts of a repo package filename separated in
 * their specified parts.
 *
 * @author Oliver Siegmar
 */
public class GenericRepoPackage implements RepoPackage {

    /**
     * The basename.
     */
    private final String basename;

    /**
     * The version.
     */
    private final String version;

    /**
     * The revision.
     */
    private final String revision;

    /**
     * The arch.
     */
    private final String arch;

    /**
     * The extension.
     */
    private final String extension;

    /**
     *
     */
    private final boolean immutable;

    /**
     * Constructs a {@code repo package} based on the given filename.
     */
    public GenericRepoPackage(final String basename, final String version, final String revision, final String arch,
                              final String extension, final boolean immutable) {
        this.basename = basename;
        this.version = version;
        this.revision = revision;
        this.arch = arch;
        this.extension = extension;
        this.immutable = immutable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getArch() {
        return arch;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBasename() {
        return basename;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRevision() {
        return revision;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getVersion() {
        return version;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExtension() {
        return extension;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isImmutable() {
        return immutable;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final GenericRepoPackage that = (GenericRepoPackage) o;

        if (immutable != that.immutable) {
            return false;
        }
        if (arch != null ? !arch.equals(that.arch) : that.arch != null) {
            return false;
        }
        if (basename != null ? !basename.equals(that.basename) : that.basename != null) {
            return false;
        }
        if (extension != null ? !extension.equals(that.extension) : that.extension != null) {
            return false;
        }
        if (revision != null ? !revision.equals(that.revision) : that.revision != null) {
            return false;
        }
        if (version != null ? !version.equals(that.version) : that.version != null) {
            return false;
        }

        return true;
    }

    // CSOFF: MagicNumber
    @Override
    public int hashCode() {
        int result = basename != null ? basename.hashCode() : 0;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (revision != null ? revision.hashCode() : 0);
        result = 31 * result + (arch != null ? arch.hashCode() : 0);
        result = 31 * result + (extension != null ? extension.hashCode() : 0);
        result = 31 * result + (immutable ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("basename", basename)
            .append("version", version)
            .append("revision", revision)
            .append("arch", arch)
            .append("extension", extension)
            .append("immutable", immutable)
            .toString();
    }

}
