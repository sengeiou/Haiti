/** 
 * @(#)MeterErrorFlag.java       1.0 08/10/27 *
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

import com.aimir.fep.util.DataFormat;

/**
 * @author Kang SoYi ksoyi@nuritelecom.com
 */
public class MeterErrorFlag {
	
    public static final byte EEPROM_CLASS0_CHECKSUM_ERROR  = (byte)0x000001;
    public static final byte EEPROM_CLASS1_CHECKSUM_ERROR  = (byte)0x000002;
    public static final byte EEPROM_CLASS2_CHECKSUM_ERROR  = (byte)0x000004;
    public static final byte EEPROM_CLASS3_CHECKSUM_ERROR  = (byte)0x000008;
    public static final byte EEPROM_CLASS4_CHECKSUM_ERROR  = (byte)0x000010;
     
    public static final byte EEPROM_CLASS19_CHECKSUM_ERROR  = (byte)0x000100;
    public static final byte EEPROM_CLASS20_CHECKSUM_ERROR  = (byte)0x000200;
    public static final byte EEPROM_CLASS21_CHECKSUM_ERROR  = (byte)0x000400;
    public static final byte EEPROM_CLASS22_CHECKSUM_ERROR  = (byte)0x000800;
    
    public static final byte POWER_FAIL_CHECKSUM_ERROR  	= (byte)0x002000;
    public static final byte EEPROM_CLASS14_15_CHECKSUM_ERROR=(byte)0x008000;
    
    public static final byte PERSONALITY_CHECKSUM_ERROR  	= (byte)0x010000;
    public static final byte CONF_CLASS_CHECKSUM_ERROR  	= (byte)0x020000;
    public static final byte OSCILLATOR_ERROR  				= (byte)0x040000;
    public static final byte TOU_CARRY_OVER  				= (byte)0x080000;
    public static final byte INTERNAL_METER_COMM_BUS_ERROR	= (byte)0x100000;
    public static final byte WARNING_TREATED_AS_ERROR_FLAG	= (byte)0x800000;
    
    
	private byte[] data;
    private Log log = LogFactory.getLog(MeterErrorFlag.class);
	
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public MeterErrorFlag(byte[] data) {
		this.data = data;
	}
	
	 /**
     * EEPROM_CLASS0_CHECKSUM_ERROR
     */
    public boolean getEEPROM_CLASS0_CHECKSUM_ERROR() throws Exception{
        int flag =(int)(DataFormat.hex2long(data) &EEPROM_CLASS0_CHECKSUM_ERROR);
        if (flag != 0){
            return true;
        }
        return false;
    }	
    /**
     * EEPROM_CLASS1_CHECKSUM_ERROR
     */
    public boolean getEEPROM_CLASS1_CHECKSUM_ERROR() throws Exception{
        int flag =(int)(DataFormat.hex2long(data) &EEPROM_CLASS1_CHECKSUM_ERROR);
        if (flag != 0){
            return true;
        }
        return false;
    }
    /**
     * EEPROM_CLASS2_CHECKSUM_ERROR
     */
    public boolean getEEPROM_CLASS2_CHECKSUM_ERROR() throws Exception{
        int flag =(int)(DataFormat.hex2long(data) &EEPROM_CLASS2_CHECKSUM_ERROR);
        if (flag != 0){
            return true;
        }
        return false;
    }
    /**
     * EEPROM_CLASS3_CHECKSUM_ERROR
     */
    public boolean getEEPROM_CLASS3_CHECKSUM_ERROR() throws Exception{
        int flag =(int)(DataFormat.hex2long(data) &EEPROM_CLASS3_CHECKSUM_ERROR);
        if (flag != 0){
            return true;
        }
        return false;
    }
    /**
     * EEPROM_CLASS4_CHECKSUM_ERROR
     */
    public boolean getEEPROM_CLASS4_CHECKSUM_ERROR() throws Exception{
        int flag =(int)(DataFormat.hex2long(data) &EEPROM_CLASS4_CHECKSUM_ERROR);
        if (flag != 0){
            return true;
        }
        return false;
    }
    /**
     * EEPROM_CLASS19_CHECKSUM_ERROR
     */
    public boolean getEEPROM_CLASS19_CHECKSUM_ERROR() throws Exception{
        int flag =(int)(DataFormat.hex2long(data) &EEPROM_CLASS19_CHECKSUM_ERROR);
        if (flag != 0){
            return true;
        }
        return false;
    }
    /**
     * EEPROM_CLASS20_CHECKSUM_ERROR
     */
    public boolean getEEPROM_CLASS20_CHECKSUM_ERROR() throws Exception{
        int flag =(int)(DataFormat.hex2long(data) &EEPROM_CLASS20_CHECKSUM_ERROR);
        if (flag != 0){
            return true;
        }
        return false;
    }
    
    /**
     * EEPROM_CLASS21_CHECKSUM_ERROR
     */
    public boolean getEEPROM_CLASS21_CHECKSUM_ERROR() throws Exception{
        int flag =(int)(DataFormat.hex2long(data) &EEPROM_CLASS21_CHECKSUM_ERROR);
        if (flag != 0){
            return true;
        }
        return false;
    }
    /**
     * EEPROM_CLASS22_CHECKSUM_ERROR
     */
    public boolean getEEPROM_CLASS22_CHECKSUM_ERROR() throws Exception{
        int flag =(int)(DataFormat.hex2long(data) &EEPROM_CLASS22_CHECKSUM_ERROR);
        if (flag != 0){
            return true;
        }
        return false;
    }
    /**
     * POWER_FAIL_CHECKSUM_ERROR
     */
    public boolean getPOWER_FAIL_CHECKSUM_ERROR() throws Exception{
        int flag =(int)(DataFormat.hex2long(data) &POWER_FAIL_CHECKSUM_ERROR);
        if (flag != 0){
            return true;
        }
        return false;
    }
    /**
     * EEPROM_CLASS14_15_CHECKSUM_ERROR
     */
    public boolean getEEPROM_CLASS14_15_CHECKSUM_ERROR() throws Exception{
        int flag =(int)(DataFormat.hex2long(data) &EEPROM_CLASS14_15_CHECKSUM_ERROR);
        if (flag != 0){
            return true;
        }
        return false;
    }
    /**
     * PERSONALITY_CHECKSUM_ERROR
     */
    public boolean getPERSONALITY_CHECKSUM_ERROR() throws Exception{
        int flag =(int)(DataFormat.hex2long(data) &PERSONALITY_CHECKSUM_ERROR);
        if (flag != 0){
            return true;
        }
        return false;
    }
    /**
     * CONF_CLASS_CHECKSUM_ERROR
     */
    public boolean getCONF_CLASS_CHECKSUM_ERROR() throws Exception{
        int flag =(int)(DataFormat.hex2long(data) &CONF_CLASS_CHECKSUM_ERROR);
        if (flag != 0){
            return true;
        }
        return false;
    }
    /**
     * OSCILLATOR_ERROR
     */
    public boolean getOSCILLATOR_ERROR() throws Exception{
        int flag =(int)(DataFormat.hex2long(data) &OSCILLATOR_ERROR);
        if (flag != 0){
            return true;
        }
        return false;
    }
    /**
     * TOU_CARRY_OVER
     */
    public boolean getTOU_CARRY_OVER() throws Exception{
        int flag =(int)(DataFormat.hex2long(data) &TOU_CARRY_OVER);
        if (flag != 0){
            return true;
        }
        return false;
    }
    /**
     * INTERNAL_METER_COMM_BUS_ERROR
     */
    public boolean getINTERNAL_METER_COMM_BUS_ERROR() throws Exception{
        int flag =(int)(DataFormat.hex2long(data) &INTERNAL_METER_COMM_BUS_ERROR);
        if (flag != 0){
            return true;
        }
        return false;
    }
    /**
     * WARNING_TREATED_AS_ERROR_FLAG
     */
    public boolean getWARNING_TREATED_AS_ERROR_FLAG() throws Exception {
        int flag =(int)(DataFormat.hex2long(data) &WARNING_TREATED_AS_ERROR_FLAG);
        if (flag != 0){
            return true;
        }
        return false;
    }
    
    public String getLog()
    {
        StringBuffer sb = new StringBuffer();
        try{   
            if(getEEPROM_CLASS0_CHECKSUM_ERROR())
                sb.append("<dt>EEPROM_CLASS0_CHECKSUM_ERROR ERROR</dt>");
            if(getEEPROM_CLASS1_CHECKSUM_ERROR())
                sb.append("<dt>EEPROM_CLASS1_CHECKSUM_ERROR ERROR</dt>");
            if(getEEPROM_CLASS2_CHECKSUM_ERROR())
                sb.append("<dt>EEPROM_CLASS2_CHECKSUM_ERROR ERROR</dt>");
            if(getEEPROM_CLASS3_CHECKSUM_ERROR())
                sb.append("<dt>EEPROM_CLASS3_CHECKSUM_ERROR ERROR</dt>");
            if(getEEPROM_CLASS4_CHECKSUM_ERROR())
                sb.append("<dt>EEPROM_CLASS4_CHECKSUM_ERROR ERROR</dt>");
            if(getEEPROM_CLASS19_CHECKSUM_ERROR())
                sb.append("<dt>EEPROM_CLASS19_CHECKSUM_ERROR ERROR</dt>");
            if(getEEPROM_CLASS20_CHECKSUM_ERROR())
                sb.append("<dt>EEPROM_CLASS20_CHECKSUM_ERROR ERROR</dt>");
            if(getEEPROM_CLASS21_CHECKSUM_ERROR())
                sb.append("<dt>EEPROM_CLASS21_CHECKSUM_ERROR ERROR</dt>");
            if(getEEPROM_CLASS22_CHECKSUM_ERROR())
                sb.append("<dt>EEPROM_CLASS22_CHECKSUM_ERROR ERROR</dt>");
            if(getPOWER_FAIL_CHECKSUM_ERROR())
                sb.append("<dt>POWER_FAIL_CHECKSUM_ERROR ERROR</dt>");
            if(getEEPROM_CLASS14_15_CHECKSUM_ERROR())
                sb.append("<dt>EEPROM_CLASS14_15_CHECKSUM_ERROR ERROR</dt>");
            if(getPERSONALITY_CHECKSUM_ERROR())
                sb.append("<dt>PERSONALITY_CHECKSUM_ERROR ERROR</dt>");
            if(getCONF_CLASS_CHECKSUM_ERROR())
                sb.append("<dt>CONF_CLASS_CHECKSUM_ERROR ERROR</dt>");
            if(getOSCILLATOR_ERROR())
                sb.append("<dt>OSCILLATOR_ERROR ERROR</dt>");
            if(getTOU_CARRY_OVER())
                sb.append("<dt>TOU_CARRY_OVER ERROR</dt>");
            if(getINTERNAL_METER_COMM_BUS_ERROR())
                sb.append("<dt>INTERNAL_METER_COMM_BUS_ERROR ERROR</dt>");
            if(getWARNING_TREATED_AS_ERROR_FLAG())
                sb.append("<dt>WARNING_TREATED_AS_ERROR_FLAG ERROR</dt>");
        }catch(Exception e){
            log.warn("MeterErrorFlag TO STRING ERR=>"+e.getMessage());
        }
        return sb.toString();
    }
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("MeterErrorFlag DATA[");        
            sb.append("(EEPROM_CLASS0_CHECKSUM_ERROR=").append(""+getEEPROM_CLASS0_CHECKSUM_ERROR()).append("),");
            sb.append("(EEPROM_CLASS1_CHECKSUM_ERROR=").append(""+getEEPROM_CLASS1_CHECKSUM_ERROR()).append("),");
            sb.append("(EEPROM_CLASS2_CHECKSUM_ERROR=").append(""+getEEPROM_CLASS2_CHECKSUM_ERROR()).append("),");
            sb.append("(EEPROM_CLASS3_CHECKSUM_ERROR=").append(""+getEEPROM_CLASS3_CHECKSUM_ERROR()).append("),");
            sb.append("(EEPROM_CLASS4_CHECKSUM_ERROR=").append(""+getEEPROM_CLASS4_CHECKSUM_ERROR()).append("),");
            sb.append("(EEPROM_CLASS19_CHECKSUM_ERROR=").append(""+getEEPROM_CLASS19_CHECKSUM_ERROR()).append("),");
            sb.append("(EEPROM_CLASS20_CHECKSUM_ERROR=").append(""+getEEPROM_CLASS20_CHECKSUM_ERROR()).append("),");
            sb.append("(EEPROM_CLASS21_CHECKSUM_ERROR=").append(""+getEEPROM_CLASS21_CHECKSUM_ERROR()).append("),");
            sb.append("(EEPROM_CLASS22_CHECKSUM_ERROR=").append(""+getEEPROM_CLASS22_CHECKSUM_ERROR()).append("),");
            sb.append("(POWER_FAIL_CHECKSUM_ERROR=").append(""+getPOWER_FAIL_CHECKSUM_ERROR()).append("),");
            sb.append("(EEPROM_CLASS14_15_CHECKSUM_ERROR=").append(""+getEEPROM_CLASS14_15_CHECKSUM_ERROR()).append("),");
            sb.append("(PERSONALITY_CHECKSUM_ERROR=").append(""+getPERSONALITY_CHECKSUM_ERROR()).append("),");
            sb.append("(CONF_CLASS_CHECKSUM_ERROR=").append(""+getCONF_CLASS_CHECKSUM_ERROR()).append("),");
            sb.append("(OSCILLATOR_ERROR=").append(""+getOSCILLATOR_ERROR()).append("),");
            sb.append("(TOU_CARRY_OVER=").append(""+getTOU_CARRY_OVER()).append("),");
            sb.append("(INTERNAL_METER_COMM_BUS_ERROR=").append(""+getINTERNAL_METER_COMM_BUS_ERROR()).append("),");
            sb.append("(WARNING_TREATED_AS_ERROR_FLAG=").append(""+getWARNING_TREATED_AS_ERROR_FLAG()).append(')');
            sb.append("]\n");
        }catch(Exception e){
            log.warn("MeterErrorFlag TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }
}
