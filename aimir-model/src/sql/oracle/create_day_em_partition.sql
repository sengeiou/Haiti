drop table "BMT3"."DAY_EM"  CASCADE CONSTRAINTS;

CREATE TABLE "BMT3"."DAY_EM"
(
   CHANNEL decimal(10) NOT NULL,
   YYYYMMDD varchar2(8) NOT NULL,
   DST decimal(22) DEFAULT 0  NOT NULL,
   MDEV_ID varchar2(20) NOT NULL,
   MDEV_TYPE varchar2(20) NOT NULL,
   BASEVALUE float(126),
   CONTRACT_ID decimal(10),
   DAY_TYPE decimal(10),
   DEVICE_ID varchar2(20),
   DEVICE_TYPE varchar2(255),
   ENDDEVICE_ID decimal(10),
   LOCATION_ID decimal(10),
   METER_ID decimal(10),
   METERINGTYPE decimal(10),
   MODEM_ID decimal(10),
   SEND_RESULT varchar2(255),
   SIC varchar2(20),
   SUPPLIER_ID decimal(10),
   TOTAL float(126),
   VALUE_00 float(126),
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
   WRITEDATE varchar2(14),
   CONSTRAINT SYS_C004262 PRIMARY KEY (CHANNEL,YYYYMMDD,DST,MDEV_ID,MDEV_TYPE) using index tablespace BMT3IDX
)
partition by HASH (channel)
PARTITIONS 7
store in (BMT3CH1, BMT3CH2, BMT3CH3, BMT3CH4, BMT3CH5, BMT3CH6, BMT3CH7) nologging
;
ALTER TABLE "BMT3"."DAY_EM"
ADD CONSTRAINT FK77C7644BD6C540B4
FOREIGN KEY (ENDDEVICE_ID)
REFERENCES "BMT3"."ENDDEVICE"(ID)
;
ALTER TABLE "BMT3"."DAY_EM"
ADD CONSTRAINT FK77C7644B8F8DBE27
FOREIGN KEY (LOCATION_ID)
REFERENCES "BMT3"."LOCATION"(ID)
;
ALTER TABLE "BMT3"."DAY_EM"
ADD CONSTRAINT FK77C7644B7D01F2C7
FOREIGN KEY (SUPPLIER_ID)
REFERENCES "BMT3"."SUPPLIER"(ID)
;
ALTER TABLE "BMT3"."DAY_EM"
ADD CONSTRAINT FK77C7644BC0690AF4
FOREIGN KEY (MODEM_ID)
REFERENCES "BMT3"."MODEM"(ID)
;
ALTER TABLE "BMT3"."DAY_EM"
ADD CONSTRAINT FK77C7644B5CF66507
FOREIGN KEY (CONTRACT_ID)
REFERENCES "BMT3"."CONTRACT"(ID)
;
ALTER TABLE "BMT3"."DAY_EM"
ADD CONSTRAINT FK77C7644BCAB63A94
FOREIGN KEY (METER_ID)
REFERENCES "BMT3"."METER"(ID)
;
CREATE INDEX IDX_DAY_EM_01 ON "BMT3"."DAY_EM" 
(
  MDEV_TYPE,
  MDEV_ID,
  DST,
  YYYYMMDD,
  CHANNEL,
  LOCATION_ID
) tablespace BMT3IDX nologging
;
CREATE UNIQUE INDEX SYS_C004262 ON "BMT3"."DAY_EM" using index tablespace BMT3IDX
(
  CHANNEL,
  YYYYMMDD,
  DST,
  MDEV_ID,
  MDEV_TYPE
)
;
alter table "AIMIR"."DAY_EM" add partition dayem20141007 values less than ('20141008') tablespace AIMIRDAT33201410;
alter table "AIMIR"."DAY_EM" add partition dayem20141008 values less than ('20141009') tablespace AIMIRDAT33201410;
alter table "AIMIR"."DAY_EM" add partition dayem20141009 values less than ('20141010') tablespace AIMIRDAT33201410;
alter table "AIMIR"."DAY_EM" add partition dayem20141010 values less than ('20141011') tablespace AIMIRDAT33201410;
alter table "AIMIR"."DAY_EM" add partition dayem20141011 values less than ('20141012') tablespace AIMIRDAT33201410;
alter table "AIMIR"."DAY_EM" add partition dayem20141012 values less than ('20141013') tablespace AIMIRDAT33201410;
alter table "AIMIR"."DAY_EM" add partition dayem20141013 values less than ('20141014') tablespace AIMIRDAT33201410;


alter table "AIMIR"."DAY_EM" add partition dayem20141006 values less than ('20150501') tablespace AIMIRDAT33201410;
alter table "AIMIR"."DAY_EM" add partition dayem20141006 values less than ('20150601') tablespace AIMIRDAT33201410;
alter table "AIMIR"."DAY_EM" add partition dayem20141006 values less than ('20150701') tablespace AIMIRDAT33201410;
alter table "AIMIR"."DAY_EM" add partition dayem20141006 values less than ('20150801') tablespace AIMIRDAT33201410;
alter table "AIMIR"."DAY_EM" add partition dayem20141006 values less than ('20150901') tablespace AIMIRDAT33201410;
alter table "AIMIR"."DAY_EM" add partition dayem20141006 values less than ('20151001') tablespace AIMIRDAT33201410;
alter table "AIMIR"."DAY_EM" add partition dayem20141006 values less than ('20151101') tablespace AIMIRDAT33201410;
alter table "AIMIR"."DAY_EM" add partition dayem20141006 values less than ('20151201') tablespace AIMIRDAT33201410;
alter table "AIMIR"."DAY_EM" add partition dayem20141006 values less than ('20160101') tablespace AIMIRDAT33201410;
alter table "AIMIR"."DAY_EM" add partition dayem20141006 values less than ('20160201') tablespace AIMIRDAT33201410;


alter table "AIMIR"."DAY_EM" add partition dayem201602 values less than ('20160301') tablespace AIMIRDAT201602;
alter table "AIMIR"."DAY_EM" add partition dayem201603 values less than ('20160401') tablespace AIMIRDAT201603;
alter table "AIMIR"."DAY_EM" add partition dayem201604 values less than ('20160501') tablespace AIMIRDAT201604;
alter table "AIMIR"."DAY_EM" add partition dayem201605 values less than ('20160601') tablespace AIMIRDAT201605;
alter table "AIMIR"."DAY_EM" add partition dayem201606 values less than ('20160701') tablespace AIMIRDAT201606;
alter table "AIMIR"."DAY_EM" add partition dayem201607 values less than ('20160801') tablespace AIMIRDAT201607;
alter table "AIMIR"."DAY_EM" add partition dayem201608 values less than ('20160901') tablespace AIMIRDAT201608;
alter table "AIMIR"."DAY_EM" add partition dayem201609 values less than ('20161001') tablespace AIMIRDAT201609;
alter table "AIMIR"."DAY_EM" add partition dayem201610 values less than ('20161101') tablespace AIMIRDAT201610;
alter table "AIMIR"."DAY_EM" add partition dayem201612 values less than ('20170101') tablespace AIMIRDAT201612;
alter table "AIMIR"."DAY_EM" add partition dayem201701 values less than ('20170201') tablespace AIMIRDAT201701;
alter table "AIMIR"."DAY_EM" add partition dayem201702 values less than ('20170301') tablespace AIMIRDAT201702;
alter table "AIMIR"."DAY_EM" add partition dayem201703 values less than ('20170401') tablespace AIMIRDAT201703;
alter table "AIMIR"."DAY_EM" add partition dayem201704 values less than ('20170501') tablespace AIMIRDAT201704;
alter table "AIMIR"."DAY_EM" add partition dayem201705 values less than ('20170601') tablespace AIMIRDAT201705;
alter table "AIMIR"."DAY_EM" add partition dayem201706 values less than ('20170701') tablespace AIMIRDAT201706;
alter table "AIMIR"."DAY_EM" add partition dayem201707 values less than ('20170801') tablespace AIMIRDAT201707;
alter table "AIMIR"."DAY_EM" add partition dayem201708 values less than ('20170901') tablespace AIMIRDAT201708;
alter table "AIMIR"."DAY_EM" add partition dayem201709 values less than ('20171001') tablespace AIMIRDAT201709;
alter table "AIMIR"."DAY_EM" add partition dayem201710 values less than ('20171101') tablespace AIMIRDAT201710;
alter table "AIMIR"."DAY_EM" add partition dayem201711 values less than ('20171201') tablespace AIMIRDAT201711;
alter table "AIMIR"."DAY_EM" add partition dayem201712 values less than ('20180101') tablespace AIMIRDAT201712;
alter table "AIMIR"."DAY_EM" add partition dayem201801 values less than ('20180201') tablespace AIMIRDAT201801;
alter table "AIMIR"."DAY_EM" add partition dayem201802 values less than ('20180301') tablespace AIMIRDAT201802;
alter table "AIMIR"."DAY_EM" add partition dayem201803 values less than ('20180401') tablespace AIMIRDAT201803;
alter table "AIMIR"."DAY_EM" add partition dayem201804 values less than ('20180501') tablespace AIMIRDAT201804;
alter table "AIMIR"."DAY_EM" add partition dayem201805 values less than ('20180601') tablespace AIMIRDAT201805;
alter table "AIMIR"."DAY_EM" add partition dayem201806 values less than ('20180701') tablespace AIMIRDAT201806;
alter table "AIMIR"."DAY_EM" add partition dayem201807 values less than ('20180801') tablespace AIMIRDAT201807;
alter table "AIMIR"."DAY_EM" add partition dayem201808 values less than ('20180901') tablespace AIMIRDAT201808;
alter table "AIMIR"."DAY_EM" add partition dayem201809 values less than ('20181001') tablespace AIMIRDAT201809;
alter table "AIMIR"."DAY_EM" add partition dayem201810 values less than ('20181101') tablespace AIMIRDAT201810;
alter table "AIMIR"."DAY_EM" add partition dayem201811 values less than ('20181201') tablespace AIMIRDAT201811;
alter table "AIMIR"."DAY_EM" add partition dayem201812 values less than ('20190101') tablespace AIMIRDAT201812;
alter table "AIMIR"."DAY_EM" add partition dayem201901 values less than ('20190201') tablespace AIMIRDAT201901;
alter table "AIMIR"."DAY_EM" add partition dayem201902 values less than ('20190301') tablespace AIMIRDAT201902;
alter table "AIMIR"."DAY_EM" add partition dayem201903 values less than ('20190401') tablespace AIMIRDAT201903;
alter table "AIMIR"."DAY_EM" add partition dayem201904 values less than ('20190501') tablespace AIMIRDAT201904;
alter table "AIMIR"."DAY_EM" add partition dayem201905 values less than ('20190601') tablespace AIMIRDAT201905;
alter table "AIMIR"."DAY_EM" add partition dayem201906 values less than ('20190701') tablespace AIMIRDAT201906;
alter table "AIMIR"."DAY_EM" add partition dayem201907 values less than ('20190801') tablespace AIMIRDAT201907;
alter table "AIMIR"."DAY_EM" add partition dayem201908 values less than ('20190901') tablespace AIMIRDAT201908;
alter table "AIMIR"."DAY_EM" add partition dayem201909 values less than ('20191001') tablespace AIMIRDAT201909;
alter table "AIMIR"."DAY_EM" add partition dayem201910 values less than ('20191101') tablespace AIMIRDAT201910;
alter table "AIMIR"."DAY_EM" add partition dayem201911 values less than ('20191201') tablespace AIMIRDAT201911;
alter table "AIMIR"."DAY_EM" add partition dayem201912 values less than ('20200101') tablespace AIMIRDAT201912;
alter table "AIMIR"."DAY_EM" add partition dayem202001 values less than ('20200201') tablespace AIMIRDAT202001;
alter table "AIMIR"."DAY_EM" add partition dayem202002 values less than ('20200301') tablespace AIMIRDAT202002;
alter table "AIMIR"."DAY_EM" add partition dayem202003 values less than ('20200401') tablespace AIMIRDAT202003;
alter table "AIMIR"."DAY_EM" add partition dayem202004 values less than ('20200501') tablespace AIMIRDAT202004;
alter table "AIMIR"."DAY_EM" add partition dayem202005 values less than ('20200601') tablespace AIMIRDAT202005;
alter table "AIMIR"."DAY_EM" add partition dayem202006 values less than ('20200701') tablespace AIMIRDAT202006;
alter table "AIMIR"."DAY_EM" add partition dayem202007 values less than ('20200801') tablespace AIMIRDAT202007;
alter table "AIMIR"."DAY_EM" add partition dayem202008 values less than ('20200901') tablespace AIMIRDAT202008;
alter table "AIMIR"."DAY_EM" add partition dayem202009 values less than ('20201001') tablespace AIMIRDAT202009;
alter table "AIMIR"."DAY_EM" add partition dayem202010 values less than ('20201101') tablespace AIMIRDAT202010;
alter table "AIMIR"."DAY_EM" add partition dayem202011 values less than ('20201201') tablespace AIMIRDAT202011;
alter table "AIMIR"."DAY_EM" add partition dayem202012 values less than ('20210101') tablespace AIMIRDAT202012;