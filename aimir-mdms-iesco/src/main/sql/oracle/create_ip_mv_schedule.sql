begin
    DBMS_SCHEDULER.CREATE_JOB (
       job_name             => 'process_generate_outbound',
       job_type             => 'PLSQL_BLOCK',
       job_action           => 'begin aimir_mv.run_all; end;',
       repeat_interval      => 'FREQ=MINUTELY;INTERVAL=2;BYSECOND=17', 
       enabled              =>  TRUE,
       comments             => 'Generate outboundmeter value');
end;
/

begin
    DBMS_SCHEDULER.CREATE_JOB (
       job_name             => 'process_validation',
       job_type             => 'PLSQL_BLOCK',
       job_action           => 'begin aimir_validation.process_mv; end;',
       repeat_interval      => 'FREQ=MINUTELY;INTERVAL=2;BYSECOND=17', 
       enabled              =>  TRUE,
       comments             => 'Validate meter value');
end;
/

------  parallels process
begin
    DBMS_SCHEDULER.CREATE_JOB (
       job_name             => 'process_validation_em_1',
       job_type             => 'PLSQL_BLOCK',
       job_action           => 'begin aimir_validation.process_mv_schedule(''EM'',0);aimir_validation.process_mv_schedule(''EM'',1); end;',
       repeat_interval      => 'FREQ=MINUTELY;INTERVAL=2;BYSECOND=17', 
       enabled              =>  TRUE,
       comments             => 'Validate meter value');
end;
/

begin
    DBMS_SCHEDULER.CREATE_JOB (
       job_name             => 'process_validation_em_2',
       job_type             => 'PLSQL_BLOCK',
       job_action           => 'begin aimir_validation.process_mv_schedule(''EM'',2);aimir_validation.process_mv_schedule(''EM'',3); end;',
       repeat_interval      => 'FREQ=MINUTELY;INTERVAL=2;BYSECOND=17', 
       enabled              =>  TRUE,
       comments             => 'Validate meter value');
end;
/

begin
    DBMS_SCHEDULER.CREATE_JOB (
       job_name             => 'process_validation_em_3',
       job_type             => 'PLSQL_BLOCK',
       job_action           => 'begin aimir_validation.process_mv_schedule(''EM'',4);aimir_validation.process_mv_schedule(''EM'',5); end;',
       repeat_interval      => 'FREQ=MINUTELY;INTERVAL=2;BYSECOND=17', 
       enabled              =>  TRUE,
       comments             => 'Validate meter value');
end;
/

begin
    DBMS_SCHEDULER.CREATE_JOB (
       job_name             => 'process_validation_em_4',
       job_type             => 'PLSQL_BLOCK',
       job_action           => 'begin aimir_validation.process_mv_schedule(''EM'',6);aimir_validation.process_mv_schedule(''EM'',7); end;',
       repeat_interval      => 'FREQ=MINUTELY;INTERVAL=2;BYSECOND=17', 
       enabled              =>  TRUE,
       comments             => 'Validate meter value');
end;
/

begin
    DBMS_SCHEDULER.CREATE_JOB (
       job_name             => 'process_validation_em_5',
       job_type             => 'PLSQL_BLOCK',
       job_action           => 'begin aimir_validation.process_mv_schedule(''EM'',8);aimir_validation.process_mv_schedule(''EM'',9); end;',
       repeat_interval      => 'FREQ=MINUTELY;INTERVAL=2;BYSECOND=17', 
       enabled              =>  TRUE,
       comments             => 'Validate meter value');
end;
/

begin
    DBMS_SCHEDULER.CREATE_JOB (
       job_name             => 'process_validation_wm',
       job_type             => 'PLSQL_BLOCK',
       job_action           => 'begin aimir_validation.process_mv_schedule(''WM'',0);aimir_validation.process_mv_schedule(''WM'',1);aimir_validation.process_mv_schedule(''WM'',2);aimir_validation.process_mv_schedule(''WM'',3);aimir_validation.process_mv_schedule(''WM'',4);aimir_validation.process_mv_schedule(''WM'',5);aimir_validation.process_mv_schedule(''WM'',6);aimir_validation.process_mv_schedule(''WM'',7);aimir_validation.process_mv_schedule(''WM'',8);aimir_validation.process_mv_schedule(''WM'',9); end;',
       repeat_interval      => 'FREQ=MINUTELY;INTERVAL=2;BYSECOND=17', 
       enabled              =>  TRUE,
       comments             => 'Validate meter value');
end;
/

begin
    DBMS_SCHEDULER.CREATE_JOB (
       job_name             => 'process_validation_gm',
       job_type             => 'PLSQL_BLOCK',
       job_action           => 'begin aimir_validation.process_mv_schedule(''GM'',0);aimir_validation.process_mv_schedule(''GM'',1);aimir_validation.process_mv_schedule(''GM'',2);aimir_validation.process_mv_schedule(''GM'',3);aimir_validation.process_mv_schedule(''GM'',4);aimir_validation.process_mv_schedule(''GM'',5);aimir_validation.process_mv_schedule(''GM'',6);aimir_validation.process_mv_schedule(''GM'',7);aimir_validation.process_mv_schedule(''GM'',8);aimir_validation.process_mv_schedule(''GM'',9); end;',
       repeat_interval      => 'FREQ=MINUTELY;INTERVAL=2;BYSECOND=17', 
       enabled              =>  TRUE,
       comments             => 'Validate meter value');
end;
/

begin
    DBMS_SCHEDULER.CREATE_JOB (
       job_name             => 'process_validation_hm',
       job_type             => 'PLSQL_BLOCK',
       job_action           => 'begin aimir_validation.process_mv_schedule(''HM'',0);aimir_validation.process_mv_schedule(''HM'',1);aimir_validation.process_mv_schedule(''HM'',2);aimir_validation.process_mv_schedule(''HM'',3);aimir_validation.process_mv_schedule(''HM'',4);aimir_validation.process_mv_schedule(''HM'',5);aimir_validation.process_mv_schedule(''HM'',6);aimir_validation.process_mv_schedule(''HM'',7);aimir_validation.process_mv_schedule(''HM'',8);aimir_validation.process_mv_schedule(''HM'',9); end;',
       repeat_interval      => 'FREQ=MINUTELY;INTERVAL=2;BYSECOND=17', 
       enabled              =>  TRUE,
       comments             => 'Validate meter value');
end;
/

