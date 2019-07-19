/** 
 * @(#)ST072.java       1.0 07/01/29 *
 * 
 * Event Log Actual Dimension Log Table.
 * Copyright (c) 2007-2008 NuriTelecom, Inc.
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

/**
 * @author Park YeonKyoung goodjob@nuritelecom.com
 */
public class ST072 implements java.io.Serializable {

	private static final long serialVersionUID = 3345767272117723596L;

	private byte[] data;

    private static Log log = LogFactory.getLog(ST072.class);
	
    public ST072() {}
    
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public ST072(byte[] data) {
		this.data = data;
	}

}
