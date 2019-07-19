/** 
 * @(#)ST132.java       1.0 06/12/14 *
 * 
 * Relay On and Off Event log table Class.
 * Copyright (c) 2006-2007 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.SM110Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;
import com.aimir.util.DateTimeUtil;

/**
 * @author Park YeonKyoung goodjob@nuritelecom.com
 */
public class ST132 implements java.io.Serializable {

	private static final long serialVersionUID = 8378285320785311897L;
	private static Log log = LogFactory.getLog(ST132.class);
	private byte[] data;

    private final int OFS_NBR_RELAY_SWITCHON  = 0;
    private final int OFS_NBR_RELAY_SWITCHOFF = 2;

    private final int LEN_NBR_RELAY_SWITCHON  = 2;
    private final int LEN_NBR_RELAY_SWITCHOFF = 2;

    private final int LEN_EVENTTIME           = 6;
    private final int LEN_USERID              = 2;
    private final int LEN_OPERATETYPE         = 1;

    private int nbr_relay_switchon;
    private int nbr_relay_switchoff;
    private EventLogData[] eventdata;
    
    public ST132() {}

	/**
	 * Constructor .<p>
	 */
	public ST132(byte[] data) 
    {
		this.data = data;
        parse();
	}

    public int getNBR_RELAY_SWITCHON()
    {
        return this.nbr_relay_switchon;
    }

    public int getNBR_RELAY_SWITCHOFF()
    {
        return this.nbr_relay_switchoff;
    }

    public void parse()
    {
        try{
            this.nbr_relay_switchon 
                = DataFormat.hex2unsigned16(
                    DataFormat.LSB2MSB(
                        DataFormat.select(
                            data,OFS_NBR_RELAY_SWITCHON,LEN_NBR_RELAY_SWITCHON)));

            this.nbr_relay_switchoff
                = DataFormat.hex2unsigned16(
                    DataFormat.LSB2MSB(
                        DataFormat.select(
                            data,OFS_NBR_RELAY_SWITCHOFF,LEN_NBR_RELAY_SWITCHOFF)));

            int ofs = LEN_NBR_RELAY_SWITCHON+LEN_NBR_RELAY_SWITCHOFF;

            String eventtime;
            int    userid;
            int    type;

            for(int i = 0; i < 10; i++){
                eventtime = getYymmddhhmmss(data,ofs,LEN_EVENTTIME);
                ofs += LEN_EVENTTIME;
                userid = DataFormat.hex2unsigned16(
                            DataFormat.LSB2MSB(
                                DataFormat.select(
                                    data,ofs,LEN_USERID)));
                ofs += LEN_USERID;
                type = DataFormat.hex2unsigned8(data[ofs]);
                ofs += LEN_OPERATETYPE;

                this.eventdata[i] = new EventLogData();
                this.eventdata[i].setDate(eventtime.substring(0,8));
                this.eventdata[i].setTime(eventtime.substring(8,14));
                this.eventdata[i].setFlag(type);
                this.eventdata[i].setMsg(getEventMessage(type));
            }

            /*
            for(int i = 0; i < 10; i++){
                eventtime = getYymmddhhmmss(data,ofs,LEN_EVENTTIME);
                ofs += LEN_EVENTTIME;
                userid = DataFormat.hex2unsigned16(
                            DataFormat.LSB2MSB(
                                DataFormat.select(
                                    data,ofs,LEN_USERID)));
                ofs += LEN_USERID;
                type = 2;   //relay switch off: 2

                res.put("Event Time",         eventtime);
                res.put("User ID",            ""+userid);
                res.put("Event Type",         ""+type);
                res.put("Event Type Message", getEventMessage(type));
            }
            */

        } catch(Exception e){
            log.warn("parse relay status log error=>"+e.getMessage());
        }
    }

    public EventLogData[] getEventLog()
    {
        return this.eventdata;
    }

    private String getYymmddhhmmss(byte[] b, int offset, int len)
        throws Exception {

            int blen = b.length;
            if(blen-offset < 6)
                throw new Exception("YYMMDDHHMMSS FORMAT ERROR : "+(blen-offset));
            if(len != 6)
                throw new Exception("YYMMDDHHMMSS LEN ERROR : "+len);

            int idx = offset;

            int yy = DataFormat.hex2unsigned8(b[idx++]);
            int mm = DataFormat.hex2unsigned8(b[idx++]);
            int dd = DataFormat.hex2unsigned8(b[idx++]);
            int hh = DataFormat.hex2unsigned8(b[idx++]);
            int MM = DataFormat.hex2unsigned8(b[idx++]);
            int ss = DataFormat.hex2unsigned8(b[idx++]);

            StringBuffer ret = new StringBuffer();

            int currcen = (Integer.parseInt(DateTimeUtil
                    .getCurrentDateTimeByFormat("yyyy"))/100)*100;

            int year   = yy;
            if(year != 0){
                year = yy + currcen;
            }

            ret.append(Util.frontAppendNStr('0',Integer.toString(year),4));
            ret.append(Util.frontAppendNStr('0',Integer.toString(mm),2));
            ret.append(Util.frontAppendNStr('0',Integer.toString(dd),2));
            ret.append(Util.frontAppendNStr('0',Integer.toString(hh),2));
            ret.append(Util.frontAppendNStr('0',Integer.toString(MM),2));
            ret.append(Util.frontAppendNStr('0',Integer.toString(ss),2));

            return ret.toString();

    }

    private String getEventMessage(int type)
    {
        switch(type){
            case 0:
                return "command switch on";
            case 1:
                return "use press key to switch on";
            case 2:
                return "switch off";
            default:
                return "unknown event";
        }
    }

}
