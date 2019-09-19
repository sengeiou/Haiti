CREATE TABLE AIMIR.DUMMY_DAYEM_TO_MONTHEM
(
MDEV_ID VARCHAR2(20) NOT NULL,
YYYYMMDD VARCHAR2(8) NOT NULL,
CHANNEL VARCHAR2(3) NOT NULL,
MDEV_TYPE VARCHAR2(20) NOT NULL,
DST NUMBER(2) NOT NULL,
HH VARCHAR2(2) NOT NULL,
WRITEDATE VARCHAR2(14)
);
CREATE INDEX AIMIR.IDX_DUMMY_DAYEM_TO_MONTHEM ON AIMIR.DUMMY_DAYEM_TO_MONTHEM(MDEV_ID, YYYYMMDD, CHANNEL, MDEV_TYPE, DST);
