/**
 * (@)# EMnVCommandFrame.java
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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.fep.protocol.emnv.actions.EMnVActions.ActionCommandType;
import com.aimir.fep.protocol.emnv.actions.EMnVActions.ErrorBitType;
import com.aimir.fep.protocol.emnv.actions.EMnVActions.EventLogCode;
import com.aimir.fep.protocol.emnv.actions.EMnVActions.RWSType;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants.EMnVCommandSubFrameType;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

/**
 * @author simhanger
 *
 */
//public class EMnVCommandFramePayLoad implements EMnVGeneralFramePayLoad {
public class EMnVCommandFramePayLoad extends EMnVGeneralFramePayLoad {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerFactory.getLogger(EMnVCommandFramePayLoad.class);

	private byte[] payload_data;
	private EMnVCommandSubFrameType subFrameType;
	private ActionCommandType commandType;
	private RWSType rwsType;
	private ErrorBitType errorBitType;

	/*
	 * ON_DEMAND, METER_TIMESYNC, METER_SCAN 용
	 */
	private byte[] destHDLCAddress; // 2Byte : Ondemand, Meter TimeSync, Meter Scan 에서 사용
	private byte[] lpIndex;         // 1Byte : Ondemand
	private byte[] modemPort;       // 2Byte : Inverter Log에서 사용
	
	/*
	 * SERVER_IP 용
	 */
	private byte[] serverIp; // 4byte 

	/*
	 * SERVER_PORT 용
	 */
	private byte[] serverPort; // 2byte

	/*
	 * LP_INTERVAL, HW_RESET_INTERVAL 용
	 */
	private byte[] interval;      // 1byte
	
	/*
	 * 모뎀(mobile) Number 용
	 */
	private byte[] mNumber;   // 16byte
	
	/*
	 * 고정키변경 용
	 */
	private byte[] staticKey;  // 16byte
	
	/*
	 * Event Log용
	 */
	private List<HashMap<String, Object>> eventLogList;  
	
	/*
	 * Inverter Information 용
	 */
	private int maxPortSize; // 모뎀이 지원하는 인버터의 최대수
	private List<HashMap<String, Object>> inverterInfoList;
	
	/*
	 * Inverter Setup 용
	 */
    private byte[] inverterPortNumber;
    private byte[] inverterEnable;
    private byte[] inverterDeviceNumber;
    private byte[] inverterVendor;
	
	public EMnVCommandFramePayLoad(byte[] payload_data) {
		this.payload_data = payload_data;
	}

	public EMnVCommandFramePayLoad(EMnVCommandSubFrameType subFrameType) {
		this.subFrameType = subFrameType;
	}

	public byte[] getPayload_data() {
		return payload_data;
	}

	public void setPayload_data(byte[] payload_data) {
		this.payload_data = payload_data;
	}

	public EMnVCommandSubFrameType getSubFrameType() {
		return subFrameType;
	}

	public void setSubFrameType(EMnVCommandSubFrameType subFrameType) {
		this.subFrameType = subFrameType;
	}

	public void setLpIndex(byte[] lpIndex) {
		this.lpIndex = lpIndex;
	}

	public ActionCommandType getCommandType() {
		return commandType;
	}

	public void setCommandType(ActionCommandType commandType) {
		this.commandType = commandType;
	}

	public RWSType getRwsType() {
		return rwsType;
	}

	public void setRwsType(RWSType rwsType) {
		this.rwsType = rwsType;
	}

	public ErrorBitType getErrorBitType() {
		return errorBitType;
	}

	public void setErrorBitType(ErrorBitType errorBitType) {
		this.errorBitType = errorBitType;
	}

	public byte[] getDestHDLCAddress() {
		return destHDLCAddress;
	}

	public void setDestHDLCAddress(byte[] destHDLCAddress) {
		this.destHDLCAddress = destHDLCAddress;
	}
	
	public byte[] getModemPort() {
		return modemPort;
	}

	public void setModemPort(byte[] modemPort) {
		this.modemPort = modemPort;
	}

	public byte getLpIndex() {
		return lpIndex[0];
	}

	public void setLpIndex(byte lpIndex) {
		byte temp[] = new byte[1];
		temp[0] = lpIndex;

		this.lpIndex = temp;
	}

	public byte[] getServerIp() {
		return serverIp;
	}

	public void setServerIp(byte[] serverIp) {
		this.serverIp = serverIp;
	}

	public byte[] getServerPort() {
		return serverPort;
	}

	public void setServerPort(byte[] serverPort) {
		this.serverPort = serverPort;
	}

	public byte[] getInterval() {
		return interval;
	}

	public void setInterval(byte[] interval) {
		this.interval = interval;
	}

	public byte[] getmNumber() {
		return mNumber;
	}

	public void setmNumber(byte[] mNumber) {
		this.mNumber = mNumber;
	}

	public byte[] getStaticKey() {
		return staticKey;
	}

	public void setStaticKey(byte[] staticKey) {
		this.staticKey = staticKey;
	}

	public List<HashMap<String, Object>> getEventLogList() {
		return eventLogList;
	}

	public void setEventLogList(List<HashMap<String, Object>> eventLogList) {
		this.eventLogList = eventLogList;
	}
	
	public List<HashMap<String, Object>> getInverterInfoList() {
		return inverterInfoList;
	}

	public int getMaxPortSize() {
		return maxPortSize;
	}

	public void setMaxPortSize(int maxPortSize) {
		this.maxPortSize = maxPortSize;
	}

	public void setInverterInfoList(List<HashMap<String, Object>> inverterInfoList) {
		this.inverterInfoList = inverterInfoList;
	}
	
	public byte[] getInverterPortNumber() {
		return inverterPortNumber;
	}

	public void setInverterPortNumber(String inverterPortNumber) {
		byte temp[] = new byte[1];
		temp[0] = DataUtil.getByteToInt(inverterPortNumber);
		
		this.inverterPortNumber = temp;
	}

	public byte[] getInverterEnable() {
		return inverterEnable;
	}

	public void setInverterEnable(String inverterEnable) {
		byte temp[] = new byte[1];
		temp[0] = DataUtil.getByteToInt(inverterEnable);
		
		this.inverterEnable = temp;
	}

	public byte[] getInverterDeviceNumber() {
		return inverterDeviceNumber;
	}

	public void setInverterDeviceNumber(String inverterDeviceNumber) {
		this.inverterDeviceNumber = Hex.encode(inverterDeviceNumber);
	}

	public byte[] getInverterVendor() {
		return inverterVendor;
	}

	public void setInverterVendor(String inverterVendor) {
		byte temp[] = new byte[1];
		temp[0] = DataUtil.getByteToInt(inverterVendor);
		
		this.inverterVendor = temp;
	}

	@Override
	public void decode() throws Exception {
		switch (decodeSubFrameType()) {
		case SERVER_IP:
			if(rwsType == RWSType.R){
				serverIp = new byte[4];
				System.arraycopy(payload_data, 2, serverIp, 0, serverIp.length);
			}
			break;
		case SERVER_PORT:
			if(rwsType == RWSType.R){
				serverPort = new byte[2];
				System.arraycopy(payload_data, 2, serverPort, 0, serverPort.length);
			}
			break;
		case LP_INTERVAL:
			if(rwsType == RWSType.R){
				interval = new byte[1];
				System.arraycopy(payload_data, 2, interval, 0, interval.length);
			}
			break;
		case HW_RESET_INTERVAL:
			if(rwsType == RWSType.R){
				interval = new byte[1];
				System.arraycopy(payload_data, 2, interval, 0, interval.length);
			}
			break;
		case NV_RESET:
			// Data 없음.	
			break;
		case M_NUMBER:
			if(rwsType == RWSType.R){
				mNumber = new byte[16];
				System.arraycopy(payload_data, 2, mNumber, 0, mNumber.length);
			}
			break;
		case HW_RESET:
			// Data 없음.	
			break;
		case EVENT_LOG:
			if(rwsType == RWSType.R){
				eventLogList = new ArrayList<HashMap<String,Object>>();
				int offset = 2; // Sub Frame Type 데이터.
				
				byte[] countByte = new byte[1];
				System.arraycopy(payload_data, offset, countByte, 0, countByte.length);
				int count = DataUtil.getIntToBytes(countByte);
				offset = offset + countByte.length;
				
				HashMap<String, Object> eventLogData;
				byte[] timeByte = null;
				byte[] codeByte = null;
				
				for(int i=0; i<count; i++){
					timeByte = new byte[7];
					codeByte = new byte[1];
					
					System.arraycopy(payload_data, offset, timeByte, 0, timeByte.length);
					offset = offset + timeByte.length;
					
					System.arraycopy(payload_data, offset, codeByte, 0, codeByte.length);
					offset = offset + codeByte.length;
					
					eventLogData = new HashMap<String, Object>();
					eventLogData.put("TIME", DataUtil.getEMnvModemDate(timeByte));
					eventLogData.put("CODE", EventLogCode.getItem(codeByte[0]));
				
					eventLogList.add(eventLogData);
				}
			}
			break;
		case KEY_CHANGE:
			// Data 없음.
			break;
		case ON_DEMAND:
			// Data 없음.
			break;
		case ON_DEMAND_INVERTER_LOG:
			// Data 없음.
			break;			
		case METER_TIMESYNC:
			// Data 없음.
			break;
		case METER_SCAN:
			// Data 없음.	
			break;
		case INVERTER_INFO:
			if(rwsType == RWSType.R){
				inverterInfoList = new ArrayList<HashMap<String,Object>>();
				int offset = 2;  // Sub Frame Type 데이터.
				
				byte[] maxPortSizeByte = new byte[1];
				System.arraycopy(payload_data, offset, maxPortSizeByte, 0, maxPortSizeByte.length);
				maxPortSize = DataUtil.getIntToBytes(maxPortSizeByte);
				offset = offset + maxPortSizeByte.length;
				
				HashMap<String, Object> inverterData;
				byte[] portNumberByte = null;
				byte[] enableByte = null;
				byte[] deviceNumberByte = null;
				byte[] vendorByte = null;
				
				while(offset < payload_data.length){
					portNumberByte = new byte[1];
					enableByte = new byte[1];
					deviceNumberByte = new byte[1];
					vendorByte = new byte[1];
					
					System.arraycopy(payload_data, offset, portNumberByte, 0, portNumberByte.length);
					offset = offset + portNumberByte.length;
					
					System.arraycopy(payload_data, offset, enableByte, 0, enableByte.length);
					offset = offset + enableByte.length;
					
					System.arraycopy(payload_data, offset, deviceNumberByte, 0, deviceNumberByte.length);
					offset = offset + deviceNumberByte.length;
					
					System.arraycopy(payload_data, offset, vendorByte, 0, vendorByte.length);
					offset = offset + vendorByte.length;
					
					inverterData = new HashMap<String, Object>();
					inverterData.put("PORT_NUMBER", DataUtil.getIntToBytes(portNumberByte));
					inverterData.put("ENABLE", DataUtil.getIntToBytes(enableByte));
					inverterData.put("DEVICE_NUMBER", Hex.decode(deviceNumberByte));
					
					int temp = DataUtil.getIntToBytes(vendorByte);
					switch (temp) {
					case 0:
						inverterData.put("VENDOR", "LS산전");
						break;
					case 1:
						inverterData.put("VENDOR", "현대중공업");
						break;
					case 2:
						inverterData.put("VENDOR", "로크웰");
						break;
					default:
						inverterData.put("VENDOR", "알수없음=" + temp);
						break;
					}
					
					inverterInfoList.add(inverterData);
				}
			}
			break;
		case INVERTER_SETUP:

			break;
		default:
			break;
		}
	}

	@Override
	public byte[] encode() throws Exception {
		byte[] result = encodeSubFrameType();

		switch (commandType) {
		case SERVER_IP:
			if(rwsType == RWSType.W){
				result = DataUtil.append(result, serverIp);
				logger.debug("[PROTOCOL][COMMAND_SUB_FRAME][ENCODE]: SERVER_IP={}", Hex.decode(serverIp));
			}
			break;
		case SERVER_PORT:
			if(rwsType == RWSType.W){
				result = DataUtil.append(result, serverPort);
				logger.debug("[PROTOCOL][COMMAND_SUB_FRAME][ENCODE]: SERVER_PORT={}", Hex.decode(serverPort));
			}
			break;
		case LP_INTERVAL:
			if(rwsType == RWSType.W){
				result = DataUtil.append(result, interval);
				logger.debug("[PROTOCOL][COMMAND_SUB_FRAME][ENCODE]: LP_INTERVAL={}", Hex.decode(interval));
			}
			break;
		case HW_RESET_INTERVAL:
			if(rwsType == RWSType.W){
				result = DataUtil.append(result, interval);
				logger.debug("[PROTOCOL][COMMAND_SUB_FRAME][ENCODE]: HW_RESET_INTERVAL={}", Hex.decode(interval));
			}
			break;
		case NV_RESET:
			// Data 없음.
			break;
		case M_NUMBER:
			// Data 없음.
			break;
		case HW_RESET:
			// Data 없음.
			break;
		case EVENT_LOG:
			// Data 없음.
			break;
		case KEY_CHANGE:
			if(rwsType == RWSType.W){
				result = DataUtil.append(result, staticKey);
				logger.debug("[PROTOCOL][COMMAND_SUB_FRAME][ENCODE]: KEY_CHANGE={}", Hex.decode(staticKey));
			}
			break;
		case ON_DEMAND:
			result = DataUtil.append(result, destHDLCAddress);
			result = DataUtil.append(result, lpIndex);

			logger.debug("[PROTOCOL][COMMAND_SUB_FRAME][ENCODE]: DEST_HDLC_ADDRESS={}, LP_INDEX={}", Hex.decode(destHDLCAddress), DataUtil.getIntToBytes(lpIndex));
			break;
		case ON_DEMAND_INVERTER_LOG:
			result = DataUtil.append(result, modemPort);
			result = DataUtil.append(result, lpIndex);

			logger.debug("[PROTOCOL][COMMAND_SUB_FRAME][ENCODE]: MODEM_PORT={}, LP_INDEX={}", Hex.decode(modemPort), DataUtil.getIntToBytes(lpIndex));
			break;			
		case METER_TIMESYNC:
			result = DataUtil.append(result, destHDLCAddress);
			logger.debug("[PROTOCOL][COMMAND_SUB_FRAME][ENCODE]: DEST_HDLC_ADDRESS={}", Hex.decode(destHDLCAddress));
			break;
		case METER_SCAN:
			result = DataUtil.append(result, destHDLCAddress);
			logger.debug("[PROTOCOL][COMMAND_SUB_FRAME][ENCODE]: DEST_HDLC_ADDRESS={}", Hex.decode(destHDLCAddress));
			break;
		case INVERTER_INFO:
			// Data 없음.
			break;
		case INVERTER_SETUP:
			result = DataUtil.append(result, inverterPortNumber);
			result = DataUtil.append(result, inverterEnable);
			result = DataUtil.append(result, inverterDeviceNumber);
			result = DataUtil.append(result, inverterVendor);
			
			logger.debug("[PROTOCOL][COMMAND_SUB_FRAME][ENCODE]: PORT_NUMBER={}, ENABLE={}, DEVICE_NUMBER={}, VENDOR={}"
					, DataUtil.getIntToBytes(inverterPortNumber), DataUtil.getIntToBytes(inverterEnable), Hex.decode(inverterDeviceNumber), DataUtil.getIntToBytes(inverterVendor));
			break;
		default:
			break;
		}

		return result;
	}

	private byte[] encodeSubFrameType() {
		byte[] result = new byte[2];
		String byteString = "";
		byteString += String.format("%02d", new BigInteger(Integer.toBinaryString(rwsType.getValue()))); // R/W/S
		byteString += String.format("%02d", new BigInteger(Integer.toBinaryString(errorBitType.getValue()))); // Error 
		byteString += "0000";
		result[0] = (byte) Integer.parseInt(byteString, 2);

		byteString = "";
		byteString += String.format("%08d", new BigInteger(Integer.toBinaryString(commandType.getValue()))); // Command Frame identifier
		result[1] = (byte) Integer.parseInt(byteString, 2);

		logger.debug("[PROTOCOL][COMMAND_SUB_FRAME][ENCODE]: RWS_TYPE={}, ERROR_BIT_TYPE={}, COMMAND={} | ByteString={}, Hex={}", new Object[] { rwsType.name(), errorBitType.name(), commandType.name(), byteString, Hex.getHexDump(result) });

		return result;
	}

	private ActionCommandType decodeSubFrameType() {
		byte[] subFrame = new byte[2];
		System.arraycopy(payload_data, 0, subFrame, 0, subFrame.length);

		rwsType = RWSType.getItem(subFrame[0] >>> 6 & 0x03);   
		errorBitType = ErrorBitType.getItem((subFrame[0] >>> 4) & 0x03);
		commandType = ActionCommandType.getItem(subFrame[1]);

		return commandType;
	}

	@Override
	public boolean isValidation(Object obj) throws Exception {
		boolean result = true;

		switch (subFrameType) {
		case COMMAND_RESPONSE:
			if (subFrameType == null || commandType == null || rwsType == null || errorBitType == null) {
				result = false;
			}
			break;
		default:
			break;
		}

		return result;
	}

}
