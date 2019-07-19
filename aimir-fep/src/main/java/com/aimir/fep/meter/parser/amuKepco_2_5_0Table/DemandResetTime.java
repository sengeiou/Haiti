package com.aimir.fep.meter.parser.amuKepco_2_5_0Table;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;

/**
 * Demand Reset Time
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 4. 9. 오후 5:55:52$
 */
public class DemandResetTime {
	
	private static Log log = LogFactory.getLog(DemandResetTime.class);
	
	private static final int OFS_YEAR 					= 0;
	private static final int OFS_MONTH 					= 2;
	private static final int OFS_DAY 					= 3;
	private static final int OFS_HOUR 					= 4;
	private static final int OFS_MIN 					= 5;
	private static final int OFS_SEC					= 6;
	//private static final int OFS_TYPE 				= 7;
	
	private static final int LEN_YEAR 					= 2;
	private static final int LEN_DEMAND_RESETTIME 		= 8;
	// Demand Reset Time Data
	private byte[] rawData = null;
	
	/**
	 * Constructor
	 * @param rawData
	 */
	public DemandResetTime(byte[] rawData){
		this.rawData = rawData;
	}
	
    /**
     * YYYY+MM+DD+HH+MM+SS
     * @return
     * @throws Exception
     */
    public String getResetTime() throws Exception  {
    	
    	String lastResetTime ="";
		for(int i=0; i < 10 ; i++){
			byte[] data1 = DataFormat.select( rawData,i*(LEN_DEMAND_RESETTIME), LEN_DEMAND_RESETTIME);
			String cResetTime = getDateTime(data1);
			if(i ==0 ) lastResetTime = cResetTime;
			
			if(DateCompare(cResetTime, lastResetTime) > 0 ){
				lastResetTime = cResetTime;
			}
		}
		return lastResetTime;
    }
    
    public String getDateTime(byte[] date1) throws Exception  {
    	
        int year = DataFormat.hex2dec(DataFormat.LSB2MSB(DataFormat.select(date1,OFS_YEAR,LEN_YEAR)));
        int month = DataFormat.hex2dec(DataFormat.select(date1,OFS_MONTH,1));
        int date = DataFormat.hex2dec(DataFormat.select(date1,OFS_DAY,1));
        int hour = DataFormat.hex2dec(DataFormat.select(date1,OFS_HOUR,1));
        int min = DataFormat.hex2dec(DataFormat.select(date1,OFS_MIN,1));
        int sec = DataFormat.hex2dec(DataFormat.select(date1,OFS_SEC,1));

        StringBuffer ret = new StringBuffer();
        
        ret.append(Util.frontAppendNStr('0',Integer.toString(year),4));
        ret.append(Util.frontAppendNStr('0',Integer.toString(month),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(date),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(hour),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(min),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(sec),2));
        return ret.toString();
    }
    
    /**
     * 두 String형 날짜 크기 비교
     * @param str1
     * @param str2
     * @return
     */
    public int DateCompare(String sDate1, String sDate2){
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

		Calendar calendar1 = null;
	    Calendar calendar2 = null;
	    
		try {
            Date dDate1 = sdf.parse(sDate1);
            calendar1 = Calendar.getInstance();
            calendar1.setTime(dDate1);
                
            Date dDate2 = sdf.parse(sDate2);
            calendar2 = Calendar.getInstance();
            calendar2.setTime(dDate2);
            
        }catch(Exception e){
            log.warn(" Date Compare Parse Exception");
        }
        
        int diffInDays = calendar1.compareTo(calendar2);
        return diffInDays;
	}
}


