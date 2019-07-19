/**
 * 
 */
package com.aimir.fep.protocol.nip.client.batch.jobs.ota;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.OTATargetType;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.protocol.fmp.common.Target;
import com.aimir.fep.protocol.smsp.SMSConstants;
import com.aimir.fep.tool.batch.manager.IBatchJob;
import com.aimir.fep.trap.actions.SP.EV_SP_200_66_0_Action;
import com.aimir.fep.trap.common.EV_Action.OTA_UPGRADE_RESULT_CODE;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.Device.DeviceType;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.util.DateTimeUtil;

/**
 * @author simhanger
 *
 */
public class MBBOTABatchJob implements IBatchJob {
	private static Logger logger = LoggerFactory.getLogger(MBBOTABatchJob.class);
	private String executorName;

	private CommandGW gw;
	private OTATargetType otaTargetType;
	private Target target;
	private HashMap<String, Object> asyncMbbParamMap;
	private boolean useAsyncChannel;
	private String commandName;
	private String newFwVersion;

	@SuppressWarnings("unchecked")
	public MBBOTABatchJob(String executorName, CommandGW gw, OTATargetType otaTargetType, Target target, Map<String, Object> params, boolean useAsyncChannel) {
		this.newFwVersion = params.get("fw_version").toString();

		logger.info("[CREATE JOB] ExecutorName={}, OTATargetType={}, MeterId={}, ModemId={}, newFwVersion={}}, useAsyncChannel={}", executorName, otaTargetType.name(), target.getMeterId(), target.getModemId(), newFwVersion, useAsyncChannel);

		this.gw = gw;
		this.otaTargetType = otaTargetType;
		this.target = target;
		this.asyncMbbParamMap = (HashMap<String, Object>) ((HashMap<String, Object>) params).clone();
		asyncMbbParamMap.remove("image"); // Unnecessary parameter remove.

		this.useAsyncChannel = useAsyncChannel;
	}

	@Override
	public void run() {
		logger.info("Excute [START] ExecutorName={}, OTATargetType={}, MeterId={}, ModemId={}, newFwVersion={}, ExecuteType={}, Target={}", executorName, otaTargetType, target.getMeterId(), target.getModemId(), newFwVersion, (useAsyncChannel == true ? "AsyncChannel" : "SMS"), target);

		logger.debug("#### Job parameters ####");
		logger.debug("# fw_crc={}", asyncMbbParamMap.get("fw_crc"));
		logger.debug("# fw_version={}", newFwVersion);
		logger.debug("# image_identifier={}", asyncMbbParamMap.get("image_identifier"));
		logger.debug("# checkSum={}", asyncMbbParamMap.get("checkSum"));
		logger.debug("# take_over={}", asyncMbbParamMap.get("take_over"));
		logger.debug("# model={}", asyncMbbParamMap.get("model"));
		logger.debug("# fwFileName={}", asyncMbbParamMap.get("fwFileName"));
		logger.debug("# fw_path={}", asyncMbbParamMap.get("fw_path"));
		logger.debug("");

		OTA_UPGRADE_RESULT_CODE resultCode = null;
		boolean executeResult = false;
		String oldFWVersion = "";
		String exceptionMessage = null;
		boolean versionCheck = true;
		String openTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");

		ModemDao modemDao = DataUtil.getBean(ModemDao.class);
		Modem modem = modemDao.get(target.getModemId());
		TargetClass modemTypeTargetClass = TargetClass.valueOf(modem.getModemType().name());
		TargetClass targetClass = null;
		DeviceType otaTargetDeviceType = null;

		try {
			if (otaTargetType == OTATargetType.METER) {
				/** 유형별 필요 파라미터 설정 */
				targetClass = TargetClass.EnergyMeter;
				otaTargetDeviceType = DeviceType.Meter;

				MeterDao meterDao = DataUtil.getBean(MeterDao.class);
				Meter meter = meterDao.get(target.getMeterId());

				oldFWVersion = meter.getSwVersion();
				logger.debug("Meter={}, FW Version. OldVersion=({}) ==> NewVersion=({})", target.getMeterId(), oldFWVersion, newFwVersion);

				if (useAsyncChannel == false && (modem.getPhoneNumber() == null || modem.getPhoneNumber().equals(""))) {
					logger.info("### [SKIP] This Meter have no PhoneNumber in Modem. MeterId = {}, ModemID = {}", meter.getMdsId(), modem.getDeviceSerial());

					exceptionMessage = "[SKIP] This Meter have no PhoneNumber in Modem.";
					resultCode = OTA_UPGRADE_RESULT_CODE.OTAERR_EXECUTE_FAIL;
					versionCheck = false;
				} else if (oldFWVersion.equals(newFwVersion)) {
					logger.info("### [SKIP] This Meter is already same Firmware version. MeterId = {}, Firmware Version = {}", meter.getMdsId(), newFwVersion);

					exceptionMessage = "[SKIP] This Meter is already same Firmware version.";
					resultCode = OTA_UPGRADE_RESULT_CODE.OTAERR_SAME_VERSION;
					versionCheck = false;
				} else {
					asyncMbbParamMap.put("meterId", target.getMeterId());
					commandName = "cmdMeterOTAStart";
				}
			} else if (otaTargetType == OTATargetType.MODEM) {
				/** 유형별 필요 파라미터 설정 */
				targetClass = TargetClass.valueOf(modem.getModemType().name());
				otaTargetDeviceType = DeviceType.Modem;

				oldFWVersion = modem.getFwVer();
				logger.debug("Modem={}, PhoneNumber={}, FW Version. OldVersion=({}) ==> NewVersion=({})", target.getModemId(), (target.getPhoneNumber() == null ? "Null~!!" : target.getPhoneNumber()), oldFWVersion, newFwVersion);

				if (useAsyncChannel == false && (modem.getPhoneNumber() == null || modem.getPhoneNumber().equals(""))) {
					logger.info("### [SKIP] This Modem have no PhoneNumber. ModemID = {}", modem.getDeviceSerial());

					exceptionMessage = "[SKIP] This Modem have no PhoneNumber.";
					resultCode = OTA_UPGRADE_RESULT_CODE.OTAERR_EXECUTE_FAIL;
					versionCheck = false;
				} else if (oldFWVersion.equals(newFwVersion)) {
					logger.info("### [SKIP] This Modem is already same Firmware version. ModemID = {}, Firmware Version = {}", modem.getDeviceSerial(), newFwVersion);

					exceptionMessage = "[SKIP] This Modem is already same Firmware version.";
					resultCode = OTA_UPGRADE_RESULT_CODE.OTAERR_SAME_VERSION;
					versionCheck = false;
				} else {
					asyncMbbParamMap.put("modemId", target.getModemId());
					commandName = "cmdModemOTAStart";
				}
			} else {
				logger.error("Unknown OTA Target type.");
				throw new Exception("Unknown OTA Target type.");
			}

			if (versionCheck) {
				if (useAsyncChannel) {
					commandName += "AsyncChannel"; // Command Name에 "AsyncChannel"를 추가하여 SMS를 보내지 않도록 한다. ([SP-738] MBB OTA via upload channel)
				}

				mbbOTARun();
				executeResult = true;

			}

			logger.debug("Excute [END] OTATargetType={}, newFwVersion={}, ExecuteType={}, Target={}", otaTargetType, newFwVersion, (useAsyncChannel == true ? "AsyncChannel" : "SMS"), target);
		} catch (Exception e) {
			logger.error("Excute " + otaTargetType.name() + " OTA error - " + e.toString(), e);
			resultCode = OTA_UPGRADE_RESULT_CODE.UNKNOWN;
			exceptionMessage = e.toString();
		}

		if (!executeResult) {
			openTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
			String errMsg = (exceptionMessage == null ? otaTargetType + " OTA Error" : otaTargetType + " OTA Error - " + exceptionMessage);

			/*
			 * 200.66 Event Save.
			 */
			EV_SP_200_66_0_Action action3 = new EV_SP_200_66_0_Action();
			action3.makeEvent(modemTypeTargetClass, target.getModemId(), targetClass, openTime, resultCode, errMsg, "HES", modem.getLocation());
			action3.updateOTAHistory((otaTargetType == OTATargetType.MODEM ? target.getModemId() : target.getMeterId()), otaTargetDeviceType, openTime, resultCode, errMsg);

			logger.debug("Execute Result fail. event message = {}", errMsg);
		}

		logger.info("[FINISHED] ExecutorName={}, OTATargetType={}, MeterId={}, ModemId={}, newFwVersion={}, ExecuteType={}, Target={}", executorName, otaTargetType, target.getMeterId(), target.getModemId(), newFwVersion, (useAsyncChannel == true ? "AsyncChannel" : "SMS"), target);
	}

	/**
	 * MBB OTA RUN (Send SMS and save Async_log) If commandName contains
	 * "AsyncCannel" String, save only Async_log
	 * 
	 * @throws Exception
	 */
	private void mbbOTARun() throws Exception {
		if (target.getTargetType().name().equals(ModemType.MMIU.name())) {
			List<String> paramListForSMS = new ArrayList<String>();
			paramListForSMS.add(FMPProperty.getProperty("smpp.hes.fep.server", ""));
			paramListForSMS.add(FMPProperty.getProperty("soria.modem.tls.port", ""));
			paramListForSMS.add(FMPProperty.getProperty("smpp.auth.port", ""));

			String cmdMap = null;
			ObjectMapper om = new ObjectMapper();
			if (asyncMbbParamMap != null) {
				cmdMap = om.writeValueAsString(asyncMbbParamMap);
			}

			logger.debug("Send SMS params modemID={}, phoneNumber={}, commandName={}, cmdMap={}", target.getModemId(), target.getPhoneNumber(), commandName, cmdMap);

			/*
			 * sendSMS()에서 commandName에 "AsyncChannel"이 포함되어 있을 경우 Async관련 데이터만 저장하고 SMS는 보내지 않는다.
			 * 국내 SMS TEST 로직  : SMSServiceGabiaClient.java / execute()
			 * SORIA : SMS_Requester.java / sendSMS()
			 */
			String messageId = gw.sendSMS(commandName, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), target.getPhoneNumber(), target.getModemId(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), paramListForSMS, cmdMap);

			logger.debug("Send SMS Result. messageId = {}", messageId);

			if (messageId == null || messageId.equals("FAIL") || messageId.equals("FAIL-CONNECT")) {
				throw new Exception("Send SMS Error - " + messageId);
			}
		} else {
			throw new Exception("Type Missmatch. this modem is not MMIU Type modem.");
		}
	}

	@Override
	public String getExecutorName() {
		return executorName;
	}

	@Override
	public String getName() {
		return target.getTargetId();
	}

	@Override
	public void printResult(String title, ResultStatus status, String desc) {
		logger.info("{}, {}, {}", title, status.name(), desc);
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = ((IBatchJob) obj).getName().equals(getName());
		logger.debug("[Equals Check] ThisObj=[{}], ParamObj=[{}], is equals?=[{}]", getName(), ((IBatchJob) obj).getName(), result);

		return result;
	}
}
