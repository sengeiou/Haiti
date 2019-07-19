package com.aimir.fep.trap.actions.LK;

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
 * Event ID : 223.1.0 evtOtaStart Processing 
 * 
 *  1) reqId          - UINT : OTA request ID
 *  2) taskId         - UINT  : OTA task ID
 *  3) upgradeType    - BYTE : Upgrade type (0x01: Modem, 0x02: Sensor/Meter)
 *  4) imageKey       - STREAM : Image key
 *  5) target         - STRING : Target address
 *  6) size			  - UINT : 전체 Image size
 *  
 *  SriLanka의 경우 DCU, MODEM OTA는 DCU에서 올리고 METER의 경우는 HES에서 올림. (DCU - Meter OTA는 없음) - 2018.07.10 정상훈차장과 협의
 */
@Component
public class EV_LK_223_1_0_Action implements EV_Action {
	private static Logger log = LoggerFactory.getLogger(EV_LK_223_1_0_Action.class);

	@Autowired
	MCUDao mcuDao;
	Location location;

	/*
	 * Please don't change EVENT_MESSAGE message. because of concerned FIRMWARE_ISSUE_HISTORY searching in DB. 
	 */
	private final String EVENT_NAME = "OTA Start";
	private final String EVENT_MESSAGE = "Started writing FW";

	@SuppressWarnings("unused")
	public void execute(FMPTrap trap, EventAlertLog event) throws Exception {
		log.debug("EventName[evtOtaStart] " + " EventCode[" + trap.getCode() + "] Modem[" + trap.getSourceId() + "]");

		try {
			String issueDate = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");

			String mcuId = trap.getMcuId();
			MCU mcu = mcuDao.get(mcuId);
			location = mcu.getLocation();

			log.debug("[EV_LK_223_1_0_Action][evtOtaStart] DCU = {}({}), Location = {}, EventCode = {}", trap.getMcuId(), trap.getIpAddr(), location.getName(), trap.getCode());

			String reqId = StringUtil.nullToBlank(event.getEventAttrValue("uintEntry"));
			log.debug("[EV_LK_223_1_0_Action] reqId={}", reqId);

			String taskId = StringUtil.nullToBlank(event.getEventAttrValue("uintEntry.1"));
			log.debug("[EV_LK_223_1_0_Action] taskId={}", taskId);

			String upgradeType = StringUtil.nullToBlank(event.getEventAttrValue("byteEntry"));
			log.debug("[EV_LK_223_1_0_Action] upgradeType={}", OTA_UPGRADE_TYPE.getItem(upgradeType).getTargetClass().name());

			String imageKey = StringUtil.nullToBlank(event.getEventAttrValue("streamEntry"));
			log.debug("[EV_LK_223_1_0_Action] imageKey={}", imageKey);

			String target = StringUtil.nullToBlank(event.getEventAttrValue("stringEntry"));
			log.debug("[EV_LK_223_1_0_Action] target={}", target);

			String size = StringUtil.nullToBlank(event.getEventAttrValue("uintEntry.2"));
			log.debug("[EV_LK_223_1_0_Action] size={}", size);

			if (mcu != null) {
				mcu.setLastCommDate(issueDate);

				event.setActivatorType(OTA_UPGRADE_TYPE.getItem(upgradeType).getTargetClass());
				event.setActivatorId(target);
				event.setLocation(location);

				event.setActivatorIp(mcu.getIpAddr());
				event.setSupplier(mcu.getSupplier());

				EventAlertAttr ea = EventUtil.makeEventAlertAttr("message", "java.lang.String", getEventMessage(OTA_UPGRADE_TYPE.getItem(upgradeType).getTargetClass(), "DCU"));
				event.append(ea);

				/*
				 * History 정보 Update
				 */
				DeviceType deviceType = null;
				if (OTA_UPGRADE_TYPE.getItem(upgradeType) == OTA_UPGRADE_TYPE.METER) {
					deviceType = DeviceType.Meter;
				} else if (OTA_UPGRADE_TYPE.getItem(upgradeType) == OTA_UPGRADE_TYPE.MODEM) {
					deviceType = DeviceType.Modem;
				} else {
					deviceType = DeviceType.MCU;
				}

				updateOTAHistory(target, deviceType, issueDate, reqId);

			} else {
				log.debug("[EV_LK_223_1_0_Action][evtOtaStart] DCU = {}({}) : Unknown DCU", trap.getMcuId(), trap.getIpAddr());
			}
		} catch (Exception e) {
			log.error("[EV_LK_223_1_0_Action][evtOtaStart] Error - ", e);
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
		log.debug("updateOTAHistory save start.");
		log.debug("Update OTA History params. DeviceId={}, DeviceType={}, OpentTime={}, RequestId={}", deviceId, deviceType.name(), openTime, requestId);

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
				firmwareIssueHistoryDao.updateOTAHistory(EVENT_MESSAGE, deviceId, deviceType, openTime, "OK", requestId);
			} else {
				firmwareIssueHistoryDao.updateOTAHistory(EVENT_MESSAGE, deviceId, deviceType, openTime, "OK");
			}

			txManager.commit(txStatus);
			log.debug("updateOTAHistory commit finished.");
		} catch (Exception e) {
			log.error("ERROR on FirmwareIssueHistory update Transaction - " + e.getMessage(), e);
			if (txStatus != null) {
				txManager.rollback(txStatus);
			}
		}

		/*
		 * FirmwareIssue Update
		 */
		try {
			txStatus = txManager.getTransaction(null);

			firmwareIssueHistoryDao = DataUtil.getBean(FirmwareIssueHistoryDao.class);
			if (requestId != null) {
				firmwareIssueHistoryDao.updateOTAHistoryIssue(EVENT_MESSAGE, deviceId, deviceType, requestId);
			} else {
				firmwareIssueHistoryDao.updateOTAHistoryIssue(EVENT_MESSAGE, deviceId, deviceType);
			}

			txManager.commit(txStatus);
			log.debug("updateOTAHistoryIssue commit finished.");
		} catch (Exception e) {
			log.error("ERROR on FirmwareIssue update Transaction - " + e.getMessage(), e);
			if (txStatus != null) {
				txManager.rollback(txStatus);
			}
		}

		log.debug("updateOTAHistory save end.");
	}

	public void makeEvent(TargetClass activatorType, String activatorId, TargetClass targetType, String openTime, String operatorType, Location location) {
		log.debug("[EV_LK_223_1_0_Action][OTA Start] MakeEvent.");

		String resultValue = getEventMessage(targetType, operatorType);

		EventAlertLog eventAlertLog = new EventAlertLog();
		eventAlertLog.setStatus(EventStatus.Open);
		eventAlertLog.setOpenTime(openTime);
		eventAlertLog.setLocation(location);

		try {
			EventUtil.sendEvent(EVENT_NAME, activatorType, activatorId, openTime, new String[][] { { "message", resultValue } }, eventAlertLog);
			log.debug("[EV_LK_223_1_0_Action][openTime={}] evtOTADownloadStart - {}", openTime, resultValue);
		} catch (Exception e) {
			log.error("Event save Error - " + e, e);
		}
	}

}
