package com.aimir.service.device.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.device.DeviceDao;
import com.aimir.dao.device.MCUInstallImgDao;
import com.aimir.model.device.Device;
import com.aimir.model.device.MCU;
import com.aimir.model.device.MCUInstallImg;
import com.aimir.model.device.Device.DeviceType;
import com.aimir.service.device.MCUInstallImgManager;

@Service(value = "mcuInstallImgManager")
@Transactional(readOnly=false)
public class MCUInstallImgManagerImpl implements MCUInstallImgManager {

    @Autowired
    DeviceDao deviceDao;
    
    @Autowired
    MCUInstallImgDao mcuInstallImgDao;
    
	public List<Device> getDevicesByDeviceType(String[] array) {
		
		List<Device> devices = deviceDao.getDevicesByDeviceType(DeviceType.Meter);
		
		return devices;
	}

	public void addMCUInstallImg(MCU mcu, String currentTimeMillisName, String orginalName) {
		
		MCUInstallImg mcuInstallImg = new MCUInstallImg();
		mcuInstallImg.setCurrentTimeMillisName(currentTimeMillisName);
		mcuInstallImg.setOrginalName(orginalName);
		mcuInstallImg.setMcuId(mcu);
		
		mcuInstallImgDao.add(mcuInstallImg);
	}

	public void updateMCUInstallImg(long mcuInstallImgId, String currentTimeMillisName, String orginalName) {
		
		MCUInstallImg mcuInstallImg = mcuInstallImgDao.get(mcuInstallImgId);
		mcuInstallImg.setCurrentTimeMillisName(currentTimeMillisName);
		mcuInstallImg.setOrginalName(orginalName);
		
		mcuInstallImgDao.update(mcuInstallImg);
		
	}

	public MCUInstallImg getMCUInstallImg(long mcuInstallImgId) {

		return mcuInstallImgDao.get(mcuInstallImgId);
		
	}

	public int deleteMCUInstallImg(long mcuInstallImgId) {
		
		return mcuInstallImgDao.deleteById(mcuInstallImgId);
	}
}
