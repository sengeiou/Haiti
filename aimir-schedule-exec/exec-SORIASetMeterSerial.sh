#!/bin/bash

. /home/aimir/.bashrc


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


echo ""
echo "###################################################"
echo "### SORIA Kaifa Meter - set meter serial  batch Tool ###"
echo "###################################################"
echo ""

nohup mvn -e -f pom-SORIASetMeterSerial.xml antrun:run -DmbbCommandWaitTime -Dspring.instrument.path=$MVNREPOSITORY/org/springframework/spring-instrument/4.2.5.RELEASE > /dev/null 2>&1 &
