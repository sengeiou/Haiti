--Tablespace는 사이트에 따라 변경될 수 있음.
DROP TABLE "AIMIR"."LPWM_TO_DAYWM";
CREATE TABLE "AIMIR"."LPWM_TO_DAYWM" 
(	"MDEV_ID" VARCHAR2(20 BYTE) NOT NULL ENABLE, 
	"YYYYMMDDHHMISS" VARCHAR2(14 BYTE) NOT NULL ENABLE, 
	"CHANNEL" NUMBER(3,0) NOT NULL ENABLE, 
	"MDEV_TYPE" VARCHAR2(20 BYTE) NOT NULL ENABLE, 
	"DST" NUMBER(2,0) DEFAULT 0 NOT NULL ENABLE, 
	"WRITEDATE" VARCHAR2(14 BYTE)
)
NOCOMPRESS LOGGING
TABLESPACE "AIMIRDAT" ;

CREATE INDEX "AIMIR"."IDX_LPWM_TO_DAYWM" ON "AIMIR"."LPWM_TO_DAYWM" ("MDEV_ID", "YYYYMMDDHHMISS", "CHANNEL", "MDEV_TYPE", "DST") 
TABLESPACE "AIMIRDAT" ;