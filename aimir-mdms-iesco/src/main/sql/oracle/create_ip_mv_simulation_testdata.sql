DECLARE
  v_meter_id_em NUMBER;
  v_meter_id_wm NUMBER;
  v_meter_id_gm NUMBER;
  v_meter_id_hm NUMBER;
  v_meter_id_temp NUMBER;
  v_temp_count NUMBER;
  v_temp_meter_id NUMBER;
  v_temp_location_id NUMBER;
  v_temp_location_count NUMBER;
  v_default_supplier_id NUMBER;
  v_default_metertype_id NUMBER;
  v_default_devicemodel_id NUMBER;
  v_default_meterstatus_id NUMBER;
BEGIN
  v_meter_id_em := 10000000;
  v_meter_id_wm := 20000000;
  v_meter_id_gm := 30000000;
  v_meter_id_hm := 40000000;
  v_meter_id_temp := 0;

  SELECT COUNT(*) INTO v_temp_location_count FROM LOCATION;

  SELECT ID INTO v_default_supplier_id FROM SUPPLIER;
  SELECT ID INTO v_default_meterstatus_id FROM CODE WHERE CODE='1.3.3.8';
  -- EM
  SELECT ID INTO v_default_metertype_id FROM CODE WHERE CODE='1.3.1.1';
  SELECT ID INTO v_default_devicemodel_id FROM DEVICEMODEL WHERE CODE='303'; 
  FOR v_temp_count IN 1..1000 LOOP
    v_meter_id_temp := v_meter_id_em+v_temp_count;
    SELECT METER_SEQ.NEXTVAL INTO v_temp_meter_id FROM DUAL;
    SELECT ID INTO v_temp_location_id
    FROM (SELECT ROWNUM NO, ID FROM LOCATION ORDER BY ID)
    WHERE NO=1+MOD(v_temp_count - 1,v_temp_location_count);

    INSERT INTO METER(
      ID,METER,INSTALL_DATE,INSTALL_ID,INSTALL_PROPERTY,LOCATION_ID,MDS_ID,
      METER_STATUS,METERTYPE_ID,DEVICEMODEL_ID,SUPPLIER_ID,WRITE_DATE)
    VALUES(
      v_temp_meter_id,'EnergyMeter',TO_CHAR(CURRENT_DATE,'YYYYMMDDHH24MISS'),v_meter_id_temp,
      v_meter_id_temp,v_temp_location_id,v_meter_id_temp,v_default_meterstatus_id,v_default_metertype_id,
      v_default_devicemodel_id,v_default_supplier_id,TO_CHAR(CURRENT_DATE,'YYYYMMDDHH24MISS')
    );
  END LOOP;

  -- WM
  SELECT ID INTO v_default_metertype_id FROM CODE WHERE CODE='1.3.1.2';
  SELECT ID INTO v_default_devicemodel_id FROM DEVICEMODEL WHERE CODE='1003'; 
  FOR v_temp_count IN 1..10 LOOP
    v_meter_id_temp := v_meter_id_wm + v_temp_count;
    SELECT METER_SEQ.NEXTVAL INTO v_temp_meter_id FROM DUAL;
    SELECT ID INTO v_temp_location_id
    FROM (SELECT ROWNUM NO, ID FROM LOCATION ORDER BY ID)
    WHERE NO=1+MOD(v_temp_count - 1,v_temp_location_count);

    INSERT INTO METER(
      ID,METER,INSTALL_DATE,INSTALL_ID,INSTALL_PROPERTY,LOCATION_ID,MDS_ID,
      METER_STATUS,METERTYPE_ID,DEVICEMODEL_ID,SUPPLIER_ID,WRITE_DATE)
    VALUES(
      v_temp_meter_id,'WaterMeter',TO_CHAR(CURRENT_DATE,'YYYYMMDDHH24MISS'),v_meter_id_temp,
      v_meter_id_temp,v_temp_location_id,v_meter_id_temp,v_default_meterstatus_id,v_default_metertype_id,
      v_default_devicemodel_id,v_default_supplier_id,TO_CHAR(CURRENT_DATE,'YYYYMMDDHH24MISS')
    );
  END LOOP;

  -- GM
  SELECT ID INTO v_default_metertype_id FROM CODE WHERE CODE='1.3.1.3';
  SELECT ID INTO v_default_devicemodel_id FROM DEVICEMODEL WHERE CODE='1002'; 
  FOR v_temp_count IN 1..10 LOOP
    v_meter_id_temp := v_meter_id_gm+v_temp_count;
    SELECT METER_SEQ.NEXTVAL INTO v_temp_meter_id FROM DUAL;
    SELECT ID INTO v_temp_location_id
    FROM (SELECT ROWNUM NO, ID FROM LOCATION ORDER BY ID)
    WHERE NO=1+MOD(v_temp_count - 1,v_temp_location_count);

    INSERT INTO METER(
      ID,METER,INSTALL_DATE,INSTALL_ID,INSTALL_PROPERTY,LOCATION_ID,MDS_ID,
      METER_STATUS,METERTYPE_ID,DEVICEMODEL_ID,SUPPLIER_ID,WRITE_DATE)
    VALUES(
      v_temp_meter_id,'GasMeter',TO_CHAR(CURRENT_DATE,'YYYYMMDDHH24MISS'),v_meter_id_temp,
      v_meter_id_temp,v_temp_location_id,v_meter_id_temp,v_default_meterstatus_id,v_default_metertype_id,
      v_default_devicemodel_id,v_default_supplier_id,TO_CHAR(CURRENT_DATE,'YYYYMMDDHH24MISS')
    );
  END LOOP;

  -- HM
  SELECT ID INTO v_default_metertype_id FROM CODE WHERE CODE='1.3.1.4';
  SELECT ID INTO v_default_devicemodel_id FROM DEVICEMODEL WHERE CODE='1001'; 
  FOR v_temp_count IN 1..10 LOOP
    v_meter_id_temp := v_meter_id_hm+v_temp_count;
    SELECT METER_SEQ.NEXTVAL INTO v_temp_meter_id FROM DUAL;
    SELECT ID INTO v_temp_location_id
    FROM (SELECT ROWNUM NO, ID FROM LOCATION ORDER BY ID)
    WHERE NO=1+MOD(v_temp_count - 1,v_temp_location_count);

    INSERT INTO METER(
      ID,METER,INSTALL_DATE,INSTALL_ID,INSTALL_PROPERTY,LOCATION_ID,MDS_ID,
      METER_STATUS,METERTYPE_ID,DEVICEMODEL_ID,SUPPLIER_ID,WRITE_DATE)
    VALUES(
      v_temp_meter_id,'HeatMeter',TO_CHAR(CURRENT_DATE,'YYYYMMDDHH24MISS'),v_meter_id_temp,
      v_meter_id_temp,v_temp_location_id,v_meter_id_temp,v_default_meterstatus_id,v_default_metertype_id,
      v_default_devicemodel_id,v_default_supplier_id,TO_CHAR(CURRENT_DATE,'YYYYMMDDHH24MISS')
    );
  END LOOP;

END;
/