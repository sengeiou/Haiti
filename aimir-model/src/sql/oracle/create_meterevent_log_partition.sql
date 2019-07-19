drop table "AIMIR"."METEREVENT_LOG"  CASCADE CONSTRAINTS;

CREATE TABLE "AIMIR"."METEREVENT_LOG"
(
   ACTIVATOR_ID varchar2(100) NOT NULL,
   METEREVENT_ID varchar2(100) NOT NULL,
   OPEN_TIME varchar2(14) NOT NULL,
   ACTIVATOR_TYPE varchar2(255) NOT NULL,
   INTEGRATED decimal(22) DEFAULT 0,
   MESSAGE varchar2(255),
   SUPPLIER_ID decimal(10),
   WRITETIME varchar2(14) NOT NULL,
   YYYYMMDD varchar2(8) NOT NULL,
   CONSTRAINT SYS_C004578 PRIMARY KEY (ACTIVATOR_ID,METEREVENT_ID,OPEN_TIME)
)
partition by range (yyyymmdd)
(
   partition metereventlog201408 values less than ('20140901') tablespace AIMIRDAT201408
)
;
ALTER TABLE "AIMIR"."METEREVENT_LOG"
ADD CONSTRAINT FKC88616967D01F2C7
FOREIGN KEY (SUPPLIER_ID)
REFERENCES "AIMIR"."SUPPLIER"(ID)
;
CREATE INDEX IDX_METEREVENT_LOG_01 ON "AIMIR"."METEREVENT_LOG"
(
  METEREVENT_ID,
  ACTIVATOR_ID,
  YYYYMMDD,
  OPEN_TIME
)
;
CREATE UNIQUE INDEX SYS_C004578 ON "AIMIR"."METEREVENT_LOG"
(
  ACTIVATOR_ID,
  METEREVENT_ID,
  OPEN_TIME
)
;

alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201409 values less than ('20141001') tablespace AIMIRDAT201409;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201410 values less than ('20141101') tablespace AIMIRDAT201410;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201411 values less than ('20141201') tablespace AIMIRDAT201411;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201412 values less than ('20150101') tablespace AIMIRDAT201412;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201501 values less than ('20150201') tablespace AIMIRDAT201501;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201502 values less than ('20150301') tablespace AIMIRDAT201502;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201503 values less than ('20150401') tablespace AIMIRDAT201503;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201504 values less than ('20150501') tablespace AIMIRDAT201504;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201505 values less than ('20150601') tablespace AIMIRDAT201505;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201506 values less than ('20150701') tablespace AIMIRDAT201506;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201507 values less than ('20150801') tablespace AIMIRDAT201507;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201508 values less than ('20150901') tablespace AIMIRDAT201508;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201509 values less than ('20151001') tablespace AIMIRDAT201509;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201510 values less than ('20151101') tablespace AIMIRDAT201510;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201511 values less than ('20151201') tablespace AIMIRDAT201511;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201512 values less than ('20160101') tablespace AIMIRDAT201512;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201601 values less than ('20160201') tablespace AIMIRDAT201601;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201602 values less than ('20160301') tablespace AIMIRDAT201602;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201603 values less than ('20160401') tablespace AIMIRDAT201603;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201604 values less than ('20160501') tablespace AIMIRDAT201604;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201605 values less than ('20160601') tablespace AIMIRDAT201605;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201606 values less than ('20160701') tablespace AIMIRDAT201606;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201607 values less than ('20160801') tablespace AIMIRDAT201607;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201608 values less than ('20160901') tablespace AIMIRDAT201608;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201609 values less than ('20161001') tablespace AIMIRDAT201609;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201610 values less than ('20161101') tablespace AIMIRDAT201610;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201612 values less than ('20170101') tablespace AIMIRDAT201612;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201701 values less than ('20170201') tablespace AIMIRDAT201701;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201702 values less than ('20170301') tablespace AIMIRDAT201702;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201703 values less than ('20170401') tablespace AIMIRDAT201703;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201704 values less than ('20170501') tablespace AIMIRDAT201704;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201705 values less than ('20170601') tablespace AIMIRDAT201705;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201706 values less than ('20170701') tablespace AIMIRDAT201706;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201707 values less than ('20170801') tablespace AIMIRDAT201707;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201708 values less than ('20170901') tablespace AIMIRDAT201708;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201709 values less than ('20171001') tablespace AIMIRDAT201709;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201710 values less than ('20171101') tablespace AIMIRDAT201710;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201711 values less than ('20171201') tablespace AIMIRDAT201711;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201712 values less than ('20180101') tablespace AIMIRDAT201712;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201801 values less than ('20180201') tablespace AIMIRDAT201801;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201802 values less than ('20180301') tablespace AIMIRDAT201802;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201803 values less than ('20180401') tablespace AIMIRDAT201803;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201804 values less than ('20180501') tablespace AIMIRDAT201804;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201805 values less than ('20180601') tablespace AIMIRDAT201805;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201806 values less than ('20180701') tablespace AIMIRDAT201806;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201807 values less than ('20180801') tablespace AIMIRDAT201807;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201808 values less than ('20180901') tablespace AIMIRDAT201808;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201809 values less than ('20181001') tablespace AIMIRDAT201809;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201810 values less than ('20181101') tablespace AIMIRDAT201810;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201811 values less than ('20181201') tablespace AIMIRDAT201811;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201812 values less than ('20190101') tablespace AIMIRDAT201812;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201901 values less than ('20190201') tablespace AIMIRDAT201901;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201902 values less than ('20190301') tablespace AIMIRDAT201902;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201903 values less than ('20190401') tablespace AIMIRDAT201903;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201904 values less than ('20190501') tablespace AIMIRDAT201904;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201905 values less than ('20190601') tablespace AIMIRDAT201905;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201906 values less than ('20190701') tablespace AIMIRDAT201906;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201907 values less than ('20190801') tablespace AIMIRDAT201907;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201908 values less than ('20190901') tablespace AIMIRDAT201908;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201909 values less than ('20191001') tablespace AIMIRDAT201909;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201910 values less than ('20191101') tablespace AIMIRDAT201910;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201911 values less than ('20191201') tablespace AIMIRDAT201911;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog201912 values less than ('20200101') tablespace AIMIRDAT201912;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog202001 values less than ('20200201') tablespace AIMIRDAT202001;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog202002 values less than ('20200301') tablespace AIMIRDAT202002;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog202003 values less than ('20200401') tablespace AIMIRDAT202003;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog202004 values less than ('20200501') tablespace AIMIRDAT202004;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog202005 values less than ('20200601') tablespace AIMIRDAT202005;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog202006 values less than ('20200701') tablespace AIMIRDAT202006;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog202007 values less than ('20200801') tablespace AIMIRDAT202007;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog202008 values less than ('20200901') tablespace AIMIRDAT202008;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog202009 values less than ('20201001') tablespace AIMIRDAT202009;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog202010 values less than ('20201101') tablespace AIMIRDAT202010;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog202011 values less than ('20201201') tablespace AIMIRDAT202011;
alter table "AIMIR"."METEREVENT_LOG" add partition metereventlog202012 values less than ('20210101') tablespace AIMIRDAT202012;