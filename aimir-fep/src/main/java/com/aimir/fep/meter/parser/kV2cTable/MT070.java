/** 
 * @(#)MT070.java       1.0 06/12/14 *
 * 
 * Meter Configuration Constants Table Class.
 * Copyright (c) 2006-2007 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.kV2cTable;

import com.aimir.fep.util.DataFormat;

/**
 * DATE_FORMAT         : 02 
 * SUPPRESS_LEAD_ZEROS : 00 
 * DISP_SCALAR         : 00 
 * DEMAND_DISP_UNITS   : 00 
 * PRIMARY_DISPLAY     : 00 
 * OFS_DISP_MULTIPLIER : 01 00 00 00 
 *                     : 33 33 42 00 00 00 00 20 20 20 
 *                       20 20 20 20 20 20 20 20 20 20 
 *                       20 20 20 20 20 20 20 20 20 20 
 *                       20 20 20 20 20 20 20 
 */

/**
 * @author Park YeonKyoung goodjob@nuritelecom.com
 */
public class MT070 {
	
	public static final int OFS_DATE_FORMAT         = 0;
	public static final int OFS_SUPPRESS_LEAD_ZEROS = 1;
	public static final int OFS_DISP_SCALAR         = 2;
	public static final int OFS_DEMAND_DISP_UNITS   = 3;
	public static final int OFS_PRIMARY_DISPLAY     = 4;
	public static final int OFS_DISP_MULTIPLIER     = 5;
	
	public static final int LEN_DATE_FORMAT         = 1;
	public static final int LEN_SUPPRESS_LEAD_ZEROS = 1;
	public static final int LEN_DISP_SCALAR         = 1;
	public static final int LEN_DEMAND_DISP_UNITS   = 1;
	public static final int LEN_PRIMARY_DISPLAY     = 1;
	public static final int LEN_DISP_MULTIPLIER     = 4;
	
	private byte[] data;

	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public MT070(byte[] data) {
		this.data = data;
	}

	public String getDATE_FORMAT() throws Exception {
		int df = DataFormat.hex2unsigned8(data[OFS_DATE_FORMAT]);
		
		switch(df){
			case 0:
				return "DDMMYY";
			case 1:
				return "MMDDYY";
			case 2:
				return "YYMMDD";
			default:
				return "N/A";
		}
	}

	public int getDISP_SCALAR() throws Exception {
		int ds = DataFormat.hex2unsigned8(data[OFS_DISP_SCALAR]);

        /*        
		switch(ds){
			case 0:
				return 1;
			case 1:
				return Math.pow(10,-1);
			case 2:
				return Math.pow(10,-2);
			case 3:
				return Math.pow(10,-3);
			default:
				return 1;
		}
        */
        return ds;
	}
	
	
	public byte parseDISP_SCALAR() throws Exception {
		return data[OFS_DISP_SCALAR];
	}
	
	
	public int getDISP_MULTIPLIER() throws Exception {
		return DataFormat.hex2dec(
				DataFormat.LSB2MSB(
					DataFormat.select(
						data,OFS_DISP_MULTIPLIER,LEN_DISP_MULTIPLIER)));
	}
	
	public byte[] parseDISP_MULTIPLIER() throws Exception {
		return DataFormat.dec2hex(
			DataFormat.hex2dec(
				DataFormat.LSB2MSB(
					DataFormat.select(
						data,OFS_DISP_MULTIPLIER,LEN_DISP_MULTIPLIER))));
	}


}
