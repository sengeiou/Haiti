package com.aimir.fep.protocol.nip.frame.payload;

import com.aimir.fep.util.DataUtil;

public class AlarmEvent extends PayloadFrame {
    private byte[] empty = new byte[0]; 
    
    public enum AlarmId{
//      [Old Version]
//    	PowerFail (0x0101}),
//    	PowerRestore (0x0102}),
//    	CaseAlarmOpen (0x0201}),
//    	CaseAlarmClose (0x0202}),
//    	LineMissing (0x0301}),
//    	MeterError (0x0401}),
//    	ThresholdWarning (0x0501}),
//    	MagneticTamper (0x0601}),
//    	ConfigurationChanged (0x0701}),
//    	Installed (0x0702}),
//    	Registered (0x0703}),
//    	FirmwareUpdate (0x0704}),
//    	TimeSynced (0x0705}),
//    	BatteryHealthLowBattery (0x0801}),
//    	BatteryHealthLowBatteryRestore (0x0802}),
//    	Download (0x0901}),
//    	Result (0x0902}),
//    	CommunicationFailure (0x0A01}),
//    	CommunicationRestore (0x0A02}),
//    	MeterValueAlarm (0x0B01}),
//    	MeterFailAlarm (0x0B02}),
//    	FullyCharged (0x0C01}),
//    	BoundaryValue (0x0D01}),
//    	Undervoltage (0x0D02}),
//    	Overvoltage (0x0D03}),
//    	PowerOutage (0x0D04}),
//    	PowerreEstablished (0x0D05}),
//    	VoltageUnbalance (0x0D06}),
//    	EnergyLevel (0x0E01}),
//    	MeteringFail_HLS (0x0F01}),
//    	CommunicationFail_TLS_DTLS (0x0F02});
    	
//    	[New Version] Network Interface Protocol 5.34 Version.
    	PowerFail (0x0101),
    	PowerRestore (0x0102),
    	CaseAlarmOpen (0x0201),
    	CaseAlarmClose (0x0202),
    	LineMissing (0x0301),
    	MeterError (0x0401),
    	TimeSynchronization (0x0501),
    	ModemEvent_MeterTimeSynced (0x0502),
    	MeterUpgradeFinished(0x0503),
    	Reset (0x1008),
    	ThresholdWarning (0x0701),
    	MagneticTamper (0x0801),
    	Equipment_ConfigurationChanged (0x0901),
    	Equipment_Installed (0x0902),    	
    	Equipment_Registered (0x0903),
    	Equipment_SelfTest (0x0904),
    	Equipment_Shutdown (0x0905),
    	Equipment_Restart (0x0906),
    	Equipment_FirmwareUpdate (0x0907),
    	BatteryHealthLowBattery (0x0A01),
    	BatteryHealthLowBatteryRestore (0x0A02),
    	FactoryDefaultSetting (0x0B01),
    	MeterNOResponse (0x0B02),
    	DuplicatedEquipment (0x0C01),
    	OTA_Download (0x0D01),
    	OTA_Start (0x0D02),
    	OTA_End (0x0D03),
    	OTA_Result (0x0D04),
    	OTA_FOTA_Result (0x0D05),
    	OTA_ModuleUpgradeResult (0x0D06),
    	CommunicationFailure (0x0E01),
    	CommunicationRestore (0x0E02),
    	Malfunction_DiskError (0x0F01),
    	Malfunction_DiskRestore (0x0F02),
    	Malfunction_MemoryError (0x0F03),
    	Malfunction_MemoryRestore (0x0F04),
    	MeterValueAlarm (0x1001),
    	MeteringValueIncorrect (0x1101),
    	BatteryCharging_Start (0x1201),
    	BatteryCharging_End (0x1202),
    	SecurityAlarm_MeteringFail_HLS (0x1301),
    	SecurityAlarm_CommunicationFail_TLS_DTLS (0x1302),
    	DoorOpen (0x1302),
    	DoorClose (0x1302),
    	CBStatus (0x1302);
    	
    	private int code;
    	AlarmId(int code) {
           this.code = code;
        }
       
        public int getCode() {
           return this.code;
        }
    }
    
    public int count;
    public String[] time;
    public AlarmId[] _alarmId;
    public int[] payload;
    
    public byte[] getEmpty() {
		return empty;
	}
	public void setEmpty(byte[] empty) {
		this.empty = empty;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String[] getTime() {
		return time;
	}
	public void setTime(String[] time) {
		this.time = time;
	}
	public int[] getPayload() {
		return payload;
	}
	public void setPayload(int[] payload) {
		this.payload = payload;
	}
	public AlarmId[] getAlarmId() {
		return _alarmId;
	}
	public void setAlarmId(AlarmId[] _alarmId) {
		this._alarmId = _alarmId;
	}
	public void setAlarmId(int idx, int code) {
        for (AlarmId c : AlarmId.values()) {
            if (c.getCode() == code) {
            	_alarmId[idx] = c;
                break;
            }
        }
    }
    
    @Override
    public void decode(byte[] bx) {
    	 int pos = 0;
		 byte[] b = new byte[1];
	     System.arraycopy(bx, pos, b, 0, b.length);
	     pos += b.length;
	     count = DataUtil.getIntToByte(b[0]);
	     
	     time = new String[count];
	     _alarmId = new AlarmId[count];
	     payload = new int[count];
	     
	     for (int i = 0; i < count; i ++) {
    	     b = new byte[7];
    	     System.arraycopy(bx, pos, b, 0, b.length);
    	     time[i] = String.format("%4d%02d%02d%02d%02d%02d", 
    	                DataUtil.getIntTo2Byte(new byte[]{b[0], b[1]}),
    	                DataUtil.getIntToByte(b[2]),
    	                DataUtil.getIntToByte(b[3]),
    	                DataUtil.getIntToByte(b[4]),
    	                DataUtil.getIntToByte(b[5]),
    	                DataUtil.getIntToByte(b[6]));
    	     pos += b.length;
    	     
    	     b = new byte[2];
    	     System.arraycopy(bx, pos, b, 0, b.length);
    	     setAlarmId(i, DataUtil.getIntTo2Byte(b));
    	     pos += b.length;
    	     
    	     b = new byte[4];
    	     System.arraycopy(bx, pos, b, 0, b.length);
    	     payload[i] = DataUtil.getIntTo4Byte(b);
    	     pos += b.length;
	     }
    }

	@Override
	public String toString() {
	    StringBuffer buf = new StringBuffer();
	    buf.append("[AlarmEvent]");
	    buf.append("[count:" + count + "]");
	    for (int i = 0; i < count; i++) {
	        buf.append("{idx:"+i+"=");
	        buf.append("[time:" + time[i] + "]");
	        buf.append("[alarmId:" + _alarmId[i] + "]");
	        buf.append("[payload:" + payload[i] + "]");
	        buf.append("}");
	    }
	    
	    return buf.toString();
	}
    
    @Override
    public byte[] encode() throws Exception {
        return empty;
    }

    @Override
    public void setCommandFlow(byte code){ }
  
    @Override
    public void setCommandType(byte code){ }
   
    @Override
    public byte[] getFrameTid(){ return null;}
    
    @Override
    public void setFrameTid(byte[] code){}
}
