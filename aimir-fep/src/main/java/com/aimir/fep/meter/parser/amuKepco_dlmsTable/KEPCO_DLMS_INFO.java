package com.aimir.fep.meter.parser.amuKepco_dlmsTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;

/**
 * KEPCO DLMS Information Field
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 4. 20. 오전 10:37:40$
 */
public class KEPCO_DLMS_INFO {

	private static Log log = LogFactory.getLog(KEPCO_DLMS_INFO.class);
	
	private static final int OFS_SW_VER 						= 0;  	// S/W Version
	private static final int OFS_HW_VER 						= 1;  	// H/W Version
	//private static final int OFS_METER_SERIAL					= 2;	
	/** 2010.5.31
	 * protocol(1)+제조사(1)+제조년도(1)+일련번호(3)
	 * 20byte Serial 중에서 일련번호 3byte만 추출  
	 */	
	private static final int OFS_METER_SERIAL					= 5;	// Meter Serial
	private static final int OFS_METER_PRODUCTION_COMPANY		= 22;	// 전력량계 제작사 (ASCII 데이터)
	private static final int OFS_METER_TYPE						= 23;	// 전력량계 종류  HEX
	private static final int OFS_METER_ENERGY					= 24;	// 전력량 BIN 데이터
	private static final int OFS_METER_NUMBER					= 25;	// 전력량계 번호 
	private static final int OFS_METER_STATUS_ERROR				= 32;	// 전력량계 상태정보
	private static final int OFS_METER_STATUS_CAUTION			= 33;	// 전력량계 상태정보
	private static final int OFS_METER_REG_K					= 34;	// 전력량계 계기정수 
	private static final int OFS_METER_REG_K_VOLTAGE			= 36;	// 전력량계 계기정수 Voltage Ratio
	private static final int OFS_METER_REG_K_CURRENT			= 38;	// 전력량계 계기정수 Current Ratio
	private static final int OFS_METER_BILLING_DAY 				= 40;	// 전력량계 정기검침일 (Day)
	private static final int OFS_METER_LP_INTERVAL				= 41;	// 전력량계 LP Interval
	private static final int OFS_METER_LAST_LP_TIME				= 42;	// 전력량계 마지막 LP 날짜&시간 
	private static final int OFS_METER_DEMAND_RESET_TIME		= 48;	// 전력량계 Demand Reset 날짜&시간(C+YY+M+D+H+S+W)
	private static final int OFS_METER_CURRENT_TIME				= 57;	// 전력량계 현재날짜&시간(YY+M+D+H+M+S+W)
	private static final int OFS_METER_LP_ENABLE				= 65;	// 0: Disable , 1: Enable
	private static final int OFS_LCD_POWER_DECIMAL_POINT		= 66;	// LCD 전력 표시 소수점 자리수
	private static final int OFS_LCD_ENERGY_DECIMAL_POINT		= 67;	// LCD 전력량 표시 소수점 자리수
	private static final int OFS_LAST_MONTH_DATA_CHECK			= 68;	// 전월 데이터 유무
	private static final int OFS_POWER_OUTAGE_RECODE_COUNT		= 69;	// 정전 기록 횟수
	
	private static final int LEN_SW_VER 						= 1; 
	private static final int LEN_HW_VER 						= 1; 
	//private static final int LEN_METER_SERIAL					= 20;
	private static final int LEN_METER_SERIAL					= 3;	// Meter Serial
	private static final int LEN_METER_NUMBER					= 7;
	private static final int LEN_METER_REG_K					= 2;
	private static final int LEN_METER_REG_K_VOLTAGE			= 2;
	private static final int LEN_METER_REG_K_CURRENT			= 2;
	private static final int LEN_METER_LAST_LP_TIME				= 6; 
	private static final int LEN_METER_DEMAND_RESET_TIME		= 9;
	private static final int LEN_METER_CURRENT_TIME				= 8;

	private byte[] 			rawData								= null;
	private double 			ke 									= 1;
	
	/**
	 * Constructor .<p>
	 * @param rawData
	 */
	public KEPCO_DLMS_INFO(byte[] rawData) {
		this.rawData = rawData;
	}
	
	/**
	 * Constructor .<p>
	 * @param rawData
	 * @param ke
	 */
	public KEPCO_DLMS_INFO(byte[] rawData ,double ke) {
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
     * get Meter Production Company (ASCII)
     * @return
     */
    public int getMeterProductionCompany()throws Exception {
    	 int ret = DataUtil.getIntToByte(rawData[OFS_METER_PRODUCTION_COMPANY]);
         return ret;
    }
    
    /**
     * get Meter Type
     * @return
     */
    public int getMeterType()throws Exception {
        int ret = DataFormat.hex2unsigned8(rawData[OFS_METER_TYPE]);
        return ret;
    }
	
    /**
     * get Meter Energy
     * 전력량
     * @return
     */
    public int getMeterEnergy()throws Exception {
        int ret = DataFormat.hex2unsigned8(rawData[OFS_METER_ENERGY]);
        return ret;
    }
    
    /**
     * get Meter Number
     * 전력량계 번호
     * @return
     */
    public int getMeterNumber(){
    	
    	int ret =0;
    	try{
            ret = DataUtil.getIntToBytes(DataFormat.select(rawData,OFS_METER_NUMBER,LEN_METER_NUMBER));
        }catch(Exception e){
            log.warn("invalid model->"+e.getMessage());
        }
        return ret;
    }
    
    /**
     * get Meter Status Error
     * @return
     * @throws Exception
     */
    public MeterStatusError getMeterStatusError() throws Exception{
	   return new MeterStatusError(rawData[OFS_METER_STATUS_ERROR]);
    }
   
    /**
     * get Meter Status Caution
     * @return
     * @throws Exception
     */
    public MeterStatusCaution getMeterStatusCaution() throws Exception{
	   return new MeterStatusCaution(rawData[OFS_METER_STATUS_CAUTION]);
    }
    
    /**
     * get Meter RegK 계기정수
     * @return
     */
    public int getMeterRegK(){
    	
    	int ret =0;
        try {
			return DataFormat.hex2dec(DataFormat.select(rawData,OFS_METER_REG_K,LEN_METER_REG_K));
		} catch (Exception e) {
			log.warn("invalid model->"+e.getMessage());
		}
		
		return ret;
    }
    
    /**
     * get Meter RegK Voltage Ratio
     * @return
     */
    public int getMeterRegKVoltageRatio(){
    	
    	int ret =0;
        try {
			return DataFormat.hex2dec(DataFormat.select(rawData,OFS_METER_REG_K_VOLTAGE,LEN_METER_REG_K_VOLTAGE));
		} catch (Exception e) {
			log.warn("invalid model->"+e.getMessage());
		}
		return ret;
    }
    
    /**
     * get Meter RegK Current Ratio 
     * @return
     */
    public int getMeterRegKCurrentRatio(){
    	
		int ret =0;
		try {
			return DataFormat.hex2dec(DataFormat.select(rawData,OFS_METER_REG_K_CURRENT,LEN_METER_REG_K_CURRENT));
		} catch (Exception e) {
			log.warn("invalid model->"+e.getMessage());
		}
		return ret;
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
	 * get Meter LP Interval (minute)
	 * @return
	 * @throws Exception
	 */
	public int getMeterLPInterval() throws Exception {
		int ret = DataFormat.hex2unsigned8(rawData[OFS_METER_LP_INTERVAL]);
        return ret;
	}
	
	/**
	 * get Meter Last LP Time
	 * @return
	 * @throws Exception
	 */
	public MeterLastLPDateTime getMeterLastLPTime() throws Exception {
		 return new MeterLastLPDateTime(
		            DataFormat.select(
		                rawData,OFS_METER_LAST_LP_TIME,LEN_METER_LAST_LP_TIME));
	}
	
	/**
	 * get Meter Demand Reset Time
	 * 
	 * @return
	 * @throws Exception
	 */
	public DemandResetTime getMeterDemandResetTime()throws Exception {
		 return new DemandResetTime(
		            DataFormat.select(
		                rawData,OFS_METER_DEMAND_RESET_TIME,LEN_METER_DEMAND_RESET_TIME));
	}
	
	/**
	 * get Meter Current Time
	 * @return
	 * @throws Exception
	 */
	public CurrentTime getMeterCurrentTime()throws Exception {
		return new CurrentTime(
				DataFormat.select(rawData, OFS_METER_CURRENT_TIME, LEN_METER_CURRENT_TIME));
	}
	
	/**
	 * get Meter LP Enable
	 * @return
	 * @throws Exception
	 */
	public int getMeterLPEnable() throws Exception {
		int ret = DataFormat.hex2unsigned8(rawData[OFS_METER_LP_ENABLE]);
        return ret;
	}
	
	/**
	 * get LCD Power Decimal Point
	 * LCD 전력 표시 소수점 자리수
	 * @return
	 * @throws Exception
	 */
	public int getLCDPowerDecimalPoint() throws Exception {
		int ret = DataFormat.hex2unsigned8(rawData[OFS_LCD_POWER_DECIMAL_POINT]);
        return ret;
	}
	
	/**
	 * get LCD Energy Decimal Point
	 * LCD 전력량 표시 소수점 자리수
	 * @return
	 * @throws Exception
	 */
	public int getLCDEnergyDecimalPoint() throws Exception {
		int ret = DataFormat.hex2unsigned8(rawData[OFS_LCD_ENERGY_DECIMAL_POINT]);
        return ret;
	}
	
	/**
	 * get Last Month Data Check
	 * 전월 데이터 유무
	 * @return
	 * @throws Exception
	 */
	public int getLastMonthDataCheck() throws Exception {
		int ret = DataFormat.hex2unsigned8(rawData[OFS_LAST_MONTH_DATA_CHECK]);
        return ret;
	}
	
	
	/**
	 * get Power Outage Recode Count
	 * 정전 기록 횟수
	 * @return
	 * @throws Exception
	 */
	public int getPowerOutageRecodeCount() throws Exception {
		int ret = DataFormat.hex2unsigned8(rawData[OFS_POWER_OUTAGE_RECODE_COUNT]);
        return ret;
	}
	
	/**
	 * get to String
	 */
	public String toString()
    {
    
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("KEPCO DLMS INFO DATA[");        
            sb.append("(SW_VER=").append(getSwVer()).append("),");
            sb.append("(HW_VER=").append(getHwVer()).append("),");
            sb.append("(Meter Serial=").append(getMeterSerial()).append("),"); 
            sb.append("(Meter Production Company=").append(getMeterProductionCompany()).append("),"); 
            sb.append("(Meter Type=").append(getMeterType()).append("),"); 
            sb.append("(Meter Energy=").append(getMeterEnergy()).append("),"); 
            sb.append("(Meter Number=").append(getMeterNumber()).append("),"); 
            sb.append("(Meter Status=").append(getMeterStatusError().getLog()).append(getMeterStatusCaution().getLog()).append("),");
            sb.append("(Meter RegK=").append(getMeterRegK()).append("),"); 
            sb.append("(Meter RegK Voltage Ratio=").append(getMeterRegKVoltageRatio()).append("),"); 
            sb.append("(Meter RegK Current Ratio=").append(getMeterRegKCurrentRatio()).append("),"); 
            sb.append("(Meter Billing Day=").append(getMeterBillingDay()).append("),"); 
            sb.append("(Meter LP Interval=").append(getMeterLPInterval()).append("),"); 
            sb.append("(Meter Last LP Time=").append(getMeterLastLPTime().getLpDateTime()).append("),"); 
            sb.append("(Meter Demand Reset Time=").append(getMeterDemandResetTime().getDemandResetTime()).append("),"); 
            sb.append("(Meter LP Enable=").append(getMeterLPEnable()).append("),"); 
            sb.append("(LCD Power Decimal Point=").append(getLCDPowerDecimalPoint()).append("),"); 
            sb.append("(LCD Energy Decimal Point=").append(getLCDEnergyDecimalPoint()).append("),"); 
            sb.append("(LAST MONTH DATA CHECK =").append(getLCDEnergyDecimalPoint()).append("),");
            sb.append("(POWER OUTAGE RECODE COUNT=").append(getLCDEnergyDecimalPoint()).append(')');
            sb.append("]\n");
        }catch(Exception e){
            log.warn("KEPCO DLMS toString  Error =>"+e.getMessage());
        }

        return sb.toString();
    }
}


