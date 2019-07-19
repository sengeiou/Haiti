/** 
 * @(#)MT072.java       1.0 06/12/14 *
 * 
 * Line-Side diagnostics/Power Quality Data Table Class.
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
public class MT072 {
	
	public static final int OFS_CURRENT_ANGLE_PHA    = 0;
	public static final int OFS_VOLTAGE_ANGLE_PHA    = 2;
	public static final int OFS_CURRENT_ANGLE_PHB    = 4;
	public static final int OFS_VOLTAGE_ANGLE_PHB    = 6;
	public static final int OFS_CURRENT_ANGLE_PHC    = 8;
	public static final int OFS_VOLTAGE_ANGLE_PHC    = 10;
	
	public static final int OFS_CURRENT_MAG_PHA      = 12;
	public static final int OFS_VOLTAGE_MAG_PHA      = 14;
	public static final int OFS_CURRENT_MAG_PHB      = 16;
	public static final int OFS_VOLTAGE_MAG_PHB      = 18;
	public static final int OFS_CURRENT_MAG_PHC      = 20;
	public static final int OFS_VOLTAGE_MAG_PHC      = 22;	
	
	public static final int OFS_DU_PF                = 24;
	
	public static final int OFS_DIAG1_COUNTERS       = 25;
	public static final int OFS_DIAG2_COUNTERS       = 26;
	public static final int OFS_DIAG3_COUNTERS       = 27;
	public static final int OFS_DIAG4_COUNTERS       = 28;
	public static final int OFS_DIAG5_PHA_COUNTERS   = 29;
	public static final int OFS_DIAG5_PHB_COUNTERS   = 30;
	public static final int OFS_DIAG5_PHC_COUNTERS   = 31;
	public static final int OFS_DIAG5_TOTAL_COUNTERS = 32;
	public static final int OFS_DIAG6_COUNTERS       = 33;
	public static final int OFS_DIAG7_COUNTERS       = 34;
	public static final int OFS_DIAG8_COUNTERS       = 35;
	
	public static final int OFS_DIAG_CAUTIONS        = 36;
	
	public static final int LEN_CURRENT_ANGLE_PHA    = 2;
	public static final int LEN_VOLTAGE_ANGLE_PHA    = 2;
	public static final int LEN_CURRENT_ANGLE_PHB    = 2;
	public static final int LEN_VOLTAGE_ANGLE_PHB    = 2;
	public static final int LEN_CURRENT_ANGLE_PHC    = 2;
	public static final int LEN_VOLTAGE_ANGLE_PHC    = 2;
	
	public static final int LEN_CURRENT_MAG_PHA      = 2;
	public static final int LEN_VOLTAGE_MAG_PHA      = 2;
	public static final int LEN_CURRENT_MAG_PHB      = 2;
	public static final int LEN_VOLTAGE_MAG_PHB      = 2;
	public static final int LEN_CURRENT_MAG_PHC      = 2;
	public static final int LEN_VOLTAGE_MAG_PHC      = 2;
	
	public static final int LEN_DU_PF                = 1;
	
	public static final int LEN_DIAG1_COUNTERS       = 1;
	public static final int LEN_DIAG2_COUNTERS       = 1;
	public static final int LEN_DIAG3_COUNTERS       = 1;
	public static final int LEN_DIAG4_COUNTERS       = 1;
	public static final int LEN_DIAG5_PHA_COUNTERS   = 1;
	public static final int LEN_DIAG5_PHB_COUNTERS   = 1;
	public static final int LEN_DIAG5_PHC_COUNTERS   = 1;
	public static final int LEN_DIAG5_TOTAL_COUNTERS = 1;
	public static final int LEN_DIAG6_COUNTERS       = 1;
	public static final int LEN_DIAG7_COUNTERS       = 1;
	public static final int LEN_DIAG8_COUNTERS       = 1;
	
	public static final int LEN_DIAG_CAUTIONS        = 1;
	
	private byte[] data;

	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public MT072(byte[] data) {
		this.data = data;
	}


	public int getCURRENT_ANGLE_PHA() throws Exception {
		return DataFormat.hex2dec(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,OFS_CURRENT_ANGLE_PHA,LEN_CURRENT_ANGLE_PHA)));
	}
	
	public int getVOLTAGE_ANGLE_PHA() throws Exception {
		return DataFormat.hex2dec(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,OFS_CURRENT_ANGLE_PHA,LEN_CURRENT_ANGLE_PHA)));
	}
	
	public int getCURRENT_ANGLE_PHB() throws Exception {
		return DataFormat.hex2dec(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,OFS_CURRENT_ANGLE_PHB,LEN_CURRENT_ANGLE_PHB)));
	}
	
	public int getVOLTAGE_ANGLE_PHB() throws Exception {
		return DataFormat.hex2dec(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,OFS_VOLTAGE_ANGLE_PHB,LEN_VOLTAGE_ANGLE_PHB)));
	}
	
	public int getCURRENT_ANGLE_PHC() throws Exception {
		return DataFormat.hex2dec(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,OFS_CURRENT_ANGLE_PHC,LEN_CURRENT_ANGLE_PHC)));
	}
	
	public int getVOLTAGE_ANGLE_PHC() throws Exception {
		return DataFormat.hex2dec(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,OFS_VOLTAGE_ANGLE_PHC,LEN_VOLTAGE_ANGLE_PHC)));
	}

	public int getCURRENT_MAG_PHA() throws Exception {
		return DataFormat.hex2dec(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,OFS_CURRENT_MAG_PHA,LEN_CURRENT_MAG_PHA)));
	}
	
	public int getVOLTAGE_MAG_PHA() throws Exception {
		return DataFormat.hex2dec(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,OFS_VOLTAGE_MAG_PHA,LEN_VOLTAGE_MAG_PHA)));
	}
	
	public int getCURRENT_MAG_PHB() throws Exception {
		return DataFormat.hex2dec(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,OFS_CURRENT_MAG_PHB,LEN_CURRENT_MAG_PHB)));
	}
	
	public int getVOLTAGE_MAG_PHB() throws Exception {
		return DataFormat.hex2dec(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,OFS_VOLTAGE_MAG_PHB,LEN_VOLTAGE_MAG_PHB)));
	}
	
	public int getCURRENT_MAG_PHC() throws Exception {
		return DataFormat.hex2dec(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,OFS_CURRENT_MAG_PHC,LEN_CURRENT_MAG_PHC)));
	}
	
	public int getVOLTAGE_MAG_PHC() throws Exception {
		return DataFormat.hex2dec(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,OFS_VOLTAGE_MAG_PHC,LEN_VOLTAGE_MAG_PHC)));
	}

	public int getDU_PF() throws Exception {
		return  DataFormat.hex2unsigned8(data[OFS_DU_PF]);
	}
	
	public int getDIAG1_COUNTERS() throws Exception {
		return  DataFormat.hex2unsigned8(data[OFS_DIAG1_COUNTERS]);
	}
	
	public int getDIAG2_COUNTERS() throws Exception {
		return  DataFormat.hex2unsigned8(data[OFS_DIAG2_COUNTERS]);
	}
	
	public int getDIAG3_COUNTERS() throws Exception {
		return  DataFormat.hex2unsigned8(data[OFS_DIAG3_COUNTERS]);
	}
	
	public int getDIAG4_COUNTERS() throws Exception {
		return  DataFormat.hex2unsigned8(data[OFS_DIAG4_COUNTERS]);
	}
	
	public int getDIAG5_PHA_COUNTERS() throws Exception {
		return  DataFormat.hex2unsigned8(data[OFS_DIAG5_PHA_COUNTERS]);
	}
	
	public int getDIAG5_PHB_COUNTERS() throws Exception {
		return  DataFormat.hex2unsigned8(data[OFS_DIAG5_PHB_COUNTERS]);
	}
	
	public int getDIAG5_PHC_COUNTERS() throws Exception {
		return  DataFormat.hex2unsigned8(data[OFS_DIAG5_PHC_COUNTERS]);
	}
	
	public int getDIAG5_TOTAL_COUNTERS() throws Exception {
		return  DataFormat.hex2unsigned8(data[OFS_DIAG5_TOTAL_COUNTERS]);
	}
	
	public int getDIAG6_COUNTERS() throws Exception {
		return  DataFormat.hex2unsigned8(data[OFS_DIAG6_COUNTERS]);
	}
	
	public int getDIAG7_COUNTERS() throws Exception {
		return  DataFormat.hex2unsigned8(data[OFS_DIAG7_COUNTERS]);
	}
	
	public int getDIAG8_COUNTERS() throws Exception {
		return  DataFormat.hex2unsigned8(data[OFS_DIAG8_COUNTERS]);
	}
	
}
