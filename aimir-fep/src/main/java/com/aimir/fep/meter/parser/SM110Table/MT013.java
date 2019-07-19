/** 
 * @(#)MT013.java       1.0 06/12/14 *
 * 
 * Phase Angle Multipliers Class.
 * Copyright (c) 2006-2007 NuriTelecom, Inc.
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
 * @author Park YeonKyoung goodjob@nuritelecom.com
 */
public class MT013 {
	
	private final int OFS_PHASE_A_VOLT_MULT    = 0;
	private final int OFS_PHASE_A_CURRENT_MULT = 2;
	private final int OFS_PHASE_B_VOLT_MULT    = 4;
	private final int OFS_PHASE_B_CURRENT_MULT = 6;
	private final int OFS_PHASE_C_VOLT_MULT    = 8;
	private final int OFS_PHASE_C_CURRENT_MULT = 10;
	
	private final int LEN_PHASE_A_VOLT_MULT    = 2;
	private final int LEN_PHASE_A_CURRENT_MULT = 2;
	private final int LEN_PHASE_B_VOLT_MULT    = 2;
	private final int LEN_PHASE_B_CURRENT_MULT = 2;
	private final int LEN_PHASE_C_VOLT_MULT    = 2;
	private final int LEN_PHASE_C_CURRENT_MULT = 2;
		
	private byte[] data;
	/**
	 * Constructor .<p>
	 * @param data - read data (header,crch,crcl)
	 */
	public MT013(byte[] data) {
		this.data = data;
	}
	
	public int getPHASE_A_VOLT_MULT() throws Exception {
		return DataFormat.hex2dec(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,OFS_PHASE_A_VOLT_MULT,LEN_PHASE_A_VOLT_MULT)));
	}
	
	public int getPHASE_A_CURRENT_MULT() throws Exception {
		return DataFormat.hex2dec(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,OFS_PHASE_A_CURRENT_MULT,LEN_PHASE_A_CURRENT_MULT)));
	}
	
	public int getPHASE_B_VOLT_MULT() throws Exception {
		return DataFormat.hex2dec(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,OFS_PHASE_B_VOLT_MULT,LEN_PHASE_B_VOLT_MULT)));
	}
	
	public int getPHASE_B_CURRENT_MULT() throws Exception {
		return DataFormat.hex2dec(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,OFS_PHASE_B_CURRENT_MULT,LEN_PHASE_B_CURRENT_MULT)));
	}
	
	public int getPHASE_C_VOLT_MULT() throws Exception {
		return DataFormat.hex2dec(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,OFS_PHASE_C_VOLT_MULT,LEN_PHASE_C_VOLT_MULT)));
	}
	
	public int getPHASE_C_CURRENT_MULT() throws Exception {
		return DataFormat.hex2dec(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,OFS_PHASE_C_CURRENT_MULT,LEN_PHASE_C_CURRENT_MULT)));
	}

}
