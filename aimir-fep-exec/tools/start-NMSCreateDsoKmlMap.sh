#!/bin/sh

. /home/aimir/.bashrc

AIMIR_HOME=/home/aimir/aimir4/aimiramm
AIMIR_TASK=$AIMIR_HOME/aimir-fep-exec

CHK_PARAM=(`which grep | wc`)
if [ $CHK_PARAM -ge 2 ]
then
	LOC_GREP=(`which --skip-alias grep`)
	LOC_AWK=(`which --skip-alias awk`)
else
	LOC_GREP=(`which grep`)
	LOC_AWK=(`which awk`)
fi
MVNREPOSITORY=(`$LOC_GREP localRepository ~/.m2/settings.xml | $LOC_AWK -F "[><]" '{print $3}'`)
if [ "$MVNREPOSITORY" == "" ]
then
	MVNREPOSITORY=~/.m2/repository
fi

count=`/bin/ps -eaf | grep pom-NMSCreateDsoKmlMap.xml | grep antrun:run | grep -v grep | wc -l`
if [ $count -gt 0 ]; then
    echo "process is running."
#    exit 1
fi

##################################
#DSONAME=
#EXDSONAME=
##################################



count=`ps -eaf | grep pom-NMSCreateDsoKmlMap.xml | grep -v grep | wc -l`
if [ $count -gt 0 ]; then
 echo "process is running."
 exit 1
fi

mvn -e -f $AIMIR_HOME/aimir-fep-exec/pom-NMSCreateDsoKmlMap.xml antrun:run -Dspring.instrument.path=$AIMIR_HOME/aimir-project-lib/org/springframework/spring-instrument/4.2.5.RELEASE -DdsoName=$DSONAME -DexDsoName=$EXDSONAME 2>&1 > /dev/null &