/** 
 * @(#)Class18.java       1.0 04/09/20 *
 * 
 * Load ProfileData Class.
 * Copyright (c) 2004-2005 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
package com.aimir.fep.meter.parser.a1rlTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;
import com.aimir.util.DateTimeUtil;

/**
 * @author Park YeonKyoung yeonkyoung@hanmail.net
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Class18 {
	
    private Log logger = LogFactory.getLog(getClass());

	private int LEN_DAYHDR   = 6;	// day header size
	private int LEN_DASIZE   = 0;	// one day data size
	private int LEN_TOTSIZE  = 0;	// total LP size
	private int day = 0;			// n day
	private int LEN_CHANREC  = 2;	// chnnel count.
	private int LEN_CHANSIZE = 4;   // one channel size.
	private int LEN_INTERVAL = 0;
	//private int emptycount = 0;
	private int totpulsecnt = 0;
	private double uke = 1;
	private byte[] starttime;
	private byte[] endtime;
	private String metertime; //meter time
	
	private int LP_BLOCK_SIZE = 16; //LP 1 BLOCK Size = 14 byte
	private byte[] data;
	private byte[] lpdata;
	
	/**
	 * Constructor.
	 * @param data - read data
	 * @param day - day's count.
	 * @param dasize - one day's byte length ex ) 390 byte
	 */
	public Class18(byte[] data, 
				   int day, int dasize, 
				   int chan_cnt, int interval,
				   double uke,
				   String metertime) {
				   	
		this.data         = data;
		this.LEN_DASIZE   = dasize;
		this.LEN_CHANREC  = chan_cnt;
		this.LEN_TOTSIZE  = data.length;
		this.LEN_INTERVAL = interval;
		this.uke = uke;
		this.metertime = metertime;

		try {
			parseLPData();
		} catch (Exception e) {
			logger.warn(e.getMessage());
		}
	}
	
	
	/**
	 * Get LP Pulse Count. 
	 * @return
	 */
	public byte[] getPulseCount() throws Exception {
		
		int pcount = 0;
		byte[] pulsecount = new byte[2];

		pcount = this.totpulsecnt;
		pulsecount[0] = (byte)((pcount >> 8)&0xff);
		pulsecount[1] = (byte)(pcount & 0xff);
		
		return pulsecount;	
	}
	
	public int getTotpulseCount() throws Exception {
		return this.totpulsecnt;
	}
	
	public byte[] getLPData() {
		return this.lpdata;
	}
	
	public byte[] getStartTime(){
		return this.starttime;
	}
	
	public byte[] getEndTime(){
		return this.endtime;
	}
	
	/**
	 * LP Scale : Active/Reactive Scale : 0x00,0x00
	 * @return
	 */
	public byte[] getLPScale(){
		byte[] scale = {-3,-3};
		return scale;
	}
	
	/**
	 * parsing LP Data (date+lpdata)
	 * @return
	 */
	private byte[] parseLPData() throws Exception {

		byte[] temp = new byte[(LEN_TOTSIZE/LEN_DASIZE)*96*LP_BLOCK_SIZE];//96 means one day LP count

		int i = 0;
		int idx = 0;
		this.totpulsecnt = 0;

		while(i < LEN_TOTSIZE/LEN_DASIZE){

			byte[] startdate = new byte[6];
			startdate = parseDate(i*LEN_DASIZE,LEN_DAYHDR-3);
			
			try {
				byte[] daybuf = parseLP(i*LEN_DASIZE+LEN_DAYHDR,startdate);
				System.arraycopy(daybuf,0,temp,idx,daybuf.length);
				idx += daybuf.length;
			} catch (Exception e) {
				logger.debug(e);
			}
			if(i == 0){
				this.starttime = new byte[6];
				System.arraycopy(startdate,0,starttime,0,4);
				this.starttime[4] = 0x00;
				this.starttime[5] = 0x0f;
			}

			i++;
		}

		this.lpdata = new byte[idx];
		System.arraycopy(temp,0,this.lpdata,0,idx);

		return lpdata;
	}
	

	
	/**
	 * parse lp data
	 * @param start
	 * @return
	 */
	private byte[] parseLP(int start,byte[] startdate) throws Exception {
		
		byte[] temp   = new byte[LP_BLOCK_SIZE*24*60/LEN_INTERVAL];		
		String lpdate = DataFormat.hexDateToStr(startdate);		

		int idx = start;

		byte[] lpchandata = new byte[LEN_CHANREC*LEN_CHANSIZE+2];
		int i = 0;
		int ofs = 0;
		int pulsecnt = 0;
		while(i < 24*60/LEN_INTERVAL){
			
			try{
				lpdate = Util.addMinYymmdd(lpdate,LEN_INTERVAL);

				lpchandata = parseChanData(idx,lpdate);

				if(lpchandata != null && lpchandata.length == LEN_CHANREC*LEN_CHANSIZE+2){
					
					byte[] date = DataFormat.strDate2Hex(lpdate);
					System.arraycopy(date,0,temp,ofs,6);
					this.endtime = date;
					ofs += 6;
					System.arraycopy(lpchandata,0,temp,ofs,10);
					ofs += 10;
					this.totpulsecnt++;
					pulsecnt++;
				}else{
					//if null count ++
					//this.emptycount++;
				}

			}catch(Exception e){
				logger.warn(e.getMessage());
			}
			idx += LEN_CHANREC*2; 
			i++;
		}		
		
		byte[] lp = new byte[LP_BLOCK_SIZE*pulsecnt];
		System.arraycopy(temp,0,lp,0,LP_BLOCK_SIZE*pulsecnt);

		return lp;
	}
	
	
	/**
	 * Get Channel Data(8) and Event Data(2)
	 * EVENT 
	 * EVENTFLAG 0x01
	 * INITVAL   0x02
	 * OVERFLOW  0x03
	 * 
	 * @param start
	 * @param lpdate
	 * @return
	 * @throws Exception
	 */
	private byte[] parseChanData(int start,String lpdate) throws Exception {
		
		byte[] lp = new byte[LEN_CHANREC*LEN_CHANSIZE+2];

		try{
			int i = start;
			int j = 0;
			int chan_val = 0;
			//boolean powerfail = false;
			byte event = 0x00;
			int eventflag = 0;
			//while(i < start+LEN_CHANREC*2){

				for(int k = 0; k < LEN_CHANREC;k++){
					chan_val = DataFormat.hex2dec(data,i,2);
					chan_val = chan_val & 0x7FFF;
					eventflag = DataFormat.hex2dec(data,i,2) & 0x7FFF;
					
					if(chan_val <= 0x7ffd){ //normal
						chan_val = (int) (chan_val*uke*60/LEN_INTERVAL);
						//chan_val = chan_val & 0xffff;
						event = 0x00;
					}
					else if(chan_val == 0x7ffe){ //pulse overflow
						chan_val = (int) (chan_val*uke*60/LEN_INTERVAL);
						//chan_val = chan_val & 0xffff;
						event = 0x03;
					}else if(chan_val == 0x7fff){//Initialization Value
						if(Long.parseLong(lpdate.substring(0,12)) 
							< Long.parseLong(metertime.substring(0,12))){
								chan_val = 0;
								event = 0x02;
							}

						else
							throw new Exception("INIT LP VAL");
						//0x7ffff is initialization value.
					}
					lp[j++] = (byte)(chan_val >> 24);
					lp[j++] = (byte)(chan_val >> 16);
					lp[j++] = (byte)(chan_val >> 8);
					lp[j++] = (byte)(chan_val & 0xff);
					i += 2;
				}

				lp[j++] = 0x00;
				if(eventflag == 1)
					lp[j++] = 0x00;
				else
					lp[j++] = event;

			return lp;
		}catch(Exception e){
			//logger.warn(e);
			return null;
		}

	}

	
	/**
	 * Get Date 
	 * YYYY MM DD (4yte)
	 * @param start - start offset
	 * @param end   - end offset
	 * @return
	 */
	private byte[] parseDate(int start, int len) throws Exception {
		
		byte[] date = new byte[4];
		String s = DataFormat.bcd2str(data,start,len);
		
		int curryy = (Integer.parseInt(DateTimeUtil
                .getCurrentDateTimeByFormat("yyyy"))/100)*100;
		int year   = Integer.parseInt(s.substring( 0,2));
		if(year != 0){
			year   = Integer.parseInt(s.substring( 0,2))+curryy;
		}
		int month  = Integer.parseInt(s.substring( 2,4));
		int day    = Integer.parseInt(s.substring( 4,6));
		
		date[0] = (byte) (year >> 8);
		date[1] = (byte) (0xff & year);
		date[2] = (byte) month;
		date[3] = (byte) day;
		
		return date;
	}
	
	
	/**
	 * load profile day record year.
	 *  ex) 00~99
	 * @return
	 */
	public int parseYear(){
		return 0;
	}
	 
	/**
	 * load profile day record month.
	 *  ex) 01~12
	 * @return
	 */
	public int parseMonth() {
		 return 0;
	}
	
	/**
	 * load profile day record day.
	 *  ex) 01~31
	 * @return
	 */
	public int parseDay() {
		return day;
	}
	
	
	/**
	 * load profile record day of week counter.
	 * 1 = Sunday
	 * 2 = Monday
	 * 3 = Tuesday
	 * 4 = Wednesday
	 * 5 = Thursday
	 * 6 = Friday
	 * 7 = Saturday
	 * 
	 * @return
	 */
	public int parseDowk() {
		return 0;
	}
	
	/**
	 * holiday status.
	 * true(1)  : is a holiday.
	 * false(0) : not a holiday.   
	 * @return
	 */
	public boolean isHoliday() {
		return false;
	}
	
	/**
	 * Daylight savings time status Load profile data is always kept
	 * in standard time.
	 * 
	 * This status flag indicates whether or not the meter time clock
	 * was using daylight savings time when the day record was
	 * initialized.
	 * 
	 * true (1) : daylight saving time.
	 * false(0) : standard time.
	 * 
	 * @return
	 */
	public boolean isDST() {
		return false;
	}

	
	/**
	 * pulse count
	 * if one day 96 pulse
	 * return 1
	 * @return
	 */
	
	public int parseCount() {
		return this.day;
	}
	
	public int parseTotalSize() throws Exception {
		return parseLPData().length;
	}
	
}
