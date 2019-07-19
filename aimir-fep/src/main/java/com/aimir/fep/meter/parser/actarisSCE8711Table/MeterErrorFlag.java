/** 
 * @(#)MeterErrorFlag.java       1.0 09/03/17 *
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
 
package com.aimir.fep.meter.parser.actarisSCE8711Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Kang, SoYi ksoyi@nuritelecom.com
 */
public class MeterErrorFlag {
	
    public static final int OFS_TIMEBASE_ERROR  	= 0;
    public static final int OFS_READ_WRITE_ERROR  	= 1;
    public static final int OFS_CHECKSUM_ERROR  	= 2;
    public static final int OFS_OTHER_ERROR		 	= 3;
	
    //OTHER ERROR
    public static final byte MEASURING_SYSTEM_ERROR 	= (byte)0x02;
    public static final byte PARAMETERIZATION_INCOMPLETE= (byte)0x04;
    public static final byte SET_MODE_NOT_COMPLETED		= (byte)0x08;
    public static final byte MICROPROCESSOR_SYSTEM_ERROR= (byte)0x10;
    public static final byte COMMUNICATION_BLOCKED		= (byte)0x20;
    public static final byte EEPROM_ID_INVALID			= (byte)0x40;
    public static final byte EXT_BOARD_ID_INVLAID		= (byte)0x80;
    
    //CHECKSUM-ERROR
    public static final byte ROM_MICROPROCESSOR_CHECKSUM= (byte)0x01;
    public static final byte EEPROM_BACKUPDATA_CHECKSUM	= (byte)0x02;
    public static final byte EEPROM_PARAMETER_CHECKSUM	= (byte)0x04;
    public static final byte EEPROM_DATA_PROFILE_CHECKSUM= (byte)0x08;

    //READ-WRITE ERROR
    public static final byte MAIN_MEMORY				= (byte)0x01;
    public static final byte BACKUP_PARAMETER_MEMORY	= (byte)0x02;
    public static final byte MEASURING_SYSTEM			= (byte)0x04;
    public static final byte TIME_BASE_READWRITE		= (byte)0x08;
    public static final byte DATA_PROFILE_MEMORY		= (byte)0x10;
    public static final byte RIPPLE_CONTROL_RECEIVER 	= (byte)0x20;
    public static final byte COMMINUCATION_UTIL		 	= (byte)0x40;
    public static final byte DISPLAY_CARD			 	= (byte)0x80;
    
    //TIMEBASED ERROR
    public static final byte INSUFFICIENT_BATTERY_VOLT	= (byte)0x01;
    public static final byte INVALID_TIME_OR_DATE		= (byte)0x02;
    
	private byte[] data;
	private byte otherError;
	private byte checksumError;
	private byte readwriteError;
	private byte timebaseError;
	
    private Log log = LogFactory.getLog(MeterErrorFlag.class);
	
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public MeterErrorFlag(byte[] data) {
		this.data = data;
		otherError     = data[OFS_OTHER_ERROR];
		checksumError  = data[OFS_CHECKSUM_ERROR];
		readwriteError = data[OFS_READ_WRITE_ERROR];
		timebaseError  = data[OFS_TIMEBASE_ERROR];
	}

    /**
     * MEASURING_SYSTEM_ERROR
     */
    public boolean getMEASURING_SYSTEM_ERROR() {
        int flag =(int)(otherError&MEASURING_SYSTEM_ERROR);
        if (flag !=0)
            return true;
        return false;
    }

    /**
     * PARAMETERIZATION_INCOMPLETE
     */
    public boolean getPARAMETERIZATION_INCOMPLETE() {
        int flag =(int)(otherError&PARAMETERIZATION_INCOMPLETE);
        if (flag !=0)
            return true;
        return false;
    }
    
    /**
     * SET_MODE_NOT_COMPLETED
     */
    public boolean getSET_MODE_NOT_COMPLETED() {
        int flag =(int)(otherError&SET_MODE_NOT_COMPLETED);
        if (flag !=0)
            return true;
        return false;
    }
    
    /**
     * MICROPROCESSOR_SYSTEM_ERROR
     */
    public boolean getMICROPROCESSOR_SYSTEM_ERROR() {
        int flag =(int)(otherError&MICROPROCESSOR_SYSTEM_ERROR);
        if (flag !=0)
            return true;
        return false;
    }
    
    /**
     * COMMUNICATION_BLOCKED
     */
    public boolean getCOMMUNICATION_BLOCKED() {
        int flag =(int)(otherError&COMMUNICATION_BLOCKED);
        if (flag !=0)
            return true;
        return false;
    }
    
    /**
     * EEPROM_ID_INVALID
     */
    public boolean getEEPROM_ID_INVALID() {
        int flag =(int)(otherError&EEPROM_ID_INVALID);
        if (flag !=0)
            return true;
        return false;
    }
    
    /**
     * EXT_BOARD_ID_INVLAID
     */
    public boolean getEXT_BOARD_ID_INVLAID() {
        int flag =(int)(otherError&EXT_BOARD_ID_INVLAID);
        if (flag !=0)
            return true;
        return false;
    }
    
    /**
     * ROM_MICROPROCESSOR_CHECKSUM
     */
    public boolean getROM_MICROPROCESSOR_CHECKSUM() {
        int flag =(int)(checksumError&ROM_MICROPROCESSOR_CHECKSUM);
        if (flag !=0)
            return true;
        return false;
    }
    
    /**
     * EEPROM_BACKUPDATA_CHECKSUM
     */
    public boolean getEEPROM_BACKUPDATA_CHECKSUM() {
        int flag =(int)(checksumError&EEPROM_BACKUPDATA_CHECKSUM);
        if (flag !=0)
            return true;
        return false;
    }
    
    /**
     * EEPROM_PARAMETER_CHECKSUM
     */
    public boolean getEEPROM_PARAMETER_CHECKSUM() {
        int flag =(int)(checksumError&EEPROM_PARAMETER_CHECKSUM);
        if (flag !=0)
            return true;
        return false;
    }
    
    /**
     * EEPROM_DATA_PROFILE_CHECKSUM
     */
    public boolean getEEPROM_DATA_PROFILE_CHECKSUM() {
        int flag =(int)(checksumError&EEPROM_DATA_PROFILE_CHECKSUM);
        if (flag !=0)
            return true;
        return false;
    }
    
    /**
     * MAIN_MEMORY
     */
    public boolean getMAIN_MEMORY() {
        int flag =(int)(readwriteError&MAIN_MEMORY);
        if (flag !=0)
            return true;
        return false;
    }
    
    /**
     * BACKUP_PARAMETER_MEMORY
     */
    public boolean getBACKUP_PARAMETER_MEMORY() {
        int flag =(int)(readwriteError&BACKUP_PARAMETER_MEMORY);
        if (flag !=0)
            return true;
        return false;
    }
    
    /**
     * MEASURING_SYSTEM
     */
    public boolean getMEASURING_SYSTEM() {
        int flag =(int)(readwriteError&MEASURING_SYSTEM);
        if (flag !=0)
            return true;
        return false;
    }
    
    /**
     * TIME_BASE_READWRITE
     */
    public boolean getTIME_BASE_READWRITE() {
        int flag =(int)(readwriteError&TIME_BASE_READWRITE);
        if (flag !=0)
            return true;
        return false;
    }
    
    /**
     * DATA_PROFILE_MEMORY
     */
    public boolean getDATA_PROFILE_MEMORY() {
        int flag =(int)(readwriteError&DATA_PROFILE_MEMORY);
        if (flag !=0)
            return true;
        return false;
    }
    
    /**
     * RIPPLE_CONTROL_RECEIVER
     */
    public boolean getRIPPLE_CONTROL_RECEIVER() {
        int flag =(int)(readwriteError&RIPPLE_CONTROL_RECEIVER);
        if (flag !=0)
            return true;
        return false;
    }
    
    /**
     * COMMINUCATION_UTIL
     */
    public boolean getCOMMINUCATION_UTIL() {
        int flag =(int)(readwriteError&COMMINUCATION_UTIL);
        if (flag !=0)
            return true;
        return false;
    }
   
    /**
     * DISPLAY_CARD
     */
    public boolean getDISPLAY_CARD() {
        int flag =(int)(readwriteError&DISPLAY_CARD);
        if (flag !=0)
            return true;
        return false;
    }
    
    /**
     * INSUFFICIENT_BATTERY_VOLT
     */
    public boolean getINSUFFICIENT_BATTERY_VOLT() {
        int flag =(int)(timebaseError&INSUFFICIENT_BATTERY_VOLT);
        if (flag !=0)
            return true;
        return false;
    }    
    /**
     * INVALID_TIME_OR_DATE
     */
    public boolean getINVALID_TIME_OR_DATE() {
        int flag =(int)(timebaseError&INVALID_TIME_OR_DATE);
        if (flag !=0)
            return true;
        return false;
    }    
        
    public String getLog()
    {
        StringBuffer sb = new StringBuffer();
        try{   
            if(getMEASURING_SYSTEM_ERROR())
                sb.append("<dt>MEASURING_SYSTEM_ERROR</dt>");
            if(getPARAMETERIZATION_INCOMPLETE())
                sb.append("<dt>PARAMETERIZATION_INCOMPLETE</dt>");
            if(getSET_MODE_NOT_COMPLETED())
                sb.append("<dt>SET_MODE_NOT_COMPLETED</dt>");
            if(getMICROPROCESSOR_SYSTEM_ERROR())
                sb.append("<dt>MICROPROCESSOR_SYSTEM_ERROR</dt>");
            if(getCOMMUNICATION_BLOCKED())
                sb.append("<dt>COMMUNICATION_BLOCKED</dt>");
            if(getEEPROM_ID_INVALID())
                sb.append("<dt>EEPROM_ID_INVALID</dt>");
            if(getEXT_BOARD_ID_INVLAID())
                sb.append("<dt>EXT_BOARD_ID_INVLAID</dt>");
            
            if(getROM_MICROPROCESSOR_CHECKSUM())
                sb.append("<dt>ROM_MICROPROCESSOR_CHECKSUM_ERROR</dt>");
            if(getEEPROM_BACKUPDATA_CHECKSUM())
                sb.append("<dt>EEPROM_BACKUPDATA_CHECKSUM_ERROR</dt>");
            if(getEEPROM_PARAMETER_CHECKSUM())
                sb.append("<dt>EEPROM_PARAMETER_CHECKSUM_ERROR</dt>");
            if(getEEPROM_DATA_PROFILE_CHECKSUM())
                sb.append("<dt>EEPROM_DATA_PROFILE_CHECKSUM_ERROR</dt>");
            
            if(getMAIN_MEMORY())
                sb.append("<dt>MAIN_MEMORY_READWRITE_ERROR</dt>");
            if(getBACKUP_PARAMETER_MEMORY())
                sb.append("<dt>BACKUP_PARAMETER_MEMORY_READWRITE_ERROR</dt>");
            if(getMEASURING_SYSTEM())
                sb.append("<dt>MEASURING_SYSTEM_READWRITE_ERROR</dt>");
            if(getTIME_BASE_READWRITE())
                sb.append("<dt>TIME_BASE_READWRITE_ERROR</dt>");
            if(getDATA_PROFILE_MEMORY())
                sb.append("<dt>DATA_PROFILE_MEMORY_READWRITE_ERROR</dt>");
            if(getRIPPLE_CONTROL_RECEIVER())
                sb.append("<dt>RIPPLE_CONTROL_RECEIVER_READWRITE_ERROR</dt>");
            if(getCOMMINUCATION_UTIL())
                sb.append("<dt>COMMINUCATION_UTIL_READWRITE_ERROR</dt>");
            if(getDISPLAY_CARD())
                sb.append("<dt>DISPLAY_CARD_READWRITE_ERROR</dt>");
            
            if(getINSUFFICIENT_BATTERY_VOLT())
                sb.append("<dt>INSUFFICIENT_BATTERY_VOLT</dt>");
            if(getINVALID_TIME_OR_DATE())
                sb.append("<dt>INVALID_TIME_OR_DATE</dt>");           
            
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
            sb.append("(MEASURING_SYSTEM_ERROR=").append(""+getMEASURING_SYSTEM_ERROR()).append("),");
            sb.append("(PARAMETERIZATION_INCOMPLETE=").append(""+getPARAMETERIZATION_INCOMPLETE()).append("),");
            sb.append("(SET_MODE_NOT_COMPLETED=").append(""+getSET_MODE_NOT_COMPLETED()).append("),");
            sb.append("(MICROPROCESSOR_SYSTEM_ERROR=").append(""+getMICROPROCESSOR_SYSTEM_ERROR()).append("),");
            sb.append("(COMMUNICATION_BLOCKED=").append(""+getCOMMUNICATION_BLOCKED()).append("),");
            sb.append("(EEPROM_ID_INVALID=").append(""+getEEPROM_ID_INVALID()).append("),");
            sb.append("(EXT_BOARD_ID_INVLAID=").append(""+getEXT_BOARD_ID_INVLAID()).append("),");
            
            sb.append("(ROM_MICROPROCESSOR_CHECKSUM_ERROR=").append(""+getROM_MICROPROCESSOR_CHECKSUM()).append("),");
            sb.append("(EEPROM_BACKUPDATA_CHECKSUM_ERROR=").append(""+getEEPROM_BACKUPDATA_CHECKSUM()).append("),");
            sb.append("(EEPROM_PARAMETER_CHECKSUM_ERROR=").append(""+getEEPROM_PARAMETER_CHECKSUM()).append("),");
            sb.append("(EEPROM_DATA_PROFILE_CHECKSUM_ERROR=").append(""+getEEPROM_DATA_PROFILE_CHECKSUM()).append("),");
            
            sb.append("(MAIN_MEMORY_READWRITE_ERROR=").append(""+getMAIN_MEMORY()).append("),");
            sb.append("(BACKUP_PARAMETER_MEMORY_READWRITE_ERROR=").append(""+getBACKUP_PARAMETER_MEMORY()).append("),");
            sb.append("(MEASURING_SYSTEM_READWRITE_ERROR=").append(""+getMEASURING_SYSTEM()).append("),");
            sb.append("(TIME_BASE_READWRITE_ERROR=").append(""+getTIME_BASE_READWRITE()).append("),");            
            sb.append("(DATA_PROFILE_MEMORY_READWRITE_ERROR=").append(""+getDATA_PROFILE_MEMORY()).append("),");
            sb.append("(RIPPLE_CONTROL_RECEIVER_READWRITE_ERROR=").append(""+getRIPPLE_CONTROL_RECEIVER()).append("),");
            sb.append("(COMMINUCATION_UTIL_READWRITE_ERROR=").append(""+getCOMMINUCATION_UTIL()).append("),");
            sb.append("(DISPLAY_CARD_READWRITE_ERROR=").append(""+getDISPLAY_CARD()).append("),");
            
            sb.append("(INSUFFICIENT_BATTERY_VOLT=").append(""+getINSUFFICIENT_BATTERY_VOLT()).append("),");
            sb.append("(INVALID_TIME_OR_DATE=").append(""+getINVALID_TIME_OR_DATE()).append(')');
            
            sb.append("]\n");
        }catch(Exception e){
            log.warn("MeterErrorFlag TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }
}
