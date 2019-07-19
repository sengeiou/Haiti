package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.MeterEventLogDao;
import com.aimir.model.device.MeterEventLog;
import com.aimir.util.Condition;

@Repository(value = "metereventlogDao")
public class MeterEventLogDaoImpl extends AbstractJpaDao<MeterEventLog, Long> implements MeterEventLogDao {

	Log log = LogFactory.getLog(MeterEventLogDaoImpl.class);
	
	public MeterEventLogDaoImpl() {
		super(MeterEventLog.class);
	}

    @Override
    public Class<MeterEventLog> getPersistentClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getSumFieldByCondition(Set<Condition> conditions,
            String field, String... groupBy) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getMeterEventLogMiniChartData(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getMeterEventLogProfileData(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getMeterEventLogMaxChartData(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getMeterEventLogMeterByEventGridData(
            Map<String, Object> conditionMap, boolean isTotal) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getMeterEventLogEventByMeterGridData(
            Map<String, Object> conditionMap, boolean isTotal) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMeterEventLogNotIntegratedData(boolean useInsert) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void batchUpdateMeterEventLogIntegrated(
            List<MeterEventLog> meterEventLogList) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<MeterEventLog> getEventLogListByActivator(
            Map<String, Object> condition, String eId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getLastEventLogByEventId(Map<String, Object> condition,
            String[] eId) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public List<Object> getEventLogByMds_id(String mdsId){
    	// TODO Auto-generated method stub
        return null;
    }
}