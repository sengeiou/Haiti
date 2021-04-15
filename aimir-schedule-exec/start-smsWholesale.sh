#!/bin/sh

PID=`/bin/ps -eaf | /bin/grep java | /bin/grep HaitiSMSTask | /bin/awk '{print $2}'`
for pid in $PID
do
        echo "kill -9 $pid"
        kill -9 $pid
done

mvn -e -f /home/aimir/aimiramm/aimir-schedule-exec/pom-HaitiSMSTask.xml -DmdevId= -DsmsType=WHOLESALE antrun:run 2>&1 > /dev/null &