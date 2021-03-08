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

##################################
LOCATION=""
MSA=""
MDS_ID=""
WORKER=""
TIMEOUT=""
TESTMODE=0

##################################

if [ $# -eq 1 ]; then
    LOCATION=$1
    MSA=""
elif [ $# -ge 2 ]; then
    LOCATION=$1
    MSA=$2
fi


count=`ps -eaf | grep pom-UpdateMeterFWVersion.xml | grep -v grep | wc -l`
if [ $count -gt 0 ]; then
 echo "process is running."
 exit 1
fi


mvn -e -f $AIMIR_HOME/aimir-fep-exec/pom-UpdateMeterFWVersion.xml antrun:run -DtaskName=UpdateMeterFWVersion -Dspring.instrument.path=$AIMIR_HOME/aimir-project-lib/org/springframework/spring-instrument/4.2.5.RELEASE -Dlocation=$LOCATION -Dmsa=$MSA -Dmds_id=$MDS_ID -Dworker=$WORKER -Dtimeout=$TIMEOUT -Dtestmode=$TESTMODE 2>&1 > /dev/null &

#mvn --debug -e -f $AIMIR_HOME/aimir-fep-exec/pom-UpdateMeterFWVersion.xml antrun:run -DtaskName=UpdateMeterFWVersion -Dspring.instrument.path=$AIMIR_HOME/aimir-project-lib/org/springframework/spring-instrument/4.2.5.RELEASE -Dlocation=$LOCATION -Dmsa=$MSA -Dmds_id=$MDS_ID -Dworker=$WORKER -Dtimeout=$TIMEOUT
