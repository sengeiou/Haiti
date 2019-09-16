CREATE TABLE LP_EM_EXT_100
(
    MDEV_ID         VARCHAR(20), 
    YYYYMMDDHHMISS  VARCHAR(14), 
    CHANNEL         SMALLINT, 
    MDEV_TYPE       VARCHAR(20),
    DST             TINYINT, 
    DEVICE_ID       VARCHAR(20), 
    DEVICE_TYPE     VARCHAR(15), 
    METERINGTYPE    TINYINT,
    DEVICE_SERIAL   VARCHAR(20), 
    LP_STATUS       VARCHAR(20), 
    INTERVAL_YN     TINYINT, 
    VALUE           DECIMAL(19,4),
    WRITEDATE       VARCHAR(14), 
    CONTRACT_ID     BIGINT, 
    MODEM_TIME      VARCHAR(14), 
    DCU_TIME        VARCHAR(14)
)
ORGANIZATION EXTERNAL 
(TYPE ORACLE_LOADER
DEFAULT DIRECTORY AIMIR_LP_EXT_FILES
ACCESS PARAMETERS
(RECORDS DELIMITED BY 'n'   
NOBADFILE   
NOLOGFILE   
NODISCARDFILE   
FIELDS TERMINATED BY '|'   
NULLIF = `null`  
)
LOCATION ( 'LP_EM_EXT_100' )
)
REJECT LIMIT UNLIMITED;
