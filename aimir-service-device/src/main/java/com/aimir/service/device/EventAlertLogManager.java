package com.aimir.service.device;

import java.util.List;
import java.util.Map;

import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.EventAlertLogVO;

public interface EventAlertLogManager {
	
	public void addEventAlertLog(EventAlertLog eventAlertLog);
	public List<EventAlertLogVO> getEventAlertLogRealTime(String[] values);
	
	/**
	 * @Desc  EventAlertLogRealTime log fetch mng.
	 * @param userId
	 * @param supplierId
	 * @param first
	 * @param max
	 * @return
	 */
	public List<EventAlertLogVO> getEventAlertLogRealTimeForMax(Integer userId, Integer supplierId, Integer first, Integer max);
	
	public List<EventAlertLogVO> getEventAlertLogRealTimeForMini(Integer userId, Integer supplierId, String searchStartDate, String searchEndDate);
	public Map<String, Object> getEventAlertLogRealTimeTotal(Integer userId, Integer supplierId);
	public List<EventAlertLogVO> getEventAlertLogHistory(String[] values);
	public List<EventAlertLogVO> getEventAlertLogHistoryExcel(String[] values);
	public Map<String, Object> getEventAlertLogHistoryTotal(String[] values);
	
	
	
//	public List<EventAlertLogSummaryVO> getEventAlertLogByActivatorType(String[] conditions);
//	public List<EventAlertLogSummaryVO> getEventAlertLogByActivatorTypeForMini(String[] conditions);
    public List<Map<String, Object>> getEventAlertLogByActivatorType(Map<String, Object> conditionMap);
    public List<Map<String, Object>> getEventAlertLogByActivatorTypeForMini(Map<String, Object> conditionMap);
//	public List<EventAlertLogSummaryVO> getEventAlertLogByMessage(String[] conditions);
//	public List<EventAlertLogSummaryVO> getEventAlertLogByMessageForMini(String[] conditions);
    public List<Map<String, Object>> getEventAlertLogByMessage(Map<String, Object> conditionMap);
    public List<Map<String, Object>> getEventAlertLogByMessageForMini(Map<String, Object> conditionMap);

    
    /**
     * @desc 이벤트 얼럿 로그 이력의 토탈 카운트 fetch mng.
     * @param values
     * @return
     */
	public String getEventAlertLogHistoryTotalCnt(String[] values);
    
    /**
	 * @Desc : fetch Event Alert Log History for extjs
	 * @param values
	 * @return
	 */
	public List<EventAlertLogVO> getEventAlertLogHistory2(String[] values, Map<String, String> conditionMap);
	
    
    
    
    /**
     * method name : getEventAlertLogFromDB<b/>
     * method Desc : MDIS. EventAlertLog 가젯에서 RealTime 데이터를 DB에서 조회한다.
     *
     * @param conditionMap
     * {@code}
     *         Integer supplierId = (Integer)conditionMap.get("supplierId");
     *         String startDateTime = (String)conditionMap.get("startDateTime");
     * @return List of Map {activatorId, activatorType, activatorIp, status, eventMessage, location, openTime, closeTime, duration}
     */
    public List<Map<String, Object>> getEventAlertLogFromDB(Map<String, Object> conditionMap);
    
	public List<EventAlertLog> getEventAlertLogs(
			Map<String, String> parameters, Map<String, Integer> pagingVars);
	
	public long getEventAlertLogCount(Map<String, String> parameters);
}