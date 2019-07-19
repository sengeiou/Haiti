/**
 * (@)# ModemInfo.java
 *
 * 2015. 4. 29.
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
package com.aimir.fep.protocol.emnv.frame.payload;

import java.io.Serializable;

import com.aimir.constants.CommonConstants.ModemType;

/**
 * @author simhanger
 *
 */
public interface IModemInfo extends Serializable {
	public String getDeviceId();

	public void decode(byte[] data);

	public byte[] encode();

	public ModemType getModemType();
	
	public int getTotalLength();
}
