package com.aimir.dao.device;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.Meter;

public interface MeterTimeDao extends GenericDao<Meter, Integer> {
 	
    // TimeDiff 
    public List<Object> getMeterTimeTimeDiffChart(Map<String, Object> condition);
    public List<Object> getMeterTimeTimeDiffComplianceChart(Map<String, Object> condition);
    public List<Object> getMeterTimeTimeDiffGrid(Map<String, Object> condition);
    
    // SyncLog
    public List<Object> getMeterTimeSyncLogChart(Map<String, Object> condition);
    public List<Object> getMeterTimeSyncLogAutoChart(Map<String, Object> condition);
    public List<Object> getMeterTimeSyncLogManualChart(Map<String, Object> condition);
    public List<Object> getMeterTimeSyncLogGrid(Map<String, Object> condition);
    public List<Object> getMeterTimeThresholdGrid(Map<String, Object> condition);
    
}
