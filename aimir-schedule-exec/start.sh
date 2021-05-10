#!/bin/sh
nohup mvn -e antrun:run -DscName=SC -DjmxPort=1999 -DspringContext=spring-quartz-sla.xml  2>&1 > schedule_debug1.log & 