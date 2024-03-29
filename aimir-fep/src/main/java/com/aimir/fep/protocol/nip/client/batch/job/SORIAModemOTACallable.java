/**
 * 
 */
package com.aimir.fep.protocol.nip.client.batch.job;

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.bypass.BypassDevice;
import com.aimir.fep.protocol.fmp.common.Target;
import com.aimir.fep.protocol.nip.CommandNIProxy;
import com.aimir.fep.protocol.nip.client.NiClient;
import com.aimir.fep.protocol.nip.client.actions.CommandActionResult;
import com.aimir.fep.protocol.nip.client.batch.excutor.IBatchCallable;
import com.aimir.fep.protocol.nip.client.batch.excutor.CallableBatchExcutor.CBE_RESULT_CONSTANTS;
import com.aimir.fep.protocol.nip.client.batch.excutor.CallableBatchExcutor.CBE_STATUS_CONSTANTS;
import com.aimir.fep.protocol.nip.frame.GeneralFrame;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.FrameOption_Type;
import com.aimir.fep.trap.actions.SP.EV_SP_200_64_0_Action;
import com.aimir.fep.trap.actions.SP.EV_SP_200_65_0_Action;
import com.aimir.fep.trap.actions.SP.EV_SP_200_66_0_Action;
import com.aimir.fep.trap.common.EV_Action.OTA_UPGRADE_RESULT_CODE;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.Device.DeviceType;
import com.aimir.model.device.Modem;
import com.aimir.util.DateTimeUtil;

/**
 * @author simhanger
 *
 */
@Deprecated
public class SORIAModemOTACallable implements IBatchCallable {
	private static Logger logger = LoggerFactory.getLogger(SORIAModemOTACallable.class);
	private Target target;
	private Map<String, Object> params;
	private IoSession externalNISession;

	public SORIAModemOTACallable(Target target, Map<String, Object> params) {
		logger.debug("# SORIAModemOTACallable = [{}][{}]", target.toString(), params.toString());
		this.target = target;
		this.params = params;
	}

	public void setExternalNISession(IoSession session) {
		this.externalNISession = session;
	}

	@Override
	public Map<CBE_RESULT_CONSTANTS, Object> call() throws Exception {
		logger.debug("# SORIAModemOTACallable Start.");
		/*
		 * OTA 시작 Event 저장
		 */
		ModemDao modemDao = DataUtil.getBean(ModemDao.class);
		Modem modem = modemDao.get(target.getModemId());
		TargetClass tClass = TargetClass.valueOf(modem.getModemType().name());

		String openTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
		EV_SP_200_64_0_Action action = new EV_SP_200_64_0_Action();
		action.makeEvent(tClass, target.getModemId(), tClass, openTime, "HES", modem.getLocation());
		action.updateOTAHistory(target.getModemId(), DeviceType.Modem, openTime);

		GeneralFrame generalFrame = CommandNIProxy.setGeneralFrameOption(FrameOption_Type.Firmware, null);
		generalFrame.setFrame();

		NiClient client = CommandNIProxy.getClient(target, generalFrame);
		logger.debug("tempCode ClientHASh = {}", client.hashCode());

		if (externalNISession != null) {
			if (externalNISession.isActive() && externalNISession.isConnected()) {
				client.setSession(externalNISession);
			} else {
				throw new Exception("NI Session is disconnected.");
			}
		}

		BypassDevice bd = new BypassDevice();
		bd.setMeterId(target.getMeterId());
		bd.setModemId(target.getModemId());
		bd.setFw_bin((byte[]) params.get("image"));
		bd.setRemainPackateLength(((byte[]) params.get("image")).length);
		bd.setFwVersion((String) params.get("fw_version"));
		bd.setFwCRC((String) params.get("fw_crc"));
		bd.setTakeOver(Boolean.valueOf((String) params.get("take_over")));
		
		// INSERT START SP-681
		bd.setOptionalVersion((String) params.get("optversion"));
		bd.setOptionalModel((String) params.get("optmodel"));
		
    	//  delete at NI Protocol 5.70
		//bd.setOptionalInstallTime((String) params.get("opttime"));
		// INSERT END SP-681
		
		
		// 모뎀 종류별 패킷 크기 설정
		if (modem.getModemType() == ModemType.MMIU && (modem.getProtocolType() == Protocol.SMS || modem.getProtocolType() == Protocol.GPRS)) { // MBB Modem
			bd.setPacket_size(Integer.parseInt(FMPProperty.getProperty("ota.firmware.modem.packetsize.mbb", "1024")));
		} else if (modem.getModemType() == ModemType.MMIU && modem.getProtocolType() == Protocol.IP) { // Ethernet Modem
			bd.setPacket_size(Integer.parseInt(FMPProperty.getProperty("ota.firmware.modem.packetsize.ethernet", "1024")));
		} else if (modem.getModemType() == ModemType.SubGiga && modem.getProtocolType() == Protocol.IP) { // RF Modem
			bd.setPacket_size(Integer.parseInt(FMPProperty.getProperty("ota.firmware.modem.packetsize.rf", "256")));
		}

		Map<String, Object> conParams = new HashMap<String, Object>();
		conParams.put("NAME_SPACE", target.getNameSpace());
		conParams.put("COMMAND", "cmdModemOTAStart");
		conParams.put("BYPASS_DEVICE", bd);

		/*
		 * Return value setting
		 */
		CommandActionResult actionResult = new CommandActionResult();
		actionResult.setCommnad("cmdModemOTAStart");
		actionResult.setSuccess(false);
		actionResult.setResultValue("Modem OTA Fail.");
		generalFrame.setCommandActionResult(actionResult);

		logger.debug("[SORIAModemOTACallable][{}] Excute START", target.getModemId());
		generalFrame = client.sendCommand(target, generalFrame, null, "cmdModemOTAStart", conParams);
		logger.debug("[SORIAModemOTACallable][{}] Excute END", target.getModemId());

		boolean excuteResult = actionResult.isSuccess();
		Map<CBE_RESULT_CONSTANTS, Object> result = new HashMap<CBE_RESULT_CONSTANTS, Object>();
		result.put(CBE_RESULT_CONSTANTS.TARGET_ID, target.getModemId());
		result.put(CBE_RESULT_CONSTANTS.RESULT_STATE, excuteResult == true ? CBE_STATUS_CONSTANTS.SUCCESS : CBE_STATUS_CONSTANTS.FAIL);
		result.put(CBE_RESULT_CONSTANTS.RESULT_VALUE, generalFrame.getCommandActionResult().getResultValue());

		/*
		 * 실패시 종료 Event저장
		 */
		if (!excuteResult) {
			openTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
			EV_SP_200_65_0_Action action2 = new EV_SP_200_65_0_Action();
			action2.makeEvent(tClass, target.getModemId(), tClass, openTime, "0", OTA_UPGRADE_RESULT_CODE.OTAERR_NI_TRN_FAIL, null, "HES", modem.getLocation());
			action2.updateOTAHistory(bd.getModemId(), DeviceType.Modem, openTime, OTA_UPGRADE_RESULT_CODE.OTAERR_NI_TRN_FAIL, generalFrame.getCommandActionResult().getResultValue().toString());

			EV_SP_200_66_0_Action action3 = new EV_SP_200_66_0_Action();
			action3.makeEvent(tClass, target.getModemId(), tClass, openTime, OTA_UPGRADE_RESULT_CODE.OTAERR_NI_TRN_FAIL, null, "HES", modem.getLocation());
			action3.updateOTAHistory(bd.getModemId(), DeviceType.Modem, openTime, OTA_UPGRADE_RESULT_CODE.OTAERR_NI_TRN_FAIL, generalFrame.getCommandActionResult().getResultValue().toString());
		}

		return result;
	}

}
