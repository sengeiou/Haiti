package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.DeviceDao;
import com.aimir.model.device.Device;
import com.aimir.model.device.Device.DeviceType;
import com.aimir.util.Condition;

@Repository(value = "deviceDao")
public class DeviceDaoImpl extends AbstractJpaDao<Device, Long> implements DeviceDao {

	public DeviceDaoImpl() {
		super(Device.class);
	}

    @Override
    public List<Device> getDevicesByDeviceType(DeviceType deviceType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<Device> getPersistentClass() {
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
