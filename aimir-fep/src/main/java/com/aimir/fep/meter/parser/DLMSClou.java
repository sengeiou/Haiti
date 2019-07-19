package com.aimir.fep.meter.parser;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.fep.command.conf.DLMSMeta.CONTROL_STATE;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.parser.DLMSClouTable.DLMSTable;
import com.aimir.fep.meter.parser.DLMSClouTable.DLMSVARIABLE;
import com.aimir.fep.meter.parser.DLMSClouTable.DLMSVARIABLE.DLMS_CLASS_ATTR;
import com.aimir.fep.meter.parser.DLMSClouTable.DLMSVARIABLE.ENERGY_LOAD_PROFILE;
import com.aimir.fep.meter.parser.DLMSClouTable.DLMSVARIABLE.METER_DEVICE_MODEL;
import com.aimir.fep.meter.parser.DLMSClouTable.DLMSVARIABLE.OBIS;
import com.aimir.fep.meter.parser.DLMSClouTable.DLMSVARIABLE.RELAY_STATUS_CLOU;
import com.aimir.fep.meter.parser.DLMSKepcoTable.LPComparator;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.EnergyMeter;
import com.aimir.model.system.Supplier;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

public class DLMSClou extends MeterDataParser implements java.io.Serializable {

	private static final long serialVersionUID = 5099915362766709642L;

	private static Log log = LogFactory.getLog(DLMSClou.class);

	LPData[] lpData = null;

	Double[] MeteringDataChannelData = null;

	LinkedHashMap<String, Map<String, Object>> result = new LinkedHashMap<String, Map<String, Object>>();

	String meterID = "";
	String modemID = "";
	String fwVersion = "";
	String meterModel = "";
	String logicalNumber = "";
	String manufactureSerial = "";
	String servicePointSerial = "";
	String meterVendor = "";
	Long ct_ratio = 0L;
	Long vt_ratio = 0L;
	Long ct_den = 0L;
	Long vt_den = 0L;
	Long trans_num = 0L;
	byte[] phaseType = null;
	Long meterStatus = 0L;
	RELAY_STATUS_CLOU relayStatus;
	CONTROL_STATE loadCtrlState;
	int loadCtrlMode;
	Double limiterInfo = 0d;
	Double limiterInfoMin = 0d;
	int modemPort = 0;

	int lpInterval = 60;
	boolean existLpInterval = false;

	double activePulseConstant = 1;

	Double meteringValue = null;
	Double ct = 1d;

	public enum CHANNEL_IDX {
		CUMULATIVE_ACTIVEENERGY_IMPORT(1), CUMULATIVE_ACTIVEENERGY_EXPORT(2), CUMULATIVE_REACTIVEENERGY_IMPORT(
				3), CUMULATIVE_REACTIVEENERGY_EXPORT(
						4), INSTANTANEOUS_VOLTAGE_L1(5), INSTANTANEOUS_VOLTAGE_L2(6), INSTANTANEOUS_VOLTAGE_L3(7);

		private int index;

		CHANNEL_IDX(int index) {
			this.index = index;
		}

		public int getIndex() {
			return this.index;
		}

		public void setIndex(int index) {
			this.index = index;
		}
	}

	@Override
	public byte[] getRawData() {
		return null;
	}

	@Override
	public int getLength() {
		return 0;
	}

	@Override
	public void parse(byte[] data) throws Exception {
		System.out.println("Meter:" + meter.getMdsId() + ", DLMS parse:" + Hex.decode(data));
		log.debug("Meter:" + meter.getMdsId() + ", DLMS parse:" + Hex.decode(data));
		String obisCode = "";
		int clazz = 0;
		int attr = 0;

		int pos = 0;
		int len = 0;
		// DLMS Header OBIS(6), CLASS(1), ATTR(1), LENGTH(2)
		// DLMS Tag Tag(1), DATA or LEN/DATA (*)
		byte[] OBIS = new byte[6];
		byte[] CLAZZ = new byte[2];
		byte[] ATTR = new byte[1];
		byte[] LEN = new byte[2];
		byte[] TAGDATA = null;

		DLMSTable dlms = null;
		while (pos < data.length) {

			dlms = new DLMSTable();
			try {
				System.arraycopy(data, pos, OBIS, 0, OBIS.length);
				pos += OBIS.length;
				obisCode = Hex.decode(OBIS);
				dlms.setObis(obisCode);
				System.out.println("OBIS[" + obisCode + "] = " + dlms.getDlmsHeader().getObis().name());

				System.arraycopy(data, pos, CLAZZ, 0, CLAZZ.length);
				pos += CLAZZ.length;
				clazz = DataUtil.getIntTo2Byte(CLAZZ);
				dlms.setClazz(clazz);
				System.out.println("CLASS[" + clazz + "] = " + dlms.getDlmsHeader().getClazz().name());

				if (dlms.getDlmsHeader().getClazz() == null)
					break;

				System.arraycopy(data, pos, ATTR, 0, ATTR.length);
				pos += ATTR.length;
				attr = DataUtil.getIntToBytes(ATTR);
				dlms.setAttr(attr);
				System.out.println("ATTR[" + attr + "] = " + dlms.getDlmsHeader().getAttr().name());

				System.arraycopy(data, pos, LEN, 0, LEN.length);
				pos += LEN.length;
				len = DataUtil.getIntTo2Byte(LEN);
				System.out.println("LENGTH[" + len + "]");
				dlms.setLength(len);
			} catch (IndexOutOfBoundsException e1) {
				e1.printStackTrace();
				break;
			} catch (Exception e) {
				System.out.println("obisCode=" + obisCode + ", clazz=" + clazz + ", attr=" + attr + ", len=" + len);
				e.printStackTrace();
				continue;
			}

			if (len == 0)
				continue;

			TAGDATA = new byte[len];
			if (pos + TAGDATA.length <= data.length) {
				System.arraycopy(data, pos, TAGDATA, 0, TAGDATA.length);
				pos += TAGDATA.length;
			} else {
				System.arraycopy(data, pos, TAGDATA, 0, data.length - pos);
				pos += data.length - pos;
			}

			System.out.println("TAGDATA=[" + Hex.decode(TAGDATA) + "]");

			dlms.setMeter(meter);
			dlms.parseDlmsTag(TAGDATA);
			Map<String, Object> dlmsData = dlms.getData();
			System.out.println("dlms.getDlmsHeader().getObis() = " + dlms.getDlmsHeader().getObis().name());
			if (dlms.getDlmsHeader().getObis() == DLMSVARIABLE.OBIS.ENERGY_LOAD_PROFILE
					&& dlms.getDlmsHeader().getAttr() == DLMS_CLASS_ATTR.PROFILE_GENERIC_ATTR04) {
				lpInterval = ((Long) dlmsData.get("LpInterval")).intValue() / 60; // sec -> min
				// lpInterval = dlmsData
				result.put(DLMSVARIABLE.OBIS.LP_INTERVAL.getCode(), dlmsData);
				System.out.println("LP_INTERVAL[" + lpInterval + "]");
			}else if (dlms.getDlmsHeader().getObis() == DLMSVARIABLE.OBIS.ENERGY_LOAD_PROFILE) {
				for (int cnt = 0;; cnt++) {
					obisCode = dlms.getDlmsHeader().getObis().getCode() + "-" + cnt;
					if (!result.containsKey(obisCode)) {
						result.put(obisCode, dlmsData);
						break;
					}
				}
			} else {
				result.put(obisCode, dlms.getData());
			}

			//
			// for (int cnt = 0; ;cnt++) {
			// obisCode = dlms.getDlmsHeader().getObis().getCode() + "-" + cnt;
			// if (!result.containsKey(obisCode)) {
			// result.put(obisCode, tempMap);
			// break;
			// }
			// }
			// } else if (dlms.getDlmsHeader().getObis() == DLMSVARIABLE.OBIS.RELAY_STATUS)
			// {
			// Map tempMap = dlms.getData();
			// if ( tempMap.containsKey("Relay Status") ||
			// tempMap.containsKey("LoadControlStatus") ||
			// tempMap.containsKey("LoadControlMode") ){
			// obisCode = dlms.getDlmsHeader().getObis().getCode()+ "-" +
			// dlms.getDlmsHeader().getAttr();
			// }
			// result.put(obisCode, dlms.getData());
			// }
			System.out.println();
		}

		MeterType meterType = MeterType.valueOf(this.getMeter().getMeterType().getName());
		switch (meterType) {
		case EnergyMeter:
			EnergyMeter meter = (EnergyMeter) this.getMeter();
			this.ct = 1.0;
			if (meter != null && meter.getCt() != null && meter.getCt() > 0) {
				ct = meter.getCt();
			}

			setCt(ct);
			break;
		case GasMeter:
			break;
		case WaterMeter:
			break;
		default:
			break;
		}
		System.out.println(result);
		setMeterInfo();
		setLPData();
		setLPChannelData();
		setMeteringValue();
	}

	public void setMeterInfo() {
		// DLMSClouMDSaver.java의 saveMeterInfomation() 참조하면서 연결 필요
		System.out.println("=== setMeterInfo(S) ===");
		try {
			Map<String, Object> map = null;
			map = (Map<String, Object>) result.get(OBIS.DEVICE_INFO.getCode());
			if (map != null) {
				System.out.println("OBIS[" + OBIS.DEVICE_INFO.name() + "]");
				Object obj = null;
				obj = map.get(OBIS.DEVICE_INFO.getName());
				if (obj != null) {
					String hexString = (String) obj;
					byte[] bytes = org.apache.commons.codec.binary.Hex.decodeHex(hexString.toCharArray());
					meterVendor = new String(bytes, "UTF-8");
					log.debug("hexString[" + hexString + "] -> [" + new String(bytes, "UTF-8") + "]");
					System.out.println("hexString[" + hexString + "] -> [" + new String(bytes, "UTF-8") + "]");
				}
			}

			map = (Map<String, Object>) result.get(OBIS.LOGICAL_NUMBER.getCode());
			if (map != null) {
				System.out.println("OBIS[" + OBIS.LOGICAL_NUMBER.name() + "]");
				Object obj = null;
				obj = map.get(OBIS.LOGICAL_NUMBER.getName());
				if (obj != null) {
					String hexString = (String) obj;
					byte[] bytes = org.apache.commons.codec.binary.Hex.decodeHex(hexString.toCharArray());
					log.debug("hexString[" + hexString + "] -> [" + new String(bytes, "UTF-8") + "]");
					System.out.println("hexString[" + hexString + "] -> [" + new String(bytes, "UTF-8") + "]");
				}
			}

			map = (Map<String, Object>) result.get(OBIS.MANUFACTURE_ID.getCode());
			if (map != null) {
				System.out.println("OBIS[" + OBIS.MANUFACTURE_ID.name() + "]");
				Object obj = null;
				obj = map.get(OBIS.MANUFACTURE_ID.getName());
				if (obj != null) {
					String hexString = (String) obj;
					byte[] bytes = org.apache.commons.codec.binary.Hex.decodeHex(hexString.toCharArray());
					log.debug("hexString[" + hexString + "] -> [" + new String(bytes, "UTF-8") + "]");
					System.out.println("hexString[" + hexString + "] -> [" + new String(bytes, "UTF-8") + "]");
				}
			}

			map = (Map<String, Object>) result.get(OBIS.METER_TYPE.getCode());
			if (map != null) {
				System.out.println("OBIS[" + OBIS.METER_TYPE.name() + "]");
				Object obj = null;
				obj = map.get(OBIS.METER_TYPE.getName());
				if (obj != null) {
					System.out.println("Value[" + (int) obj + "]");
				}
			}

			map = (Map<String, Object>) result.get(OBIS.CATEGOTY.getCode());
			if (map != null) {
				System.out.println("OBIS[" + OBIS.CATEGOTY.name() + "]");
				Object obj = null;
				obj = map.get(OBIS.CATEGOTY.getName());
				if (obj != null) {
					System.out.println("Value[" + (int) obj + "]");
				}
			}

			map = (Map<String, Object>) result.get(OBIS.APP_FW_ID.getCode());
			if (map != null) {
				System.out.println("OBIS[" + OBIS.APP_FW_ID.name() + "]");
				Object obj = null;
				obj = map.get(OBIS.APP_FW_ID.getName());
				if (obj != null) {
					String hexString = (String) obj;
					byte[] bytes = org.apache.commons.codec.binary.Hex.decodeHex(hexString.toCharArray());
					log.debug("hexString[" + hexString + "] -> [" + new String(bytes, "UTF-8") + "]");
					System.out.println("hexString[" + hexString + "] -> [" + new String(bytes, "UTF-8") + "]");
				}
			}

			map = (Map<String, Object>) result.get(OBIS.METER_SERIAL_NUMBER.getCode());
			if (map != null) {
				System.out.println("OBIS[" + OBIS.METER_SERIAL_NUMBER.name() + "]");
				Object obj = null;
				obj = map.get(OBIS.METER_SERIAL_NUMBER.getName());
				if (obj != null) {
					String hexString = (String) obj;
					byte[] bytes = org.apache.commons.codec.binary.Hex.decodeHex(hexString.toCharArray());
					meterID = new String(bytes, "UTF-8");
					log.debug("hexString[" + hexString + "] -> meterID[" + meterID + "]");
					System.out.println("hexString[" + hexString + "] -> meterID[" + meterID + "]");
				}
				log.debug("METER_SERIAL_NUMBER[" + meterID + "]");
			}

			map = (Map<String, Object>) result.get(OBIS.METER_TIME.getCode());
			if (map != null) {
				Object obj = map.get(OBIS.METER_TIME.getName());
				if (obj != null) {
					meterTime = (String) obj;
					if (meterTime.length() == 12)
						meterTime = meterTime + "00";
					meter.setLastReadDate(meterTime);
					log.debug("METER_TIME[" + meterTime + "]");
				}
			}
			map = (Map<String, Object>) result.get(OBIS.METER_VENDOR.getCode());
			if (map != null) {
				Object obj = null;
				obj = map.get(OBIS.METER_VENDOR.getName());
				if (obj != null)
					meterVendor = (String) obj;
				log.debug("METER_VENDOR[" + meterVendor + "]");
			}
			map = (Map<String, Object>) result.get(OBIS.MANUFACTURE_SERIAL.getCode());
			if (map != null) {
				Object obj = map.get(OBIS.MANUFACTURE_SERIAL.getName());
				if (obj != null)
					manufactureSerial = (String) obj;
				log.debug("MANUFACTURE_SERIAL[" + manufactureSerial + "]");
			}
			map = (Map<String, Object>) result.get(OBIS.METER_MODEL.getCode());
			if (map != null) {
				Object obj = map.get(OBIS.METER_MODEL.getName());
				if (obj != null)
					meterModel = (String) obj;
				this.meterModel = meterModel;
				log.debug("METER_MODEL[" + meterModel + "]");
			}
			map = (Map<String, Object>) result.get(OBIS.LOGICAL_NUMBER.getCode());
			if (map != null) {
				Object obj = map.get(OBIS.LOGICAL_NUMBER.getName());
				if (obj != null)
					logicalNumber = (String) obj;
				log.debug("LOGICAL_NUMBER[" + logicalNumber + "]");
			}
			map = (Map<String, Object>) result.get(OBIS.FW_VERSION.getCode());
			if (map != null) {
				Object obj = map.get(OBIS.FW_VERSION.getName());
				if (obj != null)
					fwVersion = (String) obj;
				log.debug("FW_VERSION[" + fwVersion + "]");
			}
			map = (Map<String, Object>) result.get(OBIS.CT_RATIO.getCode());
			if (map != null) {
				Object obj = map.get(OBIS.CT_RATIO.getName());
				if (obj instanceof OCTET) {
					log.debug("CT_RATIO[null]");
					ct_ratio = null;
				} else {
					if (obj != null)
						ct_ratio = (Long) obj;
					log.debug("CT_RATIO[" + ct_ratio + "]");
				}
			}
			map = (Map<String, Object>) result.get(OBIS.PT_RATIO.getCode());
			if (map != null) {
				Object obj = map.get(OBIS.PT_RATIO.getName());
				if (obj instanceof OCTET) {
					log.debug("PT_RATIO[null]");
					vt_ratio = null;
				} else {
					if (obj != null)
						vt_ratio = (Long) obj;
					log.debug("PT_RATIO[" + vt_ratio + "]");
				}
			}
			map = (Map<String, Object>) result.get(OBIS.ALARM_OBJECT.getCode());
			if (map != null) {
				Object obj = map.get(OBIS.ALARM_OBJECT.getName());
				if (obj != null)
					meterStatus = (Long) obj;
				log.debug("METER_STATUS(ALARM_OBJECT[" + meterStatus + "])");
			}
			map = (Map<String, Object>) result
					.get(OBIS.RELAY_STATUS.getCode() + "-" + DLMSVARIABLE.DLMS_CLASS_ATTR.REGISTER_ATTR02);
			if (map != null) {
				Object obj = map.get("Relay Status");
				if (obj != null)
					relayStatus = (RELAY_STATUS_CLOU) obj;
				log.debug("RELAY STATUS([" + relayStatus.getCode() + "]");
			}
			map = (Map<String, Object>) result
					.get(OBIS.RELAY_STATUS.getCode() + "-" + DLMSVARIABLE.DLMS_CLASS_ATTR.REGISTER_ATTR03);
			if (map != null) {
				Object obj = map.get("LoadControlStatus");
				if (obj != null)
					loadCtrlState = (CONTROL_STATE) obj;
				log.debug("RELAY LOAD CONTROL STATE([" + loadCtrlState.getCode() + "]");
			}
			map = (Map<String, Object>) result
					.get(OBIS.RELAY_STATUS.getCode() + "-" + DLMSVARIABLE.DLMS_CLASS_ATTR.REGISTER_ATTR04);
			if (map != null) {
				Object obj = map.get("LoadControlMode");
				if (obj != null)
					loadCtrlMode = (int) obj;
				log.debug("RELAY LOAD CONTROL MODE([" + loadCtrlMode + "]");
			}
			map = (Map<String, Object>) result.get(OBIS.LIMITER_INFO.getCode());
			if (map != null) {
				Object obj = map.get("LimiterInfo");
				if (obj != null)
					limiterInfo = (Double) obj;
				log.debug("LimiterInfo([" + limiterInfo + "]");
			}
			map = (Map<String, Object>) result.get(OBIS.LIMITER_INFO.getCode());
			if (map != null) {
				Object obj = map.get("LimiterInfoMin");
				if (obj != null)
					limiterInfoMin = (Double) obj;
				log.debug("LimiterInfo Min([" + limiterInfoMin + "]");
			}
		} catch (Exception e) {
			log.error(e, e);
		} finally {
			log.debug(meter.getMdsId() + " setMeterInfo finished");
			System.out.println("=== setMeterInfo(E) ===");
		}
	}

	public void setLPData() {
		try {
			List<LPData> lpDataList = new ArrayList<LPData>();
			METER_DEVICE_MODEL meterMo = DLMSVARIABLE.getMeterDeviceModel(meter.getModel().getName());

			Double lp = 0.0;
			Object value = null;
			Map<String, Object> lpMap = null;
			int cnt = 0;
			LPData _lpData = null;

			double activeEnergyImport = 0.0;
			double activeEnergyExport = 0.0;
			double intervalactiveEnergyImport = 0.0;
			double intervalactiveEnergyExport = 0.0;
			double averageDemandValueImport = 0.0;
			double averageDemandValueExport = 0.0;

			for (int i = 0; i < result.size(); i++) {
				if (!result.containsKey(OBIS.ENERGY_LOAD_PROFILE.getCode() + "-" + i)) {
					System.out.println("break!");
					break;
				}

				lpMap = (Map<String, Object>) result.get(OBIS.ENERGY_LOAD_PROFILE.getCode() + "-" + i);
				cnt = 0;

				while (true) {
					value = lpMap.get(ENERGY_LOAD_PROFILE.ActiveEnergyImport.name() + "-" + cnt);
					if (value == null)
						break;

					if (value != null) {
						if (value instanceof OCTET)
							lp = (double) DataUtil.getLongToBytes(((OCTET) value).getValue());
						else if (value instanceof Long)
							lp = ((Long) value).doubleValue();

						activeEnergyImport = (lp / activePulseConstant) * 0.01;
					}

					value = lpMap.get(ENERGY_LOAD_PROFILE.ActiveEnergyExport.name() + "-" + cnt);
					if (value != null) {
						if (value instanceof OCTET)
							activeEnergyExport = (double) DataUtil.getLongToBytes(((OCTET) value).getValue());
						else if (value instanceof Long)
							activeEnergyExport = ((Long) value).doubleValue();

						activeEnergyExport /= activePulseConstant;
						activeEnergyExport *= 0.01;
					}

					value = lpMap.get(ENERGY_LOAD_PROFILE.IntervalActiveEnergyImport.name() + "-" + cnt);
					if (value != null) {
						if (value instanceof OCTET)
							intervalactiveEnergyImport = (double) DataUtil.getLongToBytes(((OCTET) value).getValue());
						else if (value instanceof Long)
							intervalactiveEnergyImport = ((Long) value).doubleValue();

						intervalactiveEnergyImport *= 0.01;
					}

					value = lpMap.get(ENERGY_LOAD_PROFILE.IntervalActiveEnergyExport.name() + "-" + cnt);
					if (value != null) {
						if (value instanceof OCTET)
							intervalactiveEnergyExport = (double) DataUtil.getLongToBytes(((OCTET) value).getValue());
						else if (value instanceof Long)
							intervalactiveEnergyExport = ((Long) value).doubleValue();

						intervalactiveEnergyExport *= 0.01;
					}

					value = lpMap.get(ENERGY_LOAD_PROFILE.AverageDemandValueImport.name() + "-" + cnt);
					if (value != null) {
						if (value instanceof OCTET)
							averageDemandValueImport = (double) DataUtil.getLongToBytes(((OCTET) value).getValue());
						else if (value instanceof Long)
							averageDemandValueImport = ((Long) value).doubleValue();

						averageDemandValueImport *= 0.01;
					}

					value = lpMap.get(ENERGY_LOAD_PROFILE.AverageDemandValueExport.name() + "-" + cnt);
					if (value != null) {
						if (value instanceof OCTET)
							averageDemandValueExport = (double) DataUtil.getLongToBytes(((OCTET) value).getValue());
						else if (value instanceof Long)
							averageDemandValueExport = ((Long) value).doubleValue();

						averageDemandValueExport *= 0.01;
					}

					// Get Meter Time & Operation Time
					Long lmeteringTime = meteringTime != null
							? DateTimeUtil.getDateFromYYYYMMDDHHMMSS(meteringTime).getTime()
							: new Date().getTime();
					;
					Long lmeterTime = meterTime != null ? DateTimeUtil.getDateFromYYYYMMDDHHMMSS(meterTime).getTime()
							: lmeteringTime;

					_lpData = new LPData((String) lpMap.get(ENERGY_LOAD_PROFILE.Date.name() + "-" + cnt), lp,
							activeEnergyImport);
					_lpData.setPF(1d);
					switch (meterMo) {
					case CL710K22:
						_lpData.setCh(new Double[] { activeEnergyImport, activeEnergyExport, intervalactiveEnergyImport,
								intervalactiveEnergyExport, averageDemandValueImport, averageDemandValueExport,
								lmeterTime.doubleValue(), lmeteringTime.doubleValue() });
						break;
					case CL730S22:
						break;
					case CL730D22H:
						break;
					case CL730D22L:
						break;
					}
					/*
					 * value = lpMap.get(ENERGY_LOAD_PROFILE.Status.name()+"-"+cnt++); if (value !=
					 * null) { if (value instanceof OCTET){
					 * _lpData.setFlag((int)DataUtil.getIntToBytes(((OCTET)value).getValue()));
					 * _lpData.setStatus(String.valueOf(value)); } else if (value instanceof
					 * Integer){ _lpData.setFlag(((Integer)value).intValue());
					 * _lpData.setStatus(String.valueOf(value)); } }
					 */

					if (_lpData.getDatetime() != null && !_lpData.getDatetime().substring(0, 4).equals("1792")) {
						lpDataList.add(_lpData);
						System.out.println(_lpData.toString());
					} else {
						try {
							EventUtil.sendEvent("Meter Value Alarm",
									TargetClass.valueOf(meter.getMeterType().getName()), meter.getMdsId(),
									new String[][] {
											{ "message", "Wrong Date LP, DateTime[" + _lpData.getDatetime() + "]" } });
						} catch (Exception ignore) {
						}
					}

					Collections.sort(lpDataList, LPComparator.TIMESTAMP_ORDER);
					lpDataList = checkDupLPAndWrongLPTime(lpDataList);
					lpData = lpDataList.toArray(new LPData[0]); // INSERT SP-501 (Uncomment)
					log.debug("########################lpData.length:" + lpData.length);

					cnt++;
				}

			}

		} catch (Exception e) {
			log.error(e, e);
		} finally {
			log.debug(meter.getMdsId() + " setLPData finished");
		}
	}

	public void setLPChannelData() {

	}

	public void setMeteringValue() {
		try {
			if (lpData != null && lpData.length > 0) {
				meteringValue = lpData[lpData.length - 1].getLpValue();
			}
			System.out.println("METERING_VALUE[" + meteringValue + "]");
		} catch (Exception e) {
			log.error(e, e);
		}
	}

	@Override
	public Double getMeteringValue() {
		return meteringValue;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		try {
			builder.append("mdsId:" + this.getMeter().getMdsId());
			builder.append(", meteringValue:" + this.getMeteringValue());
			builder.append(", deviceType:" + this.getDeviceType());
			builder.append(", deviceId:" + this.getDeviceId());
			builder.append(", mdevType:" + this.getMDevType());
			builder.append(", mdevId:" + this.getMDevId());
			builder.append(", meteringTime:" + this.getMeteringTime());
		} catch (Exception e) {

		}

		return builder.toString();
	}

	@Override
	public int getFlag() {
		return 0;
	}

	public int getLpInterval() {
		return lpInterval;
	}

	@Override
	public void setFlag(int flag) {

	}

	public Double getCt() {
		return ct;
	}

	public void setCt(Double ct) {
		this.ct = ct;
	}

	/**
	 * Time consistency check
	 * 
	 * @param chkDate
	 * @return
	 */
	private boolean checkLpDataTime(String chkDate) {
		boolean ret = true;
		String cd = chkDate.substring(8, 12);
		if ("5255".equals(cd)) {
			ret = false;
		} else {
			// Time check
			String hh = cd.substring(0, 2);
			String mm = cd.substring(2, 4);
			if (Integer.parseInt(hh) > 23) {
				ret = false;
			} else if (Integer.parseInt(mm) > 59) {
				ret = false;
			}
		}

		return ret;
	}

	private List<LPData> checkDupLPAndWrongLPTime(List<LPData> list) throws Exception {
		List<LPData> totalList = list;
		List<LPData> removeList = new ArrayList<LPData>();
		LPData prevLPData = null;

		for (int i = 0; i < list.size(); i++) {
			// SP-783
			// Time consistency check
			if (!checkLpDataTime(list.get(i).getDatetime())) {
				removeList.add(list.get(i));
				try {
					EventUtil.sendEvent("Meter Value Alarm", TargetClass.valueOf(meter.getMeterType().getName()),
							meter.getMdsId(), new String[][] {
									{ "message", "Wrong Date LP, DateTime[" + list.get(i).getDatetime() + "]" } });
				} catch (Exception ignore) {
				}
			} else {
				if (prevLPData != null && prevLPData.getDatetime() != null && !prevLPData.getDatetime().equals("")) {
					if (list.get(i).getDatetime().equals(prevLPData.getDatetime())
							&& list.get(i).getCh()[0].equals(prevLPData.getCh()[0])) {
						// log.warn("time equls:" +list.get(i).getDatetime());
						removeList.add(list.get(i));
						try {
							EventUtil.sendEvent("Meter Value Alarm",
									TargetClass.valueOf(meter.getMeterType().getName()), meter.getMdsId(),
									new String[][] { { "message", "Duplicate LP, DateTime[" + list.get(i).getDatetime()
											+ "] LP Val[" + list.get(i).getCh()[0] + "]" } });
						} catch (Exception ignore) {
						}

					} else if (list.get(i).getDatetime().equals(prevLPData.getDatetime())
							&& list.get(i).getCh()[0] > prevLPData.getCh()[0]) {
						System.out.println("time equls:" + list.get(i).getDatetime());
						removeList.add(list.get(i - 1));
						try {
							EventUtil.sendEvent("Meter Value Alarm",
									TargetClass.valueOf(meter.getMeterType().getName()), meter.getMdsId(),
									new String[][] { { "message",
											"Duplicate LP and Diff Value DateTime[" + list.get(i).getDatetime()
													+ "] LP Val[" + list.get(i).getCh()[0] + "/" + prevLPData.getCh()[0]
													+ "]" } });
						} catch (Exception ignore) {
						}
					} else if (list.get(i).getDatetime().equals(prevLPData.getDatetime())
							&& list.get(i).getCh()[0] < prevLPData.getCh()[0]) {
						System.out.println("time equls:" + list.get(i).getDatetime());
						removeList.add(list.get(i));
						try {
							EventUtil.sendEvent("Meter Value Alarm",
									TargetClass.valueOf(meter.getMeterType().getName()), meter.getMdsId(),
									new String[][] { { "message",
											"Duplicate LP and Diff Value DateTime[" + list.get(i).getDatetime()
													+ "] LP Val[" + list.get(i).getCh()[0] + "/" + prevLPData.getCh()[0]
													+ "]" } });
						} catch (Exception ignore) {
						}
					}

				}
				prevLPData = list.get(i);

				if (list.get(i).getDatetime().startsWith("1994") || list.get(i).getDatetime().startsWith("2000")
						|| (list.get(i).getDatetime().startsWith("2057")
								&& !TimeUtil.getCurrentTime().startsWith("205"))) {
					removeList.add(list.get(i));
					try {
						EventUtil.sendEvent("Meter Value Alarm", TargetClass.valueOf(meter.getMeterType().getName()),
								meter.getMdsId(), new String[][] {
										{ "message", "Wrong Date LP, DateTime[" + list.get(i).getDatetime() + "]" } });
					} catch (Exception ignore) {
					}
				}
				if (meterTime != null && !"".equals(meterTime) && meterTime.length() == 14
						&& list.get(i).getDatetime().compareTo(meterTime.substring(0, 12)) > 0) {
					removeList.add(list.get(i));
					try {
						EventUtil.sendEvent("Meter Value Alarm", TargetClass.valueOf(meter.getMeterType().getName()),
								meter.getMdsId(), new String[][] { { "message", "Wrong Date LP, DateTime["
										+ list.get(i).getDatetime() + "] Meter Time[" + meterTime + "]" } });
					} catch (Exception ignore) {
					}
				}

				Long lpTime = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(list.get(i).getDatetime() + "00").getTime();
				Long serverTime = new Date().getTime();
				;

				if (lpTime > serverTime) {
					try {
						EventUtil.sendEvent("Meter Value Alarm", TargetClass.valueOf(meter.getMeterType().getName()),
								meter.getMdsId(),
								new String[][] { { "message", "Wrong Date LP, DateTime[" + list.get(i).getDatetime()
										+ "] Current Time[" + TimeUtil.getCurrentTime() + "]" } });
					} catch (Exception ignore) {
					}
				}
			}
		}

		totalList.removeAll(removeList);
		return totalList;
	}

	public String getFwVersion() {
		return fwVersion;
	}

	public Long getCtRatio() {
		return ct_ratio;
	}

	public Long getVtRatio() {
		return vt_ratio;
	}

    public LinkedHashMap<String, Object> getRelayStatus() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        Map<String, Object> disconectCtrlMap = result.get(OBIS.RELAY_STATUS.getCode());
//        Map<String, Object> disconectCtrlMap = result.get(OBIS.RELAY_STATUS.getCode() + "-" + DLMSVARIABLE.DLMS_CLASS_ATTR.REGISTER_ATTR02);
        
        log.info("result   :   "+result.toString());
        log.info("disconectCtrlMap   :   "+disconectCtrlMap.toString());
        
        if ( disconectCtrlMap!=null && disconectCtrlMap.get("Relay Status") != null ) {
            log.debug("disconectCtrlMap ==>>>>> " + disconectCtrlMap.toString());
        	map.put("Relay Status", disconectCtrlMap.get("Relay Status"));
        }
        
        disconectCtrlMap = result.get(OBIS.RELAY_STATUS.getCode() + "-" + DLMSVARIABLE.DLMS_CLASS_ATTR.REGISTER_ATTR03);
        if ( disconectCtrlMap!=null && disconectCtrlMap.get("LoadControlStatus") != null ){
            log.debug("disconectCtrlMap ==>>>>> " + disconectCtrlMap.toString());
            map.put("LoadControlStatus", disconectCtrlMap.get("LoadControlStatus"));
        }
        
        disconectCtrlMap = result.get(OBIS.RELAY_STATUS.getCode() + "-" + DLMSVARIABLE.DLMS_CLASS_ATTR.REGISTER_ATTR04);
        if ( disconectCtrlMap!=null && disconectCtrlMap.get("LoadControlMode") != null ){
            log.debug("disconectCtrlMap ==>>>>> " + disconectCtrlMap.toString());
            map.put("LoadControlMode", disconectCtrlMap.get("LoadControlMode"));
        }
        return map;
    }

	public LPData[] getLpData() {
		return lpData;
	}

	@Override
	public LinkedHashMap<?, ?> getData() {
		Map<String, Object> data = new LinkedHashMap<String, Object>(16, 0.75f, false);
		Map<String, Object> resultSubData = null;
		String key = null;

		DecimalFormat decimalf = null;
		SimpleDateFormat datef14 = null;

		if (meter != null && meter.getSupplier() != null) {
			Supplier supplier = meter.getSupplier();
			if (supplier != null) {
				String lang = supplier.getLang().getCode_2letter();
				String country = supplier.getCountry().getCode_2letter();

				decimalf = TimeLocaleUtil.getDecimalFormat(supplier);
				TimeLocaleUtil.setSupplier(supplier); // INSERT SP-884
				datef14 = new SimpleDateFormat(TimeLocaleUtil.getDateFormat(14, lang, country));
			}
		} else {
			// locail, If no information is to use the default format.
			decimalf = new DecimalFormat();
			datef14 = new SimpleDateFormat();
		}

		for (Iterator i = result.keySet().iterator(); i.hasNext();) {
			key = (String) i.next();
			resultSubData = result.get(key);
			// Before the conquest of many recent results typically only put.
			// if (key.startsWith(DLMSVARIABLE.OBIS.POWER_FAILURE.getCode()) ||
			// key.startsWith(DLMSVARIABLE.OBIS.POWER_RESTORE.getCode())) {
			//
			// }
			// else {
			if (resultSubData != null) {
				String idx = "";
				if (key.lastIndexOf("-") != -1) {
					idx = key.substring(key.lastIndexOf("-") + 1);
					key = key.substring(0, key.lastIndexOf("-"));
				}
				String subkey = null;
				Object subvalue = null;
				for (Iterator subi = resultSubData.keySet().iterator(); subi.hasNext();) {
					subkey = (String) subi.next();
					if (!subkey.contains(DLMSVARIABLE.UNDEFINED)) {
						subvalue = resultSubData.get(subkey);
						if (subvalue instanceof String) {
							if (((String) subvalue).contains(":date=")) {
								try {
									data.put(OBIS.getObis(key).getName() + idx + " : " + subkey,
											datef14.format(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(
													((String) subvalue).substring(6) + "00")));
								} catch (Exception e) {
									data.put(OBIS.getObis(key).getName() + idx + " : " + subkey, subvalue);
								}
							} else if (subkey.contains("Date") && !((String) subvalue).contains(":date=")
									&& ((String) subvalue).length() == 12) {
								try {
									data.put(OBIS.getObis(key).getName() + idx + " : " + subkey,
											datef14.format(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(subvalue + "00")));
								} catch (Exception e) {
									data.put(OBIS.getObis(key).getName() + idx + " : " + subkey, subvalue);
								}
							}
						} else if (subvalue instanceof Number) {
							if (modemPort > 0 && (OBIS.getObis(key).getName() + idx).toString()
									.startsWith(OBIS.MBUSMASTER_LOAD_PROFILE.getName().toString())) {
								if (subkey.startsWith("Ch" + modemPort)) {
									data.put(OBIS.getObis(key).getName() + idx + " : " + subkey,
											decimalf.format(subvalue));
								}
							} else {
								if ((OBIS.getObis(key).getName() + idx).toString()
										.startsWith(OBIS.ENERGY_LOAD_PROFILE.getName().toString())
										&& (subkey.startsWith("ActiveEnergyImport")
												|| subkey.startsWith("ActiveEnergyExport")
												|| subkey.startsWith("ReactiveEnergyImport")
												|| subkey.startsWith("ReactiveEnergyExport"))) {
									Double dvalue = ((Number) subvalue).intValue() * 0.001;
									data.put(OBIS.getObis(key).getName() + idx + " : " + subkey,
											decimalf.format(((Number) subvalue).intValue() * 0.001));
								} else if ((OBIS.getObis(key).getName() + idx).toString()
										.startsWith(OBIS.ENERGY_LOAD_PROFILE.getName().toString())
										&& (subkey.startsWith("Status"))) {
									data.put(OBIS.getObis(key).getName() + idx + " : " + subkey,
											DLMSTable.getLP_STATUS(new byte[] { ((Number) subvalue).byteValue() }));
								} else {
									data.put(OBIS.getObis(key).getName() + idx + " : " + subkey,
											decimalf.format(subvalue));
								}
							}
						} else {
							String valueStr = subvalue.toString();
							if (valueStr != null && !valueStr.matches("\\p{Print}*")) {
								valueStr = Hex.decode(valueStr.getBytes());
								// log.debug("Key = " + OBIS.getObis(key).getName()+idx+" : "+subkey +
								// ", Class = " + subvalue.getClass().getName() +
								// ", Value = " + valueStr);
							}
							if (valueStr == null) {
								valueStr = "";
							}
							OBIS _obis = OBIS.getObis(key);
							if (_obis != null)
								data.put(_obis.getName() + idx + " : " + subkey, valueStr);
							else {
								log.warn("OBIS[" + key + "] not exist");
							}
						}
					}
				}
			}
		}
		// }
		return (LinkedHashMap) data;
	}

}
