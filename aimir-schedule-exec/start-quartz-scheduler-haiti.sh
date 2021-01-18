#!/bin/sh
 ~/.bashrc

count=`/bin/ps -eaf | grep spring-quartz-haiti.xml | grep antrun:run | grep -v grep | wc -l`
if [ $count -gt 0 ]; then
    echo "process is running."
    exit 1
fi

#nohup mvn -e antrun:run -DscName=SC -DjmxPort=1999 -DspringContext=spring-quartz-haiti.xml  2>&1 > /dev/null & 
nohup mvn -e antrun:run -DscName=SC -DjmxPort=1999 -DspringContext=spring-quartz-haiti.xml  2>&1 > quart_log 
