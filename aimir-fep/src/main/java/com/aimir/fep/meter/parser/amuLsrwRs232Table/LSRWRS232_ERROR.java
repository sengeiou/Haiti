package com.aimir.fep.meter.parser.amuLsrwRs232Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;

/**
 * get LSRW Error Feild
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 3. 4. 오전 11:24:34$
 */
public class LSRWRS232_ERROR {

	private static Log log = LogFactory.getLog(LSRWRS232_ERROR.class);
	
	private byte[] rawData;
	
	private static final int OFS_ERROR_CODE 		= 0;
	private static final int LEN_ERROR_CODE 		= 2;
	/**
	 * Constructor
	 */
	public LSRWRS232_ERROR(byte[] rawData) {
		this.rawData = rawData;
	}	
	

	/**
	 * get Error Code
	 * @return
	 * @throws Exception
	 */
	public int getErrorCode() throws Exception {        
		
		int ret = DataFormat.hex2unsigned16(
	        		DataFormat.select(rawData, OFS_ERROR_CODE, LEN_ERROR_CODE));
		log.debug("Error Code : " + ret);
	    return ret;
	}
	
	
	/**
	 * get System Status
	 * @return
	 * @throws Exception
	 */
	public SystemStatus getSystemStatus() throws Exception {        
		
		return new SystemStatus(DataFormat.select(rawData, OFS_ERROR_CODE, LEN_ERROR_CODE));
	}
	
}
