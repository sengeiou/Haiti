#!/bin/bash

cd `dirname $0`
echo `pwd` 

JDBC_FILE=../WEB-INF/classes/jdbc.properties
JDBC_URL=`cat  $JDBC_FILE | awk -F= '{if ($1=="jdbc.url") print $2}' | awk -F@ '{print $2}' | sed -e 's/\:/\//2' | sed 's/^[ \t]*//g' | sed 's/[ \t\r\n]*$//g'`
JDBC_PASS=`cat $JDBC_FILE | awk -F= '{if ($1=="jdbc.password") print $2}'| sed 's/^[ \t]*//g' | sed 's/[ \t\r\n]*$//g'`
JDBC_USER=`cat $JDBC_FILE | awk -F= '{if ($1=="jdbc.username") print $2}'| sed 's/^[ \t]*//g' | sed 's/[ \t\r\n]*$//g'`
export DB_CONN=$JDBC_USER/$JDBC_PASS@$JDBC_URL
echo $DB_CONN

if [ -e data ] ; then
	echo "data directory is already exist"
else 
	mkdir data
fi

sqlplus -S ${DB_CONN} > meter_map.txt << EOF
set pages 0
set feedback off
select 'METER_MAP_EXIST='||count(*) from user_tables where table_name = 'METER_MAP';
EOF
#echo "METER_MAP_EXIST=0" > meter_map.txt

ago7day=$( date +"%Y%m%d" -d '7 days ago' )
#echo $ago7day
#sqlplus -S ${DB_CONN} > dculist.txt <<EOF
#
#set pages 0
#set feedback off
#set linesize 2000
#
#select sys_id||'  '||id from mcu where last_comm_date > '$ago7day' and (mcu_status is null or mcu_status != (select id from code where code='1.1.4.2')) and gpiox is not null and gpioy is not null;
#EOF

sqlplus -S ${DB_CONN} > dsolist.txt <<EOF
set pages 0
set feedback off
set linesize 2000

select loc.name from location loc inner join mcu m on m.location_id = loc.id  where  m.last_comm_date > '$ago7day' and (m.mcu_status is null or m.mcu_status != (select id from code where code='1.1.4.2'))
    and  m.gpiox is not null and m.gpioy is not null group by loc.name;
EOF

#./startKML.sh
./startKMLDSO.sh
#./startData.sh

