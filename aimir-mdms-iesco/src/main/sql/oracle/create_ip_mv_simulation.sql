CREATE OR REPLACE PACKAGE sim_lp_xx AS
  PROCEDURE run_lp_mv;
  PROCEDURE run_lp_xx(
    v_metertable    VARCHAR,
    v_metercount    NUMBER
  );
END sim_lp_xx;
/

CREATE OR REPLACE PACKAGE BODY sim_lp_xx AS
  PROCEDURE insert_lp_xx(
    v_metertable    VARCHAR,
    v_mdev_type     VARCHAR,
    v_mdev_id       NUMBER,
    v_location_id   NUMBER,
    v_date_current  VARCHAR,
    v_hh            VARCHAR,
    v_channel       VARCHAR,
    v_dst           NUMBER,
    v_total_m_value FLOAT
  ) AS
  BEGIN
    IF v_metertable='EM' THEN
      INSERT INTO lp_em(mdev_type,mdev_id,location_id,yyyymmdd,yyyymmddhh,hh,channel,dst,value_00,device_id,device_type) 
      VALUES(v_mdev_type,v_mdev_id,v_location_id,v_date_current,v_date_current||v_hh,v_hh,v_channel,v_dst,v_total_m_value,1,'DCU');
      INSERT INTO power_quality(mdev_type,mdev_id,yyyymmdd,yyyymmddhhmm,hhmm,dst,device_id,device_type,
        vol_a,vol_thd_a,vol_angle_a,vol_b,vol_thd_b,vol_angle_b,vol_c,vol_thd_c,vol_angle_c,writedate)
      VALUES(v_mdev_type,v_mdev_id,v_date_current,v_date_current||v_hh||'00',v_hh||'00',v_dst,1,'DCU',
        230+trunc(DBMS_RANDOM.VALUE(0,3),2),225+trunc(DBMS_RANDOM.VALUE(0,2),2),235+trunc(DBMS_RANDOM.VALUE(0,3),2),
        230+trunc(DBMS_RANDOM.VALUE(0,3),2),225+trunc(DBMS_RANDOM.VALUE(0,2),2),235+trunc(DBMS_RANDOM.VALUE(0,3),2),
        230+trunc(DBMS_RANDOM.VALUE(0,3),2),225+trunc(DBMS_RANDOM.VALUE(0,2),2),235+trunc(DBMS_RANDOM.VALUE(0,3),2),
        to_char(current_date,'yyyymmddhh24miss'));
    ELSIF v_metertable='WM' THEN
      INSERT INTO lp_wm(mdev_type,mdev_id,location_id,yyyymmdd,yyyymmddhh,hh,channel,dst,value_00,device_id,device_type)
      VALUES(v_mdev_type,v_mdev_id,v_location_id,v_date_current,v_date_current||v_hh,v_hh,v_channel,v_dst,v_total_m_value,1,'DCU');
    ELSIF v_metertable='HM' THEN
      INSERT INTO lp_hm(mdev_type,mdev_id,location_id,yyyymmdd,yyyymmddhh,hh,channel,dst,value_00,device_id,device_type)
      VALUES(v_mdev_type,v_mdev_id,v_location_id,v_date_current,v_date_current||v_hh,v_hh,v_channel,v_dst,v_total_m_value,1,'DCU');
    END IF;
  END insert_lp_xx;

  PROCEDURE update_lp_xx(
    v_metertable    VARCHAR,
    v_mdev_type     VARCHAR,
    v_mdev_id       NUMBER,
    v_date_current  VARCHAR,
    v_hh            VARCHAR,
    v_channel       VARCHAR,
    v_total_m_value FLOAT
  ) AS
  BEGIN
    IF v_metertable='EM' THEN
      UPDATE lp_em SET value_00=v_total_m_value WHERE mdev_type=v_mdev_type AND mdev_id=v_mdev_id AND yyyymmdd=v_date_current AND yyyymmddhh=v_date_current||v_hh AND hh=v_hh AND channel=v_channel AND value_00 is null;
    ELSIF v_metertable='WM' THEN
      UPDATE lp_wm SET value_00=v_total_m_value WHERE mdev_type=v_mdev_type AND mdev_id=v_mdev_id AND yyyymmdd=v_date_current AND yyyymmddhh=v_date_current||v_hh AND hh=v_hh AND channel=v_channel AND value_00 is null;
    ELSIF v_metertable='GM' THEN
      UPDATE lp_gm SET value_00=v_total_m_value WHERE mdev_type=v_mdev_type AND mdev_id=v_mdev_id AND yyyymmdd=v_date_current AND yyyymmddhh=v_date_current||v_hh AND hh=v_hh AND channel=v_channel AND value_00 is null;
    ELSIF v_metertable='HM' THEN
      UPDATE lp_hm SET value_00=v_total_m_value WHERE mdev_type=v_mdev_type AND mdev_id=v_mdev_id AND yyyymmdd=v_date_current AND yyyymmddhh=v_date_current||v_hh AND hh=v_hh AND channel=v_channel AND value_00 is null;
    END IF;
  END update_lp_xx;

  PROCEDURE run_lp_xx(
    v_metertable    VARCHAR,
    v_metercount    NUMBER
    ) AS
    v_m_dev_id_def  NUMBER;
    V_mdev_id       NUMBER;
    v_mdev_id_temp  NUMBER;
    v_mdev_type     VARCHAR(20);
    v_date_current  VARCHAR(8);
    v_date_previous VARCHAR(14);
    v_hh            VARCHAR(2);
    v_dst           NUMBER;
    v_total_m_value FLOAT;
    v_m_value       FLOAT;
    v_data_count    NUMBER;
    v_location_id   NUMBER;
  BEGIN
    EXECUTE IMMEDIATE 'ALTER SESSION SET TIME_ZONE=''+00:00''';
    v_mdev_type     := 'Meter';
    v_dst           := 0;
    v_date_current  := to_char(current_date,'yyyymmdd');
    v_date_previous := to_char(current_date-2,'yyyymmddhh24miss');
    v_hh            := to_char(current_date, 'hh24');
    v_total_m_value := 0;
    v_m_value       := 0;
    v_m_dev_id_def  := 0;
 
    IF v_metertable='EM' THEN
      v_m_dev_id_def := 10000000;
    ELSIF v_metertable='WM' THEN
      v_m_dev_id_def := 20000000;
    ELSIF v_metertable='GM' THEN
      v_m_dev_id_def := 30000000;
    ELSIF v_metertable='HM' THEN
      v_m_dev_id_def := 40000000;
    END IF;
    FOR v_mdev_id_temp IN 1..v_metercount LOOP
      v_mdev_id := v_m_dev_id_def + v_mdev_id_temp;
      SELECT location_id into v_location_id FROM meter
      WHERE mds_id=to_char(v_mdev_id);

      FOR v_channel IN (SELECT DISTINCT to_char(channel_id) AS name FROM channel_config WHERE data_type='Lp'||v_metertable UNION
                        SELECT '998' FROM dual UNION
                        SELECT '999' FROM dual) LOOP

        CASE (v_channel.name)
        WHEN '998' THEN
          SELECT (TO_DATE(v_date_current||v_hh,'yyyymmddhh24')-TO_DATE('19700101','yyyymmdd'))*24*60*60*1000 
          INTO v_total_m_value FROM dual;
        WHEN '999' THEN
          SELECT (current_date-TO_DATE('19700101','yyyymmdd'))*24*60*60*1000
          INTO v_total_m_value FROM dual;
        ELSE
          IF v_metertable='EM' THEN
            SELECT MAX(mv_value) INTO v_total_m_value
            FROM ip_mv_temp_em
            WHERE mdev_id = v_mdev_id AND yyyymmddhhmmss >= v_date_previous
              AND channel = v_channel.name AND mdev_type = v_mdev_type;
          ELSIF v_metertable='WM' THEN
            SELECT MAX(mv_value) INTO v_total_m_value
            FROM ip_mv_temp_wm
            WHERE mdev_id = v_mdev_id AND yyyymmddhhmmss >= v_date_previous
              AND channel = v_channel.name AND mdev_type = v_mdev_type;
          ELSIF v_metertable='GM' THEN
            SELECT MAX(mv_value) INTO v_total_m_value
            FROM ip_mv_temp_gm
            WHERE mdev_id = v_mdev_id AND yyyymmddhhmmss >= v_date_previous
              AND channel = v_channel.name AND mdev_type = v_mdev_type;
          ELSIF v_metertable='HM' THEN
            SELECT MAX(mv_value) INTO v_total_m_value
            FROM ip_mv_temp_hm
            WHERE mdev_id = v_mdev_id AND yyyymmddhhmmss >= v_date_previous
              AND channel = v_channel.name AND mdev_type = v_mdev_type;
          END IF;

          SELECT DBMS_RANDOM.VALUE(0.01,1) INTO v_m_value from dual;

          IF v_total_m_value IS null THEN
            v_total_m_value := 0;
          END IF;

          v_total_m_value := v_total_m_value + v_m_value;
        END CASE;

        IF v_metertable='EM' THEN
          SELECT COUNT(*) INTO v_data_count
          FROM lp_em
          WHERE mdev_id = v_mdev_id AND yyyymmdd >= v_date_current
            AND channel = v_channel.name AND mdev_type = v_mdev_type;
        ELSIF v_metertable='WM' THEN
          SELECT COUNT(*) INTO v_data_count
          FROM lp_wm
          WHERE mdev_id = v_mdev_id AND yyyymmdd >= v_date_current
            AND channel = v_channel.name AND mdev_type = v_mdev_type;
        ELSIF v_metertable='GM' THEN
          SELECT COUNT(*) INTO v_data_count
          FROM lp_gm
          WHERE mdev_id = v_mdev_id AND yyyymmdd >= v_date_current
            AND channel = v_channel.name AND mdev_type = v_mdev_type;
        ELSIF v_metertable='HM' THEN
          SELECT COUNT(*) INTO v_data_count
          FROM lp_hm
          WHERE mdev_id = v_mdev_id AND yyyymmdd >= v_date_current
            AND channel = v_channel.name AND mdev_type = v_mdev_type;
        END IF;

        IF v_data_count = 0 THEN
          insert_lp_xx(v_metertable,v_mdev_type,v_mdev_id,v_location_id,v_date_current,v_hh,v_channel.name,v_dst,v_total_m_value);
        ELSE
          update_lp_xx(v_metertable,v_mdev_type,v_mdev_id,v_date_current,v_hh,v_channel.name,v_total_m_value);
        END IF;
  
      END LOOP;
  
      COMMIT;
    END LOOP;
  END run_lp_xx;

  PROCEDURE run_lp_mv AS
  BEGIN
    run_lp_xx('EM',1000);
    run_lp_xx('GM',10);
    run_lp_xx('WM',10);
    run_lp_xx('HM',10);
  END run_lp_mv;

END sim_lp_xx;
/

begin
    DBMS_SCHEDULER.CREATE_JOB (
       job_name             => 'SCHEDULE_SIM_LP_XX',
       job_type             => 'PLSQL_BLOCK',
       job_action           => 'begin sim_lp_xx.run_lp_mv; end;',
       repeat_interval      => 'FREQ=HOURLY;BYMINUTE=5', 
       enabled              => TRUE,
       comments             => 'Make bulk data');
end;
/
