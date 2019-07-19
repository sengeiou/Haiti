#!/bin/bash

source ./setenv.sh

PID=(`/bin/ps -eaf | $LOC_GREP java | $LOC_GREP log4j-mvdelivery | $LOC_AWK '{print $2}'`)
PLEN=${#PID[@]}

if [ $PLEN -ge 1 ]
then 
    echo "MVDelivery alive, try to stop"
else
    while [ $PLEN -eq 0 ]
    do
        mvn -o -e -f pom-mvdelivery.xml antrun:run -DfepName=MVDelivery -Dport=8089 -DconfigFile=config/spring-mv-integration-weblogic.xml -DjmxPort=1399 -DmvnRepository=$MVNREPOSITORY > /dev/null 2>&1 &
        echo "start MVDelivery"
        sleep 30
        PID=(`/bin/ps -eaf | $LOC_GREP java | $LOC_GREP log4j-mvdelivery | $LOC_AWK '{print $2}'`)
        PLEN=${#PID[@]}
        echo "MVDelivery $PLEN"
    done
fi
