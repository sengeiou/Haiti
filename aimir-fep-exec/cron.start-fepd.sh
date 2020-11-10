#!/bin/bash

. /home/aimir/.bashrc

CHK_ALIVE=(`netstat -ano|grep :::1299 | grep LISTEN | wc -l`)
if [ $CHK_ALIVE  -ge 1 ]
then
echo "fepd is working"
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

cd /home/aimir/aimiramm.dev/aimir-fep-exec
mvn -e -f pom-fepd.xml antrun:run -DfepName=FEP1 -DjmxPort=1299 -DenableWS=true -Dspring.instrument.path=$MVNREPOSITORY/org/springframework/spring-instrument/4.2.5.RELEASE 2>&1 > fepd.log & 

#mvn -e -f pom-fepd.xml antrun:run -DfepName=FEP1 -DjmxPort=1299 -DenableWS=true -Dspring.instrument.path=$MVNREPOSITORY/org/springframework/spring-instrument/4.2.5.RELEASE 2>&1 > /dev/null & 

fi

