drop table "BMT3"."COMMLOG"  CASCADE CONSTRAINTS;

CREATE TABLE "BMT3"."COMMLOG"
(
   ID decimal(19) PRIMARY KEY NOT NULL,
   COMM_RESULT decimal(10),
   DESCR varchar2(255),
   END_TIME varchar2(14),
   ERROR_MEASUMENT_DATA_CNT decimal(10),
   ERROR_REASON varchar2(255),
   IDX1 varchar2(255),
   INTERFACE_CODE decimal(10),
   IS_COMPRESSED decimal(10),
   LOCATION_ID decimal(10),
   OPERATION_CODE varchar2(255),
   PROTOCOL_CODE decimal(10),
   RCV_BYTES decimal(10),
   RECEIVER varchar2(255),
   RECEIVER_ID varchar2(255),
   RECEIVER_IP varchar2(255),
   RECEIVER_PORT varchar2(255),
   RECEIVER_TYPE_CODE decimal(10),
   RESULT varchar2(255),
   SEND_BYTES decimal(10),
   SENDER varchar2(255),
   SENDER_ID varchar2(255),
   SENDER_IP varchar2(255),
   SENDER_LOCATION decimal(10),
   SENDER_PORT varchar2(255),
   SENDER_TYPE_CODE decimal(10),
   START_DATE varchar2(8),
   START_DATE_TIME varchar2(14),
   START_TIME varchar2(6),
   STRRECEIVERBYTES varchar2(255),
   STRSENDBYTES varchar2(255),
   STRTOTALCOMMTIME varchar2(255),
   SUCC_MEASUMENT_DATA_CNT decimal(10),
   SUPPLIERED_ID varchar2(255),
   SVC_TYPE_CODE decimal(10),
   TIME varchar2(255),
   TOTAL_COMM_TIME decimal(10),
   TOTAL_MEASUMENT_DATA_CNT decimal(10),
   UNCOM_PRESSED_SEND_BYTES decimal(10),
   UNCON_PRESSED_RCV_BYTES decimal(10)
)
partition by range (start_date_time)
(
   partition commlog201408 values less than ('20140901000000') tablespace BMT3DAT201408
)
;
ALTER TABLE "BMT3"."COMMLOG"
ADD CONSTRAINT FK6371949823B8EDAD
FOREIGN KEY (SVC_TYPE_CODE)
REFERENCES "BMT3"."CODE"(ID)
;
ALTER TABLE "BMT3"."COMMLOG"
ADD CONSTRAINT FK63719498D4C9327C
FOREIGN KEY (RECEIVER_TYPE_CODE)
REFERENCES "BMT3"."CODE"(ID)
;
ALTER TABLE "BMT3"."COMMLOG"
ADD CONSTRAINT FK637194984283982E
FOREIGN KEY (PROTOCOL_CODE)
REFERENCES "BMT3"."CODE"(ID)
;
ALTER TABLE "BMT3"."COMMLOG"
ADD CONSTRAINT FK637194988F8DBE27
FOREIGN KEY (LOCATION_ID)
REFERENCES "BMT3"."LOCATION"(ID)
;
ALTER TABLE "BMT3"."COMMLOG"
ADD CONSTRAINT FK63719498D26FB62D
FOREIGN KEY (INTERFACE_CODE)
REFERENCES "BMT3"."CODE"(ID)
;
ALTER TABLE "BMT3"."COMMLOG"
ADD CONSTRAINT FK63719498360A4342
FOREIGN KEY (SENDER_TYPE_CODE)
REFERENCES "BMT3"."CODE"(ID)
;
ALTER TABLE "BMT3"."COMMLOG"
ADD CONSTRAINT FK63719498640866C1
FOREIGN KEY (SENDER_LOCATION)
REFERENCES "BMT3"."LOCATION"(ID)
;
CREATE INDEX IDX_COMMLOG_01 ON "BMT3"."COMMLOG"
(
  SUPPLIERED_ID,
  SVC_TYPE_CODE,
  START_DATE,
  SENDER_ID,
  LOCATION_ID
) tablespace BMT3IDX
;
CREATE UNIQUE INDEX SYS_C004207 ON "BMT3"."COMMLOG"(ID) using index tablespace BMT3IDX
;
CREATE INDEX IDX_COMMLOG_02 ON "BMT3"."COMMLOG" 
(
  SUPPLIERED_ID,
  START_DATE,
  SENDER_ID,
  LOCATION_ID
) tablespace BMT3IDX
;

alter table "BMT3"."COMMLOG" add partition commlog201409 values less than ('20141001000000') tablespace BMT3DAT201409;
alter table "BMT3"."COMMLOG" add partition commlog201410 values less than ('20141101000000') tablespace BMT3DAT201410;
alter table "BMT3"."COMMLOG" add partition commlog201411 values less than ('20141201000000') tablespace BMT3DAT201411;
alter table "BMT3"."COMMLOG" add partition commlog201412 values less than ('20150101000000') tablespace BMT3DAT201412;
alter table "BMT3"."COMMLOG" add partition commlog201501 values less than ('20150201000000') tablespace BMT3DAT201501;
alter table "BMT3"."COMMLOG" add partition commlog201502 values less than ('20150301000000') tablespace BMT3DAT201502;
alter table "BMT3"."COMMLOG" add partition commlog201503 values less than ('20150401000000') tablespace BMT3DAT201503;
alter table "BMT3"."COMMLOG" add partition commlog201504 values less than ('20150501000000') tablespace BMT3DAT201504;
alter table "BMT3"."COMMLOG" add partition commlog201505 values less than ('20150601000000') tablespace BMT3DAT201505;
alter table "BMT3"."COMMLOG" add partition commlog201506 values less than ('20150701000000') tablespace BMT3DAT201506;
alter table "BMT3"."COMMLOG" add partition commlog201507 values less than ('20150801000000') tablespace BMT3DAT201507;
alter table "BMT3"."COMMLOG" add partition commlog201508 values less than ('20150901000000') tablespace BMT3DAT201508;
alter table "BMT3"."COMMLOG" add partition commlog201509 values less than ('20151001000000') tablespace BMT3DAT201509;
alter table "BMT3"."COMMLOG" add partition commlog201510 values less than ('20151101000000') tablespace BMT3DAT201510;
alter table "BMT3"."COMMLOG" add partition commlog201511 values less than ('20151201000000') tablespace BMT3DAT201511;
alter table "BMT3"."COMMLOG" add partition commlog201512 values less than ('20160101000000') tablespace BMT3DAT201512;
alter table "BMT3"."COMMLOG" add partition commlog201601 values less than ('20160201000000') tablespace BMT3DAT201601;
alter table "BMT3"."COMMLOG" add partition commlog201602 values less than ('20160301000000') tablespace BMT3DAT201602;
alter table "BMT3"."COMMLOG" add partition commlog201603 values less than ('20160401000000') tablespace BMT3DAT201603;
alter table "BMT3"."COMMLOG" add partition commlog201604 values less than ('20160501000000') tablespace BMT3DAT201604;
alter table "BMT3"."COMMLOG" add partition commlog201605 values less than ('20160601000000') tablespace BMT3DAT201605;
alter table "BMT3"."COMMLOG" add partition commlog201606 values less than ('20160701000000') tablespace BMT3DAT201606;
alter table "BMT3"."COMMLOG" add partition commlog201607 values less than ('20160801000000') tablespace BMT3DAT201607;
alter table "BMT3"."COMMLOG" add partition commlog201608 values less than ('20160901000000') tablespace BMT3DAT201608;
alter table "BMT3"."COMMLOG" add partition commlog201609 values less than ('20161001000000') tablespace BMT3DAT201609;
alter table "BMT3"."COMMLOG" add partition commlog201610 values less than ('20161101000000') tablespace BMT3DAT201610;
alter table "BMT3"."COMMLOG" add partition commlog201612 values less than ('20170101000000') tablespace BMT3DAT201612;
alter table "BMT3"."COMMLOG" add partition commlog201701 values less than ('20170201000000') tablespace BMT3DAT201701;
alter table "BMT3"."COMMLOG" add partition commlog201702 values less than ('20170301000000') tablespace BMT3DAT201702;
alter table "BMT3"."COMMLOG" add partition commlog201703 values less than ('20170401000000') tablespace BMT3DAT201703;
alter table "BMT3"."COMMLOG" add partition commlog201704 values less than ('20170501000000') tablespace BMT3DAT201704;
alter table "BMT3"."COMMLOG" add partition commlog201705 values less than ('20170601000000') tablespace BMT3DAT201705;
alter table "BMT3"."COMMLOG" add partition commlog201706 values less than ('20170701000000') tablespace BMT3DAT201706;
alter table "BMT3"."COMMLOG" add partition commlog201707 values less than ('20170801000000') tablespace BMT3DAT201707;
alter table "BMT3"."COMMLOG" add partition commlog201708 values less than ('20170901000000') tablespace BMT3DAT201708;
alter table "BMT3"."COMMLOG" add partition commlog201709 values less than ('20171001000000') tablespace BMT3DAT201709;
alter table "BMT3"."COMMLOG" add partition commlog201710 values less than ('20171101000000') tablespace BMT3DAT201710;
alter table "BMT3"."COMMLOG" add partition commlog201711 values less than ('20171201000000') tablespace BMT3DAT201711;
alter table "BMT3"."COMMLOG" add partition commlog201712 values less than ('20180101000000') tablespace BMT3DAT201712;
alter table "BMT3"."COMMLOG" add partition commlog201801 values less than ('20180201000000') tablespace BMT3DAT201801;
alter table "BMT3"."COMMLOG" add partition commlog201802 values less than ('20180301000000') tablespace BMT3DAT201802;
alter table "BMT3"."COMMLOG" add partition commlog201803 values less than ('20180401000000') tablespace BMT3DAT201803;
alter table "BMT3"."COMMLOG" add partition commlog201804 values less than ('20180501000000') tablespace BMT3DAT201804;
alter table "BMT3"."COMMLOG" add partition commlog201805 values less than ('20180601000000') tablespace BMT3DAT201805;
alter table "BMT3"."COMMLOG" add partition commlog201806 values less than ('20180701000000') tablespace BMT3DAT201806;
alter table "BMT3"."COMMLOG" add partition commlog201807 values less than ('20180801000000') tablespace BMT3DAT201807;
alter table "BMT3"."COMMLOG" add partition commlog201808 values less than ('20180901000000') tablespace BMT3DAT201808;
alter table "BMT3"."COMMLOG" add partition commlog201809 values less than ('20181001000000') tablespace BMT3DAT201809;
alter table "BMT3"."COMMLOG" add partition commlog201810 values less than ('20181101000000') tablespace BMT3DAT201810;
alter table "BMT3"."COMMLOG" add partition commlog201811 values less than ('20181201000000') tablespace BMT3DAT201811;
alter table "BMT3"."COMMLOG" add partition commlog201812 values less than ('20190101000000') tablespace BMT3DAT201812;
alter table "BMT3"."COMMLOG" add partition commlog201901 values less than ('20190201000000') tablespace BMT3DAT201901;
alter table "BMT3"."COMMLOG" add partition commlog201902 values less than ('20190301000000') tablespace BMT3DAT201902;
alter table "BMT3"."COMMLOG" add partition commlog201903 values less than ('20190401000000') tablespace BMT3DAT201903;
alter table "BMT3"."COMMLOG" add partition commlog201904 values less than ('20190501000000') tablespace BMT3DAT201904;
alter table "BMT3"."COMMLOG" add partition commlog201905 values less than ('20190601000000') tablespace BMT3DAT201905;
alter table "BMT3"."COMMLOG" add partition commlog201906 values less than ('20190701000000') tablespace BMT3DAT201906;
alter table "BMT3"."COMMLOG" add partition commlog201907 values less than ('20190801000000') tablespace BMT3DAT201907;
alter table "BMT3"."COMMLOG" add partition commlog201908 values less than ('20190901000000') tablespace BMT3DAT201908;
alter table "BMT3"."COMMLOG" add partition commlog201909 values less than ('20191001000000') tablespace BMT3DAT201909;
alter table "BMT3"."COMMLOG" add partition commlog201910 values less than ('20191101000000') tablespace BMT3DAT201910;
alter table "BMT3"."COMMLOG" add partition commlog201911 values less than ('20191201000000') tablespace BMT3DAT201911;
alter table "BMT3"."COMMLOG" add partition commlog201912 values less than ('20200101000000') tablespace BMT3DAT201912;
alter table "BMT3"."COMMLOG" add partition commlog202001 values less than ('20200201000000') tablespace BMT3DAT202001;
alter table "BMT3"."COMMLOG" add partition commlog202002 values less than ('20200301000000') tablespace BMT3DAT202002;
alter table "BMT3"."COMMLOG" add partition commlog202003 values less than ('20200401000000') tablespace BMT3DAT202003;
alter table "BMT3"."COMMLOG" add partition commlog202004 values less than ('20200501000000') tablespace BMT3DAT202004;
alter table "BMT3"."COMMLOG" add partition commlog202005 values less than ('20200601000000') tablespace BMT3DAT202005;
alter table "BMT3"."COMMLOG" add partition commlog202006 values less than ('20200701000000') tablespace BMT3DAT202006;
alter table "BMT3"."COMMLOG" add partition commlog202007 values less than ('20200801000000') tablespace BMT3DAT202007;
alter table "BMT3"."COMMLOG" add partition commlog202008 values less than ('20200901000000') tablespace BMT3DAT202008;
alter table "BMT3"."COMMLOG" add partition commlog202009 values less than ('20201001000000') tablespace BMT3DAT202009;
alter table "BMT3"."COMMLOG" add partition commlog202010 values less than ('20201101000000') tablespace BMT3DAT202010;
alter table "BMT3"."COMMLOG" add partition commlog202011 values less than ('20201201000000') tablespace BMT3DAT202011;
alter table "BMT3"."COMMLOG" add partition commlog202012 values less than ('20210101000000') tablespace BMT3DAT202012;