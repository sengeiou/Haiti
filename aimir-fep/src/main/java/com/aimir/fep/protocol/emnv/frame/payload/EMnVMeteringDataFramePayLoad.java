/**
 * (@)# EMnVMeteringDataFrame.java
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.fep.protocol.emnv.exception.EMnVSystemException;
import com.aimir.fep.protocol.emnv.exception.EMnVSystemException.EMnVExceptionReason;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants.EMnVMeterType;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants.EMnVMeteringDataSatus;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants.EMnVMeteringDataType;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants.FrameControlAddr;
import com.aimir.fep.util.Hex;

/**
 * @author simhanger
 *
 */
//public class EMnVMeteringDataFramePayLoad implements EMnVGeneralFramePayLoad {
public class EMnVMeteringDataFramePayLoad extends EMnVGeneralFramePayLoad {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerFactory.getLogger(EMnVMeteringDataFramePayLoad.class);

	private byte[] payload_data;

	private FrameControlAddr sRC_ADDR_TYPE;
	private String sourceAddress;
	private EMnVMeterType meterType; // G-type, E-type1.1, E-type1.0, Modbus, dcu
	private EMnVMeteringDataType meteringDataType; // Billing or Load profile
	private EMnVMeteringDataSatus status; // 모뎀정상, 검침실패, 정전발생
	private ModemType modemInfoType; // DCU or ZigBee or Sub-giga
	private IModemInfo modemInfo;
	private byte[] mdData;

	private int modemInfoLength;
	private int mdDataLentgh;
	

	public EMnVMeteringDataFramePayLoad(byte[] payload_data) {
		this.payload_data = payload_data;
	}

	/**
	 * SRC ADDRESS 타입이 IPv6 => Sub-Giga, EUI_64 => ZigBee, MOBILE_NUMBER => DCU로
	 * 처리.
	 * 
	 * DCU 단독             ==> SRC_ADDRESS_TYPE = DCU, METER_TYPE = DCU , MODEM_INFO = DCU
	 * DCU + ZIGBEE_GTYPE   ==> SRC_ADDRESS_TYPE = EUI64, METER_TYPE = GTYPE, MODEM_INFO = ZIGBEE 
	 * DCU + ZIGBEE_ETYPE   ==> SRC_ADDRESS_TYPE = EUI64, METER_TYPE = ETYPE, MODEM_INFO = ZIGBEE 
	 * DCU + SUB_GIGA_GTYPE ==> SRC_ADDRESS_TYPE = IPv6, METER_TYPE = GTYPE, MODEM_INFO = SUB_GIGA 
	 * DCU + SUB_GIGA_ETYPE ==> SRC_ADDRESS_TYPE = IPv6, METER_TYPE = ETYPE, MODEM_INFO = SUB_GIGA
	 * 
	 * LTE_GTYPE            ==> SRC_ADDRESS_TYPE = MOBILE_NUMBER, METER_TYPE = GTYPE, MODEM_INFO = DCU 
	 * LTE_ETYPE            ==> SRC_ADDRESS_TYPE = MOBILE_NUMBER, METER_TYPE = ETYPE, MODEM_INFO = DCU
	 */
	public void setModemInfoType(FrameControlAddr sRC_ADDR_TYPE) {
		this.sRC_ADDR_TYPE = sRC_ADDR_TYPE;
		if (sRC_ADDR_TYPE == FrameControlAddr.IPv6) { // Sub-Giga
			modemInfoType = ModemType.SubGiga;
		} else if (sRC_ADDR_TYPE == FrameControlAddr.EUI64) { // ZigBee
			modemInfoType = ModemType.ZigBee;
		} else if (sRC_ADDR_TYPE == FrameControlAddr.MOBILE_NUMBER) {
			modemInfoType = ModemType.LTE; // LTE 단독
		} else {
			modemInfoType = null;
		}
	}

	public String getSourceAddress() {
		return sourceAddress;
	}

	public void setSourceAddress(String sourceAddress) {
		this.sourceAddress = sourceAddress;
	}

	public EMnVMeterType getMeterType() {
		return meterType;
	}

	public EMnVMeteringDataType getMeteringDataType() {
		return meteringDataType;
	}

	public EMnVMeteringDataSatus getStatus() {
		return status;
	}

	public ModemType getModemInfoType() {
		return modemInfoType;
	}

	public IModemInfo getModemInfo() {
		return modemInfo;
	}

	public byte[] getMdData() {
		return mdData;
	}

	public int getModemInfoLength() {
		return modemInfoLength;
	}

	public int getMdDataLentgh() {
		return mdDataLentgh;
	}

	@Override
	public void decode() throws Exception {
		try {
			meterType = EMnVMeterType.getItem(payload_data[0]);
			logger.info("[PROTOCOL][METERING_DATA_FRAME] METER_TYPE(1byte):[{}] ==> {}", meterType.name(), payload_data[0]);
			
			meteringDataType = EMnVMeteringDataType.getItem(payload_data[1]);
			logger.info("[PROTOCOL][METERING_DATA_FRAME] METERING_DATA_TYPE(1byte):[{}] ==> {}", meteringDataType.name(), payload_data[1]);
			
			status = EMnVMeteringDataSatus.getItem(payload_data[2]);
			logger.info("[PROTOCOL][METERING_DATA_FRAME] STATUS(1byte):[{}] ==> {}", status.name(), payload_data[2]);
			
			if(status == EMnVMeteringDataSatus.METERING_FAIL){
				logger.warn("#### 미터링 데이터 상태 FAIL. 모뎀업체에 문의 필요. ####");
				logger.warn("#### 미터링 데이터 상태 FAIL. 모뎀업체에 문의 필요. ####");
				logger.warn("#### 미터링 데이터 상태 FAIL. 모뎀업체에 문의 필요. ####");
				logger.warn("#### 미터링 데이터 상태 FAIL. 모뎀업체에 문의 필요. 임시로 등록 가능하도록 해둠. ####");
//				throw new EMnVSystemException(EMnVExceptionReason.METERING_DATA_STATUS_FAIL);  모뎀등록은 가능하도록 Exception처리는 하지 않음.
			}
			
			if (meterType == EMnVMeterType.DCU) {
				modemInfo = new DCUInfo(sourceAddress);
				modemInfoLength = ((DCUInfo) modemInfo).getTotalLength();
			} 
			// G-Type, E-Type1.0, E-Type1.1, Modbus
			else {    
				if (modemInfoType == ModemType.SubGiga) {
					modemInfo = new SubgigaModemInfo(sourceAddress);
					modemInfoLength = modemInfo.getTotalLength();
				} else if (modemInfoType == ModemType.ZigBee) {
					modemInfo = new ZigBeeModemInfo(sourceAddress);
					modemInfoLength = modemInfo.getTotalLength();
				} else if (modemInfoType == ModemType.LTE) {
					modemInfo = new DCUInfo(sourceAddress);
					modemInfoLength = modemInfo.getTotalLength();
				} else {
					throw new EMnVSystemException(EMnVExceptionReason.UNKNOWN, "Unknow MeterType");
				}
			}
			
			/**
			 * 미터타입 재설정
        	 * SRC ADDRESS 타입이 IPv6 => Sub-Giga, EUI_64 => ZigBee, MOBILE_NUMBER => DCU로
        	 * 처리.
        	 * 
        	 * DCU 단독             ==> SRC_ADDRESS_TYPE = DCU, METER_TYPE = DCU , MODEM_INFO = DCU
        	 * DCU + ZIGBEE_GTYPE   ==> SRC_ADDRESS_TYPE = EUI64, METER_TYPE = GTYPE, MODEM_INFO = ZIGBEE 
        	 * DCU + ZIGBEE_ETYPE   ==> SRC_ADDRESS_TYPE = EUI64, METER_TYPE = ETYPE, MODEM_INFO = ZIGBEE 
        	 * DCU + SUB_GIGA_GTYPE ==> SRC_ADDRESS_TYPE = IPv6, METER_TYPE = GTYPE, MODEM_INFO = SUB_GIGA 
        	 * DCU + SUB_GIGA_ETYPE ==> SRC_ADDRESS_TYPE = IPv6, METER_TYPE = ETYPE, MODEM_INFO = SUB_GIGA
        	 * 
        	 * LTE_GTYPE            ==> SRC_ADDRESS_TYPE = MOBILE_NUMBER, METER_TYPE = GTYPE, MODEM_INFO = DCU 
        	 * LTE_ETYPE            ==> SRC_ADDRESS_TYPE = MOBILE_NUMBER, METER_TYPE = ETYPE, MODEM_INFO = DCU
        	 */
			if(sRC_ADDR_TYPE == FrameControlAddr.EUI64 && meterType == EMnVMeterType.G_TYPE){
				meterType = EMnVMeterType.ZIGBEE_G_TYPE;
			}else if(sRC_ADDR_TYPE == FrameControlAddr.EUI64 && meterType == EMnVMeterType.E_TYPE_1_0){
				meterType = EMnVMeterType.ZIGBEE_E_TYPE_1_0;				
			}else if(sRC_ADDR_TYPE == FrameControlAddr.EUI64 && meterType == EMnVMeterType.E_TYPE_1_1){
				meterType = EMnVMeterType.ZIGBEE_E_TYPE_1_1;	
			}else if(sRC_ADDR_TYPE == FrameControlAddr.EUI64 && meterType == EMnVMeterType.MODBUS){
				meterType = EMnVMeterType.ZIGBEE_MODBUS;				
			}else if(sRC_ADDR_TYPE == FrameControlAddr.EUI64 && meterType == EMnVMeterType.INVERTER_LOG){
				meterType = EMnVMeterType.ZIGBEE_INVERTER_LOG;				
			}
			
			else if(sRC_ADDR_TYPE == FrameControlAddr.IPv6 && meterType == EMnVMeterType.G_TYPE){
				meterType = EMnVMeterType.SUBGIGA_G_TYPE;
			}else if(sRC_ADDR_TYPE == FrameControlAddr.IPv6 && meterType == EMnVMeterType.E_TYPE_1_0){
				meterType = EMnVMeterType.SUBGIGA_E_TYPE_1_0;
			}else if(sRC_ADDR_TYPE == FrameControlAddr.IPv6 && meterType == EMnVMeterType.E_TYPE_1_1){
				meterType = EMnVMeterType.SUBGIGA_E_TYPE_1_1;
			}else if(sRC_ADDR_TYPE == FrameControlAddr.IPv6 && meterType == EMnVMeterType.MODBUS){
				meterType = EMnVMeterType.SUBGIGA_MODBUS;
			}else if(sRC_ADDR_TYPE == FrameControlAddr.IPv6 && meterType == EMnVMeterType.INVERTER_LOG){
				meterType = EMnVMeterType.SUBGIGA_INVERTER_LOG;
			}
			
			else if(sRC_ADDR_TYPE == FrameControlAddr.MOBILE_NUMBER && meterType == EMnVMeterType.G_TYPE){
				meterType = EMnVMeterType.LTE_G_TYPE;
			}else if(sRC_ADDR_TYPE == FrameControlAddr.MOBILE_NUMBER && meterType == EMnVMeterType.E_TYPE_1_0){
				meterType = EMnVMeterType.LTE_E_TYPE_1_0;
			}else if(sRC_ADDR_TYPE == FrameControlAddr.MOBILE_NUMBER && meterType == EMnVMeterType.E_TYPE_1_1){
				meterType = EMnVMeterType.LTE_E_TYPE_1_1;
			}else if(sRC_ADDR_TYPE == FrameControlAddr.MOBILE_NUMBER && meterType == EMnVMeterType.MODBUS){
				meterType = EMnVMeterType.LTE_MODBUS;
			}else if(sRC_ADDR_TYPE == FrameControlAddr.MOBILE_NUMBER && meterType == EMnVMeterType.INVERTER_LOG){
				meterType = EMnVMeterType.LTE_INVERTER_LOG;
			}else{
				throw new EMnVSystemException(EMnVExceptionReason.UNKNOWN, "Unknow Meter Type ~ !!!!!");
			}
			
			byte[] info = new byte[modemInfoLength];
			System.arraycopy(payload_data, (meterType.getLength() + meteringDataType.getLength() + status.getLength()), info, 0, modemInfoLength);
			logger.info("[PROTOCOL][METERING_DATA_FRAME] MODEM_INFO({}):[{}] ==> {}", new Object[]{ modemInfoLength,  "", Hex.decode(info)});			
			modemInfo.decode(info);

			// Metering Data			
			mdDataLentgh = payload_data.length - (meterType.getLength() + meteringDataType.getLength() + status.getLength()) - modemInfoLength;

			mdData = new byte[mdDataLentgh];
			System.arraycopy(payload_data, meterType.getLength() + meteringDataType.getLength() + status.getLength() + modemInfoLength, mdData, 0, mdDataLentgh);
			logger.info("[PROTOCOL][METERING_DATA_FRAME] DATA({}):[{}] ==> {}", new Object[]{ mdDataLentgh,  "", Hex.decode(mdData)});

		} catch (Exception ex) {
			logger.error("MDFrame Decode error - {}", ex);
		}
	}

	@Override
	public byte[] encode() throws Exception {
		return null;
	}

	@Override
	public boolean isValidation(Object obj) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

}
