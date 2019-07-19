#!/bin/bash

source ./setenv.sh

PID=(`/bin/ps -eaf | $LOC_GREP java | $LOC_GREP spring-ev-integration-realtime.xml | $LOC_AWK '{print $2}'`)
PLEN=${#PID[@]}

if [ $PLEN -ge 1 ]
then 
    echo "EVDeliveryRealtime alive, try to stop"
else
    mvn -o -e -f pom-evdelivery-realtime-old.xml antrun:run -DfepName=EVDeliveryRealtimeOld -DconfigFile=config/spring-ev-integration-realtime-old.xml -DjmxPort=1299 -DmvnRepository=$MVNREPOSITORY > /dev/null 2>&1 &
    echo "start EVDeliveryRealtime"
fi
