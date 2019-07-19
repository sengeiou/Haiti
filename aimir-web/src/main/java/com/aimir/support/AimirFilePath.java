package com.aimir.support;

import org.springframework.stereotype.Component;

@Component("aimirFilePath")
public class AimirFilePath {

	private String photoBasePath;
	private String photoTempPath;
	private String uiPath;
	
	private String devicePath;		// Device Vendor Model 경로
	private String mcuPath;			// mcu 경로
	private String modemPath;		// modem 경로
	private String meterPath;		// meter 경로
	
	private String defaultPath;		// 기본경로 경로
	private String homeDevicePath;  // HomeDevice 경로
	
	public String getDefaultPath() {
		return defaultPath;
	}
	public void setDefaultPath(String defaultPath) {
		this.defaultPath = defaultPath;
	}
	public String getPhotoBasePath() {
		return photoBasePath;
	}
	public String getDevicePath() {
		return devicePath;
	}
	public void setDevicePath(String devicePath) {
		this.devicePath = devicePath;
	}
	public String getMcuPath() {
		return mcuPath;
	}
	public void setMcuPath(String mcuPath) {
		this.mcuPath = mcuPath;
	}
	public String getModemPath() {
		return modemPath;
	}
	public void setModemPath(String modemPath) {
		this.modemPath = modemPath;
	}
	public String getMeterPath() {
		return meterPath;
	}
	public void setMeterPath(String meterPath) {
		this.meterPath = meterPath;
	}
	public void setPhotoBasePath(String photoBasePath) {
		this.photoBasePath = photoBasePath;
	}
	
	public String getPhotoTempPath() {
		return photoTempPath;
	}
	public void setPhotoTempPath(String photoTempPath) {
		this.photoTempPath = photoTempPath;
	}

	public String getHomeDevicePath() {
		return homeDevicePath;
	}
	public void setHomeDevicePath(String homeDevicePath) {
		this.homeDevicePath = homeDevicePath;
	}

	public String getUiPath() {
		return uiPath;
	}
	public void setUiPath(String uiPath) {
		this.uiPath = uiPath;
	}
	
}
