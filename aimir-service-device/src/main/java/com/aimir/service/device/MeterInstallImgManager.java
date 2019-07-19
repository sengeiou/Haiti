package com.aimir.service.device;

import java.util.List;
import java.util.Map;

public interface MeterInstallImgManager {

	public List<Object>    getMeterInstallImgList(Integer meterId);

	public void insertMeterInstallImg(Map<String, Object> condition);
	public String deleteMeterInstallImg(Integer meterInstallImgId);
	
	public List<Object> deleteMeterInstallAllImg(Integer meterId);
		
}
