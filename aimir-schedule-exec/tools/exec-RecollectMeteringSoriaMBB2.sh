#!/bin/bash

#. /home/aimir/.bashrc

#### Recollect Metering Soria parameter ###
# specify Meter or Modem for DEVICE_TYPE
DEVICE_TYPE=Modem
SUPPLIER_NAME=SORIA
#FROM_DATE=20180312
#TO_DATE=20180313
FROM_DATE=""
TO_DATE=""
# specify Meter Type
METER_TYPE=EnergyMeter
#SHOW_LIST=true
#SYS_ID=
#METER_ID=6970631400812621
FORCE=false
USE_ASYNC_CHANNEL=true

today=`date '+%Y-%m-%d'`
offset=2
count=1
countmax=6
interval=1
##################################

#AIMIR_HOME=/home/aimir/aimiramm
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

while true
do
	if [ $count -gt $countmax ]; then
		break
	fi

	if [ $count -gt 1 ]; then
		sleep $interval
	fi

	nDays=`expr $countmax - $count + $offset`

	FROM_DATE=`date -d "$today $nDays days ago" '+%Y%m%d'`
	TO_DATE=$FROM_DATE

	mvn -e -f $AIMIR_TASK/pom-RecollectMeteringSoriaMBB.xml antrun:run -DtaskName=RecollectMeteringSoriaMBB -DdeviceType=$DEVICE_TYPE -DsupplierName=$SUPPLIER_NAME -DfromDate=$FROM_DATE -DtoDate=$TO_DATE -DmeterType=$METER_TYPE -DmeterId=$METER_ID -DsysId=$SYS_ID -Dshowlist=$SHOW_LIST -Dforce=$FORCE -DuseAsyncChannel=$USE_ASYNC_CHANNEL -Dspring.instrument.path=$MVNREPOSITORY/org/springframework/spring-instrument/4.2.5.RELEASE 2>&1 > /dev/null
#	mvn -e -f $AIMIR_TASK/pom-RecollectMeteringSoriaMBB.xml antrun:run -DtaskName=RecollectMeteringSoriaMBB -DdeviceType=$DEVICE_TYPE -DsupplierName=$SUPPLIER_NAME -DfromDate=$FROM_DATE -DtoDate=$TO_DATE -DmeterType=$METER_TYPE -DmeterId=$METER_ID -DsysId=$SYS_ID -Dshowlist=$SHOW_LIST -Dforce=$FORCE -DuseAsyncChannel=$USE_ASYNC_CHANNEL -Dspring.instrument.path=$MVNREPOSITORY/org/springframework/spring-instrument/4.2.5.RELEASE 2>&1

	count=`expr $count + 1`
done
