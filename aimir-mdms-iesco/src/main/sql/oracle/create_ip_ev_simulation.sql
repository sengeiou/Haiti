CREATE OR REPLACE PACKAGE sim_meterevent_log AS
  PROCEDURE run_meterevent_log;
END sim_meterevent_log;
/

CREATE OR REPLACE PACKAGE BODY sim_meterevent_log AS
  PROCEDURE run_meterevent_log AS
    v_mdev_id       NUMBER;
    v_mdev_id_temp  NUMBER;
    v_data_count    NUMBER;
    v_meterevent_id VARCHAR2(30);
  BEGIN
    EXECUTE IMMEDIATE 'ALTER SESSION SET TIME_ZONE=''+00:00''';

    v_mdev_id      := 0;
    v_mdev_id_temp := 0;
    FOR v_mdev_id_temp IN 1..40 LOOP
      v_mdev_id := 10000000 + v_mdev_id_temp;

      IF MOD(v_mdev_id,4) = 0 THEN
        BEGIN
          SELECT meterevent_id INTO v_meterevent_id
          FROM meterevent_log 
          WHERE activator_id=v_mdev_id AND open_time=(SELECT MAX(open_time) FROM meterevent_log where activator_id=v_mdev_id AND open_time>TO_CHAR(current_date-2,'yyyymmddhh24miss'));
        EXCEPTION
          WHEN OTHERS THEN
            v_meterevent_id := 'STE.LSIS.LSIQ-1P.2';
        END;

        IF v_meterevent_id = 'STE.LSIS.LSIQ-1P.1' THEN
          -- Power restore
          INSERT INTO METEREVENT_LOG(ACTIVATOR_ID,METEREVENT_ID,OPEN_TIME,ACTIVATOR_TYPE,INTEGRATED,MESSAGE,SUPPLIER_ID,WRITETIME,YYYYMMDD) 
          values (v_mdev_id,'STE.LSIS.LSIQ-1P.2',TO_CHAR(current_date-1/24/60,'yyyymmddhh24miss'),'EnergyMeter',null,null,(select min(id) from supplier),TO_CHAR(current_date,'yyyymmddhh24miss'),TO_CHAR(current_date,'yyyymmdd'));
        ELSIF v_meterevent_id = 'STE.LSIS.LSIQ-1P.2' THEN
          -- Power Outage
          INSERT INTO METEREVENT_LOG(ACTIVATOR_ID,METEREVENT_ID,OPEN_TIME,ACTIVATOR_TYPE,INTEGRATED,MESSAGE,SUPPLIER_ID,WRITETIME,YYYYMMDD) 
          values (v_mdev_id,'STE.LSIS.LSIQ-1P.1',TO_CHAR(current_date-1/24/60,'yyyymmddhh24miss'),'EnergyMeter',null,null,(select min(id) from supplier),TO_CHAR(current_date,'yyyymmddhh24miss'),TO_CHAR(current_date,'yyyymmdd'));
        END IF;

      ELSIF MOD(v_mdev_id,4) = 1 THEN
        -- Battery Low voltage
        INSERT INTO METEREVENT_LOG(ACTIVATOR_ID,METEREVENT_ID,OPEN_TIME,ACTIVATOR_TYPE,INTEGRATED,MESSAGE,SUPPLIER_ID,WRITETIME,YYYYMMDD) 
        values (v_mdev_id,'STE.LSIS.LSIQ-1P.8',TO_CHAR(current_date-1/24/60,'yyyymmddhh24miss'),'EnergyMeter',null,null,(select min(id) from supplier),TO_CHAR(current_date,'yyyymmddhh24miss'),TO_CHAR(current_date,'yyyymmdd'));

      ELSIF MOD(v_mdev_id,4) = 2 THEN
        -- Time changed
        INSERT INTO METEREVENT_LOG(ACTIVATOR_ID,METEREVENT_ID,OPEN_TIME,ACTIVATOR_TYPE,INTEGRATED,MESSAGE,SUPPLIER_ID,WRITETIME,YYYYMMDD) 
        values (v_mdev_id,'STE.LSIS.LSIQ-1P.5',TO_CHAR(current_date-1/24/60,'yyyymmddhh24miss'),'EnergyMeter',null,null,(select min(id) from supplier),TO_CHAR(current_date,'yyyymmddhh24miss'),TO_CHAR(current_date,'yyyymmdd'));

      ELSIF MOD(v_mdev_id,4) = 3 THEN
        -- Meter Cover open
        INSERT INTO METEREVENT_LOG(ACTIVATOR_ID,METEREVENT_ID,OPEN_TIME,ACTIVATOR_TYPE,INTEGRATED,MESSAGE,SUPPLIER_ID,WRITETIME,YYYYMMDD) 
        values (v_mdev_id,'STE.LSIS.LSIQ-1P.44',TO_CHAR(current_date-1/24/60,'yyyymmddhh24miss'),'EnergyMeter',null,null,(select min(id) from supplier),TO_CHAR(current_date,'yyyymmddhh24miss'),TO_CHAR(current_date,'yyyymmdd'));

      END IF;
    END LOOP;
    COMMIT;
  END run_meterevent_log;

END sim_meterevent_log;
/

begin
    DBMS_SCHEDULER.CREATE_JOB (
       job_name             => 'SCHEDULE_SIM_METEREVENT_LOG',
       job_type             => 'PLSQL_BLOCK',
       job_action           => 'begin sim_meterevent_log.run_meterevent_log; end;',
       repeat_interval      => 'FREQ=HOURLY;BYMINUTE=30', 
       enabled              => TRUE,
       comments             => 'Make bulk data');
end;
/
