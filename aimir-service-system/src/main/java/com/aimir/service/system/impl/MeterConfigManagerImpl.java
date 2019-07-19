package com.aimir.service.system.impl;

import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.system.MeterConfigDao;
import com.aimir.model.system.MeterConfig;
import com.aimir.service.system.MeterConfigManager;

@WebService(endpointInterface = "com.aimir.service.system.MeterConfigManager")
@Service(value = "meterConfigManager")
@Transactional
public class MeterConfigManagerImpl implements MeterConfigManager{

	@Autowired
	MeterConfigDao dao;

	public MeterConfig getMeterConfig(Integer meterConfigId) {
		return dao.get(meterConfigId);
	}
	
	
	public MeterConfig updateMeterConfig(MeterConfig meterConfig) {		
		return dao.update(meterConfig);
	}
	
	public void deleteMeterConfig(MeterConfig meterConfig) {		
		dao.delete(meterConfig);
	}

	public MeterConfig addMeterConfig(MeterConfig meterConfig) {
		return dao.add(meterConfig);
	}


	public MeterConfig getDeviceConfigs(Integer configId) {
		return dao.getDeviceConfig(configId);
	}
	
	public Integer getLastId(){
		Integer configId = dao.getMeterConfigId();
		
		return configId+1;
		
	}

}
