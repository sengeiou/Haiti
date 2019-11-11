/** 
 * @(#)NT509.java       1.0 2019-11-07 *
 * 
 * Copyright (c) 2009-2010 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.SM110Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;
import com.aimir.util.DateTimeUtil;

/**
 * @author Park Jiwoong wll27471297@nuritelecom.com
 */
public class NT509 implements java.io.Serializable {

	private static final long serialVersionUID = -3024653700839379563L;
	
    private static Log log = LogFactory.getLog(NT509.class);
	
	// Info Frame : 44 Byte
	// -> CURRENT_TIME : 11 Byte
	private byte[] TIME_ZONE = new byte[2];
	private byte[] DST_VALUE = new byte[2];
	private byte[] YEAR = new byte[2];
	private byte[] MONTH = new byte[1];
	private byte[] DAY = new byte[1];
	private byte[] HOUR = new byte[1];
	private byte[] MINUTE = new byte[1];
	private byte[] SECOND = new byte[1];

	private byte[] OPERATING_DAY = new byte[2];
	private byte[] ACTIVE_MINUTE = new byte[2];
	private byte[] BATTERY_VOLT = new byte[2];
	private byte[] CONSUMPTION_CURRENT = new byte[2];
	private byte[] OFFSET = new byte[1];

	private byte[] CURRENT_PULSE = new byte[4];
	private byte[] LP_CHOICE = new byte[1];
	private byte[] LP_PERIOD = new byte[1]; 
	private byte[] LP_DATE = new byte[4];
	private byte[] BASE_PULSE = new byte[4];
	private byte[] FW_VERSION = new byte[1];

	private byte[] FW_BUILD = new byte[1];
	private byte[] HW_VERSION = new byte[1];
	private byte[] SW_VERSION = new byte[1];
	private byte[] LQI = new byte[1];
	private byte[] RSSI = new byte[1];
	private byte[] NODE_KIND_TYPE = new byte[1];
	private byte[] ALARM_FLAG = new byte[1];
	private byte[] NETWORK_TYPE = new byte[1];
	private byte[] ENERGY_LEVEL = new byte[1];
	
	// LP Data
	private byte[] LP_DATA = new byte[4];
	private byte[] LP_BASE_PULSE = new byte[4];

//	private byte[] data;
    
    public NT509() { }
    
	public NT509(byte[] data) {
//		this.data = data;
		parse(data);
		printAll();
	}
	
	public void parse(byte[] data) {
    	int pos = 0;
		
		System.arraycopy(data, pos, TIME_ZONE, 0, TIME_ZONE.length); pos += TIME_ZONE.length;		
		System.arraycopy(data, pos, DST_VALUE, 0, DST_VALUE.length); pos += DST_VALUE.length;
		System.arraycopy(data, pos, YEAR, 0, YEAR.length); pos += YEAR.length;
		System.arraycopy(data, pos, MONTH, 0, MONTH.length); pos += MONTH.length;
		System.arraycopy(data, pos, DAY, 0, DAY.length); pos += DAY.length;
		System.arraycopy(data, pos, HOUR, 0, HOUR.length); pos += HOUR.length;
		System.arraycopy(data, pos, MINUTE, 0, MINUTE.length); pos += MINUTE.length;
		System.arraycopy(data, pos, SECOND, 0, SECOND.length); pos += SECOND.length;
		
		System.arraycopy(data, pos, OPERATING_DAY, 0, OPERATING_DAY.length); pos += OPERATING_DAY.length;
		System.arraycopy(data, pos, ACTIVE_MINUTE, 0, ACTIVE_MINUTE.length); pos += ACTIVE_MINUTE.length;
		System.arraycopy(data, pos, BATTERY_VOLT, 0, BATTERY_VOLT.length); pos += BATTERY_VOLT.length;
		System.arraycopy(data, pos, CONSUMPTION_CURRENT, 0, CONSUMPTION_CURRENT.length); pos += CONSUMPTION_CURRENT.length;
		System.arraycopy(data, pos, OFFSET, 0, OFFSET.length); pos += OFFSET.length;

		System.arraycopy(data, pos, CURRENT_PULSE, 0, CURRENT_PULSE.length); pos += CURRENT_PULSE.length;
		System.arraycopy(data, pos, LP_CHOICE, 0, LP_CHOICE.length); pos += LP_CHOICE.length;
		System.arraycopy(data, pos, LP_PERIOD, 0, LP_PERIOD.length); pos += LP_PERIOD.length;
		System.arraycopy(data, pos, LP_DATE, 0, LP_DATE.length); pos += LP_DATE.length;
		System.arraycopy(data, pos, BASE_PULSE, 0, BASE_PULSE.length); pos += BASE_PULSE.length;
		System.arraycopy(data, pos, FW_VERSION, 0, FW_VERSION.length); pos += FW_VERSION.length;
		
		System.arraycopy(data, pos, FW_BUILD, 0, FW_BUILD.length); pos += FW_BUILD.length;
		System.arraycopy(data, pos, HW_VERSION, 0, HW_VERSION.length); pos += HW_VERSION.length;
		System.arraycopy(data, pos, SW_VERSION, 0, SW_VERSION.length); pos += SW_VERSION.length;
		System.arraycopy(data, pos, LQI, 0, LQI.length); pos += LQI.length;
		System.arraycopy(data, pos, RSSI, 0, RSSI.length); pos += RSSI.length;
		System.arraycopy(data, pos, NODE_KIND_TYPE, 0, NODE_KIND_TYPE.length); pos += NODE_KIND_TYPE.length;
		System.arraycopy(data, pos, ALARM_FLAG, 0, ALARM_FLAG.length); pos += ALARM_FLAG.length;
		System.arraycopy(data, pos, NETWORK_TYPE, 0, NETWORK_TYPE.length); pos += NETWORK_TYPE.length;
		System.arraycopy(data, pos, ENERGY_LEVEL, 0, ENERGY_LEVEL.length); pos += ENERGY_LEVEL.length;

		System.arraycopy(data, pos, LP_DATA, 0, LP_DATA.length); pos += LP_DATA.length;
		System.arraycopy(data, pos, LP_BASE_PULSE, 0, LP_BASE_PULSE.length); pos += LP_BASE_PULSE.length;
		
		log.debug("pos = "+pos+", data.length = "+data.length);
    }
	
	public void printAll() {
		StringBuilder sb = new StringBuilder();
		sb.append("TIME_ZONE="+new String(TIME_ZONE).trim()).append(", ")
		  .append("DST_VALUE="+new String(DST_VALUE).trim()).append(", ")
		  .append("YEAR="+new String(YEAR).trim()).append(", ")
		  .append("MONTH="+new String(MONTH).trim()).append(", ")
		  .append("DAY="+new String(DAY).trim()).append(", ")
		  .append("HOUR="+new String(HOUR).trim()).append(", ")
		  .append("MINUTE="+new String(MINUTE).trim()).append(", ")
		  .append("SECOND"+new String(SECOND).trim()).append(", ")
		  
		  .append("OPERATING_DAY="+new String(OPERATING_DAY).trim()).append(", ")
		  .append("ACTIVE_MINUTE="+new String(ACTIVE_MINUTE).trim()).append(", ")
		  .append("BATTERY_VOLT="+new String(BATTERY_VOLT).trim()).append(", ")
		  .append("CONSUMPTION_CURRENT="+new String(CONSUMPTION_CURRENT).trim()).append(", ")
		  .append("OFFSET="+new String(OFFSET).trim()).append(", ")
		  
		  .append("CURRENT_PULSE="+new String(CURRENT_PULSE).trim()).append(", ")
		  .append("LP_CHOICE="+new String(LP_CHOICE).trim()).append(", ")
		  .append("LP_PERIOD="+new String(LP_PERIOD).trim()).append(", ")
		  .append("LP_DATE="+new String(LP_DATE).trim()).append(", ")
		  .append("BASE_PULSE="+new String(BASE_PULSE).trim()).append(", ")
		  .append("FW_VERSION="+new String(FW_VERSION).trim()).append(", ")
		  
		  .append("FW_BUILD="+new String(FW_BUILD).trim()).append(", ")
		  .append("HW_VERSION="+new String(HW_VERSION).trim()).append(", ")
		  .append("SW_VERSION="+new String(SW_VERSION).trim()).append(", ")
		  .append("LQI="+new String(LQI).trim()).append(", ")
		  .append("RSSI="+new String(RSSI).trim()).append(", ")
		  .append("NODE_KIND_TYPE="+new String(NODE_KIND_TYPE).trim()).append(", ")
		  .append("ALARM_FLAG="+new String(ALARM_FLAG).trim()).append(", ")
		  .append("NETWORK_TYPE="+new String(NETWORK_TYPE).trim()).append(", ")
		  .append("ENERGY_LEVEL="+new String(ENERGY_LEVEL).trim()).append(", ")
		  
		  .append("LP_DATA="+new String(LP_DATA).trim()).append(", ")
		  .append("LP_BASE_PULSE="+new String(LP_BASE_PULSE).trim());
		
		log.info("NT509["+sb.toString()+"]");
	}
}
