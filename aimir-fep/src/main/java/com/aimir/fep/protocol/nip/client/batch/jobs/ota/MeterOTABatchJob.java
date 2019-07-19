/**
 * 
 */
package com.aimir.fep.protocol.nip.client.batch.jobs.ota;

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.MeterDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.protocol.fmp.common.Target;
import com.aimir.fep.protocol.nip.client.actions.NICommandActionHandlerAdaptor;
import com.aimir.fep.protocol.nip.client.bypass.BypassClient;
import com.aimir.fep.protocol.nip.client.bypass.BypassResult;
import com.aimir.fep.tool.batch.manager.IBatchJob;
import com.aimir.fep.trap.actions.SP.EV_SP_200_64_0_Action;
import com.aimir.fep.trap.actions.SP.EV_SP_200_66_0_Action;
import com.aimir.fep.trap.common.EV_Action.OTA_UPGRADE_RESULT_CODE;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.Device.DeviceType;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Location;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.StringUtil;

/**
 * @author simhanger
 *
 */
public class MeterOTABatchJob implements IBatchJob {
	private static Logger logger = LoggerFactory.getLogger(MeterOTABatchJob.class);
	private String executorName;

	private CommandGW gw;
	private Target target;
	private HashMap<String, Object> params;
	private IoSession externalNISession;
	private BypassClient bypassClient;
	private String newFwVersion = null;
	private BypassResult bypassResult = null;

	@SuppressWarnings("unchecked")
	public MeterOTABatchJob(String executorName, CommandGW gw, Target target, Map<String, Object> params) {
		logger.info("[CREATE JOB] ExecutorName={}, MeterId={}, ModemId={}, newFwVersion={}", executorName, target.getMeterId(), target.getModemId(), newFwVersion);

		this.executorName = executorName;

		this.gw = gw;
		this.target = target;
		this.params = (HashMap<String, Object>) ((HashMap<String, Object>) params).clone();
		this.newFwVersion = params.get("fw_version").toString();
	}

	public void setExternalNISession(IoSession session) {
		this.externalNISession = session;
	}

	@Override
	public void run() {
		logger.info("Excute [START] ExecutorName={}, MeterId={}, ModemId={}, newFwVersion={}, Target={}", executorName, target.getMeterId(), target.getModemId(), newFwVersion, target);

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
		Location location = meter.getLocation();

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
						((NICommandActionHandlerAdaptor) externalNISession.getHandler()).setBypassCommandAction(externalNISession, bypassClient.getBypassCommandCommandAction());
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
				action.makeEvent(TargetClass.EnergyMeter, target.getMeterId(), TargetClass.EnergyMeter, openTime, "HES", location);
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
					//					meter.setSwVersion(newFwVersion);
					//					meterDao.update(meter);
					//
					//					logger.debug("Upadate Meter FW Version. OldFWVersion={} => NewFWVersion={}, resultMessage={}", oldFWVersion, newFwVersion, bypassResult.getResultValue());
					executeResult = true;
				} else {
					resultCode = OTA_UPGRADE_RESULT_CODE.OTAERR_EXECUTE_FAIL;
				}
			}

			logger.debug("Excute [END] MeterId={}, ModemId={}, newFwVersion={}, Target={}", target.getMeterId(), target.getModemId(), newFwVersion, target);
		} catch (Exception e) {
			logger.error("Meter OTA Execute error - " + e.toString(), e);
			resultCode = OTA_UPGRADE_RESULT_CODE.OTAERR_EXECUTE_FAIL;
			exceptionMessage = e.getMessage();
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
		} else {
			/*
			 * F/W Version Update.
			 */
			try {
				logger.info("FW Version Checking... Sleep [" + (5 * 1000 * 60) + "]");
				Thread.sleep(5 * 1000 * 60);
				Map<String, Object> result = gw.cmdGetMeterFWVersion(meter.getMdsId());
				logger.info("FW Version Checking... result = " + StringUtil.objectToJsonString(result));
			} catch (Exception e) {
				logger.debug("FW Version Check Error - " + e.getMessage(), e);
			}
		}

		logger.info("[FINISHED] ExecutorName={}, MeterId={}, ModemId={}, newFwVersion={}", executorName, target.getMeterId(), target.getModemId(), newFwVersion);
	}

	@Override
	public String getExecutorName() {
		return executorName;
	}

	@Override
	public String getName() {
		return target.getMeterId();
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
		result.put(target.getMeterId(), bypassResult.getResultValue());

		return result;
	}
}
