#!/bin/bash

cat map_head.txt > map_temp.xml

sqlplus -S ${DB_CONN} >> map_temp.xml <<EOF
set pages 0
set feedback off
set linesize 5000

select '<Placemark><name>DCU: '||sys_id||'</name><description><![CDATA[Device ID: '||sys_id||'<br/>IP Address: '||nvl(ip_addr,'')||'</br>IPV6 Address: '||nvl(ipv6_addr,'')||
       '</br>Status: '||nvl((select name from code where id = mcu_status),'UnKnown')||
       '</br>GpioX: '||gpiox||'</br>GpioY: '||gpioy||'</br>GpioZ: '||nvl(gpioz,0.0)||']]></description>'||
       '<Point><coordinates>'|| gpiox || ',' || gpioy || ',0.0</coordinates></Point><styleUrl>'||
      (case  when code.code='1.1.4.1' then '#dcuIconBlue' when code.code='1.1.4.3' then '#dcuIconGray' when code.code='1.1.4.4' then '#dcuIconRed' when code.code='1.1.4.5' then '#dcuIconOrange' when code.code is null then '#dcuIconPurple' end ) ||
      '</styleUrl>'||
       '<ExtendedData><Data name="mcu"><value>{"id":'||mcu.id||',"sysId":"'||sys_id||'","location":"'||location.name||'"}</value></Data></ExtendedData>'||
       '</Placemark>'
from mcu left outer join code code on mcu.mcu_status = code.id  left outer join location on mcu.location_id = location.id where mcu.sys_id='$1'
union all
select '<Placemark><name>Meter: '||b.mds_id||'</name>'||
       '<description><![CDATA[Device ID: '||b.mds_id||'<br/>Modem: '||a.device_serial||'<br/>GS1: '||b.gs1||
       '<br/>SW Ver: '||b.sw_version||
       '<br/>FW Ver: '||a.fw_ver||'('||trim(to_char(a.fw_revision,'XXXX'))||')'||
--       '<br/>GPS X: '||c.cust_gpiox||'<br/>GPS Y: '||c.cust_gpioy||'<br/>RSSI: '||a.rssi||
      '<br/>GPS X: '||b.gpiox||'<br/>GPS Y: '||b.gpioy||'<br/>RSSI: '||a.rssi||
       '<br/>Last LP date(last 3 days) : '|| to_char(to_date(l.maxreaddate,'yyyymmddhh24'),'yyyy/mm/dd hh24')||
       '<br/>LP Count(last 3 days) : '||nvl(l.cnt,0)||
       '<br/>Parent : '|| p.mds_id||']]></description>'||
--       '<Point><coordinates>'|| c.cust_gpiox || ',' || c.cust_gpioy || ',0.0</coordinates></Point><styleUrl>'||
      '<Point><coordinates>'|| b.gpiox || ',' || b.gpioy || ',0.0</coordinates></Point><styleUrl>'||
       (case when 3*24*60/b.LP_INTERVAL/cnt*100 = 100  then '#meterIconBlue' when cnt is null then '#meterIconRed' else '#meterIconYellow' end)||'</styleUrl>'||
	'<ExtendedData><Data name="meter"><value>{"id":'||b.id||',"mdsId":"'||b.mds_id||'","modemId":'||a.id||'}</value></Data>'||
        '<Data name="modem"><value>{"id":'||a.id||',"deviceSerial":"'||a.device_serial||'","status":"'||(nvl((select name from code where id = a.modem_status), 'UnKown'))||'","protocol":"'||a.protocol_type||'","type":"'||a.modem_type||
        '","fwver":"'||a.fw_ver||'('||trim(to_char(a.fw_revision,'XXXX'))||')","rssi":"'||a.rssi||
        '","parent":"'||(case when a.modem_id is not null then (select device_serial from modem where id = a.modem_id) else '' end )||
        '","x":'||nvl(a.gpiox,0)||',"y":'||nvl(a.gpioy,0)||',"z":'||nvl(a.gpioz,0)||'}</value></Data>'||
       '</ExtendedData></Placemark>'
from modem a
join mcu on mcu.id=a.MCU_ID
join meter b on a.id = b.modem_id
--join meter_map c on b.mds_id=c.mds_id
left outer join meter p on a.modem_id = p.modem_id
left outer join (
    select mdev_id,sum(value_cnt) cnt,max(yyyymmddhh) maxreaddate from lp_em
    where mdev_id in (select mds_id from meter where modem_id in (select id from modem where mcu_id=(select id from mcu where sys_id='$1')))
        and channel=1
        and yyyymmdd between to_char(sysdate-3,'yyyymmdd') and to_char(sysdate,'yyyymmdd')
        and yyyymmddhh between to_char(sysdate-3,'yyyymmdd') and to_char(sysdate,'yyyymmdd')
    group by mdev_id
) l on b.mds_id=l.mdev_id
--where a.mcu_id = (select id from mcu where sys_id = '$1')
where mcu.sys_id='$1'
and b.gpiox is not null and (b.meter_status!=(select id from code where code='1.3.3.9') or b.meter_status is null)
union all
select '<Placemark><name></name><styleUrl>#line-0288D1-1-nodesc</styleUrl><LineString><tessellate>1</tessellate><coordinates>'
 --      || d.cust_gpiox || ',' || d.cust_gpioy || ',0.0 ' || nvl(p2.cust_gpiox,mcu.gpiox) || ',' || nvl(p2.cust_gpioy,mcu.gpioy) || ',0.0</coordinates></LineString></Placemark>' as Placemark
      || c.gpiox || ',' || c.gpioy || ',0.0 ' || nvl(p.gpiox,mcu.gpiox) || ',' || nvl(p.gpioy,mcu.gpioy) || ',0.0</coordinates></LineString></Placemark>' as Placemark
from MODEM
join mcu on mcu.id=modem.MCU_ID
join meter c on modem.id = c.modem_id
--join meter_map d on c.mds_id=d.mds_id
left outer join meter p on modem.modem_id = p.modem_id
--left outer join meter_map p2 on p.mds_id = p2.mds_id
where mcu.sys_id='$1'
and c.gpiox is not null  and  (c.meter_status!=(select id from code where code='1.3.3.9') or c.meter_status is null) 
;

EOF

if [ $? -ne 0 ] ; then
        echo "map_$1 error\n"
        mv map_temp.xml data/map_$1.error
        exit
fi
cat map_tail.txt >> map_temp.xml
which xmllint > /dev/null  2>&1
if [ $? -eq 0 ] ; then
        xmllint --format map_temp.xml >data/map_$1.kml
        if [ $? -ne 0 ] ; then
                mv map_temp.xml data/map_$1.error
        fi
        rm -f map_temp.xml
else
        mv map_temp.xml data/map_$1.kml
fi

