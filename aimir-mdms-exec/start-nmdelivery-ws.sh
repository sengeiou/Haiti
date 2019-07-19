#!/bin/bash

source ./setenv.sh

PID=(`/bin/ps -eaf | $LOC_GREP java | $LOC_GREP spring-nm-integration-ws.xml | $LOC_AWK '{print $2}'`)
PLEN=${#PID[@]}

if [ $PLEN -ge 1 ]
then 
    echo "NMDelivery alive, try to stop"
else
    mvn -o -e -f pom-nmdelivery.xml antrun:run -DfepName=NMDelivery -DconfigFile=config/spring-nm-integration-ws.xml -DmvnRepository=$MVNREPOSITORY > /dev/null 2>&1 &
    echo "start NMDelivery"
fi
