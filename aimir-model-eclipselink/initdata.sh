#!/bin/bash

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

if [ $# -eq 0 ]
then
    echo "USAGE: ./initdata.sh ../aimir-model/src/initdata/xxxx.xml"
    exit 
fi

mvn -e -f pom-init.xml antrun:run -Dfile.encoding=EUC_KR -Declipselink.path=$MVNREPOSITORY/org/eclipse/persistence/eclipselink -Dspring.instrument.path=$MVNREPOSITORY/org/springframework/spring-instrument -DdataFile=$1
