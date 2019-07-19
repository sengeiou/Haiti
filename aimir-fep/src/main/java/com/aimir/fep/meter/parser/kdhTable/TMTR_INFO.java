/* 
 * @(#)TMTR_INFO.java       1.0 2008-06-02 *
 * 
 * Meter Information
 * Copyright (c) 2008-2009 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
/**
 * @author Kang SoYi ksoyi@nuritelecom.com
 */
package com.aimir.fep.meter.parser.kdhTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;
import com.aimir.util.DateTimeUtil;

/**
 * @author YK.Park
 */
public class TMTR_INFO implements java.io.Serializable{    

    public static final int LEN_TMTR_INFO = 17;
	public static final int LEN_METER_ID = 8;
	public static final int LEN_METER_PULSE = 1;//reserved
	public static final int LEN_METER_TYPE = 1;
	public static final int LEN_METER_STATUS = 1;
	public static final int LEN_CURRENT_METER_DATETIME= 6;

	public static final int OFS_METER_ID = 0;
    public static final int OFS_METER_PULSE = 8;
    public static final int OFS_METER_TYPE = 9;
    public static final int OFS_METER_STATUS = 10;
	public static final int OFS_CURRENT_METER_DATETIME= 11;
	
	private byte[] rawData = null;

    private static Log log = LogFactory.getLog(TMTR_INFO.class);
    
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public TMTR_INFO(byte[] rawData) {
        this.rawData = rawData;
	}
    
    public int getMETER_TYPE(){
         int kind = rawData[OFS_METER_TYPE]& 0xFF;
         return kind;
    }
    
    public String getMETER_TYPE_NAME()
    {
        int kind = getMETER_TYPE();
        switch(kind)
        {
            case 6: return "(included communication module) commercial heat meter";
            case 7: return "(excluded communication module) commercial heat meter";
        }
        return "unknown["+kind+"]";
    }
    public String getCURRENT_METER_DATETIME(){

        int idx = OFS_CURRENT_METER_DATETIME;

        int yy = rawData[idx++] & 0xFF;
        int mm = rawData[idx++] & 0xFF;
        int dd = rawData[idx++] & 0xFF;
        int hh = rawData[idx++] & 0xFF;
        int MM = rawData[idx++] & 0xFF;
        int ss = rawData[idx++] & 0xFF;

        StringBuffer ret = new StringBuffer();

        int currcen = (Integer.parseInt(DateTimeUtil
                .getCurrentDateTimeByFormat("yyyy"))/100)*100;

        int year   = yy;
        if(year != 0){
        year = yy + currcen;
        }

        ret.append(Util.frontAppendNStr('0',Integer.toString(year),4));
        ret.append(Util.frontAppendNStr('0',Integer.toString(mm),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(dd),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(hh),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(MM),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(ss),2));
        return ret.toString();
    }
     
    public MeterCautionFlag getMeterCautionFlag() throws Exception {
       return new MeterCautionFlag(rawData[OFS_METER_STATUS]);
    }    
   
    public String getDateTime(){
        String date="";
        try{
           date = getCURRENT_METER_DATETIME();
        }catch(Exception e){
            log.warn("DateTime->"+e.getMessage());
        }
        return date;
    }
    
    public String getMETER_ID(){
        String id = "";
        try{
            id =  new String(DataFormat.select(
                                    rawData,OFS_METER_ID,LEN_METER_ID)).trim();
        }catch(Exception e){
            log.warn("METER_ID->"+e.getMessage());
        }
        
        return id;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("TMTR_INFO DATA[");        
            sb.append("(CURRENT_METER_DATETIME=").append(getCURRENT_METER_DATETIME()).append("),");
            sb.append("(Meter Id=").append(getMETER_ID()).append("),");
            sb.append("(Meter Type=").append(getMETER_TYPE_NAME()).append(')');
            sb.append("(MeterCautionFlag=").append(getMeterCautionFlag().toString()).append(')');            
            sb.append("]\n");
        }catch(Exception e){
            log.warn("TMTR_INFO TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }

}
