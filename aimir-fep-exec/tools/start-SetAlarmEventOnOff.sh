#!/bin/sh

export PATH=$PATH:/home/aimir1/jvm/jdk1.8.0_73/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games:/etc/tomcat7/bin

export JAVA_HOME=/home/aimir1/jvm/jdk1.8.0_73

##################################
COUNT=2
COMMAND="010100|010200"
VERSION="06010075"
THRESHOLD=3
BEFORETIME=24
FILE="/home/aimir1/aimiramm.3.3/aimir-fep-exec/setAlermEventOnOff.txt"
#FILE=""
WORKER=""
TIMEOUT=""
TESTMODE=0
##################################



count=`ps -eaf | grep pom-SetAlarmEventOnOff.xml | grep -v grep | wc -l`
if [ $count -gt 0 ]; then
 echo "process is running."
 exit 1
fi


/usr/bin/mvn -e -f /home/aimir1/aimiramm.3.3/aimir-fep-exec/pom-SetAlarmEventOnOff.xml antrun:run -Dspring.instrument.path=/home/aimir1/.m2/repository/org/springframework/spring-instrument/4.2.5.RELEASE -Dcount=$COUNT -Dcommand=$COMMAND -Dversion=$VERSION -Dthreshold=$THRESHOLD -Dbeforetime=$BEFORETIME -Dfile=$FILE -Dworker=$WORKER -Dtimeout=$TIMEOUT -Dtestmode=$TESTMODE 2>&1 > /dev/null &

#/usr/bin/mvn --debug -e -f /home/aimir1/aimiramm.3.3/aimir-fep-exec/pom-SetAlarmEventOnOff.xml antrun:run -Dspring.instrument.path=/home/aimir1/.m2/repository/org/springframework/spring-instrument/4.2.5.RELEASE -Dcount=$COUNT -Dcommand=$COMMAND -Dversion=$VERSION -Dthreshold=$THRESHOLD -Dbeforetime=$BEFORETIME -Dfile=$FILE -Dworker=$WORKER -Dtimeout=$TIMEOUT -Dtestmode=$TESTMODE > ./SetAlarmEventOnOff.log &

