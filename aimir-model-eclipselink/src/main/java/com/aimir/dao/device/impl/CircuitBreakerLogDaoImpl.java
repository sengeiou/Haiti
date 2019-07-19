package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.CircuitBreakerLogDao;
import com.aimir.model.device.CircuitBreakerLog;
import com.aimir.util.Condition;

@Repository(value = "circuitbreakerlogDao")
public class CircuitBreakerLogDaoImpl extends AbstractJpaDao<CircuitBreakerLog, Long> implements CircuitBreakerLogDao {
    
	public CircuitBreakerLogDaoImpl() {
		super(CircuitBreakerLog.class);
	}

    @Override
    public List<CircuitBreakerLog> getCircuitBreakerLogGridData(
            Map<String, String> paramMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long getCircuitBreakerLogGridDataCount(Map<String, String> paramMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, String>> getCircuitBreakerLogChartData(
            Map<String, String> paramMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<CircuitBreakerLog> getPersistentClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getSumFieldByCondition(Set<Condition> conditions,
            String field, String... groupBy) {
        // TODO Auto-generated method stub
        return null;
    }
}	
