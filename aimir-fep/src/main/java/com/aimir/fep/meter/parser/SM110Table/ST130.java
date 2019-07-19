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

import com.aimir.fep.util.DataUtil;

/**
 * @author Park YeonKyoung goodjob@nuritelecom.com
 */
public class ST130 implements java.io.Serializable {

	private static final long serialVersionUID = 4218911709627363210L;
	private static Log log = LogFactory.getLog(ST130.class);
	private byte[] data;
	private int relay_status = 0;
	private int relay_activate_status = 0;

	public ST130() {}
	
	/**
	 * Constructor .<p>
	 */
	public ST130(byte[] data) 
    {
		this.data = data;
        parse();
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
        if(data == null || data.length < 2){
            return null;
        }else{
            LinkedHashMap res = new LinkedHashMap(2);

            
            res.put("relay status"          , ""+this.relay_status);
            res.put("relay activate status" , ""+this.relay_activate_status);
            return res;
        }
    }

    public void parse() 
    {

        try {
            if(data.length >= 2){
                this.relay_status = DataUtil.getIntToByte(data[0]);
                this.relay_activate_status = DataUtil.getIntToByte(data[1]);
            }else{
                log.warn("Not Valid Data!");
            }
        } catch(Exception e){
            log.warn("ST130 parse data error!: "+e.getMessage());
        }
    }
}
