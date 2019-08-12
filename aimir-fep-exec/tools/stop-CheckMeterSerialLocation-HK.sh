#!/bin/sh


CHK_PARAM=(`which grep | wc`)
if [ $CHK_PARAM -ge 2 ]
then
	LOC_GREP=(`which --skip-alias grep`)
	LOC_AWK=(`which --skip-alias awk`)
	LOC_KILL=(`which --skip-alias kill`)
else
	LOC_GREP=(`which grep`)
	LOC_AWK=(`which awk`)
	LOC_KILL=(`which kill`)
fi

list=`/bin/ps -eaf | grep start-CheckMeterSerialLocation-HK.sh | grep -v grep | awk '{print $2}'`
for pid in $list
do
	/bin/kill $pid
done

list=`/bin/ps -eaf | grep pom-CheckMeterSerialLocation.xml | grep antrun:run | grep -v grep | awk '{print $2}'`
for pid in $list
do
	/bin/kill $pid
done
