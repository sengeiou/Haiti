package com.aimir.service.system.impl;

import java.util.List;

import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.system.DeviceVendorDao;
import com.aimir.model.system.DeviceVendor;
import com.aimir.service.system.DeviceVendorManager;


@WebService(endpointInterface = "com.aimir.service.system.DeviceVendorManager")
@Service(value = "deviceVendorManager")
@Transactional
public class DeviceVendorManagerImpl implements DeviceVendorManager {
	
	@Autowired
	DeviceVendorDao dao;
	
	public DeviceVendor getDeviceVendor(int vendorId) {
		return dao.get(vendorId);
	}

	public List<DeviceVendor> getDeviceVendorsBySupplierId(int supplierId) {
		return dao.getDeviceVendorsOrderByName();
	}

	public DeviceVendor addDeviceVendor(DeviceVendor deviceVendor) {
		return dao.add(deviceVendor);
	}
	
	public DeviceVendor updateDeviceVendor(DeviceVendor deviceVendor) {
		return dao.update(deviceVendor);
	}
	
	public int deleteDeviceVendor(int deviceVendorId) {
		return dao.deleteById(deviceVendorId);
	}

	public List<Object[]> getDeviceVendorsForTree(int supplierId) {
		return dao.getDeviceVendorsForTree(supplierId);
	}

	public List<DeviceVendor> getDeviceVendorByName(Integer supplierId,	String name) {
		return dao.getDeviceVendorByName(supplierId, name);
	}

	public void deleteDeviceVendor(DeviceVendor deviceVendor) {
		dao.delete(deviceVendor);
	}

	public List<DeviceVendor> getDeviceVendorByCode(Integer supplierId,	Integer code) {
		return dao.getDeviceVendorByCode(supplierId, code);
	}

}
