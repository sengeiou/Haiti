/** 
 * @(#)ST063.java       1.0 06/12/14 *
 * 
 * Load Profile Status.
 * Copyright (c) 2006-2007 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.SM110Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;

/**
 * @author Park YeonKyoung goodjob@nuritelecom.com
 */
public class ST063 implements java.io.Serializable {

	private static final long serialVersionUID = 7483690333947090633L;
	private final int OFS_LP_SET_STATUS_FLAG = 0;
	private final int OFS_NBR_VALID_BLOCKS   = 1;
	private final int OFS_LAST_BLOCK_ELEMENT = 3;
	private final int OFS_LAST_BLOCK_SEQ_NUM = 5;
	private final int OFS_NBR_UNREAD_BLOCKS  = 9;
	private final int OFS_NBR_VALID_INT      = 11;
	
	private final int LEN_LP_SET_STATUS_FLAG = 1;
	private final int LEN_NBR_VALID_BLOCKS   = 2;
	private final int LEN_LAST_BLOCK_ELEMENT = 2;
	private final int LEN_LAST_BLOCK_SEQ_NUM = 4;
	private final int LEN_NBR_UNREAD_BLOCKS  = 2;
	private final int LEN_NBR_VALID_INT      = 2;
	
	private byte[] data;
    private static Log log = LogFactory.getLog(ST063.class);
    
    public ST063() {}
    
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public ST063(byte[] data) {
		this.data = data;
	}
	
	public int getNBR_VALID_BLOCKS() throws Exception {
		return DataFormat.hex2unsigned16(
				DataFormat.LSB2MSB(
					DataFormat.select(
						data,OFS_NBR_VALID_BLOCKS,LEN_NBR_VALID_BLOCKS)));
	}
	
	public int getNBR_VALID_INT() throws Exception {
		return DataFormat.hex2unsigned16(
				DataFormat.LSB2MSB(
					DataFormat.select(
						data,OFS_NBR_VALID_INT,LEN_NBR_VALID_INT)));
	}
	
	
	public boolean isBlockOrderDescending(){
		
		byte statusFlag = data[OFS_LP_SET_STATUS_FLAG];
		
		if((statusFlag & 0x01) > 0){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean isIntervalOrderDescending(){
		
		byte statusFlag = data[OFS_LP_SET_STATUS_FLAG];
		if((statusFlag & 0x10) > 0){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean isActiveMode(){
		byte statusFlag = data[OFS_LP_SET_STATUS_FLAG];
		if((statusFlag & 0x20) > 0){
			return true;
		}else{
			return false;
		}
	}

}
