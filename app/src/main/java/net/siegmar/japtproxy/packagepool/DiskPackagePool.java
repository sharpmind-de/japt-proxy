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

import net.siegmar.japtproxy.exception.InvalidRepoPackageNameException;
import net.siegmar.japtproxy.misc.Backend;
import net.siegmar.japtproxy.misc.Configuration;
import net.siegmar.japtproxy.packages.RepoPackage;
import net.siegmar.japtproxy.packages.RepoPackageBuilder;
import net.siegmar.japtproxy.packages.RepoPackageFilter;
import net.siegmar.japtproxy.poolobject.PoolFile;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The DiskPackagePool is an implementation of PackagePool that
 * stores the packages on a disk.
 *
 * @author Oliver Siegmar
 */
public class DiskPackagePool implements PackagePool<PoolFile> {

    /**
     * The logger instance.
     */
    private static final Logger LOG = LoggerFactory.getLogger(DiskPackagePool.class);
    private final PackageCleanTask task = new PackageCleanTask();
    private final Timer timer = new Timer();
    private final Object lock = new Object();
    private Configuration configuration;
    /**
     * The version comparator - used for finding and removing old repo
     * package versions.
     */
    private Comparator<RepoPackage> comparator;
    private RepoPackageBuilder repoPackageBuilder;
    private Set<PoolFile> filesToRemove = new HashSet<>();
    private int startDelay;
    private int repeatInterval;

    @Required
    public void setConfiguration(final Configuration configuration) {
        this.configuration = configuration;
    }

    @Required
    public void setComparator(final Comparator<RepoPackage> comparator) {
        this.comparator = comparator;
    }

    @Required
    public void setRepoPackageBuilder(final RepoPackageBuilder repoPackageBuilder) {
        this.repoPackageBuilder = repoPackageBuilder;
    }

    @Required
    public void setStartDelay(final int startDelay) {
        this.startDelay = startDelay;
    }

    @Required
    public void setRepeatInterval(final int repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    @PostConstruct
    public void init() {
        timer.schedule(task, startDelay, repeatInterval);
    }

    @PreDestroy
    public void destroy() {
        timer.cancel();
        task.run();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PoolFile getPoolObject(final Backend backend, final String resourceName)
        throws IOException {
        final File file = new File(backend.getDirectory(), resourceName);

        LOG.debug("Initialized pool file '{}'", file.getAbsolutePath());

        RepoPackage repoPackage = null;
        try {
            repoPackage = repoPackageBuilder.newPackage(resourceName);
        } catch (final InvalidRepoPackageNameException e) {
            LOG.debug("Requested resource '{}' is not a valid repository package", resourceName);
        }

        return new PoolFile(file, repoPackage);
    }

    /**
     * Schedules a resource name for removal.
     */
    @Override
    public void removeOldPackages(final PoolFile poolFile) {
        synchronized (lock) {
            filesToRemove.add(poolFile);
        }
    }

    private class PackageCleanTask extends TimerTask {

        @Override
        public void run() {
            removeOldPackagesInternal();
        }

        /**
         * Analyzes and removed all old scheduled resources.
         */
        private void removeOldPackagesInternal() {
            LOG.debug("Starting removal check for scheduled resources");
            final Set<PoolFile> myFilesToRemove;
            synchronized (lock) {
                if (filesToRemove.isEmpty()) {
                    return;
                }
                myFilesToRemove = filesToRemove;
                filesToRemove = new HashSet<>();
            }
            for (final PoolFile fileToRemove : myFilesToRemove) {
                removeOldPackagesInternal(fileToRemove);
            }
        }

        private void removeOldPackagesInternal(final PoolFile poolFile) {
            final File resource = poolFile.getResource();
            final File dirOfPackage = resource.getParentFile();

            final RepoPackage repoPackage = poolFile.getRepoPackage();

            LOG.debug("Analyzing old package removal for package: {}", repoPackage);

            final String basename = repoPackage.getBasename();
            final String architecture = repoPackage.getArch();

            LOG.debug("Searching for old packages with basename '{}' and architechture '{}' in '{}'",
                basename, architecture, dirOfPackage.getAbsolutePath());

            final RepoPackageFilter repoPackageFilter = new RepoPackageFilter(repoPackage, repoPackageBuilder);
            final String[] files = dirOfPackage.list(repoPackageFilter);

            final int maxVersions = configuration.getMaxVersions();
            final int overhang = files.length - maxVersions;

            if (overhang <= 0) {
                LOG.debug("No old files found to remove");
                return;
            }

            try {
                Arrays.sort(repoPackageBuilder.newPackages(files), comparator);
            } catch (final InvalidRepoPackageNameException e) {
                LOG.error("List of repo packages couldn't be sorted due to an invalid package name", e);
                return;
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Found {} files ({}). Max files: {} - Files to remove: {}",
                    files.length, ArrayUtils.toString(files), maxVersions, overhang);
            }

            for (int i = 0; i < overhang; i++) {
                final File file = new File(dirOfPackage, files[i]);
                LOG.debug("Remove old file: '{}'", file.getName());
                if (!file.delete()) {
                    LOG.error("Couldn't delete file '{}'", file);
                }
            }
        }

    }

}
