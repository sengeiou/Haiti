/**
 * (@)# EMnVGeneralDataFrame.java
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
package com.aimir.fep.protocol.emnv.frame;

import java.math.BigInteger;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.fep.protocol.emnv.exception.EMnVSystemException;
import com.aimir.fep.protocol.emnv.exception.EMnVSystemException.EMnVExceptionReason;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants.FrameControlAddr;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants.FrameControlSecurity;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants.FrameType;
import com.aimir.fep.protocol.emnv.frame.payload.EMnVAckNackFramePayLoad;
import com.aimir.fep.protocol.emnv.frame.payload.EMnVCommandFramePayLoad;
import com.aimir.fep.protocol.emnv.frame.payload.EMnVGeneralFramePayLoad;
import com.aimir.fep.protocol.emnv.frame.payload.EMnVLinkFramePayLoad;
import com.aimir.fep.protocol.emnv.frame.payload.EMnVMeteringDataFramePayLoad;
import com.aimir.fep.protocol.emnv.frame.payload.EMnVOTAFramePayLoad;
import com.aimir.fep.protocol.emnv.frame.payload.EMnVSubgigaInterfaceFramePayLoad;
import com.aimir.fep.protocol.emnv.frame.payload.EMnVZigbeeInterfaceFramePayLoad;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

/**
 * @author simhanger
 *
 */
public class EMnVGeneralDataFrame {
	private static Logger log = LoggerFactory.getLogger(EMnVGeneralDataFrame.class);

	private EMnVConstants.General SOF;
	private FrameType frameType;
	private EMnVAuth auth;
	private EMnVFrameControl vFrameControl;

	private byte[] dest_addr_byte;
	private String destAddress;
	private byte[] source_addr_byte;
	private String sourceAddress;

	private int sequence;
	private int payloadLength;
	private EMnVGeneralFramePayLoad generalFramePayLoad;
	private EMnVCRC32 crc32;

	public EMnVConstants.General getSOF() {
		return SOF;
	}

	public void setSOF(EMnVConstants.General sOF) {
		SOF = sOF;
	}

	public FrameType getFrameType() {
		return frameType;
	}

	public void setFrameType(FrameType frameType) {
		this.frameType = frameType;
	}

	public EMnVAuth getAuth() {
		return auth;
	}

	public void setAuth(EMnVAuth auth) {
		this.auth = auth;
	}

	public EMnVFrameControl getvFrameControl() {
		return vFrameControl;
	}

	public void setvFrameControl(EMnVFrameControl vFrameControl) {
		this.vFrameControl = vFrameControl;
	}

	public String getDestAddress() {
		return destAddress;
	}

	public void setDestAddress(String destAddress) {
		this.destAddress = destAddress;
	}

	public String getSourceAddress() {
		return sourceAddress;
	}

	public void setSourceAddress(String sourceAddress) {
		this.sourceAddress = sourceAddress;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public int getPayloadLength() {
		return payloadLength;
	}

	public void setPayloadLength(int payloadLength) {
		this.payloadLength = payloadLength;
	}

	public byte[] getDest_addr_byte() {
		return dest_addr_byte;
	}

	public void setDest_addr_byte(byte[] dest_addr_byte) {
		this.dest_addr_byte = dest_addr_byte;
	}

	public byte[] getSource_addr_byte() {
		return source_addr_byte;
	}

	public void setSource_addr_byte(byte[] source_addr_byte) {
		this.source_addr_byte = source_addr_byte;
	}

	public EMnVGeneralFramePayLoad getGeneralFramePayLoad() {
		return generalFramePayLoad;
	}

	public void setGeneralFramePayLoad(EMnVGeneralFramePayLoad generalFramePayLoad) {
		this.generalFramePayLoad = generalFramePayLoad;
	}

	public boolean checkCRC32() {
		boolean result = false;

		if (crc32 == null) {
			log.error("ERROR - CRC32 체크 오류");
		} else {
			result = crc32.check();
		}

		return result;
	}
	
	public void decode(IoBuffer bytebuffer) throws Exception {
		bytebuffer.rewind();

		int startPos = bytebuffer.position();
		//EMnVAbstractGeneralDataFrame frame = null;
		/*
		 * START FLAG - 3Byte
		 */
		byte[] sof_byte = new byte[EMnVConstants.SOF_LEN];
		bytebuffer.get(sof_byte, 0, EMnVConstants.SOF_LEN);
		SOF = EMnVConstants.General.getItem(DataUtil.getString(sof_byte));

		/*
		 * AUTH - 15Byte
		 */
		byte[] auth_byte = new byte[EMnVConstants.AUTH_LEN];
		bytebuffer.get(auth_byte, 0, EMnVConstants.AUTH_LEN);
		auth = new EMnVAuth(auth_byte);
		log.info("[PROTOCOL][GENERAL_FRAME_FORMAT] AUTH(=IMEI)(15byte):[{}] ==> HEX=[{}]", "", Hex.decode(auth_byte));
		
		/*
		 * FRAME TYPE  - 1Byte
		 */
		byte[] frameType_byte = new byte[EMnVConstants.FRAME_TYPE_LEN];
		bytebuffer.get(frameType_byte, 0, EMnVConstants.FRAME_TYPE_LEN);
		frameType = FrameType.getItem(DataUtil.getIntToByte(frameType_byte[0]));
		log.info("[PROTOCOL][GENERAL_FRAME_FORMAT] FRAME_TYPE(1byte):[{}] ==> HEX=[{}]", frameType.name(), Hex.decode(frameType_byte));

		/*
		 * FRAME CONTROL - 2Byte
		 */
		byte[] frameControl_byte = new byte[EMnVConstants.FRAME_CONTROL_LEN];
		bytebuffer.get(frameControl_byte, 0, EMnVConstants.FRAME_CONTROL_LEN);
		vFrameControl = new EMnVFrameControl(frameControl_byte);
		log.info("[PROTOCOL][GENERAL_FRAME_FORMAT] FRAME_CONTROL(2byte)]: DST_ADDR_TYPE={}, SRC_ADDR_TYPE={}"
				+ ", SECURITY_ENABLE={}, ACK_REQ_ENABLE={}  | Values=[{}]"
				, new Object[]{vFrameControl.getDST_ADDR_TYPE().name(), vFrameControl.getSRC_ADDR_TYPE().name()
						, vFrameControl.getSECURITY_ENABLE().name(), vFrameControl.getACK_REQ_ENABLE().name(), Hex.decode(frameControl_byte)});
		
		/**
		 * 
		 * 
		 * SECURITY_DISABLE 모드로만 운영하려면 아래코드 주석 해제 할것.
		 * 
		 */
		/**
  		if(vFrameControl.getSECURITY_ENABLE() == FrameControlSecurity.SECURITY_DISABLE){
			log.warn("#### Please Check Modems Encription Mode ###");
			log.warn("#### Please Check Modems Encription Mode ###");
			log.warn("#### Please Check Modems Encription Mode ###");
			throw new EMnVSystemException(EMnVExceptionReason.INVALID_SECURITY_MODE);
		}
		*/
		
		
		/*
		 * DESTINATION ADDRESS - 0/4/8/16Byte
		 */
		switch (vFrameControl.getDST_ADDR_TYPE()) {
		case NON_ADDRES:
			destAddress = null;
			break;
		case IPv4:
			dest_addr_byte = new byte[FrameControlAddr.IPv4.getLength()];
			bytebuffer.get(dest_addr_byte, 0, FrameControlAddr.IPv4.getLength());
			destAddress = DataUtil.decodeIpAddr(dest_addr_byte);
			break;
		case IPv6:
			dest_addr_byte = new byte[FrameControlAddr.IPv6.getLength()];
			bytebuffer.get(dest_addr_byte, 0, FrameControlAddr.IPv6.getLength());

			destAddress = DataUtil.decodeIPv6Addr(dest_addr_byte);
			break;
		case MOBILE_NUMBER:
			dest_addr_byte = new byte[FrameControlAddr.MOBILE_NUMBER.getLength()];
			bytebuffer.get(dest_addr_byte, 0, FrameControlAddr.MOBILE_NUMBER.getLength());

			destAddress = DataUtil.getString(dest_addr_byte);
			break;
		case EUI64:
			dest_addr_byte = new byte[FrameControlAddr.EUI64.getLength()];
			bytebuffer.get(dest_addr_byte, 0, FrameControlAddr.EUI64.getLength());

			destAddress = String.valueOf(DataUtil.getLongTo8Byte(dest_addr_byte));
			break;
		default:
			throw new EMnVSystemException(EMnVExceptionReason.UNKNOWN_ADDR);
		}

		log.info("[PROTOCOL][GENERAL_FRAME_FORMAT] DESTINATION_ADDRESS({}byte):[{}] ==> HEX=[{}]", new Object[] { vFrameControl.getDST_ADDR_TYPE().getLength(), destAddress, Hex.decode(dest_addr_byte) });

		/*
		 * SOURCE ADDRESS - 0/4/8/16Byte
		 */
		switch (vFrameControl.getSRC_ADDR_TYPE()) {
		case NON_ADDRES:
			sourceAddress = null;
			break;
		case IPv4:
			source_addr_byte = new byte[FrameControlAddr.IPv4.getLength()];
			bytebuffer.get(source_addr_byte, 0, FrameControlAddr.IPv4.getLength());

			sourceAddress = DataUtil.decodeIpAddr(source_addr_byte).trim();
			break;
		case IPv6: // SUB-GIGA
			source_addr_byte = new byte[FrameControlAddr.IPv6.getLength()];
			bytebuffer.get(source_addr_byte, 0, FrameControlAddr.IPv6.getLength());

			sourceAddress = DataUtil.decodeIPv6Addr(source_addr_byte).trim();
			break;
		case MOBILE_NUMBER:
			source_addr_byte = new byte[FrameControlAddr.MOBILE_NUMBER.getLength()];
			bytebuffer.get(source_addr_byte, 0, FrameControlAddr.MOBILE_NUMBER.getLength());

			byte[] temp = new byte[11];
			System.arraycopy(source_addr_byte, 5, temp, 0, temp.length);
			sourceAddress = DataUtil.getString(temp).trim();
			//sourceAddress = DataUtil.getString(addr_byte).substring(5, 16);  //상위5개바이트는 0으로 채움				
			break;
		case EUI64: // ZIG-BEE
			source_addr_byte = new byte[FrameControlAddr.EUI64.getLength()];
			bytebuffer.get(source_addr_byte, 0, FrameControlAddr.EUI64.getLength());

			sourceAddress = String.valueOf(DataUtil.getLongTo8Byte(source_addr_byte)).trim();
			break;
		default:
			throw new EMnVSystemException(EMnVExceptionReason.UNKNOWN_ADDR);
		}
		
		log.info("[PROTOCOL][GENERAL_FRAME_FORMAT] SOURCE_ADDRESS({}byte):[{}] ==> HEX=[{}]", new Object[] { vFrameControl.getDST_ADDR_TYPE().getLength(), sourceAddress, Hex.decode(source_addr_byte) });

		/*
		 * SEQUENCE - 1byte
		 */
		byte[] sequence_byte = new byte[EMnVConstants.SEQUENCE_LEN];
		bytebuffer.get(sequence_byte, 0, EMnVConstants.SEQUENCE_LEN);
		sequence = DataUtil.getIntToByte(sequence_byte[0]);
		log.info("[PROTOCOL][GENERAL_FRAME_FORMAT] SEQUENCE(1byte):[{}] ==> HEX=[{}]", sequence, Hex.decode(sequence_byte));

		/*
		 * LENGTH - 4byte
		 */
		byte[] lenth_byte = new byte[EMnVConstants.LENGTH_LEN];
		bytebuffer.get(lenth_byte, 0, EMnVConstants.LENGTH_LEN);

		/**
		 * 보안모드 사용하지 않을경우
		 *  : 4byte 전체가 data(payload)의 총길이를 표현
		 * 보안모드 사용할경우
		 *  : 상위 4bit는 padding크기, 하위 28bit는 payload 길이를 표현
		 *    padding바이트의 크기는 1~16byte 사이. 만일 padding byte의 길이가 16byte이면 0으로 세팅   
		 */
		if (vFrameControl.getSECURITY_ENABLE() == FrameControlSecurity.SECURITY_DISABLE) { // 보안모드 사용여부
			payloadLength = DataUtil.getIntTo4Byte(lenth_byte);
		} else {
			int paddingLength = (lenth_byte[0] & 0xf0) >> 4; // 상위 4bit는 패딩바이트 길이 
			if (paddingLength == 0) { // 패딩바이트가 16byte일경우 패딩비트를 0으로 세팅하므로 길이구할때 이렇게 처리해야함.
				paddingLength = 16;
			}

			lenth_byte[0] = (byte) (lenth_byte[0] & 0x0f);
			payloadLength = DataUtil.getIntTo4Byte(lenth_byte);
			payloadLength = paddingLength + payloadLength; // 암호화된 payload의 길이 = padding 길이 + 암호화된 payload의 길이
		}
		log.info("[PROTOCOL][GENERAL_FRAME_FORMAT] 보안모드 :[{}]", vFrameControl.getSECURITY_ENABLE());
		log.info("[PROTOCOL][GENERAL_FRAME_FORMAT] LENGTH(4byte):[{}] ==> HEX=[{}]", payloadLength, Hex.decode(lenth_byte));

		/*
		 * DATA - PAYLOAD -Nbyte
		 */
		byte[] payload_byte = new byte[payloadLength];
		bytebuffer.get(payload_byte, 0, payloadLength);
		
		/*
		 * Payload Data 복호화.
		 */
		if(vFrameControl.getSECURITY_ENABLE() == FrameControlSecurity.SECURITY_ENABLE){
			payload_byte = auth.doDecryption(payload_byte, source_addr_byte);			
		}
		
		switch (frameType) {
		case LINK_FRAME:
			generalFramePayLoad = new EMnVLinkFramePayLoad(payload_byte);
			break;
		case ACK_NAK_FRAME:
			generalFramePayLoad = new EMnVAckNackFramePayLoad(payload_byte);
			break;
		case COMMAND_FRAME:
			generalFramePayLoad = new EMnVCommandFramePayLoad(payload_byte);
			break;
		case METERING_DATA_FRAME:
			generalFramePayLoad = new EMnVMeteringDataFramePayLoad(payload_byte); // decoding 하지 않고 데이터만 저장.
			break;
		case OTA_FRAME:
			generalFramePayLoad = new EMnVOTAFramePayLoad(payload_byte);
			break;
		case ZIGBEE_INTERFACE_FRAME:
			generalFramePayLoad = new EMnVZigbeeInterfaceFramePayLoad(payload_byte);
			break;
		case SUBGIGA_INTERFACE_FRAME:
			generalFramePayLoad = new EMnVSubgigaInterfaceFramePayLoad(payload_byte);
			break;
		default:
			throw new EMnVSystemException(EMnVExceptionReason.UNKNOWN_PAYLOAD_FRAME);
		}
		log.info("[PROTOCOL][GENERAL_FRAME_FORMAT] PAYLOAD DATA({}byte):[{}] ==> HEX=[{}]", "N", payload_byte.length, Hex.decode(payload_byte));

		// IMEI값 설정
		generalFramePayLoad.setModemImei(auth.getModemImei());
		
		/*
		 * CRC32 - FOOTER 4byte
		 */
		int nowPos = bytebuffer.position();

		byte[] crc32_byte = new byte[EMnVConstants.CRC32_LEN];
		bytebuffer.get(crc32_byte, 0, EMnVConstants.CRC32_LEN);
		crc32 = new EMnVCRC32();
		crc32.setCrcByte(crc32_byte, nowPos, bytebuffer);

		log.info("[PROTOCOL][GENERAL_FRAME_FORMAT] CRC32(4byte):[{}] ==> {}", "", Hex.decode(crc32_byte));

//		log.info("EMnVGeneralDataFram DECODE ==> SOF={} / AUTH={} / FRAME_TYPE={} " + "/ DST_ADDR_TYPE={} / SRC_ADDR_TYPE={} / SECURITY_ENABLE={} / ACK_REQ_ENABLE={} " + "/ SEQUENCT={} / PAY_LOAD_LENGTH={} / CRC={}",
//				new Object[] { SOF, auth.toString(), frameType, destAddress, sourceAddress, vFrameControl.getSECURITY_ENABLE(), vFrameControl.getACK_REQ_ENABLE(), sequence, payloadLength, Hex.decode(crc32_byte) });

	}

	public byte[] encode() throws Exception{
		byte[] bx = EMnVConstants.General.getItem("KEP").getData(); // 1
		bx = DataUtil.append(bx, auth.getAuth_byte()); // 15   
		bx = DataUtil.append(bx, new byte[]{frameType.getData()} ); // 1
		bx = DataUtil.append(bx, vFrameControl.encode());  // 2
		bx = DataUtil.append(bx, dest_addr_byte);  // 0, 4, 8, 16
		bx = DataUtil.append(bx, source_addr_byte); // 0, 4, 8, 16
		bx = DataUtil.append(bx, new byte[]{DataUtil.getByteToInt(sequence)}); // 1

		byte[] payload = generalFramePayLoad.encode();
		int orgPayloadLenth = payload.length;
		
		
		if (vFrameControl.getSECURITY_ENABLE() == FrameControlSecurity.SECURITY_DISABLE) { // 보안모드 사용여부
			bx = DataUtil.append(bx, DataUtil.get4ByteToInt(orgPayloadLenth)); //   4
		} else {
			/*
			 * Payload Data 암호화.
			 */
			payload = auth.doEncryption(payload);	
			int ciperPayloadLenth = payload.length;
			int paddginSize = ciperPayloadLenth - orgPayloadLenth;
			
			if(paddginSize == 16){  // Padding Byte의 길이가 16Byte일 경우 해당비트를 0으로 세팅한다.
				paddginSize = 0;
			}
						
			String byteString = "";
			byteString += String.format("%04d", new BigInteger(Integer.toBinaryString(paddginSize)));
			byteString += String.format("%028d", new BigInteger(Integer.toBinaryString(orgPayloadLenth)));  
			
			byte[] result = new byte[4];
			result[0] = (byte) Integer.parseInt((String) byteString.subSequence(0, 8), 2);
			result[1] = (byte) Integer.parseInt((String) byteString.subSequence(8, 16), 2);
			result[2] = (byte) Integer.parseInt((String) byteString.subSequence(16, 24), 2);
			result[3] = (byte) Integer.parseInt((String) byteString.subSequence(24, 32), 2);

			log.debug("Plain Payload Length={}, Cyper Palyload Length={}, LengthByteString={}, Hex={}"
					, new Object[]{orgPayloadLenth, ciperPayloadLenth, byteString, Hex.decode(result)});
			
			bx = DataUtil.append(bx, result);
		}
		
		bx = DataUtil.append(bx, payload);   // Nbyte, Payload
		return bx;
	}
}
