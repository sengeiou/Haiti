#!/bin/sh
mvn -e -f pom-feph.xml antrun:run -DfepName=FEPEV -Dif4Port=8001 -DniTcpPort=18041 -DniUdpPort=18042 -DauthTcpPort=19041 -DauthUdpPort=19042 -DcommandPort=18940 -DjmxPort=11049 -Dspring.instrument.path=/home/aimir1/.m2/repository/org/springframework/spring-instrument/4.2.5.RELEASE 2>&1 > /dev/null &
