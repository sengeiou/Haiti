#!/bin/bash
 ~/.bashrc

/home/aimir/maven/bin/mvn -e -f /home/aimir/aimiramm.dev/aimir-schedule-exec/pom-BlockDailyEMBillingTask.xml antrun:run  2>&1 > /dev/null &
