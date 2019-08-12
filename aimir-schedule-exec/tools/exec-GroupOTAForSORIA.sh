#!/bin/bash

. /home/aimir/.bashrc

#### Group OTA For SORIA parameter ###
LOCATION_NAME=SSYS
DIRECTLY=true
DEVICE_MODEL_NAME=NAMR-P214SR
FIRMWARE_FILE_NAME=NAMR-P214SR-soria-0016-OTA_Test
FIRMWARE_VERSION=0016
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



mvn -e -f $AIMIR_TASK/pom-GroupOTAForSORIA.xml antrun:run -DtaskName=GroupOTAForSORIA -location=$LOCATION_NAME -Ddirectly=$DIRECTLY -DdeviceModelName=$DEVICE_MODEL_NAME -DfirmwareFileName=$FIRMWARE_FILE_NAME -DfirmwareVersion=$FIRMWARE_VERSION -Dspring.instrument.path=$MVNREPOSITORY/org/springframework/spring-instrument/4.2.5.RELEASE 2>&1 > /dev/null &