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
 * Event ID : 223.2.0 evtOtaEnd Processing 
 * 
 * 1) reqId			UINT	4 	OTA request ID
 * 2) taskId		UINT	4	OTA task ID
 * 3) upgradeType	BYTE	1	Upgrade type (0x01: Modem, 0x02: Sensor/Meter)
 * 4) imageKey		STREAM	N	Image key
 * 5) target		STRING	N	Target address
 * 6) size			UINT	4	전체 Image size
 * 7) offset		UINT	4	전송된 Image size
 * 8) result		BYTE	1	OTA result (표 13)
 * 9) lastStartTime	UINT	4	마지막 task start time
 * 10)lastEndTime	UINT	4	마지막 task end time
 * 11)elapse		UINT	4	소요시간(초)
 * 12)count		   	UINT	4	시도 횟수 
 *
 * SriLanka의 경우 DCU, MODEM OTA는 DCU에서 올리고 METER의 경우는 HES에서 올림. (DCU - Meter OTA는 없음) - 2018.07.10 정상훈차장과 협의
 * 
 * evtOtaEnd(223.2.0)
 */
@Component
public class EV_LK_223_2_0_Action implements EV_Action {
	private static Logger log = LoggerFactory.getLogger(EV_LK_223_2_0_Action.class);

	@Autowired
	private MCUDao mcuDao;
	private String target;
	private String size;
	private String result;
	private String lastStartTime;
	private String lastEndTime;
	private String elapse;
	private String count;

	/*
	 * Please don't change EVENT_MESSAGE message. because of concerned FIRMWARE_ISSUE_HISTORY searching in DB. 
	 */
	private final String EVENT_NAME = "OTA End";
	private final String EVENT_MESSAGE_END = "Ended writing FW";
	private final String EVENT_MESSAGE_RESULT = "OTA Result";

	@SuppressWarnings("unused")
	public void execute(FMPTrap trap, EventAlertLog event) throws Exception {
		log.debug("[EV_LK_223_2_0_Action][evtOtaEnd][{}] Execute.", EVENT_MESSAGE_END);

		try {
			String issueDate = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");

			
			String mcuId = trap.getMcuId();
			MCU mcu = mcuDao.get(mcuId);
			Location location = mcu.getLocation();
			log.debug("[EV_LK_223_2_0_Action][evtOtaEnd] DCU = {}({}), Location = {}, EventCode = {}", trap.getMcuId(), trap.getIpAddr(), location.getName(), trap.getCode());

			String reqId = StringUtil.nullToBlank(event.getEventAttrValue("uintEntry"));
			log.debug("[EV_LK_223_2_0_Action] reqId={}", reqId);

			String taskId = StringUtil.nullToBlank(event.getEventAttrValue("uintEntry.1"));
			log.debug("[EV_LK_223_2_0_Action] taskId={}", taskId);

			String upgradeType = StringUtil.nullToBlank(event.getEventAttrValue("byteEntry"));
			log.debug("[EV_LK_223_2_0_Action] upgradeType={}", OTA_UPGRADE_TYPE.getItem(upgradeType).getTargetClass().name());

			String imageKey = StringUtil.nullToBlank(event.getEventAttrValue("streamEntry"));
			log.debug("[EV_LK_223_2_0_Action] imageKey={}", imageKey);

			target = StringUtil.nullToBlank(event.getEventAttrValue("stringEntry"));
			log.debug("[EV_LK_223_2_0_Action] target={}", target);

			size = StringUtil.nullToBlank(event.getEventAttrValue("uintEntry.2"));
			log.debug("[EV_LK_223_2_0_Action] size={}", size);

			String offset = StringUtil.nullToBlank(event.getEventAttrValue("uintEntry.3"));
			log.debug("[EV_LK_223_2_0_Action] offset={}", offset);

			result = StringUtil.nullToBlank(event.getEventAttrValue("byteEntry.4"));
			log.debug("[EV_LK_223_2_0_Action] result={}", OTA_UPGRADE_RESULT_CODE.getItem(Integer.parseInt(result)).getDesc() + "(" + result +")");

			lastStartTime = StringUtil.nullToBlank(event.getEventAttrValue("uintEntry.5"));
			log.debug("[EV_LK_223_2_0_Action] lastStartTime={}", lastStartTime);

			lastEndTime = StringUtil.nullToBlank(event.getEventAttrValue("uintEntry.6"));
			log.debug("[EV_LK_223_2_0_Action] lastEndTime={}", lastEndTime);

			elapse = StringUtil.nullToBlank(event.getEventAttrValue("uintEntry.7"));
			log.debug("[EV_LK_223_2_0_Action] elapse={}", elapse);

			count = StringUtil.nullToBlank(event.getEventAttrValue("uintEntry.8"));
			log.debug("[EV_LK_223_2_0_Action] count={}", count);

			if (mcu != null) {
				mcu.setLastCommDate(issueDate);
				
				/*
				 * Result Event 저장
				 */
				event.setActivatorType(OTA_UPGRADE_TYPE.getItem(upgradeType).getTargetClass());
				event.setActivatorId(target);
				event.setLocation(mcu.getLocation());
				
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

				updateOTAHistory(target, deviceType, issueDate, OTA_UPGRADE_RESULT_CODE.getItem(Integer.parseInt(result)), getEventMessage(OTA_UPGRADE_TYPE.getItem(upgradeType).getTargetClass(), "DCU"), reqId);
				log.debug("[EV_LK_223_2_0_Action][evtOTAResult][{}] Execute.", EVENT_MESSAGE_RESULT);
			} else {
				log.debug("[EV_LK_223_2_0_Action][evtOTAResult] DCU = {}({}) : Unknown DCU", trap.getMcuId(), trap.getIpAddr());
			}
		} catch (Exception e) {
			log.error("evtOTAResult save error - " + e.getMessage(), e);
		}
	}

	/**
	 * EV_LK_223_2_0_Action Event Make
	 * 
	 * @param activatorType
	 * @param activatorId
	 * @param targetType
	 * @param openTime
	 * @param resultCode
	 * @param operatorType
	 *            - HES or DCU
	 */
	public void makeEvent(TargetClass activatorType, String activatorId, TargetClass targetType, String openTime, OTA_UPGRADE_RESULT_CODE resultCode, String message, String operatorType, Location location) {
		log.debug("[EV_LK_223_2_0_Action][OTA End] MakeEvent.");

		String resultValue = "Target Type=[" + targetType.name() + "]" + ", OperatorType=[" + operatorType + "], ResultCode=[" + resultCode.name() + "], Msg = " + message;

		EventAlertLog eventAlertLog = new EventAlertLog();
		eventAlertLog.setStatus(EventStatus.Open);
		eventAlertLog.setOpenTime(openTime);
		eventAlertLog.setLocation(location);

		try {
			EventUtil.sendEvent(EVENT_NAME, activatorType, activatorId, openTime, new String[][] { { "message", resultValue } }, eventAlertLog);
			log.debug("[EV_LK_223_2_0_Action][openTime={}] evtOtaEndResult - {}", openTime, resultValue);
		} catch (Exception e) {
			log.error("Event save Error - " + e, e);
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
		builder.append("[" + EVENT_MESSAGE_RESULT + "]");
		builder.append("Target Type=[" + targetType.name() + "]");
		builder.append("Target Address=[" + target + "]");
		builder.append(", OperatorType=[" + operatorType + "]");
		builder.append(", Size=[" + size + "]");
		builder.append(", Result=[" + OTA_UPGRADE_RESULT_CODE.getItem(Integer.parseInt(result)).getDesc() + "]");
		builder.append(", LastStartTime=[" + lastStartTime + "]");
		builder.append(", LastEndTime=[" + lastEndTime + "]");
		builder.append(", Elapse=[" + elapse + "]");
		builder.append(", Count=[" + count + "]");

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
		log.debug("updateOTAHistory save start.");
		log.debug("Update OTA History params. DeviceId={}, DeviceType={}, OpentTime={}, ResultCode={}, Desc={}, requestId={}"
				, deviceId, deviceType.name(), openTime, resultCode, desc, requestId);

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
				firmwareIssueHistoryDao.updateOTAHistory(EVENT_MESSAGE_RESULT, deviceId, deviceType, openTime, saveResult, requestId);
			} else {
				firmwareIssueHistoryDao.updateOTAHistory(EVENT_MESSAGE_RESULT, deviceId, deviceType, openTime, saveResult);
			}

			txManager.commit(txStatus);
			log.debug("updateOTAHistory commit finished.");
		} catch (Exception e) {
			log.error("ERROR on FirmwareIssueHistory update Transaction - " + e.getMessage(), e);
			if (txStatus != null) {
				txManager.rollback(txStatus);
			}
		}

		log.debug("-------------");

		/*
		 * FirmwareIssue Update
		 */
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
			txStatus = txManager.getTransaction(null);

			firmwareIssueHistoryDao = DataUtil.getBean(FirmwareIssueHistoryDao.class);
			if (requestId != null) {
				firmwareIssueHistoryDao.updateOTAHistoryIssue(EVENT_MESSAGE_RESULT, deviceId, deviceType, requestId);
			} else {
				firmwareIssueHistoryDao.updateOTAHistoryIssue(EVENT_MESSAGE_RESULT, deviceId, deviceType);
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

	/**
	 * History information Update.
	 * 
	 * @param deviceId
	 * @param deviceType
	 * @param openTime
	 */
	@Deprecated
	public void updateOTAHistory(String deviceId, DeviceType deviceType, String openTime, String eventMessage) {
		log.debug("Update OTA History params. DeviceId={}, DeviceType={}, OpentTime={}", deviceId, deviceType.name(), openTime);

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		FirmwareIssueHistoryDao firmwareIssueHistoryDao = DataUtil.getBean(FirmwareIssueHistoryDao.class);

		/*
		 * 개별 Device OTA 이력 UPDATE. 
		 *  - DCU의 경우 Trap이벤트에 issuedate, firmwareid 정보가 없기때문에 가장 최근에 실행한 Device 의 이력을 업데이트하는 방식으로 진행함. 
		 */
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
			txStatus = txManager.getTransaction(null);
			firmwareIssueHistoryDao.updateOTAHistory(eventMessage, deviceId, deviceType, openTime, "OK");
			txManager.commit(txStatus);
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
			firmwareIssueHistoryDao.updateOTAHistoryIssue(eventMessage, deviceId, deviceType);
			txManager.commit(txStatus);
		} catch (Exception e) {
			log.error("ERROR on FirmwareIssue update Transaction - " + e.getMessage(), e);
			if (txStatus != null) {
				txManager.rollback(txStatus);
			}
		}
	}

}
