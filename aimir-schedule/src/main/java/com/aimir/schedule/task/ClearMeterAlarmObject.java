package com.aimir.schedule.task;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.configuration.DataConfiguration; // INSERT 2018/02/19 #SP-892
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.hibernate.SessionFactory;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.constants.CommonConstants.ModemIFType;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.OperatorType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.constants.CommonConstants.TR_OPTION;
import com.aimir.constants.CommonConstants.TR_STATE;
import com.aimir.dao.device.AsyncCommandLogDao;
import com.aimir.dao.device.AsyncCommandParamDao;
import com.aimir.dao.device.AsyncCommandResultDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MMIUDao;
import com.aimir.dao.device.MeterAttrDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.device.OperationLogDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.OperatorDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.protocol.smsp.SMSConstants;
import com.aimir.fep.protocol.smsp.SMSConstants.COMMAND_TYPE;
import com.aimir.fep.protocol.smsp.command.frame.sms.ResponseFrame;

import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.AsyncCommandLog;
import com.aimir.model.device.AsyncCommandParam;
import com.aimir.model.device.AsyncCommandResult;
import com.aimir.model.device.MMIU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.MeterAttr;
import com.aimir.model.device.Modem;
import com.aimir.schedule.command.CmdOperationUtil;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeUtil;

import net.sf.json.JSONArray;

/**
 * Create 2018/08/14 SP-987
 *
 */
/**
 * @author 
 *
 */
@Service
public class ClearMeterAlarmObject extends ScheduleTask
{
	private static Logger logger = LoggerFactory.getLogger(RecollectMeteringSoriaTaskMBB.class);
	@Resource(name = "transactionManager")
	HibernateTransactionManager txmanager;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	SupplierDao supplierDao;
	@Autowired
	ModemDao modemDao;

	@Autowired
	MMIUDao mmiuDao;
	@Autowired
	MCUDao mcuDao;

	@Autowired
	MeterDao meterDao;

	@Autowired
	MeterAttrDao meterAttrDao;

	@Autowired
	CodeDao codeDao;

	@Autowired
	OperatorDao 	operatorDao;

	@Autowired
	CmdOperationUtil cmdOperationUtil;


	@Autowired
	OperationLogDao operationLogDao;

	@Autowired
	AsyncCommandLogDao asyncCommandLogDao;


	@Autowired
	AsyncCommandResultDao resultDao;

	private String[] 	meters = null;

	private int _maxThreadWorker = 10;
	private int _maxMmiuThreadWorker = 10;
	private int _maxSubgigaThreadWorker = 10;
	private int _maxReadRecordNum = 2000;

	private int _timeout = 1380;
	private int _smsTimeOut = 60;
	private String _lastLinkTime = null;
	private String _alarmDate = null;
	private boolean     useAsyncChannel = false; 
	private boolean     clearMeterAttr = true;
	private String 		modemType = "ALL";
	
	private String 	_startTime = null;
	private final String _alarmValueNone  = "00000000,10000000"; // 0x10000000 == "Modem Communication OK"
	private final String _clearValue  = "00000000";
	
	public String[] getMeters() {
		return meters;
	}

	public void setUseAsyncChannel(boolean useAsyncChannel) {
		this.useAsyncChannel = useAsyncChannel;
	}

	public void setClearMeterAttr(boolean clearMeterAttr) {
		this.clearMeterAttr = clearMeterAttr;
	}
	public void setMeters(String[] _meters) {
		this.meters = _meters;
	}

	public void setModemType(String modemType) {
		this.modemType = modemType;
	}

	private List<Map<String,Object>> getMucsToClearAlarmObject()
	{
		Map<String, Object> param = new HashMap<String,Object>();
		List<Map<String,Object>> mcuList = null;
		TransactionStatus txstatus = null;
		try {
			txstatus = txmanager.getTransaction(null);
			param.put("alarmValue",_alarmValueNone); 
			param.put("lastLinkTime", _lastLinkTime);
			mcuList  = meterAttrDao.getMucsToClearAlarmObject(param);
			txmanager.commit(txstatus);
		}catch  (Exception e) {
			logger.error("GetMeter Error" + e, e);
			if (txstatus != null&& !txstatus.isCompleted())
				txmanager.rollback(txstatus);
		}
		return mcuList;

	}
	
	private List getTargetMetersList(String[] meters) {
		ArrayList<HashMap<String,Object>> ret = new ArrayList<HashMap<String,Object>>();
		TransactionStatus txstatus = null;
		try {
			txstatus = txmanager.getTransaction(null);
			for (int i  = 0; i < meters.length ; i++) {
				HashMap<String,Object> entry = new HashMap<String,Object>();
				Meter meter = meterDao.get(meters[i]);
				if ( meter == null ) 
					continue;
				Modem modem = meter.getModem();
				if ( modem == null)
					continue;
				if ( meter != null ) {
					entry.put("meterId", meter.getId());
					entry.put("mdsId", meter.getMdsId());
					entry.put("deviceSerial", modem.getDeviceSerial());
					entry.put("modemType", modem.getModemType().name());
					entry.put("protocolType", modem.getProtocolType().name());
					if ( modem.getModemType() == ModemType.MMIU && modem.getProtocolType() == Protocol.SMS ) {
						MMIU mmiu = mmiuDao.get(modem.getId());
						entry.put("phoneNumber", mmiu.getPhoneNumber());
					} 
					entry.put("modemId",  modem.getId());
				}
				ret.add(entry);
			}
			txmanager.commit(txstatus);
		}catch  (Exception e) {
				logger.error("GetMeter Error" + e, e);
				if (txstatus != null&& !txstatus.isCompleted())
					txmanager.rollback(txstatus);
		}
		return ret;
	}
	
	private List getTargetMetersMMIU(Integer page, Integer limit, boolean count)
	{
		TransactionStatus txstatus = null;
		//Session session = null;
		List<Object> ret = new ArrayList<Object>();
		logger.debug("page[{}] limit[{}] count[{}]", page, limit,count);
		try {
			txstatus = txmanager.getTransaction(null);
			HashMap<String,Object> param = new HashMap<String,Object>();

			if ( !count ) {
				param.put("page", page);
				param.put("limit", limit);
			}
			param.put("alarmValue", _alarmValueNone); 
			param.put("modemType", "MMIU");
			param.put("lastLinkTime", _lastLinkTime);
			if ( clearMeterAttr ) {
				param.put("alarmDate2", _startTime);
			}
			param.put("alarmDate1", _alarmDate);

			ret =  meterAttrDao.getMetersToClearAlarmObject(param, count);
			txmanager.commit(txstatus);
			logger.debug("READ COUNT={}", ret.size());
		}
		catch  (Exception e) {
			logger.error("RecollectThread.run error - " + e, e);
			if (txstatus != null&& !txstatus.isCompleted())
				txmanager.rollback(txstatus);
		}
		return ret;
	}
	private List getTargetMeters(Integer page, Integer limit, boolean count)
	{
		TransactionStatus txstatus = null;
		//Session session = null;
		List<Object> ret = new ArrayList<Object>();
		logger.debug("page[{}] limit[{}] count[{}]", page, limit,count);
		try {
			txstatus = txmanager.getTransaction(null);
			HashMap<String,Object> param = new HashMap<String,Object>();

			if ( !count ) {
				param.put("page", page);
				param.put("limit", limit);
			}
			param.put("alarmValue", _alarmValueNone); //0,268435456  == 0x10000000 == "Modem Communication OK"
			param.put("alarmDate1", _alarmDate);
			param.put("lastLinkTime", _lastLinkTime);
			if ( clearMeterAttr ) {
				param.put("alarmDate2", _startTime);
			}
			ret =  meterAttrDao.getMetersToClearAlarmObject(param, count);
			txmanager.commit(txstatus);
		}
		catch  (Exception e) {
			logger.error("RecollectThread.run error - " + e, e);
			if (txstatus != null&& !txstatus.isCompleted())
				txmanager.rollback(txstatus);
		}
		return ret;
	}

	private List getTargetMetersByMcu(Integer mcuId)
	{
		TransactionStatus txstatus = null;
		//Session session = null;
		List<Object> ret = new ArrayList<Object>();
		try {
			txstatus = txmanager.getTransaction(null);
			HashMap<String,Object> param = new HashMap<String,Object>();
			param.put("mcuId", mcuId);
			param.put("alarmValue", _alarmValueNone); //0,268435456  == 0x10000000 == "Modem Communication OK"
			param.put("lastLinkTime", _lastLinkTime);

			ret =  meterAttrDao.getMetersToClearAlarmObject(param, false);
			txmanager.commit(txstatus);
		}
		catch  (Exception e) {
			logger.error("RecollectThread.run error - " + e, e);
			if (txstatus != null&& !txstatus.isCompleted())
				txmanager.rollback(txstatus);
		}
		return ret;
	}


	class ClearMeterAlarmObjectThread implements Callable<Integer> {
		Map<String,Object> entry;

		public ClearMeterAlarmObjectThread(Map<String,Object>entry) { 
			this.entry = entry;
		}

		public Integer call() throws Exception {
			doClearMeterAlarmObject(entry);
			return 1;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void execute(JobExecutionContext context) {

		Properties prop = new Properties();
		Date startDate = new Date();
		long startTime = startDate.getTime();
		_startTime = DateTimeUtil.getDateString(startDate);
		
		try {
			//txstatus = txmanager.getTransaction(null);
			try{
				prop.load(getClass().getClassLoader().getResourceAsStream("config/schedule-ClearMeterAlarmObject.properties"));
			}catch(Exception e){
				logger.error("Can't not read property file. -" + e,e);
			}

			_maxMmiuThreadWorker = Integer.valueOf((String) prop.getProperty("clear.meteralarm.mmiu.maxworker","10"));
			_maxSubgigaThreadWorker = Integer.valueOf((String) prop.getProperty("clear.meteralarm.subgiga.maxworker","10"));
			
			_maxReadRecordNum = Integer.parseInt(prop.getProperty("clear.meteralarm.maxread", "2000"));
			_timeout = Integer.parseInt(prop.getProperty("clear.meteralarm.timeout", "720")); // 60 * 12 min
			_smsTimeOut =  Integer.parseInt(prop.getProperty("clear.meteralarm.smstimeout", "60")); // 60 sec
			
			Integer beforeHour = Integer.parseInt(prop.getProperty("clear.meteralarm.lastLinkTime.beforeHour", "24"));
			Integer updateHour = Integer.parseInt(prop.getProperty("clear.meteralarm.alarmDate.beforeHour", "168")); // 1 week 
			
			Calendar calendar = Calendar.getInstance();
			Date now = new Date();
			calendar.setTime(now);
			calendar.add(Calendar.HOUR, (beforeHour*-1));
			Date lastLinkDate = calendar.getTime();
			_lastLinkTime = DateTimeUtil.getDateString(lastLinkDate);
			
			if ( updateHour > 0 ) {
				calendar.setTime(now);
				calendar.add(Calendar.HOUR, (updateHour*-1));
				Date lastUpdateDate = calendar.getTime();
				_alarmDate = DateTimeUtil.getDateString(lastUpdateDate);
			}
			
			logger.info("maxSubGigaThreadWorker[{}] maxMmiuThreadWorker[{}] maxReadRecordNum[{}]  timeout[{}] lastLinkTime[{}]",
					_maxSubgigaThreadWorker, _maxMmiuThreadWorker, _maxReadRecordNum,_timeout,_lastLinkTime);
			
			final ExecutorService executor = Executors.newCachedThreadPool();
			List<Future<Long>> list = new ArrayList<Future<Long>>();
			Future<Long> future = null;
			if ( meters == null && (modemType.equals("ALL") ||  modemType.equals("SubGiga")) ) {
				future = (Future<Long>) executor.submit(new SubGigaStartThread(_lastLinkTime));
				list.add(future);
			}
			if ( modemType.equals("ALL") ||  modemType.equals("MMIU") ) {
				future = (Future<Long>) executor.submit(new MmiuStartThread(_lastLinkTime));
				list.add(future);
			}
			executor.shutdown();
			executor.awaitTermination(_timeout, TimeUnit.MINUTES);
			
			for (Future<Long> future2 : list) {
				future2.get();
			}
		}catch (Exception e){
			logger.error(e.getMessage(),e);
		}
		finally {
			//if (txstatus != null&& !txstatus.isCompleted())
			//txmanager.rollback(txstatus);
		}
	}

	/*
	 * Send SMS
	 */
	public Map sendSmsForCmdServer( String phoneNumber,String deviceSerial, String messageType, String commandCode, String commandName, Map<String, String> paramMap, long timeout) throws Exception {
		logger.debug("[sendSmsAndGetResult] " + " messageType: " + messageType + " commandCode: " + commandCode + " commandName: " + commandName);
		Map<String, String> map = null;
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> condition = new HashMap<String, Object>();
		String mobliePhNum = null;
		String euiId = null;
		DataConfiguration config = null;
		TransactionStatus txstatus = null;
		try {
			txstatus = txmanager.getTransaction(null);
			mobliePhNum = phoneNumber;
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
				logger.error("Can't not read property file. -" + e,e);
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

			logger.debug("Send SMS euiId: " + euiId + ", mobliePhNum: " + mobliePhNum + ", commandName: " + commandName + ", cmdMap " + cmdMap +",timeout: " + timeout);
			resultMap = sendSms(condition, paramListForSMS, cmdMap); // Send SMS!
			//String response_messageType = resultMap.get("messageType").toString();
			String response_messageId = resultMap.get("messageId") == null ? "F" : resultMap.get("messageId").toString();

			if (response_messageId.equals("F") || response_messageId.equals("CF")) { // Fail
				logger.debug("trId={}",response_messageId);
				return null;
			} else {
				long newTime = System.currentTimeMillis();
				long oldTime = newTime;
				Integer lastStatus = null;
				Map<String, String> retryMap = new HashMap<String, String>();
				retryMap.put("RESET_RETRY", "true");
				retryMap.put("trId", response_messageId);
				logger.debug("trId={}",response_messageId );
				while( true ) {
					if( (newTime - oldTime) >= timeout ) {
						break;
					}

					Long trId = Long.parseLong(response_messageId);
					//lastStatus = asyncCommandLogDao.getCmdStatus(modem.getDeviceSerial(), commandName);
					lastStatus = asyncCommandLogDao.getCmdStatusByTrId(deviceSerial, trId);
					if ( lastStatus == null){
						logger.error("Can't find ASYNC_COMMAND_LOG");
						return null;
					}
					if (TR_STATE.Success.getCode() == lastStatus) {
						break;
					}

					Thread.sleep(5000);
					newTime = System.currentTimeMillis();
				}

				if (TR_STATE.Success.getCode() != lastStatus) {
					logger.debug("FAIL : Communication Error but Send SMS Success. STATUS[" + lastStatus +"]  " + euiId + "  " + commandName);
					return retryMap;
				} else {
					ObjectMapper mapper = new ObjectMapper();
					List<AsyncCommandResult> asyncResult = resultDao.getCmdResults(deviceSerial, Long.parseLong(response_messageId),commandName); //ASYNC_COMMAND_RESULT에서 결과 값을 가져옴
					if (asyncResult == null || asyncResult.size() <= 0) {
						logger.debug("FAIL : Send SMS but fail to execute " + euiId + "  " + commandName);
						return retryMap;
					} else { // Success
						String resultStr = "";
						for (int i = 0; i < asyncResult.size(); i++) {
							resultStr += asyncResult.get(i).getResultValue();
						}
						map = mapper.readValue(resultStr, new TypeReference<Map<String, String>>() {
						});
						logger.debug("Success get result");
					}
				}
			}
			txmanager.commit(txstatus);
		}catch  (Exception e) {
			logger.error("GetMeter Error" + e, e);
			if (txstatus != null&& !txstatus.isCompleted())
				txmanager.rollback(txstatus);
		}
		return map;
	}


	public boolean  doClearMeterAlarmObject(Map<String,Object> entry) {
		logger.debug("MeterId[{}] modemId[{}] modemType[{}] protocolType[{}]",
				(String)entry.get("mdsId"),(String)entry.get("deviceSerial"),(String)entry.get("modemType"),(String)entry.get("protocolType"));

		//Map<String,Object> modemMap = new HashMap<String,Object>();
		//List<Map<String, Object>> modemList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> rtnStrList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> modemList = new ArrayList<Map<String, Object>>();
		ResultStatus status = ResultStatus.FAIL;
		//Meter meter = null;
		String cmd = "cmdMeterParamSet";
		JSONArray jsonArr = null;
		String mdsId = "";
		String deviceSerial ="";
		String modemType = "";
		String protocolType = "";
		String phoneNumber = null;
		boolean ret = false;
		try{
			List<Map<String,String>> paramList = new ArrayList<Map<String,String>>();
			Map<String,String> paramMap = new HashMap<String,String>();

			String obisCode = "0.0.97.98.0.255";
			String classId = "1";
			String attributeNo = "2";
			String dataType = "double-long-unsigned";
			String accessRight = "RW";
			String value = "[{\"value\":\"0\"}]";
			String paramType = "paramSet";
			paramMap.put(paramType, obisCode+"|"+classId+"|"+attributeNo+"|"+accessRight+"|"+dataType+"|"+value);
			paramList.add(paramMap);

			mdsId = (String)entry.get("mdsId");

			modemType =(String)entry.get("modemType");
			protocolType = (String)entry.get("protocolType");
			deviceSerial = (String)entry.get("deviceSerial");
			
			if( ModemType.MMIU.name().equals(modemType)  && Protocol.SMS.name().equals(protocolType) ) {
				String mobileNo = (String)entry.get("phoneNumber");
				if (mobileNo == null || "".equals(mobileNo)) {
					throw new Exception("Phone number is empty");
				}
				phoneNumber = mobileNo;
			}
		
			
			if( ModemType.MMIU.name().equals(modemType) && Protocol.SMS.name().equals(protocolType)) {
				Map<String, String> result;
				String cmdResult = "";
				
				if ( useAsyncChannel ) {
					executeUseAsyncMode(mdsId, deviceSerial, modemType, protocolType, SMSConstants.COMMAND_TYPE.NI.getTypeCode(), paramMap);
					if ( clearMeterAttr ) {
						// async result is unknown, so clear value anyway
						saveMeterAttr((Integer)entry.get("meterId"), _clearValue);
					}
				}
				else {
					result = sendSmsForCmdServer( phoneNumber, deviceSerial,  SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), cmd, paramMap, _smsTimeOut*1000); 

					if(result != null){
						cmdResult =  result.get("RESULT").toString();
						logger.info("RESULT[Success] Meter[{}] Modem[{}] ModemType[{}] ProtocolType[{}] CmdResult[{}]",
								mdsId, deviceSerial, modemType, protocolType, cmdResult);
						ret = true;
						if ( clearMeterAttr ) {
							saveMeterAttr((Integer)entry.get("meterId"), _clearValue);
						}
					}else{
						throw new Exception("SMS Fail");
					}
				}
			} else {
				List<Map<String, Object>> result = null;
				Protocol prot = getProtocolType(protocolType);
				if ( prot == null ) {
					throw new Exception("protocol Type is not supported");
				}

				result = cmdOperationUtil.cmdMeterParamSet(deviceSerial,
						paramList.get(0).get("paramSet"),prot);
				boolean success = false;
				boolean setValue = false;
				if ( result != null && result.size() > 0) {
					for(Map<String,Object> ent : result) {
						String eParamType = (String) ent.get("paramType");
						String eParamValue = (String) ent.get("paramValue");
						logger.debug("resut:paramType="+ eParamType + "paramValue="+ eParamValue);
						if ( "RESULT_VALUE".equalsIgnoreCase(eParamType) 
								&& "Success".equalsIgnoreCase(eParamValue)) {
							success = true;
						}
						else if ( "RESULT_STEP".equalsIgnoreCase(eParamType) 
								&& "SET_VALUE".equalsIgnoreCase(eParamValue)) {
							setValue = true;
						}
					}
				}
				if (success && setValue ) {
					logger.info("RESULT[Success] Meter[{}] Modem[{}] ModemType[{}] ProtocolType[{}] CmdResult[{}]",
							mdsId, deviceSerial, modemType, protocolType, "DONE!");
					ret = true;
					if ( clearMeterAttr ) {
						saveMeterAttr((Integer)entry.get("meterId"), _clearValue);
					}
				}
				else {
					throw new Exception("Result is NULL or Fail");
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			logger.error("RESULT[Fail] Meter[{}] Modem[{}] ModemType[{}] ProtocolType[{}] ErrorMessage[{}]",
					mdsId, deviceSerial, modemType, protocolType, e.getMessage());
		}
		return ret;
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
					logger.error("SendSMS excute error - " + e, e);
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
		// 결과처리 로직 (E)
		resultMap.put("messageId", messageId);
		return resultMap;
	}

	/**
	 * @param args
	 * Usage
	 * 	mvn -e -f $AIMIR_TASK/pom-ClearMeterAlarmObject.xml antrun:run -DtaskName=ClearMeterAlarmObject -Dspring.instrument.path=$MVNREPOSITORY/org/springframework/spring-instrument/4.2.5.RELEASE
	 */
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();

		logger.info("#### ClearMeterAlarmObject start. ###");


		String meters[] = null;
		String meter = null;
		boolean useAsyncChannel = true;
		boolean clearMeterAttr = true;
		String modemType = "ALL";
		for (int i = 0; i < args.length; i += 2) {
			String nextArg = args[i];

			logger.debug("arg[i]=" + args[i] + "arg[i+1]=" + args[i+1]);

			if (nextArg.startsWith("-device")) {
				if ( args[i + 1] != null && args[i + 1].length() > 0 && !"${device}".equals(args[i + 1])) {
					meter = args[i + 1].trim();
					meters = meter.split(",");
				}
			}
			else if ( nextArg.startsWith("-useAsyncChannel")){
				if ( args[i + 1] != null && args[i + 1].length() > 0 && !"${useAsyncChannel}".equals(args[i + 1])) {
					useAsyncChannel = Boolean.parseBoolean(args[i+1]);
				}
			}
			else if ( nextArg.startsWith("-clearMeterAttr")){
				if ( args[i + 1] != null && args[i + 1].length() > 0 && !"${clearMeterAttr}".equals(args[i + 1])) {
					clearMeterAttr = Boolean.parseBoolean(args[i+1]);
				}
			}
			else if ( nextArg.startsWith("-modemType")) {
				if ( args[i + 1] != null && args[i + 1].length() > 0 && !"${modemType}".equals(args[i + 1])) {
					if ( "SubGiga".equalsIgnoreCase(args[i+1])) {
						modemType = "SubGiga";
					}
					else if ( "MMIU".equalsIgnoreCase(args[i+1])) {
						modemType = "MMIU";
					}
				}
			}
		}
		logger.info("ClearMeterAlarmObject . meters[{}] useAsyncChannel=[{}] clearMeterAttr[{}] modemType[{}]", meter,useAsyncChannel,clearMeterAttr, modemType );

		try {
			ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "spring-ClearMeterAlarmObject.xml" });
			DataUtil.setApplicationContext(ctx);

			ClearMeterAlarmObject task = (ClearMeterAlarmObject) ctx.getBean(ClearMeterAlarmObject.class);

			task.setMeters(meters);
			task.setUseAsyncChannel(useAsyncChannel);
			task.setClearMeterAttr(clearMeterAttr);
			task.setModemType(modemType);
			task.execute(null);

		} catch (Exception e) {
			logger.error("ClearMeterAlarmObject excute error - " + e, e);
		} finally {
			logger.info("#### ClearMeterAlarmObject Task finished - Elapse Time : {} ###", DateTimeUtil.getElapseTimeToString(System.currentTimeMillis() - startTime));
			System.exit(0);
		}
	}

	Protocol getProtocolType(String name)
	{
	    for(Protocol pro : Protocol.values()){
	    	if ( pro.name().equals(name)) {
	    		return pro;
	    	}
	    }
	    return null;
	}
	

	private void executeUseAsyncMode(String meterId, String deviceSerial, String modemType, String protocolType, String commandCode, Map<String,String> paramMap) throws Exception {	
		List<Map<String, Object>> asyncTargetList = null;
		asyncTargetList = new ArrayList<>();
		
		String commandName = "cmdMeterParamSet";
		
		Map<String, Object> targetMap = new HashMap<String, Object>();
		
		paramMap.put("CommandCode", commandCode);
		targetMap.put("deviceSerial", deviceSerial);
		targetMap.put("asycParams", paramMap);
		
		asyncTargetList.add(targetMap);

		if ( asyncTargetList != null && 0 < asyncTargetList.size() && commandName != null){
			saveAsyncCommandByUseAsyncOption(asyncTargetList, commandName);	
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void saveAsyncCommandByUseAsyncOption(List<Map<String, Object>> targetList, String commandName){
		TransactionStatus txStatus = null;

		try {
			String currentTime = TimeUtil.getCurrentTime();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmssSSS");
			int count = 0;

			logger.info("============== Async Command save start ======================");
			for(Map<String, Object> target : targetList) {
				txStatus = txmanager.getTransaction(null);

				Calendar calendar = Calendar.getInstance();
				Thread.sleep(10);
				String sequence = dateFormat.format(calendar.getTime());

				AsyncCommandLogDao asyncCommandLogDao = DataUtil.getBean(AsyncCommandLogDao.class);
				AsyncCommandLog asyncCommandLog = new AsyncCommandLog();
				asyncCommandLog.setTrId(Long.parseLong(sequence));
				asyncCommandLog.setMcuId((String)target.get("deviceSerial"));
				asyncCommandLog.setDeviceType(ModemIFType.MBB.name());
				asyncCommandLog.setDeviceId((String)target.get("deviceSerial"));
				asyncCommandLog.setCommand(commandName);
				asyncCommandLog.setTrOption(TR_OPTION.ASYNC_OPT_RETURN_DATA_SAVE.getCode());
				asyncCommandLog.setState(TR_STATE.Waiting.getCode());
				asyncCommandLog.setOperator(OperatorType.OPERATOR.name());
				asyncCommandLog.setCreateTime(currentTime);
				asyncCommandLog.setRequestTime(currentTime);
				asyncCommandLog.setLastTime(null);
				asyncCommandLogDao.add(asyncCommandLog);
				logger.debug("asyncCommandLog ==> " + asyncCommandLog.toJSONString());

				int num = 0;
				if (target.get("asycParams") != null && ((Map<String, String>)target.get("asycParams")).size() > 0) {
					AsyncCommandParamDao asyncCommandParamDao = DataUtil.getBean(AsyncCommandParamDao.class);
					Map<String, String> asycParams = (Map<String, String>) target.get("asycParams");

					Iterator<String> iter = asycParams.keySet().iterator();
					while (iter.hasNext()) {
						String key = iter.next();

						AsyncCommandParam asyncCommandParam = new AsyncCommandParam();
						asyncCommandParam.setMcuId(asyncCommandLog.getMcuId());
						asyncCommandParam.setNum(num++);
						asyncCommandParam.setParamType(key);
						asyncCommandParam.setParamValue(asycParams.get(key));
						asyncCommandParam.setTrId(asyncCommandLog.getTrId());
						if(key.equals("CommandCode")) {
							asyncCommandParam.setTrType("CommandCode");
						}else {
							asyncCommandParam.setTrType("CMD");
						}

						asyncCommandParamDao.add(asyncCommandParam);
						logger.debug("asyncCommandParam ==> " + asyncCommandParam.toJSONString());
					}
				}

				txmanager.commit(txStatus);
				logger.info(++count +"/"+ targetList.size() + ". TID [" + sequence + "] save ok.");
			}

			logger.info("============== Async Command save finished ======================");
		}catch (Exception e) { 
			logger.error("Save AsyncLog Error - " + e.getMessage(), e);

			if(txStatus != null) {
				txmanager.rollback(txStatus);
			}    
		}

	}

	void saveMeterAttr(Integer meterId, String val ) {
		TransactionStatus txStatus = null;
		
		try {
			String updateTime = DateTimeUtil.getDateString(new Date());
			txStatus = txmanager.getTransaction(null);

			MeterAttr meterAttr = meterAttrDao.getByMeterId(meterId);
			if ( meterAttr != null ) {
				meterAttr.setAlarmValue(val);
				meterAttr.setAlarmDate(updateTime);
				meterAttrDao.update(meterAttr);
			}
			txmanager.commit(txStatus);
		}catch (Exception e) { 
			logger.error("meterAttr Error - " + e.getMessage(), e);
			if(txStatus != null) {
				txmanager.rollback(txStatus);
			}    
		}
	}
	
	/**
	 * SubGiga Start Thread
	 *
	 */
	class SubGigaStartThread extends Thread {
		private String lastLinkTime = "";
		
		SubGigaStartThread(String lastLinkTime){
			if ( lastLinkTime != null)
				this.lastLinkTime =  lastLinkTime;
		}
		
		public void run(){
			logger.info("*****     Start Meter SubGiga Clear     *****");
			int cnt = 0;

			try {	

				List<Map<String,Object>> mcuList = getMucsToClearAlarmObject();
				
				ExecutorService pool1 = Executors.newFixedThreadPool(_maxSubgigaThreadWorker);
				
				SubGigaThread threads1[] = new SubGigaThread[mcuList.size()];
				int i = 0;

				for (Map<String,Object> mcu : mcuList) {
					logger.info(cnt++ + ": MCU[" + mcu.get("sysId") + "] Recollect Start ");

					threads1[i] = new SubGigaThread(mcu);
					
					pool1.execute(threads1[i]);
					i++;
				}

				logger.info("ExecutorService for mcu shutdown.");
				pool1.shutdown();
				logger.info("ExecutorService for mcu awaitTermination. [" + _timeout + "]min");
				pool1.awaitTermination(_timeout, TimeUnit.MINUTES);

				cnt = 0;
				i = 0;   
			}catch (Exception e){
				logger.error(e.getMessage(),e);
			}
		}
	}

	class SubGigaThread extends Thread {
		Map<String,Object> entry;

		SubGigaThread(Map<String,Object> entry)  {
			this.entry = entry;
		}
		public void run() {
			long threadId = Thread.currentThread().getId();
		
			List<Map<String,Object>> recList = getTargetMetersByMcu((Integer)entry.get("mcuId"));
			logger.info("ThreadID[{}] MCU[{}] Meter(SubGiga) collect[{}] Metering thread start.",
					threadId, entry.get("sysId"), recList.size());
			try {
				for ( Map<String,Object> entry : recList) {				
					doClearMeterAlarmObject(entry);
				}
			} catch (Exception ex) {
				logger.info("ThreadID[{}] Mcu Clear MeterAlarmObject  thread end. MCU[{}] is  failed.", 
						threadId,entry.get("sysId"));	
			} 
			logger.info("ThreadID[{}] MCU[{}] Meter(SubGiga) size[{}] Clear MeterAlarmObject  end.",
					threadId, entry.get("sysId"), recList.size());
		}
	}
	
	/**
	 * MMIU Clear Meter Alarm Object Start Thread
	 *
	 */
	class MmiuStartThread extends Thread {
		private String dso = null;
		private String lastLinkTime  = null;
		MmiuStartThread(String lastLinkTime){
			this.lastLinkTime = lastLinkTime;
		}

		public void run() {
			try {
				Date startDate = new Date();
				long startTime = startDate.getTime();
				int allRecNum = 0;
				if ( meters == null || meters.length == 0  ) {
					List<BigDecimal>  ret =  getTargetMetersMMIU(null,null,true );

					if ( ret.size() !=  1 ) {
						logger.error("Can't get Record Count of MeterAttr !");
						throw new Exception("Can't get Record Count of MeterAttr !");
					}
					allRecNum = ret.get(0).intValue();
				}
				else {
					allRecNum = meters.length;
				}

				logger.info("MeterAttr Target Record Count = {} ", allRecNum);

				ExecutorService executorService = Executors.newFixedThreadPool(_maxMmiuThreadWorker);

				CompletionService<Integer> completionService
				= new ExecutorCompletionService<Integer>(executorService); 

				int readNum = (allRecNum / _maxReadRecordNum ) + 1 ;
				int finNum = 0;
				int startNum = 0;
				boolean finish = false;
				for ( int readCnt = 0; readCnt < readNum; readCnt ++ ) {
					if ( finish )
						break;
					List<Map<String,Object>>  targetList =  new  ArrayList<Map<String,Object>>();
					if ( meters == null || meters.length == 0  ) {
						targetList = getTargetMetersMMIU(readCnt, _maxReadRecordNum, false );
					}else {
						targetList = getTargetMetersList(meters);
					}

					int targetNum = targetList.size();

					for (int i = 0; i < targetNum; i++ ) {
						Map<String,Object> entry = targetList.get(i);

						completionService.submit(new ClearMeterAlarmObjectThread(entry));
						startNum ++;
					}
					// Wait until free thread
					while ( finNum <= startNum  - _maxThreadWorker)  { 
						Date now = new Date();
						long nowTime = now.getTime();
						if ( (nowTime - startTime) / (1000 * 60) >  _timeout ) {
							finish = true;
							break;
						}
						Future<Integer> future = completionService.take(); 
						Integer result = future.get(); 
						finNum += 1;
					}
				}
				executorService.shutdown(); 
				// Wait All job are finished.
				while (  !finish &&  finNum < startNum )  {
					Date now = new Date();
					long nowTime = now.getTime();
					if ( (nowTime - startTime) / (1000 * 60) >  _timeout ) {
						finish = true;
						break;
					}
					Future<Integer> future = completionService.take(); 
					Integer result = future.get(); 
					finNum += 1;
				} 
			}catch (Exception e){
				logger.error(e.getMessage(),e);
			}
			finally {
				//if (txstatus != null&& !txstatus.isCompleted())
				//txmanager.rollback(txstatus);
			}
		}
	}
}
