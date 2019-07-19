package com.aimir.fep.protocol.fmp.frame.amu;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.frame.AMUGeneralDataFrame;
import com.aimir.fep.util.DataFormat;

/**
 * Event Frame Boot up
 * 
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 2. 19. 오후 3:48:43$
 */
public class EventDataBootUp extends AMUFramePayLoad{
	
	private Log log = LogFactory.getLog(EventDataBootUp.class);
	
	EventDataBootUpInfo bootInfo;
	
	/**
	 * constructor
	 */
	public EventDataBootUp(){
	}
	
	/**
	 * constructor
	 * 
	 * @param gdf
	 * @throws Exception
	 */
	public EventDataBootUp(AMUGeneralDataFrame gdf) throws Exception{

		try{
			byte[] rawData = gdf.getFp();
			decode(rawData);
			
		}catch(Exception e){
			log.error("EventFrame[Boot Up] Error : ", e);
			throw e;
		}
	}
	
	/**
	 * constructor
	 * 
	 * @param payLoadData
	 * @throws Exception
	 */
	public EventDataBootUp(byte[] framePayLoad) throws Exception{
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
			
			byte[] info 	= DataFormat.select(framePayLoad, pos, 
					AMUFramePayLoadConstants.FormatLength.EVENT_BOOTUP_INFO);

			bootInfo = new EventDataBootUpInfo(info);
			
		}catch(Exception e){
			log.error("Boot Up Event Frame decode failed : ", e);
			throw e;
		}
	}
}


