#!/bin/bash

. /home/aimir/.bashrc

CHK_ALIVE=(`netstat -ano|grep :::8001 | grep LISTEN | wc -l`)
if [ $CHK_ALIVE  -ge 1 ]
then
echo "feph is working"
else
echo start

CHK_PARAM=(`which grep | wc`)
if [ $CHK_PARAM -ge 2 ]
then
    LOC_GREP=(`which --skip-alias grep`)
    LOC_AWK=(`which --skip-alias awk`)
else
    LOC_GREP=(`which grep`)
    LOC_AWK=(`which awk`)
fi
PID=(`/bin/ps -eaf | $LOC_GREP java | $LOC_GREP log4j-multispeak | $LOC_AWK '{print $2}'`)
PLEN=${#PID[@]}
MVNREPOSITORY=(`$LOC_GREP localRepository ~/.m2/settings.xml | $LOC_AWK -F "[><]" '{print $3}'`)
if [ "$MVNREPOSITORY" == "" ]
then
    MVNREPOSITORY=~/.m2/repository
fi

cd /home/aimir/aimiramm/aimir-fep-dcu-ev-exec
mvn -e -f pom-feph.xml antrun:run -DfepName=FEPEV -Dif4Port=8001 -DniTcpPort=18041 -DniUdpPort=18042 -DauthTcpPort=19041 -DauthUdpPort=19042 -DcommandPort=18940 -DjmxPort=11049 2>&1 > /dev/null &
#mvn -e -f pom-feph.xml antrun:run -DfepName=FEPEV -Dif4Port=8000 -DniTcpPort=8001 -DniUdpPort=8002 -DcommandPort=8900 -DniPanaPort=8004 -DjmxPort=1099 2>&1 > /dev/null &

#mvn -e -f pom-feph.xml antrun:run -DfepName=FEP1 -Dif4Port=8000 -DniTcpPort=8001 -DniUdpPort=8002 -DcommandPort=8900 -DjmxPort=1099 -Dspring.instrument.path=$MVNREPOSITORY/org/springframework/spring-instrument/4.2.5.RELEASE 2>&1 > /dev/null &

fi

