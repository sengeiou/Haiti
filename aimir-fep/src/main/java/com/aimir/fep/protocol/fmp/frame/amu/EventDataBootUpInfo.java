package com.aimir.fep.protocol.fmp.frame.amu;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;

/**
 * Event Data Boot Up Information
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 4. 19. 오후 2:56:34$
 */
public class EventDataBootUpInfo {

	private static Log log = LogFactory.getLog(EventDataBootUpInfo.class);
	
	public static final int OFS_RESET_REASON 	= 0;
	public static final int OFS_RESET_COUNT 	= 1;
	public static final int OFS_FIRMWARE_VER	= 3;
	public static final int OFS_HARDWARE_VER	= 5;
	public static final int OFS_PROCOTOL_VER 	= 7;
	public static final int OFS_STACK_VER		= 9;
	
	public static final int LEN_RESET_COUNT 	= 2;
	public static final int LEN_FIRMWARE_VER	= 2;
	public static final int LEN_HARDWARE_VER	= 2;
	public static final int LEN_PROCOTOL_VER 	= 2;
	public static final int LEN_STACK_VER		= 2;
	
	byte[] rawData 		= null;
	
	/**
	 * constructor
	 */
	public EventDataBootUpInfo(){
	}
	
	/**
	 * constructor
	 */
	public EventDataBootUpInfo(byte[] rawData) throws Exception{
		
		try{
			this.rawData = rawData;
		}catch(Exception e){
			throw e;
		}
		
	}
	
	/**
	 * get Reset Reason
	 * @return
	 * @throws Exception
	 */
	public int getResetReason() throws Exception{
		return DataFormat.hex2unsigned8(rawData[OFS_RESET_REASON]);
	}
	
	/**
	 * get Reset Count
	 * @return
	 * @throws Exception
	 */
	public int getResetCount() throws Exception{
		return DataFormat.hex2dec(DataFormat.select(rawData,OFS_RESET_COUNT,LEN_RESET_COUNT));
	}
	
	/**
	 * get FirmWare Version
	 * @return
	 * @throws Exception
	 */
	public String getFirmwareVer() throws Exception{
		return new String(DataFormat.select(rawData, OFS_FIRMWARE_VER, LEN_FIRMWARE_VER));
	}
	
	/**
	 * get HardWare VerSion
	 * @return
	 * @throws Exception
	 */
	public String getHardwareVer() throws Exception{
		return new String(DataFormat.select(rawData, OFS_HARDWARE_VER, LEN_HARDWARE_VER));
	}
	
	/**
	 * get Protocol Version
	 * @return
	 * @throws Exception
	 */
	public String getProtocolVer() throws Exception{
		return new String(DataFormat.select(rawData, OFS_PROCOTOL_VER, LEN_PROCOTOL_VER));
	}

	/**
	 * get Stack Version
	 * @return
	 * @throws Exception
	 */
	public String getStackVer() throws Exception{
		return new String(DataFormat.select(rawData, OFS_STACK_VER, LEN_STACK_VER));
	}
	
	
	public String toString() {
		
        StringBuffer sb = new StringBuffer();
        
        try{
        	sb.append(" RESETREASON[" + getResetReason() + "]"
        		 	+ ", RESETCOUNT[" + getResetCount() + "]"
                    + ", FIRMWAREVERSION[" + getFirmwareVer() + "]"
                    + ", HARDWAREVERSION[" + getHardwareVer() + "]"
                    + ", PROTOCOLVERSION[" + getProtocolVer() + "]"
                    + ", STACKVERSION[" + getStackVer() + "]"
                   );
        }catch (Exception e) {
        	log.warn("EventData BootUp Information TO STRING ERR=>"+e.getMessage());
		}
        return sb.toString();
	 }
	 
}


