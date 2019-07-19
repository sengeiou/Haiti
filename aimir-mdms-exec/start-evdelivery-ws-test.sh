#!/bin/bash

source ./setenv.sh

PID=(`/bin/ps -eaf | $LOC_GREP java | $LOC_GREP spring-ev-integration-ws-test.xml | $LOC_AWK '{print $2}'`)
PLEN=${#PID[@]}

if [ $PLEN -ge 1 ]
then 
    echo "EVDelivery alive, try to stop"
else
    mvn -o -e -f pom-evdelivery-test.xml antrun:run -DfepName=EVDeliveryTest -Dport=8080 -DconfigFile=config/spring-ev-integration-ws-test.xml
    echo "start EVDeliveryTest"
fi
