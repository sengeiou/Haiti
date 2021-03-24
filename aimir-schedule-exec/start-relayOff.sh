#!/bin/sh
mvn -e -f /home/aimir/aimiramm.dev/aimir-schedule-exec/pom-HaitiRelayoffTask.xml -DmdevId= -DdcuSysId= antrun:run 2>&1 > /dev/null &