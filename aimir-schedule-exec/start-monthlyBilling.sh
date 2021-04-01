#!/bin/sh

PID=`/bin/ps -eaf | /bin/grep java | /bin/grep EDHMonthlyBillingTask | /bin/awk '{print $2}'`
for pid in $PID
do
        echo "kill -9 $pid"
        kill -9 $pid
done

mvn -e -f /home/aimir/aimiramm.dev/aimir-schedule-exec/pom-EDHMonthlyBillingTask.xml -DmdevId= antrun:run 2>&1 > /dev/null &