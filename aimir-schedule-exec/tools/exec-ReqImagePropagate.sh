#!/bin/bash

. /home/aimir/.bashrc

#### ReqImagePropagate parameter ###
MCU_ID="778"
#0x06: 3rd-party Coordinator
#0x07: 3rd-party Modem
#UPGRADE_TYPE="0x06"
UPGRADE_TYPE="0x07"
#Fixed value
CONTROL="0xFF"
#Unique ID (random generated and unique value) UNIT is "WORD"
#IMAGE_KEY="0001"
IMAGE_KEY="0002"
#IMAGE_URL="http://172.16.10.111:8085/firmware-file/fw/modem/NURITelecom/NAMR-P214SR/0152/HQ_GREEN_ROUTER_0152_0152.bin"
IMAGE_URL="http://172.16.10.111:8085/firmware-file/fw/dcu-coordinate/NURITelecom/NDC-I336/0152/HQ_GREEN_COORDI_0152_0152.bin"
#CHECKSUM="af0bf20"
CHECKSUM="26f47f6"
IMAGE_VERSION="0152"
#RF Coordinator Modem: "NCB-S201"
#RF Router Modem: "NAMR-P214SR"
#MBB Modem: "NAMR-P117LT"
#ETH Modem: "NAMR-P212ET"
#Kaifa Sungle Phase Meter: "KFSP"
#Kaifa Poly Phase Meter: "KFPP"
#TARGET_MODEL="NAMR-P214SR"
TARGET_MODEL="NAMR-P214SRTEST"
CLONE_COUNT="96"
MODEM_LIST="000B120000000002"
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


mvn -e -f $AIMIR_TASK/pom-ReqImagePropagate.xml antrun:run -DtaskName=ReqImagePropagate -DmcuId=$MCU_ID -DupgradeType=$UPGRADE_TYPE -Dcontrol=$CONTROL -DimageKey=$IMAGE_KEY -DimageUrl=$IMAGE_URL -DcheckSum=$CHECKSUM -DimageVersion=$IMAGE_VERSION -DtargetModel=$TARGET_MODEL -DmodemList=$MODEM_LIST -Dspring.instrument.path=$MVNREPOSITORY/org/springframework/spring-instrument/4.2.5.RELEASE 2>&1 > /dev/null &

#mvn -X -e -f $AIMIR_TASK/pom-ReqImagePropagate.xml antrun:run -DtaskName=ReqImagePropagate -DmcuId=$MCU_ID -DupgradeType=$UPGRADE_TYPE -Dcontrol=$CONTROL -DimageKey=$IMAGE_KEY -DimageUrl=$IMAGE_URL -DcheckSum=$CHECKSUM -DimageVersion=$IMAGE_VERSION -DtargetModel=$TARGET_MODEL -DcloneCount=$CLONE_COUNT -DmodemList=$MODEM_LIST -Dspring.instrument.path=$MVNREPOSITORY/org/springframework/spring-instrument/4.2.5.RELEASE
