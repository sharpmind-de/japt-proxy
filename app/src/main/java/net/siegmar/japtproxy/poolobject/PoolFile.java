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
package net.siegmar.japtproxy.poolobject;

import net.siegmar.japtproxy.misc.Util;
import net.siegmar.japtproxy.packages.RepoPackage;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A file-based PoolObject implementation.
 *
 * @author Oliver Siegmar
 */
public class PoolFile implements PoolObject {

    /**
     * The temp resource file handle.
     */
    private final File tmpResource;

    /**
     * The resource file handle.
     */
    private final File resource;

    private final RepoPackage repoPackage;

    /**
     * Initialize the PoolFile with the given resource.
     *
     * @param resource the file handle.
     * @throws IOException is thrown if the initialization fails.
     */
    public PoolFile(final File resource, final RepoPackage repoPackage)
        throws IOException {
        this.resource = resource;
        this.repoPackage = repoPackage;
        tmpResource = new File(resource.getAbsolutePath() + ".tmp");

        final File dir = resource.getParentFile();
        FileUtils.forceMkdir(dir);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getLastModified() {
        return resource.lastModified();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLastModified(final long lastModified) throws IOException {
        if (!tmpResource.setLastModified(lastModified)) {
            throw new IOException("Couldn't modify last modification timesatamp");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getSize() {
        return resource.length();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getContentType() {
        return Util.getMimetype(resource.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getInputStream()
        throws IOException {
        return new FileInputStream(resource);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutputStream getOutputStream()
        throws IOException {
        return new FileOutputStream(tmpResource);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store() throws IOException {
        if (resource.exists()) {
            if (!resource.delete()) {
                throw new IOException("Couldn't delete file '" + resource + "'");
            }
        }

        if (!tmpResource.renameTo(resource)) {
            throw new IOException("Couldn't rename '" + tmpResource + "' to '" + resource + "'");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove() {
        FileUtils.deleteQuietly(resource);
        FileUtils.deleteQuietly(tmpResource);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return resource.getName();
    }

    public File getResource() {
        return resource;
    }

    @Override
    public RepoPackage getRepoPackage() {
        return repoPackage;
    }

}
