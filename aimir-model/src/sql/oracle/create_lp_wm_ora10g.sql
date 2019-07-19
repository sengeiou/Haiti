drop table "AIMIR"."LP_WM"  CASCADE CONSTRAINTS;

CREATE TABLE "AIMIR"."LP_WM"
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
   CONSTRAINT SYS_C008425 PRIMARY KEY (CHANNEL,YYYYMMDDHH,DST,MDEV_ID,MDEV_TYPE)
)
partition by range (yyyymmddhh)
(
   partition lpwm201305 values less than ('2013060100') tablespace AMR3DAT201305
)
;
ALTER TABLE "AIMIR"."LP_WM"
ADD CONSTRAINT FK454C7715CF66507
FOREIGN KEY (CONTRACT_ID)
REFERENCES "AIMIR"."CONTRACT"(ID)
;
ALTER TABLE "AIMIR"."LP_WM"
ADD CONSTRAINT FK454C771CAB63A94
FOREIGN KEY (METER_ID)
REFERENCES "AIMIR"."METER"(ID)
;
ALTER TABLE "AIMIR"."LP_WM"
ADD CONSTRAINT FK454C771D6C540B4
FOREIGN KEY (ENDDEVICE_ID)
REFERENCES "AIMIR"."ENDDEVICE"(ID)
;
ALTER TABLE "AIMIR"."LP_WM"
ADD CONSTRAINT FK454C7718F8DBE27
FOREIGN KEY (LOCATION_ID)
REFERENCES "AIMIR"."LOCATION"(ID)
;
ALTER TABLE "AIMIR"."LP_WM"
ADD CONSTRAINT FK454C7717D01F2C7
FOREIGN KEY (SUPPLIER_ID)
REFERENCES "AIMIR"."SUPPLIER"(ID)
;
ALTER TABLE "AIMIR"."LP_WM"
ADD CONSTRAINT FK454C771C0690AF4
FOREIGN KEY (MODEM_ID)
REFERENCES "AIMIR"."MODEM"(ID)
;
CREATE INDEX IDX_"AIMIR"."LP_WM"_02 ON "AIMIR"."LP_WM"
(
  MDEV_TYPE,
  MDEV_ID,
  YYYYMMDD,
  CHANNEL
)
;
CREATE INDEX IDX_"AIMIR"."LP_WM"_01 ON "AIMIR"."LP_WM"
(
  MDEV_TYPE,
  MDEV_ID,
  DST,
  YYYYMMDD
)
;
CREATE UNIQUE INDEX SYS_C008425 ON "AIMIR"."LP_WM"
(
  CHANNEL,
  YYYYMMDDHH,
  DST,
  MDEV_ID,
  MDEV_TYPE
)
;
CREATE INDEX IDX_"AIMIR"."LP_WM"_03 ON "AIMIR"."LP_WM"
(
  MDEV_TYPE,
  MDEV_ID,
  YYYYMMDDHH,
  CHANNEL
)
;

alter table "AIMIR"."LP_WM" add partition lpwm201306 values less than ('2013070100') tablespace AMR3DAT201306;
alter table "AIMIR"."LP_WM" add partition lpwm201307 values less than ('2013080100') tablespace AMR3DAT201307;
alter table "AIMIR"."LP_WM" add partition lpwm201308 values less than ('2013090100') tablespace AMR3DAT201308;
alter table "AIMIR"."LP_WM" add partition lpwm201309 values less than ('2013100100') tablespace AMR3DAT201309;
alter table "AIMIR"."LP_WM" add partition lpwm201310 values less than ('2013110100') tablespace AMR3DAT201310;
alter table "AIMIR"."LP_WM" add partition lpwm201311 values less than ('2013120100') tablespace AMR3DAT201311;
alter table "AIMIR"."LP_WM" add partition lpwm201312 values less than ('2014010100') tablespace AMR3DAT201312;
alter table "AIMIR"."LP_WM" add partition lpwm201401 values less than ('2014020100') tablespace AMR3DAT201401;
alter table "AIMIR"."LP_WM" add partition lpwm201402 values less than ('2014030100') tablespace AMR3DAT201402;
alter table "AIMIR"."LP_WM" add partition lpwm201403 values less than ('2014040100') tablespace AMR3DAT201403;
alter table "AIMIR"."LP_WM" add partition lpwm201404 values less than ('2014050100') tablespace AMR3DAT201404;
alter table "AIMIR"."LP_WM" add partition lpwm201405 values less than ('2014060100') tablespace AMR3DAT201405;
alter table "AIMIR"."LP_WM" add partition lpwm201406 values less than ('2014070100') tablespace AMR3DAT201406;
alter table "AIMIR"."LP_WM" add partition lpwm201407 values less than ('2014080100') tablespace AMR3DAT201407;
alter table "AIMIR"."LP_WM" add partition lpwm201408 values less than ('2014090100') tablespace AMR3DAT201408;
alter table "AIMIR"."LP_WM" add partition lpwm201409 values less than ('2014100100') tablespace AMR3DAT201409;
alter table "AIMIR"."LP_WM" add partition lpwm201410 values less than ('2014110100') tablespace AMR3DAT201410;
alter table "AIMIR"."LP_WM" add partition lpwm201411 values less than ('2014120100') tablespace AMR3DAT201411;
alter table "AIMIR"."LP_WM" add partition lpwm201412 values less than ('2015010100') tablespace AMR3DAT201412;
alter table "AIMIR"."LP_WM" add partition lpwm201501 values less than ('2015020100') tablespace AMR3DAT201501;
alter table "AIMIR"."LP_WM" add partition lpwm201502 values less than ('2015030100') tablespace AMR3DAT201502;
alter table "AIMIR"."LP_WM" add partition lpwm201503 values less than ('2015040100') tablespace AMR3DAT201503;
alter table "AIMIR"."LP_WM" add partition lpwm201504 values less than ('2015050100') tablespace AMR3DAT201504;
alter table "AIMIR"."LP_WM" add partition lpwm201505 values less than ('2015060100') tablespace AMR3DAT201505;
alter table "AIMIR"."LP_WM" add partition lpwm201506 values less than ('2015070100') tablespace AMR3DAT201506;
alter table "AIMIR"."LP_WM" add partition lpwm201507 values less than ('2015080100') tablespace AMR3DAT201507;
alter table "AIMIR"."LP_WM" add partition lpwm201508 values less than ('2015090100') tablespace AMR3DAT201508;
alter table "AIMIR"."LP_WM" add partition lpwm201509 values less than ('2015100100') tablespace AMR3DAT201509;
alter table "AIMIR"."LP_WM" add partition lpwm201510 values less than ('2015110100') tablespace AMR3DAT201510;
alter table "AIMIR"."LP_WM" add partition lpwm201511 values less than ('2015120100') tablespace AMR3DAT201511;
alter table "AIMIR"."LP_WM" add partition lpwm201512 values less than ('2016010100') tablespace AMR3DAT201512;
alter table "AIMIR"."LP_WM" add partition lpwm201601 values less than ('2016020100') tablespace AMR3DAT201601;
alter table "AIMIR"."LP_WM" add partition lpwm201602 values less than ('2016030100') tablespace AMR3DAT201602;
alter table "AIMIR"."LP_WM" add partition lpwm201603 values less than ('2016040100') tablespace AMR3DAT201603;
alter table "AIMIR"."LP_WM" add partition lpwm201604 values less than ('2016050100') tablespace AMR3DAT201604;
alter table "AIMIR"."LP_WM" add partition lpwm201605 values less than ('2016060100') tablespace AMR3DAT201605;
alter table "AIMIR"."LP_WM" add partition lpwm201606 values less than ('2016070100') tablespace AMR3DAT201606;
alter table "AIMIR"."LP_WM" add partition lpwm201607 values less than ('2016080100') tablespace AMR3DAT201607;
alter table "AIMIR"."LP_WM" add partition lpwm201608 values less than ('2016090100') tablespace AMR3DAT201608;
alter table "AIMIR"."LP_WM" add partition lpwm201609 values less than ('2016100100') tablespace AMR3DAT201609;
alter table "AIMIR"."LP_WM" add partition lpwm201610 values less than ('2016110100') tablespace AMR3DAT201610;
alter table "AIMIR"."LP_WM" add partition lpwm201612 values less than ('2017010100') tablespace AMR3DAT201612;
alter table "AIMIR"."LP_WM" add partition lpwm201701 values less than ('2017020100') tablespace AMR3DAT201701;
alter table "AIMIR"."LP_WM" add partition lpwm201702 values less than ('2017030100') tablespace AMR3DAT201702;
alter table "AIMIR"."LP_WM" add partition lpwm201703 values less than ('2017040100') tablespace AMR3DAT201703;
alter table "AIMIR"."LP_WM" add partition lpwm201704 values less than ('2017050100') tablespace AMR3DAT201704;
alter table "AIMIR"."LP_WM" add partition lpwm201705 values less than ('2017060100') tablespace AMR3DAT201705;
alter table "AIMIR"."LP_WM" add partition lpwm201706 values less than ('2017070100') tablespace AMR3DAT201706;
alter table "AIMIR"."LP_WM" add partition lpwm201707 values less than ('2017080100') tablespace AMR3DAT201707;
alter table "AIMIR"."LP_WM" add partition lpwm201708 values less than ('2017090100') tablespace AMR3DAT201708;
alter table "AIMIR"."LP_WM" add partition lpwm201709 values less than ('2017100100') tablespace AMR3DAT201709;
alter table "AIMIR"."LP_WM" add partition lpwm201710 values less than ('2017110100') tablespace AMR3DAT201710;
alter table "AIMIR"."LP_WM" add partition lpwm201711 values less than ('2017120100') tablespace AMR3DAT201711;
alter table "AIMIR"."LP_WM" add partition lpwm201712 values less than ('2018010100') tablespace AMR3DAT201712;
alter table "AIMIR"."LP_WM" add partition lpwm201801 values less than ('2018020100') tablespace AMR3DAT201801;
alter table "AIMIR"."LP_WM" add partition lpwm201802 values less than ('2018030100') tablespace AMR3DAT201802;
alter table "AIMIR"."LP_WM" add partition lpwm201803 values less than ('2018040100') tablespace AMR3DAT201803;
alter table "AIMIR"."LP_WM" add partition lpwm201804 values less than ('2018050100') tablespace AMR3DAT201804;
alter table "AIMIR"."LP_WM" add partition lpwm201805 values less than ('2018060100') tablespace AMR3DAT201805;
alter table "AIMIR"."LP_WM" add partition lpwm201806 values less than ('2018070100') tablespace AMR3DAT201806;
alter table "AIMIR"."LP_WM" add partition lpwm201807 values less than ('2018080100') tablespace AMR3DAT201807;
alter table "AIMIR"."LP_WM" add partition lpwm201808 values less than ('2018090100') tablespace AMR3DAT201808;
alter table "AIMIR"."LP_WM" add partition lpwm201809 values less than ('2018100100') tablespace AMR3DAT201809;
alter table "AIMIR"."LP_WM" add partition lpwm201810 values less than ('2018110100') tablespace AMR3DAT201810;
alter table "AIMIR"."LP_WM" add partition lpwm201811 values less than ('2018120100') tablespace AMR3DAT201811;
alter table "AIMIR"."LP_WM" add partition lpwm201812 values less than ('2019010100') tablespace AMR3DAT201812;
alter table "AIMIR"."LP_WM" add partition lpwm201901 values less than ('2019020100') tablespace AMR3DAT201901;
alter table "AIMIR"."LP_WM" add partition lpwm201902 values less than ('2019030100') tablespace AMR3DAT201902;
alter table "AIMIR"."LP_WM" add partition lpwm201903 values less than ('2019040100') tablespace AMR3DAT201903;
alter table "AIMIR"."LP_WM" add partition lpwm201904 values less than ('2019050100') tablespace AMR3DAT201904;
alter table "AIMIR"."LP_WM" add partition lpwm201905 values less than ('2019060100') tablespace AMR3DAT201905;
alter table "AIMIR"."LP_WM" add partition lpwm201906 values less than ('2019070100') tablespace AMR3DAT201906;
alter table "AIMIR"."LP_WM" add partition lpwm201907 values less than ('2019080100') tablespace AMR3DAT201907;
alter table "AIMIR"."LP_WM" add partition lpwm201908 values less than ('2019090100') tablespace AMR3DAT201908;
alter table "AIMIR"."LP_WM" add partition lpwm201909 values less than ('2019100100') tablespace AMR3DAT201909;
alter table "AIMIR"."LP_WM" add partition lpwm201910 values less than ('2019110100') tablespace AMR3DAT201910;
alter table "AIMIR"."LP_WM" add partition lpwm201911 values less than ('2019120100') tablespace AMR3DAT201911;
alter table "AIMIR"."LP_WM" add partition lpwm201912 values less than ('2020010100') tablespace AMR3DAT201912;
alter table "AIMIR"."LP_WM" add partition lpwm202001 values less than ('2020020100') tablespace AMR3DAT202001;
alter table "AIMIR"."LP_WM" add partition lpwm202002 values less than ('2020030100') tablespace AMR3DAT202002;
alter table "AIMIR"."LP_WM" add partition lpwm202003 values less than ('2020040100') tablespace AMR3DAT202003;
alter table "AIMIR"."LP_WM" add partition lpwm202004 values less than ('2020050100') tablespace AMR3DAT202004;
alter table "AIMIR"."LP_WM" add partition lpwm202005 values less than ('2020060100') tablespace AMR3DAT202005;
alter table "AIMIR"."LP_WM" add partition lpwm202006 values less than ('2020070100') tablespace AMR3DAT202006;
alter table "AIMIR"."LP_WM" add partition lpwm202007 values less than ('2020080100') tablespace AMR3DAT202007;
alter table "AIMIR"."LP_WM" add partition lpwm202008 values less than ('2020090100') tablespace AMR3DAT202008;
alter table "AIMIR"."LP_WM" add partition lpwm202009 values less than ('2020100100') tablespace AMR3DAT202009;
alter table "AIMIR"."LP_WM" add partition lpwm202010 values less than ('2020110100') tablespace AMR3DAT202010;
alter table "AIMIR"."LP_WM" add partition lpwm202011 values less than ('2020120100') tablespace AMR3DAT202011;
alter table "AIMIR"."LP_WM" add partition lpwm202012 values less than ('2021010100') tablespace AMR3DAT202012;