/** 
 * @(#)NURI_T002.java       1.0 07/01/29 *
 * 
 * Communication Statistics Data Class.
 * Copyright (c) 2007-2008 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.MeteringFail;
import com.aimir.fep.util.DataFormat;

/**
 * @author Park YeonKyoung goodjob@nuritelecom.com
 */
public class NURI_T002 implements java.io.Serializable {

	private static final long serialVersionUID = 714547722383987995L;
	byte[] data;
    private static Log log = LogFactory.getLog(NURI_T002.class);
    
    public static final int OFS_MODEM_ERROR = 0;
    public static final int LEN_MODEM_ERROR = 2;
    public static final int OFS_METER_ERROR = 2;
    public static final int LEN_METER_ERROR = 1;
	
    public NURI_T002() {}
    
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public NURI_T002(byte[] data) {
		this.data = data;
	}
    
    public int getMODEM_ERROR() throws Exception {
        return DataFormat.hex2signed16(data,OFS_MODEM_ERROR,LEN_MODEM_ERROR);
    }
    
    public static String getMODEM_ERROR_NAME(int code){

        switch(code){
            case    0: return "";//sensor status is normal
            case   -1: return "Wrong device";
            case   -2: return "Device is not working";
            case   -3: return "The device already while using";
            case   -4: return "Memory error";
            case   -5: return "Invalid handle";
            case   -6: return "Invalid buffer";
            case   -7: return "Invalid data length";
            case   -8: return "Invalid timeout value";
            case   -9: return "Already initialized";
            case  -10: return "The API did not initialize";
            case  -11: return "Invalid ID";
            case  -12: return "The device which is not register";
            case  -13: return "The service is not started";
            case  -14: return "When the service operates already, re-start";
            case  -15: return "Invalid interface";
            case  -16: return "Device error";
            case  -17: return "Binding resources excess";
            case  -18: return "The sensor already while using";
            case  -19: return "Invalid key value";
            case  -20: return "Response time out";
            case  -21: return "Invalid parameter";
            case  -22: return "Wrong command";
            case  -23: return "Unknown command";
            case  -24: return "Not connected";
            case  -25: return "The cordinator was not prepared";
            case  -26: return "Invalid option";
            case  -27: return "Invalid method call";
            case -100: return "When requesting a work with the ASYNC, the answer back which is returned";
            case -200: return "Serial time out";
            case -201: return "Serial error";
            case -202: return "Frame control error";
            case -203: return "Invalid request";
            case -204: return "Invalid frame type";
            case -205: return "CRC16 error";
            case -230: return "The function calling which is not supported.";
            case -231: return "Wrong data";
            case -232: return "There is not a network which is connected";
            case -233: return "Network FORM error";
            case -234: return "Network LEAVE error";
            case -235: return "Duplication binding occurrence when connect with sensor";
            case -236: return "The Binding resource kicks full";
            case -237: return "Searching sensor failure";
            case -238: return "Sensor and binding failure";
            case -239: return "With sensor binding truncation failure";
            case -240: return "Combined already with sensor";
            case -260: return "Data error";
            case -261: return "Data payload length error";
            case -262: return "Delivery error (Sensor transmission failure)";
            case -263: return "Sensor binding error (connect to fail with sensor)";
            default  : return "["+code+"] is unknown error";
        }

    }
    
    public String getMODEM_ERROR_NAME() throws Exception {
        int code = getMODEM_ERROR();
        switch(code){
            case    0: return "";//sensor status is normal
            case   -1: return "Wrong device";
            case   -2: return "Device is not working";
            case   -3: return "The device already while using";
            case   -4: return "Memory error";
            case   -5: return "Invalid handle";
            case   -6: return "Invalid buffer";
            case   -7: return "Invalid data length";
            case   -8: return "Invalid timeout value";
            case   -9: return "Already initialized";
            case  -10: return "The API did not initialize";
            case  -11: return "Invalid ID";
            case  -12: return "The device which is not register";
            case  -13: return "The service is not started";
            case  -14: return "When the service operates already, re-start";
            case  -15: return "Invalid interface";
            case  -16: return "Device error";
            case  -17: return "Binding resources excess";
            case  -18: return "The sensor already while using";
            case  -19: return "Invalid key value";
            case  -20: return "Response time out";
            case  -21: return "Invalid parameter";
            case  -22: return "Wrong command";
            case  -23: return "Unknown command";
            case  -24: return "Not connected";
            case  -25: return "The cordinator was not prepared";
            case  -26: return "Invalid option";
            case  -27: return "Invalid method call";
            case -100: return "When requesting a work with the ASYNC, the answer back which is returned";
            case -200: return "Serial time out";
            case -201: return "Serial error";
            case -202: return "Frame control error";
            case -203: return "Invalid request";
            case -204: return "Invalid frame type";
            case -205: return "CRC16 error";
            case -230: return "The function calling which is not supported.";
            case -231: return "Wrong data";
            case -232: return "There is not a network which is connected";
            case -233: return "Network FORM error";
            case -234: return "Network LEAVE error";
            case -235: return "Duplication binding occurrence when connect with sensor";
            case -236: return "The Binding resource kicks full";
            case -237: return "Searching sensor failure";
            case -238: return "Sensor and binding failure";
            case -239: return "With sensor binding truncation failure";
            case -240: return "Combined already with sensor";
            case -260: return "Data error";
            case -261: return "Data payload length error";
            case -262: return "Delivery error (Sensor transmission failure)";
            case -263: return "Sensor binding error (connect to fail with sensor)";
            default  : return "["+code+"] is unknown error";
        }

    }
    
    public int getMETER_ERROR() throws Exception {
        return DataFormat.hex2unsigned8(
            data[OFS_METER_ERROR]);
    }
    
    public String getMETER_ERROR_NAME() throws Exception {
        int code = getMETER_ERROR();
        switch(code){
            case 0x00: return "";//No error
            case 0xF1: return "[Meter] no answer";
            case 0xF2: return "[FRAME] error CRC";
            case 0xF5: return "[FRAME] error length";
            case 0xF7: return "[METER] error security";
            case 0x01: return "[ANSI] error";
            case 0x02: return "[ANSI] service not supported";
            case 0x03: return "[ANSI] insufficient security clearance";
            case 0x04: return "[ANSI] operation not possible";
            case 0x05: return "[ANSI] inappropriate action requested";
            case 0x06: return "[ANSI] device busy";
            case 0x07: return "[ANSI] data not ready";
            case 0x08: return "[ANSI] data locked";
            case 0x09: return "[ANSI] renegotiate request";
            case 0x0A: return "[ANSI] invalid service sequence state";
            default  : return "["+code+"] is unknown error";
        }
    }
    
    public MeteringFail getMeteringFail(){
        
        try{
            if(data != null && data.length == (LEN_MODEM_ERROR+LEN_METER_ERROR))
            {
                MeteringFail fail = new MeteringFail();                
                fail.setModemErrCode(getMODEM_ERROR());
                fail.setModemErrCodeName(getMODEM_ERROR_NAME());
                fail.setMeterErrCode(getMETER_ERROR());
                fail.setMeterErrCodeName(getMETER_ERROR_NAME());
                return fail;
            }
        }catch(Exception e){
            log.warn(e.getMessage());
        }
        return null;
    }

}