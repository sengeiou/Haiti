#!/bin/bash

#./refreshNMSInfo.sh
. ./meter_map.txt

cat dculist.txt | while read dcuid dbid
do
	if [ $METER_MAP_EXIST -eq 1 ] ; then
		./getKML.sh $dcuid
	else
		./getKMLNoMap.sh $dcuid
	fi
done

echo "$0 finished"
