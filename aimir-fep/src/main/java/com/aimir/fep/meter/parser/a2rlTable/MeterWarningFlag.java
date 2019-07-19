/** 
 * @(#)MeterWarningFlag.java       1.0 08/10/27 *
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
public class MeterWarningFlag {
	
    public static final byte REVERSE_METER_PULSE  	= (byte)0x01;
    public static final byte DEMAND_OVERLOAD_VALUE  = (byte)0x02;
    public static final byte LOW_BATTERY_VOLTAGE	= (byte)0x04;
    public static final byte PHASE_POTENTIAL_MISSING= (byte)0x08;
    public static final byte INTERNAL_COMM_FAIL 	= (byte)0x10;
    public static final byte SERVICE_BG_TEST_FAIL 	= (byte)0x20;
    public static final byte CHECKSUM_ERROR			= (byte)0x40;

	private byte data;
    private Log log = LogFactory.getLog(MeterWarningFlag.class);
	
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public MeterWarningFlag(byte data) {
		this.data = data;
	}
	
    /**
     * REVERSE_METER_PULSE
     */
    public boolean getREVERSE_METER_PULSE() {
        int flag =(int)(data&REVERSE_METER_PULSE);
        if (flag != 0){
            return true;
        }
        return false;
    }	
	
    /**
     * DEMAND_OVERLOAD_VALUE
     */
    public boolean getDEMAND_OVERLOAD_VALUE() {
        int flag =(int)(data&DEMAND_OVERLOAD_VALUE);
        if (flag != 0){
            return true;
        }
        return false;
    }
    
    /**
     * LOW_BATTERY_VOLTAGE
     */
    public boolean getLOW_BATTERY_VOLTAGE() {
        int flag =(int)(data&LOW_BATTERY_VOLTAGE);
        if (flag != 0){
            return true;
        }
        return false;
    }
    
    /**
     * PHASE_POTENTIAL_MISSING
     */
    public boolean getPHASE_POTENTIAL_MISSING() {
        int flag =(int)(data&PHASE_POTENTIAL_MISSING);
        if (flag != 0){
            return true;
        }
        return false;
    }
    
    /**
     * INTERNAL_COMM_FAIL
     */
    public boolean getINTERNAL_COMM_FAIL() {
        int flag =(int)(data&INTERNAL_COMM_FAIL);
        if (flag != 0){
            return true;
        }
        return false;
    }
    
    /**
     * SERVICE_BG_TEST_FAIL
     */
    public boolean getSERVICE_BG_TEST_FAIL() {
        int flag =(int)(data&SERVICE_BG_TEST_FAIL);
        if (flag != 0){
            return true;
        }
        return false;
    }
    
    /**
     * CHECKSUM_ERROR
     */
    public boolean getCHECKSUM_ERROR() {
        int flag =(int)(data&CHECKSUM_ERROR);
        if (flag != 0){
            return true;
        }
        return false;
    }
    
    public String getLog()
    {
        StringBuffer sb = new StringBuffer();
        try{   
            if(getREVERSE_METER_PULSE())
                sb.append("<dt>REVERSE_METER_PULSE WARNING</dt>");
            if(getDEMAND_OVERLOAD_VALUE())
                sb.append("<dt>DEMAND_OVERLOAD_VALUE WARNING</dt>");
            if(getLOW_BATTERY_VOLTAGE())
                sb.append("<dt>LOW_BATTERY_VOLTAGE WARNING</dt>");
            if(getPHASE_POTENTIAL_MISSING())
                sb.append("<dt>PHASE_POTENTIAL_MISSING WARNING</dt>");
            if(getINTERNAL_COMM_FAIL())
                sb.append("<dt>INTERNAL_COMM_FAIL WARNING</dt>");
            if(getSERVICE_BG_TEST_FAIL())
                sb.append("<dt>SERVICE_TEST_OR_Background_TEST_FAIL WARNING</dt>");
            if(getCHECKSUM_ERROR())
                sb.append("<dt>CHECKSUM_ERROR WARNING</dt>");
        }catch(Exception e){
            log.error("MeterWarningFlag TO STRING ERR=>"+e.getMessage());
        }
        return sb.toString();
    }
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("MeterWarningFlag DATA[");        
            sb.append("(REVERSE_METER_PULSE=").append(""+getREVERSE_METER_PULSE()).append("),");
            sb.append("(DEMAND_OVERLOAD_VALUE=").append(""+getDEMAND_OVERLOAD_VALUE()).append("),");
            sb.append("(LOW_BATTERY_VOLTAGE=").append(""+getLOW_BATTERY_VOLTAGE()).append("),");
            sb.append("(PHASE_POTENTIAL_MISSING=").append(""+getPHASE_POTENTIAL_MISSING()).append("),");
            sb.append("(INTERNAL_COMM_FAIL=").append(""+getINTERNAL_COMM_FAIL()).append("),");
            sb.append("(SERVICE_TEST_OR_Background_TEST_FAIL=").append(""+getSERVICE_BG_TEST_FAIL()).append("),");
            sb.append("(CHECKSUM_ERROR=").append(""+getCHECKSUM_ERROR()).append(')');
            sb.append("]\n");
        }catch(Exception e){
            log.warn("MeterWarningFlag TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }
}
