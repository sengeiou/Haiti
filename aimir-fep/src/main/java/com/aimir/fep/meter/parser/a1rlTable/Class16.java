/** 
 * @(#)Class16.java       1.0 04/09/20 *
 * 
 * Event Log Data Class.
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
import com.aimir.util.DateTimeUtil;

/**
 * @author Park YeonKyoung yeonkyoung@hanmail.net
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Class16 {

	public static final int OFS_EVREC = 0;
	public static final int LEN_EVREC = 7;
	
	private final int EVENT_BLOCK_SIZE = 9;
	
	private byte[] data;
	
    private Log logger = LogFactory.getLog(getClass());
	
	/**
	 * Constructor
	 * @param data
	 */
	public Class16(byte[] data){
		this.data = data;
	}
	
	
	/**
	 * Get Event Log Count.<p>
	 *
	 */
	public int parseEventCount(){

		if(data.length > 0)
			return (data.length/LEN_EVREC);
		else 
			return 0x00;
	}
	
	public byte[] parseLastEventTime(){

		int ecount = 0; // event count.
		byte[] eventdate = new byte[7];
		if(data.length > 0)
			ecount = data.length/LEN_EVREC;
		
		try{
			if(ecount > 0) {
				int i = 0;
				while(i < ecount){
					eventdate = parseDate(i*LEN_EVREC+1,LEN_EVREC-1);
					i++;
				}
			}
		}catch(Exception e){
			logger.debug(e);
		}
		//logger.debug("last ev time : "+Util.getHexString(eventdate));
		return eventdate;
	}
	
	public byte[] parseStartEventTime(){

		int ecount = 0; // event count.
		byte[] eventdate = new byte[7];
		if(data.length > 0)
			ecount = data.length/LEN_EVREC;
		
		try{
			if(ecount > 0) {
				int i = 0;
				eventdate = parseDate(i*LEN_EVREC+1,LEN_EVREC-1);
			}
		}catch(Exception e){
			logger.debug(e);
		}
		//logger.debug("start ev time : "+Util.getHexString(eventdate));
		return eventdate;
	}
	
	/**
	 * Parsing Event Log Data.<p>
	 *
	 */
	public byte[] parseEventData(){

		int ecount = 0; // event count.
	
		if(data.length > 0)
			ecount = data.length/LEN_EVREC;
			
		byte[] buffer = new byte[EVENT_BLOCK_SIZE*ecount];
		
		int idx = 0;

		try{
			if(ecount > 0) {
			
				int i = 0;
				while(i < ecount){
				
					byte[] eventdate = new byte[7];
					byte[] event     = new byte[2];
				
					event[1]  = data[i*LEN_EVREC];
					eventdate = parseDate(i*LEN_EVREC+1,LEN_EVREC-1);

					System.arraycopy(eventdate,0,buffer,idx,eventdate.length);
					idx += eventdate.length;
					System.arraycopy(event,0,buffer,idx,event.length);
					idx += 2;
					i++;
				}
			}
		}catch(Exception e){
			logger.debug(e);
		}

		return buffer;
	}


	/**
	 * Get Event Type.<p>
	 * 
	 * 0   = power fail event.<p>
	 * 1   = power up event.  <p>
	 * 2   = time before a date/time change event. including DST adjustments.<p>
	 * 3   = time after date/time change, including DST adjustments.<p>
	 * 4   = start of test mode.<p>
	 * 5   = end of test mode.<p>
	 * 6   = demand reset.<p>
	 * 255 = event log reset.<p>
	 * @return - Event Code Type.
	 */
	public int parseEVTYPE(){
		return DataFormat.hex2unsigned8(data[OFS_EVREC]);
	}


	/**
	 * power fail event.<p>
	 * @return 
	 */
	public boolean isPowerFailEvent() {
		if(parseEVTYPE() == 0)
			return true;
		return false;
	}


	/**
	 * power up event.<p>
	 * @return
	 */
	public boolean isPowerUpEvent() {
		if(parseEVTYPE() == 1)
			return true;
		return false;
	}
	
	/**
	 * time before a date/time change event. including DST adjustments.<p>
	 * @return
	 */
	public boolean isTimeBeforeChangeEvent() {
		if(parseEVTYPE() == 2)
			return true;
		return false;
	}
	
	/**
	 * time after date/time change, 
	 * including DST adjustments.<p>
	 * @return
	 */
	public boolean isTimeAfterChangeEvent() {
		if(parseEVTYPE() == 3)
			return true;
		return false;
	}
	
	/**
	 * start of test mode.<p>
	 * @return
	 */
	public boolean isStartOfTestMode() {
		if(parseEVTYPE() == 4)
			return true;
		return false; 
	}
	
	/**
	 * end of test mode.<p>
	 * @return
	 */
	public boolean isEndOfTestMode() {
		if(parseEVTYPE() == 5)
			return true;
		return false; 
	}
	
	/**
	 * demand reset.<p>
	 * @return
	 */
	public boolean isDemandReset() {
		if(parseEVTYPE() == 6)
			return true;
		return false; 
	}
	
	/**
	 * event log reset.<p>
	 * @return
	 */
	public boolean isEventLogReset() {
		if(parseEVTYPE() == 255)
			return true;
		return false; 
	}
	
	/**
	 * Year Of Event .<p>
	 * @return
	 */
	public int parseEVYR() throws Exception {
		return DataFormat.bcd2dec(data,OFS_EVREC+1,1);
	}
	
	/**
	 * Month Of Event.<p>
	 * @return
	 */
	public int parseEVMON() throws Exception {
		return DataFormat.bcd2dec(data,OFS_EVREC+2,1);
	}
	
	/**
	 * Day Of Event.<p>
	 * @return
	 */
	public int parseEVDAY() throws Exception {
		return DataFormat.bcd2dec(data,OFS_EVREC+3,1);
	}
	
	/**
	 * Hour Of Event.<p>
	 * @return
	 */
	public int parseEVHR() throws Exception {
		return DataFormat.bcd2dec(data,OFS_EVREC+4,1);
	}
	
	/**
	 * Minutes of Event.<p>
	 * @return
	 */
	public int parseEVMIN() throws Exception {
		return DataFormat.bcd2dec(data,OFS_EVREC+5,OFS_EVREC+6);
	}
	
	/**
	 * Second of event.<p>
	 * @return
	 */
	public int parseEVSEC() throws Exception {
		return DataFormat.bcd2dec(data,OFS_EVREC+6,OFS_EVREC+7);
	}

	/**
	 * Get Date 
	 * YYYY MM DD (4yte)
	 * @param start - start offset
	 * @param end   - end offset
	 * @return
	 */
	public byte[] parseDate(int start, int len) throws Exception {
		
		byte[] date = new byte[7];
		String s =  DataFormat.bcd2str(data,start,len);
		
		int curryy = (Integer.parseInt(DateTimeUtil
                .getCurrentDateTimeByFormat("yyyy"))/100)*100;
		int year   = Integer.parseInt(s.substring( 0,2));
		if(year != 0){
			year   = Integer.parseInt(s.substring( 0,2))+curryy;
		}
		int month  = Integer.parseInt(s.substring( 2, 4));
		int day    = Integer.parseInt(s.substring( 4, 6));
		int hh     = Integer.parseInt(s.substring( 6, 8));
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
	
}
