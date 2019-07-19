/**
 * (@)# NestFrameDecorator.java
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

import com.aimir.fep.bypass.decofactory.consts.DlmsConstants;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstants.XDLMS_APDU;
import com.aimir.fep.bypass.decofactory.consts.HdlcConstants.HdlcFrameType;
import com.aimir.fep.bypass.decofactory.decoframe.INestedFrame;
import com.aimir.fep.bypass.decofactory.protocolfactory.BypassFrameFactory.Procedure;
import com.aimir.fep.protocol.nip.client.multisession.MultiSession;

/**
 * @author simhanger
 *
 */
public abstract class NestFrameDecorator implements INestedFrame {
	protected INestedFrame nestedFrame;

	public NestFrameDecorator(INestedFrame nestedFrame) {
		this.nestedFrame = nestedFrame;
	}

//	@Override
//	public byte[] encode(HdlcObjectType hdlcType, Procedure procedure, HashMap<String, Object> param, String command) {
//		return nestedFrame.encode(hdlcType, procedure, param, command);
//	}
	
	@Override
	public byte[] encode(HdlcFrameType hdlcType, XDLMS_APDU dlmsApdu, Procedure procedure, HashMap<String, Object> param, String command) {
		return nestedFrame.encode(hdlcType, dlmsApdu, procedure, param, command);
	}

//	@Override
//	public boolean decode(byte[] frame, Procedure procedure, String command) {
//		return nestedFrame.decode(frame, procedure, command);
//	}

	@Override
	public boolean decode(MultiSession session, byte[] frame, Procedure procedure, String command) {
		return nestedFrame.decode(session, frame, procedure, command);
	}
	

	@Override
	public Object getResultData() {
		return nestedFrame.getResultData();
	}

	@Override
	public void setResultData(Object resultData) {
		nestedFrame.setResultData(resultData);
	}

	@Override
	public void setHDLCFrameLength(int length) {
		nestedFrame.setHDLCFrameLength(length);
	}

	@Override
	public int getHDLCFrameLength() {
		return nestedFrame.getHDLCFrameLength();
	}

	@Override
	public String toByteString() {
		return nestedFrame.toByteString();
	}

	@Override
	public void setHDLCFrameType(HdlcFrameType hdlcFrameType) {
		nestedFrame.setHDLCFrameType(hdlcFrameType);
	}

	@Override
	public HdlcFrameType getHDLCFrameType() {
		return nestedFrame.getHDLCFrameType();
	}

	@Override
	public void setDlmsApdu(DlmsConstants.XDLMS_APDU dlmsApdu) {
		nestedFrame.setDlmsApdu(dlmsApdu);
	}

	@Override
	public XDLMS_APDU getDlmsApdu() {
		return nestedFrame.getDlmsApdu();
	}

	@Override
	public boolean isHDLCSegmented() {
		return nestedFrame.isHDLCSegmented();
	}

	@Override
	public void setHDLCSegmented(boolean isHDLCSegmented) {
		nestedFrame.setHDLCSegmented(isHDLCSegmented);
	}

	@Override
	public String getMeterId() {
		return nestedFrame.getMeterId();
	}

	@Override
	public void setMeterId(String meterId) {
		nestedFrame.setMeterId(meterId);
	}

	@Override
	public Object customDecode(Procedure procedure, byte[] data) {
		return nestedFrame.customDecode(procedure, data);
	}	
	
//	@Override
//	public int getType() {
//		return nestedFrame.getType();
//	}
//
//	@Override
//	public void setType(int type) {
//		nestedFrame.setType(type);
//	}
//	
//	
	@Override
	public int[] getMeterRSCount() {
		return nestedFrame.getMeterRSCount();
	}
	
	@Override
	public void setMeterRSCount(int[] rsCount) {
		nestedFrame.setMeterRSCount(rsCount);
	}
}
