package com.aimir.service.device;

import java.util.List;

import com.aimir.model.device.Device;
import com.aimir.model.device.MCU;
import com.aimir.model.device.MCUInstallImg;

public interface MCUInstallImgManager {

	public List<Device> getDevicesByDeviceType(String[] array);

	public void addMCUInstallImg(MCU mcu, String currentTimeMillisName, String orginalName);

	public void updateMCUInstallImg(long mcuInstallImgId, String currentTimeMillisName, String orginalName);

	public MCUInstallImg getMCUInstallImg(long mcuInstallImgId);

	public int deleteMCUInstallImg(long mcuInstallImgId);	
}
