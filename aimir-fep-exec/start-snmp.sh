#!/bin/sh

#iptables -t nat -A PREROUTING -i em1 -p udp --dport 162 -j REDIRECT --to-port 16200
mvn -e -f pom-SnmpTrapManager.xml antrun:run -DfepName=FEP1 -DtrapPort=16200 -DjmxPort=1599 2>&1 > /dev/null &