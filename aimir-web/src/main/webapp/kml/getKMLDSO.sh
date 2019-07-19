#!/bin/bash

cat map_head.txt > map_temp.xml

sqlplus -S ${DB_CONN} >> map_temp.xml <<EOF
set pages 0
set feedback off
set linesize 5000
set serveroutput on

declare
  v_result varchar2(1000);
begin
  FOR dculist_rec IN (
    SELECT id
      FROM mcu
     WHERE location_id = (select id from location where name='$1') and (mcu_status is null or mcu_status != (select id from code where code='1.1.4.2')) and gpiox is not null and gpioy is not null)
  LOOP
    FOR data_rec IN (
      select '<Placemark><name>DCU: '||sys_id||'</name><description><![CDATA[Device ID: '||sys_id||'<br/>IP Address: '||nvl(ip_addr,'')||'</br>IPV6 Address: '||nvl(ipv6_addr,'')||
            '</br>Status: '||nvl((select name from code where id = mcu_status),'UnKnwon')||
            '</br>GpioX: '||gpiox||'</br>GpioY: '||gpioy||'</br>GpioZ: '||nvl(gpioz,0.0)||']]></description>'||
             '<Point><coordinates>'|| gpiox || ',' || gpioy || ',0.0</coordinates></Point><styleUrl>'||
             (case  when code.code='1.1.4.1' then '#dcuIconBlue' when code.code='1.1.4.3' then '#dcuIconGray' when code.code='1.1.4.4' then '#dcuIconRed' when code.code='1.1.4.5' then '#dcuIconOrange' when code.code is null then '#dcuIconPurple' end ) ||
             '</styleUrl>'||
             '<ExtendedData><Data name="mcu"><value>{"id":'||mcu.id||',"sysId":"'||sys_id||'"}</value></Data></ExtendedData>'||                  '</Placemark>' as result
      from mcu left outer join code code on mcu.mcu_status = code.id where mcu.id=dculist_rec.id
    )
    LOOP
      dbms_output.put_line(data_rec.result);
    END LOOP;
  END LOOP;

  FOR data_rec IN (
      select '<Placemark><name>Meter: '||b.mds_id||'</name>'||
             '<description><![CDATA[Device ID: '||b.mds_id||'<br/>Modem: '||a.device_serial||'<br/>GS1: '||b.gs1||
             '<br/>SW Ver: '||b.sw_version||
             '<br/>FW Ver: '||a.fw_ver||'('||trim(to_char(a.fw_revision,'XXXX'))||')'||
             '<br/>GPS X: '||c.cust_gpiox||'<br/>GPS Y: '||c.cust_gpioy||'<br/>RSSI: '||a.rssi||
             '<br/>Last LP date(last 3 days) : '|| to_char(to_date(l.maxreaddate,'yyyymmddhh24'),'yyyy/mm/dd hh24')||
             '<br/>LP Count(last 3 days) : '||nvl(l.cnt,0)||
             '<br/>Parent : ]]></description>'||
             '<Point><coordinates>'||c.cust_gpiox || ',' ||c.cust_gpioy || ',0.0</coordinates></Point><styleUrl>'||
             (case when 3*24*60/b.LP_INTERVAL/cnt*100 = 100  then '#meterIconBlue' when cnt is null then '#meterIconRed' else '#meterIconYellow' end)||'</styleUrl>'||
             '<ExtendedData><Data name="meter"><value>{"id":'||b.id||',"mdsId":"'||b.mds_id||'","modemId":'||a.id||'}</value></Data>'||
             '<Data name="modem"><value>{"id":'||a.id||',"deviceSerial":"'||a.device_serial||'","status":"'||(nvl((select name from code where id = a.modem_status), 'UnKown'))||'","protocol":"'||a.protocol_type||'","type":"'||a.modem_type||
            '","fwver":"'||a.fw_ver||'('||trim(to_char(a.fw_revision,'XXXX'))||')'||
            '","x":'||nvl(a.gpiox,0)||',"y":'||nvl(a.gpioy,0)||',"z":'||nvl(a.gpioz,0)||'}</value></Data>'||
            '</ExtendedData></Placemark>' as result
      from modem a
      join meter b on a.id = b.modem_id
      join meter_map c on b.mds_id=c.mds_id
      left outer join (
          select mdev_id,sum(value_cnt) cnt,max(yyyymmddhh) maxreaddate from lp_em
          where mdev_id in (select mds_id from meter where location_id=(select id from location where name='$1') and modem_id in (select id from modem where modem='MMIU' and location_id=(select id from location where name='$1')))
            and channel=1
            and yyyymmdd between to_char(sysdate-3,'yyyymmdd') and to_char(sysdate,'yyyymmdd')
            and yyyymmddhh between to_char(sysdate-3,'yyyymmdd') and to_char(sysdate,'yyyymmdd')
          group by mdev_id
      ) l on b.mds_id=l.mdev_id
      where a.modem='MMIU' and b.location_id=(select id from location where name='$1') and b.gpiox is not null and (b.meter_status!=(select id from code where code='1.3.3.9') or b.meter_status is null)
  )
  LOOP
    dbms_output.put_line(data_rec.result);
  END LOOP;


  FOR data_rec IN (
    select '<Placemark><name>Meter: '||b.mds_id||'</name>'||
           '<description><![CDATA[Meter: '||b.mds_id||'<br/>Modem: <br/>GS1: '||b.gs1||
           '<br/>SW Ver: '||b.sw_version||
           '<br/>FW Ver: '||
           '<br/>GPS X: '|| b.gpiox||'<br/>GPS Y: '||b.gpioy||'<br/>RSSI: '||
           '<br/>Last LP date(last 3 days) : '||
           '<br/>LP Count(last 3 days) : '||
           '<br/>Parent : <br/>Hop: ]]></description>'||
           '<Point><coordinates>'|| b.gpiox || ',' || b.gpioy || ',0.0</coordinates></Point><styleUrl>#meterIconRed</styleUrl></Placemark>'
           as result
    from meter b
    where b.location_id = (select id from location where name='$1')
    and b.modem_id is null and b.gpiox is not null and (b.meter_status!=(select id from code where code='1.3.3.9') or b.meter_status is null)
  )
  LOOP
    dbms_output.put_line(data_rec.result);
  END LOOP;

END;
/
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

