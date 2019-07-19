/**
 * (@)# DCUInfo.java
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

/**
 * @author simhanger
 *
 */
public class DCUInfo implements IModemInfo {
	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(DCUInfo.class);

	private ModemType modemType = ModemType.LTE;
	private final int totalLength = 108;
	private String deviceId;

	private byte[] fwVersion; // 10
	private byte[] hwVersion; // 10
	private byte[] nowTime; // 7 , 현재시간
	private byte[] rsrp; // 4 , RSRP(Reference Signal Received Power (dBm)
	private byte[] txPower; // 4, 송신세기 (TX power) (dBm)
	private byte[] rsrq; //4,  RSRQ(Reference Signal Received Quality) (dBm)
	private byte[] plmn; //8, Public Land Mobile Network
	private byte[] prodCompany; //20,  제작사 / 문자열 – xxxxxxxxxxxxxxxx(20자리) / (상위부터 값을 채우고 하위 bytes가 없을시 나머지는 0을 채운다.)
	private byte[] prodDate; //20, 제조년월일 / 문자열 – xxxxxxxxxxxxxxxx(20자리) / (상위부터 값을 채우고 하위 bytes가 없을시 나머지는 0을 채운다.)
	private byte[] prodNumber; //20,  제조번호 / 문자열 – xxxxxxxxxxxxxxxx(20자리) / (상위부터 값을 채우고 하위 bytes가 없을시 나머지는 0을 채운다.)
	private byte[] meteringPeriod; //1, 검침주기 = 분 / 15분 - Hex 값 1 byte

	public DCUInfo(String sourceAddress) {
		deviceId = sourceAddress;

		fwVersion = new byte[10];
		hwVersion = new byte[10];
		nowTime = new byte[7];
		rsrp = new byte[4];
		txPower = new byte[4];
		rsrq = new byte[4];
		plmn = new byte[8];
		prodCompany = new byte[20];
		prodDate = new byte[20];
		prodNumber = new byte[20];
		meteringPeriod = new byte[1];
	}

	@Override
	public int getTotalLength() {
		return totalLength;
	}
	
	public byte[] getFwVersion() {
		return fwVersion;
	}

	public byte[] getHwVersion() {
		return hwVersion;
	}

	public byte[] getNowTime() {
		return nowTime;
	}

	public byte[] getRsrp() {
		return rsrp;
	}

	public byte[] getTxPower() {
		return txPower;
	}

	public byte[] getRsrq() {
		return rsrq;
	}

	public byte[] getPlmn() {
		return plmn;
	}

	public byte[] getProdCompany() {
		return prodCompany;
	}

	public byte[] getProdDate() {
		return prodDate;
	}

	public byte[] getProdNumber() {
		return prodNumber;
	}

	public byte[] getMeteringPeriod() {
		return meteringPeriod;
	}

	@Override
	public void decode(byte[] data) {
		try {
			log.debug("[PROTOCOL][MODEM_INFO][{}] DCU_INFO({}):[{}] ==> {}", new Object[] { modemType.name(), data.length, "", Hex.decode(data) });

			System.arraycopy(data, 0, fwVersion, 0, 10);
			System.arraycopy(data, 10, hwVersion, 0, 10);
			System.arraycopy(data, 20, nowTime, 0, 7);
			System.arraycopy(data, 27, rsrp, 0, 4);
			System.arraycopy(data, 31, txPower, 0, 4);
			System.arraycopy(data, 35, rsrq, 0, 4);
			System.arraycopy(data, 39, plmn, 0, 8);

			System.arraycopy(data, 47, prodCompany, 0, 20);
			prodCompany = DataUtil.trim0x00Byte(prodCompany);

			System.arraycopy(data, 67, prodDate, 0, 20);
			prodDate = DataUtil.trim0x00Byte(prodDate);

			System.arraycopy(data, 87, prodNumber, 0, 20);
			prodNumber = DataUtil.trim0x00Byte(prodNumber);

			System.arraycopy(data, 107, meteringPeriod, 0, 1);

			log.debug("[PROTOCOL][MODEM_INFO][{}] DCU_INFO({}):[{}] ==> {}", new Object[] { modemType.name(), data.length, "", toString() });
		} catch (Exception e) {
			log.debug("DCUInfo decode error - {}", e);
		}
	}

	@Override
	public byte[] encode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ModemType getModemType() {
		return modemType;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		try {
			builder.append("DCUInfo [modemType=");
			builder.append(modemType);
			builder.append(", totalLength=");
			builder.append(totalLength);
			builder.append(", deviceId=");
			builder.append(deviceId);
			builder.append(", fwVersion=");
			builder.append(new String(fwVersion));
			builder.append(", hwVersion=");
			builder.append(new String(hwVersion));
			builder.append(", nowTime=");
			builder.append(DataUtil.getEMnvModemDate(nowTime));
			builder.append(", rsrp=");
			builder.append(new String(rsrp));
			builder.append(", txPower=");
			builder.append(new String(txPower));
			builder.append(", rsrq=");
			builder.append(new String(rsrq));
			builder.append(", plmn=");
			builder.append(new String(plmn));
			builder.append(", prodCompany=");
			builder.append(new String(prodCompany));
			builder.append(", prodDate=");
			builder.append(new String(prodDate));
			builder.append(", prodNumber=");
			builder.append(new String(prodNumber));
			builder.append(", meteringPeriod=");
			builder.append(DataUtil.getIntToByte(meteringPeriod[0]));
			builder.append("]");
		} catch (Exception e) {
			log.debug("DCUInfo toString Error - {}", e);
		}
		return builder.toString();
	}

	@Override
	public String getDeviceId() {
		return deviceId;
	}

}
