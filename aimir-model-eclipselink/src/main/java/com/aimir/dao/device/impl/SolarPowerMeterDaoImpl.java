package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.SolarPowerMeterDao;
import com.aimir.model.device.SolarPowerMeter;
import com.aimir.util.Condition;

@Repository(value = "SolarPowerMeterDao")
public class SolarPowerMeterDaoImpl extends AbstractJpaDao<SolarPowerMeter, Integer> implements SolarPowerMeterDao {

    Log logger = LogFactory.getLog(SolarPowerMeterDaoImpl.class);
    
    public SolarPowerMeterDaoImpl() {
		super(SolarPowerMeter.class);
	}
	
	@Override
	public long totalByConditions(Set<Condition> condition) {
		List<Object> ret = findTotalCountByConditions(condition);
		return (Long) ret.get(0);
	}

    @Override
    public Class<SolarPowerMeter> getPersistentClass() {
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
