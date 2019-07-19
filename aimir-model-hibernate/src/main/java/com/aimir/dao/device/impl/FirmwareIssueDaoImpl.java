/**
 * Copyright Nuri Telecom Corp.
 * 파일명: FirmwareIssueDaoImpl
 * 작성일자/작성자 : 2016.09.13 박연경
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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.FirmwareIssueDao;
import com.aimir.model.device.FirmwareIssue;
import com.aimir.model.device.FirmwareIssuePk;
import com.aimir.util.Condition;

@Repository(value = "firmwareIssueDao")
public class FirmwareIssueDaoImpl extends AbstractHibernateGenericDao<FirmwareIssue, FirmwareIssuePk> implements FirmwareIssueDao {
	private static Log logger = LogFactory.getLog(FirmwareIssueDaoImpl.class);

	@Autowired
	protected FirmwareIssueDaoImpl(SessionFactory sessionFactory) {
		super(FirmwareIssue.class);
		super.setSessionFactory(sessionFactory);
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> getFirmwareIssueList(Map<String, Object> condition) {
		String supplierId = String.valueOf(condition.get("supplierId"));
		String locationId = String.valueOf(condition.get("locationId"));
		String fileName = String.valueOf(condition.get("fileName"));
		String modelName = String.valueOf(condition.get("modelName"));
		String fwVer = String.valueOf(condition.get("fwVer"));
		String equipKind = String.valueOf(condition.get("equipKind"));
		String startDate = String.valueOf(condition.get("startDate"));
		String endDate = String.valueOf(condition.get("endDate"));
		String isTotal = String.valueOf(condition.get("isTotal"));
		String commandType = String.valueOf(condition.get("commandType"));
		Integer page = (Integer) condition.get("page");
		Integer limit = (Integer) condition.get("limit");
		
		StringBuffer sqlBuf = new StringBuffer();
		
		if(isTotal.equals("true")) {
			sqlBuf.append("\nSELECT COUNT(*) AS cnt FROM ( ");
		}
		
		sqlBuf.append("SELECT        			                    											 \n");
		sqlBuf.append("       firm_issue.LOCATIONID,                											 \n");
		sqlBuf.append("       firm.FILENAME,                        											 \n");
		sqlBuf.append("       firm_issue.ISSUEDATE,                 											 \n");
		sqlBuf.append("       firm.EQUIP_MODEL,                     											 \n");
		sqlBuf.append("       firm.FW_VERSION,                      											 \n");
		sqlBuf.append("       firm.EQUIP_KIND,                      											 \n");
		sqlBuf.append("       firm_issue.TOTALCOUNT,                											 \n");
		sqlBuf.append("       firm_issue.STEP1COUNT,                											 \n");
		sqlBuf.append("       firm_issue.STEP2COUNT,               											     \n");
		sqlBuf.append("       firm_issue.STEP3COUNT,                											 \n");
		sqlBuf.append("       firm_issue.STEP4COUNT,                											 \n");
		sqlBuf.append("       firm_issue.STEP5COUNT,                											 \n");
		sqlBuf.append("       firm_issue.STEP6COUNT,               												 \n");
		sqlBuf.append("       firm_issue.STEP7COUNT,               												 \n");
		sqlBuf.append("       firm_issue.FIRMWAREID,               												 \n");
		sqlBuf.append("       firm.ID,                      												         \n");
		sqlBuf.append("       firm_issue.EXECUTE_TYPE,                      										 \n");
		sqlBuf.append("       firm_issue.COMMAND_TYPE                      										 \n");
		sqlBuf.append("FROM FIRMWARE_ISSUE firm_issue JOIN FIRMWARE firm ON (firm_issue.FIRMWAREID = firm.ID)    \n");
		sqlBuf.append("WHERE  firm.SUPPLIER_ID = :supplierId                               \n");
		sqlBuf.append("AND    firm.EQUIP_KIND = :equipKind                                 \n");
		if (!locationId.isEmpty())
			sqlBuf.append("AND    firm_issue.LOCATIONID = :locationId                      \n");
		if (!fileName.isEmpty())
			sqlBuf.append("AND    firm.FILENAME LIKE :fileName                             \n");
		if (!modelName.isEmpty())
			sqlBuf.append("AND    firm.EQUIP_MODEL LIKE :modelName                         \n");
		if (!fwVer.isEmpty())
			sqlBuf.append("AND    firm.FW_VERSION = :fwVer                                 \n");
		if (!commandType.isEmpty())
			sqlBuf.append("AND    firm_issue.COMMAND_TYPE = :commandType                                 \n");
		if ((!startDate.isEmpty()) && (!endDate.isEmpty()))
			sqlBuf.append("AND	  firm_issue.ISSUEDATE BETWEEN :startDate AND :endDate     \n");
		sqlBuf.append("ORDER BY  firm_issue.ISSUEDATE DESC, firm_issue.LOCATIONID ASC\n");
		
		if(isTotal.equals("true")) {
			sqlBuf.append("\n) totalCount");
		}

		SQLQuery query = getSession().createSQLQuery(sqlBuf.toString());
		query = getSession().createSQLQuery(sqlBuf.toString());
		query.setInteger("supplierId", Integer.parseInt(supplierId));
		query.setString("equipKind", equipKind);
		if (!locationId.isEmpty())
			query.setInteger("locationId", Integer.parseInt(locationId));
		if (!fileName.isEmpty())
			query.setString("fileName", new StringBuilder().append('%').append(fileName).append('%').toString());
		if (!modelName.isEmpty())
			query.setString("modelName", new StringBuilder().append('%').append(modelName).append('%').toString());
		if (!fwVer.isEmpty())
			query.setString("fwVer", fwVer);
		if (!commandType.isEmpty())
			query.setString("commandType", commandType);

		if ((!startDate.isEmpty()) && (!endDate.isEmpty())) {
			while (startDate.length() % 14 != 0) {
				startDate = startDate + "0";
			}
			if (endDate.length() == 8) {
				endDate = endDate + "235959";
			}
			while (endDate.length() % 14 != 0) {
				endDate = endDate + "0";
			}

			query.setString("startDate", startDate);
			query.setString("endDate", endDate);
		}
		
		if(!isTotal.equals("true")) {
			if (page != null && limit != null) {
				query.setFirstResult((page - 1) * limit);
				query.setMaxResults(limit);
			}
		}
		
		return query.list();
	}

	@Override
	public List<FirmwareIssue> getFirmwareIssue(Set<Condition> condition2) {
		// TODO Auto-generated method stub
		return null;
	}

}
