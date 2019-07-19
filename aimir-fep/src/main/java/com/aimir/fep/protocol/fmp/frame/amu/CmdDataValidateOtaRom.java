package com.aimir.fep.protocol.fmp.frame.amu;

import java.io.ByteArrayOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.frame.AMUDataFrameStatus;
import com.aimir.fep.util.DataFormat;

/**
 * CmdDataValidateOtaRom
 * 
 * Command Frame Validate OTA-ROM Command
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 2. 19. 오전 11:03:15$
 */
public class CmdDataValidateOtaRom extends AMUFramePayLoad{
	
	private Log log = LogFactory.getLog(CmdDataValidateOtaRom.class);
	
	byte[] imageLength;
	byte[] imageCrc;
	
	/**
	 * constructor
	 */
	public CmdDataValidateOtaRom(){
	}

	/**
	 * constructor
	 * 
	 * @param isRequest
	 * @param isOpen
	 * @param isResRequest
	 * @param length
	 * @param imageCrc
	 */
	public CmdDataValidateOtaRom(boolean isRequest, boolean isResRequest,int responseTime, byte[] length, byte[] imageCrc ){
		
		this.identifier   = AMUFramePayLoadConstants.FrameIdentifier.CMD_VALIDATE_OTAROM;
		
		if(isRequest)
			this.control |= (byte)0x80;
		if(isResRequest)
			this.control |= (byte)0x02;
		
		this.responseTime = (byte)responseTime;
		this.imageLength  = length;
		this.imageCrc	  = imageCrc;
	}		
	/**
	 * constructor
	 * 
	 * @param payLoadData
	 * @throws Exception
	 */
	public CmdDataValidateOtaRom(byte[] framePayLoad) throws Exception{
		try {
			decode(framePayLoad);
		} catch (Exception e) {
			throw e;
		}
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
	 * get Image CRC
	 * @return
	 */
	public byte[] getImageCrc() {
		return imageCrc;
	}

	/**
	 * set Image CRC
	 * @param imageCrc
	 */
	public void setImageCrc(byte[] imageCrc) {
		this.imageCrc = imageCrc;
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
				
				this.frameStatus = new AMUDataFrameStatus(statCode);
			}else{
				throw new Exception("Validate OTA-ROM Command decode failed, Not receive Response");
			}
			
		}catch(Exception e){
			log.error("Validate OTA-ROM Command decode failed : ", e);
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
				bao.write(this.imageLength			, 0 , AMUFramePayLoadConstants.FormatLength.CMD_VALIDATE_OTAROM_IMAGE);
				bao.write(this.imageCrc				, 0 , AMUFramePayLoadConstants.FormatLength.CMD_VALIDATE_OTAROM_IMAGE_CRC);
			}else{
				throw new Exception("Validate OTA-ROM Command encode failed, Not Request");
			}
		}catch(Exception e){
			log.error("Validate OTA-ROM Command encode failed : ", e);
			throw e;
		}
		return bao.toByteArray();	
	}	
}


