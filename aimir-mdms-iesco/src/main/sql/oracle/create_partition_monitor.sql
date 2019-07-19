CREATE TABLE PARTITION_CONTROL 
(	ID VARCHAR2(40) NOT NULL, 
	PARTITIONINGINTERVAL VARCHAR2(20) DEFAULT 'MONTHLY', 
	SQLCOMMAND VARCHAR2(2000), 
	STARTDATETRUNCATE VARCHAR2(5) DEFAULT 'dd', 
	ITERATIONS NUMBER(*,0) DEFAULT 10, 
	STARTDATEOFFSET NUMBER DEFAULT 0, 
	ACTIVE NUMBER(1,0) DEFAULT 1, 
	CONSTRAINT PARTITION_CONTROL_PK PRIMARY KEY (ID)
);

CREATE TABLE PARTITION_DATE_FORMAT 
(	ID NUMBER(*,0) NOT NULL, 
	KEYVALUE VARCHAR2(30) NOT NULL, 
	FORMATSTRING VARCHAR2(30) NOT NULL, 
	CONSTRAINT PARTITION_DATE_FORMAT_PK PRIMARY KEY (ID, KEYVALUE)
);
 
CREATE TABLE PARTITION_LOG 
(	TABLE_NAME VARCHAR2(40), 
	PARTITION_NAME VARCHAR2(255), 
	STARTTIME DATE, 
	ENDTIME DATE, 
	ERRORCODE NUMBER, 
	DESCRIPTION VARCHAR2(255)
);

Insert into PARTITION_DATE_FORMAT (ID,KEYVALUE,FORMATSTRING) values (0,'YEAR','YYYY');
Insert into PARTITION_DATE_FORMAT (ID,KEYVALUE,FORMATSTRING) values (0,'MONTH','YYYYMM');
Insert into PARTITION_DATE_FORMAT (ID,KEYVALUE,FORMATSTRING) values (0,'DATE','YYYYMMDD');
Insert into PARTITION_DATE_FORMAT (ID,KEYVALUE,FORMATSTRING) values (0,'HOUR','YYYYMMDDHH24');
Insert into PARTITION_DATE_FORMAT (ID,KEYVALUE,FORMATSTRING) values (0,'MINUTE','YYYYMMDDHH24MI');
Insert into PARTITION_DATE_FORMAT (ID,KEYVALUE,FORMATSTRING) values (0,'SECOND','YYYYMMDDHH24MISS');
Insert into PARTITION_DATE_FORMAT (ID,KEYVALUE,FORMATSTRING) values (0,'SHORTMONTH','YYMM');
Insert into PARTITION_DATE_FORMAT (ID,KEYVALUE,FORMATSTRING) values (0,'SHORTDATE','YYMMDD');

/** SAMPLE
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('OUTBOUND_0_GM','DAILY','ALTER TABLE OUTBOUND_0_GM ADD PARTITION PART{STARTDATE} VALUES LESS THAN(to_date(''{LASTSECOND}'',''yyyymmddhh24miss'')) tablespace AMRPART update indexes','dd',10,0,0);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('OUTBOUND_1_GM','DAILY','ALTER TABLE OUTBOUND_1_GM ADD PARTITION PART{STARTDATE} VALUES LESS THAN(to_date(''{LASTSECOND}'',''yyyymmddhh24miss'')) tablespace AMRPART update indexes','dd',10,0,0);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('OUTBOUND_2_GM','DAILY','ALTER TABLE OUTBOUND_2_GM ADD PARTITION PART{STARTDATE} VALUES LESS THAN(to_date(''{LASTSECOND}'',''yyyymmddhh24miss'')) tablespace AMRPART update indexes','dd',10,0,0);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('OUTBOUND_3_GM','DAILY','ALTER TABLE OUTBOUND_3_GM ADD PARTITION PART{STARTDATE} VALUES LESS THAN(to_date(''{LASTSECOND}'',''yyyymmddhh24miss'')) tablespace AMRPART update indexes','dd',10,0,0);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('OUTBOUND_0_GM DROP','DAILY','ALTER TABLE OUTBOUND_0_GM DROP PARTITION PART{STARTDATE} update indexes','dd',15,-30,0);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('OUTBOUND_1_GM DROP','DAILY','ALTER TABLE OUTBOUND_1_GM DROP PARTITION PART{STARTDATE} update indexes','dd',15,-30,0);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('OUTBOUND_2_GM DROP','DAILY','ALTER TABLE OUTBOUND_2_GM DROP PARTITION PART{STARTDATE} update indexes','dd',15,-30,0);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('OUTBOUND_3_GM DROP','DAILY','ALTER TABLE OUTBOUND_3_GM DROP PARTITION PART{STARTDATE} update indexes','dd',15,-30,0);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('METEREVENTLOG','MONTHLY','ALTER TABLE METEREVENTLOG SPLIT PARTITION PARTHIGH at (''{LASTDATE}'') into (partition PART{STARTDATE}, partition parthigh) update indexes','mm',3,0,1);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('DAY_STAT','DAILY','ALTER TABLE DAY_STAT ADD PARTITION PART{STARTDATE} VALUES LESS THAN(''{NEXTPERIODDATE}'') tablespace AMRDAT{STARTSHORTMONTH} update indexes','dd',90,0,1);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('MONTH_STAT','MONTHLY','ALTER TABLE MONTH_STAT ADD PARTITION PART{STARTDATE} VALUES LESS THAN(''{NEXTPERIODMONTH}'') tablespace AMRDAT{STARTSHORTMONTH} update indexes','mm',3,0,1);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('MONTHLYSUMMARY','MONTHLY','ALTER TABLE MONTHLYSUMMARY ADD PARTITION PART{STARTDATE} VALUES LESS THAN(''{NEXTPERIODDATE}'') tablespace AMRDAT{STARTSHORTMONTH} update indexes','mm',3,0,1);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('COMM_LOG_STAT','MONTHLY','ALTER TABLE COMM_LOG_STAT ADD PARTITION PART{STARTDATE} VALUES LESS THAN(''{NEXTPERIODDATE}'') tablespace AMRDAT{STARTSHORTMONTH} update indexes','mm',3,0,1);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('F60_BOX ADD','DAILY','ALTER TABLE F60_BOX ADD PARTITION F60_BOX_PART_{STARTDATE} VALUES LESS THAN(''{LASTSECOND}'') tablespace AMRF60TS update indexes','dd',6,0,0);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('CURRENT_EM','DAILY','ALTER TABLE CURRENT_EM ADD PARTITION PART{STARTDATE} VALUES LESS THAN(''{LASTSECOND}'') tablespace AMRDAT{STARTSHORTMONTH}  update indexes','dd',90,0,1);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('F60_BOX REMOVE','DAILY','ALTER TABLE F60_BOX drop PARTITION F60_BOX_PART_{STARTDATE} update indexes','dd',9,-50,0);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('BILL_SELFREAD','MONTHLY','ALTER TABLE BILL_SELFREAD ADD PARTITION PART{STARTDATE} VALUES LESS THAN(''{NEXTPERIODDATE}'') tablespace AMRDAT update indexes','mm',3,0,1);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('CHANGE_HIS_LOG','MONTHLY','ALTER TABLE CHANGE_HIS_LOG ADD PARTITION PART{STARTDATE} VALUES LESS THAN(''{NEXTPERIODMONTH}'') tablespace AMRDAT{STARTSHORTMONTH} update indexes','mm',3,0,1);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('COMM_LOG','MONTHLY','ALTER TABLE COMM_LOG ADD PARTITION PART{STARTDATE} VALUES LESS THAN(''{LASTSECOND}'') tablespace AMRDAT{STARTSHORTMONTH} update indexes','mm',3,0,1);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('DAY_HM','MONTHLY','ALTER TABLE DAY_HM ADD PARTITION PART{STARTDATE} VALUES LESS THAN(''{NEXTPERIODDATE}'') tablespace AMRDAT{STARTSHORTMONTH} update indexes','mm',3,0,0);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('INSTRUMENT_EM','WEEKLYWITHINMONTH','ALTER TABLE INSTRUMENT_EM ADD PARTITION PART{STARTDATE} VALUES LESS THAN(''{NEXTPERIODDATE}'') tablespace AMRDAT{STARTSHORTMONTH} update indexes','mm',16,0,1);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('LP_EM','WEEKLYWITHINMONTH','ALTER TABLE LP_EM ADD PARTITION PART{STARTDATE} VALUES LESS THAN(''{NEXTPERIODDATE}'') tablespace AMRDAT{STARTSHORTMONTH} update indexes','mm',16,0,1);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('MEASUREMENT_HISTORY','MONTHLY','ALTER TABLE MEASUREMENT_HISTORY ADD PARTITION PART{STARTDATE} VALUES LESS THAN(to_date(''{LASTSECOND}'',''yyyymmddhh24miss'')) tablespace AMRDAT update indexes','mm',3,0,0);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('METERING_STAT','MONTHLY','ALTER TABLE METERING_STAT ADD PARTITION PART{STARTDATE} VALUES LESS THAN(''{NEXTPERIODMONTH}'') tablespace AMRDAT{STARTSHORTMONTH} update indexes','mm',3,0,1);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('MONTH_HM','MONTHLY','ALTER TABLE MONTH_HM ADD PARTITION PART{STARTDATE} VALUES LESS THAN(''{NEXTPERIODDATE}'') tablespace AMRDAT{STARTSHORTMONTH} update indexes','mm',3,0,0);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('OPERATION_LOG','MONTHLY','ALTER TABLE OPERATION_LOG ADD PARTITION PART{STARTDATE} VALUES LESS THAN(''{NEXTPERIODDATE}'') tablespace AMRDAT{STARTSHORTMONTH} update indexes','mm',3,0,1);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('OUTBOUND_0_EM','DAILY','ALTER TABLE OUTBOUND_0_EM ADD PARTITION PART{STARTDATE} VALUES LESS THAN(to_date(''{LASTSECOND}'',''yyyymmddhh24miss'')) tablespace AMRPART update indexes','dd',10,0,0);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('OUTBOUND_1_EM','DAILY','ALTER TABLE OUTBOUND_1_EM ADD PARTITION PART{STARTDATE} VALUES LESS THAN(to_date(''{LASTSECOND}'',''yyyymmddhh24miss'')) tablespace AMRPART update indexes','dd',10,0,0);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('OUTBOUND_2_EM','DAILY','ALTER TABLE OUTBOUND_2_EM ADD PARTITION PART{STARTDATE} VALUES LESS THAN(to_date(''{LASTSECOND}'',''yyyymmddhh24miss'')) tablespace AMRPART update indexes','dd',10,0,0);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('OUTBOUND_3_EM','DAILY','ALTER TABLE OUTBOUND_3_EM ADD PARTITION PART{STARTDATE} VALUES LESS THAN(to_date(''{LASTSECOND}'',''yyyymmddhh24miss'')) tablespace AMRPART update indexes','dd',10,0,0);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('OUTBOUND_0_WM','DAILY','ALTER TABLE OUTBOUND_0_WM ADD PARTITION PART{STARTDATE} VALUES LESS THAN(to_date(''{LASTSECOND}'',''yyyymmddhh24miss'')) tablespace AMRPART update indexes','dd',10,0,0);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('OUTBOUND_1_WM','DAILY','ALTER TABLE OUTBOUND_1_WM ADD PARTITION PART{STARTDATE} VALUES LESS THAN(to_date(''{LASTSECOND}'',''yyyymmddhh24miss'')) tablespace AMRPART update indexes','dd',10,0,0);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('OUTBOUND_2_WM','DAILY','ALTER TABLE OUTBOUND_2_WM ADD PARTITION PART{STARTDATE} VALUES LESS THAN(to_date(''{LASTSECOND}'',''yyyymmddhh24miss'')) tablespace AMRPART update indexes','dd',10,0,0);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('OUTBOUND_3_WM','DAILY','ALTER TABLE OUTBOUND_3_WM ADD PARTITION PART{STARTDATE} VALUES LESS THAN(to_date(''{LASTSECOND}'',''yyyymmddhh24miss'')) tablespace AMRPART update indexes','dd',10,0,0);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('OUTBOUND_0_HM','DAILY','ALTER TABLE OUTBOUND_0_HM ADD PARTITION PART{STARTDATE} VALUES LESS THAN(to_date(''{LASTSECOND}'',''yyyymmddhh24miss'')) tablespace AMRPART update indexes','dd',10,0,0);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('OUTBOUND_1_HM','DAILY','ALTER TABLE OUTBOUND_1_HM ADD PARTITION PART{STARTDATE} VALUES LESS THAN(to_date(''{LASTSECOND}'',''yyyymmddhh24miss'')) tablespace AMRPART update indexes','dd',10,0,0);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('OUTBOUND_2_HM','DAILY','ALTER TABLE OUTBOUND_2_HM ADD PARTITION PART{STARTDATE} VALUES LESS THAN(to_date(''{LASTSECOND}'',''yyyymmddhh24miss'')) tablespace AMRPART update indexes','dd',10,0,0);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('OUTBOUND_3_HM','DAILY','ALTER TABLE OUTBOUND_3_HM ADD PARTITION PART{STARTDATE} VALUES LESS THAN(to_date(''{LASTSECOND}'',''yyyymmddhh24miss'')) tablespace AMRPART update indexes','dd',10,0,0);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('OUTBOUND_0_EM DROP','DAILY','ALTER TABLE OUTBOUND_0_EM DROP PARTITION PART{STARTDATE} update indexes','dd',15,-30,0);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('OUTBOUND_1_EM DROP','DAILY','ALTER TABLE OUTBOUND_1_EM DROP PARTITION PART{STARTDATE} update indexes','dd',15,-30,0);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('OUTBOUND_2_EM DROP','DAILY','ALTER TABLE OUTBOUND_2_EM DROP PARTITION PART{STARTDATE} update indexes','dd',15,-30,0);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('OUTBOUND_3_EM DROP','DAILY','ALTER TABLE OUTBOUND_3_EM DROP PARTITION PART{STARTDATE} update indexes','dd',15,-30,0);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('OUTBOUND_0_WM DROP','DAILY','ALTER TABLE OUTBOUND_0_WM DROP PARTITION PART{STARTDATE} update indexes','dd',15,-30,0);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('OUTBOUND_1_WM DROP','DAILY','ALTER TABLE OUTBOUND_1_WM DROP PARTITION PART{STARTDATE} update indexes','dd',15,-30,0);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('OUTBOUND_2_WM DROP','DAILY','ALTER TABLE OUTBOUND_2_WM DROP PARTITION PART{STARTDATE} update indexes','dd',15,-30,0);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('OUTBOUND_3_WM DROP','DAILY','ALTER TABLE OUTBOUND_3_WM DROP PARTITION PART{STARTDATE} update indexes','dd',15,-30,0);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('OUTBOUND_0_HM DROP','DAILY','ALTER TABLE OUTBOUND_0_HM DROP PARTITION PART{STARTDATE} update indexes','dd',15,-30,0);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('OUTBOUND_1_HM DROP','DAILY','ALTER TABLE OUTBOUND_1_HM DROP PARTITION PART{STARTDATE} update indexes','dd',15,-30,0);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('OUTBOUND_2_HM DROP','DAILY','ALTER TABLE OUTBOUND_2_HM DROP PARTITION PART{STARTDATE} update indexes','dd',15,-30,0);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('OUTBOUND_3_HM DROP','DAILY','ALTER TABLE OUTBOUND_3_HM DROP PARTITION PART{STARTDATE} update indexes','dd',15,-30,0);
*/


commit work;

create or replace 
PACKAGE PARTITION_PACKAGE AS 
  partition_redefined EXCEPTION;
  partition_split EXCEPTION;
  partition_unknown EXCEPTION;
  partition_duplicate EXCEPTION;
  tablespace_unknown EXCEPTION;
  PRAGMA EXCEPTION_INIT(partition_redefined, -14074);
  PRAGMA EXCEPTION_INIT(partition_split, -14080);
  PRAGMA EXCEPTION_INIT(partition_unknown, -2149);
  PRAGMA EXCEPTION_INIT(partition_duplicate, -14013);
  PRAGMA EXCEPTION_INIT(tablespace_unknown, -959);
  

  seconds_per_day CONSTANT NUMBER := 24*60*60;
  second_fraction CONSTANT NUMBER := 1/seconds_per_day;

  type string_table_type is table of varchar2(32) index by varchar2(32);
  
  format_table string_table_type;


  procedure load_format_table(id_ in number default 0);
  procedure clear_format_table;

  function exchange_string(instr in varchar2,date_table in string_table_type) return varchar2;


  procedure run_id(id_ partition_control.id%type,verbose_ in number default 0,debug_ in number default 0);
  procedure run_one(v_partition_control partition_control%rowtype,verbose_ in number default 0,debug_ in number default 0);
  procedure run_all(verbose_ in number default 0,debug_ in number default 0);

END PARTITION_PACKAGE;
/

create or replace 
PACKAGE BODY PARTITION_PACKAGE AS

/*
INSERT INTO "AIMIR"."PARTITION_DATE_FORMAT" (ID, KEYVALUE, FORMATSTRING) VALUES ('0', 'YEAR', 'YYYY');
INSERT INTO "AIMIR"."PARTITION_DATE_FORMAT" (ID, KEYVALUE, FORMATSTRING) VALUES ('0', 'MONTH', 'YYYYMM');
INSERT INTO "AIMIR"."PARTITION_DATE_FORMAT" (ID, KEYVALUE, FORMATSTRING) VALUES ('0', 'DATE', 'YYYYMMDD');
INSERT INTO "AIMIR"."PARTITION_DATE_FORMAT" (ID, KEYVALUE, FORMATSTRING) VALUES ('0', 'HOUR', 'YYYYMMDDHH24');
INSERT INTO "AIMIR"."PARTITION_DATE_FORMAT" (ID, KEYVALUE, FORMATSTRING) VALUES ('0', 'MINUTE', 'YYYYMMDDHH24MI');
INSERT INTO "AIMIR"."PARTITION_DATE_FORMAT" (ID, KEYVALUE, FORMATSTRING) VALUES ('0', 'SECOND', 'YYYYMMDDHH24MISS');
*/


  function exchange_string(instr in varchar2,date_table in string_table_type) return varchar2 is
    i varchar2(30);
    retval varchar2(2000);
  begin
    i:=date_table.first;
    retval:=instr;
    loop
      exit when i is null;
      retval:=replace(retval,i,date_table(i));
      i:=date_table.next(i);
    end loop;
    return retval;
  end exchange_string;

  function format_date(datum in date,formattype in varchar2) return varchar2 is
    retval varchar2(30);
  begin
    retval:=to_char(datum,format_table(formattype));
    return retval;
  end format_date;

  function generate_date_table(startperiod in date,nextperiodstart in date) return string_table_type is
    retval string_table_type;
    i varchar2(30);
  begin
    if format_table.count=0 then
      load_format_table;
    end if;

    retval('{NOW}'):=format_date(sysdate,'SECOND');
    retval('{TODAY}'):=format_date(sysdate,'DATE');

    i:=format_table.first;
    loop
      exit when i is null;
      retval('{START'||i||'}'):=format_date(startperiod,i);
      i:=format_table.next(i);
    end loop;
    
--    retval('{STARTSECOND}'):=format_date(startperiod,'SECOND');
--    retval('{STARTMINUTE}'):=format_date(startperiod,'MINUTE');
--    retval('{STARTHOUR}'):=format_date(startperiod,'HOUR');
--    retval('{STARTDATE}'):=format_date(startperiod,'DATE');
--    retval('{STARTMONTH}'):=format_date(startperiod,'MONTH');
--    retval('{STARTYEAR}'):=format_date(startperiod,'YEAR');

    i:=format_table.first;
    loop
      exit when i is null;
      retval('{NEXTPERIOD'||i||'}'):=format_date(nextperiodstart,i);
      i:=format_table.next(i);
    end loop;
    
--    retval('{NEXTPERIODSECOND}'):=format_date(nextperiodstart,'SECOND');
--    retval('{NEXTPERIODMINUTE}'):=format_date(nextperiodstart,'MINUTE');
--    retval('{NEXTPERIODHOUR}'):=format_date(nextperiodstart,'HOUR');
--    retval('{NEXTPERIODDATE}'):=format_date(nextperiodstart,'DATE');
--    retval('{NEXTPERIODMONTH}'):=format_date(nextperiodstart,'MONTH');
--    retval('{NEXTPERIODYEAR}'):=format_date(nextperiodstart,'YEAR');

    i:=format_table.first;
    loop
      exit when i is null;
      retval('{LAST'||i||'}'):=format_date(nextperiodstart-second_fraction,i);
      i:=format_table.next(i);
    end loop;
    
--    retval('{LASTSECOND}'):=format_date(nextperiodstart-second_fraction,'SECOND');
--    retval('{LASTMINUTE}'):=format_date(nextperiodstart-second_fraction,'MINUTE');
--    retval('{LASTHOUR}'):=format_date(nextperiodstart-second_fraction,'HOUR');
--    retval('{LASTDATE}'):=format_date(nextperiodstart-second_fraction,'DATE');
--    retval('{LASTMONTH}'):=format_date(nextperiodstart-second_fraction,'MONTH');
--    retval('{LASTYEAR}'):=format_date(nextperiodstart-second_fraction,'YEAR');
    return retval;
  end generate_date_table;

  function next_period(starttime in date, periodtype in varchar2) return date is
    retval date;
  begin
    case (periodtype)
      when 'DAILY' then retval:=starttime+1;
      when 'WEEKLY' then retval:=starttime+7;
      when 'WEEKLYWITHINMONTH' then retval:=least(starttime+7,add_months(trunc(starttime,'mm'),1)); --TODO:Check
      when 'MONTHLY' then retval:=add_months(starttime,1);
      when 'QUARTERLY' then retval:=add_months(starttime,3);
      when 'HALFYEAR' then retval:=add_months(starttime,6);
      when 'YEARLY' then retval:=add_months(starttime,12);
    end case;
    return retval;
  end next_period;

  function generate_date_table(starttime in date,periodtype in varchar2) return string_table_type is
  begin
    return generate_date_table(starttime,next_period(starttime,periodtype));
  end generate_date_table;


  procedure load_format_table(id_ in number default 0) is
  begin
    --format_table
    FOR r IN (select keyvalue,formatstring from partition_date_format where id = id_) LOOP
--      dbms_output.put_line(r.keyvalue||'=>'||r.formatstring);
      format_table(r.keyvalue) := r.formatstring;
    END LOOP;
--    dbms_output.put_line('Load format table, size:'||format_table.count);
  end;

  procedure clear_format_table is
    t string_table_type;
  begin
    format_table:=t;
  end;

  procedure run_id(id_ partition_control.id%type,verbose_ in number default 0,debug_ in number default 0) is
    v_partition_control partition_control%rowtype;
  begin
    clear_format_table;
    if format_table.count=0 then
      load_format_table;
    end if;
    
    select *
    into v_partition_control
    from partition_control
    where id = id_;
    run_one(v_partition_control,verbose_,debug_);
  end;

  procedure run_one(v_partition_control partition_control%rowtype,verbose_ in number default 0,debug_ in number default 0) is
    date_table string_table_type;
    t varchar2(2000);
    test_str varchar2(2000);
    d date;
    starttime_ date;
    errcode_ number;
    errdesc_ varchar2(255);
  begin

    d:=trunc(sysdate+v_partition_control.startdateoffset,v_partition_control.startdatetruncate);
    for iter in 0..v_partition_control.iterations loop  
      date_table:=generate_date_table(d,v_partition_control.partitioninginterval);  
      t:=exchange_string(v_partition_control.sqlcommand,date_table);
      starttime_:=sysdate;

      begin
        if verbose_>0 then
          dbms_output.put_line(t||';');
        end if;
        if debug_=0 then
          execute immediate t;
          insert into partition_log(starttime, endtime, table_name, partition_name, errorcode, description)
          values(starttime_,sysdate,v_partition_control.id,t,0,'Successfull');
          commit work;
        end if;
      exception
        when partition_redefined or partition_split or tablespace_unknown or partition_duplicate or partition_unknown then
          null;
      end;

      d:=next_period(d,v_partition_control.partitioninginterval);
    end loop;
  end run_one;

  procedure run_all(verbose_ in number default 0,debug_ in number default 0) is
    date_table string_table_type;
    t varchar2(2000);
    v_partition_control partition_control%rowtype;
    test_str varchar2(2000);
    d date;
    cursor c1 is
      select *
      from partition_control
      where active=1;
    starttime_ date;
    errcode_ number;
    errdesc_ varchar2(255);
  begin

    clear_format_table;
    if format_table.count=0 then
      load_format_table;
    end if;
    
    for v_partition_control in c1 loop
      run_one(v_partition_control,verbose_,debug_);
    end loop;
  end run_all;
END PARTITION_PACKAGE;
/

BEGIN
  DBMS_SCHEDULER.CREATE_JOB (
   job_name           =>  'HANDLE_PARTITIONS',
   job_type           =>  'STORED_PROCEDURE',
   job_action         =>  'partition_package.run_all',
   job_class          =>  'DEFAULT_JOB_CLASS',
   start_date         =>  SYSTIMESTAMP,
   repeat_interval    =>  'FREQ=HOURLY;INTERVAL=3',
   enabled            =>  TRUE,
   comments           =>  'partition_package.run_all');
END;
/

