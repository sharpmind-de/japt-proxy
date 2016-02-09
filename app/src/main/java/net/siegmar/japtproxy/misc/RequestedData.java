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

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Class that holds request information.
 *
 * @author Oliver Siegmar
 */
public class RequestedData {

    /**
     * The requested resource.
     */
    private String requestedResource;

    /**
     * The requestModifiedSince timestamp.
     */
    private long requestModifiedSince;

    /**
     * The requested target.
     */
    private String requestedTarget;

    /**
     * The requested backend.
     */
    private String requestedBackend;

    private String userAgent;

    private String url;
    private String hostUrl;
    private String scheme;
    private String serverName;
    private int serverPort;


    /**
     * Returns the requested resource.
     *
     * @return the requested resource
     */
    public String getRequestedResource() {
        return requestedResource;
    }

    /**
     * Sets the requested resource.
     *
     * @param requestedResource the requested resource
     */
    public void setRequestedResource(final String requestedResource) {
        this.requestedResource = requestedResource;
    }

    /**
     * Returns the requestModifiedSince timestamp.
     *
     * @return the requestModifiedSince timestamp
     */
    public long getRequestModifiedSince() {
        return requestModifiedSince;
    }

    /**
     * Sets the requestModifiedSince timestamp.
     *
     * @param requestModifiedSince the requestModifiedSince timestamp
     */
    public void setRequestModifiedSince(final long requestModifiedSince) {
        this.requestModifiedSince = requestModifiedSince;
    }

    /**
     * Returns the requested target.
     *
     * @return the requested target
     */
    public String getRequestedTarget() {
        return requestedTarget;
    }

    /**
     * Sets the requested target.
     *
     * @param requestedTarget the requested target
     */
    public void setRequestedTarget(final String requestedTarget) {
        this.requestedTarget = requestedTarget;
    }

    /**
     * Returns the requested backend.
     *
     * @return the requested backend
     */
    public String getRequestedBackend() {
        return requestedBackend;
    }

    /**
     * Sets the requested backend.
     *
     * @param requestedBackend the requested backend
     */
    public void setRequestedBackend(final String requestedBackend) {
        this.requestedBackend = requestedBackend;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(final String userAgent) {
        this.userAgent = userAgent;
    }


    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHostUrl() {
        return hostUrl;
    }

    public void setHostUrl(String hostUrl) {
        this.hostUrl = hostUrl;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("target", requestedTarget)
                .append("resource", requestedResource)
                .append("backend", requestedBackend)
                .append("modSince", requestModifiedSince)
                .append("userAgent", userAgent)
                .append("url", url)
                .append("hostUrl", hostUrl)
                .append("scheme", scheme)
                .append("serverName", serverName)
                .append("serverPort", serverPort)
                .toString();
    }

}
