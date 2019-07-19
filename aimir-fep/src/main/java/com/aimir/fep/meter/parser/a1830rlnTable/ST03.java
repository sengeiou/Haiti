/** 
 * @(#)ST03.java       1.0 06/06/23 *
 * 
 * Meter Status Class.
 * Copyright (c) 2004-2005 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.a1830rlnTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;


/**
 * @author Park YeonKyoung yeonkyoung@hanmail.net
 */
public class ST03 implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6731577860566590521L;
	public static final int OFS_STD_STATUS1 = 1;
	public static final int LEN_STD_STATUS1 = 2;
	
	public static final int OFS_STD_STATUS2 = 3;
	public static final int LEN_STD_STATUS2 = 1;
	
	public static final int OFS_MFG_STATUS  = 4;
	public static final int LEN_MFG_STATUS  = 13;
	
	private byte[] data;

    private Log logger = LogFactory.getLog(getClass());
	
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public ST03(byte[] data) {
		this.data = data;
	}
	
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public ST03() {

	}
	
	public byte[] parseStatus1() throws Exception {
		return DataFormat.select(data,OFS_STD_STATUS1,LEN_STD_STATUS1);
	}
	
	public byte[] parseStatus2() throws Exception {
		return DataFormat.select(data,OFS_STD_STATUS2,LEN_STD_STATUS2);
	}
	
	public byte[] parseMFGStatus() throws Exception {
		return DataFormat.select(data,OFS_MFG_STATUS,LEN_MFG_STATUS);
	}
	
	public byte[] parseMeterStat() throws Exception {
		
		byte[] mstatus = new byte[15];
		
		byte[] status1 = DataFormat.select(data,OFS_STD_STATUS1,LEN_STD_STATUS1);
		byte[] mfg_status = DataFormat.select(data,OFS_MFG_STATUS,LEN_MFG_STATUS);
		
		System.arraycopy(status1,0,mstatus,0,2);
		System.arraycopy(mfg_status,0,mstatus,2,13);
		
		return mstatus;
		
	}
	
	public String getMeterStatus(byte[] status){

		StringBuffer sb = new StringBuffer();
		StringBuffer msg = new StringBuffer();
		
		String[] st = new String[]{
				"low battery",
				"clock error",
				"registered memory error",
				"ROM failure",
				"RAM failure",
				"self check error",
				"configuration error",
				"unprogrammed",
				"filler",
				"filler",
				"reverse rotation",
				"tamper detect",
				"power failure",
				"demand overload",
				"low loss potential",
				"measurement error",
				"spare",
				"button press clear data",
				"button press demand reset",
				"time changed",
				"pending table activated",
				"self rdad data available",
				"previous season data available",
				"demand reset data available"
			};
		
		try{
			for(int i = 0; i < status.length; i++){
				int convertInt = DataFormat.hex2dec(status, i, 1);
				String convertStr 
					= Util.frontAppendNStr('0',Integer.toBinaryString(convertInt),8);
				sb.append(convertStr);
			}
						
			for(int i = 0; i < st.length;i++){
				if(sb.charAt(i) == '1'){
					msg.append(st[i]);
				}
			}
		}catch(Exception e){
			logger.warn(e.getMessage());
		}
		
		return msg.toString();

	}
	
}
