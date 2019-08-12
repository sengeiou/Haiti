#!/bin/bash

. /home/aimir/.bashrc
#. /home/aimirtmp/.bashrc

#### Recollect Metering Soria parameter ###
# specify Meter or Modem for DEVICE_TYPE
DEVICE_TYPE=MCU
SUPPLIER_NAME=SORIA
# specify yyyymmdd or yyyymmddhh for FROM_DATE and TO_DATE, if not specify, recollect yesterday
FROM_DATE=""
TO_DATE=""
METER_ID=""

today=`date '+%Y-%m-%d'`
offset=2
count=1
countmax=6
interval=1
##################################

if [ $# -eq 1 ]; then
	expr $1 + 1 > /dev/null 2>&1
	RET=$?
	if [ $RET -lt 2 ]; then
		countmax=$1
	fi

elif [ $# -eq 2 ]; then
	expr $1 + 1 > /dev/null 2>&1
	RET=$?
	if [ $RET -lt 2 ]; then
		countmax=$1
	fi

	expr $2 + 1 > /dev/null 2>&1
	RET=$?
	if [ $RET -lt 2 ]; then
		offset=$2
	fi
fi

AIMIR_HOME=/home/aimir/aimir4/aimiramm
#AIMIR_HOME=/home/aimirtmp/aimiramm
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

	mvn -e -f $AIMIR_TASK/pom-RecollectMeteringSoria.xml antrun:run -DtaskName=RecollectMeteringSoria -DdeviceType=$DEVICE_TYPE -DsupplierName=$SUPPLIER_NAME -DfromDate=$FROM_DATE -DtoDate=$TO_DATE -DmeterId=$METER_ID -Dspring.instrument.path=$MVNREPOSITORY/org/springframework/spring-instrument/4.2.5.RELEASE 2>&1 > /dev/null

#	mvn -e -f $AIMIR_TASK/pom-RecollectMeteringSoria.xml antrun:run -DtaskName=RecollectMeteringSoria -DdeviceType=$DEVICE_TYPE -DsupplierName=$SUPPLIER_NAME -DfromDate=$FROM_DATE -DtoDate=$TO_DATE -DmeterId=$METER_ID -Dspring.instrument.path=$MVNREPOSITORY/org/springframework/spring-instrument/4.2.5.RELEASE 2>&1

	count=`expr $count + 1`
done

