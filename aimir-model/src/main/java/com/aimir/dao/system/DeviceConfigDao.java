package com.aimir.dao.system;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.DeviceConfig;

public interface DeviceConfigDao extends GenericDao<DeviceConfig, Integer>{
	
	/**
     * method name : getDeviceConfigByModelId
     * method Desc : DeviceModel의 아이디와 일치하는 DeviceConfig 정보를 리턴한다.
     * 
	 * @param deviceModelId Deviceconfig.deviceModel.id
	 * @return @see com.aimir.model.system.DeviceConfig
	 */
	public DeviceConfig getDeviceConfigByModelId(Integer deviceModelId);
}
