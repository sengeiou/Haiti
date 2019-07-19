package com.aimir.fep.meter.parser.amuPulseTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.parser.ModemLPData;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Util;

/**
 * PULSE LP DATA 
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 3. 8. 오전 10:13:47$
 */
public class PULSE_LP {

	private static Log log 	= LogFactory.getLog(PULSE_LP.class);
	
	private static final int  	LEN_LP_PERIOD			= 2;
	private static final int  	LEN_LP_COUNT			= 1;
	private static final int  	LEN_LP_STATUS			= 1;
	private static final int  	LEN_LP_RECORD_TIME		= 6;
	private static final int  	LEN_LP_BASE_PULSE		= 4;
	private static final int  	LEN_LP					= 2;
	
	private int 				period 					= 0;
	private int 				lpDataCnt 				= 0;
	private ModemLPData[] 		lpData 					= null;
	
	/* 추후 LP STATUS가 필요한 경우를 위해  추가  */
	private int 				lpStatus				= 0;
	
	/**
	 * Constructor
	 * 
	 * @param rawData
	 */
	public PULSE_LP(byte[] rawData) throws Exception{
		try{
			parse(rawData);
		}catch(Exception e){
			throw e;
		}
	}
	
	/**
	 * get LP STATUS
	 * @return lpStatus
	 */
	public int getLpStatus() {
		return lpStatus;
	}
	
	/**
	 * get LP STATUS Description
	 * @return lpStatusDesc
	 */
	public String getLpStatusDesc(){
		
		String lpStatusDesc="";
		
		switch(lpStatus){
			case 0: lpStatusDesc= "Normal" ; 			break;
			case 1: lpStatusDesc= "Initial LP Block" ; 	break;
			case 2: lpStatusDesc= "SRAM Crack" ; 		break;
			case 3: lpStatusDesc= "TIME Sync +" ; 		break;
			case 4: lpStatusDesc= "TIME Sync -" ; 		break;
			case 5: lpStatusDesc= "Current Pulse" ; 	break;
			default: log.warn("No Match LP STATUS Description");
		}
		
		return lpStatusDesc;
	}
	/**
	 * set LP STATUS
	 * @param lpStatus
	 */
	public void setLpStatus(int lpStatus) {
		this.lpStatus = lpStatus;
	}
	
	/**
	 * get LP DATA Count
	 * @return lpDataCnt
	 */
	public int getLpDataCnt() {
		return lpDataCnt;
	}

	/**
	 * set LP DATA Count
	 * @param lpDataCnt
	 */
	public void setLpDataCnt(int lpDataCnt) {
		this.lpDataCnt = lpDataCnt;
	}

	/**
	 * get LP Period
	 * @return period
	 */
	public int getPeriod() {
		return period;
	}

	/**
	 * set LP Period
	 * @param period
	 */
	public void setPeriod(int period) {
		this.period = period;
	}

	/**
	 * get LP DATA
	 * @return lpData
	 */
	public ModemLPData[] getLpData() {
		return lpData;
	}

	/**
	 * set LP DATA
	 * @param lpData
	 */
	public void setLpData(ModemLPData[] lpData) {
		this.lpData = lpData;
	}
	
	/**
	 * PULSE LP DATA Field Parse
	 * 
	 * @param data
	 * @throws Exception
	 */
	public void parse(byte[] data) throws Exception{
    	
		byte[] LPPERIOD 		= new byte[LEN_LP_PERIOD];
	    byte[] LPDATACNT 		= new byte[LEN_LP_COUNT];
	    
	    byte[] LPSTATUS			= new byte[LEN_LP_STATUS];
	    byte[] LPRECORDTIME		= new byte[LEN_LP_RECORD_TIME];
	    byte[] BASEPULSE 		= new byte[LEN_LP_BASE_PULSE];
	    byte[] LP 				= new byte[LEN_LP];
	    
    	try{
    		int pos = 0;
    		
    		System.arraycopy(data, pos, LPPERIOD, 0, LPPERIOD.length);
            pos += LPPERIOD.length;
            period = DataFormat.hex2dec(LPPERIOD);
            log.debug("LPPERIOD[" + period + "]");
    		
            System.arraycopy(data, pos, LPDATACNT, 0, LPDATACNT.length);
            pos += LPDATACNT.length;
            lpDataCnt = DataFormat.hex2dec(LPDATACNT);
            DataUtil.convertEndian(LPDATACNT);
            log.debug("LPDATACNT[" + lpDataCnt + "]");
            
            int lpLength = LPSTATUS.length 			// LP Status
            				+ LPRECORDTIME.length 	// Record Time   
            				+ BASEPULSE.length		// Base Pulse 
            				+ (LP.length * 24);		// LP Data
            
            byte[] bx = new byte[lpDataCnt * lpLength];
            System.arraycopy(data, pos, bx, 0, bx.length);
            
            lpData 	= new ModemLPData[lpDataCnt];
            pos 	= 0;
            int idx = 0;
            
            for (int i = 0; i < lpDataCnt; i++) {
            	
            	// LP Status
            	System.arraycopy(bx, pos, LPSTATUS, 0, LPSTATUS.length);
            	this.lpStatus		= DataUtil.getIntToBytes(LPSTATUS);
                pos += LPSTATUS.length;
                
                // LP Record
                System.arraycopy(bx, pos, LPRECORDTIME, 0, LPRECORDTIME.length);
                String lpRecordTime	= Util.byteToString(LPRECORDTIME);
                pos += LPRECORDTIME.length;
                
                // Base Pulse
                System.arraycopy(bx, pos, BASEPULSE, 0, BASEPULSE.length);
                Long basePulse		= DataFormat.hex2long(BASEPULSE);
                pos += BASEPULSE.length;
                
                /* lpStatus 가  0 일때만 저장 */
                // "0".equals(lpStatus)
                if( new byte[]{0x00} == LPSTATUS){ 
                    log.debug(" LP DATA INDEX : " + idx );
                    
                    lpData[idx] = new ModemLPData();
                    // 초기 Record Time 에 주기(period * i)만큼 분(min)을 더한다 .
                    lpData[idx].setLpDate(lpRecordTime.substring(0,12));
                    lpData[idx].setBasePulse(new double[]{basePulse});
                    lpData[idx].setLp(new double[1][24]);
                    
                    // ModemLPData의 lp 배열구조가 변경됨. 주의
                    for (int j = 0; j < lpData[idx].getLp()[0].length; j++) {
                        System.arraycopy(bx, pos, LP, 0, LP.length);
                        pos += LP.length;
                        lpData[idx].getLp()[0][j] = DataUtil.getIntTo2Byte(LP);
                    }
                    // index increase
                    idx++;
                }
               
            }
            
    	}catch(Exception e){
    		log.error("Pulse LP DATA Field Parse Failed ", e);
    	}
    }
    
    
    
}