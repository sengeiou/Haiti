/**
 * 
 */
package com.aimir.fep.trap.actions.PH;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.FirmwareIssueHistoryDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.Device.DeviceType;
import com.aimir.model.device.EventAlertAttr;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.notification.FMPTrap;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.StringUtil;

/*
 * Event ID : 200.59.0 evtFWUpdate Processing - Firmware Update
 *
 * 1) RequestID   - UINT 
 * 2) UpgradeType - BYTE 
 * 3) Version     - STRING
 * 4) TargetModel - STRING
 * 
 */
@Component
public class EV_PH_200_59_0_Action implements EV_Action {
	private static Logger logger = LoggerFactory.getLogger(EV_PH_200_59_0_Action.class);

	@Autowired
	MCUDao mcuDao;

	/*
	 * Please don't change EVENT_MESSAGE message. because of concerned FIRMWARE_ISSUE_HISTORY searching in DB. 
	 */
	private final String EVENT_MESSAGE = "Firmware Update";

	@Override
	public void execute(FMPTrap trap, EventAlertLog event) throws Exception {
		logger.debug("[EV_PH_200_59_0_Action][evtFWUpdate][{}] Execute.", EVENT_MESSAGE);

		try {
			String issueDate = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");

			String mcuId = trap.getMcuId();
			MCU mcu = mcuDao.get(mcuId);

			logger.debug("[EV_PH_200_59_0_Action][evtFWUpdate] DCU = {}({}), EventCode = {}", trap.getMcuId(), trap.getIpAddr(), trap.getCode());

			String requestId = StringUtil.nullToBlank(event.getEventAttrValue("uintEntry"));
			logger.debug("[EV_PH_200_59_0_Action] requestId={}", requestId);

			String upgradeType = StringUtil.nullToBlank(event.getEventAttrValue("byteEntry"));
			OTA_UPGRADE_TYPE upgardeType = OTA_UPGRADE_TYPE.getItem(upgradeType);
			TargetClass targetClass = OTA_UPGRADE_TYPE.getItem(upgradeType).getTargetClass();
			logger.debug("[EV_PH_200_59_0_Action] UpgradeType={}, TargetClass={}", upgardeType, targetClass.name());

			DeviceType deviceType = null;
			switch (upgardeType) {
			case MODEM:
			case THIRD_PARTY_MODEM:
				deviceType = DeviceType.Modem;
				break;
			case METER:
				deviceType = DeviceType.Meter;
				break;
			case DCU_FW:
			case DCU_KERNEL:
			case DCU_COORDINATE:
			case THIRD_PARTY_COORDINATE:
				deviceType = DeviceType.MCU;
				break;
			default:
				deviceType = DeviceType.MCU;
				break;
			}

			String version = StringUtil.nullToBlank(event.getEventAttrValue("stringEntry"));
			logger.debug("[EV_PH_200_59_0_Action] version={}", version);

			String targetModel = StringUtil.nullToBlank(event.getEventAttrValue("stringEntry.1")); // Thirdparty 방식일때만 값이 채워져서 올라옴.
			logger.debug("[EV_PH_200_59_0_Action] targetModel={}", targetModel);

			if (mcu != null) {
				/*
				 * DCU Last Comm date update.
				 */
				mcu.setLastCommDate(issueDate);

				/*
				 * Event save.
				 */
				event.setActivatorType(OTA_UPGRADE_TYPE.getItem(upgradeType).getTargetClass());
				event.setActivatorId(trap.getSourceId());
				event.setLocation(mcu.getLocation());

				String msg = getEventMessage(OTA_UPGRADE_TYPE.getItem(upgradeType).getTargetClass(), version, "DCU") + ", RequestId=" + requestId;

				EventAlertAttr ea = EventUtil.makeEventAlertAttr("message", "java.lang.String", msg);
				event.append(ea);

				/*
				 * Update OTA History save.
				 */
				updateOTAHistory(mcuId, deviceType, issueDate, version, requestId);
			} else {
				logger.debug("[EV_PH_200_59_0_Action][evtFWUpdate] DCU = {}({}) : Unknown MCU", trap.getMcuId(), trap.getIpAddr());
			}
		} catch (Exception e) {
			logger.error("[EV_PH_200_59_0_Action][evtFWUpdate] Error - ", e);
		}
	}

	/**
	 * Event message make
	 * 
	 * @param targetType
	 * @param version
	 * @param operatorType
	 * @return
	 */
	private String getEventMessage(TargetClass targetType, String version, String operatorType) {
		StringBuilder builder = new StringBuilder();
		builder.append("[" + EVENT_MESSAGE + "]");
		builder.append("Target Type=[" + targetType.name() + "]");
		builder.append(", Version=[" + version + "]");
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
	public void updateOTAHistory(String deviceId, DeviceType deviceType, String openTime, String resultStatus, String requestId) {
		logger.debug("updateOTAHistory save start.");
		logger.debug("Update OTA History params. DeviceId={}, DeviceType={}, OpentTime={}, ResultStatus={}, requestId={}", deviceId, deviceType, openTime, resultStatus, requestId);
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
				firmwareIssueHistoryDao.updateOTAHistoryFor63_59_31(EVENT_MESSAGE, openTime, resultStatus, requestId);
			} else {
				firmwareIssueHistoryDao.updateOTAHistory(EVENT_MESSAGE, deviceId, deviceType, openTime, resultStatus);
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
