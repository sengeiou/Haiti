/** 
 * @(#)Class9.java       1.0 04/09/16 *
 * 
 * Billing Data Class (Previous Month)
 * Copyright (c) 2004-2005 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.a1rlTable;

import com.aimir.fep.util.DataFormat;

/**
 * @author Park YeonKyoung yeonkyoung@hanmail.net
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Class10 {
	
	public static final int OFS_KH      = 0;
	public static final int OFS_PR      = 3;
	public static final int OFS_MTRSN   = 5;
	public static final int OFS_KEADJ   = 10;
	public static final int OFS_KDADJ   = 15;
	public static final int OFS_ENEWCON = 20;
	public static final int OFS_ENEWACT = 23;
	
	public static final int LEN_KH      = 3;
	public static final int LEN_PR      = 1;
	public static final int LEN_MTRSN   = 5;
	public static final int LEN_KEADJ   = 5;
	public static final int LEN_KDADJ   = 5;
	public static final int LEN_ENEWCON = 3;
	public static final int LEN_ENEWACT = 1;
	
	
	private byte[] data;
    
	/**
	 * Constructor
	 * 
	 * @param data - read data (exclusion header,crch,crcl)
	 */
	public Class10(byte[] data){
		this.data = data;
	}
	
	/**
	 * 
	 * [Wh/rev]
	 * 
	 * @return
	 */
	public double parseKH() throws Exception {
		return DataFormat.bcd2dec(data,OFS_KH,LEN_KH)*Math.pow(-10,3);
	}
	
	/**
	 * 
	 * [pulse/rev]
	 * @return
	 */
	public int parsePR() throws Exception {
		return DataFormat.bcd2dec(data,OFS_PR,LEN_PR);
	}
	
	/**
	 * 
	 * Meter serial number
	 * @return
	 */
	public String parseMTRSN() throws Exception {
		return DataFormat.bcd2str(data,OFS_MTRSN,LEN_MTRSN);
	}
	
	/**
	 * 
	 * [kWh/pulse]
	 * move by DPLOCE.
	 * 
	 * @return
	 */
	public double parseKEADJ() throws Exception {
		return DataFormat.bcd2long(data,OFS_KEADJ,LEN_KEADJ)*Math.pow(-10,6);
	}
	
	/**
	 * 
	 * [W/pulse]
	 * move by DPLOCE
	 * This value calculated from  KEADJ and  demand interval value.
	 * @return
	 */
	public double parseKDADJ() throws Exception {
		return DataFormat.bcd2long(data,OFS_KDADJ,LEN_KDADJ)*Math.pow(-10,6);
	}

}
