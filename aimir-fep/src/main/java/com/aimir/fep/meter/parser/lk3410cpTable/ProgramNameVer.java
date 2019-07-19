/** 
 * @(#)ST021.java       1.0 07/11/09 *
 * 
 * Actual Dimension Register Table.
 * Copyright (c) 2006-2007 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.lk3410cpTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;

/**
 * @author Kang SoYi ksoyi@nuritelecom.com
 */
public class ProgramNameVer {
	
	public static final int OFS_PROGRAM_NAME  = 0;
	public static final int OFS_PROGRAM_VER  = 4;
    
    public static final int LEN_PROGRAM_NAME  = 4;
    public static final int LEN_PROGRAM_VER  = 4;
        
	private byte[] data;
    private static Log log = LogFactory.getLog(ProgramNameVer.class);
	
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public ProgramNameVer(byte[] data) {
		this.data = data;
	}
	
	/**
	 * get Program Name
	 */
	public String getProgramName()  throws Exception {
		return new String(DataFormat.select(
                          data,OFS_PROGRAM_NAME,LEN_PROGRAM_NAME)).trim();
	}
	
	/**
	 * get Progrma Version
	 */
	public String getProgramVersion()  throws Exception {
		return new String(DataFormat.select(
                          data,OFS_PROGRAM_VER,LEN_PROGRAM_VER)).trim();
	}
	
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("ProgramNameVer DATA[");        
            sb.append("(ProgramName=").append(getProgramName()).append("),");
            sb.append("(ProgrmaVersion=").append(getProgramVersion()).append(')');
            sb.append("]\n");
        }catch(Exception e){
            log.warn("ProgramNameVer TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }


}
