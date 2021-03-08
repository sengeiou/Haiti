#!/bin/sh
mvn -e -f pom-feph.xml antrun:run -DfepName=FEP1 -Dif4Port=8000 -DniTcpPort=7001 -DniUdpPort=8002 -DauthTcpPort=9001 -DauthUdpPort=9002 -DcommandPort=8900 -DjmxPort=1099 -Dspring.instrument.path=/home/aimir1/.m2/repository/org/springframework/spring-instrument/4.2.5.RELEASE 2>&1 > /dev/null &
