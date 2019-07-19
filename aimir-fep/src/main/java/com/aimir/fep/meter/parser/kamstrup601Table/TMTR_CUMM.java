/* 
 * @(#)TMTR_CUMM.java       1.0 2009-03-03 *
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
 * @author Kang, Soyi
 */
package com.aimir.fep.meter.parser.kamstrup601Table;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.HMData;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Util;

public class TMTR_CUMM implements java.io.Serializable{     

    public static String TABLE_KIND = "CUMM";
    public static final int TABLE_CODE = 5;

	public static final int OFS_DATE =0;
	public static final int OFS_CURR_DATE =4;
	
	public static final int LEN_DATE =4;
	public static final int LEN_CURR_DATA =4;
	
	private byte[] rawData = null;

    private static Log log = LogFactory.getLog(TMTR_CUMM.class);
    
    private HMData[] hmData = null;
    private double siEx = 0;
	private double flowSiEx = 0;
	
	/**
	 * Constructor .<p>
	 * 
	 * @param data - 
	 */
	public TMTR_CUMM(byte[] rawData, double siEx,  double flowSiEx) {
		this.rawData = rawData;
		this.siEx = siEx;
		this.flowSiEx = flowSiEx;
		try{
			this.hmData = parse();
	    }catch(Exception e)
	    {
	        log.error(e,e);
	    }
	}
        
	public HMData[] parse() throws Exception {

        ArrayList<HMData> list = new ArrayList<HMData>();

        int ofs = OFS_DATE;
        log.debug("siEx :"+ siEx);
        int channelCount = 1;
        
        if(rawData.length % (LEN_DATE + LEN_CURR_DATA + LEN_CURR_DATA) == 0){
        	channelCount = 2;
        }
        
        while(ofs<rawData.length){
        	String datetime = getDate(DataFormat.select(rawData,ofs,LEN_DATE));
        	ofs+=LEN_DATE;
    		HMData hm = new HMData();
    		hm.setDate(datetime);
    		hm.setChannelCnt(channelCount);
    		
    		if(channelCount == 2){        		
                hm.setCh(1, new Double(DataUtil.getIntToBytes(DataFormat.LSB2MSB(DataFormat.select(rawData,ofs,LEN_CURR_DATA)))*siEx));
                ofs += LEN_CURR_DATA;
    			hm.setCh(2, new Double(DataUtil.getIntToBytes(DataFormat.LSB2MSB(DataFormat.select(rawData,ofs,LEN_CURR_DATA)))*flowSiEx));
                ofs += LEN_CURR_DATA;
                list.add(hm);
    		}else{
                hm.setCh(1, new Double(DataUtil.getIntToBytes(DataFormat.LSB2MSB(DataFormat.select(rawData,ofs,LEN_CURR_DATA)))*siEx));
                ofs += LEN_CURR_DATA;
                list.add(hm);
    		}
        }
        
        if(list != null && list.size() > 0){
        	HMData[] data = null;
            Object[] obj = list.toArray();            
            data = new HMData[obj.length];
            for(int i = 0; i < obj.length; i++){
                data[i] = (HMData)obj[i];
            }
            return data;
        }
        else
        {
            return null;
        }
	}
	
	public String getDate(byte[] date){
		try{
			int idx=0;
			int year = DataFormat.hex2unsigned16(DataFormat.select(date, 0, 2));
	        idx =idx+2;
			int mm = DataFormat.hex2unsigned8(date[idx++]);
			int dd = DataFormat.hex2unsigned8(date[idx++]);
			
			StringBuffer ret = new StringBuffer();
			
			ret.append(Util.frontAppendNStr('0',Integer.toString(year),4));
			ret.append(Util.frontAppendNStr('0',Integer.toString(mm),2));
			ret.append(Util.frontAppendNStr('0',Integer.toString(dd),2));
			
			return ret.toString();
			
        }catch(Exception e)
        {
            log.error(e,e);
            return null;
        }
	}
    
    public HMData[] getData()
    {
        return this.hmData;
    }

	public void setData(int idx, Double ch1, Double ch2){
		this.hmData[idx].setCh(1, ch1 );
		this.hmData[idx].setCh(2, ch2 );
	}
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("TMTR_CUMM DATA["); 
            for(int i = 0; i < hmData.length; i++){
                sb.append(hmData[i].toString());
            }
            sb.append("]\n");
        }catch(Exception e){
            log.warn("TMTR_CUMM TO STRING ERR=>"+e.getMessage());
        }
        return sb.toString();
    }
}