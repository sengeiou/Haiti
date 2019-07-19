package com.aimir.dao.device;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.EventAlertLogVO;
import com.aimir.util.Condition;

public interface EventAlertLogDao extends GenericDao<EventAlertLog, Long> {
	public List<EventAlertLogVO> getEventAlertLogRealTime(Set<Condition> conditions);

	public List<EventAlertLogVO> getEventAlertLogRealTimeForMini(Set<Condition> conditions, String searchStartDate,
			String searchEndDate);

	public List<EventAlertLogVO> getEventAlertLogHistory(Set<Condition> conditions);

	public List<EventAlertLogVO> getEventAlertLogHistory(Set<Condition> conditions, List<Integer> locations);

	public List<EventAlertLogVO> getEventAlertLogHistoryExcel(Set<Condition> conditions, List<Integer> locations);

	public List<EventAlertLogVO> getEventAlertLogHistoryCount(Set<Condition> conditions);

	public List<EventAlertLogVO> getEventAlertLogHistoryCount(Set<Condition> conditions, List<Integer> locations);

	// public List<EventAlertLogSummaryVO>
	// getEventAlertLogByActivatorType(String[] conditions);
	public List<Map<String, Object>> getEventAlertLogByActivatorType(Map<String, Object> conditionMap);

	// public List<EventAlertLogSummaryVO>
	// getEventAlertLogByActivatorTypeForMini(String[] conditions);
	// public List<EventAlertLogSummaryVO> getEventAlertLogByMessage(String[]
	// conditions);
	public List<Map<String, Object>> getEventAlertLogByMessage(Map<String, Object> conditionMap);

	// public List<EventAlertLogSummaryVO>
	// getEventAlertLogByMessageForMini(String[] conditions);
	public String getEventAlertLogCount(Map<String, String> map);

	@Deprecated
	public List<Map<String, String>> getEventAlertLogs(Map<String, String> map);

	/**
	 * @desc EventAlertLogHistory fetch dao (페이징 처리 추가 )
	 * @param conditions
	 * @param locations
	 * @return
	 */
	public List<EventAlertLogVO> getEventAlertLogHistory2(Set<Condition> conditions, List<Integer> locations,
			Map<String, String> conditionMap);

	/**
	 * @desc getEventAlertLogHistoryTotalCnt fetch dao
	 * @param conditions
	 * @return
	 */
	public String getEventAlertLogHistoryTotalCnt(Set<Condition> conditions, List<Integer> locations);

	/**
	 * method name : getEventAlertLogFromDB<b/> method Desc : MDIS.
	 * EventAlertLog 가젯에서 RealTime 데이터를 조회한다.
	 *
	 * @param conditionMap
	 *            {@code} Integer supplierId =
	 *            (Integer)conditionMap.get("supplierId"); String startDateTime
	 *            = (String)conditionMap.get("startDateTime");
	 * @return List of Map {activatorId, activatorType, activatorIp, status,
	 *         eventMessage, location, openTime, closeTime, duration}
	 */
	public List<Map<String, Object>> getEventAlertLogFromDB(Map<String, Object> conditionMap);

	public List<EventAlertLog> getOpenEventAlertLog(String activatorType, String activatorId, Integer eventAlertId);

	public long findTotalByConditions(Set<Condition> conditions);

	/**
	 * method name : getMcuEventAlertLogList<b/> method Desc : Concentrator
	 * Management 맥스가젯 History 탭에서 장애내역을 조회한다.
	 *
	 * @param conditionMap
	 * @param isCount
	 * @return
	 */
	public List<Map<String, Object>> getMcuEventAlertLogList(Map<String, Object> conditionMap, boolean isCount);

	public List<Map<String, Object>> getMcuLogType(Map<String, Object> conditionMap);
	
	public List<Map<String, Object>> getProblematicMetersEvent(Map<String, Object> conditionMap);		// INSERT SP-818

}