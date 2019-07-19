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

package com.aimir.fep.meter.parser.Mk6NTable;

import java.text.DecimalFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.TOU_BLOCK;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Hex;

/**
 * @author kaze kaze@nuritelecom.com
 */
public class BillingData implements java.io.Serializable{

	private static final long serialVersionUID = 8142091770015815850L;
	private final int LEN_SUMMATION=8;
    private final int LEN_DEMAND = 4;
    private final int LEN_DEMAND_TOTAL = 8;
    private final int LEN_MDEMAND_TIME = 6;

    private final int NBR_TIERS =2;
    private final int CNT_BLOCK =9; //1,2,3,4,5,6,7,8,total

    private byte[] data;
    private static Log log = LogFactory.getLog(BillingData.class);
    DecimalFormat dformat = new DecimalFormat("#0.000000");

    private String resetTime;
    private int currentSeason;
    private int resetCnt;
    private int channelCnt;
    private int activeChannelCnt;
    private int reActiveChannelCnt;
	private TOU_BLOCK[] tou_block;

	/**
	 * Constructor .<p>
	 *
	 * @param data - read data (header,crch,crcl)
	 */
	public BillingData(String billingType, String meterId, byte[] data, String resetTime, int currentSeason, int resetCnt, int channelCnt, int activeChannelCnt, int reActiveChannelCnt) {
		this.data = data;
		this.resetTime=resetTime;
		this.currentSeason=currentSeason;
		this.resetCnt=resetCnt;
		this.channelCnt=channelCnt;
		this.activeChannelCnt=activeChannelCnt;
		this.reActiveChannelCnt=reActiveChannelCnt;
		this.tou_block = new TOU_BLOCK[this.CNT_BLOCK];
		if(billingType.equals("TPB")){
    		log.info("\n//-------------------------------------------------------");
    		log.info("//  Mk6N Previous Billing Parser Start : meterId["+meterId+"]");
    		log.info("//-------------------------------------------------------");
    		log.info("Previous Billing resetTime: "+resetTime);
            log.info("Previous Billing currentSeason: "+currentSeason);
            log.info("Previous Billing resetCnt: "+resetCnt);
            log.info("Previous Billing channelCnt: "+channelCnt);
            log.info("Previous Billing activeChannelCnt: "+activeChannelCnt);
            log.info("Previous Billing reActiveChannelCnt: "+reActiveChannelCnt);
		}else if(billingType.equals("BTB")){
		    log.info("\n//-------------------------------------------------------");
            log.info("//  Mk6N Billing Total Parser Start : meterId["+meterId+"]");
            log.info("//-------------------------------------------------------");
            log.info("Billing Total resetTime: "+resetTime);
            log.info("Billing Total currentSeason: "+currentSeason);
            log.info("Billing Total resetCnt: "+resetCnt);
            log.info("Billing Total channelCnt: "+channelCnt);
            log.info("Billing Total activeChannelCnt: "+activeChannelCnt);
            log.info("Billing Total reActiveChannelCnt: "+reActiveChannelCnt);
		}
		try {
			parseData(billingType, null);
		} catch (Exception e) {
            log.warn("BillingData Parse Error :",e);
            e.printStackTrace();
		}
	}
	public BillingData(String billingType, String meterId, TOU_BLOCK[] prev_blocks, byte[] data, String resetTime, int currentSeason, int resetCnt, int channelCnt, int activeChannelCnt, int reActiveChannelCnt) {
        this.data = data;
        this.resetTime=resetTime;
        this.currentSeason=currentSeason;
        this.resetCnt=resetCnt;
        this.channelCnt=channelCnt;
        this.activeChannelCnt=activeChannelCnt;
        this.reActiveChannelCnt=reActiveChannelCnt;
        this.tou_block = new TOU_BLOCK[this.CNT_BLOCK];
        if(billingType.equals("BTB")){
            log.info("\n//-------------------------------------------------------");
            log.info("//  Mk6N Billing Total Parser Start : meterId["+meterId+"]");
            log.info("//-------------------------------------------------------");
            log.info("Billing Total resetTime: "+resetTime);
            log.info("Billing Total currentSeason: "+currentSeason);
            log.info("Billing Total resetCnt: "+resetCnt);
            log.info("Billing Total channelCnt: "+channelCnt);
            log.info("Billing Total activeChannelCnt: "+activeChannelCnt);
            log.info("Billing Total reActiveChannelCnt: "+reActiveChannelCnt);
        }
        try {
            parseData(billingType, prev_blocks);
        } catch (Exception e) {
            log.warn("BillingData Parse Error :",e);
            e.printStackTrace();
        }
    }

	/**
	 * Constructor .<p>
	 *
	 * @param data - read data (header,crch,crcl)
	 */
	public BillingData(String billingType, String meterId, byte[] data, int currentSeason, int channelCnt, int activeChannelCnt, int reActiveChannelCnt) {
		this.data = data;
		this.currentSeason=currentSeason;
		this.channelCnt=channelCnt;
		this.activeChannelCnt=activeChannelCnt;
		this.reActiveChannelCnt=reActiveChannelCnt;
		this.tou_block = new TOU_BLOCK[this.CNT_BLOCK];
		if(billingType.equals("TCB")){
    		log.info("\n//-------------------------------------------------------");
    		log.info("//  Mk6N Current Billing Parser Start : meterId["+meterId+"]");
    		log.info("//-------------------------------------------------------");
    		log.info("Current Billing currentSeason: "+currentSeason);
    		log.info("Current Billing channelCnt: "+channelCnt);
    		log.info("Current Billing activeChannelCnt: "+activeChannelCnt);
    		log.info("Current Billing reActiveChannelCnt: "+reActiveChannelCnt);
		}
		try {
			parseData(billingType, null);
		} catch (Exception e) {
            log.warn("BillingData Parse Error :",e);
            e.printStackTrace();
		}
	}

	public BillingData(String billingType, String meterId, TOU_BLOCK[] curr_blocks, byte[] data, int currentSeason, int channelCnt, int activeChannelCnt, int reActiveChannelCnt) {
        this.data = data;
        this.currentSeason=currentSeason;
        this.channelCnt=channelCnt;
        this.activeChannelCnt=activeChannelCnt;
        this.reActiveChannelCnt=reActiveChannelCnt;
        this.tou_block = new TOU_BLOCK[this.CNT_BLOCK];
        if(billingType.equals("TTB")){
            log.info("\n//-------------------------------------------------------");
            log.info("//  Mk6N Total Parser Start : meterId["+meterId+"]");
            log.info("//-------------------------------------------------------");
            log.info("Total currentSeason: "+currentSeason);
            log.info("Total channelCnt: "+channelCnt);
            log.info("Total activeChannelCnt: "+activeChannelCnt);
            log.info("Total reActiveChannelCnt: "+reActiveChannelCnt);
        }
        try {
            parseData(billingType, curr_blocks);
        } catch (Exception e) {
            log.warn("BillingData Parse Error :",e);
            e.printStackTrace();
        }
    }



	public TOU_BLOCK[] getTOU_BLOCK(){
		return this.tou_block;
	}

	private void parseData(String billingType, TOU_BLOCK[] blocks) throws Exception {
		double energy=0;
		double currentDemand=0;
		String demandTime="";
		int len_block=18;

		int len_demand=0;
		if(billingType.equals("TPB")||billingType.equals("TCB")){
		    len_demand=LEN_DEMAND;
		    len_block=18;
		}else if(billingType.equals("BTB")||billingType.equals("TTB")){
		    len_demand=LEN_DEMAND_TOTAL;
		    len_block=16;
		}
		for(int i = 0; i < this.CNT_BLOCK; i++){
			tou_block[i] = new TOU_BLOCK(NBR_TIERS,
                                         NBR_TIERS,
                                         NBR_TIERS,
                                         NBR_TIERS,
                                         NBR_TIERS);
			if(resetTime!=null && resetTime.length()>0)
				tou_block[i].setResetTime(resetTime);
			if(resetCnt!=0)
				tou_block[i].setResetCount(resetCnt);

			int idx=0;
			if(i==0){
            	idx=CNT_BLOCK-1;
            }else{
            	idx=i-1;
            }
			//Active
			energy = (DataFormat.bytesToDouble(DataFormat.select(data,(len_block*channelCnt*idx)+(LEN_SUMMATION*activeChannelCnt),LEN_SUMMATION)))/1000;
			if(billingType.equals("TPB")||billingType.equals("TCB")){
			    currentDemand = (DataFormat.bytesToFloat(DataFormat.select(data,(len_block*channelCnt*idx)+(LEN_SUMMATION*channelCnt)+(len_demand*activeChannelCnt),len_demand)))/1000;
	        }else if(billingType.equals("BTB")||billingType.equals("TTB")){
	            currentDemand=((Double)blocks[i].getCurrDemand(0)).doubleValue();
	        }
			if(billingType.equals("TPB")||billingType.equals("TCB")){
			    demandTime=(new DateTimeFormat(DataFormat.select(data,(len_block*channelCnt*idx)+(LEN_SUMMATION*channelCnt)+(len_demand*channelCnt)+(LEN_MDEMAND_TIME*activeChannelCnt),LEN_MDEMAND_TIME))).getDateTime();
			}else{
			    demandTime=(blocks[i].getEventTime(0)).toString();
			}
			log.info("========== "+billingType+" Rate["+idx+"] Block, TOU_BLOCK["+i+"] Block ==========");
			log.info("Active Channel["+activeChannelCnt+"]");
			log.info("Active energy: "+energy+" , OFFSET: "+((len_block*channelCnt*idx)+(LEN_SUMMATION*activeChannelCnt))+" , RAW DATA: "+Hex.decode(DataFormat.select(data,(len_block*channelCnt*idx)+(LEN_SUMMATION*activeChannelCnt),LEN_SUMMATION)));
            log.info("Active currentDemand: "+currentDemand+" , OFFSET: "+((len_block*channelCnt*idx)+(LEN_SUMMATION*channelCnt)+(len_demand*activeChannelCnt))+" , RAW DATA: "+Hex.decode(DataFormat.select(data,(len_block*channelCnt*idx)+(LEN_SUMMATION*channelCnt)+(len_demand*activeChannelCnt),len_demand)));
            if(billingType.equals("TPB")||billingType.equals("TCB")){
                log.info("Active demandTime: "+demandTime+" , OFFSET: "+((len_block*channelCnt*idx)+(LEN_SUMMATION*channelCnt)+(len_demand*channelCnt)+(LEN_MDEMAND_TIME*activeChannelCnt))+" , RAW DATA: "+Hex.decode(DataFormat.select(data,(len_block*channelCnt*idx)+(LEN_SUMMATION*channelCnt)+(len_demand*channelCnt)+(LEN_MDEMAND_TIME*activeChannelCnt),LEN_MDEMAND_TIME)));
            }else if(billingType.equals("BTB")||billingType.equals("TTB")){
                log.info("Active demandTime: "+demandTime);
            }

            tou_block[i].setSummations(0, new Double(energy));
            tou_block[i].setCurrDemand(0, new Double(currentDemand));
            tou_block[i].setEventTime(0, demandTime);

            tou_block[i].setCumDemand(0, new Double(0.0));
            tou_block[i].setCoincident(0, new Double(0.0));
            //Reactive
            energy = (DataFormat.bytesToDouble(DataFormat.select(data,(len_block*channelCnt*idx)+(LEN_SUMMATION*reActiveChannelCnt),LEN_SUMMATION)))/1000;
            if(billingType.equals("TPB")||billingType.equals("TCB")){
                currentDemand = (DataFormat.bytesToFloat(DataFormat.select(data,(len_block*channelCnt*idx)+(LEN_SUMMATION*channelCnt)+(len_demand*reActiveChannelCnt),len_demand)))/1000;
            }else if(billingType.equals("BTB")||billingType.equals("TTB")){
                currentDemand=((Double)blocks[i].getCurrDemand(1)).doubleValue();
            }
            if(billingType.equals("TPB")||billingType.equals("TCB")){
                demandTime=(new DateTimeFormat(DataFormat.select(data,(len_block*channelCnt*idx)+(LEN_SUMMATION*channelCnt)+(len_demand*channelCnt)+(LEN_MDEMAND_TIME*reActiveChannelCnt),LEN_MDEMAND_TIME))).getDateTime();
            }else if(billingType.equals("BTB")||billingType.equals("TTB")){
                demandTime=(blocks[i].getEventTime(1)).toString();
            }

            log.info("Reactive Channel["+reActiveChannelCnt+"]");
            log.info("Reactive energy: "+energy+" , OFFSET: "+((len_block*channelCnt*idx)+(LEN_SUMMATION*reActiveChannelCnt))+" , RAW DATA: "+Hex.decode(DataFormat.select(data,(len_block*channelCnt*idx)+(LEN_SUMMATION*reActiveChannelCnt),LEN_SUMMATION)));
            log.info("Reactive currentDemand: "+currentDemand+" , OFFSET: "+((len_block*channelCnt*idx)+(LEN_SUMMATION*channelCnt)+(len_demand*reActiveChannelCnt))+" , RAW DATA: "+Hex.decode(DataFormat.select(data,(len_block*channelCnt*idx)+(LEN_SUMMATION*channelCnt)+(len_demand*reActiveChannelCnt),len_demand)));
            if(billingType.equals("TPB")||billingType.equals("TCB")){
                log.info("Reactive demandTime: "+demandTime+" , OFFSET: "+((len_block*channelCnt*idx)+(LEN_SUMMATION*channelCnt)+(len_demand*channelCnt)+(LEN_MDEMAND_TIME*reActiveChannelCnt))+" , RAW DATA: "+Hex.decode(DataFormat.select(data,(len_block*channelCnt*idx)+(LEN_SUMMATION*channelCnt)+(len_demand*channelCnt)+(LEN_MDEMAND_TIME*reActiveChannelCnt),LEN_MDEMAND_TIME)));
            }else if(billingType.equals("BTB")||billingType.equals("TTB")){
                log.info("Reactive demandTime: "+demandTime);
            }


            tou_block[i].setSummations(1, new Double(energy));
            tou_block[i].setCurrDemand(1, new Double(currentDemand));
            tou_block[i].setEventTime(1, demandTime);

            tou_block[i].setCumDemand(1, new Double(0.0));
            tou_block[i].setCoincident(1, new Double(0.0));

		}
	}
}
