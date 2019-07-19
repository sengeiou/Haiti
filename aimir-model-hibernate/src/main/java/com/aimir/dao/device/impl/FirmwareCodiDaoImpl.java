/**
 * Copyright Nuri Telecom Corp.
 * 파일명: FirmwareCodiDaoImpl
 * 작성일자/작성자 :Nuri com
 * @see 
 *
 * 펌웨어 관리자 페이지 Component
 * 
 * ============================================================================
 * 수정 내역
 * NO  수정일자           수정자             수정내역
 * 1.  2010.12.24  최창희             펌웨어 관리자 페이지 관련 함수 추가
 * 2.
 * ============================================================================
 */
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
import com.aimir.dao.device.FirmwareCodiDao;
import com.aimir.model.device.FirmwareBoard;
import com.aimir.model.device.FirmwareCodi;
import com.aimir.model.device.FirmwareMCU;
import com.aimir.model.device.FirmwareModem;

@Repository(value = "firmwarecodiDao")
public class FirmwareCodiDaoImpl extends AbstractHibernateGenericDao<FirmwareCodi, Integer> implements FirmwareCodiDao {
	private static Log logger = LogFactory.getLog(FirmwareCodiDaoImpl.class);

	@Autowired
	protected FirmwareCodiDaoImpl(SessionFactory sessionFactory) {
		super(FirmwareCodi.class);
		super.setSessionFactory(sessionFactory);
	}

	public FirmwareCodi get(String firmwareId) {
		return findByCondition("firmwareId", firmwareId);
	}
	
	
	/**
	 * Codi 배포 리스트 조회 
	 **/
	@SuppressWarnings("unchecked")
	public List<Object> getMcuCodiFirmwareList(Map<String, Object> condition) {
		int devicemodel_id = Integer.parseInt((String)condition.get("devicemodel_id"));
		int firstResults = Integer.parseInt((String)condition.get("firstResults"));
		int maxResults = Integer.parseInt((String)condition.get("maxResults"));
		String equip_type = String.valueOf(condition.get("equip_type"));
		String equip_kind = String.valueOf(condition.get("equip_kind"));
		int equip_typeCD = Integer.parseInt(String.valueOf(condition.get("equip_typeCD")));
		
		StringBuffer sbQuery = new StringBuffer();
		sbQuery.append(" SELECT sys_hw_version, sys_sw_version, sys_sw_revision, ar,  binaryfilename, firmware_id,writedate,released_date , writer  FROM (  	");                                                                                                                       
		sbQuery.append(" 		SELECT mcd.codi_hw_ver as sys_hw_version,mcd.codi_fw_ver as sys_sw_version, '' as writedate    ");
		sbQuery.append("               ,'' as released_date, mcd.codi_fw_build as sys_sw_revision,0 as ar,''as binaryfilename  ");
		sbQuery.append("               ,'' as firmware_id,  '-' as writer,mu.sys_sw_revision as mcubuild                          ");
		sbQuery.append("       FROM mcu mu INNER JOIN mcu_codi mcd ON( mu.supplier_id = :supplier_id AND mcd.id = mu.mcu_codi_id  )  ");                                                                                                                                                 
		sbQuery.append(" 		union all                                                             ");                                                                                                                                           
		sbQuery.append(" 		SELECT frm.hw_version as sys_hw_version, frm.fw_version as sys_sw_version,brd.writedate as writedate         ");
		sbQuery.append("               ,frm.released_date as released_date, frm.build as sys_sw_revision ,frm.arm as ar, frm.binaryfilename  ");
		sbQuery.append("               ,frm.firmware_id, (select loginid from OPERATOR where id = brd.OPERATOR_ID ) as writer, '' as mcubuild                       ");
		sbQuery.append("        FROM FIRMWARE frm JOIN FIRMWAREBOARD brd                       ");
		sbQuery.append("               ON (frm.id = brd.firmware_id)             					 ");
		sbQuery.append(" 		WHERE frm.equip_kind = :equip_kind                               ");
		sbQuery.append(" 		and frm.supplier_id = :supplier_id                                       ");
		sbQuery.append(" ) A    order by sys_hw_version   desc ,sys_sw_revision desc			 ");
		
		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		query = getSession().createSQLQuery(sbQuery.toString());
//		query.setInteger("devicemodel_id", devicemodel_id);
//		query.setString("equip_type", equip_type);
		query.setString("equip_kind", equip_kind);
		query.setInteger("supplier_id", Integer.parseInt(String.valueOf(condition.get("supplierId"))));
//		query.setInteger("device_typeCD", equip_typeCD);
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
	 * MCU 배포 리스트 조회 전체 count Paging Bar 기능 구현 하기 위해 필요.
	 * */
	@SuppressWarnings("unchecked")
	public String getFirmwareMcuCodiListCNT(Map<String, Object> condition) {
		int devicemodel_id = Integer.parseInt((String)condition.get("devicemodel_id"));
		String equip_type = String.valueOf(condition.get("equip_type"));
		String equip_kind = String.valueOf(condition.get("equip_kind"));
		int equip_typeCD = Integer.parseInt(String.valueOf(condition.get("equip_typeCD")));
		
		StringBuffer sbQuery = new StringBuffer();		
			sbQuery.append(" SELECT count(*) cnt  FROM (       																						");                                                                                                                       
			sbQuery.append(" 		SELECT mcd.codi_hw_ver as sys_hw_version,mcd.codi_fw_ver as sys_sw_version, '' as writedate    ");
			sbQuery.append("               ,'' as released_date, mcd.codi_fw_build as sys_sw_revision,0 as ar,''as binaryfilename  ");
			sbQuery.append("               ,'' as firmware_id, mu.supplier_id as supplier_id                           ");
			sbQuery.append("       FROM mcu mu INNER JOIN mcu_codi mcd ON( mu.supplier_id = :supplier_id AND mcd.id = mu.mcu_codi_id )  ");                                                                                                                                                 
			sbQuery.append(" 		union all                                                             ");                                                                                                                                           
			sbQuery.append(" 		SELECT frm.hw_version as sys_hw_version, frm.fw_version as sys_sw_version,brd.writedate as writedate         ");
			sbQuery.append("               ,frm.released_date as released_date, frm.build as sys_sw_revision ,frm.arm as ar, frm.binaryfilename  ");
			sbQuery.append("               ,frm.firmware_id, frm.supplier_id                             ");
			sbQuery.append("        FROM FIRMWARE frm JOIN FIRMWAREBOARD brd                       ");
			sbQuery.append("               ON (frm.id = brd.firmware_id)             					 ");
			sbQuery.append(" 		WHERE frm.equip_kind = :equip_kind                               ");
			sbQuery.append(" 		and frm.supplier_id = :supplier_id                                       ");
			sbQuery.append(" ) A       										 ");
			
			SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
			query = getSession().createSQLQuery(sbQuery.toString());
//			query.setInteger("devicemodel_id", devicemodel_id);
//			query.setString("equip_type", equip_type);
			query.setString("equip_kind", equip_kind);
			query.setInteger("supplier_id", Integer.parseInt(String.valueOf(condition.get("supplierId"))));
//			query.setInteger("device_typeCD", equip_typeCD);
			
    	return String.valueOf(query.list().get(0));
	}	
	
	/**
	 * 배포 리스트 조회에서 Equip Total 을 따로 조회 하는 쿼리 (지역을 뺀 전체 조건)
	 * */
	@SuppressWarnings("unchecked")
	public String getMcuCodiEquipCnt(Map<String, Object> param) {
		
		String writer = String.valueOf(param.get("writer"));
		String equip_type = String.valueOf(param.get("equip_type"));

		StringBuffer sbQuery = new StringBuffer();
		if(writer.equals("-")){//writer가 없으면 장비로 간주
			 sbQuery.append(" SELECT count(*) FROM FIRMWARE  \n");
			 sbQuery.append(" WHERE  build  =  :sys_sw_revision  \n");
//			 sbQuery.append(" AND devicemodel_id = :devicemodel_id     \n");
			 sbQuery.append(" AND fw_version  = :sys_sw_version  \n");
			 sbQuery.append(" AND hw_version  =  :sys_hw_version    \n");       
			 sbQuery.append(" AND supplier_id = :supplier_id  \n");
			 sbQuery.append(" AND equip_type = :equip_type   \n");
		}else{
			sbQuery.append(" SELECT count(*)  \n");
			sbQuery.append(" FROM  mcu T1  INNER JOIN mcu_codi T2   \n");
			sbQuery.append(" 		 ON ( \n");
			sbQuery.append("             T2.codi_fw_ver = :sys_sw_version AND  \n");
			sbQuery.append("             T2.codi_fw_build = :sys_sw_revision  AND \n");
			sbQuery.append("             T2.codi_hw_ver = :sys_hw_version AND \n");
			//sbQuery.append("             T1.location_id  = :location_id  AND \n");
			sbQuery.append("             T1.supplier_id = :supplier_id  AND  \n");
			sbQuery.append("             T1.mcu_codi_id = T2.ID   \n");
			sbQuery.append("             )    \n");
		}
        
		
		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		query = getSession().createSQLQuery(sbQuery.toString());
//		query.setInteger("devicemodel_id",       Integer.parseInt(String.valueOf(param.get("devicemodel_id"))));
		query.setString("sys_hw_version",  String.valueOf(param.get("hw_version")));
		query.setString("sys_sw_version",  String.valueOf(param.get("sw_version")));
		query.setString("sys_sw_revision", String.valueOf(param.get("build")));
		//query.setInteger("location_id", Integer.parseInt(String.valueOf(param.get("locaionId"))));
		query.setInteger("supplier_id", Integer.parseInt(String.valueOf(param.get("supplierId"))));
		if(writer.equals("-")){//writer가 없으면 장비로 간주
			query.setString("equip_type", equip_type);
//			query.setInteger("devicemodel_id",       Integer.parseInt(String.valueOf(param.get("devicemodel_id"))));
		}
		
//    	result.add(query.list());
    	return String.valueOf(query.list().get(0));
	}
	
	
	/**
	 * 해당지역  ID와 MCU별 장비 수(getMcuCodiEquipCnt()함수와 같은 쿼리 이나 지역별로 세부 조건이 추가 되었다)
	 * MCU_CODI 테이블에는 지역 컬럼이 없어 MCU테이블과 조인  
	 **/
	@SuppressWarnings("unchecked")
	public String getDistriButeCodiIdCnt(Map<String, Object> param, String location_id,String location_name) {
		
		StringBuffer sbQuery = new StringBuffer();
     
		sbQuery.append(" SELECT  count(*) cnt \n");			
		sbQuery.append(" FROM  mcu T1  INNER JOIN mcu_codi T2   \n");
		sbQuery.append(" 		 ON ( \n");
		//sbQuery.append("             T2.mcu_codi_device_id = :devicemodel_id AND \n");
//		sbQuery.append("             T2.mcu_codi_binding_id = T1.mcu_codi_id AND  \n");
		sbQuery.append("             T2.codi_fw_ver = :sys_sw_version AND  \n");
		sbQuery.append("             T2.codi_fw_build = :sys_sw_revision  AND \n");
		sbQuery.append("             T2.codi_hw_ver = :sys_hw_version AND \n");
		sbQuery.append("             T1.location_id  = :location_id  AND \n");
		sbQuery.append("             T1.supplier_id = :supplier_id   AND \n");
		sbQuery.append("             T1.mcu_codi_id = T2.ID   \n");
		sbQuery.append("             )    \n");

		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		query = getSession().createSQLQuery(sbQuery.toString());
//{equip_type=Codi, supplierId=22, hwVersion=1.0, fileName=SWAMM_0_0___1.0_1.0_106_false.ebl, equip_kind=Codi244, devicemodel_id=Codi, swRevision=106, equip_typeCD=244, swVersion=1.0}
		//query.setInteger("sys_id", Integer.parseInt(String.valueOf(param.get("mcuId"))));
		//query.setInteger("devicemodel_id", Integer.parseInt(String.valueOf(param.get("devicemodel_id"))));
		query.setString("sys_hw_version", String.valueOf(param.get("hwVersion")));
		query.setString("sys_sw_version", String.valueOf(param.get("swVersion")));
		query.setString("sys_sw_revision", String.valueOf(param.get("swRevision")));
		query.setInteger("location_id", Integer.parseInt(String.valueOf(param.get("location_id"))));
		query.setInteger("supplier_id", Integer.parseInt(String.valueOf(param.get("supplierId"))));
		return String.valueOf(query.list().get(0));
	}
	
	/**
	 * 해당지역 MCU ID와 MCU별 장비 리스트 (modem, codi는 id가 트리 형태로 다시 표시 된다.) 
	 **/
	@SuppressWarnings("unchecked")
	public List<Object> getdistributeCodiIdDivList(Map<String, Object> param, String location_id,String location_name) {

		StringBuffer sbQuery = new StringBuffer();

//		sbQuery.append(" SELECT  T1.SYS_ID , T1.LOCATION_ID  \n");			
		sbQuery.append(" SELECT  T1.SYS_ID, T1.ID   \n");			
		sbQuery.append(" FROM  mcu T1  INNER JOIN mcu_codi T2   \n");
		sbQuery.append(" 		 ON ( \n");
//		sbQuery.append("             T2.mcu_codi_binding_id = :sys_id AND \n");
		sbQuery.append("             T2.codi_fw_ver = :sys_sw_version AND  \n");
		sbQuery.append("             T2.codi_fw_build = :sys_sw_revision  AND \n");
		sbQuery.append("             T2.codi_hw_ver = :sys_hw_version AND \n");
		sbQuery.append("             T1.location_id  = :location_id  AND \n");
		sbQuery.append("             T1.supplier_id = :supplier_id AND  \n");
		sbQuery.append("            T1.mcu_codi_id = T2.ID   \n");
		sbQuery.append("             )    \n");
		sbQuery.append(" GROUP BY  T1.SYS_ID, T1.ID     \n");

		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		query = getSession().createSQLQuery(sbQuery.toString());
//		query.setInteger("sys_id", Integer.parseInt(String.valueOf(param.get("mcuId"))));
		query.setString("sys_hw_version", String.valueOf(param.get("hwVersion")));
		query.setString("sys_sw_version", String.valueOf(param.get("swVersion")));
		query.setString("sys_sw_revision", String.valueOf(param.get("swRevision")));
		query.setInteger("location_id", Integer.parseInt(location_id));
		query.setInteger("supplier_id", Integer.parseInt(String.valueOf(param.get("supplierId"))));
		
    	return query.list();
	}
	
	/**
	 * codi, modem만 실행 됨
	 * mcu별 장비 수를 클릭 하면 id가 Tree형태로 다시 표시 됨. (id count를 미리 출력_보여주기 위함)
	 **/
	@SuppressWarnings("unchecked")
	public String getDistriButeMcuCodiModelListCnt(Map<String, Object> param, String mcuId){
		String equip_kind = String.valueOf(param.get("equip_kind"));

		StringBuffer sbQuery = new StringBuffer();

		sbQuery.append(" SELECT  count(*) cnt \n");			
		sbQuery.append(" FROM  mcu T1  INNER JOIN mcu_codi T2   \n");
		sbQuery.append(" 		 ON ( \n");
//		sbQuery.append("             T2.mcu_codi_binding_id = :sys_id AND \n");
		sbQuery.append("             T2.codi_fw_ver = :sys_sw_version AND  \n");
		sbQuery.append("             T2.codi_fw_build = :sys_sw_revision  AND \n");
		sbQuery.append("             T2.codi_hw_ver = :sys_hw_version AND \n");
		sbQuery.append("             T1.location_id  = :location_id  AND \n");
		sbQuery.append("             T1.supplier_id = :supplier_id AND  \n");
		sbQuery.append("             T1.mcu_codi_id = T2.ID  \n");
		sbQuery.append("             )    \n");

		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		query = getSession().createSQLQuery(sbQuery.toString());
		query.setInteger("sys_id", Integer.parseInt(String.valueOf(param.get("mcuId"))));
		query.setString("sys_hw_version", String.valueOf(param.get("hwVersion")));
		query.setString("sys_sw_version", String.valueOf(param.get("swVersion")));
		query.setString("sys_sw_revision", String.valueOf(param.get("swRevision")));
		query.setInteger("location_id", Integer.parseInt(String.valueOf(param.get("locaionId"))));
		query.setInteger("supplier_id", Integer.parseInt(String.valueOf(param.get("supplierId"))));

		return String.valueOf(query.list().get(0));
	}
	
	/**
	 * codi, modem만 실행 됨
	 * mcu별 장비 수를 클릭 하면 id가 Tree형태로 다시 표시 됨. 
	 **/
	@SuppressWarnings("unchecked")
	public List<Object>  getDistriButeMcuCodiModelList(Map<String, Object> param, String mcuId){
		String equip_kind = String.valueOf(param.get("equip_kind"));
		
		StringBuffer sbQuery = new StringBuffer();


		sbQuery.append(" SELECT  T1.ID, T1.LOCATION_ID, T2.MCU_CODI_DEVICE_ID, T2.CODI_FW_VER, T2.CODI_FW_BUILD, T2.CODI_HW_VER  \n");			
		sbQuery.append(" FROM  mcu T1  INNER JOIN mcu_codi T2   \n");
		sbQuery.append(" 		 ON ( \n");
//		sbQuery.append("             T2.mcu_codi_binding_id = :sys_id AND \n");
		sbQuery.append("             T2.codi_fw_ver = :sys_sw_version AND  \n");
		sbQuery.append("             T2.codi_fw_build = :sys_sw_revision  AND \n");
		sbQuery.append("             T2.codi_hw_ver = :sys_hw_version AND \n");
		sbQuery.append("             T1.location_id  = :location_id  AND \n");
		sbQuery.append("             T1.supplier_id = :supplier_id  AND \n");
		sbQuery.append("             T1.mcu_codi_id = T2.ID  \n");
		sbQuery.append("             )    \n");

		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		query = getSession().createSQLQuery(sbQuery.toString());
		query.setInteger("sys_id", Integer.parseInt(String.valueOf(param.get("mcuId"))));
		query.setString("sys_hw_version", String.valueOf(param.get("hwVersion")));
		query.setString("sys_sw_version", String.valueOf(param.get("swVersion")));
		query.setString("sys_sw_revision", String.valueOf(param.get("swRevision")));
		query.setInteger("location_id", Integer.parseInt(String.valueOf(param.get("locaionId"))));
		query.setInteger("supplier_id", Integer.parseInt(String.valueOf(param.get("supplierId"))));

		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> distributeCodiLocationStatus(Map<String, Object> param){
		
		StringBuffer sqlBuf = new StringBuffer();
		sqlBuf.append("SELECT          \n");
		sqlBuf.append("       localname,  \n");
		sqlBuf.append("       tr_id,  \n");
		sqlBuf.append("       total,  \n");
		sqlBuf.append("       total-succ-cancel-error AS Pexec,  \n");
		sqlBuf.append("       succ,  \n");
		sqlBuf.append("       cancel,  \n");
		sqlBuf.append("       error,  \n");
		sqlBuf.append("       trigger_step,  \n");
		sqlBuf.append("       trigger_state, \n");
		sqlBuf.append("       ota_step,      \n");
		sqlBuf.append("       ota_state,      \n");	
		sqlBuf.append("       parent_id      \n");	
		sqlBuf.append("  FROM  \n");
		sqlBuf.append("       ( \n");
		sqlBuf.append("                SELECT mc.sys_id as codiid,his.trigger_step,his.trigger_state, his.ota_step, his.ota_state ,his.tr_id, \n");
		sqlBuf.append("                      loc.id as locid, \n");
		sqlBuf.append("                      loc.name as localname, \n");
		sqlBuf.append("                      loc.parent_id as parent_id  , \n");
		sqlBuf.append("                      count(mc.sys_id)   as total , \n");
		sqlBuf.append("                     SUM  \n");
		sqlBuf.append("                      (  \n");
		sqlBuf.append("                          CASE  \n");
		sqlBuf.append("                              WHEN  \n");
		sqlBuf.append("                                 his.trigger_step=4 AND his.trigger_state=0  \n");
		sqlBuf.append("                              THEN 1  \n");
		sqlBuf.append("                              ELSE 0  \n");
		sqlBuf.append("                          END  \n");
		sqlBuf.append("                      ) AS SUCC,  \n");
		sqlBuf.append("                      SUM  \n");
		sqlBuf.append("                      (  \n");
		sqlBuf.append("                          CASE  \n");
		sqlBuf.append("                              WHEN his.ota_state=2  \n");
		sqlBuf.append("                              THEN 1  \n");
		sqlBuf.append("                              ELSE 0  \n");
		sqlBuf.append("                          END  \n");
		sqlBuf.append("                      ) AS CANCEL ,  \n");
		sqlBuf.append("                      SUM  \n");
		sqlBuf.append("                      (  \n");
		sqlBuf.append("                          CASE  \n");
		sqlBuf.append("                              WHEN  \n");
		sqlBuf.append("                                  (  \n");
		sqlBuf.append("                                      his.trigger_state=1 AND his.ota_state!=2 \n");
		sqlBuf.append("                                  )  \n");
		sqlBuf.append("                                  OR  \n");
		sqlBuf.append("                                  (  \n");
		sqlBuf.append("                                      his.ota_state=1  \n");
		sqlBuf.append("                                  )  \n");
		sqlBuf.append("                              THEN 1  \n");
		sqlBuf.append("                              ELSE 0  \n");
		sqlBuf.append("                          END  \n");
		sqlBuf.append("                      ) AS ERROR  \n");
		sqlBuf.append("                FROM( \n");
		sqlBuf.append("                    SELECT everyhis.*  \n");
		sqlBuf.append("                    FROM FIRMWARE_HISTORY everyhis,     \n");
		sqlBuf.append("                         ( \n");
		sqlBuf.append("                          SELECT equip_id, MAX(issue_date) AS issue_date   \n");
		sqlBuf.append("                          FROM FIRMWARE_HISTORY     where equip_kind = 'Codi'        \n");
		sqlBuf.append("                          GROUP BY equip_id        \n");
		sqlBuf.append("                         ) lasthis  \n");
		sqlBuf.append("                  WHERE lasthis.equip_id=everyhis.equip_id  \n");
		sqlBuf.append("                  AND lasthis.issue_date=everyhis.issue_date       \n");
		sqlBuf.append("                )his  \n");
//		sqlBuf.append("                 join MCU_CODI codi on ( codi.codi_fw_ver = :fw_version  AND codi.codi_hw_ver= :hw_version  AND codi.codi_fw_build= :build  AND his.device_id+0 =  codi.mcu_codi_device_id) \n");
		sqlBuf.append("                 join FIRMWARE_TRIGGER tri on ( his.tr_id = tri.id  AND TRI.target_firmware= :firmware_id AND TRI.target_fwbuild = :build   AND TRI.target_fwver  = :fw_version AND TRI.target_hwver= :hw_version  ) \n");
		sqlBuf.append("                 join MCU mc on (mc.sys_id = his.equip_id) \n"); 
		sqlBuf.append("                 join LOCATION loc on (loc.id = mc.location_id) \n");
		sqlBuf.append("        GROUP BY  mc.sys_id , \n");
		sqlBuf.append("                  loc.id , \n");
		sqlBuf.append("                 loc.name, \n");
		sqlBuf.append("                 loc.parent_id , \n");
		sqlBuf.append("                 his.trigger_step,his.trigger_state, his.ota_step, his.ota_state,his.tr_id \n");
		sqlBuf.append("            ) STAT \n");

		logger.debug(sqlBuf.toString());
		
		SQLQuery query = getSession().createSQLQuery(sqlBuf.toString());
		query = getSession().createSQLQuery(sqlBuf.toString());
		query.setString("firmware_id", String.valueOf(param.get("firmware_Id")));
		query.setString("hw_version", String.valueOf(param.get("hw_version")));
		query.setString("fw_version", String.valueOf(param.get("fw_version")));
		query.setString("build", String.valueOf(param.get("build")));
		
    	return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> distributeCodiTriggerIdStatus(Map<String, Object> param){

		StringBuffer sqlBuf = new StringBuffer();
		sqlBuf.append("SELECT tr_id,   \n");
		sqlBuf.append("       mcuid,  \n");
		sqlBuf.append("       total,  \n");
		sqlBuf.append("       total-succ-cancel-error AS Pexec,  \n");
		sqlBuf.append("       succ,  \n");
		sqlBuf.append("       cancel,  \n");
		sqlBuf.append("       error,  \n");
		sqlBuf.append("       trigger_step,  \n");
		sqlBuf.append("       trigger_state, \n");
		sqlBuf.append("       ota_step,      \n");
		sqlBuf.append("       ota_state      \n");	
		sqlBuf.append("  FROM  \n");
		sqlBuf.append("       ( \n");
		sqlBuf.append("                SELECT mc.sys_id as mcuid, his.trigger_step,his.trigger_state, his.ota_step, his.ota_state , his.tr_id,  \n");
		sqlBuf.append("                      count(mc.sys_id)   as total , \n");
		sqlBuf.append("                     SUM  \n");
		sqlBuf.append("                      (  \n");
		sqlBuf.append("                          CASE  \n");
		sqlBuf.append("                              WHEN  \n");
		sqlBuf.append("                                 his.trigger_step=4 AND his.trigger_state=0  \n");
		sqlBuf.append("                              THEN 1  \n");
		sqlBuf.append("                              ELSE 0  \n");
		sqlBuf.append("                          END  \n");
		sqlBuf.append("                      ) AS SUCC,  \n");
		sqlBuf.append("                      SUM  \n");
		sqlBuf.append("                      (  \n");
		sqlBuf.append("                          CASE  \n");
		sqlBuf.append("                              WHEN his.ota_state=2  \n");
		sqlBuf.append("                              THEN 1  \n");
		sqlBuf.append("                              ELSE 0  \n");
		sqlBuf.append("                          END  \n");
		sqlBuf.append("                      ) AS CANCEL ,  \n");
		sqlBuf.append("                      SUM  \n");
		sqlBuf.append("                      (  \n");
		sqlBuf.append("                          CASE  \n");
		sqlBuf.append("                              WHEN  \n");
		sqlBuf.append("                                  (  \n");
		sqlBuf.append("                                      his.trigger_state=1 AND his.ota_state!=2 \n");
		sqlBuf.append("                                  )  \n");
		sqlBuf.append("                                  OR  \n");
		sqlBuf.append("                                  (  \n");
		sqlBuf.append("                                      his.ota_state=1  \n");
		sqlBuf.append("                                  )  \n");
		sqlBuf.append("                              THEN 1  \n");
		sqlBuf.append("                              ELSE 0  \n");
		sqlBuf.append("                          END  \n");
		sqlBuf.append("                      ) AS ERROR  \n");
		sqlBuf.append("                FROM( \n");
		sqlBuf.append("                    SELECT everyhis.*  \n");
		sqlBuf.append("                    FROM FIRMWARE_HISTORY everyhis,     \n");
		sqlBuf.append("                         ( \n");
		sqlBuf.append("                          SELECT equip_id, MAX(issue_date) AS issue_date   \n");
		sqlBuf.append("                          FROM FIRMWARE_HISTORY   where equip_kind = 'Codi'       \n");
		sqlBuf.append("                          GROUP BY equip_id        \n");
		sqlBuf.append("                         ) lasthis  \n");
		sqlBuf.append("                  WHERE lasthis.equip_id=everyhis.equip_id  \n");
		sqlBuf.append("                  AND lasthis.issue_date=everyhis.issue_date       \n");
		sqlBuf.append("                )his  \n");
		sqlBuf.append("                 join FIRMWARE_TRIGGER tri on ( his.tr_id = tri.id  AND TRI.target_firmware= :firmware_id AND TRI.target_fwbuild = :build   AND TRI.target_fwver  = :fw_version AND TRI.target_hwver= :hw_version  ) \n");
		sqlBuf.append("                 join mcu mc on (mc.sys_id = his.equip_id)               \n");
		sqlBuf.append("                 join mcu_codi codi on (mc.mcu_codi_id = codi.id)              \n");
//		sqlBuf.append("                 join FIRMWARE firm on ( firm.hw_version=:hw_version  AND firm.fw_version=:fw_version  AND firm.firmware_id= :build  )               \n");
		sqlBuf.append("        GROUP BY mc.sys_id , his.trigger_step,his.trigger_state, his.ota_step, his.ota_state,his.tr_id \n");
		sqlBuf.append("            ) STAT \n");

		logger.debug(sqlBuf.toString());
		
		SQLQuery query = getSession().createSQLQuery(sqlBuf.toString());
		query = getSession().createSQLQuery(sqlBuf.toString());
		query.setString("firmware_id", String.valueOf(param.get("firmware_Id")));
		query.setString("hw_version", String.valueOf(param.get("hw_version")));
		query.setString("fw_version", String.valueOf(param.get("fw_version")));
		query.setString("build", String.valueOf(param.get("build")));
		
    	return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> distributeCodiLocStatusDetail(Map<String, Object> param){

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
		sqlBuf.append(" JOIN FIRMWARE_TRIGGER tri ON (his.tr_id = tri.id AND tri.target_firmware= :firmware_id ) \n");
		sqlBuf.append(" JOIN MCU_CODI codi ON (codi.id = mc.mcu_codi_id) \n");
		sqlBuf.append(" JOIN MCU mc ON ( mc.sys_id=his.equip_id AND mc.location_id = :location_id ) \n");
		sqlBuf.append(" ORDER BY his.tr_id, his.equip_id \n");

		
		SQLQuery query = getSession().createSQLQuery(sqlBuf.toString());
		query = getSession().createSQLQuery(sqlBuf.toString());
		query.setString("firmware_id", String.valueOf(param.get("firmware_id")));
		query.setInteger("location_id", Integer.parseInt(String.valueOf(param.get("location_id"))));
		
    	return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> getReDistCodiList(Map<String, Object> param){
		logger.debug(this.getClass().getName()+":"+"getReDistCodiList()");
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
		sqlBuf.append("       (SELECT BINARYFILENAME FROM FIRMWARE FRM WHERE FRM.FIRMWARE_ID=TRI.SRC_FIRMWARE ) AS BINARYFILENAME  \n");
		sqlBuf.append("       (SELECT BINARYFILENAME FROM FIRMWARE FRM WHERE FRM.FIRMWARE_ID=TRI.TARGET_FIRMWARE ) AS TARGET_BINARYFILENAME,  \n");
		sqlBuf.append("       HIS.TRIGGER_CNT,  \n");
		sqlBuf.append("       HIS.TRIGGER_HISTORY  \n");
		sqlBuf.append("FROM   \n");
		sqlBuf.append("(  \n");
		sqlBuf.append("              SELECT EVERYHIS.*  \n");
		sqlBuf.append("              FROM FIRMWARE_HISTORY EVERYHIS,  \n");
		sqlBuf.append("              ( \n");
		sqlBuf.append("                          SELECT ID,  \n");
		sqlBuf.append("                          		EQUIP_ID,  \n");
		sqlBuf.append("                          MAX(ISSUE_DATE) AS ISSUE_DATE  \n");
		sqlBuf.append("                          FROM   \n");
		sqlBuf.append("                          ( \n");
		sqlBuf.append("                                SELECT TRI.ID,  \n");
		sqlBuf.append("                                		HIS.ISSUE_DATE,  \n");
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
		sqlBuf.append(" mcu target , \n");
		sqlBuf.append(" mcu_codi mcucodi \n");
		sqlBuf.append(" WHERE HIS.TR_ID=TRI.ID  \n");
		if(gubun.equals("step3")){
			sqlBuf.append(" AND TRI.ID = :tr_id  \n");
		}
		sqlBuf.append(" AND target.mcu_codi_id = mcucodi.id \n");
		sqlBuf.append(" AND target.sys_id = his.equip_id \n");
//		sqlBuf.append(" AND target_firmware= :target_firmware \n");
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
//		sqlBuf.append(" AND (( HIS.OTA_STEP=:ota_step OR HIS.OTA_STEP=:ota_step ) AND ( HIS.OTA_STATE=:ota_state ) OR (HIS.TRIGGER_STEP=:trigger_step AND HIS.TRIGGER_STATE=:trigger_state))   \n");

		SQLQuery query = getSession().createSQLQuery(sqlBuf.toString());
		query = getSession().createSQLQuery(sqlBuf.toString());
		query.setString("target_firmware", String.valueOf(param.get("target_firmware")));
		if(gubun.equals("step3")){
			query.setInteger("tr_id", tr_id);
		}
    	return query.list();
	}	
	
	@SuppressWarnings("unchecked")
	public String getMcuBuildByCodiFirmware(Map<String, Object> param){

		StringBuffer sqlBuf = new StringBuffer();
		sqlBuf.append("SELECT mu.sys_sw_revision from MCU_codi cod join mcu mu on (cod.id = mu.MCU_CODI_ID) \n");
		sqlBuf.append("where cod.CODI_FW_VER = :codi_fw_ver \n");
		sqlBuf.append("and cod.CODI_FW_BUILD = :codi_fw_build \n");
		sqlBuf.append("and cod.CODI_HW_VER = :codi_hw_ver \n");

		SQLQuery query = getSession().createSQLQuery(sqlBuf.toString());
		query = getSession().createSQLQuery(sqlBuf.toString());
		query.setString("codi_fw_ver", String.valueOf(param.get("sw_version")));
		query.setString("codi_hw_ver", String.valueOf(param.get("hw_version")));
		query.setString("codi_fw_build", String.valueOf(param.get("build")));
		
    	return String.valueOf(query.list().get(0));
	}
	
	
	@SuppressWarnings("unchecked")
	public void addFirmWareCodi(FirmwareCodi firmware,FirmwareBoard firmwareBoard)throws Exception {
			getSession().save(firmware);
			getSession().save(firmwareBoard);
	}
	
	@SuppressWarnings("unchecked")
	public void updateFirmWareCodi(FirmwareCodi firmware,FirmwareBoard firmwareBoard)throws Exception {
			getSession().update(firmware);
			getSession().update(firmwareBoard);
	}
	
}
