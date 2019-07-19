/**
 * 
 */
package com.aimir.fep.protocol.nip.client.batch.jobs.ota;

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.OTATargetType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.bypass.BypassDevice;
import com.aimir.fep.protocol.fmp.common.Target;
import com.aimir.fep.protocol.nip.CommandNIProxy;
import com.aimir.fep.protocol.nip.client.NiClient;
import com.aimir.fep.protocol.nip.frame.GeneralFrame;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.FrameOption_Type;
import com.aimir.fep.tool.batch.manager.IBatchJob;
import com.aimir.fep.trap.actions.SP.EV_SP_200_64_0_Action;
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
public class ModemOTABatchJob implements IBatchJob {
	private static Logger logger = LoggerFactory.getLogger(ModemOTABatchJob.class);
	private String executorName;

	private String commandName = "cmdModemOTAStart";
	private OTATargetType otaTargetType;
	private Target target;
	private HashMap<String, Object> params;
	private IoSession externalNISession;
	private String newFwVersion = null;
	private Map<String, Object> resultNotiParams = null;

	@SuppressWarnings("unchecked")
	public ModemOTABatchJob(String executorName, OTATargetType otaTargetType, Target target, Map<String, Object> params) {
		logger.info("[CREATE JOB] ExecutorName={}, OTATargetType={}, MeterId={}, ModemId={}, newFwVersion={}", executorName, otaTargetType.name(), target.getMeterId(), target.getModemId(), newFwVersion);

		this.executorName = executorName;

		this.otaTargetType = otaTargetType;
		this.target = target;
		this.params = (HashMap<String, Object>) ((HashMap<String, Object>) params).clone();
		this.newFwVersion = params.get("fw_version").toString();
	}

	public void setExternalNISession(IoSession session) {
		this.externalNISession = session;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		logger.info("Excute [START] ExecutorName={}, OTATargetType={}, MeterId={}, ModemId={}, newFwVersion={}, Target={}", executorName, otaTargetType, target.getMeterId(), target.getModemId(), newFwVersion, target);

		logger.debug("#### Job parameters ####");
		logger.debug("# fw_crc={}", params.get("fw_crc"));
		logger.debug("# fw_version={}", newFwVersion);
		logger.debug("# image_identifier={}", params.get("image_identifier"));
		logger.debug("# checkSum={}", params.get("checkSum"));
		logger.debug("# take_over={}", params.get("take_over"));
		logger.debug("# model={}", params.get("model"));
		logger.debug("# fwFileName={}", params.get("fwFileName"));
		logger.debug("# fw_path={}", params.get("fw_path"));
		logger.debug("# image size={}", ((byte[]) params.get("image")).length);
		logger.debug("");

		OTA_UPGRADE_RESULT_CODE resultCode = null;
		boolean versionCheck = true;
		boolean executeResult = false;
		String exceptionMessage = null;
		String openTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");

		ModemDao modemDao = DataUtil.getBean(ModemDao.class);
		Modem modem = modemDao.get(target.getModemId());
		MeterDao meterDao = null;
		Meter meter = null;
		String oldFWVersion = "";

		TargetClass modemTypeTargetClass = TargetClass.valueOf(modem.getModemType().name());
		TargetClass targetClass = null;
		DeviceType otaTargetDeviceType = null;
		NiClient client = null;

		try {
			/**
			 * F/W Version Check and 유형별 필요 옵션 설정
			 */
			String model = params.get("model").toString();
			String fwFileName = params.get("fwFileName").toString();
			String optionModel = "";
			if (otaTargetType == OTATargetType.MODEM) {
				oldFWVersion = modem.getFwVer();
				logger.debug("Modem={}, FW Version. OldVersion=({}) ==> NewVersion=({})", target.getModemId(), oldFWVersion, newFwVersion);

				if (oldFWVersion.equals(newFwVersion)) {
					logger.info("### [SKIP] This Modem is already same Firmware version. Modem ID = {}, Firmware Version = {}", target.getModemId(), newFwVersion);

					exceptionMessage = "[SKIP] This Modem is already same Firmware version.";
					resultCode = OTA_UPGRADE_RESULT_CODE.OTAERR_SAME_VERSION;
					versionCheck = false;
				}

				/** 유형별 필요 파라미터 설정 */
				targetClass = TargetClass.valueOf(modem.getModemType().name());
				otaTargetDeviceType = DeviceType.Modem;
				optionModel = model;
			} else if (otaTargetType == OTATargetType.METER) {
				meterDao = DataUtil.getBean(MeterDao.class);
				meter = meterDao.get(target.getMeterId());
				oldFWVersion = meter.getSwVersion();
				logger.debug("Meter={}, FW Version. OldVersion=({}) ==> NewVersion=({})", target.getMeterId(), oldFWVersion, newFwVersion);

				if (oldFWVersion.equals(newFwVersion)) {
					logger.info("### [SKIP] This Meter is already same Firmware version. Meter ID = {}, Firmware Version = {}", target.getMeterId(), newFwVersion);

					exceptionMessage = "[SKIP] This Meter is already same Firmware version.";
					resultCode = OTA_UPGRADE_RESULT_CODE.OTAERR_SAME_VERSION;
					versionCheck = false;
				}

				/** 유형별 필요 파라미터 설정 */
				targetClass = TargetClass.EnergyMeter;
				otaTargetDeviceType = DeviceType.Meter;
				if (model.substring(0, 3).equals("MA3")) { // 3상인경우
					if (fwFileName.startsWith("KFPP_V")) {
						optionModel = fwFileName; // ex) KFPP_V06010072
					} else {
						throw new Exception("Missmatch FW information. Model=" + model + ", File=" + fwFileName);
					}
				} else if (model.substring(0, 3).equals("MA1")) { // 단상인경우
					if (fwFileName.substring(0, 6).equals("KFSP_V")) {
						optionModel = fwFileName; // ex) KFSP_V05010075
					} else {
						throw new Exception("Missmatch FW information. Model=" + model + ", File=" + fwFileName);
					}
				} else {
					throw new Exception("Unknown Meter Model. Model=" + model + ", File=" + fwFileName);
				}

			}

			if (versionCheck) {
				/*
				 * 200.64 Event Save.
				 */
				logger.debug("AcivatorType={}, ActivatorId={}, TargetType={}, TargetId={}, openTime={}", modemTypeTargetClass, target.getModemId(), targetClass, target.getTargetId(), openTime);
				EV_SP_200_64_0_Action action = new EV_SP_200_64_0_Action();
				action.makeEvent(modemTypeTargetClass, target.getModemId(), targetClass, openTime, "HES", target.getLocation());
				action.updateOTAHistory((otaTargetType == OTATargetType.MODEM ? target.getModemId() : target.getMeterId()), otaTargetDeviceType, openTime);

				GeneralFrame generalFrame = CommandNIProxy.setGeneralFrameOption(FrameOption_Type.Firmware, null);
				generalFrame.setFrame();
				client = CommandNIProxy.getClient(target, generalFrame);
				logger.debug("tempCode ClientHASh = {}", client.hashCode());

				if (externalNISession != null) {
					if (externalNISession.isActive() && externalNISession.isConnected()) {
						logger.debug("External NI Session set~! session = {}", externalNISession.getRemoteAddress());
						client.setSession(externalNISession);
					} else {
						throw new Exception("NI Session is disconnected.");
					}
				}

				BypassDevice bd = new BypassDevice();
				bd.setMeterId(target.getMeterId());
				bd.setModemId(target.getModemId());
				bd.setModemType(modem.getModemType());
				bd.setFw_bin((byte[]) params.get("image"));
				bd.setRemainPackateLength(((byte[]) params.get("image")).length);
				bd.setFwVersion(newFwVersion);
				bd.setFwCRC((String) params.get("fw_crc"));
				bd.setTakeOver(Boolean.valueOf(String.valueOf(params.get("take_over"))));
				bd.setOptionalVersion(params.get("fw_version").toString());
				bd.setOptionalModel(optionModel);
				bd.setTargetClass(targetClass);
				bd.setOtaTargetDeviceType(otaTargetDeviceType); // Modem이 Modem OTA 할때는 DeviceType.Modem, Modem이 Meter OTA 할때는 DeviceType.Meter

				// 모뎀 종류별 패킷 크기 설정
				String otaModemType = "";
				if (modem.getModemType() == ModemType.MMIU && (modem.getProtocolType() == Protocol.SMS || modem.getProtocolType() == Protocol.GPRS)) { // MBB Modem
					bd.setPacket_size(Integer.parseInt(FMPProperty.getProperty("ota.firmware.modem.packetsize.mbb", "1024")));
					otaModemType = "MBB Modem";
				} else if (modem.getModemType() == ModemType.MMIU && modem.getProtocolType() == Protocol.IP) { // Ethernet Modem
					bd.setPacket_size(Integer.parseInt(FMPProperty.getProperty("ota.firmware.modem.packetsize.ethernet", "1024")));
					otaModemType = "Ethernet Modem";
				} else if (modem.getModemType() == ModemType.SubGiga && modem.getProtocolType() == Protocol.IP) { // RF Modem
					bd.setPacket_size(Integer.parseInt(FMPProperty.getProperty("ota.firmware.modem.packetsize.rf", "256")));
					otaModemType = "RF Modem";
				}

				Map<String, Object> conParams = new HashMap<String, Object>();
				conParams.put("NAME_SPACE", target.getNameSpace());
				conParams.put("COMMAND", "cmdModemOTAStart");
				conParams.put("BYPASS_DEVICE", bd);

				logger.debug("[ModemOTABatchJob] Excute START - TargetId={}, TargetType={}, ModemType={}, newFwVersion={}", target.getModemId(), otaTargetType, otaModemType, newFwVersion);

				generalFrame = client.sendCommand(target, generalFrame, null, commandName, conParams);

				//				notifyParams.put("modemId", bd.getModemId());
				//				notifyParams.put("meterId", bd.getMeterId());
				//				notifyParams.put("fwVersion", bd.getFwVersion());
				//				notifyParams.put("elapseTime", "");
				//				notifyParams.put("result", false);
				//				notifyParams.put("resultMessage", "");

				resultNotiParams = (Map<String, Object>) client.getNotiParams();

				logger.debug("Recieved NotiParam - TargetId={}, TargetType={}, ModemType={}, newFwVersion={}, result={}", target.getModemId(), otaTargetType, otaModemType, newFwVersion, resultNotiParams != null ? resultNotiParams.toString() : "null~!!");

				/*
				 * F/W Version Update.
				 */
				if (resultNotiParams != null && (boolean) resultNotiParams.get("result")) {
					//					if (otaTargetType == OTATargetType.MODEM) {
					//						modem.setFwVer((String) resultNotiParams.get("fwVersion"));
					//						modemDao.update(modem);
					//
					//					} else if (otaTargetType == OTATargetType.METER) {
					//						meter.setSwVersion((String) resultNotiParams.get("fwVersion"));
					//						meterDao.update(meter);
					//					}

					logger.debug("Upadate Success. {} FW Version. OldFWVersion={} => NewFWVersion={}, resultMessage={}", otaTargetType.name(), oldFWVersion, (String) resultNotiParams.get("fwVersion"), resultNotiParams.get("resultMessage"));
					executeResult = true;
				} else {
					resultCode = OTA_UPGRADE_RESULT_CODE.OTAERR_EXECUTE_FAIL;
				}
			}

			logger.debug("Excute [END] OTATargetType={}, OTATypeCode={}, newFwVersion={}, Target={}", otaTargetType, newFwVersion, target);
		} catch (Exception e) {
			logger.error("Excute " + otaTargetType.name() + " OTA error - " + e.toString(), e);
			exceptionMessage = e.toString();
		} finally {
			client.dispose();
		}

		if (!executeResult) {
			openTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
			String errMsg = (exceptionMessage == null ? otaTargetDeviceType + " OTA Error" : otaTargetDeviceType + " OTA Error - " + exceptionMessage);
			if (resultNotiParams != null && resultNotiParams.containsKey("resultMessage")) {
				errMsg += " [" + resultNotiParams.get("resultMessage") + "]";
			}

			/*
			 * 200.66 Event Save.
			 */
			EV_SP_200_66_0_Action action3 = new EV_SP_200_66_0_Action();
			action3.makeEvent(modemTypeTargetClass, target.getModemId(), targetClass, openTime, resultCode, errMsg, "HES", modem.getLocation());
			//action3.updateOTAHistory(target.getTargetId(), otaTargetDeviceType, openTime, OTA_UPGRADE_RESULT_CODE.OTAERR_EXECUTE_FAIL, generalFrame.getCommandActionResult().getResultValue().toString());
			action3.updateOTAHistory((otaTargetType == OTATargetType.MODEM ? target.getModemId() : target.getMeterId()), otaTargetDeviceType, openTime, resultCode, errMsg);

			logger.debug("Execute Result fail. event message = {}", errMsg);
		}

		logger.info("[FINISHED] ExecutorName={}, MeterId={}, ModemId={}", executorName, target.getMeterId(), target.getModemId());
	}

	@Override
	public String getExecutorName() {
		return executorName;
	}

	@Override
	public String getName() {
		return target.getModemId();
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

	public Map<String, Object> getResultForMBB() {
		Map<String, Object> result = new HashMap<String, Object>();
		if (otaTargetType == OTATargetType.MODEM) {
			result.put(target.getModemId(), resultNotiParams);
		} else {
			result.put(target.getMeterId(), resultNotiParams);
		}

		return result;
	}
}
