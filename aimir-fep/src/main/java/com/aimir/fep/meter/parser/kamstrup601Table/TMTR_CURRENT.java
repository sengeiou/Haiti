/* 
 * @(#)TMTR_CURRENT.java       1.0 2008-06-02 *
 * 
 * Meter CURRENT Data (Instantenous)
 * Copyright (c) 2008-2009 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
/**
 * @author YK.Park
 */
package com.aimir.fep.meter.parser.kamstrup601Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.HMData;

public class TMTR_CURRENT extends TMTR_LP {     

    public static String TABLE_KIND = "CURRENT";
    public static final int TABLE_CODE = 3;
	public static final int LEN_METER_ID = 8;

	public static final int OFS_CURRENT_METER_DATETIME= 0;
	
	private byte[] rawData = null;

    private static Log log = LogFactory.getLog(TMTR_CURRENT.class);
    
    private HMData[] hmData = null;
    
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public TMTR_CURRENT(byte[] rawData,String table_kind) {
        super(rawData,table_kind);
	}
        
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("TMTR_CURRENT DATA["); 
            for(int i = 0; i < hmData.length; i++){
                sb.append(hmData[i].toString());
            }
            sb.append("]\n");
        }catch(Exception e){
            log.warn("TMTR_CURRENT TO STRING ERR=>"+e.getMessage());
        }
        return sb.toString();
    }
}