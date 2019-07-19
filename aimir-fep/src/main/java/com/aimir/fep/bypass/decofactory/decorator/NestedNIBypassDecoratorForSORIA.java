/**
 * (@)# NestedNIDecorator.java
 *
 * 2016. 4. 15.
 *
 * Copyright (c) 2013 NURITELECOM, Inc.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of 
 * NURITELECOM, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with NURITELECOM, Inc.
 *
 * For more information on this product, please see
 * http://www.nuritelecom.co.kr
 *
 */
package com.aimir.fep.bypass.decofactory.decorator;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.constants.CommonConstants.McuType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstants;
import com.aimir.fep.bypass.decofactory.consts.HdlcConstants.HdlcFrameType;
import com.aimir.fep.bypass.decofactory.decoframe.INestedFrame;
import com.aimir.fep.bypass.decofactory.protocolfactory.BypassFrameFactory.Procedure;
import com.aimir.fep.protocol.fmp.common.Target;
import com.aimir.fep.protocol.nip.client.multisession.MultiSession;
import com.aimir.fep.protocol.nip.common.GeneralDataFrame;
import com.aimir.fep.protocol.nip.frame.GeneralFrame;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.FrameControl_Ack;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.FrameControl_Pending;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.FrameOption_AddressType;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.FrameOption_NetworkStatus;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.FrameOption_Type;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.NetworkType;
import com.aimir.fep.protocol.nip.frame.payload.Bypass;
import com.aimir.fep.protocol.nip.frame.payload.Bypass.TID_Type;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

/**
 * Decorator For NiBypass
 *
 */
public class NestedNIBypassDecoratorForSORIA extends NestFrameDecorator {
	private static Logger logger = LoggerFactory.getLogger(NestedNIBypassDecoratorForSORIA.class);
	private byte[] gdNIFrame = null;
	private Target  target = null;
	private NetworkType networkType = NetworkType.Sub1Ghz_SORIA;
	private byte[] sendTid = null;  // SP-722
	private byte[] receiveTid = null;// SP-722

	/**
	 * @param nestedFrame
	 */
	public NestedNIBypassDecoratorForSORIA(INestedFrame nestedFrame) {
		super(nestedFrame);
	}

	@Override
	//public boolean decode(byte[] frame, Procedure procedure, String command) {
	public boolean decode(MultiSession session, byte[] frame, Procedure procedure, String command) {
		
		// TODO Auto-generated method stub
		
		logger.info("## Excute NestedNIBypassDecoratorForSORIA Decoding...");
		boolean result = false;
		int pos = 0;
		logger.debug(Hex.decode((byte[])frame));
		try {      
	        GeneralFrame gframe = new GeneralFrame();
	        gframe.decode((byte[])frame);
	        if ( gframe.foType != FrameOption_Type.Bypass ){
	        	logger.error("gframe foType is not Bypass");
	        	throw new Exception("gframe foType is not Bypass");
	        }
	        Bypass bpass = (Bypass)gframe.getPayload();
	        receiveTid =  bpass.getFrameTid(); // SP-722
			//result  = super.decode(bpass.getPayload(), procedure, command);
	        result  = super.decode(session, bpass.getPayload(), procedure, command);
		}
		catch (Exception e) {
			logger.error("NI Encoding Error - {}", e);
		}	
		return result;

	}

	@Override
	//public byte[] encode(HdlcObjectType hdlcType, Procedure procedure, HashMap<String, Object> param, String command) {
	public byte[] encode(HdlcFrameType hdlcType, DlmsConstants.XDLMS_APDU dlmsApdu, Procedure procedure, HashMap<String, Object> param, String command) {
		// TODO Auto-generated method stub
		logger.info("## Excute NestedNIBypassDecoratorForSORIA Encoding [{}]", hdlcType.name());
//		logger.info("## Excute NestedNIBypassDecoratorForSORIA Encoding params = [{}]", param.toString());
		logger.info("## Excute NestedNIBypassDecoratorForSORIA Encoding params = [{}]", param == null ? "null" : param.toString());

		// if re-sending frame(with same TID), return gdNIFrame  SP-722 
		if ( param != null && param.get("niRetry")!= null){
			int retry = (int)param.get("niRetry");
			if ( retry > 0){
				logger.info("Retry Sending, so return before sended frame:{}", Hex.decode(gdNIFrame));
				return gdNIFrame;
			}
		}
		
		try {
			//if ( hdlcType == HdlcObjectType.SNRM ){
			if ( hdlcType == HdlcFrameType.SNRM ){
				target  = (Target)param.get("target");
				if ( target != null ){
					if (target.getReceiverType().equals("MMIU") || target.getTargetType() == McuType.MMIU) {
						if (target.getProtocol() == Protocol.SMS) { // MBB Modem / TCP
							networkType = NetworkType.MBB;
						} else if (target.getProtocol() == Protocol.IP) { // Ethernet Modem / TCP
							networkType = NetworkType.Ethernet;							
						} else if(target.getProtocol() == Protocol.GPRS) {
							networkType = NetworkType.MBB; //mbb tcp mode
						}						
					} else if (target.getReceiverType().equals("SubGiga")) {
						if (target.getProtocol() == Protocol.IP) { // RF Modem / UDP
							networkType = NetworkType.Sub1Ghz_SORIA;
						} else {
							throw new Exception("Unknown Protocol type.");
						}
					}		
					logger.debug("target type [" + target.getTargetType().name() + "]");
					logger.debug("protocol [" + target.getProtocol().name() + "]");
					logger.debug("networkType [" + networkType.name() + "]");
				}
				else {
					logger.debug("target parameter is null.");				
				}
			}			
		} catch (Exception e) {
			logger.error("NI Encoding Error - {}", e);
			gdNIFrame = null;
			return gdNIFrame;
		}	
		

		
		try{
			gdNIFrame = new byte[] {};
			/**
			 * FRAME TYPE & LENGTH
			 */
			/**
			 * HDLC FRAME Encoding
			 */
			//byte[] hdlcEncodeArray = super.encode(hdlcType, procedure, param, command);
			byte[] hdlcEncodeArray = super.encode(hdlcType, dlmsApdu, procedure, param, command);


			// Make GeneralFrame TYPE=Pypass
			GeneralFrame generalFrame = new GeneralFrame();
			generalFrame.setFrameOption_Type(FrameOption_Type.Bypass);
			generalFrame.setFcPending(FrameControl_Pending.LastFrame);
			generalFrame.setFcAck(FrameControl_Ack.None);
			generalFrame.setFoNetworkStatus(FrameOption_NetworkStatus.None);
			generalFrame.setFoAddrType(FrameOption_AddressType.None);
			generalFrame.setSeqNumber(new byte[] { DataUtil.getByteToInt(0) });
			generalFrame.setNetworkType(networkType);


			HashMap<String, Object> gfparam = new HashMap<String,Object>();

			//gfparam.put("tidType", TID_Type.Enable);
			/*
			 * 임시로 disable 처리해둠 추후 enable로 바꾸고 tid증가시키는 로직과, 응답미수신시 재전송하는 로직 추가해서 넣을것. 
			 * 그렇게 하면 rf모뎀쪽에서는 hes로부터 수신된 동일한 tid에 대해서 먼저발송했던 응답을 재전송해주는 로직으로 동작하게됨.
			 */
			// UPDATE START SP-722
			//gfparam.put("tidType", TID_Type.Disable);  
			//gfparam.put("tidLocation", DataUtil.getByteToInt(0));
			//gfparam.put("tranId", DataUtil.getByteToInt(0));
			TID_Type tidType = TID_Type.Disable;
			byte tidLocation = 0;
			byte tranId  = 0;
			if ( param != null ){
				if ( param.get("niTidType") != null){
					tidType = (TID_Type)param.get("niTidType");
				}
				if ( param.get("niTidLocation") != null ){
					tidLocation = DataUtil.getByteToInt((int)param.get("niTidLocation"));
				}
				if ( param.get("niTransId") != null ){
					tranId = DataUtil.getByteToInt((int)param.get("niTransId"));
				}
			}
			gfparam.put("tidType", tidType);
			gfparam.put("tidLocation", tidLocation);
			gfparam.put("tranId", tranId);
			// UPDATE END SP-722
			gfparam.put("payload", hdlcEncodeArray);

			gdNIFrame = new GeneralDataFrame().make(generalFrame, gfparam);
			// INSERT START  SP-722
			if ( sendTid == null ) 
				sendTid = new byte[1];
			sendTid[0] = (byte)( tidType.getCode() | tidLocation | tranId );
			// INSERT END SP-722
		} catch (Exception e) {
			logger.error("NI Encoding Error - {}", e);
			gdNIFrame = null;
		}	
		return gdNIFrame;
	}

	@Override
	public Object getResultData() {
		// TODO Auto-generated method stub
		return super.getResultData();
	}

//	@Override
//	public int getType() {
//		// TODO Auto-generated method stub
//		return super.getType();
//	}

	@Override
	public void setResultData(Object resultData) {
		// TODO Auto-generated method stub
		super.setResultData(resultData);
	}

//	@Override
//	public void setType(int type) {
//		// TODO Auto-generated method stub
//		super.setType(type);
//	}

	@Override
	public String toByteString() {
		// TODO Auto-generated method stub
		return super.toByteString();
	}

	@Override
	public Object customDecode(Procedure procedure, byte[] data) {
		return super.customDecode(procedure, data);
	}
	
	/**
	 * @return the sendTid
	 */
	public byte[] getSendTid() {
		return sendTid;
	}

	/**
	 * @param sendTid the sendTid to set
	 */
	public void setSendTid(byte[] sendTid) {
		this.sendTid = sendTid;
	}
	/**
	 * @return the receiveTid
	 */
	public byte[] getReceiveTid() {
		return receiveTid;
	}

	/**
	 * @param receiveTid the receiveTid to set
	 */
	public void setReceiveTid(byte[] receiveTid) {
		this.receiveTid = receiveTid;
	}
	/**
	 * SP-737
	 * @return the hdlcRRFrame
	 */
	public boolean isHdlcRRFrame() {
		boolean  isHdlcRRFrame = false;
		
		if ( nestedFrame != null && nestedFrame instanceof NestedHDLCDecoratorForSORIA ){
			NestedHDLCDecoratorForSORIA hdlcFrame = (NestedHDLCDecoratorForSORIA)nestedFrame;
			//if ( hdlcFrame.isRrFrame())
			if(hdlcFrame.getHDLCFrameType() == HdlcFrameType.RR)
				isHdlcRRFrame = true;	
		}
		return isHdlcRRFrame;
	}

}
