package com.aimir.fep.trap.actions.SP;

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
import com.aimir.dao.device.MeterDao;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.trap.common.EV_Action.OTA_UPGRADE_TYPE;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.Device.DeviceType;
import com.aimir.model.system.Location;
import com.aimir.model.device.EventAlertAttr;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Meter;
import com.aimir.notification.FMPTrap;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.StringUtil;

/*
 * Event ID : EV_SP_200_66_0 OTAResult event Processing Class
 *
 * 1) RequestID   - UINT, oid=1.6.0
 * 2) UpgradeType - BYTE, oid=1.4.0
 * 3) TargetID    - STRING, oid=1.11.0
 * 4) Result      - BYTE
 * 5) TargetModel - STRING
 */
@Component
public class EV_SP_200_66_0_Action implements EV_Action {
	private static Logger logger = LoggerFactory.getLogger(EV_SP_200_66_0_Action.class);

	@Autowired
	MCUDao mcuDao;

	/*
	 * Please don't change EVENT_MESSAGE message. because of concerned FIRMWARE_ISSUE_HISTORY searching in DB. 
	 */
	private final String EVENT_MESSAGE = "OTA Result";

	@Override
	public void execute(FMPTrap trap, EventAlertLog event) throws Exception {
		logger.debug("[EV_SP_200_66_0_Action][evtOTADownloadResult][{}] Execute.", EVENT_MESSAGE);

		try {
			String issueDate = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");

			String mcuId = trap.getMcuId();
			MCU mcu = mcuDao.get(mcuId);

			logger.debug("[EV_SP_200_66_0_Action][evtOTADownloadResult] DCU = {}({}), EventCode = {}", trap.getMcuId(), trap.getIpAddr(), trap.getCode());

			String requestId = StringUtil.nullToBlank(event.getEventAttrValue("uintEntry"));
			logger.debug("[EV_SP_200_66_0_Action] requestId={}", requestId);

			String upgradeType = StringUtil.nullToBlank(event.getEventAttrValue("byteEntry"));
			logger.debug("[EV_SP_200_66_0_Action] upgradeType={}, TargetClass={}", OTA_UPGRADE_TYPE.getItem(upgradeType), OTA_UPGRADE_TYPE.getItem(upgradeType).getTargetClass().name());

			String targetId = StringUtil.nullToBlank(event.getEventAttrValue("stringEntry"));
			logger.debug("[EV_SP_200_66_0_Action] targetId={}", targetId);

			String excuteResult = StringUtil.nullToBlank(event.getEventAttrValue("byteEntry.1"));
			int resultCode = -1;
			try {
				resultCode = Integer.parseInt(excuteResult);
			} catch (Exception e) {
				logger.error("Result code parsing error -" + excuteResult + " : " + e, e);
			}
			logger.debug("[EV_SP_200_66_0_Action] result={}", OTA_UPGRADE_RESULT_CODE.getItem(resultCode).getDesc());

			String targetModel = StringUtil.nullToBlank(event.getEventAttrValue("stringEntry.2")); // Thirdparty 방식일때만 값이 채워져서 올라옴.
			logger.debug("[EV_SP_200_66_0_Action] targetModel={}", targetModel);

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

				EventAlertAttr ea = EventUtil.makeEventAlertAttr("message", "java.lang.String", getEventMessage(OTA_UPGRADE_TYPE.getItem(upgradeType).getTargetClass(), OTA_UPGRADE_RESULT_CODE.getItem(resultCode), "DCU"));
				event.append(ea);

				/*
				 * Update OTA History save.
				 */
				updateOTAHistory(targetId, deviceType, issueDate, OTA_UPGRADE_RESULT_CODE.getItem(resultCode), null, requestId);
			} else {
				logger.debug("[EV_SP_200_66_0_Action][evtOTADownloadResult] DCU = {}({}) : Unknown MCU", trap.getMcuId(), trap.getIpAddr());
			}
		} catch (Exception e) {
			logger.error("[EV_SP_200_66_0_Action][evtOTADownloadResult] Error - ", e);
		}
	}

	/**
	 * EV_SP_200_66_0 Event Make
	 * 
	 * @param activatorType
	 * @param activatorId
	 * @param targetType
	 * @param openTime
	 * @param isSuccess
	 * @param operatorType
	 *            - HES or DCU
	 */
	public void makeEvent(TargetClass activatorType, String activatorId, TargetClass targetType, String openTime, OTA_UPGRADE_RESULT_CODE resultCode, String message, String operatorType, Location location) {
		logger.debug("[EV_SP_200_66_0_Action][activatorId={}][evtOTADownloadResult] MakeEvent.", activatorId);

		//String resultValue = "[OTA Result] Target Type=[" + targetType.name() + "], Result=[" + resultCode.getDesc() + "], OperatorType=[" + operatorType + "]";
		String resultValue = getEventMessage(targetType, resultCode, operatorType);
		if (message != null && !message.equals("")) {
			resultValue += ", Msg=[" + message + "]";
		}

		EventAlertLog eventAlertLog = new EventAlertLog();
		eventAlertLog.setStatus(EventStatus.Open);
		eventAlertLog.setOpenTime(openTime);
		eventAlertLog.setLocation(location);

		try {
			EventUtil.sendEvent("OTA", activatorType, activatorId, openTime, new String[][] { { "message", resultValue } }, eventAlertLog);
			logger.debug("[EV_SP_200_66_0_Action][activatorId={}][openTime={}] evtOTADownloadResult - {}", activatorId, openTime, resultValue);
		} catch (Exception e) {
			logger.error("Event save Error - " + e, e);
		}
	}

	/**
	 * Event message make
	 * 
	 * @param targetType
	 * @param resultCode
	 * @param operatorType
	 * @return
	 */
	private String getEventMessage(TargetClass targetType, OTA_UPGRADE_RESULT_CODE resultCode, String operatorType) {
		StringBuilder builder = new StringBuilder();
		builder.append("[" + EVENT_MESSAGE + "]");
		builder.append("Target Type=[" + targetType.name() + "]");
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
		logger.debug("Update OTA History params. DeviceId={}, DeviceType={}, OpentTime={}, ResultCode={}, Desc={}, requestId={}", deviceId, deviceType.name(), openTime, resultCode, desc, requestId);

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
	}

	// INSERT START SP-707
	/**
	 * History information Update 2.
	 * 
	 * @param deviceId
	 * @param deviceType
	 * @param openTime
	 * @param resultCode
	 * @param desc
	 * @param fwVersion
	 * 
	 * @deprecated
	 */
	public void updateOTAHistory2(String deviceId, DeviceType deviceType, String openTime, OTA_UPGRADE_RESULT_CODE resultCode, String desc, String fwVersion) {
		logger.debug("Update OTA History 2 params. DeviceId={}, DeviceType={}, OpentTime={}, ResultCode={}, Desc={}, F/W Version={}", deviceId, deviceType.name(), openTime, resultCode, desc, fwVersion);

		try {
			updateOTAHistory(deviceId, deviceType, openTime, resultCode, desc);

			updateMeterFWVersion(deviceId, deviceType, resultCode, fwVersion);
		} catch (Exception e) {
			logger.error("ERROR on FirmwareIssue update Transaction - " + e.getMessage(), e);
		}
	}

	/**
	 * Update F/W Version(SW Version) of Meter
	 * 
	 * @param deviceId
	 * @param deviceType
	 * @param resultCode
	 * @param fwVresion
	 * 
	 * @deprecated
	 */
	private void updateMeterFWVersion(String deviceId, DeviceType deviceType, OTA_UPGRADE_RESULT_CODE resultCode, String fwVersion) {
		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		MeterDao meterDao = DataUtil.getBean(MeterDao.class);

		if (deviceId == null || deviceType != DeviceType.Meter || resultCode == null || !resultCode.getDesc().equals("Success") || fwVersion == null || fwVersion.equals(""))
			return;

		try {
			/*
			 * Meter FW Version UPDATE
			 */
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
			txStatus = txManager.getTransaction(null);
			Meter meter = meterDao.get(deviceId);
			meter.setSwVersion(fwVersion);

			txManager.commit(txStatus);
			logger.debug("[EV_SP_200_66_0_Action] updateMeterFWVersion F/W Version =[" + fwVersion + "]");
		} catch (Exception e) {
			logger.error("[EV_SP_200_66_0_Action] updateMeterFWVersion Error - " + e, e);
			if (txStatus != null) {
				txManager.rollback(txStatus);
			}
			//throw e;
		}
	}
	// INSERT END SP-707

}
