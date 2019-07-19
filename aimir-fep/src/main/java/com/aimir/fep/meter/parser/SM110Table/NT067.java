/** 
 * @(#)NT067.java       1.0 09/04/23 *
 * 
 * Meter Program Constants 2 Table Class.
 * Copyright (c) 2009-2010 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.SM110Table;

import com.aimir.fep.util.DataFormat;

/**
00 40 
94 52 
 */

/**
 * @author Park YeonKyoung goodjob@nuritelecom.com
 */
public class NT067 implements java.io.Serializable {

	private static final long serialVersionUID = -1148625574342719143L;
	public static final int OFS_CUR_TRANS_RATIO         = 0;
	public static final int OFS_POT_TRANS_RATIO         = 2;
	
	public static final int LEN_CUR_TRANS_RATIO         = 2;
	public static final int LEN_POT_TRANS_RATIO         = 2;
	
	private byte[] data;
	
    public NT067() {}
    
	/**
	 * Constructor .<p>
	 */
	public NT067(byte[] data) {
		this.data = data;
	}

	public int getCUR_TRANS_RATIO() throws Exception {
		return DataFormat.hex2dec(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,OFS_CUR_TRANS_RATIO,LEN_CUR_TRANS_RATIO)));
	}
	
	public int getPOT_TRANS_RATIO() throws Exception {
		return DataFormat.hex2dec(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,OFS_POT_TRANS_RATIO,LEN_POT_TRANS_RATIO)));
	}
	
	public byte[] parseCUR_TRANS_RATIO() throws Exception {
		return DataFormat.dec2hex(
			DataFormat.hex2dec(
				DataFormat.LSB2MSB(
					DataFormat.select(
						data,OFS_CUR_TRANS_RATIO,LEN_CUR_TRANS_RATIO))));
	}
	
	public byte[] parsePOT_TRANS_RATIO() throws Exception {
		return DataFormat.dec2hex(
			DataFormat.hex2dec(
				DataFormat.LSB2MSB(
					DataFormat.select(
						data,OFS_POT_TRANS_RATIO,LEN_POT_TRANS_RATIO))));
	}


}
