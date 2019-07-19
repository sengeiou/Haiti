#!/bin/bash

. /home/aimir/.bashrc

#### Recollect Metering Soria parameter ###
# specify Meter or Modem for DEVICE_TYPE
DEVICE_TYPE=Modem
SUPPLIER_NAME=SORIA
# specify Meter Type
METER_TYPE=EnergyMeter
#SHOW_LIST=true
#SYS_ID=
#METER_ID=
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

mvn -e -f $AIMIR_TASK/pom-RecollectMeteringSoria3.xml antrun:run -DtaskName=RecollectMeteringSoria3 -DdeviceType=$DEVICE_TYPE -DsupplierName=$SUPPLIER_NAME -DmeterType=$METER_TYPE -DmeterId=$METER_ID -DsysId=$SYS_ID -Dshowlist=$SHOW_LIST -Dspring.instrument.path=$MVNREPOSITORY/org/springframework/spring-instrument/4.2.5.RELEASE 
