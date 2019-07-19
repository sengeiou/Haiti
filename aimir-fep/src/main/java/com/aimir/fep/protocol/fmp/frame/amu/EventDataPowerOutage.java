package com.aimir.fep.protocol.fmp.frame.amu;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.frame.AMUGeneralDataConstants;
import com.aimir.fep.protocol.fmp.frame.AMUGeneralDataFrame;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;

/**
 * Event Frame Power outage & recovery
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 2. 19. 오후 2:43:35$
 */
public class EventDataPowerOutage extends AMUFramePayLoad{
	
	private Log log = LogFactory.getLog(EventDataPowerOutage.class);
	
	byte mcuAddrType;
	byte[] mcuAddress		= null;
	byte[] powetStatus		= null;
	EventDataPowerOutagetTime powerOutAgeTime ;
	
	/**
	 * constructor
	 */
	public EventDataPowerOutage(){
	}
	
	/**
	 * constructor
	 * 
	 * @param gdf
	 * @throws Exception
	 */
	public EventDataPowerOutage(AMUGeneralDataFrame gdf) throws Exception{

		try{
			byte[] rawData = gdf.getFp();
			decode(rawData);
			
		}catch(Exception e){
			log.error("EventFrame[Power outage & recovery] Error : ", e);
			throw e;
		}
	}
	
	/**
	 * constructor
	 * 
	 * @param payLoadData
	 * @throws Exception
	 */
	public EventDataPowerOutage(byte[] framePayLoad) throws Exception{
		try{
			decode(framePayLoad);
		}catch(Exception e){
			throw e;
		}
	}

	/**
	 * get MCU Address Type
	 * @return
	 */
	public byte getMcuAddrType() {
		return mcuAddrType;
	}

	/**
	 * set MCU Address Type
	 * @param mcuAddrType
	 */
	public void setMcuAddrType(byte mcuAddrType) {
		this.mcuAddrType = mcuAddrType;
	}

	/**
	 * get MCU Address 
	 * @return
	 */
	public byte[] getMcuAddress(){
		return mcuAddress;
	}

	/**
	 * set MCU Address
	 * @param mcuAddr
	 */
	public void setMcuAddress(byte[] mcuAddress) {
		this.mcuAddress = mcuAddress;
	}

	/**
	 * decode
	 * 
	 * @param payLoadData
	 * @throws Exception
	 */
	public void decode(byte[] framePayLoad)throws Exception{
		
		try{
			int pos =0;
			this.identifier = framePayLoad[pos];
			pos += AMUFramePayLoadConstants.FormatLength.FRAME_IDENTIFIER;
			
			this.mcuAddrType = framePayLoad[pos];
			pos += AMUFramePayLoadConstants.FormatLength.EVENT_MCU_ADDRESS_TYPE;
			
			// 0x00이면 MCU Address 필드를 사용않함
			if(mcuAddrType != (byte)0x00){
				this.mcuAddress	= DataFormat.select(framePayLoad, pos, 
						AMUFramePayLoadConstants.FormatLength.EVENT_MCU_ADDRESS); 
				pos += AMUFramePayLoadConstants.FormatLength.EVENT_MCU_ADDRESS;
			}
			
			this.powetStatus = DataFormat.select(framePayLoad, pos, 
					AMUFramePayLoadConstants.FormatLength.EVENT_STATUS);
			pos += AMUFramePayLoadConstants.FormatLength.EVENT_STATUS;
			
			//this.frameStatus = new AMUDataFrameStatus(this.status);
			
			byte[] time	= DataFormat.select(framePayLoad, pos, 
					AMUFramePayLoadConstants.FormatLength.EVENT_POWER_OUTAGE_TIME);
			
			powerOutAgeTime = new EventDataPowerOutagetTime(time);
			
		}catch(Exception e){
			log.error("Power outage & recovery Event Frame decode failed : ", e);
			throw e;
		}
	}
	
	/**
	 * get MCU Address
	 * LSB부터 유효한값 설정
	 * @return
	 */
	public String getMcuID() throws Exception{
		
		byte[] lmMcuId = null;
		switch(this.mcuAddrType){
		case (byte) 0x01: lmMcuId = DataFormat.select(	// 4byte
								DataFormat.LSB2MSB(mcuAddress), 0, 
								AMUGeneralDataConstants.ADDRTYPE_IP_LEN); break;
		case (byte) 0x02: lmMcuId = DataFormat.select(	// 6byte
								DataFormat.LSB2MSB(mcuAddress), 0, 
								AMUGeneralDataConstants.ADDRTYPE_IP_LEN); break;
		case (byte) 0x03: lmMcuId = DataFormat.select(	// 8byte
								DataFormat.LSB2MSB(mcuAddress), 0, 
								AMUGeneralDataConstants.ADDRTYPE_IP_LEN); break;
		default : 
			log.debug("get String Mcu Address Failed !");
		}
		return DataUtil.getString(lmMcuId);
	}
	
	/**
	 * get PowerStatus
	 * @return
	 */
	public int getPowerStatus(){
		return DataUtil.getIntToByte(powetStatus[0]);
	}
	
	/**
	 * get Power Status Description
	 * @return
	 */
	public String getPowerStatusDesc(){
		
		switch(powetStatus[0]){
		case (byte)0x00: return "Power Outage";
		case (byte)0x01: return "Power Recovery";
		case (byte)0x02: return "Line Missing";
		case (byte)0x03: return "Line Missing Recovery";
		default : 
			log.debug("Can't Fount Power Status Desciption");
		}
		return "";
	}
	
	/**
	 * get 1Line Status Description
	 * @return
	 */
	public String get1LineStatusDesc(){
		
		if((byte)((byte)(powetStatus[1] & 0x80)) > 0){
			return "1Line Missing";
		}else{
			return "Normal";
		}
	}
	
	/**
	 * get 2Line Status Description
	 * @return
	 */
	public String get2LineStatusDesc(){
		
		if((byte)((byte)(powetStatus[1] & 0x40)) > 0){
			return "2Line Missing";
		}else{
			return "Normal";
		}
	}
	
	/**
	 * get 3Line Status Description
	 * @return
	 */
	public String get3LineStatusDesc(){
		
		if((byte)((byte)(powetStatus[1] & 0x20)) > 0){
			return "3Line Missing";
		}else{
			return "Normal";
		}
	}
}


