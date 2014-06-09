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

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("target", requestedTarget)
            .append("resource", requestedResource)
            .append("backend", requestedBackend)
            .append("modSince", requestModifiedSince)
            .append("userAgent", userAgent)
            .toString();
    }

}
