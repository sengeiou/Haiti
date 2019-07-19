package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.CommStatusByCommDeviceDao;
import com.aimir.model.device.CommStatusByCommDevice;
import com.aimir.model.device.CommStatusByCommDevicePk;
import com.aimir.util.Condition;

@Repository(value = "commStatusByCommDeviceDao")
public class CommStatusByCommDeviceDaoImpl extends AbstractJpaDao<CommStatusByCommDevice, CommStatusByCommDevicePk> 
					implements CommStatusByCommDeviceDao {
	
	public CommStatusByCommDeviceDaoImpl() {
		super(CommStatusByCommDevice.class);
	}

    @Override
    public Class<CommStatusByCommDevice> getPersistentClass() {
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
