/** 
 * @(#)ST21.java       1.0 05/07/25 *
 * 
 * Actual Dimension Register Table.
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


/**
 * @author Park YeonKyoung yeonkyoung@hanmail.net
 */
public class ST21 implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4681285248709083843L;
	public static final int OFS_REG_FUNC1_FLAG  = 0;
	public static final int OFS_REG_FUNC2_FLAG  = 1;
	public static final int OFS_NBR_SELF_READS  = 2;
	public static final int OFS_NBR_SUMMATIONS  = 3;
	public static final int OFS_NBR_DEMANDS     = 4;
	public static final int OFS_NBR_COINCIDENT  = 5;
	public static final int OFS_NBR_OCCUR       = 6;
	public static final int OFS_NBR_TIERS       = 7;
	public static final int OFS_NBR_PRESENT_DMD = 8;
	public static final int OFS_NBR_PRESENT_VAL = 9;
	
	private byte[] data;

    private Log logger = LogFactory.getLog(getClass());
	
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public ST21(byte[] data) {
		this.data = data;
	}
	
	public int getNBR_SELF_READS() {
		return DataFormat.hex2unsigned8(data[OFS_NBR_SELF_READS]);
	}
	
	public int getNBR_SUMMATIONS() {
		return DataFormat.hex2unsigned8(data[OFS_NBR_SUMMATIONS]);
	}
	
	public int getNBR_DEMANDS() {
		return DataFormat.hex2unsigned8(data[OFS_NBR_DEMANDS]);
	}
	
	public int getNBR_COINCIDENT() {
		return DataFormat.hex2unsigned8(data[OFS_NBR_COINCIDENT]);
	}

	public int getNBR_OCCUR() {
		return DataFormat.hex2unsigned8(data[OFS_NBR_OCCUR]);
	}
	
	public int getNBR_TIERS() {
		return DataFormat.hex2unsigned8(data[OFS_NBR_TIERS]);
	}

	public int getNBR_PRESENT_DMD() {
		return DataFormat.hex2unsigned8(data[OFS_NBR_PRESENT_DMD]);
	}
	
	public int getNBR_PRESENT_VAL() {
		return DataFormat.hex2unsigned8(data[OFS_NBR_PRESENT_VAL]);
	}

}
