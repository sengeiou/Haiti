/**
 * (@)# SORIA_DLMSFrame.java
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
package com.aimir.fep.bypass.decofactory.decoframe;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.fep.bypass.decofactory.consts.DlmsConstants;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstants.XDLMS_APDU;
import com.aimir.fep.bypass.decofactory.consts.HdlcConstants.HdlcFrameType;
import com.aimir.fep.bypass.decofactory.protocolfactory.BypassFrameFactory.Procedure;
import com.aimir.fep.protocol.nip.client.multisession.MultiSession;
import com.aimir.fep.util.Hex;

/**
 * @author simhanger
 *
 */
public class IESCO_DLMSFrame implements INestedFrame {
	private static Logger logger = LoggerFactory.getLogger(IESCO_DLMSFrame.class);

	private byte[] gdDLMSFrame = null;
	private boolean isHDLCSegmented;
	private int hdlcFrameLength;
	private HdlcFrameType hdlcFrameType = HdlcFrameType.NULL;
	private XDLMS_APDU dlmsApdu = XDLMS_APDU.NULL;
	
	//private HdlcObjectType controlType;
	public Object resultData;
	private String meterId;
	private int[] meterRSCount = null;

	@Override
	//public byte[] encode(HdlcObjectType hdlcType, Procedure procedure, HashMap<String, Object> param, String command) {
	public byte[] encode(HdlcFrameType hdlcType, XDLMS_APDU dlmsApdu, Procedure procedure, HashMap<String, Object> param, String command) {
		logger.debug("## Excute IESCO_DLMSFrame Encoding [" + hdlcType.name() + "]");

		/*
		 * 추후 분기하여 처리 해야할 사항이 있을수도 있기때문에 분리해둠.
		 */
		if (hdlcType == HdlcFrameType.SNRM && dlmsApdu == DlmsConstants.XDLMS_APDU.NULL) {
			gdDLMSFrame = new byte[] {};
		} else if (hdlcType == HdlcFrameType.DISC && dlmsApdu == DlmsConstants.XDLMS_APDU.NULL) {
			gdDLMSFrame = new byte[] {};
		} else if (hdlcType == HdlcFrameType.I) {
			switch (dlmsApdu) {
			case AARQ:
				gdDLMSFrame = new byte[] {};
				break;
			case ACTION_REQUEST:
				gdDLMSFrame = new byte[] {};
				break;
			case GLO_ACTION_REQUEST:
				gdDLMSFrame = new byte[] {};
				break;
			default:
				break;
			}
		}
		return gdDLMSFrame;
	}

	@Override
	//public boolean decode(byte[] rawFrame, Procedure procedure, String command) {
	public boolean decode(MultiSession session, byte[] rawFrame, Procedure procedure, String command) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toByteString() {
		return Hex.decode(gdDLMSFrame);
	}

//	@Override
//	public int getType() {
//		return HdlcObjectType.getItem(controlType);
//	}
//
//	@Override
//	public void setType(int type) {
//		controlType = HdlcObjectType.getItem(type);
//	}

	@Override
	public Object getResultData() {
		return resultData;
	}

	@Override
	public void setResultData(Object resultData) {
		this.resultData = resultData;
	}

	@Override
	public Object customDecode(Procedure procedure, byte[] data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMeterId() {
		return meterId;
	}

	@Override
	public void setMeterId(String meterId) {
		this.meterId = meterId;
	}

	@Override
	public boolean isHDLCSegmented() {
		return isHDLCSegmented;
	}

	@Override
	public void setHDLCSegmented(boolean isHDLCSegmented) {
		this.isHDLCSegmented = isHDLCSegmented;
	}

	@Override
	public void setHDLCFrameLength(int length) {
		this.hdlcFrameLength = length;
	}

	@Override
	public int getHDLCFrameLength() {
		return hdlcFrameLength;
	}

	@Override
	public void setHDLCFrameType(HdlcFrameType hdlcFrameType) {
		this.hdlcFrameType = hdlcFrameType;
	}

	@Override
	public HdlcFrameType getHDLCFrameType() {
		return hdlcFrameType;
	}

	@Override
	public void setDlmsApdu(XDLMS_APDU dlmsApdu) {
		this.dlmsApdu = dlmsApdu;
	}

	@Override
	public XDLMS_APDU getDlmsApdu() {
		return dlmsApdu;
	}
	
	@Override
	public void setMeterRSCount(int[] rsCount) {
		if(meterRSCount == null){
			meterRSCount = rsCount;			
		}else{
			int[] temp = new int[4];
			temp[0] = (2 < meterRSCount.length ? meterRSCount[2] : 0);
			temp[1] = (2 < meterRSCount.length ? meterRSCount[3] : 0);
			temp[2] = rsCount[0];
			temp[3] = rsCount[1];
			
			meterRSCount = temp;
		}
	}

	@Override
	public int[] getMeterRSCount() {
		return meterRSCount;
	}
}
