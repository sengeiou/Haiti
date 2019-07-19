#!/bin/bash

source ./setenv.sh

PID=(`/bin/ps -eaf | $LOC_GREP java | $LOC_GREP spring-osb-ws-test.xml | $LOC_AWK '{print $2}'`)
PLEN=${#PID[@]}

if [ $PLEN -ge 1 ]
then 
    echo "OSB Testserver alive, try to stop"
else
    mvn -o -e -f pom-osb-test.xml antrun:run -DfepName=OSBTestServer -Dport=8080 -DconfigFile=config/spring-osb-ws-test.xml -DmvnRepository=$MVNREPOSITORY > /dev/null 2>&1 &
    echo "start OSB Testserver"
fi
