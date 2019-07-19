create or replace trigger IP_LP_HM
after insert or update on LP_HM
for each row
declare
  capture_date_meter number;
  capture_date_dcu number;
  meter_value_status number;
  channelidx number;
  ignoredata_exp exception;
  temp_date date;
  v_today timestamp with time zone;
begin
  EXECUTE IMMEDIATE 'ALTER SESSION SET TIME_ZONE=''+00:00''';
  v_today := current_timestamp;

  capture_date_meter := null;
  capture_date_dcu := null;
  meter_value_status := null;
  channelidx := null;

  begin
    temp_date := to_date(:new.YYYYMMDDHH,'yyyymmddhh24');
    exception
      when others then
      raise ignoredata_exp;
  end;

  if (:new.CHANNEL=5) then
    channelidx := 998;
  elsif (:new.CHANNEL=6) then
    channelidx := 999;
  --elsif (:new.CHANNEL=0 or :new.CHANNEL=98 or :new.CHANNEL=100) then   -- 0(Co2) 98(integrate flag, send or not?) 100(Validation flag)
  elsif (:new.CHANNEL=0 or :new.CHANNEL=98) then   -- 0(Co2) 98(integrate flag, send or not?) 100(Validation flag)
    raise ignoredata_exp;
  else
    channelidx := :new.CHANNEL;
  end if;

  if (channelidx!=999 AND channelidx!=998 AND channelidx != 100) then

    if (:old.VALUE_00 is null and :new.VALUE_00 is not null) or :old.VALUE_00 != :new.VALUE_00 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'0000',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_00);
        capture_date_meter := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'0000',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu   := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'0000',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'0000',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'0000',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_00,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_01 is null and :new.VALUE_01 is not null) or :old.VALUE_01 != :new.VALUE_01 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'0100',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_01);
        capture_date_meter    := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'0100',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu      := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'0100',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'0100',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'0100',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_01,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_02 is null and :new.VALUE_02 is not null) or :old.VALUE_02 != :new.VALUE_02 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'0200',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_02);
        capture_date_meter    := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'0200',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu      := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'0200',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'0200',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'0200',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_02,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_03 is null and :new.VALUE_03 is not null) or :old.VALUE_03 != :new.VALUE_03 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'0300',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_03);
        capture_date_meter    := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'0300',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu      := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'0300',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'0300',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'0300',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_03,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_04 is null and :new.VALUE_04 is not null) or :old.VALUE_04 != :new.VALUE_04 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'0400',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_04);
        capture_date_meter    := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'0400',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu      := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'0400',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'0400',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'0400',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_04,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_05 is null and :new.VALUE_05 is not null) or :old.VALUE_05 != :new.VALUE_05 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'0500',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_05);
        capture_date_meter    := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'0500',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu      := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'0500',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'0500',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'0500',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_05,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_06 is null and :new.VALUE_06 is not null) or :old.VALUE_06 != :new.VALUE_06 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'0600',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_06);
        capture_date_meter    := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'0600',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu      := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'0600',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'0600',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'0600',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_06,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_07 is null and :new.VALUE_07 is not null) or :old.VALUE_07 != :new.VALUE_07 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'0700',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_07);
        capture_date_meter    := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'0700',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu      := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'0700',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'0700',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'0700',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_07,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_08 is null and :new.VALUE_08 is not null) or :old.VALUE_08 != :new.VALUE_08 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'0800',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_08);
        capture_date_meter    := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'0800',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu      := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'0800',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'0800',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'0800',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_08,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_09 is null and :new.VALUE_09 is not null) or :old.VALUE_09 != :new.VALUE_09 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'0900',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_09);
        capture_date_meter    := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'0900',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu      := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'0900',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'0900',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'0900',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_09,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_10 is null and :new.VALUE_10 is not null) or :old.VALUE_10 != :new.VALUE_10 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'1000',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_10);
        capture_date_meter    := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'1000',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu      := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'1000',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'1000',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'1000',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_10,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_11 is null and :new.VALUE_11 is not null) or :old.VALUE_11 != :new.VALUE_11 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'1100',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_11);
        capture_date_meter    := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'1100',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu      := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'1100',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'1100',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'1100',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_11,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_12 is null and :new.VALUE_12 is not null) or :old.VALUE_12 != :new.VALUE_12 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'1200',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_12);
        capture_date_meter    := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'1200',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu      := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'1200',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'1200',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'1200',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_12,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_13 is null and :new.VALUE_13 is not null) or :old.VALUE_13 != :new.VALUE_13 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'1300',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_13);
        capture_date_meter    := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'1300',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu      := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'1300',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'1300',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'1300',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_13,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_14 is null and :new.VALUE_14 is not null) or :old.VALUE_14 != :new.VALUE_14 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'1400',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_14);
        capture_date_meter    := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'1400',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu      := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'1400',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'1400',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'1400',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_14,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_15 is null and :new.VALUE_15 is not null) or :old.VALUE_15 != :new.VALUE_15 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'1500',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_15);
        capture_date_meter    := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'1500',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu      := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'1500',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'1500',100,:new.MDEV_ID,:new.MDEV_TYPE);
       AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'1500',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_15,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_16 is null and :new.VALUE_16 is not null) or :old.VALUE_16 != :new.VALUE_16 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'1600',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_16);
        capture_date_meter    := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'1600',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu      := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'1600',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'1600',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'1600',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_16,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_17 is null and :new.VALUE_17 is not null) or :old.VALUE_17 != :new.VALUE_17 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'1700',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_17);
        capture_date_meter    := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'1700',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu      := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'1700',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'1700',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'1700',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_17,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_18 is null and :new.VALUE_18 is not null) or :old.VALUE_18 != :new.VALUE_18 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'1800',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_18);
        capture_date_meter    := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'1800',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu      := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'1800',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'1800',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'1800',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_18,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_19 is null and :new.VALUE_19 is not null) or :old.VALUE_19 != :new.VALUE_19 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'1900',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_19);
        capture_date_meter    := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'1900',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu      := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'1900',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'1900',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'1900',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_19,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_20 is null and :new.VALUE_20 is not null) or :old.VALUE_20 != :new.VALUE_20 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'2000',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_20);
        capture_date_meter    := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'2000',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu      := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'2000',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'2000',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'2000',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_20,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_21 is null and :new.VALUE_21 is not null) or :old.VALUE_21 != :new.VALUE_21 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'2100',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_21);
        capture_date_meter    := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'2100',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu      := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'2100',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'2100',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'2100',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_21,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_22 is null and :new.VALUE_22 is not null) or :old.VALUE_22 != :new.VALUE_22 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'2200',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_22);
        capture_date_meter    := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'2200',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu      := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'2200',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'2200',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'2200',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_22,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_23 is null and :new.VALUE_23 is not null) or :old.VALUE_23 != :new.VALUE_23 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'2300',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_23);
        capture_date_meter    := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'2300',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu      := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'2300',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'2300',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'2300',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_23,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_24 is null and :new.VALUE_24 is not null) or :old.VALUE_24 != :new.VALUE_24 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'2400',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_24);
        capture_date_meter    := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'2400',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu      := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'2400',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'2400',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'2400',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_24,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_25 is null and :new.VALUE_25 is not null) or :old.VALUE_25 != :new.VALUE_25 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'2500',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_25);
        capture_date_meter    := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'2500',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu      := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'2500',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'2500',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'2500',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_25,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_26 is null and :new.VALUE_26 is not null) or :old.VALUE_26 != :new.VALUE_26 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'2600',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_26);
        capture_date_meter    := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'2600',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu      := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'2600',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'2600',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'2600',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_26,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_27 is null and :new.VALUE_27 is not null) or :old.VALUE_27 != :new.VALUE_27 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'2700',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_27);
        capture_date_meter    := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'2700',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu      := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'2700',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'2700',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'2700',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_27,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_28 is null and :new.VALUE_28 is not null) or :old.VALUE_28 != :new.VALUE_28 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'2800',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_28);
        capture_date_meter    := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'2800',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu      := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'2800',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'2800',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'2800',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_28,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_29 is null and :new.VALUE_29 is not null) or :old.VALUE_29 != :new.VALUE_29 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'2900',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_29);
        capture_date_meter    := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'2900',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu      := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'2900',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'2900',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'2900',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_29,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_30 is null and :new.VALUE_30 is not null) or :old.VALUE_30 != :new.VALUE_30 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'3000',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_30);
        capture_date_meter    := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'3000',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu      := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'3000',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'3000',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'3000',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_30,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_31 is null and :new.VALUE_31 is not null) or :old.VALUE_31 != :new.VALUE_31 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'3100',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_31);
        capture_date_meter    := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'3100',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu      := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'3100',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'3100',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'3100',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_31,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_32 is null and :new.VALUE_32 is not null) or :old.VALUE_32 != :new.VALUE_32 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'3200',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_32);
        capture_date_meter    := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'3200',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu      := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'3200',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'3200',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'3200',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_32,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_33 is null and :new.VALUE_33 is not null) or :old.VALUE_33 != :new.VALUE_33 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'3300',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_33);
        capture_date_meter := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'3300',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu   := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'3300',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'3300',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'3300',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_33,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_34 is null and :new.VALUE_34 is not null) or :old.VALUE_34 != :new.VALUE_34 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'3400',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_34);
        capture_date_meter := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'3400',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu   := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'3400',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'3400',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'3400',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_34,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_35 is null and :new.VALUE_35 is not null) or :old.VALUE_35 != :new.VALUE_35 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'3500',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_35);
        capture_date_meter := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'3500',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu   := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'3500',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'3500',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'3500',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_35,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_36 is null and :new.VALUE_36 is not null) or :old.VALUE_36 != :new.VALUE_36 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'3600',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_36);
        capture_date_meter := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'3600',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu   := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'3600',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'3600',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'3600',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_36,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_37 is null and :new.VALUE_37 is not null) or :old.VALUE_37 != :new.VALUE_37 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'3700',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_37);
        capture_date_meter := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'3700',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu   := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'3700',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'3700',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'3700',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_37,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_38 is null and :new.VALUE_38 is not null) or :old.VALUE_38 != :new.VALUE_38 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'3800',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_38);
        capture_date_meter := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'3800',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu   := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'3800',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'3800',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'3800',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_38,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_39 is null and :new.VALUE_39 is not null) or :old.VALUE_39 != :new.VALUE_39 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'3900',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_39);
        capture_date_meter := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'3900',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu   := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'3900',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'3900',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'3900',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_39,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_40 is null and :new.VALUE_40 is not null) or :old.VALUE_40 != :new.VALUE_40 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'4000',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_40);
        capture_date_meter := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'4000',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu   := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'4000',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'4000',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'4000',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_40,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_41 is null and :new.VALUE_41 is not null) or :old.VALUE_41 != :new.VALUE_41 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'4100',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_41);
        capture_date_meter := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'4100',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu   := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'4100',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'4100',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'4100',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_41,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_42 is null and :new.VALUE_42 is not null) or :old.VALUE_42 != :new.VALUE_42 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'4200',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_42);
        capture_date_meter := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'4200',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu   := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'4200',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'4200',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'4200',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_42,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_43 is null and :new.VALUE_43 is not null) or :old.VALUE_43 != :new.VALUE_43 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'4300',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_43);
        capture_date_meter := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'4300',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu   := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'4300',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'4300',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'4300',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_43,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_44 is null and :new.VALUE_44 is not null) or :old.VALUE_44 != :new.VALUE_44 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'4400',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_44);
        capture_date_meter := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'4400',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu   := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'4400',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'4400',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'4400',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_44,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_45 is null and :new.VALUE_45 is not null) or :old.VALUE_45 != :new.VALUE_45 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'4500',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_45);
        capture_date_meter := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'4500',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu   := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'4500',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'4500',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'4500',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_45,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_46 is null and :new.VALUE_46 is not null) or :old.VALUE_46 != :new.VALUE_46 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'4600',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_46);
        capture_date_meter := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'4600',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu   := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'4600',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'4600',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'4600',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_46,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_47 is null and :new.VALUE_47 is not null) or :old.VALUE_47 != :new.VALUE_47 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'4700',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_47);
        capture_date_meter := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'4700',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu   := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'4700',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'4700',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'4700',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_47,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_48 is null and :new.VALUE_48 is not null) or :old.VALUE_48 != :new.VALUE_48 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'4800',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_48);
        capture_date_meter := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'4800',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu   := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'4800',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'4800',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'4800',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_48,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_49 is null and :new.VALUE_49 is not null) or :old.VALUE_49 != :new.VALUE_49 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'4900',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_49);
        capture_date_meter := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'4900',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu   := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'4900',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'4900',100,:new.MDEV_ID,:new.MDEV_TYPE);
       AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'4900',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_49,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_50 is null and :new.VALUE_50 is not null) or :old.VALUE_50 != :new.VALUE_50 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'5000',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_50);
        capture_date_meter := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'5000',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu   := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'5000',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'5000',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'5000',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_50,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_51 is null and :new.VALUE_51 is not null) or :old.VALUE_51 != :new.VALUE_51 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'5100',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_51);
        capture_date_meter := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'5100',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu   := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'5100',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'5100',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'5100',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_51,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_52 is null and :new.VALUE_52 is not null) or :old.VALUE_52 != :new.VALUE_52 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'5200',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_52);
        capture_date_meter := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'5200',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu   := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'5200',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'5200',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'5200',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_52,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_53 is null and :new.VALUE_53 is not null) or :old.VALUE_53 != :new.VALUE_53 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'5300',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_53);
        capture_date_meter := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'5300',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu   := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'5300',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'5300',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'5300',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_53,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_54 is null and :new.VALUE_54 is not null) or :old.VALUE_54 != :new.VALUE_54 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'5400',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_54);
        capture_date_meter := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'5400',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu   := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'5400',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'5400',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'5400',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_54,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_55 is null and :new.VALUE_55 is not null) or :old.VALUE_55 != :new.VALUE_55 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'5500',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_55);
        capture_date_meter := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'5500',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu   := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'5500',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'5500',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'5500',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_55,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_56 is null and :new.VALUE_56 is not null) or :old.VALUE_56 != :new.VALUE_56 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'5600',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_56);
        capture_date_meter := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'5600',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu   := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'5600',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'5600',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'5600',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_56,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_57 is null and :new.VALUE_57 is not null) or :old.VALUE_57 != :new.VALUE_57 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'5700',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_57);
        capture_date_meter := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'5700',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu   := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'5700',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'5700',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'5700',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_57,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_58 is null and :new.VALUE_58 is not null) or :old.VALUE_58 != :new.VALUE_58 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'5800',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_58);
        capture_date_meter := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'5800',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu   := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'5800',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'5800',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'5800',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_58,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;
    if (:old.VALUE_59 is null and :new.VALUE_59 is not null) or :old.VALUE_59 != :new.VALUE_59 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'5900',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_59);
        capture_date_meter := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'5900',998,:new.MDEV_ID,:new.MDEV_TYPE);
        capture_date_dcu   := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'5900',999,:new.MDEV_ID,:new.MDEV_TYPE);
        meter_value_status := AIMIR_IP.GET_MV_FROM_TEMP('HM',:new.YYYYMMDDHH||'5900',100,:new.MDEV_ID,:new.MDEV_TYPE);
        AIMIR_IP.INSERT_MV_VALID('HM',:new.YYYYMMDDHH||'5900',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_59,meter_value_status,:new.LOCATION_ID,capture_date_meter,capture_date_dcu,v_today);

        exception
          when others then
          null;
      end;
    end if;

  else

    if (:old.VALUE_00 is null and :new.VALUE_00 is not null) or :old.VALUE_00 != :new.VALUE_00 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'0000',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_00);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'0000',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_00);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'0000',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_00);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_01 is null and :new.VALUE_01 is not null) or :old.VALUE_01 != :new.VALUE_01 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'0100',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_01);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'0100',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_01);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'0100',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_01);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_02 is null and :new.VALUE_02 is not null) or :old.VALUE_02 != :new.VALUE_02 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'0200',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_02);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'0200',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_02);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'0200',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_02);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_03 is null and :new.VALUE_03 is not null) or :old.VALUE_03 != :new.VALUE_03 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'0300',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_03);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'0300',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_03);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'0300',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_03);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_04 is null and :new.VALUE_04 is not null) or :old.VALUE_04 != :new.VALUE_04 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'0400',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_04);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'0400',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_04);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'0400',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_04);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_05 is null and :new.VALUE_05 is not null) or :old.VALUE_05 != :new.VALUE_05 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'0500',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_05);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'0500',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_05);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'0500',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_05);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_06 is null and :new.VALUE_06 is not null) or :old.VALUE_06 != :new.VALUE_06 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'0600',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_06);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'0600',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_06);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'0600',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_06);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_07 is null and :new.VALUE_07 is not null) or :old.VALUE_07 != :new.VALUE_07 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'0700',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_07);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'0700',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_07);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'0700',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_07);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_08 is null and :new.VALUE_08 is not null) or :old.VALUE_08 != :new.VALUE_08 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'0800',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_08);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'0800',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_08);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'0800',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_08);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_09 is null and :new.VALUE_09 is not null) or :old.VALUE_09 != :new.VALUE_09 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'0900',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_09);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'0900',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_09);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'0900',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_09);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_10 is null and :new.VALUE_10 is not null) or :old.VALUE_10 != :new.VALUE_10 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'1000',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_10);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'1000',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_10);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'1000',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_10);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_11 is null and :new.VALUE_11 is not null) or :old.VALUE_11 != :new.VALUE_11 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'1100',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_11);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'1100',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_11);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'1100',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_11);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_12 is null and :new.VALUE_12 is not null) or :old.VALUE_12 != :new.VALUE_12 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'1200',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_12);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'1200',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_12);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'1200',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_12);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_13 is null and :new.VALUE_13 is not null) or :old.VALUE_13 != :new.VALUE_13 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'1300',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_13);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'1300',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_13);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'1300',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_13);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_14 is null and :new.VALUE_14 is not null) or :old.VALUE_14 != :new.VALUE_14 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'1400',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_14);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'1400',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_14);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'1400',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_14);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_15 is null and :new.VALUE_15 is not null) or :old.VALUE_15 != :new.VALUE_15 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'1500',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_15);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'1500',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_15);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'1500',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_15);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_16 is null and :new.VALUE_16 is not null) or :old.VALUE_16 != :new.VALUE_16 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'1600',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_16);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'1600',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_16);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'1600',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_16);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_17 is null and :new.VALUE_17 is not null) or :old.VALUE_17 != :new.VALUE_17 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'1700',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_17);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'1700',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_17);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'1700',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_17);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_18 is null and :new.VALUE_18 is not null) or :old.VALUE_18 != :new.VALUE_18 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'1800',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_18);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'1800',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_18);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'1800',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_18);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_19 is null and :new.VALUE_19 is not null) or :old.VALUE_19 != :new.VALUE_19 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'1900',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_19);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'1900',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_19);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'1900',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_19);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_20 is null and :new.VALUE_20 is not null) or :old.VALUE_20 != :new.VALUE_20 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'2000',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_20);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'2000',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_20);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'2000',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_20);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_21 is null and :new.VALUE_21 is not null) or :old.VALUE_21 != :new.VALUE_21 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'2100',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_21);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'2100',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_21);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'2100',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_21);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_22 is null and :new.VALUE_22 is not null) or :old.VALUE_22 != :new.VALUE_22 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'2200',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_22);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'2200',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_22);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'2200',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_22);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_23 is null and :new.VALUE_23 is not null) or :old.VALUE_23 != :new.VALUE_23 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'2300',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_23);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'2300',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_23);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'2300',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_23);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_24 is null and :new.VALUE_24 is not null) or :old.VALUE_24 != :new.VALUE_24 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'2400',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_24);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'2400',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_24);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'2400',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_24);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_25 is null and :new.VALUE_25 is not null) or :old.VALUE_25 != :new.VALUE_25 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'2500',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_25);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'2500',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_25);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'2500',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_25);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_26 is null and :new.VALUE_26 is not null) or :old.VALUE_26 != :new.VALUE_26 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'2600',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_26);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'2600',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_26);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'2600',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_26);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_27 is null and :new.VALUE_27 is not null) or :old.VALUE_27 != :new.VALUE_27 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'2700',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_27);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'2700',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_27);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'2700',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_27);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_28 is null and :new.VALUE_28 is not null) or :old.VALUE_28 != :new.VALUE_28 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'2800',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_28);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'2800',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_28);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'2800',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_28);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_29 is null and :new.VALUE_29 is not null) or :old.VALUE_29 != :new.VALUE_29 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'2900',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_29);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'2900',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_29);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'2900',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_29);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_30 is null and :new.VALUE_30 is not null) or :old.VALUE_30 != :new.VALUE_30 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'3000',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_30);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'3000',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_30);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'3000',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_30);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_31 is null and :new.VALUE_31 is not null) or :old.VALUE_31 != :new.VALUE_31 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'3100',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_31);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'3100',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_31);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'3100',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_31);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_32 is null and :new.VALUE_32 is not null) or :old.VALUE_32 != :new.VALUE_32 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'3200',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_32);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'3200',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_32);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'3200',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_32);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_33 is null and :new.VALUE_33 is not null) or :old.VALUE_33 != :new.VALUE_33 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'3300',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_33);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'3300',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_33);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'3300',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_33);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_34 is null and :new.VALUE_34 is not null) or :old.VALUE_34 != :new.VALUE_34 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'3400',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_34);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'1200',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_12);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'1200',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_12);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_35 is null and :new.VALUE_35 is not null) or :old.VALUE_35 != :new.VALUE_35 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'3500',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_35);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'3500',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_35);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'3500',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_35);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_36 is null and :new.VALUE_36 is not null) or :old.VALUE_36 != :new.VALUE_36 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'3600',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_36);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'1200',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_12);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'1200',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_12);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_37 is null and :new.VALUE_37 is not null) or :old.VALUE_37 != :new.VALUE_37 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'3700',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_37);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'3700',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_37);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'3700',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_37);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_38 is null and :new.VALUE_38 is not null) or :old.VALUE_38 != :new.VALUE_38 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'3800',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_38);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'3800',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_38);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'3800',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_38);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_39 is null and :new.VALUE_39 is not null) or :old.VALUE_39 != :new.VALUE_39 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'3900',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_39);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'3900',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_39);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'3900',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_39);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_40 is null and :new.VALUE_40 is not null) or :old.VALUE_40 != :new.VALUE_40 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'4000',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_40);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'4000',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_40);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'4000',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_40);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_41 is null and :new.VALUE_41 is not null) or :old.VALUE_41 != :new.VALUE_41 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'4100',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_41);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'4100',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_41);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'4100',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_41);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_42 is null and :new.VALUE_42 is not null) or :old.VALUE_42 != :new.VALUE_42 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'4200',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_42);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'4200',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_42);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'4200',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_42);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_43 is null and :new.VALUE_43 is not null) or :old.VALUE_43 != :new.VALUE_43 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'4300',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_43);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'4300',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_43);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'4300',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_43);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_44 is null and :new.VALUE_44 is not null) or :old.VALUE_44 != :new.VALUE_44 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'4400',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_44);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'4400',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_44);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'4400',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_44);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_45 is null and :new.VALUE_45 is not null) or :old.VALUE_45 != :new.VALUE_45 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'4500',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_45);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'4500',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_45);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'4500',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_45);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_46 is null and :new.VALUE_46 is not null) or :old.VALUE_46 != :new.VALUE_46 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'4600',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_46);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'4600',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_46);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'4600',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_46);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_47 is null and :new.VALUE_47 is not null) or :old.VALUE_47 != :new.VALUE_47 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'4700',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_47);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'4700',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_47);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'4700',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_47);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_48 is null and :new.VALUE_48 is not null) or :old.VALUE_48 != :new.VALUE_48 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'4800',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_48);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'4800',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_48);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'4800',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_48);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_49 is null and :new.VALUE_49 is not null) or :old.VALUE_49 != :new.VALUE_49 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'4900',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_49);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'4900',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_49);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'4900',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_49);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_50 is null and :new.VALUE_50 is not null) or :old.VALUE_50 != :new.VALUE_50 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'5000',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_50);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'5000',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_50);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'5000',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_50);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_51 is null and :new.VALUE_51 is not null) or :old.VALUE_51 != :new.VALUE_51 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'5100',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_51);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'5100',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_51);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'5100',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_51);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_52 is null and :new.VALUE_52 is not null) or :old.VALUE_52 != :new.VALUE_52 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'5200',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_52);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'5200',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_52);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'5200',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_52);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_53 is null and :new.VALUE_53 is not null) or :old.VALUE_53 != :new.VALUE_53 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'5300',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_53);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'5300',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_53);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'5300',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_53);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_54 is null and :new.VALUE_54 is not null) or :old.VALUE_54 != :new.VALUE_54 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'5400',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_54);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'5400',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_54);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'5400',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_54);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_55 is null and :new.VALUE_55 is not null) or :old.VALUE_55 != :new.VALUE_55 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'5500',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_55);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'5500',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_55);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'5500',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_55);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_56 is null and :new.VALUE_56 is not null) or :old.VALUE_56 != :new.VALUE_56 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'5600',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_56);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'5600',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_56);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'5600',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_56);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_57 is null and :new.VALUE_57 is not null) or :old.VALUE_57 != :new.VALUE_57 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'5700',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_57);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'5700',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_57);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'5700',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_57);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_58 is null and :new.VALUE_58 is not null) or :old.VALUE_58 != :new.VALUE_58 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'5800',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_58);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'5800',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_58);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'5800',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_58);
        end if;
      exception
        when others then
        null;
      end;
    end if;
    if (:old.VALUE_59 is null and :new.VALUE_59 is not null) or :old.VALUE_59 != :new.VALUE_59 then
      begin
        AIMIR_IP.INSERT_MV_TEMP('HM',:new.YYYYMMDDHH||'5900',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.VALUE_59);
        if channelidx = 998 or channelidx = 999 then
          AIMIR_IP.UPDATECAPDATE_VALID('HM',:new.YYYYMMDDHH||'5900',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_59);
        elsif channelidx = 100 then
          AIMIR_IP.UPDATESTATUS_VALID('HM',:new.YYYYMMDDHH||'5900',channelidx,:new.MDEV_ID,:new.MDEV_TYPE,:new.DEVICE_TYPE,:new.DEVICE_ID,:new.VALUE_59);
        end if;
      exception
        when others then
        null;
      end;
    end if;

  end if;

exception
when ignoredata_exp then
  null;
when others then
  null;
end;
/