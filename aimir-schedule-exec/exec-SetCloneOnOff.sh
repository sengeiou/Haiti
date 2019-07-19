#!/bin/bash

. /home/aimir/.bashrc

#### SetCloneOnOff parameter ###
MODEM_ID=""
#0314:Use own image (auto-propagation X)
#0315:Use own image (auto- propagation O)  
#8798:Use system image (auto-propagation X)
#8799:Use system image (auto- propagation O)
CLONE_CODE="0314"
COUNT="20"
VERSION=""
EUI_COUNT="0"
EUI_LIST=""
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


#mvn -e -f $AIMIR_TASK/pom-SetCloneOnOff.xml antrun:run -DtaskName=SetCloneOnOff -DmodemId=$MODEM_ID -DcloneCode=$CLONE_CODE -Dcount=$COUNT -Dversion=$VERSION -DeuiCount=$EUI_COUNT -DeuiList=$EUI_LIST -Dspring.instrument.path=$MVNREPOSITORY/org/springframework/spring-instrument/4.2.5.RELEASE 2>&1 > /dev/null &

mvn -X -e -f $AIMIR_TASK/pom-SetCloneOnOff.xml antrun:run -DtaskName=SetCloneOnOff -DmodemId=$MODEM_ID -DcloneCode=$CLONE_CODE -Dcount=$COUNT -Dversion=$VERSION -DeuiCount=$EUI_COUNT -DeuiList=$EUI_LIST -Dspring.instrument.path=$MVNREPOSITORY/org/springframework/spring-instrument/4.2.5.RELEASE

