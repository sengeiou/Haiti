package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.MeterTimeDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.device.Meter;
import com.aimir.util.Condition;

@Repository(value = "meterTimeDao")
public class MeterTimeDaoImpl extends AbstractJpaDao<Meter, Integer> implements MeterTimeDao {

	Log logger = LogFactory.getLog(MeterTimeDaoImpl.class);

	@Autowired
	SupplierDao supplierDao;
	
	public MeterTimeDaoImpl() {
		super(Meter.class);
	}

    @Override
    public Class<Meter> getPersistentClass() {
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
    public List<Object> getMeterTimeTimeDiffChart(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMeterTimeTimeDiffComplianceChart(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMeterTimeTimeDiffGrid(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMeterTimeSyncLogChart(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMeterTimeSyncLogAutoChart(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMeterTimeSyncLogManualChart(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMeterTimeSyncLogGrid(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMeterTimeThresholdGrid(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }
}


