#!/bin/sh

#PID=`/bin/ps -eaf | /bin/grep log4j-fepd`
PID=`/bin/ps -eaf | /bin/grep java | /bin/grep FEP1 | /bin/grep fepd | /bin/awk '{print $2}'`

for pid in $PID
do
        echo "kill -9 $pid"
        kill -9 $pid
done

echo "Complete kill fepd!"

