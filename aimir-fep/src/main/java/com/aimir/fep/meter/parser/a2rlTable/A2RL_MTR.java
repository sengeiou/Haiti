/* 
 * @(#)A2RL_MTR.java       1.0 08/10/27 *
 * 
 * Meter Information
 * Copyright (c) 2007-2008 NuriTelecom, Inc.
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
package com.aimir.fep.meter.parser.a2rlTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;
import com.aimir.util.DateTimeUtil;

/**
 * @author Kang Soyi ksoyi@nuritelecom.com
 */
public class A2RL_MTR {    
    
    public static final int OFS_SERIAL_NUMBER = 0;
    public static final int OFS_METER_CONSTANT = 5;
    public static final int OFS_METER_WARNING = 10;
    public static final int OFS_METER_ERROR = 11;
    public static final int OFS_METER_STATUS = 14;
    public static final int OFS_METER_ELEMENT = 15;
    public static final int OFS_CT_RATIO = 16;
    public static final int OFS_VT_RATIO = 19;
    public static final int OFS_DEMAND_FORMAT = 22;
    public static final int OFS_ENERGY_FORMAT = 24;
    public static final int OFS_DATETIME = 26;
    
	public static final int LEN_SERIAL_NUMBER = 5;
	public static final int LEN_METER_CONSTANT = 5;
	public static final int LEN_METER_WARNING = 1;
	public static final int LEN_METER_ERROR = 3;
    public static final int LEN_METER_STATUS = 1;
    public static final int LEN_METER_ELEMENT = 1;
    public static final int LEN_CT_RATIO = 3;
    public static final int LEN_VT_RATIO = 3;
    public static final int LEN_DEMAND_FORMAT = 2;
    public static final int LEN_ENERGY_FORMAT = 2;
    public static final int LEN_DATETIME = 6;
    
	private byte[] rawData = null;

    private Log log = LogFactory.getLog(A2RL_MTR.class);
    
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public A2RL_MTR(byte[] rawData) {
        this.rawData = rawData;
	}
    
	public String getMeterSerial() throws Exception {
		return DataFormat.bcd2str(rawData, OFS_SERIAL_NUMBER, LEN_SERIAL_NUMBER).substring(3);
	}
	
	public double getMETER_CONSTANT() {
		double meterConstant =0.0;
		
		try{
			meterConstant = (DataFormat.bcd2dec(rawData, OFS_METER_CONSTANT, LEN_METER_CONSTANT))*0.000001;
		}catch(Exception e){
			 log.warn("METER_CONSTANT->"+e.getMessage());
		}
		
		return  meterConstant;
	}
	
	public int getMETER_ELEMENT() throws Exception{
		return DataFormat.bcd2dec(rawData, OFS_METER_ELEMENT, LEN_METER_ELEMENT); 
	}
	
	public Double getCT_RATIO() throws Exception{
		return new Double((DataFormat.bcd2dec(rawData, OFS_CT_RATIO, LEN_CT_RATIO))*0.01); 
	}
	
	public Double getVT_RATIO() throws Exception{ 
		return new Double((DataFormat.bcd2dec(rawData, OFS_VT_RATIO, LEN_VT_RATIO))*0.01);
	}
	
	public int getDEMAND_FORMAT() throws Exception{
		return (DataFormat.hex2unsigned16(DataFormat.select(rawData, OFS_DEMAND_FORMAT, LEN_DEMAND_FORMAT))); 
	}
	
	public int getENERGY_FORMAT() throws Exception{
		return (DataFormat.hex2unsigned16(DataFormat.select(rawData, OFS_ENERGY_FORMAT, LEN_ENERGY_FORMAT))); 
	}
	
    public MeterWarningFlag getMeterWarningFlag() throws Exception{
       return new MeterWarningFlag(rawData[OFS_METER_WARNING]);
    }
    
    public MeterErrorFlag getMeterErrorFlag()  throws Exception {
       return new MeterErrorFlag(DataFormat.select(rawData, OFS_METER_ERROR, LEN_METER_ERROR));
    }
    
    public MeterStatusFlag getMeterStatusFlag() throws Exception{
       return new MeterStatusFlag(rawData[OFS_METER_STATUS]);
    }    
   
    public String getDateTime(){
        String date="";
        try{
           date = getDateTime(DataFormat.select(rawData, OFS_DATETIME, LEN_DATETIME));
        }catch(Exception e){
            log.warn("DateTime->"+e.getMessage());
        }
        return date;
    }
    
    public String getMETER_ID(){
        String id = "";
        try{
            id =  getMeterSerial();
        }catch(Exception e){
            log.warn("METER_ID->"+e.getMessage());
        }
        
        return id;
    }
    
	public String getDateTime(byte[] datetime) throws Exception {

		String s =  DataFormat.bcd2str(datetime);	
		
		int curryy = (Integer.parseInt(DateTimeUtil
                .getCurrentDateTimeByFormat("yyyy"))/100)*100;
		
		int year   = Integer.parseInt(s.substring( 0,2));
		if(year != 0){
			year   = curryy+Integer.parseInt(s.substring( 0,2));
		}
		
		return ""+year+""+s.substring(2);
	}
	
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("A2RL_MTR DATA[");        
            sb.append("(MeterSerial =").append(getMeterSerial().toString()).append("),");
            sb.append("(METER_CONSTANT=").append(""+getMETER_CONSTANT()).append("),");
            sb.append("(METER_ELEMENT=").append(""+getMETER_ELEMENT()).append("),");
            sb.append("(CT_RATIO=").append(""+getCT_RATIO()).append("),");
            sb.append("(VT_RATIO=").append(""+getVT_RATIO()).append("),");
            sb.append("(DEMAND_FORMAT=").append(getDEMAND_FORMAT()).append("),");
            sb.append("(ENERGY_FORMAT=").append(getENERGY_FORMAT()).append("),");
            sb.append("(MeterWarningFlag=").append(getMeterWarningFlag().toString()).append("),");
            sb.append("(MeterErrorFlag=").append(getMeterErrorFlag().toString()).append("),");
            sb.append("(MeterStatusFlag=").append(getMeterStatusFlag().toString()).append(')');
            sb.append("]\n");
        }catch(Exception e){
            log.warn("A2RL_MTR TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }

}
