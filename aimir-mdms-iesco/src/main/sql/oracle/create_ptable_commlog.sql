DROP TABLE COMMLOG;

DECLARE
  STMT_DDL VARCHAR2(4000);
BEGIN
  STMT_DDL := 'CREATE TABLE COMMLOG '||CHR(10)||
              '( '||CHR(10)||
              '  ID                       NUMBER(19) NOT NULL, '||CHR(10)||
              '  COMM_RESULT              NUMBER(10),    '||CHR(10)||
              '  DESCR                    VARCHAR2(255), '||CHR(10)||
              '  END_TIME                 VARCHAR2(14),  '||CHR(10)||
              '  ERROR_MEASUMENT_DATA_CNT NUMBER(10),    '||CHR(10)||
              '  ERROR_REASON             VARCHAR2(2000),'||CHR(10)||
              '  IDX1                     VARCHAR2(255), '||CHR(10)||
              '  INTERFACE_CODE           NUMBER(10),    '||CHR(10)||
              '  IS_COMPRESSED            NUMBER(10),    '||CHR(10)||
              '  LOCATION_ID              NUMBER(10),    '||CHR(10)||
              '  OPERATION_CODE           VARCHAR2(255), '||CHR(10)||
              '  PROTOCOL_CODE            NUMBER(10),    '||CHR(10)||
              '  RCV_BYTES                NUMBER(10),    '||CHR(10)||
              '  RECEIVER                 VARCHAR2(255), '||CHR(10)||
              '  RECEIVER_ID              VARCHAR2(255), '||CHR(10)||
              '  RECEIVER_IP              VARCHAR2(255), '||CHR(10)||
              '  RECEIVER_PORT            VARCHAR2(255), '||CHR(10)||
              '  RECEIVER_TYPE_CODE       NUMBER(10),    '||CHR(10)||
              '  RESULT                   VARCHAR2(255), '||CHR(10)||
              '  SEND_BYTES               NUMBER(10),    '||CHR(10)||
              '  SENDER                   VARCHAR2(255), '||CHR(10)||
              '  SENDER_ID                VARCHAR2(255), '||CHR(10)||
              '  SENDER_IP                VARCHAR2(255), '||CHR(10)||
              '  SENDER_LOCATION          NUMBER(10),    '||CHR(10)||
              '  SENDER_PORT              VARCHAR2(255), '||CHR(10)||
              '  SENDER_TYPE_CODE         NUMBER(10),    '||CHR(10)||
              '  START_DATE               VARCHAR2(8),   '||CHR(10)||
              '  START_DATE_TIME          VARCHAR2(14),  '||CHR(10)||
              '  START_TIME               VARCHAR2(6),   '||CHR(10)||
              '  STRRECEIVERBYTES         VARCHAR2(255), '||CHR(10)||
              '  STRSENDBYTES             VARCHAR2(255), '||CHR(10)||
              '  STRTOTALCOMMTIME         VARCHAR2(255), '||CHR(10)||
              '  SUCC_MEASUMENT_DATA_CNT  NUMBER(10),    '||CHR(10)||
              '  SUPPLIERED_ID            VARCHAR2(255), '||CHR(10)||
              '  SVC_TYPE_CODE            NUMBER(10),    '||CHR(10)||
              '  TIME                     VARCHAR2(255), '||CHR(10)||
              '  TOTAL_COMM_TIME          NUMBER(10),    '||CHR(10)||
              '  TOTAL_MEASUMENT_DATA_CNT NUMBER(10),    '||CHR(10)||
              '  UNCOM_PRESSED_SEND_BYTES NUMBER(10),    '||CHR(10)||
              '  UNCON_PRESSED_RCV_BYTES  NUMBER(10),    '||CHR(10)||
              '  CONSTRAINT COMMLOG_PROTOCOL_CODE_FK FOREIGN KEY(PROTOCOL_CODE) REFERENCES CODE (ID), '||CHR(10)||
              '  CONSTRAINT COMMLOG_SENDER_LOCATION_FK FOREIGN KEY(SENDER_LOCATION) REFERENCES LOCATION (ID), '||CHR(10)||
              '  CONSTRAINT COMMLOG_SENDER_TYPE_CODE_FK FOREIGN KEY(SENDER_TYPE_CODE) REFERENCES CODE (ID), '||CHR(10)||
              '  CONSTRAINT COMMLOG_SVC_TYPE_CODE_FK FOREIGN KEY(SVC_TYPE_CODE) REFERENCES CODE (ID), '||CHR(10)||
              '  CONSTRAINT COMMLOG_INTERFACE_CODE_FK FOREIGN KEY(INTERFACE_CODE) REFERENCES CODE (ID), '||CHR(10)||
              '  CONSTRAINT COMMLOG_RECEIVER_TYPE_CODE_FK FOREIGN KEY(RECEIVER_TYPE_CODE) REFERENCES CODE (ID), '||CHR(10)||
              '  CONSTRAINT COMMLOG_LOCATION_ID_FK FOREIGN KEY(LOCATION_ID) REFERENCES LOCATION (ID) '||CHR(10)||
              ') '||CHR(10)||
              'PARTITION BY RANGE(START_DATE) '||CHR(10)||
              '( '||CHR(10)||
              '  PARTITION PART'||TO_CHAR(CURRENT_DATE,'YYYYMMDD')||' VALUES LESS THAN ('''||TO_CHAR(CURRENT_DATE+1,'YYYYMMDD')||''') '||CHR(10)||
              ') TABLESPACE AIMIRPART ';
  EXECUTE IMMEDIATE STMT_DDL;
  STMT_DDL := 'CREATE UNIQUE INDEX COMMLOG_PK ON COMMLOG (ID) LOCAL TABLESPACE AIMIRPART';
  EXECUTE IMMEDIATE STMT_DDL;
END;
/

CREATE INDEX COMMLOG_IDX_01 ON COMMLOG(START_DATE, SUPPLIERED_ID, SVC_TYPE_CODE, SENDER_ID, LOCATION_ID) LOCAL TABLESPACE AIMIRPART;
CREATE INDEX COMMLOG_IDX_02 ON COMMLOG(START_DATE, SUPPLIERED_ID, SENDER_ID, LOCATION_ID) LOCAL TABLESPACE AIMIRPART;

INSERT INTO PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('COMMLOG','DAILY','ALTER TABLE COMMLOG ADD PARTITION PART{STARTDATE} VALUES LESS THAN(''{NEXTPERIODDATE}'') update indexes','dd',10,0,1);
INSERT INTO PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('COMMLOG DROP','DAILY','ALTER TABLE COMMLOG DROP PARTITION PART{STARTDATE} update indexes','dd',15,-365,1);


commit work;
