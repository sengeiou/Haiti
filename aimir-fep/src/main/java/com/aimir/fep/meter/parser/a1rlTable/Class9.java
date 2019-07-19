/** 
 * @(#)Class9.java       1.0 04/09/16 *
 * 
 * 
 * Copyright (c) 2004-2005 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.a1rlTable;

import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;
import com.aimir.util.DateTimeUtil;

/**
 * @author Park YeonKyoung yeonkyoung@hanmail.net
 */
public class Class9 {

	public static final int OFS_SYSERR  = 1;
	public static final int OFS_SYSWARN = 4;
	public static final int OFS_SYSSTAT = 5;
	public static final int OFS_CUMDDR  = 6;
	public static final int OFS_CUMDPUL = 7;
	public static final int OFS_PWRLOG  = 8;
	public static final int OFS_PSTART  = 12;
	public static final int OFS_PEND    = 18;
	public static final int OFS_TD      = 27;
	public static final int OFS_TRI     = 33;
	public static final int OFS_DATATR  = 35;
	public static final int OFS_DATREP  = 38;
	public static final int OFS_DATMOD  = 41;
	public static final int OFS_CUMDR   = 44;
	public static final int OFS_CUMCOMM = 45;
	public static final int OFS_CUMOUT  = 46;
	
	public static final int LEN_SYSERR  = 3;
	public static final int LEN_SYSWARN = 1;
	public static final int LEN_SYSSTAT = 1;
	public static final int LEN_CUMDDR  = 1;
	public static final int LEN_CUMDPUL = 1;
	public static final int LEN_PWRLOG  = 4;
	public static final int LEN_PSTART  = 6;
	public static final int LEN_PEND    = 6;
	public static final int LEN_TD      = 6;
	public static final int LEN_TRI     = 2;
	public static final int LEN_DATATR  = 3;
	public static final int LEN_DATREP  = 3;
	public static final int LEN_DATMOD  = 3;
	public static final int LEN_CUMDR   = 1;
	public static final int LEN_CUMCOMM = 1;
	public static final int LEN_CUMOUT  = 2;
	
	private byte[] data;
	
	/**
	 * Constructor
	 * @param data
	 */
	public Class9 (byte[] data){
		this.data = data;
	}
	
	public byte parseSYSWARN() {
		return data[OFS_SYSWARN];	
	}
	
	public byte[] parseSYSERR() throws Exception{
		return DataFormat.select(data,OFS_SYSERR,LEN_SYSERR);
	}
	
	public byte[] parseSYSSTATUS() throws Exception {

		byte[] b = new byte[6];
		b[0] = parseSYSWARN();
		System.arraycopy(parseSYSERR(),0,b,1,3);
		b[4] = data[OFS_SYSSTAT];
		b[5] = 0x00;
		
		return b;
	}
		
	/**
	 * Cumulative day count from Last demand reset.
	 * @return
	 */	
	public long parseCUMDDR() throws Exception {
		return DataFormat.bcd2long(data,OFS_CUMDDR,LEN_CUMDDR);
	}


	/**
	 * Cumulative day count from Last pulse.
	 * @return
	 */
	public long parseCUMDPUL() throws Exception {
		return DataFormat.bcd2long(data,OFS_CUMDPUL,LEN_CUMDPUL);
	}
	
	/**
	 * Cumulative Power fail time.
	 * @return
	 */
	public String parsePWRLOG(){
		return null;
	}
	
	/**
	 * start power fail time
	 * @return
	 */
	public String parsePSTART() throws Exception {
		return DataFormat.bcd2str(data,OFS_PSTART,LEN_PSTART);
	}
	
	/**
	 * end power fail time
	 * @return
	 */
	public String parsePEND() throws Exception {
		return DataFormat.bcd2str(data,OFS_PEND,LEN_PEND);
	}
	
	/**
	 * now date and time
	 * @return
	 */
	public byte[] parseTD() throws Exception {
		
		byte[] date = new byte[7];

		String s = DataFormat.bcd2str(data,OFS_TD,LEN_TD);

		int curryy = (Integer.parseInt(DateTimeUtil
                .getCurrentDateTimeByFormat("yyyy"))/100)*100;
		int year   = Integer.parseInt(s.substring( 0,2));
		if(year != 0){
			year   = Integer.parseInt(s.substring( 0,2))+curryy;
		}
		int month  = Integer.parseInt(s.substring( 2,4));
		int day    = Integer.parseInt(s.substring( 4,6));
		int hh     = Integer.parseInt(s.substring( 6,8));
		int mm     = Integer.parseInt(s.substring( 8,10));
		int ss     = Integer.parseInt(s.substring(10,12));
		
		date[0] = (byte) (year >> 8);
		date[1] = (byte) (0xff & year);
		date[2] = (byte) month;
		date[3] = (byte) day;
		date[4] = (byte) hh;
		date[5] = (byte) mm;
		date[6] = (byte) ss;
		
		return date;
		
	}
	
	
	public String getYyyymmdd() throws Exception {
		
		StringBuffer b = new StringBuffer();
		String s = DataFormat.bcd2str(data,OFS_TD,LEN_TD);

		int curryy = (Integer.parseInt(DateTimeUtil
                .getCurrentDateTimeByFormat("yyyy"))/100)*100;
		int year   = Integer.parseInt(s.substring( 0,2));
		if(year != 0){
			year   = Integer.parseInt(s.substring( 0,2))+curryy;
		}
		int month  = Integer.parseInt(s.substring( 2,4));
		int day    = Integer.parseInt(s.substring( 4,6));
		
		b.append(Util.frontAppendNStr('0',Integer.toString(year),4));
		b.append(Util.frontAppendNStr('0',Integer.toString(month),2));
		b.append(Util.frontAppendNStr('0',Integer.toString(day),2));
		
		return b.toString();
		
	}
	
	public String getYyyymmddhhmm() throws Exception {
		
		StringBuffer b = new StringBuffer();
		String s = DataFormat.bcd2str(data,OFS_TD,LEN_TD);

		int curryy = (Integer.parseInt(DateTimeUtil
                .getCurrentDateTimeByFormat("yyyy"))/100)*100;
		int year   = Integer.parseInt(s.substring( 0,2));
		if(year != 0){
			year   = Integer.parseInt(s.substring( 0,2))+curryy;
		}
		int month  = Integer.parseInt(s.substring( 2,4));
		int day    = Integer.parseInt(s.substring( 4,6));
		int hh     = Integer.parseInt(s.substring( 6,8));
		int mm     = Integer.parseInt(s.substring( 8,10));
		
		b.append(Util.frontAppendNStr('0',Integer.toString(year),4));
		b.append(Util.frontAppendNStr('0',Integer.toString(month),2));
		b.append(Util.frontAppendNStr('0',Integer.toString(day),2));
		b.append(Util.frontAppendNStr('0',Integer.toString(hh),2));
		b.append(Util.frontAppendNStr('0',Integer.toString(mm),2));
		
		return b.toString();
	}

	public String getYyyymmddhhmmss() throws Exception {
		
		StringBuffer b = new StringBuffer();
		String s = DataFormat.bcd2str(data,OFS_TD,LEN_TD);

		int curryy = (Integer.parseInt(DateTimeUtil
                .getCurrentDateTimeByFormat("yyyy"))/100)*100;
		int year   = Integer.parseInt(s.substring( 0,2));
		if(year != 0){
			year   = Integer.parseInt(s.substring( 0,2))+curryy;
		}
		int month  = Integer.parseInt(s.substring( 2,4));
		int day    = Integer.parseInt(s.substring( 4,6));
		int hh     = Integer.parseInt(s.substring( 6,8));
		int mm     = Integer.parseInt(s.substring( 8,10));
		int ss     = Integer.parseInt(s.substring(10,12));
		
		b.append(Util.frontAppendNStr('0',Integer.toString(year),4));
		b.append(Util.frontAppendNStr('0',Integer.toString(month),2));
		b.append(Util.frontAppendNStr('0',Integer.toString(day),2));
		b.append(Util.frontAppendNStr('0',Integer.toString(hh),2));
		b.append(Util.frontAppendNStr('0',Integer.toString(mm),2));
		b.append(Util.frontAppendNStr('0',Integer.toString(ss),2));
		
		return b.toString();
	}

	
	public String getYyyymmddhhmmss(long delay) throws Exception {
		
		StringBuffer b = new StringBuffer();
		String s = DataFormat.bcd2str(data,OFS_TD,LEN_TD);

		int curryy = (Integer.parseInt(DateTimeUtil
                .getCurrentDateTimeByFormat("yyyy"))/100)*100;
		int year   = Integer.parseInt(s.substring( 0,2));
		if(year != 0){
			year   = Integer.parseInt(s.substring( 0,2))+curryy;
		}
		int month  = Integer.parseInt(s.substring( 2,4));
		int day    = Integer.parseInt(s.substring( 4,6));
		int hh     = Integer.parseInt(s.substring( 6,8));
		int mm     = Integer.parseInt(s.substring( 8,10));
		int ss     = Integer.parseInt(s.substring(10,12))+ (int)(delay/1000);
		
		b.append(Util.frontAppendNStr('0',Integer.toString(year),4));
		b.append(Util.frontAppendNStr('0',Integer.toString(month),2));
		b.append(Util.frontAppendNStr('0',Integer.toString(day),2));
		b.append(Util.frontAppendNStr('0',Integer.toString(hh),2));
		b.append(Util.frontAppendNStr('0',Integer.toString(mm),2));
		b.append(Util.frontAppendNStr('0',Integer.toString(ss),2));
		
		return b.toString();
	}
	
	public long getDateTime() throws Exception {
		return Util.getMilliTimes(getYyyymmddhhmmss());
	}
	
	
	/**
	 * 
	 * @return
	 */
	public String parseTRI() throws Exception {
		return DataFormat.bcd2str(data,OFS_TRI,LEN_TRI);
	}
	
	/**
	 * Reset Date and Time.
	 * @return
	 */
	public byte[] parseDATATR() throws Exception {
		
		byte[] date = new byte[7];
		
		String s = DataFormat.bcd2str(data,OFS_DATATR,LEN_DATATR);
		
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
		date[4] = 0x00;
		date[5] = 0x00;
		date[6] = 0x00;

		return date;
	
	}
	
	/**
	 * 
	 * @return
	 */
	public String parseDATREP() throws Exception {
		return DataFormat.bcd2str(data,OFS_DATREP,LEN_DATREP);
	}
	
	/**
	 * 
	 * @return
	 */
	public String parseDATMOD() throws Exception {
		return DataFormat.bcd2str(data,OFS_DATMOD,LEN_DATMOD);
	}
	
	
	/**
	 * Reset Count.
	 * @return
	 */
	public int parseCUMDR(){
		return DataFormat.hex2unsigned8(data[OFS_CUMDR]);
	} 
	
	/**
	 * 
	 * @return
	 */
	public long parseCUMCOMM() throws Exception {
		return DataFormat.bcd2long(data,OFS_CUMCOMM,LEN_CUMCOMM);
	} 
	
	/**
	 * 
	 * @return
	 */
	public long parseCUMOUT() throws Exception {
		return DataFormat.bcd2long(data,OFS_CUMOUT,LEN_CUMOUT);
	} 
	
	/**
	 * Get Meter Time Difference 
	 * Between Server Time and Meter Time
	 * @return secs
	 * @throws Exception
	 */
	public int getTimeDiff(long delaytime) throws Exception {
		
		long systime = System.currentTimeMillis()-delaytime;
		long metertime = Util.getMilliTimes(getYyyymmddhhmmss());
		return (int)((systime-metertime)/1000);
	}
	
	/**
	 * Get Meter Time Difference 
	 * Between Server Time and Meter Time
	 * @return secs
	 * @throws Exception
	 */
	public int getTimeDiff() throws Exception {		
		long systime = System.currentTimeMillis();
		long metertime = Util.getMilliTimes(getYyyymmddhhmmss());
		return (int)((systime-metertime)/1000);
	}
	
}
