#!/bin/bash
. ./meter_map.txt

cat dsolist.txt | while read dsoname
do
	if [ $METER_MAP_EXIST -eq 1 ] ; then
		./getKMLDSO.sh $dsoname
	else
		./getKMLDSONoMap.sh $dsoname
	fi
done
echo "$0 finished"
