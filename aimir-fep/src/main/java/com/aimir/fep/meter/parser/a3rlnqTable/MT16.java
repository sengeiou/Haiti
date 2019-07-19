/** 
 * @(#)MT16.java       1.0 05/07/25 *
 * 
 * Instruementation Scale Class.
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
public class MT16 {
	
	public static final int OFS_INS_SCALE = 0;
	
	private byte[] data;
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public MT16(byte[] data) {
		this.data = data;
	}

	public byte parseINSScale() throws Exception {
		return data[OFS_INS_SCALE];
	}

	public double getINSScale() throws Exception {
		return Math.pow(10,DataFormat.hex2signed8(data[OFS_INS_SCALE]));
	}
	
	public int getINScaleP() throws Exception {
		return DataFormat.hex2signed8(data[OFS_INS_SCALE]);
	}


}
