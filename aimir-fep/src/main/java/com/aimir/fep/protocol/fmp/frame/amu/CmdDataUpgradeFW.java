package com.aimir.fep.protocol.fmp.frame.amu;

import java.io.ByteArrayOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.frame.AMUDataFrameStatus;
import com.aimir.fep.util.DataFormat;

/**
 * CmdDataUpgradeFW
 * 
 * Command Frame Upgrade F/W command
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 2. 19. 오전 11:28:25$
 */
public class CmdDataUpgradeFW extends AMUFramePayLoad{
	
	private Log log = LogFactory.getLog(CmdDataUpgradeFW.class);
	
	/**
	 * constructor
	 */
	public CmdDataUpgradeFW(){
	}
	
	/**
	 * constructor
	 * 
	 * @param isRequest
	 * @param isResRequest
	 * @param upGradeType
	 */
	public CmdDataUpgradeFW(boolean isRequest, boolean isResRequest , byte upGradeType){
		
		this.identifier = AMUFramePayLoadConstants.FrameIdentifier.CMD_UPGRADE_FW;
		
		if(isRequest)
			this.control |= (byte)0x80;
		if(isResRequest)
			this.control |= (byte)0x02;
		/**	  
		 * 	 upGradeType  0x01 	- 자체 F/W upgrade 	0x02	- External EM250 serial upgrade
		 */
		this.responseTime = (byte)responseTime;
		this.payLoadData = new byte[]{upGradeType};
	}	
	/**
	 * constructor
	 * 
	 * @param payLoadData
	 * @throws Exception
	 */
	public CmdDataUpgradeFW(byte[] framePayLoad) throws Exception{
		try {
			decode(framePayLoad);
		} catch (Exception e) {
			throw e;
		}
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
				throw new Exception("Upgrade F/W Command decode failed, Not receive Response");
			}
		}catch(Exception e){
			log.error("Upgrade F/W Command decode failed : ", e);
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
				bao.write(this.payLoadData			, 0 , AMUFramePayLoadConstants.FormatLength.CMD_UPGRADE_FW_TYPE);
			}else{
				throw new Exception("Upgrade F/W Command encode failed, Not Request");
			}
			
		}catch(Exception e){
			log.error("Upgrade F/W Command encode failed : ", e);
			throw e;
		}
		return bao.toByteArray();	
	}	

}


