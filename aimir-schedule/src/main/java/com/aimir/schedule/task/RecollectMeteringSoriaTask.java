package com.aimir.schedule.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.constants.CommonConstants.TR_STATE;
import com.aimir.dao.device.AsyncCommandLogDao;
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
import com.aimir.fep.protocol.smsp.SMSConstants.COMMAND_TYPE;
import com.aimir.fep.protocol.smsp.command.frame.sms.ResponseFrame;
//import com.aimir.fep.schedule.task.MeterTimeSyncSoriaTask.McuModemList;
//import com.aimir.fep.schedule.task.MeterTimeSyncSoriaTask.mcuSyncTimeThread;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.AsyncCommandResult;
import com.aimir.model.device.MCU;
import com.aimir.model.device.MMIU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.device.OperationLog;
import com.aimir.model.system.Code;
import com.aimir.model.system.Operator;
import com.aimir.model.system.Role;
import com.aimir.model.system.Supplier;
import com.aimir.schedule.command.CmdOperationUtil;
import com.aimir.schedule.util.CommonUtil;
import com.aimir.util.DateTimeUtil;

import net.sf.json.JSONArray;

/**
 * Create 2016/12/16 SP-414
 *
 */
@Service
public class RecollectMeteringSoriaTask extends ScheduleTask 
{
	private static Logger logger = LoggerFactory.getLogger(RecollectMeteringSoriaTask.class);
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
    // INSERT START SP-476
    private boolean 	forceOption;
    private boolean		_isUseIF4Command = true;
    private int			_beforeTime = 24;
    // INSERT START SP-476    
    private String		mbbTypeWithMCU = "Modem";	    // INSERT SP-993
    
    // INSERT START SP-1051
    private String		dso;
    private boolean     _enableSMSRes = false;
    private String		excludeType="";
	// INSERT END SP-1051
    
    private String		targetSLA = ""; //SP-1075 0:targetsla30=0, 1:targetsla30=1 null or "": not use sla table

	private CommonConstants.DateType dateType;
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
	
	// INSERT START SP-476
	public boolean getForceOption() {
		return forceOption;
	}

	public void setForceOption(boolean force) {
		this.forceOption = force;
	}
	
	// INSERT END SP-476
	
	// INSERT START SP-993
	public String getMBBTypeWithMCU() {
		return mbbTypeWithMCU;
	}

	public void setMBBTypeWithMCU(String type) {
		this.mbbTypeWithMCU = type;
	}
	// INSERT END SP-993
	
	// INSERT START SP-1051
	public String getDSO() {
		return dso;
	}

	public void setDSO(String dso) {
		this.dso = dso;
	}

	public String getExcludeType() {
		return excludeType;
	}

	public void setExcludeType(String type) {
		this.excludeType = type;
	}
	// INSERT END SP-1051
	
	// INSERT START SP-1075
	public String getTargetSLA() {
		return targetSLA;
	}

	public void setTargetSLA(String type) {
		this.targetSLA = type;
	}	
	// INSERT END SP-1075
	
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
	        // UPDATE START SP-476
//	        calendar.add(Calendar.HOUR, -24);
	        calendar.add(Calendar.HOUR, (_beforeTime*-1));	        
	        // UPDATE END SP-476
	        Date before24Date = calendar.getTime();
	        
	    	MeterType[] meterTypes = {MeterType.EnergyMeter};
	    	for ( MeterType meterType : meterTypes){
	    		Map<String, Object> params = new HashMap<String, Object>();  
	    		params.put("channel", 1);
	    		params.put("searchStartDate",fromDate);
	    		params.put("searchEndDate", toDate);
	    		params.put("dateType", CommonConstants.DateType.HOURLY.getCode());
	    		params.put("meterType", meterType.name());
	    		params.put("supplierId", supplierId);
	    		params.put("lastLinkTime", DateTimeUtil.getDateString(before24Date));
	    		logger.info("Get Missing Meters: startDate[{}] endDate[{}] meterType[{}] supplierId[{}] lastLinkTime[{}] locationName[{}]",
	    				fromDate, toDate, meterType.name(), supplierId, DateTimeUtil.getDateString(before24Date), dso);
	    		if ( meterId != null && !"".equals(meterId)){
	    			params.put("mdsId", meterId);
	    		}
	    		params.put("locationName", dso);		    		// INSERT SP-1051

	    		List<Object> gaps  = null;
	    		
	    		// UPDATE START AP-1075
	    		if (targetSLA == null || targetSLA.equals("")) {	    			
		    		if ( dateType == CommonConstants.DateType.DAILY ){
			    		logger.debug("getMissingMeters:params={}", params);
		    			gaps = meterDao.getMissingMetersForRecollect(params);// UPDATE  SP-784
		    		}
		    		else {
		    			logger.debug("getMissingMetersForRecollectByHour:params={}", params);
		    			gaps = meterDao.getMissingMetersForRecollectByHour(params);
		    		}
	    		}
	    		else {
	    			params.put("targetSLA30", Integer.parseInt(targetSLA));
	    			logger.debug("getMissingMetersForRecollectSLA:params={}", params);
	    			gaps = meterDao.getMissingMetersForRecollectSLA(params);
	    		}
	    		
	    		StringBuffer sbuf = new StringBuffer();
	    		// UPDATE START SP-784
//	    		StringBuffer sbuf2 = new StringBuffer();
//    			for (Object obj : gaps) {
//    				boolean link = false;
//    				HashMap<String,Object> resultMap = (HashMap<String, Object>) obj;
//    				Meter meter = meterDao.get((String)resultMap.get("mdsId"));
//    				if ( meter != null ){
//    					Modem modem = meter.getModem();
//    					String lastLinkTime = null;
//    					if ( modem != null && (lastLinkTime = modem.getLastLinkTime()) != null){
//    						Date lastLinkDate = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(lastLinkTime);
//    						if ( before24Date.before(lastLinkDate)){
//    							allGaps.add(obj);
//    		    				sbuf.append("'" + (String) resultMap.get("mdsId") + "',");
//    		    				link = true;
//    						}
//    					}
//    				}
//    				if ( link == false){
//    					sbuf2.append("'" + (String) resultMap.get("mdsId") + "',");
//    				}
//    			}
//    			logger.debug("Missing Meters({}):{}", meterType.name(),sbuf.toString());
//       			logger.debug("Missing but Last Link Date is before {} ({}):{}", 
//       					new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(before24Date), meterType.name(),sbuf2.toString());
	    		for (Object obj : gaps) {
	    			HashMap<String,Object> resultMap = (HashMap<String, Object>) obj;
	    			sbuf.append("'" + (String) resultMap.get("mdsId") + "',");
	    		}
	    		logger.debug("Missing Meters({}) Count= {}:{}", meterType.name(),gaps.size(), sbuf.toString());
	    		allGaps.addAll(gaps);
	    		// UPDATE END SP-784
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
	
    // INSERT START SP-633
    public class McuDeviceList {
    	Integer mcuId;
    	String  sysId; // SP-784
    	List<String> deviceIdList;
    	String	meterMdsId;
    	
    	public boolean equals(Object obj){
    		
    		if(obj instanceof McuDeviceList) {
    			McuDeviceList t = (McuDeviceList)obj;
        		if (this.mcuId.compareTo(t.mcuId) == 0 ) return true;
        		else return false;
    		}
    		return false;
    	}
    }
        
    private List<McuDeviceList> getMcuList(List<Object> gaps, int deviceMeter) {
    	/*
    	 * Output List<McuMeterList> is list of McuMeterList. 
    	 * Output meters is list of meter which doesn't have a mcu.
    	 */
    	try{
	    	logger.debug("getMcuList(gaps[" + gaps.size() + "])");

    		List<Object> orgList = new ArrayList<Object>();
	    	List<McuDeviceList> mcuList = new ArrayList<McuDeviceList>();
	    	orgList.addAll(gaps);
	    	gaps.clear();
	    	
	        for (Object obj : orgList) {
				HashMap<String,Object> resultMap = (HashMap<String, Object>) obj;
				String mdsId = (String) resultMap.get("mdsId");
				Meter meter = meterDao.get(mdsId);
				Modem modem = meter.getModem();
				if (modem == null) {
					logger.debug("Meter[" + mdsId + "] . Modem is null .");
	        		continue;
	        	}
				MCU mcu = modem.getMcu();
				if (mcu != null) {
					logger.debug("MCU[" + mcu.getId() + "] . Meter[" + meter.getMdsId() + "] . Modem[" + modem.getDeviceSerial() + "] .");
					int i;
					for (i = 0; i < mcuList.size(); i++) {	// Search the same mcu.
						if ( mcuList.get(i).mcuId.compareTo(mcu.getId())==0 ) { 
							break;
						}
					}
					if (i < mcuList.size()) {	// find!
						logger.debug("MCU[" + mcu.getId() + "] has been already listed. Meter[" + mdsId + "],Modem[" + modem.getDeviceSerial() + " is added.");
						if ( deviceMeter==1 ){	// meter
							mcuList.get(i).deviceIdList.add(mdsId);
						} else {	// modem
							mcuList.get(i).deviceIdList.add(modem.getDeviceSerial());
						}
						continue;
					}
					McuDeviceList mml = new McuDeviceList();
					mml.mcuId = mcu.getId();
					mml.deviceIdList = new ArrayList<String>();
					if ( deviceMeter==1 ){	// meter
						mml.deviceIdList.add(mdsId);
					}else{	// modem
						mml.deviceIdList.add(modem.getDeviceSerial());
					}
					mml.meterMdsId = mdsId;
					mcuList.add(mml);
				} else {
					gaps.add(obj); 
				}
	        }
	        return mcuList;
    	}
        catch (Exception e) {
        	logger.debug(e.getMessage());
        	return null;
        }   	
    }
    
    /**
     * SP-784
     * @param gaps
     * @param deviceMeter
     * @return
     */
    private List<McuDeviceList> getMcuList2(List<Object> gaps, int deviceMeter) {
    	/*
    	 * Output List<McuMeterList> is list of McuMeterList. 
    	 * Output meters is list of meter which doesn't have a mcu.
    	 */
    	try{
	    	logger.debug("getMcuList2(gaps[" + gaps.size() + "])");

    		List<Object> orgList = new ArrayList<Object>();
	    	List<McuDeviceList> mcuList = new ArrayList<McuDeviceList>();
	    	orgList.addAll(gaps);
	    	gaps.clear();
	    	
	        for (Object obj : orgList) {
				HashMap<String,Object> resultMap = (HashMap<String, Object>) obj;
				String mdsId = (String) resultMap.get("mdsId");
				String deviceSerial = (String) resultMap.get("deviceSerial");
				String sysId = (String) resultMap.get("sysId");
				Integer mcuId = (Integer) resultMap.get("mcuId");
				
				if (mcuId != null) {
//					logger.debug("MCU_ID[" + sysId + "] . Meter[" + mdsId + "] . Modem[" + deviceSerial + "] .");
					int i;
					for (i = 0; i < mcuList.size(); i++) {	// Search the same mcu.
						if ( mcuList.get(i).mcuId.compareTo(mcuId)==0 ) { 
							break;
						}
					}
					if (i < mcuList.size()) {	// find!
//						logger.debug("MCU_ID[" +  sysId + "] has been already listed. Meter[" + mdsId + "],Modem[" + deviceSerial + " is added.");
						if ( deviceMeter==1 ){	// meter
							mcuList.get(i).deviceIdList.add(mdsId);
						} else {	// modem
							mcuList.get(i).deviceIdList.add(deviceSerial);
						}
						continue;
					}
					McuDeviceList mml = new McuDeviceList();
					mml.mcuId = mcuId;
					mml.sysId = sysId;
					mml.deviceIdList = new ArrayList<String>();
					if ( deviceMeter==1 ){	// meter
						mml.deviceIdList.add(mdsId);
					}else{	// modem
						mml.deviceIdList.add(deviceSerial);
					}
					mml.meterMdsId = mdsId;
//					logger.debug("create MCU_ID[" +  sysId + "] list and Meter[" + mdsId + "],Modem[" + deviceSerial + " is added.");
					mcuList.add(mml);
				} else {
					gaps.add(obj); 
				}
	        }
	        return mcuList;
    	}
        catch (Exception e) {
        	logger.debug(e.getMessage());
        	return null;
        }   	
    }    
    private List<McuDeviceList> getMcuList_KOREANENV_TEST(List<Object> gaps, int deviceMeter) {
    	/*
    	 * Output List<McuMeterList> is list of McuMeterList. 
    	 * Output meters is list of meter which doesn't have a mcu.
    	 */
    	try{
	    	logger.debug("getMcuList2(gaps[" + gaps.size() + "])");
	    	List<McuDeviceList> mcuList = new ArrayList<McuDeviceList>();
	    	gaps.clear();

	    	int idx = 0;
	        for (idx=0; idx<3; idx++) {
				String mdsId = "";
	        	if (idx==0){
	        		mdsId = "5100000000000042";
	        	} else if (idx==1){
	        		mdsId = "5100000000000044";
	        	} else if (idx==2){
	        		mdsId = "5100000000000035";
	        	}
				Meter meter = meterDao.get(mdsId);
				Modem modem = meter.getModem();
				if (modem == null) {
					logger.debug("Meter[" + mdsId + "] . Modem is null .");
	        		continue;
	        	}
				MCU mcu = modem.getMcu();

				if (mcu != null) {
					logger.debug("MCU[" + mcu.getId() + "] . Meter[" + meter.getMdsId() + "] . Modem[" + modem.getDeviceSerial() + "] .");
					int i;
					for (i = 0; i < mcuList.size(); i++) {	// Search the same mcu.
						if ( mcuList.get(i).mcuId.compareTo(mcu.getId())==0 ) { 
							break;
						}
					}
					if (i < mcuList.size()) {	// find!
						logger.debug("MCU[" + mcu.getId() + "] has been already listed. Meter[" + mdsId + "],Modem[" + modem.getDeviceSerial() + " is added.");
						if ( deviceMeter==1 ){	// meter
							mcuList.get(i).deviceIdList.add(mdsId);
						} else {	// modem
							mcuList.get(i).deviceIdList.add(modem.getDeviceSerial());
						}
						continue;
					}
					McuDeviceList mml = new McuDeviceList();
					mml.mcuId = mcu.getId();
					mml.deviceIdList = new ArrayList<String>();
					if ( deviceMeter==1 ){	// meter
						mml.deviceIdList.add(mdsId);
					}else{	// modem
						mml.deviceIdList.add(modem.getDeviceSerial());
					}
					mml.meterMdsId = mdsId;
					mcuList.add(mml);
				}
	        }
	        return mcuList;
    	}
        catch (Exception e) {
        	logger.debug(e.getMessage());
        	return null;
        }   	
    }    
    // INSERT END SP-633
	
	@Override
	public void execute(JobExecutionContext context) {
        TransactionStatus txstatus = null;
		int _maxThreadWorker = 10;
		int _maxMcuThreadWorker = 10;	// INSERT SP-633 
		int _maxRecollectModem = 10;	// INSERT SP-633
		int _deviceMeter = 0;	// INSERT SP-633
		long _timeout = 3600;
		Properties prop = new Properties();
		
		try {
			//txstatus = txmanager.getTransaction(null);
			try{
				prop.load(getClass().getClassLoader().getResourceAsStream("config/schedule-RecollectMeteringSoria.properties"));
			}catch(Exception e){
				logger.error("Can't not read property file. -" + e,e);
			}
			_maxThreadWorker = Integer.parseInt(prop.getProperty("recollect.ondemand.maxworker", "10"));
			// INSERT START SP-633
			_maxMcuThreadWorker = Integer.parseInt(prop.getProperty("recollect.ondemand.mcu.maxworker", "10"));
	        _maxRecollectModem = Integer.parseInt(prop.getProperty("recollect.ondemand.modem.max", "400"));
			_deviceMeter = Integer.parseInt(prop.getProperty("recollect.ondemand.device.meter", "0"));
			// INSERT END SP-633
			// INSERT START SP-476
			_timeout = Integer.parseInt(prop.getProperty("recollect.ondemand.timeout", "3600"));
			if (prop.getProperty("recollect.ondemand.useif4", "true").equals("false")) {
				_isUseIF4Command = false;
			}
			_beforeTime = Integer.parseInt(prop.getProperty("recollect.ondemand.beforeTime", "24"));
			// INSERT END SP-476
			// INSERT START SP-1051
			if (prop.getProperty("recollect.ondemand.smsresponse", "false").equals("true")) {
				_enableSMSRes = true;
			}
			// INSERT START SP-1051		 
			
//			Map<String, String> TFBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -24);
//			String TFDate = TFBeforeHour.get("date").replace("-", "") + TFBeforeHour.get("time").replace(".", "");
//			fromDate = TFDate.substring(0,8);
//			toDate = TFDate.substring(0,8);
			//=> INSERT START 2017.03.31 SP-549
			boolean bAutoRecovery = false;
			if ( fromDate == null || "".equals(fromDate) || toDate == null || "".endsWith(toDate )){
				Map<String, String> TFBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -24);
				String TFDate = TFBeforeHour.get("date").replace("-", "") + TFBeforeHour.get("time").replace(".", "");	
				dateType = CommonConstants.DateType.DAILY;
				fromDate = TFDate.substring(0,8);
				toDate = TFDate.substring(0,8);
				bAutoRecovery = true;
			}
			//=> INSERT END   2017.03.31 SP-549

			List<Object> gaps = getGaps(fromDate, toDate);
    		logger.info("Total Meter to need recollect metering ["+ gaps.isEmpty() != null ? gaps.size()+"" : "0" + "]");
    		int cnt = 0;

    		// INSERT START SP-476
    		if ((gaps.isEmpty()) && (forceOption == true)) {
    			Map<String, Object> obj = new HashMap<String, Object>();
    			obj.put("mdsId", meterId);
    			gaps.add(obj);
    			logger.debug("Forced option is specified.");
    		}
    		// INSERT END SP-476
    		
    		ExecutorService pool = Executors.newFixedThreadPool(_maxThreadWorker);
    		RecollectThread threads[] = new RecollectThread[gaps.size()];
    		int	i = 0;
    
    		if(!gaps.isEmpty()){
    			//=> INSERT START 2017.02.24 SP-549
    			if ( !deviceType.equals(DeviceType.Meter) && bAutoRecovery ){
    				toDate = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss").substring(0,8);
    			}
    			logger.info("RecollectMeteringSoriaTask . deviceType:" + deviceType +",fromDate=" + fromDate + ",toDate=" + toDate);
    			//=> INSERT END   2017.02.24 SP-549

    			// INSERT START SP-633
                if (deviceType == CommonConstants.DeviceType.MCU) {
                	List<McuDeviceList> mculist = getMcuList2(gaps, _deviceMeter);
                	//List<McuDeviceList> mculist = getMcuList_KOREANENV_TEST(gaps, _deviceMeter);
                	
                	// Get a dummy mdsId for calling mcuRecollectThread(); 
                	//HashMap<String,Object> resultMap = (HashMap<String, Object>) gaps.get(0);
    				//String mdsId = (String) resultMap.get("mdsId");

        			// INSERT START SP-1051
                    if (excludeType.toLowerCase().contains("SubGiga".toLowerCase())) {
            			logger.info("Exclude SubGiga meters.");
                    }
                    else {
        			// INSERT END SP-1051                	
	                	for ( int mi = 0; mi < mculist.size(); mi++){
	                		StringBuffer sb = new StringBuffer();
	                		sb.append("Recorrect via MCU: MCU[" + mculist.get(mi).sysId+ "]　MCU_ID[" + mculist.get(mi).mcuId + "] MeterCount [" + mculist.get(mi).deviceIdList.size() + "]");
	//                		if ( _deviceMeter == 1 ) 
	//                			sb.append("Meters[");
	//                		else 
	//                			sb.append("Modems[");
	//                		for ( String devId : mculist.get(mi).deviceIdList ){
	//                			sb.append(devId + ",");
	//                		}
	//                		sb.append("]");
	                		logger.debug(sb.toString());
	                	}
	                	cnt = 0;
	
	                    ExecutorService pool1 = Executors.newFixedThreadPool(_maxMcuThreadWorker);
	                	mcuRecollectThread threads1[] = new mcuRecollectThread[mculist.size()];
	                	i = 0;
	                
	                	for (McuDeviceList mcuMeter : mculist) {
	                		logger.info(cnt++ + ": MCU[" + mcuMeter.mcuId + "] MdsId(for call command)[" + mcuMeter.meterMdsId + "]");
	
	                		if ( dateType == CommonConstants.DateType.DAILY ){
	        					threads1[i] = new mcuRecollectThread(mcuMeter, meterDao.get(mcuMeter.meterMdsId), mcuMeter.meterMdsId, fromDate+"000000", toDate+"235959",
	        							loginId, _maxRecollectModem);
	        				}
	        				else{
	         					threads1[i] = new mcuRecollectThread(mcuMeter, meterDao.get(mcuMeter.meterMdsId), mcuMeter.meterMdsId, fromDate+"0000", toDate+"5959",
	         							loginId, _maxRecollectModem);
	        				}
	                		pool1.execute(threads1[i]);
	                		i++;
	                	}
	
	                    logger.info("ExecutorService for mcu shutdown.");
	                    pool1.shutdown();
	                    logger.info("ExecutorService for mcu awaitTermination. [" + _timeout + "]sec");
	                    pool1.awaitTermination(_timeout, TimeUnit.SECONDS);
                	
                    }// INSERT SP-1051                	
                }
                cnt = 0;
                i = 0;                
                // INSERT END SP-633
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
        	this.thrNo = thrNo;
        	this.mdsId = mdsId;
        	this.deviceType = deviceType;
        	this.fromDate = fromDate;
        	this.toDate = toDate;
        	this.loginId = roginId;
        }
        
        public void run() {
        	TransactionStatus txstatus = null;

        	logger.info("[No={}] RecollecThread Start mdsId[{}] deviceType[{}] fromDate[{}] toDate[{}]", thrNo, mdsId, deviceType.name(), fromDate, toDate, loginId  );
        	try {
        		txstatus = txmanager.getTransaction(null);	 
        		Map result = null;
        		Meter meter = meterDao.get(mdsId);
        		if (meter == null ) {
        			logger.error("[{}] Meter[" + mdsId + "] is null" ,mdsId);
            		txmanager.commit(txstatus);
        			throw new Exception("Meter[" + mdsId + "] is null");
        		}
        		Modem modem = meter.getModem();
        		if (modem == null ) {
        			logger.error("[{}] modem of Meter[" + mdsId + "] is null" ,mdsId);
            		txmanager.commit(txstatus);
        			throw new Exception("modem of Meter[" + mdsId + "] is null");
        		}
        		txmanager.commit(txstatus);

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

        		String nOption = "";
        		logger.info("[{}]  ModemType = {}, ProtocolType = {}, mbbTypeWithMCU = {}",
        				mdsId, modem.getModemType().name(),  modem.getProtocolType().name(), mbbTypeWithMCU);
        		if((modem.getModemType() == ModemType.MMIU) && (modem.getProtocolType() == Protocol.SMS)) {
        			// INSERT START SP-1051
                    if (excludeType.toLowerCase().contains("SMS".toLowerCase())) {
            			logger.info("Exclude MBB(SMS) meters.");
            			throw new Exception("Exclude MBB(SMS) meters.");
                    }
        			// INSERT END SP-1051
        			
        			if ( deviceType == CommonConstants.DeviceType.Meter){
        				result = onDemandMeterBypassMBB(meter.getMdsId(), fromDate, toDate, loginId);
        				logger.debug("[{}] send sms command : onDemandMeterBypassMBB", mdsId);
        			}
        			else if ( deviceType == CommonConstants.DeviceType.Modem){
        				result = romReadBypassMBB(meter.getMdsId(), fromDate, toDate, loginId);
        				logger.debug("[{}] send sms command : romReadBypassMBB", mdsId);
        			}
        			// INSERT START SP-1051
        			else if ( deviceType == CommonConstants.DeviceType.MCU){
        				if (mbbTypeWithMCU.equals("Modem")) {
            				result = romReadBypassMBB(meter.getMdsId(), fromDate, toDate, loginId);
            				logger.debug("[{}] send sms command : romReadBypassMBB", mdsId);
        				} else if (mbbTypeWithMCU.equals("Meter")) {
            				result = onDemandMeterBypassMBB(meter.getMdsId(), fromDate, toDate, loginId);
            				logger.debug("[{}] send sms command : onDemandMeterBypassMBB", mdsId);
        				}         	        				
        			}
        			// INSERT END SP-1051
        		}
        		else {
        			// INSERT START SP-1051
        			if((modem.getModemType() == ModemType.MMIU) && (modem.getProtocolType() == Protocol.GPRS) &&
        				(excludeType.toLowerCase().contains("GPRS".toLowerCase()))) {
            			logger.info("Exclude MBB(GPRS) meters.");
            			throw new Exception("Exclude MBB(GPRS) meters.");
                    }
        			if((modem.getModemType() == ModemType.SubGiga) &&
            				(excludeType.toLowerCase().contains("SubGiga".toLowerCase()))) {
            			logger.info("Exclude SubGiga meters.");
            			throw new Exception("Exclude SubGiga meters.");
                    }
        			// INSERT END SP-1051        			
        			
        			if ( deviceType == CommonConstants.DeviceType.Meter){
        				result = cmdOperationUtil.doOnDemand(meter, 0, loginId, nOption, fromDate, toDate);
        				logger.debug("[{}] call doOnDemand", mdsId);
        			}
        			else if ( deviceType == CommonConstants.DeviceType.Modem){
        				// UPDATE START SP-476
//        				result = cmdOperationUtil.cmdGetROMRead(meter, 0, loginId, nOption, fromDate, toDate);
//        				logger.debug("[{}] call cmdGetROMRead", mdsId);
        				if (_isUseIF4Command && !(modem.getModemType() == ModemType.MMIU)) { //SP-993
        					result = cmdOperationUtil.cmdDmdNiGetRomRead(meter, 0, loginId, fromDate, toDate);
            				logger.debug("[{}] call cmdDmdNiGetRomRead", mdsId);        					
        				} else {
            				result = cmdOperationUtil.cmdGetROMRead(meter, 0, loginId, nOption, fromDate, toDate);
            				logger.debug("[{}] call cmdGetROMRead", mdsId);
        				}
        				// UPDATE END SP-476
        			}
        			// INSERT START SP-476
        			else if ( deviceType == CommonConstants.DeviceType.MCU){
        				// UPDATE START SP-993
//        				result = cmdOperationUtil.cmdGetMeteringData(meter, 0, loginId, nOption, fromDate, toDate, null);
//        				logger.debug("[{}] call cmdGetMeteringData", mdsId);
        				
        				if (!(modem.getModemType() == ModemType.MMIU)) {
            				result = cmdOperationUtil.cmdGetMeteringData(meter, 0, loginId, nOption, fromDate, toDate, null);
            				logger.debug("[{}] call cmdGetMeteringData", mdsId);        					
        				} else if (mbbTypeWithMCU.equals("Modem")) {
            				result = cmdOperationUtil.cmdGetROMRead(meter, 0, loginId, nOption, fromDate, toDate);
            				logger.debug("[{}] call cmdGetROMRead", mdsId);
        				} else if (mbbTypeWithMCU.equals("Meter")) {
            				result = cmdOperationUtil.doOnDemand(meter, 0, loginId, nOption, fromDate, toDate);
            				logger.debug("[{}] call doOnDemand", mdsId);        					
        				}         				
        				// UPDATE END SP-993
        			}
        			// INSERT END SP-476
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
//        		txmanager.commit(txstatus);
        	}
        	catch  (Exception e) {
        		logger.error("[" +mdsId+ "] RecollectThread.run error - " + e, e);
//        		if (txstatus != null&& !txstatus.isCompleted())
//        			txmanager.rollback(txstatus);
        	} finally {
        		txstatus = null;
        	}
        	
        	logger.info("[No={}] RecollecThread End [{}]", thrNo, mdsId );
        }
    }

    // INSERT START SP-633 
//    class mcuRecollectThread extends Thread {
//    	
//        McuModemList mcuModems;
//        Meter	target;
//    	String mdsId;
//    	String fromDate;
//    	String toDate;
//    	String loginId;
//    	int limitOfModems = 0;    	
//    	
//        mcuRecollectThread(McuModemList mcuModems, Meter meter,String mdsId, String fromDate, String toDate, String loginId, int limit)  {
//    		try {
//	    		this.mcuModems = mcuModems;
//	    		this.target = meter;
//	    		this.mdsId = mdsId;
//	    		this.fromDate = fromDate;
//	    		this.toDate = toDate;
//	    		this.loginId = loginId;
//	    		limitOfModems = limit;
//            }
//            catch (Exception ee) {}        
//        }
//        
//        public void run() {
//        	
//        	//TransactionStatus txstatus = null;
//
//            	logger.info("ThreadID[" + Thread.currentThread().getId() + "] Mcu Rcollect Metering thread start. MCU[" + mcuModems.mcuId + "]");
//
//            	try {
//            		
//    	    		//txstatus = txmanager.getTransaction(null);	
//
//            		
//    				logger.debug("meterDao.get() ["+  mdsId + "]"  );
//
//    				//Meter meter = meterDao.get(mdsId); // dummy Meter for cmdGetMeteringData()
//            		List<String> modemList = mcuModems.modemDeviceIdList;
//            		List<String> splitModems = null;
//            		Map result = null;
//            		int k = modemList.size() / limitOfModems;
//            		int cnt = 0;
//            		
//    				logger.debug("limitOfModems ["+  limitOfModems + "]"  );
//    				logger.debug("k ["+  k + "]"  );
//            		for (int i = 0; i < k + 1; i++) {
//            			splitModems = new ArrayList<String>();
//            			
//            			for (int j = 0; j < limitOfModems; j++) {
//            				if (cnt == modemList.size()) {
//            					break;
//            				}
//            				logger.debug("modem [" + i + "][" + j + "][" + (String)modemList.get(cnt) + "]"  );
//            				splitModems.add((String)modemList.get(cnt++));
//            			}
//            			String[] modems = splitModems.toArray(new String[splitModems.size()]);
//
//            			String nOption = "";
//            			result = cmdOperationUtil.cmdGetMeteringData(target, 0, loginId, nOption, fromDate, toDate, modems);
//        				logger.debug("Mcu[{}] call cmdGetMeteringData", mcuModems.mcuId);
//            			splitModems = null;
//            		}
//            		//txmanager.commit(txstatus);
//            	} catch (Exception e){
//            		logger.info("ThreadID[" + Thread.currentThread().getId() + "] Mcu Recollect Metering thread end. MCU[" + mcuModems.mcuId + "] is failed.");
//            		//if (txstatus != null&& !txstatus.isCompleted())
//            		//	txmanager.rollback(txstatus);
//            	}	
//
//            	logger.info("ThreadID[" + Thread.currentThread().getId() + "] Mcu Recollect Metering thread end. MCU[" + mcuModems.mcuId + "]");            	
//        }
//    }
    class mcuRecollectThread extends Thread {

    	McuDeviceList mcuDevices;
    	Meter	target;
    	String mdsId;
    	String fromDate;
    	String toDate;
    	String loginId;
    	int limitOfDevices = 0;

    	mcuRecollectThread(McuDeviceList mcuDevices, Meter meter,String mdsId, String fromDate, String toDate, String loginId, int limit)  {
    		try {
    			this.mcuDevices = mcuDevices;
    			this.target = meter;
    			this.mdsId = mdsId;
    			this.fromDate = fromDate;
    			this.toDate = toDate;
    			this.loginId = loginId;
    			this.limitOfDevices = limit;
    		}
    		catch (Exception ee) {}        
    	}

    	public void run() {

    		logger.info("ThreadID[" + Thread.currentThread().getId() + "] Mcu Rcollect Metering thread start. MCU[" + mcuDevices.mcuId + "]");

    		try {

    			logger.debug("meterDao.get() ["+  mdsId + "]"  );

    			List<String> deviceList = mcuDevices.deviceIdList;
    			List<String> splitDevices = null;
    			Map result = null;
    			int k = deviceList.size() / limitOfDevices;
    			int cnt = 0;

    			logger.debug("limitOfDevices ["+  limitOfDevices + "]"  );
    			logger.debug("k ["+  k + "]"  );
    			for (int i = 0; i < k + 1; i++) {
    				splitDevices = new ArrayList<String>();

    				for (int j = 0; j < limitOfDevices; j++) {
    					if (cnt == deviceList.size()) {
    						break;
    					}
    					logger.debug("device [" + i + "][" + j + "][" + (String)deviceList.get(cnt) + "]"  );
    					splitDevices.add((String)deviceList.get(cnt++));
    				}
    				String[] devices = splitDevices.toArray(new String[splitDevices.size()]);

    				String nOption = "";
    				result = cmdOperationUtil.cmdGetMeteringData(target, 0, loginId, nOption, fromDate, toDate, devices);
    				logger.debug("Mcu[{}] call cmdGetMeteringData", mcuDevices.mcuId);
    				splitDevices = null;
    			}
    		} catch (Exception e){
    			logger.info("ThreadID[" + Thread.currentThread().getId() + "] Mcu Recollect Metering thread end. MCU[" + mcuDevices.mcuId + "] is failed.");
    		}	

    		logger.info("ThreadID[" + Thread.currentThread().getId() + "] Mcu Recollect Metering thread end. MCU[" + mcuDevices.mcuId + "]");
    	}
    }
    // INSERT END SP-633 
    
    /*
     * Send SMS
     */
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
			//String response_messageType = resultMap.get("messageType").toString();
			String response_messageId = resultMap.get("messageId") == null ? "F" : resultMap.get("messageId").toString();
			/*
			 * 결과 처리
			 */
			if (response_messageId.equals("F") || response_messageId.equals("CF")) { // Fail
				logger.debug(response_messageId);
				return null;
			} 
			else if (!_enableSMSRes) {
				Map<String, String> map = new HashMap<String,String>();
				map.put("resultStr", "Success");
				logger.debug("Sending of SMS request succeeded.");
				logger.debug("Not wait a response.");
				return map;
			}
			else {
				
			    int loopCount = 0;
                Integer lastStatus = null;
                while(loopCount < 6) {
                    lastStatus = asyncCommandLogDao.getCmdStatus(modem.getDeviceSerial(), commandName);
                    if (TR_STATE.Success.getCode() == lastStatus) {
                        break;
                    }
                    loopCount++;
                    Thread.sleep(10000);
                }
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

		return resultMap;
	}

	
    private Map<String, String> onDemandMeterBypassMBB(String mdsId, String fromDate, String toDate, String loginId) {
    	
    	logger.debug("onDemandMeterBypassMBB Start mdsId[{}] fromDate[{}] toDate[{}] loginId[{}]",
    			mdsId, fromDate,toDate,loginId);
    	ResultStatus status = ResultStatus.FAIL;
		Meter meter = null;
		String cmd = "cmdMeterParamGet";
        String detailInfo = "";
        String rtnStr = "";
        Map<String,String> returnMap = null; 
        JSONArray jsonArr = null;
        try{
//    		if (loginId != null ){
//    			if (!commandAuthCheck(loginId,  "8.1.10")) {
//    				throw new Exception("No permission");
//    			}
//    		}
    		
    		meter = meterDao.get(mdsId);
            int modemPort = 0;
			if ( meter.getModemPort() != null ){
				modemPort = meter.getModemPort().intValue();
			}
			if ( modemPort > 5){
				logger.error("[{}] ModemPort: {} is not Support", mdsId, modemPort);
				throw new Exception("ModemPort:" + modemPort + " is not Support");
			}    		
    		
			Map<String,String> paramMap = new HashMap<String,String>();
			if (modemPort==0) {
		    	logger.debug("cmdGetLoadProfile ["+ mdsId + "][" + modemPort +  "]["  +  fromDate + "][" +toDate +"]");

		    	String obisCode = DataUtil.convertObis(OBIS.ENERGY_LOAD_PROFILE.getCode());
				int classId = DLMS_CLASS.PROFILE_GENERIC.getClazz();
				int attrId = DLMS_CLASS_ATTR.PROFILE_GENERIC_ATTR02.getAttr();
				
				Map<String,String> valueMap = CommonUtil.getParamValueByRange(fromDate,toDate);
				String value = CommonUtil.meterParamMapToJSON(valueMap);
				
				logger.debug("[{}] ObisCode=> {}, classID => {}, attributeId => ", mdsId, obisCode,classId,attrId);
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
	            		rtnStr = "FAIL : Phone number is empty!";
	        		}            	
	            	else if (!Protocol.SMS.equals(mmiu.getProtocolType())) {
	            		logger.warn(String.format("[" + cmd + "] Invalid ProtocolType"));
	            		rtnStr = "FAIL : Invalid ProtocolType!";
	    			}
	            	else {	            	
						Long trId = System.currentTimeMillis();
						Map<String, String> result;
						String cmdResult = "";
						
						result = sendSmsForCmdServer(mmiu, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), cmd, paramMap); 						
						if(result != null){
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
				                if (meter.getModem() != null) {
				                	meter.getModem().setCommState(1);
				                }
				                
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
    			
   	                
    			paramMap.put("meterId", mdsId);
    			paramMap.put("fromDate",fromDate);
    			paramMap.put("toDate", toDate);
    			
       			asyncResult = sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), cmd, paramMap);	

    			if(asyncResult != null){
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
	 * 	mvn -e -f $AIMIR_TASK/pom-RecollectMeteringSoria.xml antrun:run -DtaskName=RecollectMeteringSoria -DdeviceType=DeviceType -DsupplierName=SupplierName -DfromDate=FromYYYYMMDD -DtoDate=ToYYYYMMDD -Dspring.instrument.path=$MVNREPOSITORY/org/springframework/spring-instrument/4.2.5.RELEASE
	 */
	public static void main(String[] args) {
		logger.info("-----");
		logger.info("-----");
		logger.info("-----");
		logger.info("#### RecollectMeteringSoriaTask start. ###");
		
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
		String force = "false";		// INSERT SP-476
		String mbbWithMcu = "Modem";	// INSERT SP-993
		String dso = "";			// INSERT SP-1051
		String exclude = "";		// INSERT SP-1051
		String sla = "";			// INSERT SP-1075
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
			// INSERT START SP-993
			else if ( nextArg.startsWith("-mbbWithMcu")){
				if ( !"${mbbWithMcu}".equals(args[i + 1])){
					mbbWithMcu = new String(args[i + 1]);
				}
			}			
			// INSERT END SP-993
			// INSERT START SP-1051
			else if ( nextArg.startsWith("-dso")){
				if ( !"${dso}".equals(args[i + 1])){
					dso = new String(args[i + 1]);
				}
			}
			else if ( nextArg.startsWith("-excludeType")){
				if ( !"${excludeType}".equals(args[i + 1])){
					exclude = new String(args[i + 1]);
				}
			}
			// INSERT END SP-1051
			// INSERT START SP-1075
			else if ( nextArg.startsWith("-sla")){
				if ( !"${sla}".equals(args[i + 1])){
					sla = new String(args[i + 1]);
				}
			}
			// INSERT END SP-1075
			
		}

		dateType = checkDate(fromDate, toDate );

		if ( dateType == null ){
			logger.info("RecollectMeteringSoriaTask -DdeviceType=Meter|Modem [-DsupplierName=supplierName] [-DfromDate=fromDateYYYYMMDD(hh)] [-DtoDate=toDateYYYYMMDD(hh)] [-DmeterId=meterId]");
			System.exit(1);
		}
		logger.info("RecollectMeteringSoriaTask . devicetype={} supplierName={} fromDate={} toDate={} meterId={} ", 
				deviceType, supplierName,  fromDate, toDate, meterId);

		if (deviceType == null ) {
			deviceType = "Meter";
		}
		if (supplierName == null ){
			supplierName = "";
		}
		// INSERT START SP-1051
		if ((mbbWithMcu == null ) || (mbbWithMcu.equals(""))){
			mbbWithMcu = "Modem";
		}
		// INSERT END SP-1051
		//=> DELETE START 2017.03.31 SP-549
		//if ( fromDate == null || "".equals(fromDate) || toDate == null || "".endsWith(toDate )){
		//	Map<String, String> TFBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -24);
		//	String TFDate = TFBeforeHour.get("date").replace("-", "") + TFBeforeHour.get("time").replace(".", "");	
		//	dateType = CommonConstants.DateType.DAILY;
		//	fromDate = TFDate.substring(0,8);
		//	toDate = TFDate.substring(0,8);
		//}
		//=> DELETE END   2017.03.31 SP-549

		try {
			ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "spring-RecollectMeteringSoria.xml" });
			DataUtil.setApplicationContext(ctx);

			RecollectMeteringSoriaTask task = (RecollectMeteringSoriaTask) ctx.getBean(RecollectMeteringSoriaTask.class);

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
			// INSERT START SP-476
	        else if (CommonConstants.DeviceType.MCU.name().equals(deviceType)){
	        	task.setDeviceType(CommonConstants.DeviceType.MCU);
	        }
			// INSERT END SP-476
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

			task.setMBBTypeWithMCU(mbbWithMcu);	// INSERT SP-993
			task.setDSO(dso); 				// INSERT SP-1051
			task.setExcludeType(exclude);	// INSERT SP-1051
			task.setTargetSLA(sla); 		// INSERT SP-1075 
			task.execute(null);

		} catch (Exception e) {
			logger.error("RecollectMeteringSoriaTask excute error - " + e, e);
		} finally {
			logger.info("#### RecollectMeteringSoriaTask Task finished. ###");		
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
//			logger.info("RecollectMeteringSoriaTask -DdeviceType=Meter|Modem [-DsupplierName=supplierName] [-DfromDate=fromDateYYYYMMDD] [-DtoDate=toDateYYYYMMDD]");
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




}
