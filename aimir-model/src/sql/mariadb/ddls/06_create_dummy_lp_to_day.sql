CREATE TABLE AIMIR.DUMMY_LPEM_TO_DAYEM
(
MDEV_ID VARCHAR(20) NOT NULL,
YYYYMMDDHHMISS VARCHAR(14) NOT NULL,
CHANNEL VARCHAR(3) NOT NULL,
MDEV_TYPE VARCHAR(20) NOT NULL,
DST DECIMAL(38) NOT NULL,
WRITEDATE VARCHAR(14)
);
CREATE INDEX AIMIR.IDX_DUMMY_LPEM_TO_DAYEM ON AIMIR.DUMMY_LPEM_TO_DAYEM(MDEV_ID, YYYYMMDDHHMISS, CHANNEL, MDEV_TYPE, DST);
