#!/bin/bash

source ./setenv.sh

PID=(`/bin/ps -eaf | $LOC_GREP java | $LOC_GREP log4j-metercontrol | $LOC_AWK '{print $2}'`)
PLEN=${#PID[@]}

if [ $PLEN -ge 1 ]
then 
    echo "Metercontrol alive, try to stop"
else
    while [ $PLEN -eq 0 ]
    do
        mvn -o -e -f pom-metercontrol-ws.xml antrun:run -DfepName=Metercontrol  -DhttpsPort=8450 -DconfigFile=config/spring-metercontrol-ws.xml -DjmxPort=1198 -DmvnRepository=$MVNREPOSITORY > /dev/null 2>&1 &
        echo "start Metercontrol"
        sleep 60
        PID=(`/bin/ps -eaf | $LOC_GREP java | $LOC_GREP log4j-metercontrol | $LOC_AWK '{print $2}'`)
        PLEN=${#PID[@]}
        echo "Metercontrol $PLEN"
    done
fi
