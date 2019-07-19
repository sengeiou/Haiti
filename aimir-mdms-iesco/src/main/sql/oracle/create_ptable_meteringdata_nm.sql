DECLARE
  c int;
  STMT_DDL VARCHAR2(4000);
BEGIN
  select count(*) into c from user_tables where table_name = 'METERINGDATA_NM';
   if c = 1 then
      execute immediate 'drop table METERINGDATA_NM';
   end if;

  FOR TABLENAME IN (select 'NM' as name from dual)
  LOOP
    STMT_DDL := 'CREATE TABLE METERINGDATA_'||TABLENAME.name||' '||CHR(10)||
                '( '||CHR(10)||
                '  DEVICE_ID      VARCHAR2(20),  '||CHR(10)||
                '  DEVICE_TYPE    VARCHAR2(255), '||CHR(10)||
                '  HHMMSS         VARCHAR2(6),   '||CHR(10)||
                '  METERINGTYPE   NUMBER(10),    '||CHR(10)||
                '  VALUE          NUMBER(19,4),  '||CHR(10)||
                '  WRITEDATE      VARCHAR2(14),  '||CHR(10)||
                '  YYYYMMDD       VARCHAR2(8),   '||CHR(10)||
                '  MDEV_ID        VARCHAR2(20) NOT NULL, '||CHR(10)||
                '  MDEV_ID_LAST   VARCHAR2(1 CHAR) GENERATED ALWAYS AS (SUBSTR(MDEV_ID,LENGTH(MDEV_ID),1)) VIRTUAL, '||CHR(10)||
                '  YYYYMMDDHHMMSS VARCHAR2(14) NOT NULL, '||CHR(10)||
                '  DST            NUMBER(38) DEFAULT 0,  '||CHR(10)||
                '  MDEV_TYPE      VARCHAR2(20) NOT NULL, '||CHR(10)||
                '  CONTRACT_ID    NUMBER(10), '||CHR(10)||
                '  ENDDEVICE_ID   NUMBER(10), '||CHR(10)||
                '  LOCATION_ID    NUMBER(10), '||CHR(10)||
                '  METER_ID       NUMBER(10), '||CHR(10)||
                '  MODEM_ID       NUMBER(10), '||CHR(10)||
                '  SUPPLIER_ID    NUMBER(10) NOT NULL, '||CHR(10)||
                '  CH1            NUMBER(19,4),  '||CHR(10)||
                '  CH2            NUMBER(19,4),  '||CHR(10)||
                '  CH3            NUMBER(19,4),  '||CHR(10)||
                '  CH4            NUMBER(19,4),  '||CHR(10)||
                '  CH5            NUMBER(19,4),  '||CHR(10)||
                '  CH6            NUMBER(19,4),  '||CHR(10)||
                '  CH7            NUMBER(19,4),  '||CHR(10)||
                '  CONSTRAINT METERINGDATA_'||TABLENAME.name||'_PK PRIMARY KEY(YYYYMMDDHHMMSS,DST,MDEV_ID,MDEV_TYPE), '||CHR(10)||
                '  CONSTRAINT METERINGDATA_'||TABLENAME.name||'_MODEM_ID_FK FOREIGN KEY(MODEM_ID) REFERENCES MODEM(ID), '||CHR(10)||
                '  CONSTRAINT METERINGDATA_'||TABLENAME.name||'_CONTRACT_ID_FK FOREIGN KEY(CONTRACT_ID) REFERENCES CONTRACT(ID), '||CHR(10)||
                '  CONSTRAINT METERINGDATA_'||TABLENAME.name||'_LOCATION_ID_FK FOREIGN KEY(LOCATION_ID) REFERENCES LOCATION(ID), '||CHR(10)||
                '  CONSTRAINT METERINGDATA_'||TABLENAME.name||'_ENDDEVIC_ID_FK FOREIGN KEY(ENDDEVICE_ID) REFERENCES ENDDEVICE(ID), '||CHR(10)||
                '  CONSTRAINT METERINGDATA_'||TABLENAME.name||'_SUPPLIER_ID_FK FOREIGN KEY(SUPPLIER_ID) REFERENCES SUPPLIER(ID), '||CHR(10)||
                '  CONSTRAINT METERINGDATA_'||TABLENAME.name||'_METER_ID_FK FOREIGN KEY(METER_ID) REFERENCES METER(ID) '||CHR(10)||
                ') '||CHR(10)||
                'PARTITION BY RANGE(YYYYMMDDHHMMSS) '||CHR(10)||
                'SUBPARTITION BY LIST (MDEV_ID_LAST) '||CHR(10)||
                '   SUBPARTITION TEMPLATE '||CHR(10)||
                '   ( '||CHR(10)||
                '      SUBPARTITION ID0 VALUES (''0''), '||CHR(10)||
                '      SUBPARTITION ID1 VALUES (''1''), '||CHR(10)||
                '      SUBPARTITION ID2 VALUES (''2''), '||CHR(10)||
                '      SUBPARTITION ID3 VALUES (''3''), '||CHR(10)||
                '      SUBPARTITION ID4 VALUES (''4''), '||CHR(10)||
                '      SUBPARTITION ID5 VALUES (''5''), '||CHR(10)||
                '      SUBPARTITION ID6 VALUES (''6''), '||CHR(10)||
                '      SUBPARTITION ID7 VALUES (''7''), '||CHR(10)||
                '      SUBPARTITION ID8 VALUES (''8''), '||CHR(10)||
                '      SUBPARTITION ID9 VALUES (''9''), '||CHR(10)||
                '      SUBPARTITION IDA VALUES (DEFAULT) '||CHR(10)||
                '   ) '||CHR(10)||
                '( '||CHR(10)||
                '  PARTITION PART'||TO_CHAR(CURRENT_DATE,'YYYYMMDD')||' VALUES LESS THAN ('''||TO_CHAR(CURRENT_DATE+1,'YYYYMMDD')||'000000'') '||CHR(10)||
                ') TABLESPACE AIMIRPART';
    EXECUTE IMMEDIATE STMT_DDL;
  END LOOP;
  
  FOR TABLENAME IN (select 'NM' as name from dual )
  LOOP
    STMT_DDL := 'CREATE INDEX METERINGDATA_'||TABLENAME.name||'_IDX_01 ON METERINGDATA_'||TABLENAME.name||'(MDEV_ID,YYYYMMDDHHMMSS,MDEV_TYPE,LOCATION_ID) LOCAL TABLESPACE AIMIRPART';
    EXECUTE IMMEDIATE STMT_DDL;
  END LOOP;
END;
/


INSERT INTO PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('METERINGDATA_NM','DAILY','ALTER TABLE METERINGDATA_NM ADD PARTITION PART{STARTDATE} VALUES LESS THAN(''{NEXTPERIODDATE}0000'') update indexes','dd',10,0,1);
INSERT INTO PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) values ('METERINGDATA_NM DROP','DAILY','ALTER TABLE METERINGDATA_NM DROP PARTITION PART{STARTDATE} update indexes','dd',45,-105,1);


commit work;
