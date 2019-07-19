drop table "BMT3"."MONTH_EM"  CASCADE CONSTRAINTS;

CREATE TABLE "BMT3"."MONTH_EM"
(
   CHANNEL decimal(10) NOT NULL,
   YYYYMM varchar2(6) NOT NULL,
   DST decimal(22) DEFAULT 0  NOT NULL,
   MDEV_ID varchar2(20) NOT NULL,
   MDEV_TYPE varchar2(20) NOT NULL,
   BASEVALUE float(126),
   CONTRACT_ID decimal(10),
   DEVICE_ID varchar2(20),
   DEVICE_TYPE varchar2(255),
   ENDDEVICE_ID decimal(10),
   LOCATION_ID decimal(10),
   METER_ID decimal(10),
   METERINGTYPE decimal(10),
   MODEM_ID decimal(10),
   SEND_RESULT varchar2(255),
   SUPPLIER_ID decimal(10),
   TOTAL float(126),
   VALUE_01 float(126),
   VALUE_02 float(126),
   VALUE_03 float(126),
   VALUE_04 float(126),
   VALUE_05 float(126),
   VALUE_06 float(126),
   VALUE_07 float(126),
   VALUE_08 float(126),
   VALUE_09 float(126),
   VALUE_10 float(126),
   VALUE_11 float(126),
   VALUE_12 float(126),
   VALUE_13 float(126),
   VALUE_14 float(126),
   VALUE_15 float(126),
   VALUE_16 float(126),
   VALUE_17 float(126),
   VALUE_18 float(126),
   VALUE_19 float(126),
   VALUE_20 float(126),
   VALUE_21 float(126),
   VALUE_22 float(126),
   VALUE_23 float(126),
   VALUE_24 float(126),
   VALUE_25 float(126),
   VALUE_26 float(126),
   VALUE_27 float(126),
   VALUE_28 float(126),
   VALUE_29 float(126),
   VALUE_30 float(126),
   VALUE_31 float(126),
   WRITEDATE varchar2(14),
   CONSTRAINT SYS_C004657 PRIMARY KEY (CHANNEL,YYYYMM,DST,MDEV_ID,MDEV_TYPE) using index tablespace BMT3IDX
)
partition by HASH (channel)
PARTITIONS 7
store in (BMT3CH1, BMT3CH2, BMT3CH3, BMT3CH4, BMT3CH5, BMT3CH6, BMT3CH7) nologging
;
ALTER TABLE "BMT3"."MONTH_EM"
ADD CONSTRAINT FK1BA2A6E75CF66507
FOREIGN KEY (CONTRACT_ID)
REFERENCES "BMT3"."CONTRACT"(ID)
;
ALTER TABLE "BMT3"."MONTH_EM"
ADD CONSTRAINT FK1BA2A6E77D01F2C7
FOREIGN KEY (SUPPLIER_ID)
REFERENCES "BMT3"."SUPPLIER"(ID)
;
ALTER TABLE "BMT3"."MONTH_EM"
ADD CONSTRAINT FK1BA2A6E7C0690AF4
FOREIGN KEY (MODEM_ID)
REFERENCES "BMT3"."MODEM"(ID)
;
ALTER TABLE "BMT3"."MONTH_EM"
ADD CONSTRAINT FK1BA2A6E7CAB63A94
FOREIGN KEY (METER_ID)
REFERENCES "BMT3"."METER"(ID)
;
ALTER TABLE "BMT3"."MONTH_EM"
ADD CONSTRAINT FK1BA2A6E78F8DBE27
FOREIGN KEY (LOCATION_ID)
REFERENCES "BMT3"."LOCATION"(ID)
;
ALTER TABLE "BMT3"."MONTH_EM"
ADD CONSTRAINT FK1BA2A6E7D6C540B4
FOREIGN KEY (ENDDEVICE_ID)
REFERENCES "BMT3"."ENDDEVICE"(ID)
;
CREATE UNIQUE INDEX SYS_C004657 ON "BMT3"."MONTH_EM" using index tablespace BMT3IDX
(
  CHANNEL,
  YYYYMM,
  DST,
  MDEV_ID,
  MDEV_TYPE
)
;
CREATE INDEX IDX_MONTH_EM_01 ON "BMT3"."MONTH_EM"
(
  MDEV_TYPE,
  MDEV_ID,
  DST,
  YYYYMM,
  CHANNEL,
  LOCATION_ID
) tablespace BMT3IDX nologging
;
create index IDX_MONTH_EM_02 on BMT3.MONTH_EM (mdev_type, mdev_id, yyyymm) tablespace BMT3IDX nologging

alter table "AIMIR"."MONTH_EM" add partition monthem201409 values less than ('201410') tablespace AIMIRDAT33201410;
alter table "AIMIR"."MONTH_EM" add partition monthem201410 values less than ('201411') tablespace AIMIRDAT33201410;
alter table "AIMIR"."MONTH_EM" add partition monthem201411 values less than ('201412') tablespace AIMIRDAT201411;
alter table "AIMIR"."MONTH_EM" add partition monthem201412 values less than ('201501') tablespace AIMIRDAT201412;
alter table "AIMIR"."MONTH_EM" add partition monthem201501 values less than ('201502') tablespace AIMIRDAT201501;
alter table "AIMIR"."MONTH_EM" add partition monthem201502 values less than ('201503') tablespace AIMIRDAT201502;
alter table "AIMIR"."MONTH_EM" add partition monthem201503 values less than ('201504') tablespace AIMIRDAT201503;
alter table "AIMIR"."MONTH_EM" add partition monthem201504 values less than ('201505') tablespace AIMIRDAT201504;
alter table "AIMIR"."MONTH_EM" add partition monthem201505 values less than ('201506') tablespace AIMIRDAT201505;
alter table "AIMIR"."MONTH_EM" add partition monthem201506 values less than ('201507') tablespace AIMIRDAT201506;
alter table "AIMIR"."MONTH_EM" add partition monthem201507 values less than ('201508') tablespace AIMIRDAT201507;
alter table "AIMIR"."MONTH_EM" add partition monthem201508 values less than ('201509') tablespace AIMIRDAT201508;
alter table "AIMIR"."MONTH_EM" add partition monthem201509 values less than ('201510') tablespace AIMIRDAT201509;
alter table "AIMIR"."MONTH_EM" add partition monthem201510 values less than ('201511') tablespace AIMIRDAT201510;
alter table "AIMIR"."MONTH_EM" add partition monthem201511 values less than ('201512') tablespace AIMIRDAT201511;
alter table "AIMIR"."MONTH_EM" add partition monthem201512 values less than ('201601') tablespace AIMIRDAT201512;
alter table "AIMIR"."MONTH_EM" add partition monthem201601 values less than ('201602') tablespace AIMIRDAT201601;
alter table "AIMIR"."MONTH_EM" add partition monthem201602 values less than ('201603') tablespace AIMIRDAT201602;
alter table "AIMIR"."MONTH_EM" add partition monthem201603 values less than ('201604') tablespace AIMIRDAT201603;
alter table "AIMIR"."MONTH_EM" add partition monthem201604 values less than ('201605') tablespace AIMIRDAT201604;
alter table "AIMIR"."MONTH_EM" add partition monthem201605 values less than ('201606') tablespace AIMIRDAT201605;
alter table "AIMIR"."MONTH_EM" add partition monthem201606 values less than ('201607') tablespace AIMIRDAT201606;
alter table "AIMIR"."MONTH_EM" add partition monthem201607 values less than ('201608') tablespace AIMIRDAT201607;
alter table "AIMIR"."MONTH_EM" add partition monthem201608 values less than ('201609') tablespace AIMIRDAT201608;
alter table "AIMIR"."MONTH_EM" add partition monthem201609 values less than ('201610') tablespace AIMIRDAT201609;
alter table "AIMIR"."MONTH_EM" add partition monthem201610 values less than ('201611') tablespace AIMIRDAT201610;
alter table "AIMIR"."MONTH_EM" add partition monthem201612 values less than ('201701') tablespace AIMIRDAT201612;
alter table "AIMIR"."MONTH_EM" add partition monthem201701 values less than ('201702') tablespace AIMIRDAT201701;
alter table "AIMIR"."MONTH_EM" add partition monthem201702 values less than ('201703') tablespace AIMIRDAT201702;
alter table "AIMIR"."MONTH_EM" add partition monthem201703 values less than ('201704') tablespace AIMIRDAT201703;
alter table "AIMIR"."MONTH_EM" add partition monthem201704 values less than ('201705') tablespace AIMIRDAT201704;
alter table "AIMIR"."MONTH_EM" add partition monthem201705 values less than ('201706') tablespace AIMIRDAT201705;
alter table "AIMIR"."MONTH_EM" add partition monthem201706 values less than ('201707') tablespace AIMIRDAT201706;
alter table "AIMIR"."MONTH_EM" add partition monthem201707 values less than ('201708') tablespace AIMIRDAT201707;
alter table "AIMIR"."MONTH_EM" add partition monthem201708 values less than ('201709') tablespace AIMIRDAT201708;
alter table "AIMIR"."MONTH_EM" add partition monthem201709 values less than ('201710') tablespace AIMIRDAT201709;
alter table "AIMIR"."MONTH_EM" add partition monthem201710 values less than ('201711') tablespace AIMIRDAT201710;
alter table "AIMIR"."MONTH_EM" add partition monthem201711 values less than ('201712') tablespace AIMIRDAT201711;
alter table "AIMIR"."MONTH_EM" add partition monthem201712 values less than ('201801') tablespace AIMIRDAT201712;
alter table "AIMIR"."MONTH_EM" add partition monthem201801 values less than ('201802') tablespace AIMIRDAT201801;
alter table "AIMIR"."MONTH_EM" add partition monthem201802 values less than ('201803') tablespace AIMIRDAT201802;
alter table "AIMIR"."MONTH_EM" add partition monthem201803 values less than ('201804') tablespace AIMIRDAT201803;
alter table "AIMIR"."MONTH_EM" add partition monthem201804 values less than ('201805') tablespace AIMIRDAT201804;
alter table "AIMIR"."MONTH_EM" add partition monthem201805 values less than ('201806') tablespace AIMIRDAT201805;
alter table "AIMIR"."MONTH_EM" add partition monthem201806 values less than ('201807') tablespace AIMIRDAT201806;
alter table "AIMIR"."MONTH_EM" add partition monthem201807 values less than ('201808') tablespace AIMIRDAT201807;
alter table "AIMIR"."MONTH_EM" add partition monthem201808 values less than ('201809') tablespace AIMIRDAT201808;
alter table "AIMIR"."MONTH_EM" add partition monthem201809 values less than ('201810') tablespace AIMIRDAT201809;
alter table "AIMIR"."MONTH_EM" add partition monthem201810 values less than ('201811') tablespace AIMIRDAT201810;
alter table "AIMIR"."MONTH_EM" add partition monthem201811 values less than ('201812') tablespace AIMIRDAT201811;
alter table "AIMIR"."MONTH_EM" add partition monthem201812 values less than ('201901') tablespace AIMIRDAT201812;
alter table "AIMIR"."MONTH_EM" add partition monthem201901 values less than ('201902') tablespace AIMIRDAT201901;
alter table "AIMIR"."MONTH_EM" add partition monthem201902 values less than ('201903') tablespace AIMIRDAT201902;
alter table "AIMIR"."MONTH_EM" add partition monthem201903 values less than ('201904') tablespace AIMIRDAT201903;
alter table "AIMIR"."MONTH_EM" add partition monthem201904 values less than ('201905') tablespace AIMIRDAT201904;
alter table "AIMIR"."MONTH_EM" add partition monthem201905 values less than ('201906') tablespace AIMIRDAT201905;
alter table "AIMIR"."MONTH_EM" add partition monthem201906 values less than ('201907') tablespace AIMIRDAT201906;
alter table "AIMIR"."MONTH_EM" add partition monthem201907 values less than ('201908') tablespace AIMIRDAT201907;
alter table "AIMIR"."MONTH_EM" add partition monthem201908 values less than ('201909') tablespace AIMIRDAT201908;
alter table "AIMIR"."MONTH_EM" add partition monthem201909 values less than ('201910') tablespace AIMIRDAT201909;
alter table "AIMIR"."MONTH_EM" add partition monthem201910 values less than ('201911') tablespace AIMIRDAT201910;
alter table "AIMIR"."MONTH_EM" add partition monthem201911 values less than ('201912') tablespace AIMIRDAT201911;
alter table "AIMIR"."MONTH_EM" add partition monthem201912 values less than ('202001') tablespace AIMIRDAT201912;
alter table "AIMIR"."MONTH_EM" add partition monthem202001 values less than ('202002') tablespace AIMIRDAT202001;
alter table "AIMIR"."MONTH_EM" add partition monthem202002 values less than ('202003') tablespace AIMIRDAT202002;
alter table "AIMIR"."MONTH_EM" add partition monthem202003 values less than ('202004') tablespace AIMIRDAT202003;
alter table "AIMIR"."MONTH_EM" add partition monthem202004 values less than ('202005') tablespace AIMIRDAT202004;
alter table "AIMIR"."MONTH_EM" add partition monthem202005 values less than ('202006') tablespace AIMIRDAT202005;
alter table "AIMIR"."MONTH_EM" add partition monthem202006 values less than ('202007') tablespace AIMIRDAT202006;
alter table "AIMIR"."MONTH_EM" add partition monthem202007 values less than ('202008') tablespace AIMIRDAT202007;
alter table "AIMIR"."MONTH_EM" add partition monthem202008 values less than ('202009') tablespace AIMIRDAT202008;
alter table "AIMIR"."MONTH_EM" add partition monthem202009 values less than ('202010') tablespace AIMIRDAT202009;
alter table "AIMIR"."MONTH_EM" add partition monthem202010 values less than ('202011') tablespace AIMIRDAT202010;
alter table "AIMIR"."MONTH_EM" add partition monthem202011 values less than ('202012') tablespace AIMIRDAT202011;
alter table "AIMIR"."MONTH_EM" add partition monthem202012 values less than ('202101') tablespace AIMIRDAT202012;