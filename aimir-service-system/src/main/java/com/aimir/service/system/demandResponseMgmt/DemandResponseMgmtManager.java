package com.aimir.service.system.demandResponseMgmt;

import java.util.List;
import java.util.Map;

import com.aimir.model.system.DemandResponseEventLog;

/**
 * DemandResponseMgmtManager.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 5. 29.   v1.0       eunmiae         
 *
 */
public interface DemandResponseMgmtManager {

	//public List<Map<String, Object>> getHomeDeviceInfo(String groupId, String homeDeviceGroupName, String homeDeviceCategory);
	public List<Map<String, Object>> getHomeDeviceDRMgmtInfo(String groupId, String homeDeviceGroupName, String homeDeviceCategory);
	public void updateDrProgramMandatoryInfo(int id, String drProgramMandatory);
	public void updateEndDeviceDrLevel(int endDeviceId, int categoryCode, int drLevel);
	/**
	 * method name : 
	 * method Desc :
	 *
	 * @param userId
	 * @param contractNumber
	 * @param page
	 * @param limit
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	public List<Map<String, Object>> getDemandResponseHistory(String userId, String contractNumber, int page, int limit, String fromDate, String toDate);
	/**
	 * method name : getDemandResponseHistoryTotalCount
	 * method Desc : DR Event 이력의 총 건수를 취득한다.
	 *
	 * @param userId 로그인 유저의 아이디
	 * @param contractNumber 로그인 유저에게 맵핑된 계약 번호
	 * @param fromDate 검색 시작 날짜
	 * @param toDate 검색 종료 날짜
	 * @return
	 */
	public String getDemandResponseHistoryTotalCount(String userId, String contractNumber, String fromDate, String toDate);
	/**
	 * method name : pollDrWebService
	 * method Desc : DR서버로 부터 DR Event를 가져 온다.
	 *
	 * @return
	 */
	public List<Map<String, Object>> pollDrWebService();
	/**
	 * method name : deleteDemandResponseEventLog
	 * method Desc : DemandResponse Event 로그를 삭제한다. (Not Used)
	 *
	 * @param id
	 */
	public void deleteDemandResponseEventLog(int id);

	/**
	 * method name : setOptDREventOptOutStatus
	 * method Desc : Demand Response 참여 상태를 갱신한다.
	 *
	 * @param optOutStatus 참여 상태
	 * @param eventIdentifier 이벤트 식별자
	 * @param mcuId MCU ID for ACT Demo
	 * @param sensorId Sensor ID for ACT Demo
	 */
	public void setOptDREventOptOutStatus(int optOutStatus, String eventIdentifier, String mcuId, String sensorId);
}
