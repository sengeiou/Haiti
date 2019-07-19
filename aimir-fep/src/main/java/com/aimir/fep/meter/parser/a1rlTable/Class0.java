/** 
 * @(#)Class0.java       1.0 04/09/16 *
 * 
 * Meter Information Class.
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
public class Class0 {
	
	public static final int OFS_UKE     = 4;
	public static final int OFS_DPLOCE  = 11;
	public static final int OFS_DPLOCD  = 12;
	public static final int OFS_VTRATIO = 14;
	public static final int OFS_CTRATIO = 17;
	public static final int OFS_XFACTOR = 20;
	
	public static final int LEN_UKE     = 5;
	public static final int LEN_DPLOCE  = 1;
	public static final int LEN_DPLOCD  = 1;
	public static final int LEN_VTRATIO = 2;
	public static final int LEN_CTRATIO = 2;
	public static final int LEN_XFACTOR = 4;
	
	private byte[] data;

	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public Class0(byte[] data) {
		this.data = data;
	}

	
	/**
	 * 10 BCD digits.         <p>
	 * fixed implied decimal. <p> 
	 * !!!!watt-hours per equivalent pulse.<p>
	 * Used for primary metering and is activated by PCODE:pprim.<p>
	 * @return
	 */
	public double parseUKE() throws Exception {
		//return DataFormat.bcd2long(data,OFS_UKE,LEN_UKE)*0.001;
		return DataFormat.bcd2long(data,OFS_UKE,LEN_UKE);
	}

	
	/**
	 * Energy Format. <p>
	 * Decimal location applied to all energy values.<p>
	 *   0 = no decimal displayed. <p>
	 * 1-4 = decimal location on display begining at the right.<p>
	 * @return - 4byte hex data
	 */
	public byte[] parseDPLOCE() throws Exception {
		//return (int) Math.pow(10,(int)data.charAt(OFS_DPLOCE));
		return DataFormat.bcd2hex(data,OFS_DPLOCE,LEN_DPLOCE);
	}
	
	
	/**
	 * Demand Format. <p>
	 * Decimal location applied to all demand value. <p>
	 *   0 = no decimal displayed.<p>
	 * 1-4 = decimal location on display begining at the right.<p>
	 *  
	 * @return - 4byte hex data
	 */
	public byte[] parseDPLOCD() throws Exception {
		//return (int) Math.pow(10,(int)data.charAt(OFS_DPLOCD));
		return DataFormat.bcd2hex(data,OFS_DPLOCD,LEN_DPLOCD);
	}

	
	/**
	 * VT Ratio. <p>
	 * 
	 * @return - 4byte hex data
	 */
	public byte[] parseVTRATIO() throws Exception {
		return DataFormat.bcd2hex(data,OFS_VTRATIO,LEN_VTRATIO);
	}
	
	
	/**
	 * CT Ratio. <p>
	 * 
	 * @return - 4 byte hex data
	 */
	public byte[] parseCTRATIO() throws Exception {
		return DataFormat.bcd2hex(data,OFS_CTRATIO,LEN_CTRATIO);
	}
	
	
	/**
	 * XFACTOR. <p>
	 * @return
	 */
	public double parseXFACTOR() throws Exception {
		//return getDoubleValue(OFS_XFACTOR,OFS_XFACTOR+LEN_XFACTOR);
		return DataFormat.bcd2dec(data,OFS_XFACTOR,LEN_XFACTOR);
	}

}
