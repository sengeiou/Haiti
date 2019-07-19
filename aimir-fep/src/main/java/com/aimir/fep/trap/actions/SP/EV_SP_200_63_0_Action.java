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
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.trap.common.EV_Action.OTA_UPGRADE_TYPE;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.Device.DeviceType;
import com.aimir.model.system.Location;
import com.aimir.model.device.EventAlertAttr;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.notification.FMPTrap;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.StringUtil;

/*
 * Event ID : 200.63.0 evtOTADownload Processing
 * 
 * 1) RequestID   - UINT 
 * 2) UpgradeType - BYTE 
 * 3) ImageUrl    - STRING
 * 4) TargetModel - STRING
 */
@Component
public class EV_SP_200_63_0_Action implements EV_Action {
	private static Logger logger = LoggerFactory.getLogger(EV_SP_200_63_0_Action.class);

	@Autowired
	MCUDao mcuDao;

	/*
	 * Please don't change EVENT_MESSAGE message. because of concerned FIRMWARE_ISSUE_HISTORY searching in DB. 
	 */
	private final String EVENT_MESSAGE = "Took OTA Command";

	@Override
	public void execute(FMPTrap trap, EventAlertLog event) throws Exception {
		logger.debug("[EV_SP_200_63_0_Action][evtOTADownload][{}] Execute.", EVENT_MESSAGE);

		try {
			String issueDate = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");

			String mcuId = trap.getMcuId();
			MCU mcu = mcuDao.get(mcuId);

			logger.debug("[EV_SP_200_63_0_Action][evtOTADownload] DCU = {}({}), EventCode = {}", trap.getMcuId(), trap.getIpAddr(), trap.getCode());

			String requestId = StringUtil.nullToBlank(event.getEventAttrValue("uintEntry"));
			logger.debug("[EV_SP_200_63_0_Action] requestId={}", requestId);

			String upgradeType = StringUtil.nullToBlank(event.getEventAttrValue("byteEntry"));
			logger.debug("[EV_SP_200_63_0_Action] upgradeType={}, TargetClass={}", OTA_UPGRADE_TYPE.getItem(upgradeType), OTA_UPGRADE_TYPE.getItem(upgradeType).getTargetClass().name());

			String imageUrl = StringUtil.nullToBlank(event.getEventAttrValue("stringEntry"));
			logger.debug("[EV_SP_200_63_0_Action] imageUrl={}", imageUrl);

			String targetModel = StringUtil.nullToBlank(event.getEventAttrValue("stringEntry.1")); // Thirdparty 방식일때만 값이 채워져서 올라옴.
			logger.debug("[EV_SP_200_63_0_Action] targetModel={}", targetModel);

			if (mcu != null) {
				/*
				 * DCU Last Comm date update.
				 */
				mcu.setLastCommDate(issueDate);

				/*
				 * Event save.
				 */
				event.setActivatorType(OTA_UPGRADE_TYPE.getItem(upgradeType).getTargetClass());
				event.setActivatorId(trap.getMcuId());
				event.setLocation(mcu.getLocation());

				String msg = getEventMessage(OTA_UPGRADE_TYPE.getItem(upgradeType).getTargetClass(), "DCU") + ", RequestId=" + requestId;

				EventAlertAttr ea = EventUtil.makeEventAlertAttr("message", "java.lang.String", msg);
				event.append(ea);

				/*
				 * Update OTA History save.
				 */
				updateOTAHistory(null, null, issueDate, requestId);
			} else {
				logger.debug("[EV_SP_200_63_0_Action][evtOTADownload] DCU = {}({}) : Unknown MCU", trap.getMcuId(), trap.getIpAddr());
			}

		} catch (Exception e) {
			logger.error("[EV_SP_200_63_0_Action][evtOTADownload] Error - ", e);
		}

	}

	/**
	 * EV_SP_200_63_0 Event make
	 * 
	 * @param activatorType
	 * @param activatorId
	 * @param targetType
	 * @param openTime
	 * @param operatorType
	 *            - HES or DCU
	 */
	public void makeEvent(TargetClass activatorType, String activatorId, TargetClass targetType, String openTime, String operatorType, Location location) {
		logger.debug("[EV_SP_200_63_0_Action][evtOTADownload] MakeEvent.");
		logger.debug("TargetClass = {}, activatorId={}, targetType={}, openTime={}, operatorType={}", activatorType, activatorId, targetType, openTime, operatorType);

		String resultValue = getEventMessage(targetType, operatorType);

		EventAlertLog eventAlertLog = new EventAlertLog();
		eventAlertLog.setStatus(EventStatus.Open);
		eventAlertLog.setOpenTime(openTime);
		eventAlertLog.setLocation(location);

		try {
			EventUtil.sendEvent("OTA", activatorType, activatorId, openTime, new String[][] { { "message", resultValue } }, eventAlertLog);
			logger.debug("[EV_SP_200_63_0_Action][openTime={}] evtOTADownload - {}", openTime, resultValue);
		} catch (Exception e) {
			logger.error("Event save Error - " + e, e);
		}
	}

	/**
	 * Event message make
	 * 
	 * @param targetType
	 * @param operatorType
	 * @return
	 */
	private String getEventMessage(TargetClass targetType, String operatorType) {
		StringBuilder builder = new StringBuilder();
		builder.append("[" + EVENT_MESSAGE + "]");
		builder.append("Target Type=[" + targetType.name() + "]");
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
	public void updateOTAHistory(String deviceId, DeviceType deviceType, String openTime) {
		updateOTAHistory(deviceId, deviceType, openTime, null);
	}

	public void updateOTAHistory(String deviceId, DeviceType deviceType, String openTime, String requestId) {
		logger.debug("updateOTAHistory save start.");
		logger.debug("Update OTA History params. DeviceId={}, DeviceType={}, OpentTime={}, RequestId={}", deviceId, deviceType.name(), openTime, requestId);

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		FirmwareIssueHistoryDao firmwareIssueHistoryDao = null;

		/*
		 * 개별 Device OTA 이력 UPDATE. 
		 */
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
			txStatus = txManager.getTransaction(null);
			firmwareIssueHistoryDao = DataUtil.getBean(FirmwareIssueHistoryDao.class);
			if (requestId != null) {
				firmwareIssueHistoryDao.updateOTAHistoryFor63_59_31(EVENT_MESSAGE, openTime, "OK", requestId);
			} else {
				firmwareIssueHistoryDao.updateOTAHistory(EVENT_MESSAGE, deviceId, deviceType, openTime, "OK");
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
				firmwareIssueHistoryDao.updateOTAHistoryIssueFor63_59_31(EVENT_MESSAGE, requestId);
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
