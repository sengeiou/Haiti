/** 
 * @(#)ST05.java       1.0 05/07/25 *
 * 
 * Meter Serial Class.
 * Copyright (c) 2004-2005 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.a3rlnqTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;


/**
 * @author Park YeonKyoung yeonkyoung@hanmail.net
 */
public class ST05 {
	
	public static final int OFS_MSERIAL = 12;
	public static final int LEN_MSERIAL = 8;
	
	private byte[] data;

    private static Log logger = LogFactory.getLog(ST05.class);
	
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public ST05(byte[] data) {
		this.data = data;
	}

	public byte[] parseMSerial() {
		
		byte[] b = new byte[]{0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30};
		
		try{
			b =  DataFormat.select(data,OFS_MSERIAL,LEN_MSERIAL);
		}catch(Exception e){
			logger.warn("meter serial wrong! : "+e.getMessage());
		}
		return b;
	}

	public String getMSerial() {

		String mserial = new String("00000000");

		try{
			mserial = new String(DataFormat.select(data,OFS_MSERIAL,LEN_MSERIAL)).trim();
			
			if(mserial == null || mserial.equals("")){
				mserial = "00000000";
			}

		}catch(Exception e){
			logger.warn("meter serial : "+e.getMessage());
		}

		return mserial;
	}

}
