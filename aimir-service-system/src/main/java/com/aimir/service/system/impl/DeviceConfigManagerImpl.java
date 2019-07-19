package com.aimir.service.system.impl;

import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.system.DeviceConfigDao;
import com.aimir.model.system.DeviceConfig;
import com.aimir.service.system.DeviceConfigManager;

@WebService(endpointInterface = "com.aimir.service.system.DeviceConfigManager")
@Service(value = "deviceConfigManager")
@Transactional
public class DeviceConfigManagerImpl implements DeviceConfigManager{

	@Autowired
	DeviceConfigDao dao;

	public DeviceConfig getDeviceConfig(Integer deviceConfigId) {
		return dao.get(deviceConfigId);
	}
	
	public DeviceConfig addDeviceConfig(DeviceConfig deviceConfig) {
		return dao.add(deviceConfig);
	}

	public DeviceConfig updateDeviceConfig(DeviceConfig deviceConfig) {
		return dao.update(deviceConfig);
	}
	
	public void deleteDeviceConfig(DeviceConfig deviceConfig) {
		
		dao.delete(deviceConfig);
	}

	public DeviceConfig getDeviceConfigByModelId(Integer deviceModelId) {
		return dao.getDeviceConfigByModelId(deviceModelId);
	}

}
