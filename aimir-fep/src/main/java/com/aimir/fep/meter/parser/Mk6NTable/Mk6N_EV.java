/*
 * @(#)Mk6N_EV.java       1.0 2008/08/22 *
 *
 * Load Profile.
 * Copyright (c) 2007-2008 NuriTelecom, Inc.
 * All rights reserved. *
 * This software is the confidential and proprietary information of
 * Nuritelcom, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Nuritelecom.
 */

package com.aimir.fep.meter.parser.Mk6NTable;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Hex;
import com.aimir.util.DateTimeUtil;

/**
 * @author kaze kaze@nuritelecom.com
 */
public class Mk6N_EV implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5545361675626142783L;

	Log log = LogFactory.getLog(Mk6N_EV.class);

	public byte[] rawData = null;
    public int channelCnt;
    public int eventCnt;
    public EventLogData[] eventData;
    public String statusFlag="";
    public String meterId="";

    public ConfigurationEventChannel statusConfig;
    public ConfigurationEventChannel[] eventConfig;

    private static final int OFF_NBR_EVT_CH=0;
    private static final int OFF_EVT_STATUS_CONFIG=1;
    private static final int OFF_EVT_DATA_CONFIG=11;
    private int OFF_NBR_EVT_ENTRIES;
    private int OFF_EVT_DATA;

    private static final int LEN_NBR_EVT_CH=1;
    private static final int LEN_EVT_STATUS_CONFIG=10;
    private static final int LEN_EVT_DATA_CONFIG=10;
    private static final int LEN_NBR_EVT_ENTRIES=2;
    private int LEN_EVT_DATA;

    DecimalFormat dformat = new DecimalFormat("#0.000000");


	/**
	 * Constructor .<p>
	 * @param data - read data (header,crch,crcl)
	 */
	public Mk6N_EV(byte[] rawData, String meterId) {
		this.rawData = rawData;
		try {
			this.meterId=meterId;
			parse();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


    public Mk6N_EV() {
	}


	public int getChannelCnt() throws Exception {
    	channelCnt=rawData[OFF_NBR_EVT_CH];
        return channelCnt;
    }

    public int getEventCnt() throws Exception {
    	OFF_NBR_EVT_ENTRIES=OFF_EVT_STATUS_CONFIG+LEN_EVT_STATUS_CONFIG+LEN_EVT_DATA_CONFIG*channelCnt;
    	eventCnt=DataFormat.hex2unsigned16(DataFormat.select(rawData, OFF_NBR_EVT_ENTRIES, LEN_NBR_EVT_ENTRIES));
    	if(eventCnt>20) eventCnt=20;
    	return eventCnt;
    }

    public ConfigurationEventChannel[] getEventDataConfig() throws Exception {
        eventConfig = new ConfigurationEventChannel[channelCnt];
        for(int i=0; i<channelCnt; i++){
        	eventConfig[i]=new ConfigurationEventChannel(DataFormat.select(rawData, OFF_EVT_DATA_CONFIG+LEN_EVT_DATA_CONFIG*i, LEN_EVT_DATA_CONFIG));
        }
        return eventConfig;
    }
    public ConfigurationEventChannel getStatusConfig() throws Exception {
    	statusConfig=new ConfigurationEventChannel(DataFormat.select(rawData, OFF_EVT_STATUS_CONFIG, LEN_EVT_STATUS_CONFIG));
        return statusConfig;
    }

    @SuppressWarnings("unchecked")
    public EventLogData[] parse() throws Exception {
    	log.info("\n//-------------------------------------------------------");
		log.info("//  Mk6N Event Parser Start");
		log.info("//-------------------------------------------------------");
    	channelCnt=getChannelCnt();
		eventCnt=getEventCnt();
		statusConfig=getStatusConfig();
		eventConfig=getEventDataConfig();

    	eventData = new EventLogData[eventCnt];
        OFF_EVT_DATA=LEN_NBR_EVT_CH+LEN_EVT_STATUS_CONFIG+LEN_EVT_DATA_CONFIG*channelCnt+LEN_NBR_EVT_ENTRIES;
        LEN_EVT_DATA=statusConfig.getCh_size()+getEventOffSet(channelCnt);

        //Map eventClassMap=getMeterEventClassMap(meterId);
        for(int i = 0; i < eventData.length; i++){
            byte[] eventEntry = new byte[statusConfig.getCh_size()];
            eventEntry =DataFormat.select(rawData,OFF_EVT_DATA+(i*LEN_EVT_DATA),LEN_EVT_DATA);

            StatusFlag status=new StatusFlag(DataFormat.select(eventEntry,0,statusConfig.getCh_size()));
            if(status.getLog().length()>0){
            	statusFlag=statusFlag+status.getLog();
            }
            log.info("==== EVENT ENTRY["+i+"] - Offset: "+(OFF_EVT_DATA+(i*LEN_EVT_DATA))+", Len: "+LEN_EVT_DATA+", Raw: "+Hex.decode(eventEntry));
            EventLogData tempEventData= parseEvent(eventEntry);

            
            /*
            //Correct Event Message
            if(tempEventData.getMsg().matches("[0-9a-zA-z\\s:\\.]+")){
	            if(eventClassMap.containsKey(tempEventData.getMsg())){
	            	String mapValue=(String)eventClassMap.get(tempEventData.getMsg());
	            	value=Integer.parseInt(mapValue.substring(mapValue.lastIndexOf(".")+1));
	            }else{
	            	value=getValue(eventClassMap);
	            	//insertMeterEventClass(meterId, tempEventData.getMsg(),value);
	            	//Insert EventClassMap
	            	//eventClassMap.put(tempEventData.getMsg(), value);
	            }
            }
            //Incorrect Event Message
            else{
            	log.error("Invalid Event Msg: "+tempEventData.getMsg());
            	if(eventClassMap.containsKey("Invalid Event Msg")){
	            	String mapValue=(String)eventClassMap.get("Invalid Event Msg");
	            	value=Integer.parseInt(mapValue.substring(mapValue.lastIndexOf(".")+1));
	            }else{
	            	value=getValue(eventClassMap);
	            	//insertMeterEventClass(meterId, "Invalid Event Msg",value);
	            	//Insert EventClassMap
	            	eventClassMap.put("Invalid Event Msg", value);
	            }
            }
            */

            eventData[i]= new EventLogData();
            eventData[i].setDate(tempEventData.getDate());
            eventData[i].setTime(tempEventData.getTime());
            eventData[i].setKind("STE");
            eventData[i].setMsg(tempEventData.getMsg());
            eventData[i].setFlag(status.getFlagNumber());
        }
        return eventData;
    }

    public int getEventOffSet(int idx){
    	int eventOffset=0;
    	for(int i=0;i<idx;i++){
    		eventOffset=eventOffset+eventConfig[i].getCh_size();
    	}
    	return eventOffset;
    }

    private EventLogData parseEvent(byte[] eventEntry) throws Exception{
    	EventLogData eventData = new EventLogData();

    	for(int i=0;i<channelCnt;i++){
    		//Time
    		if(eventConfig[i].getCh_type_char()=='T'){
    			String eventDate=getEventDate((int)DataFormat.hex2unsigned32(DataFormat.select(eventEntry, statusConfig.getCh_size()+getEventOffSet(i), eventConfig[i].getCh_size())));
    			eventData.setDate(eventDate.substring(0, 8));
    			eventData.setTime(eventDate.substring(8, 14));
    			log.info("Event Date: "+eventData.getDate()+eventData.getTime());
    		}
    		//Event Msg
    		else{
    			eventData.setMsg(new String(eventEntry, statusConfig.getCh_size()+getEventOffSet(i), eventConfig[i].getCh_size()).trim());
    			log.info("Event Msg: "+eventData.getMsg());
    		}
    	}
    	return eventData;
    }

	public String getEventDate(int Sec)
			throws Exception {

		String dateString = new String();

		try {
			Calendar c = Calendar.getInstance();

			int yy = 1996;
			int mm = 1;
			int day = 1;
			int HH = 0;
			int MM = 0;
			int SS = 0;

			if (mm < 1 || mm > 12)
				throw new Exception("Month Wrong Format!");
			if (day < 1 || day > 31)
				throw new Exception("Day Wrong Format !");
			if (HH < 0 || HH > 24)
				throw new Exception("Hour Wrong Format !");
			if (MM < 0 || MM > 59)
				throw new Exception("Minutes Wrong Format !");

			/* why mm-1 because month start from 0 */
			c.set(yy, mm - 1, day, HH, MM, SS+Sec);
			dateString = DateTimeUtil.getDateString(c.getTime());

		} catch (Exception e) {
			throw new Exception("Util.addMinYymmdd() : " + e.getMessage());
		}
		return dateString;
	}
	
	/*

    public void insertMeterEventClass(String meterId, String descr, int value) {

		StringBuffer query = new StringBuffer();
		PreparedStatement pstmt = null;
		Connection dbConn=null;
		try {
			dbConn = JDBCUtil.getConnection();

			MOINSTANCE mo = EMUtil.getMeterMO(meterId);
            String vendor="10";;
            String model="18";
            if(mo!=null){
                vendor = mo.getPropertyValueString("vendor");
                model = mo.getPropertyValueString("model");
            }

			query.append("insert into metereventclass ");
			query.append("(id,value,");
			query.append(" metertype,vendor,");
			query.append(" model,kind, name,descr,issupport,priority)\n");
			query.append(" values(?,?,?,?,?,?,?,?,?,?)\n");
			int idx = 0;
			String id="STE."+AimirModel.MT_ENERGY_METER+"."+vendor+"."+model+"."+value;
			pstmt = dbConn.prepareStatement(JDBCUtil.toDB(query.toString()));
			pstmt.setString(++idx, JDBCUtil.toDB(id));
			pstmt.setString(++idx, JDBCUtil.toDB(String.valueOf(value)));
			pstmt.setString(++idx, JDBCUtil.toDB(String.valueOf(AimirModel.MT_ENERGY_METER)));
			pstmt.setString(++idx, JDBCUtil.toDB(vendor));
			pstmt.setString(++idx, JDBCUtil.toDB(model));
			pstmt.setString(++idx, JDBCUtil.toDB("STE"));
			pstmt.setString(++idx, JDBCUtil.toDB(descr));
			pstmt.setString(++idx, JDBCUtil.toDB(descr));
			pstmt.setInt(++idx, 1);
			pstmt.setInt(++idx, 1);
			pstmt.executeUpdate();
		} catch (Exception ex) {
			log.error("save eventlogdata failed : query[" + query.toString());
			log.error("save eventlogdata failed : ", ex);
		} finally {
			try {
				if (dbConn != null)
					dbConn.close();
			} catch (Exception ex) {
			}
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (Exception ex) {
			}
		}
	}
	
	*/

    public int getValue(Map eventClass){
    	int max=0;
    	int value=0;
    	Set set = eventClass.keySet();
    	Iterator iterator = set.iterator();

    	while (iterator.hasNext()) {
    	    String mapKey = (String)iterator.next();
    	    String mapValue = (String)eventClass.get(mapKey);
    	    if(Integer.parseInt(mapValue.substring(mapValue.lastIndexOf(".")+1))>max){
    	    	max=Integer.parseInt(mapValue.substring(mapValue.lastIndexOf(".")+1));
    	    }
    	}
    	value=++max;
    	return value;
    }
    /**
     * @param meterId
     * @return Map Key:descr, Value:id
     */
    /*
    public Map getMeterEventClassMap(String meterId) {

    	Map eventClassMap =new HashMap();

		StringBuffer sql = new StringBuffer();

		sql.append(" SELECT ID,DESCR \n");
		sql.append(" FROM METEREVENTCLASS").append(" \n");
		sql.append(" WHERE VENDOR=? \n");
		sql.append("    AND MODEL=? \n");

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection dbConn = null;

		// log.debug("getCountOfEventLog SQL["+sql.toString()+"]");

		try {
		    MOINSTANCE mo = EMUtil.getMeterMO(meterId);
            String vendor="10";;
            String model="18";
            if(mo!=null){
                vendor = mo.getPropertyValueString("vendor");
                model = mo.getPropertyValueString("model");
            }

			dbConn = JDBCUtil.getConnection();
			pstmt = dbConn.prepareStatement(JDBCUtil.toDB(sql.toString()));
			pstmt.setString(1, vendor);
			pstmt.setString(2, model);

			rs = pstmt.executeQuery();
			while (rs.next()){
				eventClassMap.put(rs.getString("descr"), rs.getString("id"));
			}
		} catch (Exception ex) {
			log.error("getMeterEventClassMap :: query[" + sql.toString() + "]");
			log.error(ex, ex);
		} finally {
			JDBCUtil.close(rs, pstmt, dbConn);
		}

		return eventClassMap;
	}
	*/

    public EventLogData[] getEvent() {
        return this.eventData;
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

	    retValue.append("Mk6N_EV [ ")
	        .append("rawData = ").append(Hex.decode(this.rawData)).append('\n')
	        .append("channelCnt = ").append(this.channelCnt).append('\n')
	        .append("eventCnt = ").append(this.eventCnt).append('\n')
	        .append(" ]");

	    return retValue.toString();
	}

}