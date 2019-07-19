package com.aimir.fep.meter.parser.amuKepco_2_5_0Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;

/**
 * KEPCO v2.5.0 Information Field
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 4. 8. 오후 5:06:50$
 */
public class KEPCO_2_5_0_INFO {

	private static Log log = LogFactory.getLog(KEPCO_2_5_0_INFO.class);
	
	private static final int OFS_SW_VER 						= 0;  	// S/W Version
	private static final int OFS_HW_VER 						= 1;  	// H/W Version	
	//private static final int OFS_METER_SERIAL					= 2;	
	/** 2010.5.31
	 * protocol(1)+제조사(1)+제조년도(1)+일련번호(3)
	 * 20byte Serial 중에서 일련번호 3byte만 추출  
	 */	
	private static final int OFS_METER_SERIAL					= 5;	// Meter Serial
	private static final int OFS_METER_TYPE						= 22;	// 전력량계 종류
	private static final int OFS_METER_PRODUCTION_COMPANY		= 23;	// 전력량계 제작사
	private static final int OFS_METER_PRODUCTION_YEAR			= 24;	// 전력량계 제작년도
	private static final int OFS_METER_NUMBER					= 25;	// 전력량계 번호 (LSB-MSB)
	private static final int OFS_METER_STATUS					= 28;	// 전력량계 상태정보
	private static final int OFS_METER_REG_K					= 29;	// 전력량계 계기정수(LSB-MSB)
	private static final int OFS_METER_CT_PT					= 31;	// 전력량계 변성기 배수(LSB-MSB)
	private static final int OFS_METER_DATA_FORMAT				= 35;	// 전력량계 Data format
	private static final int OFS_METER_CURRENT_TIME				= 40;	// 전력량계 현재 시간
	private static final int OFS_METER_DEMAND_RESET_TIME		= 47;	// 전력량계 Demand Reset Time 1
	private static final int OFS_METER_CNT_MANU_RECOVERY		= 127;	// 전력량계 수동복귀 횟수(LSB-MSB)
	private static final int OFS_METER_BILLING_DAY 				= 129;	// 전력량계 정기검침일
	private static final int OFS_METER_DEMAND_INTERVAL			= 130;	// 전력량계 Demand Interval

	private static final int LEN_SW_VER 						= 1; 	// S/W Version
	private static final int LEN_HW_VER 						= 1; 	// H/W Version
	private static final int LEN_METER_SERIAL					= 3;	// Meter Serial
	private static final int LEN_METER_NUMBER					= 3;	// 전력량계 번호 (LSB-MSB)
	private static final int LEN_METER_REG_K					= 2;	// 전력량계 계기정수(LSB-MSB)
	private static final int LEN_METER_CT_PT					= 4; 	// 전력량계 변성기 배수(LSB-MSB)
	private static final int LEN_METER_DATA_FORMAT				= 5; 	// 전력량계 Data format
	private static final int LEN_METER_CURRENT_TIME				= 7; 	// 전력량계 현재 시간
	private static final int LEN_METER_DEMAND_RESET_TIME		= 8; 	// 전력량계 Demand Reset Time 1
	private static final int LEN_METER_CNT_MANU_RECOVERY		= 2; 	// 전력량계 수동복귀 횟수(LSB-MSB)
	
	private byte[] 			rawData								= null;
	private double 			ke 									= 1;
	
	/**
	 * Constructor .<p>
	 * @param rawData
	 */
	public KEPCO_2_5_0_INFO(byte[] rawData) {
		this.rawData = rawData;
	}
	
	/**
	 * Constructor .<p>
	 * @param rawData
	 * @param ke
	 */
	public KEPCO_2_5_0_INFO(byte[] rawData ,double ke) {
		this.rawData = rawData;
		this.ke	= ke;
	}	
	
	/**
     * get raw Data 
     * @return rawData 
     */
    public byte[] getRawData(){
        return rawData;
    }
    
    /**
     * set raw Data
     * @param rawData
     */
    public void setrawData(byte[] rawData){
    	this.rawData = rawData;
    }
	
    /**
	 * get KE 계기정수
	 * @return
	 */
	public Double getKe(){
		return this.ke;
	}
	
	/**
	 * set KE 
	 * @param ke
	 */
	public void setKe(double ke){
		this.ke =ke;
	}
	
	/**
     * software version
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
            int serial =  DataUtil.getIntToBytes(DataFormat.LSB2MSB(DataFormat.select(rawData,OFS_METER_SERIAL,LEN_METER_SERIAL)));             
            ret = Integer.toString(serial);
           
        }catch(Exception e){
            log.warn("invalid model->"+e.getMessage());
        }
        return ret;
    }
    
    /**
     * get Meter Type
     * @return
     */
    public int getMeterType() throws Exception {
        int ret = DataFormat.hex2unsigned8(rawData[OFS_METER_TYPE]);
        return ret;
    }
    
    /**
     * get Meter Production Company
     * @return
     */
    public int getMeterProductionCompany()throws Exception {
    	 int ret = DataFormat.hex2unsigned8(rawData[OFS_METER_PRODUCTION_COMPANY]);
         return ret;
    }
    
    /**
     * get Meter Production Year
     * @return
     */
    public int getMeterProductionYear()throws Exception {
    	int ret = DataFormat.hex2unsigned8(rawData[OFS_METER_PRODUCTION_YEAR]);
    	return ret;
        
   }
    
    /**
     * get Meter Number
     * @return
     */
    public int getMeterNumber(){
    	
    	int ret =0;
    	try{
            ret = DataFormat.hex2dec(DataFormat.LSB2MSB(
            				DataFormat.select(rawData,OFS_METER_NUMBER,LEN_METER_NUMBER)));
        }catch(Exception e){
            log.warn("invalid model->"+e.getMessage());
        }
        return ret;
   }
    
    /**
     * get Meter Status 
     * @return
     * @throws Exception
     */
    public MeterStatus getMeterStatus() throws Exception {
        return new MeterStatus(rawData[OFS_METER_STATUS]);
    	
    }
    
    /**
     * get Meter RegK 계기정수
     * @return
     */
    public int getMeterRegK(){
    	
    	int ret =0;
        try {
			ret=  DataFormat.hex2dec(DataFormat.LSB2MSB(DataFormat.select(
			            rawData,OFS_METER_REG_K,LEN_METER_REG_K)));
		} catch (Exception e) {
			log.warn("invalid model->"+e.getMessage());
		}
		
		return ret;
    }
    
    /**
     * get Meter CT PT
     * @return
     */
    public int getMeterCtPt() {
    	
    	int ret =0;
        try {
			ret = DataFormat.hex2dec(DataFormat.LSB2MSB(
					DataFormat.select( rawData,OFS_METER_CT_PT,LEN_METER_CT_PT)));
		} catch (Exception e) {
			log.warn("invalid model->"+e.getMessage());
		}
		return ret;
    }
    
    /**
     * get Meter Data Format 
     * @return
     * @throws Exception
     */
    public DataFormatForMetering getMeterDataFormat() throws Exception {
        return new DataFormatForMetering(
            DataFormat.select(
                rawData,OFS_METER_DATA_FORMAT,LEN_METER_DATA_FORMAT));
    }
    
    /**
     * get Meter Current Time
     * @return
     */
    public CurrentTime getMeterCurrentTime() throws Exception {
        
    	return new CurrentTime(DataFormat.select(
                rawData,OFS_METER_CURRENT_TIME,LEN_METER_CURRENT_TIME));

    }
    
    /**
     * get Meter Demand Reset Time 
     * @return
     * @throws Exception
     */
    public String getMeterDemandResetTime() throws Exception {
    	
    	String datetime = "";
    	DemandResetTime resetTime = new DemandResetTime(DataFormat.select(
                		rawData, OFS_METER_DEMAND_RESET_TIME, LEN_METER_DEMAND_RESET_TIME*10));
    	
    	datetime = resetTime.getResetTime();
    	return datetime;
    }
    
   
    /**
	 * get Count Of Manual Recovery 수동복귀 횟수 
	 * @return
	 * @throws Exception
	 */
	public int getCountOfManualRecovery() throws Exception {
        return DataFormat.hex2dec(DataFormat.LSB2MSB(DataFormat.select(
        		rawData,OFS_METER_CNT_MANU_RECOVERY,LEN_METER_CNT_MANU_RECOVERY)));
    }
  
	/**
	 * get Meter Billing Day 
	 * @return
	 * @throws Exception
	 */
	public int getMeterBillingDay() throws Exception {
		int ret = DataFormat.hex2unsigned8(rawData[OFS_METER_BILLING_DAY]);
        return ret;
	}
	
	/**
	 * get Meter Demand Interval
	 * @return
	 * @throws Exception
	 */
	public int getMeterDemandInterval() throws Exception {
		int ret = DataFormat.hex2unsigned8(rawData[OFS_METER_DEMAND_INTERVAL]);
        return ret;
	}
	
	
	public String toString()
    {
    
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("KEPCO_2_5_0 INFO DATA[");        
            sb.append("(SW_VER=").append(getSwVer()).append("),");
            sb.append("(HW_VER=").append(getHwVer()).append("),");
            sb.append("(Meter Type)").append(getMeterType()).append("),");
            sb.append("(Meter Production Company())").append(getMeterProductionCompany()).append("),");
            sb.append("(Meter Production Year)").append(getMeterProductionYear()).append("),");
            sb.append("(Meter Number)").append(getMeterNumber()).append("),");
            sb.append("(Meter Status)").append(getMeterStatus().getLog()).append("),");
            sb.append("(Meter RegK)").append(getMeterRegK()).append("),");
            sb.append("(Meter CT PT)").append(getMeterCtPt()).append("),");
            sb.append("(Meter DataFormat)").append(getMeterDataFormat().toString()).append("),");
            sb.append("(Meter CurrentTime)").append(getMeterCurrentTime().getCurrnetTime()).append("),");
            sb.append("(Meter Demand Reset Time10)").append(getMeterDemandResetTime()).append("),");
            sb.append("(Meter Count Of Manual Recovery)").append(getCountOfManualRecovery()).append("),");
            sb.append("(Meter Billing Day)").append(getMeterBillingDay()).append("),");
            sb.append("(Meter Meter Demand Interval)").append(getMeterDemandInterval()).append("),");
            sb.append("]\n");
        }catch(Exception e){
            log.warn("KEPCO_2_5_0 toString  Error =>"+e.getMessage());
        }

        return sb.toString();
    }
}


