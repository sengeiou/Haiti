/**
 * (@)# BypassGDFactory.java
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
package com.aimir.fep.bypass.decofactory.protocolfactory;

import java.util.HashMap;

import com.aimir.fep.protocol.nip.client.multisession.MultiSession;

/**
 * @author simhanger
 *
 */
public class BypassGDFactory extends BypassFrameFactory {

	@Override
	public BypassFrameResult receiveBypass(MultiSession session, byte[] rawFrame) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean start(MultiSession session, Object type) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setParam(HashMap<String, Object> params) {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop(MultiSession session) {
		// TODO Auto-generated method stub

	}

}
