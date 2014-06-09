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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This ResourceLock utility class is used to obtain ReadWriteLocks for
 * specific lockIdentifiers.
 *
 * @author Oliver Siegmar
 */
public final class ResourceLock {

    /**
     * A Map holding ReadWriteLocks and a reference counter.
     */
    private static final Map<String, LockGroup> LOCKS = new HashMap<>();

    /**
     * Private utility constructor.
     */
    private ResourceLock() {
        // No public constructor for utility classes.
    }

    /**
     * Method to obtain the ReadWriteLock for a given lockIdentifier.
     *
     * @param lockIdentifier the lockIdentifier
     * @return the ReadWriteLock
     */
    public static synchronized ReadWriteLock obtainLocker(final String lockIdentifier) {
        LockGroup lockGroup = LOCKS.get(lockIdentifier);

        if (lockGroup != null) {
            lockGroup.addReference();
            return lockGroup.getLock();
        }

        lockGroup = new LockGroup();

        LOCKS.put(lockIdentifier, lockGroup);

        return lockGroup.getLock();
    }

    /**
     * Method to release the ReadWriteLock for a given lockIdentifier.
     *
     * @param lockIdentifier the lockIdentifier
     */
    public static synchronized void releaseLocker(final String lockIdentifier) {
        final LockGroup lockGroup = LOCKS.get(lockIdentifier);

        if (lockGroup.removeReference() == 0) {
            LOCKS.remove(lockIdentifier);
        }
    }

    private static final class LockGroup {

        private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        private int references = 1;

        public int addReference() {
            return ++references;
        }

        public int removeReference() {
            return --references;
        }

        public ReentrantReadWriteLock getLock() {
            return lock;
        }

    }

}
