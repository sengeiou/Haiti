#!/bin/bash

source ./setenv.sh

PID=(`/bin/ps -eaf | $LOC_GREP java | $LOC_GREP spring-ev-integration-ws.xml | $LOC_AWK '{print $2}'`)
PLEN=${#PID[@]}

if [ $PLEN -ge 1 ]
then 
    echo "EVDelivery alive, try to stop"
else
    mvn -o -e -f pom-evdelivery.xml antrun:run -DfepName=EVDelivery -Dport=8089 -DconfigFile=config/spring-ev-integration-ws.xml -DjmxPort=1399 -DmvnRepository=$MVNREPOSITORY > /dev/null 2>&1 &
    echo "start EVDelivery"
fi
