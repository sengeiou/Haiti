package com.aimir.dao.device;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.MeterAttr;

/**
 * SP-898
 * @author
 *
 */
public interface MeterAttrDao extends GenericDao<MeterAttr, Long> {
    public MeterAttr getByMeterId(Integer meter_id);
    public MeterAttr getByMdsId(String mdsId);
    /**
     * SP-987
     * @param conditionMap
     * @param isTotal
     * @return
     */
    public List<Object> getMetersToClearAlarmObject(Map<String, Object> conditionMap, boolean isTotal);
    
    /**
     * SP-987
     * @param conditionMap
     * @return
     */
    public List<Map<String,Object>> getMucsToClearAlarmObject(Map<String, Object> conditionMap);
}
