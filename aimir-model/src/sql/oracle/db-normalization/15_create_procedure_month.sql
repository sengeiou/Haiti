CREATE OR REPLACE PROCEDURE AIMIR.BATCH_MONTH_EM IS

V_SQL VARCHAR2(100); 
V_ROWS NUMBER;

BEGIN        
DBMS_OUTPUT.ENABLE;  

V_SQL := 'TRUNCATE TABLE AIMIR.DUMMY_DAYEM_TO_MONTHEM';
EXECUTE IMMEDIATE V_SQL;
INSERT INTO AIMIR.DUMMY_DAYEM_TO_MONTHEM SELECT /*+ INDEX(DAYEM_TO_MONTHEM IDX_DAYEM_TO_MONTHEM) */ MDEV_ID, YYYYMMDD, CHANNEL, MDEV_TYPE, DST, HH, WRITEDATE FROM AIMIR.DAYEM_TO_MONTHEM WHERE ROWNUM <= 100000;      
V_SQL := 'ANALYZE TABLE AIMIR.DUMMY_DAYEM_TO_MONTHEM ESTIMATE STATISTICS';      
EXECUTE IMMEDIATE V_SQL;


MERGE INTO AIMIR.MONTH_EM T_TABLE USING
 (SELECT A.MDEV_ID, A.YYYYMM, A.CHANNEL, A.MDEV_TYPE, A.DST, A.DD, A.METER_ID, A.METERINGTYPE, A.MDEV_ID_LAST, A.MODEM_ID, A.DEVICE_ID, A.DEVICE_TYPE, SUM(A.C_VALUE) C_VALUE,
 MIN(A.BASEVALUE) BASEVALUE, CASE WHEN A.CH_METHOD = 'MAX' THEN MAX(VALUE) WHEN A.CH_METHOD = 'SUM' THEN SUM(VALUE) WHEN A.CH_METHOD = 'AVG' THEN AVG(VALUE) END VALUE,
 MIN(A.WRITEDATE) WRITEDATE, A.SUPPLIER_ID, A.CH_METHOD, CONTRACT_VALUE.CONTRACT_ID CONTRACT_ID, A.ENDDEVICE_ID FROM
  (SELECT MDEV_ID, SUBSTR(YYYYMMDD, 1, 6) YYYYMM, CHANNEL, MDEV_TYPE, DST, SUBSTR(YYYYMMDD, 7, 2) DD, METER_ID, METERINGTYPE, MDEV_ID_LAST, MODEM_ID, DEVICE_ID, DEVICE_TYPE, WRITEDATE, SUPPLIER_ID, CH_METHOD, ENDDEVICE_ID, C_VALUE, BASEVALUE, VALUE FROM
   (SELECT /*+ RESULT_CACHE */ A.MDEV_ID, A.YYYYMMDD, A.CHANNEL, A.MDEV_TYPE, A.DST, A.HH, A.METER_ID, A.METERINGTYPE, A.MDEV_ID_LAST, A.MODEM_ID, A.DEVICE_ID, A.DEVICE_TYPE, A.C_VALUE, A.BASEVALUE, A.VALUE VALUE, A.WRITEDATE, A.SUPPLIER_ID, A.CH_METHOD, A.CONTRACT_ID, A.ENDDEVICE_ID
   FROM AIMIR.DAY_EM A JOIN (SELECT /*+ INDEX_FFS(DUMMY_DAYEM_TO_MONTHEM, IDX_DUMMY_DAYEM_TO_MONTHEM) */ MDEV_ID, SUBSTR(YYYYMMDD, 1, 6) YYYYMM, CHANNEL, MDEV_TYPE, DST FROM AIMIR.DUMMY_DAYEM_TO_MONTHEM GROUP BY MDEV_ID, SUBSTR(YYYYMMDD, 1, 6), CHANNEL, MDEV_TYPE, DST) B     
   ON A.MDEV_ID = B.MDEV_ID AND A.YYYYMMDD BETWEEN B.YYYYMM||'00' AND B.YYYYMM||'31' AND A.CHANNEL = B.CHANNEL AND A.MDEV_TYPE = B.MDEV_TYPE AND A.DST = B.DST))A
  LEFT JOIN
  (SELECT A.MDEV_ID, SUBSTR(A.YYYYMMDD, 1, 6) YYYYMM, A.CHANNEL, A.MDEV_TYPE, A.DST, A.CONTRACT_ID FROM
   (SELECT /*+ RESULT_CACHE */ A.MDEV_ID, A.YYYYMMDD, A.CHANNEL, A.MDEV_TYPE, A.DST, A.HH, A.METER_ID, A.METERINGTYPE, A.MDEV_ID_LAST, A.MODEM_ID, A.DEVICE_ID, A.DEVICE_TYPE, A.C_VALUE, A.BASEVALUE, A.VALUE VALUE, A.WRITEDATE, A.SUPPLIER_ID, A.CH_METHOD, A.CONTRACT_ID, A.ENDDEVICE_ID
   FROM AIMIR.DAY_EM A JOIN (SELECT /*+ INDEX_FFS(DUMMY_DAYEM_TO_MONTHEM, IDX_DUMMY_DAYEM_TO_MONTHEM) */ MDEV_ID, SUBSTR(YYYYMMDD, 1, 6) YYYYMM, CHANNEL, MDEV_TYPE, DST FROM AIMIR.DUMMY_DAYEM_TO_MONTHEM GROUP BY MDEV_ID, SUBSTR(YYYYMMDD, 1, 6), CHANNEL, MDEV_TYPE, DST) B     
   ON A.MDEV_ID = B.MDEV_ID AND A.YYYYMMDD BETWEEN B.YYYYMM||'00' AND B.YYYYMM||'31' AND A.CHANNEL = B.CHANNEL AND A.MDEV_TYPE = B.MDEV_TYPE AND A.DST = B.DST) A
   JOIN
   (SELECT MDEV_ID, SUBSTR(YYYYMMDD, 1, 6) YYYYMM, CHANNEL, MDEV_TYPE, DST, MAX(YYYYMMDD||HH) YYYYMMDDHH FROM
    (SELECT /*+ RESULT_CACHE */ A.MDEV_ID, A.YYYYMMDD, A.CHANNEL, A.MDEV_TYPE, A.DST, A.HH, A.METER_ID, A.METERINGTYPE, A.MDEV_ID_LAST, A.MODEM_ID, A.DEVICE_ID, A.DEVICE_TYPE, A.C_VALUE, A.BASEVALUE, A.VALUE VALUE, A.WRITEDATE, A.SUPPLIER_ID, A.CH_METHOD, A.CONTRACT_ID, A.ENDDEVICE_ID
    FROM AIMIR.DAY_EM A JOIN (SELECT /*+ INDEX_FFS(DUMMY_DAYEM_TO_MONTHEM, IDX_DUMMY_DAYEM_TO_MONTHEM) */ MDEV_ID, SUBSTR(YYYYMMDD, 1, 6) YYYYMM, CHANNEL, MDEV_TYPE, DST FROM AIMIR.DUMMY_DAYEM_TO_MONTHEM GROUP BY MDEV_ID, SUBSTR(YYYYMMDD, 1, 6), CHANNEL, MDEV_TYPE, DST) B     
    ON A.MDEV_ID = B.MDEV_ID AND A.YYYYMMDD BETWEEN B.YYYYMM||'00' AND B.YYYYMM||'31' AND A.CHANNEL = B.CHANNEL AND A.MDEV_TYPE = B.MDEV_TYPE AND A.DST = B.DST)
   GROUP BY MDEV_ID, SUBSTR(YYYYMMDD, 1, 6), CHANNEL, MDEV_TYPE, DST) B
  ON A.MDEV_ID = B.MDEV_ID AND A.YYYYMMDD = SUBSTR(B.YYYYMMDDHH, 1, 8) AND A.HH = SUBSTR(B.YYYYMMDDHH, 9, 2) AND A.CHANNEL = B.CHANNEL AND A.MDEV_TYPE = B.MDEV_TYPE AND A.DST = B.DST) CONTRACT_VALUE
 ON A.MDEV_ID = CONTRACT_VALUE.MDEV_ID AND A.YYYYMM = CONTRACT_VALUE.YYYYMM AND A.CHANNEL = CONTRACT_VALUE.CHANNEL AND A.MDEV_TYPE = CONTRACT_VALUE.MDEV_TYPE AND A.DST = CONTRACT_VALUE.DST
 GROUP BY A.MDEV_ID, A.YYYYMM, A.CHANNEL, A.MDEV_TYPE, A.DST, A.DD, A.METER_ID, A.METERINGTYPE, A.MDEV_ID_LAST, A.MODEM_ID, A.DEVICE_ID, A.DEVICE_TYPE, A.SUPPLIER_ID, A.CH_METHOD, CONTRACT_VALUE.CONTRACT_ID, A.ENDDEVICE_ID) S_TABLE
ON (T_TABLE.MDEV_ID = S_TABLE.MDEV_ID AND T_TABLE.YYYYMM = S_TABLE.YYYYMM AND T_TABLE.CHANNEL = S_TABLE.CHANNEL AND T_TABLE.MDEV_TYPE = S_TABLE.MDEV_TYPE AND T_TABLE.DST = S_TABLE.DST AND T_TABLE.DD = S_TABLE.DD)
WHEN MATCHED THEN UPDATE SET T_TABLE.METER_ID = S_TABLE.METER_ID, T_TABLE.METERINGTYPE = S_TABLE.METERINGTYPE, T_TABLE.MDEV_ID_LAST = S_TABLE.MDEV_ID_LAST, T_TABLE.MODEM_ID = S_TABLE.MODEM_ID, T_TABLE.DEVICE_ID = S_TABLE.DEVICE_ID,         
                             T_TABLE.DEVICE_TYPE = S_TABLE.DEVICE_TYPE, T_TABLE.C_VALUE = S_TABLE.C_VALUE, T_TABLE.BASEVALUE = S_TABLE.BASEVALUE, T_TABLE.VALUE = S_TABLE.VALUE, T_TABLE.WRITEDATE = S_TABLE.WRITEDATE, T_TABLE.SUPPLIER_ID = S_TABLE.SUPPLIER_ID, T_TABLE.CH_METHOD = S_TABLE.CH_METHOD, T_TABLE.CONTRACT_ID = S_TABLE.CONTRACT_ID, T_TABLE.ENDDEVICE_ID = S_TABLE.ENDDEVICE_ID 
WHEN NOT MATCHED THEN INSERT VALUES (S_TABLE.MDEV_ID, S_TABLE.YYYYMM, S_TABLE.CHANNEL, S_TABLE.MDEV_TYPE, S_TABLE.DST, S_TABLE.DD, S_TABLE.METER_ID, S_TABLE.METERINGTYPE, S_TABLE.MDEV_ID_LAST, S_TABLE.MODEM_ID, S_TABLE.DEVICE_ID, S_TABLE.DEVICE_TYPE, S_TABLE.C_VALUE, S_TABLE.BASEVALUE, S_TABLE.VALUE, S_TABLE.WRITEDATE, S_TABLE.SUPPLIER_ID, S_TABLE.CH_METHOD, S_TABLE.CONTRACT_ID, S_TABLE.ENDDEVICE_ID);

DELETE FROM AIMIR.DAYEM_TO_MONTHEM A WHERE EXISTS         
(SELECT 1 FROM AIMIR.DUMMY_DAYEM_TO_MONTHEM B WHERE A.MDEV_ID = B.MDEV_ID AND A.YYYYMMDD = B.YYYYMMDD AND A.CHANNEL = B.CHANNEL AND A.MDEV_TYPE = B.MDEV_TYPE AND A.DST = B.DST AND A.HH = B.HH AND A.WRITEDATE = B.WRITEDATE);
    
EXCEPTION WHEN OTHERS THEN            
          DBMS_OUTPUT.PUT_LINE('ERR CODE : ' || TO_CHAR(SQLCODE));          
          DBMS_OUTPUT.PUT_LINE('ERR MESSAGE : ' || SQLERRM);        
                 
END;
