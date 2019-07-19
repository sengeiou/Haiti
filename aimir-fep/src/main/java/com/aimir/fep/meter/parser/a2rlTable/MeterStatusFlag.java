/** 
 * @(#)MeterStatusFlag.java       1.0 08/10/27 *
 * 
 * Actual Dimension Register Table.
 * Copyright (c) 2006-2007 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.a2rlTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Kang SoYi ksoyi@nuritelecom.com
 */
public class MeterStatusFlag {
	
    public static final byte TEST_MODE_STATUS  	= (byte)0x01;
    public static final byte LOAD_CONTROL_RELAY_STATUS_FROM_THRESHOLD_CONTROL_FLAG  = (byte)0x02;
    public static final byte LOAD_CONTROL_RELAY_STATUS_FROM_RATE_TABLE_CONTROL_FLAG	= (byte)0x04;
    public static final byte METER_TIME_BASE= (byte)0x08;
    public static final byte PROGRAMMED_AC_LINE_FREQUENCY_INDICATOR	= (byte)0x10;
    public static final byte PHASE_C_POTENTIAL 	= (byte)0x20;
    public static final byte PHASE_B_POTENTIAL	= (byte)0x40;
    public static final byte PHASE_A_POTENTIAL	= (byte)0x80;

	private byte data;
    private Log log = LogFactory.getLog(MeterStatusFlag.class);
	
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public MeterStatusFlag(byte data) {
		this.data = data;
	}
	
    /**
     * TEST_MODE_STATUS
     */
    public boolean getTEST_MODE_STATUS() {
        int flag =(int)(data&TEST_MODE_STATUS);
        if (flag != 0){
            return true;
        }
        return false;
    }	
    
    /**
     * LOAD_CONTROL_RELAY_STATUS_FROM_THRESHOLD_CONTROL_FLAG
     */
    public boolean getLOAD_CONTROL_RELAY_STATUS_FROM_THRESHOLD_CONTROL_FLAG() {
        int flag =(int)(data&LOAD_CONTROL_RELAY_STATUS_FROM_THRESHOLD_CONTROL_FLAG);
        if (flag != 0){
            return true;
        }
        return false;
    }
    
    /**
     * LOAD_CONTROL_RELAY_STATUS_FROM_RATE_TABLE_CONTROL_FLAG
     */
    public boolean getLOAD_CONTROL_RELAY_STATUS_FROM_RATE_TABLE_CONTROL_FLAG() {
        int flag =(int)(data&LOAD_CONTROL_RELAY_STATUS_FROM_RATE_TABLE_CONTROL_FLAG);
        if (flag != 0){
            return true;
        }
        return false;
    }
    /**
     * METER_TIME_BASE
     */
    public boolean getMETER_TIME_BASE() {
        int flag =(int)(data&METER_TIME_BASE);
        if (flag != 0){
            return true;
        }
        return false;
    }
    /**
     * PROGRAMMED_AC_LINE_FREQUENCY_INDICATOR
     */
    public boolean getPROGRAMMED_AC_LINE_FREQUENCY_INDICATOR() {
        int flag =(int)(data&PROGRAMMED_AC_LINE_FREQUENCY_INDICATOR);
        if (flag != 0){
            return true;
        }
        return false;
    }
    /**
     * PHASE_C_POTENTIAL
     */
    public boolean getPHASE_C_POTENTIAL() {
        int flag =(int)(data&PHASE_C_POTENTIAL);
        if (flag != 0){
            return true;
        }
        return false;
    }
    /**
     * PHASE_B_POTENTIAL
     */
    public boolean getPHASE_B_POTENTIAL() {
        int flag =(int)(data&PHASE_B_POTENTIAL);
        if (flag != 0){
            return true;
        }
        return false;
    }
    /**
     * PHASE_A_POTENTIAL
     */
    public boolean getPHASE_A_POTENTIAL() {
        int flag =(int)(data&PHASE_A_POTENTIAL);
        if (flag != 0){
            return true;
        }
        return false;
    }
    public String getLog()
    {
        StringBuffer sb = new StringBuffer();
        try{   
            if(getTEST_MODE_STATUS())
                sb.append("<dt>TEST_MODE_STATUS</dt>");
            if(getLOAD_CONTROL_RELAY_STATUS_FROM_THRESHOLD_CONTROL_FLAG())
                sb.append("<dt>LOAD_CONTROL_RELAY_STATUS_FROM_THRESHOLD_CONTROL_FLAG</dt>");
            if(getLOAD_CONTROL_RELAY_STATUS_FROM_RATE_TABLE_CONTROL_FLAG())
                sb.append("<dt>LOAD_CONTROL_RELAY_STATUS_FROM_RATE_TABLE_CONTROL_FLAG</dt>");
            if(getMETER_TIME_BASE())
                sb.append("<dt>METER_TIME_BASE</dt>");
            if(getPROGRAMMED_AC_LINE_FREQUENCY_INDICATOR())
                sb.append("<dt>PROGRAMMED_AC_LINE_FREQUENCY_INDICATOR</dt>");
            if(getPHASE_C_POTENTIAL())
                sb.append("<dt>PHASE_C_POTENTIAL</dt>");
            if(getPHASE_B_POTENTIAL())
                sb.append("<dt>PHASE_B_POTENTIAL</dt>");
            if(getPHASE_A_POTENTIAL())
                sb.append("<dt>PHASE_A_POTENTIAL</dt>");
        }catch(Exception e){
            log.error("MeterStatusFlag TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("MeterStatusFlag DATA[");        
            sb.append("(TEST_MODE_STATUS=").append(""+getTEST_MODE_STATUS()).append("),");
            sb.append("(LOAD_CONTROL_RELAY_STATUS_FROM_THRESHOLD_CONTROL_FLAG=").append(""+getLOAD_CONTROL_RELAY_STATUS_FROM_THRESHOLD_CONTROL_FLAG()).append("),");
            sb.append("(LOAD_CONTROL_RELAY_STATUS_FROM_RATE_TABLE_CONTROL_FLAG=").append(""+getLOAD_CONTROL_RELAY_STATUS_FROM_RATE_TABLE_CONTROL_FLAG()).append("),");
            sb.append("(METER_TIME_BASE=").append(""+getMETER_TIME_BASE()).append("),");
            sb.append("(PROGRAMMED_AC_LINE_FREQUENCY_INDICATOR=").append(""+getPROGRAMMED_AC_LINE_FREQUENCY_INDICATOR()).append("),");
            sb.append("(PHASE_C_POTENTIAL=").append(""+getPHASE_C_POTENTIAL()).append("),");
            sb.append("(PHASE_B_POTENTIAL=").append(""+getPHASE_B_POTENTIAL()).append("),");
            sb.append("(PHASE_A_POTENTIAL=").append(""+getPHASE_A_POTENTIAL()).append(')');
            sb.append("]\n");
        }catch(Exception e){
            log.warn("MeterStatusFlag TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }
}
