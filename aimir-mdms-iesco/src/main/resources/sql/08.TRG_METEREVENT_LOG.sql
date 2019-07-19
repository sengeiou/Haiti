/* ***********************************
 * 	@Copyright : NURITelecom
 * 	@ProjectName : aimir_mdms_moe
 * 	@FileName : TRG_METEREVENT_LOG.sql
 * 	@Author : kyunghee.yoon
 * 	@Date : 2018.09.20
 *************************************/

create or replace TRIGGER TRG_METEREVENT_LOG
AFTER INSERT OR UPDATE ON METEREVENT_LOG
FOR EACH ROW
DECLARE
BEGIN

	PKG_MDM_DATA.INSERT_METEREVENT_LOG (
		  :NEW.ACTIVATOR_ID
		, :NEW.METEREVENT_ID
		, :NEW.OPEN_TIME
		, :NEW.ACTIVATOR_TYPE
		, :NEW.MESSAGE		
		, :NEW.WRITETIME
	);

END;