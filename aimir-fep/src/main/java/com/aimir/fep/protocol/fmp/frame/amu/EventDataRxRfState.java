package com.aimir.fep.protocol.fmp.frame.amu;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.frame.AMUGeneralDataFrame;

/**
 * Event Frame RX RF State
 * 2010.04.12 수정문서에서  삭제됌
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 2. 19. 오후 3:55:49$
 */
public class EventDataRxRfState extends AMUFramePayLoad {

	private Log log = LogFactory.getLog(EventDataRxRfState.class);
	
	byte miuType;
	byte[] nodeID;
	byte rfPower;
	byte recvRSSI;
	byte recvLQI;
	
	/**
	 * constructor
	 */
	public EventDataRxRfState(){
	}
	
	/**
	 * constructor
	 * 
	 * @param gdf
	 * @throws Exception
	 */
	public EventDataRxRfState(AMUGeneralDataFrame gdf) throws Exception{

		try{
			byte[] rawData = gdf.getFp();
			decode(rawData);
			
		}catch(Exception e){
			log.error("EventFrame[Rx Rf State] Error : ", e);
			throw e;
		}
	}
	
	/**
	 * constructor
	 * 
	 * @param payLoadData
	 * @throws Exception
	 */
	public EventDataRxRfState(byte[] framePayLoad) throws Exception{
		try{
			decode(framePayLoad);
		}catch(Exception e){
			throw e;
		}
	}
	
	
	
	public byte getMiuType() {
		return miuType;
	}

	public void setMiuType(byte miuType) {
		this.miuType = miuType;
	}

	public byte[] getNodeID() {
		return nodeID;
	}

	public void setNodeID(byte[] nodeID) {
		this.nodeID = nodeID;
	}

	public byte getRfPower() {
		return rfPower;
	}

	public void setRfPower(byte rfPower) {
		this.rfPower = rfPower;
	}

	public byte getRecvRSSI() {
		return recvRSSI;
	}

	public void setRecvRSSI(byte recvRSSI) {
		this.recvRSSI = recvRSSI;
	}

	public byte getRecvLQI() {
		return recvLQI;
	}

	public void setRecvLQI(byte recvLQI) {
		this.recvLQI = recvLQI;
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
			
			/*
			this.miuType	= framePayLoad[pos];
			pos += AMUFramePayLoadConstants.FormatLength.EVENT_RX_RF_MIU_TYPE;
			
			this.nodeID		= new byte[AMUFramePayLoadConstants.FormatLength.EVENT_RX_RF_NODE_ID];
			System.arraycopy(framePayLoad, pos, nodeID, 0, AMUFramePayLoadConstants.FormatLength.EVENT_RX_RF_NODE_ID);
			pos += AMUFramePayLoadConstants.FormatLength.EVENT_RX_RF_NODE_ID;
			
			this.rfPower	= framePayLoad[pos];
			pos += AMUFramePayLoadConstants.FormatLength.EVENT_RX_RF_RF_POWER;
			
			this.recvRSSI	= framePayLoad[pos];
			pos += AMUFramePayLoadConstants.FormatLength.EVENT_RX_RF_RECEVIED_RSSI;
			
			this.recvLQI	= framePayLoad[pos];
			pos += AMUFramePayLoadConstants.FormatLength.EVENT_RX_RF_RECEVIED_LQI;
			*/
		}catch(Exception e){
			log.error("Rx Rf State Event Frame decode failed : ", e);
			throw e;
		}
	}
}