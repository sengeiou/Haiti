/* 
 * @(#)LGRW3410_LP.java       1.0 08/03/31 *
 * 
 * Load Profile.
 * Copyright (c) 2007-2008 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.lgrw3410Table;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;

/**
 * @author Kang SoYi ksoyi@nuritelecom.com
 */
public class LGRW3410_LP {
    
	private byte[] rawData = null;
    private int regK=1;
    private int ctvt=1;
    private int resolution=15;
    private double ke =0.05;
    
    public static final int LEN_METER_ID=8;
	private final int LEN_METER_STATUS=4;
	
    public static final int LEN_LP_DATA_SET = 12;
   
    public static final int LEN_PULSE = 2;
    public static final int LEN_BATTERY_MISSING = 2;
    public static final int LEN_POWER_OUTAGE = 2;
    public static final int LEN_TIME_CHANGED = 2;
    public static final int LEN_PROGRAM_CHANGED = 2;
    public static final int LEN_LP_DATE = 4;
    
    public static final int OFS_METER_ID=0;
    public static final int OFS_METER_STATUS = 8;
    public static final int OFS_LP_DATA_SET =12;
    public static final int OFS_PULSE = 0;
    public static final int OFS_BATTERY_MISSING = 0;
    public static final int OFS_POWER_OUTAGE = 0;
    public static final int OFS_TIME_CHANGED = 4;
    public static final int OFS_PROGRAM_CHANGED = 4;
    public static final int OFS_LP_DATE = 8;
        
    public static final int MASK_LP_DATA = 0x3fff;
    public static final int MASK_BATTERY_MISSING = 0x80;
    public static final int MASK_POWER_OUTAGE = 0x40;
    
    public static final int MASK_TIME_CHANGED = 0x80;
    public static final int MASK_PROGRAM_CHANGED = 0x40;
       
    DecimalFormat dformat = new DecimalFormat("###############0.000000");
        
    private static Log log = LogFactory.getLog(LGRW3410_LP.class);
    
	/**
	 * Constructor .<p> 
	 * @param data - read data (header,crch,crcl)
	 */
	public LGRW3410_LP(byte[] rawData, int regK, double ke) {
		this.rawData = rawData;
        this.regK = regK;
        this.ke = ke;
      //  this.energyscale = energyscale;
	}
    
    /**
     * The time in minutes LP interval duration ST61
     * LP Interval Time
     * Default 15
     * (0,1,2,3,4,5,6,10,12,15,20,30,60)
     * @return
     */
    public int getINTERVAL_TIME() {
        return 15;
    }
	public MeterManufacture getMeterManufacture() throws Exception {
        return new MeterManufacture(
            DataFormat.select(
            		rawData,OFS_METER_ID, LEN_METER_ID));
	}
	
	public MeterStatus getMeterSatus() throws Exception {
        return new MeterStatus(
            DataFormat.select(
            		rawData,OFS_METER_STATUS, LEN_METER_STATUS));
	}

    public int getNBR_LP_DATA() throws Exception {
        return (rawData.length - (OFS_LP_DATA_SET+1) )/LEN_LP_DATA_SET;
    }
    
    public String getChannelMap() throws Exception{
        return "ch1=Active Energy[kWh],v=Active Power[kW],ch2=Lag Reactive Energy[kVarh],v=Lag Reactive Power[kVar]";
    }
        
    @SuppressWarnings("unchecked")
    public LPData[] parse() throws Exception {
        
       // this.LEN_EXTENDED_INT_STATUS = (LEN_NBR_CHNS_SET1 / 2) + 1;
        ArrayList<LPData> list = new ArrayList<LPData>();
        LPData[] interval = new LPData[getNBR_LP_DATA()];
        log.debug("interval.length ="+interval.length);
        resolution = getINTERVAL_TIME();
        for(int i = 0; i < interval.length; i++){
            
            byte[] blk = new byte[LEN_LP_DATA_SET];
            
            blk =DataFormat.select(rawData,OFS_LP_DATA_SET+(i*LEN_LP_DATA_SET),LEN_LP_DATA_SET);
            list.add(parseChannel(blk));       
        }
        
        Collections.sort(list,LPComparator.TIMESTAMP_ORDER);        
        return checkEmptyLP(list);
    }  
    
    @SuppressWarnings({ "unused", "unchecked" })
    private LPData[] checkEmptyLP(ArrayList<LPData> list) throws Exception
    {
        int interval = getINTERVAL_TIME();
        ArrayList<LPData> emptylist = new ArrayList<LPData>();
        Double[] ch  = new Double[2];
        Double[] v  = new Double[2];
        
        for(int i = 0; i < 2; i++){
            ch[i] = new Double(0.0);
            v[i] = new Double(0.0);
        }
        
        String prevTime = "";
        String currentTime = "";

        Iterator it = list.iterator();
        while(it.hasNext()){
            currentTime = ((LPData)it.next()).getDatetime();

            if(prevTime != null && !prevTime.equals("")){
                String temp = Util.addMinYymmdd(prevTime, interval);
                if(!temp.equals(currentTime))
                {
                	int diffMin = (int) ((Util.getMilliTimes(prevTime+"00")-
                            Util.getMilliTimes(currentTime+"00"))/1000/60);
                    
                    for(int i = 0; i < (diffMin/interval) ; i++){
                        LPData data = new LPData();
                        data.setV(v);
                        data.setCh(ch);
                        data.setFlag(0);
                        data.setPF(1.0);
                        data.setDatetime(Util.addMinYymmdd(prevTime, interval*(i+1)));
                        emptylist.add(data);
                    }                
                }
            }
            prevTime = currentTime;

        }
        
        Iterator it2 = emptylist.iterator();
        while(it2.hasNext()){
            list.add((LPData)it2.next());
        }
        
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
    }
    
    @SuppressWarnings("unchecked")
    private LPData parseChannel(byte[] block) throws Exception{

        LPData lpdata = new LPData();
        
        Double[] ch  = new Double[2];
        Double[] v  = new Double[2];
        
        for(int i = 0; i < 2; i++){
        	
            ch[i] =  new Double(dformat.format((double) (DataFormat.hex2dec( DataFormat.select(
                       block,OFS_PULSE+(i*LEN_PULSE),LEN_PULSE)) & MASK_LP_DATA) *ke*0.001));
            v[i] = new Double(dformat.format(ch[i]*((double)60/resolution)));
        }
        
        lpdata.setV(v);
        lpdata.setCh(ch);
        lpdata.setFlag(0);
        lpdata.setPF(getPF(ch[0], ch[1]));
        lpdata.setDatetime(new LPIntervalTime(DataFormat.select(
                            block,OFS_LP_DATE, LEN_LP_DATE)).getIntervalTime());
        return lpdata;
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