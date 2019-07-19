/** 
 * @(#)Class6.java       1.0 04/09/16 *
 * 
 * Meter Element Information
 * Copyright (c) 2004-2005 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.a1rlTable;

/**
 * Metering Function Block class.
 * @author Park YeonKyoung yeonkyoung@hanmail.net
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Class6 {
	
	public static final int OFS_XUME = 3;

	public static final int LEN_XUM = 1;
	
	private byte[] data;
	
	public Class6(byte[] data) {
		this.data = data;
	}
	
	/**
	 * Meter Element. Get
	 * XUOM (7~6) bit
	 * number of meter elements, 2 bit binary.
	 * 
	 * 01 = meter has one voltage elements.
	 * 00 = meter has two voltage elements.
	 * 10 = meter has three voltage elements.
	 * 11 = meter has 2 1/2 elements
	 * 
	 * @return
	 * Two Voltage   : 0x00(3P 3W)
	 * One Voltage   : 0x40(1P 2W)
	 * Three Voltage : 0x80(3P 4W)
	 * 2 1/2 Voltage : 0xc0(1P 3W)
	 */
	public byte parseXUME() throws Exception {

		int xume = ((data[OFS_XUME] & 0xFF) >> 6) & 0xFF;

		switch(xume){
			case 0 :
				return (byte)0x00;
			case 1 :
				return (byte)0x40;
			case 2 :
				return (byte)0x80;
			case 3 :
				return (byte)0xc0;
			default :
				throw new Exception("N/A XUME TYPE");
		}

	}

}
