package com.aimir.fep.meter.parser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.parser.DLMSKepcoTable.LPComparator;
import com.aimir.fep.meter.parser.ModbusInverterTable.ModbusInverterDefaultVariable.MODBUS_DEFAULT_CODE;
import com.aimir.fep.meter.parser.ModbusInverterTable.ModbusInverterF500Variable.MODBUS_ROCKWELL_CODE;
import com.aimir.fep.meter.parser.ModbusInverterTable.ModbusInverteriP5AVariable.MODBUS_LS_IP5A_CODE;
import com.aimir.fep.meter.parser.ModbusInverterTable.ModbusInverterIS7variable.MODBUS_LS_IS7_CODE;
import com.aimir.fep.meter.parser.ModbusInverterTable.ModbusInverterN700EVariable.MODBUS_HYUNDAI_CODE;
import com.aimir.fep.meter.parser.ModbusInverterTable.ModebusDefaultInverterTable;
import com.aimir.fep.meter.parser.ModbusInverterTable.ModebusInverterCommonTable;
import com.aimir.fep.protocol.emnv.exception.EMnVSystemException;
import com.aimir.fep.protocol.emnv.exception.EMnVSystemException.EMnVExceptionReason;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants.EMnVMeteringDataType;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants.EMnVModebusVendorModelType;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants.EMnVModebusVendorType;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeUtil;

public class ModbusEMnVDefault extends MeterDataParser implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(ModbusEMnVDefault.class);
	private EMnVMeteringDataType meteringDataType; // Billing or Load Profile
	private LPData[] lpData = null;
	private EMnVModebusVendorType vendorType;
	private EMnVModebusVendorModelType inverterModelType;
	private int portNumber = -1;  // defalut = -1;
	private String stationId;
	private int toatalLPCount;

	Map<String, Object> inverterDataMap;
	LinkedHashMap<MODBUS_DEFAULT_CODE, Map<String, Object>> result = new LinkedHashMap<MODBUS_DEFAULT_CODE, Map<String, Object>>();

	@Override
	public void parse(byte[] data) throws Exception {
		log.info("    ");
		log.info("    ");
		log.info("    ");
		log.info("################ Default Inverter 로그확인 시작 #########################");
		log.info("ModbusEMnV-DEFAULT parse:[{}] [{}]", meteringDataType != null ? meteringDataType.name() : "", Hex.decode(data));

		int pos = 0;
		toatalLPCount = (data.length - EMnVConstants.MODEBUS_PORT_NUM_LEN - EMnVConstants.MODBUS_VENDOR_TYPE_LEN 
				- EMnVConstants.MODEBUS_INVERTER_MODEL_LEN - EMnVConstants.MODBUS_STATION_BYTE_LEN) 
				/ (EMnVConstants.MODEBUS_DATA_TIME_LEN + EMnVConstants.MODEBUS_FREQUENCY_LEN 
						+ EMnVConstants.MODEBUS_VOLTAGE_LEN + EMnVConstants.MODEBUS_CURRENT_LEN);

		// Port Number
		byte[] portNumber_byte = new byte[EMnVConstants.MODEBUS_PORT_NUM_LEN];
		System.arraycopy(data, pos, portNumber_byte, 0, portNumber_byte.length);
		portNumber = Integer.parseInt(Hex.getHexDump(portNumber_byte).trim());
		pos += EMnVConstants.MODEBUS_PORT_NUM_LEN;
		log.info("[PROTOCOL][MODBUS] PORT_NUM(1byte):[{}] ==> HEX=[{}]", portNumber, Hex.decode(portNumber_byte));

		// Vendor Type
		byte[] vendorType_byte = new byte[EMnVConstants.MODBUS_VENDOR_TYPE_LEN];
		System.arraycopy(data, pos, vendorType_byte, 0, vendorType_byte.length);
		vendorType = EMnVModebusVendorType.getItem(vendorType_byte[0]);
		pos += EMnVConstants.MODBUS_VENDOR_TYPE_LEN;
		log.info("[PROTOCOL][MODBUS] VENDOER_TYPE(1byte):[{}] ==> HEX=[{}]", vendorType.getDesc(), Hex.decode(vendorType_byte));

		// Inverter Model
		byte[] inverterModel_byte = new byte[EMnVConstants.MODEBUS_INVERTER_MODEL_LEN];
		System.arraycopy(data, pos, inverterModel_byte, 0, inverterModel_byte.length);

		switch (vendorType) {
		case HYUNDAI:
			// 인버터 모델을 사용하지 않고 있음. 추후 추가시 이용할것.
			inverterModelType = EMnVModebusVendorModelType.DEFAULT;
			break;
		case LS:
			inverterModelType = EMnVModebusVendorModelType.getItem(Hex.decode(inverterModel_byte));
			break;
		case ROCKWELL:
			// 인버터 모델을 사용하지 않고 있음. 추후 추가시 이용할것.
			inverterModelType = EMnVModebusVendorModelType.DEFAULT;
			break;
		default:
			break;
		}

		pos += EMnVConstants.MODEBUS_INVERTER_MODEL_LEN;
		log.info("[PROTOCOL][MODBUS] INVERTER_MODEL(2byte):[{}] ==> HEX=[{}]", inverterModelType.getDesc(), Hex.decode(inverterModel_byte));

		// Station id
		byte[] station_byte = new byte[EMnVConstants.MODBUS_STATION_BYTE_LEN];
		System.arraycopy(data, pos, station_byte, 0, station_byte.length);
		stationId = Hex.getHexDump(station_byte).trim();
		pos += EMnVConstants.MODBUS_STATION_BYTE_LEN;
		log.info("[PROTOCOL][MODBUS] STATION_BYTE(1byte):[{}] ==> HEX=[{}]", stationId, Hex.decode(station_byte));

		ModebusDefaultInverterTable mTable = new ModebusDefaultInverterTable();
		inverterDataMap = mTable.setData(pos, data, toatalLPCount); // return 값 : 진행된 pos

		log.debug("### MODBUS_DEFAULT_CODE TOTAL ==> {}", inverterDataMap.toString());

		setInverterInfo();
	}

	private Map<String, Object> setMCodeResult(MODBUS_DEFAULT_CODE mCode) throws EMnVSystemException {
		Map<String, Object> mMap = new HashMap<String, Object>();

		switch (mCode) {
		case OUTPUT_FREQUENCY:
			switch (vendorType) {
			case HYUNDAI:
				mMap.put("UNIT_CONST", MODBUS_HYUNDAI_CODE.OUTPUT_FREQUENCY_MONITOR.getUnitConst());
				mMap.put("UNIT", MODBUS_HYUNDAI_CODE.OUTPUT_FREQUENCY_MONITOR.getUnit());
				break;
			case LS:
				switch (inverterModelType) {
				case LS_IP5A:
					mMap.put("UNIT_CONST", MODBUS_LS_IP5A_CODE.OUTPUT_FREQUENCY.getUnitConst());
					mMap.put("UNIT", MODBUS_LS_IP5A_CODE.OUTPUT_FREQUENCY.getUnit());
					break;
				case LS_IS7:
					mMap.put("UNIT_CONST", MODBUS_LS_IS7_CODE.OUTPUT_FREQUENCY.getUnitConst());
					mMap.put("UNIT", MODBUS_LS_IS7_CODE.OUTPUT_FREQUENCY.getUnit());
					break;
				case DEFAULT:
					throw new EMnVSystemException(EMnVExceptionReason.UNREGISTERD_INVERTER_MODEL);
				default:
					break;
				}
				break;
			case ROCKWELL:
				mMap.put("UNIT_CONST", MODBUS_ROCKWELL_CODE.OUTPUT_FREQUENCY.getUnitConst());
				mMap.put("UNIT", MODBUS_ROCKWELL_CODE.OUTPUT_FREQUENCY.getUnit());
				break;
			default:
				break;
			}
			break;
		case OUTPUT_CURRENT:
			switch (vendorType) {
			case HYUNDAI:
				mMap.put("UNIT_CONST", MODBUS_HYUNDAI_CODE.OUTPUT_CURRENT_MONITOR.getUnitConst());
				mMap.put("UNIT", MODBUS_HYUNDAI_CODE.OUTPUT_CURRENT_MONITOR.getUnit());
				break;
			case LS:
				switch (inverterModelType) {
				case LS_IP5A:
					mMap.put("UNIT_CONST", MODBUS_LS_IP5A_CODE.OUTPUT_CURRENT.getUnitConst());
					mMap.put("UNIT", MODBUS_LS_IP5A_CODE.OUTPUT_CURRENT.getUnit());
					break;
				case LS_IS7:
					mMap.put("UNIT_CONST", MODBUS_LS_IS7_CODE.OUTPUT_CURRENT.getUnitConst());
					mMap.put("UNIT", MODBUS_LS_IS7_CODE.OUTPUT_CURRENT.getUnit());
					break;
				case DEFAULT:
					throw new EMnVSystemException(EMnVExceptionReason.UNREGISTERD_INVERTER_MODEL);
				default:
					break;
				}
				break;
			case ROCKWELL:
				mMap.put("UNIT_CONST", MODBUS_ROCKWELL_CODE.OUTPUT_CURRENT.getUnitConst());
				mMap.put("UNIT", MODBUS_ROCKWELL_CODE.OUTPUT_CURRENT.getUnit());
				break;
			default:
				break;
			}
			break;
		case OUTPUT_VOLTAGE:
			switch (vendorType) {
			case HYUNDAI:
				mMap.put("UNIT_CONST", MODBUS_HYUNDAI_CODE.OUTPUT_VOLTAGE_MONITOR.getUnitConst());
				mMap.put("UNIT", MODBUS_HYUNDAI_CODE.OUTPUT_VOLTAGE_MONITOR.getUnit());
				break;
			case LS:
				switch (inverterModelType) {
				case LS_IP5A:
					mMap.put("UNIT_CONST", MODBUS_LS_IP5A_CODE.OUTPUT_VOLTAGE.getUnitConst());
					mMap.put("UNIT", MODBUS_LS_IP5A_CODE.OUTPUT_VOLTAGE.getUnit());
					break;
				case LS_IS7:
					mMap.put("UNIT_CONST", MODBUS_LS_IS7_CODE.OUTPUT_VOLTAGE.getUnitConst());
					mMap.put("UNIT", MODBUS_LS_IS7_CODE.OUTPUT_VOLTAGE.getUnit());
					break;
				case DEFAULT:
					throw new EMnVSystemException(EMnVExceptionReason.UNREGISTERD_INVERTER_MODEL);
				default:
					break;
				}
				break;
			case ROCKWELL:
				mMap.put("UNIT_CONST", MODBUS_ROCKWELL_CODE.OUTPUT_VOLTAGE.getUnitConst());
				mMap.put("UNIT", MODBUS_ROCKWELL_CODE.OUTPUT_VOLTAGE.getUnit());
				break;
			default:
				break;
			}
			break;
		case DATE:
			mMap.put("UNIT_CONST", null);
			mMap.put("UNIT", null);
			break;
		default:
			break;
		}

		return mMap;
	}

	public void setInverterInfo() {
		try {
			// 인버터 아이디 생성
			String meterId = getCreateInverterId();
			meter.setMdsId(meterId);
			meter.setModemPort(portNumber);

			// Inverter는 시간정보가 없기때문에 현재 시간을 미터시간으로 설정함.
			meterTime = TimeUtil.getCurrentTime().substring(0, 12);
		} catch (Exception e) {
			log.error("ERROR - ", e);
		}
	}

	/**
	 * 인버터 아이디 생성 ~!! 모뎀아이디 + "-" + 제조사코드 + Station Address
	 * 
	 * @return
	 */
	public String getCreateInverterId() {
		String modemId = meter.getModem().getDeviceSerial();
		String venderId = String.format("%02d", vendorType.getValue());
		String inverterId = modemId + "-" + venderId + stationId;

		log.info("MDSID = {}, modem id = {}, vendor id = {}, station id = {}", new Object[] { inverterId, modemId, venderId, stationId });

		return inverterId;
	}

	/**
	 * System.out.println() 으로 HEX 출력.
	 * 
	 * @param data
	 * @param tPos
	 * @param showLength
	 */
	public void printHexByteString(byte[] data, int tPos, int showLength) {
		int loggingLenth = (data.length - tPos) < showLength ? data.length - tPos : showLength;

		byte[] logging = new byte[showLength];
		System.arraycopy(data, tPos, logging, 0, loggingLenth);
		log.info("### SHOW HEX POS[" + tPos + "] 부터 " + loggingLenth + "byte ==> " + Hex.getHexDump(logging));
	}

	public void postParse() {
		setLPData();
	}

	public LPData[] getLPData() {
		return lpData;
	}

	public void setLPData() {
		try {
			Map<String, LPData> lpDataMap = new HashMap<String, LPData>();
			Map<String, Object> mCodeMap = null;
			double outputCurrent = 0.0;
			double outputFrequency = 0.0;
			double outputVoltage = 0.0;

			// 일단 출력 주파수를 lp로 지정한다. 차후 아닐경우 수정할것.
			Double lp = 0.0;
			Double lpValue = 0.0;
			LPData lpEl = null;

			ModebusInverterCommonTable commonTable = new ModebusInverterCommonTable();

			for (int i = 0; i < toatalLPCount; i++) {
				/*
				 *  출력 주파수
				 *   : 15 ~60 정도 나옴.
				 */
				mCodeMap = setMCodeResult(MODBUS_DEFAULT_CODE.OUTPUT_FREQUENCY);
				if (mCodeMap != null) {
					outputFrequency = (double) (Integer) inverterDataMap.get(MODBUS_DEFAULT_CODE.OUTPUT_FREQUENCY.name() + "-" + i);
					lp = outputFrequency;
					if (mCodeMap.get("UNIT_CONST") != null) {
						outputFrequency = outputFrequency * Double.parseDouble((String) mCodeMap.get("UNIT_CONST"));
						lpValue = outputFrequency;
					}
				}
				/*
				 * 출력 전류
				 *  : 종류에 따라 여러가지 나옴.
				 */
				mCodeMap = setMCodeResult(MODBUS_DEFAULT_CODE.OUTPUT_CURRENT);
				if (mCodeMap != null) {
					outputCurrent = (double) (Integer) inverterDataMap.get(MODBUS_DEFAULT_CODE.OUTPUT_CURRENT.name() + "-" + i);
					if (mCodeMap.get("UNIT_CONST") != null) {
						outputCurrent = outputCurrent * Double.parseDouble((String) mCodeMap.get("UNIT_CONST"));
					}
				}
				/*
				 *  출력 전압
				 *  : 110V용, 220V용, 380V용 에따라 -15% ~ +15% 정도 나오는데
				 *  보통 250 +_ 30 정도 나옴.
				 */
				mCodeMap = setMCodeResult(MODBUS_DEFAULT_CODE.OUTPUT_VOLTAGE);
				if (mCodeMap != null) {
					outputVoltage = (double) (Integer) inverterDataMap.get(MODBUS_DEFAULT_CODE.OUTPUT_VOLTAGE.name() + "-" + i);
					if (mCodeMap.get("UNIT_CONST") != null) {
						outputVoltage = outputVoltage * Double.parseDouble((String) mCodeMap.get("UNIT_CONST"));
					}
				}
				// Data Time
				String dataTime = (String) inverterDataMap.get(MODBUS_DEFAULT_CODE.DATE.name() + "-" + i);

				lpEl = new LPData(commonTable.resetLpTime(DateTimeUtil.getCalendar(dataTime)), lp, lpValue);
				lpEl.setCh(new Double[] { outputCurrent, outputFrequency, outputVoltage });

				lpDataMap.put(lpEl.getDatetime(), lpEl);
			}

			lpData = lpDataMap.values().toArray(new LPData[0]);
			Arrays.sort(lpData, LPComparator.TIMESTAMP_ORDER);

			log.info("######################## LpData.length:" + lpData.length);
		} catch (Exception e) {
			log.error("ERROR-", e);
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
	public Double getMeteringValue() {
		return null;
	}

	@Override
	public String toString() {
		return null;
	}

	@Override
	public LinkedHashMap<?, ?> getData() {
		return null;
	}

	@Override
	public int getFlag() {
		return 0;
	}

	@Override
	public void setFlag(int flag) {
		meteringDataType = EMnVMeteringDataType.getItem(DataUtil.getByteToInt(flag));
	}

	public String getMeterID() {
		return this.meter.getMdsId();
	}

}
