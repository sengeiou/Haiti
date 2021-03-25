#!/bin/sh
mvn -e -f /home/aimir/aimiramm.dev/aimir-schedule-exec/pom-HaitiSMSTask.xml -DsmsType=DIRECT antrun:run 2>&1 > /dev/null &