package com.aimir.service.device;

import java.util.List;
import java.util.Map;

import com.aimir.model.device.MeterEvent;

public interface MeterEventLogManager {

	/**
	 * 미터 이벤트 로그 - 미니가젯 차트 데이터 조회
	 * 
	 * @param conditionMap
	 * @return
	 */
    public List<Map<String, Object>> getMeterEventLogMiniChartData(Map<String, Object> conditionMap);

    /**
     * 미터 이벤트 로그 - 미니가젯 프로파일 데이터 조회
     * 
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeterEventLogProfileData(Map<String, Object> conditionMap);

    /**
     * 미터 이벤트 로그 - 미니가젯 프로파일 데이터 저장
     * 
     * @param conditionMap
     * @return
     */
    public void updateMeterEventLogProfileData(Map<String, Object> conditionMap);

    /**
     * Event Name 리스트를 조회한다.
     * 
     * @return
     */
    public List<Map<String, Object>> getEventNames();
    
    /**
     * Meter Event를 저장한다.
     * 
     * @param meterEvent
     */
    public void add(MeterEvent meterEvent) throws Exception;
    
    public void update(MeterEvent meterEvent) throws Exception;
    
    public void delete(MeterEvent meterEvent) throws Exception;

    /**
     * 미터 이벤트 로그 - 맥스가젯 차트 데이터 조회
     * 
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeterEventLogMaxChartData(Map<String, Object> conditionMap);

    /**
     * 미터 이벤트 로그 - 맥스가젯 이벤트별 미터기 데이터 Count 조회
     * 
     * @param conditionMap
     * @return
     */
    public Integer getMeterEventLogMeterByEventGridDataCount(Map<String, Object> conditionMap);
    
    /**
     * 미터 이벤트 로그 - 맥스가젯 이벤트별 미터기 데이터 조회
     * 
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeterEventLogMeterByEventGridData(Map<String, Object> conditionMap);
    
    /**
     * 미터 이벤트 로그 - 맥스가젯 미터기별 이벤트 데이터 Count 조회
     * 
     * @param conditionMap
     * @return
     */
    public Integer getMeterEventLogEventByMeterGridDataCount(Map<String, Object> conditionMap);
    
    /**
     * 미터 이벤트 로그 - 맥스가젯 미터기별 이벤트 데이터 조회
     * 
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeterEventLogEventByMeterGridData(Map<String, Object> conditionMap);
    
    /**
     * 미터 이벤트를 검색해 온다.
     * 
     * @param conditionMap
     * @return
     */
    public MeterEvent getMeterEventByCondition(Map<String,Object> conditionMap);
}
