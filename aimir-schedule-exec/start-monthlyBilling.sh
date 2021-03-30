#!/bin/sh
mvn -e -f /home/aimir/aimiramm.dev/aimir-schedule-exec/pom-EDHMonthlyBillingTask.xml -DmdevId= antrun:run 2>&1 > /dev/null &