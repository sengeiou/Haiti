/** 
 * @(#)MT115.java       1.0 2019-11-07 *
 * 
 * Relay Status table Class.
 * Copyright (c) 2006-2007 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.SM110Table;

import java.util.LinkedHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;

/**
 * Load Control Status Table
 * @author Park Jiwoong wll27471297@nuritelecom.com
 */
public class MT115 implements java.io.Serializable {

	private static final long serialVersionUID = 5490087588582714186L;
	
	private static Log log = LogFactory.getLog(MT115.class);

	private byte[] TMP_BYTE = new byte[1];
	
	// STATUS : 16 Bit -> 2 Byte
	private boolean[] FILLER_1 = new boolean[5]; // 15-11
	private boolean[] LOAD_SIDE_FREQ_ERROR = new boolean[1]; // 10
	private boolean[] LINE_SIDE_FREQ_ERROR = new boolean[1]; // 9
	private boolean[] MANUAL_ARMED_TIME_OUT = new boolean[1]; // 8
	private boolean[] PPM_ALERT = new boolean[1]; // 7
	private boolean[] SWITCH_FAILED_TO_OPEN = new boolean[1]; // 6 
	private boolean[] BYPASSED = new boolean[1]; // 5
	private boolean[] FILLER_2 = new boolean[1]; // 4
	private boolean[] ALTERNATE_SOURCE = new boolean[1]; // 3
	private boolean[] SWITCH_FAILED_TO_CLOSE = new boolean[1]; // 2
	private boolean[] SWITCH_CONTROLLER_ERROR = new boolean[1]; // 1
	private boolean[] COMMUNICATION_ERROR = new boolean[1]; // 0
	// HISTORY : 16 Bit -> 2 Byte
	// -> RCDC_STATUS : 8 Bit
	private boolean[] FILLER_3 = new boolean[1]; // 7
	private boolean[] WAITING_TO_ARM = new boolean[1]; // 6 
	private boolean[] LOCKOUT_IN_EFFECT = new boolean[1]; // 5
	private boolean[] OUTAGE_OPEN_IN_EFFECT = new boolean[1]; // 4
	private boolean[] ARMED_WAITING_TO_CLOSE = new boolean[1]; // 3
	private boolean[] OPEN_HOLD_FOR_COMMAND = new boolean[1]; // 2
	private boolean[] DESIRED_SWITCH_STATE = new boolean[1]; // 1
	private boolean[] ACTUAL_SWITCH_STATE = new boolean[1]; // 0
	// -> LC_STATE
	private boolean[] PPM_DISCONNECT = new boolean[1]; // 7
	private boolean[] DLP_DISCONNECT = new boolean[1]; // 6 
	private boolean[] ECP_DISCONNECT = new boolean[1]; // 5
	private boolean[] FILLER_4 = new boolean[1]; // 4
	private boolean[] PPM_ENABLED = new boolean[1]; // 3
	private boolean[] DLP_ENABLED = new boolean[1]; // 2
	private boolean[] ECP_ENABLED = new boolean[1]; // 1
	private boolean[] FILLER_5 = new boolean[1]; // 0

	private byte[] LC_RECONNECT_ATTEMPT_COUNT = new byte[1];
	private byte[] ECP_ACCUMULATOR_OR_PPM_REMAINING_CREDIT = new byte[4];
	private byte[] LC_DEMAND_CALCULATED = new byte[4];
	private byte[] DURATION_COUNT_DOWN = new byte[2];
	private byte[] HOURS = new byte[1];
	private byte[] MINUTES = new byte[1];
	private byte[] LAST_CMD_STATUS = new byte[1];
	private byte[] FILLER_6 = new byte[4];
	private byte[] CRC = new byte[2];
	
	
//	private byte[] data;

	public MT115() {}
	
	public MT115(byte[] data) {
//		this.data = data;
        parse(data);
        printAll();
	}
	
	public static boolean[] booleanArrayFromByte(byte x) {
	    boolean bs[] = new boolean[8];
	    bs[0] = ((x & 0x01) != 0);
	    bs[1] = ((x & 0x02) != 0);
	    bs[2] = ((x & 0x04) != 0);
	    bs[3] = ((x & 0x08) != 0);
	    bs[4] = ((x & 0x10) != 0);
	    bs[5] = ((x & 0x20) != 0);
	    bs[6] = ((x & 0x40) != 0);
	    bs[7] = ((x & 0x80) != 0);
	    return bs;
	}

	public String booleanArr2Str(boolean[] arr) {
		StringBuilder sb = new StringBuilder();
		sb.append("[ ");
		for(boolean bool : arr) {
			sb.append((bool == true) ? "1" : "0").append(" ");
		}
		sb.append("]");
		return sb.toString();
	}
	
    public void parse(byte[] data) {
    	int pos = 0;
		int boolPos = 0;
    	boolean[] tmpBoolArr = new boolean[8];		
		
		System.arraycopy(data, pos, TMP_BYTE, 0, TMP_BYTE.length); pos += TMP_BYTE.length;
		tmpBoolArr = booleanArrayFromByte(TMP_BYTE[0]);
		System.arraycopy(tmpBoolArr, boolPos, FILLER_1, 0, FILLER_1.length);  boolPos += FILLER_1.length;
		System.arraycopy(tmpBoolArr, boolPos, LOAD_SIDE_FREQ_ERROR, 0, LOAD_SIDE_FREQ_ERROR.length); boolPos += LOAD_SIDE_FREQ_ERROR.length;
		System.arraycopy(tmpBoolArr, boolPos, LINE_SIDE_FREQ_ERROR, 0, LINE_SIDE_FREQ_ERROR.length); boolPos += LINE_SIDE_FREQ_ERROR.length;
		System.arraycopy(tmpBoolArr, boolPos, MANUAL_ARMED_TIME_OUT, 0, MANUAL_ARMED_TIME_OUT.length);  boolPos = 0;

		System.arraycopy(data, pos, TMP_BYTE, 0, TMP_BYTE.length); pos += TMP_BYTE.length;
		tmpBoolArr = booleanArrayFromByte(TMP_BYTE[0]);
		System.arraycopy(tmpBoolArr, boolPos, PPM_ALERT, 0, PPM_ALERT.length); boolPos += PPM_ALERT.length;
		System.arraycopy(tmpBoolArr, boolPos, SWITCH_FAILED_TO_OPEN, 0, SWITCH_FAILED_TO_OPEN.length); boolPos += SWITCH_FAILED_TO_OPEN.length;
		System.arraycopy(tmpBoolArr, boolPos, BYPASSED, 0, BYPASSED.length); boolPos += BYPASSED.length;
		System.arraycopy(tmpBoolArr, boolPos, FILLER_2, 0, FILLER_2.length); boolPos += FILLER_2.length;
		System.arraycopy(tmpBoolArr, boolPos, ALTERNATE_SOURCE, 0, ALTERNATE_SOURCE.length); boolPos += ALTERNATE_SOURCE.length;
		System.arraycopy(tmpBoolArr, boolPos, SWITCH_FAILED_TO_CLOSE, 0, SWITCH_FAILED_TO_CLOSE.length); boolPos += SWITCH_FAILED_TO_CLOSE.length;
		System.arraycopy(tmpBoolArr, boolPos, SWITCH_CONTROLLER_ERROR, 0, SWITCH_CONTROLLER_ERROR.length); boolPos += SWITCH_CONTROLLER_ERROR.length;
		System.arraycopy(tmpBoolArr, boolPos, COMMUNICATION_ERROR, 0, COMMUNICATION_ERROR.length); boolPos = 0;

		System.arraycopy(data, pos, TMP_BYTE, 0, TMP_BYTE.length); pos += TMP_BYTE.length;
		tmpBoolArr = booleanArrayFromByte(TMP_BYTE[0]);
		System.arraycopy(tmpBoolArr, boolPos, FILLER_3, 0, FILLER_3.length); boolPos += FILLER_3.length;
		System.arraycopy(tmpBoolArr, boolPos, WAITING_TO_ARM, 0, WAITING_TO_ARM.length); boolPos += WAITING_TO_ARM.length;
		System.arraycopy(tmpBoolArr, boolPos, LOCKOUT_IN_EFFECT, 0, LOCKOUT_IN_EFFECT.length); boolPos += LOCKOUT_IN_EFFECT.length;
		System.arraycopy(tmpBoolArr, boolPos, OUTAGE_OPEN_IN_EFFECT, 0, OUTAGE_OPEN_IN_EFFECT.length); boolPos += OUTAGE_OPEN_IN_EFFECT.length;
		System.arraycopy(tmpBoolArr, boolPos, ARMED_WAITING_TO_CLOSE, 0, ARMED_WAITING_TO_CLOSE.length); boolPos += ARMED_WAITING_TO_CLOSE.length;
		System.arraycopy(tmpBoolArr, boolPos, OPEN_HOLD_FOR_COMMAND, 0, OPEN_HOLD_FOR_COMMAND.length); boolPos += OPEN_HOLD_FOR_COMMAND.length;
		System.arraycopy(tmpBoolArr, boolPos, DESIRED_SWITCH_STATE, 0, DESIRED_SWITCH_STATE.length); boolPos += DESIRED_SWITCH_STATE.length;
		System.arraycopy(tmpBoolArr, boolPos, ACTUAL_SWITCH_STATE, 0, ACTUAL_SWITCH_STATE.length); boolPos = 0;
		
		System.arraycopy(data, pos, TMP_BYTE, 0, TMP_BYTE.length); pos += TMP_BYTE.length;
		tmpBoolArr = booleanArrayFromByte(TMP_BYTE[0]);
		System.arraycopy(tmpBoolArr, boolPos, PPM_DISCONNECT, 0, PPM_DISCONNECT.length); boolPos += PPM_DISCONNECT.length;
		System.arraycopy(tmpBoolArr, boolPos, DLP_DISCONNECT, 0, DLP_DISCONNECT.length); boolPos += DLP_DISCONNECT.length;
		System.arraycopy(tmpBoolArr, boolPos, ECP_DISCONNECT, 0, ECP_DISCONNECT.length); boolPos += ECP_DISCONNECT.length;
		System.arraycopy(tmpBoolArr, boolPos, FILLER_4, 0, FILLER_4.length);  boolPos += FILLER_4.length;
		System.arraycopy(tmpBoolArr, boolPos, PPM_ENABLED, 0, PPM_ENABLED.length); boolPos += PPM_ENABLED.length;
		System.arraycopy(tmpBoolArr, boolPos, DLP_ENABLED, 0, DLP_ENABLED.length); boolPos += DLP_ENABLED.length;
		System.arraycopy(tmpBoolArr, boolPos, ECP_ENABLED, 0, ECP_ENABLED.length); boolPos += ECP_ENABLED.length;
		System.arraycopy(tmpBoolArr, boolPos, FILLER_5, 0, FILLER_5.length);  boolPos = 0;
		
		System.arraycopy(data, pos, LC_RECONNECT_ATTEMPT_COUNT, 0, LC_RECONNECT_ATTEMPT_COUNT.length); pos += LC_RECONNECT_ATTEMPT_COUNT.length;
		System.arraycopy(data, pos, ECP_ACCUMULATOR_OR_PPM_REMAINING_CREDIT, 0, ECP_ACCUMULATOR_OR_PPM_REMAINING_CREDIT.length); pos += ECP_ACCUMULATOR_OR_PPM_REMAINING_CREDIT.length;
		System.arraycopy(data, pos, LC_DEMAND_CALCULATED, 0, LC_DEMAND_CALCULATED.length); pos += LC_DEMAND_CALCULATED.length;
		System.arraycopy(data, pos, DURATION_COUNT_DOWN, 0, DURATION_COUNT_DOWN.length); pos += DURATION_COUNT_DOWN.length;
		System.arraycopy(data, pos, HOURS, 0, HOURS.length); pos += HOURS.length;
		System.arraycopy(data, pos, MINUTES, 0, MINUTES.length); pos += MINUTES.length;
		System.arraycopy(data, pos, LAST_CMD_STATUS, 0, LAST_CMD_STATUS.length); pos += LAST_CMD_STATUS.length;
		System.arraycopy(data, pos, FILLER_6, 0, FILLER_6.length);  pos += FILLER_6.length;
		System.arraycopy(data, pos, CRC, 0, CRC.length); pos += CRC.length;
		
		log.debug("pos = "+pos+", data.length = "+data.length);
    }
	
	public String printAll() {
		StringBuilder sb = new StringBuilder();
		try {
			sb.append("  FILLER_1="+booleanArr2Str(FILLER_1).trim()).append(", \n")
			  .append("  LOAD_SIDE_FREQ_ERROR="+booleanArr2Str(LOAD_SIDE_FREQ_ERROR).trim()).append(", \n")
			  .append("  LINE_SIDE_FREQ_ERROR="+booleanArr2Str(LINE_SIDE_FREQ_ERROR).trim()).append(", \n")
			  .append("  MANUAL_ARMED_TIME_OUT="+booleanArr2Str(MANUAL_ARMED_TIME_OUT).trim()).append(", \n")
			  .append("  PPM_ALERT="+booleanArr2Str(PPM_ALERT).trim()).append(", \n")
			  .append("  SWITCH_FAILED_TO_OPEN="+booleanArr2Str(SWITCH_FAILED_TO_OPEN).trim()).append(", \n")
			  .append("  BYPASSED="+booleanArr2Str(BYPASSED).trim()).append(", \n")
			  .append("  FILLER_2="+booleanArr2Str(FILLER_2).trim()).append(", \n")
			  .append("  ALTERNATE_SOURCE="+booleanArr2Str(ALTERNATE_SOURCE).trim()).append(", \n")
			  .append("  SWITCH_FAILED_TO_CLOSE="+booleanArr2Str(SWITCH_FAILED_TO_CLOSE).trim()).append(", \n")
			  .append("  SWITCH_CONTROLLER_ERROR="+booleanArr2Str(SWITCH_CONTROLLER_ERROR).trim()).append(", \n")
			  .append("  COMMUNICATION_ERROR="+booleanArr2Str(COMMUNICATION_ERROR).trim()).append(", \n")
			  
			  .append("  FILLER_3="+booleanArr2Str(FILLER_3).trim()).append(", \n")
			  .append("  WAITING_TO_ARM="+booleanArr2Str(WAITING_TO_ARM).trim()).append(", \n")
			  .append("  LOCKOUT_IN_EFFECT="+booleanArr2Str(LOCKOUT_IN_EFFECT).trim()).append(", \n")
			  .append("  OUTAGE_OPEN_IN_EFFECT="+booleanArr2Str(OUTAGE_OPEN_IN_EFFECT).trim()).append(", \n")
			  .append("  ARMED_WAITING_TO_CLOSE="+booleanArr2Str(ARMED_WAITING_TO_CLOSE).trim()).append(", \n")
			  .append("  OPEN_HOLD_FOR_COMMAND="+booleanArr2Str(OPEN_HOLD_FOR_COMMAND).trim()).append(", \n")
			  .append("  DESIRED_SWITCH_STATE="+booleanArr2Str(DESIRED_SWITCH_STATE).trim()).append(", \n")
			  .append("  ACTUAL_SWITCH_STATE="+booleanArr2Str(ACTUAL_SWITCH_STATE).trim()).append(", \n")
			  
			  .append("  PPM_DISCONNECT="+booleanArr2Str(PPM_DISCONNECT).trim()).append(", \n")
			  .append("  DLP_DISCONNECT="+booleanArr2Str(DLP_DISCONNECT).trim()).append(", \n")
			  .append("  ECP_DISCONNECT="+booleanArr2Str(ECP_DISCONNECT).trim()).append(", \n")
			  .append("  FILLER_4="+booleanArr2Str(FILLER_4).trim()).append(", \n")
			  .append("  PPM_ENABLED="+booleanArr2Str(PPM_ENABLED).trim()).append(", \n")
			  .append("  DLP_ENABLED="+booleanArr2Str(DLP_ENABLED).trim()).append(", \n")
			  .append("  ECP_ENABLED="+booleanArr2Str(ECP_ENABLED).trim()).append(", \n")
			  .append("  FILLER_5="+booleanArr2Str(FILLER_5).trim()).append(", \n")
			  
			  .append("  LC_RECONNECT_ATTEMPT_COUNT="+DataFormat.hex2dec(LC_RECONNECT_ATTEMPT_COUNT)).append(", \n")
			  .append("  ECP_ACCUMULATOR_OR_PPM_REMAINING_CREDIT="+DataFormat.hex2dec(ECP_ACCUMULATOR_OR_PPM_REMAINING_CREDIT)).append(", \n")
			  .append("  LC_DEMAND_CALCULATED="+DataFormat.hex2dec(LC_DEMAND_CALCULATED)).append(", \n")
			  .append("  DURATION_COUNT_DOWN="+DataFormat.hex2dec(DURATION_COUNT_DOWN)).append(", \n")
			  .append("  HOURS="+DataFormat.hex2dec(HOURS)).append(", \n")
			  .append("  MINUTES="+DataFormat.hex2dec(MINUTES)).append(", \n")
			  .append("  LAST_CMD_STATUS"+DataFormat.hex2dec(LAST_CMD_STATUS)).append(", \n")
			  .append("  FILLER_6="+DataFormat.hex2dec(FILLER_6)).append(", ")
			  .append("  CRC="+DataFormat.hex2dec(CRC));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		log.debug("MT115[\n"+sb.toString()+"\n]\n");
		
		return "MT115[\n"+sb.toString()+"\n]\n";
	}
	
    public LinkedHashMap getData(){
//        if(data == null || data.length < 20){
//            return null;
//        }else{
            LinkedHashMap res = new LinkedHashMap(2);            
//            res.put("relay status"          , ""+this.relay_status);
//            res.put("relay activate status" , ""+this.relay_activate_status);
            return res;
//        }
    }
}
