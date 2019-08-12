#!/bin/bash
. /home/aimir/.bashrc

PID=`/bin/ps -eaf | /bin/grep java | /bin/grep MDRestore | /bin/awk '{print $2}'`

for pid in $PID
do
        echo "kill -9 $pid"
        kill -9 $pid
done

sleep 10

# start all fep(fepa, feph, fepd)
CHK_PARAM=(`which grep | wc`)
if [ $CHK_PARAM -ge 2 ]
then
    LOC_GREP=(`which --skip-alias grep`)
    LOC_AWK=(`which --skip-alias awk`)
else
    LOC_GREP=(`which grep`)
    LOC_AWK=(`which awk`)
fi
PID=(`/bin/ps -eaf | $LOC_GREP java | $LOC_GREP log4j-restore | $LOC_AWK '{print $2}'`)
PLEN=${#PID[@]}
MVNREPOSITORY=(`$LOC_GREP localRepository ~/.m2/settings.xml | $LOC_AWK -F "[><]" '{print $3}'`)
if [ "$MVNREPOSITORY" == "" ]
then
    MVNREPOSITORY=~/.m2/repository
fi

mvn -o -e -f /home/aimir/aimir4/aimiramm/aimir-fep-exec/pom-restore.xml antrun:run -DfepName=FEP1 2>&1 > /dev/null &
