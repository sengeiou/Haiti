#!/bin/bash

source ./setenv.sh

PID=(`/bin/ps -eaf | $LOC_GREP java | $LOC_GREP log4j-multispeak | $LOC_AWK '{print $2}'`)
PLEN=${#PID[@]}

if [ $PLEN -ge 1 ]
then 
    echo "Multispeak alive, try to stop"
else
    while [ $PLEN -eq 0 ]
    do
        mvn -o -e -f pom-multispeak-jms.xml antrun:run -DfepName=Multispeak -DconfigFile=./config/spring-multispeak-jms-weblogic.xml -DjmxPort=1399 -DmvnRepository=$MVNREPOSITORY > /dev/null 2>&1 &
        echo "start multispeak"
        sleep 30
        PID=(`/bin/ps -eaf | $LOC_GREP java | $LOC_GREP log4j-multispeak | $LOC_AWK '{print $2}'`)
        PLEN=${#PID[@]}
        echo "Multispeak $PLEN"
    done
fi
