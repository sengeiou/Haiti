/**
 * @(#)StatusFlag.java       1.0 2008/08/11 *
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

package com.aimir.fep.meter.parser.Mk6NTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;

/**
 * @author kaze kaze@nuritelecom.com
 */
/**
 * @author nuri
 *
 */
public class StatusFlag implements java.io.Serializable{

	private static final long serialVersionUID = -5481072874154822002L;
	public static final short AN_ERROR_OCCURRED_READING_ONE_OF_THE_REGISTERS  = (short)0x0001;
    public static final short FILLED_ENTRY_NO_DATA  = (short)0x0002;
    public static final short POWER_FAILED_DURING_INTERVAL  = (short)0x0004;
    public static final short INCOMPLETE_INTERVAL  = (short)0x0008;
    public static final short DAYLIGHT_SAVING_WAS_IN_EFFECT  = (short)0x0010;
    public static final short CALIBRATION_LOST  = (short)0x0020;
    public static final short EFA_FAILURE  = (short)0x0040;
    public static final short U_EFA_FAILURE  = (short)0x0080;
    public static final short DATA_CHECKSUM_ERROR  = (short)0x0100;

	private short shortData=0;
	private String strData="";
    private static Log log = LogFactory.getLog(StatusFlag.class);

	/**
	 * @param byteData
	 * @throws Exception
	 */
	public StatusFlag(byte[] byteData) throws Exception {
		shortData = (short)DataFormat.hex2dec(byteData);
	}

	/**
	 * @param strData
	 */
	public StatusFlag(String strData) {
		this.strData = strData;
	}

	
	/**
	 * @return
	 */
	public int getFlagNumber(){
		return shortData;
	}


    public boolean getAN_ERROR_OCCURRED_READING_ONE_OF_THE_REGISTERS() {
        short flag =(short)((shortData&AN_ERROR_OCCURRED_READING_ONE_OF_THE_REGISTERS) >> 0);
        if (flag == 1){
            return true;
        }
        return false;
	}

	public boolean getFILLED_ENTRY_NO_DATA() {
		short flag =(short)((shortData&FILLED_ENTRY_NO_DATA) >> 1);
        if (flag == 1){
            return true;
        }
        return false;
	}

	public boolean getPOWER_FAILED_DURING_INTERVAL() {
		short flag =(short)((shortData&POWER_FAILED_DURING_INTERVAL) >> 2);
        if (flag == 1){
            return true;
        }
        return false;
	}

	public boolean getINCOMPLETE_INTERVAL() {
		short flag =(short)((shortData&INCOMPLETE_INTERVAL) >> 3);
        if (flag == 1){
            return true;
        }
        return false;
	}

	public boolean getDAYLIGHT_SAVING_WAS_IN_EFFECT() {
		short flag =(short)((shortData&DAYLIGHT_SAVING_WAS_IN_EFFECT) >> 4);
        if (flag == 1){
            return true;
        }
        return false;
	}

	public boolean getCALIBRATION_LOST() {
		short flag =(short)((shortData&CALIBRATION_LOST) >> 5);
        if (flag == 1){
            return true;
        }
        return false;
	}

	public boolean getEFA_FAILURE() {
		short flag =(short)((shortData&EFA_FAILURE) >> 6);
        if (flag == 1){
            return true;
        }
        return false;
	}

	public boolean getU_EFA_FAILURE() {
		short flag =(short)((shortData&U_EFA_FAILURE) >> 7);
        if (flag == 1){
            return true;
        }
        return false;
	}

	public boolean getDATA_CHECKSUM_ERROR() {
		short flag =(short)((shortData&DATA_CHECKSUM_ERROR) >> 8);
        if (flag == 1){
            return true;
        }
        return false;
	}

	public String getLog()
    {
        StringBuffer sb = new StringBuffer();
        try{
            if(getAN_ERROR_OCCURRED_READING_ONE_OF_THE_REGISTERS() || strData.contains("K")){
                sb.append("<dt>An error occurred reading one of the registers</dt>");
            }
            if(getFILLED_ENTRY_NO_DATA() || strData.contains("A")){
            	sb.append("<dt>Filled entry - no data</dt>");
            }
            if(getPOWER_FAILED_DURING_INTERVAL() || strData.contains("P")){
            	sb.append("<dt>Power failed during interval</dt>");
            }
            if(getINCOMPLETE_INTERVAL() || strData.contains("I")){
            	sb.append("<dt>Incomplete interval</dt>");
            }
            if(getDAYLIGHT_SAVING_WAS_IN_EFFECT() || strData.contains("D")){
            	sb.append("<dt>Daylight savings was in effect</dt>");
            }
            if(getCALIBRATION_LOST() || strData.contains("L")){
            	sb.append("<dt>Calibration lost</dt>");
            }
            if(getEFA_FAILURE() || strData.contains("W")){
            	sb.append("<dt>S,V,F,R or M EFA failure (any or all). Also E flag for Mk6e</dt>");
            	if(strData.contains("S")){
            		sb.append("<dt>S - Asymmetic Power</dt>");
            	}
            	if(strData.contains("V")){
            		sb.append("<dt>V - Voltage Tolerance Error</dt>");
            	}
            	if(strData.contains("F")){
            		sb.append("<dt>F - VT Failure</dt>");
            	}
            	if(strData.contains("R")){
            		sb.append("<dt>R - Incorrect Phase Rotation</dt>");
            	}
            	if(strData.contains("M")){
            		sb.append("<dt>M - Reverse Power</dt>");
            	}
            	if(strData.contains("E")){
            		sb.append("<dt>E - Analog Reference Failure</dt>");
            	}
            }
            if(getU_EFA_FAILURE() || strData.contains("O")){
            	sb.append("<dt>U EFA failure - User flag</dt>");
            }
            if(getDATA_CHECKSUM_ERROR() || strData.contains("B")){
            	sb.append("<dt>Data checksum error - this record should be trated with caution</dt>");
            }
        }catch(Exception e){
            log.error("StatusFlag TO STRING ERR=>"+e.getMessage());
        }
        return sb.toString();
    }

	public String toString(){
		StringBuffer sb = new StringBuffer();
		try{
            sb.append("StatusFlag DATA[");
            sb.append("(AN_ERROR_OCCURRED_READING_ONE_OF_THE_REGISTERS=").append(""+getAN_ERROR_OCCURRED_READING_ONE_OF_THE_REGISTERS()).append("),");
            sb.append("(FILLED_ENTRY_NO_DATA=").append(""+getFILLED_ENTRY_NO_DATA()).append("),");
            sb.append("(POWER_FAILED_DURING_INTERVAL=").append(""+getPOWER_FAILED_DURING_INTERVAL()).append("),");
            sb.append("(INCOMPLETE_INTERVAL=").append(""+getINCOMPLETE_INTERVAL()).append("),");
            sb.append("(DAYLIGHT_SAVING_WAS_IN_EFFECT=").append(""+getDAYLIGHT_SAVING_WAS_IN_EFFECT()).append("),");
            sb.append("(CALIBRATION_LOST=").append(""+getCALIBRATION_LOST()).append("),");
            sb.append("(EFA_FAILURE=").append(""+getEFA_FAILURE()).append("),");
            sb.append("(U_EFA_FAILURE=").append(""+getU_EFA_FAILURE()).append("),");
            sb.append("(DATA_CHECKSUM_ERROR=").append(""+getDATA_CHECKSUM_ERROR()).append(')');
            sb.append("]\n");
        }catch(Exception e){
            log.warn("StatusFlag TO STRING ERR=>"+e.getMessage());
        }
		return sb.toString();
	}

}
