/** 
 * @(#)ST130.java       1.0 06/12/14 *
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

/**
 * 04 00 04 00 02 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 
 * 00 00
 * This table contains the necessary data to describe the current load control operation. 
 * The field, ECP_OR_PPM_ACCUM is a shared between PPM and ECP Override, 
 * therefore a meter should be configured for one of these modes. 
 * When a meter is configured for prepayment operation it is recommended that ECP operation is set to ECP Ignore or ECP Normal, 
 * neither of which uses the accumulator. 
 * If meter is to be used for ECP Override All or ECP Override Delta operation, 
 * then it is recommended that prepayment operation never be activated for this meter. 
 * Alternating between ECP Override operation and prepayment mode will cause the shared accumulator to become unreliable.
 * 
 * Load Control Status Table
 * @author Park YeonKyoung goodjob@nuritelecom.com
 */
public class MT117 implements java.io.Serializable {

	private static final long serialVersionUID = 709635401950654214L;
	private static Log log = LogFactory.getLog(MT117.class);
	private byte[] data;
	private int relay_status = 0;
	private int relay_activate_status = 0;

    private byte[] RCDC_STATUS_REGISTER = new byte[1];    
    private byte[] RCDC_STATE_REGISTER = new byte[1];
    private byte[] RCDC_CHARGE_CAPACITOR_VOLT = new byte[1];
    private byte[] FW = new byte[1];
    
    public enum RCDCSTATE {
    	
    	ACTUAL_SWITCH_STATE((byte)0x80), //0 = Open, 1 = Closed 
    	COMMANDED_SWITCH_STATE((byte)0x40),//0 = Open, 1 = Closed
    	AC_VOLTAGE_STATE((byte)0x20),
    	EXTERNAL_AC_VOLTAGE((byte)0x08),
    	ARM_FAULT((byte)0x04), 
    	CAPACITOR_FAULT((byte)0x02),
    	RESERVED((byte)0x01);

    	private byte code;

    	
    	RCDCSTATE(byte code){
    		this.code = code;
    	}
    	
        public byte getCode() {
            return this.code;
        }
    }

	public MT117() {}
	
	/**
	 * Constructor .<p>
	 */
	public MT117(byte[] data) 
    {
		this.data = data;
        parse();
	}

    public void parse() 
    {
    	int pos = 0;
		System.arraycopy(data, pos, RCDC_STATUS_REGISTER, 0, RCDC_STATUS_REGISTER.length);
		pos += RCDC_STATUS_REGISTER.length;			
		System.arraycopy(data, pos, RCDC_STATE_REGISTER, 0, RCDC_STATE_REGISTER.length);
		pos += RCDC_STATE_REGISTER.length;
		System.arraycopy(data, pos, RCDC_CHARGE_CAPACITOR_VOLT, 0, RCDC_CHARGE_CAPACITOR_VOLT.length);
		pos += RCDC_CHARGE_CAPACITOR_VOLT.length;
		System.arraycopy(data, pos, FW, 0, FW.length);
		pos += FW.length;			

		if((RCDC_STATUS_REGISTER[0] & RCDCSTATE.ACTUAL_SWITCH_STATE.getCode()) > 0){
			this.relay_status = 0;
		}else{
			this.relay_status = 1;
		}

		if((RCDC_STATUS_REGISTER[0] & RCDCSTATE.ARM_FAULT.getCode()) > 0){ //TODO TEST Indicates the RCDC switchboard received an open or close command, but the required arm enable bit is not set.
			this.relay_activate_status = 0;
		}else{
			this.relay_activate_status = 1;
		}
    }
    
    public String getRelayStatusString() 
    {
        if(this.relay_status == 0){
            return "Off";
        }else {
            return "On";
        }
    }
    
    public String getRelayActivateStatusString() 
    {
        if(this.relay_activate_status == 0){
            return "Off";
        }else {
            return "On";
        }
    }
    
	public int getRelayStatus() 
    {
        return this.relay_status;
	}
	
	public int getRelayActivateStatus() 
    {
        return this.relay_activate_status;
	}

    public LinkedHashMap getData()
    {
        if(data == null || data.length < 20){
            return null;
        }else{
            LinkedHashMap res = new LinkedHashMap(2);            
            res.put("relay status"          , ""+this.relay_status);
            res.put("relay activate status" , ""+this.relay_activate_status);
            return res;
        }
    }

}
