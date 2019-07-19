package com.aimir.service.device;

import java.util.List;
import java.util.Map;

import com.aimir.model.device.EventAlert;
import com.aimir.model.device.Threshold;		// INSERT SP-193

public interface EventAlertManager {
	public EventAlert getEventAlert(Integer eventAlertId);
	public List<EventAlert> getEventAlerts();
	public List<EventAlert> getEventAlertsByType(String eventAlertType);
	
	/**
     * EventAlert 테이블 데이터를 조회
     * @param conditionMap
     * @return paging 처리된 리스트
     */
    public List<EventAlert> getEventAlertListWithPaging(Map<String, String> conditionMap);
    
    /**
     * EventAlert 테이블의 데이터 개수 조회 
     * @param conditionMap
     * @return number
     */ 
    public Integer getEventAlertListCount(Map<String, String> conditionMap);
    
    /**
     * 변경내용을 EventAlert 테이블에 적용
     * @param conditionMap
     * @return Map<String,String> 결과정보
     */
    public Map<String,String> updateEventAlertConfig(Map<String,String> conditionMap);
    
    // INSERT START SP-193
    public Map<String,String> updateAllThreshold(List<Map<String,String>> conditionMapList);
    
    public List<Threshold> getAllThreshold();
    
    // INSERT END SP-193    
}
