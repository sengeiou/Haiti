package com.aimir.schedule.task;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.constants.CommonConstants.TR_STATE;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.AsyncCommandLogDao;
import com.aimir.dao.device.AsyncCommandResultDao;
import com.aimir.dao.device.EventAlertDao;
import com.aimir.dao.device.EventAlertLogDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MMIUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.device.OperationLogDao;
import com.aimir.dao.device.SubGigaDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.OperatorDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.command.ws.client.CommandWS;
import com.aimir.fep.command.ws.client.Exception_Exception;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.DLMS_CLASS;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.DLMS_CLASS_ATTR;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.OBIS;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.NIAttributeId;
import com.aimir.fep.protocol.smsp.SMSConstants;
import com.aimir.fep.protocol.smsp.SMSConstants.COMMAND_TYPE;
import com.aimir.fep.protocol.smsp.command.frame.sms.ResponseFrame;
import com.aimir.fep.tool.SetAlarmEventOnOff;
import com.aimir.fep.tool.SetAlarmEventOnOff.McuDeviceList;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.AsyncCommandResult;
import com.aimir.model.device.EventAlert;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.model.device.MMIU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.device.NetworkInfoLog;
import com.aimir.model.device.OperationLog;
import com.aimir.model.device.SubGiga;
import com.aimir.model.system.Code;
import com.aimir.model.system.Operator;
import com.aimir.model.system.Role;
import com.aimir.model.system.Supplier;
import com.aimir.schedule.command.CmdManager;
import com.aimir.schedule.command.CmdOperationUtil;
import com.aimir.schedule.util.CommonUtil;
import com.aimir.util.Condition;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.Condition.Restriction;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Create 2016/12/16 SP-414
 *
 */
@Service
public class GetProblematicMeterListTask extends ScheduleTask 
{
	@Resource(name = "transactionManager")
	HibernateTransactionManager txmanager;    
    
    @Autowired
    MCUDao mcuDao;
    
    @Autowired
    ModemDao modemDao;
    
    @Autowired
    MeterDao meterDao;
    
    @Autowired
    SubGigaDao subGigaDao;

    @Autowired
    SupplierDao supplierDao;

    @Autowired
	EventAlertDao eventAlertDao;
    
    @Autowired
    EventAlertLogDao eventAlertLogDao;
    
    private static Log log = LogFactory.getLog(GetProblematicMeterListTask.class);

    private String 		supplierName;
    int		supplierId;     

    private static int			_testMode = 0;
    private static int			_pingOff = 0;
    private static int  		_maxThreadWorker = 10;
    private static long 		_timeout = 10800;
    
    private static int			_targetDays = 3;
    private static int			_lpFlag = 1;
    private static int			_eventPeriod = 30;
    private static String		_location = "";
    private static String		_msa = "";

    private static int			packetSize = 64;
    private static int			pingCount = 3;
    
	private static String regexIPv4 = "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$";
	private static String regexIPv6 = "^(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]).){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]).){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))$";
	private static String regexIPv4andIPv6 = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)|(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]).){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]).){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))$";

	
	Map<String, String> pingResult = new HashMap<String, String>();
	
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"spring-schedule-task.xml"}); 
        DataUtil.setApplicationContext(ctx);

        log.info("======================== GetProblematicMeterListTask start. ========================");        
		log.info("ARG_0[" + args[0] + "] ARG_1[" + args[1] + "] ARG_2[" + args[2] + "] ARG_3[" + args[3] + 
				"] ARG_4[" + args[4] +"] ARG_5[" + args[5] +"] ARG_6[" + args[6] +  "]");
    	        
        // TARGETDAYS
        if (args[0].length() > 0) {
        	_targetDays = Integer.parseInt(args[0]);   
        }
        // LPFLAG (0:Including missing meters, 1:Only lp count 0)
        if (args[1].length() > 0) {
        	_lpFlag = Integer.parseInt(args[1]);  
        }
        // EVENTPERIOD
        if (args[2].length() > 0) {
        	_eventPeriod = Integer.parseInt(args[2]);
        }        
        // LOCATION (location name)
        if (args[3].length() > 0) {
        	_location = args[3]; 
        }
        // MSA
        if (args[4].length() > 0) {
        	_msa = args[4];     
        }
        
        // MAX_THREAD_WORKER
        if (args[5].length() > 0) {
            _maxThreadWorker = Integer.parseInt(args[5]);        	
        }
        // TIMEOUT
        if (args[6].length() > 0) {
            _timeout = Integer.parseInt(args[6]);        	
        }
        // TESTMODE
        if (args[7].length() > 0) {
        	_testMode = Integer.parseInt(args[7]);        	
        }     
        //PINGOFF
        if (args[8].length() > 0) {
        	_pingOff = Integer.parseInt(args[8]);        	
        }     
        
        
        GetProblematicMeterListTask task = ctx.getBean(GetProblematicMeterListTask.class);
        task.execute(null);
        log.info("======================== GetProblematicMeterListTask end. ========================");
        System.exit(0);
    }    

    private List<Object> getTargetList(String fromDate, String toDate)
	{
        TransactionStatus txstatus = null;	
		List<Object> targetList  = null;
		List<Object> resultList  = new ArrayList<Object>();

    	try {
    		log.debug("getTargetList() Start. fromDate[" + fromDate + "],toDate[" + toDate + "]" ); 
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
				log.info("Default Supplier={" + supplier.getName() + "}");
			}

    		Map<String, Object> params = new HashMap<String, Object>();  
    		params.put("channel", 1);
    		params.put("searchStartDate",fromDate);
    		params.put("searchEndDate", toDate);
    		params.put("meterType", MeterType.EnergyMeter.name());
    		params.put("supplierId", supplierId);
    		params.put("locationName", _location);
    		params.put("msa", _msa);
	    		
    		targetList = meterDao.getProblematicMeters(params);			
		
    		log.debug("getProblematicMeters() count [" + targetList.size() + "]");

    		int count = 0;
    		for(Object obj : targetList) {
    			HashMap<String, Object> map = new HashMap<String, Object>((HashMap<String, Object>)obj);
    			String deviceSerial = (String) map.get("deviceSerial");
    			if (_lpFlag == 1) {
    				int lpCount = ((Number)map.get("lpcount")).intValue();
    				if (lpCount > 0) {
    					log.debug(deviceSerial + " lp count [" + lpCount + "] remove from list.");
    					continue;
    				}
    			}
    			
    			log.debug("-------" + deviceSerial + "-------");
    			Modem modem = modemDao.get(deviceSerial);
    			if (modem == null) {
    				map.put("Modem_class", modem);    				
    			}
    			else {
	    			if (modem.getModemType() == ModemType.SubGiga) {
	    				if (modem instanceof SubGiga == true) {
	    					SubGiga subGigaModem = (SubGiga) modem;
	    					map.put("Modem_class", subGigaModem);
	    					log.debug("SubGiga cast Success.");	    					
	    				}
	    				else {
	    					log.debug("Modemtype SubGiga but different class.");
	    					if (modem instanceof Modem == true) {
		    					log.debug("instanceof Modem is true.");	    					
	    					}
	    					SubGiga subGigaModem = subGigaDao.get(deviceSerial);
		    				if (subGigaModem instanceof SubGiga == true) {
		    					log.debug("SubGigaDao.get Success.");
		    					map.put("Modem_class", subGigaModem);
		    				}
		    				else {
		    					log.debug("SubGigaDao.get Fail.");
		    					map.put("Modem_class", modem);
		    				}
	    				}
	    			}
	    			else if (modem.getModemType() == ModemType.MMIU) {
	    				if (modem instanceof MMIU == true) {
	    					MMIU mmiuModem = (MMIU) modem;
	    					map.put("Modem_class", mmiuModem);
	    					log.debug("MMIU cast Success.");	    					
	    				}
	    				else {
	    					log.debug("Modemtype MMIU but different class.");
	        				map.put("Modem_class", modem);
	    				}
	    				
	    			}
	    			else {
	    				map.put("Modem_class", modem);
	    			}
    			}
    			resultList.add(map);
    			
    			if (_testMode > 0) {
    				count++;
    				if (count >= _testMode) break;
    			}
    		}

    		txmanager.commit(txstatus);
    	}
    	catch  (Exception e) {
    		log.error("getTargetList error - " + e, e);
    		if (txstatus != null&& !txstatus.isCompleted())
    			txmanager.rollback(txstatus);
    	}
		log.debug("list count [" + resultList.size() + "]"); 
		log.debug("getTargetList() End."); 
    	return resultList;
	}    

    private Map<String, Object> getEventList(String fromDate, String toDate, String message)
    {
        TransactionStatus txstatus = null;	
		List<Map<String, Object>> resultList  = new ArrayList<Map<String, Object>>();
		Map<String, Object> resultMap = new HashMap<String, Object>();

    	try {
    		log.debug("getEventList() Start. fromDate[" + fromDate + "],toDate[" + toDate + "],message[" +message + "]"); 
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
				log.info("Default Supplier={" + supplier.getName() + "}");
			}

    		Map<String, Object> params = new HashMap<String, Object>();  
    		params.put("supplierId", supplierId);
    		params.put("locationName", _location);
    		params.put("searchStartDate",fromDate);
    		params.put("searchEndDate", toDate);
    		params.put("message", message);
	    		
    		resultList = eventAlertLogDao.getProblematicMetersEvent(params);
    		log.debug("getProblematicMetersEvent() count [" + resultList.size() + "]");
    		for (Map<String, Object> eventMap : resultList) {
        		resultMap.put((String) eventMap.get("activatorId"), eventMap);
    		}
    		
    		txmanager.commit(txstatus);    	
    	}
    	catch  (Exception e) {
    		log.error("getEventList error - " + e, e);
    		if (txstatus != null&& !txstatus.isCompleted())
    			txmanager.rollback(txstatus);
    	}
    	
    	return resultMap;
    }    
    
    @Override
    public void execute(JobExecutionContext context) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            Calendar today = Calendar.getInstance();
            String currDateTime = sdf.format(today.getTime());
            
            String		fromDate;
            String		toDate;
            
			Map<String, String> TFBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, (-1*(24*_targetDays)));
			String TFDate = TFBeforeHour.get("date").replace("-", "") + TFBeforeHour.get("time").replace(".", "");	
			fromDate = TFDate.substring(0,8);
			TFBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -24);
			TFDate = TFBeforeHour.get("date").replace("-", "") + TFBeforeHour.get("time").replace(".", "");		
			toDate = TFDate.substring(0,8);

			log.debug("########### Meter List ###########");
			List<Object> targetList = getTargetList(fromDate, toDate);

			// Event
			TFBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, (-1*(24*(_eventPeriod-1))));
			TFDate = TFBeforeHour.get("date").replace("-", "") + TFBeforeHour.get("time").replace(".", "");	
			fromDate = TFDate.substring(0,8);
			TFBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, 0);
			TFDate = TFBeforeHour.get("date").replace("-", "") + TFBeforeHour.get("time").replace(".", "");		
			toDate = TFDate.substring(0,8);

			log.debug("########### Meter No Response List ###########");
			Map<String, Object> eventMap1 = getEventList(fromDate, toDate, "Meter No Response");	// activatorId = DEVICE_SERIAL
			log.debug("########### Power Restore List ###########");
			Map<String, Object> eventMap2 = getEventList(fromDate, toDate, "Power Restore");		// activatorId = MDS_ID
			log.debug("########### 0F40000 List ###########");
			Map<String, Object> eventMap3 = getEventList(fromDate, toDate, "0F40000");				// activatorId = DEVICE_SERIAL
			log.debug("########### Join Modem List ###########");
			Map<String, Object> eventMap4 = getEventList(fromDate, toDate, "Join Modem");			// activatorId = DEVICE_SERIAL
			// SP-818
			log.debug("########### Metering Fail for HLS ###########");
			Map<String, Object> eventMap5 = getEventList(fromDate, toDate, "Metering Fail for HLS");

			if (_pingOff == 0) {
				// Ping
				log.debug("########### Ping thread Start. ###########");
	
				// Initialize
	    		pingResult.clear();
	        	
	            ExecutorService pool = Executors.newFixedThreadPool(_maxThreadWorker);
	            ExecPingThread threads[] = new ExecPingThread[targetList.size()];
	        	int	i = 0;
	    		int cnt = 0;
	        
	        	for (Object obj : targetList) {
	        		HashMap<String,Object> map = (HashMap<String, Object>) obj;   			
	        		if ((Modem) map.get("Modem_class") == null) {
	        			log.debug("modem is null. skip ping. Meter[" + (String)map.get("mdsId") + "]");
	        			continue;
	        		}
	
	        		threads[i] = new ExecPingThread(
	        				(Modem) map.get("Modem_class")
	        				);
	        		pool.execute(threads[i]);
	        		i++;
	        	}
	
	            log.info("ExecutorService shutdown.");
	            pool.shutdown();
	            log.info("ExecutorService awaitTermination. [" + _timeout + "]sec");
	            pool.awaitTermination(_timeout, TimeUnit.SECONDS);
	
				log.debug("########### Ping thread End. ###########");
			}
			
			// make csv file
            List<Map<String, Object>> outputList = new ArrayList<Map<String, Object>>();
        	for (Object obj : targetList) {
        		HashMap<String,Object> map = (HashMap<String, Object>) obj;   			
        		String deviceSerial = (String)map.get("deviceSerial");
        		String mdsId = (String)map.get("mdsId");
        		// Meter No Response
        		if (eventMap1.containsKey(deviceSerial)) {
        			HashMap<String,Object> event = (HashMap<String, Object>)eventMap1.get(deviceSerial);
        			map.put("MeterNoResponse", (Number)event.get("idCount"));
        			map.put("MeterNoResponse_LastDate", (String)event.get("lastDate"));
        		}
        		else {
        			map.put("MeterNoResponse", 0);
        			map.put("MeterNoResponse_LastDate", "");
        		}
        		// Power Restore
        		if (eventMap2.containsKey(mdsId)) {
        			HashMap<String,Object> event = (HashMap<String, Object>)eventMap2.get(mdsId);
        			map.put("PowerRestore", (Number)event.get("idCount"));
        			map.put("PowerRestore_LastDate", (String)event.get("lastDate"));
        		}
        		else {
        			map.put("PowerRestore", 0);
        			map.put("PowerRestore_LastDate", "");
        		}
        		// Key Issue
        		if (eventMap3.containsKey(deviceSerial)) {
        			HashMap<String,Object> event = (HashMap<String, Object>)eventMap3.get(deviceSerial);
        			map.put("KeyIssue", (Number)event.get("idCount"));
        			map.put("KeyIssue_LastDate", (String)event.get("lastDate"));
        		}
        		else {
        			map.put("KeyIssue", 0);
        			map.put("KeyIssue_LastDate", "");
        		}        		
        		// Join Modem
        		if (eventMap4.containsKey(deviceSerial)) {
        			HashMap<String,Object> event = (HashMap<String, Object>)eventMap4.get(deviceSerial);
        			map.put("JoinModem", (Number)event.get("idCount"));
        			map.put("JoinModem_LastDate", (String)event.get("lastDate"));
        		}
        		else {
        			map.put("JoinModem", 0);
        			map.put("JoinModem_LastDate", "");
        		}        		            
    			// SP-818
        		// Metering Fail for HLS
        		if (eventMap5.containsKey(deviceSerial)) {
        			HashMap<String,Object> event = (HashMap<String, Object>)eventMap5.get(deviceSerial);
        			map.put("MeteringFailforHLS", (Number)event.get("idCount"));
        			map.put("MeteringFailforHLS_LastDate", (String)event.get("lastDate"));
        		}
        		else {
        			map.put("MeteringFailforHLS", 0);
        			map.put("MeteringFailforHLS_LastDate", "");
        		}
        		
        		// Ping Result
        		if (pingResult.containsKey(deviceSerial)) {
        			String res = pingResult.get(deviceSerial);
        			map.put("Ping_Result", res);
        		}
        		else {
        			map.put("Ping_Result", "");
        		}
        		
        		outputList.add(map);
        	}
			log.debug("########### Output csv file. ###########");
            makeResultFile(outputList, currDateTime, _location, _msa);
        }
        catch (Exception ee) {
        	log.debug(ee.getMessage());
        }
        finally {
        }
    }

	synchronized private void makeResultFile(List<Map<String, Object>> outputList, String dateTime, String location, String msa){
		String fileName = dateTime + ".csv";

		if (location.length() == 0) {
			fileName = "ALLDSO_" + dateTime + ".csv";
		}
		else if ((location.length() > 0) && (msa.length() > 0)) {
			fileName = location + "_" + msa + "_" + dateTime + ".csv";
		}
		else {
			fileName = location + "_" + dateTime + ".csv";			
		}
		
		//		String fileName = "C:\\SORIA\\3.3\\aimir-schedule\\target\\classes\\problematic\\" + location + "_" + dateTime + ".csv";
        try{
        	log.debug(fileName);
        	File file = new File(fileName) ;
        	file.createNewFile();
        	FileOutputStream fos = new FileOutputStream(fileName);
        	OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
        	BufferedWriter bw = new BufferedWriter(osw);
        	
        	StringBuffer buffer = new StringBuffer();
        	// Header
        	buffer.append("LOCATION,MSA,MDS_ID,GS1,DEVICE_SERIAL,DCU_SYS_ID,GPIOY,GPIOX,MODEM_TYPE,FW_VER,LAST_LINK_TIME,COMM_STATE,COLLECT_CNT,");
        	buffer.append("PING_RESULT,");
        	buffer.append("METER_NO_RESPONSE,METER_NO_RESPONSE_LASTDATE,");
        	buffer.append("POWER_RESTORE,POWER_RESTORE_LASTDATE,");
        	buffer.append("KEY_ISSUE,KEY_ISSUE_LASTDATE,");
        	buffer.append("JOIN_MODEM,JOIN_MODEM_LASTDATE,");
        	buffer.append("METERING_FAIL_FOR_HLS,METERING_FAIL_FOR_HLS_LASTDATE");

        	bw.write(buffer.toString());
        	bw.newLine();
        
        	// Data
        	for (Map<String, Object> map : outputList) {
        		StringBuffer dataBuffer = new StringBuffer();

        		dataBuffer.append(Optional.ofNullable((String)map.get("location")).orElse("") + ",");
        		dataBuffer.append(Optional.ofNullable((String)map.get("msa")).orElse("") + ",");
        		dataBuffer.append(Optional.ofNullable((String)map.get("mdsId")).orElse("") + ",");
        		dataBuffer.append(Optional.ofNullable((String)map.get("gs1")).orElse("") + ",");
        		dataBuffer.append(Optional.ofNullable((String)map.get("deviceSerial")).orElse("") + ",");
        		dataBuffer.append(Optional.ofNullable((String)map.get("sysId")).orElse("") + ",");
        		if (map.get("gpioy") == null) {
        			dataBuffer.append(",");
        		} else {
        			dataBuffer.append((Number)map.get("gpioy") + ",");
        		}
        		if (map.get("gpiox") == null) {
        			dataBuffer.append(",");
        		} else {
        			dataBuffer.append((Number)map.get("gpiox") + ",");
        		}
        		dataBuffer.append(Optional.ofNullable((String)map.get("modem")).orElse("") + ",");
        		dataBuffer.append(Optional.ofNullable((String)map.get("fw_ver")).orElse("") + ",");
        		dataBuffer.append(Optional.ofNullable((String)map.get("last_link_time")).orElse("") + ",");
        		dataBuffer.append(Optional.ofNullable((Number)map.get("comm_state")).orElse(0) + ",");
        		dataBuffer.append(Optional.ofNullable((Number)map.get("lpcount")).orElse(0) + ",");
        		dataBuffer.append(Optional.ofNullable((String)map.get("Ping_Result")).orElse("") + ",");
        		dataBuffer.append(Optional.ofNullable((Number)map.get("MeterNoResponse")).orElse(0) + ",");
        		dataBuffer.append(Optional.ofNullable((String)map.get("MeterNoResponse_LastDate")).orElse("") + ",");
        		dataBuffer.append(Optional.ofNullable((Number)map.get("PowerRestore")).orElse(0) + ",");
        		dataBuffer.append(Optional.ofNullable((String)map.get("PowerRestore_LastDate")).orElse("") + ",");
        		dataBuffer.append(Optional.ofNullable((Number)map.get("KeyIssue")).orElse(0) + ",");
        		dataBuffer.append(Optional.ofNullable((String)map.get("KeyIssue_LastDate")).orElse("") + ",");
        		dataBuffer.append(Optional.ofNullable((Number)map.get("JoinModem")).orElse(0) + ",");
        		dataBuffer.append(Optional.ofNullable((String)map.get("JoinModem_LastDate")).orElse("") + ",");
        		dataBuffer.append(Optional.ofNullable((Number)map.get("MeteringFailforHLS")).orElse(0) + ",");
        		dataBuffer.append(Optional.ofNullable((String)map.get("MeteringFailforHLS_LastDate")).orElse(""));
        		
                bw.write(dataBuffer.toString());
            	bw.newLine();

        	}
        	
        	bw.close();        	
        }catch(Exception e){
            log.error("makeResultFile error - " + e, e);
        }
	}

	synchronized private void setPingResult(String deviceSerial, String result){
		pingResult.put(deviceSerial, result);		
	}
	
    protected class ExecPingThread extends Thread {
    	Modem modem;
    	String deviceSerial;
    	public ExecPingThread(Modem modem) {
    		try {
	    		this.modem = modem;
	    		this.deviceSerial = modem.getDeviceSerial();
            }
            catch (Exception ee) {}
    	}
    	public void run() {
        	log.info("ThreadID[" + Thread.currentThread().getId() + "] ExecPingThread thread start. DeviceSerial[" + deviceSerial + "]");

			try {

				String result=execPing(modem);
				setPingResult(deviceSerial, result);
	    		   	
        	} catch (Exception e){
        		log.info("ThreadID[" + Thread.currentThread().getId() + "] ExecPingThread thread end. DeviceSerial[" + deviceSerial + "] is failed.");
        	}	
    	}
    }        	
	
	
	private String execPing(Modem modem) {
		String cmdResult = "";
		String result = "FAIL";

		try {
			cmdResult = doModemPing(modem, packetSize, pingCount);
		} catch (Exception e) {
			log.error(e, e);
		}
		
		log.debug(cmdResult);

		if (cmdResult == null ||
			cmdResult.equals("FAIL") ||
			cmdResult.equals("NOT-SUPPORT") ||
			cmdResult.equals("NO-IP") ||
			cmdResult.equals("LOSS-ALL") ||
			cmdResult.isEmpty()
			) {
			result = "FAIL";
		} else {
			result = "SUCCESS";
		}
		
		return result;
	}

    private String checkPingResult(int packetSize, int count,  String ipAddress, String cmdResult) throws Exception {
    	NetworkInfoLog nlog = new NetworkInfoLog();
    	
    	if(cmdResult.equals("FAIL")) {
    		return "FAIL";
    	}
    	
    	// 결과 분석 영역(S)
		double avgRtt = 0.0;
		double loss = 0.0;
		double avgTtl = 0.0;
		int timeOut_count = 0;
		int timeOutCheckCount = 0;
		int timeOutIndex = -1;
		double defaultNum = 0.0;
		String defaultText = "non";
		String timeOut = "Request timed out.";
		String lostAll = "100% loss";
		String lostAllLinux = "100% packet loss";
		String hostUnreachable = "unreachable";
		String hostError = "not find host";
		String hostErrorLinux = "unknown";
		
		// IP 유효 검사
		if (cmdResult.contains(hostError) || cmdResult.contains(hostUnreachable) || cmdResult.contains(hostErrorLinux)) {
			return "FAIL";
		}
		
		if (cmdResult.contains(lostAllLinux)) {
			loss = 100.0;
			avgTtl = defaultNum;
			avgRtt = defaultNum;
			packetSize = 0;
		} else {

			// 평균 RTT
			String[] splitDiagonal = cmdResult.split("/");
			String[] splitAvgRTT = splitDiagonal[4].split("=");
			String avg_Rtt = splitAvgRTT[0];
			avgRtt = Double.parseDouble(avg_Rtt);

			// 손실
			String[] splitComma = cmdResult.split(",");
			String str_loss = splitComma[2].replaceAll("[^0-9]", "");
			loss = Double.parseDouble(str_loss);

			// 평균 TTL
			String[] splitTtlPattern = cmdResult.split("ttl=");
			String[] splitTtl = null;
			String avg_Ttl = "";
			String str_Ttl = "";
			int int_Ttl = 0;
			int sum_Ttl = 0;

			Pattern pattern = Pattern.compile(timeOut);
			Matcher matcher = pattern.matcher(cmdResult);

			for (int i = 0; matcher.find(i); i = matcher.end()) {
				timeOut_count++;
			}

			for (int i = 1; i < splitTtlPattern.length; i++) {
				splitTtl = splitTtlPattern[i].split("=");
				str_Ttl = splitTtl[0].replaceAll("[^0-9]", "");
				int_Ttl = Integer.parseInt(str_Ttl);

				sum_Ttl += int_Ttl;
			}

			avgTtl = sum_Ttl / (count - timeOut_count);
		}
		// 결과 분석 영역(E)
		
		nlog.setTargetNode(defaultText);
		nlog.setCommand("ICMP");
		nlog.setDateTime(DateTimeUtil.getDateString(new Date()));
		nlog.setTemperature(defaultNum);
		nlog.setWeather(defaultText);
		nlog.setLoss(loss);
		nlog.setTtl(avgTtl);
		nlog.setRtt(avgRtt);
		nlog.setPacketSize(packetSize);
		nlog.setIpAddr(ipAddress);

		log.info(nlog.toJSONString());
		log.debug("IP[" + ipAddress + "], PACKET_SIZE[" + packetSize + "] COUNT[" + count + "] " + "LOSS[" + loss + "] AVG_TTL[" + avgTtl + "] AVG_RTT[" + avgRtt + "]");
		
		if (nlog.getLoss() == 100) { 
			return "FAIL";
		}
		
		return cmdResult;
    }
    
	private String doModemPing(Modem modem, int packetSize, int count) throws Exception {
		
    	//NetworkInfoLog nlog = new NetworkInfoLog();
        String cmdResult = "";
        String ipv4 = "";
		String ipv6 = "";
		String statusNetwork = "";
        String statusNetworkForIpv4 = "";
		String statusNetworkForIpv6 = "";

		try {
			if (modem.getModemType() == null) {
				log.debug("Modem Type is null.");
				return "NOT-SUPPORT";			
			}
			
			if (modem.getModemType() == ModemType.SubGiga) { // Modem Type이 SubGiga일 경우
				SubGiga subGigaModem = (SubGiga) modem;
				ipv4 = subGigaModem.getIpAddr();
				ipv6 = subGigaModem.getIpv6Address();
				log.debug("Subgiga cast success. [" + modem.getDeviceSerial() + "]");
			} else if ((modem.getModemType() == ModemType.MMIU)) { // Modem Type이 MMIU일 경우
				MMIU mmiuModem = (MMIU) modem;
				ipv4 = mmiuModem.getIpAddr();
				ipv6 = mmiuModem.getIpv6Address();
				
				if (modem.getProtocolType() == Protocol.SMS) {
					// MMIU-SMS인데 IP주소가 없는 경우, 에러처리
					// MMIU-SMS인데 IP주소가 있는 경우, 해당 IP로 명령 실행
					if (ipv4 == null && ipv6 == null) {
						return "NOT-SUPPORT";
					}
				}
			}
		} catch (Exception e) {
			log.debug("Exception!! [" + modem.getDeviceSerial() + "]");
			log.error(e, e);
		}
		
		// IPv4,IPv6 주소값 모두 null일 경우
		if(ipv4 == null && ipv6 == null) { 
			return "NO-IP";	
		}
		
		if (ipv6 == null) { // CASE 1: IPv4 주소값만 들어있을 경우
			cmdResult = cmdLinuxPing( packetSize, count, ipv4);
			statusNetwork = checkPingResult(packetSize, count, ipv4, cmdResult);
		} else if (ipv4 == null) { // CASE 2: IPv6 주소값만 들어있을 경우
			cmdResult = cmdLinuxPing(packetSize, count, ipv6);
			statusNetwork = checkPingResult(packetSize, count, ipv6, cmdResult);
		} else if(ipv4 != null && ipv6 != null) { // CASE 3: IPv4,IPv6 주소값 모두 들어있을 경우
			String ipv4_pingResult = "";
			String ipv6_pingResult = "";
			
			// IPv4 Logic
			ipv4_pingResult = cmdLinuxPing(packetSize, count, ipv4);
			statusNetworkForIpv4 = checkPingResult(packetSize, count, ipv4, ipv4_pingResult);
			
			// IPv6 Logic
			ipv6_pingResult = cmdLinuxPing( packetSize, count, ipv6); 
			statusNetworkForIpv6 = checkPingResult(packetSize, count, ipv6, ipv6_pingResult);
			
			cmdResult = "[IPv4]\n" + ipv4_pingResult + "\n\n" + "[IPv6]\n" + ipv6_pingResult;
		} else {
			return "FAIL";
		}
		
		if(statusNetwork == "FAIL" || statusNetworkForIpv4 == "FAIL" || statusNetworkForIpv6 == "FAIL") {
        	return "LOSS-ALL";
        }		
		
		return cmdResult;
	}	

	private String cmdLinuxPing(int packet_size, int count, String ipAddress) throws Exception_Exception {
    	Pattern pattern;
        List<String> commands = new ArrayList<String>();
        String packetSize = Integer.toString(packet_size);
        String count_ping = Integer.toString(count);
        
        
        pattern = Pattern.compile(regexIPv4andIPv6);
        if(ipAddress == null || pattern.matcher(ipAddress).matches() == false){
        	return "FAIL";
        	
        } else {
        	// IPv4
        	pattern = Pattern.compile(regexIPv4);
    		if(pattern.matcher(ipAddress).matches() == true){
    			commands.add("ping");
                commands.add("-c");
                commands.add(count_ping);
                commands.add("-s");
                commands.add(packetSize);
                commands.add(ipAddress);
    		}
    		
            // IPv6
    		pattern = Pattern.compile(regexIPv6);
    		if(pattern.matcher(ipAddress).matches() == true){
    			commands.add("ping6");
                commands.add("-c");
                commands.add(count_ping);
                commands.add("-s");
                commands.add(packetSize);
                commands.add(ipAddress);
    		}
        }
        
    	return icmpPing(commands);
    }
	
	private String icmpPing(List<String> commands) {
		
		//NetworkInfoLog nlog = new NetworkInfoLog();
		String str = "";
    	String cmdResult = "";
		
		try {
			ProcessBuilder pb = new ProcessBuilder(commands);
			Process process = pb.start();
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
	        
			while ((str = stdInput.readLine()) != null) {
				cmdResult += str + "\n";
			}

			if (cmdResult == "") {
				log.debug("Network is unreachable");
				return "FAIL";
			}
		} catch (Exception e) {
			log.error("Exception occurred in icmpPing : " + e,e);
		}
		
		log.info(cmdResult);
		return cmdResult;
	}
}
