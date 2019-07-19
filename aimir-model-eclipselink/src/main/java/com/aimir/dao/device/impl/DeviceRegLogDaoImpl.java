package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.DeviceRegLogDao;
import com.aimir.model.device.DeviceRegLog;
import com.aimir.util.Condition;

@Repository(value = "devicereglogDao")
public class DeviceRegLogDaoImpl extends AbstractJpaDao<DeviceRegLog, Long> implements DeviceRegLogDao {

	public DeviceRegLogDaoImpl() {
		super(DeviceRegLog.class);
	}

    @Override
    public Class<DeviceRegLog> getPersistentClass() {
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
