/**
 * 
 */
package com.aimir.fep.protocol.nip.client.batch.job;

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.fep.protocol.fmp.common.Target;
import com.aimir.fep.protocol.nip.client.batch.excutor.IBatchCallable;
import com.aimir.fep.protocol.nip.client.batch.excutor.CallableBatchExcutor.CBE_RESULT_CONSTANTS;
import com.aimir.fep.protocol.nip.client.batch.excutor.CallableBatchExcutor.CBE_STATUS_CONSTANTS;
import com.aimir.fep.protocol.nip.client.bypass.BypassClient;
import com.aimir.fep.protocol.nip.client.bypass.BypassResult;
import com.aimir.fep.trap.actions.SP.EV_SP_200_64_0_Action;
import com.aimir.fep.trap.actions.SP.EV_SP_200_65_0_Action;
import com.aimir.fep.trap.actions.SP.EV_SP_200_66_0_Action;
import com.aimir.fep.trap.common.EV_Action.OTA_UPGRADE_RESULT_CODE;
import com.aimir.model.device.Device.DeviceType;
import com.aimir.util.DateTimeUtil;

/**
 * @author simhanger
 *
 */
@Deprecated
public class SORIAMeterOTACallable implements IBatchCallable {
	private static Logger logger = LoggerFactory.getLogger(SORIAMeterOTACallable.class);
	private Target target;
	private Map<String, Object> params;
	private BypassClient bypassClient;
	private IoSession externalNISession;

	public SORIAMeterOTACallable(Target target, Map<String, Object> params) {
		this.target = target;
		this.params = params;
	}

	public void setExternalNISession(IoSession session) {
		this.externalNISession = session;
	}

	@Override
	public Map<CBE_RESULT_CONSTANTS, Object> call() throws Exception {
		bypassClient = new BypassClient(target);
		bypassClient.setParams(params);

		if (externalNISession != null) {
			if (externalNISession.isActive() && externalNISession.isConnected()) {
				bypassClient.setExternalNISession(externalNISession);
			} else {
				throw new Exception("NI Session is disconnected.");
			}
		}

		//		Thread.sleep(10000);
		//		BypassResult bypassResult = new BypassResult();
		//		bypassResult.setSuccess(true);
		//		bypassResult.setResultValue("테스트 성공");

		/*
		 * OTA 시작 Event 저장
		 */
		String openTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
		EV_SP_200_64_0_Action action = new EV_SP_200_64_0_Action();
		action.makeEvent(TargetClass.EnergyMeter, target.getMeterId(), TargetClass.EnergyMeter, openTime, "HES", target.getLocation());
		action.updateOTAHistory(target.getMeterId(), DeviceType.Meter, openTime);

		logger.debug("[SORIAMeterOTACallable][{}] Excute START", target.getMeterId());
		BypassResult bypassResult = bypassClient.excute("cmdMeterOTAStart");
		boolean excuteResult = bypassResult.isSuccess();

		logger.debug("[SORIAMeterOTACallable][{}] Excute END = {}", target.getMeterId(), bypassResult.toString());

		Map<CBE_RESULT_CONSTANTS, Object> result = new HashMap<CBE_RESULT_CONSTANTS, Object>();
		result.put(CBE_RESULT_CONSTANTS.TARGET_ID, target.getMeterId());
		result.put(CBE_RESULT_CONSTANTS.RESULT_STATE, excuteResult == true ? CBE_STATUS_CONSTANTS.SUCCESS : CBE_STATUS_CONSTANTS.FAIL);
		result.put(CBE_RESULT_CONSTANTS.RESULT_VALUE, bypassResult.getResultValue().toString());

		logger.debug("[SORIAMeterOTACallable] Result={}", result.toString());

		/*
		 * 실패시 종료 Event저장
		 */
		if (!excuteResult) {
			openTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
			EV_SP_200_65_0_Action action2 = new EV_SP_200_65_0_Action();
			action2.makeEvent(TargetClass.EnergyMeter, target.getMeterId(), TargetClass.EnergyMeter, openTime, "0", OTA_UPGRADE_RESULT_CODE.OTAERR_BYPASS_EXCUTE_FAIL, null, "HES", target.getLocation());
			action2.updateOTAHistory(target.getMeterId(), DeviceType.Meter, openTime, OTA_UPGRADE_RESULT_CODE.OTAERR_BYPASS_EXCUTE_FAIL, bypassResult.getResultValue().toString());

			EV_SP_200_66_0_Action action3 = new EV_SP_200_66_0_Action();
			action3.makeEvent(TargetClass.EnergyMeter, target.getMeterId(), TargetClass.EnergyMeter, openTime, OTA_UPGRADE_RESULT_CODE.OTAERR_BYPASS_EXCUTE_FAIL, null, "HES", target.getLocation());
			action3.updateOTAHistory(target.getMeterId(), DeviceType.Meter, openTime, OTA_UPGRADE_RESULT_CODE.OTAERR_BYPASS_EXCUTE_FAIL, bypassResult.getResultValue().toString());
		}

		return result;
	}
}
