#!/bin/bash

source ./setenv.sh

PID=(`/bin/ps -eaf | $LOC_GREP java | $LOC_GREP spring-mv-integration-ws-test.xml | $LOC_AWK '{print $2}'`)
PLEN=${#PID[@]}


if [ $PLEN -ge 1 ]
then 
    echo "MVDelivery alive, try to stop"
else
    mvn -o -e -f pom-mvdelivery-test.xml antrun:run -DfepName=MVDeliveryTest -Dport=8080 -DconfigFile=config/spring-mv-integration-ws-test.xml
    echo "start MVDeliveryTest"
fi
