create or replace trigger IP_METEREVENT_LOG
after insert on METEREVENT_LOG
for each row
declare
begin
  if length(:new.OPEN_TIME)=14 and substr(:new.OPEN_TIME,1,4)>='2000' then
    insert into IP_EV_TEMP(EVTYPE,ACTIVATOR_ID,METEREVENT_ID,EVENTOBIS_ID,OPEN_TIME,ACTIVATOR_TYPE,
      INTEGRATED,MESSAGE,SUPPLIER_ID,LOCATION_ID,WRITETIME,YYYYMMDD)
    select 'M' as EVTYPE,:new.ACTIVATOR_ID,:new.METEREVENT_ID,b.obiseventid,:new.OPEN_TIME,:new.ACTIVATOR_TYPE,
      :new.INTEGRATED,:new.MESSAGE,:new.SUPPLIER_ID,(select location_id from meter where mds_id=:new.ACTIVATOR_ID),
      :new.WRITETIME,:new.YYYYMMDD
    from dual a,ip_ev_eventobis b, ip_ev_option c
    where :new.METEREVENT_ID=b.aimireventid and b.obiseventid=c.attributename and c.codetype='EV' and c.attributevalue='TRUE' and b.isuse='TRUE';

    insert into IP_EV_DELIVERY(EVTYPE,ACTIVATOR_ID,METEREVENT_ID,EVENTOBIS_ID,OPEN_TIME,ACTIVATOR_TYPE,
      INTEGRATED,MESSAGE,SUPPLIER_ID,LOCATION_ID,WRITETIME,YYYYMMDD)
    select 'M' as EVTYPE,:new.ACTIVATOR_ID,:new.METEREVENT_ID,b.obiseventid,:new.OPEN_TIME,:new.ACTIVATOR_TYPE,
      :new.INTEGRATED,:new.MESSAGE,:new.SUPPLIER_ID,(select location_id from meter where mds_id=:new.ACTIVATOR_ID),
      :new.WRITETIME,:new.YYYYMMDD
    from dual,ip_ev_eventobis b, ip_ev_option c
    where :new.METEREVENT_ID=b.aimireventid and b.obiseventid=c.attributename and c.codetype='EV' and c.attributevalue='TRUE' and b.isuse='TRUE';
  end if;

  exception
    when others then
      null;
end;
/

create or replace trigger IP_EVENTALERTLOG
after insert or update on EVENTALERTLOG
for each row
declare
  id_poweralarm NUMBER;
  id_coveralarm NUMBER;
  id_thresholdalarm NUMBER;
  id_installalarm NUMBER;
begin
  id_poweralarm := 0;
  id_coveralarm := 0;
  id_thresholdalarm := 0;
  id_installalarm := 0;

  begin
    select id into id_poweralarm from eventalert where name='Power Alarm';
    select id into id_coveralarm from eventalert where name='Cover Alarm';
    select id into id_thresholdalarm from eventalert where name='Threshold Warning';
    select id into id_installalarm from eventalert where name='Equipment Installation';
  exception
    when others then
      null;
  end;

  if length(:new.OPENTIME)=14 and substr(:new.OPENTIME,1,4)>='2000' then
    if id_poweralarm = :new.EVENTALERT_ID then
      if :new.MESSAGE = 'Power Down' then

        insert into IP_EV_TEMP(EVTYPE,ACTIVATOR_ID,METEREVENT_ID,EVENTOBIS_ID,OPEN_TIME,ACTIVATOR_TYPE,
          INTEGRATED,MESSAGE,SUPPLIER_ID,LOCATION_ID,WRITETIME,YYYYMMDD)
        select 'A',a.ACTIVATORID,b.NAME||' '||a.STATUS,c.obiseventid,a.OPEN_TIME,a.ACTIVATOR_TYPE,'',a.MESSAGE,
               a.SUPPLIER_ID,a.LOCATION_ID, a.WRITETIME,a.YYYYMMDD
        from (select :new.ACTIVATORID as ACTIVATORID, :new.EVENTALERT_ID as EVENTALERT_ID, :new.STATUS AS STATUS,
                     DECODE(:new.STATUS,'Open',:new.OPENTIME,:new.CLOSETIME) as OPEN_TIME,:new.ACTIVATOR_TYPE as ACTIVATOR_TYPE,
                     :new.MESSAGE as MESSAGE,:new.SUPPLIER_ID as SUPPLIER_ID,:new.LOCATION_ID as LOCATION_ID,
                     :new.WRITETIME as WRITETIME,SUBSTR(:new.WRITETIME,0,8) AS YYYYMMDD from dual) a,
             eventalert b, ip_ev_eventobis c, ip_ev_option d
        where a.eventalert_id=b.id and b.NAME||' '||a.STATUS=c.aimireventid
          and c.obiseventid=d.attributename and d.codetype='EV' and d.attributevalue='TRUE' and c.isuse='TRUE';

        insert into IP_EV_DELIVERY(EVTYPE,ACTIVATOR_ID,METEREVENT_ID,EVENTOBIS_ID,OPEN_TIME,ACTIVATOR_TYPE,
          INTEGRATED,MESSAGE,SUPPLIER_ID,LOCATION_ID,WRITETIME,YYYYMMDD)
        select 'A',a.ACTIVATORID,b.NAME||' '||a.STATUS,c.obiseventid,a.OPEN_TIME,a.ACTIVATOR_TYPE,'',a.MESSAGE,
               a.SUPPLIER_ID,a.LOCATION_ID, a.WRITETIME,a.YYYYMMDD
        from (select :new.ACTIVATORID as ACTIVATORID, :new.EVENTALERT_ID as EVENTALERT_ID, :new.STATUS AS STATUS,
                     DECODE(:new.STATUS,'Open',:new.OPENTIME,:new.CLOSETIME) as OPEN_TIME,:new.ACTIVATOR_TYPE as ACTIVATOR_TYPE,
                     :new.MESSAGE as MESSAGE,:new.SUPPLIER_ID as SUPPLIER_ID,:new.LOCATION_ID as LOCATION_ID,
                     :new.WRITETIME as WRITETIME,SUBSTR(:new.WRITETIME,0,8) AS YYYYMMDD from dual) a,
             eventalert b, ip_ev_eventobis c, ip_ev_option d
        where a.eventalert_id=b.id and b.NAME||' '||a.STATUS=c.aimireventid
          and c.obiseventid=d.attributename and d.codetype='EV' and d.attributevalue='TRUE' and c.isuse='TRUE';

      elsif :new.MESSAGE = 'Power Restore' then

        insert into IP_EV_TEMP(EVTYPE,ACTIVATOR_ID,METEREVENT_ID,EVENTOBIS_ID,OPEN_TIME,ACTIVATOR_TYPE,
          INTEGRATED,MESSAGE,SUPPLIER_ID,LOCATION_ID,WRITETIME,YYYYMMDD)
        select 'A',a.ACTIVATORID,b.NAME||' '||a.STATUS,c.obiseventid,a.OPEN_TIME,a.ACTIVATOR_TYPE,'',a.MESSAGE,
               a.SUPPLIER_ID,a.LOCATION_ID, a.WRITETIME,a.YYYYMMDD
        from (select :new.ACTIVATORID as ACTIVATORID, :new.EVENTALERT_ID as EVENTALERT_ID, :new.STATUS AS STATUS,
                     DECODE(:new.STATUS,'Open',:new.OPENTIME,:new.CLOSETIME) as OPEN_TIME,:new.ACTIVATOR_TYPE as ACTIVATOR_TYPE,
                     :new.MESSAGE as MESSAGE,:new.SUPPLIER_ID as SUPPLIER_ID,:new.LOCATION_ID as LOCATION_ID,
                     :new.WRITETIME as WRITETIME,SUBSTR(:new.WRITETIME,0,8) AS YYYYMMDD from dual) a,
             eventalert b, ip_ev_eventobis c, ip_ev_option d
        where a.eventalert_id=b.id and b.NAME||' '||a.STATUS=c.aimireventid
          and c.obiseventid=d.attributename and d.codetype='EV' and d.attributevalue='TRUE' and c.isuse='TRUE';

        insert into IP_EV_DELIVERY(EVTYPE,ACTIVATOR_ID,METEREVENT_ID,EVENTOBIS_ID,OPEN_TIME,ACTIVATOR_TYPE,
          INTEGRATED,MESSAGE,SUPPLIER_ID,LOCATION_ID,WRITETIME,YYYYMMDD)
        select 'A',a.ACTIVATORID,b.NAME||' '||a.STATUS,c.obiseventid,a.OPEN_TIME,a.ACTIVATOR_TYPE,'',a.MESSAGE,
               a.SUPPLIER_ID,a.LOCATION_ID, a.WRITETIME,a.YYYYMMDD
        from (select :new.ACTIVATORID as ACTIVATORID, :new.EVENTALERT_ID as EVENTALERT_ID, :new.STATUS AS STATUS,
                     DECODE(:new.STATUS,'Open',:new.OPENTIME,:new.CLOSETIME) as OPEN_TIME,:new.ACTIVATOR_TYPE as ACTIVATOR_TYPE,
                     :new.MESSAGE as MESSAGE,:new.SUPPLIER_ID as SUPPLIER_ID,:new.LOCATION_ID as LOCATION_ID,
                     :new.WRITETIME as WRITETIME,SUBSTR(:new.WRITETIME,0,8) AS YYYYMMDD from dual) a,
             eventalert b, ip_ev_eventobis c, ip_ev_option d
        where a.eventalert_id=b.id and b.NAME||' '||a.STATUS=c.aimireventid
          and c.obiseventid=d.attributename and d.codetype='EV' and d.attributevalue='TRUE' and c.isuse='TRUE';

      elsif INSTR(:new.MESSAGE,'Missing') > -1 and INSTR(:new.MESSAGE,'L1') > -1 then

        insert into IP_EV_TEMP(EVTYPE,ACTIVATOR_ID,METEREVENT_ID,EVENTOBIS_ID,OPEN_TIME,ACTIVATOR_TYPE,
          INTEGRATED,MESSAGE,SUPPLIER_ID,LOCATION_ID,WRITETIME,YYYYMMDD)
        select 'A',a.ACTIVATORID,a.MESSAGE||' '||a.STATUS,c.obiseventid,a.OPEN_TIME,a.ACTIVATOR_TYPE,'',a.MESSAGE,
               a.SUPPLIER_ID,a.LOCATION_ID, a.WRITETIME,a.YYYYMMDD
        from (select :new.ACTIVATORID as ACTIVATORID, :new.EVENTALERT_ID as EVENTALERT_ID, :new.STATUS AS STATUS,
                     DECODE(:new.STATUS,'Open',:new.OPENTIME,:new.CLOSETIME) as OPEN_TIME,:new.ACTIVATOR_TYPE as ACTIVATOR_TYPE,
                     'L1 Missing' as MESSAGE,:new.SUPPLIER_ID as SUPPLIER_ID,:new.LOCATION_ID as LOCATION_ID,
                     :new.WRITETIME as WRITETIME,SUBSTR(:new.WRITETIME,0,8) AS YYYYMMDD from dual) a,
             ip_ev_eventobis c, ip_ev_option d
        where a.MESSAGE||' '||a.STATUS=c.aimireventid
          and c.obiseventid=d.attributename and d.codetype='EV' and d.attributevalue='TRUE' and c.isuse='TRUE';

        insert into IP_EV_DELIVERY(EVTYPE,ACTIVATOR_ID,METEREVENT_ID,EVENTOBIS_ID,OPEN_TIME,ACTIVATOR_TYPE,
          INTEGRATED,MESSAGE,SUPPLIER_ID,LOCATION_ID,WRITETIME,YYYYMMDD)
        select 'A',a.ACTIVATORID,a.MESSAGE||' '||a.STATUS,c.obiseventid,a.OPEN_TIME,a.ACTIVATOR_TYPE,'',a.MESSAGE,
               a.SUPPLIER_ID,a.LOCATION_ID, a.WRITETIME,a.YYYYMMDD
        from (select :new.ACTIVATORID as ACTIVATORID, :new.EVENTALERT_ID as EVENTALERT_ID, :new.STATUS AS STATUS,
                     DECODE(:new.STATUS,'Open',:new.OPENTIME,:new.CLOSETIME) as OPEN_TIME,:new.ACTIVATOR_TYPE as ACTIVATOR_TYPE,
                     'L1 Missing' as MESSAGE,:new.SUPPLIER_ID as SUPPLIER_ID,:new.LOCATION_ID as LOCATION_ID,
                     :new.WRITETIME as WRITETIME,SUBSTR(:new.WRITETIME,0,8) AS YYYYMMDD from dual) a,
             ip_ev_eventobis c, ip_ev_option d
        where a.MESSAGE||' '||a.STATUS=c.aimireventid
          and c.obiseventid=d.attributename and d.codetype='EV' and d.attributevalue='TRUE' and c.isuse='TRUE';

      elsif INSTR(:new.MESSAGE,'Missing') > -1 and INSTR(:new.MESSAGE,'L2') > -1 then

        insert into IP_EV_TEMP(EVTYPE,ACTIVATOR_ID,METEREVENT_ID,EVENTOBIS_ID,OPEN_TIME,ACTIVATOR_TYPE,
          INTEGRATED,MESSAGE,SUPPLIER_ID,LOCATION_ID,WRITETIME,YYYYMMDD)
        select 'A',a.ACTIVATORID,a.MESSAGE||' '||a.STATUS,c.obiseventid,a.OPEN_TIME,a.ACTIVATOR_TYPE,'',a.MESSAGE,
               a.SUPPLIER_ID,a.LOCATION_ID, a.WRITETIME,a.YYYYMMDD
        from (select :new.ACTIVATORID as ACTIVATORID, :new.EVENTALERT_ID as EVENTALERT_ID, :new.STATUS AS STATUS,
                     DECODE(:new.STATUS,'Open',:new.OPENTIME,:new.CLOSETIME) as OPEN_TIME,:new.ACTIVATOR_TYPE as ACTIVATOR_TYPE,
                     'L2 Missing' as MESSAGE,:new.SUPPLIER_ID as SUPPLIER_ID,:new.LOCATION_ID as LOCATION_ID,
                     :new.WRITETIME as WRITETIME,SUBSTR(:new.WRITETIME,0,8) AS YYYYMMDD from dual) a,
             ip_ev_eventobis c, ip_ev_option d
        where a.MESSAGE||' '||a.STATUS=c.aimireventid
          and c.obiseventid=d.attributename and d.codetype='EV' and d.attributevalue='TRUE' and c.isuse='TRUE';

        insert into IP_EV_DELIVERY(EVTYPE,ACTIVATOR_ID,METEREVENT_ID,EVENTOBIS_ID,OPEN_TIME,ACTIVATOR_TYPE,
          INTEGRATED,MESSAGE,SUPPLIER_ID,LOCATION_ID,WRITETIME,YYYYMMDD)
        select 'A',a.ACTIVATORID,a.MESSAGE||' '||a.STATUS,c.obiseventid,a.OPEN_TIME,a.ACTIVATOR_TYPE,'',a.MESSAGE,
               a.SUPPLIER_ID,a.LOCATION_ID, a.WRITETIME,a.YYYYMMDD
        from (select :new.ACTIVATORID as ACTIVATORID, :new.EVENTALERT_ID as EVENTALERT_ID, :new.STATUS AS STATUS,
                     DECODE(:new.STATUS,'Open',:new.OPENTIME,:new.CLOSETIME) as OPEN_TIME,:new.ACTIVATOR_TYPE as ACTIVATOR_TYPE,
                     'L2 Missing' as MESSAGE,:new.SUPPLIER_ID as SUPPLIER_ID,:new.LOCATION_ID as LOCATION_ID,
                     :new.WRITETIME as WRITETIME,SUBSTR(:new.WRITETIME,0,8) AS YYYYMMDD from dual) a,
             ip_ev_eventobis c, ip_ev_option d
        where a.MESSAGE||' '||a.STATUS=c.aimireventid
          and c.obiseventid=d.attributename and d.codetype='EV' and d.attributevalue='TRUE' and c.isuse='TRUE';

      elsif INSTR(:new.MESSAGE,'Missing') > -1 and INSTR(:new.MESSAGE,'L3') > -1 then

        insert into IP_EV_TEMP(EVTYPE,ACTIVATOR_ID,METEREVENT_ID,EVENTOBIS_ID,OPEN_TIME,ACTIVATOR_TYPE,
          INTEGRATED,MESSAGE,SUPPLIER_ID,LOCATION_ID,WRITETIME,YYYYMMDD)
        select 'A',a.ACTIVATORID,a.MESSAGE||' '||a.STATUS,c.obiseventid,a.OPEN_TIME,a.ACTIVATOR_TYPE,'',a.MESSAGE,
               a.SUPPLIER_ID,a.LOCATION_ID, a.WRITETIME,a.YYYYMMDD
        from (select :new.ACTIVATORID as ACTIVATORID, :new.EVENTALERT_ID as EVENTALERT_ID, :new.STATUS AS STATUS,
                     DECODE(:new.STATUS,'Open',:new.OPENTIME,:new.CLOSETIME) as OPEN_TIME,:new.ACTIVATOR_TYPE as ACTIVATOR_TYPE,
                     'L3 Missing' as MESSAGE,:new.SUPPLIER_ID as SUPPLIER_ID,:new.LOCATION_ID as LOCATION_ID,
                     :new.WRITETIME as WRITETIME,SUBSTR(:new.WRITETIME,0,8) AS YYYYMMDD from dual) a,
             ip_ev_eventobis c, ip_ev_option d
        where a.MESSAGE||' '||a.STATUS=c.aimireventid
          and c.obiseventid=d.attributename and d.codetype='EV' and d.attributevalue='TRUE' and c.isuse='TRUE';

        insert into IP_EV_DELIVERY(EVTYPE,ACTIVATOR_ID,METEREVENT_ID,EVENTOBIS_ID,OPEN_TIME,ACTIVATOR_TYPE,
          INTEGRATED,MESSAGE,SUPPLIER_ID,LOCATION_ID,WRITETIME,YYYYMMDD)
        select 'A',a.ACTIVATORID,a.MESSAGE||' '||a.STATUS,c.obiseventid,a.OPEN_TIME,a.ACTIVATOR_TYPE,'',a.MESSAGE,
             a.SUPPLIER_ID,a.LOCATION_ID, a.WRITETIME,a.YYYYMMDD
        from (select :new.ACTIVATORID as ACTIVATORID, :new.EVENTALERT_ID as EVENTALERT_ID, :new.STATUS AS STATUS,
                     DECODE(:new.STATUS,'Open',:new.OPENTIME,:new.CLOSETIME) as OPEN_TIME,:new.ACTIVATOR_TYPE as ACTIVATOR_TYPE,
                     'L3 Missing' as MESSAGE,:new.SUPPLIER_ID as SUPPLIER_ID,:new.LOCATION_ID as LOCATION_ID,
                     :new.WRITETIME as WRITETIME,SUBSTR(:new.WRITETIME,0,8) AS YYYYMMDD from dual) a,
             ip_ev_eventobis c, ip_ev_option d
        where a.MESSAGE||' '||a.STATUS=c.aimireventid
          and c.obiseventid=d.attributename and d.codetype='EV' and d.attributevalue='TRUE' and c.isuse='TRUE';

      end if;

    elsif id_coveralarm = :new.EVENTALERT_ID  then

      insert into IP_EV_TEMP(EVTYPE,ACTIVATOR_ID,METEREVENT_ID,EVENTOBIS_ID,OPEN_TIME,ACTIVATOR_TYPE,
        INTEGRATED,MESSAGE,SUPPLIER_ID,LOCATION_ID,WRITETIME,YYYYMMDD)
      select 'A',a.ACTIVATORID,b.NAME||' '||a.STATUS,c.obiseventid,a.OPEN_TIME,a.ACTIVATOR_TYPE,'',a.MESSAGE,
             a.SUPPLIER_ID,a.LOCATION_ID, a.WRITETIME,a.YYYYMMDD
      from (select :new.ACTIVATORID as ACTIVATORID, :new.EVENTALERT_ID as EVENTALERT_ID, :new.STATUS AS STATUS,
                   DECODE(:new.STATUS,'Open',:new.OPENTIME,:new.CLOSETIME) as OPEN_TIME,:new.ACTIVATOR_TYPE as ACTIVATOR_TYPE,
                   :new.MESSAGE as MESSAGE,:new.SUPPLIER_ID as SUPPLIER_ID,:new.LOCATION_ID as LOCATION_ID,
                   :new.WRITETIME as WRITETIME,SUBSTR(:new.WRITETIME,0,8) AS YYYYMMDD from dual) a,
           eventalert b, ip_ev_eventobis c, ip_ev_option d
      where a.eventalert_id=b.id and b.NAME||' '||a.STATUS=c.aimireventid
        and c.obiseventid=d.attributename and d.codetype='EV' and d.attributevalue='TRUE' and c.isuse='TRUE';

      insert into IP_EV_DELIVERY(EVTYPE,ACTIVATOR_ID,METEREVENT_ID,EVENTOBIS_ID,OPEN_TIME,ACTIVATOR_TYPE,
        INTEGRATED,MESSAGE,SUPPLIER_ID,LOCATION_ID,WRITETIME,YYYYMMDD)
      select 'A',a.ACTIVATORID,b.NAME||' '||a.STATUS,c.obiseventid,a.OPEN_TIME,a.ACTIVATOR_TYPE,'',a.MESSAGE,
             a.SUPPLIER_ID,a.LOCATION_ID, a.WRITETIME,a.YYYYMMDD
      from (select :new.ACTIVATORID as ACTIVATORID, :new.EVENTALERT_ID as EVENTALERT_ID, :new.STATUS AS STATUS,
                   DECODE(:new.STATUS,'Open',:new.OPENTIME,:new.CLOSETIME) as OPEN_TIME,:new.ACTIVATOR_TYPE as ACTIVATOR_TYPE,
                   :new.MESSAGE as MESSAGE,:new.SUPPLIER_ID as SUPPLIER_ID,:new.LOCATION_ID as LOCATION_ID,
                   :new.WRITETIME as WRITETIME,SUBSTR(:new.WRITETIME,0,8) AS YYYYMMDD from dual) a,
           eventalert b, ip_ev_eventobis c, ip_ev_option d
      where a.eventalert_id=b.id and b.NAME||' '||a.STATUS=c.aimireventid
        and c.obiseventid=d.attributename and d.codetype='EV' and d.attributevalue='TRUE' and c.isuse='TRUE';

    elsif id_thresholdalarm = :new.EVENTALERT_ID  then

      insert into IP_EV_TEMP(EVTYPE,ACTIVATOR_ID,METEREVENT_ID,EVENTOBIS_ID,OPEN_TIME,ACTIVATOR_TYPE,
        INTEGRATED,MESSAGE,SUPPLIER_ID,LOCATION_ID,WRITETIME,YYYYMMDD)
      select 'A',a.ACTIVATORID,b.NAME||' '||a.STATUS,c.obiseventid,a.OPEN_TIME,a.ACTIVATOR_TYPE,'',a.MESSAGE,
             a.SUPPLIER_ID,a.LOCATION_ID, a.WRITETIME,a.YYYYMMDD
      from (select :new.ACTIVATORID as ACTIVATORID, :new.EVENTALERT_ID as EVENTALERT_ID, :new.STATUS AS STATUS,
                   DECODE(:new.STATUS,'Open',:new.OPENTIME,:new.CLOSETIME) as OPEN_TIME,:new.ACTIVATOR_TYPE as ACTIVATOR_TYPE,
                   :new.MESSAGE as MESSAGE,:new.SUPPLIER_ID as SUPPLIER_ID,:new.LOCATION_ID as LOCATION_ID,
                   :new.WRITETIME as WRITETIME,SUBSTR(:new.WRITETIME,0,8) AS YYYYMMDD from dual) a,
           eventalert b, ip_ev_eventobis c, ip_ev_option d
      where a.eventalert_id=b.id and b.NAME||' '||a.STATUS=c.aimireventid
        and c.obiseventid=d.attributename and d.codetype='EV' and d.attributevalue='TRUE' and c.isuse='TRUE';

      insert into IP_EV_DELIVERY(EVTYPE,ACTIVATOR_ID,METEREVENT_ID,EVENTOBIS_ID,OPEN_TIME,ACTIVATOR_TYPE,
        INTEGRATED,MESSAGE,SUPPLIER_ID,LOCATION_ID,WRITETIME,YYYYMMDD)
      select 'A',a.ACTIVATORID,b.NAME||' '||a.STATUS,c.obiseventid,a.OPEN_TIME,a.ACTIVATOR_TYPE,'',a.MESSAGE,
             a.SUPPLIER_ID,a.LOCATION_ID, a.WRITETIME,a.YYYYMMDD
      from (select :new.ACTIVATORID as ACTIVATORID, :new.EVENTALERT_ID as EVENTALERT_ID, :new.STATUS AS STATUS,
                   DECODE(:new.STATUS,'Open',:new.OPENTIME,:new.CLOSETIME) as OPEN_TIME,:new.ACTIVATOR_TYPE as ACTIVATOR_TYPE,
                   :new.MESSAGE as MESSAGE,:new.SUPPLIER_ID as SUPPLIER_ID,:new.LOCATION_ID as LOCATION_ID,
                   :new.WRITETIME as WRITETIME,SUBSTR(:new.WRITETIME,0,8) AS YYYYMMDD from dual) a,
           eventalert b, ip_ev_eventobis c, ip_ev_option d
      where a.eventalert_id=b.id and b.NAME||' '||a.STATUS=c.aimireventid
        and c.obiseventid=d.attributename and d.codetype='EV' and d.attributevalue='TRUE' and c.isuse='TRUE';

    elsif id_installalarm = :new.EVENTALERT_ID  and :new.ACTIVATOR_TYPE='EnergyMeter' then

      insert into IP_EV_TEMP(EVTYPE,ACTIVATOR_ID,METEREVENT_ID,EVENTOBIS_ID,OPEN_TIME,ACTIVATOR_TYPE,
        INTEGRATED,MESSAGE,SUPPLIER_ID,LOCATION_ID,WRITETIME,YYYYMMDD)
      select 'A',a.ACTIVATORID,b.NAME||' '||a.STATUS,c.obiseventid,a.OPEN_TIME,a.ACTIVATOR_TYPE,'',a.MESSAGE,
             a.SUPPLIER_ID,a.LOCATION_ID, a.WRITETIME,a.YYYYMMDD
      from (select :new.ACTIVATORID as ACTIVATORID, :new.EVENTALERT_ID as EVENTALERT_ID, :new.STATUS AS STATUS,
                   DECODE(:new.STATUS,'Open',:new.OPENTIME,:new.CLOSETIME) as OPEN_TIME,:new.ACTIVATOR_TYPE as ACTIVATOR_TYPE,
                   :new.MESSAGE as MESSAGE,:new.SUPPLIER_ID as SUPPLIER_ID,:new.LOCATION_ID as LOCATION_ID,
                   :new.WRITETIME as WRITETIME,SUBSTR(:new.WRITETIME,0,8) AS YYYYMMDD from dual) a,
           eventalert b, ip_ev_eventobis c, ip_ev_option d
      where a.eventalert_id=b.id and b.NAME||' '||a.STATUS=c.aimireventid
        and c.obiseventid=d.attributename and d.codetype='EV' and d.attributevalue='TRUE' and c.isuse='TRUE';

      insert into IP_EV_DELIVERY(EVTYPE,ACTIVATOR_ID,METEREVENT_ID,EVENTOBIS_ID,OPEN_TIME,ACTIVATOR_TYPE,
        INTEGRATED,MESSAGE,SUPPLIER_ID,LOCATION_ID,WRITETIME,YYYYMMDD)
      select 'A',a.ACTIVATORID,b.NAME||' '||a.STATUS,c.obiseventid,a.OPEN_TIME,a.ACTIVATOR_TYPE,'',a.MESSAGE,
             a.SUPPLIER_ID,a.LOCATION_ID, a.WRITETIME,a.YYYYMMDD
      from (select :new.ACTIVATORID as ACTIVATORID, :new.EVENTALERT_ID as EVENTALERT_ID, :new.STATUS AS STATUS,
                   DECODE(:new.STATUS,'Open',:new.OPENTIME,:new.CLOSETIME) as OPEN_TIME,:new.ACTIVATOR_TYPE as ACTIVATOR_TYPE,
                 :new.MESSAGE as MESSAGE,:new.SUPPLIER_ID as SUPPLIER_ID,:new.LOCATION_ID as LOCATION_ID,
                 :new.WRITETIME as WRITETIME,SUBSTR(:new.WRITETIME,0,8) AS YYYYMMDD from dual) a,
           eventalert b, ip_ev_eventobis c, ip_ev_option d
      where a.eventalert_id=b.id and b.NAME||' '||a.STATUS=c.aimireventid
        and c.obiseventid=d.attributename and d.codetype='EV' and d.attributevalue='TRUE' and c.isuse='TRUE';

    end if;
  end if;

  exception
    when others then
      null;
end;
/
