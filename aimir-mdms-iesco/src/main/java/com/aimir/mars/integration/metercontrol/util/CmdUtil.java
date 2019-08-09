package com.aimir.mars.integration.metercontrol.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.TR_STATE;
import com.aimir.dao.device.AsyncCommandLogDao;
import com.aimir.dao.device.AsyncCommandResultDao;
import com.aimir.dao.device.MMIUDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.integration.WSMeterConfigLogDao;
import com.aimir.dao.integration.WSMeterConfigResultDao;
import com.aimir.fep.protocol.smsp.SMSConstants.COMMAND_TYPE;
import com.aimir.fep.protocol.smsp.command.frame.sms.ResponseFrame;
import com.aimir.mars.integration.metercontrol.server.McResponse;
import com.aimir.mars.integration.metercontrol.util.MeterControlConstants.ErrorCode;
import com.aimir.mars.integration.metercontrol.util.MeterControlConstants.ObisInfo;
//import com.aimir.mars.util.MarsProperty;
import com.aimir.model.device.AsyncCommandResult;
import com.aimir.model.device.MMIU;
import com.aimir.model.device.Modem;
import com.aimir.model.integration.WSMeterConfigLog;
import com.aimir.model.integration.WSMeterConfigResult;
import com.aimir.schedule.command.CmdOperationUtil;
import com.aimir.service.device.AsyncCommandLogManager;
import com.aimir.service.system.CircuitBreakerManager;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeUtil;

@Service
public class CmdUtil {
	private static Log log = LogFactory.getLog(CmdUtil.class);
	@Resource(name="transactionManager")
	HibernateTransactionManager txManager;

	@Autowired
	MMIUDao mmiuDao;
	@Autowired
	ModemDao modemDao;
//	@Autowired
//	CodeManager codeManager;
//	@Autowired
//	MeterManager meterManager;
//	@Autowired
//	ModemManager modemManager;
//	@Autowired
//	ContractManager contractManager;
//	@Autowired
//	OperationLogManager operationLogManager;
	@Autowired
	AsyncCommandLogManager asyncCommandLogManager;
	@Autowired
	CircuitBreakerManager circuitBreakerManager;
	@Autowired
	private CmdOperationUtil cmdOperationUtil;

	@Autowired
	private AsyncCommandLogDao asyncCommandLogDao;
	@Autowired
	private AsyncCommandResultDao resultDao;
	@Autowired
	private WSMeterConfigLogDao wsMeterconfigLogDao;
	@Autowired
	private WSMeterConfigResultDao wsMeterconfigResultDao;

	private static DefaultTransactionDefinition PropRequired = null;
	static {
		PropRequired =  new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED);
	}

	
	private static Integer  _waitingTimeout = Integer.valueOf(MeterControlProperty.getProperty("meterconfig.waiting.timeout", "3600"));  //unit sec, 3600sec=1hour
	private static Integer  _runningTimeout = Integer.valueOf(MeterControlProperty.getProperty("meterconfig.runing.timeout", "240"));    //unit sec, 240sec=4min
	/*
	 * Send SMS
	 */
	public List<Map<String,String>> sendSmsForCmdServer( String deviceSerial, String messageType, String commandCode, String commandName, Map<String, String> paramMap, long timeout) throws Exception {

		log.info( "deviceSerial: " + deviceSerial + " messageType: " + messageType + " commandCode: " + commandCode + " commandName: " + commandName + "timeout: " + timeout);
		Map<String, String> map = null;
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> condition = new HashMap<String, Object>();
		String euiId = null;
		List<Map<String, String>> result  = new ArrayList<Map<String, String>>();
		TransactionStatus txstatus = null;
		try {
			txstatus = txManager.getTransaction(null);
			Map<String,Object> scm = new HashMap<String,Object>();
			scm.put("modemId",  deviceSerial);
			scm.put("modemType", ModemType.MMIU.toString());
			Modem  modem = modemDao.get(deviceSerial);
			MMIU mmiu = mmiuDao.get(modem.getId());
			String mobliePhNum = mmiu.getPhoneNumber();
			euiId = deviceSerial;

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
				log.error("Can't not read property file. -" + e,e);
			}
			String serverIp = prop.getProperty("smpp.hes.fep.server") == null ? "" : prop.getProperty("smpp.hes.fep.server").trim();
			String serverPort = prop.getProperty("soria.modem.tls.port") == null ? "" : prop.getProperty("soria.modem.tls.port").trim();
			String authPort = prop.getProperty("smpp.auth.port") == null ? "" : prop.getProperty("smpp.auth.port").trim();
			paramListForSMS.add(serverIp);
			paramListForSMS.add(serverPort);
			paramListForSMS.add(authPort);


			String cmdMap = null;
			ObjectMapper om = new ObjectMapper();
			if (paramMap != null)
				cmdMap = om.writeValueAsString(paramMap);

			log.debug("Send SMS euiId: " + euiId + ", mobliePhNum: " + mobliePhNum + ", commandName: " + commandName + ", cmdMap " + cmdMap +",timeout: " + timeout);
			resultMap = sendSms(condition, paramListForSMS, cmdMap); // Send SMS!
			//String response_messageType = resultMap.get("messageType").toString();
			String response_messageId = resultMap.get("messageId") == null ? "F" : resultMap.get("messageId").toString();

			if (response_messageId.equals("FAIL") || response_messageId.equals("F") || response_messageId.equals("CF")) { // Fail
				log.debug("trId=" + response_messageId);
				throw new Exception(ErrorCode.MeterNotReached.name());
			} else {
				long newTime = System.currentTimeMillis();
				long oldTime = newTime;
				Integer lastStatus = null;
				Map<String, String> retryMap = new HashMap<String, String>();
				//retryMap.put("RESET_RETRY", "true");
				//retryMap.put("trId", response_messageId);
				log.debug("trId=" +response_messageId );
				while( true ) {
					if( (newTime - oldTime) >= timeout ) {
						break;
					}

					Long trId = Long.parseLong(response_messageId);
					//lastStatus = asyncCommandLogDao.getCmdStatus(modem.getDeviceSerial(), commandName);
					lastStatus = asyncCommandLogDao.getCmdStatusByTrId(deviceSerial, trId);
					if ( lastStatus == null){
						throw new Exception(ErrorCode.MeterNotReached.name());
					}
					if (TR_STATE.Success.getCode() == lastStatus) {
						break;
					}

					Thread.sleep(5000);
					newTime = System.currentTimeMillis();
				}

				if (TR_STATE.Success.getCode() != lastStatus) {
					log.error("FAIL : Communication Error but Send SMS Success. STATUS[" + lastStatus +"]  " + euiId + "  " + commandName);
					//result.add(retryMap);
				} else {
					ObjectMapper mapper = new ObjectMapper();
					List<AsyncCommandResult> asyncResult = resultDao.getCmdResults(deviceSerial, Long.parseLong(response_messageId),commandName); //ASYNC_COMMAND_RESULT에서 결과 값을 가져옴
					if (asyncResult == null || asyncResult.size() <= 0) {
						log.debug("FAIL : Send SMS but fail to execute " + euiId + "  " + commandName);
						throw new Exception(ErrorCode.SystemError.name());
					} else { // Success
						String resultStr = "";
						for (int i = 0; i < asyncResult.size(); i++) {
							resultStr += asyncResult.get(i).getResultValue();
						}
						log.debug("Async Result :" + resultStr);
						map = mapper.readValue(resultStr, new TypeReference<Map<String, String>>() {
						});
						
						if ( map != null && map.size() > 0) {
							for ( String key : map.keySet()) {
								Map<String, String> entryMap = new LinkedHashMap<String,String>();
								entryMap.put("paramType", key);
								entryMap.put("paramValue", map.get(key));
								//log.debug("Result Map:" + entryMap.toString() );
								result.add(entryMap);
							}
						}
						
					}
				}
			}
			txManager.commit(txstatus);
		}catch  (Exception e) {
			log.error("Error" + e, e);
			if (txstatus != null&& !txstatus.isCompleted())
				txManager.rollback(txstatus);
		}
		return result;
	}
	public Map<String, Object> sendSms(Map<String, Object> condition, List<String> paramList, String cmdMap) throws Exception {

		Map<String, Object> resultMap = new HashMap<String, Object>();
		String euiId = condition.get("euiId").toString();
		String messageId = cmdOperationUtil.sendSMS(condition, paramList, cmdMap);
		String commandCode = condition.get("commandCode").toString();

		// 결과처리 로직 (S)
		String rtnMessage = null;
		// MBB Modem으로 전송하는 SMS 명령이
		// 55(set up environment For NI),56(~~CoAP),57(~~SNMP)일 경우
		// Async_command_Result 조회를 하지않고, message id만 55, 56, 57 명령 처리 로직으로 넘겨준다.
		if (commandCode.equals(COMMAND_TYPE.NI.getTypeCode()) || commandCode.equals(COMMAND_TYPE.COAP.getTypeCode()) || commandCode.equals(COMMAND_TYPE.SNMP.getTypeCode())
				||	commandCode.equals(COMMAND_TYPE.RESTART_COMM_MODEM.getTypeCode()) ) {
			if (messageId.equals("FAIL")) {
				resultMap.put("messageId", "F");
			} else if (messageId.equals("FAIL-CONNECT")) {
				resultMap.put("messageId", "CF");
			} else {
				resultMap.put("messageId", messageId);
			}
		} 
		else {
			if (messageId.equals("FAIL")) {
				resultMap.put("messageType", "F");
			} else if (messageId.equals("FAIL-CONNECT")) {
				resultMap.put("messageType", "CF");
			} else {
				try {
					int time = 0;
					int interver = 5000;		// 5 second
					int period  = 20000;		// 20 second
					while ( time != period ){
						Thread.sleep(interver);
						time += interver;

						if (rtnMessage != null) {
							break;
						} else {
							try {
								rtnMessage = resultDao.getCmdResults(euiId, Long.parseLong(messageId));
							} catch (Exception e) {
								rtnMessage = null;
							}
						}
					}

				} catch (Exception e) {
					log.error("SendSMS excute error - " + e, e);
					resultMap.put("messageType", "F");
					return resultMap;
				}
				if (rtnMessage == null) {
					resultMap.put("messageType", "F");
					return resultMap;
				}
				ResponseFrame responseFrame = new ResponseFrame();
				resultMap = responseFrame.decode(rtnMessage);
			}
		}
		if ( resultMap.get("messageId") == null )
			resultMap.put("messageId", messageId);
		return resultMap;
	}

	public McResponse waitAndGetWSMeterConfigResult(String meterId, String trId, String command, int timeoutMillis){

		log.info( "meterId: " + meterId + " trId: " + trId + " command: " + command +  "timeout: " + timeoutMillis);

		McResponse res = new McResponse();
		long newTime = System.currentTimeMillis();
		long oldTime = newTime;
		Integer lastStatus = null ;
		String resultValue = null;
		Integer errorCode = null;
		TransactionStatus txstatus = null;
		ErrorCode err = ErrorCode.Success;
		try {

			txstatus = txManager.getTransaction(null);
			WSMeterConfigLog wcLog = null;


			while( true ) {
				if( (newTime - oldTime) > timeoutMillis ) {
					break;
				}
				Thread.sleep(3000);
				wsMeterconfigLogDao.clear();
				wcLog  = wsMeterconfigLogDao.getByAsyncTrId(meterId, trId, command);

				if ( wcLog == null ) {
					log.error("Record not found. meterId: " + meterId + " trId: " + trId + " command: " + command);
					err = ErrorCode.NoResult;
					res.setErrorString(err.getMessage());
					res.setErrorCode(err.getCode());
					throw  new Exception();
				}
				lastStatus = wcLog.getState();
				errorCode = wcLog.getErrorCode();

				if (TR_STATE.Waiting.getCode() != lastStatus && TR_STATE.Running.getCode() != lastStatus ) {
					break;
				}
				newTime = System.currentTimeMillis();

			}

			if (TR_STATE.Waiting.getCode() == lastStatus || TR_STATE.Running.getCode() == lastStatus ) {
				// UPDATE DATA BASE
				errorCode = ErrorCode.TimeOut.getCode();
				wcLog.setState(TR_STATE.Terminate.getCode());
				wcLog.setErrorCode(errorCode);
				wcLog.setUpdateDate(TimeUtil.getCurrentTime());
			}
			else if ( lastStatus == TR_STATE.Success.getCode() &&
					errorCode == ErrorCode.Success.getCode() ) {
				// Success and get ResultValue
				if (wcLog.getCommand().startsWith("Get")) {
					List<WSMeterConfigResult> results = wsMeterconfigResultDao.getResultsByAsyncTrId(meterId, trId, command);

					if (results == null || results.size() <= 0) {
						log.debug("Can't get Reulst. meterId: " + meterId + " trId: " + trId );
						// error 
						errorCode = ErrorCode.SystemError.getCode();
						wcLog.setState(TR_STATE.Terminate.getCode());
						wcLog.setErrorCode(errorCode);
						wcLog.setUpdateDate(TimeUtil.getCurrentTime());
					} else { // Success
						String resultStr = "";
						for (int i = 0; i < results.size(); i++) {
							resultStr += results.get(i).getResultValue();
						}
						ObisInfo obis = ObisInfo.getByCommand(wcLog.getCommand());
						resultValue = obis.getValueFromResult(resultStr);
//						ObjectMapper mapper = new ObjectMapper();
//                        Map<String, String> map = mapper.readValue(resultStr,
//                                new TypeReference<Map<String, String>>() {
//                                });
//						resultValue = (String)map.get("value");
					}
				}
			}
			err = ErrorCode.getErrorCode(errorCode);

			if ( resultValue != null ) {
				res.setResultValue(resultValue);
			}
			res.setErrorString(err.getMessage());
			res.setErrorCode(err.getCode());

			txManager.commit(txstatus);
		}catch  (Exception e) {
			log.error("Error" + e, e);
			if (txstatus != null&& !txstatus.isCompleted())
				txManager.rollback(txstatus);
			
			
			if ( err == ErrorCode.Success) 
				err = ErrorCode.SystemError;
			res.setErrorString(err.getMessage());
			res.setErrorCode(err.getCode());
		}
		return res;
	}

	public McResponse getWSMeterConfigResult(String meterId, String trId, String command ){
		McResponse res = new McResponse();
		long newTime = System.currentTimeMillis();
		long oldTime = newTime;
		Integer lastStatus = null ;
		String resultValue = null;
		Integer errorCode = null;
		TransactionStatus txstatus = null;
		ErrorCode err = ErrorCode.Success;
		
		try {

			txstatus = txManager.getTransaction(null);
			WSMeterConfigLog wcLog = null;
			wcLog  = wsMeterconfigLogDao.getByAsyncTrId(meterId, trId, command);
			if ( wcLog == null ) {
				log.error("Record not found. meterId: " + meterId + " trId: " + trId + " command: " + command);
				err = ErrorCode.NoResult;
				res.setErrorString(err.getMessage());
				res.setErrorCode(err.getCode());
				throw new Exception();
			}
			lastStatus = wcLog.getState();
			errorCode = wcLog.getErrorCode();

			TR_STATE state = TR_STATE.valueOf(lastStatus);
			Date currentDate = new Date();
			switch ( state  ) {
				case Waiting:
					// Waiting, perhaps on ActiveMQueue
					Date writeDate = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(wcLog.getWriteDate());
					long waitingTime = (currentDate.getTime() - writeDate.getTime()) / 1000;
					if ( waitingTime > _waitingTimeout ) {
						lastStatus = TR_STATE.Terminate.getCode();
						errorCode = ErrorCode.SystemError.getCode();
						// change status , errocode, updateTime 
						wcLog.setState(lastStatus);
						wcLog.setErrorCode(errorCode);
						wcLog.setUpdateDate(TimeUtil.getCurrentTime());
					}
					else {
						errorCode = ErrorCode.Running.getCode();
					}
					break;
				case Running:
					// Running, waiting for fep returns result
					Date updateDate = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(wcLog.getUpdateDate());
					long runningTime = (currentDate.getTime() - updateDate.getTime()) / 1000;
					if ( runningTime > _runningTimeout ) {
						lastStatus = TR_STATE.Terminate.getCode();
						errorCode = ErrorCode.MeterNotReached.getCode();
						// change status , errocode, updateTime 
						wcLog.setState(lastStatus);
						wcLog.setErrorCode(errorCode);
						wcLog.setUpdateDate(TimeUtil.getCurrentTime());
					}
					else {
						errorCode = ErrorCode.Running.getCode();
					}
					break;
				case Success:
					if ( errorCode == null ) {
						// Update ErrorCode 
						log.debug("state == SUCCESS but ERRORCODE is null. meterId: " + meterId + " trId: " + trId );
						errorCode = ErrorCode.SystemError.getCode();
						wcLog.setErrorCode(ErrorCode.SystemError.getCode());
						wcLog.setUpdateDate(TimeUtil.getCurrentTime());
					}
					else if ( errorCode == ErrorCode.Success.getCode() ) {
						if (wcLog.getCommand().startsWith("Get")) {
							List<WSMeterConfigResult> results = wsMeterconfigResultDao.getResultsByAsyncTrId(meterId, trId, command);

							if (results == null || results.size() <= 0) {
								log.debug("Can't get Reulst. meterId: " + meterId + " trId: " + trId );
								errorCode = ErrorCode.SystemError.getCode();
								wcLog.setErrorCode(ErrorCode.SystemError.getCode());
								wcLog.setUpdateDate(TimeUtil.getCurrentTime());
							} else { // Success !!
								String resultStr = "";
								for (int i = 0; i < results.size(); i++) {
									resultStr += results.get(i).getResultValue();
								}
								ObisInfo obis = ObisInfo.getByCommand(wcLog.getCommand());
								resultValue = obis.getValueFromResult(resultStr);
							}
						}
					}
					break;
				default:
					if ( errorCode == null ) {
						errorCode = ErrorCode.SystemError.getCode();
					}
					break;
			}

			err = ErrorCode.getErrorCode(errorCode);

			if ( resultValue != null ) {
				res.setResultValue(resultValue);
			}
			res.setErrorString(err.getMessage());
			res.setErrorCode(err.getCode());

			txManager.commit(txstatus);
		}catch  (Exception e) {
			log.error("Error" + e, e);
			try {
				if (txstatus != null&& !txstatus.isCompleted())
					txManager.rollback(txstatus);
			} catch (Exception ee) {
				
			}
			if ( err == ErrorCode.Success) 
				err = ErrorCode.SystemError;
			res.setErrorString(err.getMessage());
			res.setErrorCode(err.getCode());
		}
		return res;
	}
	
	public String createWsMeterconfigLog(String meterId, 
			String deviceSerial, ModemType modemType, Protocol protocolType, String deviceType, 
			String attributeNo, String classId, String obisCode,
			String operator, Integer state, Integer errCode,
			String command,String parameter,
			String requestDate,
			String writeDate,
			String updateDate
			) {

		TransactionStatus txStatus = null;
		String uuid = null;
		if ( meterId == null ) meterId = "";
		
		try {
			txStatus = txManager.getTransaction(PropRequired);

			WSMeterConfigLog mclog = new WSMeterConfigLog();

			uuid  = UUID.randomUUID().toString();
			mclog.setTrId(uuid);
			mclog.setDeviceId(meterId);
			mclog.setModemId(deviceSerial);
			mclog.setModemType(modemType);
			mclog.setProtocolType(protocolType);
			mclog.setDeviceType(deviceType);
			mclog.setAttributeNo(attributeNo);
			mclog.setClassId(classId);
			mclog.setObisCode(obisCode);
			mclog.setOperator(operator);
			mclog.setState(state);
			mclog.setCommand(command);
			mclog.setParameter(parameter);
			mclog.setWriteDate(writeDate);
			mclog.setUpdateDate(updateDate);
			mclog.setRequestDate(requestDate);

			mclog.setErrorCode(errCode);
			wsMeterconfigLogDao.add(mclog);
			
			if ( txStatus != null ) {
				txManager.commit(txStatus);
			}

		}
		catch (Exception e) {
			if (txStatus != null) {
				try {
					txManager.rollback(txStatus);
				}
				catch (Exception ee) {}
				uuid = null;
			}
			log.error(e, e);
			log.error( " failed to save .trId:" + uuid + " meterId: " + meterId +  " deviceSerial:" + deviceSerial + " modemType:" + modemType 
					+" protocolType:" +  protocolType
					+" deviceType:" +  deviceType 
					+" attributeNo:" + attributeNo 
					+" classId:" + classId 
					+" obisCode:" +  obisCode
					+" operator:" +  operator
					+" state:" +  state
					+" command: " + command
					+" parameter:" +  parameter
					+" writeDate:" +  writeDate
					+"updateDate :" + updateDate 
					 +"errCode:" + errCode);
		}
		return requestDate+ "-" + uuid;
	}

}