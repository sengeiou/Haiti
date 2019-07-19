package com.aimir.fep.meter.parser.amuKmpMc601Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;

/**
 * KMP MC601 Information Field
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 3. 18. 오전 10:27:21$
 */
public class KMPMC601_INFO implements java.io.Serializable{
	
	private static Log log = LogFactory.getLog(KMPMC601_INFO.class);
	
	private static final int OFS_SW_VER 						= 0;
	private static final int OFS_HW_VER 						= 1;
	private static final int OFS_METER_SERIAL					= 2;
	private static final int OFS_CURRENT_TIME					= 6;
	private static final int OFS_SYSTEM_STATUS					= 13;

	private static final int LEN_SW_VER 						= 1; 
	private static final int LEN_HW_VER 						= 1;
	private static final int LEN_METER_SERIAL					= 4;
	private static final int LEN_CURRENT_TIME					= 7;
	private static final int LEN_SYSTEM_STATUS					= 2;
	
	private byte[] rawData;
	
	public KMPMC601_INFO(byte[] rawData){
		this.rawData = rawData;
	}
	
	/**
     * get S/W version
     * @return
     */
    public String getSwVer(){
        
    	String ret = new String();
        try{
            ret = new String(DataFormat.select(rawData,OFS_SW_VER,LEN_SW_VER)).trim();

        }catch(Exception e){
            log.warn("invalid model->"+e.getMessage());
        }

        return ret;
    }
    
    /**
     * Hardware version
     * @return
     */
    public String getHwVer() {
        
        String ret = new String();
        try{
            ret = new String(DataFormat.select(rawData,OFS_HW_VER,LEN_HW_VER)).trim();

        }catch(Exception e){
            log.warn("invalid model->"+e.getMessage());
        }
        return ret;
    }
    
    /**
     * get Meter Serial
     * @return	
     */
    public String getMeterSerial(){
        
        String ret = new String();
        try{
            int serial =  DataUtil.getIntToBytes(DataFormat.select(rawData,OFS_METER_SERIAL,LEN_METER_SERIAL));              
            ret = Integer.toString(serial);
           
        }catch(Exception e){
            log.warn("invalid model->"+e.getMessage());
        }
        return ret;
    }
    
    /**
	 * get Current Time Class
	 * @return	curTime
	 */
	public CurrentTimeData getMeterCurrentTime() throws Exception {
		
		CurrentTimeData ret = null;
		try {
			ret = new CurrentTimeData(DataFormat.select(rawData,OFS_CURRENT_TIME,LEN_CURRENT_TIME));
		} catch (Exception e) {
			log.warn("[KMP MC601 ] get Current Time Data Failed " + e.getMessage());
		}
		return ret;
	}
    
	/**
	 * get Meter Status
	 * 
	 * @return MeterStatus
	 * @throws Exception
	 */
	public SystemStatus getSystemStatus() throws Exception {
        return new SystemStatus(
            DataFormat.select(rawData,OFS_SYSTEM_STATUS, LEN_SYSTEM_STATUS));
	}
	
	public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("KMP MC601 Meter INFO DATA[");        
            sb.append("(SW_VERSION=").append(getSwVer()).append("),");
            sb.append("(HW_VERSION=").append(getHwVer()).append("),");
            sb.append("(METER_SERIAL=").append(getMeterSerial()).append("),");
            sb.append("(CURRENT TIME =").append(getMeterCurrentTime().getCurrnetTime()).append("),");
            sb.append("(SYSTEM_STATUS Log =").append(getSystemStatus().getLog()).append("),");
            sb.append("]\n");
        }catch(Exception e){
            log.warn("KMP MC601 INFO TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }
}


