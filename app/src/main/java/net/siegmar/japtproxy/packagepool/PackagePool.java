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

import net.siegmar.japtproxy.misc.Backend;
import net.siegmar.japtproxy.poolobject.PoolObject;

import java.io.IOException;

/**
 * The PackagePool interface defines methods to operate with
 * the package pool. The package pool contains all the packages
 * this proxy is responsible for.
 *
 * @author Oliver Siegmar
 */
public interface PackagePool<T extends PoolObject> {

    /**
     * Returns a PoolObject for the given resource name.
     *
     * @param resourceName the resource name.
     * @return the PoolObject for the given resource name.
     * @throws IOException is thrown if the lookup failed.
     */
    T getPoolObject(Backend backend, String resourceName) throws IOException;

    /**
     * Remove old versions of a resource.
     *
     * @param poolObject the resource to remove.
     */
    void removeOldPackages(T poolObject);

}
