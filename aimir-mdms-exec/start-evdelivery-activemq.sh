#!/bin/bash

source ./setenv.sh

PID=(`/bin/ps -eaf | $LOC_GREP java | $LOC_GREP log4j-evdelivery | $LOC_AWK '{print $2}'`)
PLEN=${#PID[@]}

if [ $PLEN -ge 1 ]
then 
    echo "EVDelivery alive, try to stop"
else
    while [ $PLEN -eq 0 ]
    do
        mvn -o -e -f pom-evdelivery.xml antrun:run -DfepName=EVDelivery -Dport=8089 -DconfigFile=config/spring-ev-integration-activemq.xml -DjmxPort=1399 -DmvnRepository=$MVNREPOSITORY > /dev/null 2>&1 &
        echo "start EVDelivery"
        sleep 30
        PID=(`/bin/ps -eaf | $LOC_GREP java | $LOC_GREP log4j-evdelivery | $LOC_AWK '{print $2}'`)
        PLEN=${#PID[@]}
        echo "EVDelivery $PLEN"
    done
fi
