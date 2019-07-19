drop table "BMT3"."EVENTALERTLOG"  CASCADE CONSTRAINTS;

CREATE TABLE "BMT3"."EVENTALERTLOG"
(
   ID decimal(19) PRIMARY KEY NOT NULL,
   ACTIVATORID varchar2(100) NOT NULL,
   ACTIVATORIP varchar2(100),
   ACTIVATOR_TYPE varchar2(255) NOT NULL,
   CLOSETIME varchar2(14),
   DURATION varchar2(255),
   EVENTALERT_ID decimal(10),
   LOCATION_ID decimal(10),
   MESSAGE varchar2(255),
   OCCURCNT decimal(10),
   OPENTIME varchar2(14) NOT NULL,
   SEVERITY_TYPE varchar2(255) NOT NULL,
   STATUS varchar2(255) NOT NULL,
   SUPPLIER_ID decimal(10),
   WRITETIME varchar2(14) NOT NULL
)
partition by range (OPENTIME)
(
   partition eventalertlog201408 values less than (20140901000000) tablespace BMT3DAT201408

)
;
ALTER TABLE "BMT3"."EVENTALERTLOG"
ADD CONSTRAINT FKA4669CE28F8DBE27
FOREIGN KEY (LOCATION_ID)
REFERENCES "BMT3"."LOCATION"(ID)
;
ALTER TABLE "BMT3"."EVENTALERTLOG"
ADD CONSTRAINT FKA4669CE2FB138520
FOREIGN KEY (EVENTALERT_ID)
REFERENCES "BMT3"."EVENTALERT"(ID)
;
ALTER TABLE "BMT3"."EVENTALERTLOG"
ADD CONSTRAINT FKA4669CE27D01F2C7
FOREIGN KEY (SUPPLIER_ID)
REFERENCES "BMT3"."SUPPLIER"(ID)
;
CREATE INDEX IDX_EVENTALERTLOG_01 ON "BMT3"."EVENTALERTLOG"
(
  ACTIVATOR_TYPE,
  ACTIVATORID,
  EVENTALERT_ID
) tablespace BMT3IDX
;
CREATE INDEX IDX_OPENTIME ON "BMT3"."EVENTALERTLOG"(OPENTIME) tablespace BMT3IDX
;
CREATE UNIQUE INDEX SYS_C004372 ON "BMT3"."EVENTALERTLOG"(ID)
;

alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201409 values less than ('20141001000000') tablespace BMT3DAT201409;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201410 values less than ('20141101000000') tablespace BMT3DAT201410;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201411 values less than ('20141201000000') tablespace BMT3DAT201411;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201412 values less than ('20150101000000') tablespace BMT3DAT201412;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201501 values less than ('20150201000000') tablespace BMT3DAT201501;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201502 values less than ('20150301000000') tablespace BMT3DAT201502;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201503 values less than ('20150401000000') tablespace BMT3DAT201503;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201504 values less than ('20150501000000') tablespace BMT3DAT201504;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201505 values less than ('20150601000000') tablespace BMT3DAT201505;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201506 values less than ('20150701000000') tablespace BMT3DAT201506;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201507 values less than ('20150801000000') tablespace BMT3DAT201507;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201508 values less than ('20150901000000') tablespace BMT3DAT201508;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201509 values less than ('20151001000000') tablespace BMT3DAT201509;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201510 values less than ('20151101000000') tablespace BMT3DAT201510;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201511 values less than ('20151201000000') tablespace BMT3DAT201511;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201512 values less than ('20160101000000') tablespace BMT3DAT201512;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201601 values less than ('20160201000000') tablespace BMT3DAT201601;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201602 values less than ('20160301000000') tablespace BMT3DAT201602;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201603 values less than ('20160401000000') tablespace BMT3DAT201603;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201604 values less than ('20160501000000') tablespace BMT3DAT201604;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201605 values less than ('20160601000000') tablespace BMT3DAT201605;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201606 values less than ('20160701000000') tablespace BMT3DAT201606;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201607 values less than ('20160801000000') tablespace BMT3DAT201607;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201608 values less than ('20160901000000') tablespace BMT3DAT201608;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201609 values less than ('20161001000000') tablespace BMT3DAT201609;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201610 values less than ('20161101000000') tablespace BMT3DAT201610;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201612 values less than ('20170101000000') tablespace BMT3DAT201612;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201701 values less than ('20170201000000') tablespace BMT3DAT201701;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201702 values less than ('20170301000000') tablespace BMT3DAT201702;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201703 values less than ('20170401000000') tablespace BMT3DAT201703;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201704 values less than ('20170501000000') tablespace BMT3DAT201704;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201705 values less than ('20170601000000') tablespace BMT3DAT201705;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201706 values less than ('20170701000000') tablespace BMT3DAT201706;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201707 values less than ('20170801000000') tablespace BMT3DAT201707;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201708 values less than ('20170901000000') tablespace BMT3DAT201708;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201709 values less than ('20171001000000') tablespace BMT3DAT201709;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201710 values less than ('20171101000000') tablespace BMT3DAT201710;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201711 values less than ('20171201000000') tablespace BMT3DAT201711;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201712 values less than ('20180101000000') tablespace BMT3DAT201712;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201801 values less than ('20180201000000') tablespace BMT3DAT201801;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201802 values less than ('20180301000000') tablespace BMT3DAT201802;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201803 values less than ('20180401000000') tablespace BMT3DAT201803;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201804 values less than ('20180501000000') tablespace BMT3DAT201804;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201805 values less than ('20180601000000') tablespace BMT3DAT201805;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201806 values less than ('20180701000000') tablespace BMT3DAT201806;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201807 values less than ('20180801000000') tablespace BMT3DAT201807;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201808 values less than ('20180901000000') tablespace BMT3DAT201808;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201809 values less than ('20181001000000') tablespace BMT3DAT201809;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201810 values less than ('20181101000000') tablespace BMT3DAT201810;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201811 values less than ('20181201000000') tablespace BMT3DAT201811;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201812 values less than ('20190101000000') tablespace BMT3DAT201812;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201901 values less than ('20190201000000') tablespace BMT3DAT201901;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201902 values less than ('20190301000000') tablespace BMT3DAT201902;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201903 values less than ('20190401000000') tablespace BMT3DAT201903;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201904 values less than ('20190501000000') tablespace BMT3DAT201904;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201905 values less than ('20190601000000') tablespace BMT3DAT201905;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201906 values less than ('20190701000000') tablespace BMT3DAT201906;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201907 values less than ('20190801000000') tablespace BMT3DAT201907;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201908 values less than ('20190901000000') tablespace BMT3DAT201908;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201909 values less than ('20191001000000') tablespace BMT3DAT201909;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201910 values less than ('20191101000000') tablespace BMT3DAT201910;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201911 values less than ('20191201000000') tablespace BMT3DAT201911;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog201912 values less than ('20200101000000') tablespace BMT3DAT201912;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog202001 values less than ('20200201000000') tablespace BMT3DAT202001;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog202002 values less than ('20200301000000') tablespace BMT3DAT202002;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog202003 values less than ('20200401000000') tablespace BMT3DAT202003;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog202004 values less than ('20200501000000') tablespace BMT3DAT202004;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog202005 values less than ('20200601000000') tablespace BMT3DAT202005;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog202006 values less than ('20200701000000') tablespace BMT3DAT202006;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog202007 values less than ('20200801000000') tablespace BMT3DAT202007;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog202008 values less than ('20200901000000') tablespace BMT3DAT202008;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog202009 values less than ('20201001000000') tablespace BMT3DAT202009;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog202010 values less than ('20201101000000') tablespace BMT3DAT202010;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog202011 values less than ('20201201000000') tablespace BMT3DAT202011;
alter table "BMT3"."EVENTALERTLOG" add partition eventalertlog202012 values less than ('20210101000000') tablespace BMT3DAT202012;

drop table "BMT3"."EVENTALERT_ATTR" CASCADE CONSTRAINTS;

CREATE TABLE "BMT3"."EVENTALERT_ATTR"
(
   ID decimal(19) PRIMARY KEY NOT NULL,
   ATTRNAME varchar2(255) NOT NULL,
   ATTRTYPE varchar2(255) NOT NULL,
   EVENTALERTLOG_ID decimal(19),
   OID varchar2(255),
   VALUE varchar2(255)
)
partition by range (id)
interval (1)
store in (BMT3DATHASH0001, BMT3DATHASH0002, BMT3DATHASH0003, BMT3DATHASH0004, BMT3DATHASH0005,
BMT3DATHASH0006, BMT3DATHASH0007, BMT3DATHASH0008, BMT3DATHASH0009, BMT3DATHASH0010) 
(
   partition eventalertlogatt01 values less than (100000000),
   partition eventalertlogatt02 values less than (200000000),
   partition eventalertlogatt03 values less than (300000000)
)
;
ALTER TABLE "BMT3"."EVENTALERT_ATTR"
ADD CONSTRAINT FK2638890E1B186DF4
FOREIGN KEY (EVENTALERTLOG_ID)
REFERENCES "BMT3"."EVENTALERTLOG"(ID)
;
CREATE UNIQUE INDEX SYS_C004377 ON "BMT3"."EVENTALERT_ATTR"(ID)
;