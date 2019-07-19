package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.FirmwareMCUDao;
import com.aimir.model.device.FirmwareBoard;
import com.aimir.model.device.FirmwareMCU;
import com.aimir.util.SQLWrapper;

@Repository(value = "firmwaremcuDao")
public class FirmwareMCUDaoImpl extends AbstractHibernateGenericDao<FirmwareMCU, Integer> implements FirmwareMCUDao {
	private static Log logger = LogFactory.getLog(FirmwareMCUDaoImpl.class);

	@Autowired
	protected FirmwareMCUDaoImpl(SessionFactory sessionFactory) {
		super(FirmwareMCU.class);
		super.setSessionFactory(sessionFactory);
	}
	
	/**
	 * MCU 배포 리스트 조회 
	 * */
	@SuppressWarnings("unchecked")
	public List<Object> getMcuFirmwareList(Map<String, Object> condition) {
		logger.debug(this.getClass().getName()+":"+"getMcuFirmwareList()");
		int devicemodel_id = Integer.parseInt((String)condition.get("devicemodel_id"));
		int firstResults = Integer.parseInt((String)condition.get("firstResults"));
		int maxResults = Integer.parseInt((String)condition.get("maxResults"));
		String equip_type = String.valueOf(condition.get("equip_type"));
		int equip_typeCD = Integer.parseInt(String.valueOf(condition.get("equip_typeCD")));
		
		/*
		 * 상세조건에 DEFICETYPE, supplier_id 도 추가 (MODEM, COID 도 같이) 
		 * */
		StringBuffer sbQuery = new StringBuffer();
		sbQuery.append(" SELECT sys_hw_version, sys_sw_version, sys_sw_revision, ar,  binaryfilename, firmware_id,writedate,released_date , writer  FROM (                  \n");
		sbQuery.append("        SELECT sys_hw_version, sys_sw_version,install_date as writedate,install_date as released_date, sys_sw_revision ,0 as ar,''as binaryfilename,'' as firmware_id, '-' as writer , sys_sw_revision as mcubild          \n");
		sbQuery.append("        FROM MCU                                  \n");
		sbQuery.append("        WHERE devicemodel_id = :devicemodel_id       \n");			
		sbQuery.append("        and supplier_id = :supplier_id     \n");			
		sbQuery.append("        and mcu_type = :equip_typeCD     \n");			
		sbQuery.append("        and sys_hw_version CONCAT sys_sw_version CONCAT sys_sw_revision not in (select hw_version CONCAT fw_version CONCAT build from firmware)     \n");		
		sbQuery.append("        union all           \n");
		sbQuery.append("        SELECT frm.hw_version as sys_hw_version, frm.fw_version as sys_sw_version,brd.writedate as writedate,frm.released_date as released_date,  \n");
		sbQuery.append("        frm.build as sys_sw_revision ,frm.arm as ar, frm.binaryfilename, frm.firmware_id, (select loginid from OPERATOR where id = brd.OPERATOR_ID ) as writer  , '' as mcubild   \n");
		sbQuery.append("        FROM FIRMWARE frm JOIN FIRMWAREBOARD brd  \n");
		sbQuery.append("        ON (frm.id = brd.firmware_id)                   \n");
		sbQuery.append("        WHERE frm.devicemodel_id = :devicemodel_id          \n");
		sbQuery.append("        and frm.supplier_id = :supplier_id          \n");
		sbQuery.append("        and frm.mcu_type = :equip_type          \n");			
		sbQuery.append(" ) A order by sys_hw_version desc, sys_sw_revision desc   \n");		
		
		SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQuery.toString()));
		query.setInteger("devicemodel_id", devicemodel_id);
		query.setInteger("supplier_id", Integer.parseInt(String.valueOf(condition.get("supplierId"))));
		query.setInteger("equip_typeCD", equip_typeCD);
		query.setString("equip_type", equip_type);
		if(firstResults > 0 || maxResults > 0 ){
			query.setFirstResult(firstResults);
			query.setMaxResults(maxResults);
		}else{
			query.setFirstResult(0);
			query.setMaxResults(2);
		}
		
    	return query.list();
	}
	/**
	 * 배포 리스트 조회에서 Equip Total 을 따로 조회 하는 쿼리 
	 * */
	@SuppressWarnings("unchecked")
	public String getFirmwareMcuListCNT(Map<String, Object> condition) {
		logger.debug(this.getClass().getName()+":"+"getFirmwareMcuListCNT()");
		int devicemodel_id = Integer.parseInt((String)condition.get("devicemodel_id"));
		String equip_type = String.valueOf(condition.get("equip_type"));
		int equip_typeCD = Integer.parseInt(String.valueOf(condition.get("equip_typeCD")));
		
		StringBuffer sbQuery = new StringBuffer();		
		sbQuery.append(" SELECT count(*) cnt FROM (      \n");
		sbQuery.append("        SELECT sys_hw_version, sys_sw_version,install_date as writedate,install_date as released_date, sys_sw_revision ,0 as ar,''as binaryfilename,'' as firmware_id, supplier_id             \n");
		sbQuery.append("        FROM MCU                                        \n");
		sbQuery.append("        WHERE devicemodel_id = :devicemodel_id          \n");			
		sbQuery.append("        and supplier_id = :supplier_id          \n");			
		sbQuery.append("        and mcu_type = :equip_typeCD          \n");		
		sbQuery.append("        and sys_hw_version CONCAT sys_sw_version CONCAT sys_sw_revision not in (select hw_version CONCAT fw_version CONCAT build from firmware)     \n");	
		sbQuery.append("        union all                                       \n");
		sbQuery.append("        SELECT frm.hw_version as sys_hw_version, frm.fw_version as sys_sw_version,brd.writedate as writedate,frm.released_date as released_date, frm.build as sys_sw_revision ,frm.arm as ar, frm.binaryfilename, frm.firmware_id, frm.supplier_id       \n");
		sbQuery.append("        FROM FIRMWARE frm JOIN FIRMWAREBOARD brd  \n");
		sbQuery.append("        ON (frm.id = brd.firmware_id)                   \n");
		sbQuery.append("        WHERE frm.devicemodel_id = :devicemodel_id          \n");
		sbQuery.append("        and frm.supplier_id = :supplier_id          \n");
		sbQuery.append("        and frm.mcu_type = :equip_type          \n");
		sbQuery.append(" ) A    \n");		
		
		SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQuery.toString()));
		query.setInteger("devicemodel_id", devicemodel_id);
		query.setInteger("supplier_id", Integer.parseInt(String.valueOf(condition.get("supplierId"))));
		query.setInteger("equip_typeCD", equip_typeCD);
		query.setString("equip_type", equip_type);		

    	return String.valueOf(query.list().get(0));
	}	
	
	/**
	 * MCU 배포 리스트 조회에서 Equip Total 을 따로 조회 하는 쿼리 (지역이 빠진 전체)
	 * */
	@SuppressWarnings("unchecked")
	public String getMcuEquipCnt(Map<String, Object> param) {
		logger.debug(this.getClass().getName()+":"+"getMcuEquipCnt()");
		int 	equip_typeCD = Integer.parseInt(String.valueOf(param.get("equip_typeCD")));
		String 	writer 		 = String.valueOf(param.get("writer"));
		
		StringBuffer sbQuery = new StringBuffer();
		
		//if(writer.equals("-")){//writer가 없으면 장비로 간주
		//	 sbQuery.append(" SELECT count(*) FROM FIRMWARE  \n");
		//	 sbQuery.append(" WHERE devicemodel_id = :devicemodel_id  \n");
		//	 sbQuery.append(" AND build  =  :sys_sw_revision     \n");
		//	 sbQuery.append(" AND fw_version  = :sys_sw_version  \n");
		//	 sbQuery.append(" AND hw_version  =  :sys_hw_version    \n");       
		//	 sbQuery.append(" AND supplier_id = :supplierId  \n");
		//	 sbQuery.append(" AND equip_type = :equip_type   \n");
		//}else{
	        sbQuery.append(" SELECT count(*) as equipCnt FROM mcu   \n");
	        sbQuery.append(" WHERE supplier_id = :supplierId   		\n");
			sbQuery.append(" AND devicemodel_id = :devicemodel_id   \n");
			sbQuery.append(" AND sys_sw_revision = :sys_sw_revision \n");
			sbQuery.append(" AND sys_sw_version  = :sys_sw_version  \n");
			sbQuery.append(" AND sys_hw_version  = :sys_hw_version  \n");
			sbQuery.append(" AND mcu_type  = :equip_typeCD  \n");
		//}
		

		
		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		query = getSession().createSQLQuery(sbQuery.toString());
		query.setInteger("supplierId",     Integer.parseInt(String.valueOf(param.get("supplierId"))));
		query.setInteger("devicemodel_id",       Integer.parseInt(String.valueOf(param.get("devicemodel_id"))));
		query.setString("sys_hw_version",  String.valueOf(param.get("hw_version")));
		query.setString("sys_sw_version",  String.valueOf(param.get("sw_version")));
		query.setString("sys_sw_revision", String.valueOf(param.get("build")));
		//if(writer.equals("-")){
		//	query.setString("equip_type",       String.valueOf(param.get("equip_type")));
		//}else{
			query.setInteger("equip_typeCD",       Integer.parseInt(String.valueOf(param.get("equip_typeCD"))));			
		//}

		
//    	result.add(query.list());
    	return String.valueOf(query.list().get(0));
	}
	
	
	/**
	 * 해당지역 MCU ID와 MCU별 장비 수 (getMcuEquipCnt() 함수와 같은 쿼리 이나 지역별로 세부 조건이 추가 되었다.)
	 * */
	@SuppressWarnings("unchecked")
	public String getDistriButeMcuIdCnt(Map<String, Object> param, String location_id,String location_name) {
		logger.debug(this.getClass().getName()+":"+"getDistriButeMcuIdCnt()");
		int equip_typeCD = Integer.parseInt(String.valueOf(param.get("equip_typeCD")));

		StringBuffer sbQuery = new StringBuffer();
		sbQuery.append(" SELECT  count(*) cnt                  \n");
		sbQuery.append(" FROM MCU t1                        \n");
		sbQuery.append(" WHERE t1.devicemodel_id= :devicemodel_id         \n");
		sbQuery.append(" AND t1.sys_hw_version=  :hw_version  \n");
		sbQuery.append(" AND t1.sys_sw_version=  :sw_version  \n");
		sbQuery.append(" AND t1.sys_sw_revision= :build       \n");
		sbQuery.append(" AND t1.location_id = :location_id    \n");
		sbQuery.append(" AND t1.mcu_type  = :equip_typeCD  \n");
		
		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		query = getSession().createSQLQuery(sbQuery.toString());
		query.setInteger("devicemodel_id", Integer.parseInt(String.valueOf(param.get("devicemodel_id"))));
		query.setString("hw_version", String.valueOf(param.get("hwVersion")));
		query.setString("sw_version", String.valueOf(param.get("swVersion")));
		query.setString("build", String.valueOf(param.get("swRevision")));
		query.setInteger("location_id", Integer.parseInt(location_id));
		query.setInteger("equip_typeCD",       Integer.parseInt(String.valueOf(param.get("equip_typeCD"))));

		return String.valueOf(query.list().get(0));
	}
	
	/**
	 * 해당지역 MCU ID와 MCU별 장비 리스트 
	 * */
	@SuppressWarnings("unchecked")
	public List<Object> getdistributeMcuIdDivList(Map<String, Object> param, String location_id,String location_name) {
		logger.debug(this.getClass().getName()+":"+"getdistributeMcuIdDivList()");
		int equip_typeCD = Integer.parseInt(String.valueOf(param.get("equip_typeCD")));
//		String gubun = String.valueOf(param.get("gubun"));

		StringBuffer sbQuery = new StringBuffer();
		/*sbQuery.append(" SELECT  t1.id sensorMcu,           \n");
		sbQuery.append("         t1.sys_id sys_id,          \n");
		sbQuery.append("         t1.id t1Id,            \n");
		sbQuery.append("         t1.location_id locid, \n");
		sbQuery.append("         t1.sys_hw_version , \n");
		sbQuery.append("         t1.sys_sw_version, \n");
		sbQuery.append("         t1.sys_sw_revision, \n");
		sbQuery.append("         t1.mcu_type, \n");
		sbQuery.append("         t1.sys_model, \n");
		sbQuery.append("         t1.id \n");//★★ 확인 필요함. 200번 서버는  db는 mcu_id가 있으나, 14번 서버는 없음.
*/		sbQuery.append(" SELECT  t1.sys_id,t1.id            \n");
		sbQuery.append(" FROM MCU t1                        \n");
		sbQuery.append(" WHERE t1.devicemodel_id= :devicemodel_id          \n");
		sbQuery.append(" AND t1.sys_hw_version=  :hw_version  \n");
		sbQuery.append(" AND t1.sys_sw_version=  :sw_version  \n");
		sbQuery.append(" AND t1.sys_sw_revision= :build       \n");
		sbQuery.append(" AND t1.location_id = :location_id    \n");	
		sbQuery.append(" AND t1.mcu_type  = :equip_typeCD  \n");
		sbQuery.append(" GROUP BY  t1.sys_id,t1.id     \n");
		
		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		query = getSession().createSQLQuery(sbQuery.toString());
		query.setInteger("devicemodel_id", Integer.parseInt(String.valueOf(param.get("devicemodel_id"))));
		query.setString("hw_version", String.valueOf(param.get("hwVersion")));
		query.setString("sw_version", String.valueOf(param.get("swVersion")));
		query.setString("build", String.valueOf(param.get("swRevision")));
		query.setInteger("location_id", Integer.parseInt(location_id));	
		query.setInteger("equip_typeCD", Integer.parseInt(String.valueOf(param.get("equip_typeCD"))));

    	return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> distributeWriterStatus(Map<String, Object> param){
		logger.debug(this.getClass().getName()+":"+"distributeWriterStatus()");
		StringBuffer sbQuery = new StringBuffer();
		sbQuery.append(" select brd.title, brd.content, brd.writedate,brd.operator_id     \n");
		sbQuery.append(" from FIRMWARE frm  JOIN FIRMWAREBOARD brd     \n");
		sbQuery.append(" ON (frm.id = brd.firmware_id)    \n");
		sbQuery.append(" where  frm.firmware_id= :firmware_id     \n");
		
		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		query = getSession().createSQLQuery(sbQuery.toString());
		query.setString("firmware_id", String.valueOf(param.get("firmware_id")));
    	return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> distributeLocationStatus(Map<String, Object> param){
		logger.debug(this.getClass().getName()+":"+"distributeLocationStatus()");
		StringBuffer sbQuery = new StringBuffer();
		sbQuery.append(" SELECT  localname,                                                                                                                                                                                                   \n "); 
		sbQuery.append("         tr_id,                                                                                                                                                                                                     \n ");
		sbQuery.append("         total,                                                                                                                                                                                                     \n ");
		sbQuery.append("         succ,                                                                                                                                                                                                      \n ");
		sbQuery.append("         (stat.total-stat.succ-stat.cancel-stat.error) as  pexec,                                                                                                                                                   \n ");
		sbQuery.append("         cancel,                                                                                                                                                                                                    \n ");
		sbQuery.append("         error ,                                                                                                                                                                                                    \n ");
		sbQuery.append("        trigger_step,  \n");
		sbQuery.append("        trigger_state, \n");
		sbQuery.append("        ota_step,      \n");
		sbQuery.append("        ota_state,      \n");		
		sbQuery.append("        equip_id      \n");		
		sbQuery.append("   FROM              (                                                                                                                                                                                                         \n ");
		sbQuery.append("                     select tri.id, his.tr_id ,his.trigger_step,his.trigger_state, his.ota_step, his.ota_state ,  equip_id as equip_id, log.id as locid , log.name as localname , log.parent_id as parentid,                                                                                                    \n ");
		sbQuery.append("                         COUNT( target.sys_id) AS total,                                                                                                                                                                    \n ");
		sbQuery.append("                           SUM                                                                                                                                                                                                 \n ");
		sbQuery.append("                           (                                                                                                                                                                                                   \n ");
		sbQuery.append("                               CASE                                                                                                                                                                                            \n ");
		sbQuery.append("                                   WHEN                                                                                                                                                                                        \n ");
		sbQuery.append("                                       his.trigger_step=4 AND his.trigger_state=0                                                                                                                                              \n ");
		sbQuery.append("                                   THEN 1                                                                                                                                                                                      \n ");
		sbQuery.append("                                   ELSE 0                                                                                                                                                                                      \n ");
		sbQuery.append("                               END                                                                                                                                                                                             \n ");
		sbQuery.append("                           ) AS SUCC,                                                                                                                                                                                          \n ");
		sbQuery.append("                           SUM                                                                                                                                                                                                 \n ");
		sbQuery.append("                           (                                                                                                                                                                                                   \n ");
		sbQuery.append("                               CASE                                                                                                                                                                                            \n ");
		sbQuery.append("                                   WHEN his.ota_state=2                                                                                                                                                                        \n ");
		sbQuery.append("                                   THEN 1                                                                                                                                                                                      \n ");
		sbQuery.append("                                   ELSE 0                                                                                                                                                                                      \n ");
		sbQuery.append("                               END                                                                                                                                                                                             \n ");
		sbQuery.append("                           ) AS CANCEL ,                                                                                                                                                                                       \n ");
		sbQuery.append("                           SUM                                                                                                                                                                                                 \n ");
		sbQuery.append("                           (                                                                                                                                                                                                   \n ");
		sbQuery.append("                               CASE                                                                                                                                                                                            \n ");
		sbQuery.append("                                   WHEN                                                                                                                                                                                        \n ");
		sbQuery.append("                                       (                                                                                                                                                                                       \n ");
		sbQuery.append("                                           his.trigger_state=1 AND his.ota_state!=2                                                                                                                                            \n ");
		sbQuery.append("                                       )                                                                                                                                                                                       \n ");
		sbQuery.append("                                       OR                                                                                                                                                                                      \n ");
		sbQuery.append("                                       (                                                                                                                                                                                       \n ");
		sbQuery.append("                                           his.ota_state=1                                                                                                                                                                     \n ");
		sbQuery.append("                                       )                                                                                                                                                                                       \n ");
		sbQuery.append("                                   THEN 1                                                                                                                                                                                      \n ");
		sbQuery.append("                                   ELSE 0                                                                                                                                                                                      \n ");
		sbQuery.append("                               END                                                                                                                                                                                             \n ");
		sbQuery.append("                           ) AS ERROR                                                                                                                                                                                          \n ");
		sbQuery.append("                 from                                                                                                                                                                                                          \n ");
		sbQuery.append("                 (                                                                                                                                                                                                             \n ");
		sbQuery.append("                     SELECT everyhis.*                                                                                                                                                                                         \n ");
		sbQuery.append("                     FROM FIRMWARE_HISTORY everyhis,                                                                                                                                                                           \n ");
		sbQuery.append("                     (                                                                                                                                                                                                         \n ");
		sbQuery.append("                         SELECT equip_id, MAX(issue_date) AS issue_date                                                                                                                                                        \n ");
		sbQuery.append("                         FROM FIRMWARE_HISTORY     where equip_kind = 'MCU'                                                                                                                                                                               \n ");
		sbQuery.append("                         GROUP BY equip_id                                                                                                                                                                                     \n ");
		sbQuery.append("                     )lasthis                                                                                                                                                                                                  \n ");
		sbQuery.append("                    WHERE lasthis.equip_id=everyhis.equip_id                                                                                                                                                                   \n ");
		sbQuery.append("                    AND lasthis.issue_date=everyhis.issue_date                                                                                                                                                                 \n ");
		sbQuery.append("                 ) his join FIRMWARE_TRIGGER tri on (his.tr_id=tri.id and tri.target_hwver= :hw_version AND tri.target_fwver= :fw_version  AND tri.target_firmware= :firmware_id  and tri.target_fwbuild = :build   )                                                                                  \n ");
//		sbQuery.append("                       join FIRMWARE firm on (tri.target_firmware=firm.firmware_id AND firm.hw_version= :hw_version AND firm.fw_version= :fw_version AND firm.build= :build  AND firm.firmware_id= :firmware_id  )   \n ");
		sbQuery.append("                       join MCU target on (target.sys_id = his.equip_id)                                                                                                                                                         \n ");
		sbQuery.append("                       join LOCATION log on (target.id=log.id )                                                                                                                                                                \n ");
		sbQuery.append("         GROUP BY  tri.id , his.tr_id,his.equip_id , log.id, log.name , log.parent_id ,his.trigger_step,his.trigger_state, his.ota_step, his.ota_state                                                                                                                                                        \n ");
		sbQuery.append("    ) stat                                                                                                                                                                                                                     \n ");
		logger.debug(sbQuery.toString());
		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		query = getSession().createSQLQuery(sbQuery.toString());
		query.setString("firmware_id", String.valueOf(param.get("firmware_Id")));
		query.setString("hw_version", String.valueOf(param.get("hw_version")));
		query.setString("fw_version", String.valueOf(param.get("fw_version")));
		query.setString("build", String.valueOf(param.get("build")));
		
    	return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> distributeTriggerIdStatus(Map<String, Object> param){
		logger.debug(this.getClass().getName()+":"+"distributeTriggerIdStatus()");
		StringBuffer sbQuery = new StringBuffer();
		sbQuery.append(" SELECT equip_id,                                                                                                                                                												\n");	
		sbQuery.append("        tr_id,                                                                                                                                                                  \n");
		sbQuery.append("        total,                                                                                                                                                                        \n");
		sbQuery.append("        succ,                                                                                                                                                                         \n");
		sbQuery.append("        TOTAL-SUCC-CANCEL-ERROR AS pexec,                                                                                                                                             \n");		
		sbQuery.append("        cancel,                                                                                                                                                                       \n");
		sbQuery.append("        error,                                                                                                                                                                         \n");
		sbQuery.append("        trigger_step,                                                                                                                                                                         \n");
		sbQuery.append("        trigger_state,                                                                                                                                                                         \n");
		sbQuery.append("        ota_step,                                                                                                                                                                          \n");
		sbQuery.append("        ota_state                                                                                                                                                                         \n");		
		sbQuery.append("   FROM                                                                                                                                                                               \n");
		sbQuery.append("        (SELECT his.tr_id, his.trigger_step,his.trigger_state, his.ota_step, his.ota_state ,                                                                                                                                                          \n");
		sbQuery.append("               equip_id as equip_id,                                                                                                                                          \n");
		sbQuery.append("               COUNT(target.sys_id) AS TOTAL,                                                                                                                                     \n");
		sbQuery.append("               SUM                                                                                                                                                                    \n");
		sbQuery.append("               (                                                                                                                                                                      \n");
		sbQuery.append("                   CASE                                                                                                                                                               \n");
		sbQuery.append("                       WHEN his.trigger_step=4                                                                                                                                        \n");
		sbQuery.append("                           AND his.trigger_state=0                                                                                                                                    \n");
		sbQuery.append("                       THEN 1                                                                                                                                                         \n");
		sbQuery.append("                       ELSE 0                                                                                                                                                         \n");
		sbQuery.append("                   END                                                                                                                                                                \n");
		sbQuery.append("               ) AS SUCC,                                                                                                                                                             \n");
		sbQuery.append("               SUM                                                                                                                                                                    \n");
		sbQuery.append("               (                                                                                                                                                                      \n");
		sbQuery.append("                   CASE                                                                                                                                                               \n");
		sbQuery.append("                       WHEN his.ota_state=2                                                                                                                                           \n");
		sbQuery.append("                       THEN 1                                                                                                                                                         \n");
		sbQuery.append("                       ELSE 0                                                                                                                                                         \n");
		sbQuery.append("                   END                                                                                                                                                                \n");
		sbQuery.append("               ) AS CANCEL ,                                                                                                                                                          \n");
		sbQuery.append("               SUM                                                                                                                                                                    \n");
		sbQuery.append("               (                                                                                                                                                                      \n");
		sbQuery.append("                   CASE                                                                                                                                                               \n");
		sbQuery.append("                       WHEN                                                                                                                                                           \n");
		sbQuery.append("                           (                                                                                                                                                          \n");
		sbQuery.append("                               his.trigger_state=1                                                                                                                                    \n");
		sbQuery.append("                               AND his.ota_state!=2                                                                                                                                   \n");
		sbQuery.append("                           )                                                                                                                                                          \n");
		sbQuery.append("                           OR                                                                                                                                                         \n");
		sbQuery.append("                           (                                                                                                                                                          \n");
		sbQuery.append("                               his.ota_state=1                                                                                                                                        \n");
		sbQuery.append("                           )                                                                                                                                                          \n");
		sbQuery.append("                       THEN 1                                                                                                                                                         \n");
		sbQuery.append("                       ELSE 0                                                                                                                                                         \n");
		sbQuery.append("                   END                                                                                                                                                                \n");
		sbQuery.append("               ) AS ERROR                                                                                                                                                             \n");
		sbQuery.append("          FROM                                                                                                                                                                        \n");
		sbQuery.append("               (                                                                                                                                                                      \n");
		sbQuery.append("                     SELECT everyhis.*                                                                                                                                                \n");
		sbQuery.append("                     FROM FIRMWARE_HISTORY everyhis,                                                                                                                                  \n");
		sbQuery.append("                     (                                                                                                                                                                \n");
		sbQuery.append("                         SELECT equip_id, MAX(issue_date) AS issue_date                                                                                                               \n");
		sbQuery.append("                         FROM FIRMWARE_HISTORY   where equip_kind = 'MCU'                                                                                                                                        \n");
		sbQuery.append("                         GROUP BY equip_id                                                                                                                                            \n");
		sbQuery.append("                     )lasthis                                                                                                                                                         \n");
		sbQuery.append("                    WHERE lasthis.equip_id=everyhis.equip_id                                                                                                                          \n");
		sbQuery.append("                    AND lasthis.issue_date=everyhis.issue_date                                                                                                                        \n");
		sbQuery.append("               ) his                                                                                                                                                                  \n");
//		sbQuery.append("                 join FIRMWARE_TRIGGER tri on (his.tr_id=tri.id AND tri.target_firmware= :firmware_id )                                             								 \n");
		sbQuery.append("              JOIN FIRMWARE_TRIGGER tri ON (  his.tr_id=tri.id and tri.target_hwver= :hw_version AND tri.target_fwver= :fw_version  AND tri.target_firmware= :firmware_id  and tri.target_fwbuild = :build )  	\n");	
//		sbQuery.append("                 join FIRMWARE firm on ( firm.hw_version= :hw_version AND firm.fw_version= :fw_version AND firm.build= :build AND firm.firmware_id=:firmware_id )     				 \n");
		sbQuery.append("                 join MCU target on (  target.sys_id = his.equip_id)                                                                          \n");
		sbQuery.append("         GROUP BY his.tr_id,                                                                                                                                                          \n");
		sbQuery.append("                  --his.mcu_id,                                                                                                                                                       \n");
		sbQuery.append("                  his.equip_id ,his.trigger_step,his.trigger_state, his.ota_step, his.ota_state                                                                                                                                                           \n");
		sbQuery.append("        ) STAT 										    \n");
		sbQuery.append(" ORDER BY tr_id DESC   									\n"); 

		logger.debug(sbQuery.toString());
		
		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		query = getSession().createSQLQuery(sbQuery.toString());
		query.setString("firmware_id", String.valueOf(param.get("firmware_Id")));
		query.setString("hw_version", String.valueOf(param.get("hw_version")));
		query.setString("fw_version", String.valueOf(param.get("fw_version")));
		query.setString("build", String.valueOf(param.get("build")));
		
    	return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> distributeMCULocStatusDetail(Map<String, Object> param){
		logger.debug(this.getClass().getName()+":"+"distributeMCULocStatusDetail()");
		StringBuffer sqlBuf = new StringBuffer();
		sqlBuf.append("SELECT * \n");
		sqlBuf.append("FROM        \n");
		sqlBuf.append("( \n");
		sqlBuf.append("    SELECT everyhis.*  \n");
		sqlBuf.append("    FROM FIRMWARE_HISTORY everyhis,  \n");
		sqlBuf.append("    ( \n");
		sqlBuf.append("        SELECT equip_id, MAX(issue_date) AS issue_date  \n");
		sqlBuf.append("        FROM FIRMWARE_HISTORY  \n");
		sqlBuf.append("        GROUP BY equip_id \n");
		sqlBuf.append("    )lasthis \n");
		sqlBuf.append("    WHERE lasthis.equip_id=everyhis.equip_id  \n");
		sqlBuf.append("    AND lasthis.issue_date=everyhis.issue_date  \n");
		sqlBuf.append(")his \n");
		sqlBuf.append(" JOIN FIRMWARE_TRIGGER tri ON (his.tr_id = tri.id AND tri.target_firmware=:firmware_id ) \n");
		sqlBuf.append(" JOIN MCU mc ON ( mc.sys_id=his.equip_id AND mc.location_id=:location_id) \n");
		sqlBuf.append("ORDER BY his.tr_id, his.equip_id \n");

		
		SQLQuery query = getSession().createSQLQuery(sqlBuf.toString());
		query = getSession().createSQLQuery(sqlBuf.toString());
		query.setString("firmware_id", String.valueOf(param.get("firmware_id")));
		query.setInteger("location_id", Integer.parseInt(String.valueOf(param.get("location_id"))));
		
    	return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> getReDistMcuList(Map<String, Object> param){
		logger.debug(this.getClass().getName()+":"+"getReDistMcuList()");
		String gubun = String.valueOf(param.get("gubun"));
		int tr_id = Integer.parseInt(String.valueOf(param.get("tr_id")));
		StringBuffer sqlBuf = new StringBuffer();
		sqlBuf.append("SELECT HIS.TR_ID,  \n");
		sqlBuf.append("       TARGET.SYS_ID,  \n");
		sqlBuf.append("       HIS.EQUIP_KIND,  \n");
		sqlBuf.append("       HIS.EQUIP_TYPE,  \n");
		sqlBuf.append("       HIS.EQUIP_VENDOR,  \n");
		sqlBuf.append("       HIS.EQUIP_MODEL,  \n");
		sqlBuf.append("       HIS.EQUIP_ID,  \n");
		sqlBuf.append("       TRI.SRC_FIRMWARE,  \n");
		sqlBuf.append("       TRI.TARGET_FIRMWARE,  \n");
		sqlBuf.append("       (SELECT BINARYFILENAME FROM FIRMWARE FRM WHERE FRM.FIRMWARE_ID=TRI.SRC_FIRMWARE ) AS BINARYFILENAME,  \n");
		sqlBuf.append("       (SELECT BINARYFILENAME FROM FIRMWARE FRM WHERE FRM.FIRMWARE_ID=TRI.TARGET_FIRMWARE ) AS TARGET_BINARYFILENAME,  \n");
		sqlBuf.append("       HIS.TRIGGER_CNT,  \n");
		sqlBuf.append("       HIS.TRIGGER_HISTORY  \n");
		sqlBuf.append("FROM   \n");
		sqlBuf.append("(  \n");
		sqlBuf.append("              SELECT EVERYHIS.*  \n");
		sqlBuf.append("              FROM FIRMWARE_HISTORY EVERYHIS,  \n");
		sqlBuf.append("              ( \n");
		sqlBuf.append("                          SELECT EQUIP_ID,  \n");
		sqlBuf.append("                                 ID,  \n");
		sqlBuf.append("                          MAX(ISSUE_DATE) AS ISSUE_DATE  \n");
		sqlBuf.append("                          FROM   \n");
		sqlBuf.append("                          ( \n");
		sqlBuf.append("                                SELECT TRI.ID,  \n");
		sqlBuf.append("                                     HIS.ISSUE_DATE,  \n");
		sqlBuf.append("                                     HIS.EQUIP_ID  \n");
		sqlBuf.append("                                FROM FIRMWARE_HISTORY his,  \n");
		sqlBuf.append("                                     firmware firm,  \n");
		sqlBuf.append("                                     FIRMWARE_TRIGGER tri  \n");
		sqlBuf.append("                               WHERE tri.id=HIS.TR_ID  \n");
		sqlBuf.append("                                     AND TRI.SRC_FIRMWARE=FIRM.FIRMWARE_ID  \n");
		sqlBuf.append(" 									AND TRI.target_firmware= :target_firmware  \n");		
//		sqlBuf.append("                                     AND FIRM.ARM= 0 \n");
		sqlBuf.append("                          ) T1 \n");
		sqlBuf.append("                          GROUP BY EQUIP_ID,ID  \n");
		sqlBuf.append("              ) LASTHIS  \n");
		sqlBuf.append("              WHERE LASTHIS.EQUIP_ID=EVERYHIS.EQUIP_ID  \n");
		sqlBuf.append("              AND LASTHIS.ISSUE_DATE=EVERYHIS.ISSUE_DATE  \n");
		sqlBuf.append(")his,  \n");
		sqlBuf.append(" FIRMWARE_TRIGGER tri,  \n");
		sqlBuf.append(" mcu target  \n");
		sqlBuf.append(" WHERE HIS.TR_ID=TRI.ID  \n");
		sqlBuf.append(" AND TARGET.SYS_ID = :sys_id \n");
		if(gubun.equals("step3")){
			sqlBuf.append(" AND TRI.ID = :tr_id  \n");
		}
		sqlBuf.append(" AND TARGET.SYS_ID=HIS.EQUIP_ID  \n");
//		sqlBuf.append(" AND target_firmware= :target_firmware  \n");
		
		if(String.valueOf(param.get("stateType")).equals("succ")){
			sqlBuf.append(" AND (( HIS.OTASTEP=10 OR HIS.OTASTEP=31 ) AND ( HIS.OTASTATE=0 ) OR (HIS.TRIGGERSTEP=4 AND HIS.TRIGGERSTATE=0))  \n");
		}
		//execute
		else if(String.valueOf(param.get("stateType")).equals("exec")){
			sqlBuf.append(" AND HIS.TRIGGERSTATE=0 AND HIS.OTASTATE=0 AND HIS.OTASTEP!=10 AND HIS.OTASTEP!=31 AND HIS.TRIGGERSTEP!=3) \n");
		}
		//cancel
		else if(String.valueOf(param.get("stateType")).equals("cancel")){
			sqlBuf.append(" AND HIS.OTASTATE=2 \n");
		}
		//error
		else if(String.valueOf(param.get("stateType")).equals("error")){
			sqlBuf.append(" AND ((HIS.TRIGGERSTATE=1 AND HIS.OTASTATE!=2) OR HIS.OTASTATE=1) \n");
		}
		logger.debug(sqlBuf.toString());
		SQLQuery query = getSession().createSQLQuery(sqlBuf.toString());
		query = getSession().createSQLQuery(sqlBuf.toString());
		query.setString("target_firmware", String.valueOf(param.get("target_firmware")));
		query.setString("sys_id", String.valueOf(param.get("mcu_id")));
		if(gubun.equals("step3")){
			query.setInteger("tr_id", tr_id);
		}
		
    	return query.list();
	}	
	
/*	@SuppressWarnings("unchecked")
	public String getMcuIdbyModemModelID(String devicemodel_id,String supplierId,String equip_type){
		logger.debug(this.getClass().getName()+":"+"getMcuIdbyModem()");
		StringBuffer sqlBuf = new StringBuffer();
		sqlBuf.append("SELECT mc.sys_id                                                  \n");
		sqlBuf.append("FROM modem mdm  join mcu mc on(mc.ID = mdm.MCU_ID)                                                                                                                                                                                                                \n");
		sqlBuf.append("WHERE mdm.DEVICEMODEL_ID = :devicemodel_id     \n");
		sqlBuf.append("AND  mdm.modem_type = :modem_type     \n");
		sqlBuf.append("AND  mdm.supplier_id = :supplier_id    \n");
		
		SQLQuery query = getSession().createSQLQuery(sqlBuf.toString());
		query = getSession().createSQLQuery(sqlBuf.toString());
		
		query.setInteger("devicemodel_id", Integer.parseInt(devicemodel_id));
		query.setInteger("supplier_id", Integer.parseInt(supplierId));
		query.setString("modem_type", equip_type);
		
    	return String.valueOf(query.list().get(0));
	}*/
	
	public FirmwareMCU get(String firmwareId) {
		return findByCondition("firmwareId", firmwareId);
	}
	
	@SuppressWarnings("unchecked")
	public void addFirmWareMCU(FirmwareMCU firmware,FirmwareBoard firmwareBoard)throws Exception {
		logger.debug(this.getClass().getName()+":"+"addFirmWareMCU()");
			getSession().save(firmware);
			getSession().save(firmwareBoard);
	}
	
	@SuppressWarnings("unchecked")
	public void updateFirmWareMCU(FirmwareMCU firmware,FirmwareBoard firmwareBoard)throws Exception {
		logger.debug(this.getClass().getName()+":"+"updateFirmWareMCU()");
			getSession().update(firmware);
			getSession().update(firmwareBoard);
	}
	
	@SuppressWarnings("unchecked")
	public String getIDbyMcuSysId(String sys_id){
		logger.debug(this.getClass().getName()+":"+"getIDbyMcuSysId()");
		StringBuffer sqlBuf = new StringBuffer();
		sqlBuf.append("select id from mcu \n");
		sqlBuf.append("where sys_id = :sys_id    \n");
		
		SQLQuery query = getSession().createSQLQuery(sqlBuf.toString());
		query = getSession().createSQLQuery(sqlBuf.toString());
		query.setString("sys_id", sys_id);
		
    	return String.valueOf(query.list().get(0));
	}
}
