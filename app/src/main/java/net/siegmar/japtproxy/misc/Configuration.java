/**
 * Japt-Proxy: The JAVA(TM) based APT-Proxy
 * <p/>
 * Copyright (C) 2006-2008  Oliver Siegmar <oliver@siegmar.net>
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.siegmar.japtproxy.misc;

import net.siegmar.japtproxy.exception.InitializationException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.math.NumberUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to read and hold the Japt-Proxy configuration.
 *
 * @author Oliver Siegmar
 */
public class Configuration {

    /**
     * The logger instance.
     */
    private static final Logger LOG = LoggerFactory.getLogger(Configuration.class);

    /**
     * The directory where the cache files are stored.
     */
    private final File cacheDir;

    /**
     * The host name of the http proxy server.
     */
    private final String httpProxy;

    /**
     * The maximum amount of versions allowed to exists.
     */
    private final Integer maxVersions;

    /**
     * The map of backend systems. The key is the name of the backend.
     */
    private final Map<String, Backend> backendSystems = new HashMap<>();

    /**
     * Initialize the configuration with the given config file.
     *
     * @param configFile the config file.
     * @throws InitializationException is thrown if the configuration is erroneous.
     */
    public Configuration(final File configFile) throws InitializationException {
        try {
            final SAXBuilder sax = new SAXBuilder();
            final Document doc = sax.build(configFile);
            final Element rootElement = doc.getRootElement();

            cacheDir = new File(rootElement.getChildTextTrim("cache-dir"));
            httpProxy = rootElement.getChildTextTrim("http-proxy");

            final String maxVersionsString = rootElement.getChildTextTrim("max-versions");

            maxVersions = NumberUtils.createInteger(maxVersionsString);

/*
            final List<Element> backends = rootElement.getChild("backends").getChildren();

            // disable backend config


            for (final Element e : backends) {
                final List<Element> confUrls = e.getChildren();

                if (confUrls == null || confUrls.isEmpty()) {
                    continue;
                }

                final String name = e.getAttributeValue("name");

                final String type = e.getAttributeValue("type");
                if (type == null) {
                    throw new InitializationException("type attribute is missing for backend '" + name + "'");
                }

                final String dir = e.getAttributeValue("dir");

                final Backend backend = new Backend(BackendType.valueOf(type.toUpperCase(Locale.ENGLISH)));
                final File backendDirectory = new File(cacheDir, StringUtils.defaultIfEmpty(dir, name));

                FileUtils.forceMkdir(backendDirectory);

                backend.setDirectory(backendDirectory);

                for (final Element confUrl : confUrls) {
                    backend.addUrl(new URL(confUrl.getTextTrim()));
                }

                backendSystems.put(name, backend);
            }
*/
        } catch (final IOException | JDOMException e) {
            throw new InitializationException("Error reading configuration", e);
        }

        LOG.debug("Initialized configuration: {}", this);
    }

    /**
     * Returns the Http-Proxy to be used.
     *
     * @return the Http-Proxy to be used
     */
    public String getHttpProxy() {
        return httpProxy;
    }

    /**
     * Returns the max. amount of versions to be kept from a repo package.
     *
     * @return the max. amount of versions to be kept from a repo package
     */
    public Integer getMaxVersions() {
        return maxVersions;
    }

/*
    public Backend getBackend(final String backendName) {
        return backendSystems.get(backendName);
    }
*/

    public Backend getBackend(final RequestedData requestedData) {
        // check if we have a backend
        Backend backend = backendSystems.get(requestedData.getServerName());

        if (backend != null) return backend;

        // create a new backend on the fly (only DEB for now)
        try {
            backend = new Backend(BackendType.DEB);
            final File backendDirectory = new File(cacheDir, requestedData.getServerName());

            FileUtils.forceMkdir(backendDirectory);
            backend.setDirectory(backendDirectory);
            backend.addUrl(new URL(requestedData.getHostUrl() + "/" + requestedData.getRequestedBackend()));

            backendSystems.put(requestedData.getServerName(), backend);

        } catch (final IOException e) {
            //log("Error reading configuration", e);
            return null;
        }


        return backend;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("cacheDir", cacheDir)
                .append("httpProxy", httpProxy)
                .append("maxVersions", maxVersions)
                .append("backendSystems", backendSystems)
                .toString();
    }

}
