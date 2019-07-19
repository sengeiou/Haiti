package com.aimir.dao.device;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.MeterEvent;

public interface MeterEventDao extends GenericDao<MeterEvent, Long> {
    
    /**
     * 미터 이벤트 로그 - 이벤트 이름 조회
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getEventNames();

    /**
     * 미터 이벤트 로그 - 이벤트 이름 리스트로 이벤트 아이디 리스트 조회
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getEventIdsByNames(String[] meterEventNames);
    
    /**
     * 미터 이벤트를 검색해 온다.
     * 
     * @param conditionMap
     * @return
     */
    public MeterEvent getMeterEventByCondition(Map<String,Object> conditionMap);

    /**
     * 미터 이벤트 로그 - 이벤트 이름, 모델명으로 아이디 조회
     * @param meterEventNames
     * @param modelName (ex. LSIQ-1P, LSIQ-3PCV,...)
     * @return MeterEvent.Id List
     */
    public List<Object> getEventIdsByNames2(String meterEventNames, String modelName);
}
