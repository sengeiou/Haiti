#!/bin/sh

PID=`/bin/ps -eaf | /bin/grep java | /bin/grep SC | /bin/awk '{print $2}'`

for pid in $PID
do
        echo "kill -9 $pid"
        kill -9 $pid
done