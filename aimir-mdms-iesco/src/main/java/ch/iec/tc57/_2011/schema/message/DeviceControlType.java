package ch.iec.tc57._2011.schema.message;

public enum DeviceControlType {

	REMOTE_DEMAND_RESET		("3.8.0.214",	"REMOTE DEMAND RESET");
	
	private DeviceEventType deviceEventType;
	private String deviceControlCode;
	private String deviceControlName;
	
	DeviceControlType(String controlCode, String controlName) {
		 this.deviceControlCode = controlCode;
		 this.deviceControlName = controlName;
	}

	public String getDeviceControlCode() {
		return deviceControlCode;
	}
	
	public String getDeviceControlName() {
		return deviceControlName;
	}
	
	public DeviceEventType getDeviceEventType() {
		return deviceEventType;
	}
	
	public static DeviceControlType getControlByControlCode(String str) {
		DeviceControlType val = null;
	
		for(DeviceControlType re : values()) {
			if(re.getDeviceControlCode().equals(str)) {
				val = re;
			}
		}
		
		return val;
	}
}
