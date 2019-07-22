/**
 * (@)# SORIAGetMeterKeyTask.java
 *
 * 2017. 3. 27.
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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import javax.annotation.Resource;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.OTAType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.constants.CommonConstants.TR_STATE;
import com.aimir.dao.device.AsyncCommandLogDao;
import com.aimir.dao.device.AsyncCommandResultDao;
import com.aimir.dao.device.MMIUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.protocol.smsp.SMSConstants;
import com.aimir.fep.protocol.smsp.SMSConstants.COMMAND_TYPE;
import com.aimir.fep.protocol.smsp.command.frame.sms.ResponseFrame;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.AsyncCommandResult;
import com.aimir.model.device.MMIU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.schedule.command.CmdOperationUtil;
import com.aimir.util.CalendarUtil;

import net.sf.json.JSONObject;

/**
 * @author simhanger
 *
 */
@Service
public class SORIAGetMeterKeyTask extends ScheduleTask {
	private static Logger logger = LoggerFactory.getLogger(SORIAGetMeterKeyTask.class);

	@Resource(name = "transactionManager")
	HibernateTransactionManager txmanager;

	@Autowired
	MeterDao meterDao;

	@Autowired
	ModemDao modemDao;

	@Autowired
	MMIUDao mmiuDao;

	@Autowired
	CmdOperationUtil cmdOperationUtil;

	@Autowired
	AsyncCommandLogDao asyncCommandLogDao;

	@Autowired
	AsyncCommandResultDao resultDao;

	private static int MBB_COMMAND_WAIT_TIME = 60000;
	private static String fileName = "SORIAGetMeterKey_list.txt";
	private boolean isNowRunning = false;
	
	private final String commandType = "cmdSORIAGetMeterKey";

	@Override
	public void execute(JobExecutionContext context) {
		if (isNowRunning) {
			logger.info("########### CommandBatch Task[{}] is already running...", commandType);
			return;
		}
		
		isNowRunning = true;
		Date startDate = new Date();
		long startTime = startDate.getTime();

		logger.info("########### START Command[{}] - {} ###############", new Object[] { commandType, CalendarUtil.getDatetimeString(startDate, "yyyy-MM-dd HH:mm:ss") });

		List<String> targetList = setTargetList();
		
		if(targetList != null && 0 < targetList.size()){
			for(String meterSerial : targetList){
				logger.info("");  // \n
				commandStart(meterSerial);
			}			
		}else{
			logger.warn("Cannot find target list.");
		}

		logger.info("");  // \n
		long endTime = System.currentTimeMillis();
		logger.info("FINISHED Command[{}] - Elapse Time : {}s", commandType, (endTime - startTime) / 1000.0f);

		logger.info("########### END CommandBatch Task[{}] ############", commandType);
		isNowRunning = false;

		System.exit(0);
	}
	
	private List<String> setTargetList() {
		List<String> targetList = null;
		InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream(fileName);
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
			logger.warn("[{}] file not found", fileName);
		}

		return targetList;
	}
	
	
	private void commandStart(String meterSerial) {
		Modem modem = null;
		OTAType targetType;
		
		/*
		 * 1. Init
		 */
		TransactionStatus txstatus = null;
		txstatus = txmanager.getTransaction(null);

		try {
			Meter meter = meterDao.findByCondition("mdsId", meterSerial);
			if (meter == null) {
				throw new Exception("Meter[" + meterSerial + "] is not exists.");
			}

			modem = meter.getModem();
			if (modem == null) {
				throw new Exception("Meter[" + meterSerial + "] have no modem.");
			}

			if (modem.getModemType() == ModemType.MMIU && modem.getProtocolType() == Protocol.SMS) { // MBB Modem
				targetType = OTAType.METER_MBB;
			} else if (modem.getModemType() == ModemType.MMIU && (modem.getProtocolType() == Protocol.IP || modem.getProtocolType() == Protocol.GPRS)) { // Ethernet Modem
				targetType = OTAType.METER_ETHERNET;
			} else if (modem.getModemType() == ModemType.SubGiga && modem.getProtocolType() == Protocol.IP) { // RF Modem
				targetType = OTAType.METER_RF;
			} else {
				throw new Exception("Unknown Modem type or Protocol type.");
			}

			logger.info("### Command = [{}], Meter = [{}], Modem = [{}], Target Type = [{}] ###", commandType, meterSerial, modem.getDeviceSerial(), targetType);
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
		
		/*
		 * 2. Command execute.
		 */
		try {
			Map<String, Object> result = new HashMap<>();

			// ETHERNET, RF인경우
			if (targetType == OTAType.METER_ETHERNET || targetType == OTAType.METER_RF) {
				try {
					result = cmdOperationUtil.cmdSORIAGetMeterKey(meterSerial, modem.getProtocolType());
				} catch (Exception e) {
					logger.error("Command Excute Exception - Target type = [" + targetType + "] Meter = [" + meterSerial + "] Modem = [" + modem.getDeviceSerial() + "]", e);
				}
			}
			// MBB 인 경우
			else if (targetType == OTAType.METER_MBB) {
				try {
					Map<String, String> asyncModemMBBParamMap = new HashMap<String, String>();
					asyncModemMBBParamMap.put("meterId", meterSerial);

					Map<String, String> mbbMeterResult = sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), commandType, asyncModemMBBParamMap);
					if (mbbMeterResult != null && 0 < mbbMeterResult.size()) {
						result.putAll(mbbMeterResult);
					}
				} catch (Exception e) {
					logger.error("Command Excute Exception - Target type = [" + targetType + "] Meter = [" + meterSerial + "] Modem = [" + modem.getDeviceSerial() + "]", e);
				}

			} else {
				logger.error("Unknown target type.");
			}

			logger.debug("[" + targetType + "] cmdSORIAGetMeterKey Result = [{}]", result == null ? "null" : result.toString());

			/*
			 * result
			 */
			ResultStatus status = ResultStatus.SUCCESS;
			String rtnMessage = "";
			if (result != null && 0 < result.size()) {
				if (!Boolean.valueOf((boolean) result.get("result"))) {
					status = ResultStatus.FAIL;
					rtnMessage = String.valueOf(result.get("resultValue"));
				}else{
					rtnMessage = String.valueOf(result.get("resultValue"));	
				}
				
				logger.debug("cmdSORIAGetMeterKey returnValue =>> " + rtnMessage);
			} else {
				status = ResultStatus.FAIL;
				rtnMessage = "FAIL : result receive fail.";
				logger.debug("FAIL : result receive fail.");
			}			
			
			
			if(status == ResultStatus.SUCCESS){
				logger.info(rtnMessage);	
			}else{
				logger.info("------ fail -----");
			}
			
			
//			if (result != null && !result.toString().equals("")) {
//				Iterator<String> it = result.keySet().iterator();
//				while (it.hasNext()) {
//					String key = it.next();
//					JSONObject jo = JSONObject.fromObject(String.valueOf(result.get(key)));
//					String rtValue = "";
//					if (jo != null && 0 < jo.size()) {
//						if (!jo.getBoolean("RESULT")) {
//						}
//
//						rtValue = jo.getString("RESULT_VALUE");
//						logger.debug("cmdSORIAGetMeterKey key= " + key + ", returnValue =>> " + rtValue);
//					} else {
//						rtValue = "FAIL : result receive fail.";
//						logger.debug("FAIL : result receive fail.");
//					}
//
//				}
//			} else {
//				logger.error("FAIL : Command Fail - [{}][{}] - can not connect to server.", targetType, meterSerial);
//			}

		} catch (Exception e) {
			//logger.error("FAIL : Command Fail - [{}][{}] - {}", targetType, meterSerial, e);
			logger.error("FAIL : Command Fail - [" + targetType + "][" + meterSerial + "] - " + e, e);
		}
	}

	/*
	 * SMS를 보내고  Async테이블에서 결과를 가져오는 함수 입니다.
	 */
	public Map<String, String> sendSmsForCmdServer(Modem modem, String messageType, String commandCode, String commandName, Map<String, String> paramMap) throws Exception {
		logger.debug("[sendSmsAndGetResult] " + " messageType: " + messageType + " commandCode: " + commandCode + " commandName: " + commandName);

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
			String response_messageId = resultMap.get("messageId") == null ? "F" : resultMap.get("messageId").toString();

			/*
			 * 결과 처리
			 */
			if (response_messageId.equals("F") || response_messageId.equals("CF")) { // Fail
				logger.debug(response_messageId);
				return null;
			} else {
				Thread.sleep(MBB_COMMAND_WAIT_TIME);
				Integer lastStatus = asyncCommandLogDao.getCmdStatus(modem.getDeviceSerial(), commandName);

				if (TR_STATE.Success.getCode() != lastStatus) {
					logger.debug("FAIL : Communication Error but Send SMS Success.  " + euiId + "  " + commandName);
					return null;
				} else {
					ObjectMapper mapper = new ObjectMapper();
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
		logger.info("#### SORIAGetMeterKey Task start. ###");
		
		if(0 < args.length && args[0].equals("-mbbCommandWaitTime")){
			try {
				MBB_COMMAND_WAIT_TIME = Integer.parseInt(args[1]);
			} catch (Exception e) {
				logger.error("Arg Parsing Error - Default Timeout setting 60000");
			}						
		}
		
		logger.info("SORIAGetMeterKey params. MBB_COMMAND_WAIT_TIME={}", MBB_COMMAND_WAIT_TIME);

		try {
			ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "spring-SORIAGetMeterKey.xml" });
			DataUtil.setApplicationContext(ctx);

			SORIAGetMeterKeyTask task = (SORIAGetMeterKeyTask) ctx.getBean(SORIAGetMeterKeyTask.class);
			task.execute(null);

		} catch (Exception e) {
			logger.error("SORIAGetMeterKey excute error - " + e, e);
		} finally {
			logger.info("#### SORIAGetMeterKey Task finished. ###");
			System.exit(0);
		}

	}
}
