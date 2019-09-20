BEGIN 
SYS.DBMS_SCHEDULER.CREATE_JOB(
JOB_NAME => 'MONTH_EM_SCHEDULER'
, JOB_TYPE => 'PLSQL_BLOCK'
, JOB_ACTION => 'BEGIN AIMIR.BATCH_MONTH_EM; DBMS_RESULT_CACHE.FLUSH; END;'
, START_DATE => SYSTIMESTAMP
, REPEAT_INTERVAL => 'FREQ=SECONDLY; INTERVAL=1'
, COMMENTS => 'MONTH_EM_BATCH_JOB'
);
END;

BEGIN
SYS.DBMS_SCHEDULER.ENABLE('MONTH_EM_SCHEDULER');
END;
