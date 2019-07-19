#!/bin/bash

. /home/aimir/.bashrc

#### Recollect Metering Soria parameter ###
# specify Meter or Modem for DEVICE_TYPE
DEVICE_TYPE=MCU
#DEVICE_TYPE=Meter
SUPPLIER_NAME=SORIA
# specify yyyymmdd or yyyymmddhh for FROM_DATE and TO_DATE, if not specify, recollect yesterday
FROM_DATE=""
TO_DATE=""
METER_ID=""

#Specify DSO name to be recollected.DSO name is enclosed in single quotation.
#To specify multiple DSO, separate them with a comma.
#If this option is not specified, recollect to all DSO.
#ex)DSO="'LocationName1','LocationName2'"
DSO="'BKK','NTE','VRN','ETEL'"

# Specify Recollect(Meter or Modem) type for MBB.
#If this option is not specified,  recollect type is Modem.
MBBWITHMCU=""

# Specify the devicetype(MCU or SMS or GPRS) to exclude Recollect.
#To specify multiple type, separate them with a comma.
EXCLUDETYPE=""

# 0:targetsla30=0 or null, 1:targetsla30=1  "": not use sla table
SLA="1"
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

mvn -e -f $AIMIR_TASK/pom-RecollectMeteringSoria.xml antrun:run -DtaskName=RecollectMeteringSoria -DdeviceType=$DEVICE_TYPE -DmeterListFile=$FILE -DsupplierName=$SUPPLIER_NAME -DfromDate=$FROM_DATE -DtoDate=$TO_DATE -DmeterId=$METER_ID -Ddso=$DSO -DmbbWithMcu=$MBBWITHMCU -DexcludeType=$EXCLUDETYPE -Dsla=$SLA -Dspring.instrument.path=$MVNREPOSITORY/org/springframework/spring-instrument/4.2.5.RELEASE 2>&1 > /dev/null &

