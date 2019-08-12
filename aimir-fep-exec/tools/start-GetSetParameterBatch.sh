#/bin/bash

. /home/aimir/.bashrc

AIMIR_HOME=/home/aimir/aimiramm
AIMIR_TASK=$AIMIR_HOME/aimir-fep-exec

##############################################
FILE="meterlist.txt"
MDSID=""

# get or set or act
TYPE="get"
#TYPE="set"

# obisCode|classId|attributeNo|accessRight|dataType|value

# Mbus Status Notification Scheduler
PARAMS="0.0.15.0.0.255|22|4|||[[\"FFFFFFFFFFFF000000\"],[\"FFFFFFFFFFFF150000\"],[\"FFFFFFFFFFFF300000\"],[\"FFFFFFFFFFFF450000\"]]"

# Voltage analog change threshold(default=59000)
#PARAMS="0.1.94.31.5.255|3|2|RW|double-long-unsigned|[{value:59000}]"

#Current analog change threshold(default=30000)
#PARAMS="0.1.94.31.7.255|3|2|RW|double-long-unsigned|[{value:30000}]"

# M-Bus Client Setup Channel 1
#PARAMS="0.1.24.1.0.255|72|1|||[{value:3}]"


##############################################


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

count=`/bin/ps -eaf | grep pom-GetSetParameterBatch.xml | grep antrun:run | grep -v grep | wc -l`
if [ $count -gt 0 ]; then
    echo "process is running."
    exit 1
fi


#mvn -e -f $AIMIR_HOME/aimir-fep-exec/pom-MeterTimeSyncEvnTask.xml antrun:run 2>&1 > /dev/null &
#mvn -e -f $AIMIR_HOME/aimir-fep-exec/pom-GetSetParameterBatch.xml antrun:run -Dmaven.repo.local=/home/aimir/aimiramm/aimir-project-lib_work -DtaskName=GetSetParameterBatch -Dmdsid=$MDSID -Dtype=$TYPE -Dparams=$PARAMS -Dfile=$FILE -Dmaxthread="" -Dtimeout=""

mvn -e -f $AIMIR_HOME/aimir-fep-exec/pom-GetSetParameterBatch.xml antrun:run -DtaskName=GetSetParameterBatch -Dmdsid=$MDSID -Dtype=$TYPE -Dparams=$PARAMS -Dfile=$FILE -Dmaxthread="" -Dtimeout="" -Ddev="" 2>&1 > /dev/null &

