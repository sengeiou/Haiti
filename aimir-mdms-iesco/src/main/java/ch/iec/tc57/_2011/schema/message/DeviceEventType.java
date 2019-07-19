package ch.iec.tc57._2011.schema.message;

import java.util.ArrayList;
import java.util.List;

public enum DeviceEventType {
	
	REMOTE_DEMAND_RESET_OK		("3.8.0.215",	DeviceControlType.REMOTE_DEMAND_RESET, "REMOTE DEMAND RESET OK"),
	REMOTE_DEMAND_RESET_FAIL	("3.8.0.65",	DeviceControlType.REMOTE_DEMAND_RESET, "REMOTE DEMAND RESET FAIL");
	
	private DeviceControlType deviceControlType;
	private String deviceEventCode;
	private String deviceEventName;
	
	DeviceEventType(String deviceEventCode, DeviceControlType deviceControlType, String deviceEventName) {
		
		this.deviceControlType = deviceControlType;
		this.deviceEventCode = deviceEventCode;
		this.deviceEventName = deviceEventName;
	}

	public DeviceControlType getDeviceControlType() {
		return deviceControlType;
	}

	public String getDeviceEventCode() {
		return deviceEventCode;
	}
	
	public String getDeviceEventName() {
		return deviceEventName;
	}

	public static DeviceEventType getEventByEventCode(String str){
		DeviceEventType val = null ;
		
		for(DeviceEventType re :  values()) {
			if(re.getDeviceEventCode().equals(str)) {
				val = re;
			}
		}
		
		return val;
	}
}