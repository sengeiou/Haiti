drop table "BMT3"."LP_EM"  CASCADE CONSTRAINTS;

CREATE TABLE "BMT3"."LP_EM"
(
   CHANNEL decimal(10) NOT NULL,
   YYYYMMDDHH varchar2(10) NOT NULL,
   DST decimal(22) DEFAULT 0  NOT NULL,
   MDEV_ID varchar2(20) NOT NULL,
   MDEV_TYPE varchar2(20) NOT NULL,
   CONTRACT_ID decimal(10),
   DEVICE_ID varchar2(20),
   DEVICE_TYPE varchar2(255),
   ENDDEVICE_ID decimal(10),
   HH varchar2(2),
   LOCATION_ID decimal(10),
   METER_ID decimal(10),
   METERINGTYPE decimal(10),
   MODEM_ID decimal(10),
   SEND_RESULT varchar2(255),
   SUPPLIER_ID decimal(10),
   VALUE float(126),
   VALUE_CNT decimal(10),
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
   VALUE_24 float(126),
   VALUE_25 float(126),
   VALUE_26 float(126),
   VALUE_27 float(126),
   VALUE_28 float(126),
   VALUE_29 float(126),
   VALUE_30 float(126),
   VALUE_31 float(126),
   VALUE_32 float(126),
   VALUE_33 float(126),
   VALUE_34 float(126),
   VALUE_35 float(126),
   VALUE_36 float(126),
   VALUE_37 float(126),
   VALUE_38 float(126),
   VALUE_39 float(126),
   VALUE_40 float(126),
   VALUE_41 float(126),
   VALUE_42 float(126),
   VALUE_43 float(126),
   VALUE_44 float(126),
   VALUE_45 float(126),
   VALUE_46 float(126),
   VALUE_47 float(126),
   VALUE_48 float(126),
   VALUE_49 float(126),
   VALUE_50 float(126),
   VALUE_51 float(126),
   VALUE_52 float(126),
   VALUE_53 float(126),
   VALUE_54 float(126),
   VALUE_55 float(126),
   VALUE_56 float(126),
   VALUE_57 float(126),
   VALUE_58 float(126),
   VALUE_59 float(126),
   WRITEDATE varchar2(14),
   YYYYMMDD varchar2(8),
   CONSTRAINT SYS_C004466 PRIMARY KEY (CHANNEL,YYYYMMDDHH,DST,MDEV_ID,MDEV_TYPE) using index tablespace BMT3IDX
)
partition by HASH (channel)
PARTITIONS 7
store in (BMT3CH1, BMT3CH2, BMT3CH3, BMT3CH4, BMT3CH5, BMT3CH6, BMT3CH7) nologging
;
ALTER TABLE "BMT3"."LP_EM"
ADD CONSTRAINT FK454C5438F8DBE27
FOREIGN KEY (LOCATION_ID)
REFERENCES "BMT3"."LOCATION"(ID)
;
ALTER TABLE "BMT3"."LP_EM"
ADD CONSTRAINT FK454C5435CF66507
FOREIGN KEY (CONTRACT_ID)
REFERENCES "BMT3"."CONTRACT"(ID)
;
ALTER TABLE "BMT3"."LP_EM"
ADD CONSTRAINT FK454C5437D01F2C7
FOREIGN KEY (SUPPLIER_ID)
REFERENCES "BMT3"."SUPPLIER"(ID)
;
ALTER TABLE "BMT3"."LP_EM"
ADD CONSTRAINT FK454C543CAB63A94
FOREIGN KEY (METER_ID)
REFERENCES "BMT3"."METER"(ID)
;
ALTER TABLE "BMT3"."LP_EM"
ADD CONSTRAINT FK454C543C0690AF4
FOREIGN KEY (MODEM_ID)
REFERENCES "BMT3"."MODEM"(ID)
;
ALTER TABLE "BMT3"."LP_EM"
ADD CONSTRAINT FK454C543D6C540B4
FOREIGN KEY (ENDDEVICE_ID)
REFERENCES "BMT3"."ENDDEVICE"(ID)
;
CREATE UNIQUE INDEX SYS_C004466 ON "BMT3"."LP_EM" using index tablespace BMT3IDX
(
  CHANNEL,
  YYYYMMDDHH,
  DST,
  MDEV_ID,
  MDEV_TYPE
)
;
CREATE INDEX IDX_LP_EM_03 ON "BMT3"."LP_EM"
(
  MDEV_TYPE,
  MDEV_ID,
  YYYYMMDDHH,
  CHANNEL
) tablespace BMT3IDX nologging

;
CREATE INDEX IDX_LP_EM_02 ON "BMT3"."LP_EM"
(
  MDEV_TYPE,
  MDEV_ID,
  YYYYMMDD,
  CHANNEL
) tablespace BMT3IDX nologging
;
CREATE INDEX IDX_LP_EM_01 ON "BMT3"."LP_EM" 
(
  MDEV_TYPE,
  MDEV_ID,
  DST,
  YYYYMMDD
) tablespace BMT3IDX nologging
;

alter table "AIMIR"."LP_EM" add partition lpem2014100610 values less than ('2014100611') tablespace AIMIRDAT33201410;
alter table "AIMIR"."LP_EM" add partition lpem2014100611 values less than ('2014100612') tablespace AIMIRDAT33201410;
alter table "AIMIR"."LP_EM" add partition lpem2014100612 values less than ('2014100613') tablespace AIMIRDAT33201410;
alter table "AIMIR"."LP_EM" add partition lpem2014100613 values less than ('2014100614') tablespace AIMIRDAT33201410;
alter table "AIMIR"."LP_EM" add partition lpem2014100614 values less than ('2014100615') tablespace AIMIRDAT33201410;
alter table "AIMIR"."LP_EM" add partition lpem2014100615 values less than ('2014100616') tablespace AIMIRDAT33201410;
alter table "AIMIR"."LP_EM" add partition lpem2014100616 values less than ('2014100617') tablespace AIMIRDAT33201410;
alter table "AIMIR"."LP_EM" add partition lpem2014100617 values less than ('2014100618') tablespace AIMIRDAT33201410;
alter table "AIMIR"."LP_EM" add partition lpem2014100618 values less than ('2014100619') tablespace AIMIRDAT33201410;
alter table "AIMIR"."LP_EM" add partition lpem2014100619 values less than ('2014100620') tablespace AIMIRDAT33201410;
alter table "AIMIR"."LP_EM" add partition lpem2014100620 values less than ('2014100621') tablespace AIMIRDAT33201410;
alter table "AIMIR"."LP_EM" add partition lpem2014100621 values less than ('2014100622') tablespace AIMIRDAT33201410;
alter table "AIMIR"."LP_EM" add partition lpem2014100622 values less than ('2014100623') tablespace AIMIRDAT33201410;
alter table "AIMIR"."LP_EM" add partition lpem2014100623 values less than ('2014100700') tablespace AIMIRDAT33201410;
alter table "AIMIR"."LP_EM" add partition lpem2014100700 values less than ('2014100701') tablespace AIMIRDAT33201410;
alter table "AIMIR"."LP_EM" add partition lpem2014100701 values less than ('2014100702') tablespace AIMIRDAT33201410;
alter table "AIMIR"."LP_EM" add partition lpem2014100702 values less than ('2014100703') tablespace AIMIRDAT33201410;
alter table "AIMIR"."LP_EM" add partition lpem2014100703 values less than ('2014100704') tablespace AIMIRDAT33201410;
alter table "AIMIR"."LP_EM" add partition lpem2014100704 values less than ('2014100705') tablespace AIMIRDAT33201410;
alter table "AIMIR"."LP_EM" add partition lpem2014100705 values less than ('2014100706') tablespace AIMIRDAT33201410;
alter table "AIMIR"."LP_EM" add partition lpem2014100706 values less than ('2014100707') tablespace AIMIRDAT33201410;
alter table "AIMIR"."LP_EM" add partition lpem2014100707 values less than ('2014100708') tablespace AIMIRDAT33201410;
alter table "AIMIR"."LP_EM" add partition lpem2014100708 values less than ('2014100709') tablespace AIMIRDAT33201410;
alter table "AIMIR"."LP_EM" add partition lpem2014100709 values less than ('2014100710') tablespace AIMIRDAT33201410;
alter table "AIMIR"."LP_EM" add partition lpem2014100710 values less than ('2014100711') tablespace AIMIRDAT33201410;
alter table "AIMIR"."LP_EM" add partition lpem2014100711 values less than ('2014100712') tablespace AIMIRDAT33201410;
alter table "AIMIR"."LP_EM" add partition lpem2014100712 values less than ('2014100713') tablespace AIMIRDAT33201410;
alter table "AIMIR"."LP_EM" add partition lpem2014100713 values less than ('2014100714') tablespace AIMIRDAT33201410;
alter table "AIMIR"."LP_EM" add partition lpem2014100714 values less than ('2014100715') tablespace AIMIRDAT33201410;
alter table "AIMIR"."LP_EM" add partition lpem2014100715 values less than ('2014100716') tablespace AIMIRDAT33201410;
alter table "AIMIR"."LP_EM" add partition lpem2014100716 values less than ('2014100717') tablespace AIMIRDAT33201410;
alter table "AIMIR"."LP_EM" add partition lpem2014100717 values less than ('2014100718') tablespace AIMIRDAT33201410;
alter table "AIMIR"."LP_EM" add partition lpem2014100718 values less than ('2014100719') tablespace AIMIRDAT33201410;
alter table "AIMIR"."LP_EM" add partition lpem2014100719 values less than ('2014100720') tablespace AIMIRDAT33201410;
alter table "AIMIR"."LP_EM" add partition lpem2014100720 values less than ('2014100721') tablespace AIMIRDAT33201410;
alter table "AIMIR"."LP_EM" add partition lpem2014100721 values less than ('2014100722') tablespace AIMIRDAT33201410;
alter table "AIMIR"."LP_EM" add partition lpem2014100722 values less than ('2014100723') tablespace AIMIRDAT33201410;
alter table "AIMIR"."LP_EM" add partition lpem2014100723 values less than ('2014100800') tablespace AIMIRDAT33201410;
alter table "AIMIR"."LP_EM" add partition lpem2014100800 values less than ('2014100801') tablespace AIMIRDAT33201410;


alter table "AIMIR"."LP_EM" add partition lpem201801 values less than ('2018020100') tablespace AIMIRDAT201801;
alter table "AIMIR"."LP_EM" add partition lpem201802 values less than ('2018030100') tablespace AIMIRDAT201802;
alter table "AIMIR"."LP_EM" add partition lpem201803 values less than ('2018040100') tablespace AIMIRDAT201803;
alter table "AIMIR"."LP_EM" add partition lpem201804 values less than ('2018050100') tablespace AIMIRDAT201804;
alter table "AIMIR"."LP_EM" add partition lpem201805 values less than ('2018060100') tablespace AIMIRDAT201805;
alter table "AIMIR"."LP_EM" add partition lpem201806 values less than ('2018070100') tablespace AIMIRDAT201806;
alter table "AIMIR"."LP_EM" add partition lpem201807 values less than ('2018080100') tablespace AIMIRDAT201807;
alter table "AIMIR"."LP_EM" add partition lpem201808 values less than ('2018090100') tablespace AIMIRDAT201808;
alter table "AIMIR"."LP_EM" add partition lpem201809 values less than ('2018100100') tablespace AIMIRDAT201809;
alter table "AIMIR"."LP_EM" add partition lpem201810 values less than ('2018110100') tablespace AIMIRDAT201810;
alter table "AIMIR"."LP_EM" add partition lpem201811 values less than ('2018120100') tablespace AIMIRDAT201811;
alter table "AIMIR"."LP_EM" add partition lpem201812 values less than ('2019010100') tablespace AIMIRDAT201812;
alter table "AIMIR"."LP_EM" add partition lpem201901 values less than ('2019020100') tablespace AIMIRDAT201901;
alter table "AIMIR"."LP_EM" add partition lpem201902 values less than ('2019030100') tablespace AIMIRDAT201902;
alter table "AIMIR"."LP_EM" add partition lpem201903 values less than ('2019040100') tablespace AIMIRDAT201903;
alter table "AIMIR"."LP_EM" add partition lpem201904 values less than ('2019050100') tablespace AIMIRDAT201904;
alter table "AIMIR"."LP_EM" add partition lpem201905 values less than ('2019060100') tablespace AIMIRDAT201905;
alter table "AIMIR"."LP_EM" add partition lpem201906 values less than ('2019070100') tablespace AIMIRDAT201906;
alter table "AIMIR"."LP_EM" add partition lpem201907 values less than ('2019080100') tablespace AIMIRDAT201907;
alter table "AIMIR"."LP_EM" add partition lpem201908 values less than ('2019090100') tablespace AIMIRDAT201908;
alter table "AIMIR"."LP_EM" add partition lpem201909 values less than ('2019100100') tablespace AIMIRDAT201909;
alter table "AIMIR"."LP_EM" add partition lpem201910 values less than ('2019110100') tablespace AIMIRDAT201910;
alter table "AIMIR"."LP_EM" add partition lpem201911 values less than ('2019120100') tablespace AIMIRDAT201911;
alter table "AIMIR"."LP_EM" add partition lpem201912 values less than ('2020010100') tablespace AIMIRDAT201912;
alter table "AIMIR"."LP_EM" add partition lpem202001 values less than ('2020020100') tablespace AIMIRDAT202001;
alter table "AIMIR"."LP_EM" add partition lpem202002 values less than ('2020030100') tablespace AIMIRDAT202002;
alter table "AIMIR"."LP_EM" add partition lpem202003 values less than ('2020040100') tablespace AIMIRDAT202003;
alter table "AIMIR"."LP_EM" add partition lpem202004 values less than ('2020050100') tablespace AIMIRDAT202004;
alter table "AIMIR"."LP_EM" add partition lpem202005 values less than ('2020060100') tablespace AIMIRDAT202005;
alter table "AIMIR"."LP_EM" add partition lpem202006 values less than ('2020070100') tablespace AIMIRDAT202006;
alter table "AIMIR"."LP_EM" add partition lpem202007 values less than ('2020080100') tablespace AIMIRDAT202007;
alter table "AIMIR"."LP_EM" add partition lpem202008 values less than ('2020090100') tablespace AIMIRDAT202008;
alter table "AIMIR"."LP_EM" add partition lpem202009 values less than ('2020100100') tablespace AIMIRDAT202009;
alter table "AIMIR"."LP_EM" add partition lpem202010 values less than ('2020110100') tablespace AIMIRDAT202010;


alter table "AIMIR"."LP_EM" add partition lpem202011 values less than ('2020120100') tablespace AIMIRDAT202011;
alter table "AIMIR"."LP_EM" add partition lpem202012 values less than ('2021010100') tablespace AIMIRDAT202012;