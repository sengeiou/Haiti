package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.MCUCodiDeviceDao;
import com.aimir.model.device.MCUCodiDevice;
import com.aimir.util.Condition;

@Repository(value = "mcucodideviceDao")
public class MCUCodiDeviceDaoImpl extends AbstractJpaDao<MCUCodiDevice, Long> implements MCUCodiDeviceDao {

	public MCUCodiDeviceDaoImpl() {
		super(MCUCodiDevice.class);
	}

    @Override
    public Class<MCUCodiDevice> getPersistentClass() {
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
