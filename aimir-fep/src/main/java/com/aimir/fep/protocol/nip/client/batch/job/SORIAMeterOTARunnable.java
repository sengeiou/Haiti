/**
 * 
 */
package com.aimir.fep.protocol.nip.client.batch.job;

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.constants.CommonConstants.OTATargetType;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.MeterDao;
import com.aimir.fep.protocol.fmp.common.Target;
import com.aimir.fep.protocol.nip.client.actions.NICommandActionHandlerAdaptor;
import com.aimir.fep.protocol.nip.client.batch.excutor.IBatchRunnable;
import com.aimir.fep.protocol.nip.client.bypass.BypassClient;
import com.aimir.fep.protocol.nip.client.bypass.BypassResult;
import com.aimir.fep.trap.actions.SP.EV_SP_200_64_0_Action;
import com.aimir.fep.trap.actions.SP.EV_SP_200_66_0_Action;
import com.aimir.fep.trap.common.EV_Action.OTA_UPGRADE_RESULT_CODE;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.Device.DeviceType;
import com.aimir.model.device.Meter;
import com.aimir.util.DateTimeUtil;

/**
 * @author simhanger
 *
 */
@Deprecated
public class SORIAMeterOTARunnable implements IBatchRunnable {
	private static Logger logger = LoggerFactory.getLogger(SORIAMeterOTARunnable.class);
	private Target target;
	private HashMap<String, Object> params;
	private IoSession externalNISession;
	private BypassClient bypassClient;
	private String newFwVersion;
	private BypassResult bypassResult = null;

	@SuppressWarnings("unchecked")
	public SORIAMeterOTARunnable(Target target, Map<String, Object> params) {
		this.target = target;
		this.params = (HashMap<String, Object>) ((HashMap<String, Object>) params).clone();
		this.newFwVersion = params.get("fw_version").toString();
	}

	public void setExternalNISession(IoSession session) {
		this.externalNISession = session;
	}

	@Override
	public void run() {
		logger.debug("# SORIAMeterOTARunnable Start.");
		logger.debug("Excute [START] MeterId={}, ModemId={}, newFwVersion={}, Target={}", target.getMeterId(), target.getModemId(), newFwVersion, target);

		logger.debug("#### Job parameters ###");
		logger.debug("# fw_crc={}", params.get("fw_crc"));
		logger.debug("# fw_version={}", params.get("fw_version"));
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

		MeterDao meterDao = DataUtil.getBean(MeterDao.class);
		Meter meter = meterDao.get(target.getMeterId());
		String oldFWVersion = meter.getSwVersion();

		try {
			/**
			 * F/W Version Check
			 */
			logger.debug("Meter={}, FW Version. OldVersion=({}) ==> NewVersion=({})", target.getMeterId(), oldFWVersion, newFwVersion);
			if (oldFWVersion.equals(newFwVersion)) {
				logger.info("### [SKIP] This Meter is already same Firmware version. MeterId = {}, Firmware Version = {}", meter.getMdsId(), newFwVersion);
				
				exceptionMessage = "[SKIP] This Meter is already same Firmware version.";
				resultCode = OTA_UPGRADE_RESULT_CODE.OTAERR_SAME_VERSION;
				versionCheck = false;
			}

			if (versionCheck) {
				bypassClient = new BypassClient(target);
				bypassClient.setParams(params);

				/** For MBB */
				if (externalNISession != null) {
					logger.debug("## Set External NI Session !! ##");
					if (externalNISession.isActive() && externalNISession.isConnected()) {
						bypassClient.setExternalNISession(externalNISession);
						((NICommandActionHandlerAdaptor)externalNISession.getHandler()).setBypassCommandAction(externalNISession, bypassClient.getBypassCommandCommandAction());
					} else {
						throw new Exception("NI Session is disconnected.");
					}
				}

				//		Thread.sleep(10000);
				//		BypassResult bypassResult = new BypassResult();
				//		bypassResult.setSuccess(true);
				//		bypassResult.setResultValue("테스트 성공");

				/*
				 * 200.64 Event Save.
				 */
				EV_SP_200_64_0_Action action = new EV_SP_200_64_0_Action();
				action.makeEvent(TargetClass.EnergyMeter, target.getMeterId(), TargetClass.EnergyMeter, openTime, "HES", target.getLocation());
				action.updateOTAHistory(target.getMeterId(), DeviceType.Meter, openTime);

				int useNiBypass = Integer.parseInt(FMPProperty.getProperty("ota.firmware.meter.nibypass.use", "0"));
				if (useNiBypass > 0) {
					logger.debug("[cmdMeterOTAStart] Using NI Bypass frame.");
					bypassResult = bypassClient.excuteNiBypass("cmdMeterOTAStart");
				} else {
					logger.debug("[cmdMeterOTAStart] Using Null Bypass.");
					bypassResult = bypassClient.excute("cmdMeterOTAStart");
				}

				logger.debug("Recieved BypassResult. result={}", bypassResult != null ? bypassResult.toString() : "null~!");

				/*
				 * F/W Version Update.
				 */
				if (bypassResult != null && bypassResult.isSuccess()) {
					meter.setSwVersion(newFwVersion);
					meterDao.update(meter);

					logger.debug("Upadate Meter FW Version. OldFWVersion={} => NewFWVersion={}, resultMessage={}", oldFWVersion, newFwVersion, bypassResult.getResultValue());
					executeResult = true;
				}else{
					resultCode = OTA_UPGRADE_RESULT_CODE.OTAERR_EXECUTE_FAIL;
				}
			}

			logger.debug("Excute [END] MeterId={}, ModemId={}, newFwVersion={}, Target={}", target.getMeterId(), target.getModemId(), newFwVersion, target);

		} catch (Exception e) {
			logger.error("Meter OTA Execute error - " + e.toString(), e);
			exceptionMessage = e.toString();
		}

		if (!executeResult) {
			openTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
			String errMsg = (exceptionMessage == null ? " OTA Error" : " OTA Error - " + exceptionMessage);
			if (bypassResult != null && !bypassResult.getResultValue().equals("")) {
				errMsg += " [" + bypassResult.getResultValue().toString() + "]";
			}

			EV_SP_200_66_0_Action action2 = new EV_SP_200_66_0_Action();
			action2.makeEvent(TargetClass.EnergyMeter, target.getMeterId(), TargetClass.EnergyMeter, openTime, resultCode, errMsg, "HES", meter.getLocation());
			action2.updateOTAHistory(target.getMeterId(), DeviceType.Meter, openTime, resultCode, errMsg);

			logger.debug("Execute Result fail. event message = {}", errMsg);
		}

		logger.debug("# SORIAMeterOTARunnable End.");
	}

	@Override
	public String getName() {
		return target.getMeterId();
	}

	@Override
	public void printResult(String title, ResultStatus status, String desc) {
		logger.info(title + "," + status.name() + "," + desc);
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = ((IBatchRunnable) obj).getName().equals(getName());
		logger.debug("[Equals Check] ThisObj=[{}], ParamObj=[{}], is equals?=[{}]", getName(), ((IBatchRunnable) obj).getName(), result);

		return result;
	}
	
	public Map<String, Object> getResultForMBB(){
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(target.getMeterId(), bypassResult.getResultValue());

		return result;
	}

}
