/** 
 * @(#)MT50.java       1.0 05/07/25 *
 * 
 * PQM Status Class.
 * Copyright (c) 2004-2005 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.a1830rlnTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;


/**
 * @author Park YeonKyoung yeonkyoung@hanmail.net
 */
public class MT50 implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 210725413392580363L;
	private int OFS_PQMFAIL1  = 0;
	private int OFS_PQMFAIL2  = 10;
	private int OFS_PQMFAIL3  = 20;
	private int OFS_PQMFAIL4  = 30;
	private int OFS_PQMFAIL5  = 40;
	private int OFS_PQMFAIL6  = 50;
	private int OFS_PQMFAIL7  = 60;
	private int OFS_PQMFAIL8  = 70;
	private int OFS_PQMFAIL9  = 80;	
	private int OFS_PQMFAIL10 = 90;	
	private int OFS_PQMFAIL11 = 100;	
	private int OFS_PQMFAIL12 = 110;
	
	private int OFS_PQMCNTR1  = 4;
	private int OFS_PQMCNTR2  = 14;
	private int OFS_PQMCNTR3  = 24;
	private int OFS_PQMCNTR4  = 34;
	private int OFS_PQMCNTR5  = 44;
	private int OFS_PQMCNTR6  = 54;
	private int OFS_PQMCNTR7  = 64;
	private int OFS_PQMCNTR8  = 74;
	private int OFS_PQMCNTR9  = 84;	
	private int OFS_PQMCNTR10 = 94;	
	private int OFS_PQMCNTR11 = 104;	
	private int OFS_PQMCNTR12 = 114;	
	
	private int OFS_PQMTMR1   = 6;
	private int OFS_PQMTMR2   = 16;
	private int OFS_PQMTMR3   = 26;
	private int OFS_PQMTMR4   = 36;
	private int OFS_PQMTMR5   = 46;
	private int OFS_PQMTMR6   = 56;
	private int OFS_PQMTMR7   = 66;
	private int OFS_PQMTMR8   = 76;
	private int OFS_PQMTMR9   = 86;	
	private int OFS_PQMTMR10  = 96;	
	private int OFS_PQMTMR11  = 106;	
	private int OFS_PQMTMR12  = 116;	
	
	private final int LEN_PQM_FAILTIME = 4;
	private final int LEN_PQM_COUNTER  = 2;
	private final int LEN_PQM_TIMER    = 4;
	
	private byte[] data;

    private Log logger = LogFactory.getLog(getClass());

	
	/**
	 * Constructor .<p>
	 * @param data - read data (header,crch,crcl)
	 */
	public MT50(byte[] data) {
		this.data = data;
	}
	
	/**
	 * PQM Service voltage Cumulative count.
	 * @return - 2byte hex data.
	 * @throws Exception
	 */
	public int getSvcVolCount()
					throws Exception {
		return getCumCount(OFS_PQMCNTR1,LEN_PQM_COUNTER);
	}
	
	/**
	 * PQM Service Voltage Cumulative Duration.(seconds)
	 * @return - 4 byte hex data.
	 * @throws Exception
	 */
	public long getSvcVolDuration() 
					throws Exception {
		return getCumDur(OFS_PQMTMR1,LEN_PQM_TIMER);
	}
	
	public int getSvcVolStatus() {
		return getCumStatus(OFS_PQMFAIL1,LEN_PQM_FAILTIME);
	}
	
	/**
	 * PQM Low Voltage Cumulative Count.
	 * @return - 2byte hex data.
	 * @throws Exception
	 */
	public int getLowVolCount() 
					throws Exception {
		return getCumCount(OFS_PQMCNTR2,LEN_PQM_COUNTER);
	}
	
	/**
	 * PQM Low Voltage Cumulative Duration.(seconds)
	 * @return - 4 byte hex data.
	 * @throws Exception
	 */
	public long getLowVolDuration() 
					throws Exception {
		return getCumDur(OFS_PQMTMR2,LEN_PQM_TIMER);
	}
	
	/**
	 * PQM Low Voltage Status.
	 * @return - 1 byte
	 */
	public int getLowVolStatus(){
		return getCumStatus(OFS_PQMFAIL2,LEN_PQM_FAILTIME);
	}
	
	/**
	 * PQM High Voltage Cumulative count.
	 * @return -  2 byte hex data.
	 * @throws Exception
	 */
	public int getHighVolCount() 
					throws Exception {
		return getCumCount(OFS_PQMCNTR3,LEN_PQM_COUNTER);
	}
	
	/**
	 * PQM High Voltage Cumulative Duration.(seconds)
	 * @return - 4 byte hex data.
	 * @throws Exception
	 */
	public long getHighVolDuration() 
					throws Exception {
		return getCumDur(OFS_PQMTMR3,LEN_PQM_TIMER);
	}
	
	/**
	 * PQM High Voltage Status.
	 * @return
	 */
	public int getHighVolStatus(){
		return getCumStatus(OFS_PQMFAIL3,LEN_PQM_FAILTIME);
	}
	
	/**
	 * PQM Power Frequency & Reverse Power Cumulative Count.
	 * @return - 2 byte hex data.
	 * @throws Exception
	 */
	public int getReversePowerCount() 
					throws Exception {
		return getCumCount(OFS_PQMCNTR4,LEN_PQM_COUNTER);
	}
	
	/**
	 * PQM Power Frequency & Reverse Power 
	 * Cumulative Duration.(seconds)
	 * @return -  4 byte hex data.
	 * @throws Exception
	 */
	public long getReversePowerDuration()
					 throws Exception {
		return getCumDur(OFS_PQMTMR4,LEN_PQM_TIMER);
	}
	
	/**
	 * PQM Power Frequency & Reverse Power
	 * Status.
	 * @return
	 */
	public int getReversePowerStatus(){
		return getCumStatus(OFS_PQMFAIL4,LEN_PQM_FAILTIME);
	}
	
	/**
	 * PQM Low Current Cumulative Count.
	 * @return - 2 byte hex data.
	 * @throws Exception
	 */
	public int getLowCurrentCount()
					throws Exception {
		return getCumCount(OFS_PQMCNTR5,LEN_PQM_COUNTER);
	}
	
	/**
	 * PQM Low Current Duration.(seconds)
	 * @return -  4 byte hex data.
	 * @throws Exception
	 */
	public long getLowCurrentDuration()
					throws Exception {
		return getCumDur(OFS_PQMTMR5,LEN_PQM_TIMER);
	}
	
	/**
	 * PQM Low Current Status.
	 * @return
	 */
	public int getLowCurrentStatus(){
		return getCumStatus(OFS_PQMFAIL5,LEN_PQM_FAILTIME);
	}
	
	/**
	 * PQM Power Factor Cumulative Count.
	 * @return - 2 byte hex data.
	 * @throws Exception
	 */
	public int getPowerFactorCount()
					throws Exception {
		return getCumCount(OFS_PQMCNTR6,LEN_PQM_COUNTER);
	}
	
	/**
	 * PQM Power Factor Cumulative Duration.(seconds)
	 * @return - 4 byte hex data.
	 * @throws Exception
	 */
	public long getPowerFactorDuration() 
					throws Exception {
		return getCumDur(OFS_PQMTMR6,LEN_PQM_TIMER);
	}
	
	/**
	 * PQM Power Factor Status.
	 * @return
	 */
	public int getPowerFactorStatus(){
		return getCumStatus(OFS_PQMFAIL6,LEN_PQM_FAILTIME);
	}
	
	/**
	 * PQM 2nd Harmonic Cumulative Count.
	 * @return - 2 byte hex data.
	 * @throws Exception
	 */
	public int getHarmonicCount()
					throws Exception {
		return getCumCount(OFS_PQMCNTR7,LEN_PQM_COUNTER);
	}
	
	/**
	 * PQM 2nd Harmonic Cumulative Duration.(seconds)
	 * @return -  4 byte hex data.
	 * @throws Exception
	 */
	public long getHarmonicDuration()
					throws Exception {
		return getCumDur(OFS_PQMTMR7,LEN_PQM_TIMER);
	}
	
	/**
	 * PQM 2nd Harmonic Status.
	 * @return
	 */
	public int getHarmonicStatus(){
		return getCumStatus(OFS_PQMFAIL7,LEN_PQM_FAILTIME);
	}
	
	/**
	 * PQM THD(Total Harmonic Distortion) Current 
	 * Cumulative count.
	 * @return -  2 byte hex data.
	 * @throws Exception
	 */
	public int getTHDCurrCount()
					throws Exception {
		return getCumCount(OFS_PQMCNTR8,LEN_PQM_COUNTER);
	}
	
	/**
	 * PQM THD(Total Harmonic Distortion) Current 
	 * Cumulative Duration.(seconds)
	 * @return
	 * @throws Exception
	 */
	public long getTHDCurrDuration()
					throws Exception {
		return getCumDur(OFS_PQMTMR8,LEN_PQM_TIMER);
	}
	
	/**
	 * PQM THD(Total Harmonic Distortion) Current 
	 * status.
	 * @return
	 */
	public int getTHDCurrStatus(){
		return getCumStatus(OFS_PQMFAIL8,LEN_PQM_FAILTIME);
	}
	
	/**
	 * PQM THD(Total Harmonic Distortion) Voltage 
	 * Cumulative Count.
	 * @return -  2 byte hex data.
	 * @throws Exception
	 */
	public int getTHDVolCount() 
					throws Exception {
		return getCumCount(OFS_PQMCNTR9,LEN_PQM_COUNTER);
	}
	
	/**
	 * PQM THD(Total Harmonic Distortion) Voltage
	 * Cumulative Duration.(seconds)
	 * @return -  4 byte hex data.
	 * @throws Exception
	 */
	public long getTHDVolDuration()
					throws Exception {
		return getCumDur(OFS_PQMTMR9,LEN_PQM_TIMER);
	}
	
	/**
	 * PQM THD(Total Harmonic Distortion) 
	 * Status.
	 * @return
	 */
	public int getTHDVolStatus(){
		return getCumStatus(OFS_PQMFAIL9,LEN_PQM_FAILTIME);
	}
	

	public int getVolImbCount() 
					throws Exception {
		return getCumCount(OFS_PQMCNTR10,LEN_PQM_COUNTER);				
	}
	
	public long getVolImbDuration()
					throws Exception {
		return getCumDur(OFS_PQMTMR10,LEN_PQM_TIMER);				
	}
	
	public int getVolImbStatus(){
		return getCumStatus(OFS_PQMFAIL10,LEN_PQM_FAILTIME);
	}
	
	
	public int getCurrImbCount() 
					throws Exception {
		return getCumCount(OFS_PQMCNTR11,LEN_PQM_COUNTER);				
	}
	
	public long getCurrImbDuration()
					throws Exception {
		return getCumDur(OFS_PQMTMR11,LEN_PQM_TIMER);				
	}
	
	public int getCurrImbStatus(){
		return getCumStatus(OFS_PQMFAIL11,LEN_PQM_FAILTIME);
	}
	
	public int getTDDCount() 
					throws Exception {
		return getCumCount(OFS_PQMCNTR12,LEN_PQM_COUNTER);				
	}
	
	public long getTDDDuration()
					throws Exception {
		return getCumDur(OFS_PQMTMR12,LEN_PQM_TIMER);				
	}
	
	public int getTDDStatus(){
		return getCumStatus(OFS_PQMFAIL12,LEN_PQM_FAILTIME);
	}

	protected int getCumCount(int offset, int len) 
					throws Exception {

		return DataFormat.hex2unsigned16(
				DataFormat.LSB2MSB(
						DataFormat.select(
								data,offset,len)));
	}
	
	protected long getCumDur(int offset, int len) 
				throws Exception {

		return DataFormat.hex2long(
				DataFormat.LSB2MSB(
						DataFormat.select(
								data,offset,len)));
	}
	
	protected int getCumStatus(int offset, int len){
		
		long fail_secs = 0;
		try {
			fail_secs =
				DataFormat.hex2long(
					DataFormat.LSB2MSB(DataFormat.select(data, offset, len)));
		} catch (Exception e) {
			logger.warn(e.getMessage());
		}
									
		if(fail_secs!=0)
			return 0x01;
		else
			return 0x00;
	}


}
