/*
 * @(#)Mk6N_IS.java       1.0 2008/08/21 *
 *
 * Instrument.
 * Copyright (c) 2007-2008 NuriTelecom, Inc.
 * All rights reserved. *
 * This software is the confidential and proprietary information of
 * Nuritelcom, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Nuritelecom.
 */

package com.aimir.fep.meter.parser.Mk6NTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;

/**
 * @author kaze kaze@nuritelecom.com
 */
public class Mk6N_IS implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2204596894826816148L;

	private byte[] rawData = null;

	public static final int OFF_ACTIVE_VTA_B = 0;
	public static final int OFF_ACTIVE_VTA_C = 4;
	public static final int OFF_FREQUENCY = 8;
	public static final int OFF_PH_A_VOLTAGE = 12;
	public static final int OFF_PH_B_VOLTAGE = 16;
	public static final int OFF_PH_C_VOLTAGE = 20;
	public static final int OFF_PH_A_CURRENT = 24;
	public static final int OFF_PH_B_CURRENT = 28;
	public static final int OFF_PH_C_CURRENT = 32;
	public static final int OFF_PH_A_ANGLE = 36;
	public static final int OFF_PH_B_ANGLE = 40;
	public static final int OFF_PH_C_ANGLE = 44;
	public static final int OFF_PH_A_WATT = 48;
	public static final int OFF_PH_B_WATT = 52;
	public static final int OFF_PH_C_WATT = 56;
	public static final int OFF_PH_A_VAR = 60;
	public static final int OFF_PH_B_VAR = 64;
	public static final int OFF_PH_C_VAR = 68;
	public static final int OFF_PH_A_VA = 72;
	public static final int OFF_PH_B_VA = 76;
	public static final int OFF_PH_C_VA = 80;

	public static final int LEN_ACTIVE_VTA_B = 4;
	public static final int LEN_ACTIVE_VTA_C = 4;
	public static final int LEN_FREQUENCY = 4;
	public static final int LEN_PH_A_VOLTAGE = 4;
	public static final int LEN_PH_B_VOLTAGE = 4;
	public static final int LEN_PH_C_VOLTAGE = 4;
	public static final int LEN_PH_A_CURRENT = 4;
	public static final int LEN_PH_B_CURRENT = 4;
	public static final int LEN_PH_C_CURRENT = 4;
	public static final int LEN_PH_A_ANGLE = 4;
	public static final int LEN_PH_B_ANGLE = 4;
	public static final int LEN_PH_C_ANGLE = 4;
	public static final int LEN_PH_A_WATT = 4;
	public static final int LEN_PH_B_WATT = 4;
	public static final int LEN_PH_C_WATT = 4;
	public static final int LEN_PH_A_VAR = 4;
	public static final int LEN_PH_B_VAR = 4;
	public static final int LEN_PH_C_VAR = 4;
	public static final int LEN_PH_A_VA = 4;
	public static final int LEN_PH_B_VA = 4;
	public static final int LEN_PH_C_VA = 4;

    private static Log log = LogFactory.getLog(Mk6N_IS.class);

	/**
	 * Constructor .<p>
	 *
	 * @param data - read data (header,crch,crcl)
	 */
	public Mk6N_IS(byte[] rawData) {
		this.rawData = rawData;
	}

	public float getACTIVE_VTA_B() throws Exception {
		return DataFormat.bytesToFloat(DataFormat.select(rawData, OFF_ACTIVE_VTA_B, LEN_ACTIVE_VTA_B));
	}

	public float getACTIVE_VTA_C() throws Exception {
		return DataFormat.bytesToFloat(DataFormat.select(rawData, OFF_ACTIVE_VTA_C, LEN_ACTIVE_VTA_C));
	}

	public Double getFREQUENCY() throws Exception {
		return DataFormat.bytesToDouble(DataFormat.select(rawData, OFF_FREQUENCY, LEN_FREQUENCY));
	}

	public Double getPH_A_VOLTAGE() throws Exception {
		return DataFormat.bytesToDouble(DataFormat.select(rawData, OFF_PH_A_VOLTAGE, LEN_PH_A_VOLTAGE));
	}

	public Double getPH_B_VOLTAGE() throws Exception {
		return DataFormat.bytesToDouble(DataFormat.select(rawData, OFF_PH_B_VOLTAGE, LEN_PH_B_VOLTAGE));
	}

	public Double getPH_C_VOLTAGE() throws Exception {
		return DataFormat.bytesToDouble(DataFormat.select(rawData, OFF_PH_C_VOLTAGE, LEN_PH_C_VOLTAGE));
	}

	public Double getPH_A_CURRENT() throws Exception {
		return DataFormat.bytesToDouble(DataFormat.select(rawData, OFF_PH_A_CURRENT, LEN_PH_A_CURRENT));
	}

	public Double getPH_B_CURRENT() throws Exception {
		return DataFormat.bytesToDouble(DataFormat.select(rawData, OFF_PH_B_CURRENT, LEN_PH_B_CURRENT));
	}

	public Double getPH_C_CURRENT() throws Exception {
		return DataFormat.bytesToDouble(DataFormat.select(rawData, OFF_PH_C_CURRENT, LEN_PH_C_CURRENT));
	}

	public Double getPH_A_ANGLE() throws Exception {
		return DataFormat.bytesToDouble(DataFormat.select(rawData, OFF_PH_A_ANGLE, LEN_PH_A_ANGLE));
	}

	public Double getPH_B_ANGLE() throws Exception {
		return DataFormat.bytesToDouble(DataFormat.select(rawData, OFF_PH_B_ANGLE, LEN_PH_B_ANGLE));
	}

	public Double getPH_C_ANGLE() throws Exception {
		return DataFormat.bytesToDouble(DataFormat.select(rawData, OFF_PH_C_ANGLE, LEN_PH_C_ANGLE));
	}

	public Double getPH_A_WATT() throws Exception {
		return DataFormat.bytesToDouble(DataFormat.select(rawData, OFF_PH_A_WATT, LEN_PH_A_WATT))/1000;
	}

	public Double getPH_B_WATT() throws Exception {
		return DataFormat.bytesToDouble(DataFormat.select(rawData, OFF_PH_B_WATT, LEN_PH_B_WATT))/1000;
	}

	public Double getPH_C_WATT() throws Exception {
		return DataFormat.bytesToDouble(DataFormat.select(rawData, OFF_PH_C_WATT, LEN_PH_C_WATT))/1000;
	}

	public Double getPH_A_VAR() throws Exception {
		return DataFormat.bytesToDouble(DataFormat.select(rawData, OFF_PH_A_VAR, LEN_PH_A_VAR))/1000;
	}

	public Double getPH_B_VAR() throws Exception {
		return DataFormat.bytesToDouble(DataFormat.select(rawData, OFF_PH_B_VAR, LEN_PH_B_VAR))/1000;
	}

	public Double getPH_C_VAR() throws Exception {
		return DataFormat.bytesToDouble(DataFormat.select(rawData, OFF_PH_C_VAR, LEN_PH_C_VAR))/1000;
	}

	public Double getPH_A_VA() throws Exception {
		return DataFormat.bytesToDouble(DataFormat.select(rawData, OFF_PH_A_VA, LEN_PH_A_VA))/1000;
	}

	public Double getPH_B_VA() throws Exception {
		return DataFormat.bytesToDouble(DataFormat.select(rawData, OFF_PH_B_VA, LEN_PH_B_VA))/1000;
	}

	public Double getPH_C_VA() throws Exception {
		return DataFormat.bytesToDouble(DataFormat.select(rawData, OFF_PH_C_VA, LEN_PH_C_VA))/1000;
	}

	/**
	 * Constructs a <code>String</code> with all attributes
	 * in name = value format.
	 *
	 * @return a <code>String</code> representation
	 * of this object.
	 */
	public String toString()
	{
	    StringBuffer retValue = new StringBuffer();
	    try{
	    retValue.append("Mk6N_IS [ ")
	        .append("ACTIVE_VTA_B = ").append(getACTIVE_VTA_B()).append('\n')
	        .append("ACTIVE_VTA_C = ").append(getACTIVE_VTA_C()).append('\n')
	        .append("FREQUENCY = ").append(getFREQUENCY()).append('\n')
	        .append("PH_A_VOLTAGE = ").append(getPH_A_VOLTAGE()).append('\n')
	        .append("PH_B_VOLTAGE = ").append(getPH_B_VOLTAGE()).append('\n')
	        .append("PH_C_VOLTAGE = ").append(getPH_C_VOLTAGE()).append('\n')
	        .append("PH_A_CURRENT = ").append(getPH_A_CURRENT()).append('\n')
	        .append("PH_B_CURRENT = ").append(getPH_B_CURRENT()).append('\n')
	        .append("PH_C_CURENT = ").append(getPH_C_CURRENT()).append('\n')
	        .append("PH_A_ANGLE = ").append(getPH_A_ANGLE()).append('\n')
	        .append("PH_B_ANGLE = ").append(getPH_B_ANGLE()).append('\n')
	        .append("PH_C_ANGLE = ").append(getPH_C_ANGLE()).append('\n')
	        .append("PH_A_WATT = ").append(getPH_A_WATT()).append('\n')
	        .append("PH_B_WATT = ").append(getPH_B_WATT()).append('\n')
	        .append("PH_C_WATT = ").append(getPH_C_WATT()).append('\n')
	        .append("PH_A_VAR = ").append(getPH_A_VAR()).append('\n')
	        .append("PH_B_VAR = ").append(getPH_B_VAR()).append('\n')
	        .append("PH_C_VAR = ").append(getPH_C_VAR()).append('\n')
	        .append("PH_A_VA = ").append(getPH_A_VA()).append('\n')
	        .append("PH_B_VA = ").append(getPH_B_VA()).append('\n')
	        .append("PH_C_VA = ").append(getPH_C_VA()).append('\n')
	        .append(" ]");
	    }catch(Exception e){
	    	log.error("Mk6N_IS TO STRING ERR=>"+e.getMessage());
	    }
	    return retValue.toString();
	}
}
