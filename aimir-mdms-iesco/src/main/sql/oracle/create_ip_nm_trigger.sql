create or replace trigger IP_METERINGDATA_NM
after insert or update on METERINGDATA_NM
for each row
declare
  capture_date_meter number;
  capture_date_dcu number;
  location_id number; 
  is_savedata varchar2(20);
begin
  select (to_date(:new.YYYYMMDDHHMMSS,'yyyymmddhh24miss')-to_date('19700101','yyyymmdd'))*24*60*60*1000 into capture_date_meter from dual;
  capture_date_dcu   := capture_date_meter;
  select location_id into location_id from meter where MDS_ID=:new.MDEV_ID and meter='EnergyMeter';
  begin
        select ATTRIBUTEVALUE INTO is_savedata FROM IP_NM_OPTION  WHERE ATTRIBUTENAME='CNF_SAVEDATA_NM';
    EXCEPTION
        when NO_DATA_FOUND then
            is_savedata := 'FALSE';
  end;
  if (:old.CH1 is null and :new.CH1 is not null) or :old.CH1 != :new.CH1 then
    begin
      AIMIR_NM.INSERT_NM_DATA(is_savedata, :new.YYYYMMDDHHMMSS,1,:new.MDEV_ID,:new.MDEV_TYPE,location_id,:new.CH1,1001,capture_date_meter,capture_date_dcu,:new.DEVICE_TYPE,:new.DEVICE_ID);
      exception
        when others then
        null;
    end;
  end if;

  if (:old.CH2 is null and :new.CH2 is not null) or :old.CH2 != :new.CH2 then
    begin
      AIMIR_NM.INSERT_NM_DATA(is_savedata, :new.YYYYMMDDHHMMSS,2,:new.MDEV_ID,:new.MDEV_TYPE,location_id,:new.CH2,1001,capture_date_meter,capture_date_dcu,:new.DEVICE_TYPE,:new.DEVICE_ID);
     exception
        when others then
        null;
    end;
  end if;


  if (:old.CH4 is null and :new.CH4 is not null) or :old.CH4 != :new.CH4 then
    begin
           AIMIR_NM.INSERT_NM_DATA(is_savedata, :new.YYYYMMDDHHMMSS,4,:new.MDEV_ID,:new.MDEV_TYPE,location_id,:new.CH4,1001,capture_date_meter,capture_date_dcu,:new.DEVICE_TYPE,:new.DEVICE_ID);
      exception
        when others then
        null;
    end;
  end if;
--- converted analog current and converted analog voltage are not sended.

exception
when others then
  null;
end;
/

