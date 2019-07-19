/**
 * Copyright Nuri Telecom Corp.
 * 파일명: FirmwareModemDaoImpl
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
import com.aimir.dao.device.FirmwareModemDao;
import com.aimir.model.device.FirmwareBoard;
import com.aimir.model.device.FirmwareModem;
import com.aimir.util.SQLWrapper;

@Repository(value = "firmwaremodemDao")
public class FirmwareModemDaoImpl extends AbstractHibernateGenericDao<FirmwareModem, Integer> implements FirmwareModemDao {
	private static Log logger = LogFactory.getLog(FirmwareModemDaoImpl.class);

	@Autowired
	protected FirmwareModemDaoImpl(SessionFactory sessionFactory) {
		super(FirmwareModem.class);
		super.setSessionFactory(sessionFactory);
	}

	public FirmwareModem get(String firmwareId) {
		return findByCondition("firmwareId", firmwareId);
	}
	
	/**
	 * modem 배포 리스트 조회 
	 * */
	@SuppressWarnings("unchecked")
	public List<Object> getModemFirmwareList(Map<String, Object> condition) {
		int devicemodel_id = Integer.parseInt((String)condition.get("devicemodel_id"));
		int firstResults = Integer.parseInt((String)condition.get("firstResults"));
		int maxResults = Integer.parseInt((String)condition.get("maxResults"));
		String equip_kind = (String)condition.get("equip_kind");
		String equip_type = String.valueOf(condition.get("equip_type"));
		int equip_typeCD = Integer.parseInt(String.valueOf(condition.get("equip_typeCD")));
		
		/*
		 * 상세조건에 DEFICETYPE 도 추가 (MODEM, COID 도 같이) 
		 * */
		StringBuffer sbQuery = new StringBuffer();
		if(equip_type.equals("MMIU")||equip_type.equals("IEIU")){
			sbQuery.append(" SELECT sys_hw_version, sys_sw_version, '-' sys_sw_revision, ar,  binaryfilename, firmware_id,writedate,released_date , writer  FROM (   ");
		}else{
			sbQuery.append(" SELECT sys_hw_version, sys_sw_version, sys_sw_revision, ar,  binaryfilename, firmware_id,writedate,released_date , writer  FROM (   ");			
		}
	    sbQuery.append("         SELECT mdm.HW_VER as sys_hw_version, mdm.FW_VER as sys_sw_version, mdm.INSTALL_DATE as writedate     ");                       
	    sbQuery.append("                  ,'' as released_date, mdm.FW_REVISION as sys_sw_revision,0 as ar,''as binaryfilename        ");
		if(equip_type.equals("MMIU")||equip_type.equals("IEIU")){
			sbQuery.append("                    ,'' as firmware_id, '-' as writer,  mdm.FW_REVISION as build     ");
			sbQuery.append("         FROM modem mdm                                                                                       ");
		}else{
			sbQuery.append("                    ,'' as firmware_id, '-' as writer, mc.sys_sw_revision as mcubuild                                                            ");
			sbQuery.append("         FROM modem mdm      join mcu mc on (mc.id = mdm.mcu_id)                                                                                      ");			
		}
		sbQuery.append("         WHERE mdm.DEVICEMODEL_ID = :devicemodel_id                                                           ");                                                                                                                                
		sbQuery.append("         and mdm.modem_type = :equip_type                                                                   ");                                                                                                                                
		sbQuery.append("         and mdm.supplier_id = :supplier_id                                                                   ");      
		sbQuery.append("         and mdm.HW_VER CONCAT mdm.FW_VER CONCAT mdm.FW_REVISION not in (select hw_version CONCAT fw_version CONCAT build from firmware)     \n");
		sbQuery.append("        union all                                                                                             ");
		sbQuery.append(" 		SELECT frm.hw_version as sys_hw_version, frm.fw_version as sys_sw_version,brd.writedate as writedate  ");
		sbQuery.append("               ,frm.released_date as released_date, frm.build as sys_sw_revision ,frm.arm as ar, frm.binaryfilename   ");
		sbQuery.append("               ,frm.firmware_id, (select loginid from OPERATOR where id = brd.OPERATOR_ID ) as writer , '' as mcubuild                                                                           ");
		sbQuery.append("        FROM FIRMWARE frm JOIN FIRMWAREBOARD brd                                                                ");
		sbQuery.append("               ON (frm.id = brd.firmware_id)                                                                          ");
		sbQuery.append(" 		WHERE frm.devicemodel_id = :devicemodel_id                                                                        ");                                                                                                                                               
		sbQuery.append(" 		and frm.supplier_id = :supplier_id                                                                               ");
		sbQuery.append(" 		and frm.modem_type = :equip_type                                       ");
		if(equip_type.equals("ZEUMBus")){
			sbQuery.append(" 		and frm.arm = 1                                       ");
		}
		sbQuery.append(" ) A order by sys_hw_version    desc ,sys_sw_revision desc                                                                                 ");

		SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQuery.toString()));
		query.setInteger("devicemodel_id", devicemodel_id);
		query.setInteger("supplier_id", Integer.parseInt(String.valueOf(condition.get("supplierId"))));
//		query.setInteger("equip_typeCD", equip_typeCD);
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
	 * Modem 배포 리스트 조회 전체 count Paging Bar 기능 구현 하기 위해 필요.
	 * */
	@SuppressWarnings("unchecked")
	public String getFirmwareModemListCNT(Map<String, Object> condition) {
		int devicemodel_id = Integer.parseInt((String)condition.get("devicemodel_id"));
		String equip_type = String.valueOf(condition.get("equip_type"));
		int equip_typeCD = Integer.parseInt(String.valueOf(condition.get("equip_typeCD")));

		StringBuffer sbQuery = new StringBuffer();		
			sbQuery.append(" SELECT count(*) cnt  FROM (       ");
			sbQuery.append("         SELECT mdm.HW_VER as sys_hw_version, mdm.FW_VER as sys_sw_version, mdm.INSTALL_DATE as writedate     ");                       
		    sbQuery.append("                  ,'' as released_date, mdm.FW_REVISION as sys_sw_revision,0 as ar,''as binaryfilename        ");
			sbQuery.append("                    ,'' as firmware_id, mdm.supplier_id as supplier_id                                                      ");
			sbQuery.append("         FROM modem mdm                                                                                       ");                                                                                                                          
			sbQuery.append("         WHERE mdm.DEVICEMODEL_ID = :devicemodel_id                                                           ");                                                                                                                                
			sbQuery.append("         and mdm.modem_type = :equip_type                                                                   ");                                                                                                                                
			sbQuery.append("         and mdm.supplier_id = :supplier_id                                                                   ");
			sbQuery.append("         and mdm.HW_VER CONCAT mdm.FW_VER CONCAT mdm.FW_REVISION not in (select hw_version CONCAT fw_version CONCAT build from firmware)     \n");
			sbQuery.append("        union all                                                                                             ");
			sbQuery.append(" 		SELECT frm.hw_version as sys_hw_version, frm.fw_version as sys_sw_version,brd.writedate as writedate  ");
			sbQuery.append("               ,frm.released_date as released_date, frm.build as sys_sw_revision ,frm.arm as ar, frm.binaryfilename   ");
			sbQuery.append("               ,frm.firmware_id, frm.supplier_id                                                                      ");
			sbQuery.append("        FROM FIRMWARE frm JOIN FIRMWAREBOARD brd                                                                ");
			sbQuery.append("               ON (frm.id = brd.firmware_id)                                                                          ");
			sbQuery.append(" 		WHERE frm.devicemodel_id = :devicemodel_id                                                                        ");                                                                                                                                               
			sbQuery.append(" 		and frm.supplier_id = :supplier_id                                                                               ");
			sbQuery.append(" 		and frm.modem_type = :equip_type                                       ");
			sbQuery.append(" ) A                                                                                          ");

			SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQuery.toString()));
			query.setInteger("devicemodel_id", devicemodel_id);
			query.setInteger("supplier_id", Integer.parseInt(String.valueOf(condition.get("supplierId"))));
//			query.setInteger("equip_typeCD", equip_typeCD);
			query.setString("equip_type", equip_type);


		
    	return String.valueOf(query.list().get(0));
	}	
	
	/**
	 * 배포 리스트 조회에서 Equip Total 을 따로 조회 하는 쿼리 (지역이 빠진 전체)
	 * */
	@SuppressWarnings("unchecked")
	public String getModemEquipCnt(Map<String, Object> param) {
		String equip_type = String.valueOf(param.get("equip_type"));
		String writer = String.valueOf(param.get("writer"));
		String build = String.valueOf(param.get("build")).toUpperCase();
		String sw_version = String.valueOf(param.get("sw_version")).toUpperCase();
//		System.out.println(sw_version);
		
		StringBuffer sbQuery = new StringBuffer();
		
		if(writer.equals("-")){//writer가 없으면 장비로 간주
		
			 sbQuery.append(" SELECT count(*) FROM FIRMWARE  \n");
			 sbQuery.append(" WHERE devicemodel_id = :devicemodel_id  \n");
			 if(!equip_type.equals("MMIU")&&!equip_type.equals("IEIU")){
			 sbQuery.append(" AND build  =  :sys_sw_revision     \n");
			 }  
			 sbQuery.append(" AND fw_version  = :sys_sw_version  \n");
			 sbQuery.append(" AND hw_version  =  :sys_hw_version    \n");       
			 sbQuery.append(" AND supplier_id = :supplier_id  \n");
			 sbQuery.append(" AND equip_type = :equip_type   \n");
				 
		}else{
	        sbQuery.append(" SELECT count(*) as equipCnt FROM modem  \n");
	        sbQuery.append(" WHERE devicemodel_id = :devicemodel_id  \n");
	        if(!equip_type.equals("MMIU")&&!equip_type.equals("IEIU")){
	        	sbQuery.append(" AND fw_revision  = :sys_sw_revision   \n");        	
	        }
			sbQuery.append(" AND fw_ver  = :sys_sw_version       \n");
			sbQuery.append(" AND hw_ver  = :sys_hw_version       \n");
			sbQuery.append(" AND supplier_id = :supplier_id    \n");
			sbQuery.append(" AND modem_type = :equip_type    \n");
		}
		
		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		query = getSession().createSQLQuery(sbQuery.toString());
		query.setInteger("devicemodel_id",       Integer.parseInt(String.valueOf(param.get("devicemodel_id"))));
		query.setString("sys_hw_version",  String.valueOf(param.get("hw_version")));
		query.setString("sys_sw_version",  sw_version);
		if(!equip_type.equals("MMIU")&&!equip_type.equals("IEIU")){
		query.setString("sys_sw_revision", build);
		}
		query.setInteger("supplier_id", Integer.parseInt(String.valueOf(param.get("supplierId"))));
		query.setString("equip_type", equip_type);
		
    	return String.valueOf(query.list().get(0));
	}
	
	/**
	 * 해당지역  ID와 MCU별 장비 수 (getFirmwareModemListCNT() 함수와 같은 쿼리이나 지역별 세부 검색 조건이 추가 되었음)
	 **/
	@SuppressWarnings("unchecked")
	public String getDistriButeModemIdCnt(Map<String, Object> param, String location_id,String location_name) {
		
		String equip_kind = String.valueOf(param.get("equip_kind"));
		String equip_type = String.valueOf(param.get("equip_type"));
		String sw_version = String.valueOf(param.get("swVersion")).toUpperCase();

		StringBuffer sbQuery = new StringBuffer();
		sbQuery.append(" SELECT  count(*) cnt                  \n");
		sbQuery.append(" FROM modem t1                        \n");
		sbQuery.append(" WHERE t1.devicemodel_id= :devicemodel_id         \n");
		sbQuery.append(" AND t1.hw_ver=  :hw_version  \n");
		sbQuery.append(" AND t1.fw_ver=  :sw_version  \n");
		if(!equip_type.equals("MMIU")&&!equip_type.equals("IEIU")){
		sbQuery.append(" AND t1.fw_revision= :build       \n");
		}
		sbQuery.append(" AND t1.location_id = :location_id    \n");
		sbQuery.append(" AND t1.supplier_id = :supplier_id    \n");
		sbQuery.append(" AND t1.modem_type = :equip_type    \n");
		
		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		query = getSession().createSQLQuery(sbQuery.toString());
		query.setInteger("devicemodel_id", Integer.parseInt(String.valueOf(param.get("devicemodel_id"))));
		query.setString("hw_version", String.valueOf(param.get("hwVersion")));
		query.setString("sw_version", sw_version);
		if(!equip_type.equals("MMIU")&&!equip_type.equals("IEIU")){
		query.setString("build", String.valueOf(param.get("swRevision")));
		}
		query.setInteger("location_id", Integer.parseInt(location_id));
		query.setInteger("supplier_id", Integer.parseInt(String.valueOf(param.get("supplierId"))));
		query.setString("equip_type", equip_type);

		return String.valueOf(query.list().get(0));
	}
	
	/**
	 * 해당지역 MCU ID와 MCU별 장비 리스트 (modem, codi는 id가 트리 형태로 다시 표시 된다.) 
	 **/
	@SuppressWarnings("unchecked")
	public List<Object> getdistributeModemIdDivList(Map<String, Object> param, String location_id,String location_name) {
		String equip_type = String.valueOf(param.get("equip_type"));
		
		/*
		 *   mcu테이블의 sys_id 를 가지고 올려면 modem테이블의 Key 값인 device_serial컬럼에 해당하는 modem.mcu_id= mcu.id 에 해당하는  sysid 를 가지고 오면 됨 
		 *   
		 *               			     ↗
		 *  ONE TO MANY	 MCU -O------O->  MODEM     MMIU같은경우 1:0
		 *                            ↘
		 *                            
		 *  단, Equip_type에서 MMIU, IEIU는 MCU테이블을 붙는것이 아니라 따로 관리가 되는 형식으로 MCU와 같은 형식으로 출력 하도록 Logic을 잡아야 함.                       
		 *   * */
		
		StringBuffer sbQuery = new StringBuffer();
//		sbQuery.append(" SELECT   t1.modem_type  ,t1.device_serial , t1.mcu_id, t1.modem, t1.devicemodel_id, t1.modem_id, t1.location_id,t2.sys_id      \n");
//		sbQuery.append(" SELECT   t1.modem_type  ,t2.sys_id , t1.mcu_id, t1.modem, t1.devicemodel_id, t1.modem_id, t1.location_id,t1.device_serial      \n");
		if(equip_type.equals("MMIU")||equip_type.equals("IEIU")){
			sbQuery.append(" SELECT   t1.device_serial, t1.mcu_id       \n");
			sbQuery.append(" FROM  modem t1  			    \n");
		}else{
			sbQuery.append(" SELECT   t2.sys_id,t2.id       \n");
			sbQuery.append(" FROM  modem t1  INNER JOIN  mcu t2 on (t2.ID = t1.MCU_ID)    \n");
		}
		sbQuery.append(" WHERE t1.devicemodel_id= :devicemodel_id           \n");
		sbQuery.append(" AND t1.hw_ver=  :hw_version  						\n");
		sbQuery.append(" AND t1.fw_ver=  :sw_version  						\n");
		if(!equip_type.equals("MMIU")&&!equip_type.equals("IEIU")){
		sbQuery.append(" AND t1.fw_revision= :build       					\n");
		}
		sbQuery.append(" AND t1.location_id = :location_id    				\n");
		sbQuery.append(" AND t1.supplier_id  = :supplier_id    				\n");
		sbQuery.append(" AND t1.modem_type  = :equip_type    				\n");
		if(equip_type.equals("MMIU")||equip_type.equals("IEIU")){
			sbQuery.append(" GROUP BY   t1.device_serial, t1.mcu_id    				\n");
		}else{
			sbQuery.append(" GROUP BY t2.sys_id ,t2.id   				\n");		
		}

		
		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		query = getSession().createSQLQuery(sbQuery.toString());
		query.setInteger("devicemodel_id", Integer.parseInt(String.valueOf(param.get("devicemodel_id"))));
		query.setString("hw_version", String.valueOf(param.get("hwVersion")));
		String sw_version = String.valueOf(param.get("swVersion")).toUpperCase();
		query.setString("sw_version", sw_version);
		if(!equip_type.equals("MMIU")&&!equip_type.equals("IEIU")){
		query.setString("build", String.valueOf(param.get("swRevision")));
		}
		query.setInteger("location_id", Integer.parseInt(location_id));
		query.setInteger("supplier_id", Integer.parseInt(String.valueOf(param.get("supplierId"))));
		query.setString("equip_type", equip_type);

    	return query.list();
	}
		
	/**
	 * codi, modem만 실행 됨
	 * mcu별 장비 수를 클릭 하면 id가 Tree형태로 다시 표시 됨. (id count를 미리 출력_보여주기 위함)
	 **/
	@SuppressWarnings("unchecked")
	public String getDistriButeModemModelListCnt(Map<String, Object> param, String mcu_id){
		String equip_kind = String.valueOf(param.get("equip_kind"));
		String equip_type = String.valueOf(param.get("equip_type"));
		StringBuffer sbQuery = new StringBuffer();
       
		/*
		 * getdistributeModemIdDivList() 함수에서 가지고온    
		 * mcu테이블의 sys_id 를 가지고 올려면 modem테이블의 Key 값인 device_serial컬럼에 해당하는 modem.mcu_id= mcu.id 에 해당하는  sysid 를 가지고 오면 됨
		 * */
		
/*		
 		sbQuery.append(" SELECT count(*) cnt FROM  modem t1     \n");
		sbQuery.append(" WHERE t1.mcu_id = :mcu_id    \n");
*/		
		sbQuery.append(" SELECT count(*) cnt      \n");
		sbQuery.append(" FROM  modem t1 inner join mcu t2 on (t1.mcu_id = t2.id and t2.sys_id = :mcu_id )     \n");
		sbQuery.append(" WHERE t1.supplier_id = :supplier_id    \n");
		if(!equip_type.equals("MMIU")&&!equip_type.equals("IEIU")){
		sbQuery.append(" AND t1.fw_revision = :sys_sw_revision   \n");
		}
		sbQuery.append(" AND t1.fw_ver = :sys_sw_version   \n");
		sbQuery.append(" AND t1.hw_ver = :sys_hw_version   \n");
		sbQuery.append(" AND t1.location_id  = :location_id  \n");
		sbQuery.append(" AND t1.modem_type = :equip_type    \n");
		sbQuery.append(" AND t1.devicemodel_id = :devicemodel_id    \n");
		
		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		query = getSession().createSQLQuery(sbQuery.toString());

		query.setString("mcu_id", mcu_id);
		query.setInteger("supplier_id", Integer.parseInt(String.valueOf(param.get("supplierId"))));
		if(!equip_type.equals("MMIU")&&!equip_type.equals("IEIU")){
		query.setString("sys_sw_revision", String.valueOf(param.get("swRevision")));
		}
		String sw_version = String.valueOf(param.get("swVersion")).toUpperCase();
		query.setString("sys_sw_version", sw_version);
		query.setString("sys_hw_version", String.valueOf(param.get("hwVersion")));
		query.setString("sys_hw_version", String.valueOf(param.get("hwVersion")));
		query.setInteger("location_id", Integer.parseInt(String.valueOf(param.get("locaionId"))));
		query.setInteger("devicemodel_id", Integer.parseInt(String.valueOf(param.get("devicemodel_id"))));
		query.setString("equip_type", equip_type);
		

		query.setString("location_id", String.valueOf(param.get("locaionId")));

		return String.valueOf(query.list().get(0));
	}
	
	/**
	 * codi, modem만 실행 됨
	 * mcu별 장비 수를 클릭 하면 id가 Tree형태로 다시 표시 됨. 
	 **/
	@SuppressWarnings("unchecked")
	public List<Object>  getDistriButeModemModelList(Map<String, Object> param, String mcu_id){
		String equip_kind = String.valueOf(param.get("equip_kind"));
		String equip_type = String.valueOf(param.get("equip_type"));
		
		StringBuffer sbQuery = new StringBuffer();
/*
		sbQuery.append(" SELECT t2.sys_id ,t1.device_serial   \n");
		sbQuery.append(" FROM modem t1  left JOIN  mcu t2 on (t2.ID = t1.MCU_ID)    \n");
*/		
		sbQuery.append(" SELECT t2.sys_id ,t1.device_serial,t2.id      \n");
		sbQuery.append(" FROM  modem t1 inner join mcu t2 on (t1.mcu_id = t2.id and t2.sys_id = :mcu_id )     \n");
		sbQuery.append(" WHERE t1.supplier_id = :supplier_id    \n");
//		sbQuery.append(" AND t1.fw_revision = :sys_sw_revision   \n");
//		sbQuery.append(" AND t1.fw_ver = :sys_sw_version   \n");
//		sbQuery.append(" AND t1.hw_ver = :sys_hw_version   \n");
		sbQuery.append(" AND t1.location_id  = :location_id  \n");
		sbQuery.append(" AND t1.modem_type = :equip_type    \n");
		sbQuery.append(" AND t1.devicemodel_id = :devicemodel_id    \n");

		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		query = getSession().createSQLQuery(sbQuery.toString());
		query.setString("mcu_id", mcu_id);
		query.setInteger("supplier_id", Integer.parseInt(String.valueOf(param.get("supplierId"))));
//		query.setString("sys_sw_revision", String.valueOf(param.get("swRevision")));
//		query.setString("sys_sw_version", String.valueOf(param.get("swVersion")));
//		query.setString("sys_hw_version", String.valueOf(param.get("hwVersion")));
//		query.setString("sys_hw_version", String.valueOf(param.get("hwVersion")));
		query.setInteger("location_id", Integer.parseInt(String.valueOf(param.get("locaionId"))));
		query.setInteger("devicemodel_id", Integer.parseInt(String.valueOf(param.get("devicemodel_id"))));
		query.setString("equip_type",equip_type);

		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> distributeModemLocationStatus(Map<String, Object> param){
		String equip_type = String.valueOf(param.get("equip_type")); 
		StringBuffer sqlBuf = new StringBuffer();
		sqlBuf.append("SELECT    \n");
		sqlBuf.append("       localname, \n");
		sqlBuf.append("       tr_id,      \n");
		sqlBuf.append("       total, \n");
		sqlBuf.append("       (total-succ-cancel-error) AS mathexec,  \n");
		sqlBuf.append("       succ, \n");
		sqlBuf.append("       cancel, \n");
		sqlBuf.append("       error,  \n");
		sqlBuf.append("       trigger_step,  \n");
		sqlBuf.append("       trigger_state, \n");
		sqlBuf.append("       ota_step,      \n");
		sqlBuf.append("       ota_state,      \n");
		sqlBuf.append("       device_serial \n");
		sqlBuf.append("       parent_id, \n");
		sqlBuf.append("       locid, \n");		
		sqlBuf.append("       equip_id	 \n");		
		sqlBuf.append("  FROM  \n");
		sqlBuf.append("       ( \n");
		sqlBuf.append("              SELECT  \n");
		if(equip_type.equals("MMIU")||equip_type.equals("IEIU")){//GPRS 형식은 DEVICE_SRIAL대신 SYS_ID를 가지고 배포 하기 때문에.. his.equip_id 가 대신한다.
			sqlBuf.append("                    his.equip_id  device_serial,his.trigger_step,his.trigger_state, his.ota_step, his.ota_state , his.tr_id, \n");			
			sqlBuf.append("                    loc.id locid, \n");
			sqlBuf.append("                    loc.name localname, \n");
			sqlBuf.append("                    loc.parent_id parent_id, \n");
			sqlBuf.append("                    count(his.equip_id) total, \n");
		}else{
			sqlBuf.append("                    mdm.device_serial device_serial,his.trigger_step,his.trigger_state, his.ota_step, his.ota_state , his.tr_id, \n");
			sqlBuf.append("                    loc.id locid, \n");
			sqlBuf.append("                    loc.name localname, \n");
			sqlBuf.append("                    loc.parent_id parent_id, \n");
			sqlBuf.append("                    count(mdm.device_serial) total, \n");
		}
		sqlBuf.append("                     SUM(  \n");
		sqlBuf.append("                          CASE  \n");
		sqlBuf.append("                              WHEN  \n");
		sqlBuf.append("                                  his.trigger_step=4 AND HIS.trigger_state=0  \n");
		sqlBuf.append("                              THEN 1  \n");
		sqlBuf.append("                              ELSE 0  \n");
		sqlBuf.append("                          END  \n");
		sqlBuf.append("                        ) AS SUCC, \n");
		sqlBuf.append("                      SUM  \n");
		sqlBuf.append("                      (  \n");
		sqlBuf.append("                          CASE  \n");
		sqlBuf.append("                              WHEN his.ota_state=2  \n");
		sqlBuf.append("                              THEN 1  \n");
		sqlBuf.append("                              ELSE 0  \n");
		sqlBuf.append("                          END  \n");
		sqlBuf.append("                      ) AS CANCEL, \n");
		sqlBuf.append("                      SUM  \n");
		sqlBuf.append("                      (  \n");
		sqlBuf.append("                          CASE  \n");
		sqlBuf.append("                              WHEN  \n");
		sqlBuf.append("                                  (  \n");
		sqlBuf.append("                                      his.trigger_state=1 AND his.trigger_state!=2 \n");
		sqlBuf.append("                                  )  \n");
		sqlBuf.append("                                  OR  \n");
		sqlBuf.append("                                  (  \n");
		sqlBuf.append("                                      his.ota_state=1  \n");
		sqlBuf.append("                                  )  \n");
		sqlBuf.append("                              THEN 1  \n");
		sqlBuf.append("                              ELSE 0  \n");
		sqlBuf.append("                          END  \n");
		sqlBuf.append("                      ) AS ERROR,  \n");
		sqlBuf.append("                      his.equip_id   \n");
		sqlBuf.append("              FROM \n");
		sqlBuf.append("              ( \n");
		sqlBuf.append("                SELECT everyhis.*  \n");
		sqlBuf.append("                FROM FIRMWARE_HISTORY everyhis,  \n");
		sqlBuf.append("                     ( \n");
		sqlBuf.append("                        SELECT equip_id,  \n");
		sqlBuf.append("                        MAX(issue_date) AS issue_date  \n");
		sqlBuf.append("                        FROM FIRMWARE_HISTORY  \n");
		sqlBuf.append("                        GROUP BY equip_id  \n");
		sqlBuf.append("                     ) lasthis  \n");
		sqlBuf.append("               WHERE lasthis.equip_id=everyhis.equip_id  \n");
		sqlBuf.append("               AND lasthis.issue_date=everyhis.issue_date  \n");
		sqlBuf.append("              )his \n");
		if(equip_type.equals("MMIU")||equip_type.equals("IEIU")){//GPRS SMS 형식은 BUILD가 없기 때문
//			sqlBuf.append("              JOIN MODEM mdm ON(mdm.device_serial=his.equip_id)  \n");
			sqlBuf.append("              JOIN FIRMWARE_TRIGGER tri ON (  his.tr_id=tri.id and tri.target_hwver= :hw_version AND tri.target_fwver= :fw_version  AND tri.target_firmware= :firmware_id  )  	\n");
//			sqlBuf.append("              JOIN FIRMWARE firm on ( firm.hw_version= :hw_version AND firm.fw_version= :fw_version  AND firm.firmware_id= :firmware_id   )    \n");
//			sqlBuf.append("              JOIN MCU target on ( target.sys_id = his.equip_id)   \n");
//			sqlBuf.append("              JOIN LOCATION loc ON (target.location_id = loc.id)  \n");
			sqlBuf.append("              join modem mdm on ( mdm.device_serial = his.equip_id)    \n"); 
			sqlBuf.append("              JOIN LOCATION loc ON (mdm.location_id = loc.id)  \n");
			sqlBuf.append("            GROUP BY  \n");
		}else{
			sqlBuf.append("              JOIN MODEM mdm ON(mdm.device_serial=his.equip_id)  \n");
			sqlBuf.append("              JOIN FIRMWARE_TRIGGER tri ON (  his.tr_id=tri.id and tri.target_hwver= :hw_version AND tri.target_fwver= :fw_version  AND tri.target_firmware= :firmware_id  and tri.target_fwbuild = :build )  	\n");
//			sqlBuf.append("              JOIN FIRMWARE firm on ( firm.hw_version= :hw_version AND firm.fw_version= :fw_version  AND firm.build= :build  AND firm.firmware_id= :firmware_id   )    \n");
			sqlBuf.append("              JOIN MCU target on ( target.id = mdm.mcu_id)   \n");
			sqlBuf.append("              JOIN LOCATION loc ON (mdm.location_id = loc.id)  \n");
			sqlBuf.append("            GROUP BY  \n");
			sqlBuf.append("              mdm.device_serial,  \n");
		}

		sqlBuf.append("              loc.id,  \n");
		sqlBuf.append("              loc.name,  \n");
		sqlBuf.append("              loc.parent_id ,his.trigger_step,his.trigger_state, his.ota_step, his.ota_state, his.tr_id  ,his.equip_id   \n");
		sqlBuf.append("    )stat \n");

		
		SQLQuery query = getSession().createSQLQuery(sqlBuf.toString());
		query = getSession().createSQLQuery(sqlBuf.toString());
		query.setString("firmware_id", String.valueOf(param.get("firmware_Id")));
		query.setString("hw_version", String.valueOf(param.get("hw_version")));
		query.setString("fw_version", String.valueOf(param.get("fw_version")));
		if(!equip_type.equals("MMIU")&&!equip_type.equals("IEIU")){
			query.setString("build", String.valueOf(param.get("build")));	
		}
		
		
    	return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> distributeModemTriggerIdStatus(Map<String, Object> param){
		String equip_type = String.valueOf(param.get("equip_type")); 
		StringBuffer sqlBuf = new StringBuffer();
		sqlBuf.append("SELECT equip_id,  \n");
		sqlBuf.append("       tr_id,  \n");
		sqlBuf.append("       TOTAL,  \n");
		sqlBuf.append("       SUCC,  \n");
		sqlBuf.append("       TOTAL-SUCC-CANCEL-ERROR AS METHEXEC,  \n");
		sqlBuf.append("       CANCEL,  \n");
		sqlBuf.append("       ERROR,  \n");
		sqlBuf.append("        TRIGGER_STEP,  \n");
		sqlBuf.append("        TRIGGER_STATE, \n");
		sqlBuf.append("        OTA_STEP,      \n");
		sqlBuf.append("        OTA_STATE,      \n");		
		sqlBuf.append("       device_serial  \n");
		sqlBuf.append("  FROM  \n");
		sqlBuf.append("       (SELECT his.tr_id, his.trigger_step,his.trigger_state, his.ota_step, his.ota_state ,    \n");
		sqlBuf.append("              his.equip_id,  \n");
		if(equip_type.equals("MMIU")||equip_type.equals("IEIU")){
			sqlBuf.append("              his.equip_id device_serial,  \n");
			sqlBuf.append("              COUNT(  his.equip_id ) AS TOTAL,  \n");
		}else{
			sqlBuf.append("              mdm.device_serial device_serial,  \n");
			sqlBuf.append("              COUNT( mdm.device_serial) AS TOTAL,  \n");
		}
		
		sqlBuf.append("              SUM  \n");
		sqlBuf.append("              (  \n");
		sqlBuf.append("                  CASE  \n");
		sqlBuf.append("                      WHEN his.trigger_step=4  \n");
		sqlBuf.append("                          AND HIS.trigger_state=0  \n");
		sqlBuf.append("                      THEN 1  \n");
		sqlBuf.append("                      ELSE 0  \n");
		sqlBuf.append("                  END  \n");
		sqlBuf.append("              ) AS SUCC,  \n");
		sqlBuf.append("              SUM  \n");
		sqlBuf.append("              (  \n");
		sqlBuf.append("                  CASE  \n");
		sqlBuf.append("                      WHEN his.ota_state=2  \n");
		sqlBuf.append("                      THEN 1  \n");
		sqlBuf.append("                      ELSE 0  \n");
		sqlBuf.append("                  END  \n");
		sqlBuf.append("              ) AS CANCEL ,  \n");
		sqlBuf.append("              SUM  \n");
		sqlBuf.append("              (  \n");
		sqlBuf.append("                  CASE  \n");
		sqlBuf.append("                      WHEN  \n");
		sqlBuf.append("                          (  \n");
		sqlBuf.append("                              HIS.trigger_state=1  \n");
		sqlBuf.append("                              AND his.ota_state!=2  \n");
		sqlBuf.append("                          )  \n");
		sqlBuf.append("                          OR  \n");
		sqlBuf.append("                          (  \n");
		sqlBuf.append("                              his.ota_state=1  \n");
		sqlBuf.append("                          )  \n");
		sqlBuf.append("                      THEN 1  \n");
		sqlBuf.append("                      ELSE 0  \n");
		sqlBuf.append("                  END  \n");
		sqlBuf.append("              ) AS ERROR \n");
		sqlBuf.append("              FROM \n");
		sqlBuf.append("              ( \n");
		sqlBuf.append("                SELECT everyhis.*  \n");
		sqlBuf.append("                FROM FIRMWARE_HISTORY everyhis,  \n");
		sqlBuf.append("                     ( \n");
		sqlBuf.append("                        SELECT equip_id,  \n");
		sqlBuf.append("                        MAX(issue_date) AS issue_date  \n");
		sqlBuf.append("                        FROM FIRMWARE_HISTORY  \n");
		sqlBuf.append("                        GROUP BY equip_id                      \n");
		sqlBuf.append("                     ) lasthis  \n");
		sqlBuf.append("               WHERE lasthis.equip_id=everyhis.equip_id  \n");
		sqlBuf.append("               AND lasthis.issue_date=everyhis.issue_date  \n");
		sqlBuf.append("              )his \n");
/*		sqlBuf.append("               JOIN MODEM mdm ON(mdm.device_serial=his.equip_id AND mdm.hw_ver=:hw_version AND mdm.fw_ver=:fw_version AND mdm.fw_revision=:build AND mdm.device_serial=:firmware_id ) \n");
		sqlBuf.append("               JOIN FIRMWARE_TRIGGER tri ON (  his.tr_id=tri.id  ) \n");
*/		

		if(equip_type.equals("MMIU")||equip_type.equals("IEIU")){
			sqlBuf.append("              JOIN FIRMWARE_TRIGGER tri ON (  his.tr_id=tri.id and tri.target_hwver= :hw_version AND tri.target_fwver= :fw_version  AND tri.target_firmware= :firmware_id   )  	\n");
//			sqlBuf.append("                join MCU target on ( target.sys_id = his.equip_id)     \n");
		}else{
			sqlBuf.append("               JOIN MODEM mdm ON(mdm.device_serial=his.equip_id)  \n");		
			sqlBuf.append("              JOIN FIRMWARE_TRIGGER tri ON (  his.tr_id=tri.id and tri.target_hwver= :hw_version AND tri.target_fwver= :fw_version  AND tri.target_firmware= :firmware_id  and tri.target_fwbuild = :build )  	\n");
			sqlBuf.append("                join MCU target on ( target.id = mdm.mcu_id)     \n");
		}
		
		
		sqlBuf.append("            GROUP BY  \n");
		sqlBuf.append("              his.tr_id,  \n");
		sqlBuf.append("              his.equip_id,  \n");
		if(equip_type.equals("MMIU")||equip_type.equals("IEIU")){
			sqlBuf.append("              his.trigger_step,his.trigger_state, his.ota_step, his.ota_state  \n");
		}else{
			sqlBuf.append("              mdm.device_serial,his.trigger_step,his.trigger_state, his.ota_step, his.ota_state  \n");			
		}
		
		sqlBuf.append("     )stat \n");
		sqlBuf.append("ORDER BY tr_id DESC  \n");
 
		logger.debug(sqlBuf.toString());
		
		SQLQuery query = getSession().createSQLQuery(sqlBuf.toString());
		query = getSession().createSQLQuery(sqlBuf.toString());
		query.setString("firmware_id", String.valueOf(param.get("firmware_Id")));
		query.setString("hw_version", String.valueOf(param.get("hw_version")));
		query.setString("fw_version", String.valueOf(param.get("fw_version")));
		if(!equip_type.equals("MMIU")&&!equip_type.equals("IEIU")){
			query.setString("build", String.valueOf(param.get("build")));			
		}
		
    	return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> distributeModemLocStatusDetail(Map<String, Object> param){

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
		sqlBuf.append(" JOIN MODEM mdm ON (mdm.device_serial=his.equip_id AND mdm.location_id= :mdm.location_id ) \n");
		sqlBuf.append(" ORDER BY his.tr_id, his.equip_id \n");

		SQLQuery query = getSession().createSQLQuery(sqlBuf.toString());
		query = getSession().createSQLQuery(sqlBuf.toString());
		query.setString("firmware_id", String.valueOf(param.get("firmware_id")));
		query.setInteger("location_id", Integer.parseInt(String.valueOf(param.get("location_id"))));
		
    	return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> getReDistModemList(Map<String, Object> param){
		logger.debug(this.getClass().getName()+":"+"getReDistMcuList()");
		String gubun = String.valueOf(param.get("gubun"));
		String equip_type = String.valueOf(param.get("equip_type"));
		int tr_id = Integer.parseInt(String.valueOf(param.get("tr_id")));
		StringBuffer sqlBuf = new StringBuffer();
		sqlBuf.append("SELECT HIS.TR_ID,  \n");
		sqlBuf.append("       MCUT.SYS_ID,  \n");
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
		sqlBuf.append("       HIS.TRIGGER_HISTORY,  \n");
		if(equip_type.equals("MMIU")||equip_type.equals("IEIU")){
			sqlBuf.append("       MCUT.SYS_ID,  \n");
		}else{
			sqlBuf.append("       TARGET.DEVICE_SERIAL,  \n");	
		}
		sqlBuf.append("       MCUT.ID  \n");
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
		sqlBuf.append("                          GROUP BY EQUIP_ID ,ID \n");
		sqlBuf.append("              ) LASTHIS  \n");
		sqlBuf.append("              WHERE LASTHIS.EQUIP_ID=EVERYHIS.EQUIP_ID  \n");
		sqlBuf.append("              AND LASTHIS.ISSUE_DATE=EVERYHIS.ISSUE_DATE  \n");
		sqlBuf.append(")his,  \n");

		if(equip_type.equals("MMIU")||equip_type.equals("IEIU")){
			sqlBuf.append(" FIRMWARE_TRIGGER tri,  \n");
//			sqlBuf.append(" modem target , \n");
			sqlBuf.append(" mcu mcut \n");
			sqlBuf.append(" WHERE HIS.TR_ID=TRI.ID \n");
			sqlBuf.append(" AND TRI.ID = :tr_id  \n");
		}else{
			sqlBuf.append(" FIRMWARE_TRIGGER tri,  \n");
			sqlBuf.append(" modem target , \n");
			sqlBuf.append(" mcu mcut \n");
			sqlBuf.append(" WHERE target.mcu_id = mcut.id  \n");
			sqlBuf.append(" AND HIS.TR_ID=TRI.ID \n");
			sqlBuf.append(" AND TRI.ID = :tr_id  \n");
		}
		if(gubun.equals("step3")){
			sqlBuf.append(" AND HIS.EQUIP_ID = :equip_id  \n");
		}
		if(equip_type.equals("MMIU")||equip_type.equals("IEIU")){
			sqlBuf.append(" AND  mcut.sys_id = his.equip_id  \n");
		}else{
			sqlBuf.append(" AND target.device_serial = his.equip_id \n");			
		}
//		sqlBuf.append(" AND mcut.sys_id = his.equip_id \n");

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
		
		logger.debug(sqlBuf.toString());
		
		SQLQuery query = getSession().createSQLQuery(sqlBuf.toString());
		query = getSession().createSQLQuery(sqlBuf.toString());
		query.setString("target_firmware", String.valueOf(param.get("target_firmware")));
		query.setInteger("tr_id", tr_id);
		if(gubun.equals("step3")){
			query.setString("equip_id", String.valueOf(param.get("mcu_id")));
		}
    	return query.list();
	}	
	
	@SuppressWarnings("unchecked")
	public String getMcuBuildByModemFirmware(Map<String, Object> param){

		StringBuffer sqlBuf = new StringBuffer();
		sqlBuf.append("SELECT mu.sys_sw_revision from modem mdm join mcu mu on (mdm.MCU_ID = mu.ID) \n");
		sqlBuf.append("where  mdm.FW_REVISION = :fw_revision \n");
		sqlBuf.append("and mdm.FW_VER = :fw_ver \n");
		sqlBuf.append("and mdm.HW_VER = :hw_ver \n");
		sqlBuf.append("and mdm.SUPPLIER_ID = :supplier_id \n");
		sqlBuf.append("and mdm.DEVICEMODEL_ID = :devicemodel_id \n");
		sqlBuf.append("and mdm.MODEM_TYPE = :modem_type  \n");

		SQLQuery query = getSession().createSQLQuery(sqlBuf.toString());
		query = getSession().createSQLQuery(sqlBuf.toString());
		query.setString("fw_ver", String.valueOf(param.get("sw_version")));
		query.setString("hw_ver", String.valueOf(param.get("hw_version")));
		query.setString("fw_revision", String.valueOf(param.get("build")));
		query.setString("supplier_id", String.valueOf(param.get("supplierId")));
		query.setString("devicemodel_id", String.valueOf(param.get("model_id")));
		query.setString("modem_type", String.valueOf(param.get("equip_type")));
		
		return String.valueOf(query.list().get(0));
	}
	
	
	@SuppressWarnings("unchecked")
	public void addFirmWareModem(FirmwareModem firmware,FirmwareBoard firmwareBoard)throws Exception {
			getSession().save(firmware);
			getSession().save(firmwareBoard);
	}
	
	@SuppressWarnings("unchecked")
	public void updateFirmWareModem(FirmwareModem firmware,FirmwareBoard firmwareBoard)throws Exception {
			getSession().update(firmware);
			getSession().update(firmwareBoard);
	}
}
