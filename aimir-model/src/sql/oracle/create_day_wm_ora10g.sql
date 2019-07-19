drop table "AIMIR"."DAY_WM"  CASCADE CONSTRAINTS;

CREATE TABLE "AIMIR"."DAY_WM"
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
   CONSTRAINT SYS_C008392 PRIMARY KEY (CHANNEL,YYYYMMDD,DST,MDEV_ID,MDEV_TYPE)
)
partition by range (yyyymmdd)
(
   partition daywm201305 values less than ('20130601') tablespace AMR3DAT201305
)
;
ALTER TABLE "AIMIR"."DAY_WM"
ADD CONSTRAINT FK77C766798F8DBE27
FOREIGN KEY (LOCATION_ID)
REFERENCES "AIMIR"."LOCATION"(ID)
;
ALTER TABLE "AIMIR"."DAY_WM"
ADD CONSTRAINT FK77C766795CF66507
FOREIGN KEY (CONTRACT_ID)
REFERENCES "AIMIR"."CONTRACT"(ID)
;
ALTER TABLE "AIMIR"."DAY_WM"
ADD CONSTRAINT FK77C76679CAB63A94
FOREIGN KEY (METER_ID)
REFERENCES "AIMIR"."METER"(ID)
;
ALTER TABLE "AIMIR"."DAY_WM"
ADD CONSTRAINT FK77C76679D6C540B4
FOREIGN KEY (ENDDEVICE_ID)
REFERENCES "AIMIR"."ENDDEVICE"(ID)
;
ALTER TABLE "AIMIR"."DAY_WM"
ADD CONSTRAINT FK77C76679C0690AF4
FOREIGN KEY (MODEM_ID)
REFERENCES "AIMIR"."MODEM"(ID)
;
ALTER TABLE "AIMIR"."DAY_WM"
ADD CONSTRAINT FK77C766797D01F2C7
FOREIGN KEY (SUPPLIER_ID)
REFERENCES "AIMIR"."SUPPLIER"(ID)
;
CREATE UNIQUE INDEX SYS_C008392 ON "AIMIR"."DAY_WM"
(
  CHANNEL,
  YYYYMMDD,
  DST,
  MDEV_ID,
  MDEV_TYPE
)
;
CREATE INDEX IDX_DAY_WM_01 ON "AIMIR"."DAY_WM"
(
  MDEV_TYPE,
  MDEV_ID,
  DST,
  YYYYMMDD
)
;
alter table "AIMIR"."DAY_WM" add partition daywm201306 values less than ('20130701') tablespace AMR3DAT201306;
alter table "AIMIR"."DAY_WM" add partition daywm201307 values less than ('20130801') tablespace AMR3DAT201307;
alter table "AIMIR"."DAY_WM" add partition daywm201308 values less than ('20130901') tablespace AMR3DAT201308;
alter table "AIMIR"."DAY_WM" add partition daywm201309 values less than ('20131001') tablespace AMR3DAT201309;
alter table "AIMIR"."DAY_WM" add partition daywm201310 values less than ('20131101') tablespace AMR3DAT201310;
alter table "AIMIR"."DAY_WM" add partition daywm201311 values less than ('20131201') tablespace AMR3DAT201311;
alter table "AIMIR"."DAY_WM" add partition daywm201312 values less than ('20140101') tablespace AMR3DAT201312;
alter table "AIMIR"."DAY_WM" add partition daywm201401 values less than ('20140201') tablespace AMR3DAT201401;
alter table "AIMIR"."DAY_WM" add partition daywm201402 values less than ('20140301') tablespace AMR3DAT201402;
alter table "AIMIR"."DAY_WM" add partition daywm201403 values less than ('20140401') tablespace AMR3DAT201403;
alter table "AIMIR"."DAY_WM" add partition daywm201404 values less than ('20140501') tablespace AMR3DAT201404;
alter table "AIMIR"."DAY_WM" add partition daywm201405 values less than ('20140601') tablespace AMR3DAT201405;
alter table "AIMIR"."DAY_WM" add partition daywm201406 values less than ('20140701') tablespace AMR3DAT201406;
alter table "AIMIR"."DAY_WM" add partition daywm201407 values less than ('20140801') tablespace AMR3DAT201407;
alter table "AIMIR"."DAY_WM" add partition daywm201408 values less than ('20140901') tablespace AMR3DAT201408;
alter table "AIMIR"."DAY_WM" add partition daywm201409 values less than ('20141001') tablespace AMR3DAT201409;
alter table "AIMIR"."DAY_WM" add partition daywm201410 values less than ('20141101') tablespace AMR3DAT201410;
alter table "AIMIR"."DAY_WM" add partition daywm201411 values less than ('20141201') tablespace AMR3DAT201411;
alter table "AIMIR"."DAY_WM" add partition daywm201412 values less than ('20150101') tablespace AMR3DAT201412;
alter table "AIMIR"."DAY_WM" add partition daywm201501 values less than ('20150201') tablespace AMR3DAT201501;
alter table "AIMIR"."DAY_WM" add partition daywm201502 values less than ('20150301') tablespace AMR3DAT201502;
alter table "AIMIR"."DAY_WM" add partition daywm201503 values less than ('20150401') tablespace AMR3DAT201503;
alter table "AIMIR"."DAY_WM" add partition daywm201504 values less than ('20150501') tablespace AMR3DAT201504;
alter table "AIMIR"."DAY_WM" add partition daywm201505 values less than ('20150601') tablespace AMR3DAT201505;
alter table "AIMIR"."DAY_WM" add partition daywm201506 values less than ('20150701') tablespace AMR3DAT201506;
alter table "AIMIR"."DAY_WM" add partition daywm201507 values less than ('20150801') tablespace AMR3DAT201507;
alter table "AIMIR"."DAY_WM" add partition daywm201508 values less than ('20150901') tablespace AMR3DAT201508;
alter table "AIMIR"."DAY_WM" add partition daywm201509 values less than ('20151001') tablespace AMR3DAT201509;
alter table "AIMIR"."DAY_WM" add partition daywm201510 values less than ('20151101') tablespace AMR3DAT201510;
alter table "AIMIR"."DAY_WM" add partition daywm201511 values less than ('20151201') tablespace AMR3DAT201511;
alter table "AIMIR"."DAY_WM" add partition daywm201512 values less than ('20160101') tablespace AMR3DAT201512;
alter table "AIMIR"."DAY_WM" add partition daywm201601 values less than ('20160201') tablespace AMR3DAT201601;
alter table "AIMIR"."DAY_WM" add partition daywm201602 values less than ('20160301') tablespace AMR3DAT201602;
alter table "AIMIR"."DAY_WM" add partition daywm201603 values less than ('20160401') tablespace AMR3DAT201603;
alter table "AIMIR"."DAY_WM" add partition daywm201604 values less than ('20160501') tablespace AMR3DAT201604;
alter table "AIMIR"."DAY_WM" add partition daywm201605 values less than ('20160601') tablespace AMR3DAT201605;
alter table "AIMIR"."DAY_WM" add partition daywm201606 values less than ('20160701') tablespace AMR3DAT201606;
alter table "AIMIR"."DAY_WM" add partition daywm201607 values less than ('20160801') tablespace AMR3DAT201607;
alter table "AIMIR"."DAY_WM" add partition daywm201608 values less than ('20160901') tablespace AMR3DAT201608;
alter table "AIMIR"."DAY_WM" add partition daywm201609 values less than ('20161001') tablespace AMR3DAT201609;
alter table "AIMIR"."DAY_WM" add partition daywm201610 values less than ('20161101') tablespace AMR3DAT201610;
alter table "AIMIR"."DAY_WM" add partition daywm201612 values less than ('20170101') tablespace AMR3DAT201612;
alter table "AIMIR"."DAY_WM" add partition daywm201701 values less than ('20170201') tablespace AMR3DAT201701;
alter table "AIMIR"."DAY_WM" add partition daywm201702 values less than ('20170301') tablespace AMR3DAT201702;
alter table "AIMIR"."DAY_WM" add partition daywm201703 values less than ('20170401') tablespace AMR3DAT201703;
alter table "AIMIR"."DAY_WM" add partition daywm201704 values less than ('20170501') tablespace AMR3DAT201704;
alter table "AIMIR"."DAY_WM" add partition daywm201705 values less than ('20170601') tablespace AMR3DAT201705;
alter table "AIMIR"."DAY_WM" add partition daywm201706 values less than ('20170701') tablespace AMR3DAT201706;
alter table "AIMIR"."DAY_WM" add partition daywm201707 values less than ('20170801') tablespace AMR3DAT201707;
alter table "AIMIR"."DAY_WM" add partition daywm201708 values less than ('20170901') tablespace AMR3DAT201708;
alter table "AIMIR"."DAY_WM" add partition daywm201709 values less than ('20171001') tablespace AMR3DAT201709;
alter table "AIMIR"."DAY_WM" add partition daywm201710 values less than ('20171101') tablespace AMR3DAT201710;
alter table "AIMIR"."DAY_WM" add partition daywm201711 values less than ('20171201') tablespace AMR3DAT201711;
alter table "AIMIR"."DAY_WM" add partition daywm201712 values less than ('20180101') tablespace AMR3DAT201712;
alter table "AIMIR"."DAY_WM" add partition daywm201801 values less than ('20180201') tablespace AMR3DAT201801;
alter table "AIMIR"."DAY_WM" add partition daywm201802 values less than ('20180301') tablespace AMR3DAT201802;
alter table "AIMIR"."DAY_WM" add partition daywm201803 values less than ('20180401') tablespace AMR3DAT201803;
alter table "AIMIR"."DAY_WM" add partition daywm201804 values less than ('20180501') tablespace AMR3DAT201804;
alter table "AIMIR"."DAY_WM" add partition daywm201805 values less than ('20180601') tablespace AMR3DAT201805;
alter table "AIMIR"."DAY_WM" add partition daywm201806 values less than ('20180701') tablespace AMR3DAT201806;
alter table "AIMIR"."DAY_WM" add partition daywm201807 values less than ('20180801') tablespace AMR3DAT201807;
alter table "AIMIR"."DAY_WM" add partition daywm201808 values less than ('20180901') tablespace AMR3DAT201808;
alter table "AIMIR"."DAY_WM" add partition daywm201809 values less than ('20181001') tablespace AMR3DAT201809;
alter table "AIMIR"."DAY_WM" add partition daywm201810 values less than ('20181101') tablespace AMR3DAT201810;
alter table "AIMIR"."DAY_WM" add partition daywm201811 values less than ('20181201') tablespace AMR3DAT201811;
alter table "AIMIR"."DAY_WM" add partition daywm201812 values less than ('20190101') tablespace AMR3DAT201812;
alter table "AIMIR"."DAY_WM" add partition daywm201901 values less than ('20190201') tablespace AMR3DAT201901;
alter table "AIMIR"."DAY_WM" add partition daywm201902 values less than ('20190301') tablespace AMR3DAT201902;
alter table "AIMIR"."DAY_WM" add partition daywm201903 values less than ('20190401') tablespace AMR3DAT201903;
alter table "AIMIR"."DAY_WM" add partition daywm201904 values less than ('20190501') tablespace AMR3DAT201904;
alter table "AIMIR"."DAY_WM" add partition daywm201905 values less than ('20190601') tablespace AMR3DAT201905;
alter table "AIMIR"."DAY_WM" add partition daywm201906 values less than ('20190701') tablespace AMR3DAT201906;
alter table "AIMIR"."DAY_WM" add partition daywm201907 values less than ('20190801') tablespace AMR3DAT201907;
alter table "AIMIR"."DAY_WM" add partition daywm201908 values less than ('20190901') tablespace AMR3DAT201908;
alter table "AIMIR"."DAY_WM" add partition daywm201909 values less than ('20191001') tablespace AMR3DAT201909;
alter table "AIMIR"."DAY_WM" add partition daywm201910 values less than ('20191101') tablespace AMR3DAT201910;
alter table "AIMIR"."DAY_WM" add partition daywm201911 values less than ('20191201') tablespace AMR3DAT201911;
alter table "AIMIR"."DAY_WM" add partition daywm201912 values less than ('20200101') tablespace AMR3DAT201912;
alter table "AIMIR"."DAY_WM" add partition daywm202001 values less than ('20200201') tablespace AMR3DAT202001;
alter table "AIMIR"."DAY_WM" add partition daywm202002 values less than ('20200301') tablespace AMR3DAT202002;
alter table "AIMIR"."DAY_WM" add partition daywm202003 values less than ('20200401') tablespace AMR3DAT202003;
alter table "AIMIR"."DAY_WM" add partition daywm202004 values less than ('20200501') tablespace AMR3DAT202004;
alter table "AIMIR"."DAY_WM" add partition daywm202005 values less than ('20200601') tablespace AMR3DAT202005;
alter table "AIMIR"."DAY_WM" add partition daywm202006 values less than ('20200701') tablespace AMR3DAT202006;
alter table "AIMIR"."DAY_WM" add partition daywm202007 values less than ('20200801') tablespace AMR3DAT202007;
alter table "AIMIR"."DAY_WM" add partition daywm202008 values less than ('20200901') tablespace AMR3DAT202008;
alter table "AIMIR"."DAY_WM" add partition daywm202009 values less than ('20201001') tablespace AMR3DAT202009;
alter table "AIMIR"."DAY_WM" add partition daywm202010 values less than ('20201101') tablespace AMR3DAT202010;
alter table "AIMIR"."DAY_WM" add partition daywm202011 values less than ('20201201') tablespace AMR3DAT202011;
alter table "AIMIR"."DAY_WM" add partition daywm202012 values less than ('20210101') tablespace AMR3DAT202012;