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

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.File;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This class holds the backend configuration.
 *
 * @author Oliver Siegmar
 */
public class Backend {
    /**
     * The type of backend (debian or rpm at present).
     */
    private final BackendType type;

    /**
     * The directory where packages should be stored.
     */
    private File directory;

    /**
     * The urls of remote hosts.
     */
    private Set<URL> urls;

    public Backend(final BackendType type) {
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }
        this.type = type;
    }

    public BackendType getType() {
        return type;
    }

    /**
     * @return Returns the directory.
     */
    public File getDirectory() {
        return directory;
    }

    /**
     * @param directory The directory to set.
     */
    public void setDirectory(final File directory) {
        this.directory = directory;
    }

    /**
     * @return Returns the hosts.
     */
    public Set<URL> getUrls() {
        return urls;
    }

    /**
     * @param urls The hosts to set.
     */
    public void setUrls(final Set<URL> urls) {
        this.urls = urls;
    }

    /**
     * @param url The host to add.
     */
    public void addUrl(final URL url) {
        if (urls == null) {
            urls = new LinkedHashSet<>();
        }

        urls.add(url);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("type", type)
            .append("dir", directory)
            .append("urls", urls)
            .toString();
    }

}
