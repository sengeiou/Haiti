/**
 * (@)# EMnVAckNackFrame.java
 *
 * 2015. 4. 23.
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

import com.aimir.fep.util.DataUtil;

/**
 * @author simhanger
 *
 */
//public class EMnVAckNackFramePayLoad implements EMnVGeneralFramePayLoad {
public class EMnVAckNackFramePayLoad extends EMnVGeneralFramePayLoad {
	private static final long serialVersionUID = 1L;
	private EMnVAckNackType type;
	private byte[] payload_data;

	public enum EMnVAckNackType {
		ACK(new byte[] { (byte) 0x00 }, "0", 1), // success
		NACK(new byte[] { (byte) 0x01 }, "1", 1), // fail
		NAU(new byte[] { (byte) 0x02 }, "2", 1), // Not Access User
		UNKNOWN(new byte[] { (byte) 0x55, (byte) 0x4E, (byte) 0x4B }, "UNK", 3);

		private byte[] data;
		private String name;
		private int length;

		private EMnVAckNackType(byte[] data, String name, int length) {
			this.data = data;
			this.name = name;
			this.length = length;
		}

		public byte[] getData() {
			return data;
		}

		public String getString() {
			return name;
		}

		public int getValue() {
			return length;
		}

		public static EMnVAckNackType getItem(byte[] value) {
			for (EMnVAckNackType fc : EMnVAckNackType.values()) {
				if (fc.data[0] == value[0]) {
					return fc;
				}
			}
			return UNKNOWN;
		}
	}

	public EMnVAckNackFramePayLoad(EMnVAckNackType type) {
		this.type = type;
	}

	public EMnVAckNackFramePayLoad(byte[] payload_byte) throws Exception {
		this.payload_data = payload_byte;
		decode();
	}

	public EMnVAckNackType getType() {
		return type;
	}

	public void setType(EMnVAckNackType type) {
		this.type = type;
	}

	public byte[] getPayload_data() {
		return payload_data;
	}

	public void setPayload_data(byte[] payload_data) {
		this.payload_data = payload_data;
	}

	@Override
	public void decode() throws Exception {
		this.type = EMnVAckNackType.getItem(payload_data);
	}

	@Override
	public byte[] encode() throws Exception {
		return type.data;
	}

	@Override
	public boolean isValidation(Object obj) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

}
