package com.aimir.fep.meter.parser.amuPulseTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;

/**
 * Pulse Information Field
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 3. 8. 오전 10:13:28$
 */
public class PULSE_INFO{
	
	private static Log log = LogFactory.getLog(PULSE_INFO.class);
	
	public static final int OFS_SW_VER 			= 0;
	public static final int OFS_HW_VER 			= 1;
	public static final int OFS_METER_NAME 		= 2;
	/** 2010.5.31 
	 * protocol(1)+제조사(1)+제조년도(1)+일련번호(3)
	 * 20byte Serial 중에서 일련번호 3byte만 추출  
	 */	
	public static final int OFS_METER_SERIAL 	= 26;
	public static final int OFS_CURRENT_TIME 	= 43;
	public static final int OFS_CURRENT_PULSE 	= 54;
		
	public static final int LEN_SW_VER 			= 1;
	public static final int LEN_HW_VER			= 1;
	public static final int LEN_METER_NAME 		= 20;
	public static final int LEN_METER_SERIAL	= 3;
	public static final int LEN_CURRENT_TIME 	= 11;
	public static final int LEN_CURRENT_PULSE 	= 4;
	
	// infomation Field Data
	private byte[] rawData 	= null;
	private double cipher 	= 1.0;
	/**
	 * Constructor
	 * @param rawData
	 */
	public PULSE_INFO(byte[] rawData){
		this.rawData = rawData;
	}
    
	/**
     * get S/W version
     * @return
     */
    public String getSW_VER(){
        
        String ret = new String();
        try{
            ret = new String(DataFormat.select(rawData,OFS_SW_VER,LEN_SW_VER)).trim();

        }catch(Exception e){
            log.warn("invalid model->"+e.getMessage());
        }
        return ret;
    }
    
    /**
     * get H/W version
     * @return
     */
    public String getHW_VER(){
        
        String ret = new String();
        try{
            ret = new String(DataFormat.select(rawData,OFS_HW_VER,LEN_HW_VER)).trim();

        }catch(Exception e){
            log.warn("invalid model->"+e.getMessage());
        }
        return ret;
    }
    /**
     * get SoftWare Version
     * @return 
     */
    public double getSw_Version(){
        
    	double ret = 0.0;
        try{
            String sVer = new String(DataFormat.select(rawData,OFS_SW_VER,LEN_SW_VER)).trim();
            ret = Double.parseDouble(sVer) * cipher;
        }catch(Exception e){
            log.warn("get SW_Version Failed --> "+e.getMessage());
        }

        return ret;
    }
    
    /**
     * get HardWare Version
     * @return 
     */
    public double getHw_Version(){
        
    	double ret = 0.0;
        try{
        	String sVer = new String(DataFormat.select(rawData,OFS_HW_VER,LEN_HW_VER)).trim();
        	ret = Double.parseDouble(sVer) * cipher;
        }catch(Exception e){
            log.warn("get HW_Version Failed --> "+e.getMessage());
        }

        return ret;
    }
    
    /**
     * get Meter Name
     * 
     * @return
     * @throws Exception
     */
    public String getMeter_Name() throws Exception{
        
        String ret = new String();
        try{
            ret = new String(DataFormat.select(rawData,OFS_METER_NAME,LEN_METER_NAME)).trim();

        }catch(Exception e){
            log.warn("get Meter Name Failed --> "+e.getMessage());
        }

        return ret;
    }
    
    /**
     * get Meter Serial
     * 
     * @return
     * @throws Exception
     */
    public String getMeterSerial() throws Exception {
    	
    	String ret = new String();
        try{
        	int serial =  DataUtil.getIntToBytes(DataFormat.select(rawData,OFS_METER_SERIAL,LEN_METER_SERIAL));             
        	ret = Integer.toString(serial);
        	
        }catch(Exception e){
            log.warn("get Meter Name Failed --> "+e.getMessage());
        }

        return ret;
	}
      
    /**
     * get Current Time Field Class
     * 
     * @return
     * @throws Exception
     */
    public CurrentTimeData getCurrentTime() throws Exception{
    	return new CurrentTimeData(DataFormat.select(rawData,OFS_CURRENT_TIME,LEN_CURRENT_TIME));
    }
    
    /**
     * get Current Pulse
     * 
     * @return
     * @throws Exception
     */
    public double getCurrent_Pulse() throws Exception{
        
    	double ret = 0;
        try{
            ret = DataFormat.ba2double(rawData,OFS_CURRENT_PULSE,LEN_CURRENT_PULSE);

        }catch(Exception e){
            log.warn("get Current Pulse Failed --> "+e.getMessage());
        }
        return ret;
    }
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("AMU Pulse Information Field [");        
            sb.append("(SW_VER =").append(getSw_Version()).append("),");
            sb.append("(HW_VER =").append(getHw_Version()).append("),");
            sb.append("(Meter Name =").append(getMeter_Name()).append("),");
            sb.append("(Meter Serial=").append(getMeterSerial()).append("),");
            sb.append("(Current Time=").append(getCurrentTime().getTimeStamp()).append("),");
            sb.append("(Current Pulse=").append(getCurrent_Pulse()).append(')');
            sb.append("]\n");
        }catch(Exception e){
            log.warn("AMU Pulse Information ToString Failed =>"+e.getMessage());
        }

        return sb.toString();
    }
}