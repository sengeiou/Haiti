/* ***********************************
 * 	@Copyright : NURITelecom
 * 	@ProjectName : aimir_mdms_moe
 * 	@FileName : TRG_BILLING_MONTH_EM.sql
 * 	@Author : kyunghee.yoon
 * 	@Date : 2018.09.20
 *************************************/

create or replace TRIGGER TRG_BILLING_MONTH_EM
AFTER INSERT OR UPDATE ON BILLING_MONTH_EM
FOR EACH ROW
DECLARE
BEGIN

	PKG_MDM_DATA.INSERT_BILLING_MONTH_EM (
		  :NEW.MDEV_ID
		, :NEW.MDEV_TYPE
		, :NEW.YYYYMMDD
		, :NEW.HHMMSS
		, :NEW.CUMULACTIVEENGYIMPORT
		, :NEW.CUMULACTIVEENGYIMPORTRATE1
		, :NEW.CUMULACTIVEENGYIMPORTRATE2
		, :NEW.CUMULACTIVEENGYIMPORTRATE3
		, :NEW.CUMULACTIVEENGYIMPORTRATE4
		, :NEW.CUMULACTIVEENGYIMPORTRATE5
		, :NEW.CUMULACTIVEENGYIMPORTRATE6
		, :NEW.CUMULACTIVEENGYIMPORTRATE7
		, :NEW.CUMULACTIVEENGYIMPORTRATE8
		, :NEW.CUMULREACTIVEENGYIMPORT
		, :NEW.CUMULREACTIVEENGYIMPORTRATE1
		, :NEW.CUMULREACTIVEENGYIMPORTRATE2
		, :NEW.CUMULREACTIVEENGYIMPORTRATE3
		, :NEW.CUMULREACTIVEENGYIMPORTRATE4
		, :NEW.CUMULREACTIVEENGYIMPORTRATE5
		, :NEW.CUMULREACTIVEENGYIMPORTRATE6
		, :NEW.CUMULREACTIVEENGYIMPORTRATE7
		, :NEW.CUMULREACTIVEENGYIMPORTRATE8
		, :NEW.ACTIVEPWRDMDMAXTIMERATETOT
		, :NEW.ACTIVEPWRMAXDMDRATETOT
		, :NEW.WRITEDATE
	);

END;