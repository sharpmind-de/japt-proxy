Japt-Proxy
==========

Japt-Proxy is a JAVA based Caching Proxy for Debian's APT-System. Japt-Proxy is very reliable and lightning fast.
Japt-Proxy is usually used in environments where several Debian systems have to be kept up-to-date without downloading
the same files over and over again from the debian mirrors. Mostly these environments are data centers, Linux-powered
companies, schools, public authorities and so on.

Often Japt-Proxy is used because of small sized internet connections but it is also useful to install complete debian
systems over the net (read: over the proxy). Even with high volume internet connections Japt-Proxy can dramatically
reduce the time to install or update Debian on many machines.


Why Java?
---------

I know there are people out there thinking "Hey? Why Java?". There are a bunch of reasons why people asking those
questions. I have a simple answer to all those questions. The answer is, that Java enabled me to easily develop the
fastest and most reliable APT-Proxy. Java and the Apache Software Foundation provides such good libraries for this job,
that it wasn't very hard to bring up this tool.

If you don't trust, test it - the installation takes less than five minutes!


Doesn't a general proxy server supersede an APT-Proxy?
------------------------------------------------------

No, it doesn't. Japt-Proxy knows the characteristics of a Debian archive and their files. It knows, for example, that
.deb files won't change, because newer files would have a new name (the version is always part of the filename), hence
it won't recheck if there is a newer version of this specific file.

Furthermore you can configure the Japt-Proxy to delete old versions of a package in its cache directory - a general
proxy cache would simple delete files that haven't been requested for some time.

So, a general caching proxy like Squid is a perfect tool for caching "normal" internet traffic. If you need a proxy for
faster APT access - Japt-Proxy is the right tool for you.


Here are some key features of "Japt-Proxy"
------------------------------------------

- Caching of all apt requests from Debian archives
- Multi-threaded architecture which increases the download speed enormously when several machines are requesting
  packages
- Builds up a partial mirror directory automatically, with exactly the same directory structure and file names as the
  original mirror
- Automatic and configurable cache cleanup (purging packages in cache if newer versions exists)
- Makes sources.list configuration easier for multiple machines - if you change your internet backend server you only
  have to make the change once in Japt-Proxy, instead of on all Debian machines
- Works with multiple architectures
- Backend support for HTTP and FTP
- existing .debs can be copied into the Japt-Proxy cache directory (from a CD or DVD for example) and will be used by
  the Proxy automatically
- it supports .rpm too


Requirements
------------

- Java Runtime Environment 7


Quickstart
----------

Create directories and copy configuration

    mkdir /etc/japt-proxy /opt/japt-proxy /var/log/japt-proxy /var/cache/japt-proxy
    cp example/* /etc/japt-proxy/

Build and install Japt-Proxy

    ./gradlew assemble
    cp standalone/build/libs/japt-proxy-server-*.jar /opt/japt-proxy/japt-proxy.jar

Launching

    nohup java \
        -Djaptproxy.config=/etc/japt-proxy/japt-proxy.cfg.xml \
        -Djaptproxy.logConfig=/etc/japt-proxy/japt-proxy-log.cfg.xml \
        -Djaptproxy.contextPath=/ \
        -Djaptproxy.port=8080 \
        -jar /opt/japt-proxy/japt-proxy.jar \
        &> /dev/null < /dev/null &

See what is happening

    tail -f /var/log/japt-proxy/japt-proxy.log

Change your apt configuration (/etc/apt/sources.list) to use Japt-Proxy. Example:
    
    deb http://localhost:8080/debian/ wheezy main
    deb-src http://localhost:8080/debian/ wheezy main
    
    deb http://localhost:8080/debian-security/ wheezy/updates main
    deb-src http://localhost:8080/debian-security/ wheezy/updates main
    
    deb http://localhost:8080/debian/ wheezy-updates main
    deb-src http://localhost:8080/debian/ wheezy-updates main
    
Use it

    apt-get update
    apt-get install ...


Contribution
------------

- Fork
- Code
- Add test(s)
- Commit
- Send me a pull request


Copyright
---------

Copyright 2014 Oliver Siegmar <oliver@siegmar.net>

Japt-Proxy is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Japt-Proxy is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with Japt-Proxy.  If not, see <http://www.gnu.org/licenses/>.
