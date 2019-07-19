drop table "AIMIR"."POWER_QUALITY"  CASCADE CONSTRAINTS;

CREATE TABLE "AIMIR"."POWER_QUALITY"
(
   DST decimal(22) DEFAULT 0  NOT NULL,
   MDEV_ID varchar2(20) NOT NULL,
   MDEV_TYPE decimal(10) NOT NULL,
   YYYYMMDDHHMM varchar2(12) NOT NULL,
   CONTRACT_ID decimal(10),
   CURR_1ST_HARMONIC_MAG_A float(126),
   CURR_1ST_HARMONIC_MAG_B float(126),
   CURR_1ST_HARMONIC_MAG_C float(126),
   CURR_2ND_HARMONIC_MAG_A float(126),
   CURR_2ND_HARMONIC_MAG_B float(126),
   CURR_2ND_HARMONIC_MAG_C float(126),
   CURR_A float(126),
   CURR_ANGLE_A float(126),
   CURR_ANGLE_B float(126),
   CURR_ANGLE_C float(126),
   CURR_B float(126),
   CURR_C float(126),
   CURR_HARMONIC_A float(126),
   CURR_HARMONIC_B float(126),
   CURR_HARMONIC_C float(126),
   CURR_SEQ_N float(126),
   CURR_SEQ_P float(126),
   CURR_SEQ_Z float(126),
   CURR_THD_A float(126),
   CURR_THD_B float(126),
   CURR_THD_C float(126),
   DEVICE_ID varchar2(20),
   DEVICE_TYPE varchar2(255),
   DISTORTION_KVA_A float(126),
   DISTORTION_KVA_B float(126),
   DISTORTION_KVA_C float(126),
   DISTORTION_PF_A float(126),
   DISTORTION_PF_B float(126),
   DISTORTION_PF_C float(126),
   DISTORTION_PF_TOTAL float(126),
   ENDDEVICE_ID decimal(10),
   HHMM varchar2(4) NOT NULL,
   KVA_A float(126),
   KVA_B float(126),
   KVA_C float(126),
   KVAR_A float(126),
   KVAR_B float(126),
   KVAR_C float(126),
   KW_A float(126),
   KW_B float(126),
   KW_C float(126),
   LINE_AB float(126),
   LINE_BC float(126),
   LINE_CA float(126),
   LINE_FREQUENCY float(126),
   METER_ID decimal(10),
   MODEM_ID decimal(10),
   PF_A float(126),
   PF_B float(126),
   PF_C float(126),
   PF_TOTAL float(126),
   PH_CURR_PQM_A float(126),
   PH_CURR_PQM_B float(126),
   PH_CURR_PQM_C float(126),
   PH_FUND_CURR_A float(126),
   PH_FUND_CURR_B float(126),
   PH_FUND_CURR_C float(126),
   PH_FUND_VOL_A float(126),
   PH_FUND_VOL_B float(126),
   PH_FUND_VOL_C float(126),
   PH_VOL_PQM_A float(126),
   PH_VOL_PQM_B float(126),
   PH_VOL_PQM_C float(126),
   SUPPLIER_ID decimal(10),
   SYSTEM_PF_ANGLE float(126),
   TDD_A float(126),
   TDD_B float(126),
   TDD_C float(126),
   VOL_1ST_HARMONIC_MAG_A float(126),
   VOL_1ST_HARMONIC_MAG_B float(126),
   VOL_1ST_HARMONIC_MAG_C float(126),
   VOL_2ND_HARMONIC_A float(126),
   VOL_2ND_HARMONIC_B float(126),
   VOL_2ND_HARMONIC_C float(126),
   VOL_2ND_HARMONIC_MAG_A float(126),
   VOL_2ND_HARMONIC_MAG_B float(126),
   VOL_2ND_HARMONIC_MAG_C float(126),
   VOL_A float(126),
   VOL_ANGLE_A float(126),
   VOL_ANGLE_B float(126),
   VOL_ANGLE_C float(126),
   VOL_B float(126),
   VOL_C float(126),
   VOL_SEQ_N float(126),
   VOL_SEQ_P float(126),
   VOL_SEQ_Z float(126),
   VOL_THD_A float(126),
   VOL_THD_B float(126),
   VOL_THD_C float(126),
   WRITEDATE varchar2(14) NOT NULL,
   YYYYMMDD varchar2(8) NOT NULL,
   CONSTRAINT SYS_C004768 PRIMARY KEY (DST,MDEV_ID,MDEV_TYPE,YYYYMMDDHHMM)
)
partition by range (yyyymmddhhmm)
(
   partition powerquality201408 values less than ('201409010000') tablespace AIMIRDAT201408
)
;
ALTER TABLE "AIMIR"."POWER_QUALITY"
ADD CONSTRAINT FKB65FCF855CF66507
FOREIGN KEY (CONTRACT_ID)
REFERENCES "AIMIR"."CONTRACT"(ID)
;
ALTER TABLE "AIMIR"."POWER_QUALITY"
ADD CONSTRAINT FKB65FCF85CAB63A94
FOREIGN KEY (METER_ID)
REFERENCES "AIMIR"."METER"(ID)
;
ALTER TABLE "AIMIR"."POWER_QUALITY"
ADD CONSTRAINT FKB65FCF85D6C540B4
FOREIGN KEY (ENDDEVICE_ID)
REFERENCES "AIMIR"."ENDDEVICE"(ID)
;
ALTER TABLE "AIMIR"."POWER_QUALITY"
ADD CONSTRAINT FKB65FCF857D01F2C7
FOREIGN KEY (SUPPLIER_ID)
REFERENCES "AIMIR"."SUPPLIER"(ID)
;
ALTER TABLE "AIMIR"."POWER_QUALITY"
ADD CONSTRAINT FKB65FCF85C0690AF4
FOREIGN KEY (MODEM_ID)
REFERENCES "AIMIR"."MODEM"(ID)
;
CREATE UNIQUE INDEX SYS_C004768 ON "AIMIR"."POWER_QUALITY"
(
  DST,
  MDEV_ID,
  MDEV_TYPE,
  YYYYMMDDHHMM
)
;

alter table "AIMIR"."POWER_QUALITY" add partition powerquality201409 values less than ('201410010000') tablespace AIMIRDAT201409;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201410 values less than ('201411010000') tablespace AIMIRDAT201410;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201411 values less than ('201412010000') tablespace AIMIRDAT201411;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201412 values less than ('201501010000') tablespace AIMIRDAT201412;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201501 values less than ('201502010000') tablespace AIMIRDAT201501;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201502 values less than ('201503010000') tablespace AIMIRDAT201502;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201503 values less than ('201504010000') tablespace AIMIRDAT201503;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201504 values less than ('201505010000') tablespace AIMIRDAT201504;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201505 values less than ('201506010000') tablespace AIMIRDAT201505;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201506 values less than ('201507010000') tablespace AIMIRDAT201506;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201507 values less than ('201508010000') tablespace AIMIRDAT201507;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201508 values less than ('201509010000') tablespace AIMIRDAT201508;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201509 values less than ('201510010000') tablespace AIMIRDAT201509;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201510 values less than ('201511010000') tablespace AIMIRDAT201510;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201511 values less than ('201512010000') tablespace AIMIRDAT201511;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201512 values less than ('201601010000') tablespace AIMIRDAT201512;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201601 values less than ('201602010000') tablespace AIMIRDAT201601;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201602 values less than ('201603010000') tablespace AIMIRDAT201602;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201603 values less than ('201604010000') tablespace AIMIRDAT201603;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201604 values less than ('201605010000') tablespace AIMIRDAT201604;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201605 values less than ('201606010000') tablespace AIMIRDAT201605;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201606 values less than ('201607010000') tablespace AIMIRDAT201606;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201607 values less than ('201608010000') tablespace AIMIRDAT201607;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201608 values less than ('201609010000') tablespace AIMIRDAT201608;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201609 values less than ('201610010000') tablespace AIMIRDAT201609;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201610 values less than ('201611010000') tablespace AIMIRDAT201610;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201612 values less than ('201701010000') tablespace AIMIRDAT201612;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201701 values less than ('201702010000') tablespace AIMIRDAT201701;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201702 values less than ('201703010000') tablespace AIMIRDAT201702;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201703 values less than ('201704010000') tablespace AIMIRDAT201703;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201704 values less than ('201705010000') tablespace AIMIRDAT201704;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201705 values less than ('201706010000') tablespace AIMIRDAT201705;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201706 values less than ('201707010000') tablespace AIMIRDAT201706;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201707 values less than ('201708010000') tablespace AIMIRDAT201707;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201708 values less than ('201709010000') tablespace AIMIRDAT201708;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201709 values less than ('201710010000') tablespace AIMIRDAT201709;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201710 values less than ('201711010000') tablespace AIMIRDAT201710;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201711 values less than ('201712010000') tablespace AIMIRDAT201711;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201712 values less than ('201801010000') tablespace AIMIRDAT201712;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201801 values less than ('201802010000') tablespace AIMIRDAT201801;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201802 values less than ('201803010000') tablespace AIMIRDAT201802;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201803 values less than ('201804010000') tablespace AIMIRDAT201803;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201804 values less than ('201805010000') tablespace AIMIRDAT201804;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201805 values less than ('201806010000') tablespace AIMIRDAT201805;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201806 values less than ('201807010000') tablespace AIMIRDAT201806;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201807 values less than ('201808010000') tablespace AIMIRDAT201807;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201808 values less than ('201809010000') tablespace AIMIRDAT201808;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201809 values less than ('201810010000') tablespace AIMIRDAT201809;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201810 values less than ('201811010000') tablespace AIMIRDAT201810;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201811 values less than ('201812010000') tablespace AIMIRDAT201811;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201812 values less than ('201901010000') tablespace AIMIRDAT201812;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201901 values less than ('201902010000') tablespace AIMIRDAT201901;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201902 values less than ('201903010000') tablespace AIMIRDAT201902;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201903 values less than ('201904010000') tablespace AIMIRDAT201903;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201904 values less than ('201905010000') tablespace AIMIRDAT201904;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201905 values less than ('201906010000') tablespace AIMIRDAT201905;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201906 values less than ('201907010000') tablespace AIMIRDAT201906;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201907 values less than ('201908010000') tablespace AIMIRDAT201907;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201908 values less than ('201909010000') tablespace AIMIRDAT201908;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201909 values less than ('201910010000') tablespace AIMIRDAT201909;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201910 values less than ('201911010000') tablespace AIMIRDAT201910;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201911 values less than ('201912010000') tablespace AIMIRDAT201911;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality201912 values less than ('202001010000') tablespace AIMIRDAT201912;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality202001 values less than ('202002010000') tablespace AIMIRDAT202001;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality202002 values less than ('202003010000') tablespace AIMIRDAT202002;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality202003 values less than ('202004010000') tablespace AIMIRDAT202003;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality202004 values less than ('202005010000') tablespace AIMIRDAT202004;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality202005 values less than ('202006010000') tablespace AIMIRDAT202005;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality202006 values less than ('202007010000') tablespace AIMIRDAT202006;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality202007 values less than ('202008010000') tablespace AIMIRDAT202007;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality202008 values less than ('202009010000') tablespace AIMIRDAT202008;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality202009 values less than ('202010010000') tablespace AIMIRDAT202009;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality202010 values less than ('202011010000') tablespace AIMIRDAT202010;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality202011 values less than ('202012010000') tablespace AIMIRDAT202011;
alter table "AIMIR"."POWER_QUALITY" add partition powerquality202012 values less than ('202101010000') tablespace AIMIRDAT202012;