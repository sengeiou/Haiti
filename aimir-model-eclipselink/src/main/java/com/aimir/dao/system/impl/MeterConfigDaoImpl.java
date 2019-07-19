package com.aimir.dao.system.impl;


import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.MeterConfigDao;
import com.aimir.model.system.MeterConfig;
import com.aimir.util.Condition;

@Repository(value="meterconfigDao")
public class MeterConfigDaoImpl extends AbstractJpaDao<MeterConfig, Integer> implements MeterConfigDao{

	public MeterConfigDaoImpl() {
		super(MeterConfig.class);
	}

    @Override
    public MeterConfig getDeviceConfig(Integer configId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getMeterConfigId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<MeterConfig> getPersistentClass() {
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
