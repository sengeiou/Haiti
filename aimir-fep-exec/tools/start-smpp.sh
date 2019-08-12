#!/bin/sh
mvn -e -f pom-smpp.xml antrun:run -DfepName=FEP1 -Dfile.encoding=EUC_KR -DjmxPort=1499 -Dspring.instrument.path=/home/aimir1/.m2/repository/org/springframework/spring-instrument/4.2.5.RELEASE 2>&1 > /dev/null &

