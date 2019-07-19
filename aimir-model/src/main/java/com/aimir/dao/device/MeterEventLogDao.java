package com.aimir.dao.device;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.MeterEventLog;

public interface MeterEventLogDao extends GenericDao<MeterEventLog, Long> {

    /**
     * 미터 이벤트 로그 - 미니가젯 차트 데이터 조회
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeterEventLogMiniChartData(Map<String, Object> conditionMap);
    
    /**
     * 미터 이벤트 로그 - 미니가젯 Profile 데이터 조회
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeterEventLogProfileData(Map<String, Object> conditionMap);

    /**
     * 미터 이벤트 로그 - 맥스가젯 차트 데이터 조회
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeterEventLogMaxChartData(Map<String, Object> conditionMap);

    /**
     * 미터 이벤트 로그 - 맥스가젯 이벤트별 미터기 데이터 조회
     *
     * @param conditionMap
     * @param isTotal
     * @return
     */
    public List<Map<String, Object>> getMeterEventLogMeterByEventGridData(Map<String, Object> conditionMap, boolean isTotal);

    /**
     * 미터 이벤트 로그 - 맥스가젯 미터기별 이벤트 데이터 조회
     *
     * @param conditionMap
     * @param isTotal
     * @return
     */
    public List<Map<String, Object>> getMeterEventLogEventByMeterGridData(Map<String, Object> conditionMap, boolean isTotal);

    /**
     * method name : getMeterEventLogNotIntegratedData
     * method Desc :
     *
     * @param useInsert insert 에서 사용여부(true : SGDG_XAM3 테이블 insert 에서 사용, false : MeterEventLog 테이블 update 에서 사용) 
     * @return
     */
    public List<Object> getMeterEventLogNotIntegratedData(boolean useInsert);

    /**
     * method name : batchUpdateMeterEventLogIntegrated
     * method Desc : integrated 값을 true 로 업데이트한다.
     *
     * @param meterEventLogList
     */
    public void batchUpdateMeterEventLogIntegrated(List<MeterEventLog> meterEventLogList);
    
    /**
     * 주어진 Activator_ID, MeterEvent_ID에 해당하는 로그 조회
     * @param condition
     * @param eId
     * @return
     */
    public List<MeterEventLog> getEventLogListByActivator(Map<String,Object> condition, String eId);

    /**
     * 주어진 날짜 이전의 가장 마지막 로그 조회
     * @param condition : Activator_ID, LimitDate
     * @param eId : MeterEventId
     * @return
     */
    public List<Object> getLastEventLogByEventId(Map<String,Object> condition, String[] eId);
    
    /**
     * 주어진 mds_id로 이벤트 로그 조회
     */
    public List<Object> getEventLogByMds_id(String mdsId);
}