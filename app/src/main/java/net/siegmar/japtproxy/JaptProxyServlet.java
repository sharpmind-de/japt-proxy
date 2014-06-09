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
package net.siegmar.japtproxy;

import net.siegmar.japtproxy.exception.HandlingException;
import net.siegmar.japtproxy.exception.InvalidRequestException;
import net.siegmar.japtproxy.exception.ResourceUnavailableException;
import net.siegmar.japtproxy.exception.UnknownBackendException;
import net.siegmar.japtproxy.misc.HttpHeaderConstants;
import net.siegmar.japtproxy.misc.Util;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

/**
 * The JaptProxyServlet is the starting point for Japt-Proxy servlet. All
 * requests are handled by this servlet.
 *
 * @author Oliver Siegmar
 */
public class JaptProxyServlet extends HttpServlet {

    /**
     * The serialization identifier.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The logger instance.
     */
    private static final Logger LOG = LoggerFactory.getLogger(JaptProxyServlet.class);

    /**
     * The Japt-Proxy instance.
     */
    private JaptProxy japtProxy;

    /**
     * Logs the header of the incoming request.
     *
     * @param req the HttpServletRequest object
     */
    private static void logHeader(final HttpServletRequest req) {
        final Enumeration<String> headers = req.getHeaderNames();
        final StringBuilder sb = new StringBuilder();
        while (headers.hasMoreElements()) {
            final String headerName = headers.nextElement();
            sb.append(headerName);
            sb.append('=');
            sb.append(req.getHeader(headerName));
            if (headers.hasMoreElements()) {
                sb.append(',');
            }
        }
        LOG.debug("Request header: {}", sb);
    }

    @Required
    public void setJaptProxy(final JaptProxy japtProxy) {
        this.japtProxy = japtProxy;
    }

    /**
     * Initializes the Japt-Proxy configuration.
     *
     * @throws ServletException is thrown if the initialization fails.
     */
    @Override
    public void init()
        throws ServletException {
        LOG.info("Starting Japt-Proxy servlet {}", Util.VERSION);

        if (japtProxy == null) {
            final WebApplicationContext appCtx =
                WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
            japtProxy = appCtx.getBean("japtProxy", JaptProxy.class);
        }

        LOG.info("Japt-Proxy servlet initialization complete");
    }

    /**
     * Shut down Japt-Proxy.
     */
    @Override
    public void destroy() {
        japtProxy = null;
        LOG.info("JaptProxy shut down");
    }

    /**
     * Check the requested data and forward the request to internal sender.
     *
     * @param req the HttpServletRequest object
     * @param res the HttpServletResponse object
     * @throws ServletException {@inheritDoc}
     * @throws IOException      {@inheritDoc}
     */
    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse res)
        throws ServletException, IOException {
        res.setBufferSize(Util.DEFAULT_BUFFER_SIZE);

        MDC.put("REQUEST_ID", DigestUtils.md5Hex(Long.toString(System.currentTimeMillis())));

        LOG.debug("Incoming request from IP '{}', " +
                "User-Agent '{}'", req.getRemoteAddr(),
            req.getHeader(HttpHeaderConstants.USER_AGENT));

        if (LOG.isDebugEnabled()) {
            logHeader(req);
        }

        try {
            japtProxy.handleRequest(req, res);
        } catch (final InvalidRequestException e) {
            LOG.warn(e.getMessage());
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
            return;
        } catch (final UnknownBackendException e) {
            LOG.info(e.getMessage());
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown backend");
            return;
        } catch (final ResourceUnavailableException e) {
            LOG.debug(e.getMessage(), e);
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        } catch (final HandlingException e) {
            LOG.error("HandlingException", e);
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        } finally {
            MDC.clear();
        }

        res.flushBuffer();
    }

}
