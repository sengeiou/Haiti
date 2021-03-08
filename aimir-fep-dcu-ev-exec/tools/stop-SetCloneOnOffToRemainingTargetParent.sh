#!/bin/sh

batchToolName=SetCloneOnOffToRemainingTargetParent
PID=`/bin/ps -eaf | /bin/grep java | /bin/grep $batchToolName | /bin/awk '{print $2}'`

for pid in $PID
do
	echo "kill -9 $pid"
	kill -9 $pid
done
