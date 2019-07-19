package com.aimir.fep.meter.parser.amuKepco_2_5_0Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * KEPCO v2.5.0 Meter Status 
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 4. 9. 오후 4:00:14$
 */
public class MeterStatus {
	
    private Log log = LogFactory.getLog(MeterStatus.class);
	
	//ERROR_INFO
    public static final byte NO_ERROR		  				= (byte)0x00;
    public static final byte OPEN_PHASE_A_ERROR				= (byte)0x01;	// 결상 A
    public static final byte OPEN_PHASE_B_ERROR 			= (byte)0x02;	// 결상 B
    public static final byte OPEN_PHASE_C_ERROR				= (byte)0x04;	// 결상 C
    public static final byte RAM_ERROR  					= (byte)0x08;	// RAM ERROR
    public static final byte BATTERY_ERROR  				= (byte)0x10;	// Battery ERROR
    public static final byte LP_MEMORY_CHK_SUM  			= (byte)0x20;	// LP Memory ChK Sum
    public static final byte TOU_SUM_ERROR			  		= (byte)0x40;	// TOU 합계 오차
    
    private byte data;
    

    /**
	 * Constructor .<p>
	 * 
	 * @param data
	 */
	public MeterStatus(byte data) {
		this.data = data;
	}
	
	/**
     * NO Error
     */
    public boolean getNO_ERROR() {
    	int flag =(int)(data&NO_ERROR);
        if (flag ==0){
            return true;
        }
        return false;
    }
    
    /**
     * OPEN_PHASE_A
     */
    public boolean getOPEN_PHASE_A_ERROR() {
    	int flag =(int)(data&OPEN_PHASE_A_ERROR);
        if (flag !=0){
            return true;
        }
        return false;
    }
	
    /**
     * OPEN_PHASE_B
     */
    public boolean getOPEN_PHASE_B_ERROR() {
    	int flag =(int)(data&OPEN_PHASE_B_ERROR);
        if (flag !=0){
            return true;
        }
        return false;
    }

    /**
     * OPEN_PHASE_C
     */
    public boolean getOPEN_PHASE_C_ERROR() {
    	int flag =(int)(data&OPEN_PHASE_C_ERROR);
        if (flag !=0){
            return true;
        }
        return false;
    }

    /**
     * RAM_ERROR
     */
    public boolean getRAM_ERROR() {
    	int flag =(int)(data&RAM_ERROR);
        if (flag !=0){
            return true;
        }
        return false;
    }
    
    /**
     * BATTERY_ERROR
     */
    public boolean getBATTERY_ERROR() {
    	int flag =(int)(data&BATTERY_ERROR);
        if (flag !=0){
            return true;
        }
        return false;
    }
    
    /**
     * LP_MEMORY_CHK_SUM
     */
    public boolean getLP_MEMORY_CHK_SUM() {
    	int flag =(int)(data&LP_MEMORY_CHK_SUM);
        if (flag !=0){
            return true;
        }
        return false;
    }
    
    /**
     * TOU_SUM_ERROR
     */
    public boolean getTOU_SUM_ERROR() {
    	int flag =(int)(data&TOU_SUM_ERROR);
        if (flag !=0){
            return true;
        }
        return false;
    }
    
    /**
     * get Log
     * @return
     */
    public String getLog()
    {
        StringBuffer sb = new StringBuffer();
        try{  

            if(getNO_ERROR())
                sb.append("<dt>NO_ERROR</dt>");
            if(getOPEN_PHASE_A_ERROR())
                sb.append("<dt>OPEN_PHASE_A ERROR</dt>");
            if(getOPEN_PHASE_B_ERROR())
                sb.append("<dt>OPEN_PHASE_B ERROR</dt>");
            if(getOPEN_PHASE_C_ERROR())
                sb.append("<dt>OPEN_PHASE_C ERROR</dt>");
            if(getRAM_ERROR())
                sb.append("<dt>RAM_ERROR</dt>");
            if(getBATTERY_ERROR())
                sb.append("<dt>BATTERY_ERROR</dt>");
            if(getLP_MEMORY_CHK_SUM())
                sb.append("<dt>LP_MEMORY_CHK_SUM</dt>");
            if(getTOU_SUM_ERROR())
                sb.append("<dt>TOU_SUM_ERROR</dt>");
            
        }catch(Exception e){
            log.warn("MeterStatus TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }
}


