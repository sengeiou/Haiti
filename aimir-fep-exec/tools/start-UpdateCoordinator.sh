#!/bin/bash

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

count=`/bin/ps -eaf | grep pom-UpdateCoordinator.xml | grep antrun:run | grep -v grep | wc -l`
if [ $count -gt 0 ]; then
    echo "process is running."
#    exit 1
fi

##################################
# dcu list file
DCUCSV="/home/aimirtmp/aimiramm/aimir-fep-exec/UpdateCoordinator/"

#DEV=DEV
DEV=""

##################################

if [ $# -eq 1 ]; then
    DCUCSV=$1
elif [ $# -ge 2 ]; then
    DCUCSV=$1
    DEV=$2
fi


mvn --debug -e -f $AIMIR_HOME/aimir-fep-exec/pom-UpdateCoordinator.xml antrun:run -DtaskName=UpdateCoordinator -Dspring.instrument.path=$AIMIR_HOME/aimir-project-lib/org/springframework/spring-instrument/4.2.5.RELEASE -Ddcucsv=$DCUCSV -Ddev=$DEV 

#mvn -e -f $AIMIR_HOME/aimir-fep-exec/pom-UpdateCoordinator.xml antrun:run -DtaskName=UpdateCoordinator -Dspring.instrument.path=$AIMIR_HOME/aimir-project-lib/org/springframework/spring-instrument/4.2.5.RELEASE -Ddcucsv=$DCUCSV -Ddev=$DEV  2>&1 > /dev/null &


