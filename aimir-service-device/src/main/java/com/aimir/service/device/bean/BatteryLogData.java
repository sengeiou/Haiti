package com.aimir.service.device.bean;

public class BatteryLogData {
	private String modemId;
	private String batteryStatus;
	private String modemType;
	private String modem;
	private String powerType;
	private String meterLocation;
	private String batteryVolt;
	private Integer operatingDay;
	private String checkTime;
	private Integer activeTime;
	private Integer resetCount;
	
	public String getModemId() {
		return modemId;
	}
	public void setModemId(String modemId) {
		this.modemId = modemId;
	}
	public String getBatteryStatus() {
		return batteryStatus;
	}
	public void setBatteryStatus(String batteryStatus) {
		this.batteryStatus = batteryStatus;
	}
	public String getModemType() {
		return modemType;
	}
	public void setModemType(String modemType) {
		this.modemType = modemType;
	}
	public String getModem() {
		return modem;
	}
	public void setModem(String modem) {
		this.modem = modem;
	}
	public String getPowerType() {
		return powerType;
	}
	public void setPowerType(String powerType) {
		this.powerType = powerType;
	}
	public String getMeterLocation() {
		return meterLocation;
	}
	public void setMeterLocation(String meterLocation) {
		this.meterLocation = meterLocation;
	}
	public String getBatteryVolt() {
		return batteryVolt;
	}
	public void setBatteryVolt(String batteryVolt) {
		this.batteryVolt = batteryVolt;
	}
	public Integer getOperatingDay() {
		return operatingDay;
	}
	public void setOperatingDay(Integer operatingDay) {
		this.operatingDay = operatingDay;
	}
	public String getCheckTime() {
		return checkTime;
	}
	public void setCheckTime(String checkTime) {		
		this.checkTime = checkTime;
	}
	public Integer getActiveTime() {
		return activeTime;
	}
	public void setActiveTime(Integer activeTime) {
		this.activeTime = activeTime;
	}
	public Integer getResetCount() {
		return resetCount;
	}
	public void setResetCount(Integer resetCount) {
		this.resetCount = resetCount;
	}
	
}
