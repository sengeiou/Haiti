package com.aimir.fep.tool;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
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
import com.aimir.dao.mvm.LpEMDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.data.MeterData;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.DLMS_CLASS;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.DLMS_CLASS_ATTR;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.OBIS;
import com.aimir.fep.protocol.smsp.SMSConstants;
import com.aimir.fep.protocol.smsp.SMSConstants.COMMAND_TYPE;
import com.aimir.fep.protocol.smsp.command.frame.sms.ResponseFrame;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Util;
import com.aimir.model.device.AsyncCommandResult;
import com.aimir.model.device.MMIU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.system.Supplier;
import com.aimir.util.DateTimeUtil;


@Component
public class RecollectMeteringSoriaForOneDay {
	private static Logger logger= LoggerFactory.getLogger(RecollectMeteringSoriaForOneDay.class);

	@PersistenceContext
	protected EntityManager em;

	@Resource(name="transactionManager")
	JpaTransactionManager txmanager;

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
	AsyncCommandLogDao asyncCommandLogDao;

	@Autowired
	AsyncCommandResultDao resultDao;

	@Autowired
	CommandGW commandGW ;

    @Autowired
    protected LpEMDao lpEMDao;
    
	private DeviceType deviceType;
	private String 		supplierName;
	int		supplierId;
	private String 		loginId;
	private String		fromDate;
	String  lpcntTable = "sla_rawdata";
	//private String		toDate;
	//private String		meterId;

	//private boolean 	forceOption;
	private boolean		_isUseIF4Command = true;
	private int			_beforeTime = 24;

	private String		mbbTypeWithMCU = "Modem";

	private String		dso;
	private boolean     _enableSMSRes = false;
	private String		excludeType="";

	private String		targetSLA = ""; //SP-1075 0:targetsla30=0, 1:targetsla30=1 null or "": not use sla table
	
	private final int successIndex = 0;
	private final int failIndex = 1;
	private final int excludeIndex = 2;
	private final int unknownIndex = 3;
	private AtomicInteger[]   mcuResult = new AtomicInteger[4];
	private AtomicInteger[]   subGigaResult = new AtomicInteger[4];
	private AtomicInteger[]   gprsResult = new AtomicInteger[4];
	private AtomicInteger[]   smsResult = new AtomicInteger[4];
	//private CommonConstants.DateType dateType;

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

	public String getMBBTypeWithMCU() {
		return mbbTypeWithMCU;
	}

	public void setMBBTypeWithMCU(String type) {
		this.mbbTypeWithMCU = type;
	}

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

	public String getTargetSLA() {
		return targetSLA;
	}

	public void setTargetSLA(String type) {
		this.targetSLA = type;
	}	

	private List<Object> getGaps(String searhDate)
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
				params.put("searchStartDate",searhDate);
				String lastLinkTime = DateTimeUtil.getDateString(before24Date);
				params.put("lastLinkTime", lastLinkTime);

				params.put("locationName", dso);		    		// INSERT SP-1051

				List<Object> gaps  = null;

				if (targetSLA == null || targetSLA.equals("")) {
					targetSLA="0";
				}
				params.put("targetSLA30", Integer.parseInt(targetSLA));
				params.put("lpcntTableName", lpcntTable);
				logger.debug("getMissingMetersForRecollectSLA:params={}", params);
				gaps = meterDao.getMissingMetersForRecollectSLA(params);

				logger.info("CountInfo:All Missing Meters({}) Count={} targetSLA={} dso={} lastLinkTime={}", 
						meterType.name(),gaps.size(),
						targetSLA,
						dso,
						lastLinkTime);
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
		String  sysId; // SP-784
		List<String> deviceIdList;
		String	meterMdsId;
		String  deviceSerial;
		List<Map<String, Object>> gapList;

		public boolean equals(Object obj){

			if(obj instanceof McuDeviceList) {
				McuDeviceList t = (McuDeviceList)obj;
				if (this.mcuId.compareTo(t.mcuId) == 0 ) return true;
				else return false;
			}
			return false;
		}
	}

	/**
	 * 
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

			int subGigaCnt = 0;
			int mmiuCnt = 0;
			
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
					subGigaCnt++;
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
						mcuList.get(i).gapList.add((HashMap<String, Object>)obj);
						continue;
					}
					McuDeviceList mml = new McuDeviceList();
					mml.mcuId = mcuId;
					mml.sysId = sysId;
					mml.deviceIdList = new ArrayList<String>();
					mml.gapList = new ArrayList<Map<String,Object>>();
					if ( deviceMeter==1 ){	// meter
						mml.deviceIdList.add(mdsId);
					}else{	// modem
						mml.deviceIdList.add(deviceSerial);
					}
					mml.meterMdsId = mdsId;
					mml.deviceSerial = deviceSerial;
					mml.gapList.add((HashMap<String, Object>)obj);
					//					logger.debug("create MCU_ID[" +  sysId + "] list and Meter[" + mdsId + "],Modem[" + deviceSerial + " is added.");
					mcuList.add(mml);
				} else {
					mmiuCnt++;
					gaps.add(obj); 
				}
			}
			logger.info("CountInfo: Gap Count SubGiga[{}] MMIU[{}]", subGigaCnt, mmiuCnt);
			return mcuList;
		}
		catch (Exception e) {
			logger.debug(e.getMessage());
			return null;
		}   	
	}    

	public void execute(String[] args) {
		TransactionStatus txstatus = null;
		int _maxThreadWorker = 10;
		int _maxMcuThreadWorker = 10;
		int _maxRecollectModem = 10;
		int _deviceMeter = 0;
		long _timeout = 3600;

		Properties prop = new Properties();

		try {
			//txstatus = txmanager.getTransaction(null);
			try{
				prop.load(getClass().getClassLoader().getResourceAsStream("config/RecollectMeteringSoriaForOneDay.properties"));
			}catch(Exception e){
				logger.error("Can't not read property file. -" + e,e);
			}
			for ( int i = 0; i < subGigaResult.length; i++) {
				subGigaResult[i] = new AtomicInteger(0);
			}
			for ( int i = 0; i < gprsResult.length; i++) {
				gprsResult[i] = new AtomicInteger(0);
			}
			for ( int i = 0; i < smsResult.length; i++) {
				smsResult[i] = new AtomicInteger(0);
			}
			for ( int i = 0; i < mcuResult.length; i++) {
				mcuResult[i] = new AtomicInteger(0);
			}
			_maxThreadWorker = Integer.parseInt(prop.getProperty("recollect.ondemand.maxworker", "10"));
			_maxMcuThreadWorker = Integer.parseInt(prop.getProperty("recollect.ondemand.mcu.maxworker", "10"));
			_maxRecollectModem = Integer.parseInt(prop.getProperty("recollect.ondemand.modem.max", "400"));
			_deviceMeter = Integer.parseInt(prop.getProperty("recollect.ondemand.device.meter", "0"));
			_timeout = Integer.parseInt(prop.getProperty("recollect.ondemand.timeout", "3600"));
			lpcntTable = prop.getProperty("recollect.ondemand.lpcnt.tablename", "sla_rawdata");
			if (prop.getProperty("recollect.ondemand.useif4", "true").equals("false")) {
				_isUseIF4Command = false;
			}
			_beforeTime = Integer.parseInt(prop.getProperty("recollect.ondemand.beforeTime", "24"));
			if (prop.getProperty("recollect.ondemand.smsresponse", "false").equals("true")) {
				_enableSMSRes = true;
			}

			boolean bAutoRecovery = false;

			if ( fromDate == null || "".equals(fromDate)){
				Map<String, String> TFBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -24);
				String TFDate = TFBeforeHour.get("date").replace("-", "") + TFBeforeHour.get("time").replace(".", "");	
				//dateType = CommonConstants.DateType.DAILY;
				fromDate = TFDate.substring(0,8);
				bAutoRecovery = true;
			}

			List<Object> gaps = getGaps(fromDate);
			//logger.info("CountInfo: Total Meter to need recollect metering ["+ gaps.isEmpty() != null ? gaps.size()+"" : "0" + "]");
			int cnt = 0;


			int	i = 0;
			String execToDate = fromDate;

			if(!gaps.isEmpty()){
				if ( !deviceType.equals(DeviceType.Meter) && bAutoRecovery ){
					execToDate = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss").substring(0,8);
				}


				List<McuDeviceList> mculist = getMcuList2(gaps, _deviceMeter);	
				logger.info("START:CountInfo: MCU Count[{}] deviceType[{}] fromDate[{}] toDate[{}] dso[{}] targetsla[{}]",
						mculist.size(),
						deviceType,
						fromDate,
						execToDate,
						dso,
						targetSLA);
				//// for SubGiga 
				if (!excludeType.toLowerCase().contains("SubGiga".toLowerCase())) {
//					cnt = 0;
//					for ( int mi = 0; mi < mculist.size(); mi++){
//						StringBuffer sb = new StringBuffer();
//						sb.append("Recollect via MCU: MCU[" + mculist.get(mi).sysId+ "]　MCU_ID[" + mculist.get(mi).mcuId + "] MeterCount [" + mculist.get(mi).deviceIdList.size() + "]");
//						logger.debug(sb.toString());
//						cnt += mculist.get(mi).deviceIdList.size() ;
//					}
					cnt = 0;
					ExecutorService pool1 = Executors.newFixedThreadPool(_maxMcuThreadWorker);
					RecollectSubGigaThread threads1[] = new RecollectSubGigaThread[mculist.size()];

					i = 0;
					for (McuDeviceList mcuMeter : mculist) {
//						logger.info(cnt++ + ": MCU[" + mcuMeter.mcuId + "] MdsId(for call command)[" + mcuMeter.meterMdsId + "]");
						threads1[i] = new RecollectSubGigaThread(mcuMeter, fromDate+"000000", execToDate+"235959", _maxRecollectModem);
						pool1.execute(threads1[i]);
						i++;
					}
					logger.info("ExecutorService for SubGiga shutdown.");
					pool1.shutdown();
					logger.info("ExecutorService for SubGiga awaitTermination. [" + _timeout + "]sec");
					pool1.awaitTermination(_timeout, TimeUnit.SECONDS);

				}
				logger.info("END:CountInfo:SugGiga Result MCU[Success:{},Fail:{}] SubGiga[Success:{},Fail:{},Exclude:{},UnKnown[{}]",
						mcuResult[successIndex].get(),
						mcuResult[failIndex].get(),
						subGigaResult[successIndex].get(),
						subGigaResult[failIndex].get(),
						subGigaResult[excludeIndex].get(),
						subGigaResult[unknownIndex].get()
						);

				// MMIU 
				logger.info("START:CountInfo: MMIU Count[{}] deviceType[{}] fromDate[{}] toDate[{}] dso[{}] targetsla[{}]",
						gaps.size(),
						deviceType,
						fromDate,
						execToDate,
						dso,
						targetSLA);
				cnt = 0;
				i = 0;                
				ExecutorService pool = Executors.newFixedThreadPool(_maxThreadWorker);
				RecollectMMIUThread threads[] = new RecollectMMIUThread[gaps.size()];
				//logger.debug("Recollect Meters(for Earch) Count={}", gaps.size());
				for (Object obj : gaps) {
					HashMap<String,Object> resultMap = (HashMap<String, Object>) obj;
					String mdsId = (String) resultMap.get("mdsId");
					String mcuId = (String) resultMap.get("sysId");
					logger.info(cnt + ": Meter[" + mdsId + "] Recollect Metering");
					cnt++;
					threads[i] = new RecollectMMIUThread(cnt, mcuId, mdsId, deviceType, fromDate+"000000", execToDate+"235959", loginId);
					pool.execute(threads[i]);
					i++;
				}
				logger.info("ExecutorService for MMIU shutdown.");
				pool.shutdown();
				logger.info("ExecutorService for MMIU awaitTermination. [" + _timeout + "]sec");
				pool.awaitTermination(_timeout, TimeUnit.SECONDS);
				logger.info("END:CountInfo: MMIU Result GPRS[Success:{},Fail:{},Exclude:{},UnKnown:{}],SMS[Success:{},Fail:{},Exclude:{},UnKnown:{}] ",
						gprsResult[successIndex].get(),
						gprsResult[failIndex].get(),
						gprsResult[excludeIndex].get(),
						gprsResult[unknownIndex].get(),
						smsResult[successIndex].get(),
						smsResult[failIndex].get(),
						smsResult[excludeIndex].get(),
						smsResult[unknownIndex].get()
						);
			}
		}
		catch (Exception e) {
			logger.error("RecollectThread.run error - " + e, e);
		}
		finally {
		}
	}

	class RecollectMMIUThread extends Thread {

		private String mdsId;
		CommonConstants.DeviceType deviceType;
		String mcuId;
		String fromDate;
		String toDate;
		String loginId;
		CommonConstants.DateType dateType;
		int thrNo;
		RecollectMMIUThread(int thrNo,String mcuId, String mdsId, CommonConstants.DeviceType deviceType, String fromDate, String toDate, String roginId)  {
			this.thrNo = thrNo;
			this.mdsId = mdsId;
			this.deviceType = deviceType;
			this.fromDate = fromDate;
			this.toDate = toDate;
			this.loginId = roginId;
			this.mcuId = mcuId;
		}

		public void run() {
			TransactionStatus txstatus = null;
			//CommandGW commandGw = DataUtil.getBean(CommandGW.class);

			logger.info("[No={}] RecollectMMIUThread Start mdsId[{}] deviceType[{}] fromDate[{}] toDate[{}]", thrNo, mdsId, deviceType.name(), fromDate, toDate, loginId  );
			try { 
				Map result = null;
				Meter meter = meterDao.get(mdsId);

				if (meter == null ) {
					logger.error("Meter[{}] is null", mdsId);
					gprsResult[unknownIndex].getAndIncrement();
					throw new Exception("Meter[" + mdsId + "] is null");
				}
				Modem modem = meter.getModem();
				if (modem == null ) {
					logger.error("[{}] modem of Meter[" + mdsId + "] is null" ,mdsId);
					gprsResult[unknownIndex].getAndIncrement();
					throw new Exception("modem of Meter[" + mdsId + "] is null");
				}
				//txmanager.commit(txstatus);

				String nOption = "";
				logger.debug("[{}]  ModemType = {}, ProtocolType = {}, mbbTypeWithMCU = {}",
						mdsId, modem.getModemType().name(),  modem.getProtocolType().name(), mbbTypeWithMCU);
				if((modem.getModemType() == ModemType.MMIU) && (modem.getProtocolType() == Protocol.SMS)) {
					if (excludeType.toLowerCase().contains("SMS".toLowerCase())) {
						logger.info("Exclude MBB(SMS) meter[{}].", mdsId);
						smsResult[excludeIndex].getAndIncrement();
					}
					else {
						int modemPort = 0;
						if ( meter.getModemPort() != null ){
							modemPort = meter.getModemPort().intValue();
						}
						if ( modemPort > 5){
							logger.error("[{}] ModemPort: {} is not Support", mdsId, modemPort);
							smsResult[unknownIndex].getAndIncrement();
							throw new Exception("ModemPort:" + modemPort + " is not Support");
						} 

						if ( deviceType == CommonConstants.DeviceType.Meter ||
							(deviceType == CommonConstants.DeviceType.MCU && mbbTypeWithMCU.equals("Meter") )){
							result = onDemandMeterBypassMBB(meter, modem, fromDate, fromDate.substring(0,8)+ "235959", loginId); // from
						}
						else if ( deviceType == CommonConstants.DeviceType.Modem ||
								( deviceType == CommonConstants.DeviceType.MCU && mbbTypeWithMCU.equals("Modem"))){
							result = romReadBypassMBB(meter, modem, fromDate, toDate, loginId);
						}
						if ( result != null && result.get("result") != null 
								&& "Success".equalsIgnoreCase((String)result.get("result"))){
							logger.info("[{}] SMS Recollect Success", mdsId);
							smsResult[successIndex].getAndIncrement();
						}
						else {
							logger.error("[{}] SMS Recollect Fail", mdsId);
							smsResult[failIndex].getAndIncrement();
						}
					}				
				}
				else {
					if((modem.getModemType() == ModemType.MMIU) && (modem.getProtocolType() == Protocol.GPRS) &&
							(excludeType.toLowerCase().contains("GPRS".toLowerCase()))) {
						logger.info("Exclude MBB(GPRS) meter[{}].", mdsId);
						gprsResult[excludeIndex].getAndIncrement();
					}
					else {
						if ( deviceType == CommonConstants.DeviceType.Meter ||
								( deviceType == CommonConstants.DeviceType.MCU && mbbTypeWithMCU.equals("Meter"))){
							Class serverClazz = Class.forName(meter.getModel().getDeviceConfig().getSaverName());
							result = commandWS_onDemandMeterBypass(mcuId, mdsId, modem.getDeviceSerial(),  "", fromDate, fromDate.substring(0,8)+ "235959", serverClazz);	
							//        				result = commandGw.doOnDemand(meter, 0, loginId, nOption, fromDate, toDate);
						}
						else if ( deviceType == CommonConstants.DeviceType.Modem ||
								(deviceType == CommonConstants.DeviceType.MCU && mbbTypeWithMCU.equals("Modem"))){// "Modem" is default setting of mbbTypeWithMCU
							//            				result = commandGw.cmdGetROMRead(meter, 0, loginId, nOption, fromDate, toDate);
							result = commandGW_cmdGetROMRead(mcuId, mdsId, modem.getDeviceSerial(), nOption, fromDate, toDate);
						}
						if ( result != null && result.get("result") != null 
								&& "Success".equalsIgnoreCase((String)result.get("result"))){
							logger.info("[{}] GPRS Recollect Success", mdsId);
							gprsResult[successIndex].getAndIncrement();
						}
						else {
							logger.error("[{}] GPRS Recollect Fail", mdsId);
							gprsResult[failIndex].getAndIncrement();
						}
					}
				}
			}
			catch  (Exception e) {
				logger.error("[" +mdsId+ "] RecollectMMIUThread.run error - " + e, e);
				logger.error("[{}] RecollectMMIUThread Fail", mdsId);
			} finally {
				//txstatus = null;
			}

			logger.info("[No={}] RecollecThread End [{}]", thrNo, mdsId );
		}
	}
	class RecollectSubGigaThread extends Thread {

		McuDeviceList mcuDevices = null;
		int limitOfDevices = 1;
		String fromDate = null;
		String toDate = null;


		RecollectSubGigaThread(McuDeviceList mcuDevices,  String fromDate, String toDate, int limitOfDevices)
		{ 
			this.mcuDevices = mcuDevices;
			this.fromDate = fromDate;
			this.toDate = toDate;
			this.limitOfDevices = limitOfDevices;
		}
		public void run() {
			long threadId = Thread.currentThread().getId();

			logger.info("ThreadID[{}] MCU[{}] Meter(SubGiga)] collectNum[{}] deviceType[{}] Metering thread start.",
					threadId, mcuDevices.sysId,  mcuDevices.gapList.size(), deviceType);
			Map result = null;
			boolean mcnConnect = true;
			try {
				boolean errorExit = false;
				if ( deviceType == CommonConstants.DeviceType.MCU ) {
					List<String> deviceList = mcuDevices.deviceIdList;
					String mcuId = mcuDevices.sysId;
					String mdsId = mcuDevices.meterMdsId;
					String modemId  =  mcuDevices.deviceSerial;
					List<String> splitDevices = null;
					int k = deviceList.size() / limitOfDevices;
					int cnt = 0;

					logger.debug("limitOfDevices ["+  limitOfDevices + "]"  );
					logger.debug("k ["+  k + "]"  );
					for (int i = 0; i < k + 1; i++) {
						splitDevices = new ArrayList<String>();
						StringBuffer sb = new StringBuffer();
						for (int j = 0; j < limitOfDevices; j++) {
							if (cnt == deviceList.size()) {
								break;
							}
							logger.debug("device [" + i + "][" + j + "][" + (String)deviceList.get(cnt) + "]"  );
							sb.append((String)deviceList.get(cnt) + ",");
							splitDevices.add((String)deviceList.get(cnt++));
						}
						String[] devices = splitDevices.toArray(new String[splitDevices.size()]);

						String nOption = "";
						result = commandGW_cmdGetMeteringData(mcuId, mdsId, modemId, nOption, fromDate, toDate, devices);
						if ( result != null && result.get("result") != null 
								&& "Success".equalsIgnoreCase((String)result.get("result"))){
							logger.info("[{}] MCU Recollect Success [{}]", mcuId, sb.toString());
							mcuResult[successIndex].getAndIncrement();	
						}
						else {
							logger.error("[{}] MCU Recollect Fail [{}]", mcuId,sb.toString());
							mcuResult[failIndex].getAndIncrement();	
						}
					}
				}
				else { // Meter or Moden 
					String mcuId = mcuDevices.sysId;
					for ( Map<String,Object> gap : mcuDevices.gapList) {
						String mdsId = (String)gap.get("mdsId");
						String deviceSerial = (String)gap.get("deviceSerial");
						if ( !mcnConnect) {
							logger.error("[{}] SubGiga Recollect Fail(MCU[{}] is not connect)", mdsId,mcuId);
							subGigaResult[failIndex].getAndIncrement();	
							continue;
						}
						Meter meter = meterDao.get(mdsId);
						if (meter == null ) {
							logger.error("Meter[{}] is null", mdsId);
							subGigaResult[unknownIndex].getAndIncrement();	
							continue;
						}
						Modem modem = meter.getModem();
						if (modem == null ) {
							logger.error("Modem of Meter[{}] is null", mdsId);
							subGigaResult[unknownIndex].getAndIncrement();	
							continue;
						}
						if ( deviceType == CommonConstants.DeviceType.Meter){
							Class serverClazz = Class.forName(meter.getModel().getDeviceConfig().getSaverName());
							result = commandWS_onDemandMeterBypass(mcuId, mdsId, deviceSerial,  "", fromDate, toDate, serverClazz);	
							//		        			result = commandGw.doOnDemand(meter, 0, loginId, nOption, fromDate, toDate);
							logger.debug("[{}] call doOnDemand", mdsId);
						}
						else if ( deviceType == CommonConstants.DeviceType.Modem){
							if (_isUseIF4Command ) {
								if (  modem.getFwVer() != null && (modem.getFwVer().compareTo("1.2") >= 0)) {
									result = commandGW_cmdDmdNiGetRomRead(mcuId, mdsId, modem.getDeviceSerial(), fromDate, toDate, 3); // PollType(3) Timestamp             	
								} else {
									result = commandGW_cmdDmdNiGetRomRead(mcuId, mdsId, modem.getDeviceSerial(), fromDate, toDate, 2); // PollType(2) Offset and count
								}
							}else {
								result = commandGW_cmdGetROMRead(mcuId, mdsId, modem.getDeviceSerial(), "", fromDate, toDate);
							}
							
						}

						if ( result != null && result.get("result") != null 
								&& "Success".equalsIgnoreCase((String)result.get("result"))){
							logger.info("[{}] SubGiga Recollect Success", mdsId);
							subGigaResult[successIndex].getAndIncrement();	
						}
						else {
							logger.error("[{}] SubGiga Recollect Fail", mdsId);
							subGigaResult[failIndex].getAndIncrement();	
							if ( result.get("result") != null ) {
								String errMsg = (String)(result.get("result"));
								if ( errMsg.contains("Can't connect to DCU")) {
									mcnConnect= false;
								}
							}
						}
					}
				}

			} catch (Exception ex) {
				logger.error("Error -" + ex, ex);
				logger.info("ThreadID[{}] SubGiga Recollect Metering thread end. MCU[{}] is  failed.", 
						threadId, mcuDevices.sysId);	
			} 
			logger.info("ThreadID[{}] MCU[{}] Meter(SubGiga) collect[{}] Metering thread end.",
					threadId, mcuDevices.sysId, mcuDevices.gapList.size());
		}
	}
	/*
	 * Send SMS
	 */
	public Map sendSmsForCmdServer(Modem modem,  String messageType, String commandCode, String commandName, Map<String, String> paramMap) throws Exception {
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
			if ( mobliePhNum == null ) {
				throw new Exception("FAIL : Phone number is empty!");
			}
			euiId = modem.getDeviceSerial();

			condition.put("messageType", messageType);
			condition.put("mobliePhNum", mobliePhNum);
			condition.put("euiId", euiId);
			condition.put("commandCode", commandCode);
			condition.put("commandName", commandName);

			List<String> paramListForSMS = new ArrayList<String>();
			Properties prop = new Properties();
			try{
				prop.load(getClass().getClassLoader().getResourceAsStream("config/fmp.properties"));
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
		//		String messageId = cmdOperationUtil.sendSMS(condition, paramList, cmdMap);
		String messageId = CommandGW_sendSMS(condition,paramList,cmdMap);
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


	private Map<String, String> onDemandMeterBypassMBB(Meter meter, Modem modem, String fromDate, String toDate, String loginId) {

		logger.info("CallSMS onDemandMeterBypassMBB Start mdsId[{}] fromDate[{}] toDate[{}] loginId[{}]",
				meter.getMdsId(), fromDate,toDate,loginId);
		ResultStatus status = ResultStatus.FAIL;
		//Meter meter = null;
		String mdsId = meter.getMdsId();
		String cmd = "cmdMeterParamGet";
//		String detailInfo = "";
//		String rtnStr = "";
		Map<String,String> returnMap = new HashMap<String,String>(); 
		//JSONArray jsonArr = null;
		try{
			//    		if (loginId != null ){
			//    			if (!commandAuthCheck(loginId,  "8.1.10")) {
			//    				throw new Exception("No permission");
			//    			}
			//    		}

			//meter = meterDao.get(mdsId);
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

				Map<String,String> valueMap = Util.getParamValueByRange(fromDate,toDate);
				String value = CommandGW.meterParamMapToJSON(valueMap);

				logger.debug("[{}] ObisCode=> {}, classID => {}, attributeId => ", mdsId, obisCode,classId,attrId);
				//paramGet
				paramMap.put("paramGet", obisCode+"|"+classId+"|"+attrId+"|null|null|"+value);				
			}
			else {
				logger.debug("cmdGetLoadProfile ["+ mdsId + "][" + modemPort +  "]["  +  fromDate + "][" +toDate +"]");

				String obisCode = DataUtil.convertObis(OBIS.MBUSMASTER_LOAD_PROFILE.getCode());
				int classId = DLMS_CLASS.PROFILE_GENERIC.getClazz();
				int attrId = DLMS_CLASS_ATTR.PROFILE_GENERIC_ATTR02.getAttr();

				Map<String,String> valueMap = Util.getParamValueByRange(fromDate,toDate);
				String value = CommandGW.meterParamMapToJSON(valueMap);

				logger.debug("[{}] ObisCode=> {}, classID => {}, attributeId => ", mdsId, obisCode, classId, attrId);
				//paramGet
				paramMap.put("paramGet", obisCode+"|"+classId+"|"+attrId+"|null|null|"+value);
			}
			paramMap.put("option", "ondemandmbb");				


			Map<String,Object> map = new HashMap<String,Object>();
			try{        		
				if(meter != null && meter.getModem() != null) {
					//Modem modem = meter.getModem();
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
					//        			MMIU mmiu = (MMIU)map.get("modem");
					//	        		
					//	        		String mobileNo = mmiu.getPhoneNumber();
					//	            	if (mobileNo == null || "".equals(mobileNo)) {
					//	            		logger.warn(String.format("[" + cmd + "] Phone number is empty"));
					//	            		rtnStr = "FAIL : Phone number is empty!";
					//	        		}            	
					//	            	else if (!Protocol.SMS.equals(mmiu.getProtocolType())) {
					//	            		logger.warn(String.format("[" + cmd + "] Invalid ProtocolType"));
					//	            		rtnStr = "FAIL : Invalid ProtocolType!";
					//	    			}
					//	            	else {	            	
					Long trId = System.currentTimeMillis();
					Map<String, String> result;
					String cmdResult = "";

					result = sendSmsForCmdServer(modem,SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), cmd, paramMap); 						
					if(result != null){
//						cmdResult = "Success";							
//						status = ResultStatus.SUCCESS;
//						returnMap = new HashMap<String,String>();
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
						returnMap.put("result", "Fail[sendSmsForCmdServer Fail]");
						//cmdResult="Failed to get the resopone. See the Async Command History.";
						//cmdResult="Check the Async_Command_History.";								
					}						
					//rtnStr = cmdResult;
					//	            	}
				} 
			} catch (Exception e) {
				logger.error("[" + mdsId + "] onDemandMeterBypassMBB excute error - " + e, e);
				returnMap.put("result", "Failure[" + mdsId + " :onDemandMeterBypassMBB excute error]");
			}

		} catch (Exception e) {
			logger.warn("[" + mdsId + "] onDemandMeterBypassMBB excute error - " + e,e);
			returnMap.put("result", "Failure[" + mdsId + " :onDemandMeterBypassMBB excute error]");
		}

		return returnMap;
	}

	private Map<String, String> romReadBypassMBB(Meter meter, Modem modem, String fromDate, String toDate, String loginId) {
		logger.info("CallSMS romReadBypassMBB Start mdsId[{}] fromDate[{}] toDate[{}] loginId[{}]",
				meter.getMdsId(), fromDate,toDate,loginId);

		ResultStatus status = ResultStatus.FAIL;
		//Meter meter = null;
		String cmd = "cmdGetROMRead";

		Map<String, String> returnMap = new HashMap<String,String>();

		//meter = meterDao.get(mdsId);
		//Modem modem = meter.getModem();
		String mdsId = meter.getMdsId();
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
					returnMap.put("result","[" + mdsId + "] SMS Fail");
				}
			}
		}catch(Exception e){
			logger.error("[" + mdsId + "] romReadBypassMBB  excute error - " + e,e);
			returnMap.put("result", "Failure[" + mdsId + " :romReadBypassMBB excute error]");
		}
		return returnMap;
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
		logger.info("#### RecollectMeteringSoriaForOneDay start. ###");

		//CommonConstants.DateType  dateType = null ;
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
			else if ( nextArg.startsWith("-fromDate")){
				if ( !"${fromDate}".equals(args[i + 1]))
					fromDate = new String(args[i + 1]);
			}

			else if ( nextArg.startsWith("-mbbWithMcu")){
				if ( !"${mbbWithMcu}".equals(args[i + 1])){
					mbbWithMcu = new String(args[i + 1]);
				}
			}			
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
			else if ( nextArg.startsWith("-sla")){
				if ( !"${sla}".equals(args[i + 1])){
					sla = new String(args[i + 1]);
				}
			}

		}

		if ( !checkDate(fromDate)) {
			fromDate = null;
		}

		if (deviceType == null ) {
			deviceType = "Meter";
		}
		if (supplierName == null ){
			supplierName = "";
		}

		if ((mbbWithMcu == null ) || (mbbWithMcu.equals(""))){
			mbbWithMcu = "Modem";
		}
		
		logger.info("START: RecollectMeteringSoriaForOneDay . devicetype={} supplierName={} fromDate={} dso={} mbbWithMcu={}", 
				deviceType, supplierName,  fromDate, dso, mbbWithMcu);
		try {
			ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "config/spring-fep-schedule2.xml" });
			DataUtil.setApplicationContext(ctx);

			RecollectMeteringSoriaForOneDay task = (RecollectMeteringSoriaForOneDay) ctx.getBean(RecollectMeteringSoriaForOneDay.class);

			task.setSupplierName(supplierName);
			task.setLoginId(loginId);
			task.setFromDate(fromDate);

			if ( CommonConstants.DeviceType.Modem.name().equals(deviceType)){
				task.setDeviceType(CommonConstants.DeviceType.Modem);
			}
			else if (CommonConstants.DeviceType.Meter.name().equals(deviceType)){
				task.setDeviceType(CommonConstants.DeviceType.Meter);
			}
			else if (CommonConstants.DeviceType.MCU.name().equals(deviceType)){
				task.setDeviceType(CommonConstants.DeviceType.MCU);
			}
			else {
				logger.error("Unknown deviceType");
				System.exit(1);
			}

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

	private static boolean checkDate(String yyyymmdd) {
		boolean ret = false;
		if ( yyyymmdd == null || yyyymmdd.length() != 8 ) {
			return ret;
		}
		try {
			Date fDate =  DateTimeUtil.getDateFromYYYYMMDD(yyyymmdd);
			ret = true;
		}
		catch (Exception e) {
			logger.error("checkDate error - " + e, e);
		}
		return false;
	}

	public String CommandGW_sendSMS(Map<String, Object> condition, List<String> parameterList, String cmdMap) throws Exception {
		String messageType = condition.get("messageType").toString();
		String mobliePhNum = condition.get("mobliePhNum").toString();
		String euiId = condition.get("euiId").toString();
		String commandCode = condition.get("commandCode").toString();
		String commandName = condition.get("commandName").toString();
		List<String> paramList = parameterList;
		String rtnString;

		logger.info("CallGW_sendSMS ==> commandName : " + commandName + ", messageType : " + messageType + ", mobliePhNum : "
				+ mobliePhNum + ", euiId : " + euiId + ", commandCode : " + commandCode + ", paramList : " + paramList + ", cmdMap : " + cmdMap);

		CommandGW commandGW = DataUtil.getBean(CommandGW.class);
		rtnString = commandGW.sendSMS(commandName, messageType, mobliePhNum, euiId, commandCode, paramList, cmdMap);

		return rtnString;
	}

	// DeviceType == METER
	// MdemType.SubGiga ||
	//  ( ModemType.MMIU && (  Protocol.GPRS or  Protocol.IP ))
	private Map<String,String>  commandWS_onDemandMeterBypass(String mcuId,String meterId, String modemId, String nOption, String fromDate, String toDate , Class serverClass)  throws Exception {
		logger.info("CallWS_onDemandMeterBypas mcuId[" + mcuId + "] meterId[" + meterId+"] modemId[" + modemId+"] nOption[" + nOption + "] fromDate[" + fromDate + "] toDate[" + toDate +"]");
		Map<String,String> result = new HashMap<String,String>();
		try {
			AbstractMDSaver saver = (AbstractMDSaver)DataUtil.getBean(serverClass);        
			MeterData md = saver.onDemandMeterBypass(mcuId, meterId, modemId, nOption, fromDate, toDate);
			result.put("result", "Success");
		} catch (Exception ex) {
			logger.error("onDemandMeterBypass error - " + ex, ex);
			result.put("result", "Failure["+ex.getMessage()+"]");
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public Map<String,String> commandGW_cmdDmdNiGetRomRead(String mcuId,String meterId,String modemId,String fromDate,String toDate,int pollType )
			throws Exception
	{
		Map<String,String> result = new HashMap<String,String>();
		logger.info("CallGW_cmdDmdNiGetRomRead mcuId[" + mcuId + "] meterId[" + meterId+"] modemId[" + modemId+"] fromDate[" + fromDate + "] toDate[" + toDate +"] pollType[" + pollType + "]");

		try {
			//CommandGW commandGw = DataUtil.getBean(CommandGW.class);
			MeterData md = commandGW.cmdDmdNiGetRomRead(mcuId, meterId, modemId, fromDate, toDate, pollType);
			// UPDATE END SP-681
			result.put("result", "Success");
		}
		catch (Exception ex) {
			logger.error("cmdDmdNiGetRomRead error - " + ex, ex);
			result.put("result", "Failure["+ex.getMessage()+"]");
		}
		return result;
	}


	public Map<String,String> commandGW_cmdGetROMRead(String mcuId, String meterId,String modemId, String nOption, String fromDate,String toDate )
			throws Exception
	{
		Map<String,String> result = new HashMap<String,String>();

		logger.info("CallGW_cmdGetROMRead mcuId[" + mcuId + "] meterId[" + meterId+"] modemId[" + modemId+"] fromDate[" + fromDate + "] toDate[" + toDate +"] nOption[" + nOption + "]");
		try {
			//CommandGW commandGW = DataUtil.getBean(CommandGW.class);
			MeterData md = commandGW.cmdGetROMRead(mcuId, meterId, modemId, nOption, fromDate, toDate);
			result.put("result", "Success");
		}
		catch (Exception ex) {
			logger.error("cmdGetROMRead error - " + ex, ex);
			result.put("result", "Failure["+ex.getMessage()+"]");
		}
		return result;
	}

	public Map<String,String> commandGW_cmdGetMeteringData(String mcuId, String meterId, String modemId, String nOption, String fromDate,
			String toDate, String[] modemArray )
					throws Exception
	{
		Map<String,String> result = new HashMap<String,String>();
		logger.info("CallGW_cmdGetMeteringData mcuId[" + mcuId + "] meterId[" + meterId+"] modemId[" + modemId+"] fromDate[" + fromDate + "] toDate[" + toDate +"] nOption[" + nOption + "] modemArray["+ String.join(",",modemArray) + "]");
		try {
			//CommandGW commandGW = DataUtil.getBean(CommandGW.class);
			MeterData md = commandGW.cmdGetMeteringData(mcuId, meterId, modemId, nOption, fromDate, toDate, modemArray);
			result.put("result", "Success");
		}
		catch (Exception ex) {
			logger.error("cmdGetMeteringData error - " + ex, ex);
			result.put("result", "Failure["+ex.getMessage()+"]");
		}
		return result;
	}
}
