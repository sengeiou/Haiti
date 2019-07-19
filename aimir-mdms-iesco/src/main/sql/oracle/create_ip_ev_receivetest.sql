DECLARE
  STMT_DDL VARCHAR2(2000);
BEGIN
  STMT_DDL := 'CREATE TABLE IP_EV_RECEIVE '||
              '( '||
              '  TS                     VARCHAR2(14), '||
              '  MRID                   VARCHAR2(20), '||
              '  CREATEDDATETIME        VARCHAR2(14), '||
              '  SEVERITY               VARCHAR2(3), '||
              '  ASSETMRID              VARCHAR2(20), '||
              '  ISSUREID               VARCHAR2(10), '||
              '  EVENTDETAIL            VARCHAR2(300), '||
              '  V$CREATED              DATE DEFAULT CURRENT_DATE '||
              ') '||
              'PARTITION BY RANGE (TS) '||
              '( '||
              '  PARTITION PART'||to_char(CURRENT_DATE,'YYYYMMDD')||' VALUES LESS THAN ('''||to_char(CURRENT_DATE,'YYYYMMDD')||'235959'') '||
              ') TABLESPACE AIMIRPART ';
  EXECUTE IMMEDIATE STMT_DDL;
END;
/

Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('IP_EV_RECEIVE','DAILY','ALTER TABLE IP_EV_RECEIVE ADD PARTITION PART{STARTDATE} VALUES LESS THAN(''{LASTSECOND}'') update indexes','dd',10,0,1);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('IP_EV_RECEIVE DROP','DAILY','ALTER TABLE IP_EV_RECEIVE DROP PARTITION PART{STARTDATE} update indexes','dd',15,-100,1);
