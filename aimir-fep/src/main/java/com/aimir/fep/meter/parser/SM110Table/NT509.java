/** 
 * @(#)NT509.java       1.0 2019-11-07 *
 * 
 * Copyright (c) 2009-2010 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */

package com.aimir.fep.meter.parser.SM110Table;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Hex;

/**
 * @author Park Jiwoong wll27471297@nuritelecom.com
 */
public class NT509 implements java.io.Serializable {

	private static final long serialVersionUID = -3024653700839379563L;

	private static Log log = LogFactory.getLog(NT509.class);

	// Info Frame : 44 Byte
	// -> CURRENT_TIME : 11 Byte
	private byte[] TIME_ZONE = new byte[2];
	private byte[] DST_VALUE = new byte[2];
	private byte[] YEAR = new byte[2];
	private byte[] MONTH = new byte[1];
	private byte[] DAY = new byte[1];
	private byte[] HOUR = new byte[1];
	private byte[] MINUTE = new byte[1];
	private byte[] SECOND = new byte[1];

	private byte[] OPERATING_DAY = new byte[2];
	private byte[] ACTIVE_MINUTE = new byte[2];
	private byte[] BATTERY_VOLT = new byte[2];
	private byte[] CONSUMPTION_CURRENT = new byte[2];
	private byte[] OFFSET = new byte[1];

	private byte[] CURRENT_PULSE = new byte[4];
	private byte[] LP_CHOICE = new byte[1]; // 0:today, 1:yesterday ...
	private byte[] LP_PERIOD = new byte[1];
	private byte[] LP_DATE = new byte[4];
	private byte[] BASE_PULSE = new byte[4];
	private byte[] FW_VERSION = new byte[1];

	private byte[] FW_BUILD = new byte[1];
	private byte[] HW_VERSION = new byte[1];
	private byte[] SW_VERSION = new byte[1];
	private byte[] LQI = new byte[1];
	private byte[] RSSI = new byte[1];
	private byte[] NODE_KIND_TYPE = new byte[1];
	private byte[] ALARM_FLAG = new byte[1];
	private byte[] NETWORK_TYPE = new byte[1];
	private byte[] ENERGY_LEVEL = new byte[1];

	// LP Data
	private byte[] LP_DATE_2 = new byte[4];
	private byte[] LP_BASE_PULSE = new byte[4];
	private byte[][] LP_ARR;

	// Parse Variables
	private int lpPeriodMin;
	private String nodeKindType;
	private String networkType;
	private int energyLevel;

	public NT509() { }

	public NT509(byte[] data) throws Exception {
		parse(data);
	}

	public Calendar getFrameInfoDate() {
		Calendar tmpCal = Calendar.getInstance();
		try {
			tmpCal.set(DataFormat.hex2dec(YEAR), DataFormat.hex2dec(MONTH) - 1, DataFormat.hex2dec(DAY),
					DataFormat.hex2dec(HOUR), DataFormat.hex2dec(MINUTE), DataFormat.hex2dec(SECOND));
		} catch (Exception e) {
			log.error("YEAR=" + Hex.decode(YEAR));
			e.printStackTrace();
			tmpCal = null;
		}
		log.debug("NT509 FrameInfoDate=" + tmpCal);
		return tmpCal;
	}

	public Calendar getLpDate() {
		Calendar tmpCal = Calendar.getInstance();
		try {
			tmpCal.set(DataFormat.hex2dec(Arrays.copyOfRange(LP_DATE, 0, 2)),
					DataFormat.hex2dec(Arrays.copyOfRange(LP_DATE, 2, 3)) - 1,
					DataFormat.hex2dec(Arrays.copyOfRange(LP_DATE, 3, 4)), 0, 0, 0);
		} catch (Exception e) {
			log.error("LP_DATE=" + Hex.decode(LP_DATE));
			e.printStackTrace();
			tmpCal = null;
		}
		log.debug("NT509 LpDate=" + tmpCal);
		return tmpCal;
	}

	public void parse(byte[] data) throws Exception {
		int pos = 0;

		System.arraycopy(data, pos, TIME_ZONE, 0, TIME_ZONE.length);
		pos += TIME_ZONE.length;
		System.arraycopy(data, pos, DST_VALUE, 0, DST_VALUE.length);
		pos += DST_VALUE.length;
		System.arraycopy(data, pos, YEAR, 0, YEAR.length);
		pos += YEAR.length;
		System.arraycopy(data, pos, MONTH, 0, MONTH.length);
		pos += MONTH.length;
		System.arraycopy(data, pos, DAY, 0, DAY.length);
		pos += DAY.length;
		System.arraycopy(data, pos, HOUR, 0, HOUR.length);
		pos += HOUR.length;
		System.arraycopy(data, pos, MINUTE, 0, MINUTE.length);
		pos += MINUTE.length;
		System.arraycopy(data, pos, SECOND, 0, SECOND.length);
		pos += SECOND.length;

		System.arraycopy(data, pos, OPERATING_DAY, 0, OPERATING_DAY.length);
		pos += OPERATING_DAY.length;
		System.arraycopy(data, pos, ACTIVE_MINUTE, 0, ACTIVE_MINUTE.length);
		pos += ACTIVE_MINUTE.length;
		System.arraycopy(data, pos, BATTERY_VOLT, 0, BATTERY_VOLT.length);
		pos += BATTERY_VOLT.length;
		System.arraycopy(data, pos, CONSUMPTION_CURRENT, 0, CONSUMPTION_CURRENT.length);
		pos += CONSUMPTION_CURRENT.length;
		System.arraycopy(data, pos, OFFSET, 0, OFFSET.length);
		pos += OFFSET.length;

		System.arraycopy(data, pos, CURRENT_PULSE, 0, CURRENT_PULSE.length);
		pos += CURRENT_PULSE.length;
		System.arraycopy(data, pos, LP_CHOICE, 0, LP_CHOICE.length);
		pos += LP_CHOICE.length;
		System.arraycopy(data, pos, LP_PERIOD, 0, LP_PERIOD.length);
		pos += LP_PERIOD.length;
		System.arraycopy(data, pos, LP_DATE, 0, LP_DATE.length);
		pos += LP_DATE.length; if(byteArrValidate(LP_DATE)) throw new Exception("LP_DATE is fill with 0xFF...");
		System.arraycopy(data, pos, BASE_PULSE, 0, BASE_PULSE.length);
		pos += BASE_PULSE.length;
		System.arraycopy(data, pos, FW_VERSION, 0, FW_VERSION.length);
		pos += FW_VERSION.length;

		System.arraycopy(data, pos, FW_BUILD, 0, FW_BUILD.length);
		pos += FW_BUILD.length;
		System.arraycopy(data, pos, HW_VERSION, 0, HW_VERSION.length);
		pos += HW_VERSION.length;
		System.arraycopy(data, pos, SW_VERSION, 0, SW_VERSION.length);
		pos += SW_VERSION.length;
		System.arraycopy(data, pos, LQI, 0, LQI.length);
		pos += LQI.length;
		System.arraycopy(data, pos, RSSI, 0, RSSI.length);
		pos += RSSI.length;
		System.arraycopy(data, pos, NODE_KIND_TYPE, 0, NODE_KIND_TYPE.length);
		pos += NODE_KIND_TYPE.length;
		System.arraycopy(data, pos, ALARM_FLAG, 0, ALARM_FLAG.length);
		pos += ALARM_FLAG.length;
		System.arraycopy(data, pos, NETWORK_TYPE, 0, NETWORK_TYPE.length);
		pos += NETWORK_TYPE.length;
		System.arraycopy(data, pos, ENERGY_LEVEL, 0, ENERGY_LEVEL.length);
		pos += ENERGY_LEVEL.length;

		System.arraycopy(data, pos, LP_DATE_2, 0, LP_DATE_2.length);
		pos += LP_DATE_2.length; if(byteArrValidate(LP_DATE_2)) throw new Exception("LP_DATE_2 is fill with 0xFF...");
		System.arraycopy(data, pos, LP_BASE_PULSE, 0, LP_BASE_PULSE.length);
		pos += LP_BASE_PULSE.length;

		log.debug("pos = " + pos + ", data.length = " + data.length);

		setLpPeriod(LP_PERIOD);
		setNodeKindType(NODE_KIND_TYPE);
		setNetworkType(NETWORK_TYPE);
		setEnergyLevel(ENERGY_LEVEL);

		int lpCnt = (24 * 60) / lpPeriodMin;
		LP_ARR = new byte[lpCnt][];
		int i = 0;
		try {
			for (i = 0; i < lpCnt; i++) {
				LP_ARR[i] = new byte[2];
				System.arraycopy(data, pos, LP_ARR[i], 0, LP_ARR[i].length);
				pos += LP_ARR[i].length;
				if(byteArrValidate(LP_ARR[i])) LP_ARR[i] = null;
			}
		} catch (IndexOutOfBoundsException ie) {
			log.error("IndexOutOfBoundsException | data.length:" + data.length + ", pos:" + pos + ", lpCnt:" + lpCnt
					+ ", i:" + i);
			ie.printStackTrace();
		} catch (Exception e) {
			log.error(e, e);
		}
		log.debug("data.length:" + data.length + ", pos:" + pos + ", lpCnt:" + lpCnt + ", i:" + i);
	}

	public String printAll() {
		StringBuilder sb = new StringBuilder();
		try {
			sb.append("  TIME_ZONE=" + DataFormat.hex2dec(TIME_ZONE)).append(", \n")
					.append("  DST_VALUE=" + DataFormat.hex2dec(DST_VALUE)).append(", \n")
					.append("  YEAR=" + DataFormat.hex2dec(YEAR)).append(", \n")
					.append("  MONTH=" + DataFormat.hex2dec(MONTH)).append(", \n")
					.append("  DAY=" + DataFormat.hex2dec(DAY)).append(", \n")
					.append("  HOUR=" + DataFormat.hex2dec(HOUR)).append(", \n")
					.append("  MINUTE=" + DataFormat.hex2dec(MINUTE)).append(", \n")
					.append("  SECOND=" + DataFormat.hex2dec(SECOND)).append(", \n")

					.append("  OPERATING_DAY=" + DataFormat.hex2dec(OPERATING_DAY)).append(", \n")
					.append("  ACTIVE_MINUTE=" + DataFormat.hex2dec(ACTIVE_MINUTE)).append(", \n")
					.append("  BATTERY_VOLT=" + DataFormat.hex2dec(BATTERY_VOLT)).append(", \n")
					.append("  CONSUMPTION_CURRENT=" + DataFormat.hex2dec(CONSUMPTION_CURRENT)).append(", \n")
					.append("  OFFSET=" + DataFormat.hex2dec(OFFSET)).append(", \n")

					.append("  CURRENT_PULSE=" + DataFormat.hex2dec(CURRENT_PULSE)).append(", \n")
					.append("  LP_CHOICE=" + DataFormat.hex2dec(LP_CHOICE)).append(", \n")
					.append("  LP_PERIOD=" + DataFormat.hex2dec(LP_PERIOD)).append(", \n")
					.append("  LP_DATE=" + DataFormat.hex2dec(Arrays.copyOfRange(LP_DATE, 0, 2)) + ""
							+ DataFormat.hex2dec(Arrays.copyOfRange(LP_DATE, 2, 3)) + ""
							+ DataFormat.hex2dec(Arrays.copyOfRange(LP_DATE, 3, 4)))
					.append(", \n")

					.append("  BASE_PULSE=" + DataFormat.hex2dec(BASE_PULSE)).append(", \n")
					.append("  FW_VERSION=" + DataFormat.hex2dec(FW_VERSION)).append(", \n")

					.append("  FW_BUILD=" + DataFormat.hex2dec(FW_BUILD)).append(", \n")
					.append("  HW_VERSION=" + DataFormat.hex2dec(HW_VERSION)).append(", \n")
					.append("  SW_VERSION=" + DataFormat.hex2dec(SW_VERSION)).append(", \n")
					.append("  LQI=" + DataFormat.hex2dec(LQI)).append(", \n")
					.append("  RSSI=" + DataFormat.hex2dec(RSSI)).append(", \n")
					.append("  NODE_KIND_TYPE=" + DataFormat.hex2dec(NODE_KIND_TYPE)).append(", \n")
					.append("  ALARM_FLAG=" + DataFormat.hex2dec(ALARM_FLAG)).append(", \n")
					.append("  NETWORK_TYPE=" + DataFormat.hex2dec(NETWORK_TYPE)).append(", \n")
					.append("  ENERGY_LEVEL=" + DataFormat.hex2dec(ENERGY_LEVEL)).append(", \n")

					.append("  LP_DATE_2=" + DataFormat.hex2dec(Arrays.copyOfRange(LP_DATE_2, 0, 2)) + ""
							+ DataFormat.hex2dec(Arrays.copyOfRange(LP_DATE_2, 2, 3)) + ""
							+ DataFormat.hex2dec(Arrays.copyOfRange(LP_DATE_2, 3, 4)))
					.append(", \n").append("  LP_BASE_PULSE=" + DataFormat.hex2dec(LP_BASE_PULSE));
			for (int i = 0; i < LP_ARR.length; i++) {
				sb.append("\n").append("    LP_DATA[" + i + "]=" + ((LP_ARR[i] == null) ? "-" :DataFormat.hex2dec(LP_ARR[i])));
			}
		} catch (Exception e) {
			log.error(e,e);
		}

		return "NT509[\n" + sb.toString() + "\n]\n";
	}

	private void setLpPeriod(byte[] byteArr) {
		int val = 0;
		try {
			val = DataFormat.hex2dec(byteArr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		switch (val) {
		case 1:
			lpPeriodMin = 60;
			break;
		case 2:
			lpPeriodMin = 30;
			break;
		case 4:
			lpPeriodMin = 15;
			break;
		default:
			lpPeriodMin = 0;
			break;
		}
	}

	private void setNodeKindType(byte[] byteArr) {
		int val = 0;
		try {
			val = DataFormat.hex2dec(byteArr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		switch (val) {
		case 1:
			nodeKindType = "Gas";
			break;
		case 2:
			nodeKindType = "Water";
			break;
		case 3:
			nodeKindType = "Electronic";
			break;
		case 4:
			nodeKindType = "ACD";
			break;
		case 5:
			nodeKindType = "HMU";
			break;
		case 6:
			nodeKindType = "극동 Water";
			break;
		case 7:
			nodeKindType = "Beacon";
			break;
		case 8:
			nodeKindType = "극동 Gas";
			break;
		case 100:
			nodeKindType = "Smoke Detector";
			break;
		case 255:
			nodeKindType = "Repeater";
			break;
		default:
			nodeKindType = "Unknown";
			break;
		}
	}

	private void setNetworkType(byte[] byteArr) {
		int val = 0;
		try {
			val = DataFormat.hex2dec(byteArr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		switch (val) {
		case 0:
			networkType = "Star";
			break;
		case 1:
			networkType = "Mesh";
			break;
		default:
			networkType = "Unknown";
			break;
		}
	}

	private void setEnergyLevel(byte[] byteArr) {
		int val = 0;
		try {
			val = DataFormat.hex2dec(byteArr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (1 <= val && val <= 15) {
			energyLevel = val;
		} else if (val == 0) {
			energyLevel = -1;
		}
	}

	public int getLpPeriodMin() {
		return lpPeriodMin;
	}

	public String getNodeKindType() {
		return nodeKindType;
	}

	public String getNetworkType() {
		return networkType;
	}

	public int getEnergyLevel() {
		return energyLevel;
	}

	public ArrayList<LPData> getLpData() {
		int lpCnt = (24 * 60) / lpPeriodMin;
		ArrayList<LPData> lpDataList = new ArrayList<>();
		Calendar cal = getLpDate();
		long basePulse = 0L;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		try {
			basePulse = new Integer(DataFormat.hex2dec(LP_BASE_PULSE)).longValue();
			for (int i = 0; i < lpCnt; i++) {
				if(LP_ARR[i] == null) continue;
				Double[] ch = new Double[1];
				ch[0] = new Integer(DataFormat.hex2dec(LP_ARR[i])).doubleValue();
				LPData lpData = new LPData();
				lpData.setDatetime(sdf.format(cal.getTime()));
				lpData.setBasePulse(basePulse);
				lpData.setLPChannelCnt(1);
				lpData.setCh(ch);
				lpDataList.add(lpData);
				cal.add(Calendar.MINUTE, lpPeriodMin);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("lpDataList.size()="+lpDataList.size());
		return lpDataList;
	}
	
	/**
	 * Get formatted frame info date use SimpleDateFormat class 
	 * 
	 * @param pattern SimpleDateFormat pattern 
	 * @return
	 * @throws Exception
	 */
	public String getFrameInfoDateFormat(String pattern) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		Calendar cal = getFrameInfoDate();
		return sdf.format(cal.getTime());
		
	}
	
	public boolean byteArrValidate(byte[] arr) {
		int len = arr.length;
		if(len < 1) return false;
		
		byte[] tmpByteArr = new byte[len];
		for(int i = 0; i < tmpByteArr.length; i++) {
			tmpByteArr[i] = (byte) 0xff;
		}
		return Arrays.equals(arr, tmpByteArr);
	}
	
	public int getDst() throws Exception {
		return Integer.valueOf(DataFormat.hex2dec(DAY));
	}
}
