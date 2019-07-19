#!/bin/bash

source ./setenv.sh

PID=(`/bin/ps -eaf | $LOC_GREP java | $LOC_GREP spring-mv-integration-ws.xml | $LOC_AWK '{print $2}'`)
PLEN=${#PID[@]}

if [ $PLEN -ge 1 ]
then 
    echo "MVDelivery alive, try to stop"
else
    mvn -o -e -f pom-mvdelivery.xml antrun:run -DfepName=MVDelivery -Dport=8089 -DconfigFile=config/spring-mv-integration-ws.xml -DjmxPort=1399 -DmvnRepository=$MVNREPOSITORY > /dev/null 2>&1 &
    echo "start MVDelivery"
fi
