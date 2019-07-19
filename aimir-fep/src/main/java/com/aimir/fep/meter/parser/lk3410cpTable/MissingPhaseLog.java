/** 
 * @(#)MissingPhaseLog.java       1.0 07/11/14 *
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
public class MissingPhaseLog {
	
    public static final int LEN_NBR_MISSING_PHASE = 1;
    public static final int LEN_EVENT_TIME = 7;
    
	private byte[] data;
    private int ofs;
    private int cntMissingPhase;
    private int eventValue;//eventvalue
       
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public MissingPhaseLog(byte[] data, int ofs, int eventValue) {
		this.data = data;
        this.ofs = ofs;
        this.eventValue = eventValue;
	}
	
    /**
     * DEMAND_RESET_LOG
     */
    public EventLogData[] getMISSING_PHASE() throws Exception  {
        
        setNBR_MISSING_PHASE(ofs);
        int cnt = getNBR_MISSING_PHASE();
        ofs+=LEN_NBR_MISSING_PHASE;
        EventLogData[] eventdata = new EventLogData[cnt];
        
        for(int i=0; i<cnt; i++){
            
            String datetime = new DateTimeFormat(DataFormat.select(data, ofs, LEN_EVENT_TIME)).getDateTime();
            eventdata[i] = new EventLogData();
            eventdata[i].setDate(datetime.substring(0,8));
            eventdata[i].setTime(datetime.substring(8,14));
            eventdata[i].setKind("STE");
            eventdata[i].setFlag(eventValue);
            if(eventValue == 19){
                eventdata[i].setMsg("Phase(A) Error");
            }else if(eventValue == 20){
                eventdata[i].setMsg("Phase(B) Error");
            }else if(eventValue == 21){
                eventdata[i].setMsg("Phase(C) Error");
            }else if(eventValue == 22){
                eventdata[i].setMsg("Phase(A) Error Recovery");
            }else if(eventValue == 23){
                eventdata[i].setMsg("Phase(B) Error Recovery");
            }else if(eventValue == 24){
                eventdata[i].setMsg("Phase(C) Error Recovery");
            }else if(eventValue == 25){
                eventdata[i].setMsg("Power Failure");
            }else if(eventValue == 26){
                eventdata[i].setMsg("Power Restore");
            }

            ofs+=LEN_EVENT_TIME;
        }
        if (eventdata.length <1)
            return null;
        else
            return eventdata;
    }
    
    public void setNBR_MISSING_PHASE(int ofs1){
        cntMissingPhase= DataFormat.hex2unsigned8(data[ofs1]);
    }
    public int  getNBR_MISSING_PHASE(){
        return this.cntMissingPhase;
    }
}
