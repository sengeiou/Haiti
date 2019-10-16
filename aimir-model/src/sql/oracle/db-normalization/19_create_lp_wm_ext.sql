--Thread ID만큼 숫자를 바꾸면서 생성한다.
CREATE TABLE AIMIR.LP_WM_EXT_100
(
    MDEV_ID         VARCHAR2(20),
    YYYYMMDDHHMISS  VARCHAR2(14),
    CHANNEL         NUMBER(3),
    MDEV_TYPE       VARCHAR2(20),
    DST             NUMBER(2) ,
    DEVICE_ID       VARCHAR2(20),
    DEVICE_TYPE     VARCHAR2(15),
    METERINGTYPE    NUMBER(2),
    DEVICE_SERIAL   VARCHAR2(20),
    LP_STATUS       VARCHAR2(20),
    INTERVAL_YN     NUMBER(1),
    VALUE           NUMBER(19,4),
    WRITEDATE       VARCHAR2(14),
    CONTRACT_ID     NUMBER(10),
    MODEM_TIME      VARCHAR2(14),
    DCU_TIME        VARCHAR2(14)
)
ORGANIZATION EXTERNAL 
(TYPE ORACLE_LOADER
DEFAULT DIRECTORY AIMIR_LP_EXT_FILES
ACCESS PARAMETERS
(RECORDS DELIMITED BY '\n'   
NOBADFILE   
NOLOGFILE   
NODISCARDFILE   
FIELDS TERMINATED BY '|'   
NULLIF = "null"  
)
LOCATION ( 'LP_WM_EXT_100' )
)
REJECT LIMIT UNLIMITED;