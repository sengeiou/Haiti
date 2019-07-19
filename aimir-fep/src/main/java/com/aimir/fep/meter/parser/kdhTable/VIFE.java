/* 
 * @(#)VIFE.java       1.0 2008-06-02 *
 * 
 * Variable Information
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
package com.aimir.fep.meter.parser.kdhTable;

public class VIFE {    

    public static final int CODE_UNKNOWN = -1;
    public static final int CODE_HOUR  = 0;
    public static final int CODE_DAY   = 1;
    public static final int CODE_MONTH = 2;
    public static final int CODE_CURRENT = 3;


    public static final String[] VIFE_TABLE = {
        "Hour",
        "Day", 
        "Month",
        "Current"
    };
	
	private byte rawData = 0x00;

	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public VIFE(byte rawData) {
        this.rawData = rawData;
	}
    
    public int getVIFE()
    {
        int vife = rawData & 0x0F;
        
        switch(vife){
            case CODE_HOUR  : 
            case CODE_DAY   :
            case CODE_MONTH :
            case CODE_CURRENT :
            return vife;
        }
        return CODE_UNKNOWN;        
    }

    public String toString()
    {
        int vife = getVIFE();
        
        switch(vife){
        case CODE_HOUR  : return VIFE_TABLE[CODE_HOUR];
        case CODE_DAY   : return VIFE_TABLE[CODE_DAY];
        case CODE_MONTH : return VIFE_TABLE[CODE_MONTH];
        case CODE_CURRENT : return VIFE_TABLE[CODE_CURRENT];
        }
        return "unknown["+vife+"]";        
    }

}
