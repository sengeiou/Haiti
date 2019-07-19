package com.aimir.fep.protocol.fmp.frame.amu;

import java.io.ByteArrayOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.frame.AMUDataFrameStatus;
import com.aimir.fep.util.DataFormat;

/**
 * CmdDataReset
 * 
 * Command Frame Reset Command 
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 2. 5. 오후 3:46:57$
 */
public class CmdDataReset extends AMUFramePayLoad {

	private Log log = LogFactory.getLog(CmdDataReset.class);
	
	/**
	 * constructor
	 */
	public CmdDataReset(){
	}
	
	/**
	 * constructor
	 * 
	 * @param isRequest
	 * @param isResRequest
	 */
	public CmdDataReset(boolean isRequest, boolean isResRequest , int responseTime){
		
		this.identifier = AMUFramePayLoadConstants.FrameIdentifier.CMD_RESET;
		
		if(isRequest)
			this.control |= (byte)0x80;
		if(isResRequest)
			this.control |= (byte)0x02;
		
		this.responseTime = (byte)responseTime;
		// 0x0987 - Reset Command Code Value
		this.payLoadData = AMUFramePayLoadConstants.FormatCode.CMD_CODE_RESET;
	}
	
	/**
	 * constructor
	 * 
	 * @param payLoadData
	 * @throws Exception
	 */
	public CmdDataReset(byte[] framePayLoad) throws Exception{
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
				throw new Exception("Reset Command decode failed, Not receive Response");
			}
		}catch(Exception e){
			log.error("Reset Command decode failed : ", e);
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
				bao.write(this.payLoadData			, 0 , AMUFramePayLoadConstants.FormatLength.CMD_RESET_CODE);
			}else{
				throw new Exception("Reset Command encode failed, Not Request");
			}
		}catch(Exception e){
			log.error("Reset Command encode failed : ", e);
			throw e;
		}
		return bao.toByteArray();	
	}	
}