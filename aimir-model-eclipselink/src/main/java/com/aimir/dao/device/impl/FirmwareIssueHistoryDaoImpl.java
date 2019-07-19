/**
 * Copyright Nuri Telecom Corp.
 * 파일명: FirmwareHistoryDaoImpl
 * 작성일자/작성자 : 2016.09.13 elevas park
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.FirmwareIssueDao;
import com.aimir.dao.device.FirmwareIssueHistoryDao;
import com.aimir.model.device.Device.DeviceType;
import com.aimir.model.device.FirmwareIssue;
import com.aimir.model.device.FirmwareIssueHistory;
import com.aimir.model.device.FirmwareIssueHistoryPk;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;

@Repository(value = "firmwareIssueHistoryDao")
public class FirmwareIssueHistoryDaoImpl extends AbstractJpaDao<FirmwareIssueHistory, FirmwareIssueHistoryPk> implements FirmwareIssueHistoryDao {
	private static Log logger = LogFactory.getLog(FirmwareIssueHistoryDaoImpl.class);
	
	@Autowired
	private FirmwareIssueDao firmwareIssueDao;

	protected FirmwareIssueHistoryDaoImpl() {
		super(FirmwareIssueHistory.class);
	}

	@Override
	public Class<FirmwareIssueHistory> getPersistentClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> getSumFieldByCondition(Set<Condition> conditions, String field, String... groupBy) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> getFirmwareIssueHistoryList(Map<String, Object> condition) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public Map<String, Integer> getHistoryStepCount(FirmwareIssueHistory firmwareIssueHistory) {
		StringBuilder sb = new StringBuilder();
		sb.append("select ");
		sb.append("  count(case when step = ? then 1 end) step1 ");
		sb.append(", count(case when step = ? then 1 end) step2 ");
		sb.append(", count(case when step = ? then 1 end) step3 ");
		sb.append(", count(case when step = ? then 1 end) step4 ");
		sb.append(", count(case when step = ? then 1 end) step5 ");
		//sb.append(", count(case when step = ? then 1 end) step6 ");
		sb.append(", count(case when step = ? then 1 end) step7 ");
		sb.append(" from FIRMWARE_ISSUE_HISTORY  ");
		sb.append(" where LOCATIONID = ? and FIRMWAREID = ? and ISSUEDATE = ?");

		Query query = getEntityManager().createNativeQuery(sb.toString());

		logger.debug("locationId=" + firmwareIssueHistory.getLocationId() + ", firmwareId=" + firmwareIssueHistory.getFirmwareId() + ", issueDate=" + firmwareIssueHistory.getIssueDate());

		query.setParameter(1, "Started writing FW");
		query.setParameter(2, "Took OTA Command");
		query.setParameter(3, "Ended writing FW");
		query.setParameter(4, "OTA Result");
		query.setParameter(5, "Firmware Update");
		//query.setParameter(6, "Firmware Update");
		query.setParameter(6, "Intergrity Deviation");
		query.setParameter(7, firmwareIssueHistory.getLocationId());
		query.setParameter(8, firmwareIssueHistory.getFirmwareId());
		query.setParameter(9, firmwareIssueHistory.getIssueDate());
		
		@SuppressWarnings("unchecked")
		List<Object[]> returnList = query.getResultList();

		Map<String, Integer> resultMap = null;

		if (returnList != null && returnList.size() == 1) {
			Object[] resultObj = returnList.get(0);

			resultMap = new HashMap<>();
			resultMap.put("step1", Integer.valueOf(String.valueOf(resultObj[0])));
			resultMap.put("step2", Integer.valueOf(String.valueOf(resultObj[1])));
			resultMap.put("step3", Integer.valueOf(String.valueOf(resultObj[2])));
			resultMap.put("step4", Integer.valueOf(String.valueOf(resultObj[3])));
			resultMap.put("step5", Integer.valueOf(String.valueOf(resultObj[4])));
			//resultMap.put("step6", Integer.valueOf(String.valueOf(resultObj[5])));
			resultMap.put("step7", Integer.valueOf(String.valueOf(resultObj[5])));
		}

		logger.debug("resultMap = " + resultMap != null && !resultMap.isEmpty() ? resultMap.toString() : "");

		return resultMap;
	}
	
	
	/**
	 * History information Update.
	 * @param deviceId
	 * @param deviceType
	 * @param openTime
	 */
	public void updateOTAHistory(String eventMessage, String deviceId, DeviceType deviceType, String openTime, String resultStatus) {
		updateOTAHistory(eventMessage, deviceId, deviceType, openTime, resultStatus, null);
	}
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
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
			firmwareIssueHistory.setStep(eventMessage);  // 5
			firmwareIssueHistory.setUpdateDate(openTime);
			firmwareIssueHistory.setResultStatus(resultStatus);
			
			//update(firmwareIssueHistory);
			update(firmwareIssueHistory);			
			logger.debug("[" + eventMessage + "] FirmwareIssueHistory information update ===> " + firmwareIssueHistory.toString());
		}
	}

	/**
	 * Update OTA History for EV_SP_200_63_0_Action. 
	 * @param eventMessage
	 * @param openTime
	 * @param resultStatus
	 * @param requestId
	 */
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public void updateOTAHistoryFor63_59_31(String eventMessage, String openTime, String resultStatus, String requestId) {
		logger.debug("Update OTA History params. EventMessage=" + eventMessage
				+ ", OpentTime=" + openTime 
				+ ", ResultStatus=" + resultStatus 
				+ ", RequestId=" + requestId);
		
		Set<Condition> condition = new HashSet<Condition>();
		condition.add(new Condition("requestId", new Object[] { requestId }, null, Restriction.EQ));
		List<FirmwareIssueHistory> firmwareIssueHistoryList = findByConditions(condition);
		
		logger.debug("FirmwareIssueHistory list size = " + firmwareIssueHistoryList == null ? "null~" : firmwareIssueHistoryList.size());
		
		for(FirmwareIssueHistory history : firmwareIssueHistoryList){
			history.setStep(eventMessage);  // 5
			history.setUpdateDate(openTime);
			history.setResultStatus(resultStatus);
			
			update(history);			
			logger.debug("[" + eventMessage + "] FirmwareIssueHistory information update ===> " + history.toString());
		}				
	}
	
	
	/**
	 * History information Update.
	 * @param deviceId
	 * @param deviceType
	 * @param openTime
	 */
	public void updateOTAHistoryIssue(String eventMessage, String deviceId, DeviceType deviceType) {
		updateOTAHistoryIssue(eventMessage, deviceId, deviceType, null);
	}
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public void updateOTAHistoryIssue(String eventMessage, String deviceId, DeviceType deviceType, String requestId) {
		logger.info("Update OTA History Issue params. DeviceId=" + deviceId 
				+ ", EventMessage=" + eventMessage
				+ ", DeviceType=" + deviceType.name()
				+ ", requestId=" + requestId);

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
		
			logger.debug("[" + eventMessage + "] FirmwareIssueHistory information update ===> " + firmwareIssueHistory.toString());
		}else {
			logger.error("[" + eventMessage + "] FirmwareIssueHistory information is null.");
			return;
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
			//firmwareIssue.setStep6Count(hStepCount.get("step6"));
			firmwareIssue.setStep7Count(hStepCount.get("step7"));
			
			/*
			 * STEP별 진행한 장비의 누적 수를 표시할때 사용
			 */
			//firmwareIssue.setStep5Count(hStepCount.get("step5") + 1);			
			//log.debug("Step5 = {" + hStepCount.get("step5") + "}, setVAlue = {" + hStepCount.get("step5") + 1 + "}, total={" + firmwareIssue.toString() + "}");
			
			//firmwareIssueDao.update(firmwareIssue);
			firmwareIssueDao.update(firmwareIssue);
			
			logger.debug("[" + eventMessage + "] FirmwareIssue information update complet");
		}

	}
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public void updateOTAHistoryIssueFor63_59_31(String eventMessage, String requestId) {
		logger.info("Update OTA History Issue params."
				+ ", EventMessage=" + eventMessage
				+ ", requestId=" + requestId);

		Set<Condition> condition = new HashSet<Condition>();
		condition.add(new Condition("requestId", new Object[] { requestId }, null, Restriction.EQ));
		List<FirmwareIssueHistory> firmwareIssueHistoryList = findByConditions(condition);
		
		for(FirmwareIssueHistory history : firmwareIssueHistoryList){
			logger.debug("[" + eventMessage + "] FirmwareIssueHistory information update ===> " + history.toString());
			
			/*
			 * 그룹 Device OTA 이력 UPDATE
			 */
			Map<String, Integer> hStepCount = getHistoryStepCount(history);
			logger.debug("[" + eventMessage + "] FirmwareIssueHistory Step count information ===> " + hStepCount.toString());
			
			Set<Condition> condition2 = new HashSet<Condition>();
			condition2.add(new Condition("id.locationId", new Object[] { history.getLocationId() }, null, Restriction.EQ));
			condition2.add(new Condition("id.firmwareId", new Object[] { history.getFirmwareId() }, null, Restriction.EQ));
			condition2.add(new Condition("id.issueDate", new Object[] { history.getIssueDate() }, null, Restriction.EQ));

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
				//firmwareIssue.setStep6Count(hStepCount.get("step6"));
				firmwareIssue.setStep7Count(hStepCount.get("step7"));
				
				/*
				 * STEP별 진행한 장비의 누적 수를 표시할때 사용
				 */
				//firmwareIssue.setStep5Count(hStepCount.get("step5") + 1);			
				//log.debug("Step5 = {" + hStepCount.get("step5") + "}, setVAlue = {" + hStepCount.get("step5") + 1 + "}, total={" + firmwareIssue.toString() + "}");
				
				firmwareIssueDao.update(firmwareIssue);
				
				logger.debug("[" + eventMessage + "] FirmwareIssue information update complet");
			}			
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FirmwareIssueHistory> getRetryTargetList(String issueDate) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT fih FROM FirmwareIssueHistory fih"); 
		sb.append(" where (fih.id.issueDate = :issueDate and fih.resultStatus != 'Success' and fih.resultStatus not like '%[DOWNLOAD_SUCCESS]%')");
		sb.append(" or (fih.id.issueDate = :issueDate and fih.resultStatus is null)");		
		
        Query query = em.createQuery(sb.toString(), FirmwareIssueHistory.class);
        query.setParameter("issueDate", issueDate);

        return query.getResultList();
	}
	
	@Transactional(value = "transactionManager", readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<FirmwareIssueHistory> getTargetList(Map<String, String> params) {
		if(params != null) {
			Set<Condition> condition = new HashSet<Condition>();
			if(params.containsKey("deviceId")) {
				condition.add(new Condition("id.deviceId", new Object[] { params.get("deviceId") }, null, Restriction.EQ));	
			}
			if(params.containsKey("deviceType")) {
				condition.add(new Condition("id.deviceType", new Object[] { params.get("deviceType") }, null, Restriction.EQ));	
			}
			if(params.containsKey("issueDate")) {
				condition.add(new Condition("id.issueDate", new Object[] { params.get("issueDate") }, null, Restriction.EQ));
			}
			if(params.containsKey("dcuId")) {
				condition.add(new Condition("dcuId", new Object[] { params.get("dcuId") }, null, Restriction.EQ));
			}
			
			condition.add(new Condition("id.issueDate", null, null, Condition.Restriction.ORDERBYDESC));			

			List<FirmwareIssueHistory> firmwareIssueHistoryList = findByConditions(condition);

			return firmwareIssueHistoryList;
		}else {
			return null;
		}
	}
}
