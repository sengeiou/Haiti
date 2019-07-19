/**
 * Copyright Nuri Telecom Corp.
 * 파일명: FirmwareHistoryDaoImpl
 * 작성일자/작성자 : 2011.01.13 박연경
 * @see 
 * 
 *
 * 펌웨어 배포 이력 DAO
 * 
 * ============================================================================
 * 수정 내역
 * NO  수정일자   수정자   수정내역
 * 
 * ============================================================================
 */
package com.aimir.dao.device.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.FirmwareHistoryDao;
import com.aimir.model.device.FirmwareHistory;
import com.aimir.model.device.FirmwareHistoryPk;

@Repository(value = "firmwarehistoryDao")
public class FirmwareHistoryDaoImpl extends AbstractHibernateGenericDao<FirmwareHistory, FirmwareHistoryPk> implements FirmwareHistoryDao {
	private static Log logger = LogFactory.getLog(FirmwareHistoryDaoImpl.class);

	@Autowired
	protected FirmwareHistoryDaoImpl(SessionFactory sessionFactory) {
		super(FirmwareHistory.class);
		super.setSessionFactory(sessionFactory);
	}
	
	/**
	 * trigger 유무 체크 (cmd 호출후 작업)
	 **/
	@SuppressWarnings("unchecked")
    public String historyCheckExistEquip(Map<String, Object> param)throws Exception {
       	logger.debug(this.getClass().getName()+":"+"triggerCheckExistEquip()");
       	String equip_kind = String.valueOf(param.get("equip_kind"));
       	int arm = Integer.parseInt(String.valueOf(param.get("arm")));
       	String equip_id = String.valueOf(param.get("equip_id"));
		boolean isExist=false;

		StringBuffer sqlBuf = new StringBuffer();
    	if(equip_kind.equals("Modem")) {
    		sqlBuf.append("select count(*) as cnt from firmware_history his join  firmware_trigger tri on (tri.id = his.tr_id)       \n");
    		sqlBuf.append("                                                   join firmware firm on (tri.src_firmware=firm.firmware_id AND  firm.ARM = :arm ) \n");
    		sqlBuf.append("where his.equip_kind = :equip_kind \n");
    		sqlBuf.append("and his.equip_id = :equip_id \n");
    	}else {
    		sqlBuf.append("select count(*) as cnt from firmware_history his join  firmware_trigger tri on (tri.id = his.tr_id)       \n");
    		sqlBuf.append("                                                   join firmware firm on (tri.src_firmware=firm.firmware_id) \n");
    		sqlBuf.append("where his.equip_kind = :equip_kind \n");
    		sqlBuf.append("and his.equip_id = :equip_id \n");	
    	}

		
		SQLQuery query = getSession().createSQLQuery(sqlBuf.toString());
		query = getSession().createSQLQuery(sqlBuf.toString());
    	if(equip_kind.equals("Modem")) {
    		query.setString("equip_kind", equip_kind);
    		query.setString("equip_id", equip_id);
    		query.setInteger("arm", arm);
    	}else{
    		query.setString("equip_kind", equip_kind);
    		query.setString("equip_id", equip_id);
    	}
    	
        return String.valueOf(query.list().get(0));
    }

	/**
	 * trigger history 정보를 가지고 옮 (cmd 호출후 작업)
	 **/
    public String getTriggerHistory(Map<String, Object> param)throws Exception{
       	logger.debug(this.getClass().getName()+":"+"triggerGetTriggerHistory()");
       	String equip_kind = String.valueOf(param.get("equip_kind"));
       	int arm = Integer.parseInt(String.valueOf(param.get("arm")));
       	String equip_id = String.valueOf(param.get("equip_id"));
       	

		StringBuffer sqlBuf = new StringBuffer();
    	if(equip_kind.equals("Modem")) {
    		sqlBuf.append("SELECT MAX(trigger_history) triggerHistory  \n");
    		sqlBuf.append("FROM firmware_history his JOIN  firmware_trigger tri ON (tri.id = his.tr_id)   \n");
    		sqlBuf.append("                          JOIN  firmware firm ON (tri.src_firmware=firm.firmware_id AND  firm.ARM = :arm ) \n");
    		sqlBuf.append("WHERE his.equip_kind= :equip_kind  \n");
    		sqlBuf.append("AND his.equip_id= :equip_id \n");
    		sqlBuf.append("GROUP BY equip_id \n");
    	}else {
    		sqlBuf.append("SELECT MAX(trigger_history) triggerHistory  \n");
    		sqlBuf.append("FROM firmware_history his JOIN  firmware_trigger tri ON (tri.id = his.tr_id)   \n");
    		sqlBuf.append("                          JOIN  firmware firm ON (tri.src_firmware=firm.firmware_id ) \n");
    		sqlBuf.append("WHERE his.equip_kind= :equip_kind  \n");
    		sqlBuf.append("AND his.equip_id= :equip_id \n");
    		sqlBuf.append("GROUP BY equip_id \n");        		
    	}

		
		SQLQuery query = getSession().createSQLQuery(sqlBuf.toString());
		query = getSession().createSQLQuery(sqlBuf.toString());

    	if(equip_kind.equals("Modem")) {
    		query.setString("equip_kind", equip_kind);
    		query.setString("equip_id", equip_id);
    		query.setInteger("arm", arm);
    	}else{
    		query.setString("equip_kind", equip_kind);
    		query.setString("equip_id", equip_id);
    	}

    	int rt_Size = query.list().size();
    	String rtString = "";
    	
    	if(rt_Size>0){
    		rtString = String.valueOf(query.list().get(0));
    	}
    	
        return rtString;
    }
    
	/**
	 * history 테이블에 업데이트(cmd 호출후 작업)
	 **/
    public void insertFirmHistory(FirmwareHistory firmwareHistory)throws Exception{
    	logger.debug(this.getClass().getName()+":"+"insertFirmHistory()");
/*        pstmt.setString(17, firmHistoryBean.getTriggerId()+"_"+firmHistoryBean.getCnt());
        pstmt.setString(18, "NURI#"+AimirModel.MI_FIRMWAREHISTORY+"/"+firmHistoryBean.getTriggerId()+"_"+firmHistoryBean.getCnt());
*/
/*    		getHibernateTemplate().save(firmwareHistory);
    		getHibernateTemplate().flush();*/
//		System.out.println("============"+firmwareHistory.getMcu().getId());
    	
    	//transaction 문제로 인해.. sql문을 사용하였다.(방법이 있기는 할거 같지만.. 시간관계상...)
    	StringBuffer sqlBuf = new StringBuffer();
    	sqlBuf.append("    insert  \n");
    	sqlBuf.append("    into \n");
    	sqlBuf.append("        FIRMWARE_HISTORY \n");
    	sqlBuf.append("        (EQUIP_ID, EQUIP_KIND, EQUIP_MODEL, EQUIP_TYPE, EQUIP_VENDOR, ERROR_CODE, ISSUE_DATE,   \n");
    	if(firmwareHistory.getMcu() != null)
    		sqlBuf.append("         , MCU_ID, ");
    	sqlBuf.append("         OTA_STATE, OTA_STEP, TRIGGER_CNT, TRIGGER_HISTORY, TRIGGER_STATE, TRIGGER_STEP, TR_ID, IN_SEQ)   \n");
    	sqlBuf.append("    values \n");
    	sqlBuf.append("        (:equip_id, :equip_kind, :equip_model, :equip_type, :equip_vendor, :error_code, :issue_date,  \n");
    	if(firmwareHistory.getMcu() != null)
    		sqlBuf.append("         , :mcu_id, ");
    	sqlBuf.append("         :ota_state, :ota_step, :trigger_cnt, :trigger_history, :trigger_state, :trigger_step, :tr_id, :in_seq) \n");
    	

		SQLQuery query = getSession().createSQLQuery(sqlBuf.toString());
		query = getSession().createSQLQuery(sqlBuf.toString());		
		
		query.setString("equip_id", firmwareHistory.getEquipId());
		query.setString("equip_kind", firmwareHistory.getEquipKind());
		query.setString("equip_model", firmwareHistory.getEquipModel());
		query.setString("equip_type", firmwareHistory.getEquipType());
		query.setString("equip_vendor", firmwareHistory.getEquipVendor());
		query.setString("error_code", firmwareHistory.getErrorCode());
		query.setString("issue_date", firmwareHistory.getIssueDate());
		query.setInteger("ota_state", firmwareHistory.getOtaState().getState());
		query.setInteger("ota_step",firmwareHistory.getOtaStep().getStep());
		query.setInteger("trigger_cnt",firmwareHistory.getTriggerCnt());
		query.setString("trigger_history", firmwareHistory.getTriggerHistory());
		query.setInteger("trigger_state",firmwareHistory.getTriggerState().getCode());
		query.setInteger("trigger_step",firmwareHistory.getTriggerStep().getCode());
		query.setLong("tr_id", firmwareHistory.getTrId());
		query.setString("in_seq", firmwareHistory.getInSeq());
		if(firmwareHistory.getMcu() != null)
			query.setInteger("mcu_id", firmwareHistory.getMcu().getId());
		query.executeUpdate();

		getSession().flush();
    }
	
	/**
	 * history 테이블에 업데이트(cmd 호출 후 작업)  
	 **/
    public void updateFirmwareHistory(FirmwareHistory firmwareHistory,ArrayList<String> updateFirmwareHistory)throws Exception{
       	logger.debug(this.getClass().getName()+":"+"updateFirmwareHistory()");

       	for(int i=0; i<updateFirmwareHistory.size(); i++){
       		String equip_id = String.valueOf(updateFirmwareHistory.get(i));
             	StringBuffer sqlBuf = new StringBuffer();
           	sqlBuf.append("UPDATE FIRMWARE_HISTORY  \n");
        	sqlBuf.append("SET ERROR_CODE = :error_code  \n");
        	sqlBuf.append("WHERE equip_kind= :equip_kind  \n");
        	sqlBuf.append("AND equip_id= :equip_id \n");
        	sqlBuf.append("and tr_id = :tr_id \n");
        	SQLQuery query = getSession().createSQLQuery(sqlBuf.toString());
    		query = getSession().createSQLQuery(sqlBuf.toString());
    	    query.setInteger("tr_id", Integer.parseInt(String.valueOf(firmwareHistory.getId().getTrId())));
    		query.setString("equip_kind", firmwareHistory.getEquipKind());
    		query.setString("equip_id", equip_id);
    		query.setString("error_code", firmwareHistory.getErrorCode());
    	
        	query.executeUpdate();
       		
       	}
    }
    
    
	/**
	 * history 테이블에 업데이트(cmd 호출후 작업)
	 **/
    public void updateFirmHistory(FirmwareHistory firmwarehistory, Map<String, Object> param)throws Exception{
    
       	logger.debug(this.getClass().getName()+":"+"updateFirmHistory()");
       	String equip_kind = String.valueOf(param.get("equip_kind"));
       	int arm = Integer.parseInt(String.valueOf(param.get("arm")));
       	String equip_id = String.valueOf(param.get("equip_id"));
       	
    	StringBuffer sqlBuf = new StringBuffer();
    	sqlBuf.append("UPDATE FIRMWARE_HISTORY  \n");
    	sqlBuf.append("SET TRIGGER_HISTORY= :tr_history, TRIGGER_CNT=TRIGGER_CNT+1  \n");
    	sqlBuf.append("WHERE equip_kind= :equip_kind  \n");
    	sqlBuf.append("AND equip_id= :equip_id \n");
    	sqlBuf.append("and tr_id in( \n");
    	sqlBuf.append("            SELECT his.tr_id  \n");
    	sqlBuf.append("                FROM FIRMWARE_HISTORY his join FIRMWARE_TRIGGER tri on (tri.id = his.tr_id) \n");
    	if(equip_kind.equals("Modem")) {
        	sqlBuf.append("                                          join firmware firm on ( tri.SRC_FIRMWARE = firm.FIRMWARE_ID and  firm.arm= :arm ) \n");    		
    	}else{
        	sqlBuf.append("                                          join firmware firm on ( tri.SRC_FIRMWARE = firm.FIRMWARE_ID ) \n");
    	}
    	sqlBuf.append("        ) \n");
    	
		SQLQuery query = getSession().createSQLQuery(sqlBuf.toString());
		query = getSession().createSQLQuery(sqlBuf.toString());

		
//		firmHistoryBean.getTriggerHistory()+","+firmHistoryBean.getTriggerId()
//		System.out.println("firmwarehistory.getTrId()========================="+firmwarehistory.getTrId());
//		System.out.println("firmwarehistory.getTriggerHistory()========================="+firmwarehistory.getTriggerHistory());
		
    	if(equip_kind.equals("Modem")) {
    		query.setString("tr_history", firmwarehistory.getTriggerHistory()+","+firmwarehistory.getTrId());
    		query.setString("equip_kind", equip_kind);
    		query.setString("equip_id", equip_id);
    		query.setInteger("arm", arm);
    	}else{
    		query.setString("tr_history", firmwarehistory.getTriggerHistory()+","+firmwarehistory.getTrId());    		
    		query.setString("equip_kind", equip_kind);
    		query.setString("equip_id", equip_id);
    	}
    	
    	query.executeUpdate();
    	
    }
    
    
    /**
	 * OTAState 정보를 가지고 옮 (cmd 호출후 작업)
	 **/
    public  List<Object> getScheduleCheckOTAState(String equip_kind)throws Exception{
       	logger.debug(this.getClass().getName()+":"+"getScheduleCheckOTAState()");
       	
       	String format = "yyyyMMddHHmmss";//yyyymmddhh24miss
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar cal = Calendar.getInstance();
        String dateStr = sdf.format(cal.getTime());

       	StringBuffer sqlBuf = new StringBuffer();
       	sqlBuf.append("SELECT * FROM ( \n");
       	sqlBuf.append("     SELECT his.*,firm.arm,tri.src_firmware,tri.target_hwver,tri.target_fwver,tri.target_firmware,tri.target_fwbuild,tri.end_date,("+ dateStr+" - his.issue_date) as gap  \n");
       	sqlBuf.append("           FROM  \n");
       	sqlBuf.append("           ( \n");
       	sqlBuf.append("                SELECT everyhis.*                                                                   \n");
       	sqlBuf.append("                             FROM FIRMWARE_HISTORY everyhis,                                                  \n");
       	sqlBuf.append("                                  ( \n");
       	sqlBuf.append("                                    SELECT equip_id,  issue_date                                                              \n");
       	sqlBuf.append("                                    FROM FIRMWARE_HISTORY  \n");
       	sqlBuf.append("                                    WHERE equip_kind = '"+equip_kind+"'     \n");
       	sqlBuf.append("                ) lasthis                                                                     \n");
       	sqlBuf.append("                WHERE lasthis.equip_id=everyhis.equip_id                                              \n");
       	sqlBuf.append("                AND lasthis.issue_date=everyhis.issue_date \n");
       	sqlBuf.append("          ) his join FIRMWARE_TRIGGER TRI on ( HIS.TR_ID=TRI.ID     )     \n");
       	sqlBuf.append("                join FIRMWARE firm   on  (TRI.TARGET_FIRMWARE=firm.firmware_id  AND firm.equip_kind = '"+equip_kind+"'   ) \n");
       	sqlBuf.append(") A \n");
       	sqlBuf.append("WHERE NOT EXISTS ( \n");
       	sqlBuf.append("           SELECT* \n");
       	sqlBuf.append("           FROM                                                                                 \n");
       	sqlBuf.append("           ( \n");
       	sqlBuf.append("             SELECT everyhis.*                                                                   \n");
       	sqlBuf.append("             FROM FIRMWARE_HISTORY everyhis,                                                   \n");
       	sqlBuf.append("                  ( \n");
       	sqlBuf.append("                    SELECT equip_id,  issue_date                                                              \n");
       	sqlBuf.append("                    FROM FIRMWARE_HISTORY      \n");
       	sqlBuf.append("                    WHERE equip_kind = '"+equip_kind+"'                                                     \n");
       	sqlBuf.append("                  ) lasthis                                                               \n");
       	sqlBuf.append("             WHERE lasthis.equip_id=everyhis.equip_id                                              \n");
       	sqlBuf.append("             AND lasthis.issue_date=everyhis.issue_date                                     \n");
       	sqlBuf.append("           )  his join FIRMWARE_TRIGGER TRI on ( HIS.TR_ID=TRI.ID  AND HIS.TRIGGER_STEP = 4  AND HIS.TRIGGER_STATE = 0    )     \n");//succ
       	sqlBuf.append("                  join FIRMWARE firm   on  (TRI.TARGET_FIRMWARE=firm.firmware_id AND firm.equip_kind = '"+equip_kind+"'  ) \n");
       	sqlBuf.append(" 			WHERE A.EQUIP_ID = his.EQUIP_ID    \n");
       	sqlBuf.append(" 			AND A.TR_ID = his.tr_id  \n");
        sqlBuf.append(" 			AND A.IN_SEQ = his.in_seq \n");
        sqlBuf.append(" 			AND A.ISSUE_DATE = his.ISSUE_DATE       \n");
       	sqlBuf.append("        ) \n");
       	sqlBuf.append("AND NOT EXISTS ( \n");
       	sqlBuf.append("           SELECT * \n");
       	sqlBuf.append("           FROM                                                                                 \n");
       	sqlBuf.append("           ( \n");
       	sqlBuf.append("             SELECT everyhis.*                                                                   \n");
       	sqlBuf.append("             FROM FIRMWARE_HISTORY everyhis,                                         \n");
       	sqlBuf.append("                  ( \n");
       	sqlBuf.append("                    SELECT equip_id,  issue_date                                                              \n");
       	sqlBuf.append("                    FROM FIRMWARE_HISTORY      \n");
       	sqlBuf.append("                    WHERE equip_kind = '"+equip_kind+"'                                                     \n");
       	sqlBuf.append("                  ) lasthis                                                                     \n");
       	sqlBuf.append("               WHERE lasthis.equip_id=everyhis.equip_id                                              \n");
       	sqlBuf.append("             AND lasthis.issue_date=everyhis.issue_date                                          \n");
       	sqlBuf.append("           ) his join FIRMWARE_TRIGGER TRI on ( HIS.TR_ID=TRI.ID  AND HIS.OTA_STATE = 2    )     \n");//cancel
       	sqlBuf.append("                  join FIRMWARE firm   on  (TRI.TARGET_FIRMWARE=firm.firmware_id AND firm.equip_kind = '"+equip_kind+"'  )                                                  \n");
       	sqlBuf.append(" 			WHERE A.EQUIP_ID = his.EQUIP_ID    \n");
       	sqlBuf.append(" 			AND A.TR_ID = his.tr_id  \n");
        sqlBuf.append(" 			AND A.IN_SEQ = his.in_seq \n");
        sqlBuf.append(" 			AND A.ISSUE_DATE = his.ISSUE_DATE       \n");
       	sqlBuf.append("        ) \n");
       	sqlBuf.append("AND NOT EXISTS ( \n");
       	sqlBuf.append("           SELECT *  \n");
       	sqlBuf.append("           FROM                                                                                 \n");
       	sqlBuf.append("           ( \n");
       	sqlBuf.append("             SELECT everyhis.*                                                                   \n");
       	sqlBuf.append("             FROM FIRMWARE_HISTORY everyhis,                                                    \n");
       	sqlBuf.append("                  ( \n");
       	sqlBuf.append("                    SELECT equip_id,  issue_date                                                              \n");
       	sqlBuf.append("                    FROM FIRMWARE_HISTORY      \n");
       	sqlBuf.append("                    WHERE equip_kind = '"+equip_kind+"'                                                     \n");
       	sqlBuf.append("                  ) lasthis                                                                     \n");
       	sqlBuf.append("             WHERE lasthis.equip_id=everyhis.equip_id                                              \n");
       	sqlBuf.append("             AND lasthis.issue_date=everyhis.issue_date                                      \n");
       	sqlBuf.append("           ) his join FIRMWARE_TRIGGER TRI on ( HIS.TR_ID=TRI.ID  AND ((HIS.TRIGGER_STATE=1 AND HIS.OTA_STATE!=2) OR (HIS.OTA_STATE=1))  )     \n");//error
       	sqlBuf.append("                  join FIRMWARE firm   on  (TRI.TARGET_FIRMWARE=firm.firmware_id AND firm.equip_kind = '"+equip_kind+"'  )              \n");
       	sqlBuf.append(" 			WHERE A.EQUIP_ID = his.EQUIP_ID    \n");
       	sqlBuf.append(" 			AND A.TR_ID = his.tr_id  \n");
        sqlBuf.append(" 			AND A.IN_SEQ = his.in_seq \n");
        sqlBuf.append(" 			AND A.ISSUE_DATE = his.ISSUE_DATE       \n");       	
       	sqlBuf.append(")  \n");
       	sqlBuf.append("ORDER BY EQUIP_ID  \n");
		
       	logger.debug("dateStr="+dateStr);
       	logger.debug("equip_kind="+equip_kind);
       	logger.debug(sqlBuf.toString());
       	
		SQLQuery query = getSession().createSQLQuery(sqlBuf.toString());
		query = getSession().createSQLQuery(sqlBuf.toString());
    	
        return query.list();
    }
    
    public String equipTypeBytrID(Integer tr_id)throws Exception{
       	logger.debug(this.getClass().getName()+":"+"equipTypeBytrID()");       	

		StringBuffer sqlBuf = new StringBuffer();
		sqlBuf.append("select equip_type from firmware_history  \n");
		sqlBuf.append("where tr_id = :tr_id   \n");
		
		SQLQuery query = getSession().createSQLQuery(sqlBuf.toString());
		query = getSession().createSQLQuery(sqlBuf.toString());
		query.setInteger("tr_id", tr_id);

    	int rt_Size = query.list().size();
    	String rtString = "";
    	
    	if(rt_Size>0){
    		rtString = String.valueOf(query.list().get(0));
    	}
    	
        return rtString;
    }
    
	/**
	 * SCHEDULE에서 넘어온 SQL 실행
	 **/
    public void updateFirmHistoryBySchedule(String sql)throws Exception{
       	logger.debug(this.getClass().getName()+":"+"updateFirmHistoryBySchedule()");
       	logger.debug("sql="+sql);
		SQLQuery query = getSession().createSQLQuery(sql);
		query = getSession().createSQLQuery(sql);

    	query.executeUpdate();
    	
    }
    
    
}
