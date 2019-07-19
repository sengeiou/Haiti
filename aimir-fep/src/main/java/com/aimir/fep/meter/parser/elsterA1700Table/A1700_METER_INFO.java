package com.aimir.fep.meter.parser.elsterA1700Table;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Hex;

/**
 * 
 * @author choiEJ
 *
 */
public class A1700_METER_INFO {    
	private Log log = LogFactory.getLog(A1700_METER_INFO.class);
    
    public static final int OFS_METER_MODEL    = 0;
    public static final int OFS_METER_SERIAL   = 12;
    public static final int OFS_METER_TIME 	   = 28;
    public static final int OFS_RMS_CURRENT    = 35;
    public static final int OFS_RMS_VOLTAGE    = 63;
    public static final int OFS_ACTIVE_POWER   = 91;
    public static final int OFS_REACTIVE_POWER = 119;
    public static final int OFS_APPARENT_POWER = 147;
    public static final int OFS_POWER_FACTOR   = 175;
    public static final int OFS_FREQUENCY      = 203;
    public static final int OFS_PHASE_ANGLE    = 231;
    public static final int OFS_VT_PRIMARY     = 259;
    public static final int OFS_VT_SECONDARY   = 263;
    public static final int OFS_CT_PRIMARY     = 266;
    public static final int OFS_CT_SECONDARY   = 270;
    
    public static final int LEN_METER_MODEL    = 12;
    public static final int LEN_METER_SERIAL   = 16;
    public static final int LEN_METER_TIME 	   = 7;
    public static final int LEN_VT_CT_PRIMARY  = 4;
    public static final int LEN_VT_SECONDARY   = 3;
    public static final int LEN_CT_SECONDARY   = 2;
    
    public static final int LEN_IS_DATA        = 28;
    public static final int LEN_IS_DATA_VALUE  = 6;
    public static final int LEN_IS_DATA_SCAL   = 1;
    
	private byte[] rawData = null;
    
	/**
	 * Constructor
	 */
	public A1700_METER_INFO(byte[] rawData) {
        this.rawData = rawData;
	}
	
	public static void main(String args[]) {
    	A1700_TEST_DATA testData = new A1700_TEST_DATA();
    	A1700_METER_INFO elster = new A1700_METER_INFO(testData.getTestData_meter());
    	System.out.println(elster.toString());
	}
	
	public String getMeterModel() throws Exception {
		String meterModel = new String(DataFormat.select(rawData, OFS_METER_MODEL, LEN_METER_MODEL));
		log.debug("METER_MODEL=[" + meterModel + "]");
		
		return meterModel;
	}
	
	public String getMeterSerial() throws Exception {
		String meterSerial = new String(DataFormat.select(rawData, OFS_METER_SERIAL, LEN_METER_SERIAL)).trim();
		log.debug("METER_SERIAL=[" + meterSerial + "]");
		
		return meterSerial;
	}
	
	public String getMeterTime() throws Exception {
		String meterTime = "";
		byte[] time = DataFormat.select(rawData, OFS_METER_TIME, LEN_METER_TIME);
		byte[] temp = new byte[1];

		int sec   = Integer.parseInt(Hex.decode(DataFormat.select(time, 0, 1)));
		int min   = Integer.parseInt(Hex.decode(DataFormat.select(time, 1, 1)));
		int hour  = Integer.parseInt(Hex.decode(DataFormat.select(time, 2, 1)));
		temp[0]   = (byte) (DataFormat.select(time, 3, 1)[0] & 0x3F);
		int day   = Integer.parseInt(Hex.decode(temp));
		temp[0]   = (byte) (DataFormat.select(time, 4, 1)[0] & 0x1F);
		int month = Integer.parseInt(Hex.decode(temp));
		int year  = Integer.parseInt(Hex.decode(DataFormat.select(time, 6, 1)));
		
		Calendar cal = Calendar.getInstance();	// 현재 년도 [yy]yy 중 앞의 두 자리를 가져오기 위함. 
		
		meterTime = Integer.toString(cal.get(Calendar.YEAR)).substring(0, 2) + year + "" +
		            (month < 10? "0"+month : month) + 
		            (day < 10? "0"+day : day) +
		            (hour < 10? "0"+hour : hour) +
		            (min < 10? "0"+min : min) +
		            (sec < 10? "0"+sec : sec);

		log.debug("METER_TIME=[" + meterTime + "]");
		
		return meterTime;
	}
	
	public Map<String, Double> getRMSCurrent() throws Exception {
		return getValue(DataFormat.select(rawData, OFS_RMS_CURRENT, LEN_IS_DATA));
	}
	
	public Map<String, Double> getRMSVoltage() throws Exception {
		return getValue(DataFormat.select(rawData, OFS_RMS_VOLTAGE, LEN_IS_DATA));
	}
	
	public Double getRMSVoltageTotal() throws Exception {
		return getRMSVoltage().get("total");
	}
	
	public Double getRMSVoltagePhaseA() throws Exception {
		return getRMSVoltage().get("phaseA");
	}
	
	public Double getRMSVoltagePhaseB() throws Exception {
		return getRMSVoltage().get("phaseB");
	}
	
	public Double getRMSVoltagePhaseC() throws Exception {
		return getRMSVoltage().get("phaseC");
	}
	
	public Map<String, Double> getPowerFactor() throws Exception {
		return getValue(DataFormat.select(rawData, OFS_POWER_FACTOR, LEN_IS_DATA));
	}
	
	public Double getPowerFactorTotal() throws Exception {
		return getPowerFactor().get("total");
	}
	
	public Double getPowerFactorPhaseA() throws Exception {
		return getPowerFactor().get("phaseA");
	}
	
	public Double getPowerFactorPhaseB() throws Exception {
		return getPowerFactor().get("phaseB");
	}
	
	public Double getPowerFactorPhaseC() throws Exception {
		return getPowerFactor().get("phaseC");
	}
	
	public Map<String, Double> getActivePower() throws Exception {
		return getValue(DataFormat.select(rawData, OFS_ACTIVE_POWER, LEN_IS_DATA));
	}
	
	public Double getActivePowerTotal() throws Exception {
		return getActivePower().get("total");
	}
	
	public Double getActivePowerPhaseA() throws Exception {
		return getActivePower().get("phaseA");
	}
	
	public Double getActivePowerPhaseB() throws Exception {
		return getActivePower().get("phaseB");
	}
	
	public Double getActivePowerPhaseC() throws Exception {
		return getActivePower().get("phaseC");
	}
	
	public Map<String, Double> getReactivePower() throws Exception {
		return getValue(DataFormat.select(rawData, OFS_REACTIVE_POWER, LEN_IS_DATA));
	}
	
	public Double getReactivePowerTotal() throws Exception {
		return getReactivePower().get("total");
	}
	
	public Double getReactivePowerPhaseA() throws Exception {
		return getReactivePower().get("phaseA");
	}
	
	public Double getReactivePowerPhaseB() throws Exception {
		return getReactivePower().get("phaseB");
	}
	
	public Double getReactivePowerPhaseC() throws Exception {
		return getReactivePower().get("phaseC");
	}
	
	public Map<String, Double> getApparentPower() throws Exception {
		return getValue(DataFormat.select(rawData, OFS_APPARENT_POWER, LEN_IS_DATA));
	}
	
	public Double getApparentPowerTotal() throws Exception {
		return getApparentPower().get("total");
	}
	
	public Double getApparentPowerPhaseA() throws Exception {
		return getApparentPower().get("phaseA");
	}
	
	public Double getApparentPowerPhaseB() throws Exception {
		return getApparentPower().get("phaseB");
	}
	
	public Double getApparentPowerPhaseC() throws Exception {
		return getApparentPower().get("phaseC");
	}
	
	public Map<String, Double> getFrequency() throws Exception {
		return getValue(DataFormat.select(rawData, OFS_FREQUENCY, LEN_IS_DATA));
	}
	
	public Double getFrequencyTotal() throws Exception {
		return getFrequency().get("total");
	}
	
	public Double getFrequencyPhaseA() throws Exception {
		return getFrequency().get("phaseA");
	}
	
	public Double getFrequencyPhaseB() throws Exception {
		return getFrequency().get("phaseB");
	}
	
	public Double getFrequencyPhaseC() throws Exception {
		return getFrequency().get("phaseC");
	}
	
	public Map<String, Double> getPhaseAngle() throws Exception {
		return getValue(DataFormat.select(rawData, OFS_PHASE_ANGLE, LEN_IS_DATA));
	}
	
	public Double getPhaseAngleTotal() throws Exception {
		return getPhaseAngle().get("total");
	}
	
	public Double getPhaseAnglePhaseA() throws Exception {
		return getPhaseAngle().get("phaseA");
	}
	
	public Double getPhaseAnglePhaseB() throws Exception {
		return getPhaseAngle().get("phaseB");
	}
	
	public Double getPhaseAnglePhaseC() throws Exception {
		return getPhaseAngle().get("phaseC");
	}
	
	public String getVTPrimary() throws Exception {
		String vtPrimary = Hex.decode(DataFormat.select(rawData, OFS_VT_PRIMARY, LEN_VT_CT_PRIMARY));
		return vtPrimary;
	}
	
	public String getVTSecondary() throws Exception {
		String vtSecondary = Hex.decode(DataFormat.select(rawData, OFS_VT_SECONDARY, LEN_VT_SECONDARY));
		return vtSecondary;
	}
	
	public String getCTPrimary() throws Exception {
		String ctPrimary = Hex.decode(DataFormat.select(rawData, OFS_CT_PRIMARY, LEN_VT_CT_PRIMARY));
		return ctPrimary;
	}
	
	public String getCTSecondary() throws Exception {
		String ctSecondary = Hex.decode(DataFormat.select(rawData, OFS_CT_SECONDARY, LEN_CT_SECONDARY));
		return ctSecondary;
	}
	
	/**
	 * 채널 상세 값을 Map에 담아 리턴한다.
	 * @param instrument
	 * @return Map
	 * @throws Exception
	 */
	public Map<String, Double> getValue(byte[] instrument) throws Exception {
	    log.debug("METER_INFO_RAW[" + Hex.decode(instrument) + "]");
		Map<String, Double> data = new HashMap<String, Double>();
		int offset = 0;
		String value = "";
		int scal = 0;
		int firstByte = 0;
		
		scal = DataFormat.select(instrument, offset, LEN_IS_DATA_SCAL)[0] & 0x07;
		offset += LEN_IS_DATA_SCAL;
		log.debug("PHASE_A_SCAL=[" + scal + "]");
		
		if (scal != 7) {
    		firstByte = DataFormat.getIntToBytes(DataFormat.select(instrument, offset, 1));
    		if (firstByte != 0xFF) {
    			value = Hex.decode(DataFormat.select(instrument, offset, LEN_IS_DATA_VALUE));
    		} else {
    			value = "0.0";
    		}
    		data.put("phaseA", Double.parseDouble(value) / Math.pow(10, 4-scal));
    		log.debug("PHASE_A_VALUE=[" + data.get("phaseA") + "]");
		}
		else {
            log.info("PHASE_A_VALUE too large!!");
        }
        offset += LEN_IS_DATA_VALUE;
		
		scal = DataFormat.select(instrument, offset, LEN_IS_DATA_SCAL)[0] & 0x07;
		offset += LEN_IS_DATA_SCAL;
		log.debug("PHASE_B_SCAL=[" + scal + "]");

		if (scal != 7) {
    		firstByte = DataFormat.getIntToBytes(DataFormat.select(instrument, offset, 1));
    		if (firstByte != 0xFF) {
    			value = Hex.decode(DataFormat.select(instrument, offset, LEN_IS_DATA_VALUE));
    		} else {
    			value = "0.0";
    		}
    		data.put("phaseB", Double.parseDouble(value) / Math.pow(10, 4-scal));
    		log.debug("PHASE_B_VALUE=[" + data.get("phaseB") + "]");
		}
		else {
            log.info("PHASE_B_VALUE too large!!");
        }
		offset += LEN_IS_DATA_VALUE;
		
		scal = DataFormat.select(instrument, offset, LEN_IS_DATA_SCAL)[0] & 0x07;
		offset += LEN_IS_DATA_SCAL;
		log.debug("PHASE_C_SCAL=[" + scal + "]");
		
		if (scal != 7) {
    		firstByte = DataFormat.getIntToBytes(DataFormat.select(instrument, offset, 1));
    		if (firstByte != 0xFF) {
    			value = Hex.decode(DataFormat.select(instrument, offset, LEN_IS_DATA_VALUE));
    		} else {
    			value = "0.0";
    		}
    		data.put("phaseC", Double.parseDouble(value) / Math.pow(10, 4-scal));
    		log.debug("PHASE_C_VALUE=[" + data.get("phaseC") + "]");
		}
		else {
            log.info("PHASE_C_VALUE too large!!");
        }
		offset += LEN_IS_DATA_VALUE;
		
		scal = DataFormat.select(instrument, offset, LEN_IS_DATA_SCAL)[0] & 0x07;
        offset += LEN_IS_DATA_SCAL;
        log.debug("TOTAL_SCAL=[" + scal + "]");

        /*
         * 0:8.4, 1:9.3, 2:10.2, 3:11.1, 4:12.0, 7:Value too large
         */
        if (scal != 7) {
            // RMS current, RMS voltage, Power factor, Active power 는 total 값이 안와서
            // 첫번째바이트만 받아서 FF가 아닐 시에만 value를 double로 변환하여 저장한다.
            firstByte = DataFormat.getIntToBytes(DataFormat.select(instrument, offset, 1));
            if (firstByte != 0xFF) {
                value = Hex.decode(DataFormat.select(instrument, offset, LEN_IS_DATA_VALUE));
            } else {
                value = "0.0";
            }
            // 소수 4자리가 base
            data.put("total", Double.parseDouble(value) / Math.pow(10, 4-scal));
            log.debug("TOTAL_VALUE=[" + data.get("total") + "]");
        }
        else {
            log.info("TOTAL_VALUE too large!!");
        }
        offset += LEN_IS_DATA_VALUE;
        
		return data;
	}
	
    public String toString() {
        StringBuffer sb = new StringBuffer();
        
        try {
            sb.append("A1700_METER_INFO[\n")
              .append("  (METER_MODEL="   ).append(getMeterModel()).append("),\n")
              .append("  (METER_SERIAL="  ).append(getMeterSerial()).append("),\n")
              .append("  (METER_TIME="    ).append(getMeterTime()).append("),\n")
              .append("  (RMS_CURRENT="   ).append(getRMSCurrent().toString()).append("),\n")
              .append("  (RMS_VOLTAGE="   ).append(getRMSVoltage().toString()).append("),\n")
              .append("  (POWER_FACTOR="  ).append(getPowerFactor().toString()).append("),\n")
              .append("  (ACTIVE_POWER="  ).append(getActivePower().toString()).append("),\n")
              .append("  (REACTIVE_POWER=").append(getReactivePower().toString()).append("),\n")
              .append("  (APPARENT_POWER=").append(getApparentPower().toString()).append("),\n")
              .append("  (FREQUENCY="     ).append(getFrequency().toString()).append("),\n")
              .append("  (PHASE_ANGLE="   ).append(getPhaseAngle().toString()).append("),\n")
              .append("  (VT_PRIMARY="   ).append(getVTPrimary()).append("),\n")
              .append("  (VT_SECONDARY="   ).append(getVTSecondary()).append("),\n")
              .append("  (CT_PRIMARY="   ).append(getCTPrimary()).append("),\n")
              .append("  (CT_SECONDARY="   ).append(getCTSecondary()).append("),\n")
              .append("]\n");
        } catch (Exception e) {
            log.error("A1700_MODEM_INFO ERR => " + e.getMessage(),e);
        }
        return sb.toString();
    }
}
