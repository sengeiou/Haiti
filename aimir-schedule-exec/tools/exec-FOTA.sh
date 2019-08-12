#!/bin/bash

. /home/aimir/.bashrc

#### FOTA parameter ###
# specify yyyymmdd or yyyymmddhh for FROM_DATE and TO_DATE, if not specify, recollect yesterday
AIMIR_HOME=/home/aimir/aimir4/aimiramm
AIMIR_TASK=$AIMIR_HOME/aimir-schedule-exec

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


#mvn -e -f $AIMIR_TASK/pom-FOTA.xml antrun:run -DtaskName=TelitModuleUpgradeTask -Dspring.instrument.path=/home/aimirtest/aimiramm/aimir-project-lib/org/springframework/spring-instrument/4.2.5.RELEASE  2>&1 > /dev/null &
mvn -e -f $AIMIR_TASK/pom-FOTA.xml antrun:run -DtaskName=TelitModuleUpgradeTask -Dspring.instrument.path=$MVNREPOSITORY/org/springframework/spring-instrument/4.2.5.RELEASE

