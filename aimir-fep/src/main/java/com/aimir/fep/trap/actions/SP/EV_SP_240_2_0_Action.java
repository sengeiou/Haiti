package com.aimir.fep.trap.actions.SP;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.EventStatus;
import com.aimir.constants.CommonConstants.ModemStatus;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.FirmwareIssueHistoryDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MeterAttrDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.protocol.nip.frame.payload.AlarmEvent;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.Device.DeviceType;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.MeterAttr;
import com.aimir.model.device.Modem;
import com.aimir.model.system.Supplier;
import com.aimir.notification.FMPTrap;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeUtil;

/**
 * Event ID : EV_SP_240.2.0 (Modem Tamper Alarm)
 * 
 * 240.2 tamper alarm is not saved. alarm data has to be checked and then invoke
 * alarm.
 *
 * @author tatsumi
 * @version $Rev: 1 $, $Date: 2016-05-13 10:00:00 +0900 $,
 */
@Component
public class EV_SP_240_2_0_Action implements EV_Action {
	private static Log log = LogFactory.getLog(EV_SP_240_2_0_Action.class);

	@Resource(name = "transactionManager")
	JpaTransactionManager txmanager;

	@Autowired
	SupplierDao supplierDao;

	@Autowired
	MCUDao mcuDao;

	@Autowired
	ModemDao modemDao;

	@Autowired
	MeterDao meterDao;

	@Autowired
	FirmwareIssueHistoryDao firmwareIssueHistoryDao;

	@Autowired
	MeterAttrDao meterAttrDao;
	/*
	 * Please don't change EVENT_MESSAGE message. because of concerned FIRMWARE_ISSUE_HISTORY searching in DB. 
	 */
	private final String EVENT_MESSAGE = "Firmware Update";

	/**
	 * execute event action
	 *
	 * @param trap
	 *            - FMP Trap(Modem Tamper Event)
	 * @param event
	 *            - Event Alert Log Data
	 */
	public void execute(FMPTrap trap, EventAlertLog event) throws Exception {
		String openTime = DateTimeUtil.getCurrentDateTimeByFormat(null);
		log.debug("EV_SP_240_2_0_Action : EventName[modemTamper] " + " EventCode[" + trap.getCode() + "] MCU[" + trap.getMcuId() + "] TargetClass[" + event.getActivatorType() + "] openTime[" + openTime + "]");

		TransactionStatus txstatus = null;
		//        TargetClass targetClass = event.getActivatorType();
		try {
			txstatus = txmanager.getTransaction(null);

			// tamper alarm source is modem.
			Modem modem = modemDao.get(trap.getSourceId());
			// modem:meter = 1:1
			Meter meter = null;

			//SP-1019
            if (modem != null && !modem.getMeter().isEmpty()) {
                for (Meter m : modem.getMeter()) {
                    if (m.getModemPort() == null || m.getModemPort() == 0) {
                        meter = m;
                        break;
                    }
                }
            }

			MCU mcu = mcuDao.get(trap.getMcuId());
			if (mcu == null) {
				log.debug("no mcu intance exist mcu[" + trap.getMcuId() + "]");
				return;
			}
			log.debug("EV_SP_240_2_0_Action : event[" + event.toString() + "]");

			/*
			 * moAlarmData is streamEntry, it can be one or more.
			 * streamEntry must be taken as hex. because string is broken.
			 */
			List<String> moAlarmDataList = new ArrayList<String>();
			String moAlarmDataHex = null;
			for (int i = 0;; i++) {
				if (i > 0)
					moAlarmDataHex = event.getEventAttrValue("streamEntry." + i + ".hex");
				else
					moAlarmDataHex = event.getEventAttrValue("streamEntry.hex");

				if (moAlarmDataHex == null || "".equals(moAlarmDataHex))
					break;
				else
					moAlarmDataList.add(moAlarmDataHex);
			}

			/*
			 * common for below loop
			 */
			Integer defSupplierId = FMPProperty.getProperty("supplier.default.id") != null ? Integer.parseInt(FMPProperty.getProperty("supplier.default.id")) : null;
			Supplier supplier = null;
			if (defSupplierId != null && mcu.getSupplier() == null) {
				supplier = supplierDao.get(defSupplierId);
			} else {
				supplier = mcu.getSupplier();
			}

			/*
			 * refer to NI Protocol 3.6 Event Frame
			 * count : 1byte
			 * Alarm/Event*
			 *   - Time : 7 bytes
			 *   - id : 2 bytes
			 *   - payload : 4 bytes
			 */
			for (String moAlarmData : moAlarmDataList) {
				makeModemEvent(modem, meter, supplier, moAlarmData);
			}

		} catch (Exception ex) {
			log.error(ex, ex);
		} finally {
			if (txstatus != null)
				txmanager.commit(txstatus);
		}

		log.debug("Modem Tamper Action Compelte");
	}

	public void makeModemEvent(Modem modem, Meter meter, Supplier supplier, String moAlarmData) {
		int pos = 0;
		try {
			if (supplier != null) {
				String issueDate = moAlarmData.substring(pos, 14);
				pos += 14;
				byte[] bx = DataUtil.readByteString(issueDate);
				log.debug("issue date : [" + Hex.decode(bx) + "]");
				issueDate = DataUtil.getEMnvModemDate(bx);

				// Id
				String eventId = moAlarmData.substring(pos, pos + 4);
				pos += 4;
				bx = Hex.encode(eventId);// DataUtil.readByteString(eventId);
				Integer eventIdN = DataUtil.getIntTo2Byte(bx);

				String payload = moAlarmData.substring(pos, pos + 8);
				pos += 8;
				makeModemEvent(modem, meter, issueDate, eventIdN, payload);
			}
		} catch (Exception e) {
			log.error(e, e);
		}
	}

	public void makeModemEvent(Modem modem, Meter meter, String issueDate, Integer eventIdN, String payload) throws Exception {
		String openTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");

		byte[] bx = Hex.encode(payload);
		String eventClassName = null;
		String message = null;
		String activatorType = modem.getModemType().name();
		String activatorId = modem.getDeviceSerial();
		EventAlertLog _event = new EventAlertLog();
		_event.setOpenTime(issueDate);

		log.debug("Payload[" + Hex.decode(bx) + "]");
		AlarmEvent.AlarmId alarmId = null;
		for (AlarmEvent.AlarmId a : AlarmEvent.AlarmId.values()) {
			if (a.getCode() == eventIdN) {
				alarmId = a;
				break;
			}
		}  
		
		if(eventIdN == null || alarmId == null) {
			log.error("Unknown Event ID =" + eventIdN + " in EventFrame[" + modem.getDeviceSerial() + "]. Please check NI Protocol document.");
			return;
		}
		
		log.info("AlarmId = " + alarmId.name());
		
		if (alarmId == AlarmEvent.AlarmId.PowerFail) {
			eventClassName = "Power Alarm";
			message = "Power Down";

			if (meter != null) {
				activatorType = meter.getMeterType().getName();
				activatorId = meter.getMdsId();

				try {
					// UPDATE START SP-980
			        if (meter.getMeterStatus() == null ||  
			                (meter.getMeterStatus() != null && 
			                !meter.getMeterStatus().getName().equals("CutOff") && 
			                !meter.getMeterStatus().getName().equals("Delete"))){
			        	meter.setMeterStatus(CommonConstants.getMeterStatusByName("PowerDown"));
			        	meterDao.update(meter);
			        }
			        // UPDATE END SP-980
				} catch (Exception e) {
					log.error(e, e);
				}
			}
		} else if (alarmId == AlarmEvent.AlarmId.MeterNOResponse) {
			eventClassName = "Meter Alarm";
			message = "Meter No Response";

			try{
				modem.setCommState(ModemStatus.MeterError.getStatus());
				modem.setLastLinkTime(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
				modemDao.update(modem);
			}catch(Exception e){
				log.warn(e,e);
			}
			
		} else if (alarmId == AlarmEvent.AlarmId.PowerRestore) {
			log.debug("[PworerRestore] Event~!!!");
			eventClassName = "Power Alarm";
			message = "Power Restore";
			_event.setStatus(EventStatus.Cleared);

			if (meter != null) {
				log.debug("[PworerRestore] MeterId=" + meter.getMdsId() + ", meterModel=" + meter.getModel());
				
				
				activatorType = meter.getMeterType().getName();
				activatorId = meter.getMdsId();

				try {
					if (meter.getMeterStatus().getName().equals("PowerDown")) {
						meter.setMeterStatus(CommonConstants.getMeterStatusByName("Normal"));
						meterDao.update(meter);
					}
				} catch (Exception e) {
					log.error(e, e);
				}
			}
		} else if (alarmId == AlarmEvent.AlarmId.CaseAlarmOpen) {
			eventClassName = "Cover Alarm";
			message = "Cover Open";
			_event.append(EventUtil.makeEventAlertAttr("caseState", "java.lang.String", "Status : Open"));

			if (meter != null) {
				activatorType = meter.getMeterType().getName();
				activatorId = meter.getMdsId();
			}
		} else if (alarmId == AlarmEvent.AlarmId.CaseAlarmClose) {
			eventClassName = "Cover Alarm";
			message = "Cover Close";
			_event.append(EventUtil.makeEventAlertAttr("caseState", "java.lang.String", "Status : Close"));
			_event.setStatus(EventStatus.Cleared);

			if (meter != null) {
				activatorType = meter.getMeterType().getName();
				activatorId = meter.getMdsId();
			}
		} else if (alarmId == AlarmEvent.AlarmId.LineMissing) {
			eventClassName = "Power Alarm";
			message = "Line Missing";

			if (meter != null) {
				activatorType = meter.getMeterType().getName();
				activatorId = meter.getMdsId();
			}
		} else if (alarmId == AlarmEvent.AlarmId.Equipment_ConfigurationChanged) {
			eventClassName = "Equipment Configuration Changed";
			message = "Configuration Changed from Tamper"; //TODO check payload. it need more information.
		} else if (alarmId == AlarmEvent.AlarmId.Equipment_Installed) {
			eventClassName = "Equipment Installation";
			message = "Installed from Tamper"; //TODO check payload, it need more information
		} else if (alarmId == AlarmEvent.AlarmId.Equipment_Registered) {
			eventClassName = "Equipment Registration";
			message = "Registred from Tamper";
		} else if (alarmId == AlarmEvent.AlarmId.Equipment_FirmwareUpdate) { // Modem OTA 최종 성공시에만 올라옴
			
			eventClassName = "OTA";
			String otaStepMsg = "";
			String ver = Hex.decode(new byte[] { bx[0], bx[1] });
			ver = Double.parseDouble(ver.substring(0, 2) + "." + ver.substring(2, 4)) + "";
			message = "F/W Version[" + ver + "]";
			
			log.info("[" + alarmId + "] Received event. message = " + message);
			
			/*
			 * Modem OTA후 modem 시간이 초기화되는 이슈로인해 
			 * 이슈 발생시간을 서버시간으로 설정하고, 실제 모뎀시간을 메시지로 출력해준다.
			 */
			if (issueDate.substring(0, 8).equals("20160101") || issueDate.substring(0, 4).equals("2000") || TimeUtil.getDayDuration(issueDate, TimeUtil.getCurrentTime()) >= 90) {
				message = "Firmware Updated. Current Modem Time[" + issueDate + "] is before timesync.";
				issueDate = openTime;
			}

			modem.setFwVer(ver);
			modem.setLastLinkTime(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
			modemDao.update(modem);
			log.info("[MODEM_OTA] Equipment_FirmwareUpdate received. issueDate="+ issueDate +", ModemId=" + modem.getDeviceSerial() + ", MeterId=" + meter.getMdsId() + ", Modem FW Version old[" + modem.getFwVer() + "] to new[" +ver+ "], message=" + message);
			
			int status = DataUtil.getIntTo2Byte(new byte[] { bx[2], bx[3] });
			if ((status & 0x0001) != 0)
				otaStepMsg += "[DOWNLOAD_START]";
			if ((status & 0x0002) != 0)
				otaStepMsg += "[BACKUP_DOWNLOAD_START]";
			if ((status & 0x0004) != 0)
				otaStepMsg += "[BOOT_CLI_START]";
			if ((status & 0x0008) != 0)
				otaStepMsg += "[XMODEM_START]";
			if ((status & 0x0010) != 0)
				otaStepMsg += "[CRC_FAIL]";
			if ((status & 0x0020) != 0)
				otaStepMsg += "[BACKUP_CRC_FAIL]";
			if ((status & 0x0040) != 0)
				otaStepMsg += "[DOWNLOAD_SUCCESS]";
			if ((status & 0x0080) != 0)
				otaStepMsg += "[BACKUP_DOWNLOAD_SUCCESS]";
			if ((status & 0x0100) != 0)
				otaStepMsg += "[DOWNLOAD_FAIL]";
			if ((status & 0x0200) != 0)
				otaStepMsg += "[BACKUP_DOWNLOAD_FAIL]";
			if ((status & 0x0400) != 0)
				otaStepMsg += "[BOOT_CHECK_FAIL]";

			message += "-" + otaStepMsg;

			String histMsg = "F/W Version=[" + ver + "]" + otaStepMsg;
			updateOTAHistory(modem.getDeviceSerial(), DeviceType.Modem, openTime, histMsg);
			log.info("[MODEM_OTA] updateOTAHistory finished. eventClassName = " +eventClassName+", message=" + message + ", histMsg = " + histMsg);
		} else if (alarmId == AlarmEvent.AlarmId.TimeSynchronization) {
			eventClassName = "Time Synchronization Performed";
			message = "Time Synchronized"; //TODO check payload
		} else if (alarmId == AlarmEvent.AlarmId.ModemEvent_MeterTimeSynced) {
			// Time Synchronization Performed From Modem
			eventClassName = "Time Synchronization Performed";
			message = "MeterTime Synced By MeterTerminal."; 
			
			String diffMsg = "";
			int timeDiff = DataUtil.getIntTo4Byte(bx);
			diffMsg = "[Diff: "+timeDiff+"sec]";
			
			message += " " + diffMsg;
						
		} else if(alarmId == AlarmEvent.AlarmId.MeterUpgradeFinished){  // Meter ota by modem. Meter OTA 성공시에만 올라옴.
			String fwVersion = Hex.decode(bx);
			log.info("[" + alarmId + "] Received event. fwVersion = " + fwVersion);
			
			meter.setSwVersion(fwVersion);
			meterDao.update(meter);
			log.info("[METER_OTA_BY_MODEM] MeterUpgradeFinished Event received. ModemId=" + modem.getDeviceSerial() + ", MeterId=" + meter.getMdsId() + ", Modem FW Version old[" + meter.getSwVersion() + "] to new[" +fwVersion+ "]");
			
			eventClassName = "OTA";
			message = "Meter["+ meter.getMdsId() +"] Upgrade Finished. F/W Version=[" + fwVersion + "]";
			
			updateOTAHistory(meter.getMdsId(), DeviceType.Meter, openTime, message);
			log.info("[METER_OTA_BY_MODEM] updateOTAHistory finished. eventClassName = " +eventClassName+", message=" + message);			
		} else if (alarmId == AlarmEvent.AlarmId.BatteryHealthLowBattery) {
			eventClassName = "Power Alarm";
			message = "Low Battery";
		} else if (alarmId == AlarmEvent.AlarmId.BatteryHealthLowBatteryRestore) {
			eventClassName = "Power Alarm";
			message = "Low Battery Restore";
			_event.setStatus(EventStatus.Cleared);
		} else if (alarmId == AlarmEvent.AlarmId.OTA_Download) { // 이거 안올라옴. default : 모뎀 비활성화된 상태
			eventClassName = "OTA";
			message = "OTA_DOWN - Download";
		} else if(alarmId == AlarmEvent.AlarmId.OTA_Start){ // 이거 안올라옴. default : 모뎀 비활성화된 상태
			eventClassName = "OTA";
			message = "OTA_DOWN - Start";
		} else if(alarmId == AlarmEvent.AlarmId.OTA_End){ // 이거 안올라옴. default : 모뎀 비활성화된 상태
			eventClassName = "OTA";
			message = "OTA_DOWN - End";
		} else if (alarmId == AlarmEvent.AlarmId.OTA_Result) { // 올라옴.
			eventClassName = "OTA";
			message = "OTA_DOWN - Result";
			String otaStepMsg = "";

			if (bx[3] == 0x00)
				otaStepMsg += "[No Error]";
			else if (bx[3] == 0x01)
				otaStepMsg += "[CRC Fail]";
			else if (bx[3] == (byte)0xFF)
				otaStepMsg += "[Unknown Error]";

			message += "-" + otaStepMsg;
		} else if (alarmId == AlarmEvent.AlarmId.OTA_FOTA_Result) {
			eventClassName = "OTA";
			if(bx[3] == 0x00) {
				message = "FOTA Download Success";
			} else if(bx[3] == 0x01) {
				message = "FOTA Download Failed";
			}
		} else if (alarmId == AlarmEvent.AlarmId.OTA_ModuleUpgradeResult) {
			eventClassName = "OTA";
			if(bx[3] == 0x00) {
				message = "FOTA Upgrade Success";
			} else if(bx[3] == 0x01) {
				message = "FOTA Upgrade Failed";
			} else if(bx[3] == 0x02) {
				message = "FOTA Image Verification Failed";
			} else if(bx[3] == 0x03) {
				message = "FOTA Aleady Upgraded Image";
			}
		} else if (alarmId == AlarmEvent.AlarmId.CommunicationFailure) {
			eventClassName = "Communication Alarm";
			message = "Communication Failure";			
			
			try{
				modem.setCommState(ModemStatus.ConnectError.getStatus());
				modemDao.update(modem);
			}catch(Exception e){
				log.warn(e,e);
			}

		} else if (alarmId == AlarmEvent.AlarmId.CommunicationRestore) {
			eventClassName = "Communication Alarm";
			message = "Communication Restore";
			_event.setStatus(EventStatus.Cleared);
			try{
				modem.setCommState(ModemStatus.Normal.getStatus());
				modem.setLastLinkTime(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
				modemDao.update(modem);
			}catch(Exception e){
				log.warn(e,e);
			}
		} else if (alarmId == AlarmEvent.AlarmId.SecurityAlarm_MeteringFail_HLS) {
			eventClassName = "Security Alarm";
			message = "Metering Fail for HLS";
		} else if (alarmId == AlarmEvent.AlarmId.SecurityAlarm_CommunicationFail_TLS_DTLS) {
			eventClassName = "Security Alarm";
			message = "Communication Fail for TLS/DTLS";
		} else if (alarmId == AlarmEvent.AlarmId.MeterError) {
			eventClassName = "Meter Alarm";
			message = "Meter Error";
			try{
				modem.setCommState(ModemStatus.MeterError.getStatus());
				modem.setLastLinkTime(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
				modemDao.update(modem);
			}catch(Exception e){
				log.warn(e,e);
			}
		} else if (alarmId == AlarmEvent.AlarmId.Equipment_Restart) {
			eventClassName = "Equipment Startup";
			message = "Restart";
		} else if (alarmId == AlarmEvent.AlarmId.Equipment_SelfTest) {
			eventClassName = "Equipment Notificaiton";
			message = "Self Test";
		} else if (alarmId == AlarmEvent.AlarmId.Equipment_Shutdown) {
			eventClassName = "Equipment Notificaiton";
			message = "Shutdown";
		} else if (alarmId == AlarmEvent.AlarmId.DuplicatedEquipment) {
			eventClassName = "Equipment Notificaiton";
			message = "Duplicated";
		} else if (alarmId == AlarmEvent.AlarmId.Malfunction_DiskError) {
			eventClassName = "Malfunction Warning";
			message = "Disk Error";
		} else if (alarmId == AlarmEvent.AlarmId.Malfunction_DiskRestore) {
			eventClassName = "Malfunction Warning";
			message = "Disk Restore";
			_event.setStatus(EventStatus.Cleared);
		} else if (alarmId == AlarmEvent.AlarmId.Malfunction_MemoryError) {
			eventClassName = "Malfunction Warning";
			message = "Memory Error";
		} else if (alarmId == AlarmEvent.AlarmId.Malfunction_MemoryRestore) {
			eventClassName = "Malfunction Warning";
			message = "Memory Restore";
			_event.setStatus(EventStatus.Cleared);
		} else if (alarmId == AlarmEvent.AlarmId.MeteringValueIncorrect) {
			eventClassName = "Meter Value Alarm";
			message = "Metering Value Incorrect";
		} else if (alarmId == AlarmEvent.AlarmId.MeterValueAlarm) {
			eventClassName = "Meter Value Alarm";
			message = "Metering Value Incorrect";
		} else {
			log.debug("Other defined ID=" + eventIdN + " in EventFrame[" + modem.getDeviceSerial() + "]");
			return;
		}

		// Payload
		// String payload = moAlarmData.substring(pos, pos+8);
		// pos += 8;

		EventUtil.sendEvent(eventClassName, TargetClass.valueOf(activatorType), activatorId, issueDate, new String[][] { { "message", message } }, _event);

		log.info("eventClassName=[" + eventClassName + "], message=[" + message + "] in EventFrame[" + modem.getDeviceSerial() + "]");
		log.info("IssueDate=[" + issueDate + "], id=[" + eventIdN + "]");
		
		if ( meter != null && alarmId != null ) { //SP-1069
			saveMeterAttrToClearAlarmObject(meter, alarmId); //SP-987
		}
	}


	public void makeModemEvent(String modemId, String issueDate, Integer eventIdN, String payload) {
		try {
			Modem modem = modemDao.get(modemId);
			Meter meter = null;
			for (Meter m : modem.getMeter()) {
				if (m.getModemPort() == null || m.getModemPort() == 0) {
					meter = m;
					break;
				}
			}
			makeModemEvent(modem, meter, issueDate, eventIdN, payload);
		} catch (Exception e) {
			log.error(e, e);
		}
	}

	/**
	 * History information Update.
	 * 
	 * @param deviceId
	 * @param deviceType
	 * @param openTime
	 */
	public void updateOTAHistory(String deviceId, DeviceType deviceType, String openTime, String resultStatus) {
		log.info("updateOTAHistory save start.");
		log.info("Update OTA History param save. DeviceId=" + deviceId + ", DeviceType=" + deviceType.name() + ", OpentTime=" + openTime + ", ResultStatus=" + resultStatus);
		
		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		FirmwareIssueHistoryDao firmwareIssueHistoryDao = null;

		/*
		 * 개별 Device OTA 이력 UPDATE. 
		 *  - DCU의 경우 Trap이벤트에 issuedate, firmwareid 정보가 없기때문에 가장 최근에 실행한 Device 의 이력을 업데이트하는 방식으로 진행함. 
		 */
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
			txStatus = txManager.getTransaction(null);
			firmwareIssueHistoryDao = DataUtil.getBean(FirmwareIssueHistoryDao.class);
			
			firmwareIssueHistoryDao.updateOTAHistory(EVENT_MESSAGE, deviceId, deviceType, openTime, (resultStatus != null) ? resultStatus : "");
			txManager.commit(txStatus);
			log.info("updateOTAHistory commit finished.");
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
			
			firmwareIssueHistoryDao.updateOTAHistoryIssue(EVENT_MESSAGE, deviceId, deviceType);
			txManager.commit(txStatus);
			log.info("updateOTAHistoryIssue commit finished.");
		} catch (Exception e) {
			log.error("ERROR on FirmwareIssue update Transaction - " + e.getMessage(), e);
			if (txStatus != null) {
				txManager.rollback(txStatus);
			}
		}

	}
	

	/**
	 * SP-987
	 * Save value to METER_ATTR.ALARM_VALUE,ALARM_DATE
	 * @param meterAttr
	 * @param value
	 */
	void setValueToMeterAttr(Meter meter, int  value) {
		try {    	
			MeterAttr meterAttr = meterAttrDao.getByMeterId(meter.getId());
			String date = DateTimeUtil.getCurrentDateTimeByFormat(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
			if ( meterAttr == null ) {
				meterAttr = new MeterAttr();
				meterAttr.setMeterId(meter.getId());
				meterAttr.setAlarmValue(Integer.toHexString(value));
				meterAttr.setAlarmDate(date);
				meterAttrDao.add_requires_new(meterAttr);
			}
			else {
				meterAttr.setAlarmValue(Integer.toHexString(value));	
				meterAttr.setAlarmDate(date);
				meterAttrDao.update_requires_new(meterAttr);
			}
		} catch (Exception e) {
			log.error(e, e);
		}      
	}
	/**
	 * SP-987
	 * @param meter
	 * @param alarmId
	 */
	public void saveMeterAttrToClearAlarmObject( Meter meter , AlarmEvent.AlarmId alarmId)
	{
		String alarmName = alarmId.name();
		String eventNameList = FMPProperty.getProperty("ev_sp_240.meteralarmobject.clearevnets", "");
		log.info("mds_id=" + meter.getMdsId() + ",alarmName=" + alarmName + ",eventNameList=" + eventNameList );
		String[] eventNames = eventNameList.split(",");
		Integer alarm_value = null;
		for ( String name : eventNames ) {
			if ( name.equals(alarmName)) {
				alarm_value = alarmId.getCode();
				break;
			}
		}
		if ( alarm_value != null) {
			setValueToMeterAttr(meter, alarm_value);
		}

	}
}
