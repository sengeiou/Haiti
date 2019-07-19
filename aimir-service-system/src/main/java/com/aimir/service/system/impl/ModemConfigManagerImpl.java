package com.aimir.service.system.impl;

import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.system.ModemConfigDao;
import com.aimir.model.system.ModemConfig;
import com.aimir.service.system.ModemConfigManager;

@WebService(endpointInterface = "com.aimir.service.system.ModemConfigManager")
@Service(value = "modemConfigManager")
@Transactional
public class ModemConfigManagerImpl implements ModemConfigManager{

	@Autowired
	ModemConfigDao dao;

	public ModemConfig getModemConfig(Integer modemConfigId) {
		return dao.get(modemConfigId);
	}
	
	
	public ModemConfig updateModemConfig(ModemConfig modemConfig) {		
		return dao.update(modemConfig);
	}
	
	public void deleteModemConfig(ModemConfig modemConfig) {		
		dao.delete(modemConfig);
	}

	public ModemConfig addModemConfig(ModemConfig modemConfig) {
		return dao.add(modemConfig);
	}

//	public ModemConfig getDeviceConfigs(Integer configId) {
//		return dao.getDeviceConfig(configId);
//	}

}
