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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimetypesFileTypeMap;
import java.io.IOException;
import java.util.Locale;

/**
 * Utility class.
 *
 * @author Oliver Siegmar
 */
public final class Util {

    /**
     * The Japt-Proxy version.
     */
    public static final String VERSION;

    /**
     * The user-agent string.
     */
    public static final String USER_AGENT;

    /**
     * Buffer size that is used for all output buffers.
     */
    public static final int DEFAULT_BUFFER_SIZE = 4096;

    /**
     * The logger instance.
     */
    private static final Logger LOG = LoggerFactory.getLogger(Util.class);

    /**
     * DateFormat that is used by the 'Modified-Since' and
     * 'If-Modified-Since' HTTP headers (RFC 822).
     */
    private static final FastDateFormat RFC822_DATE_FORMAT =
        FastDateFormat.getInstance("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);

    /**
     * DateFormat that is used for log output.
     */
    private static final FastDateFormat SIMPLE_DATE_FORMAT =
        FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss 'GMT'");

    private static final MimetypesFileTypeMap FILE_TYPE_MAP = new MimetypesFileTypeMap();

    static {
        try {
            VERSION = IOUtils.toString(Util.class.getResourceAsStream("/JAPT_PROXY_VERSION"));
            USER_AGENT = "Japt-Proxy/" + VERSION;
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Private utility class constructor.
     */
    private Util() {
        // No public constructor for utility classes.
    }

    /**
     * Returns a loggable date from a given timestamp.
     *
     * @param timestamp the timestamp to convert
     * @return a loggable date
     */
    public static String getSimpleDateFromTimestamp(final long timestamp) {
        return SIMPLE_DATE_FORMAT.format(timestamp);
    }

    /**
     * Returns a rfc822 compliant date from a given timestamp.
     *
     * @param timestamp the timestamp to convert
     * @return a rfc822 compliant date
     */
    public static String getRfc822DateFromTimestamp(final long timestamp) {
        return RFC822_DATE_FORMAT.format(timestamp);
    }

    /**
     * Returns a mime-type for a given file extension. Returns null
     * if the file extension is not mapped.
     *
     * @param filename the file name to get a mime-type for
     * @return the mime-type for the given file extension
     */
    public static String getMimetype(final String filename) {
        final String contentType = FILE_TYPE_MAP.getContentType(filename);

        LOG.debug("Content-Type of '{}' is '{}'", filename, contentType);

        return contentType;
    }

}
