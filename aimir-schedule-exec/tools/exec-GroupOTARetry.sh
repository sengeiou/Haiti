#!/bin/bash

. /home/aimir/.bashrc

#### Group OTA Retry parameter ###
FIRMWARE_VERSION=06010059
FIRMWARE_FILE_NAME=KFPP_V06010059
DEVICE_MODEL_NAME=MA304T4
LOCATION_NAME=SSYS
ISSUE_DATE=20160930152053
##################################

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

mvn -e -f $AIMIR_TASK/pom-GroupOTARetry.xml antrun:run -DtaskName=GroupOTARetry -DfirmwareVersion=$FIRMWARE_VERSION -DfirmwareFileName=$FIRMWARE_FILE_NAME -DdeviceModelName=$DEVICE_MODEL_NAME -DlocationName=$LOCATION_NAME -DissueDate=$ISSUE_DATE -Dspring.instrument.path=$MVNREPOSITORY/org/springframework/spring-instrument/4.2.5.RELEASE 2>&1 > /dev/null &