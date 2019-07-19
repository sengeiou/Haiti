#!/bin/sh

### Properties ####
cloneCode=0315
cloneCount=20
fwVersion=1.37
executeType=DCU
####################

batchToolName=SetCloneOnOffToRemainingTargetParent
count=`ps -eaf | grep pom-SetCloneOnOffToRemainingTargetParent.xml | grep -v grep | wc -l`
if [ $count -gt 0 ]; then
 echo "process is running."
 exit 1
fi
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



mvn -e -f pom-SetCloneOnOffToRemainingTargetParent.xml antrun:run -DbatchToolName=$batchToolName -DcloneCode=$cloneCode -DcloneCount=$cloneCount -DfwVersion=$fwVersion -DexecuteType=$executeType antrun:run -Dspring.instrument.path=$MVNREPOSITORY/org/springframework/spring-instrument/4.2.5.RELEASE  > ./SetCloneOnOffToRemainingTargetParentDebug.log &
