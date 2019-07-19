/**
 * NAPC_W201.java
 * 2011. 11. 2.
 * Copyright (c) 2008-2009 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 * 
 * @author kskim
 */
package com.aimir.fep.meter.parser;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.constants.CommonConstants;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.system.Code;

/**
 * 
 * Parsing The W201 meter data.<br>
 * Project South Africa - Spasa / Lephalale.<br>
 * @see UMC2000G4R.java
 * @author kskim
 */
@SuppressWarnings("serial")
public class NAPC_W202 extends ZEUPLS2 implements ModemParser, java.io.Serializable {

	private static Log log = LogFactory.getLog(NAPC_W202.class);

	private byte[] CURRENT_PULSE = new byte[4];
    private byte[] SERIAL_NUMBER = new byte[12];
    private byte[] ALARM_STATUS = new byte[1];
    private byte[] METER_STATUS = new byte[1];

    private byte[] FUNCTION_TEST_RESULT = new byte[7];
    private byte[] METER_HARDWARE_VERSION = new byte[7];
    private byte[] METER_SOFTWARE_VERSION = new byte[7];

    // meter status
    private Double currentPulse = 0.0;
    private String serialNumber = null;
    private byte alarmStatus = 0x00;
    private byte meterStatus = 0x00;
    // meter version
    private String functionTestResult = null;
    private String meterHwVerison = null;
    private String meterSwVersion = null;

    

    /**
     * parse meter mesurement data
     * @param data
     */
    public void parse(byte[] data) throws Exception {
    	log.debug("HEX : " + Hex.decode(data));

    	String hexZero = new String(new byte[]{0x00});
    	
    	int tailData = 0;	// 기존 파서 뒤에 붙은 데이터 크기

    	if (this.isOnDemand()) {
    		tailData = 4 + 12 + 1 + 1 + 7 + 7 + 7;
    	} else {
    		tailData = 2;
    	}

		int pos2 = data.length - tailData;

		byte[] resultData = new byte[pos2];
		System.arraycopy(data, 0, resultData, 0, pos2);
		super.parse(data);

		if (this.isOnDemand()) {
			log.debug("isOndemnad[" + this.isOnDemand() + "]");
			System.arraycopy(data, pos2, CURRENT_PULSE, 0, CURRENT_PULSE.length);
	    	pos2 += CURRENT_PULSE.length;
	        currentPulse = new Double(DataUtil.getLongToBytes(CURRENT_PULSE));
	        log.debug("METER_CURRENT_PULSE[" + currentPulse + "]");

	        System.arraycopy(data, pos2, SERIAL_NUMBER, 0, SERIAL_NUMBER.length);
	        pos2 += SERIAL_NUMBER.length;
	        serialNumber = DataUtil.getString(SERIAL_NUMBER).replaceAll(hexZero, ""); //00는 삭제한다.
	        log.debug("SERIAL_NUMBER[" + serialNumber + "] - " + Hex.getHexDump(SERIAL_NUMBER));

	        System.arraycopy(data, pos2, ALARM_STATUS, 0, ALARM_STATUS.length);
	        pos2 += ALARM_STATUS.length;
	        alarmStatus = ALARM_STATUS[0];
	        log.debug("ALARM_STATUS[" + alarmStatus + "]");

	        System.arraycopy(data, pos2, METER_STATUS, 0, METER_STATUS.length);
	        pos2 += METER_STATUS.length;
	        meterStatus = METER_STATUS[0];
	        log.debug("METER_STATUS[" + meterStatus + "]");

	        System.arraycopy(data, pos2, FUNCTION_TEST_RESULT, 0, FUNCTION_TEST_RESULT.length);
	        pos2 += FUNCTION_TEST_RESULT.length;
	        functionTestResult = DataUtil.getString(FUNCTION_TEST_RESULT).replaceAll(hexZero, "");
	        log.debug("FUNCTION_TEST_RESULT[" + functionTestResult + "] - " + Hex.getHexDump(SERIAL_NUMBER));

	        System.arraycopy(data, pos2, METER_HARDWARE_VERSION, 0, METER_HARDWARE_VERSION.length);
	        pos2 += METER_HARDWARE_VERSION.length;
	        meterHwVerison = DataUtil.getString(METER_HARDWARE_VERSION).replaceAll(hexZero, "");
	        log.debug("METER_HARDWARE_VERSION[" + meterHwVerison + "] - " + Hex.getHexDump(SERIAL_NUMBER));

	        System.arraycopy(data, pos2, METER_SOFTWARE_VERSION, 0, METER_SOFTWARE_VERSION.length);
	        pos2 += METER_SOFTWARE_VERSION.length;
	        meterSwVersion = DataUtil.getString(METER_SOFTWARE_VERSION).replaceAll(hexZero, "");
	        log.debug("METER_SOFTWARE_VERSION[" + meterSwVersion + "] - "  + Hex.getHexDump(SERIAL_NUMBER));
    	} else {
    		log.debug("isOndemnad[" + this.isOnDemand() + "]");

    		System.arraycopy(data, pos2, ALARM_STATUS, 0, ALARM_STATUS.length);
            pos2 += ALARM_STATUS.length;
            alarmStatus = ALARM_STATUS[0];
            log.debug("ALARM_STATUS[" + alarmStatus + "]");

            System.arraycopy(data, pos2, METER_STATUS, 0, METER_STATUS.length);
            pos2 += METER_STATUS.length;
            meterStatus = METER_STATUS[0];
            log.debug("METER_STATUS[" + meterStatus + "]");
    	}
	}

    /**
     * get String
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(super.toString());
        sb.append("+[\n");

        //meter status
        sb.append("(Meter Current Pulse : ").append(currentPulse.toString()).append("),");
        sb.append("(Serial Number : ").append(serialNumber).append("),");
        sb.append("(Alarm Status : ").append(alarmStatus).append("),");
        sb.append("(Meter Status : ").append(meterStatus).append("),");

        // meter version
        sb.append("(Function Test Result : ").append(functionTestResult).append("),");
        sb.append("(Meter HW Version : ").append(meterHwVerison).append("),");
        sb.append("(Meter SW Version : ").append(meterSwVersion).append("),");

        sb.append("]\n");

        return sb.toString();
    }

    @Override
    public LinkedHashMap getDataNotFormatting()
    {
    	return getData();
    }

    @Override
    public LinkedHashMap<String, String> getData() {

        LinkedHashMap<String, String> res = new LinkedHashMap<String, String>(16,0.75f,false);

        res.put("currentTime", currentTime);
        res.put("lpPeriod", ""+lpPeriod);
        res.put("hwVersion", hwVersion);
        res.put("swVersion", swVersion);
        res.put("fwBuild", fwBuild);
        res.put("lqi", "" + lqi);
        res.put("rssi", "" + rssi);
        res.put("nodeKind", "" + nodeKind);
        res.put("alarmFlag", "" + alarmFlag);
        res.put("networkType", "" + networkType);

        // 
        res.put("meterCurrentPulse", currentPulse.toString());
        res.put("serialNumber", serialNumber);
        res.put("alarmStatus", getWaterMeterAlarmStatus(alarmStatus));
        res.put("meterStatus", CommonConstants.getGasMeterStatus(meterStatus+"").getDescr());
        res.put("functionTestResult", functionTestResult);
        res.put("meterHwVersion", meterHwVerison);
        res.put("meterSwVersion", meterSwVersion);

        return res;
    }

    /**
     * 미터의 AlarmStatus 메시지를 구한다.<br>
     * Status 메시지는 Gas Meter 와 동일하다.
     * @param status 메시지와 매칭되는 Bit 값이다. 해당 Bit값으로 메시지를 검색한다. 
     * @return 매칭되는 AlarmStatus 메시지.
     */
    private String getWaterMeterAlarmStatus(byte status) {
    	
    	//Gas 와 Water 는 같이 사용한다.
        Hashtable<String, Code> codes = CommonConstants.getGasMeterAlarmStatusCodes();
        
        StringBuffer buf = new StringBuffer();
        
        String key = null;
        for (Enumeration<String> e = codes.keys(); e.hasMoreElements(); ) {
            if (buf.length() != 0)
                buf.append(", ");
            
            key = e.nextElement();
            buf.append(codes.get(key).getDescr());
            buf.append("(");
            if ((Byte.parseByte(key) & status) != 0x00) {
                buf.append("Open");
            }
            else buf.append("Close");
            buf.append(")");
        }
        
        return buf.toString();
    }
    public Double getCurrentPulse() {
        return currentPulse;
    }

    public void setCurrentPulse(Double currentPulse) {
        this.currentPulse = currentPulse;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public byte getAlarmStatus() {
        return alarmStatus;
    }

    public void setAlarmStatus(byte alarmStatus) {
        this.alarmStatus = alarmStatus;
    }

    public byte getMeterStatus() {
        return meterStatus;
    }

    public void setMeterStatus(byte meterStatus) {
        this.meterStatus = meterStatus;
    }

    public String getFunctionTestResult() {
        return functionTestResult;
    }

    public void setFunctionTestResult(String functionTestResult) {
        this.functionTestResult = functionTestResult;
    }

    public String getMeterHwVerison() {
        return meterHwVerison;
    }

    public void setMeterHwVerison(String meterHwVerison) {
        this.meterHwVerison = meterHwVerison;
    }

    public String getMeterSwVersion() {
        return meterSwVersion;
    }

    public void setMeterSwVersion(String meterSwVersion) {
        this.meterSwVersion = meterSwVersion;
    }
    
}
