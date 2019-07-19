package com.aimir.fep.protocol.fmp.frame.amu;

import java.io.ByteArrayOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.frame.AMUDataFrameStatus;
import com.aimir.fep.util.DataFormat;

/**
 * CmdDataMiuVersion
 * 
 * Command Frame MIU Version Command
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 2. 19. 오전 11:43:54$
 */
public class CmdDataMiuVersion extends AMUFramePayLoad{
	
	private Log log = LogFactory.getLog(CmdDataMiuVersion.class);

	byte[] miuFirmwareVer				= null;
	byte[] miuHardwareVer				= null;
	byte[] mobileModuleFirmwareVer		= null;
	byte[] mobileModuleHardwareVer		= null;
	byte[] ethernetModuleFirmwareVer 	= null;
	byte[] ethernetModuleHardwareVer 	= null;
	byte[] zigBeeModuleFirmwareVer		= null;
	byte[] zigBeeModuleHardwareVer 		= null;
	byte[] zigBeeModuleProtocolVer 		= null;
	byte[] zigBeeModuleStackVer 		= null;
	
	/**
	 * constructor
	 */
	public CmdDataMiuVersion(){
	}
	
	/**
	 * constructor
	 * 
	 * @param isRequest
	 * @param isResRequest
	 */
	public CmdDataMiuVersion(boolean isRequest, boolean isResRequest , int responseTime){
		
		this.identifier = AMUFramePayLoadConstants.FrameIdentifier.CMD_MIU_VERSION;
		
		if(isRequest)
			this.control |= (byte)0x80;
		if(isResRequest)
			this.control |= (byte)0x02;
		
		this.responseTime = (byte)responseTime;
	}	
	/**
	 * constructor
	 * 
	 * @param payLoadData
	 * @throws Exception
	 */
	public CmdDataMiuVersion(byte[] framePayLoad) throws Exception{
		try {
			decode(framePayLoad);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * get MIU firmware version
	 * @return
	 */
	public byte[] getMiuFirmwareVer() {
		return miuFirmwareVer;
	}

	/**
	 * set MIU firmware version
	 * @param miuFirmwareVer
	 */
	public void setMiuFirmwareVer(byte[] miuFirmwareVer) {
		this.miuFirmwareVer = miuFirmwareVer;
	}

	/**
	 * get MIU Hardware version
	 * @return
	 */
	public byte[] getMiuHardwareVer() {
		return miuHardwareVer;
	}

	/**
	 * set MIU Hardware version
	 * @param miuHardwareVer
	 */
	public void setMiuHardwareVer(byte[] miuHardwareVer) {
		this.miuHardwareVer = miuHardwareVer;
	}

	/**
	 * get Mobile module firmware version
	 * @return
	 */
	public byte[] getMobileModuleFirmwareVer() {
		return mobileModuleFirmwareVer;
	}

	/**
	 * set Mobile module firmware version
	 * @param mobileModuleFirmwareVer
	 */
	public void setMobileModuleFirmwareVer(byte[] mobileModuleFirmwareVer) {
		this.mobileModuleFirmwareVer = mobileModuleFirmwareVer;
	}

	/**
	 * get Mobile module hardware version
	 * @return
	 */
	public byte[] getMobileModuleHardwareVer() {
		return mobileModuleHardwareVer;
	}

	/**
	 * set Mobile module hardware version
	 * @param mobileModuleHardwareVer
	 */
	public void setMobileModuleHardwareVer(byte[] mobileModuleHardwareVer) {
		this.mobileModuleHardwareVer = mobileModuleHardwareVer;
	}

	/**
	 * get Ethernet module firmware version
	 * @return
	 */
	public byte[] getEthernetModuleFirmwareVer() {
		return ethernetModuleFirmwareVer;
	}

	/**
	 * set Ethernet module firmware version
	 * @param ethernetModuleFirmwareVer
	 */
	public void setEthernetModuleFirmwareVer(byte[] ethernetModuleFirmwareVer) {
		this.ethernetModuleFirmwareVer = ethernetModuleFirmwareVer;
	}

	/**
	 * get Ethernet module hardware version
	 * @return
	 */
	public byte[] getEthernetModuleHardwareVer() {
		return ethernetModuleHardwareVer;
	}

	/**
	 * set Ethernet module hardware version
	 * @param ethernetModuleHardwareVer
	 */
	public void setEthernetModuleHardwareVer(byte[] ethernetModuleHardwareVer) {
		this.ethernetModuleHardwareVer = ethernetModuleHardwareVer;
	}

	/**
	 * get ZigBee module firmware version
	 * @return
	 */
	public byte[] getZigBeeModuleFirmwareVer() {
		return zigBeeModuleFirmwareVer;
	}

	/**
	 * set ZigBee module firmware version
	 * @param zigBeeModuleFirmwareVer
	 */
	public void setZigBeeModuleFirmwareVer(byte[] zigBeeModuleFirmwareVer) {
		this.zigBeeModuleFirmwareVer = zigBeeModuleFirmwareVer;
	}

	/**
	 * get ZigBee module hardware version
	 * @return
	 */
	public byte[] getZigBeeModuleHardwareVer() {
		return zigBeeModuleHardwareVer;
	}

	/**
	 * set ZigBee module hardware version
	 * @param zigBeeModuleHardwareVer
	 */
	public void setZigBeeModuleHardwareVer(byte[] zigBeeModuleHardwareVer) {
		this.zigBeeModuleHardwareVer = zigBeeModuleHardwareVer;
	}

	/**
	 * get ZigBee module protocol version
	 * @return
	 */
	public byte[] getZigBeeModuleProtocolVer() {
		return zigBeeModuleProtocolVer;
	}

	/**
	 * set ZigBee module protocol version
	 * @param zigBeeModuleProtocolVer
	 */
	public void setZigBeeModuleProtocolVer(byte[] zigBeeModuleProtocolVer) {
		this.zigBeeModuleProtocolVer = zigBeeModuleProtocolVer;
	}

	/**
	 * get ZigBee module stack version
	 * @return
	 */
	public byte[] getZigBeeModuleStackVer() {
		return zigBeeModuleStackVer;
	}

	/**
	 * set ZigBee module stack version
	 * @param zigBeeModuleStackVer
	 */
	public void setZigBeeModuleStackVer(byte[] zigBeeModuleStackVer) {
		this.zigBeeModuleStackVer = zigBeeModuleStackVer;
	}

	/**
	 * decode
	 * 
	 * @param payLoadData
	 * @throws Exception
	 */
	public void decode(byte[] framePayLoad) throws Exception{
		
		try{
			int pos =0;
			this.identifier = framePayLoad[pos];
			pos += AMUFramePayLoadConstants.FormatLength.FRAME_IDENTIFIER;
			
			this.control	= framePayLoad[pos];
			pos += AMUFramePayLoadConstants.FormatLength.FRAME_CONTROL;
			
			// Response
			if(!isRequest(this.control)){
				byte[] statCode = DataFormat.select(framePayLoad, pos, 
						AMUFramePayLoadConstants.FormatLength.CMD_STATUS);
				pos +=  AMUFramePayLoadConstants.FormatLength.CMD_STATUS;
				
				this.frameStatus = new AMUDataFrameStatus(statCode);
				
				this.miuFirmwareVer	= DataFormat.select(framePayLoad, pos,
						AMUFramePayLoadConstants.FormatLength.CMD_MIU_FIRMWARE_VER);
				pos += AMUFramePayLoadConstants.FormatLength.CMD_MIU_FIRMWARE_VER;
				
				this.miuHardwareVer	= DataFormat.select(framePayLoad, pos,
						AMUFramePayLoadConstants.FormatLength.CMD_MIU_HARDWARE_VER);
				pos += AMUFramePayLoadConstants.FormatLength.CMD_MIU_HARDWARE_VER;
				
				this.mobileModuleFirmwareVer = DataFormat.select(framePayLoad, pos,
						AMUFramePayLoadConstants.FormatLength.CMD_MOBILE_MODULE_FIRMWARE_VER);
				pos += AMUFramePayLoadConstants.FormatLength.CMD_MOBILE_MODULE_FIRMWARE_VER;
				
				this.mobileModuleHardwareVer = DataFormat.select(framePayLoad, pos,
						AMUFramePayLoadConstants.FormatLength.CMD_MOBILE_MODULE_HARDWARE_VER);
				pos += AMUFramePayLoadConstants.FormatLength.CMD_MOBILE_MODULE_HARDWARE_VER;
				
				this.ethernetModuleFirmwareVer = DataFormat.select(framePayLoad, pos,
						AMUFramePayLoadConstants.FormatLength.CMD_ETHERNET_MODULE_FIRMWARE_VER);
				pos += AMUFramePayLoadConstants.FormatLength.CMD_ETHERNET_MODULE_FIRMWARE_VER;
				
				this.ethernetModuleHardwareVer = DataFormat.select(framePayLoad, pos,
						AMUFramePayLoadConstants.FormatLength.CMD_ETHERNET_MODULE_HARDWARE_VER);
				pos += AMUFramePayLoadConstants.FormatLength.CMD_ETHERNET_MODULE_HARDWARE_VER;
				
				this.zigBeeModuleFirmwareVer = DataFormat.select(framePayLoad, pos,
						AMUFramePayLoadConstants.FormatLength.CMD_ZIGBEE_MODULE_FIRMWARE_VER);
				pos += AMUFramePayLoadConstants.FormatLength.CMD_ZIGBEE_MODULE_FIRMWARE_VER;
				
				this.zigBeeModuleHardwareVer = DataFormat.select(framePayLoad, pos,
						AMUFramePayLoadConstants.FormatLength.CMD_ZIGBEE_MODULE_HARDWARE_VER);
				pos += AMUFramePayLoadConstants.FormatLength.CMD_ZIGBEE_MODULE_HARDWARE_VER;
				
				this.zigBeeModuleProtocolVer = DataFormat.select(framePayLoad, pos,
						AMUFramePayLoadConstants.FormatLength.CMD_ZIGBEE_MODULE_PROTOCOL_VER);
				pos += AMUFramePayLoadConstants.FormatLength.CMD_ZIGBEE_MODULE_PROTOCOL_VER;
				
				this.zigBeeModuleStackVer = DataFormat.select(framePayLoad, pos,
						AMUFramePayLoadConstants.FormatLength.CMD_ZIGBEE_MODULE_STACK_VER);
				
			}else{
				throw new Exception("MIU Version Command decode failed, Not receive Response");
			}
		}catch(Exception e){
			log.error("MIU Version Command decode failed : ", e);
			throw e;
		}
	}
	
	/**
	 * encode
	 * 
	 * @return  byte[]
	 * @throws Exception
	 */
	public byte[] encode() throws Exception{
		
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		
		try{
			bao.write(new byte[]{this.identifier} 	, 0 , AMUFramePayLoadConstants.FormatLength.FRAME_IDENTIFIER);
			bao.write(new byte[]{this.control} 		, 0 , AMUFramePayLoadConstants.FormatLength.FRAME_CONTROL);
			bao.write(new byte[]{this.responseTime} , 0 , AMUFramePayLoadConstants.FormatLength.CMD_RESPONSE_TIME);
		}catch(Exception e){
			log.error("MIU Version Command encode failed : ", e);
			throw e;
		}
		return bao.toByteArray();	
	}	
}


