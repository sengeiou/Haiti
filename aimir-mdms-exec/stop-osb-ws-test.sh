#!/bin/sh
CHK_PARAM=(`which grep | wc`)
if [ $CHK_PARAM -ge 2 ]
then
    LOC_GREP=(`which --skip-alias grep`)
    LOC_AWK=(`which --skip-alias awk`)
else
    LOC_GREP=(`which grep`)
    LOC_AWK=(`which awk`)
fi

PID=`/bin/ps -eaf | $LOC_GREP java | $LOC_GREP spring-osb-ws-test.xml | $LOC_AWK '{print $2}'`

for pid in $PID
do
	echo "kill -9 $pid"
	kill -9 $pid
done
