DECLARE
  STMT_DDL VARCHAR2(2000);
BEGIN
  STMT_DDL := 'CREATE TABLE IP_MV_RECEIVE '||
              '( '||
              '  TS                     VARCHAR2(14), '||
              '  HEADENDEXTERNALID      VARCHAR2(20), '||
              '  DEVICEIDENTIFIERNUMBER VARCHAR2(30), '||
              '  ISSUERID               VARCHAR2(10), '||
              '  MCIDN                  VARCHAR2(20), '||
              '  STDT                   VARCHAR2(14), '||
              '  ENDT                   VARCHAR2(14), '||
              '  MLTS                   VARCHAR2(14), '||
              '  MLMETERDT              VARCHAR2(14), '||
              '  MLCAPTUREDT            VARCHAR2(14), '||
              '  MLCAPTUREDEVID         VARCHAR2(30), '||
              '  MLCAPTUREDEVTYPE       VARCHAR2(20), '||
              '  MLQ                    FLOAT, '||
              '  MLFC                   NUMBER, '||
              '  V$CREATED              DATE DEFAULT CURRENT_DATE '||
              ') '||
              'PARTITION BY RANGE (MLTS) '||
              '( '||
              '  PARTITION PART'||to_char(CURRENT_DATE,'YYYYMMDD')||' VALUES LESS THAN ('''||to_char(CURRENT_DATE,'YYYYMMDD')||'235959'') '||
              ') TABLESPACE AIMIRPART ';
  EXECUTE IMMEDIATE STMT_DDL;
END;
/

Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('IP_MV_RECEIVE','DAILY','ALTER TABLE IP_MV_RECEIVE ADD PARTITION PART{STARTDATE} VALUES LESS THAN(''{LASTSECOND}'') update indexes','dd',10,0,1);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('IP_MV_RECEIVE DROP','DAILY','ALTER TABLE IP_MV_RECEIVE DROP PARTITION PART{STARTDATE} update indexes','dd',15,-100,1);
