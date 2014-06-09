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

import net.siegmar.japtproxy.exception.InitializationException;
import net.siegmar.japtproxy.misc.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Configuration for HttpClient.
 *
 * @author Oliver Siegmar
 */
public class HttpClientConfigurer {

    private static final Logger LOG = LoggerFactory.getLogger(HttpClientConfigurer.class);
    private static final int MAX_CONNECTIONS = 20;

    private Configuration configuration;
    private int socketTimeout;
    private int connectTimeout;

    @Required
    public void setConfiguration(final Configuration configuration) {
        this.configuration = configuration;
    }

    public void setSocketTimeout(final int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public void setConnectTimeout(final int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public CloseableHttpClient build() throws InitializationException {
        final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(MAX_CONNECTIONS);
        connectionManager.setDefaultMaxPerRoute(MAX_CONNECTIONS);

        final RequestConfig requestConfig = RequestConfig
            .custom()
            .setConnectionRequestTimeout(connectTimeout)
            .setSocketTimeout(socketTimeout)
            .build();

        final HttpClientBuilder httpClientBuilder = HttpClients
            .custom()
            .setConnectionManager(connectionManager)
            .setDefaultRequestConfig(requestConfig);

        if (configuration.getHttpProxy() != null) {
            configureProxy(httpClientBuilder, configuration.getHttpProxy());
        }

        return httpClientBuilder.build();
    }

    protected void configureProxy(final HttpClientBuilder httpClientBuilder, final String proxy)
        throws InitializationException {
        final URL proxyUrl;
        try {
            proxyUrl = new URL(proxy);
        } catch (final MalformedURLException e) {
            throw new InitializationException("Invalid proxy url", e);
        }

        final String proxyHost = proxyUrl.getHost();
        final int proxyPort = proxyUrl.getPort() != -1
            ? proxyUrl.getPort()
            : proxyUrl.getDefaultPort();

        LOG.info("Set proxy server to '{}:{}'", proxyHost, proxyPort);
        httpClientBuilder.setRoutePlanner(new DefaultProxyRoutePlanner(new HttpHost(proxyHost, proxyPort)));

        final String userInfo = proxyUrl.getUserInfo();
        if (userInfo != null) {
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(new AuthScope(proxyHost, proxyPort), buildCredentials(userInfo));

            httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
        }
    }

    protected Credentials buildCredentials(final String userInfo)
        throws InitializationException {
        final String username = StringUtils.substringBefore(userInfo, ":");
        final String password = StringUtils.substringAfter(userInfo, ":");

        return username.contains("\\")
            ? buildNTCredentials(username, password)
            : new UsernamePasswordCredentials(username, password);
    }

    protected Credentials buildNTCredentials(final String userPart, final String password)
        throws InitializationException {
        final String domain = StringUtils.substringBefore(userPart, "\\");
        final String username = StringUtils.substringAfter(userPart, "\\");
        return new NTCredentials(username, password, getLocalHostName(), domain);
    }

    private static String getLocalHostName()
        throws InitializationException {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (final UnknownHostException e) {
            throw new InitializationException(
                "Unknown host name for NTLM proxy", e);
        }
    }

}
