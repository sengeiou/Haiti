/** 
 * @(#)NURI_T001.java       1.0 07/01/29 *
 * 
 * Communication Statistics Data Class.
 * Copyright (c) 2007-2008 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser;

/**
 * @author Park YeonKyoung goodjob@nuritelecom.com
 */
public class NURI_T001 implements java.io.Serializable {

	private static final long serialVersionUID = 3410472630063827526L;
	byte[] data;
	
    public NURI_T001() {}
    
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public NURI_T001(byte[] data) {
		this.data = data;
	}

}