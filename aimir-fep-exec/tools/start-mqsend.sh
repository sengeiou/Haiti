#!/bin/sh

export PATH=$PATH:/home/aimir1/jvm/jdk1.8.0_73/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games:/etc/tomcat7/bin

export JAVA_HOME=/home/aimir1/jvm/jdk1.8.0_73


count=`ps -eaf | grep pom-mqsend.xml | grep -v grep | wc -l`
if [ $count -gt 0 ]; then
 echo "process is running."
 exit 1
fi


#/usr/bin/mvn -e -f /home/aimir1/aimiramm.3.3/aimir-fep-exec/pom-mqsend.xml antrun:run -Dspring.instrument.path=/home/aimir1/.m2/repository/org/springframework/spring-instrument/4.2.5.RELEASE -Dhost=localhost -DfilePath=/home/aimir1/aimiramm.3.3/aimir-fep-exec/FEP1/db/slidewindow 

/usr/bin/mvn -e -f /home/aimir1/aimiramm.3.3/aimir-fep-exec/pom-mqsend.xml antrun:run -Dspring.instrument.path=/home/aimir1/.m2/repository/org/springframework/spring-instrument/4.2.5.RELEASE -Dhost=localhost -DfilePath=/home/aimir1/aimiramm.3.3/aimir-fep-exec/FEP1/db/slidewindow -DthreadCount=100 2>&1 > /dev/null &


