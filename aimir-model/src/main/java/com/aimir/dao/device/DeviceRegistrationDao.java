package com.aimir.dao.device;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.DeviceRegLog;
import com.aimir.model.system.DeviceVendor;

public interface DeviceRegistrationDao extends GenericDao<DeviceRegLog, Integer> {
	
	// Mini Gadget
	public List<Object> getMiniChart(Map<String, Object> condition);

	// Max Gadget
	public List<DeviceVendor> getVendorListBySubDeviceType(Map<String, Object> condition);

	// Max Gadget
	public List<Object> getDeviceRegLog(Map<String, Object> condition);
	
	public List<Object> getShipmentImportHistory(Map<String, Object> condition, boolean isTotal);
    
}
