/** 
 * @(#)A2RL_LP.java.java       1.0 08/10/23 *
 * 
 * Previous A2RL_LP Data Class.
 * Copyright (c) 2006-2007 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.a2rlTable;

import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.parser.lgrw3410Table.LPComparator;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;
import com.aimir.util.DateTimeUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Kang SoYi ksoyi@nuritelecom.com
 */
public class A2RL_LP {
    
	public static final int OFS_CURRENT_ACTIVE=0;
	public static final int OFS_CURRENT_REACTIVE=7;
	
	public static final int OFS_LP_START_DATETIME=14;
	public static final int OFS_PULSE=20;
	
	public static final int LEN_CURRENT_ACTIVE=7;
	public static final int LEN_CURRENT_REACTIVE=7;
	
	public static final int LEN_LP_START_DATETIME=6;
	public static final int LEN_PULSE=2;
	public static final int LEN_LP_DATA_SET = 4;
	
	public static final int MASK_LP_DATA = 0x7fff;
	public static final int MASK_EVENT_FLAG = 0x8000;
	
	public static final int DASIZE 		= 390; //lp day block size
	public static final int NBR_OF_LP_A_DAY = 96; 
	
	private byte[] rawData = null;
    private double ct=1;
    private double vt=1;
    private int resolution=15;
    private double ke =1.0;
               
    private static Log log = LogFactory.getLog(A2RL_LP.class);
    DecimalFormat dformat = new DecimalFormat("###############0.000000");
    
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public A2RL_LP(byte[] rawData, double ke, double ct, double vt) {
		this.rawData = rawData;
        this.ke = ke;
        this.ct = ct;
        this.vt = vt;
	}

	public LPData[] parse() throws Exception {
		
        log.debug("===============LP Parse Start=================");

        ArrayList<LPData> list = new ArrayList<LPData>();
                
        int lpPacketLength = rawData.length - OFS_LP_START_DATETIME;
        int dayCnt =lpPacketLength/DASIZE;
        if(lpPacketLength%DASIZE>0)
        	dayCnt+=1;
        
        log.debug("ke ="+ke);
        
        log.debug("dayCnt ="+dayCnt);
        
        //LPData[] nbr_LP = new LPData[lpPacketLength - 6*dayCnt];
        int nbr_LP = lpPacketLength - 6*dayCnt;
        log.debug("nbr_LP ="+nbr_LP);

        int ofs = OFS_LP_START_DATETIME;
        for(int j=0; j<dayCnt; j++){

        	String entryDateTime =getLPTime(DataFormat.select(rawData, ofs, LEN_LP_START_DATETIME));
        	ofs += LEN_LP_START_DATETIME;
        	
        	int len = rawData.length - ofs;
        	int cnt_of_lp = NBR_OF_LP_A_DAY;
        	
        	if(len <DASIZE)
        		cnt_of_lp = len/4;
        	
        	log.debug("cnt_of_lp :"+cnt_of_lp);
        	
        	for(int i = 0; i < cnt_of_lp ; i++){
	            byte[] blk = new byte[LEN_LP_DATA_SET];            
	            blk =DataFormat.select(rawData,ofs,LEN_LP_DATA_SET);
	            ofs += LEN_LP_DATA_SET;
	            LPData lp = null;
	            lp = parseChannel(blk, i, entryDateTime);
	            if(lp!=null)
	            	list.add(lp);
	        }
        }
        
        log.debug("list.size() :"+list.size());
        
        Collections.sort(list,LPComparator.TIMESTAMP_ORDER);     
        if(list != null && list.size() > 0){
            LPData[] data = null;
            Object[] obj = list.toArray();            
            data = new LPData[obj.length];
            for(int i = 0; i < obj.length; i++){
                data[i] = (LPData)obj[i];
            }
            return data;
        }
        else
        {
            return null;
        }
       // log.debug("=================LP Parse End=================");
	}

	public int getINTERVAL_TIME(){
		return resolution;
	}
	
	public double getCurrentActive() throws Exception{
		return  Double.parseDouble(dformat.format((double)(DataFormat.bcd2dec(rawData,OFS_CURRENT_ACTIVE,LEN_CURRENT_ACTIVE))*0.000001));
	}
	public double getCurrentReactive() throws Exception{
		return  Double.parseDouble(dformat.format((double)(DataFormat.bcd2dec(rawData,OFS_CURRENT_REACTIVE,LEN_CURRENT_REACTIVE))*0.000001));
	}
	
    private LPData parseChannel(byte[] block, int idx, String entryDateTime) throws Exception{

        LPData lpdata = new LPData();
        
        Double[] ch  = new Double[2];
        Double[] v  = new Double[2];

        String lpDate=Util.addMinYymmdd(entryDateTime.substring(0,12), resolution*idx);
        log.debug("lpDate :"+lpDate);
        for(int i = 0; i < 2; i++){
        	double pulse = (double) (DataFormat.hex2unsigned16( DataFormat.select(
                    block, i*LEN_PULSE, LEN_PULSE)) & MASK_LP_DATA) *ke;
        	if(pulse <= 0x7ffd){ //normal
        		ch[i] =  new Double(dformat.format(pulse));
 	            v[i] = new Double(dformat.format(ch[i]*((double)60/resolution)));
        	}
        	if(pulse == 0x7ffe){
        		return null;
        		/*
        		log.debug("overflowed pulse : "+ pulse);
        		pulse =0.0;
        		ch[i] =0.0;
        		v[i] = 0.0;
        		*/
        	}else if(pulse == 0x7fff){
        		return null;
        		/*
        		log.debug("initialized pulse : "+ pulse);
        		pulse =0.0;
        		ch[i] =0.0;
        		v[i] = 0.0;
        		*/
        	}
        }
        
        lpdata.setV(v);
        lpdata.setCh(ch);
        lpdata.setFlag(0);
        lpdata.setPF(getPF(ch[0], ch[1]));
        lpdata.setDatetime(lpDate);

        return lpdata;
    }

	public String getLPTime(byte[] datetime) throws Exception {

		log.debug("datetime=>"+Util.getHexString(datetime));
		
		String s =  DataFormat.bcd2str(DataFormat.select(datetime, 0,3));	
		log.debug("datetime string =>"+s);
		
		int curryy = (Integer.parseInt(DateTimeUtil
                .getCurrentDateTimeByFormat("yyyy"))/100)*100;
		int year   = Integer.parseInt(s.substring(0,2));
		year   += curryy;

		String lpDateTime = Util.frontAppendNStr('0',Integer.toString(year),4)+s.substring(2)+"001500";
				
		return lpDateTime;
	}
		
	private double getPF(double ch1, double ch2) throws Exception {

        double pf   = (float)(ch1/(Math.sqrt(Math.pow(ch1,2)+Math.pow(ch2,2))));

        if(ch1 == 0.0 && ch2 == 0.0)
            pf = (double) 1.0;
        /* Parsing Transform Results put Data Class */
        if(pf < 0.0 || pf > 1.0)
            throw new Exception("BILL PF DATA FORMAT ERROR : "+pf);
        return pf;
	}
}
