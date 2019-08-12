#!/bin/sh

. /home/aimir/.bashrc

AIMIR_HOME=/home/aimir/aimir4/aimiramm
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

#MVNREPOSITORY=/home/aimir/maintenance/recollect/aimiramm/aimir-project-lib

count=`/bin/ps -eaf | grep pom-RecollectMeteringSoriaForOneDay.xml | grep antrun:run | grep -v grep | wc -l`
if [ $count -gt 0 ]; then
    echo "process is running."
#    exit 1
fi

#### Recollect Metering Soria parameter ###
# specify Meter or Modem for DEVICE_TYPE
DEVICE_TYPE=MCU
#DEVICE_TYPE=Meter
SUPPLIER_NAME=SORIA
# specify yyyymmdd or yyyymmddhh for FROM_DATE and TO_DATE, if not specify, recollect yesterday
FROM_DATE=""
TO_DATE=""
METER_ID=""
#DSO="'BKK','HK'"
DSO="'HK','HKN','SFE','FIKL'"
MBBWITHMCU=""
EXCLUDETYPE=""
# 0:targetsla30=0, 1:targetsla30=1 null or "": not use sla table
SLA="1"
##################################


count=`ps -eaf | grep pom-RecollectMeteringSoriaForOneDay.xml | grep -v grep | wc -l`
if [ $count -gt 0 ]; then
 echo "process is running."
 exit 1
fi


NOW=`date +'%Y/%m/%d %H:%M:%S'`
echo $NOW "targetsla30="$SLA "Start" >> $AIMIR_TASK/RecollectMeteringSoria/ExecuteTime.log

mvn -e -f $AIMIR_TASK/pom-RecollectMeteringSoriaForOneDay.xml antrun:run -DtaskName=RecollectMeteringSoriaForOneDay -DdeviceType=$DEVICE_TYPE  -DsupplierName=$SUPPLIER_NAME -DfromDate=$FROM_DATE -DtoDate=$TO_DATE -DmeterId=$METER_ID -Ddso=$DSO -DmbbWithMcu=$MBBWITHMCU -DexcludeType=$EXCLUDETYPE  -Dsla=$SLA -Dspring.instrument.path=$MVNREPOSITORY/org/springframework/spring-instrument/4.2.5.RELEASE -Dmaven.repo.local=$MVNREPOSITORY 2>&1 > /dev/null
