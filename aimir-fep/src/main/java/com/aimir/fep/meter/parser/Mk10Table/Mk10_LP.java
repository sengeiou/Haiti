/*
 * @(#)Mk10_LP.java       1.0 2011/08/12 *
 *
 * Load Profile.
 * Copyright (c) 2011-2012 NuriTelecom, Inc.
 * All rights reserved. *
 * This software is the confidential and proprietary information of
 * Nuritelcom, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Nuritelecom.
 */

package com.aimir.fep.meter.parser.Mk10Table;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.parser.Mk6NTable.DateTimeFormat;
import com.aimir.fep.meter.parser.Mk6NTable.LPComparator;
import com.aimir.fep.meter.parser.Mk6NTable.StatusFlag;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.Util;

public class Mk10_LP implements java.io.Serializable{

	private static final long serialVersionUID = 4317744100141853427L;

	private static Log log = LogFactory.getLog(Mk10_LP.class);

	public LPData[] lpDataSet=null;
	public byte[] rawData = null;
    public int interval=15;
    public int channelCnt;
    public int entryCnt;
    public int channelLength;
    public int entryLength;
    public int lastEntryNumber;
    public String storedStartTime="";
    public String statusFlag="";
    public ConfigurationLPChannel statusConfig;
    public ConfigurationLPChannel[] channelConfig;
    
    private static final int OFF_INTERVAL_TIME=0;
    private static final int OFF_NBR_LP_CH=1;
    private static final int OFF_ENTRY_WIDTH=2;
    private static final int OFF_WIDEST_CHANNEL=4;
    private static final int OFF_STORED_START_TIME=6;
    private static final int OFF_LAST_ENTRY_NUMBER=12;
    private static final int OFF_CH_DATA_CONFIG=14;
    private static final int OFF_NBR_LP_ENTRIES = 78;
    private static final int OFF_LP_DATA_SET = 80;

    private static final int LEN_INTERVAL_TIME=1;
    private static final int LEN_NBR_LP_CH=1;
    private static final int LEN_ENTRY_WIDTH=2;
    private static final int LEN_WIDEST_CHANNEL=2;
    private static final int LEN_STORED_START_TIME=6;
    private static final int LEN_LAST_ENTRY_NUMBER=2;
    private static final int LEN_CH_DATA_CONFIG=2;
    private static final int LEN_NBR_LP_ENTRIES=2;

    final static DecimalFormat dformat = new DecimalFormat("#0.000000");

	/**
	 * Constructor .<p>
	 * @param data - read data (header,crch,crcl)
	 */
	public Mk10_LP(byte[] rawData) {
		this.rawData = rawData;
		try {
			parse();
		} catch (Exception e) {
			log.error(e,e);
		}
	}

    /**
     * The time in minutes LP interval duration
     * LP Interval Time
     * Default 15
     * (0,1,2,3,4,5,6,10,12,15,20,30,60)
     * @return
     */
    public int getInterval() {
    	try {
			interval=DataFormat.hex2dec(rawData, OFF_INTERVAL_TIME, LEN_INTERVAL_TIME);
		} catch (Exception e) {
			log.error("Error getInterval->"+e.getMessage());
		}
        return interval;
    }

    public int getEntryLength() throws Exception {
    	entryLength=DataFormat.hex2unsigned16(DataFormat.select(rawData, OFF_ENTRY_WIDTH, LEN_ENTRY_WIDTH));
        return entryLength;
    }

    public int getEntryCnt() throws Exception {
    	entryCnt=DataFormat.hex2unsigned16(DataFormat.select(rawData,OFF_NBR_LP_ENTRIES,LEN_NBR_LP_ENTRIES));
        return entryCnt;
    }

    public int getChannelLength() throws Exception {
    	channelLength=DataFormat.hex2unsigned16(DataFormat.select(rawData, OFF_WIDEST_CHANNEL, LEN_WIDEST_CHANNEL));
        return channelLength;
    }

    public int getChannelCnt() throws Exception {
    	channelCnt=rawData[OFF_NBR_LP_CH];
        return channelCnt;
    }

    public String getStoredStartTime(){
    	try {
			storedStartTime =(new DateTimeFormat(DataFormat.select(rawData,OFF_STORED_START_TIME,LEN_STORED_START_TIME))).getDateTime();
		} catch (Exception e) {
			log.error("Error get Stored Start time->"+e.getMessage());
		}
    	return storedStartTime;
    }
    
    public int getLastEntryNumber() throws Exception {
    	lastEntryNumber=DataFormat.hex2unsigned16(DataFormat.select(rawData, OFF_LAST_ENTRY_NUMBER, LEN_LAST_ENTRY_NUMBER));
        return lastEntryNumber;
    }

    public ConfigurationLPChannel[] getChDataConfig() throws Exception {
        channelConfig = new ConfigurationLPChannel[channelCnt];
        for(int i=0; i<channelCnt; i++){
        	channelConfig[i]=new ConfigurationLPChannel(
        			DataFormat.getReverseBytes(
        					DataFormat.select(rawData, OFF_CH_DATA_CONFIG+LEN_CH_DATA_CONFIG*i, LEN_CH_DATA_CONFIG)));
        }
        return channelConfig;
    }


    @SuppressWarnings("unchecked")
    public LPData[] parse() throws Exception {
    	log.info("//-------------------------------------------------------");
		log.info("//  Mk10 Load Profile Parser Start");
		log.info("//-------------------------------------------------------");
    	//init
    	interval=getInterval();
		channelCnt=getChannelCnt();
		channelLength=getChannelLength();
		channelConfig=getChDataConfig();
		entryCnt=getEntryCnt()-1;
		entryLength=getEntryLength();
		lastEntryNumber=getLastEntryNumber();
		storedStartTime=getStoredStartTime();
		log.debug("channelCount :"+channelCnt);
		log.debug("entryCount :"+entryCnt);
		log.debug("entryLength :"+entryLength);
		log.debug("storedStartTime :"+storedStartTime);

		String lastLpTime = Util.addMinYymmdd(storedStartTime.substring(0,12), lastEntryNumber*interval);
		String lpStartTime = Util.addMinYymmdd(lastLpTime.substring(0,12), -(entryCnt*interval));
		log.debug("lastLpTime :"+lastLpTime);
		log.debug("lpStartTime :"+lpStartTime);
        ArrayList<LPData> list = new ArrayList<LPData>();
        lpDataSet = new LPData[entryCnt];
        log.info("interval: "+interval+" storedStartTime: "+storedStartTime);
		try{
		    for(int i = 0; i < lpDataSet.length; i++){
        		if(OFF_LP_DATA_SET+(i*entryLength) <= rawData.length){
                	byte[] entry = new byte[getEntryLength()];
                	entry =DataFormat.select(rawData,OFF_LP_DATA_SET+(i*entryLength),entryLength);
                	String lpDate=Util.addMinYymmdd(lpStartTime.substring(0,12), interval*(i));

                	StatusFlag status=new StatusFlag(DataFormat.select(entry,channelCnt*channelLength,1));
                	if(status.getLog().length()>0){
                		statusFlag=statusFlag+status.getLog();
                	}

                	log.info("==== LP ENTRY["+i+"] - Offset: "+(OFF_LP_DATA_SET+(i*entryLength))+", Len: "+entryLength+", Raw: "+Hex.decode(entry)+", LpDate: "+lpDate);
                	list.add(parseChannel(entry,lpDate));
        		}
        	}
		}catch(Exception e){log.error(e,e);}


        Collections.sort(list,LPComparator.TIMESTAMP_ORDER);
        lpDataSet=checkEmptyLP(list);
        return lpDataSet;
    }

    @SuppressWarnings({ "unused", "unchecked" })
    private LPData[] checkEmptyLP(ArrayList<LPData> list) throws Exception
    {
        ArrayList<LPData> emptylist = new ArrayList<LPData>();
        Double[] ch  = new Double[channelCnt];
        Double[] v  = new Double[channelCnt];

        for(int i = 0; i < channelCnt; i++){
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
                    int diffMin = (int) ((Util.getMilliTimes(currentTime+"00")-Util.getMilliTimes(prevTime+"00"))/1000/60);
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

    private LPData parseChannel(byte[] block, String lpDate) throws Exception{
        LPData lpdata = new LPData();
        StringBuffer temp = new StringBuffer();
        Double[] ch  = new Double[channelCnt];
        Double[] v  = new Double[channelCnt];
        log.debug("[1234567]mk10");
        for(int i = 0; i < channelCnt; i++){

        	double rawLp=(double)(DataFormat.hex2unsigned16(DataFormat.select(block,channelLength*i,channelLength)));
        	double scalingFactor=(double)(channelConfig[i].getCh_scaling_factor());
        	double decimalPosition = channelConfig[i].getDecimalPoint();
        	ch[i] =  dformat((rawLp*scalingFactor*decimalPosition));
        	//ch[i] = rawLp;
            v[i] = dformat(ch[i]*((double)60/interval));
            temp.append("ch["+i+"]:" +ch[i]+",");
            //log.info("ch["+i+"]: " +ch[i]);
            
            log.debug(""+ ch[i] + " | " + v[i]);
        }
        //log.info(temp.toString());
        lpdata.setV(v);
        lpdata.setCh(ch);
        lpdata.setFlag(0);
        lpdata.setPF(getPF(ch[0], ch[1]));
        lpdata.setDatetime(lpDate);
        return lpdata;
    }

    private double getPF(double ch1, double ch2) throws Exception {

        double pf   = (float)(ch1/(Math.sqrt(Math.pow(ch1,2)+Math.pow(ch2,2))));

        if(ch1 == 0.0 && ch2 == 0.0)
            pf = (double) 1.0;
        /* Parsing Transform Results put Data Class */
        if(pf < 0.0 || pf > 1.0)
            throw new Exception("BILL PF DATA FORMAT ERROR : "+pf);
        return dformat(pf);
    }


	public String getStatusFlag() {
		return statusFlag;
	}

	/**
	 * Constructs a <code>String</code> with all attributes
	 * in name = value format.
	 *
	 * @return a <code>String</code> representation
	 * of this object.
	 */
	public String toString()
	{
	    StringBuffer retValue = new StringBuffer();

	    retValue.append("Mk10_LP [ ")
	        .append(super.toString()).append('\n')
	        .append("rawData = ").append(this.rawData).append('\n')
	        .append("interval = ").append(this.interval).append('\n')
	        .append("channelCnt = ").append(this.channelCnt).append('\n')
	        .append("entryCnt = ").append(this.entryCnt).append('\n')
	        .append("channelLength = ").append(this.channelLength).append('\n')
	        .append("entryLength = ").append(this.entryLength).append('\n')
	        .append("storedStartTime = ").append(this.storedStartTime).append('\n')
	        .append("statusConfig = ").append(this.statusConfig).append('\n')
	        .append("channelConfig = ").append(this.channelConfig).append('\n')
	        .append("dformat = ").append(this.dformat).append('\n')
	        .append("log = ").append(this.log).append('\n')
	        .append(" ]");

	    return retValue.toString();
	}

	public static Double dformat(Double n){
    	if(n==null)
    		return 0d;
    	return new Double(dformat.format(n));
    }
}