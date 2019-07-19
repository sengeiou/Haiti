/** 
 * @(#)ST03.java       1.0 05/07/25 *
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
 
package com.aimir.fep.meter.parser.a3rlnqTable;

import com.aimir.fep.util.DataFormat;


/**
 * @author Park YeonKyoung yeonkyoung@hanmail.net
 */
public class ST03 {
	
	public static final int OFS_STD_STATUS1 = 1;
	public static final int LEN_STD_STATUS1 = 2;
	
	public static final int OFS_STD_STATUS2 = 3;
	public static final int LEN_STD_STATUS2 = 1;
	
	public static final int OFS_MFG_STATUS  = 4;
	public static final int LEN_MFG_STATUS  = 13;
	
	private byte[] data;

	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public ST03(byte[] data) {
		this.data = data;
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

}
