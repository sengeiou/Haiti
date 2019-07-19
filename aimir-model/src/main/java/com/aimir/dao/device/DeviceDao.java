package com.aimir.dao.device;

import java.util.List;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.Device;
import com.aimir.model.device.Device.DeviceType;

public interface DeviceDao extends GenericDao<Device, Long> {
	
	public List<Device> getDevicesByDeviceType(DeviceType deviceType);

}
