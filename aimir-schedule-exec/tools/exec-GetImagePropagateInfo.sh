#!/bin/bash

. /home/aimir/.bashrc

#### GetImagePropagateInfo parameter ###
MCU_ID="778"
#0x06: 3rd-party Coordinator
#0x07: 3rd-party Modem
UPGRADE_TYPE="0x06"
##################################

AIMIR_HOME=/home/aimir1/aimiramm.3.3
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
#    MVNREPOSITORY=~/.m2/repository
     MVNREPOSITORY=/home/aimir1/aimiramm/aimir-project-lib
fi


mvn -e -f $AIMIR_TASK/pom-GetImagePropagateInfo.xml antrun:run -DtaskName=GetImagePropagateInfo -DmcuId=$MCU_ID -DupgradeType=$UPGRADE_TYPE -Dspring.instrument.path=$MVNREPOSITORY/org/springframework/spring-instrument/4.2.5.RELEASE 2>&1 > /dev/null &

#mvn -X -e -f $AIMIR_TASK/pom-GetImagePropagateInfo.xml antrun:run -DtaskName=GetImagePropagateInfo -DmcuId=$MCU_ID -DupgradeType=$UPGRADE_TYPE -Dspring.instrument.path=$MVNREPOSITORY/org/springframework/spring-instrument/4.2.5.RELEASE
