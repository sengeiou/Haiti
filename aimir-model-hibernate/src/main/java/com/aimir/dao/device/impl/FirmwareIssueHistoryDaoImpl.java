/**
 * Copyright Nuri Telecom Corp.
 * 파일명: FirmwareIssueHistoryDaoImpl
 * 작성일자/작성자 : 2016.09.13 elevas
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.FirmwareIssueDao;
import com.aimir.dao.device.FirmwareIssueHistoryDao;
import com.aimir.model.device.Device.DeviceType;
import com.aimir.model.device.FirmwareIssue;
import com.aimir.model.device.FirmwareIssueHistory;
import com.aimir.model.device.FirmwareIssueHistoryPk;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;

@Repository(value = "firmwareIssueHistoryDao")
public class FirmwareIssueHistoryDaoImpl extends AbstractHibernateGenericDao<FirmwareIssueHistory, FirmwareIssueHistoryPk> implements FirmwareIssueHistoryDao {
	private static Log logger = LogFactory.getLog(FirmwareIssueHistoryDaoImpl.class);

	@Autowired
	FirmwareIssueDao firmwareIssueDao;

	@Autowired
	protected FirmwareIssueHistoryDaoImpl(SessionFactory sessionFactory) {
		super(FirmwareIssueHistory.class);
		super.setSessionFactory(sessionFactory);
	}

	@Override
	public List<Object> getFirmwareIssueHistoryList(Map<String, Object> condition) throws Exception {
		String supplierId = String.valueOf(condition.get("supplierId"));
		String firmwareId = String.valueOf(condition.get("firmwareId"));
		String locationId = String.valueOf(condition.get("locationId"));
		String fileName = String.valueOf(condition.get("fileName"));
		String modelName = String.valueOf(condition.get("modelName"));
		String fwVer = String.valueOf(condition.get("fwVer"));
		String equipKind = String.valueOf(condition.get("equipKind"));
		String step = String.valueOf(condition.get("step"));
		String deviceId = String.valueOf(condition.get("deviceId"));
		String startDate = String.valueOf(condition.get("startDate"));
		String endDate = String.valueOf(condition.get("endDate"));
		String issueDate = String.valueOf(condition.get("issueDate"));
		String isTotal = String.valueOf(condition.get("isTotal"));
		Integer page = (Integer) condition.get("page");
		Integer limit = (Integer) condition.get("limit");

		StringBuffer sqlBuf = new StringBuffer();

		if (isTotal.equals("true")) {
			sqlBuf.append("\nSELECT COUNT(*) AS cnt FROM ( ");
		}

		sqlBuf.append("SELECT        			                    											         				\n");
		sqlBuf.append("       firm_issue_history.LOCATIONID,                											 				\n");
		sqlBuf.append("       firm.FILENAME,                        											         				\n");
		sqlBuf.append("       firm_issue_history.ISSUEDATE,                 											 				\n");
		sqlBuf.append("       firm.EQUIP_MODEL,                     											         				\n");
		sqlBuf.append("       firm.FW_VERSION,                      											         				\n");
		sqlBuf.append("       firm.EQUIP_KIND,                      											         				\n");
		sqlBuf.append("       firm_issue_history.DEVICEID,                      										 				\n");
		sqlBuf.append("       firm_issue_history.STEP,                      											 				\n");
		sqlBuf.append("       firm_issue_history.UPDATEDATE,                      										 				\n");
		sqlBuf.append("       firm_issue_history.RESULT_STATUS                     										 				\n");
		sqlBuf.append("FROM FIRMWARE_ISSUE_HISTORY firm_issue_history JOIN FIRMWARE firm ON (firm_issue_history.FIRMWAREID = firm.ID)	\n");
		sqlBuf.append("WHERE  firm.SUPPLIER_ID = :supplierId                        		    \n");
		sqlBuf.append("AND    firm.EQUIP_KIND = :equipKind                          		    \n");
		if(!step.equals("All"))
			sqlBuf.append("AND    firm_issue_history.STEP = :step                               \n");
		if (!locationId.isEmpty())
			sqlBuf.append("AND    firm_issue_history.LOCATIONID = :locationId                   \n");
		if (!firmwareId.isEmpty())
			sqlBuf.append("AND    firm_issue_history.FIRMWAREID = :firmwareId                   \n");
		if (!issueDate.isEmpty())
			sqlBuf.append("AND    firm_issue_history.ISSUEDATE = :issueDate                     \n");
		if (!fileName.isEmpty())
			sqlBuf.append("AND    firm.FILENAME LIKE :fileName                         		    \n");
		if (!modelName.isEmpty())
			sqlBuf.append("AND    firm.EQUIP_MODEL LIKE :modelName                     		    \n");
		if (!fwVer.isEmpty())
			sqlBuf.append("AND    firm.FW_VERSION = :fwVer                          		    \n");
		if (!deviceId.isEmpty())
			sqlBuf.append("AND    firm_issue_history.DEVICEID LIKE :deviceId                    \n");
		if ((!startDate.isEmpty()) && (!endDate.isEmpty()))
			sqlBuf.append("AND	  firm_issue_history.ISSUEDATE BETWEEN :startDate AND :endDate  \n");
		sqlBuf.append("ORDER BY  firm_issue_history.ISSUEDATE DESC\n");

		if (isTotal.equals("true")) {
			sqlBuf.append("\n) totalCount");
		}

		SQLQuery query = getSession().createSQLQuery(sqlBuf.toString());
		query.setInteger("supplierId", Integer.parseInt(supplierId));
		query.setString("equipKind", equipKind);
		if(!step.equals("All"))
			query.setString("step", step);
		if (!locationId.isEmpty())
			query.setInteger("locationId", Integer.parseInt(locationId));
		if (!firmwareId.isEmpty())
			query.setInteger("firmwareId", Integer.parseInt(firmwareId));
		if (!issueDate.isEmpty())
			query.setString("issueDate", issueDate);
		if (!fileName.isEmpty())
			query.setString("fileName", new StringBuilder().append('%').append(fileName).append('%').toString());
		if (!modelName.isEmpty())
			query.setString("modelName", new StringBuilder().append('%').append(modelName).append('%').toString());
		if (!fwVer.isEmpty())
			query.setString("fwVer", fwVer);
		if (!deviceId.isEmpty())
			query.setString("deviceId", new StringBuilder().append('%').append(deviceId).append('%').toString());

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

		if (!isTotal.equals("true")) {
			if (page != null && limit != null) {
				query.setFirstResult((page - 1) * limit);
				query.setMaxResults(limit);
			}
		}

		return query.list();
	}

	@Override
	public Map<String, Integer> getHistoryStepCount(FirmwareIssueHistory firmwareIssueHistory) {
		// TODO Auto-generated method stub
		return null;
	}

	//	@SuppressWarnings("unchecked")
	//	@Override
	//	public Map<String, Integer> getHistoryStepCount(FirmwareIssueHistory firmwareIssueHistory) {
	//		StringBuilder sb = new StringBuilder();
	//		sb.append("select ");
	//		sb.append("  count(case when step = 1 then 1 end) step1 ");
	//		sb.append(", count(case when step = 2 then 1 end) step2 ");
	//		sb.append(", count(case when step = 3 then 1 end) step3 ");
	//		sb.append(", count(case when step = 4 then 1 end) step4 ");
	//		sb.append(", count(case when step = 5 then 1 end) step5 ");
	//		sb.append(", count(case when step = 6 then 1 end) step6 ");
	//		sb.append(", count(case when step = 7 then 1 end) step7 ");
	//		sb.append(" from FIRMWARE_ISSUE_HISTORY  ");
	//		sb.append(" where LOCATIONID = :locationId and FIRMWAREID = :firmwareId and ISSUEDATE = :issueDate");
	//
	//		Query query = getSession().createQuery(sb.toString());
	//
	//		query.setInteger("locationId", firmwareIssueHistory.getLocationId());
	//		query.setLong("firmwareId", firmwareIssueHistory.getFirmwareId());
	//		query.setString("issueDate", firmwareIssueHistory.getIssueDate());
	//		List<Map<String, Integer>> returnList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	//
	//		return 1 == returnList.size() ? returnList.get(0) : null;
	//	}

	//	@Override
	//	public List<FirmwareIssueHistory> getRetryGroup() {
	//		List<Map<String, Object>> result;
	//		
	//		StringBuilder sb = new StringBuilder();
	//		sb.append("SELECT FIH.LOCATIONID, FIH.FIRMWAREID, FIH.ISSUEDATE, FIH.STEP, FIH.DEVICETYPE, FIH.DEVICEID, FIH.USE_BYPASS");
	//		sb.append("FROM FIRMWARE_ISSUE_HISTORY FIH LEFT OUTER JOIN FIRMWARE_ISSUE FI ON FIH.LOCATIONID = FI.LOCATIONID AND FIH.FIRMWAREID = FI.FIRMWAREID AND FIH.ISSUEDATE = FI.ISSUEDATE ");
	//		sb.append("        LEFT OUTER JOIN FIRMWARE F ON FIH.FIRMWAREID = F.ID");
	//		sb.append("WHERE STEP1COUNT IS :status");  // step1count 를 플레그값으로 바꿀것
	//		
	//		Query query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));
	//	    query.setInteger("status", 1);
	//	    result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	//
	//	    List<FirmwareIssueHistory> retryGroup = new ArrayList<>();
	//	    for(Map map : result){
	//	    	FirmwareIssueHistory fi = new FirmwareIssueHistory();
	//	    	fi.setIssueDate(String.valueOf(map.get("ISSUEDATE")));
	//	    	fi.setLocationId(Integer.valueOf(String.valueOf(map.get("LOCATIONID"))));
	//	    	fi.setFirmwareId(Long.valueOf(String.valueOf(map.get("FIRMWAREID"))));
	//	    	fi.setStep(String.valueOf(map.get("STEP")));
	//	    	fi.setDeviceType(DeviceType.getItem(Integer.valueOf(String.valueOf(map.get("DEVICEID")))));
	//	    	
	//	    	boolean temp = String.valueOf(map.get("STEP")).equals("1") ? true : false;
	//	    	fi.setUesBypass(temp);
	//	    	
	//	    	retryGroup.add(fi);
	//	    }
	//	    return retryGroup;
	//	}

	/**
	 * History information Update.
	 * 
	 * @param deviceId
	 * @param deviceType
	 * @param openTime
	 */
	public void updateOTAHistory(String eventMessage, String deviceId, DeviceType deviceType, String openTime, String resultStatus) {
		updateOTAHistory(eventMessage, deviceId, deviceType, openTime, resultStatus, null);
	}
	public void updateOTAHistory(String eventMessage, String deviceId, DeviceType deviceType, String openTime, String resultStatus, String requestId) {
		logger.info("Update OTA History params. DeviceId=" + deviceId 
				+ ", EventMessage=" + eventMessage
				+ ", DeviceType=" + deviceType.name() 
				+ ", OpentTime=" + openTime 
				+ ", ResultStatus=" + resultStatus 
				+ ", RequestId=" + requestId);
		
		/*
		 * 개별 Device OTA 이력 UPDATE. 
		 *  - DCU의 경우 Trap이벤트에 issuedate, firmwareid 정보가 없기때문에 가장 최근에 실행한 Device 의 이력을 업데이트하는 방식으로 진행함. 
		 */
		Set<Condition> condition = new HashSet<Condition>();
		condition.add(new Condition("id.deviceId", new Object[] { deviceId }, null, Restriction.EQ));
		condition.add(new Condition("id.deviceType", new Object[] { deviceType }, null, Restriction.EQ));
		if(requestId != null){
			condition.add(new Condition("requestId", new Object[] { requestId }, null, Restriction.EQ));
		}
		condition.add(new Condition("id.issueDate", null, null, Condition.Restriction.ORDERBYDESC));

		FirmwareIssueHistory firmwareIssueHistory = null;
		List<FirmwareIssueHistory> firmwareIssueHistoryList = findByConditions(condition);
		if (firmwareIssueHistoryList != null && 0 < firmwareIssueHistoryList.size()) {
			firmwareIssueHistory = firmwareIssueHistoryList.get(0); // 가장 최근에 실행한 이력
			firmwareIssueHistory.setStep(eventMessage); // 5
			firmwareIssueHistory.setUpdateDate(openTime);
			firmwareIssueHistory.setResultStatus(resultStatus);

			update(firmwareIssueHistory);
			logger.debug("[" + eventMessage + "] FirmwareIssueHistory information update ===> " + firmwareIssueHistory.toString());
		}

//		/*
//		 * 그룹 Device OTA 이력 UPDATE
//		 */
//		Map<String, Integer> hStepCount = getHistoryStepCount(firmwareIssueHistory);
//		logger.debug("[" + eventMessage + "] FirmwareIssueHistory Step count information ===> " + hStepCount.toString());
//
//		Set<Condition> condition2 = new HashSet<Condition>();
//		condition2.add(new Condition("id.locationId", new Object[] { firmwareIssueHistory.getLocationId() }, null, Restriction.EQ));
//		condition2.add(new Condition("id.firmwareId", new Object[] { firmwareIssueHistory.getFirmwareId() }, null, Restriction.EQ));
//		condition2.add(new Condition("id.issueDate", new Object[] { firmwareIssueHistory.getIssueDate() }, null, Restriction.EQ));
//
//		List<FirmwareIssue> firmwareIssueList = firmwareIssueDao.getFirmwareIssue(condition2);
//		if (firmwareIssueList != null && firmwareIssueList.size() == 1) {
//			FirmwareIssue firmwareIssue = firmwareIssueList.get(0);
//
//			/*
//			 * STEP별 진행하고 있는 장비의 수를 표시할때사용
//			 */
//			firmwareIssue.setStep1Count(hStepCount.get("step1"));
//			firmwareIssue.setStep2Count(hStepCount.get("step2"));
//			firmwareIssue.setStep3Count(hStepCount.get("step3"));
//			firmwareIssue.setStep4Count(hStepCount.get("step4"));
//			firmwareIssue.setStep5Count(hStepCount.get("step5"));
//			firmwareIssue.setStep6Count(hStepCount.get("step6"));
//			firmwareIssue.setStep7Count(hStepCount.get("step7"));
//
//			/*
//			 * STEP별 진행한 장비의 누적 수를 표시할때 사용
//			 */
//			//firmwareIssue.setStep5Count(hStepCount.get("step5") + 1);			
//			//log.debug("Step5 = {" + hStepCount.get("step5") + "}, setVAlue = {" + hStepCount.get("step5") + 1 + "}, total={" + firmwareIssue.toString() + "}");
//
//			//firmwareIssueDao.update(firmwareIssue);
//			firmwareIssueDao.update_required(firmwareIssue);
//			
//			logger.debug("[" + eventMessage + "] FirmwareIssue information update ===> " + firmwareIssue.toString());
//		}
	}
	
	/**
	 * History information Update.
	 * @param deviceId
	 * @param deviceType
	 * @param openTime
	 */
	public void updateOTAHistoryIssue(String eventMessage, String deviceId, DeviceType deviceType) {
		logger.info("Update OTA History params. DeviceId=" + deviceId 
				+ ", EventMessage=" + eventMessage
				+ ", DeviceType=" + deviceType.name());

		Set<Condition> condition = new HashSet<Condition>();
		condition.add(new Condition("id.deviceId", new Object[] { deviceId }, null, Restriction.EQ));
		condition.add(new Condition("id.deviceType", new Object[] { deviceType }, null, Restriction.EQ));
		condition.add(new Condition("id.issueDate", null, null, Condition.Restriction.ORDERBYDESC));

		FirmwareIssueHistory firmwareIssueHistory = null;
		List<FirmwareIssueHistory> firmwareIssueHistoryList = findByConditions(condition);
		if (firmwareIssueHistoryList != null && 0 < firmwareIssueHistoryList.size()) {
			firmwareIssueHistory = firmwareIssueHistoryList.get(0); // 가장 최근에 실행한 이력
		
			logger.debug("[" + eventMessage + "] FirmwareIssueHistory information update ===> " + firmwareIssueHistory.toString());
		}

		/*
		 * 그룹 Device OTA 이력 UPDATE
		 */
		Map<String, Integer> hStepCount = getHistoryStepCount(firmwareIssueHistory);
		logger.debug("[" + eventMessage + "] FirmwareIssueHistory Step count information ===> " + hStepCount.toString());
		
		Set<Condition> condition2 = new HashSet<Condition>();
		condition2.add(new Condition("id.locationId", new Object[] { firmwareIssueHistory.getLocationId() }, null, Restriction.EQ));
		condition2.add(new Condition("id.firmwareId", new Object[] { firmwareIssueHistory.getFirmwareId() }, null, Restriction.EQ));
		condition2.add(new Condition("id.issueDate", new Object[] { firmwareIssueHistory.getIssueDate() }, null, Restriction.EQ));

		List<FirmwareIssue> firmwareIssueList = firmwareIssueDao.getFirmwareIssue(condition2);
		if (firmwareIssueList != null && firmwareIssueList.size() == 1) {
			FirmwareIssue firmwareIssue = firmwareIssueList.get(0);

			/*
			 * STEP별 진행하고 있는 장비의 수를 표시할때사용
			 */
			firmwareIssue.setStep1Count(hStepCount.get("step1"));
			firmwareIssue.setStep2Count(hStepCount.get("step2"));
			firmwareIssue.setStep3Count(hStepCount.get("step3"));
			firmwareIssue.setStep4Count(hStepCount.get("step4"));
			firmwareIssue.setStep5Count(hStepCount.get("step5"));
			firmwareIssue.setStep6Count(hStepCount.get("step6"));
			firmwareIssue.setStep7Count(hStepCount.get("step7"));
			
			/*
			 * STEP별 진행한 장비의 누적 수를 표시할때 사용
			 */
			//firmwareIssue.setStep5Count(hStepCount.get("step5") + 1);			
			//log.debug("Step5 = {" + hStepCount.get("step5") + "}, setVAlue = {" + hStepCount.get("step5") + 1 + "}, total={" + firmwareIssue.toString() + "}");
			
			firmwareIssueDao.update(firmwareIssue);
			
			logger.debug("[" + eventMessage + "] FirmwareIssue information update ===> " + firmwareIssue.toString());
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<FirmwareIssueHistory> getRetryTargetList(String issueDate) {
		List<Map<String, Object>> result;
		
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT STEP, UPDATEDATE, DEVICETYPE, LOCATIONID, FIRMWAREID, ISSUEDATE, DEVICEID, USE_BYPASS, RESULT_STATUS");  
		sb.append(" FROM FIRMWARE_ISSUE_HISTORY"); 
		sb.append(" where (ISSUEDATE = :issuedate and RESULT_STATUS != 'Success' and RESULT_STATUS not like '%[DOWNLOAD_SUCCESS]%')");
		sb.append(" or (ISSUEDATE = :issuedate and RESULT_STATUS is null)");

		
		SQLQuery query = getSession().createSQLQuery(sb.toString());
		query.setString("issuedate", issueDate);
	    result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

	    List<FirmwareIssueHistory> retryTargetList = new ArrayList<>();
	    for(Map map : result){
	    	FirmwareIssueHistory fi = new FirmwareIssueHistory();
	    	fi.setStep(String.valueOf(map.get("STEP")));
	    	fi.setUpdateDate(String.valueOf(map.get("UPDATEDATE")));
	    	fi.setDeviceType(DeviceType.getItem(Integer.valueOf(String.valueOf(map.get("DEVICETYPE")))));
	    	fi.setLocationId(Integer.valueOf(String.valueOf(map.get("LOCATIONID"))));
	    	fi.setFirmwareId(Long.valueOf(String.valueOf(map.get("FIRMWAREID"))));
	    	fi.setIssueDate(String.valueOf(map.get("ISSUEDATE")));
	    	fi.setDeviceId(String.valueOf(map.get("DEVICEID")));   	
	    	fi.setUesBypass(String.valueOf(map.get("USE_BYPASS")).equals("1") ? true : false);
	    	fi.setResultStatus(String.valueOf(map.get("RESULT_STATUS")));
	    	
	    	retryTargetList.add(fi);
	    }
	    return retryTargetList;
	}

	@Override
	public void updateOTAHistoryFor63_59_31(String eVENT_MESSAGE, String openTime, String string, String requestId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateOTAHistoryIssue(String eVENT_MESSAGE, String deviceId, DeviceType deviceType, String requestId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateOTAHistoryIssueFor63_59_31(String eVENT_MESSAGE, String requestId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<FirmwareIssueHistory> getTargetList(Map<String, String> targetParams) {
		// TODO Auto-generated method stub
		return null;
	}
}
