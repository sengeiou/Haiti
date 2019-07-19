/** 
 * @(#)Class15.java       1.0 04/09/17 *
 * 
 * ABB Meter Event Log Configuration Class.
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
public class Class15 {
	
	public static final int OFS_EVSIZE = 0;
	public static final int OFS_EVSEL1 = 2;
	
	public static final int LEN_EVSIZE = 2;
	public static final int LEN_EVSEL1 = 1; 
	
	private byte[] data;
    
	/**
	 * Constructor.
	 * @param data
	 */
	public Class15(byte[] data){
		this.data = data;	
	}
	
	/**
	 * avaliable write event log count.
	 * This menory dynamically allocate by Load Profile.
	 * LPMEM = (LPMEM-RULEN - (EVSIZE * 7)) + DASIZE
	 * @return
	 */
	public int parseEVSIZE() throws Exception {
		return DataFormat.hex2dec(data,OFS_EVSIZE,LEN_EVSIZE);
	}
	
	/**
	 * 
	 * Event Type.
	 * log demand resets flag. (event type 6)
	 * (1)true  = log demand resets.
	 * (0)false = do not log demand resets.
	 * @return
	 */
	public boolean isEVDR() {
		if((0x08&data[OFS_EVSEL1]) !=0 )
			return true;
		return false;
	}
	
	/**
	 * 
	 * log test mode flag event types 4 (start test mode) and 5 (end test mode)
	 * (1) true  = log test mode.
	 * (0) false = do not log test mode.  
	 * @return
	 */
	public boolean isEVTM() {
		if((0x04&data[OFS_EVSEL1]) !=0 )
			return true;
		return false;
	}
	
	/**
	 * 
	 * log time change events : event types 2 (before time change)
	 * and 3 (after time change).
	 * (1) true  = log time changes.
	 * (0) false = do not log time changes. 
	 * @return
	 */
	public boolean isEVTC() {
		if((0x02&data[OFS_EVSEL1]) !=0 )
			return true;
		return false;
	}
	
	/**
	 * 
	 * power fail events :  event types 0 (power fail start)
	 * and 1 (power fail end).
	 * 
	 * (1) true  = log power fails.
	 * (0) false = do not log power fails.
	 * @return
	 */
	public boolean isEVPF() {
		if((0x01&data[OFS_EVSEL1]) !=0 )
			return true;
		return false;
	}
	
}
