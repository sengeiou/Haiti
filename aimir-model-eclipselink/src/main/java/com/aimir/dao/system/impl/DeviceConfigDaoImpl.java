package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.DeviceConfigDao;
import com.aimir.model.system.DeviceConfig;
import com.aimir.util.Condition;


@Repository(value = "deviceconfigDao")
public class DeviceConfigDaoImpl extends
		AbstractJpaDao<DeviceConfig, Integer> implements
		DeviceConfigDao {

	public DeviceConfigDaoImpl() {
		super(DeviceConfig.class);
	}

    @Override
    public DeviceConfig getDeviceConfigByModelId(Integer deviceModelId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<DeviceConfig> getPersistentClass() {
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
