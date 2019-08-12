#!/bin/sh

. /home/aimirtmp/.bashrc

AIMIR_HOME=/home/aimirtmp/aimiramm
AIMIR_TASK=$AIMIR_HOME/aimir-fep-exec

CHK_PARAM=(`which grep | wc`)
if [ $CHK_PARAM -ge 2 ]
then
	LOC_GREP=(`which --skip-alias grep`)
	LOC_AWK=(`which --skip-alias awk`)
else
	LOC_GREP=(`which grep`)
	LOC_AWK=(`which awk`)
fi
MVNREPOSITORY=(`$LOC_GREP localRepository ~/.m2/settings.xml | $LOC_AWK -F "[><]" '{print $3}'`)
if [ "$MVNREPOSITORY" == "" ]
then
	MVNREPOSITORY=~/.m2/repository
fi

count=`/bin/ps -eaf | grep pom-CheckMeterSerialLocation.xml | grep antrun:run | grep -v grep | wc -l`
if [ $count -gt 0 ]; then
    echo "process is running."
#    exit 1
fi

#export PATH=$PATH:/home/aimir1/jvm/jdk1.8.0_73/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games:/etc/tomcat7/bin

#export JAVA_HOME=/home/aimir1/jvm/jdk1.8.0_73

##################################
LOCATION=HK
MSA=10
MDSID=
MAXTHREADMCU=10
MAXTHREADMMIU=5
TIMEOUTMCU=3600
TIMEOUTMMIU=3600
RETRY=2
TESTMODE=1
##################################



count=`ps -eaf | grep pom-CheckMeterSerialLocation.xml | grep -v grep | wc -l`
if [ $count -gt 0 ]; then
 echo "process is running."
 exit 1
fi

#/usr/bin/mvn -e -f /home/aimir1/aimiramm.3.3/aimir-fep-exec/pom-CheckMeterSerial.xml antrun:run -Dspring.instrument.path=/home/aimir1/.m2/repository/org/springframework/spring-instrument/4.2.5.RELEASE -Dtargetfile=$TARGETFILE -Dmaxthread=$MAXTHREAD -Dtimeout=$TIMEOUT -Dretry=$RETRY -Dtestmode=$TESTMODE 2>&1 > /dev/null &

mvn --debug -e -f /home/aimirtmp/aimiramm/aimir-fep-exec/pom-CheckMeterSerialLocation.xml antrun:run -Dspring.instrument.path=/home/aimirtmp/aimiramm/aimir-project-lib/org/springframework/spring-instrument/4.2.5.RELEASE -Dlocation=$LOCATION -Dmsa=$MSA -Dmdsid=$MDSID -Dmaxthreadmcu=$MAXTHREADMCU -Dmaxthreadmmiu=$MAXTHREADMMIU -Dtimeoutmcu=$TIMEOUTMCU -Dtimeoutmmiu=$TIMEOUTMMIU -Dretry=$RETRY -Dtestmode=$TESTMODE > ./CheckMeterSerialLocation.log &
