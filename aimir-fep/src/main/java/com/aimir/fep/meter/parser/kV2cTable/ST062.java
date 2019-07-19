/** 
 * @(#)ST062.java       1.0 06/12/14 *
 * 
 * Load Profile Control Table.
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
 * @author Park YeonKyoung goodjob@goodjob
 */
public class ST062 {
	
	private final int LEN_LP_SEL_SET1        = 3;
	private final int LEN_INT_FMT_CDE1       = 1;
	private final int LEN_SCALARS_SET1       = 2;
	private final int LEN_DIVISOR_SET1       = 2;

	private byte[] data;
	private int channels;
	
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public ST062(byte[] data, int channels) {
		this.data = data;
		this.channels = channels;
	}
    
    /**
     * get lp channel kind
     * @return
     * @throws Exception
     */
    public int[] getLP_SEL_SET1() throws Exception {
        
        int offset = 0;
        int[] lp_sel = new int[this.channels];
        byte[] temp = new byte[LEN_LP_SEL_SET1];
        
        for(int i = 0; i < this.channels; i++){
            temp = DataFormat.select(data,offset,LEN_LP_SEL_SET1);
            offset += LEN_LP_SEL_SET1;
            lp_sel[i] = DataFormat.hex2unsigned8(temp[1]);
        }
        return lp_sel;
    }
	
	public int getSCALAR(int chanid) throws Exception {
		
		int offset = LEN_LP_SEL_SET1*this.channels
                   + LEN_INT_FMT_CDE1
		           + LEN_SCALARS_SET1*chanid;
		           
		return DataFormat.hex2signed16(
			DataFormat.LSB2MSB(
				DataFormat.select(data,offset,LEN_SCALARS_SET1)));
		
	}
	
	public int getDIVISOR(int chanid) throws Exception {
		
		int offset = LEN_LP_SEL_SET1*this.channels
                   + LEN_INT_FMT_CDE1
		           + LEN_SCALARS_SET1*this.channels
				   + LEN_DIVISOR_SET1*chanid;
		           
		return DataFormat.hex2signed16(
			DataFormat.LSB2MSB(
				DataFormat.select(data,offset,LEN_DIVISOR_SET1)));
		
	}
	
	
	public byte[] parseSCALAR(int chanid) throws Exception {
		
		int offset = LEN_LP_SEL_SET1*this.channels
				   + LEN_SCALARS_SET1*chanid;
		           
		return DataFormat.dec2hex((char)
			(DataFormat.hex2signed16(
			DataFormat.LSB2MSB(
				DataFormat.select(data,offset,LEN_SCALARS_SET1)))));
		
	}
	
	public byte[] parseDIVISOR(int chanid) throws Exception {
		
		int offset = LEN_LP_SEL_SET1*this.channels
				   + LEN_SCALARS_SET1*this.channels
				   + LEN_DIVISOR_SET1*chanid;
		           
		return DataFormat.dec2hex((char)
			(DataFormat.hex2signed16(
			DataFormat.LSB2MSB(
				DataFormat.select(data,offset,LEN_DIVISOR_SET1)))));
		
	}
	
}
