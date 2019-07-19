package com.aimir.dao.mvm;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.mvm.MeteringDataNM;

/**
 * SP-929
 * Store Mbus Slave I/O Module data for Net-station Monitoring
 */
public interface MeteringDataNMDao extends GenericDao<MeteringDataNM, Integer>{

    /**
     * @param conditionMap
     * @param isTotal
     * @return
     */
    public List<Map<String, Object>> getMeteringData(Map<String, Object> conditionMap, boolean isTotal);

    
    /**
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeteringDataDetail(Map<String, Object> conditionMap) ;

    
    /**
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeteringSuccessCountListPerLocation(Map<String, Object> conditionMap);
}
