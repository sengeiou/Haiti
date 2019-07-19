create or replace trigger IP_POWER_QUALITY
after insert or update on POWER_QUALITY
for each row
declare
  capture_date_meter number;
  capture_date_dcu number;
  location_id number;
  v_today timestamp with time zone;
begin
  EXECUTE IMMEDIATE 'ALTER SESSION SET TIME_ZONE=''+00:00''';
  v_today := current_timestamp;

  select (to_date(:new.YYYYMMDDHHMM,'yyyymmddhh24mi')-to_date('19700101','yyyymmdd'))*24*60*60*1000 into capture_date_meter from dual;
  capture_date_dcu   := capture_date_meter;
  select location_id into location_id from meter where MDS_ID=:new.MDEV_ID and meter='EnergyMeter';

  if (:old.VOL_THD_A is null and :new.VOL_THD_A is not null) or :old.VOL_THD_A != :new.VOL_THD_A then
    begin
      AIMIR_IP.INSERT_MV_DATA('EM',:new.YYYYMMDDHHMM||'00',981,:new.MDEV_ID,:new.MDEV_TYPE,location_id,:new.VOL_THD_A,1001,0,capture_date_meter,capture_date_dcu,:new.DEVICE_TYPE,:new.DEVICE_ID,v_today);
      exception
        when others then
        null;
    end;
  end if;
  if (:old.VOL_A is null and :new.VOL_A is not null) or :old.VOL_A != :new.VOL_A then
    begin
      AIMIR_IP.INSERT_MV_DATA('EM',:new.YYYYMMDDHHMM||'00',982,:new.MDEV_ID,:new.MDEV_TYPE,location_id,:new.VOL_A,1001,0,capture_date_meter,capture_date_dcu,:new.DEVICE_TYPE,:new.DEVICE_ID,v_today);
      exception
        when others then
        null;
    end;
  end if;
  if (:old.VOL_ANGLE_A is null and :new.VOL_ANGLE_A is not null) or :old.VOL_ANGLE_A != :new.VOL_ANGLE_A then
    begin
      AIMIR_IP.INSERT_MV_DATA('EM',:new.YYYYMMDDHHMM||'00',983,:new.MDEV_ID,:new.MDEV_TYPE,location_id,:new.VOL_ANGLE_A,1001,0,capture_date_meter,capture_date_dcu,:new.DEVICE_TYPE,:new.DEVICE_ID,v_today);
      exception
        when others then
        null;
    end;
  end if;
  if (:old.VOL_THD_B is null and :new.VOL_THD_B is not null) or :old.VOL_THD_B != :new.VOL_THD_B then
    begin
      AIMIR_IP.INSERT_MV_DATA('EM',:new.YYYYMMDDHHMM||'00',984,:new.MDEV_ID,:new.MDEV_TYPE,location_id,:new.VOL_THD_B,1001,0,capture_date_meter,capture_date_dcu,:new.DEVICE_TYPE,:new.DEVICE_ID,v_today);
      exception
        when others then
        null;
    end;
  end if;
  if (:old.VOL_B is null and :new.VOL_B is not null) or :old.VOL_B != :new.VOL_B then
    begin
      AIMIR_IP.INSERT_MV_DATA('EM',:new.YYYYMMDDHHMM||'00',985,:new.MDEV_ID,:new.MDEV_TYPE,location_id,:new.VOL_B,1001,0,capture_date_meter,capture_date_dcu,:new.DEVICE_TYPE,:new.DEVICE_ID,v_today);
      exception
        when others then
        null;
    end;
  end if;
  if (:old.VOL_ANGLE_B is null and :new.VOL_ANGLE_B is not null) or :old.VOL_ANGLE_B != :new.VOL_ANGLE_B then
    begin
      AIMIR_IP.INSERT_MV_DATA('EM',:new.YYYYMMDDHHMM||'00',986,:new.MDEV_ID,:new.MDEV_TYPE,location_id,:new.VOL_ANGLE_B,1001,0,capture_date_meter,capture_date_dcu,:new.DEVICE_TYPE,:new.DEVICE_ID,v_today);
      exception
        when others then
        null;
    end;
  end if;
  if (:old.VOL_THD_C is null and :new.VOL_THD_C is not null) or :old.VOL_THD_C != :new.VOL_THD_C then
    begin
      AIMIR_IP.INSERT_MV_DATA('EM',:new.YYYYMMDDHHMM||'00',987,:new.MDEV_ID,:new.MDEV_TYPE,location_id,:new.VOL_THD_C,1001,0,capture_date_meter,capture_date_dcu,:new.DEVICE_TYPE,:new.DEVICE_ID,v_today);
      exception
        when others then
        null;
    end;
  end if;
  if (:old.VOL_C is null and :new.VOL_C is not null) or :old.VOL_C != :new.VOL_C then
    begin
      AIMIR_IP.INSERT_MV_DATA('EM',:new.YYYYMMDDHHMM||'00',988,:new.MDEV_ID,:new.MDEV_TYPE,location_id,:new.VOL_C,1001,0,capture_date_meter,capture_date_dcu,:new.DEVICE_TYPE,:new.DEVICE_ID,v_today);
      exception
        when others then
        null;
    end;
  end if;
  if (:old.VOL_ANGLE_C is null and :new.VOL_ANGLE_C is not null) or :old.VOL_ANGLE_C != :new.VOL_ANGLE_C then
    begin
      AIMIR_IP.INSERT_MV_DATA('EM',:new.YYYYMMDDHHMM||'00',989,:new.MDEV_ID,:new.MDEV_TYPE,location_id,:new.VOL_ANGLE_C,1001,0,capture_date_meter,capture_date_dcu,:new.DEVICE_TYPE,:new.DEVICE_ID,v_today);
      exception
        when others then
        null;
    end;
  end if;

exception
when others then
  null;
end;
/