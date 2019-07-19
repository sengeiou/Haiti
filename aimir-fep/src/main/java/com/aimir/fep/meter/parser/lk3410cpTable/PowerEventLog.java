/** 
 * @(#)PowerEventLog.java       1.0 07/11/14 *
 * 
 * Actual Dimension Register Table.
 * Copyright (c) 2006-2007 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.lk3410cpTable;

import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.util.DataFormat;

/**
 * @author Kang SoYi ksoyi@nuritelecom.com
 */
public class PowerEventLog {
	
    public static final int LEN_EVENT_TIME = 7;
    
	private byte[] data;
    private int ofs;
    private int eventValue;
       
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public PowerEventLog(byte[] data, int ofs, int eventValue) {
		this.data = data;
        this.ofs = ofs;
        this.eventValue = eventValue;
	}
	
    /**
     * POWER_EVENT
     */
    public EventLogData getPOWER_EVENT() throws Exception  {
        
        EventLogData eventdata = null;

        String datetime = new DateTimeFormat(DataFormat.select(data, ofs, LEN_EVENT_TIME)).getDateTime();
        eventdata = new EventLogData();
        eventdata.setDate(datetime.substring(0,8));
        eventdata.setTime(datetime.substring(8,14));
        eventdata.setKind("STE");
        eventdata.setFlag(eventValue);
        ofs+=LEN_EVENT_TIME;

        return eventdata;
    }
}
