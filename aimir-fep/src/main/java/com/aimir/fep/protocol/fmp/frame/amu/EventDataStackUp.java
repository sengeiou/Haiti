package com.aimir.fep.protocol.fmp.frame.amu;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.frame.AMUDataFrameStatus;
import com.aimir.fep.protocol.fmp.frame.AMUGeneralDataFrame;
import com.aimir.fep.util.DataFormat;

/**
 * EventDataStackUp
 * 
 * Event Frame Stack Up
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 2. 19. 오후 3:52:11$
 */
public class EventDataStackUp extends AMUFramePayLoad{

	private Log log = LogFactory.getLog(EventDataStackUp.class);
	
	EventDataStackUpNetWorkParam netWorkParam;
	
	/**
	 * constructor
	 */
	public EventDataStackUp(){
	}
	
	/**
	 * constructor
	 * 
	 * @param gdf
	 * @throws Exception
	 */
	public EventDataStackUp(AMUGeneralDataFrame gdf) throws Exception{

		try{
			byte[] rawData = gdf.getFp();
			decode(rawData);
			
		}catch(Exception e){
			log.error("EventFrame[Stack Up] Error : ", e);
			throw e;
		}
	}
	
	/**
	 * constructor
	 * 
	 * @param payLoadData
	 * @throws Exception
	 */
	public EventDataStackUp(byte[] framePayLoad) throws Exception{
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
			
			byte[] statCode = DataFormat.select(framePayLoad, pos, 
					AMUFramePayLoadConstants.FormatLength.EVENT_STATUS);
			pos += AMUFramePayLoadConstants.FormatLength.EVENT_STATUS;
			
			this.frameStatus = new AMUDataFrameStatus(statCode);
			
			byte[] networkParam	= DataFormat.select(framePayLoad, pos,
					AMUFramePayLoadConstants.FormatLength.EVENT_STACKUP_NETWORK_PARAM);
			System.arraycopy(framePayLoad, pos, networkParam, 0, AMUFramePayLoadConstants.FormatLength.EVENT_STACKUP_NETWORK_PARAM);
			
			this.netWorkParam = new EventDataStackUpNetWorkParam(networkParam);
			
		}catch(Exception e){
			log.error("Stack Up Event Frame decode failed : ", e);
			throw e;
		}
	}
}


