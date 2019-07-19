DROP TABLE POWER_QUALITY;

DECLARE
  STMT_DDL VARCHAR2(5000);
BEGIN
  STMT_DDL := 'CREATE TABLE POWER_QUALITY '||CHR(10)||
              '( '||CHR(10)||
              '  MDEV_ID                 VARCHAR2(20)   NOT NULL, '||CHR(10)||
              '  DST                     NUMBER(*,0)    DEFAULT 0, '||CHR(10)||
              '  YYYYMMDDHHMM            VARCHAR2(12)   NOT NULL, '||CHR(10)||
              '  MDEV_TYPE               NUMBER(20,0)   NOT NULL, '||CHR(10)||
              '  SUPPLIER_ID             NUMBER(10,0), '||CHR(10)||
              '  CONTRACT_ID             NUMBER(10,0), '||CHR(10)||
              '  METER_ID                NUMBER(10,0), '||CHR(10)||
              '  MODEM_ID                NUMBER(10,0), '||CHR(10)||
              '  DEVICE_ID               VARCHAR2(20), '||CHR(10)||
              '  DEVICE_TYPE             VARCHAR2(255), '||CHR(10)||
              '  ENDDEVICE_ID            NUMBER(10,0), '||CHR(10)||
              '  HHMM                    VARCHAR2(4)    NOT NULL, '||CHR(10)||
              '  WRITEDATE               VARCHAR2(14)   NOT NULL, '||CHR(10)||
              '  YYYYMMDD                VARCHAR2(8)    NOT NULL, '||CHR(10)||
              '  CURR_1ST_HARMONIC_MAG_A NUMBER(19,4), '||CHR(10)||
              '  CURR_1ST_HARMONIC_MAG_B NUMBER(19,4), '||CHR(10)||
              '  CURR_1ST_HARMONIC_MAG_C NUMBER(19,4), '||CHR(10)||
              '  CURR_2ND_HARMONIC_MAG_A NUMBER(19,4), '||CHR(10)||
              '  CURR_2ND_HARMONIC_MAG_B NUMBER(19,4), '||CHR(10)||
              '  CURR_2ND_HARMONIC_MAG_C NUMBER(19,4), '||CHR(10)||
              '  CURR_A                  NUMBER(19,4), '||CHR(10)||
              '  CURR_B                  NUMBER(19,4), '||CHR(10)||
              '  CURR_C                  NUMBER(19,4), '||CHR(10)||
              '  CURR_ANGLE_A            NUMBER(19,4), '||CHR(10)||
              '  CURR_ANGLE_B            NUMBER(19,4), '||CHR(10)||
              '  CURR_ANGLE_C            NUMBER(19,4), '||CHR(10)||
              '  CURR_HARMONIC_A         NUMBER(19,4), '||CHR(10)||
              '  CURR_HARMONIC_B         NUMBER(19,4), '||CHR(10)||
              '  CURR_HARMONIC_C         NUMBER(19,4), '||CHR(10)||
              '  CURR_SEQ_N              NUMBER(19,4), '||CHR(10)||
              '  CURR_SEQ_P              NUMBER(19,4), '||CHR(10)||
              '  CURR_SEQ_Z              NUMBER(19,4), '||CHR(10)||
              '  CURR_THD_A              NUMBER(19,4), '||CHR(10)||
              '  CURR_THD_B              NUMBER(19,4), '||CHR(10)||
              '  CURR_THD_C              NUMBER(19,4), '||CHR(10)||
              '  DISTORTION_KVA_A        NUMBER(19,4), '||CHR(10)||
              '  DISTORTION_KVA_B        NUMBER(19,4), '||CHR(10)||
              '  DISTORTION_KVA_C        NUMBER(19,4), '||CHR(10)||
              '  DISTORTION_PF_A         NUMBER(19,4), '||CHR(10)||
              '  DISTORTION_PF_B         NUMBER(19,4), '||CHR(10)||
              '  DISTORTION_PF_C         NUMBER(19,4), '||CHR(10)||
              '  DISTORTION_PF_TOTAL     NUMBER(19,4), '||CHR(10)||
              '  KVA_A                   NUMBER(19,4), '||CHR(10)||
              '  KVA_B                   NUMBER(19,4), '||CHR(10)||
              '  KVA_C                   NUMBER(19,4), '||CHR(10)||
              '  KVAR_A                  NUMBER(19,4), '||CHR(10)||
              '  KVAR_B                  NUMBER(19,4), '||CHR(10)||
              '  KVAR_C                  NUMBER(19,4), '||CHR(10)||
              '  KW_A                    NUMBER(19,4), '||CHR(10)||
              '  KW_B                    NUMBER(19,4), '||CHR(10)||
              '  KW_C                    NUMBER(19,4), '||CHR(10)||
              '  LINE_AB                 NUMBER(19,4), '||CHR(10)||
              '  LINE_BC                 NUMBER(19,4), '||CHR(10)||
              '  LINE_CA                 NUMBER(19,4), '||CHR(10)||
              '  LINE_FREQUENCY          NUMBER(19,4), '||CHR(10)||
              '  PF_A                    NUMBER(19,4), '||CHR(10)||
              '  PF_B                    NUMBER(19,4), '||CHR(10)||
              '  PF_C                    NUMBER(19,4), '||CHR(10)||
              '  PF_TOTAL                NUMBER(19,4), '||CHR(10)||
              '  PH_CURR_PQM_A           NUMBER(19,4), '||CHR(10)||
              '  PH_CURR_PQM_B           NUMBER(19,4), '||CHR(10)||
              '  PH_CURR_PQM_C           NUMBER(19,4), '||CHR(10)||
              '  PH_FUND_CURR_A          NUMBER(19,4), '||CHR(10)||
              '  PH_FUND_CURR_B          NUMBER(19,4), '||CHR(10)||
              '  PH_FUND_CURR_C          NUMBER(19,4), '||CHR(10)||
              '  PH_FUND_VOL_A           NUMBER(19,4), '||CHR(10)||
              '  PH_FUND_VOL_B           NUMBER(19,4), '||CHR(10)||
              '  PH_FUND_VOL_C           NUMBER(19,4), '||CHR(10)||
              '  PH_VOL_PQM_A            NUMBER(19,4), '||CHR(10)||
              '  PH_VOL_PQM_B            NUMBER(19,4), '||CHR(10)||
              '  PH_VOL_PQM_C            NUMBER(19,4), '||CHR(10)||
              '  SYSTEM_PF_ANGLE         NUMBER(19,4), '||CHR(10)||
              '  TDD_A                   NUMBER(19,4), '||CHR(10)||
              '  TDD_B                   NUMBER(19,4), '||CHR(10)||
              '  TDD_C                   NUMBER(19,4), '||CHR(10)||
              '  VOL_1ST_HARMONIC_MAG_A  NUMBER(19,4), '||CHR(10)||
              '  VOL_1ST_HARMONIC_MAG_B  NUMBER(19,4), '||CHR(10)||
              '  VOL_1ST_HARMONIC_MAG_C  NUMBER(19,4), '||CHR(10)||
              '  VOL_2ND_HARMONIC_A      NUMBER(19,4), '||CHR(10)||
              '  VOL_2ND_HARMONIC_B      NUMBER(19,4), '||CHR(10)||
              '  VOL_2ND_HARMONIC_C      NUMBER(19,4), '||CHR(10)||
              '  VOL_2ND_HARMONIC_MAG_A  NUMBER(19,4), '||CHR(10)||
              '  VOL_2ND_HARMONIC_MAG_B  NUMBER(19,4), '||CHR(10)||
              '  VOL_2ND_HARMONIC_MAG_C  NUMBER(19,4), '||CHR(10)||
              '  VOL_A                   NUMBER(19,4), '||CHR(10)||
              '  VOL_B                   NUMBER(19,4), '||CHR(10)||
              '  VOL_C                   NUMBER(19,4), '||CHR(10)||
              '  VOL_ANGLE_A             NUMBER(19,4), '||CHR(10)||
              '  VOL_ANGLE_B             NUMBER(19,4), '||CHR(10)||
              '  VOL_ANGLE_C             NUMBER(19,4), '||CHR(10)||
              '  VOL_SEQ_N               NUMBER(19,4), '||CHR(10)||
              '  VOL_SEQ_P               NUMBER(19,4), '||CHR(10)||
              '  VOL_SEQ_Z               NUMBER(19,4), '||CHR(10)||
              '  VOL_THD_A               NUMBER(19,4), '||CHR(10)||
              '  VOL_THD_B               NUMBER(19,4), '||CHR(10)||
              '  VOL_THD_C               NUMBER(19,4), '||CHR(10)||
              '  CONSTRAINT POWER_QUALITY_ENDDEVICE_ID_FK FOREIGN KEY (ENDDEVICE_ID) REFERENCES ENDDEVICE (ID), '||CHR(10)||
              '  CONSTRAINT POWER_QUALITY_METER_ID_FK FOREIGN KEY (METER_ID) REFERENCES METER (ID), '||CHR(10)||
              '  CONSTRAINT POWER_QUALITY_MODEM_ID_FK FOREIGN KEY (MODEM_ID) REFERENCES MODEM (ID), '||CHR(10)||
              '  CONSTRAINT POWER_QUALITY_CONTRACT_ID_FK FOREIGN KEY (CONTRACT_ID) REFERENCES CONTRACT (ID), '||CHR(10)||
              '  CONSTRAINT POWER_QUALITY_SUPPLIER_ID_FK FOREIGN KEY (SUPPLIER_ID) REFERENCES SUPPLIER (ID)'||CHR(10)||
              ') '||CHR(10)||
              'PARTITION BY RANGE (YYYYMMDDHHMM) '||CHR(10)||
              '( '||CHR(10)||
              '  PARTITION PART'||TO_CHAR(CURRENT_DATE,'YYYYMMDD')||' VALUES LESS THAN ('''||TO_CHAR(CURRENT_DATE+1,'YYYYMMDD')||'0000'') '||CHR(10)||
              ') TABLESPACE AIMIRPART';
  EXECUTE IMMEDIATE STMT_DDL;

  STMT_DDL := 'CREATE UNIQUE INDEX POWER_QUALITY_PK ON POWER_QUALITY_PK(YYYYMMDDHHMM, MDEV_ID, MDEV_TYPE, DST) LOCAL TABLESPACE AIMIRPART';
  EXECUTE IMMEDIATE STMT_DDL;
  STMT_DDL := 'ALTER TABLE POWER_QUALITY ADD CONSTRAINT POWER_QUALITY_PK PRIMARY KEY(YYYYMMDDHHMM, MDEV_ID, MDEV_TYPE, DST) USING INDEX';
  EXECUTE IMMEDIATE STMT_DDL;
  STMT_DDL := 'CREATE INDEX POWER_QUALITY_IDX_01 ON POWER_QUALITY(YYYYMMDDHHMM, SUPPLIER_ID) LOCAL TABLESPACE AIMIRPART';
  EXECUTE IMMEDIATE STMT_DDL;
  STMT_DDL := 'CREATE INDEX POWER_QUALITY_IDX_02 ON POWER_QUALITY(YYYYMMDD) LOCAL TABLESPACE AIMIRPART';
  EXECUTE IMMEDIATE STMT_DDL;

END;
/

Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) 
values ('POWER_QUALITY','DAILY','ALTER TABLE POWER_QUALITY ADD PARTITION PART{STARTDATE} VALUES LESS THAN(''{NEXTPERIODDATE}0000'') update indexes','dd',10,0,1);
Insert into PARTITION_CONTROL (ID,PARTITIONINGINTERVAL,SQLCOMMAND,STARTDATETRUNCATE,ITERATIONS,STARTDATEOFFSET,ACTIVE) 
values ('POWER_QUALITY DROP','DAILY','ALTER TABLE POWER_QUALITY DROP PARTITION PART{STARTDATE} update indexes','dd',15,-365,1);


commit work;
