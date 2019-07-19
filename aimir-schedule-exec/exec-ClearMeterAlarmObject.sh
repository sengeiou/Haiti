#!/bin/bash

. /home/aimir/.bashrc

#### Recollect Metering Soria parameter ###
# specify Meter or Modem for DEVICE_TYPE
#DEVICE=
#USE_ASYNC_CHANNEL=true
#CLEAR_METER_ATTR=true
#MODEM_TYPE is ALL|SubGiga|MMIU
#MODEM_TYPE=ALL
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

mvn -e -f $AIMIR_TASK/pom-ClearMeterAlarmObject.xml antrun:run -DtaskName=ClearMeterAlarmObject -Ddevice=$DEVICE -DuseAsyncChannel=$USE_ASYNC_CHANNEL -DmodemType=$MODEM_TYPE -DclearMeterAttr=$CLEAR_METER_ATTR -Dspring.instrument.path=$MVNREPOSITORY/org/springframework/spring-instrument/4.2.5.RELEASE 2>&1 > /dev/null &
