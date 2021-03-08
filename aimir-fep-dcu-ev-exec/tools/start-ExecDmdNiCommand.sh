#!/bin/sh

export PATH=$PATH:/home/aimir1/jvm/jdk1.8.0_73/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games:/etc/tomcat7/bin

export JAVA_HOME=/home/aimir1/jvm/jdk1.8.0_73

##################################
MODEMID=795
TYPE=GET
#TYPE=SET
#ATTRID=2001
ATTRID=1001
PARAM=
#ATTRID=6001
#PARAM=0300000000
##################################



count=`ps -eaf | grep pom-ExecDmdNiCommand.xml | grep -v grep | wc -l`
if [ $count -gt 0 ]; then
 echo "process is running."
 exit 1
fi


#/usr/bin/mvn -e -f /home/aimir1/aimiramm.3.3/aimir-fep-exec/pom-ExecDmdNiCommand.xml antrun:run -Dspring.instrument.path=/home/aimir1/.m2/repository/org/springframework/spring-instrument/4.2.5.RELEASE -Dmodemid=$MODEMID -Dtype=$TYPE -Dattrid=$ATTRID -Dparam=$PARAM 2>&1 > /dev/null &

/usr/bin/mvn --debug -e -f /home/aimir1/aimiramm.3.3/aimir-fep-exec/pom-ExecDmdNiCommand.xml antrun:run -Dspring.instrument.path=/home/aimir1/.m2/repository/org/springframework/spring-instrument/4.2.5.RELEASE -Dmodemid=$MODEMID -Dtype=$TYPE -Dattrid=$ATTRID -Dparam=$PARAM > ./ExecDmdNiCommand.log &
