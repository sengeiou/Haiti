/**
 * (@)# GroupOTAForSORIATask.java
 *
 * 2016. 9. 29.
 *
 * Copyright (c) 2013 NURITELECOM, Inc.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of 
 * NURITELECOM, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with NURITELECOM, Inc.
 *
 * For more information on this product, please see
 * http://www.nuritelecom.co.kr
 *
 */
package com.aimir.schedule.task;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;

import javax.annotation.Resource;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.OTATargetType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.constants.CommonConstants.TR_STATE;
import com.aimir.dao.device.AsyncCommandLogDao;
import com.aimir.dao.device.AsyncCommandResultDao;
import com.aimir.dao.device.FirmwareDao;
import com.aimir.dao.device.FirmwareIssueDao;
import com.aimir.dao.device.FirmwareIssueHistoryDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MMIUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.device.OperationLogDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.OperatorDao;
import com.aimir.fep.protocol.smsp.SMSConstants;
import com.aimir.fep.protocol.smsp.SMSConstants.COMMAND_TYPE;
import com.aimir.fep.protocol.smsp.command.frame.sms.ResponseFrame;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.AsyncCommandResult;
import com.aimir.model.device.Device.DeviceType;
import com.aimir.model.device.Firmware;
import com.aimir.model.device.FirmwareIssue;
import com.aimir.model.device.FirmwareIssueHistory;
import com.aimir.model.device.MCU;
import com.aimir.model.device.MMIU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.device.OperationLog;
import com.aimir.model.system.Code;
import com.aimir.model.system.Location;
import com.aimir.model.system.Operator;
import com.aimir.model.system.Role;
import com.aimir.model.system.Supplier;
import com.aimir.schedule.command.CmdOperationUtil;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.DateTimeUtil;

import net.sf.json.JSONObject;

/**
 * @author simhanger
 *
 */
@Service
public class GroupOTAForSORIATask extends ScheduleTask {
	private static Logger logger = LoggerFactory.getLogger(GroupOTAForSORIATask.class);

	@Resource(name = "transactionManager")
	HibernateTransactionManager txmanager;

	@Autowired
	FirmwareDao firmwareDao;

	@Autowired
	LocationDao locationDao;

	@Autowired
	FirmwareIssueHistoryDao firmwareIssueHistoryDao;

	@Autowired
	FirmwareIssueDao firmwareIssueDao;

	@Autowired
	OperatorDao operatorDao;

	@Autowired
	MCUDao mcuDao;

	@Autowired
	MeterDao meterDao;

	@Autowired
	ModemDao modemDao;

	@Autowired
	CodeDao codeDao;

	@Autowired
	MMIUDao mmiuDao;

	@Autowired
	OperationLogDao operationLogDao;

//	/**
//	 * SMS 보내는 모듈에서 사용하는것들. 추후에 정리할것.
//	 */
	@Autowired
	CmdOperationUtil cmdOperationUtil;

	@Autowired
	AsyncCommandLogDao asyncCommandLogDao;

	@Autowired
	AsyncCommandResultDao resultDao;

	/*****************************************************/
	private final String LOGIN_ID = "admin";
	private final boolean TAKE_OVER = false; // 이어받기 안하는걸로 고정시킴. 필요시 입력받아 처리하는 로직으로 수정할것.
	private Supplier supplier;

	private String otaType = "GroupOTAForSORIA";
	private static String locationName;
	private Location locaion;
	private static boolean directly;
	private static String deviceModelName;
	private static String firmwareFileName;
	private static String firmwareVersion;
	private static String targetListPath;
	private OTATargetType targetType;
	private Firmware firmware;

	private List<String> targetList = new LinkedList<String>();
	private List<Object> mbbTypeTargetList = new ArrayList<Object>();

	@Override
	public void execute(JobExecutionContext context) {
		TransactionStatus txstatus = null;
		txstatus = txmanager.getTransaction(null);

		try {
			/*
			 * Set Location for use save history 
			 */
			List<Location> locations = locationDao.getLocationByName(locationName);
			if (locations == null | locations.size() <= 0) {
				logger.error("There is no Location. Please check agin.");
				return;
			}

			/*
			 * Prepare Target list
			 */
			targetList = getTargetList();
			if (targetList == null || (targetList.size() <= 0)) {
				logger.error("There is no Device or TargetType. Please check agin.");
				return;
			}

			/*
			 * Prepare Firmware
			 */
			Set<Condition> firmwareConditions = new HashSet<Condition>();
			firmwareConditions.add(new Condition("fwVersion", new Object[] { firmwareVersion }, null, Restriction.EQ));
			firmwareConditions.add(new Condition("fileName", new Object[] { firmwareFileName }, null, Restriction.EQ));
			firmwareConditions.add(new Condition("equipModel", new Object[] { deviceModelName }, null, Restriction.EQ));
			List<Firmware> firmwareList = firmwareDao.findByConditions(firmwareConditions);
			if (firmwareList != null && 0 < firmwareList.size()) {
				firmware = firmwareList.get(0);
				targetType = OTATargetType.getItem(firmware.getEquipType().toUpperCase().replaceAll("-", "_"));
				logger.debug("### Firmware info => {}", firmware.toString());
			} else {
				logger.error("Unknown Firmware. please check firmware file name at Firmware Tap in Firmware Management gadget.");
				return;
			}

			/*
			 * OTA Command Start
			 */
			commandOTAStart();
		} catch (Exception e) {
			if (txstatus != null) {
				txmanager.rollback(txstatus);
			}
			logger.error("Task Excute transaction error - " + e, e);
			return;
		}
		if (txstatus != null) {
			txmanager.commit(txstatus);
		}
	}

	/*
	 * Read targetlist from file path.
	 */
	private List<String> getTargetList() {
		List<String> targetList = null;
		InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream(targetListPath);
		if (fileInputStream != null) {
			targetList = new LinkedList<String>();

			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(fileInputStream);
			while (scanner.hasNextLine()) {
				String target = scanner.nextLine().trim();
				if (!target.equals("")) {
					targetList.add(target);
				}
			}
			logger.info("Target List({}) ===> {}", targetList.size(), targetList.toString());
		} else {
			logger.info("[{}] file not found", targetListPath);
		}

		return targetList;
	}

	/*
	 * 
	 * 
	 * OTA 실행하는 로직 추후에 상위단 프로젝트로 올려서 사용하도록 만들것.
	 * 
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void commandOTAStart() {
		logger.debug("OTA Command Params : loginId=[{}], TargetType=[{}], UsingDirectly=[{}], deviceList=[{}]", LOGIN_ID, String.join(", ", targetList), targetType.name(), directly);

		Code targetTypeCode = null;
		Code operationCode = null;
		ResultStatus status = ResultStatus.SUCCESS;
		String rtnMessage = "";
		DeviceType deviceType = null;

		/*
		 *  유형확인
		 */
		MCU mcu = null;
		Meter meter = null;
		Modem modem = null;
		Protocol protocol = null;

		switch (targetType) {
		case DCU:
			if (!commandAuthCheck(LOGIN_ID, "8.3.7")) {
				logger.error("No permission");
				return;
			}
			mcu = mcuDao.get(targetList.get(0));
			supplier = mcu.getSupplier();
			protocol = Protocol.valueOf(mcu.getProtocolType().getName());

			operationCode = codeDao.getCodeIdByCodeObject("8.3.7"); // MCU OTA
			targetTypeCode = codeDao.getCodeIdByCodeObject("1.1"); // DCU
			deviceType = DeviceType.MCU;
			break;
		case DCU_KERNEL:
			if (!commandAuthCheck(LOGIN_ID, "8.3.8")) {
				logger.error("No permission");
				return;
			}
			mcu = mcuDao.get(targetList.get(0));
			supplier = mcu.getSupplier();
			protocol = Protocol.valueOf(mcu.getProtocolType().getName());

			operationCode = codeDao.getCodeIdByCodeObject("8.3.8"); // MCU-KERNEL OTA
			targetTypeCode = codeDao.getCodeIdByCodeObject("1.1"); // DCU
			deviceType = DeviceType.MCU;
			break;
		case DCU_COORDINATE:
			if (!commandAuthCheck(LOGIN_ID, "8.3.9")) {
				logger.error("No permission");
				return;
			}
			mcu = mcuDao.get(targetList.get(0));
			supplier = mcu.getSupplier();
			protocol = Protocol.valueOf(mcu.getProtocolType().getName());
			operationCode = codeDao.getCodeIdByCodeObject("8.3.9"); // DCU_COORDINATE OTA
			targetTypeCode = codeDao.getCodeIdByCodeObject("1.1"); // DCU

			deviceType = DeviceType.MCU;
			break;
		case METER:
			if (!commandAuthCheck(LOGIN_ID, "8.1.13")) {
				logger.error("No permission");
				return;
			}

			meter = meterDao.findByCondition("mdsId", targetList.get(0));
			modem = meter.getModem();
			supplier = modem.getSupplier();
			protocol = modem.getProtocolType();

			// MBB Target 추출
			StringBuilder tempStr = new StringBuilder();
			for (String mdsId : targetList) {
				meter = meterDao.findByCondition("mdsId", mdsId);
				modem = meter.getModem();

				if (modem.getModemType() == ModemType.MMIU && modem.getProtocolType() == Protocol.SMS) { // MBB Modem
					mbbTypeTargetList.add(meter);
					tempStr.append(meter.getMdsId());
				}
			}
			// MBB Target 제거
			for (Object mbbTarget : mbbTypeTargetList) {
				if (targetList.contains(((Meter) mbbTarget).getMdsId())) {
					targetList.remove(((Meter) mbbTarget).getMdsId());
				}
			}

			logger.debug("Meter-MBB Target List = {}", tempStr.toString());
			logger.debug("Meter Target List = {}", targetList);

			operationCode = codeDao.getCodeIdByCodeObject("8.1.13"); // Meter OTA
			//targetTypeCode = codeManager.getCodeByCode("1.3.1.1"); // EnergyMeter
			targetTypeCode = codeDao.getCodeIdByCodeObject("1.3"); // Meter

			deviceType = DeviceType.Meter;
			break;
		case MODEM:
			if (!commandAuthCheck(LOGIN_ID, "8.2.6")) {
				logger.error("No permission");
				return;
			}

			operationCode = codeDao.getCodeIdByCodeObject("8.2.6"); // Modem OTA			
			targetTypeCode = codeDao.getCodeIdByCodeObject("1.2"); // Modem

			modem = modemDao.findByCondition("deviceSerial", targetList.get(0));
			supplier = modem.getSupplier();
			protocol = modem.getProtocolType();

			// MBB Target 추출
			StringBuilder tempStr1 = new StringBuilder();
			for (String deviceSerial : targetList) {
				modem = modemDao.findByCondition("deviceSerial", deviceSerial);

				if (modem.getModemType() == ModemType.MMIU && modem.getProtocolType() == Protocol.SMS) { // MBB Modem
					mbbTypeTargetList.add(modem);
					tempStr1.append(modem.getDeviceSerial());
				}
			}
			// MBB Target 제거
			for (Object mbbTarget : mbbTypeTargetList) {
				if (targetList.contains(((Modem) mbbTarget).getDeviceSerial())) {
					targetList.remove(((Modem) mbbTarget).getDeviceSerial());
				}
			}

			logger.debug("Modem-MBB Target List = {}", tempStr1.toString());
			logger.debug("Modem Target List = {}", targetList);

			//			if (modem.getModemType() == ModemType.MMIU && modem.getProtocolType() == Protocol.SMS) { // MBB Modem
			//				targetTypeCode = codeManager.getCodeByCode("1.2.1.11"); // MMIU
			//			} else if (modem.getModemType() == ModemType.MMIU && modem.getProtocolType() == Protocol.IP) { // Ethernet Modem
			//				otaType = OTAType.MODEM_ETHERNET;
			//			} else if (modem.getModemType() == ModemType.SubGiga && modem.getProtocolType() == Protocol.IP) { // RF Modem
			//				targetTypeCode = codeManager.getCodeByCode("1.2.1.101"); // SubGiga
			//
			//			}

			deviceType = DeviceType.Modem;

			break;
		default:
			break;
		}

		String issueDate = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
		/*
		 * Group OTA History 남기기
		 */
		FirmwareIssue firmwareIssue = new FirmwareIssue();
		firmwareIssue.setLocationId(locaion.getId());
		firmwareIssue.setFirmwareId(Long.parseLong(firmware.getId().toString()));
		firmwareIssue.setIssueDate(issueDate);
		firmwareIssue.setName(otaType);
		firmwareIssue.setTotalCount(targetList.size() + mbbTypeTargetList.size());

		firmwareIssueDao.saveOrUpdate(firmwareIssue);
		firmwareIssueDao.flushAndClear();

		/*
		 * OTA 타입에 따라 분기
		 */
		try {
			Map<String, Object> result = new HashMap<>();

			// DCU, DCU-KERNEL, ETHERNET, RF인경우
			// [RESULT] = {000B120000000501={"RESULT":true,"RESULT_VALUE":"Proceeding..."}}
			try {
				if (targetList != null && 0 < targetList.size()) {

					/*
					 * Single OTA History 남기기 
					 */
					for (String dId : targetList) {
						FirmwareIssueHistory firmwareIssueHistory = new FirmwareIssueHistory();
						firmwareIssueHistory.setDeviceId(dId);
						firmwareIssueHistory.setDeviceType(deviceType);
						firmwareIssueHistory.setLocationId(locaion.getId());
						firmwareIssue.setFirmwareId(Long.parseLong(firmware.getId().toString()));
						firmwareIssue.setIssueDate(issueDate);
						firmwareIssueHistory.setUpdateDate(issueDate);
						firmwareIssueHistory.setUesBypass(directly);

						firmwareIssueHistoryDao.saveOrUpdate(firmwareIssueHistory);
						firmwareIssueHistoryDao.flushAndClear();
					}

					result = cmdOperationUtil.cmdMultiFirmwareOTA(targetType, targetList, protocol, String.valueOf(TAKE_OVER), directly, firmware.getId().toString());
					logger.debug("OTA Result 1 = [{}]", result.toString());
				}
			} catch (Exception e) {
				logger.error("OTA Excute Exception - Target type = [" + targetType + "] Device = [" + targetList + "]", e);
			}

			// MBB 인 경우			
			try {
				if (mbbTypeTargetList != null && 0 < mbbTypeTargetList.size()) {
					Map<String, String> asyncModemMBBParamMap = new HashMap<String, String>();
					asyncModemMBBParamMap.put("fw_path", firmware.getFileUrlPath());
					asyncModemMBBParamMap.put("fw_crc", firmware.getCrc());
					asyncModemMBBParamMap.put("fw_version", firmware.getFwVersion());
					asyncModemMBBParamMap.put("take_over", String.valueOf(TAKE_OVER));
					asyncModemMBBParamMap.put("imageKey", firmware.getImageKey());

					/*
					 * Single OTA History Parameter 
					 */
					FirmwareIssueHistory firmwareIssueHistory = new FirmwareIssueHistory();
					firmwareIssueHistory.setDeviceType(deviceType);
					firmwareIssueHistory.setLocationId(locaion.getId());
					firmwareIssue.setFirmwareId(Long.parseLong(firmware.getId().toString()));
					firmwareIssue.setIssueDate(issueDate);
					firmwareIssueHistory.setUpdateDate(issueDate);
					firmwareIssueHistory.setUesBypass(directly);

					for (Object mbb : mbbTypeTargetList) {

						if (mbb instanceof Meter) {
							Meter tempMeter = (Meter) mbb;
							asyncModemMBBParamMap.put("meterId", tempMeter.getMdsId());

							/*
							 * Single OTA History남기기
							 */
							firmwareIssueHistory.setDeviceId(tempMeter.getMdsId());
							firmwareIssueHistoryDao.saveOrUpdate(firmwareIssueHistory);
							firmwareIssueHistoryDao.flushAndClear();

							Map<String, Object> mbbMeterResult = sendSmsForCmdServer(tempMeter.getModem(), SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), "cmdMeterOTAStart", asyncModemMBBParamMap);
							if (mbbMeterResult != null && 0 < mbbMeterResult.size()) {
								result.putAll(mbbMeterResult);
							}

						} else if (mbb instanceof Modem) {
							asyncModemMBBParamMap.put("modemId", ((Modem) mbb).getDeviceSerial());

							/*
							 * Single OTA History남기기
							 */
							firmwareIssueHistory.setDeviceId(((Modem) mbb).getDeviceSerial());
							firmwareIssueHistoryDao.saveOrUpdate(firmwareIssueHistory);
							firmwareIssueHistoryDao.flushAndClear();

							Map<String, Object> mbbModemResult = sendSmsForCmdServer((Modem) mbb, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), "cmdModemOTAStart", asyncModemMBBParamMap);

							if (mbbModemResult != null && 0 < mbbModemResult.size()) {
								result.putAll(mbbModemResult);
							}
						}

						logger.debug("OTA Result 2 = [{}]", result.toString());
					}
				}
			} catch (Exception e) {
				logger.error("OTA Excute Exception - Target type = [" + targetType + "] Device = [" + mbbTypeTargetList.toString() + "]", e);
			}

			if (result != null) {
				rtnMessage = "OTA_RESULT = ";
				for (String key : result.keySet()) {
					rtnMessage += result.get(key).toString() + ", ";
				}

				logger.debug("OTA returnValue =>> " + rtnMessage);
			} else {
				status = ResultStatus.FAIL;
				rtnMessage = "FAIL : result receive fail.";
				logger.debug("FAIL : result receive fail.");
			}

			/*
			 * Operation 저장 
			 */
			rtnMessage = "[RESULT] = " + result.toString();
			logger.debug("[{}] RtnMessage => {}", otaType, rtnMessage);

			if (result != null && !result.toString().equals("")) {
				Iterator<String> it = result.keySet().iterator();
				while (it.hasNext()) {
					String key = it.next();
					JSONObject jo = JSONObject.fromObject(String.valueOf(result.get(key)));
					String rtValue = "";
					if (jo != null && 0 < jo.size()) {
						if (!jo.getBoolean("RESULT")) {
							status = ResultStatus.FAIL;
						}

						rtValue = jo.getString("RESULT_VALUE");
						logger.debug("cmdMeterOTAStart key= " + key + ", returnValue =>> " + rtValue);
					} else {
						status = ResultStatus.FAIL;
						rtValue = "FAIL : result receive fail.";
						logger.debug("FAIL : result receive fail.");
					}

					if (operationCode != null && operationCode.getCode() != null) {
						String opMessage = "";
						if (250 <= rtValue.length()) {
							opMessage = rtValue.substring(0, 250) + "....";
						} else {
							opMessage = rtValue;
						}

						try {
							OperationLog log = new OperationLog();
							log.setOperatorType(1);//operator
							log.setOperationCommandCode(operationCode);
							log.setYyyymmdd(issueDate.substring(0, 8));
							log.setHhmmss(issueDate.substring(8, 14));
							log.setYyyymmddhhmmss(issueDate);
							log.setDescription("");
							log.setErrorReason(opMessage);
							log.setResultSrc("");
							log.setStatus(status.getCode());
							log.setTargetName(key);
							log.setTargetTypeCode(targetTypeCode);
							log.setUserId(LOGIN_ID);
							log.setSupplier(supplier);

							logger.debug("GroupOTAForSORIATask OperationLog : " + log.toString());
							operationLogDao.add(log);

						} catch (Exception e) {
							logger.error("## GroupOTAForSORIATask OperationLog save error - " + e.getMessage(), e);
						}

					} else {
						logger.error("## Operation Code is not define. please check AIMIR Code.");
					}
				}
			} else {
				logger.error("FAIL : OTA Fail - [{}][{}] - can not connect to server.", otaType, targetList);
				rtnMessage = "FAIL : OTA Fail - [" + otaType + "][" + targetList + "] - can not connect to server.";
			}

		} catch (Exception e) {
			logger.error("FAIL : OTA Fail - [{}][{}] - {}", otaType, targetList, e);
			rtnMessage = "FAIL : OTA Fail - [" + otaType + "][" + targetList + "] - " + e.getMessage();
		}

		/**
		 * 4. 결과 리턴
		 */
		//mav.addObject("rtnStr", rtnMessage);
		//return mav;

		logger.info("Result Message => {}", rtnMessage);
	}

	protected boolean commandAuthCheck(String loginId, String command) {

		Operator operator = operatorDao.getOperatorByLoginId(loginId);

		Role role = operator.getRole();
		Set<Code> commands = role.getCommands();
		Code codeCommand = null;
		if (role.getCustomerRole() != null && role.getCustomerRole()) {
			return false; //고객 권한이면 
		}

		for (Iterator<Code> i = commands.iterator(); i.hasNext();) {
			codeCommand = (Code) i.next();
			if (codeCommand.getCode().equals(command))
				return true; //관리자가 아니라도 명령에 대한 권한이 있으면
		}
		return false;
	}

	/*
	 * 
	 * 
	 * SMS 보내는 로직 추후에 상위단 프로젝트로 올려서 사용하도록 만들것.
	 * 
	 * 
	 */

	/*
	 * SMS를 보내고  Async테이블에서 결과를 가져오는 함수 입니다.
	 */
	@SuppressWarnings("rawtypes")
	public Map sendSmsForCmdServer(Modem modem, String messageType, String commandCode, String commandName, Map<String, String> paramMap) throws Exception {
		logger.debug("[sendSmsAndGetResult] " + " messageType: " + messageType + " commandCode: " + commandCode + " commandName: " + commandName);

		/*
		 * 서버에서 모뎀으로 SMS를 보낸뒤 모뎀이 서버에 접속하여 수행해야할 Command가 무엇인지
		 * 구분할 방법이 따로 없기 때문에 Transaction ID를 사용하여 구분하도록 한다.
		 */
		/*Long maxTrId = asyncCommandLogManager.getMaxTrId(modem.getDeviceSerial());
		String trnxId;
		if (maxTrId != null) {
			trnxId = String.format("%08d", maxTrId.intValue() + 1);
		} else {
			trnxId = "00000001";
		}*/

		/*
		 * 비동기 명령 저장 : SMS발송보다 먼저 저장함.
		 */
		//saveAsyncCommandForSORIA(modem.getDeviceSerial(), Long.parseLong(trnxId), commandName, paramMap, TimeUtil.getCurrentTime());

		/*
		 * SMS 발송
		 */
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> condition = new HashMap<String, Object>();
		String mobliePhNum = null;
		String euiId = null;

		if (modem.getModemType().equals(ModemType.MMIU)) {
			//MMIU mmiuModem = (MMIU) modem;
			MMIU mmiuModem = mmiuDao.get(modem.getId());
			mobliePhNum = mmiuModem.getPhoneNumber();
			euiId = modem.getDeviceSerial();

			condition.put("messageType", messageType);
			condition.put("mobliePhNum", mobliePhNum);
			condition.put("euiId", euiId);
			condition.put("commandCode", commandCode);
			condition.put("commandName", commandName);

			List<String> paramListForSMS = new ArrayList<String>();
			Properties prop = new Properties();
			try {
				prop.load(getClass().getClassLoader().getResourceAsStream("config/command.properties"));
			} catch (Exception e) {
				logger.error("Can't not read property file. -" + e, e);

			}

			String serverIp = prop.getProperty("smpp.hes.fep.server") == null ? "" : prop.getProperty("smpp.hes.fep.server").trim();
			String serverPort = prop.getProperty("soria.modem.tls.port") == null ? "" : prop.getProperty("soria.modem.tls.port").trim();
			String authPort = prop.getProperty("smpp.auth.port") == null ? "" : prop.getProperty("smpp.auth.port").trim();
			paramListForSMS.add(serverIp);
			paramListForSMS.add(serverPort);
			paramListForSMS.add(authPort);

			// modem이 Fep에 붙었을 때 실행할 command의 param들을 json String으로 넘겨줌
			String cmdMap = null;
			ObjectMapper om = new ObjectMapper();
			if (paramMap != null)
				cmdMap = om.writeValueAsString(paramMap);

			logger.debug("Send SMS euiId: " + euiId + ", mobliePhNum: " + mobliePhNum + ", commandName: " + commandName + ", cmdMap " + cmdMap);
			resultMap = sendSms(condition, paramListForSMS, cmdMap); // Send SMS!
			//String response_messageType = resultMap.get("messageType").toString();
			String response_messageId = resultMap.get("messageId") == null ? "F" : resultMap.get("messageId").toString();
			/*
			 * 결과 처리
			 */
			if (response_messageId.equals("F") || response_messageId.equals("CF")) { // Fail
				logger.debug(response_messageId);
				return null;
			} else {
				Thread.sleep(35000);
				//Integer lastStatus = asyncCommandLogManager.getCmdStatus(modem.getDeviceSerial(), commandName);
				Integer lastStatus = asyncCommandLogDao.getCmdStatus(modem.getDeviceSerial(), commandName);

				if (TR_STATE.Success.getCode() != lastStatus) {
					logger.debug("FAIL : Communication Error but Send SMS Success.  " + euiId + "  " + commandName);
					return null;
				} else {
					ObjectMapper mapper = new ObjectMapper();
					//List<AsyncCommandResult> asyncResult = asyncCommandLogManager.getCmdResults(modem.getDeviceSerial(), Long.parseLong(response_messageId),commandName); //ASYNC_COMMAND_RESULT에서 결과 값을 가져옴
					List<AsyncCommandResult> asyncResult = resultDao.getCmdResults(modem.getDeviceSerial(), Long.parseLong(response_messageId), commandName); //ASYNC_COMMAND_RESULT에서 결과 값을 가져옴
					if (asyncResult == null || asyncResult.size() <= 0) {
						logger.debug("FAIL : Send SMS but fail to execute " + euiId + "  " + commandName);
						return null;
					} else { // Success
						String resultStr = "";
						for (int i = 0; i < asyncResult.size(); i++) {
							resultStr += asyncResult.get(i).getResultValue();
						}
						Map<String, String> map = mapper.readValue(resultStr, new TypeReference<Map<String, String>>() {
						});
						logger.debug("Success get result");
						return map; // 맴 형식으로 결과 리턴
					}
				}
			}
		} else {
			throw new Exception("Type Missmatch. this modem is not MMIU Type modem.");
		}
	}

	public Map<String, Object> sendSms(Map<String, Object> condition, List<String> paramList, String cmdMap) throws Exception {

		Map<String, Object> resultMap = new HashMap<String, Object>();
		String euiId = condition.get("euiId").toString();
		String messageId = cmdOperationUtil.sendSMS(condition, paramList, cmdMap);
		String commandCode = condition.get("commandCode").toString();

		// 결과처리 로직 (S)
		String rtnMessage;
		// MBB Modem으로 전송하는 SMS 명령이
		// 55(set up environment For NI),56(~~CoAP),57(~~SNMP)일 경우
		// Async_command_Result 조회를 하지않고, message id만 55, 56, 57 명령 처리 로직으로 넘겨준다.
		if (commandCode.equals(COMMAND_TYPE.NI.getTypeCode()) || commandCode.equals(COMMAND_TYPE.COAP.getTypeCode()) || commandCode.equals(COMMAND_TYPE.SNMP.getTypeCode())) {
			if (messageId.equals("FAIL")) {
				resultMap.put("messageId", "F");
			} else if (messageId.equals("FAIL-CONNECT")) {
				resultMap.put("messageId", "CF");
			} else {
				resultMap.put("messageId", messageId);
			}
		} else {
			if (messageId.equals("FAIL")) {
				resultMap.put("messageType", "F");
			} else if (messageId.equals("FAIL-CONNECT")) {
				resultMap.put("messageType", "CF");
			} else {
				try {
					//rtnMessage = asyncCommandLogManager.getCmdResults(euiId, Long.parseLong(messageId));
					rtnMessage = resultDao.getCmdResults(euiId, Long.parseLong(messageId));
					if (rtnMessage == null) {
						resultMap.put("messageType", "F");
						return resultMap;
					}
				} catch (Exception e) {
					logger.error("SendSMS excute error - " + e, e);
					resultMap.put("messageType", "F");
					return resultMap;
				}

				ResponseFrame responseFrame = new ResponseFrame();
				resultMap = responseFrame.decode(rtnMessage);
			}
		}
		// 결과처리 로직 (E)

		return resultMap;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		logger.info("-----");
		logger.info("-----");
		logger.info("-----");
		logger.info("#### GroupOTAForSORIATask Task start. ###");

		if (args.length < 5) {
			logger.info("Usage:");
			logger.info("GroupOTAForSORIATask -DtaskName=TaskName -location=location -Ddirectly=directly -DdeviceModelName=deviceModelName -DfirmwareFileName=firmwareFileName -DfirmwareVersion=firmwareVersion");
			return;
		}

		for (int i = 0; i < args.length; i += 2) {
			String nextArg = args[i];
			if (nextArg.startsWith("-location")) {
				locationName = new String(args[i + 1]);
			} else if (nextArg.startsWith("-directly")) {
				directly = Boolean.parseBoolean(new String(args[i + 1]));
			} else if (nextArg.startsWith("-deviceModelName")) {
				deviceModelName = new String(args[i + 1]);
			} else if (nextArg.startsWith("-firmwareFileName")) {
				firmwareFileName = new String(args[i + 1]);
			} else if (nextArg.startsWith("-firmwareVersion")) {
				firmwareVersion = new String(args[i + 1]);
			} else if (nextArg.startsWith("-targetListPath")) {
				targetListPath = new String(args[i + 1]);
			}
		}

		logger.info("GroupOTAForSORIATask params. Directly={}, DeviceModelName={}, FirmwareFileName={}, FirmwareVersion={}", directly, deviceModelName, firmwareFileName, firmwareVersion);

		try {
			ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "spring-GroupOTAForSORIA.xml" });
			DataUtil.setApplicationContext(ctx);

			GroupOTAForSORIATask task = (GroupOTAForSORIATask) ctx.getBean(GroupOTAForSORIATask.class);
			task.execute(null);

		} catch (Exception e) {
			logger.error("GroupOTAForSORIATask excute error - " + e, e);
		} finally {
			logger.info("#### GroupOTAForSORIATask Task finished. ###");

			System.exit(0);
		}

	}

}
