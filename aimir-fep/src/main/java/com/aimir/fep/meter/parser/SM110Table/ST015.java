/** 
 * @(#)ST015.java       1.0 06/12/14 *
 * 
 * Meter KE,CT,VT Class.
 * Copyright (c) 2006-2007 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.SM110Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ex) 
 * fa 00 00 00 00 00 00 00 00 00 00 00 00 0a 00 00 00 00 00 0a 00 00 00 00 00 
 * fa 00 00 00 00 00 00 00 00 00 00 00 00 0a 00 00 00 00 00 0a 00 00 00 00 00 
 * fa 00 00 00 00 00 00 00 00 00 00 00 00 0a 00 00 00 00 00 0a 00 00 00 00 00 
 * fa 00 00 00 00 00 00 00 00 00 00 00 00 0a 00 00 00 00 00 0a 00 00 00 00 00 
 * fa 00 00 00 00 00 00 00 00 00 00 00 00 0a 00 00 00 00 00 0a 00 00 00 00 00 
 * fa 00 00 00 00 00 00 00 00 00 00 00 00 0a 00 00 00 00 00 0a 00 00 00 00 00 
 * fa 00 00 00 00 00 00 00 00 00 00 00 00 0a 00 00 00 00 00 0a 00 00 00 00 00 
 * fa 00 00 00 00 00 00 00 00 00 00 00 00 0a 00 00 00 00 00 0a 00 00 00 00 00 
 * fa 00 00 00 00 00 00 00 00 00 00 00 00 0a 00 00 00 00 00 0a 00 00 00 00 00 
 * fa 00 00 00 00 00 00 00 00 00 00 00 00 0a 00 00 00 00 00 
 */
/**
 * @author Park YeonKyoung yeonkyoung@hanmail.net
 */
public class ST015 {
	
	public static final int BLK_SIZE             = 25;
	public static final int LEN_MULTIPLIER       = 6;
	public static final int LEN_OFFSET           = 6;
	public static final int LEN_SET_APPLIED_FLAG = 1;
	public static final int LEN_RATIO_F1         = 6;
	public static final int LEN_RATIO_P1         = 6;

	private byte[] data;
    private static Log log = LogFactory.getLog(ST015.class);

	private int NBR_CONSTANTS_ENTRIES;
	public final static int BLOCK_SIZE = 25;
	

	public ST015(byte[] data, int NBR_CONSTANTS_ENTRIES) {
		this.data = data;
		this.NBR_CONSTANTS_ENTRIES = NBR_CONSTANTS_ENTRIES;
	}
	
	
	/*
	public long getKE() throws Exception {
		return DataFormat.hex2long(
				DataFormat.LSB2MSB(
					DataFormat.select(data,OFS_KE,LEN_KE)));
	}

	
	public byte[] parseCT() throws Exception {
		long val = DataFormat.hex2long(
					DataFormat.LSB2MSB(DataFormat.select(data,OFS_CT,LEN_CT)));
		return DataFormat.dec2hex((int)(val*instscale));
	}
	
	public byte[] parseVT() throws Exception {
		long val = DataFormat.hex2long(
					DataFormat.LSB2MSB(DataFormat.select(data,OFS_VT,LEN_VT)));
		return DataFormat.dec2hex((int)(val*instscale));
	}
	*/
	
	//public 


}
