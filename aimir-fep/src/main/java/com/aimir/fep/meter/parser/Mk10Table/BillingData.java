/**
 * @(#)BillingData.java       1.0 2008/08/12 *
 *
 * Previous Billing Data Class.
 * Copyright (c) 2006-2007 NuriTelecom, Inc.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.TOU_BLOCK;
import com.aimir.fep.meter.parser.Mk6NTable.DateTimeFormat;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;
import com.aimir.util.TimeUtil;

/**
 * @author kaze kaze@nuritelecom.com
 */
public class BillingData implements java.io.Serializable{

	private static final long serialVersionUID = 3599334467819464587L;
	private final int LEN_SUMMATION=4;
    private final int LEN_DEMAND = 4;
    private final int LEN_DEMAND_TIME = 6;
    private final int LEN_RESET_TIME = 6;
    private final int LEN_RESET_REASON = 4;
    private final int LEN_RESET_NUMBER = 4;
    public final static String PB = "PB";
    public final static String CB = "CB";

    private byte[] data;
    private static Log log = LogFactory.getLog(BillingData.class);
    DecimalFormat dformat = new DecimalFormat("#0.000000");

    private String currentMeterTime;
    private String resetTime;
    private int rateCnt;
    private int resetCnt;
    private int channelCnt;

	private TOU_BLOCK[] tou_block;

	/**
	 * Constructor .<p>
	 *
	 * @param data - read data (header,crch,crcl)
	 */
	public BillingData(String billingType, byte[] data, String currentTime, int rateCount, int channelCount) {
		
		if(rateCount > 4){
			this.rateCnt = 4;
		}else{
			this.rateCnt = rateCount;
		}
		this.data = data;
		//this.currentMeterTime = currentTime;
		this.currentMeterTime = TimeUtil.getCurrentTimeMilli();
		this.channelCnt=getChannelCount(billingType);
		
		this.tou_block = new TOU_BLOCK[this.rateCnt];

		if(billingType.equals(PB)){
			this.resetTime = getResetTime();
    		log.info("//-------------------------------------------------------");
    		log.info("//  Mk10 Previous Billing Parser Start :  ");
    		log.info("//-------------------------------------------------------");
            log.info("Previous Billing resetCnt: "+resetCnt);
            log.info("Previous Billing resetTime: "+resetTime);
            log.info("Previous Billing channelCnt: "+channelCnt);
            log.info("Previous Billing rateCnt: "+rateCount);
		}else if(billingType.equals(CB)){
			this.resetTime = currentMeterTime;
		    log.info("//-------------------------------------------------------");
            log.info("//  Mk10 Billing Total Parser Start :  ");
            log.info("//-------------------------------------------------------");
            log.info("Billing Total currentMeterTime: "+currentMeterTime);
            log.info("Billing Total channelCnt: "+channelCnt);
            log.info("Billing Total rateCnt: "+rateCount);

		}
		try {
			parseData(billingType);
		} catch (Exception e) {
            log.warn("BillingData Parse Error :",e);
            e.printStackTrace();
		}
	}
	
	public int getChannelCount(String billingType){
		
		if(data.length < 1){
			return 0;
		}
		int channelCnt = 0;
		if(billingType.equals(PB)){
			channelCnt  = (data.length - LEN_RESET_TIME - LEN_RESET_REASON - LEN_RESET_NUMBER )/((LEN_SUMMATION + LEN_DEMAND + LEN_DEMAND_TIME)*rateCnt);
		}else if(billingType.equals(CB)){
			channelCnt  = (data.length - LEN_RESET_TIME - LEN_RESET_REASON - LEN_RESET_NUMBER )/((LEN_SUMMATION + LEN_DEMAND)*rateCnt);
		}
		return channelCnt;
	}
	
	public String getResetTime(){
		String resetTime = "";
		try {
			byte[] time = new byte[6];
			System.arraycopy(data, data.length -(LEN_RESET_TIME+LEN_RESET_REASON+LEN_RESET_NUMBER) , time, 0, time.length);
			resetTime =(new DateTimeFormat(time)).getDateTime();
		} catch (Exception e) {
			log.warn(e,e);
		}
		return resetTime;
	}

	public TOU_BLOCK[] getTOU_BLOCK(){
		return this.tou_block;
	}

	private void parseData(String billingType) throws Exception {
		double energy = 0;
		double demand = 0;

		String demandTime="";

		for(int i = 0; i < this.rateCnt; i++){
			tou_block[i] = new TOU_BLOCK(channelCnt,
										 channelCnt,
										 channelCnt,
                                         channelCnt,
                                         0);

			if(resetTime!=null && resetTime.length()>0)
				tou_block[i].setResetTime(resetTime);
			if(resetCnt!=0)
				tou_block[i].setResetCount(resetCnt);
			
			
		}
		
		int offset = 0;
		
		//energy consumption
		for(int i = 0; i < this.channelCnt; i++){
			for(int j=0; j < this.rateCnt; j++){			
				log.debug("ENERGY : "+Util.getHexString(DataFormat.select(data, offset, LEN_SUMMATION)));
				energy = DataFormat.hex2long(DataFormat.LSB2MSB(DataFormat.select(data, offset, LEN_SUMMATION)))/1000;
				offset += LEN_SUMMATION;
	            tou_block[j].setSummations(i, new Double(energy));
			}
		}
		

		//demand consumption
		for(int i = 0; i < this.channelCnt; i++){
			for(int j=0; j < this.rateCnt; j++){
				log.debug("DEMAND : "+Util.getHexString(DataFormat.select(data, offset, LEN_DEMAND)));
				demand = DataFormat.hex2long(DataFormat.LSB2MSB(DataFormat.select(data, offset, LEN_DEMAND)))/1000;
				offset += LEN_DEMAND;
	            tou_block[j].setCurrDemand(i, new Double(demand));
			}
		}

		//cum demand consumption
		for(int i = 0; i < this.channelCnt; i++){
			for(int j=0; j < this.rateCnt; j++){
	            tou_block[j].setCumDemand(i, new Double(0));
			}
		}
		
		if(billingType.equals(PB)){
			//demand time
			for(int i = 0; i < this.channelCnt; i++){
				for(int j=0; j < this.rateCnt; j++){
					log.debug("DEMANDTIME : "+Util.getHexString(DataFormat.select(data, offset, LEN_DEMAND_TIME)));
					byte[] time = new byte[6];
					time = DataFormat.select(data, offset, LEN_DEMAND_TIME);
					
					demandTime = (new DateTimeFormat(time)).getDateTime();
					log.debug("Demand Time : "+demandTime);
					if(demandTime.length() > 12){
						demandTime = "";
					}
					offset += LEN_DEMAND_TIME;
		            tou_block[j].setEventTime(i, demandTime);
				}
			}
		}else{
			//demand time
			for(int i = 0; i < this.channelCnt; i++){
				for(int j=0; j < this.rateCnt; j++){
		            tou_block[j].setEventTime(i, "");
				}
			}
		}

	}
}
