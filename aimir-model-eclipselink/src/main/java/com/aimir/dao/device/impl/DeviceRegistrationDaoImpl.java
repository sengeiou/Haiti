package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.DeviceRegistrationDao;
import com.aimir.model.device.DeviceRegLog;
import com.aimir.model.system.DeviceVendor;
import com.aimir.util.Condition;

@Repository(value = "deviceregistrationDao")
public class DeviceRegistrationDaoImpl extends AbstractJpaDao<DeviceRegLog, Integer> implements DeviceRegistrationDao {

    Log logger = LogFactory.getLog(DeviceRegistrationDaoImpl.class);
    
	public DeviceRegistrationDaoImpl() {
		super(DeviceRegLog.class);
	}

    @Override
    public List<Object> getMiniChart(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<DeviceVendor> getVendorListBySubDeviceType(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getDeviceRegLog(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
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

	@Override
	public List<Object> getShipmentImportHistory(Map<String, Object> condition, boolean isTotal) {
		// TODO Auto-generated method stub
		return null;
	}
}

