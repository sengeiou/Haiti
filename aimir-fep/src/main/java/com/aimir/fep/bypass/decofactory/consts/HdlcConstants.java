/**
 * (@)# HdlcConstants.java
 *
 * 2016. 4. 12.
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
package com.aimir.fep.bypass.decofactory.consts;

import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.fep.util.DataUtil;

/**
 * @author simhanger
 *
 */
public class HdlcConstants {
	private static Logger logger = LoggerFactory.getLogger(HdlcConstants.class);

//	public enum HdlcObjectType {
//		  SNRM(0x93), UA(0x73), AARQ(0x60), AARE(0x61), GET_REQ(0xC0), SET_REQ(0xC1), ACTION_REQ(0xC3)
//		, GET_RES(0xC4), SET_RES(0xC5), ACTION_RES(0xC7), DISC(0x53), KAIFA_CUSTOM(0xFF), UNKNOWN(0x00);
//
//		private byte type;
//
//		private HdlcObjectType(int type) {
//			this.type = (byte) type;
//		}
//
//		public byte getBytes() {
//			return type;
//		}
//
//		public static HdlcObjectType getItem(byte value) {
//			for (HdlcObjectType fc : HdlcObjectType.values()) {
//				if (fc.type == value) {
//					return fc;
//				}
//			}
//			return UNKNOWN;
//		}
//
//		public static HdlcObjectType getItem(int value) {
//			for (HdlcObjectType fc : HdlcObjectType.values()) {
//				if (fc.type == DataUtil.getByteToInt(value)) {
//					return fc;
//				}
//			}
//			return UNKNOWN;
//		}
//
//		public static int getItem(HdlcObjectType value) {
//			for (HdlcObjectType fc : HdlcObjectType.values()) {
//				if (fc.type == value.type) {
//					return DataUtil.getIntToByte(fc.getBytes());
//				}
//			}
//			return 0;
//		}
//	}
	
	public enum HdlcFrameType {
		I(0x10), RR(0x01), RNR(0x05), SNRM(0x93), DISC(0x53), UA(0x73), DM(0x1F), FRMR(0x97), UI(0x13), NULL(0xFF);
		//, AARQ(0x60), AARE(0x61), GET_REQ(0xC0), SET_REQ(0xC1), ACTION_REQ(0xC3), GET_RES(0xC4), SET_RES(0xC5), ACTION_RES(0xC7), KAIFA_CUSTOM(0xFF), UNKNOWN(0x00);
		
		private byte value;

		private HdlcFrameType(int type) {
			this.value = (byte) type;
		}

		public byte getValue() {
			return value;
		}

		public static HdlcFrameType getItem(byte controlField) {

			String bs = String.format("%08d", new BigInteger(Integer.toBinaryString(DataUtil.getIntToByte(controlField))));
			if (bs.substring(7, 8).equals("0")) { // I : Infromation
				return I;
			} else if (bs.substring(4, 8).equals("0001")) { // RR : Receive Ready
				return RR;
			} else if (bs.substring(4, 8).equals("0101")) { // RNR : Receive Not Ready
				return RNR;
			} else if (bs.substring(0, 3).equals("100") && bs.substring(4, 8).equals("0011")) { // SNRM : Set Normal Response Mode
				return SNRM;
			} else if (bs.substring(0, 3).equals("010") && bs.substring(4, 8).equals("0011")) { // DISC : Disconnect
				return DISC;
			} else if (bs.substring(0, 3).equals("011") && bs.substring(4, 8).equals("0011")) { // UA : Unnumbered Ack
				return UA;
			} else if (bs.substring(0, 3).equals("000") && bs.substring(4, 8).equals("1111")) { // DM : Disconnect Mode
				return DM;
			} else if (bs.substring(0, 3).equals("100") && bs.substring(4, 8).equals("0111")) { // FRMR : Frame Reject
				return FRMR;
			} else if (bs.substring(0, 3).equals("000") && bs.substring(4, 8).equals("0011")) { // UI : Unnumbered Information
				return UI;
			} else { // Unknown
				return NULL;
			}
		}
	}

	public enum HdlcAddressType {
		DEST, SRC, KAIFA_CUSTOM_SRC;
	}

	public static int[] getRSCount(byte control){
		int[] result = null;
		
		if(control != 0){
			result = new int[2];
			String binaryString = String.format("%08d", new BigInteger(Integer.toBinaryString(DataUtil.getIntToByte(control))));
			result[0] = Integer.parseInt(binaryString.substring(0, 3), 2);
			result[1] = Integer.parseInt(binaryString.substring(4, 7), 2);
			
			//logger.debug("RECEIVE:SEND COOUNT => [{}]{}:{}", Hex.decode(new byte[]{control}), result[0], result[1]);
		}
		
		return result;
	}

	public static byte getRSCount(int receciveCount, int sendCount) {
		String binaryStringA = String.format("%03d", new BigInteger(Integer.toBinaryString(receciveCount))) + "1";
		String binaryStringB = String.format("%03d", new BigInteger(Integer.toBinaryString(sendCount))) + "0";
		
		byte result = DataUtil.getByteToInt(Integer.parseInt(binaryStringA + binaryStringB, 2));
		//logger.debug("RECEIVE:SEND COOUNT => [{}]{}:{}", Hex.decode(new byte[]{result}), binaryStringA, binaryStringB);
		 
		return result;
	}
	
	public static byte getRRCount(int receciveCount, int sendCount) {
		String binaryStringA = String.format("%03d", new BigInteger(Integer.toBinaryString(receciveCount))) + "1";
		String binaryStringB = String.format("%03d", new BigInteger(Integer.toBinaryString(sendCount))) + "1";
		byte result = DataUtil.getByteToInt(Integer.parseInt(binaryStringA + binaryStringB, 2));

		return result;
	}
	
}
