package com.aimir.fep.protocol.nip.command;

import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.google.gson.annotations.SerializedName;

public class AlarmEventCmd {
	public int alarmEventTypeId;
	
	 public enum Statue {
		@SerializedName("Off") Off((byte)0x00),
		@SerializedName("On") On((byte)0x01),
		@SerializedName("ReadWriteFailed") ReadWriteFailed((byte)0x02);
        private byte code;
        Statue(byte code) {
            this.code = code;
        }
        public byte getCode() {
            return this.code;
        }
    }
	
	public Statue _statue;
	
    public void setStatue(byte code) {
        for (Statue c : Statue.values()) {
            if (c.getCode() == code) {
            	_statue = c;
                break;
            }
        }
    }
    
    public int getAlarmEventTypeId() {
		return alarmEventTypeId;
	}

	public void setAlarmEventTypeId(int alarmEventTypeId) {
		this.alarmEventTypeId = alarmEventTypeId;
	}
	
	public String getAlarmEventType()
	{
		String typeName = "";
		switch (alarmEventTypeId)	{
			case  0x0101: typeName = new String("Power Fail"); break;                          
			case  0x0102: typeName = new String("Power Restore"); break;                       
			case  0x0201: typeName = new String("Case Alarm Open"); break;            
			case  0x0202: typeName = new String("Case Alarm Close"); break;           
			case  0x0301: typeName = new String("Line Missing"); break;                       
			case  0x0401: typeName = new String("Meter Error"); break;                         
			case  0x0501: typeName = new String("Time Synchronization"); break;       
			case  0x0502: typeName = new String("Meter Time Synchronization"); break;  
			case  0x0503: typeName = new String("Meter Upgrade Finished"); break;  
			case  0x1008: typeName = new String("Reset"); break;
			case  0x0701: typeName = new String("Threshold Warning"); break;     
			case  0x0801: typeName = new String("Magnetic Tamper"); break;                 
			case  0x0901: typeName = new String("Equipment Configuration Changed"); break;              
			case  0x0902: typeName = new String("Equipment Installed"); break;        
			case  0x0903: typeName = new String("Equipment Registered"); break;              
			case  0x0904: typeName = new String("Equipment Self-test"); break;        
			case  0x0905: typeName = new String("Equipment Shutdown"); break;        
			case  0x0906: typeName = new String("Equipment Restart"); break;              
			case  0x0907: typeName = new String("Equipment Firmware Update"); break;        
			case  0x0A01: typeName = new String("Battery Health Low Battery"); break;               
			case  0x0A02: typeName = new String("Battery Health Low Battery Restore"); break;               		
			case  0x0B01: typeName = new String("Factory Default Setting"); break;
			case  0x0B02: typeName = new String("Meter No Response"); break;   
			case  0x0C01: typeName = new String("Duplicated Equipment"); break;      
			case  0x0D01: typeName = new String("OTA Download Download"); break;        
			case  0x0D02: typeName = new String("OTA Download Start"); break;         
			case  0x0D03: typeName = new String("OTA Download End"); break;          
			case  0x0D04: typeName = new String("OTA Download Result"); break;	      
			case  0x0D05: typeName = new String("OTA FOTA Result"); break;      
			case  0x0D06: typeName = new String("OTA Module Upgrade Result"); break;		     
			case  0x0E01: typeName = new String("Communication Failure"); break;
			case  0x0E02: typeName = new String("Communication Restore"); break;
			case  0x0F01: typeName = new String("Malfunction Disk Error"); break; 
			case  0x0F02: typeName = new String("Malfunction Disk Restore"); break;
			case  0x0F03: typeName = new String("Malfunction Memory Error"); break; 
			case  0x0F04: typeName = new String("Malfunction Memory Restore"); break;
			case  0x1001: typeName = new String("Meter Value Alarm");break;
			case  0x1101: typeName = new String("Metering Value Incorrect");break;
			case  0x1201: typeName = new String("Battery Charging Start"); break; 
			case  0x1202: typeName = new String("Battery Charging End"); break; 
			case  0x1301: typeName = new String("Security Alarm	Metering Fail(HLS)"); break; 
			case  0x1302: typeName = new String("Security Alarm	Communication Fail(TLS/DTLS)"); break; 

// Old Version 		
//			case  0x0101: typeName = new String("Power Fail"); break;                          
//			case  0x0102: typeName = new String("Power Restore"); break;                       
//			case  0x0201: typeName = new String("Case Alarm Open"); break;            
//			case  0x0202: typeName = new String("Case Alarm Close"); break;           
//			case  0x0301: typeName = new String("Line Missing"); break;                        
//			case  0x0401: typeName = new String("Meter Error"); break;                         
//			case  0x0501: typeName = new String("Threshold Warning"); break;                   
//			case  0x0601: typeName = new String("Magnetic Tamper"); break;                     
//			case  0x0701: typeName = new String("Equipment Configuration Changed"); break;     
//			case  0x0702: typeName = new String("Equipment Installed"); break;                 
//			case  0x0703: typeName = new String("Equipment Registered"); break;                
//			case  0x0704: typeName = new String("Equipment Firmware Update"); break;           
//			case  0x0705: typeName = new String("Equipment Time synced"); break;               
//			case  0x0801: typeName = new String("Battery Health Low Battery"); break;          
//			case  0x0802: typeName = new String("Battery Health Low Battery Restore"); break;  
//			case  0x0901: typeName = new String("OTA Download Download"); break;              
//			case  0x0902: typeName = new String("OTA Download Result"); break;        
//			case  0x0A01: typeName = new String("Communication Failure"); break;               
//			case  0x0A02: typeName = new String("Communication Restore"); break;               
//			case  0x0B01: typeName = new String("Meter Value Alarm"); break;                   
//			case  0x0B02: typeName = new String("Meter No Response"); break;                   
//			case  0x0C01: typeName = new String("Battery Charging Fully Charged"); break;      
//			case  0x0D01: typeName = new String("Voltage Alarm Boundary Value"); break;        
//			case  0x0D02: typeName = new String("Voltage Alarm Under voltage"); break;         
//			case  0x0D03: typeName = new String("Voltage Alarm Over voltage"); break;          
//			case  0x0D04: typeName = new String("Voltage Alarm Power Outage"); break;          
//			case  0x0D05: typeName = new String("Voltage Alarm Power re-established"); break;  
//			case  0x0D06: typeName = new String("Voltage Alarm Voltage Unbalance"); break;     
//			case  0x0E01: typeName = new String("Energy Level"); break;                       
//			case  0x0F01: typeName = new String("Security Alarm	Metering Fail(HLS)"); break; 
//			case  0x0F02: typeName = new String("Communication Fail(TLS/DTLS)"); break;
//			default :
//				typeName = "0x" + Hex.decode(DataUtil.get2ByteToInt(alarmEventTypeId));
		}
		return typeName;
	}
	
	
	/* SP-701
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		String retValue;
		String statusStr = _statue == null ? "" :  _statue.name();
	    retValue = "["
	    		+ "typeId:0x" + Hex.decode(DataUtil.get2ByteToInt(alarmEventTypeId))
		        + ",typeName:" + getAlarmEventType() 
		        + ",status:" + statusStr
		        + "]";
	    return retValue;
	}
	
	String toJSONString()
	{
		String retValue;
		String statusStr = _statue == null ? "" :  _statue.name();
	    retValue = "{"
	    		+ "\"typeId\":\"" + "0x" + Hex.decode(DataUtil.get2ByteToInt(alarmEventTypeId)) + "\","
		        + "\"typeName\":\"" + getAlarmEventType() 
		        + "\",\"status\":\"" + statusStr
		        + "\"}";
	    return retValue;
	}
}