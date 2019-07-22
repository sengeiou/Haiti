package com.aimir.schedule.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.configuration.DataConfiguration; // INSERT 2018/02/19 #SP-892
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
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.constants.CommonConstants.MeterType;
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
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.device.OperationLogDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.OperatorDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.DLMS_CLASS;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.DLMS_CLASS_ATTR;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.OBIS;
import com.aimir.fep.protocol.smsp.SMSConstants;
import com.aimir.fep.protocol.smsp.SMSConstants.COMMAND_NAME;
import com.aimir.fep.protocol.smsp.SMSConstants.COMMAND_TYPE;
import com.aimir.fep.protocol.smsp.SMSConstants.MESSAGE_TYPE;
import com.aimir.fep.protocol.smsp.command.frame.sms.ResponseFrame;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.AsyncCommandLog;
import com.aimir.model.device.AsyncCommandParam;
import com.aimir.model.device.AsyncCommandResult;
import com.aimir.model.device.MMIU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.device.OperationLog;
import com.aimir.model.system.Code;
import com.aimir.model.system.Operator;
import com.aimir.model.system.Role;
import com.aimir.model.system.Supplier;
import com.aimir.schedule.command.CmdOperationUtil;
import com.aimir.schedule.task.RecollectMeteringSoriaTaskByDCU.McuDeviceList;
import com.aimir.schedule.util.CommonUtil;
import com.aimir.util.Condition;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeUtil;

/**
 * Create 2018/03/07 SP-892
 *
 */
@Service
public class RecollectMeteringSoriaTaskMBB extends ScheduleTask
{
	private static Logger logger = LoggerFactory.getLogger(RecollectMeteringSoriaTaskMBB.class);
	@Resource(name = "transactionManager")
	HibernateTransactionManager txmanager;

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

	private DeviceType deviceType;
	private String 		supplierName;
	int		supplierId;
	private String 		loginId;
	private String		fromDate;
	private String		toDate;
	private String		meterId;
	private boolean 	forceOption;
	private boolean     showList = false;
	private boolean     useAsyncChannel = false;  // SP-892 (2018-03-12)
	private int			_beforeTime = 24;

	// for timeout 
	private long		_meterHandshakeTimeout = 30;
	private long		_meterDayTimeout = 148;
	private long		_modemHandshakeTimeout = 15;
	private long		_modemDayTimeout = 48;
	
	private boolean 	_isResetModem = true; 
	
	private CommonConstants.DateType dateType;
	
	private static DefaultTransactionDefinition transDef = null;
	private static DefaultTransactionDefinition trDefOutline = null;
	static {
		transDef =  new DefaultTransactionDefinition();
		transDef.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
		
		trDefOutline =  new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_SUPPORTS);
	}
	/**
	 * @return the fromDate
	 */
	public String getFromDate() {
		return fromDate;
	}



	/**
	 * @param fromDate the fromDate to set
	 */
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}



	/**
	 * @return the toDate
	 */
	public String getToDate() {
		return toDate;
	}



	/**
	 * @param toDate the toDate to set
	 */
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}



	/**
	 * @return the loginId
	 */
	public String getLoginId() {
		return loginId;
	}



	/**
	 * @param loginId the loginId to set
	 */
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}



	/**
	 * @return the supplierName
	 */
	public String getSupplierName() {
		return supplierName;
	}



	/**
	 * @param supplierName the supplierName to set
	 */
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	/**
	 * @return the deviceType
	 */
	public DeviceType getDeviceType() {
		return deviceType;
	}

	/**
	 * @param deviceType the deviceType to set
	 */
	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

    /**
	 * @return the meterId
	 */
	public String getMeterId() {
		return meterId;
	}

	/**
	 * @param meterId the meterId to set
	 */
	public void setMeterId(String meterId) {
		this.meterId = meterId;
	}

	public CommonConstants.DateType getDateType() {
		return dateType;
	}

	public void setDateType(CommonConstants.DateType dateType) {
		this.dateType = dateType;
	}

	public boolean getForceOption() {
		return forceOption;
	}

	public void setForceOption(boolean force) {
		this.forceOption = force;
	}

	/**
	 * @return the showList
	 */
	public boolean isShowList() {
		return showList;
	}

	/**
	 * @param showList the showList to set
	 */
	public void setShowList(boolean showList) {
		this.showList = showList;
	}
	
	public void setUseAsyncChannel(boolean useAsyncChannel) {
		this.useAsyncChannel = useAsyncChannel;
	}

	private List<Object> getGaps(String fromDate, String toDate)
	{
		TransactionStatus txstatus = null;
		List<Object> allGaps = new ArrayList<Object>();
		try {
			txstatus = txmanager.getTransaction(null);

			if ( supplierName != null && !"".equals(supplierName)){
				Supplier supplier = supplierDao.getSupplierByName(supplierName);
				if ( supplier == null ){
					throw new Exception("Supplier:" + supplierName + " is not exist.");
				}
				supplierId = supplier.getId();
			}
			else {
				Supplier supplier = supplierDao.getAll().get(0);
				supplierId = supplier.getId();
				logger.info("Default Supplier={}", supplier.getName());
			}
			// calc 24 hour before
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.add(Calendar.HOUR, (_beforeTime*-1));
			Date before24Date = calendar.getTime();

			MeterType[] meterTypes = {MeterType.EnergyMeter};
			for ( MeterType meterType : meterTypes){
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("channel"        , 1);
				params.put("searchStartDate",fromDate);
				params.put("searchEndDate"  , toDate);
				params.put("dateType"       , CommonConstants.DateType.HOURLY.getCode());
				params.put("meterType"      , meterType.name());
				// -> INSERT START 2018/02/15 #SP-892
				params.put("modemType"      , ModemType.MMIU);
				params.put("protocolType"   , Protocol.SMS);
				// <- INSERT END   2018/02/15 #SP-892
				params.put("supplierId"     , supplierId);
				params.put("lastLinkTime"   , DateTimeUtil.getDateString(before24Date));
				logger.info("Get Missing Meters: startDate[{}] endDate[{}] meterType[{}] supplierId[{}] lastLinkTime[{}]",
						fromDate, toDate, meterType.name(), supplierId, DateTimeUtil.getDateString(before24Date));
				if ( meterId != null && !"".equals(meterId)){
					params.put("mdsId", meterId);
				}

				List<Object> gaps  = null;
				if ( dateType == CommonConstants.DateType.DAILY ){
					logger.debug("getMissingMeters:params={}", params);
					gaps = meterDao.getMissingMetersForRecollect(params);
				}
				else {
					logger.debug("getMissingMetersForRecollectByHour:params={}", params);
					gaps = meterDao.getMissingMetersForRecollectByHour(params);
				}

				if(useAsyncChannel) {
					logger.debug("Missing Meters({}) Total Count= {}", meterType.name(), gaps.size());
				}else {
					StringBuffer sbuf = new StringBuffer();
					for (Object obj : gaps) {
						@SuppressWarnings("unchecked")
						HashMap<String,Object> resultMap = (HashMap<String, Object>)obj;
						sbuf.append("'" + (String) resultMap.get("mdsId") + "',");
					}
					logger.debug("Missing Meters({}) Count= {}:{}", meterType.name(),gaps.size(), sbuf.toString());
				}

				allGaps.addAll(gaps);
			}
			txmanager.commit(txstatus);
		}
		catch  (Exception e) {
			logger.error("RecollectThread.run error - " + e, e);
			if (txstatus != null&& !txstatus.isCompleted())
				txmanager.rollback(txstatus);
		}
		return allGaps;
	}

	public class McuDeviceList {
		Integer mcuId;
		String  sysId;
		List<String> deviceIdList;
		String	meterMdsId;

		public boolean equals(Object obj){
			McuDeviceList t = null;
			if(obj == null) {
				return false;
			}
			if(obj instanceof McuDeviceList) {
				t = (McuDeviceList)obj;
			}else {
				return false;
			}
			
			if (this.mcuId.compareTo(t.mcuId) == 0 ) return true;
			else return false;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void execute(JobExecutionContext context) {
		int        _maxThreadWorker    =   10;
		long       _timeout            = 3600;

		
		Properties prop = new Properties();

		try {
			//txstatus = txmanager.getTransaction(null);
			try{
				prop.load(getClass().getClassLoader().getResourceAsStream("config/schedule-RecollectMeteringSoriaMBB.properties"));
			}catch(Exception e){
				logger.error("Can't not read property file. -" + e,e);
			}
			
			boolean bAutoRecovery = false;
			if ( fromDate == null || "".equals(fromDate) || toDate == null || "".endsWith(toDate )){
				Map<String, String> TFBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -24);
				String TFDate = TFBeforeHour.get("date").replace("-", "") + TFBeforeHour.get("time").replace(".", "");
				dateType = CommonConstants.DateType.DAILY;
				fromDate = TFDate.substring(0,8);
				toDate = TFDate.substring(0,8);
				bAutoRecovery = true;
			}
			
			if(useAsyncChannel) {
				logger.debug("######### UseAsyncMode execute start. #########");
	    		
				executeUseAsyncMode(bAutoRecovery);
				
				logger.debug("######### UseAsyncMode execute stop. #########");
			}else {
				_maxThreadWorker    = Integer.parseInt(prop.getProperty("recollect.ondemand.maxworker"    ,   "4"));
				_timeout            = Integer.parseInt(prop.getProperty("recollect.ondemand.timeout"      , "3600"));
				_isResetModem = Boolean.parseBoolean((String) prop.getProperty("recollect.resetmodem","true"));
				_beforeTime         = Integer.parseInt(prop.getProperty("recollect.ondemand.beforeTime"   ,   "24"));
				
				// get Timeout for earch call
				// -> INSERT START 2018/02/15 #SP-892
				_meterHandshakeTimeout = Integer.valueOf((String) prop.getProperty("meter.timeout.handshaking","30"))*1000;
				_meterDayTimeout = Integer.valueOf((String) prop.getProperty("meter.timeout.day","148"))*1000;
				_modemHandshakeTimeout = Integer.valueOf((String)prop.getProperty("modem.timeout.handshaking","15"))*1000; 
				_modemDayTimeout = Integer.valueOf((String) prop.getProperty("modem.timeout.day","48"))*1000;

				List<Object> gaps = getGaps(fromDate, toDate);
	    		logger.info("Total Meter to need recollect metering ["+ gaps.size() + "]");
	    		
	    		int cnt = 0;

	    		if ((gaps.size() == 0) && (forceOption == true)) {
	    			Map<String, Object> obj = new HashMap<String, Object>();
	    			obj.put("mdsId", meterId);
	    			gaps.add(obj);
	    			logger.debug("Forced option is specified.");
	    		}
				if ( showList ){
	    			for (Object obj : gaps) {
	    				HashMap<String,Object> resultMap = (HashMap<String, Object>) obj;
	    				String mdsId = (String) resultMap.get("mdsId");
	    				logger.info(cnt + ": Meter[" + mdsId + "] Recollect Metering");
	    				cnt++;
	    			}
					return;
				}
				
	    		ExecutorService pool = Executors.newFixedThreadPool(_maxThreadWorker);
	    		RecollectThread threads[] = new RecollectThread[gaps.size()];
	    		int	i = 0;

	    		if(gaps != null && gaps.size() > 0){
	    			if ( !deviceType.name().equals("Meter") && bAutoRecovery ){
	    				toDate = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss").substring(0,8);
	    			}
	    			logger.info("RecollectMeteringSoriaTaskMBB . deviceType:" + deviceType +",fromDate=" + fromDate + ",toDate=" + toDate + ", UseAsyncChannel = " + useAsyncChannel);

	                cnt = 0;
	                i = 0;

	            	logger.debug("Recollect Meters(for Earch) Count={}", gaps.size());
	    			for (Object obj : gaps) {
	    				HashMap<String,Object> resultMap = (HashMap<String, Object>) obj;
	    				String mdsId = (String) resultMap.get("mdsId");
	    				logger.info(cnt + ": Meter[" + mdsId + "] Recollect Metering");
	    				cnt++;
	    				if ( dateType == CommonConstants.DateType.DAILY ){
	    					threads[i] = new RecollectThread(cnt, mdsId, deviceType, fromDate+"000000", toDate+"235959", loginId);
	    				}
	    				else{
	     					threads[i] = new RecollectThread(cnt, mdsId, deviceType, fromDate+"0000", toDate+"5959", loginId);
	    				}
	    				pool.execute(threads[i]);
	    				i++;
	    			}

	    			logger.info("ExecutorService shutdown.");
	    			pool.shutdown();
	    			logger.info("ExecutorService awaitTermination. [" + _timeout + "]sec");
	    			pool.awaitTermination(_timeout, TimeUnit.SECONDS);
	    		}
	    		//txmanager.commit(txstatus);				
			}
    	}
    	catch (Exception e) {
    		logger.error("RecollectThread.run error - " + e, e);
    	}
    	finally {
    		//if (txstatus != null&& !txstatus.isCompleted())
			//txmanager.rollback(txstatus);
    	}
	}


    class RecollectThread extends Thread {

        private String mdsId;
        CommonConstants.DeviceType deviceType;
        String fromDate;
        String toDate;
        String loginId;
        CommonConstants.DateType dateType;
        int thrNo;
        
        RecollectThread(int thrNo,String mdsId,CommonConstants.DeviceType deviceType, String fromDate, String toDate, String roginId)  {
        	this.thrNo      = thrNo;
        	this.mdsId      = mdsId;
        	this.deviceType = deviceType;
        	this.fromDate   = fromDate;
        	this.toDate     = toDate;
        	this.loginId    = roginId;
        }

        public void run() {
        	TransactionStatus txstatus = null;

        	logger.info("[No={}] RecollecThread Start mdsId[{}] deviceType[{}] fromDate[{}] toDate[{}]", thrNo, mdsId, deviceType.name(), fromDate, toDate, loginId  );
        	try {
        		txstatus     = txmanager.getTransaction(trDefOutline);
        		Map<String, String>   result = null;
        		Meter meter  = meterDao.get(mdsId);
        		if (meter == null ) {
        			logger.error("[{}] Meter[" + mdsId + "] is null" ,mdsId);
        			throw new Exception("Meter[" + mdsId + "] is null");
        		}
        		Modem modem = meter.getModem();
        		if (modem == null ) {
        			logger.error("[{}] modem of Meter[" + mdsId + "] is null" ,mdsId);
        			throw new Exception("modem of Meter[" + mdsId + "] is null");
        		}

//    			Code operationCode = null;
//                if (deviceType == CommonConstants.DeviceType.MCU){
//                    operationCode = codeDao.getCodeIdByCodeObject("8.1.1");
//                } else if (deviceType == CommonConstants.DeviceType.Modem){
//                    operationCode = codeDao.getCodeIdByCodeObject("8.1.2");
//                } else if ( deviceType == CommonConstants.DeviceType.Meter){
//                    operationCode = codeDao.getCodeIdByCodeObject("8.1.3");
//                }
//
//                if (operationCode != null && loginId != null && !"".equals(loginId)){
//                	if (!commandAuthCheck(loginId,  operationCode.getCode())) {
//                		logger.error("[{}] No permission", mdsId);
//                		throw new Exception("No permission");
//                	}
//                }

//        		String nOption = "";
        		logger.info("[{}]  ModemType = {}, ProtocolType = {}",
        				mdsId, modem.getModemType().name(),  modem.getProtocolType().name());
        		if((modem.getModemType() == ModemType.MMIU) && (modem.getProtocolType() == Protocol.SMS)) {
        			if ( deviceType == CommonConstants.DeviceType.Meter){
        				result = onDemandMeterBypassMBB(meter.getMdsId(), fromDate, toDate, loginId);
        				logger.debug("[{}] send sms command : onDemandMeterBypassMBB", mdsId);
        			}
        			else if ( deviceType == CommonConstants.DeviceType.Modem){
        				result = romReadBypassMBB(meter.getMdsId(), fromDate, toDate, loginId);
        				logger.debug("[{}] send sms command : romReadBypassMBB", mdsId);
        			}
        		}

                if ( meter != null ){
                	ResultStatus status = ResultStatus.FAIL;

                	if ( result != null && result.get("result") != null
                			&& "Success".equalsIgnoreCase((String)result.get("result"))){
                		logger.info("[{}] Recollect Success", mdsId);
                		status = ResultStatus.SUCCESS;
                	}
                	else {
                		logger.error("[{}] Recollect Fail", mdsId);
                	}
//                	 if (operationCode != null && loginId != null && !"".equals(loginId)){
//            			saveOperationLog(meter.getSupplier(),
//            					meter.getMeterType(), meter.getMdsId(), loginId,
//        						operationCode, status.getCode(), "Recollect Metering - " + status.name());
//            		}

                }
        		txmanager.commit(txstatus);
        	}
        	catch  (Exception e) {
        		logger.error("[" +mdsId+ "] RecollectThread.run error - " + e, e);
        		if (txstatus != null&& !txstatus.isCompleted())
        			txmanager.rollback(txstatus);
        	}

        	logger.info("[No={}] RecollecThread End [{}]", thrNo, mdsId );
        }
    }


    /*
     * Send SMS
     */
	public Map sendSmsForCmdServer(Meter meter, Modem modem, String messageType, String commandCode, String commandName, Map<String, String> paramMap, long timeout) throws Exception {
		logger.debug("[sendSmsAndGetResult] " + " messageType: " + messageType + " commandCode: " + commandCode + " commandName: " + commandName);

		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> condition = new HashMap<String, Object>();
		String mobliePhNum = null;
		String euiId = null;
		DataConfiguration config = null;

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


			String cmdMap = null;
			ObjectMapper om = new ObjectMapper();
			if (paramMap != null)
				cmdMap = om.writeValueAsString(paramMap);

			logger.debug("Send SMS euiId: " + euiId + ", mobliePhNum: " + mobliePhNum + ", commandName: " + commandName + ", cmdMap " + cmdMap +",timeout: " + timeout);
			resultMap = sendSms(condition, paramListForSMS, cmdMap); // Send SMS!
			//String response_messageType = resultMap.get("messageType").toString();
			String response_messageId = resultMap.get("messageId") == null ? "F" : resultMap.get("messageId").toString();
			/*
			 * 결과 처리
			 */
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
					lastStatus = asyncCommandLogDao.getCmdStatusByTrId(modem.getDeviceSerial(), trId);
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
					List<AsyncCommandResult> asyncResult = resultDao.getCmdResults(modem.getDeviceSerial(), Long.parseLong(response_messageId),commandName); //ASYNC_COMMAND_RESULT에서 결과 값을 가져옴
					if (asyncResult == null || asyncResult.size() <= 0) {
						logger.debug("FAIL : Send SMS but fail to execute " + euiId + "  " + commandName);
						return retryMap;
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
			logger.error("Type Missmatch. this modem is not MMIU Type modem.");
			return null;
		}
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

	private Map<String, String> getOnDemandMeterBypassMBBParam(String mdsId, int modemPort, String fromDate, String toDate) throws Exception{
		Map<String,String> paramMap = new HashMap<String,String>();
		if (modemPort==0) {
			logger.debug("cmdGetLoadProfile ["+ mdsId + "][" + modemPort +  "]["  +  fromDate + "][" +toDate +"]");

			String obisCode = DataUtil.convertObis(OBIS.ENERGY_LOAD_PROFILE.getCode());
			int classId = DLMS_CLASS.PROFILE_GENERIC.getClazz();
			int attrId = DLMS_CLASS_ATTR.PROFILE_GENERIC_ATTR02.getAttr();

			Map<String,String> valueMap = CommonUtil.getParamValueByRange(fromDate,toDate);
			String value = CommonUtil.meterParamMapToJSON(valueMap);

			logger.debug("[{}] ObisCode=> {}, classID => {}, attributeId => {}", mdsId, obisCode,classId,attrId);
			//paramGet
			paramMap.put("paramGet", obisCode+"|"+classId+"|"+attrId+"|null|null|"+value);
		}
		else {
	    	logger.debug("cmdGetLoadProfile ["+ mdsId + "][" + modemPort +  "]["  +  fromDate + "][" +toDate +"]");

	    	String obisCode = DataUtil.convertObis(OBIS.MBUSMASTER_LOAD_PROFILE.getCode());
			int classId = DLMS_CLASS.PROFILE_GENERIC.getClazz();
			int attrId = DLMS_CLASS_ATTR.PROFILE_GENERIC_ATTR02.getAttr();

			Map<String,String> valueMap = CommonUtil.getParamValueByRange(fromDate,toDate);
			String value = CommonUtil.meterParamMapToJSON(valueMap);

			logger.debug("[{}] ObisCode=> {}, classID => {}, attributeId => ", mdsId, obisCode, classId, attrId);
			//paramGet
			paramMap.put("paramGet", obisCode+"|"+classId+"|"+attrId+"|null|null|"+value);
		}
		paramMap.put("option", "ondemandmbb");

		return paramMap;
	}
	
	private Map<String, String> onDemandMeterBypassMBB(String mdsId, String fromDate, String toDate, String loginId) {
		logger.debug("onDemandMeterBypassMBB Start mdsId[{}] fromDate[{}] toDate[{}] loginId[{}]",
				mdsId, fromDate,toDate,loginId);
		ResultStatus status = ResultStatus.FAIL;
		Meter meter = meterDao.get(mdsId);
		String cmd = "cmdMeterParamGet";
		
//		String detailInfo = "";
//		String rtnStr = "";
		Map<String,String> returnMap = null;
//		JSONArray jsonArr = null;
		try{
//			if (loginId != null ){
//				if (!commandAuthCheck(loginId,  "8.1.10")) {
//					throw new Exception("No permission");
//				}
//			}

			int modemPort = 0;
			if ( meter.getModemPort() != null ){
				modemPort = meter.getModemPort().intValue();
			}
			if ( modemPort > 5){
				logger.error("[{}] ModemPort: {} is not Support", mdsId, modemPort);
				throw new Exception("ModemPort:" + modemPort + " is not Support");
			}
			
			Map<String,String> paramMap = getOnDemandMeterBypassMBBParam(mdsId, modemPort, fromDate, toDate);
			
    		Map<String,Object> map = new HashMap<String,Object>();
    		try{
            	if(meter != null && meter.getModem() != null) {
        			Modem modem = meter.getModem();
        			if(modem.getModemType() == ModemType.MMIU && (modem.getProtocolType() == Protocol.SMS 
        					|| modem.getProtocolType() == Protocol.IP
        					|| modem.getProtocolType() == Protocol.GPRS)) {
	            		MMIU mmiu = (MMIU)mmiuDao.get(meter.getModemId());

	            		map.put("meterId", mdsId);
	            		map.put("modemType", meter.getModem().getModemType().name());
	            		map.put("protocolType", meter.getModem().getProtocolType());
	            		map.put("modem", mmiu);
            		}
            	} else {
            		logger.error("[{}] FAIL : Target ID null!", mdsId);
            	}
    		}catch(Exception e) {
    			logger.warn("[" + mdsId + "] onDemandMeterBypassMBB excute error - {}" + e,e);
 //       		rtnStr = "FAIL : Target ID null!";
    		}

        	try{
        		if(map.get("modemType") == ModemType.MMIU.name() && map.get("protocolType") == Protocol.SMS) {
        			MMIU mmiu = (MMIU)map.get("modem");

	        		String mobileNo = mmiu.getPhoneNumber();
	            	if (mobileNo == null || "".equals(mobileNo)) {
	            		logger.warn(String.format("[" + cmd + "] Phone number is empty"));
//	            		rtnStr = "FAIL : Phone number is empty!";
	        		}
	            	else if (!Protocol.SMS.equals(mmiu.getProtocolType())) {
	            		logger.warn(String.format("[" + cmd + "] Invalid ProtocolType"));
//	            		rtnStr = "FAIL : Invalid ProtocolType!";
	    			}
	            	else {
//						Long trId = System.currentTimeMillis();
						Map<String, String> result;
						String cmdResult = "";
						SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

						int days = (int) ((formatter.parse(toDate.substring(0, 8)).getTime() - formatter.parse(fromDate.substring(0, 8)).getTime())/1000/3600/24 + 1);
						int LPinterval = meter.getLpInterval();
						long timeout = _meterHandshakeTimeout + (_meterDayTimeout * (60 / LPinterval) * days);
						logger.debug("from[{}] to[{}]"
								+ "Timeout[{}] = HandshakeTimeout[{}] + DayTimeout[{}]*(60/LPinterval[{}])*days[{}]",
								fromDate, toDate, timeout,_meterHandshakeTimeout,_meterDayTimeout,LPinterval,days);
						
						result = sendSmsForCmdServer(meter, mmiu, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), cmd, paramMap, timeout);

						if (result != null && result.get("RESET_RETRY") != null  &&
								result.get("trId") != null ){
							String messageId = result.get("trId");
							Long trid = null;
							try {
								trid = Long.parseLong(messageId);
							}
							catch (Exception e){
								logger.error(e.getMessage());
							}
							if ( trid != 0 && _isResetModem ){
								logger.info("onDemandMeterBypassMBB FAILED AND RESET MODEM");
								if ( saveAsyncCommandLog( mmiu.getDeviceSerial(),  cmd, trid,  TR_STATE.Waiting.getCode())){
									restartCommModem((Modem)mmiu);
								}
							}
						}
						else if(result != null){
								cmdResult = "Success";
								status = ResultStatus.SUCCESS;
								returnMap = new HashMap<String,String>();
								returnMap.put("result", "Success");
								//detailInfo = result.get("detail").toString();
				                //log.info("detailInfo[" + detailInfo + "]");
				                //Map tmpMap = parseDetailMessageForMBB(
	                            //        detailInfo);
				        		//detailInfo = makeHTML(tmpMap);
				                //log.info("detailInfo(converted)[" + detailInfo + "]");
				                ///////////////////////
								logger.debug("sendSmsForCmdServer : SUCCESS");
//				                if (meter.getModem() != null) {
//				                	meter.getModem().setCommState(1);
//				                }

						}else{
								logger.error("sendSmsForCmdServer Fail");
								//cmdResult="Failed to get the resopone. See the Async Command History.";
								//cmdResult="Check the Async_Command_History.";
						}
						//rtnStr = cmdResult;
	            	}
				}
			} catch (Exception e) {
    			logger.error("[" + mdsId + "] onDemandMeterBypassMBB excute error - " + e,e);
			}

		} catch (Exception e) {
			logger.warn("[" + mdsId + "] onDemandMeterBypassMBB excute error - " + e,e);
			//rtnStr = "FAIL : " + e.getMessage();
		}

        return returnMap;
	}

    private Map<String, String> romReadBypassMBB(String mdsId, String fromDate, String toDate, String loginId) {
    	logger.debug("romReadBypassMBB Start mdsId[{}] fromDate[{}] toDate[{}] loginId[{}]",
    			mdsId, fromDate,toDate,loginId);

    	ResultStatus status = ResultStatus.FAIL;
    	Meter meter = null;
    	String cmd = "cmdGetROMRead";
    	Map<String, String> returnMap = null;

    	meter = meterDao.get(mdsId);
    	Modem modem = meter.getModem();

    	try {
//	    	if (!commandAuthCheck(loginId,  "8.1.10")) {
//				throw new Exception("No permission");
//	    	}

	    	if ( modem == null ){
	    		logger.error("Modem of meter[{}] is null", mdsId);
				throw new Exception("Target modem is NULL");
	    	}
	        if ( fromDate == null || "".equals(fromDate) ||
	        		toDate == null || "".equals(toDate) ) {
	        	logger.error("[" + mdsId + "] fromDate or toDate is not specified");
				throw new Exception("fromDate or toDate is not specified");
	        }

    		if( ((modem.getModemType() == ModemType.MMIU) && (modem.getProtocolType() == Protocol.SMS)) ){
    			Map<String, String> asyncResult = new HashMap<String, String>();
    			Map<String, String> paramMap = new HashMap<String, String>();
    			MMIU mmiu = (MMIU)mmiuDao.get(meter.getModemId());

    			paramMap.put("meterId", mdsId);
    			paramMap.put("fromDate",fromDate);
    			paramMap.put("toDate", toDate);
    			
    			int LPinterval = meter.getLpInterval();
    			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
				int days = (int) ((formatter.parse(toDate.substring(0, 8)).getTime() - formatter.parse(fromDate.substring(0, 8)).getTime())/1000/3600/24 + 1);
				long timeout = _modemHandshakeTimeout + (_modemDayTimeout * days);
				logger.debug("from[{}] to[{}]"
						+ "Timeout[{}] = HandshakeTimeout[{}] + DayTimeout[{}]*days[{}]",
						fromDate, toDate, timeout,_modemHandshakeTimeout,_modemDayTimeout,days);
				
       			asyncResult = sendSmsForCmdServer(meter, mmiu, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), cmd, paramMap, timeout);

       			if (asyncResult != null && asyncResult.get("RESET_RETRY") != null  &&
						asyncResult.get("trId") != null ){
					String messageId = asyncResult.get("trId");
					Long trid = null;
					try {
						trid = Long.parseLong(messageId);
					}
					catch (Exception e){
						logger.error(e.getMessage());
					}

					if ( trid != 0 && _isResetModem ){
						logger.debug("romReadBypassMBB FAILED AND RESET MODEM");
						if ( saveAsyncCommandLog( mmiu.getDeviceSerial(),  cmd, trid,  TR_STATE.Waiting.getCode())){
							restartCommModem((Modem)mmiu);
						}
					}
				}
				else if(asyncResult != null){
    				status = ResultStatus.SUCCESS;
					returnMap = new HashMap<String,String>();
					returnMap.put("result", "Success");
    			}else{
    				logger.debug("[" + mdsId + "] SMS Fail");
    			}
//    			// SUCCESS
//    			for (String key : asyncResult.keySet()) {
//	                if ( key.equals("detail") ){
//		                //// Convert escape char
//	                	String value = asyncResult.get(key).toString() ;
//		        		ObjectMapper mapper = new ObjectMapper();
//		        		String json = "{\"key\":\""+ value + "\"}";
//		        		Map<String, Object> tmpMap = new HashMap<String, Object>();
//		        		tmpMap = mapper.readValue(json, new TypeReference<Map<String, String>>(){});
//		            	log.debug("detail[" +(String)tmpMap.get("key") +"]" );
//		            	mav.addObject(key, (String)tmpMap.get("key"));
//	                }
//	                else {
//	                	mav.addObject(key, asyncResult.get(key).toString());
//	                }
//    			}
//    			mav.addObject("status", status.name());
//    			return mav;
//    		}
//    		else {
//    			mav.addObject("rtnStr", "Invalid Type!");
//    			mav.addObject("status", status.name());
//    			mav.addObject("detail", "");
//    			return mav;
    		}
    	}catch(Exception e){
    		logger.error("[" + mdsId + "] romReadBypassMBB  excute error - " + e,e);
    	}
    	return returnMap;
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

	/**
	 * @param args
	 * Usage
	 * 	mvn -e -f $AIMIR_TASK/pom-RecollectMeteringMBB.xml antrun:run -DtaskName=RecollectMeteringMBB -DdeviceType=DeviceType -DsupplierName=SupplierName -DfromDate=FromYYYYMMDD -DtoDate=ToYYYYMMDD -Dspring.instrument.path=$MVNREPOSITORY/org/springframework/spring-instrument/4.2.5.RELEASE
	 */
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		
		logger.info("-----");
		logger.info("-----");
		logger.info("-----");
		logger.info("#### RecollectMeteringSoriaTaskMBB start. ###");

//		if (args.length < 14) {
//			logger.info("Usage:");
//			logger.info("GroupOTARetryTask -DtaskName=TaskName -DfirmwareVersion=FirmwareVersion -DfirmwareFileName=FirmwareFileName -DlocationName=LocationName -DissueDate=IssueDAte");
//			return;
//		}

		CommonConstants.DateType  dateType = null ;
		String deviceType = null;
		String supplierName = null;
		String loginId = "admin";
		String fromDate = null;
		String toDate = null;
		String meterId = "";
		boolean showlist = false;
		String force = "false";		// INSERT SP-476
		boolean useAsyncChannel = false;
		
		for (int i = 0; i < args.length; i += 2) {
			String nextArg = args[i];

			logger.debug("arg[i]=" + args[i] + "arg[i+1]=" + args[i+1]);

			if (nextArg.startsWith("-deviceType")) {
				if ( !"${deviceType}".equals(args[i + 1]))
					deviceType = new String(args[i + 1]);
			}
			else if (nextArg.startsWith("-supplierName")) {
				if ( !"${supplierName}".equals(args[i + 1]))
					supplierName = new String(args[i + 1]);
			}
//			else if ( nextArg.startsWith("-loginId")){
//				if ( !"${loginId}".equals(args[i + 1]))
//					loginId = new String(args[i + 1]);
//			}
			else if ( nextArg.startsWith("-fromDate")){
				if ( !"${fromDate}".equals(args[i + 1]))
					fromDate = new String(args[i + 1]);
			}
			else if ( nextArg.startsWith("-toDate")){
				if ( !"${toDate}".equals(args[i + 1]))
					toDate = new String(args[i + 1]);
			}
			else if ( nextArg.startsWith("-meterId")){
				if ( !"${meterId}".equals(args[i + 1]))
					meterId = new String(args[i + 1]);
			}
			// INSERT START SP-476
			else if ( nextArg.startsWith("-force")){
				if ( !"${force}".equals(args[i + 1]))
					force = new String(args[i + 1]);
			}
			// INSERT END SP-476
			else if ( nextArg.startsWith("-showlist")){
				if ( !"${showlist}".equals(args[i + 1])){
					String show = new String(args[i + 1]);
					if ( "true".equals(show))
						showlist = true;
				}
			}
			
			// SP-892 (2018-03-12)
			else if ( nextArg.startsWith("-useAsyncChannel")){
				if ( !"${useAsyncChannel}".equals(args[i + 1]))
					useAsyncChannel = (args[i+1] == null ? false : Boolean.parseBoolean(args[i+1]));
			}
		}

		dateType = checkDate(fromDate, toDate );

		if ( dateType == null ){
			logger.info("RecollectMeteringSoriaTaskMBB -DdeviceType=Meter|Modem [-DsupplierName=supplierName] [-DfromDate=fromDateYYYYMMDD(hh)] [-DtoDate=toDateYYYYMMDD(hh)] [-DmeterId=meterId]");
			System.exit(1);
		}
		logger.info("RecollectMeteringSoriaTaskMBB . devicetype={} supplierName={} fromDate={} toDate={} meterId={} ",
				deviceType, supplierName,  fromDate, toDate, meterId);

		if (deviceType == null ) {
			deviceType = "Meter";
		}
		if (supplierName == null ){
			supplierName = "";
		}

		try {
			ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "spring-RecollectMeteringSoriaMBB.xml" });
			DataUtil.setApplicationContext(ctx);

			RecollectMeteringSoriaTaskMBB task = (RecollectMeteringSoriaTaskMBB) ctx.getBean(RecollectMeteringSoriaTaskMBB.class);

			task.setSupplierName(supplierName);
			task.setLoginId(loginId);
			task.setFromDate(fromDate);
			task.setToDate(toDate);
			task.setMeterId(meterId);
			task.setDateType(dateType);
			if ( CommonConstants.DeviceType.Modem.name().equals(deviceType)){
				task.setDeviceType(CommonConstants.DeviceType.Modem);
			}
			else if (CommonConstants.DeviceType.Meter.name().equals(deviceType)){
				task.setDeviceType(CommonConstants.DeviceType.Meter);
			}
			else {
				logger.error("Unknown deviceType");
				System.exit(1);
			}

			// INSERT START SP-476
			if (force.equals("true")) {
				task.setForceOption(true);
			}
			else {
				task.setForceOption(false);
			}
			// INSERT END SP-476
			if ( showlist ){
				task.setShowList(true);
			}
			
			task.setUseAsyncChannel(useAsyncChannel);
			
			task.execute(null);

		} catch (Exception e) {
			logger.error("RecollectMeteringSoriaTaskMBB excute error - " + e, e);
		} finally {
			logger.info("#### RecollectMeteringSoriaTaskMBB Task finished - Elapse Time : {} ###", DateTimeUtil.getElapseTimeToString(System.currentTimeMillis() - startTime));
			System.exit(0);
		}
	}

	private static CommonConstants.DateType checkDate(String fromDate, String toDate ){
		CommonConstants.DateType fromDateType = null;
		CommonConstants.DateType toDateType = null;

		CommonConstants.DateType dateType = null;
		try {
			if ( (fromDate == null || "".equals(fromDate)) &&
					(toDate == null || "".equals(toDate))){
				dateType =  CommonConstants.DateType.DAILY;
				return dateType;

			}
			if ( (fromDate == null || "".equals(fromDate)) &&
					!(toDate == null || "".equals(toDate))){
				logger.error("specify -fromDate with -toDate ");
				return null;
			}
			if ( !(fromDate == null || "".equals(fromDate)) &&
					(toDate == null || "".equals(toDate))){
				logger.error("specify -fromDate with -toDate ");
				return null;
			}

			if ( toDate.compareTo(fromDate) < 0 ){
				logger.error("-fromDate is after -toDate");
				return null;
			}

			if ( fromDate.length() == 10 ){
				Date fDate =  DateTimeUtil.getDateFromYYYYMMDDHHMMSS(fromDate+ "0000");
				fromDateType = CommonConstants.DateType.HOURLY;
			}
			else if (fromDate.length() == 8 ){
				Date fDate =  DateTimeUtil.getDateFromYYYYMMDD(fromDate);
				fromDateType = CommonConstants.DateType.DAILY;
			}
			else {
				logger.error( "-fromDate invalid format");
				return null;
			}

			if ( toDate.length() == 10 ){
				Date tDate =  DateTimeUtil.getDateFromYYYYMMDDHHMMSS(toDate+ "0000");
				toDateType = CommonConstants.DateType.HOURLY;
			}
			else if (fromDate.length() == 8 ){
				Date fDate =  DateTimeUtil.getDateFromYYYYMMDD(fromDate);
				toDateType = CommonConstants.DateType.DAILY;
			}
			else {
				logger.error( "-toDate invalid format");
				return null;
			}

			if ( toDateType != null || fromDateType != null ){
				if ( toDateType == CommonConstants.DateType.DAILY &&
						fromDateType == CommonConstants.DateType.DAILY ){
					if ( toDateType.compareTo(fromDateType) < 0 ){
						logger.error("-fromDate is after -toDate");
						return null;
					}
					dateType = CommonConstants.DateType.DAILY ;
					return dateType;
				}
				else if ( toDateType == CommonConstants.DateType.HOURLY &&
						fromDateType == CommonConstants.DateType.HOURLY ){

					dateType = CommonConstants.DateType.HOURLY ;
					return dateType;
				}
				else {
					logger.error( "-toDate invalid format");
					return null;
				}
			}
			return 	null;
		}
		catch ( Exception e){
			logger.error("-fromDate or -toDate is invalid format");
//			logger.info("RecollectMeteringSoriaTaskMBB -DdeviceType=Meter|Modem [-DsupplierName=supplierName] [-DfromDate=fromDateYYYYMMDD] [-DtoDate=toDateYYYYMMDD]");
			return null;
		}
	}
	private void saveOperationLog(Supplier supplier, Code targetTypeCode, String targetName, String userId, Code operationCode, Integer status, String errorReason){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar today = Calendar.getInstance();
        String currDateTime = sdf.format(today.getTime());

		OperationLog log = new OperationLog();

		log.setOperatorType(1);//operator
		log.setOperationCommandCode(operationCode);
		log.setYyyymmdd(currDateTime.substring(0,8));
		log.setHhmmss(currDateTime.substring(8,14));
		log.setYyyymmddhhmmss(currDateTime);
		log.setDescription("");
		log.setErrorReason(errorReason);
		log.setResultSrc("");
		log.setStatus(status);
		log.setTargetName(targetName);
		log.setTargetTypeCode(targetTypeCode);
		log.setUserId(userId);
		log.setSupplier(supplier);
		//logger.debug("operation log: "+log.toString());
		operationLogDao.add(log);
	}

	/* */
	public void restartCommModem( Modem modem ) {

		ResultStatus status = ResultStatus.FAIL;
		Map<String, Object> resultMap = new HashMap<String,Object>();
		Map<String, Object> condition = new HashMap<String, Object>();
		List<String> paramList = new ArrayList<String>();


		try {
			// MBB (MMIU / SMS)
			if (((modem.getModemType() == ModemType.MMIU) && (modem.getProtocolType() == Protocol.SMS))) {
				String commandName = COMMAND_NAME.RESTART_COMM_MODEM.getCmdName();
				String messageType = MESSAGE_TYPE.REQ_ACK.getTypeCode();
				String commandCode = COMMAND_TYPE.RESTART_COMM_MODEM.getTypeCode();
				MMIU mmiuModem = (MMIU) modem;
				String mobliePhNum = mmiuModem.getPhoneNumber();
				String euiId = modem.getDeviceSerial();

				if (mobliePhNum == null || euiId == null) {
					logger.error( "There is no EUI ID/Phone Number.");
					return;
				}

				condition.put("commandName", commandName);
				condition.put("messageType", messageType);
				condition.put("mobliePhNum", mobliePhNum);
				condition.put("euiId", euiId);
				condition.put("commandCode", commandCode);

				resultMap = sendSms(condition, paramList, null);

				if ( resultMap.get("messageType") != null ){
					String response_messageType = resultMap.get("messageType").toString();
					logger.info("Send SMS : messageType[{}]" ,response_messageType );
				}
			}
		} catch (Exception e) {
			logger.error("[" + modem.getDeviceSerial() + "] restartCommModem excute error - " + e,e);
		}

		return;

	}


	public boolean  saveAsyncCommandLog(String modemId, String command, Long trid, Integer cmdState){

		TransactionStatus txstatus = null;

		AsyncCommandLogDao aclDao = null;
		aclDao = DataUtil.getBean(AsyncCommandLogDao.class);
		Set<Condition> condition = null;

		// query Running state
		List<AsyncCommandLog> aclList = null;
		try{
			txstatus = txmanager.getTransaction(transDef);

			//            Long maxTrid = aclDao.getMaxTrId(modem.getDeviceSerial(),command );
			//            if ( maxTrid == null ){
			//            	logger.error("Search AsyncCommandLog failed. modemId={},command={}",
			//            			modem.getDeviceSerial(),command);
			//            	return false;
			//            }
			condition = new HashSet<Condition>();
			condition.add(new Condition("id.mcuId", new Object[] { modemId }, null, Condition.Restriction.EQ));
			condition.add(new Condition("id.trId", new Object[] { trid }, null, Condition.Restriction.EQ));
			aclList = aclDao.findByConditions(condition);

			logger.debug("AsyncCommandLog List size = " + aclList.size());
			if (aclList.size() == 1) {
				AsyncCommandLog log = aclList.get(0);
				log.setState(cmdState);
				aclDao.update(log);
			}
			logger.info("Set AsyncCommmandLog State : MCUID[{}] TRID[{}] STATE[{}]" ,modemId,trid,cmdState);
			txmanager.commit(txstatus);
			return true;
		}catch (NullPointerException e){
			logger.error("FindByConditions produce -" + e, e);
			if(txstatus != null)
				txmanager.rollback(txstatus);
			return false;
		}catch (Exception ec){
			logger.error("txmanager produce -" + ec, ec);
			if(txstatus != null)
				txmanager.rollback(txstatus);
			return false;
		}
	}
	
	
	private void executeUseAsyncMode(boolean bAutoRecovery) throws Exception {
		List<Object> gaps = null; 
		if(forceOption) {
			if(meterId == null || meterId.equals("")) {
				throw new Exception("Invalid parameter error. If using 'FORCE=true'. Execute parameter need set 'METER_ID=xxxxxx' option.");
			}
			gaps = new ArrayList<Object>();
			Map<String, Object> obj = new HashMap<String, Object>();
			obj.put("mdsId", meterId);
			gaps.add(obj);
			logger.debug("Forced option is specified.");			
		}else {
			gaps = getGaps(fromDate, toDate);
		}

		logger.info("Total Meter to need recollect metering ["+ gaps.size() + "]");
		
		List<Map<String, Object>> asyncTargetList = null;
		if(gaps != null && gaps.size() > 0){
			if ( !deviceType.equals(DeviceType.Meter) && bAutoRecovery ){
				toDate = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss").substring(0,8);
			}
			logger.info("RecollectMeteringSoriaTaskMBB . deviceType:" + deviceType +",fromDate=" + fromDate + ",toDate=" + toDate + ", UseAsyncChannel = " + useAsyncChannel);

			String commandName = null;
			String commandCode = SMSConstants.COMMAND_TYPE.NI.getTypeCode();
			
			if (dateType == CommonConstants.DateType.DAILY ){
				fromDate = fromDate+"000000";
				toDate = toDate+"235959";
			} else{
				fromDate = fromDate+"0000";
				toDate= toDate+"5959";
			}
			
			asyncTargetList = new ArrayList<>();
			List<String> invalidTargetListInfo = new ArrayList<>();
			for (Object obj : gaps) {
				@SuppressWarnings("unchecked")
				HashMap<String,Object> targetMap = (HashMap<String, Object>) ((HashMap<String, Object>) obj).clone();
				
				String meterId =  (String) targetMap.get("mdsId");
				String deviceSerial = (targetMap.get("deviceSerial") != null ? (String) targetMap.get("deviceSerial") : null);
				String modemType = (targetMap.get("modemType") != null ? (String) targetMap.get("modemType") : null);
				String protocolType = (targetMap.get("protocolType") != null ? (String) targetMap.get("protocolType") : null);
				
				if(deviceSerial != null && modemType.equals(ModemType.MMIU.name()) && protocolType.equals(Protocol.SMS.name())) {
					Map<String,String> paramMap = null;
	                
        			if ( deviceType == CommonConstants.DeviceType.Meter){
        				commandName = "cmdMeterParamGet";
        				
        				Meter meter = meterDao.get(meterId);
        				int modemPort = 0;
        				if ( meter.getModemPort() != null ){
        					modemPort = meter.getModemPort().intValue();
        				}
        				if ( modemPort > 5){
        					logger.error("[{}] ModemPort: {} is not Support", meterId, modemPort);
        					throw new Exception("ModemPort:" + modemPort + " is not Support");
        				}
        				
        				paramMap = getOnDemandMeterBypassMBBParam(meterId, modemPort, fromDate, toDate);
        				paramMap.put("CommandCode", commandCode);
        			}
        			else if ( deviceType == CommonConstants.DeviceType.Modem){
        				commandName = "cmdGetROMRead";
        				
        				paramMap = new HashMap<String,String>();
        				paramMap.put("CommandCode", commandCode);
        				paramMap.put("fromDate", fromDate);
        				paramMap.put("toDate", toDate);
        				paramMap.put("meterId", meterId);
        			}
        			
    				targetMap.put("asycParams", paramMap);    				
					asyncTargetList.add(targetMap);
				}else {
					invalidTargetListInfo.add(targetMap.toString());
				}
			}
			
			logger.debug("########################## Target Size = " + asyncTargetList.size() + " #########");
			int count = 0;
			for(Map<String,Object> targetMap : asyncTargetList) {
				logger.debug(++count + "/" + asyncTargetList.size() + ". Target Info = " + targetMap.toString());
			}
			
			logger.debug("");
			logger.debug("########################## Invalid Target Size = " + invalidTargetListInfo.size() + " #########");
			count = 0;
			for(String iTarget : invalidTargetListInfo) {
				logger.debug(++count + "/" + invalidTargetListInfo.size() + ". Target Info = " + iTarget.toString());
			}
			
			if (!showList && asyncTargetList != null && 0 < asyncTargetList.size() && commandName != null){
				saveAsyncCommandByUseAsyncOption(asyncTargetList, commandName);	
			}
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
	
	
}
