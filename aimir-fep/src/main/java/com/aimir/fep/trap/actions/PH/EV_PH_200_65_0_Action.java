package com.aimir.fep.trap.actions.PH;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.EventStatus;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.FirmwareIssueHistoryDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.trap.common.EV_Action.OTA_UPGRADE_TYPE;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.Device.DeviceType;
import com.aimir.model.device.EventAlertAttr;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.model.system.Location;
import com.aimir.notification.FMPTrap;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.StringUtil;

/*
 * Event ID : 200.65.0 evtOTADownloadEnd Processing
 * 
 *  1) RequestID   - UINT
 *  2) UpgradeType - BYTE
 *  3) TargetID    - STRING
 *  4) Result      - BYTE
 *  5) Elapsed     - INT
 *  4) TargetModel - STRING
 */
@Component
public class EV_PH_200_65_0_Action implements EV_Action {
	private static Logger logger = LoggerFactory.getLogger(EV_PH_200_65_0_Action.class);

	@Autowired
	MCUDao mcuDao;

	/*
	 * Please don't change EVENT_MESSAGE message. because of concerned FIRMWARE_ISSUE_HISTORY searching in DB. 
	 */
	private final String EVENT_MESSAGE = "Ended writing FW";

	@Override
	public void execute(FMPTrap trap, EventAlertLog event) throws Exception {
		logger.debug("[EV_PH_200_65_0_Action][evtOTADownloadEnd][{}] Execute.", EVENT_MESSAGE);

		try {
			String issueDate = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");

			String mcuId = trap.getMcuId();
			MCU mcu = mcuDao.get(mcuId);

			logger.debug("[EV_PH_200_65_0_Action][evtOTADownloadEnd] DCU = {}({}), EventCode = {}", trap.getMcuId(), trap.getIpAddr(), trap.getCode());

			String requestId = StringUtil.nullToBlank(event.getEventAttrValue("uintEntry"));
			logger.debug("[EV_PH_200_65_0_Action] requestId={}", requestId);

			String upgradeType = StringUtil.nullToBlank(event.getEventAttrValue("byteEntry"));
			logger.debug("[EV_PH_200_65_0_Action] upgradeType={}, TargetClass={}", OTA_UPGRADE_TYPE.getItem(upgradeType), OTA_UPGRADE_TYPE.getItem(upgradeType).getTargetClass().name());

			String targetId = StringUtil.nullToBlank(event.getEventAttrValue("stringEntry"));
			logger.debug("[EV_PH_200_65_0_Action] targetId={}", targetId);

			String excuteResult = StringUtil.nullToBlank(event.getEventAttrValue("byteEntry.1"));
			int resultCode = -1;
			try {
				resultCode = Integer.parseInt(excuteResult);
			} catch (Exception e) {
				logger.error("Result code parsing error -" + excuteResult + " : " + e, e);
			}
			logger.debug("[EV_PH_200_65_0_Action] result={}", OTA_UPGRADE_RESULT_CODE.getItem(resultCode).getDesc());

			String elapse = StringUtil.nullToBlank(event.getEventAttrValue("intEntry"));
			logger.debug("[EV_PH_200_65_0_Action] elapse={}", elapse);

			String targetModel = StringUtil.nullToBlank(event.getEventAttrValue("stringEntry.1")); // Thirdparty 방식일때만 값이 채워져서 올라옴.
			logger.debug("[EV_PH_200_65_0_Action] targetModel={}", targetModel);

			if (mcu != null) {
				DeviceType deviceType = null;
				switch (OTA_UPGRADE_TYPE.getItem(upgradeType)) {
				case METER:
					deviceType = DeviceType.Meter;
					break;
				case MODEM:
					deviceType = DeviceType.Modem;
					break;
				case DCU_FW:
					deviceType = DeviceType.MCU;
					break;
				case DCU_KERNEL:
					deviceType = DeviceType.MCU;
					break;
				case DCU_COORDINATE:
					deviceType = DeviceType.MCU;
					break;
				case THIRD_PARTY_COORDINATE:
				case THIRD_PARTY_MODEM:
					if (targetModel == null || targetModel.equals("")) {
						throw new Exception("Device target model is null.");
					}

					if (targetModel.equals("NAMR-P214SR") || targetModel.equals("NAMR-P117LT") || targetModel.equals("NAMR-P212ET")) { // RF || Mbb || Ethernet Modem
						deviceType = DeviceType.Modem;
					} else if (targetModel.startsWith("KFSP") || targetModel.startsWith("KFPP")) { // Kaifa Single Phase Meter || Kaifa Poly Phase Meter Meter
						deviceType = DeviceType.Meter;
					} else {
						throw new Exception("Unknown device target model.");
					}
					break;
				default:
					throw new Exception("Unknown device type.");
				}

				/*
				 * DCU Last Comm date update.
				 */
				mcu.setLastCommDate(issueDate);

				/*
				 * Event save.
				 */
				if (OTA_UPGRADE_TYPE.getItem(upgradeType) == OTA_UPGRADE_TYPE.DCU_FW
						|| OTA_UPGRADE_TYPE.getItem(upgradeType) == OTA_UPGRADE_TYPE.DCU_KERNEL 
						|| OTA_UPGRADE_TYPE.getItem(upgradeType) == OTA_UPGRADE_TYPE.DCU_COORDINATE 
						|| OTA_UPGRADE_TYPE.getItem(upgradeType) == OTA_UPGRADE_TYPE.THIRD_PARTY_MODEM
						|| OTA_UPGRADE_TYPE.getItem(upgradeType) == OTA_UPGRADE_TYPE.THIRD_PARTY_COORDINATE) {
					targetId = mcuId; // Coordinator 가 하는 경우 DCU로 이벤트 처리.					
				}
				event.setActivatorType(OTA_UPGRADE_TYPE.getItem(upgradeType).getTargetClass());
				event.setActivatorId(targetId);
				event.setLocation(mcu.getLocation());

				EventAlertAttr ea = EventUtil.makeEventAlertAttr("message", "java.lang.String", getEventMessage(OTA_UPGRADE_TYPE.getItem(upgradeType).getTargetClass(), elapse, OTA_UPGRADE_RESULT_CODE.getItem(resultCode), "DCU"));
				event.append(ea);

				/*
				 * Update OTA History save.
				 */
				updateOTAHistory(targetId, deviceType, issueDate, OTA_UPGRADE_RESULT_CODE.getItem(resultCode), null, requestId);
			} else {
				logger.debug("[EV_PH_200_65_0_Action][evtOTADownloadEnd] DCU = {}({}) : Unknown MCU", trap.getMcuId(), trap.getIpAddr());
			}
		} catch (Exception e) {
			logger.error("[EV_PH_200_65_0_Action][evtOTADownloadEnd] Error - ", e);
		}
	}

	/**
	 * EV_PH_200_65_0 Event Make
	 * 
	 * @param activatorType
	 * @param activatorId
	 * @param targetType
	 * @param openTime
	 * @param elapseTime
	 * @param isSuccess
	 * @param message
	 * @param operatorType
	 *            - HES or DCU
	 */

	public void makeEvent(TargetClass activatorType, String activatorId, TargetClass targetType, String openTime, String elapseTime, OTA_UPGRADE_RESULT_CODE resultCode, String message, String operatorType, Location location) {
		logger.debug("[EV_PH_200_65_0_Action][activatorId={}][evtOTADownloadEnd] MakeEvent.", activatorId);

		//String resultValue = "[Ended writing FW] Target Type=[" + targetType.name() + "], Elapse Time=[" + elapseTime + "], Result=[" + resultCode.getDesc() + "], OperatorType=[" + operatorType + "]";
		String resultValue = getEventMessage(targetType, elapseTime, resultCode, operatorType);
		if (message != null && !message.equals("")) {
			resultValue += ", Msg=[" + message + "]";
		}

		EventAlertLog eventAlertLog = new EventAlertLog();
		eventAlertLog.setStatus(EventStatus.Open);
		eventAlertLog.setOpenTime(openTime);
		eventAlertLog.setLocation(location);
		
		try {
			EventUtil.sendEvent("OTA", activatorType, activatorId, openTime, new String[][] { { "message", resultValue } }, eventAlertLog);
			logger.debug("[EV_PH_200_65_0_Action][activatorId={}][openTime={}] evtOTADownloadEnd - {}", activatorId, openTime, resultValue);
		} catch (Exception e) {
			logger.error("Event save Error - " + e, e);
		}
	}

	/**
	 * Event message make
	 * 
	 * @param targetType
	 * @param elapseTime
	 * @param resultCode
	 * @param operatorType
	 * @return
	 */
	private String getEventMessage(TargetClass targetType, String elapseTime, OTA_UPGRADE_RESULT_CODE resultCode, String operatorType) {
		StringBuilder builder = new StringBuilder();
		builder.append("[" + EVENT_MESSAGE + "]");
		builder.append("Target Type=[" + targetType.name() + "]");
		builder.append(", Elapse Time=[" + elapseTime + "]");
		builder.append(", Result=[" + resultCode.getDesc() + "]");
		builder.append(", OperatorType=[" + operatorType + "]");

		return builder.toString();
	}

	/**
	 * History information Update.
	 * 
	 * @param deviceId
	 * @param deviceType
	 * @param openTime
	 */
	public void updateOTAHistory(String deviceId, DeviceType deviceType, String openTime, OTA_UPGRADE_RESULT_CODE resultCode, String desc) {
		updateOTAHistory(deviceId, deviceType, openTime, resultCode, desc, null);
	}

	public void updateOTAHistory(String deviceId, DeviceType deviceType, String openTime, OTA_UPGRADE_RESULT_CODE resultCode, String desc, String requestId) {
		logger.debug("updateOTAHistory save start.");
		logger.debug("Update OTA History params. DeviceId={}, DeviceType={}, OpentTime={}, ResultCode={}, Desc={}", deviceId, deviceType.name(), openTime, resultCode, desc);

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		FirmwareIssueHistoryDao firmwareIssueHistoryDao = null;

		/*
		 * 개별 Device OTA 이력 UPDATE. 
		 */

		String saveResult = resultCode.getDesc() + ((desc != null && !desc.equals("")) ? "(" + desc + ")" : "");
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
			txStatus = txManager.getTransaction(null);
			firmwareIssueHistoryDao = DataUtil.getBean(FirmwareIssueHistoryDao.class);
			if (requestId != null) {
				firmwareIssueHistoryDao.updateOTAHistory(EVENT_MESSAGE, deviceId, deviceType, openTime, saveResult, requestId);
			} else {
				firmwareIssueHistoryDao.updateOTAHistory(EVENT_MESSAGE, deviceId, deviceType, openTime, saveResult);
			}

			txManager.commit(txStatus);
			logger.debug("updateOTAHistory commit finished.");
		} catch (Exception e) {
			logger.error("ERROR on FirmwareIssueHistory update Transaction - " + e.getMessage(), e);
			if (txStatus != null) {
				txManager.rollback(txStatus);
			}
		}

		logger.debug("-------------");

		/*
		 * FirmwareIssue Update
		 */
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
			txStatus = txManager.getTransaction(null);
			firmwareIssueHistoryDao = DataUtil.getBean(FirmwareIssueHistoryDao.class);
			if (requestId != null) {
				firmwareIssueHistoryDao.updateOTAHistoryIssue(EVENT_MESSAGE, deviceId, deviceType, requestId);
			} else {
				firmwareIssueHistoryDao.updateOTAHistoryIssue(EVENT_MESSAGE, deviceId, deviceType);
			}

			txManager.commit(txStatus);
			logger.debug("updateOTAHistoryIssue commit finished.");
		} catch (Exception e) {
			logger.error("ERROR on FirmwareIssue update Transaction - " + e.getMessage(), e);
			if (txStatus != null) {
				txManager.rollback(txStatus);
			}
		}

		logger.debug("updateOTAHistory save end.");
	}
}