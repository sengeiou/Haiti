create TABLESPACE AIMIRDAT 
DATAFILE '/home/oracle/db/oradata/orcl/aimirdat.dbf' SIZE 100M AUTOEXTEND ON next 500M maxsize 30000M;


create TABLESPACE AIMIRIDX 
DATAFILE '/home/oracle/db/oradata/orcl/aimiridx.dbf' SIZE 100M AUTOEXTEND ON next 500M maxsize 30000M;

Drop TABLE AIMIR.LP_EM;
CREATE TABLE AIMIR.LP_EM
(
    MDEV_ID         VARCHAR(20) NOT NULL,
    YYYYMMDDHHMISS  VARCHAR(14) NOT NULL,
    CHANNEL         SMALLINT NOT NULL,
    MDEV_TYPE       VARCHAR(20) NOT NULL,
    DST             DECIMAL(38) DEFAULT 0 NOT NULL,
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
PARTITION BY RANGE (YYYYMMDDHHMISS)
(
 PARTITION LP_EM_201901_01 VALUES LESS THAN ('20190111000000'),
 PARTITION LP_EM_201901_02 VALUES LESS THAN ('20190121000000'),
 PARTITION LP_EM_201901_03 VALUES LESS THAN ('20190201000000'),
 PARTITION LP_EM_201902_01 VALUES LESS THAN ('20190211000000'),
 PARTITION LP_EM_201902_02 VALUES LESS THAN ('20190221000000'),
 PARTITION LP_EM_201902_03 VALUES LESS THAN ('20190301000000'),
 PARTITION LP_EM_201903_01 VALUES LESS THAN ('20190311000000'),
 PARTITION LP_EM_201903_02 VALUES LESS THAN ('20190321000000'),
 PARTITION LP_EM_201903_03 VALUES LESS THAN ('20190401000000'),
 PARTITION LP_EM_201904_01 VALUES LESS THAN ('20190411000000'),
 PARTITION LP_EM_201904_02 VALUES LESS THAN ('20190421000000'),
 PARTITION LP_EM_201904_03 VALUES LESS THAN ('20190501000000'), 
 PARTITION LP_EM_201905_01 VALUES LESS THAN ('20190511000000'),
 PARTITION LP_EM_201905_02 VALUES LESS THAN ('20190521000000'),
 PARTITION LP_EM_201905_03 VALUES LESS THAN ('20190601000000'), 
 PARTITION LP_EM_201906_01 VALUES LESS THAN ('20190611000000'),
 PARTITION LP_EM_201906_02 VALUES LESS THAN ('20190621000000'),
 PARTITION LP_EM_201906_03 VALUES LESS THAN ('20190701000000'), 
 PARTITION LP_EM_201907_01 VALUES LESS THAN ('20190711000000'),
 PARTITION LP_EM_201907_02 VALUES LESS THAN ('20190721000000'),
 PARTITION LP_EM_201907_03 VALUES LESS THAN ('20190801000000'), 
 PARTITION LP_EM_201908_01 VALUES LESS THAN ('20190811000000'),
 PARTITION LP_EM_201908_02 VALUES LESS THAN ('20190821000000'),
 PARTITION LP_EM_201908_03 VALUES LESS THAN ('20190901000000'), 
 PARTITION LP_EM_201909_01 VALUES LESS THAN ('20190911000000'),
 PARTITION LP_EM_201909_02 VALUES LESS THAN ('20190921000000'),
 PARTITION LP_EM_201909_03 VALUES LESS THAN ('20191001000000'), 
 PARTITION LP_EM_201910_01 VALUES LESS THAN ('20191011000000'),
 PARTITION LP_EM_201910_02 VALUES LESS THAN ('20191021000000'),
 PARTITION LP_EM_201910_03 VALUES LESS THAN ('20191101000000'), 
 PARTITION LP_EM_201911_01 VALUES LESS THAN ('20191111000000'),
 PARTITION LP_EM_201911_02 VALUES LESS THAN ('20191121000000'),
 PARTITION LP_EM_201911_03 VALUES LESS THAN ('20191201000000'), 
 PARTITION LP_EM_201912_01 VALUES LESS THAN ('20191211000000'),
 PARTITION LP_EM_201912_02 VALUES LESS THAN ('20191221000000'),
 PARTITION LP_EM_201912_03 VALUES LESS THAN ('20200101000000'),
 PARTITION LP_EM_202001_01 VALUES LESS THAN ('20200111000000'),
 PARTITION LP_EM_202001_02 VALUES LESS THAN ('20200121000000'),
 PARTITION LP_EM_202001_03 VALUES LESS THAN ('20200201000000'),
 PARTITION LP_EM_202002_01 VALUES LESS THAN ('20200211000000'),
 PARTITION LP_EM_202002_02 VALUES LESS THAN ('20200221000000'),
 PARTITION LP_EM_202002_03 VALUES LESS THAN ('20200301000000'),
 PARTITION LP_EM_202003_01 VALUES LESS THAN ('20200311000000'),
 PARTITION LP_EM_202003_02 VALUES LESS THAN ('20200321000000'),
 PARTITION LP_EM_202003_03 VALUES LESS THAN ('20200401000000'),
 PARTITION LP_EM_202004_01 VALUES LESS THAN ('20200411000000'),
 PARTITION LP_EM_202004_02 VALUES LESS THAN ('20200421000000'),
 PARTITION LP_EM_202004_03 VALUES LESS THAN ('20200501000000'),
 PARTITION LP_EM_202005_01 VALUES LESS THAN ('20200511000000'),
 PARTITION LP_EM_202005_02 VALUES LESS THAN ('20200521000000'),
 PARTITION LP_EM_202005_03 VALUES LESS THAN ('20200601000000'),
 PARTITION LP_EM_202006_01 VALUES LESS THAN ('20200611000000'),
 PARTITION LP_EM_202006_02 VALUES LESS THAN ('20200621000000'),
 PARTITION LP_EM_202006_03 VALUES LESS THAN ('20200701000000'),
 PARTITION LP_EM_202007_01 VALUES LESS THAN ('20200711000000'),
 PARTITION LP_EM_202007_02 VALUES LESS THAN ('20200721000000'),
 PARTITION LP_EM_202007_03 VALUES LESS THAN ('20200801000000'),
 PARTITION LP_EM_202008_01 VALUES LESS THAN ('20200811000000'),
 PARTITION LP_EM_202008_02 VALUES LESS THAN ('20200821000000'),
 PARTITION LP_EM_202008_03 VALUES LESS THAN ('20200901000000'),
 PARTITION LP_EM_202009_01 VALUES LESS THAN ('20200911000000'),
 PARTITION LP_EM_202009_02 VALUES LESS THAN ('20200921000000'),
 PARTITION LP_EM_202009_03 VALUES LESS THAN ('20201001000000'),
 PARTITION LP_EM_202010_01 VALUES LESS THAN ('20201011000000'),
 PARTITION LP_EM_202010_02 VALUES LESS THAN ('20201021000000'),
 PARTITION LP_EM_202010_03 VALUES LESS THAN ('20201101000000'),
 PARTITION LP_EM_202011_01 VALUES LESS THAN ('20201111000000'),
 PARTITION LP_EM_202011_02 VALUES LESS THAN ('20201121000000'),
 PARTITION LP_EM_202011_03 VALUES LESS THAN ('20201201000000'),
 PARTITION LP_EM_202012_01 VALUES LESS THAN ('20201211000000'),
 PARTITION LP_EM_202012_02 VALUES LESS THAN ('20201221000000'),
 PARTITION LP_EM_202012_03 VALUES LESS THAN ('20210101000000')
);
CREATE UNIQUE INDEX AIMIR.PK_LP_EM ON AIMIR.LP_EM (MDEV_ID, YYYYMMDDHHMISS, CHANNEL, MDEV_TYPE, DST);
ALTER TABLE AIMIR.LP_EM ADD CONSTRAINT PK_LP_EM PRIMARY KEY(MDEV_ID, YYYYMMDDHHMISS, CHANNEL, MDEV_TYPE, DST);
