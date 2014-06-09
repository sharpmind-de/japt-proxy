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
package net.siegmar.japtproxy.fetcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.net.URL;
import java.util.Map;

/**
 * Factory class used to initialize fetcher classes and return their instances.
 *
 * @author Oliver Siegmar
 */
public final class FetcherPool {

    /**
     * The logger instance.
     */
    private static final Logger LOG = LoggerFactory.getLogger(FetcherPool.class);

    /**
     * The instances.
     */
    private Map<String, Fetcher> fetchers;

    @Required
    public void setFetchers(final Map<String, Fetcher> fetchers) {
        this.fetchers = fetchers;
    }

    /**
     * Initializes an fetcher based on the given targetResource.
     *
     * @param targetResource the target resource
     * @return a fetcher instance, or {@code null} if no fetcher
     * was found for the specivied targetResource
     */
    public Fetcher getInstance(final URL targetResource) {
        final String protocol = targetResource.getProtocol();

        final Fetcher fetcher = fetchers.get(protocol);

        if (fetcher != null) {
            LOG.debug("Looking up fetcher for protocol '{}', found '{}'",
                protocol, fetcher.getClass().getName());
        } else {
            LOG.debug("Looking up fetcher for protocol '{}', found none",
                protocol);
        }

        return fetcher;
    }

}
