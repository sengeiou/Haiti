package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.HeatMeterDao;
import com.aimir.model.device.HeatMeter;
import com.aimir.util.Condition;

@Repository(value = "heatmeterDao")
public class HeatMeterDaoImpl extends AbstractJpaDao<HeatMeter, Integer> implements HeatMeterDao {

    Log logger = LogFactory.getLog(HeatMeterDaoImpl.class);
    
    public HeatMeterDaoImpl() {
		super(HeatMeter.class);
	}

    @Override
    public Class<HeatMeter> getPersistentClass() {
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