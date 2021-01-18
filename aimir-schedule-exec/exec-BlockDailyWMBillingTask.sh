#!/bin/bash
 ~/.bashrc

/home/aimir/maven/bin/mvn -e -f /home/aimir/aimiramm.dev/aimir-schedule-exec/pom-BlockDailyWMBillingTask.xml antrun:run  2>&1 > BlockDailyWMBillingTask.log &
