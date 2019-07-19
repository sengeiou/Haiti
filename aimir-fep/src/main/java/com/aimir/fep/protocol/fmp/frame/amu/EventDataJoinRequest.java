package com.aimir.fep.protocol.fmp.frame.amu;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.frame.AMUGeneralDataFrame;
import com.aimir.fep.util.DataFormat;

/**
 * Event Frame Join Request
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 4. 19. 오후 4:35:52$
 */
public class EventDataJoinRequest extends AMUFramePayLoad {
	
	private Log log = LogFactory.getLog(EventDataJoinRequest.class);
	
	byte[] newNodeID		= null;
	byte[] newNodeEUI64ID	= null;
	byte deviceUpdateStatus;
	byte[] parentNodeID		= null;

	/**
	 * constructor
	 */
	public EventDataJoinRequest(){
	}
	
	/**
	 * constructor
	 * 
	 * @param gdf
	 * @throws Exception
	 */
	public EventDataJoinRequest(AMUGeneralDataFrame gdf) throws Exception{

		try{
			byte[] rawData = gdf.getFp();
			decode(rawData);
			
		}catch(Exception e){
			log.error("EventFrame[Join Request] Error : ", e);
			throw e;
		}
	}
	
	/**
	 * constructor
	 * 
	 * @param payLoadData
	 * @throws Exception
	 */
	public EventDataJoinRequest(byte[] framePayLoad) throws Exception{
		try{
			decode(framePayLoad);
		}catch(Exception e){
			throw e;
		}
	}
	
	/**
	 * decode 
	 * @param framePayLoad
	 * @throws Exception
	 */
	public void decode(byte[] framePayLoad)throws Exception{
		
		try{
			int pos =0;
			this.identifier = framePayLoad[pos];
			pos += AMUFramePayLoadConstants.FormatLength.FRAME_IDENTIFIER;
			
			this.newNodeID = DataFormat.select(framePayLoad, pos, 
					AMUFramePayLoadConstants.FormatLength.EVENT_NEW_NODE_ID);  
			pos += AMUFramePayLoadConstants.FormatLength.EVENT_NEW_NODE_ID;
			
			this.newNodeEUI64ID = DataFormat.select(framePayLoad, pos, 
					AMUFramePayLoadConstants.FormatLength.EVENT_NEW_NODE_EUI64_ID);   
			pos += AMUFramePayLoadConstants.FormatLength.EVENT_NEW_NODE_EUI64_ID;
			
			this.deviceUpdateStatus = framePayLoad[pos];
			pos += AMUFramePayLoadConstants.FormatLength.EVENT_DEVICE_UPDATE_STATUS;
			
			this.parentNodeID  = DataFormat.select(framePayLoad, pos, 
					AMUFramePayLoadConstants.FormatLength.EVENT_PARENT_NODE_ID);   
			pos += AMUFramePayLoadConstants.FormatLength.EVENT_PARENT_NODE_ID;
			
		}catch(Exception e){
			log.error("Join Request Event Frame decode failed : ", e);
			throw e;
		}
	}
	
}


