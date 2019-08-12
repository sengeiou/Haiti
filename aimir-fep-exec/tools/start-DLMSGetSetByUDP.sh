#!/bin/sh

### Properties ####
# args Param list
# 1. destIp             : 타겟 서버 IP
# 2. destPort           : 타겟 서버 Port
# 3. srcPort            : 서버 Port. 메시지 수신을 위해 열어놓고자하는 UDP서버의 Port. 음수값의경우 서버에서 자동할당.
# 4. connectionTimeout  : 타셋 서버에 접속하는 Connection time out (s)
# 5. resultCheckTimeout : 결과값 올때까지 기다리는 시간.  (s)
# 6. sendByteData       : 전송하고자 하는 바이트데이터 (Hex string)
####################
batchToolName=DLMSGetSetByUDP
destIp=fd00:2:1:0:210:ff:fe00:5d
destPort=9001
srcPort=-1
connectionTimeout=10
resultCheckTimeout=10
sendByteData=7EA020030393FEC98180140502010006020100070400000001080400000001696D7E



batchToolName=DLMSGetSetByUDP
count=`ps -eaf | grep pom-DLMSGetSet.xml | grep -v grep | wc -l`
if [ $count -gt 0 ]; then
 echo "process is running."
 exit 1
fi
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



mvn -e -f pom-DLMSGetSet.xml antrun:run -DbatchToolName=$batchToolName -DdestIp=$destIp -DdestPort=$destPort -DsrcPort=$srcPort -DconnectionTimeout=$connectionTimeout -DresultCheckTimeout=$resultCheckTimeout -DsendByteData=$sendByteData > ./DLMSGetSet.log &


