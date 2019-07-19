drop table "BMT3"."METERINGDATA_EM"  CASCADE CONSTRAINTS;

CREATE TABLE "BMT3"."METERINGDATA_EM"
(
   YYYYMMDDHHMMSS varchar2(14) NOT NULL,
   DST decimal(22) DEFAULT 0  NOT NULL,
   MDEV_ID varchar2(20) NOT NULL,
   MDEV_TYPE varchar2(20) NOT NULL,
   DEVICE_ID varchar2(20),
   DEVICE_TYPE varchar2(255),
   HHMMSS varchar2(6),
   METERINGTYPE decimal(10),
   VALUE float(126),
   WRITEDATE varchar2(14),
   YYYYMMDD varchar2(8),
   CONTRACT_ID decimal(10),
   ENDDEVICE_ID decimal(10),
   LOCATION_ID decimal(10),
   METER_ID decimal(10),
   MODEM_ID decimal(10),
   SUPPLIER_ID decimal(10) NOT NULL,
   CONSTRAINT SYS_C004584 PRIMARY KEY (YYYYMMDDHHMMSS,DST,MDEV_ID,MDEV_TYPE) using index tablespace BMT3IDX
)
partition by range (yyyymmddhhmmss)
(
   partition mdataem2014100600 values less than ('20141006120000') tablespace AIMIRDAT33201410
)
;
ALTER TABLE "BMT3"."METERINGDATA_EM"
ADD CONSTRAINT FK14F737848F8DBE27
FOREIGN KEY (LOCATION_ID)
REFERENCES "BMT3"."LOCATION"(ID)
;
ALTER TABLE "BMT3"."METERINGDATA_EM"
ADD CONSTRAINT FK14F73784CAB63A94
FOREIGN KEY (METER_ID)
REFERENCES "BMT3"."METER"(ID)
;
ALTER TABLE "BMT3"."METERINGDATA_EM"
ADD CONSTRAINT FK14F73784D6C540B4
FOREIGN KEY (ENDDEVICE_ID)
REFERENCES "BMT3"."ENDDEVICE"(ID)
;
ALTER TABLE "BMT3"."METERINGDATA_EM"
ADD CONSTRAINT FK14F737847D01F2C7
FOREIGN KEY (SUPPLIER_ID)
REFERENCES "BMT3"."SUPPLIER"(ID)
;
ALTER TABLE "BMT3"."METERINGDATA_EM"
ADD CONSTRAINT FK14F737845CF66507
FOREIGN KEY (CONTRACT_ID)
REFERENCES "BMT3"."CONTRACT"(ID)
;
ALTER TABLE "BMT3"."METERINGDATA_EM"
ADD CONSTRAINT FK14F73784C0690AF4
FOREIGN KEY (MODEM_ID)
REFERENCES "BMT3"."MODEM"(ID)
;
CREATE UNIQUE INDEX SYS_C004584 ON "AIMIR"."METERINGDATA_EM"
(
  YYYYMMDDHHMMSS,
  DST,
  MDEV_ID,
  MDEV_TYPE
)
;
CREATE INDEX IDX_METERINGDATA_EM_01 ON "BMT3"."METERINGDATA_EM"
(
  YYYYMMDDHHMMSS,
  MDEV_TYPE,
  LOCATION_ID,
  MDEV_ID
) tablespace BMT3IDX
;

alter table "AIMIR"."METERINGDATA_EM" add partition mdataem2014100612 values less than ('20141006180000') tablespace AIMIRDAT33201410;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem2014100618 values less than ('20141007000000') tablespace AIMIRDAT33201410;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem2014100700 values less than ('20141007060000') tablespace AIMIRDAT33201410;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem2014100706 values less than ('20141007120000') tablespace AIMIRDAT33201410;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem2014100712 values less than ('20141007180000') tablespace AIMIRDAT33201410;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem2014100718 values less than ('20141008000000') tablespace AIMIRDAT33201410;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem2014100800 values less than ('20141008060000') tablespace AIMIRDAT33201410;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem2014100806 values less than ('20141008120000') tablespace AIMIRDAT33201410;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem2014100812 values less than ('20141008180000') tablespace AIMIRDAT33201410;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem2014100818 values less than ('20141009000000') tablespace AIMIRDAT33201410;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem2014100900 values less than ('20141009060000') tablespace AIMIRDAT33201410;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem2014100906 values less than ('20141009120000') tablespace AIMIRDAT33201410;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem2014100912 values less than ('20141009180000') tablespace AIMIRDAT33201410;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem2014100918 values less than ('20141010000000') tablespace AIMIRDAT33201410;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem2014101000 values less than ('20141010060000') tablespace AIMIRDAT33201410;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem2014101006 values less than ('20141010120000') tablespace AIMIRDAT33201410;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem2014101012 values less than ('20141010180000') tablespace AIMIRDAT33201410;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem2014101018 values less than ('20141011000000') tablespace AIMIRDAT33201410;

alter table "AIMIR"."METERINGDATA_EM" add partition mdataem2014100622 values less than ('20150901000000') tablespace AIMIRDAT201508;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem2014100623 values less than ('20151001000000') tablespace AIMIRDAT201509;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem2014100700 values less than ('20151101000000') tablespace AIMIRDAT201510;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem2014100701 values less than ('20151201000000') tablespace AIMIRDAT201511;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem2014100702 values less than ('20160101000000') tablespace AIMIRDAT201512;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem2014100703 values less than ('20160201000000') tablespace AIMIRDAT201601;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem2014100704 values less than ('20160301000000') tablespace AIMIRDAT201602;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem2014100705 values less than ('20160401000000') tablespace AIMIRDAT201603;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem2014100706 values less than ('20160501000000') tablespace AIMIRDAT201604;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem2014100707 values less than ('20160601000000') tablespace AIMIRDAT201605;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem2014100708 values less than ('20160701000000') tablespace AIMIRDAT201606;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem2014100709 values less than ('20160801000000') tablespace AIMIRDAT201607;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem2014100710 values less than ('20160901000000') tablespace AIMIRDAT201608;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem2014100711 values less than ('20161001000000') tablespace AIMIRDAT201609;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem2014100712 values less than ('20161101000000') tablespace AIMIRDAT201610;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem2014100713 values less than ('20170101000000') tablespace AIMIRDAT201612;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem2014100714 values less than ('20170201000000') tablespace AIMIRDAT201701;


alter table "AIMIR"."METERINGDATA_EM" add partition mdataem201702 values less than ('20170301000000') tablespace AIMIRDAT201702;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem201703 values less than ('20170401000000') tablespace AIMIRDAT201703;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem201704 values less than ('20170501000000') tablespace AIMIRDAT201704;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem201705 values less than ('20170601000000') tablespace AIMIRDAT201705;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem201706 values less than ('20170701000000') tablespace AIMIRDAT201706;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem201707 values less than ('20170801000000') tablespace AIMIRDAT201707;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem201708 values less than ('20170901000000') tablespace AIMIRDAT201708;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem201709 values less than ('20171001000000') tablespace AIMIRDAT201709;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem201710 values less than ('20171101000000') tablespace AIMIRDAT201710;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem201711 values less than ('20171201000000') tablespace AIMIRDAT201711;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem201712 values less than ('20180101000000') tablespace AIMIRDAT201712;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem201801 values less than ('20180201000000') tablespace AIMIRDAT201801;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem201802 values less than ('20180301000000') tablespace AIMIRDAT201802;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem201803 values less than ('20180401000000') tablespace AIMIRDAT201803;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem201804 values less than ('20180501000000') tablespace AIMIRDAT201804;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem201805 values less than ('20180601000000') tablespace AIMIRDAT201805;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem201806 values less than ('20180701000000') tablespace AIMIRDAT201806;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem201807 values less than ('20180801000000') tablespace AIMIRDAT201807;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem201808 values less than ('20180901000000') tablespace AIMIRDAT201808;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem201809 values less than ('20181001000000') tablespace AIMIRDAT201809;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem201810 values less than ('20181101000000') tablespace AIMIRDAT201810;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem201811 values less than ('20181201000000') tablespace AIMIRDAT201811;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem201812 values less than ('20190101000000') tablespace AIMIRDAT201812;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem201901 values less than ('20190201000000') tablespace AIMIRDAT201901;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem201902 values less than ('20190301000000') tablespace AIMIRDAT201902;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem201903 values less than ('20190401000000') tablespace AIMIRDAT201903;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem201904 values less than ('20190501000000') tablespace AIMIRDAT201904;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem201905 values less than ('20190601000000') tablespace AIMIRDAT201905;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem201906 values less than ('20190701000000') tablespace AIMIRDAT201906;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem201907 values less than ('20190801000000') tablespace AIMIRDAT201907;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem201908 values less than ('20190901000000') tablespace AIMIRDAT201908;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem201909 values less than ('20191001000000') tablespace AIMIRDAT201909;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem201910 values less than ('20191101000000') tablespace AIMIRDAT201910;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem201911 values less than ('20191201000000') tablespace AIMIRDAT201911;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem201912 values less than ('20200101000000') tablespace AIMIRDAT201912;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem202001 values less than ('20200201000000') tablespace AIMIRDAT202001;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem202002 values less than ('20200301000000') tablespace AIMIRDAT202002;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem202003 values less than ('20200401000000') tablespace AIMIRDAT202003;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem202004 values less than ('20200501000000') tablespace AIMIRDAT202004;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem202005 values less than ('20200601000000') tablespace AIMIRDAT202005;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem202006 values less than ('20200701000000') tablespace AIMIRDAT202006;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem202007 values less than ('20200801000000') tablespace AIMIRDAT202007;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem202008 values less than ('20200901000000') tablespace AIMIRDAT202008;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem202009 values less than ('20201001000000') tablespace AIMIRDAT202009;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem202010 values less than ('20201101000000') tablespace AIMIRDAT202010;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem202011 values less than ('20201201000000') tablespace AIMIRDAT202011;
alter table "AIMIR"."METERINGDATA_EM" add partition mdataem202012 values less than ('20210101000000') tablespace AIMIRDAT202012;