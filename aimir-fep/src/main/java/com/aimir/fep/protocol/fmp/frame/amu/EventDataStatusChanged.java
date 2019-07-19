package com.aimir.fep.protocol.fmp.frame.amu;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.frame.AMUDataFrameStatus;
import com.aimir.fep.protocol.fmp.frame.AMUGeneralDataFrame;

/**
 * Event Frame Error Status
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 2. 19. 오후 2:19:40$
 */
public class EventDataStatusChanged extends AMUFramePayLoad {

	private Log log = LogFactory.getLog(EventDataStatusChanged.class);
	
	/**
	 * constructor
	 */
	public EventDataStatusChanged(){
	}
	
	/**
	 * constructor
	 * 
	 * @param gdf
	 * @throws Exception
	 */
	public EventDataStatusChanged(AMUGeneralDataFrame gdf) throws Exception{

		try{
			byte[] rawData = gdf.getFp();
			decode(rawData);
			
		}catch(Exception e){
			log.error("EventFrame[Status Changed] Error : ", e);
			throw e;
		}
	}
	
	/**
	 * constructor
	 * 
	 * @param payLoadData
	 * @throws Exception
	 */
	public EventDataStatusChanged(byte[] framePayLoad) throws Exception{
		try{
			decode(framePayLoad);
		}catch(Exception e){
			throw e;
		}
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
			
			byte[] statCode = new byte[AMUFramePayLoadConstants.FormatLength.EVENT_STATUS];
			System.arraycopy(framePayLoad, pos, statCode, 0, AMUFramePayLoadConstants.FormatLength.EVENT_STATUS);
			
			this.frameStatus = new AMUDataFrameStatus(statCode);
			
		}catch(Exception e){
			log.error("Status Changed Event Frame decode failed : ", e);
			throw e;
		}
	}
}


