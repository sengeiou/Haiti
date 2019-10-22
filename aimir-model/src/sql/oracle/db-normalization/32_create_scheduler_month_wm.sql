--CREATE month_wm_job
BEGIN
    DBMS_SCHEDULER.CREATE_JOB (
            job_name => '"AIMIR"."MONTH_WM_SCHEDULER"',
            job_type => 'PLSQL_BLOCK',
            job_action => 'BEGIN AIMIR.BATCH_MONTH_WM; DBMS_RESULT_CACHE.FLUSH; END;',
            number_of_arguments => 0,
            start_date => SYSTIMESTAMP,
            repeat_interval => 'FREQ=SECONDLY; INTERVAL=1',
            end_date => NULL,
            enabled => FALSE,
            auto_drop => TRUE,
            comments => 'MONTH_WM_BATCH_JOB');

 
    DBMS_SCHEDULER.SET_ATTRIBUTE( 
             name => '"AIMIR"."MONTH_WM_SCHEDULER"', 
             attribute => 'store_output', value => TRUE
             );
    DBMS_SCHEDULER.SET_ATTRIBUTE( 
             name => '"AIMIR"."MONTH_WM_SCHEDULER"', 
             attribute => 'logging_level', value => DBMS_SCHEDULER.LOGGING_OFF
             );

--ENABLE
    DBMS_SCHEDULER.enable(
             name => '"AIMIR"."MONTH_WM_SCHEDULER"');
END;
