package com.aimir.fep.protocol.fmp.frame.amu;

import java.io.ByteArrayOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.frame.AMUDataFrameStatus;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;

/**
 * CmdDataOtaRom
 * 
 * Command Frame OTA-ROM Command
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 2. 19. 오전 9:17:24$
 */
public class CmdDataOtaRom extends AMUFramePayLoad{

	private Log log = LogFactory.getLog(CmdDataOtaRom.class);
	
	byte[] address;
	byte[] imageLength;
	byte[] image;
	
	/**
	 * constructor
	 */
	public CmdDataOtaRom(){
	}
	
	/**
	 * constructor
	 * 
	 * @param isRequest
	 * @param isWrite
	 * @param isResRequest
	 * @param address
	 * @param image
	 * @param miuType
	 */
	public CmdDataOtaRom(boolean isRequest, boolean isWrite, boolean isResRequest,int responseTime, byte[] address, byte[] image ){
		
		this.identifier = AMUFramePayLoadConstants.FrameIdentifier.CMD_OTA_ROM;
		
		if(isRequest)
			this.control |= (byte)0x80;
		if(isWrite)
			this.control |= (byte)0x20;
		if(isResRequest)
			this.control |= (byte)0x02;
		
		this.responseTime = (byte)responseTime;
		this.address = address;
		this.imageLength = DataUtil.get2ByteToInt((image.length));
		this.image = image;
	}	
	
	/**
	 * constructor
	 * 
	 * @param payLoadData
	 * @throws Exception
	 */
	public CmdDataOtaRom(byte[] framePayLoad) throws Exception{
		try {
			decode(framePayLoad);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * get Address
	 * @return
	 */
	public byte[] getAddress() {
		return address;
	}

	/**
	 * set Address
	 * @param address
	 */
	public void setAddress(byte[] address) {
		this.address = address;
	}

	/**
	 * get Image Length
	 * @return
	 */
	public byte[] getImageLength() {
		return imageLength;
	}

	/**
	 * set Image Length
	 * @param imageLength
	 */
	public void setImageLength(byte[] imageLength) {
		this.imageLength = imageLength;
	}

	/**
	 * get Image
	 * @return
	 */
	public byte[] getImage() {
		return image;
	}

	/**
	 * set Image
	 * @param image
	 */
	public void setImage(byte[] image) {
		this.image = image;
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
				
				this.address = DataFormat.select(framePayLoad, pos, 
						AMUFramePayLoadConstants.FormatLength.CMD_OTAROM_ADDRESS);
				pos+=AMUFramePayLoadConstants.FormatLength.CMD_OTAROM_ADDRESS;
				
				this.imageLength = DataFormat.select(framePayLoad, pos, 
						AMUFramePayLoadConstants.FormatLength.CMD_OTAROM_IMAGE_LENGTH); 
				pos+=AMUFramePayLoadConstants.FormatLength.CMD_OTAROM_IMAGE_LENGTH;
				
				int image_len = DataUtil.getIntTo2Byte(imageLength);
				log.debug("imageLength :"+image_len);
				this.image = DataFormat.select(framePayLoad, pos, image_len);
			}else{
				throw new Exception("OTA-ROM Command decode failed, Not receive Response");
			}
		}catch(Exception e){
			log.error("OTA-ROM Command decode failed : ", e);
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
			
			// Request 
			if(isRequest(this.control)){
				
				bao.write(this.address				, 0 , AMUFramePayLoadConstants.FormatLength.CMD_OTAROM_ADDRESS);
				bao.write(this.imageLength			, 0 , AMUFramePayLoadConstants.FormatLength.CMD_OTAROM_IMAGE_LENGTH);
				// Write
				if(isWrite(this.control)){
					bao.write(this.image		, 0 , image.length);
				}
			}else{
				throw new Exception("OTA-ROM Command encode failed, Not Request");
			}
		}catch(Exception e){
			log.error("OTA-ROM Command encode failed : ", e);
			throw e;
		}
		return bao.toByteArray();	
	}	

	
}


