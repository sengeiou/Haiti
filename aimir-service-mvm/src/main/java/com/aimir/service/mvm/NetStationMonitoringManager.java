package com.aimir.service.mvm;

import java.util.List;
import java.util.Map;

import com.aimir.service.mvm.bean.MeteringFailureData;

public interface NetStationMonitoringManager {
    
    /**
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeteringDataHourlyData(Map<String, Object> conditionMap);
    

    /**
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeteringDataDetail(Map<String, Object> conditionMap);
    

    /**
     * @param conditionMap
     * @return
     */
    public Integer getMeteringDataHourlyDataTotalCount(Map<String, Object> conditionMap) ;
    

    /**
     * @param conditionMap
     * @return
     */
    public Map<String, Object> getMeteringDataDetailChartData(Map<String, Object> conditionMap) ;

    /**
     * @param params
     * @return
     */
    public List<MeteringFailureData> getMbusSlaveIoModuleCountListPerLocation(Map<String,Object> params);

    /**
     * @param conditionMap
     * @return
     */
    public List<MeteringFailureData> getMeteringSuccessRateListWithChildren(Map<String, Object> conditionMap);
}