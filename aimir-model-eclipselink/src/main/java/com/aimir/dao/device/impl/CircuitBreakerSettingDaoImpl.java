package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.CircuitBreakerSettingDao;
import com.aimir.model.device.CircuitBreakerSetting;
import com.aimir.util.Condition;


@Repository(value = "circuitbreakersettingDao")
public class CircuitBreakerSettingDaoImpl extends AbstractJpaDao<CircuitBreakerSetting, Integer> implements CircuitBreakerSettingDao {
	public CircuitBreakerSettingDaoImpl() {
		super(CircuitBreakerSetting.class);
	}

    @Override
    public Class<CircuitBreakerSetting> getPersistentClass() {
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
