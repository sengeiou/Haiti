
-- Full data
DECLARE
  STMT_DDL VARCHAR2(2000);
BEGIN

    STMT_DDL := 'CREATE TABLE IP_NM_DATA '||
                '( '||
                '  YYYYMMDDHHMMSS   VARCHAR2(14) NOT NULL, '||
                '  CHANNEL          NUMBER       NOT NULL, '||
                '  MDEV_ID          VARCHAR2(20) NOT NULL, '||
                '  MDEV_TYPE        VARCHAR2(20) NOT NULL, '||
                '  LOCATION_ID      NUMBER(10), '||
                '  MV_VALUE         NUMBER(19,4), '||
                '  MV_VALID         NUMBER       DEFAULT 0, '||
                '  CAP_DEVICE_TYPE  VARCHAR2(20), '||
                '  CAP_DEVICE_ID    VARCHAR2(255), '||
                '  CAP_DATE_METER   NUMBER(19,4), '||
                '  CAP_DATE_DCU     NUMBER(19,4), '||
                '  CREATE_DATE      DATE         DEFAULT SYSDATE, '||
                '  DELIVER_DATE     DATE, '||
                '  CONSTRAINT IP_NM_DATA_PK PRIMARY KEY (YYYYMMDDHHMMSS, MDEV_TYPE, MDEV_ID, CHANNEL) '||
                ') '||
                'PARTITION BY RANGE (YYYYMMDDHHMMSS) '||
                '( '||
                '  PARTITION PART'||to_char(CURRENT_DATE,'YYYYMMDD')||' VALUES LESS THAN ('''||to_char(CURRENT_DATE,'YYYYMMDD')||'235959'') '||
                ') TABLESPACE AIMIRPART ';
    EXECUTE IMMEDIATE STMT_DDL;

END;
/

-- Delivery step 1
DECLARE
  STMT_DDL VARCHAR2(2000);
BEGIN

    STMT_DDL := 'CREATE TABLE IP_NM_DELIVERY '||
                '( '||
                '  YYYYMMDD         VARCHAR2(8)  NOT NULL, '||
                '  YYYYMMDDHHMMSS   VARCHAR2(14) NOT NULL, '||
                '  CHANNEL          NUMBER       NOT NULL, '||
                '  MDEV_ID          VARCHAR2(20) NOT NULL, '||
                '  MDEV_TYPE        VARCHAR2(20) NOT NULL, '||
                '  LOCATION_ID      NUMBER(10), '||
                '  MV_VALUE         NUMBER(19,4), '||
                '  MV_VALID         NUMBER       DEFAULT 0, '||
                '  CAP_DEVICE_TYPE  VARCHAR2(20), '||
                '  CAP_DEVICE_ID    VARCHAR2(255), '||
                '  CAP_DATE_METER   NUMBER(19,4), '||
                '  CAP_DATE_DCU     NUMBER(19,4), '||
                '  CONSTRAINT IP_NM_DELIVERY_PK PRIMARY KEY (MDEV_TYPE, MDEV_ID, YYYYMMDD, CHANNEL, YYYYMMDDHHMMSS) '||
                ') TABLESPACE AIMIRINT ';
    EXECUTE IMMEDIATE STMT_DDL;

END;
/

CREATE SEQUENCE IP_NM_BATCH_SEQUENCE;

CREATE TABLE IP_NM_BATCHES
(
  BATCH_ID     NUMBER,
  BATCH_STATUS NUMBER,
  TARGET_TABLE VARCHAR2(5),
  CREATE_DATE DATE DEFAULT CURRENT_DATE,
  DELIVERED_DATE DATE,
  SLA_DATE DATE,
  NUMBER_OF_ROWS NUMBER,
  CONSTRAINT IP_NM_BATCHES_PK PRIMARY KEY (BATCH_ID)
) TABLESPACE AIMIRINT;

-- Delivery step 2
DECLARE
  STMT_DDL VARCHAR2(2000);
BEGIN
    STMT_DDL := 'CREATE TABLE IP_NM_OUTBOUND '||
                '( '||
                '  ROWIDFORDELETE   ROWID, '||
                '  BATCH_ID         NUMBER, '||
                '  YYYYMMDD         VARCHAR2(8)  NOT NULL, '||
                '  YYYYMMDDHHMMSS   VARCHAR2(14) NOT NULL, '||
                '  CHANNEL          NUMBER       NOT NULL, '||
                '  OBIS_ID          VARCHAR2(30) NOT NULL, '||
                '  MDEV_ID          VARCHAR2(20) NOT NULL, '||
                '  MDEV_TYPE        VARCHAR2(20) NOT NULL, '||
                '  LOCATION_NAME    VARCHAR2(255), '||
                '  LOCATION_ID      NUMBER(10), '||
                '  MV_VALUE         NUMBER(19,4), '||
                '  MV_VALID         NUMBER       DEFAULT 0, '||
                '  CAP_DEVICE_TYPE  VARCHAR2(20), '||
                '  CAP_DEVICE_ID    VARCHAR2(255), '||
                '  CAP_DATE_METER   NUMBER(19,4), '||
                '  CAP_DATE_DCU     NUMBER(19,4) '||
                ') TABLESPACE AIMIRINT';
    EXECUTE IMMEDIATE STMT_DDL;
END;
/

CREATE INDEX IP_NM_OUTBOUND_IDX_01 ON IP_NM_OUTBOUND(BATCH_ID, MDEV_ID) ;

--- option table
CREATE TABLE IP_NM_OPTION
(
  CODETYPE VARCHAR2(2),
  ATTRIBUTENAME  VARCHAR2(30),
  ATTRIBUTEVALUE VARCHAR2(60),
  CONSTRAINT IP_NM_OPTION_PK PRIMARY KEY (CODETYPE,ATTRIBUTENAME)
) TABLESPACE AIMIRINT;

Insert into IP_NM_OPTION (CODETYPE,ATTRIBUTENAME,ATTRIBUTEVALUE) values ('OP','CNF_DELIVERY_NM','TRUE');
Insert into IP_NM_OPTION (CODETYPE,ATTRIBUTENAME,ATTRIBUTEVALUE) values ('OP','CNF_SAVEDATA_NM','FALSE');
Insert into IP_NM_OPTION (CODETYPE,ATTRIBUTENAME,ATTRIBUTEVALUE) values ('OP','CNF_BATCH_MAXSIZE','10000');
-- channel obis
create table ip_nm_channelobis(
  tablename varchar2(2),
  aimirchannel varchar2(30),
  obisid varchar2(30),
  constraint ip_nm_channelobis_PK primary key(tablename,aimirchannel)
) TABLESPACE AIMIRINT;

insert into ip_nm_channelobis(tablename,aimirchannel,obisid) values('NM','1','0.1.94.31.6.255');
insert into ip_nm_channelobis(tablename,aimirchannel,obisid) values('NM','2','1.0.1.9.0.255');
insert into ip_nm_channelobis(tablename,aimirchannel,obisid) values('NM','4','1.0.1.10.0.255');

-- partition control
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('IP_NM_DATA','DAILY','ALTER TABLE IP_NM_DATA ADD PARTITION PART{STARTDATE} VALUES LESS THAN(''{LASTSECOND}'') update indexes','dd',10,0,1);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('IP_NM_DATA DROP','DAILY','ALTER TABLE IP_NM_DATA DROP PARTITION PART{STARTDATE} update indexes','dd',15,-105,1);

commit work;
