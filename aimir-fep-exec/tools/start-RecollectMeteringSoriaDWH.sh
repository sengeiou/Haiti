#!/bin/sh

. /home/aimir/.bashrc

AIMIR_HOME=/home/aimir/aimir4/aimiramm
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

count=`/bin/ps -eaf | grep pom-RecollectMeteringSoriaDWH.xml | grep antrun:run | grep -v grep | wc -l`
if [ $count -gt 0 ]; then
    echo "process is running."
#    exit 1
fi

#export PATH=$PATH:/home/aimir1/jvm/jdk1.8.0_73/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games:/etc/tomcat7/bin

#export JAVA_HOME=/home/aimir1/jvm/jdk1.8.0_73

##################################
DEVICE_TYPE=Meter
#DSO=
#-DsmsJoinMin = $SMS_JOIN_MIN
SMS_JOIN_MIN=1440
#-DmaxThreadSubGiga
MAX_THREAD_SUBGIGA=100
#-DmaxThreadMmiuGprs
MAX_THREAD_MMIU_GPRS=100
#MODEMTYPE is SubGiga,MMIUGPRS,MMIUSMS
MODEM_TYPE=ALL
##################################



count=`ps -eaf | grep pom-RecollectMeteringSoriaDWH.xml | grep -v grep | wc -l`
if [ $count -gt 0 ]; then
 echo "process is running."
 exit 1
fi

#mvn -e -f $AIMIR_HOME/aimir-fep-exec/pom-RecollectMeteringSoriaDWH.xml antrun:run -Dmaven.repo.local=$AIMIR_HOME/aimir-project-lib -Dspring.instrument.path=$AIMIR_HOME/aimir-project-lib/org/springframework/spring-instrument/4.2.5.RELEASE -DdeviceType=$DEVICE_TYPE -DmaxThreadSubGiga=$MAX_THREAD_SUBGIGA -DmaxThreadMmiuGprs=$MAX_THREAD_MMIU_GPRS -DsmsJoinMin=$SMS_JOIN_MIN -Ddso=$DSO -DmodemType=$MODEM_TYPE 2>&1 > /dev/null &
mvn -e -f $AIMIR_HOME/aimir-fep-exec/pom-RecollectMeteringSoriaDWH.xml antrun:run -Dspring.instrument.path=$AIMIR_HOME/aimir-project-lib/org/springframework/spring-instrument/4.2.5.RELEASE -DdeviceType=$DEVICE_TYPE -DmaxThreadSubGiga=$MAX_THREAD_SUBGIGA -DmaxThreadMmiuGprs=$MAX_THREAD_MMIU_GPRS -DsmsJoinMin=$SMS_JOIN_MIN -Ddso=$DSO -DmodemType=$MODEM_TYPE 2>&1 > /dev/null & 
