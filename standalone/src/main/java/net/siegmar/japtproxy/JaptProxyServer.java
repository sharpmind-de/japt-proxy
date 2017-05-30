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

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
import net.siegmar.japtproxy.misc.Util;
import org.apache.commons.lang3.StringUtils;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.BlockingChannelConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;

/**
 * The JaptProxyServer is is the starting point for Japt-Proxy standalone
 * server. It starts the embedded Jetty server with the JaptProxyServlet.
 *
 * @author Oliver Siegmar
 */
public final class JaptProxyServer {

    private static final String OPT_CONFIG = "japtproxy.config";
    private static final String OPT_CONTEXT_PATH = "japtproxy.contextPath";
    private static final String OPT_LOG_CONFIG = "japtproxy.logConfig";
    private static final String OPT_HOST = "japtproxy.host";
    private static final String OPT_PORT = "japtproxy.port";

    private static final String OPT_CONFIG_DEFAULT = "/etc/japt-proxy/japt-proxy.cfg.xml";
    private static final String OPT_CONTEXT_PATH_DEFAULT = "/";
    private static final String OPT_LOG_CONFIG_DEFAULT = "/etc/japt-proxy/japt-proxy-log.cfg.xml";
    private static final String OPT_PORT_DEFAULT = "3142";

    //private static final String[] REQUIRED_OPTIONS = { OPT_CONFIG, OPT_CONTEXT_PATH, OPT_LOG_CONFIG, OPT_PORT };
    private static final String[] REQUIRED_OPTIONS = {};

    /**
     * Private contstructor.
     */
    private JaptProxyServer() {
        // No instanciation of this class
    }

    /**
     * Initializes the underlaying logging system.
     *
     * @param logConfig log configuration filename.
     */
    private static void initializeLogging(final String logConfig) {
        final LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

        StatusPrinter.print(lc);

/*
        final JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(lc);
        lc.stop();
        try {
            configurator.doConfigure(logConfig);
        } catch (final JoranException e) {
            throw new IllegalStateException(e);
        }
*/
    }

    /**
     * Starts Jetty.
     *
     * @param host        the host to bound.
     * @param port        the port to bound.
     * @param contextPath the context path to bound.
     */
    private static void startServer(final String host, final int port, final String contextPath) {
        final Logger log = LoggerFactory.getLogger(JaptProxyServer.class);
        log.info("Starting Japt-Proxy {} on host {} port {} using context path '{}'",
            Util.VERSION, StringUtils.defaultIfEmpty(host, "*"), port, contextPath);

        // Spring startup
        final ClassPathXmlApplicationContext classPathXmlApplicationContext =
            new ClassPathXmlApplicationContext("master.xml", "standalone.xml");
        classPathXmlApplicationContext.registerShutdownHook();

        // Jetty startup
        final Server server = new Server();

        final Connector connector = new BlockingChannelConnector();

        if (!StringUtils.isBlank(host)) {
            connector.setHost(host);
        }

        connector.setPort(port);
        server.setConnectors(new Connector[]{connector});

        server.setStopAtShutdown(true);

        final Context root = new Context(server, contextPath);
        final JaptProxyServlet japtProxyServlet =
            classPathXmlApplicationContext
                .getBean("japtProxyServlet", JaptProxyServlet.class);
        root.addServlet(new ServletHolder(japtProxyServlet), "/*");

        try {
            server.start();
        } catch (final Exception e) {
            // shame on Jetty's exception handling
            throw new IllegalStateException("Couldn't start HTTP engine", e);
        }
    }

    private static boolean checkRequiredOptions() {
        boolean errorsFound = false;
        for (final String option : REQUIRED_OPTIONS) {
            if (StringUtils.isBlank(System.getProperty(option))) {
                System.err.printf("Error: Option '%s' was not specified", option);
                System.err.println();
                errorsFound = true;
            }
        }
        return errorsFound;
    }

    /**
     * Starts the JaptProxyServer with the given parameters.
     *
     * @param args command line args.
     */
    public static void main(final String[] args) {
        if (checkRequiredOptions()) {
            System.exit(1);
        }

        String configFile = System.getProperty(OPT_CONFIG);
        String contextPath = System.getProperty(OPT_CONTEXT_PATH);
        String logConfig = System.getProperty(OPT_LOG_CONFIG);
        final String host = System.getProperty(OPT_HOST);
        String port = System.getProperty(OPT_PORT);

        // Check settings
        if (configFile == null) {
            configFile = OPT_CONFIG_DEFAULT;
        }
        if (contextPath == null) {
            contextPath = OPT_CONTEXT_PATH_DEFAULT;
        }
        if (logConfig == null) {
            logConfig = OPT_LOG_CONFIG_DEFAULT;
        }
        if (port == null) {
            port = OPT_PORT_DEFAULT;
        }

        if (!new File(configFile).exists()) {
            System.err.println("Config file '" + configFile + "' doesn't exist");
            System.exit(1);
        }

        try {
            initializeLogging(logConfig);
        } catch (final RuntimeException e) {
            System.err.println("Couldn't initialize logging");
            e.printStackTrace(System.err);
            System.exit(1);
        }

        final Logger log = LoggerFactory.getLogger(JaptProxyServer.class);

        // Remove PID_FILE if specified
        final String pidFile = System.getenv("PID_FILE");
        if (pidFile != null) {
            log.debug("Will try to delete PID_FILE {} on exit", pidFile);
            new File(pidFile).deleteOnExit();
        }

        try {
            final int portNumber = Integer.parseInt(port);
            startServer(
                host,
                portNumber,
                StringUtils.stripEnd(contextPath, "/")
            );
        } catch (final NumberFormatException e) {
            log.error("The specified port number {} is not a number", port);
            System.exit(1);
        } catch (final RuntimeException e) {
            log.error("Error while starting server", e);
            System.exit(1);
        }
    }

}
