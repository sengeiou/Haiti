/**
 * (@)# MBBOTARetryTask.java
 *
 * 2017. 5. 24.
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

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.ModemIFType;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.dao.device.AsyncCommandLogDao;
import com.aimir.dao.device.MMIUDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.protocol.smsp.SMSConstants;
import com.aimir.fep.protocol.smsp.SMSConstants.COMMAND_TYPE;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.AsyncCommandLog;
import com.aimir.model.device.MMIU;
import com.aimir.model.device.Modem;
import com.aimir.schedule.command.CmdOperationUtil;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.DateTimeUtil;
import org.codehaus.jackson.map.ObjectMapper;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

@Service
public class MBBOTARetryTask extends ScheduleTask {
	private static Logger logger = LoggerFactory.getLogger(MBBOTARetryTask.class);

	@Resource(name = "transactionManager")
	HibernateTransactionManager txmanager;

	@Autowired
	ModemDao modemDao;

	@Autowired
	MMIUDao mmiuDao;

	/**
	 * SMS 보내는 모듈에서 사용하는것들. 추후에 정리할것.
	 */
	@Autowired
	CmdOperationUtil cmdOperationUtil;

	@Autowired
	AsyncCommandLogDao asyncCommandLogDao;
	
	@Override
	public void execute(JobExecutionContext context) {
		TransactionStatus txstatus = null;
		txstatus = txmanager.getTransaction(null);
		List<String> asyncList = new ArrayList<String>();
		
		Properties prop = new Properties();
		int maxWorker = 10;
		int timewait = 30;
		int maxworkertimewait = 180;
		String requestDate = "";
		try {
			
			try{
				prop.load(getClass().getClassLoader().getResourceAsStream("config/schedule-MBBOTARetryTask.properties"));
			}catch(Exception e){
				logger.error("Can't not read property file. -" + e,e);
			}
			maxWorker = Integer.parseInt(prop.getProperty("maxworker", "10"));
			timewait = Integer.parseInt(prop.getProperty("timewait", "30"));
			maxworkertimewait = Integer.parseInt(prop.getProperty("maxworkertimewait", "180"));
			requestDate=prop.getProperty("requestdate", "20170527");

			String startDate = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMdd").substring(0,8);
			
			Set<Condition> set = new HashSet<Condition>();
			
			set.add(new Condition("fwVer", new Object[] { "1.1" }, null, Restriction.NEQ));
			//set.add(new Condition("hwVer", new Object[] { "1.0" }, null, Restriction.EQ));
			set.add(new Condition("protocolType", new Object[] { Protocol.SMS }, null, Restriction.EQ));
			set.add(new Condition("installDate", null, null,Restriction.NOTNULL));
			set.add(new Condition("lastLinkTime", new Object[] {startDate+"000000", startDate+"235959" }, null,Restriction.BETWEEN));

			List<MMIU> mbblist = mmiuDao.findByConditions(set);
			List<String> tempList = new ArrayList<String>();
			for(MMIU mmiu: mbblist){
				tempList.add(mmiu.getDeviceSerial());
			}
			String[] mbbIds = tempList.toArray(new String[0]);			
			
			set = new HashSet<Condition>();
			
			Condition cdt1 = new Condition("deviceType", new Object[] { ModemIFType.MBB.name() }, null, Restriction.EQ);
			set.add(cdt1);
			Condition cdt2 = new Condition("command", new Object[] { "cmdModemOTAStart" }, null,Restriction.EQ);
			set.add(cdt2);
			Condition cdt3 = new Condition("state", new Object[] { new Integer(CommonConstants.TR_STATE.Success.getCode())}, null,Restriction.NEQ);
			set.add(cdt3);
			Condition cdt4 = new Condition("requestTime", new Object[] {requestDate+"000000"}, null,Restriction.GT);
			set.add(cdt4);
			
			if(mbbIds != null && mbbIds.length> 0){
				Condition cdt5 = new Condition("id.mcuId", mbbIds, null, Restriction.IN);
				set.add(cdt5);
			}
			
			List<AsyncCommandLog> list = asyncCommandLogDao.findByConditions(set);
			
			for(AsyncCommandLog acl : list){
				asyncList.add(acl.getMcuId());
				acl.setState(new Integer(CommonConstants.TR_STATE.Waiting.getCode()));				
			}			
			
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
		
		txstatus = txmanager.getTransaction(null);
		
		try{
			int cnt= 0;
			for(String deviceSerial: asyncList){
				Modem modem = modemDao.get(deviceSerial);
				sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.RESTART_COMM_MODEM.getTypeCode(), SMSConstants.COMMAND_NAME.RESTART_COMM_MODEM.getCmdName(), null);
				
				if ((++cnt) % maxWorker == 0) {
					Thread.sleep(maxworkertimewait*1000);
                }else{
                	Thread.sleep(timewait*1000);
                }
			}
		}catch (Exception e) {
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
	 * SMS를 보내고  Async테이블에서 결과를 가져오는 함수 입니다.
	 */
	public void sendSmsForCmdServer(Modem modem, String messageType, String commandCode, String commandName, Map<String, String> paramMap) throws Exception {
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
            try{
                prop.load(getClass().getClassLoader().getResourceAsStream("config/command.properties"));
            }catch(Exception e){
                logger.error("Can't not read property file. -" + e,e);

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
			} else {
				logger.debug("Success get result");
				//Thread.sleep(35000);
				/*
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
				*/
			}
		} else {
			throw new Exception("Type Missmatch. this modem is not MMIU Type modem.");
		}
	}

	public Map<String, Object> sendSms(Map<String, Object> condition, List<String> paramList, String cmdMap) throws Exception {

		Map<String, Object> resultMap = new HashMap<String, Object>();
		String messageId = cmdOperationUtil.sendSMS(condition, paramList, cmdMap);
		String commandCode = condition.get("commandCode").toString();

		// 결과처리 로직 (S)
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
				/*
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
				*/
				resultMap.put("messageType", "F");
			}
		}
		// 결과처리 로직 (E)

		return resultMap;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		logger.info("#### MBBOTARetry Task start. ###");

		try {
			ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "spring-MBBOTARetryTask.xml" });
			DataUtil.setApplicationContext(ctx);

			MBBOTARetryTask task = (MBBOTARetryTask) ctx.getBean(MBBOTARetryTask.class);
			task.execute(null);

		} catch (Exception e) {
			logger.error("MBBOTARetryTask excute error - " + e, e);
		} finally {
			logger.info("#### MBBOTARetry Task finished. ###");			
			System.exit(0);
		}

	}

}
