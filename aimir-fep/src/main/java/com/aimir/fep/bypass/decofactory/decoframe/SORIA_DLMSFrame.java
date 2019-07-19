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

import com.aimir.fep.bypass.decofactory.consts.DlmsConstants.XDLMS_APDU;
import com.aimir.fep.bypass.decofactory.consts.HdlcConstants.HdlcFrameType;
import com.aimir.fep.bypass.decofactory.protocolfactory.BypassFrameFactory.Procedure;
import com.aimir.fep.protocol.nip.client.multisession.MultiSession;

/**
 * @author simhanger
 *
 */
public class SORIA_DLMSFrame implements INestedFrame {

	@Override
	public byte[] encode(HdlcFrameType hdlcFrameType, XDLMS_APDU dlmsApdu, Procedure procedure, HashMap<String, Object> param, String command) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean decode(MultiSession session, byte[] rawFrame, Procedure procedure, String command) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object getResultData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setResultData(Object resultData) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setHDLCFrameType(HdlcFrameType hdlcFrameType) {
		// TODO Auto-generated method stub

	}

	@Override
	public HdlcFrameType getHDLCFrameType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDlmsApdu(XDLMS_APDU dlmsApdu) {
		// TODO Auto-generated method stub

	}

	@Override
	public XDLMS_APDU getDlmsApdu() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isHDLCSegmented() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setHDLCSegmented(boolean isHDLCSegmented) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setHDLCFrameLength(int length) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getHDLCFrameLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getMeterId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMeterId(String meterId) {
		// TODO Auto-generated method stub

	}

	@Override
	public String toByteString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMeterRSCount(int[] rsCount) {
		// TODO Auto-generated method stub

	}

	@Override
	public int[] getMeterRSCount() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object customDecode(Procedure procedure, byte[] data) {
		// TODO Auto-generated method stub
		return null;
	}

}
