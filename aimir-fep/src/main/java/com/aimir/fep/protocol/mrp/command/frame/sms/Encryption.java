/** 
 * @(#)Encryption.java       1.0 2008-11-24 *
 * Copyright (c) 2008-2009 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelecom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */

package com.aimir.fep.protocol.mrp.command.frame.sms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * Encryption. 
 * 
 * @version     1.0 1 Nov 2008 
 * @author		YK.Park 
 */


public class Encryption {

	private static Log log = LogFactory.getLog(Encryption.class);
	private String src = null;
	private byte[] dest = null;
    /**
     *  Constructor. <p>
     */ 
    public Encryption(byte[] frame) 
    {
    	this.src = new String(frame);
    	//change();
    }
    
    public Encryption(StringBuffer frameBuffer) 
    {
    	this.src = frameBuffer.toString();
    	//change();
    }
    
    public Encryption(String frame) 
    {
    	this.src = frame;
    	//change();
    }
    
    
    public String decode()
    {
    	return new String(dest);
    }
    
    public String encode()
    {
    	return new String(dest);
    }

    public void change()
    {
    	
    	log.debug("beforeEncrypt:"+src);
    	char temp = ' ';
    	char com_pass = ' ';
    	dest = new byte[src.length()];
    	
    	for(int i = 0; i < src.length(); i++){
    		temp = src.charAt(i);
    		if(temp >= '0' && temp <= '9'){
    			if(temp == '0' || temp == '9'){
    				com_pass = temp;
    			}else{
        			com_pass = (char)('9' - (temp-'0'));
    			}
    			log.debug("["+temp+"],["+com_pass+"]");
    		}else if((temp >= 'a' && temp <='z') || (temp >= 'A' && temp <= 'Z')){
    			if(temp == 'a' || temp == 'z' || temp == 'A' || temp == 'Z'){
    				com_pass = temp;
    			}else{
        			com_pass = (char)('Z' - (temp-'A'));
    			}
    			log.debug("["+temp+"],["+com_pass+"]");
    		}else{
    			com_pass = temp;
    			log.debug("["+temp+"],["+com_pass+"]");
    		}
    		dest[i] = (byte)com_pass;
    	}
    }

}
