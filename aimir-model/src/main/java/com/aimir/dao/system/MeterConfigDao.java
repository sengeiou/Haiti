package com.aimir.dao.system;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.MeterConfig;

public interface MeterConfigDao extends GenericDao<MeterConfig, Integer>{
	
	/**
     * method name : getDeviceConfig
     * method Desc : DeviceModel의 아이디와 일치하는 MeterConfig 정보를 리턴한다.
     * 
	 * @param deviceModelId MeterConfig.deviceconfig.id
	 * @return @see com.aimir.model.system.MeterConfig
	 */
	MeterConfig getDeviceConfig(Integer configId);
	Integer getMeterConfigId();
}
