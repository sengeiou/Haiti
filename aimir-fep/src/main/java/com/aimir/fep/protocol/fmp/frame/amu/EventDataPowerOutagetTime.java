package com.aimir.fep.protocol.fmp.frame.amu;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;

/**
 * Power Outage Time
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 4. 19. 오후 2:23:46$
 */
public class EventDataPowerOutagetTime {
	
	private static Log log = LogFactory.getLog(EventDataPowerOutagetTime.class);
	
	public static final int OFS_TIME_ZONE 	= 0;
	public static final int OFS_DST_VALUE 	= 2;
	public static final int OFS_YEAR		= 4;
	public static final int OFS_MONTH		= 6;
	public static final int OFS_DAY 		= 7;
	public static final int OFS_HOUR 		= 8;
	public static final int OFS_MINUTE 		= 9;
	public static final int OFS_SECOND 		= 10;
	
	public static final int LEN_TIME_ZONE 	= 2;
	public static final int LEN_DST_VALUE 	= 2;
	public static final int LEN_YEAR 		= 2;
	    
	byte[] rawData 		= null;
	
	
	/**
	 * constructor
	 */
	public EventDataPowerOutagetTime(){
	}
	
	/**
	 * constructor
	 * @param rawData
	 * @throws Exception
	 */
	public EventDataPowerOutagetTime(byte[] rawData) throws Exception{
		
		try{
			this.rawData = rawData;
		}catch(Exception e){
			throw e;
		}
	}
	
	/**
	 * get Time Zone
	 * @return
	 * @throws Exception
	 */
	public int getTimeZone() throws Exception{
		return DataFormat.hex2dec(DataFormat.select(rawData,OFS_TIME_ZONE,LEN_TIME_ZONE));
	}
	
	/**
	 * get Dst Value
	 * @return
	 * @throws Exception
	 */
	public int getDstValue() throws Exception{
		return DataFormat.hex2dec(DataFormat.select(rawData,OFS_DST_VALUE,LEN_DST_VALUE));
	}

	/**
	 * get Year
	 * @return
	 * @throws Exception
	 */
	public int getYear() throws Exception{
		return DataFormat.hex2dec(DataFormat.select(rawData,OFS_YEAR,LEN_YEAR));
	}

	/**
	 * get Month
	 * @return
	 * @throws Exception
	 */
	public int getMonth() throws Exception {
		return DataFormat.hex2unsigned8(rawData[OFS_MONTH]);
	}
	
	/**
	 * get Day
	 * @return
	 * @throws Exception
	 */
	public int getDay() throws Exception {
		return DataFormat.hex2unsigned8(rawData[OFS_DAY]);
	}
	
	/**
	 * get Hour
	 * @return
	 * @throws Exception
	 */
	public int getHour() throws Exception {
		return DataFormat.hex2unsigned8(rawData[OFS_HOUR]);
	}
	
	/**
	 * get Minute
	 * @return
	 * @throws Exception
	 */
	public int getMinute() throws Exception {
		return DataFormat.hex2unsigned8(rawData[OFS_MINUTE]);
	}
	
	/**
	 * get Second
	 * @return
	 * @throws Exception
	 */
	public int getSecond() throws Exception {
		return DataFormat.hex2unsigned8(rawData[OFS_SECOND]);
	}
	
	/**
	 * get Time
	 * @return
	 * @throws Exception
	 */
	public String getTime() throws Exception  {
		
		int timeZone 	= getTimeZone();
		int dstValue 	= getDstValue();
        int year 		= getYear();
        int month 		= getMonth();
        int day 		= getDay();
        int hour 		= getHour();
        int min 		= getMinute();
        int sec 		= getSecond();

        StringBuffer ret = new StringBuffer();
        ret.append(Util.frontAppendNStr('0',Integer.toString(timeZone),4));
        ret.append(Util.frontAppendNStr('0',Integer.toString(dstValue),4));
        ret.append(Util.frontAppendNStr('0',Integer.toString(year),4));
        ret.append(Util.frontAppendNStr('0',Integer.toString(month),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(day),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(hour),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(min),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(sec),2));
        return ret.toString();
    }
	
	 public String toString()
	    {
	        StringBuffer sb = new StringBuffer();
	        try{
	            sb.append("EventData PowerOutAge Time[");        
	            sb.append("(Time =").append(""+getTime()).append(')');
	            sb.append("]\n");
	        }catch(Exception e){
	            log.warn("EventData PowerOutAge Time  TO STRING ERR=>"+e.getMessage());
	        }

	        return sb.toString();
	    }
}


