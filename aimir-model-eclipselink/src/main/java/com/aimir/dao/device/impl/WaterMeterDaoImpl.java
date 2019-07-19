package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.WaterMeterDao;
import com.aimir.model.device.WaterMeter;
import com.aimir.util.Condition;

@Repository(value = "watermeterDao")
public class WaterMeterDaoImpl extends AbstractJpaDao<WaterMeter, Integer> implements WaterMeterDao {

    Log logger = LogFactory.getLog(WaterMeterDaoImpl.class);
    
    public WaterMeterDaoImpl() {
		super(WaterMeter.class);
	}

    @Override
    public Class<WaterMeter> getPersistentClass() {
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