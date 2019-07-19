/**
 * Copyright Nuri Telecom Corp.
 * 파일명: FirmwareDaoImpl
 * 작성일자/작성자 : 2010.12.06 최창희
 * @see 
 * 
 *
 * 펌웨어 관리자 페이지 DAO
 * 
 * ============================================================================
 * 수정 내역
 * NO  수정일자   수정자   수정내역
 * 
 * ============================================================================
 */
package com.aimir.dao.device.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.FirmwareDao;
import com.aimir.model.device.Firmware;
import com.aimir.model.device.FirmwareCodi;
import com.aimir.model.device.FirmwareMCU;
import com.aimir.model.device.FirmwareModem;
import com.aimir.util.StringUtil;

@Repository(value = "firmwareDao")
public class FirmwareDaoImpl extends AbstractHibernateGenericDao<Firmware, Integer> implements FirmwareDao {
	private static Log logger = LogFactory.getLog(FirmwareDaoImpl.class);

	@Autowired
	protected FirmwareDaoImpl(SessionFactory sessionFactory) {
		super(Firmware.class);
		super.setSessionFactory(sessionFactory);
	}

	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public Firmware get(int id){
		return findByCondition("id", id);
	}
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public Firmware getByFirmwareId(String firmwareId) {
		return findByCondition("firmwareId", firmwareId);
	}
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public Serializable setFirmware(Firmware firmware) {
		return getSession().save(firmware);
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public List<Object> getStatisticsStr(Map<String, Object> condition) {
		String firmware_id = (String)condition.get("firmware_id");
		String hw_version = (String)condition.get("hw_version");
		String fw_version = (String)condition.get("sw_version");
		String build = (String)condition.get("build");
		String devicemodel_id = (String)condition.get("devicemodel_id");
		String equip_type = (String)condition.get("equip_type");
		
		StringBuffer sbQuery = new StringBuffer();
		sbQuery.append("  SELECT STAT.firmware_id,                                            			 \n");
		sbQuery.append(" 			stat.total,                                             			\n");
		sbQuery.append(" 			stat.succ,                                             				\n");
		sbQuery.append(" 			(stat.total-stat.succ-stat.cancel-stat.error ) as pexec ,           \n");
		sbQuery.append(" 			stat.cancel,                                            			\n");
		sbQuery.append("  			stat.error                                            				\n");
		sbQuery.append("  FROM (                                            							\n");
		sbQuery.append("        SELECT  outfirm.firmware_id,                                             \n");
		sbQuery.append("                COUNT( outfirm.firmware_id) AS total ,                           \n");
		sbQuery.append("                SUM(                                                                \n");
		sbQuery.append("                  CASE                                                           \n");
		sbQuery.append("                      WHEN                                                       \n");
		sbQuery.append("                      outhis.trigger_step=4 and outhis.trigger_state=0           \n");
		sbQuery.append("                      THEN 1                                                     \n");
		sbQuery.append("                      ELSE 0                                                     \n");
		sbQuery.append("                  END                                                            \n");
		sbQuery.append("                ) AS succ,                                                       \n");
		sbQuery.append("                SUM(                                                                \n");
		sbQuery.append("                   CASE                                                          \n");
		sbQuery.append("                       WHEN outhis.ota_state=2                                   \n");
		sbQuery.append("                       THEN 1                                                    \n");
		sbQuery.append("                       ELSE 0                                                    \n");
		sbQuery.append("                   END                                                           \n");
		sbQuery.append("                ) AS cancel,                                                     \n");
		sbQuery.append("                SUM(                                                                \n");
		sbQuery.append("                   CASE                                                          \n");
		sbQuery.append("                       WHEN                                                      \n");
		sbQuery.append("                           (                                                     \n");
		sbQuery.append("                               outhis.trigger_state=1  AND outhis.ota_state!=2   \n");
		sbQuery.append("                           )                                                     \n");
		sbQuery.append("                           OR                                                    \n");
		sbQuery.append("                           (                                                     \n");
		sbQuery.append("                               outhis.ota_state=1                                \n");
		sbQuery.append("                           )                                                     \n");
		sbQuery.append("                       THEN 1                                                    \n");
		sbQuery.append("                       ELSE 0                                                    \n");
		sbQuery.append("                   END                                                           \n");
		sbQuery.append("                ) AS ERROR 			                                             \n");
		sbQuery.append("        FROM                                                                     \n");
		sbQuery.append("            (                                                                    \n");
		sbQuery.append("            SELECT everyhis.*                                                    \n");
		sbQuery.append("            FROM FIRMWARE_HISTORY everyhis,                                      \n");
		sbQuery.append("                 (                                                               \n");
		sbQuery.append("                    SELECT tb1.equip_id,                                         \n");
		sbQuery.append("                        MAX(tb1.issue_date) AS issue_date                        \n");
		sbQuery.append("                    FROM                                                         \n");
		sbQuery.append("                        (                                                        \n");
		sbQuery.append("                         SELECT his.issue_date, his.equip_id                     \n");
		sbQuery.append("                         FROM FIRMWARE_HISTORY his,                              \n");
		sbQuery.append("                              FIRMWARE firm,                                     \n");
		sbQuery.append("                              FIRMWARE_TRIGGER tri                               \n");
		sbQuery.append("                         WHERE tri.ID=HIS.TR_ID                               \n");
		if(!equip_type.equals("Codi")){
			sbQuery.append("                         AND  firm.devicemodel_id = :devicemodel_id              \n");			
		}
		sbQuery.append("                         AND tri.src_firmware=firm.firmware_id                   \n");
		sbQuery.append("                           AND his.EQUIP_TYPE = :equip_type                   \n");
		sbQuery.append("                         ) tb1                                                   \n");
		sbQuery.append("                    GROUP BY tb1.equip_id                                        \n");
		sbQuery.append("                 ) lasthis                                                       \n");
		sbQuery.append("            WHERE lasthis.equip_id=everyhis.equip_id                             \n");
		sbQuery.append("            AND lasthis.issue_date=everyhis.issue_date                           \n");
		sbQuery.append("            ) outhis,                                                            \n");
		sbQuery.append("              FIRMWARE outfirm,                                                  \n");
		sbQuery.append("              FIRMWARE_TRIGGER outtri                                            \n");
		sbQuery.append("        WHERE outhis.tr_id=outtri.id                                          \n");
		sbQuery.append("        AND outtri.target_firmware=outfirm.firmware_id                           \n");
		sbQuery.append("        AND outfirm.hw_version= :hw_version                                      \n");
		sbQuery.append("        AND outfirm.fw_version= :fw_version                                      \n");
		if(!equip_type.equals("MMIU")&&!equip_type.equals("IEIU")){
		sbQuery.append("        AND outfirm.build= :build                                                \n");
		}
		sbQuery.append("        AND outfirm.firmware_id=  :firmware_id				                      \n");
		sbQuery.append("        AND outtri.target_firmware= :firmware_id                    			  \n");
		sbQuery.append("        GROUP BY outfirm.firmware_id                                             \n");
		sbQuery.append(" ) stat                                            								\n");
		
		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		query = getSession().createSQLQuery(sbQuery.toString());
		query.setString("hw_version", hw_version);
		query.setString("fw_version", fw_version);
		if(!equip_type.equals("MMIU")&&!equip_type.equals("IEIU")){
		query.setString("build", build);
		}
		query.setString("firmware_id", firmware_id);
		query.setString("equip_type", equip_type);
		if(!equip_type.equals("Codi")){
			query.setInteger("devicemodel_id", Integer.parseInt(devicemodel_id));			
		}
		
    	return query.list();
	}	
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public List<FirmwareMCU> getMCUFirmwareList(Map<String, Object> condition) {
		Criteria criteria = getSession().createCriteria(FirmwareMCU.class);
		if(condition != null) {
	        Set<String> set = condition.keySet();
	        Object []hmKeys = set.toArray();
	        for (int i=0; i<hmKeys.length; i++) {
	            String key = (String)hmKeys[i];
	            criteria.add(Restrictions.eq(key, condition.get(key)));
	        }
		}
        List<FirmwareMCU> firmwares = (List<FirmwareMCU>) criteria.list();
        
        return firmwares; 
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public List<FirmwareCodi> getCodiFirmwareList(Map<String, Object> condition) {
		Criteria criteria = getSession().createCriteria(FirmwareCodi.class);

		if(condition != null) {
	        Set<String> set = condition.keySet();
	        Object []hmKeys = set.toArray();
	        for (int i=0; i<hmKeys.length; i++) {
	            String key = (String)hmKeys[i];
	            criteria.add(Restrictions.eq(key, condition.get(key)));
	        }
		}
        List<FirmwareCodi> firmwares = (List<FirmwareCodi>) criteria.list();

        return firmwares;
	}
	
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public List<FirmwareModem> getModemFirmwareList(Map<String, Object> condition) {
		Criteria criteria = getSession().createCriteria(FirmwareModem.class);

		if(condition != null) {
	        Set<String> set = condition.keySet();
	        Object []hmKeys = set.toArray();
	        for (int i=0; i<hmKeys.length; i++) {
	            String key = (String)hmKeys[i];
	            criteria.add(Restrictions.eq(key, condition.get(key)));
	        }
		}

		List<FirmwareModem> firmwares = (List<FirmwareModem>) criteria.list();

        return firmwares;
	}

	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public List<Object> distributeFmStatusEqDetail(Map<String, Object> param){

		StringBuffer sqlBuf = new StringBuffer();
		sqlBuf.append("SELECT         \n");
		sqlBuf.append("       his.trigger_step,  \n");
		sqlBuf.append("       his.trigger_state,  \n");
		sqlBuf.append("       his.ota_step,  \n");
		sqlBuf.append("       his.ota_state,  \n");
		sqlBuf.append("       his.error_code,  \n");
		sqlBuf.append("       his.tr_id,  \n");
		sqlBuf.append("       his.device_id, \n");
		sqlBuf.append("       (SELECT name FROM LOCATION WHERE id= :location_id) as locname, \n");
		sqlBuf.append("       his.trigger_cnt , \n");
		sqlBuf.append("       his.trigger_history , \n");
		sqlBuf.append("       tri.src_firmware, \n");
		sqlBuf.append("       (select binaryfilename from firmware where firmware_id=tri.src_firmware ) as srcfilename, \n");
		sqlBuf.append("        tri.target_firmware, \n");
		sqlBuf.append("       (select binaryfilename from firmware where firmware_id=tri.target_firmware ) as targetfilename, \n");
		sqlBuf.append("       tri.target_hwver, \n");
		sqlBuf.append("       tri.target_fwver, \n");
		sqlBuf.append("       tri.target_fwbuild, \n");
		sqlBuf.append("       tri.create_date,  \n");
		sqlBuf.append("       tri.end_date,  \n");
		sqlBuf.append("       his.equip_kind,  \n");
		sqlBuf.append("       his.equip_type,  \n");
		sqlBuf.append("       his.equip_vendor,  \n");
		sqlBuf.append("       his.equip_model,  \n");
		sqlBuf.append("       his.equip_id, \n");
		sqlBuf.append("       (select  \n");
		sqlBuf.append("        (CASE WHEN t1.issue_date = his.equip_id THEN 'true' ELSE 'false' END ) as islasttrigger \n");
		sqlBuf.append("        from  \n");
		sqlBuf.append("            (select inhis.issue_date,  \n");
		sqlBuf.append("                     inhis.equip_id  \n");
		sqlBuf.append("             from firmware_history inhis  \n");
		sqlBuf.append("                  join firmware_trigger intri on (intri.id=inhis.tr_id ) \n");
		sqlBuf.append("                  join firmware infirm on ( intri.src_firmware=infirm.firmware_id) \n");
		sqlBuf.append("            ) t1 \n");
		sqlBuf.append("            where t1.equip_id= his.equip_id ) islasttrigger, \n");
		sqlBuf.append("      his.mcu_id \n");
		sqlBuf.append("FROM  \n");
		sqlBuf.append("    FIRMWARE_HISTORY his JOIN FIRMWARE_TRIGGER tri ON (his.tr_id = tri.id)                           \n");
		sqlBuf.append("WHERE his.tr_id = :tr_id  \n");
		sqlBuf.append("ORDER BY isLastTrigger,his.tr_id  desc  \n");

		
		SQLQuery query = getSession().createSQLQuery(sqlBuf.toString());
		query = getSession().createSQLQuery(sqlBuf.toString());
		query.setInteger("location_id", Integer.parseInt(String.valueOf(param.get("location_id"))));
		query.setInteger("tr_id", Integer.parseInt(String.valueOf(param.get("tr_id"))));
		//query.setString("firmware_id", String.valueOf(param.get("firmware_id")));
		
		
    	return query.list();
	}
	
	/**
	 * 배포 파일관리 조회리스트 count 
	 * */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public String getFirmwareFileMgmListCNT(Map<String, Object> condition) {
		int devicemodel_id = Integer.parseInt((String)condition.get("devicemodel_id"));
		String equip_type = String.valueOf(condition.get("equip_type"));
		String equip_kind = String.valueOf(condition.get("equip_kind"));
		int equip_typeCD = Integer.parseInt(String.valueOf(condition.get("equip_typeCD")));
		
		
		StringBuffer sqlBuf = new StringBuffer();
		sqlBuf.append("SELECT count(*) cnt   \n");
		sqlBuf.append("FROM FIRMWARE frm JOIN FIRMWAREBOARD brd  ON (frm.id = brd.firmware_id)    \n");
		sqlBuf.append("WHERE    frm.supplier_id = :supplier_id      \n");
		
		if(equip_kind.equals("Modem")){
			sqlBuf.append("and frm.devicemodel_id = :devicemodel_id       \n");
			sqlBuf.append("and frm.modem_type = :mcu_type   \n");
		}else if(equip_kind.equals("MCU")){
			sqlBuf.append("and frm.devicemodel_id = :devicemodel_id       \n");
			sqlBuf.append("and frm.mcu_type = :mcu_type   \n");						
		}else if(equip_kind.equals("Codi")){
			sqlBuf.append("and frm.equip_kind = :equip_kind       \n");
		}
		
		SQLQuery query = getSession().createSQLQuery(sqlBuf.toString());
		query = getSession().createSQLQuery(sqlBuf.toString());
		query.setInteger("supplier_id", Integer.parseInt(String.valueOf(condition.get("supplierId"))));
		if(equip_kind.equals("Modem")||equip_kind.equals("MCU")){
			query.setInteger("devicemodel_id", devicemodel_id);
			query.setString("mcu_type", equip_type);	
		}else if(equip_kind.equals("Codi")){
			query.setString("equip_kind", equip_kind);	
		}		

    	return String.valueOf(query.list().get(0));
	}	
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public List<Object> getFirmwareFileList(Map<String, Object> condition) {
		String supplier_Id = String.valueOf(condition.get("supplierId"));
		String equip_kind = String.valueOf(condition.get("equip_kind"));
		String fileName = String.valueOf(condition.get("fileName"));
		String modelName = String.valueOf(condition.get("modelName"));
		String modelId = String.valueOf(condition.get("modelId"));
		String fwVer = String.valueOf(condition.get("fwVer"));
		
		List<Object> result		= new ArrayList<Object>();
		StringBuffer sqlBuf = new StringBuffer();
		
		sqlBuf.append("SELECT        			                    \n");		
		sqlBuf.append("       frm.EQUIP_MODEL,                      \n");
		sqlBuf.append("       frm.FIRMWARE_ID,                      \n");
		sqlBuf.append("       frm.SUPPLIER_ID,                      \n");
		sqlBuf.append("       frm.EQUIP_KIND,                       \n");
		sqlBuf.append("       frm.EQUIP_TYPE,                       \n");
		sqlBuf.append("       frm.EQUIP_VENDOR,                     \n");
		sqlBuf.append("       frm.ARM,                              \n");
		sqlBuf.append("       frm.HW_VERSION,                       \n");
		sqlBuf.append("       frm.FW_VERSION,                       \n");
		sqlBuf.append("       frm.BUILD,                            \n");
		sqlBuf.append("       frm.RELEASED_DATE,                    \n");
		sqlBuf.append("       frm.BINARYFILENAME,                   \n");
		sqlBuf.append("       frm.DEVICEMODEL_ID,                   \n");
		sqlBuf.append("       frm.CHECK_SUM,		                \n");
		sqlBuf.append("       frm.CRC,		                        \n");
		sqlBuf.append("       frm.IMAGE_KEY,		                \n");
		sqlBuf.append("       frm.FILE_PATH,	                    \n");
		sqlBuf.append("       frm.FILE_URL_PATH,	                \n");
		sqlBuf.append("       frm.ID                                \n");
		sqlBuf.append("FROM   FIRMWARE frm                          \n");
		sqlBuf.append("WHERE  frm.SUPPLIER_ID = :supplier_id        \n");
		sqlBuf.append("AND    frm.EQUIP_KIND  = :equip_kind         \n");
		sqlBuf.append("AND    frm.BUILD  <> 'Deleted'		        \n"); // SP-967
		if (!fileName.isEmpty())
			sqlBuf.append("AND    frm.BINARYFILENAME  like :fileName         \n");
		if (!modelName.isEmpty())
			sqlBuf.append("AND    frm.EQUIP_MODEL  like :modelName         \n");
		if (!modelId.isEmpty())
			sqlBuf.append("AND    frm.DEVICEMODEL_ID  = :modelId         \n");
		if (!fwVer.isEmpty())
			sqlBuf.append("AND    frm.FW_VERSION  like :fwVer         \n");
		sqlBuf.append("ORDER BY  RELEASED_DATE DESC\n");
		
		SQLQuery query = getSession().createSQLQuery(sqlBuf.toString());
		query = getSession().createSQLQuery(sqlBuf.toString());
		query.setInteger("supplier_id", Integer.parseInt(supplier_Id));
		query.setString("equip_kind", equip_kind);
		if (!fileName.isEmpty())
		query.setString("fileName",  fileName+"%");
		if (!modelName.isEmpty())
		query.setString("modelName", modelName+"%");
		if (!modelId.isEmpty())
			query.setInteger("modelId", Integer.parseInt(modelId));
		if (!fwVer.isEmpty())
		query.setString("fwVer", fwVer+"%");
		
		return query.list();
	}
	
	/**
	 * 배포 파일관리 조회리스트 
	 * */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public List<Object> getFirmwareFileMgmList(Map<String, Object> condition) {
		int devicemodel_id = Integer.parseInt((String)condition.get("devicemodel_id"));
		String equip_type = String.valueOf(condition.get("equip_type"));
		String equip_kind = String.valueOf(condition.get("equip_kind"));
		int equip_typeCD = Integer.parseInt(String.valueOf(condition.get("equip_typeCD")));
		int firstResults = Integer.parseInt((String)condition.get("firstResults"));
		int maxResults = Integer.parseInt((String)condition.get("maxResults"));		
		
		StringBuffer sqlBuf = new StringBuffer();
		sqlBuf.append("SELECT frm.MCU_TYPE, frm.MODEM_TYPE, frm.equip_kind, frm.hw_version , frm.fw_version , brd.TITLE, brd.writedate , \n");
		sqlBuf.append("       frm.released_date , (select loginid from OPERATOR where id = brd.OPERATOR_ID ) loginid,   \n");
		sqlBuf.append("       frm.build ,frm.firmware_id, frm.supplier_id,frm.equip_vendor  ,frm.equip_model, frm.arm,brd.CONTENT \n");
		sqlBuf.append("FROM FIRMWARE frm JOIN FIRMWAREBOARD brd  ON (frm.id = brd.firmware_id)    \n");
		sqlBuf.append("WHERE    frm.supplier_id = :supplier_id      \n");
		
		if(equip_kind.equals("Modem")){
			sqlBuf.append("and frm.devicemodel_id = :devicemodel_id       \n");
			sqlBuf.append("and frm.modem_type = :mcu_type   \n");
		}else if(equip_kind.equals("MCU")){
			sqlBuf.append("and frm.devicemodel_id = :devicemodel_id       \n");
			sqlBuf.append("and frm.mcu_type = :mcu_type   \n");						
		}else if(equip_kind.equals("Codi")){
			sqlBuf.append("and frm.equip_kind = :equip_kind       \n");
		}
		sqlBuf.append("ORDER BY  frm.hw_version  desc , frm.build  desc \n");
		
		
		SQLQuery query = getSession().createSQLQuery(sqlBuf.toString());
		query = getSession().createSQLQuery(sqlBuf.toString());
		
		query.setInteger("supplier_id", Integer.parseInt(String.valueOf(condition.get("supplierId"))));
		if(equip_kind.equals("Modem")||equip_kind.equals("MCU")){
			query.setInteger("devicemodel_id", devicemodel_id);
			query.setString("mcu_type", equip_type);	
		}else if(equip_kind.equals("Codi")){
			query.setString("equip_kind", equip_kind);	
		}
			
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
	 * Firmware추가 시 파일이 존재하는지 여부 체크 
	 * */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public String checkExistFirmware(Map<String, Object> condition) {
		boolean returnCheck = true;
		String equip_type = String.valueOf(condition.get("equip_type"));
		String hw_version = String.valueOf(condition.get("hw_version"));
		String fw_version = String.valueOf(condition.get("fw_version"));
		int arm = String.valueOf(condition.get("arm")).equals("false")?0:1;
		int devicemodel_id = Integer.parseInt(String.valueOf(condition.get("devicemodel_id")));
		String vendor = String.valueOf(condition.get("vendor"));
		String equip_kind = String.valueOf(condition.get("equip_kind"));
		String build = String.valueOf(condition.get("build"));
		//int equip_typeCD = Integer.parseInt(String.valueOf(condition.get("equip_typeCD")));
		
		
		StringBuffer sqlBuf = new StringBuffer();
		sqlBuf.append("SELECT firm.id fwid , firmbd.id  fwidbd \n");
		sqlBuf.append("  FROM FIRMWARE firm \n");
		sqlBuf.append("        join FIRMWAREBOARD  firmbd on (firmbd.FIRMWARE_ID = firm.ID)  \n");
		sqlBuf.append(" WHERE equip_kind =  :equip_kind \n");
		sqlBuf.append(" AND hw_version = :hw_version  \n");
		sqlBuf.append(" AND fw_version = :fw_version \n");
		sqlBuf.append(" AND build = :build \n");
		if(!equip_kind.equals("Codi")){
			sqlBuf.append(" AND equip_vendor = :vendor \n");
			sqlBuf.append(" AND devicemodel_id = :devicemodel_id \n");
		}
		if(equip_kind.equals("Modem")){
			sqlBuf.append(" AND arm = :arm \n");	
			sqlBuf.append(" AND modem_type = :modem_type \n");	
		}else if(equip_kind.equals("MCU")){
			sqlBuf.append(" AND mcu_type = :mcu_type \n");	
		}
		
		SQLQuery query = getSession().createSQLQuery(sqlBuf.toString());
		query = getSession().createSQLQuery(sqlBuf.toString());
		query.setString("equip_kind", equip_kind);	
		query.setString("hw_version", hw_version);	
		query.setString("fw_version", fw_version);
		query.setString("build", build);	
		if(!equip_kind.equals("Codi")){
		query.setString("vendor", vendor);		
		query.setInteger("devicemodel_id", devicemodel_id);
		}
		if(equip_kind.equals("Modem")){
			query.setInteger("arm", arm);		
			query.setString("modem_type", equip_type);		
		}else if(equip_kind.equals("MCU")){
			query.setString("mcu_type", equip_type);
		}

		List<Object> listobj = query.list();
		
		String returnStr = "";
		
		for (Object obj : listobj) {
	        Object[] objs = (Object[])obj;
			returnStr = objs[0]+"|"+objs[1];
		}
    	return returnStr;
	}
	
/*	@SuppressWarnings("unchecked")
	public void addFirmWare(FirmwareMCU firmware,FirmwareBoard firmwareBoard)throws Exception {
			getHibernateTemplate().save(firmware);
			getHibernateTemplate().save(firmwareBoard);
	}*/		
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public Firmware add(Firmware entity){
		entity.setFirmwareId((entity.getSupplier() == null ? "ALL" : entity
				.getSupplier().getId()) + "_" + entity.getEquipKind() + "_"
				+ StringUtil.nullCheck(entity.getEquipType(), "0") + "_"
				+ StringUtil.nullCheck(entity.getEquipVendor(), "") + "_"
				+ StringUtil.nullCheck(entity.getEquipModel(), "") + "_"
				+ entity.getHwVersion() + "_" + entity.getFwVersion() + "_"
				+ entity.getBuild()+ "_" + entity.isArm());
		Firmware rtnEntity = null;
		rtnEntity = super.add(entity);

		return rtnEntity;
	}
	
	/**
	 * 배포이력 TriggerList 조회 
	 * */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public List<Object> getTriggerListStep1(Map<String, Object> condition,String locationStr)throws Exception {
		
		String equip_kind = String.valueOf(condition.get("equip_kind"));
		String equip_id = String.valueOf(condition.get("equip_id"));
		String fromDate = String.valueOf(condition.get("fromDate"));
		String toDate = String.valueOf(condition.get("toDate"));
		String state = String.valueOf(condition.get("state"));
		String equip_vendor =  String.valueOf(condition.get("equip_vendor")) == null ? "" :String.valueOf(condition.get("equip_vendor")) ;
		String equip_model =  String.valueOf(condition.get("equip_model")) == null ? "" :String.valueOf(condition.get("equip_model")) ;
		int trId = Integer.parseInt(String.valueOf(condition.get("trId")).equals("")?"0":String.valueOf(condition.get("trId")));
		int firstResults = Integer.parseInt(String.valueOf(condition.get("firstResults")));
		int maxResults = Integer.parseInt(String.valueOf(condition.get("maxResults")));
		String equip_type_nm = "";
		
		if(String.valueOf(condition.get("equip_type")).equals("56")){
			equip_type_nm = "MMIU";
		}else if(String.valueOf(condition.get("equip_type")).equals("57")){
			equip_type_nm = "IEIU";
		}

		StringBuffer sqlBuf = new StringBuffer();
		if(equip_kind.equals("Modem")&& String.valueOf(condition.get("equip_type")).equals("")){
			sqlBuf.append("SELECT DISTINCT TRIGGERLIST.*  \n");
			sqlBuf.append("FROM   \n");
			sqlBuf.append("(   \n");
			sqlBuf.append("        SELECT TRI.ID TRIID,   \n");
			sqlBuf.append("                HIS3.EQUIP_KIND,  \n");
			sqlBuf.append("                HIS3.EQUIP_TYPE,  \n");
			sqlBuf.append("                HIS3.HW_VER,   \n");
			sqlBuf.append("                HIS3.SW_VER,  \n");
			sqlBuf.append("                HIS3.FW_REVISION,  \n");
			sqlBuf.append("                TRI.TARGET_HWVER,   \n");
			sqlBuf.append("                TRI.TARGET_FWVER,   \n");
			sqlBuf.append("                TRI.TARGET_FWBUILD,  \n");
			sqlBuf.append("                HIS3.CNT TOTALCNT,   \n");
			sqlBuf.append("                HIS3.SUCC,   \n");
			sqlBuf.append("                HIS3.PEXEC,   \n");
			sqlBuf.append("                HIS3.CANCEL,   \n");
			sqlBuf.append("                HIS3.ERROR,   \n");
			sqlBuf.append("                TRI.SRC_FIRMWARE,  \n");
			sqlBuf.append("                TRI.TARGET_FIRMWARE,   \n");
			//sqlBuf.append("                TRI.TARGET_FIRMWARE,   \n");
			//sqlBuf.append("                HIS3.TRIGGER_STEP ,  \n");
			//sqlBuf.append("                HIS3.TRIGGER_STATE,  \n");
			//sqlBuf.append("                TRI.CREATE_DATE,  \n");
			//sqlBuf.append("                TRI.END_DATE,  \n");
			//sqlBuf.append("                HIS3.EQUIP_VENDOR,   \n");
			//sqlBuf.append("                HIS3.EQUIP_MODEL,   \n");
			//sqlBuf.append("                HIS3.TRIGGER_CNT,  \n");
			sqlBuf.append("                HIS3.LOCATION_ID  \n");
			sqlBuf.append("           FROM FIRMWARE_TRIGGER TRI,   \n");
			sqlBuf.append("           (  \n");
			//sqlBuf.append("                  SELECT  EQUIP_KIND,EQUIP_TYPE,HW_VER,SW_VER,FW_REVISION, CNT ,SUCC,PEXEC,CANCEL,ERROR,TRIGGER_STEP,TRIGGER_STATE,EQUIP_VENDOR,EQUIP_MODEL,TRIGGER_CNT,LOCATION_ID,TR_ID \n");
			sqlBuf.append("                  SELECT  EQUIP_KIND,EQUIP_TYPE,HW_VER,SW_VER,FW_REVISION, CNT ,SUCC,PEXEC,CANCEL,ERROR,TR_ID, LOCATION_ID \n");
			sqlBuf.append("                   FROM   \n");
			sqlBuf.append("                        (  \n");
			sqlBuf.append("                            SELECT  MAX(TR_ID) MAXTR_ID,   \n");
			sqlBuf.append("                                    COUNT(TR_ID) CNT,          \n");
			sqlBuf.append("                                    SUM   \n");
			sqlBuf.append("                                    (   \n");
			sqlBuf.append("                                        CASE   \n");
			sqlBuf.append("                                            WHEN HIS1.TRIGGER_STEP=4   \n");
			sqlBuf.append("                                            AND HIS1.TRIGGER_STATE=0   \n");
			sqlBuf.append("                                            THEN 1   \n");
			sqlBuf.append("                                            ELSE 0   \n");
			sqlBuf.append("                                        END   \n");
			sqlBuf.append("                                    ) AS SUCC,   \n");
			sqlBuf.append("                                    SUM   \n");
			sqlBuf.append("                                    (   \n");
			sqlBuf.append("                                        CASE   \n");
			sqlBuf.append("                                            WHEN HIS1.TRIGGER_STATE!=1   \n");
			sqlBuf.append("                                                AND HIS1.OTA_STATE!=1   \n");
			sqlBuf.append("                                                AND HIS1.TRIGGER_STEP!=4   \n");
			sqlBuf.append("                                            THEN 1   \n");
			sqlBuf.append("                                            ELSE 0   \n");
			sqlBuf.append("                                        END   \n");
			sqlBuf.append("                                    ) AS PEXEC,   \n");
			sqlBuf.append("                                    SUM   \n");
			sqlBuf.append("                                    (   \n");
			sqlBuf.append("                                        CASE   \n");
			sqlBuf.append("                                            WHEN HIS1.OTA_STATE=2   \n");
			sqlBuf.append("                                            THEN 1   \n");
			sqlBuf.append("                                            ELSE 0   \n");
			sqlBuf.append("                                        END   \n");
			sqlBuf.append("                                    ) AS CANCEL ,   \n");
			sqlBuf.append("                                    SUM   \n");
			sqlBuf.append("                                    (   \n");
			sqlBuf.append("                                        CASE   \n");
			sqlBuf.append("                                            WHEN   \n");
			sqlBuf.append("                                                (   \n");
			sqlBuf.append("                                                    HIS1.TRIGGER_STATE=1   \n");
			sqlBuf.append("                                                    AND HIS1.OTA_STATE!=2   \n");
			sqlBuf.append("                                                )   \n");
			sqlBuf.append("                                                OR   \n");
			sqlBuf.append("                                                (   \n");
			sqlBuf.append("                                                    HIS1.OTA_STATE=1   \n");
			sqlBuf.append("                                                )   \n");
			sqlBuf.append("                                            THEN 1   \n");
			sqlBuf.append("                                            ELSE 0   \n");
			sqlBuf.append("                                        END   \n");
			sqlBuf.append("                                    ) AS ERROR    \n");
			sqlBuf.append("                            FROM FIRMWARE_HISTORY HIS1   \n");
			sqlBuf.append("                                 JOIN (SELECT HW_VER, SW_VER,FW_REVISION,LOCATION_ID, DEVICE_SERIAL FROM MODEM) mdm ON(mdm.device_serial=HIS1.equip_id \n");
			if(locationStr != null && !"".equals(locationStr)){
				sqlBuf.append("                                 AND mdm.LOCATION_ID in "+locationStr+" \n");
			}
			sqlBuf.append("                                  )  \n");
			sqlBuf.append("                            WHERE  HIS1.EQUIP_KIND = :equip_kind   \n");
			sqlBuf.append("                            GROUP BY TR_ID   \n");
			sqlBuf.append("                        ) CNT  \n");
			sqlBuf.append("                        JOIN FIRMWARE_HISTORY HIS2 ON (CNT.MAXTR_ID=HIS2.TR_ID)   \n");
			sqlBuf.append("                        JOIN (SELECT HW_VER, SW_VER,FW_REVISION,LOCATION_ID, DEVICE_SERIAL FROM MODEM) mdm ON(mdm.device_serial=HIS2.equip_id)  \n");
			sqlBuf.append("                        UNION  \n");
			//sqlBuf.append("                        SELECT EQUIP_KIND,EQUIP_TYPE,SYS_HW_VERSION AS HW_VER,SYS_SW_VERSION AS SW_VER,SYS_SW_REVISION AS FW_REVISION ,CNT ,SUCC,PEXEC,CANCEL,ERROR,TRIGGER_STEP,TRIGGER_STATE,EQUIP_VENDOR,EQUIP_MODEL,TRIGGER_CNT,LOCATION_ID,TR_ID \n");
			sqlBuf.append("                        SELECT EQUIP_KIND,EQUIP_TYPE,SYS_HW_VERSION AS HW_VER,SYS_SW_VERSION AS SW_VER,SYS_SW_REVISION AS FW_REVISION ,CNT ,SUCC,PEXEC,CANCEL,ERROR,TR_ID, LOCATION_ID \n");
			sqlBuf.append("                        FROM   \n");
			sqlBuf.append("                        (  \n");
			sqlBuf.append("                            SELECT  MAX(TR_ID) MAXTR_ID,   \n");
			sqlBuf.append("                                    COUNT(TR_ID) CNT,          \n");
			sqlBuf.append("                                    SUM   \n");
			sqlBuf.append("                                    (   \n");
			sqlBuf.append("                                        CASE   \n");
			sqlBuf.append("                                            WHEN HIS1.TRIGGER_STEP=4   \n");
			sqlBuf.append("                                            AND HIS1.TRIGGER_STATE=0   \n");
			sqlBuf.append("                                            THEN 1   \n");
			sqlBuf.append("                                            ELSE 0   \n");
			sqlBuf.append("                                        END   \n");
			sqlBuf.append("                                    ) AS SUCC,   \n");
			sqlBuf.append("                                    SUM   \n");
			sqlBuf.append("                                    (   \n");
			sqlBuf.append("                                        CASE   \n");
			sqlBuf.append("                                            WHEN HIS1.TRIGGER_STATE!=1   \n");
			sqlBuf.append("                                                AND HIS1.OTA_STATE!=1   \n");
			sqlBuf.append("                                                AND HIS1.TRIGGER_STEP!=4   \n");
			sqlBuf.append("                                            THEN 1   \n");
			sqlBuf.append("                                            ELSE 0   \n");
			sqlBuf.append("                                        END   \n");
			sqlBuf.append("                                    ) AS PEXEC,   \n");
			sqlBuf.append("                                    SUM   \n");
			sqlBuf.append("                                    (   \n");
			sqlBuf.append("                                        CASE   \n");
			sqlBuf.append("                                            WHEN HIS1.OTA_STATE=2   \n");
			sqlBuf.append("                                            THEN 1   \n");
			sqlBuf.append("                                            ELSE 0   \n");
			sqlBuf.append("                                        END   \n");
			sqlBuf.append("                                    ) AS CANCEL ,   \n");
			sqlBuf.append("                                    SUM   \n");
			sqlBuf.append("                                    (   \n");
			sqlBuf.append("                                        CASE   \n");
			sqlBuf.append("                                            WHEN   \n");
			sqlBuf.append("                                                (   \n");
			sqlBuf.append("                                                    HIS1.TRIGGER_STATE=1   \n");
			sqlBuf.append("                                                    AND HIS1.OTA_STATE!=2   \n");
			sqlBuf.append("                                                )   \n");
			sqlBuf.append("                                                OR   \n");
			sqlBuf.append("                                                (   \n");
			sqlBuf.append("                                                    HIS1.OTA_STATE=1   \n");
			sqlBuf.append("                                                )   \n");
			sqlBuf.append("                                            THEN 1   \n");
			sqlBuf.append("                                            ELSE 0   \n");
			sqlBuf.append("                                        END   \n");
			sqlBuf.append("                                    ) AS ERROR    \n");
			sqlBuf.append("                            FROM FIRMWARE_HISTORY HIS1   \n");
			sqlBuf.append("                                 JOIN MCU mc ON(mc.sys_id=HIS1.equip_id \n");			
			if(locationStr != null && !"".equals(locationStr)){
				sqlBuf.append("                                 AND mc.LOCATION_ID in "+locationStr+" \n");
			}			
			sqlBuf.append("                                  )  \n");
			sqlBuf.append("                            WHERE  HIS1.EQUIP_KIND = :equip_kind   \n");
			sqlBuf.append("                            GROUP BY TR_ID   \n");
			sqlBuf.append("                        ) CNT  \n");
			sqlBuf.append("                        JOIN FIRMWARE_HISTORY HIS2 ON (CNT.MAXTR_ID=HIS2.TR_ID)   \n");
			sqlBuf.append("                        JOIN MCU mc ON(mc.sys_id=HIS2.equip_id)                             \n");
			sqlBuf.append("          ) HIS3   \n");
			sqlBuf.append("          WHERE TRI.ID=HIS3.TR_ID   \n");
			sqlBuf.append("         AND TRI.CREATE_DATE BETWEEN :fromDate AND :toDate \n");
			//sqlBuf.append(" ORDER BY TRI.CREATE_DATE DESC   \n");
			sqlBuf.append(") TRIGGERLIST   \n");


		}else{
			sqlBuf.append("SELECT DISTINCT TRIGGERLIST.* \n");
			sqlBuf.append("FROM  \n");
			sqlBuf.append("		(  \n");
			sqlBuf.append("		SELECT TRI.ID TRIID,  \n");
			sqlBuf.append("                HIS3.EQUIP_KIND, \n");	
			sqlBuf.append("                HIS3.EQUIP_TYPE, \n");
			if(equip_kind.equals("Modem")&&!equip_type_nm.equals("MMIU")&&!equip_type_nm.equals("IEIU")){
				sqlBuf.append("                HIS3.HW_VER,  \n");
				sqlBuf.append("                HIS3.SW_VER, \n");
				sqlBuf.append("                HIS3.FW_REVISION, \n");	
			}else if(equip_kind.equals("MCU")||equip_type_nm.equals("MMIU")||equip_type_nm.equals("IEIU")){
				sqlBuf.append("                HIS3.SYS_HW_VERSION,  \n");
				sqlBuf.append("                HIS3.SYS_SW_VERSION,  \n");
				sqlBuf.append("                HIS3.SYS_SW_REVISION,  \n");		
			}else if(equip_kind.equals("Codi")){
				sqlBuf.append("                HIS3.CODI_HW_VER,  \n");
				sqlBuf.append("                HIS3.CODI_FW_VER,  \n");
				sqlBuf.append("                HIS3.CODI_FW_BUILD,  \n");		
			}
			sqlBuf.append("                TRI.TARGET_HWVER,  \n");
			sqlBuf.append("                TRI.TARGET_FWVER,  \n");
			sqlBuf.append("                TRI.TARGET_FWBUILD, \n");
			sqlBuf.append("                HIS3.CNT TOTALCNT,  \n");
			sqlBuf.append("                HIS3.SUCC,  \n");
			sqlBuf.append("                HIS3.PEXEC,  \n");
			sqlBuf.append("                HIS3.CANCEL,  \n");
			sqlBuf.append("                HIS3.ERROR,  \n");
			sqlBuf.append("                TRI.SRC_FIRMWARE, \n");
			sqlBuf.append("                TRI.TARGET_FIRMWARE,  \n");
			//sqlBuf.append("                TRI.TARGET_FIRMWARE,  \n");
			//sqlBuf.append("                HIS3.TRIGGER_STEP , \n");
			//sqlBuf.append("                HIS3.TRIGGER_STATE, \n");
			//sqlBuf.append("                TRI.CREATE_DATE, \n");
			//sqlBuf.append("                TRI.END_DATE, \n");
			//sqlBuf.append("                HIS3.EQUIP_VENDOR,  \n");
			//sqlBuf.append("                HIS3.EQUIP_MODEL,  \n");
			//sqlBuf.append("                HIS3.TRIGGER_CNT, \n");
			sqlBuf.append("                HIS3.LOCATION_ID, \n");
			sqlBuf.append("                TRI.SRC_HWVER,  \n");
			sqlBuf.append("                TRI.SRC_FWVER, \n");
			sqlBuf.append("                TRI.SRC_FWBUILD \n");
			sqlBuf.append("           FROM FIRMWARE_TRIGGER TRI,  \n");
			sqlBuf.append("                ( \n");
			sqlBuf.append("                   SELECT * \n");
			sqlBuf.append("                   FROM  \n");
			sqlBuf.append("                        ( \n");
			sqlBuf.append("                            SELECT  MAX(TR_ID) MAXTR_ID,  \n");
			sqlBuf.append("                                    COUNT(TR_ID) CNT,         \n");
			sqlBuf.append("                                    SUM  \n");
			sqlBuf.append("                                    (  \n");
			sqlBuf.append("                                        CASE  \n");
			sqlBuf.append("                                            WHEN HIS1.TRIGGER_STEP=4  \n");
			sqlBuf.append("                                            AND HIS1.TRIGGER_STATE=0  \n");
			sqlBuf.append("                                            THEN 1  \n");
			sqlBuf.append("                                            ELSE 0  \n");
			sqlBuf.append("                                        END  \n");
			sqlBuf.append("                                    ) AS SUCC,  \n");
			sqlBuf.append("                                    SUM  \n");
			sqlBuf.append("                                    (  \n");
			sqlBuf.append("                                        CASE  \n");
			sqlBuf.append("                                            WHEN HIS1.TRIGGER_STATE!=1  \n");
			sqlBuf.append("                                                AND HIS1.OTA_STATE!=1  \n");
			sqlBuf.append("                                                AND HIS1.TRIGGER_STEP!=4  \n");
			sqlBuf.append("                                            THEN 1  \n");
			sqlBuf.append("                                            ELSE 0  \n");
			sqlBuf.append("                                        END  \n");
			sqlBuf.append("                                    ) AS PEXEC,  \n");
			sqlBuf.append("                                    SUM  \n");
			sqlBuf.append("                                    (  \n");
			sqlBuf.append("                                        CASE  \n");
			sqlBuf.append("                                            WHEN HIS1.OTA_STATE=2  \n");
			sqlBuf.append("                                            THEN 1  \n");
			sqlBuf.append("                                            ELSE 0  \n");
			sqlBuf.append("                                        END  \n");
			sqlBuf.append("                                    ) AS CANCEL ,  \n");
			sqlBuf.append("                                    SUM  \n");
			sqlBuf.append("                                    (  \n");
			sqlBuf.append("                                        CASE  \n");
			sqlBuf.append("                                            WHEN  \n");
			sqlBuf.append("                                                (  \n");
			sqlBuf.append("                                                    HIS1.TRIGGER_STATE=1  \n");
			sqlBuf.append("                                                    AND HIS1.OTA_STATE!=2  \n");
			sqlBuf.append("                                                )  \n");
			sqlBuf.append("                                                OR  \n");
			sqlBuf.append("                                                (  \n");
			sqlBuf.append("                                                    HIS1.OTA_STATE=1  \n");
			sqlBuf.append("                                                )  \n");
			sqlBuf.append("                                            THEN 1  \n");
			sqlBuf.append("                                            ELSE 0  \n");
			sqlBuf.append("                                        END  \n");
			sqlBuf.append("                                    ) AS ERROR   \n");
			sqlBuf.append("                            FROM FIRMWARE_HISTORY HIS1  \n");
			if(equip_kind.equals("Modem")&&!equip_type_nm.equals("MMIU")&&!equip_type_nm.equals("IEIU")){
				//MODEM일경우
				sqlBuf.append("                                 JOIN (SELECT HW_VER, SW_VER,FW_REVISION,LOCATION_ID, DEVICE_SERIAL FROM MODEM) mdm ON(mdm.device_serial=HIS1.equip_id \n");
				if(locationStr != null && !"".equals(locationStr)){
					sqlBuf.append("                                 AND mdm.LOCATION_ID in "+locationStr+"  \n");
				}
				sqlBuf.append("                                  ) \n");
	
			}else if(equip_kind.equals("MCU")||equip_type_nm.equals("MMIU")||equip_type_nm.equals("IEIU")){
				//MCU일경우
				sqlBuf.append("                                 JOIN MCU MCUT ON (MCUT.SYS_ID = HIS1.EQUIP_ID \n");	
				if(locationStr != null && !"".equals(locationStr)){
					sqlBuf.append("                                 AND MCUT.LOCATION_ID in "+locationStr+" \n");	
				}
				sqlBuf.append("                                 ) \n");	

			}else if(equip_kind.equals("Codi")){
				//Codi일경우				
				sqlBuf.append("                                 JOIN MCU MCUT ON (MCUT.SYS_ID = HIS1.EQUIP_ID \n");
				if(locationStr != null && !"".equals(locationStr)){
					sqlBuf.append("                                 AND MCUT.LOCATION_ID in "+locationStr+" \n");
				}
				sqlBuf.append("                                  ) \n");				
				sqlBuf.append("                                 JOIN MCU_CODI CODIT ON (MCUT.MCU_CODI_ID = CODIT.MCU_CODI_BINDING_ID ) \n");			
			}
			sqlBuf.append("                            WHERE  HIS1.EQUIP_KIND = :equip_kind  \n");
			if(!String.valueOf(condition.get("equip_type")).equals("")){
			sqlBuf.append("                            AND HIS1.EQUIP_TYPE = (select name from code where id= :equip_type ) \n");			
			}
			if(!equip_vendor.equals("")&& null != equip_vendor && !equip_vendor.equals("null")){
			sqlBuf.append("                            AND HIS1.EQUIP_VENDOR = (select name from DEVICEVENDOR where id = :equip_vendor ) \n");			
			}
			if(!equip_model.equals("")&& null != equip_model && !equip_model.equals("null")){
			sqlBuf.append("                            AND HIS1.EQUIP_MODEL = (select name from DEVICEModel where id = :equip_model) \n");			
			}
			if(!equip_id.equals("")){
			sqlBuf.append("                            AND HIS1.EQUIP_ID= :equip_id \n");
			}
			sqlBuf.append("                            GROUP BY TR_ID  \n");
			sqlBuf.append("                        ) CNT \n");
			sqlBuf.append("                          JOIN FIRMWARE_HISTORY HIS2 ON (CNT.MAXTR_ID=HIS2.TR_ID)  \n");
			if(equip_kind.equals("Modem")&&!equip_type_nm.equals("MMIU")&&!equip_type_nm.equals("IEIU")){
	            sqlBuf.append("                      JOIN (SELECT HW_VER, SW_VER,FW_REVISION,LOCATION_ID, DEVICE_SERIAL FROM MODEM) mdm ON(mdm.device_serial=HIS2.equip_id) \n");			
			}else if(equip_kind.equals("MCU")||equip_type_nm.equals("MMIU")||equip_type_nm.equals("IEIU")){
				//MCU일경우
				sqlBuf.append("                      JOIN MCU MCUT ON (MCUT.SYS_ID = HIS2.EQUIP_ID )  \n");			
			}else if(equip_kind.equals("Codi")){
				//Codi일경우
				sqlBuf.append("                      JOIN MCU MCUT ON (MCUT.SYS_ID = HIS2.EQUIP_ID ) \n");			
				sqlBuf.append("                      JOIN (SELECT CODI_HW_VER, CODI_FW_VER, CODI_FW_BUILD, CODI_ID, MCU_CODI_BINDING_ID from MCU_CODI) CODIT ON (MCUT.MCU_CODI_ID = CODIT.MCU_CODI_BINDING_ID )  \n");		
			}
			sqlBuf.append("               ) HIS3  \n");
			sqlBuf.append("          WHERE TRI.ID=HIS3.TR_ID  \n");
			if( trId != 0){
				sqlBuf.append("          AND TRI.ID = :trId \n");			
			}
			if(state.equals("Succ")) {
				sqlBuf.append("      AND SUCC > 0 \n");
			}else if(state.equals("Exec")) {
				sqlBuf.append("      AND PEXEC > 0  \n");
			}else if(state.equals("Cancel")) {
				sqlBuf.append("      AND CANCEL > 0  \n");
			}else if(state.equals("Error")) {
				sqlBuf.append("      AND ERROR > 0  \n");
			}
			sqlBuf.append("          AND TRI.CREATE_DATE BETWEEN :fromDate AND :toDate  \n");
			//sqlBuf.append(" ORDER BY TRI.CREATE_DATE DESC  \n");
			sqlBuf.append(") TRIGGERLIST  \n");

		}
		


		logger.info(sqlBuf.toString());
		

		
		SQLQuery query = getSession().createSQLQuery(sqlBuf.toString());
		query = getSession().createSQLQuery(sqlBuf.toString());
		
		fromDate = fromDate.substring(0,8)+"000000";
		toDate = toDate.substring(0,8)+"235959";

		query.setString("equip_kind", equip_kind);	
		query.setString("fromDate", fromDate);	
		query.setString("toDate", toDate);	
//		query.setInteger("locationId", locationId);

		if(!String.valueOf(condition.get("equip_type")).equals("")){
		query.setInteger("equip_type", Integer.parseInt(String.valueOf(condition.get("equip_type"))));			
		}
		if(!equip_vendor.equals("") && null != equip_vendor  && !equip_vendor.equals("null")){
		query.setInteger("equip_vendor", Integer.parseInt(equip_vendor));			
		}
		if(!equip_model.equals("") && null != equip_model && !equip_model.equals("null") ){
		query.setInteger("equip_model", Integer.parseInt(equip_model));			
		}
		if( trId != 0){
			query.setInteger("trId", trId);
		}

		if(!equip_id.equals("")){
			query.setString("equip_id", equip_id);
		}
		
		if(firstResults > 0 || maxResults > 0 ){
			query.setFirstResult(firstResults);
			query.setMaxResults(maxResults);
		}else{
			query.setFirstResult(0);
			query.setMaxResults(2);
		}
		
		logger.debug("equip_kind="+equip_kind);
		logger.debug("equip_type="+String.valueOf(condition.get("equip_type")));
		logger.debug("equip_vendor="+equip_vendor);
		
		logger.debug("fromDate="+fromDate);
		logger.debug("toDate="+toDate);

		
		return query.list();
	}
	
	/**
	 * 배포이력 TriggerList 조회 CNT 
	 * */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public String getTriggerListStep1CNT(Map<String, Object> condition, String locationStr)throws Exception {
		
		String equip_kind = String.valueOf(condition.get("equip_kind"));
		String equip_id = String.valueOf(condition.get("equip_id"));
		String fromDate = String.valueOf(condition.get("fromDate"));
		String toDate = String.valueOf(condition.get("toDate"));
		String state = String.valueOf(condition.get("state"));
		String equip_vendor =  String.valueOf(condition.get("equip_vendor")) == null ? "" :String.valueOf(condition.get("equip_vendor")) ;
		String equip_model =  String.valueOf(condition.get("equip_model")) == null ? "" :String.valueOf(condition.get("equip_model")) ;
		int trId = Integer.parseInt(String.valueOf(condition.get("trId")).equals("")?"0":String.valueOf(condition.get("trId")));
		String equip_type_nm = "";
		if(String.valueOf(condition.get("equip_type")).equals("56")){
			equip_type_nm = "MMIU";
		}else if(String.valueOf(condition.get("equip_type")).equals("57")){
			equip_type_nm = "IEIU";
		}
		
		StringBuffer sqlBuf = new StringBuffer();
		if(equip_kind.equals("Modem")&& String.valueOf(condition.get("equip_type")).equals("")){
			sqlBuf.append("SELECT count(*) tcnt  \n");
			sqlBuf.append("FROM   \n");
			sqlBuf.append("(   \n");
			sqlBuf.append("        SELECT DISTINCT TRI.ID TRIID,   \n");
			sqlBuf.append("                HIS3.EQUIP_KIND,  \n");
			sqlBuf.append("                HIS3.EQUIP_TYPE,  \n");
			sqlBuf.append("                HIS3.HW_VER,   \n");
			sqlBuf.append("                HIS3.SW_VER,  \n");
			sqlBuf.append("                HIS3.FW_REVISION,  \n");
			sqlBuf.append("                TRI.TARGET_HWVER,   \n");
			sqlBuf.append("                TRI.TARGET_FWVER,   \n");
			sqlBuf.append("                TRI.TARGET_FWBUILD,  \n");
			sqlBuf.append("                HIS3.CNT TOTALCNT,   \n");
			sqlBuf.append("                HIS3.SUCC,   \n");
			sqlBuf.append("                HIS3.PEXEC,   \n");
			sqlBuf.append("                HIS3.CANCEL,   \n");
			sqlBuf.append("                HIS3.ERROR,   \n");
			sqlBuf.append("                TRI.SRC_FIRMWARE,  \n");
			sqlBuf.append("                TRI.TARGET_FIRMWARE,   \n");
			//sqlBuf.append("                TRI.TARGET_FIRMWARE,   \n");
			//sqlBuf.append("                HIS3.TRIGGER_STEP ,  \n");
			//sqlBuf.append("                HIS3.TRIGGER_STATE,  \n");
			//sqlBuf.append("                TRI.CREATE_DATE,  \n");
			//sqlBuf.append("                TRI.END_DATE,  \n");
			//sqlBuf.append("                HIS3.EQUIP_VENDOR,   \n");
			//sqlBuf.append("                HIS3.EQUIP_MODEL,   \n");
			//sqlBuf.append("                HIS3.TRIGGER_CNT,  \n");
			sqlBuf.append("                HIS3.LOCATION_ID  \n");
			sqlBuf.append("           FROM FIRMWARE_TRIGGER TRI,   \n");
			sqlBuf.append("           (  \n");
			//sqlBuf.append("                  SELECT  EQUIP_KIND,EQUIP_TYPE,HW_VER,SW_VER,FW_REVISION, CNT ,SUCC,PEXEC,CANCEL,ERROR,TRIGGER_STEP,TRIGGER_STATE,EQUIP_VENDOR,EQUIP_MODEL,TRIGGER_CNT,LOCATION_ID,TR_ID \n");
			sqlBuf.append("                  SELECT  EQUIP_KIND,EQUIP_TYPE,HW_VER,SW_VER,FW_REVISION, CNT ,SUCC,PEXEC,CANCEL,ERROR,TR_ID, LOCATION_ID \n");
			sqlBuf.append("                   FROM   \n");
			sqlBuf.append("                        (  \n");
			sqlBuf.append("                            SELECT  MAX(TR_ID) MAXTR_ID,   \n");
			sqlBuf.append("                                    COUNT(TR_ID) CNT,          \n");
			sqlBuf.append("                                    SUM   \n");
			sqlBuf.append("                                    (   \n");
			sqlBuf.append("                                        CASE   \n");
			sqlBuf.append("                                            WHEN HIS1.TRIGGER_STEP=4   \n");
			sqlBuf.append("                                            AND HIS1.TRIGGER_STATE=0   \n");
			sqlBuf.append("                                            THEN 1   \n");
			sqlBuf.append("                                            ELSE 0   \n");
			sqlBuf.append("                                        END   \n");
			sqlBuf.append("                                    ) AS SUCC,   \n");
			sqlBuf.append("                                    SUM   \n");
			sqlBuf.append("                                    (   \n");
			sqlBuf.append("                                        CASE   \n");
			sqlBuf.append("                                            WHEN HIS1.TRIGGER_STATE!=1   \n");
			sqlBuf.append("                                                AND HIS1.OTA_STATE!=1   \n");
			sqlBuf.append("                                                AND HIS1.TRIGGER_STEP!=4   \n");
			sqlBuf.append("                                            THEN 1   \n");
			sqlBuf.append("                                            ELSE 0   \n");
			sqlBuf.append("                                        END   \n");
			sqlBuf.append("                                    ) AS PEXEC,   \n");
			sqlBuf.append("                                    SUM   \n");
			sqlBuf.append("                                    (   \n");
			sqlBuf.append("                                        CASE   \n");
			sqlBuf.append("                                            WHEN HIS1.OTA_STATE=2   \n");
			sqlBuf.append("                                            THEN 1   \n");
			sqlBuf.append("                                            ELSE 0   \n");
			sqlBuf.append("                                        END   \n");
			sqlBuf.append("                                    ) AS CANCEL ,   \n");
			sqlBuf.append("                                    SUM   \n");
			sqlBuf.append("                                    (   \n");
			sqlBuf.append("                                        CASE   \n");
			sqlBuf.append("                                            WHEN   \n");
			sqlBuf.append("                                                (   \n");
			sqlBuf.append("                                                    HIS1.TRIGGER_STATE=1   \n");
			sqlBuf.append("                                                    AND HIS1.OTA_STATE!=2   \n");
			sqlBuf.append("                                                )   \n");
			sqlBuf.append("                                                OR   \n");
			sqlBuf.append("                                                (   \n");
			sqlBuf.append("                                                    HIS1.OTA_STATE=1   \n");
			sqlBuf.append("                                                )   \n");
			sqlBuf.append("                                            THEN 1   \n");
			sqlBuf.append("                                            ELSE 0   \n");
			sqlBuf.append("                                        END   \n");
			sqlBuf.append("                                    ) AS ERROR    \n");
			sqlBuf.append("                            FROM FIRMWARE_HISTORY HIS1   \n");			
			sqlBuf.append("                                 JOIN MODEM mdm ON(mdm.device_serial=HIS1.equip_id \n");
			if(locationStr != null && !"".equals(locationStr)){
				sqlBuf.append("                                 AND mdm.LOCATION_ID in "+locationStr+" \n");
			}
			sqlBuf.append("                                  )  \n");			
			sqlBuf.append("                            WHERE  HIS1.EQUIP_KIND = :equip_kind   \n");
			sqlBuf.append("                            GROUP BY TR_ID   \n");
			sqlBuf.append("                        ) CNT  \n");
			sqlBuf.append("                        JOIN FIRMWARE_HISTORY HIS2 ON (CNT.MAXTR_ID=HIS2.TR_ID)   \n");
			sqlBuf.append("                        JOIN (SELECT HW_VER, SW_VER,FW_REVISION,LOCATION_ID, DEVICE_SERIAL FROM MODEM) mdm ON(mdm.device_serial=HIS2.equip_id)  \n");
			sqlBuf.append("                        UNION  \n");
			//sqlBuf.append("                        SELECT EQUIP_KIND,EQUIP_TYPE,SYS_HW_VERSION AS HW_VER,SYS_SW_VERSION AS SW_VER,SYS_SW_REVISION AS FW_REVISION ,CNT ,SUCC,PEXEC,CANCEL,ERROR,TRIGGER_STEP,TRIGGER_STATE,EQUIP_VENDOR,EQUIP_MODEL,TRIGGER_CNT,LOCATION_ID,TR_ID \n");
			sqlBuf.append("                        SELECT EQUIP_KIND,EQUIP_TYPE,SYS_HW_VERSION AS HW_VER,SYS_SW_VERSION AS SW_VER,SYS_SW_REVISION AS FW_REVISION ,CNT ,SUCC,PEXEC,CANCEL,ERROR,TR_ID, LOCATION_ID \n");
			sqlBuf.append("                        FROM   \n");
			sqlBuf.append("                        (  \n");
			sqlBuf.append("                            SELECT  MAX(TR_ID) MAXTR_ID,   \n");
			sqlBuf.append("                                    COUNT(TR_ID) CNT,          \n");
			sqlBuf.append("                                    SUM   \n");
			sqlBuf.append("                                    (   \n");
			sqlBuf.append("                                        CASE   \n");
			sqlBuf.append("                                            WHEN HIS1.TRIGGER_STEP=4   \n");
			sqlBuf.append("                                            AND HIS1.TRIGGER_STATE=0   \n");
			sqlBuf.append("                                            THEN 1   \n");
			sqlBuf.append("                                            ELSE 0   \n");
			sqlBuf.append("                                        END   \n");
			sqlBuf.append("                                    ) AS SUCC,   \n");
			sqlBuf.append("                                    SUM   \n");
			sqlBuf.append("                                    (   \n");
			sqlBuf.append("                                        CASE   \n");
			sqlBuf.append("                                            WHEN HIS1.TRIGGER_STATE!=1   \n");
			sqlBuf.append("                                                AND HIS1.OTA_STATE!=1   \n");
			sqlBuf.append("                                                AND HIS1.TRIGGER_STEP!=4   \n");
			sqlBuf.append("                                            THEN 1   \n");
			sqlBuf.append("                                            ELSE 0   \n");
			sqlBuf.append("                                        END   \n");
			sqlBuf.append("                                    ) AS PEXEC,   \n");
			sqlBuf.append("                                    SUM   \n");
			sqlBuf.append("                                    (   \n");
			sqlBuf.append("                                        CASE   \n");
			sqlBuf.append("                                            WHEN HIS1.OTA_STATE=2   \n");
			sqlBuf.append("                                            THEN 1   \n");
			sqlBuf.append("                                            ELSE 0   \n");
			sqlBuf.append("                                        END   \n");
			sqlBuf.append("                                    ) AS CANCEL ,   \n");
			sqlBuf.append("                                    SUM   \n");
			sqlBuf.append("                                    (   \n");
			sqlBuf.append("                                        CASE   \n");
			sqlBuf.append("                                            WHEN   \n");
			sqlBuf.append("                                                (   \n");
			sqlBuf.append("                                                    HIS1.TRIGGER_STATE=1   \n");
			sqlBuf.append("                                                    AND HIS1.OTA_STATE!=2   \n");
			sqlBuf.append("                                                )   \n");
			sqlBuf.append("                                                OR   \n");
			sqlBuf.append("                                                (   \n");
			sqlBuf.append("                                                    HIS1.OTA_STATE=1   \n");
			sqlBuf.append("                                                )   \n");
			sqlBuf.append("                                            THEN 1   \n");
			sqlBuf.append("                                            ELSE 0   \n");
			sqlBuf.append("                                        END   \n");
			sqlBuf.append("                                    ) AS ERROR    \n");
			sqlBuf.append("                            FROM FIRMWARE_HISTORY HIS1   \n");
			sqlBuf.append("                                 JOIN MCU mc ON(mc.sys_id=HIS1.equip_id \n");
			if(locationStr != null && !"".equals(locationStr)){
				sqlBuf.append("                                 AND mc.LOCATION_ID in "+locationStr+"  \n");
			}
			sqlBuf.append("                                 )  \n");
			sqlBuf.append("                            WHERE  HIS1.EQUIP_KIND = :equip_kind   \n");
			sqlBuf.append("                            GROUP BY TR_ID   \n");
			sqlBuf.append("                        ) CNT  \n");
			sqlBuf.append("                        JOIN FIRMWARE_HISTORY HIS2 ON (CNT.MAXTR_ID=HIS2.TR_ID)   \n");
			sqlBuf.append("                        JOIN MCU mc ON(mc.sys_id=HIS2.equip_id)                             \n");
			sqlBuf.append("          ) HIS3   \n");
			sqlBuf.append("          WHERE TRI.ID=HIS3.TR_ID   \n");
			sqlBuf.append("         AND CREATE_DATE BETWEEN :fromDate AND :toDate \n");
			sqlBuf.append(") TRIGGERLIST   \n");
		}else{
			sqlBuf.append("SELECT count(*) tcnt \n");
			sqlBuf.append("FROM  \n");
			sqlBuf.append("		(  \n");
			sqlBuf.append("		SELECT DISTINCT TRI.ID TRIID,  \n");
			sqlBuf.append("                TRI.SRC_FIRMWARE, \n");
			sqlBuf.append("                TRI.TARGET_FIRMWARE,  \n");
			sqlBuf.append("                TRI.TARGET_HWVER,  \n");
			sqlBuf.append("                TRI.TARGET_FWVER,  \n");
			sqlBuf.append("                TRI.TARGET_FWBUILD, \n");
			//sqlBuf.append("                HIS3.TRIGGER_STEP , \n");
			//sqlBuf.append("                HIS3.TRIGGER_STATE, \n");
			//sqlBuf.append("                TRI.CREATE_DATE, \n");
			//sqlBuf.append("                TRI.END_DATE, \n");
			sqlBuf.append("                HIS3.EQUIP_KIND, \n");
			sqlBuf.append("                HIS3.EQUIP_TYPE, \n");
			//sqlBuf.append("                HIS3.EQUIP_VENDOR,  \n");
			//sqlBuf.append("                HIS3.EQUIP_MODEL,  \n");
			//sqlBuf.append("                HIS3.TRIGGER_CNT, \n");
			sqlBuf.append("                HIS3.CNT TOTALCNT,  \n");
			sqlBuf.append("                HIS3.SUCC,  \n");
			sqlBuf.append("                HIS3.PEXEC,  \n");
			sqlBuf.append("                HIS3.ERROR,  \n");
			sqlBuf.append("                HIS3.CANCEL  \n");
			sqlBuf.append("           FROM FIRMWARE_TRIGGER TRI,  \n");
			sqlBuf.append("                ( \n");
			sqlBuf.append("                   SELECT * \n");
			sqlBuf.append("                   FROM  \n");
			sqlBuf.append("                        ( \n");
			sqlBuf.append("                            SELECT  MAX(TR_ID) MAXTR_ID,  \n");
			sqlBuf.append("                                    COUNT(TR_ID) CNT,         \n");
			sqlBuf.append("                                    SUM  \n");
			sqlBuf.append("                                    (  \n");
			sqlBuf.append("                                        CASE  \n");
			sqlBuf.append("                                            WHEN HIS1.TRIGGER_STEP=4  \n");
			sqlBuf.append("                                            AND HIS1.TRIGGER_STATE=0  \n");
			sqlBuf.append("                                            THEN 1  \n");
			sqlBuf.append("                                            ELSE 0  \n");
			sqlBuf.append("                                        END  \n");
			sqlBuf.append("                                    ) AS SUCC,  \n");
			sqlBuf.append("                                    SUM  \n");
			sqlBuf.append("                                    (  \n");
			sqlBuf.append("                                        CASE  \n");
			sqlBuf.append("                                            WHEN HIS1.TRIGGER_STATE!=1  \n");
			sqlBuf.append("                                                AND HIS1.OTA_STATE!=1  \n");
			sqlBuf.append("                                                AND HIS1.TRIGGER_STEP!=4  \n");
			sqlBuf.append("                                            THEN 1  \n");
			sqlBuf.append("                                            ELSE 0  \n");
			sqlBuf.append("                                        END  \n");
			sqlBuf.append("                                    ) AS PEXEC,  \n");
			sqlBuf.append("                                    SUM  \n");
			sqlBuf.append("                                    (  \n");
			sqlBuf.append("                                        CASE  \n");
			sqlBuf.append("                                            WHEN HIS1.OTA_STATE=2  \n");
			sqlBuf.append("                                            THEN 1  \n");
			sqlBuf.append("                                            ELSE 0  \n");
			sqlBuf.append("                                        END  \n");
			sqlBuf.append("                                    ) AS CANCEL ,  \n");
			sqlBuf.append("                                    SUM  \n");
			sqlBuf.append("                                    (  \n");
			sqlBuf.append("                                        CASE  \n");
			sqlBuf.append("                                            WHEN  \n");
			sqlBuf.append("                                                (  \n");
			sqlBuf.append("                                                    HIS1.TRIGGER_STATE=1  \n");
			sqlBuf.append("                                                    AND HIS1.OTA_STATE!=2  \n");
			sqlBuf.append("                                                )  \n");
			sqlBuf.append("                                                OR  \n");
			sqlBuf.append("                                                (  \n");
			sqlBuf.append("                                                    HIS1.OTA_STATE=1  \n");
			sqlBuf.append("                                                )  \n");
			sqlBuf.append("                                            THEN 1  \n");
			sqlBuf.append("                                            ELSE 0  \n");
			sqlBuf.append("                                        END  \n");
			sqlBuf.append("                                    ) AS ERROR   \n");
			sqlBuf.append("                            FROM FIRMWARE_HISTORY HIS1  \n");
			if(equip_kind.equals("Modem")&&!equip_type_nm.equals("MMIU")&&!equip_type_nm.equals("IEIU")){
				//MODEM일경우
				sqlBuf.append("                                 JOIN MODEM mdm ON(mdm.device_serial=HIS1.equip_id \n");	
				if(locationStr != null && !"".equals(locationStr)){
				sqlBuf.append("                                  AND mdm.LOCATION_ID in "+locationStr+" \n");	
				}
				sqlBuf.append("                                  ) \n");					
				
			}else if(equip_kind.equals("MCU")||equip_type_nm.equals("MMIU")||equip_type_nm.equals("IEIU")){
				//MCU일경우
				sqlBuf.append("                                 JOIN MCU MCUT ON (MCUT.SYS_ID = HIS1.EQUIP_ID \n");
				if(locationStr != null && !"".equals(locationStr)){
					sqlBuf.append("                                 AND MCUT.LOCATION_ID in "+locationStr+" \n");
				}
				sqlBuf.append("                                 ) \n");
				
			}else if(equip_kind.equals("Codi")){
				//Codi일경우
				sqlBuf.append("                                 JOIN MCU MCUT ON (MCUT.SYS_ID = HIS1.EQUIP_ID  \n");	
				if(locationStr != null && !"".equals(locationStr)){
					sqlBuf.append("                                 AND MCUT.LOCATION_ID in "+locationStr+" \n");
				}
				sqlBuf.append("                                 ) \n");
				
				
				sqlBuf.append("                                 JOIN MCU_CODI CODIT ON (MCUT.MCU_CODI_ID = CODIT.MCU_CODI_BINDING_ID ) \n");			
			}		
			sqlBuf.append("                            WHERE  HIS1.EQUIP_KIND = :equip_kind  \n");
			if(!String.valueOf(condition.get("equip_type")).equals("")){
			sqlBuf.append("                            AND HIS1.EQUIP_TYPE =  (select name from code where id= :equip_type )  \n");			
			}
			if(!equip_vendor.equals("") && null != equip_vendor && !equip_vendor.equals("null")){
			sqlBuf.append("                            AND HIS1.EQUIP_VENDOR =(select name from DEVICEVENDOR where id = :equip_vendor )  \n");			
			}
			if(!equip_model.equals("") && null != equip_model && !equip_model.equals("null")){
			sqlBuf.append("                            AND HIS1.EQUIP_MODEL = (select name from DEVICEModel where id = :equip_model)   \n");			
			}
			if(!equip_id.equals("")){
			sqlBuf.append("                            AND HIS1.EQUIP_ID= :equip_id \n");
			}
			sqlBuf.append("                            GROUP BY TR_ID  \n");
			sqlBuf.append("                        ) CNT \n");
			sqlBuf.append("                          JOIN FIRMWARE_HISTORY HIS2 ON (CNT.MAXTR_ID=HIS2.TR_ID)  \n");
			if(equip_kind.equals("Modem")&&!equip_type_nm.equals("MMIU")&&!equip_type_nm.equals("IEIU")){
	            sqlBuf.append("                      JOIN (SELECT HW_VER, SW_VER,FW_REVISION,LOCATION_ID, DEVICE_SERIAL FROM MODEM) mdm ON(mdm.device_serial=HIS2.equip_id) \n");	
			}else if(equip_kind.equals("MCU")||equip_type_nm.equals("MMIU")||equip_type_nm.equals("IEIU")){
				//MCU일경우
				sqlBuf.append("                      JOIN MCU MCUT ON (MCUT.SYS_ID = HIS2.EQUIP_ID )  \n");			
			}else if(equip_kind.equals("Codi")){
				//Codi일경우
				sqlBuf.append("                      JOIN MCU MCUT ON (MCUT.SYS_ID = HIS2.EQUIP_ID ) \n");			
				sqlBuf.append("                      JOIN (SELECT CODI_HW_VER, CODI_FW_VER, CODI_FW_BUILD, CODI_ID, MCU_CODI_BINDING_ID from MCU_CODI) CODIT ON (MCUT.MCU_CODI_ID = CODIT.MCU_CODI_BINDING_ID )  \n");		
			}		
			sqlBuf.append("               ) HIS3  \n");
			sqlBuf.append("          WHERE TRI.ID=HIS3.TR_ID  \n");
			if( trId != 0){
				sqlBuf.append("          AND TRI.ID = :trId \n");			
			}
			if(state.equals("Succ")) {
				sqlBuf.append("      AND SUCC > 0 \n");
			}else if(state.equals("Exec")) {
				sqlBuf.append("      AND PEXEC > 0  \n");
			}else if(state.equals("Cancel")) {
				sqlBuf.append("      AND CANCEL > 0  \n");
			}else if(state.equals("Error")) {
				sqlBuf.append("      AND ERROR > 0  \n");
			}
			sqlBuf.append("          AND CREATE_DATE BETWEEN :fromDate AND :toDate  \n");
			sqlBuf.append(") TRIGGERLIST  \n");			
		}


		logger.debug(sqlBuf.toString());
		
		SQLQuery query = getSession().createSQLQuery(sqlBuf.toString());
		query = getSession().createSQLQuery(sqlBuf.toString());
		while (fromDate.length() % 14 != 0) {
			fromDate = fromDate + "0" ;
   	    }
		
		if(toDate.length() == 8){
			toDate = toDate + "235959";
		}
		while (toDate.length() % 14 != 0) {
			toDate = toDate + "0" ;
   	    }
		query.setString("equip_kind", equip_kind);	
		query.setString("fromDate", fromDate);	
		query.setString("toDate", toDate);	
//		query.setInteger("locationId", locationId);

		if(!String.valueOf(condition.get("equip_type")).equals("")){
		query.setInteger("equip_type", Integer.parseInt(String.valueOf(condition.get("equip_type"))));			
		}
		if(!equip_vendor.equals("") && null != equip_vendor && !equip_vendor.equals("null")  ){
		query.setInteger("equip_vendor", Integer.parseInt(equip_vendor));			
		}
		if(!equip_model.equals("") &&  null != equip_model && !equip_model.equals("null")){
		query.setInteger("equip_model", Integer.parseInt(equip_model));			
		}
		if( trId != 0){
			query.setInteger("trId", trId);
		}
		if(!equip_id.equals("")){
			query.setString("equip_id", equip_id);
		}
		
		logger.debug("equip_kind="+equip_kind);
		logger.debug("equip_type="+String.valueOf(condition.get("equip_type")));
		logger.debug("equip_vendor="+equip_vendor);
		
		logger.debug("fromDate="+fromDate);
		logger.debug("toDate="+toDate);
		
		return String.valueOf(query.list().get(0));
	}
	
	/**
	 * 배포이력 TriggerList>TriggerInfo 조회 
	 * */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public List<Object> getTriggerListStep2(Map<String, Object> condition)throws Exception {
		
		String locationId =  String.valueOf(condition.get("locationId")) == null ? "" :String.valueOf(condition.get("locationId")) ;
		String tr_Id =  String.valueOf(condition.get("tr_Id")) == null ? "" :String.valueOf(condition.get("tr_Id")) ;
		String equip_kind =  String.valueOf(condition.get("equip_kind")) == null ? "" :String.valueOf(condition.get("equip_kind")) ;
		String equip_type =  String.valueOf(condition.get("equip_type")) == null ? "" :String.valueOf(condition.get("equip_type")) ;

		StringBuffer sqlBuf = new StringBuffer();
		sqlBuf.append(" SELECT  \n");
		sqlBuf.append("         HIS.TRIGGER_STEP, \n");
		sqlBuf.append("         HIS.TRIGGER_STATE, \n");
		sqlBuf.append("         HIS.OTA_STEP, \n");
		sqlBuf.append("         HIS.OTA_STATE, \n");
		sqlBuf.append("         HIS.ERROR_CODE, \n");
		sqlBuf.append("         HIS.TR_ID, \n");
		sqlBuf.append("         HIS.EQUIP_TYPE, \n");
		sqlBuf.append("         TRI.CREATE_DATE, \n");
		sqlBuf.append("         TRI.END_DATE, \n");
		sqlBuf.append("         TRI.SRC_FIRMWARE, \n");
		sqlBuf.append("         TRI.SRC_FWVER, \n");
		sqlBuf.append("         TRI.SRC_HWVER, \n");
		sqlBuf.append("         TRI.SRC_FWBUILD, \n");
		sqlBuf.append("         TRI.TARGET_FIRMWARE, \n");
		sqlBuf.append("         TRI.TARGET_FWVER, \n");
		sqlBuf.append("         TRI.TARGET_HWVER, \n");
		sqlBuf.append("         TRI.TARGET_FWBUILD, \n");

		if(equip_kind.equals("Modem")&&!equip_type.equals("MMIU")&&!equip_type.equals("IEIU")){
			sqlBuf.append("                MDM.SW_VER, \n");
			sqlBuf.append("                MDM.HW_VER,  \n");
			sqlBuf.append("                MDM.FW_REVISION, \n");	
		}else if(equip_kind.equals("MCU")||equip_type.equals("MMIU")||equip_type.equals("IEIU")){
			sqlBuf.append("                MCUT.SYS_SW_VERSION,  \n");
			sqlBuf.append("                MCUT.SYS_HW_VERSION,  \n");
			sqlBuf.append("                MCUT.SYS_SW_REVISION,  \n");		
		}else if(equip_kind.equals("Codi")){
			sqlBuf.append("                CODIT.CODI_FW_VER,  \n");
			sqlBuf.append("                CODIT.CODI_HW_VER,  \n");
			sqlBuf.append("                CODIT.CODI_FW_BUILD,  \n");		
		}
		sqlBuf.append("         (SELECT NAME FROM LOCATION WHERE ID = MCUT.LOCATION_ID) AS LOCATIONID, \n");
		sqlBuf.append("         HIS.TRIGGER_CNT, \n");
		sqlBuf.append("         MCUT.SYS_ID, \n");
		sqlBuf.append("         HIS.TRIGGER_HISTORY \n");
		if(equip_kind.equals("Modem")&&!equip_type.equals("MMIU")&&!equip_type.equals("IEIU")){
			sqlBuf.append("     ,MDM.DEVICE_SERIAL \n");			
		}

		sqlBuf.append(" FROM  \n");
		sqlBuf.append("       FIRMWARE_HISTORY HIS  \n");
		sqlBuf.append("       JOIN FIRMWARE_TRIGGER TRI ON (HIS.TR_ID=TRI.ID AND HIS.TR_ID = :tr_Id) \n");
		
		if(equip_kind.equals("Modem")&&!equip_type.equals("MMIU")&&!equip_type.equals("IEIU")){
            sqlBuf.append("       JOIN MODEM MDM ON(MDM.device_serial=HIS.equip_id AND MDM.LOCATION_ID = :locationId ) \n");			
            sqlBuf.append("       JOIN MCU MCUT ON(MCUT.ID = MDM.MCU_ID ) \n");			
		}else if(equip_kind.equals("MCU")||equip_type.equals("MMIU")||equip_type.equals("IEIU")){
			//MCU일경우
			sqlBuf.append("       JOIN MCU MCUT ON (MCUT.SYS_ID = HIS.EQUIP_ID AND MCUT.LOCATION_ID = :locationId) \n");			
		}else if(equip_kind.equals("Codi")){
			//Codi일경우
			sqlBuf.append("       JOIN MCU MCUT ON (MCUT.SYS_ID = HIS.EQUIP_ID AND MCUT.LOCATION_ID = :locationId) \n");		
			sqlBuf.append("       JOIN MCU_CODI CODIT ON (MCUT.MCU_CODI_ID = CODIT.MCU_CODI_BINDING_ID ) \n");			
		}			
		sqlBuf.append(" ORDER BY TR_ID desc \n");
		
		logger.info(sqlBuf.toString());
		
		SQLQuery query = getSession().createSQLQuery(sqlBuf.toString());
		query = getSession().createSQLQuery(sqlBuf.toString());
		
		query.setInteger("locationId", Integer.parseInt(locationId));
		query.setInteger("tr_Id", Integer.parseInt(tr_Id));
		
		return query.list();
	}

}
