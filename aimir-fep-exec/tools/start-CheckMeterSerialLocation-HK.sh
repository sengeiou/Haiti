#!/bin/sh

. /home/aimirtmp/.bashrc

AIMIR_HOME=/home/aimirtmp/aimiramm
AIMIR_TASK=$AIMIR_HOME/aimir-fep-exec

CHK_PARAM=(`which grep | wc`)
if [ $CHK_PARAM -ge 2 ]
then
	LOC_GREP=(`which --skip-alias grep`)
	LOC_AWK=(`which --skip-alias awk`)
	LOC_CAT=(`which --skip-alias cat`)
else
	LOC_GREP=(`which grep`)
	LOC_AWK=(`which awk`)
	LOC_CAT=(`which cat`)
fi
MVNREPOSITORY=(`$LOC_GREP localRepository ~/.m2/settings.xml | $LOC_AWK -F "[><]" '{print $3}'`)
if [ "$MVNREPOSITORY" == "" ]
then
	MVNREPOSITORY=~/.m2/repository
fi

count=`/bin/ps -eaf | grep pom-CheckMeterSerialLocation.xml | grep antrun:run | grep -v grep | wc -l`
if [ $count -gt 0 ]; then
    echo "process is running."
#    exit 1
fi

#export PATH=$PATH:/home/aimir1/jvm/jdk1.8.0_73/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games:/etc/tomcat7/bin
#export JAVA_HOME=/home/aimir1/jvm/jdk1.8.0_73

##################################
LOCATION=HK
#MSA=01
MDSID=
MAXTHREADMCU=15
MAXTHREADMMIU=5
TIMEOUTMCU=3600
TIMEOUTMMIU=3600
RETRY=3
TESTMODE=0
#- HK MSA List ----------
MSALIST_BS="11 - Haugaland Kraft
05
9 - Haugaland Kraft
5 - HK Cleanup
107 - Haugaland
104 - Haugaland
N/A
04
5 - Haugaland Kraft
3 - Haugaland Kraft
12 - Haugaland Kraft
15 - Skånevik Ølen Kraftlag
1 - Haugaland Kraft
03
15a
2 - HK Cleanup
110 - Haugaland
103
23 - SKL
108 - Haugaland
103 - Haugaland
105
10 - Haugaland Kraft
(null)
07
02
105 - Haugaland
24 - SKL
101 - Haugaland
23
10
2 - Haugaland Kraft
7 - Haugaland Kraft
Område 15 Skånevik
3 - HK Cleanup
4 - HK Cleanup
Ukjent
4 - Haugaland Kraft
6 - Haugaland Kraft
8 - Haugaland Kraft
01"
#----------
##################################
#LOG=$AIMIR_TASK/CheckMeterSerialLocation.log
LOG=/dev/null


count=`ps -eaf | grep pom-CheckMeterSerialLocation.xml | grep -v grep | wc -l`
if [ $count -gt 0 ]; then
 echo "process is running."
 exit 1
fi


IFS_BAK=$IFS
IFS="
"

CURMSA=`cat $AIMIR_TASK/result/CheckMeterSerialLocation-curMsa-$LOCATION.txt 2>/dev/null`
#echo CURMSA=$CURMSA
curflag=off
for msa in $MSALIST_BS
do
	if [ "$CURMSA" = "$msa" ]; then
		cur_flag=start
		continue
	fi

	if [ "$cur_flag" = "start" ]; then
		MSALIST="$msa"
		cur_flag=on
		continue
	fi

	if [ "$cur_flag" = "on" ]; then
		MSALIST="$MSALIST
$msa"
	fi
done

for msa in $MSALIST_BS
do
	if [ "$cur_flag" = "off" ]; then
		MSALIST=$MSALIST_BS
		break
	fi

	if [ "$cur_flag" = "start" ]; then
		MSALIST=$MSALIST_BS
		break
	fi

	MSALIST="$MSALIST
$msa"

	if [ "$CURMSA" = "$msa" ]; then
		break
	fi
done


for msa in $MSALIST
do
	msatmp=$msa
	echo $msa > $AIMIR_TASK/result/CheckMeterSerialLocation-curMsa-$LOCATION.txt

	if [ "$msa" = "(null)" ]; then
		msatmp=""
	else
		msatmp=$msa
	fi

	mvn --debug -e -f $AIMIR_TASK/pom-CheckMeterSerialLocation.xml antrun:run -Dspring.instrument.path=$AIMIR_HOME/aimir-project-lib/org/springframework/spring-instrument/4.2.5.RELEASE -Dlocation=$LOCATION -Dmsa="$msatmp" -Dmdsid=$MDSID -Dmaxthreadmcu=$MAXTHREADMCU -Dmaxthreadmmiu=$MAXTHREADMMIU -Dtimeoutmcu=$TIMEOUTMCU -Dtimeoutmmiu=$TIMEOUTMMIU -Dretry=$RETRY -Dtestmode=$TESTMODE > $LOG
done

IFS=$IFS_BAK

