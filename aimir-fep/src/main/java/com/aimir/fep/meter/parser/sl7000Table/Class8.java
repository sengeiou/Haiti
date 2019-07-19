/** 
 * @(#)Class8.java       1.0 04/10/19 *
 * Copyright (c) 2004-2005 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
package com.aimir.fep.meter.parser.sl7000Table;

/**
 * @author Park YeonKyoung yeonkyoung@hanmail.net
 *
 * ACRATIS SL7000 meter Class. <p> * 
 * Include Meter Date & Time Information. <p>
 */
public class Class8 {

	public final static int OFS_DATETIME = 30;
	public final static int OFS_YEAR     = 30;
	public final static int OFS_MONTH    = 32;
	public final static int OFS_DAY      = 33;
	
	public final static int OFS_HOUR     = 35;
	public final static int OFS_MIN      = 36;
	public final static int OFS_SEC      = 37;
	
	
	private byte[] data;

	/**
	 * Constructor 
	 * 
	 * @param data - read data
	 */
	public Class8(byte[] data) {
		this.data = data;
	}
	
	
	/**
	 * Get Date and Time.<p>
	 * @return
	 */
	public byte[] parseDateTime(){
		
		byte[] datetime = new byte[7];
							
		/* length 8 */		
		datetime[0] = data[OFS_YEAR];
		datetime[1] = data[OFS_YEAR+1];
		datetime[2] = data[OFS_MONTH];
		datetime[3] = data[OFS_DAY];
		datetime[4] = data[OFS_HOUR];
		datetime[5] = data[OFS_MIN];
		datetime[6] = data[OFS_SEC];
		
		return datetime;
	}
	
	
}
