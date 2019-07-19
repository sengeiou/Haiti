package com.aimir.schedule.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
import com.aimir.constants.CommonConstants.ModemIFType;
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
import com.aimir.fep.meter.data.MeterData;
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
 * Create 2017/05/12 SP-677
 *
 */
@Service
public class RecollectMeteringSoriaTask2 extends ScheduleTask 
{
	private static Logger logger = LoggerFactory.getLogger(RecollectMeteringSoriaTask2.class);
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
	private MeterType 	meterType;
	private boolean 	forceOption;
	private boolean     showList = false;
	private String		sysId;
	private boolean 	mbbCollectWithMcu = false;
	/**
	 * @return the sysId
	 */
	public String getSysId() {
		return sysId;
	}



	/**
	 * @param sysId the sysId to set
	 */
	public void setSysId(String sysId) {
		this.sysId = sysId;
	}



	/**
	 * @return the showList
	 */
	public boolean isShowList() {
		return showList;
	}


	public void setMbbCollectWithMcu(boolean mbbCollectWithMcu){
		this.mbbCollectWithMcu = mbbCollectWithMcu;
	}
	/**
	 * @param showList the showList to set
	 */
	public void setShowList(boolean showList) {
		this.showList = showList;
	}
	private boolean		_isUseIF4Command = true;
	private int			_beforeTime = 24;
	private int			_dayBefore = 7;
	private int 		_stepDays = 1;

	private String		_startDate= ""; // Start Date
	private String		_endDate = "";  // End Date
	int _maxThreadWorker = 5;
	int _maxMcuThreadWorker = 5;	// INSERT SP-633 
	int _maxRecollectMcuDevice = 400;	// INSERT SP-633
	int _maxRecollectModemDevice = 5;
	int _waitMcu = 2;  // wait for mcu command
	int _deviceMeter = 0;	// INSERT SP-633
	long _timeout = 3600;
	List<Map<String,String>> _fromToDateList = null;
	private CommonConstants.DateType dateType;

	/// for test KORIADEV
	// SYS_ID=781
	//	String mdsIds[] = {"TEST120000000312",
	//			"TEST120000000183",
	//			"TEST120000000324",
	//			"TEST120000000353",
	//			"TEST120000000068",
	//			"TEST120000000179",
	//			"TEST120000000069",
	//			"TEST120000000017",
	//			"TEST120000000199",
	//			"TEST120000000198"};
	// SYS_ID=778
	String testMdsIds[] = {"5100000000000012",
			"5100000000000028",
			"5100000000000030",
			"6970631400018795",
			"6970631400019778",
			"6970631400019785"
	};

	////
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
	/**
	 * @return the meterType
	 */
	public MeterType getMeterType() {
		return meterType;
	}

	/**
	 * @param meterType the meterType to set
	 */
	public void setMeterType(MeterType meterType) {
		this.meterType = meterType;
	}

	private void waitMcuCommand()
	{
		if ( _waitMcu > 0){
			try {
				logger.debug("wait start");
				Thread.sleep(_waitMcu * 1000);
				logger.debug("wait end");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage(),e);
			}
		}
	}
	private List<Object> makeGapByMeter(String mdsId)
	{
		List<Object> gaps = new ArrayList<Object>();
		HashMap<String,Object> map = new HashMap<String, Object>();
		
		Meter meter = meterDao.get(mdsId);
		map.put("mdsId", mdsId);
		if ( meter != null ){
			Modem modem = meter.getModem();
			map.put("deviceSerial", modem.getDeviceSerial());
			if ( modem.getMcu() == null ){
				if ( modem.getModemType() == ModemType.MMIU && modem.getProtocolType() == Protocol.SMS ) {
					map.put(ModemIFType.MBB.name(), "true");
				}
				// INSERT START SP-993
				else if ( modem.getModemType() == ModemType.MMIU && modem.getProtocolType() == Protocol.GPRS ) {
					map.put(ModemIFType.MBB.name(), "true");
				}
				// INSERT END SP-993				
			}
			else {	
				map.put("sysId", modem.getMcu().getSysID());
			}
			gaps.add(map);
		}
		return gaps;
	}
	private List<Object> getGaps(String fromDate, String toDate,MeterType[] meterTypes, String sysId, String mdsId )
	{
		long threadId = Thread.currentThread().getId();
		TransactionStatus txstatus = null;	
		List<Object> allGaps = new ArrayList<Object>();
		try {
			txstatus = txmanager.getTransaction(null);	

			if ( forceOption == true && meterId != null && !"".equals(meterId)){
				makeGapByMeter(meterId);
			}
			else {
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
	
				// UPDATE END SP-476
				calendar.add(Calendar.HOUR, (_beforeTime*-1));	
	
				Date before24Date = calendar.getTime();
				int cnt2 = 0;
				for ( MeterType meterType : meterTypes){
					Map<String, Object> params = new HashMap<String, Object>();  
					params.put("channel", 1);
					params.put("searchStartDate",fromDate);
					params.put("searchEndDate", toDate);
					params.put("dateType", CommonConstants.DateType.HOURLY.getCode());
					params.put("meterType", meterType.name());
					params.put("supplierId", supplierId);
		    		params.put("lastLinkTime", DateTimeUtil.getDateString(before24Date));
					if ( meterId != null && !"".equals(meterId)){
						params.put("mdsId", meterId);
					}
					else if (mdsId != null && !"".equals(mdsId)) {
						params.put("mdsId", mdsId);
						
					}
					if ( sysId != null && !"".equals(sysId)){
						params.put("sysId", sysId);
					}
					List<Object> gaps  = null;
					if ( dateType == CommonConstants.DateType.DAILY ){
						logger.debug("ThreadID[{}] getMissingMeters:params={}", threadId, params);
						gaps = meterDao.getMissingMetersForRecollect(params); // UPDATE SP-784-2
					}
	//				else {
	//					logger.debug("getMissingMetersForRecollectByHour:params={}", params);
	//					gaps = meterDao.getMissingMetersForRecollectByHour(params);
	//				}
					StringBuffer sbuf = new StringBuffer();
					StringBuffer sbuf2 = new StringBuffer();
					// UPDATE START SP-784-2
//					for (Object obj : gaps) {
//						boolean link = false;
//						HashMap<String,Object> resultMap = (HashMap<String, Object>) obj;
//						Meter meter = meterDao.get((String)resultMap.get("mdsId"));
//						if ( meter != null ){
//							Modem modem = meter.getModem();
//							resultMap.put("port", meter.getModemPort());
//							String lastLinkTime = null;
//							if ( modem != null && (lastLinkTime = modem.getLastLinkTime()) != null){
//								Date lastLinkDate = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(lastLinkTime);
////								logger.debug("Meter[{}] lastLinkDtime[{}] before24[{}]", 
////										(String)resultMap.get("mdsId"), modem.getLastLinkTime(), before24Date.toString());
//								if ( before24Date.before(lastLinkDate)){
//									resultMap.put("deviceSerial", modem.getDeviceSerial());
//									if ( modem.getMcu() ==null ){
//										if ( modem.getModemType() == ModemType.MMIU && modem.getProtocolType() == Protocol.SMS ) {
//											resultMap.put(ModemIFType.Ethernet.name(), "true");
//										}
//									}
//									else {	
//										resultMap.put("sysId", modem.getMcu().getSysID());
//									}
//									allGaps.add(obj);
//									sbuf.append("'" + (String) resultMap.get("mdsId") + "',");
//									link = true;
//								}
//							}
//						}
//						if ( link == false){
//							sbuf2.append("'" + (String) resultMap.get("mdsId") + "',");
//							cnt2++;
//						}
//					}
					
//		    		for (Object obj : gaps) {
//		    			HashMap<String,Object> resultMap = (HashMap<String, Object>) obj;
//		    			sbuf.append("'" + (String) resultMap.get("mdsId") + "',");
//		    		}
		    		// UPDATE END  SP-784-2
//		    		logger.debug("Missing Meters({}) Count= {}:{}", meterType.name(),gaps.size(), sbuf.toString());
		    		allGaps.addAll(gaps);
					//    			logger.debug("Missing Meters({}){}", meterType.name(),sbuf.toString());
					//       			logger.debug("Missing but Modem is null or Last Link Date is before {} ({}):{}", 
					//       					new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(before24Date), meterType.name(),sbuf2.toString());	
				}
				logger.info("ThreadID[{}] Missing Meters: Collect Meter Count[{}] Not Collect Meter Count[{}]", 
						threadId, allGaps.size(), cnt2);
			}
			if (txstatus != null&& !txstatus.isCompleted()){
				txmanager.commit(txstatus);
			}
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
//		Integer mcuId;
		String  sysId;
		List<String> deviceIdList;
		String	meterMdsId;

		public boolean equals(Object obj){
			McuDeviceList t = (McuDeviceList)obj;
			if (this.sysId.compareTo(t.sysId) == 0 ) return true;
			else return false;
		}
	}
	/*
	private List<McuDeviceList> getMcuList(List<Object> gaps, int deviceMeter) {

		TransactionStatus txstatus = null;	
		List<McuDeviceList> mcuList = new ArrayList<McuDeviceList>();
		
		try{
			logger.debug("getMcuList(gaps[" + gaps.size() + "])");

			List<Object> orgList = new ArrayList<Object>();

			orgList.addAll(gaps);
			gaps.clear();

			//txstatus = txmanager.getTransaction(null);	
			
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
					logger.debug("MCU[" + mcu.getSysID() + "] . Meter[" + meter.getMdsId() + "] . Modem[" + modem.getDeviceSerial() + "] .");
					int i;
					for (i = 0; i < mcuList.size(); i++) {	// Search the same mcu.
						if ( mcuList.get(i).mcuId.compareTo(mcu.getId())==0 ) { 
							break;
						}
					}
					if (i < mcuList.size()) {	// find!
						//						logger.debug("MCU[" + mcu.getId() + "] has been already listed. Meter[" + mdsId + "],Modem[" + modem.getDeviceSerial() + " is added.");
						if ( deviceMeter==1 ){	// meter
							mcuList.get(i).deviceIdList.add(mdsId);
						} else {	// modem
							mcuList.get(i).deviceIdList.add(modem.getDeviceSerial());
						}
						continue;
					}
					McuDeviceList mml = new McuDeviceList();
					mml.mcuId = mcu.getId();
					mml.sysId = mcu.getSysID();
					mml.deviceIdList = new ArrayList<String>();
					if ( deviceMeter==1 ){	// meter
						mml.deviceIdList.add(mdsId);
					}else{	// modem
						mml.deviceIdList.add(modem.getDeviceSerial());
					}
					mml.meterMdsId = mdsId;
					mcuList.add(mml);
				} else {
					if ( modem.getModemType() == ModemType.MMIU && modem.getProtocolType() == Protocol.SMS ) {
						gaps.add(obj); 
					}
					else {
						logger.debug("Meter [{}] is ignored. (not connect MCU and not MMIU(SMS))", mdsId);
					}
				}
			}
			if (txstatus != null&& !txstatus.isCompleted()){
				txmanager.commit(txstatus);
			}
		}
		catch (Exception e) {
			logger.error("RecollectThread.run error - " + e, e);
			if (txstatus != null&& !txstatus.isCompleted())
				txmanager.rollback(txstatus);
		}
		return mcuList;
	} 
	*/
	private List<McuDeviceList> getMcuList(List<Object> gaps, int deviceMeter) {
		/*
		 * Output List<McuMeterList> is list of McuMeterList. 
		 * Output meters is list of meter which doesn't have a mcu.
		 */
		TransactionStatus txstatus = null;	
		List<McuDeviceList> mcuList = new ArrayList<McuDeviceList>();
		
		try{
			logger.debug("getMcuList(gaps[" + gaps.size() + "])");

			List<Object> orgList = new ArrayList<Object>();

			orgList.addAll(gaps);
			gaps.clear();

			//txstatus = txmanager.getTransaction(null);	
			
			for (Object obj : orgList) {
				HashMap<String,Object> resultMap = (HashMap<String, Object>) obj;
				String mdsId = (String) resultMap.get("mdsId");
				String deviceSerial = (String) resultMap.get("deviceSerial");
				String sysId = (String) resultMap.get("sysId");
				
				if ( sysId != null) {
//					logger.debug("MCU[" + sysId + "] . Meter[" + mdsId + "] . Modem[" + deviceSerial + "] .");
					int i;
					for (i = 0; i < mcuList.size(); i++) {	// Search the same mcu.
						if ( mcuList.get(i).sysId.compareTo(sysId)==0 ) { 
							break;
						}
					}
					if (i < mcuList.size()) {	// find!
						//						logger.debug("MCU[" + mcu.getId() + "] has been already listed. Meter[" + mdsId + "],Modem[" + modem.getDeviceSerial() + " is added.");
						if ( deviceMeter==1 ){	// meter
							mcuList.get(i).deviceIdList.add(mdsId);
						} else {	// modem
							mcuList.get(i).deviceIdList.add(deviceSerial);
						}
						continue;
					}
					McuDeviceList mml = new McuDeviceList();
					mml.sysId = sysId;
					mml.deviceIdList = new ArrayList<String>();
					if ( deviceMeter==1 ){	// meter
						mml.deviceIdList.add(mdsId);
					}else{	// modem
						mml.deviceIdList.add(deviceSerial);
					}
					mml.meterMdsId = mdsId;
					mcuList.add(mml);
				} else {
					// UPDATE START SP-993
//					if ( resultMap.get(ModemIFType.MBB.name()) != null ||
//						(resultMap.get("modemType") != null & ModemType.MMIU.name().equals(resultMap.get("modemType")) 
//								&& resultMap.get("protocolType") != null && Protocol.SMS.name().equals(resultMap.get("protocolType")))
//							){
					if ( resultMap.get(ModemIFType.MBB.name()) != null ||
							(resultMap.get("modemType") != null & ModemType.MMIU.name().equals(resultMap.get("modemType")) 
									&& resultMap.get("protocolType") != null && Protocol.SMS.name().equals(resultMap.get("protocolType"))) ||
							(resultMap.get("modemType") != null & ModemType.MMIU.name().equals(resultMap.get("modemType")) 
							&& resultMap.get("protocolType") != null && Protocol.GPRS.name().equals(resultMap.get("protocolType")))
							
								){
						// UPDATE END SP-993
					
						gaps.add(obj); 
					}
					else {
						logger.debug("Meter [{}] is ignored. (not connect MCU and not MMIU(SMS))", mdsId);
					}
				}
			}
		}
		catch (Exception e) {
			logger.error("RecollectThread.run error - " + e, e);
		}
		return mcuList;
	} 
	public class McuPortDeviceList {
		Integer mcuId;
		String sysId;
		HashMap<Integer,ArrayList<String>> portDeviceList;

		public boolean equals(Object obj){
			
			McuPortDeviceList t = null;
			if(obj == null) {
				return false;
			}
			if(obj instanceof McuPortDeviceList) {
				t = (McuPortDeviceList)obj;
			}else {
				return false;
			}
			
			if (t != null && this.mcuId.equals(t.mcuId)) return true;
			else return false;
		}
	}

	private LinkedHashMap<Integer , HashMap<String, ArrayList<String>>> getPortList(List<Object> gaps) {
		try{
			logger.debug("getPortList(gaps[" + gaps.size() + "])");

			LinkedHashMap<Integer , HashMap<String, ArrayList<String>>> portHash = new LinkedHashMap<Integer , HashMap<String, ArrayList<String>>> ();
			String sysId = null;

			for (Object obj : gaps) {
				HashMap<String,Object> resultMap = (HashMap<String, Object>) obj;
				String mdsId = (String) resultMap.get("mdsId");
				String deviceSerial = (String) resultMap.get("deviceSerial");
				if (deviceSerial == null) {
					logger.debug("Meter[" + mdsId + "] . Modem is null .");
					continue;
				}
				sysId = (String) resultMap.get("sysId");
				if (sysId != null) {
					Integer modemPort = (Integer)resultMap.get("port");
					if ( modemPort == null)
						modemPort = 0;

					HashMap<String, ArrayList<String>> deviceListMap = portHash.get(modemPort);
					if ( deviceListMap == null ){
						deviceListMap = new HashMap<String, ArrayList<String>>();
						ArrayList<String> meterIdList = new ArrayList<String>();
						ArrayList<String> modemIdList = new ArrayList<String>();
						deviceListMap.put("meter", meterIdList);
						deviceListMap.put("modem", modemIdList);
						portHash.put(modemPort, deviceListMap);
					}
					deviceListMap.get("meter").add(mdsId);
					deviceListMap.get("modem").add(deviceSerial);
				}
			}
			return portHash;
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
		List<McuDeviceList> mcuList = new ArrayList<McuDeviceList>();
		try{
			logger.debug("getMcuList2(gaps[" + gaps.size() + "])");

			gaps.clear();

			int idx = 0;

			for (idx=0; idx<testMdsIds.length; idx++) {
				String mdsId = testMdsIds[idx];
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
						if ( mcuList.get(i).sysId.compareTo(mcu.getSysID())==0 ) { 
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
					//mml.mcuId = mcu.getId();
					mml.sysId = mcu.getSysID();
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
			return mcuList;
		}   	
	}    
	// INSERT END SP-633
	private LinkedHashMap<Integer , HashMap<String, ArrayList<String>>> getPortList_KORIAENV_TEST(List<Object> gaps) {
		try{
			//logger.debug("getPortList(gaps[" + gaps.size() + "])");

			LinkedHashMap<Integer , HashMap<String, ArrayList<String>>> portHash = new LinkedHashMap<Integer , HashMap<String, ArrayList<String>>> ();
			String sysId = null;

			ArrayList<Object> dummyGap = new ArrayList<Object>();

			for ( int i = 0; i < testMdsIds.length; i++){
				HashMap<String,Object> resultMap = new HashMap<String, Object>();
				resultMap.put("mdsId", testMdsIds[i]);
				dummyGap.add(resultMap);
			}
			for (Object obj : dummyGap ){
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
					if ( sysId == null )
						sysId = mcu.getSysID();
					else if ( !sysId.equals(mcu.getSysID())){
						logger.error("mcu of meter {} is not match {}", mcu.getSysID(), sysId);
						continue;
					}
					Integer modemPort = 0;
					if ( meter.getModemPort() != null )
						modemPort = meter.getModemPort();

					HashMap<String, ArrayList<String>> deviceListMap = portHash.get(modemPort);
					if ( deviceListMap == null ){
						deviceListMap = new HashMap<String, ArrayList<String>>();
						ArrayList<String> meterIdList = new ArrayList<String>();
						ArrayList<String> modemIdList = new ArrayList<String>();
						deviceListMap.put("meter", meterIdList);
						deviceListMap.put("modem", modemIdList);
						portHash.put(modemPort, deviceListMap);
					}
					deviceListMap.get("meter").add(mdsId);
					deviceListMap.get("modem").add(modem.getDeviceSerial());
				}
			}
			return portHash;
		}
		catch (Exception e) {
			logger.debug(e.getMessage());
			return null;
		}  	
	}    	
	@Override
	public void execute(JobExecutionContext context) {
		TransactionStatus txstatus = null;

		//Properties prop = new Properties();
		
		final ExecutorService executor = Executors.newCachedThreadPool();
		List<Future<Long>> list = new ArrayList<Future<Long>>();
		
		try {
			loadProperties();

			getFromToDates();
			logger.info("Recollect DeviceType:{} MeterType:{} StartDate:{} EndDate:{} MbbCollectWithMCU:{}", deviceType.name(), meterType.name(), _startDate, _endDate, mbbCollectWithMcu);


			MeterType[] meterTypes = {meterType};
			List<McuDeviceList> mcuDevices = null;
			List<Object> gaps = new ArrayList<Object>();
			
			// get gaps all period
			if ( sysId != null && !"".equals(sysId)){
				 gaps = getGaps(_startDate, _endDate, meterTypes, sysId, "");
			}
			else {
				gaps = getGaps(_startDate, _endDate, meterTypes, "", "");
			}
				
			logger.info("Total Meter to need recollect metering ["+ gaps.size() + "]");
			int cnt = 0;
	
//			// INSERT START SP-476
//			if ((gaps.size() == 0) && (forceOption == true)) {
//				Map<String, Object> obj = new HashMap<String, Object>();
//				obj.put("mdsId", meterId);
//				gaps.add(obj);
//				logger.debug("Forced option is specified.");
//			}
//			// INSERT END SP-476

			if ( gaps.size() == 0 ){
				logger.info("No Meters to recollect,exit.");
			}
		
			mcuDevices = getMcuList(gaps, 1);
			
			/////////////for TEST KOREA
			//List<McuDeviceList> mcuDevices =getMcuList_KOREANENV_TEST(gaps, _deviceMeter);

			logger.info("============= Target Devices from[{}] to[{}]=================",
					_startDate, _endDate);
			for ( McuDeviceList mcudev : mcuDevices){
				logger.info("----------MCU[{}] Meter Count[{}]----------", mcudev.sysId, mcudev.deviceIdList.size());
				for(String device:mcudev.deviceIdList){
					logger.info("MCU[{}] Meter[{}]", mcudev.sysId, device);
				}
			}
			logger.info("----------MBB MeterCount[{}]----------", gaps.size());
			for (Object obj : gaps) {
				HashMap<String,Object> resultMap = (HashMap<String, Object>) obj;
				logger.info("Modem[{}] Meter[{}]",(String) resultMap.get("deviceSerial"), (String) resultMap.get("mdsId"));
			}
			logger.info("================================================================");
			if ( showList ){	
				return;
			}
			if (deviceType == CommonConstants.DeviceType.MCU) {
				// Recollect via MCU
				
				Future<Long> future = (Future<Long>) executor.submit(new McuRecollectStartThread(mcuDevices));
				list.add(future);
				if ( mbbCollectWithMcu ){
					// Recollect MBB Meters
					future = (Future<Long>) executor.submit(new  MeterMBBRecollectStartThread(gaps));
				}
				list.add(future);
			}
			else if ( deviceType == CommonConstants.DeviceType.Modem){
				// Recollect from Modem via MCU
				Future<Long> future = (Future<Long>) executor.submit(new ModemRecollectStartThread(mcuDevices));
				list.add(future);
				// Recollect MBB Meters
				future = (Future<Long>) executor.submit( new MeterMBBRecollectStartThread(gaps));
				list.add(future);
			}
			else {//Meter
				// Recollect from Meter via MCU
				Future<Long> future = (Future<Long>) executor.submit(new MeterSubGigaRecollectStartThread(mcuDevices));
				list.add(future);
				// Recollect MBB Meters
				future = (Future<Long>) executor.submit( new MeterMBBRecollectStartThread(gaps));
				list.add(future);
			}
			
		    for (Future<Long> future : list) {  
		    	future.get();
		    }
		    executor.shutdown();
		}
		catch (Exception e) {
			logger.error("RecollectThread.run error - " + e, e);
		}
		finally {
			//if (txstatus != null&& !txstatus.isCompleted())
			//txmanager.rollback(txstatus);
		}
	}

	class MeterMBBRecollectStartThread extends Thread {
		List<Object> gaps ;
		MeterMBBRecollectStartThread(List<Object> gaps){
			this.gaps = gaps;
		}
		public void run(){
			logger.info("Total Meters to need recollect metering ["+ gaps.size() + "]");
			try {
				ExecutorService pool = Executors.newFixedThreadPool(_maxThreadWorker);
				MeterMBBRecollectThread threads[] = new MeterMBBRecollectThread[gaps.size()];
				int	i = 0;
				int cnt = 0;
				for (Object obj : gaps) {
					HashMap<String,Object> resultMap = (HashMap<String, Object>) obj;
					String mdsId = (String) resultMap.get("mdsId");
					logger.info(cnt + ": Meter[" + mdsId + "] Recollect Metering");
					cnt++;
					threads[i] = new MeterMBBRecollectThread(cnt, mdsId, deviceType, _fromToDateList, loginId);
					pool.execute(threads[i]);
					i++;
				}	
				logger.info("ExecutorService for Meter shutdown.");
				pool.shutdown();
				logger.info("ExecutorService for Meter awaitTermination. [" + _timeout + "]sec");
				pool.awaitTermination(_timeout, TimeUnit.SECONDS);
				logger.info("Exit Thread");
			}catch (Exception e){
				logger.error(e.getMessage(),e);
			}
		}
	}
	class MeterMBBRecollectThread extends Thread {

		private String mdsId;
		CommonConstants.DeviceType deviceType;
		List<Map<String,String>> fromToDateList; 
		String loginId;
		CommonConstants.DateType dateType;
		int thrNo;
		MeterMBBRecollectThread(int thrNo,String mdsId,CommonConstants.DeviceType deviceType, List<Map<String,String>> fromToDateList, String roginId)  {
			this.thrNo = thrNo;
			this.mdsId = mdsId;
			this.deviceType = deviceType;
			this.fromToDateList = _fromToDateList;
			this.loginId = roginId;
		}

		public void run() {
			TransactionStatus txstatus = null;
			long threadId = Thread.currentThread().getId();
			logger.info("ThreadID[{}] MeterMBBRecollectThread Start mdsId[{}] deviceType[{}]]", threadId, mdsId, deviceType.name() );

			boolean errorExit = false;
			for ( Map<String ,String> fromTo: fromToDateList){
				if ( errorExit ){
					logger.info("ThreadID[{}] error occured. break Meter[{}]", threadId,mdsId );
					break;
				}
				String fromDate = fromTo.get("fromDate");
				String toDate   = fromTo.get("toDate");
				
				MeterType[] meterTypes = {meterType};
				List<Object> gaps = getGaps(fromDate,toDate,meterTypes, "", mdsId );
				if ( gaps.size() == 0 ){
					logger.info("ThreadID[{}] mdsId[{}] fromDate[{}] toDate[{}] is not missing", threadId, mdsId, fromDate, toDate );
					continue;
				}
				logger.info("ThreadID[{}] mdsId[{}] fromDate[{}] toDate[{}] collect Start", threadId, mdsId, fromDate, toDate );
				try {	
					
					txstatus = txmanager.getTransaction(null);	 
					Map result = null;
					//Meter meter = meterDao.get(mdsId);
//					if (meter == null ) {
//						logger.error("ThreadID[{}] Meter[" + mdsId + "] is null" ,threadId, mdsId);
//						throw new Exception("Meter[" + mdsId + "] is null");
//					}
//					Modem modem = meter.getModem();
//					if (modem == null ) {
//						logger.error("ThreadID[{}] modem of Meter[{}] is null" ,threadId, mdsId);
//						throw new Exception("modem of Meter[" + mdsId + "] is null");
//					}

					String nOption = "";
					logger.info("ThreadID[{}] mdsId[{}] ",
							threadId, mdsId);

//					if((modem.getModemType() == ModemType.MMIU) && (modem.getProtocolType() == Protocol.SMS)) {
//						result = onDemandMeterBypassMBB(meter.getMdsId(), fromDate, toDate, loginId);
//						logger.debug("ThreadID[{}] Meter[{}] send sms command : onDemandMeterBypassMBB", threadId,mdsId);
//					}

//					if ( meter != null ){
//						
//
//						if ( result != null && result.get("result") != null 
//								&& "Success".equalsIgnoreCase((String)result.get("result"))){
//							logger.info("ThreadID[{}] Meter[{}] Recollect Success",threadId, mdsId);
//							status = ResultStatus.SUCCESS;
//						}
//						else {
//							logger.error("ThreadID[{}] Meter[{}] Recollect Fail", threadId,mdsId);
//							errorExit = true;
//						}
//					}
					
        			if ( deviceType == CommonConstants.DeviceType.Meter){
        				result = onDemandMeterBypassMBB(mdsId, fromDate + "000000", toDate + "235959", loginId);
        				logger.debug("[{}] send sms command : onDemandMeterBypassMBB", mdsId);
        			}
        			else if ( deviceType == CommonConstants.DeviceType.Modem){
        				result = romReadBypassMBB(mdsId, fromDate + "000000", toDate + "235959", loginId);
        				logger.debug("[{}] send sms command : romReadBypassMBB", mdsId);
        			}
        			else { // MCU 
        				result = romReadBypassMBB(mdsId, fromDate + "000000", toDate + "235959", loginId);
        				logger.debug("[{}] send sms command : romReadBypassMBB", mdsId);
        			}
					ResultStatus status = ResultStatus.FAIL;
					if ( result != null && result.get("result") != null 
							&& "Success".equalsIgnoreCase((String)result.get("result"))){
						logger.info("ThreadID[{}] Meter[{}] Recollect Success",threadId, mdsId);
						status = ResultStatus.SUCCESS;
					}
					else {
						logger.error("ThreadID[{}] Meter[{}] Recollect Fail", threadId,mdsId);
						errorExit = true;
					}
					if (txstatus != null&& !txstatus.isCompleted())
						txmanager.commit(txstatus);
				}
				catch  (Exception e) {
					logger.error(e.getMessage(),e);
					logger.error("ThreadID[{}] Meter[{}] RecollectThread.run error[{}] - " ,threadId, mdsId, e.getMessage());
					if (txstatus != null&& !txstatus.isCompleted())
						txmanager.rollback(txstatus);
				}
			}
			logger.info("ThreadID[{} Meter[{}] RecollecThread End [{}]", threadId, mdsId );
		}
	}
	
    class McuRecollectStartThread extends Thread {
    	List<McuDeviceList> mculist ;
    	McuRecollectStartThread(List<McuDeviceList>  mculist){
    		this.mculist = mculist;
    	}
    	public void run(){
    		logger.info("Total MCU to need recollect metering ["+ mculist.size() + "]");
    		int cnt = 0;

    		try {
	    		ExecutorService pool1 = Executors.newFixedThreadPool(_maxMcuThreadWorker);
	    		McuRecollectThread threads1[] = new McuRecollectThread[mculist.size()];
	    		int i = 0;
	
	    		for (McuDeviceList mcuMeter : mculist) {
	    			logger.info(cnt++ + ": MCU[" + mcuMeter.sysId + "] MdsId(for call command)[" + mcuMeter.meterMdsId + "]");
	
	    			if ( dateType == CommonConstants.DateType.DAILY ){
	    				threads1[i] = new McuRecollectThread(mcuMeter, _fromToDateList, loginId, _maxRecollectMcuDevice);
	    			}
	    			pool1.execute(threads1[i]);
	    			i++;
	    		}
	
	    		logger.info("ExecutorService for mcu shutdown.");
	    		pool1.shutdown();
	    		logger.info("ExecutorService for mcu awaitTermination. [" + _timeout + "]sec");
	    		pool1.awaitTermination(_timeout, TimeUnit.SECONDS);
	
	    		cnt = 0;
	    		i = 0;   
    		}catch (Exception e){
    			logger.error(e.getMessage(),e);
    		}
    		
    	}
    }
    class McuRecollectThread extends Thread {

    	McuDeviceList mcuDevices;
 //   	Meter	target;
 //   	String mdsId;
 //   	String fromDate;
 //   	String toDate;
    	String loginId;
    	List<Map<String,String>> fromToDateList; 
    	int limitOfDevices = 0;

    	McuRecollectThread(McuDeviceList mcuDevices, List<Map<String,String>> fromToDateList, String loginId, int limit)  {
    		try {
    			this.mcuDevices = mcuDevices;
//    			this.target = meter;
//    			this.mdsId = mdsId;
//    			this.fromDate = fromDate;
//    			this.toDate = toDate;
    			this.fromToDateList = fromToDateList;
    			this.loginId = loginId;
    			this.limitOfDevices = limit;
    		}
    		catch (Exception ee) {}        
    	}

    	public void run() {
    		long threadId = Thread.currentThread().getId();
    		logger.info("ThreadID[{}] Mcu Rcollect Metering thread start. MCU[{}]", threadId,  mcuDevices.sysId);

    		try {
    			boolean errorExit = false;
    			for ( Map<String,String> fromToMap : fromToDateList ) {
    				if ( errorExit ){
    					logger.info("ThreadID[{}] error occured. break MCU[{}]", threadId, mcuDevices.sysId );
    					break;
    				}
    				List<String> deviceList = new ArrayList<String>();
    				String fromDate = fromToMap.get("fromDate");
    				String toDate = fromToMap.get("toDate");

    				// get gaps for earch span
    				MeterType[] meterTypes = {meterType};
    				List<Object> gaps = getGaps(fromDate, toDate, meterTypes, mcuDevices.sysId ,"");
    				
    	    		logger.info("ThreadID[{}] MCU[{}] From[{}] To[{}] Total Meter to need recollect metering [{}]",
    	    				threadId, mcuDevices.sysId , fromDate, toDate, gaps.size());
    	    		
    	    		if ( gaps.size() == 0 ){
    	    			continue;
    	    		}
    	    		
    	    		// get list of meter
    		        for (Object obj : gaps) {
    					HashMap<String,Object> resultMap = (HashMap<String, Object>) obj;
    					String mdsId = (String) resultMap.get("mdsId");
    					String deviceSerial = (String) resultMap.get("deviceSerial");
    					if (deviceSerial == null) {
    						logger.debug("ThreadID[{}] Meter[" + mdsId + "] . Modem is null .", threadId);
    		        		continue;
    		        	}
    					deviceList.add(mdsId);
    		        }
//    		        // for KOREA ENV TEST
//    		    	for ( int i = 0; i < testMdsIds.length; i++){
//    		    		deviceList.add(testMdsIds[i]);
//    		    	}
//      		        // for KOREA ENV TEST
    		        List<String> splitDevices = null;
    		        Map result = null;
    		        int k = deviceList.size() / limitOfDevices;
    		        int cnt = 0;

    		        logger.debug("ThreadID[{}] MCU[{}] limitOfDevices[{}] devidedCount[{}] deviceCount[{}]",
    		        		threadId, mcuDevices.sysId, limitOfDevices,  k , deviceList.size());
    		        
    		        for (int i = 0; i < k + 1; i++) {
        				if ( errorExit ){
        					logger.info("ThreadID[{}] error occured. break MCU[{}]", threadId, mcuDevices.sysId );
        					break;
        				}
    		        	splitDevices = new ArrayList<String>();

    		        	for (int j = 0; j < limitOfDevices; j++) {
    		        		if (cnt == deviceList.size()) {
    		        			break;
    		        		}
    		        		logger.debug("ThreadID[{}] MCU[{}] device [{}][{}] Meter[{}]",
    		        				threadId, mcuDevices.sysId, i, j, (String)deviceList.get(cnt));
    		        		splitDevices.add((String)deviceList.get(cnt++));
    		        	}
    		        	
    		        	String[] devices = splitDevices.toArray(new String[splitDevices.size()]);

    		        	String nOption = "";
    		        	waitMcuCommand();
    		        	result = cmdOperationUtil.cmdGetMeteringData(meterDao.get(devices[0]), 0, loginId, nOption, fromDate+"000000", toDate+"235959", devices);
    		        	String resultStr = "";
    		        	if ( result != null ){
	    		        	if ( result.get("result") != null && result.get("result") instanceof String )
	    		        		resultStr =( String )result.get("result");
			        		if ( "success".equalsIgnoreCase(resultStr) ){
			        			logger.info("ThreadID[{}] cmdGetMeteringData() Success: MCU[{}]  From[{}] To[{}] ", 
			        					threadId, mcuDevices.sysId, fromDate, toDate );
			        		}
			        		else {
			        			logger.info("ThreadID[{}] cmdGetMeteringData() Fail: MCU[{}] Result[{}] From[{}] To[{}] Result[{}] ", 
			        					threadId, mcuDevices.sysId, resultStr, fromDate, toDate , resultStr);
			        			if ( !resultStr.startsWith("Failure[Read timeout!")) {
				        			errorExit = true;
			        			}
			        		}
    		        	}
    		        	else {
		        			logger.info("ThreadID[{}] cmdGetMeteringData() Fail: MCU[{}] Result[{}] From[{}] To[{}] Result[{}]", 
		        					threadId, mcuDevices.sysId, resultStr, fromDate, toDate , "result is null");
		        			errorExit = true;
    		        	}
    		        	splitDevices = null;
    		        }
    			}
    		} catch (Exception e){
    			logger.error(e.getMessage(),e);
    			logger.info("ThreadID[" + Thread.currentThread().getId() + "] Mcu Recollect Metering thread end. MCU[" + mcuDevices.sysId + "] is failed.");
    		}	
    	}
    }
    
    class ModemRecollectStartThread extends Thread {
    	List<McuDeviceList> mculist ;
    	ModemRecollectStartThread(List<McuDeviceList>  mculist){
    		this.mculist = mculist;
    	}
    	public void run(){
    		logger.info("Total MCU to need recollect metering ["+ mculist.size() + "]");
    		int cnt = 0;

    		try {
	    		ExecutorService pool1 = Executors.newFixedThreadPool(_maxMcuThreadWorker);
	    		ModemRecollectThread threads1[] = new ModemRecollectThread[mculist.size()];
	    		int i = 0;
	
	    		for (McuDeviceList mcuMeter : mculist) {
	    			logger.info(cnt++ + ": MCU[" + mcuMeter.sysId + "] MdsId(for call command)[" + mcuMeter.meterMdsId + "]");
	
	    			if ( dateType == CommonConstants.DateType.DAILY ){
	    				threads1[i] = new ModemRecollectThread(mcuMeter, _fromToDateList, loginId, _maxRecollectModemDevice);
	    			}
	    			pool1.execute(threads1[i]);
	    			i++;
	    		}
	
	    		logger.info("ExecutorService for mcu shutdown.");
	    		pool1.shutdown();
	    		logger.info("ExecutorService for mcu awaitTermination. [" + _timeout + "]sec");
	    		pool1.awaitTermination(_timeout, TimeUnit.SECONDS);
	
	    		cnt = 0;
	    		i = 0;   
    		}catch (Exception e){
    			logger.error(e.getMessage(),e);
    		}
    		
    	}
    }
    class ModemRecollectThread extends Thread {

    	McuDeviceList mcuDevices;
 //   	Meter	target;
 //   	String mdsId;
 //   	String fromDate;
 //   	String toDate;
    	String loginId;
    	
    	List<Map<String,String>> fromToDateList; 
    	int limitOfDevices = 0;

    	ModemRecollectThread(McuDeviceList mcuDevices, List<Map<String,String>> fromToDateList, String loginId, int limit)  {
    		try {
    			this.mcuDevices = mcuDevices;
//    			this.target = meter;
//    			this.mdsId = mdsId;
//    			this.fromDate = fromDate;
//    			this.toDate = toDate;
    			this.fromToDateList = fromToDateList;
    			this.loginId = loginId;
    			this.limitOfDevices = limit;
    		}
    		catch (Exception ee) {}        
    	}

    	public void run() {
    		long threadId = Thread.currentThread().getId();
    		logger.info("ThreadID[" + threadId + "] Modem Rcollect Metering thread start. MCU[" + mcuDevices.sysId + "]");

    		try {
    			boolean errorExit = false;
    			for ( Map<String,String> fromToMap : fromToDateList ) {
    				if ( errorExit ){
    					logger.info("ThreadID[{}] error occured. break MCU[{}]", threadId, mcuDevices.sysId );
    					break;
    				}
    				List<String> meterIdList = new ArrayList<String>();
    				List<String> modemIdList = new ArrayList<String>();
    				String fromDate = fromToMap.get("fromDate");
    				String toDate = fromToMap.get("toDate");

    				
    				// get gaps for earch span
    				MeterType[] meterTypes = {meterType};
    				List<Object> gaps = getGaps(fromDate,toDate,meterTypes, mcuDevices.sysId,"" );
    				
    	    		logger.info("ThreadID[{}] MCU[{}] From[{}] To[{}] Total Meter to need recollect metering [{}]",
    	    				threadId, mcuDevices.sysId, fromDate, toDate, gaps.size());
    	    		
    	    		if ( gaps.size() == 0 ){
    	    			continue;
    	    		}
    	    		// get port : deviceList 	    		
    	    		LinkedHashMap<Integer,HashMap<String, ArrayList<String>>> portHash = getPortList(gaps);
    	    		
//    	    		// for KOREA ENV TEST
//    	    		portHash = getPortList_KORIAENV_TEST(null);
//    	    		// for KOREA NED TEST
    	    		for (Integer port : portHash.keySet()) {
    		        	if ( errorExit){
    		        		logger.debug("ThreadID[{}] error occured , break. MCU[{}] modemPort[{}]",
    		        				threadId, mcuDevices.sysId, port);
    		        		break;
    		        	}
    	    			// get list of meter 
    	    			meterIdList = portHash.get(port).get("meter");
    	    			modemIdList = portHash.get(port).get("modem");


        		        Map result = null;
        		        int k = meterIdList.size() / limitOfDevices;
        		        int cnt = 0;

        		        logger.debug("ThreadID[{}] MCU[{}] modemPort[{}]  limitOfDevices [{}] devidedCount[{}] deviceCount[{}]",
        		        		threadId, mcuDevices.sysId, port,  limitOfDevices, k ,meterIdList.size());
        		              
        		        for (int i = 0; i < k + 1; i++) {
        		        	if ( errorExit){
        		        		logger.debug("ThreadID[{}] error occured , break. MCU[{}] modemPort[{}] splitDeviceNum[{}]",
        		        				threadId, mcuDevices.sysId, port, i);
        		        		break;
        		        	}
        	    			List<String> splitMeters = new ArrayList<String>();
        	    			List<String> splitModems = new ArrayList<String>();
        		        	for (int j = 0; j < limitOfDevices; j++) {
            		        	if ( errorExit){
            		        		logger.debug("ThreadID[{}] error occured , break. MCU[{}] modemPort[{}] splitDeviceNum[{}][{}]",
            		        				threadId, mcuDevices.sysId, port, i, j);
            		        		break;
            		        	}
        		        		if (cnt == meterIdList.size() ){
        		        			break;
        		        		}
        		        		logger.debug("ThreadID[{}] MCU[{}] modemPort[{}]　device [{}][{}] Meter[{}] Modem[{}]",
        		        				threadId, mcuDevices.sysId, port,
        		        				i, j, (String)meterIdList.get(cnt), (String)modemIdList.get(cnt)  );
        		        		splitMeters.add((String)meterIdList.get(cnt));
        		        		splitModems.add((String)modemIdList.get(cnt));
        		        		cnt++;
        		        	}
//        		        	StringBuffer dsb = new StringBuffer();
//        		        	for ( String device : meterIdList){
//        		        		dsb.append(device + ",");
//        		        	}

        		        	String nOption = "";
        		        	waitMcuCommand();
        		        	logger.info("ThreadID[{}] cmdDmdNiGetRomReadMulti() MCU[{}] modemPort[{}] devices[{}] from[{}] to[{}]",
        		        			threadId, mcuDevices.sysId, port, arrayList2String(splitMeters), fromDate+"000000", toDate+"235959");
        		        	result = cmdOperationUtil.cmdDmdNiGetRomReadMulti(mcuDevices.sysId, port, splitMeters, splitModems, fromDate+"000000", toDate+"235959");
        		        	
        		        	
        		        	if ( result != null ){
        		        		String resultStr = (String)result.get("result");
        		        		if ("Success".equals(resultStr)){
        		        			List<String> successList = (List<String>)result.get("successMeters");
        		        			List<String> errorList = (List<String>)result.get("errorMeters");
        		        			logger.info("ThreadID[{}] cmdDmdNiGetRomReadMulti() Success: MCU[{}] modemPort[{}] SuccessMeters[{}] ErrorMeters[{}]",
        		        					threadId, mcuDevices.sysId,port,  arrayList2String(successList), arrayList2String(errorList));
        		        		}
        		        		else {
        		        			logger.error("ThreadID[{}] cmdDmdNiGetRomReadMulti() Fail: MCU[{}] modemPort[{}] Result[{}]",
        		        					threadId, mcuDevices.sysId, port ,resultStr);
        	        				if ( resultStr.startsWith("Failure[ Can't connect to DCU")){
        	        					errorExit = true;
        	        				}
        		        		}
        		        	}
        		        	else {
    		        			logger.error("ThreadID[{}] cmdDmdNiGetRomReadMulti() Fail: MCU[{}] modemPort[{}] Result[{}]",
    		        					threadId, mcuDevices.sysId, port ,"result is null");
        		        		errorExit = true;
        		        	}
        		        	splitMeters = null;
        		        	splitModems = null;
        		        }
    	    		}
    			}
        	} catch (Exception e){
        		logger.error(e.getMessage(),e);
        		logger.info("ThreadID[" + Thread.currentThread().getId() + "] Mcu Recollect Metering thread end. MCU[" + mcuDevices.sysId + "] is failed.");
        	}	
        } 
    }
    
    class MeterSubGigaRecollectStartThread extends Thread {
    	List<McuDeviceList> mculist ;
    	MeterSubGigaRecollectStartThread(List<McuDeviceList>  mculist){
    		this.mculist = mculist;
    	}
    	
    	public void run(){
    		logger.info("Total MCU to need recollect metering ["+ mculist.size() + "]");
    		int cnt = 0;

    		try {
	    		ExecutorService pool1 = Executors.newFixedThreadPool(_maxMcuThreadWorker);
	    		MeterSubGigaRecollectThread threads1[] = new MeterSubGigaRecollectThread[mculist.size()];
	    		int i = 0;
	
	    		for (McuDeviceList mcuMeter : mculist) {
	    			logger.info(cnt++ + ": MCU[" + mcuMeter.sysId + "] MdsId(for call command)[" + mcuMeter.meterMdsId + "]");
	
	    			if ( dateType == CommonConstants.DateType.DAILY ){
	    				threads1[i] = new MeterSubGigaRecollectThread(mcuMeter, _fromToDateList, loginId);
	    			}
	    			pool1.execute(threads1[i]);
	    			i++;
	    		}
	
	    		logger.info("ExecutorService for mcu shutdown.");
	    		pool1.shutdown();
	    		logger.info("ExecutorService for mcu awaitTermination. [" + _timeout + "]sec");
	    		pool1.awaitTermination(_timeout, TimeUnit.SECONDS);
	
	    		cnt = 0;
	    		i = 0;   
    		}catch (Exception e){
    			logger.error(e.getMessage(),e);
    		}
    	}
    }
    
    class MeterSubGigaRecollectThread extends Thread {

    	McuDeviceList mcuDevices;
    	String loginId;
    	List<Map<String,String>> fromToDateList; 
    	int limitOfDevices = 0;

    	MeterSubGigaRecollectThread(McuDeviceList mcuDevices, List<Map<String,String>> fromToDateList, String loginId)  {
    		try {
    			this.mcuDevices = mcuDevices;
    			this.fromToDateList = fromToDateList;
    			this.loginId = loginId;
    		}
    		catch (Exception ee) {}        
    	}

    	public void run() {
    		long threadId = Thread.currentThread().getId();
    		logger.info("ThreadID[{}] Meter(SubGiga) Rcollect Metering thread start. MCU[{}]", threadId, mcuDevices.sysId);

    		try {
    			boolean errorExit = false;
    			HashMap<String, String> errMeters = new HashMap<String,String>();
    			for ( Map<String,String> fromToMap : fromToDateList ) {
    				if ( errorExit ){
    					logger.info("ThreadID[{}] error occured. break MCU[{}]", threadId, mcuDevices.sysId );
    					break;
    				}
    				String fromDate = fromToMap.get("fromDate");
    				String toDate = fromToMap.get("toDate");
    				
    				// get gaps for earch span
    				MeterType[] meterTypes = {meterType};
    				List<Object> gaps = getGaps(fromDate,toDate,meterTypes, mcuDevices.sysId,"" );
    				
    	    		logger.info("ThreadID[{}] MCU[{}] From[{}] To[{}] Total Meter to need recollect metering [{}]",
    	    				threadId, mcuDevices.sysId, fromDate, toDate, gaps.size());
    	    		
    	    		if ( gaps.size() == 0 ){
       					logger.debug("ThreadID[{}] MCU[{}] gap count  == 0, continue ", threadId, mcuDevices.sysId );
    	    			continue;
    	    		}
    	    		
    		        for (Object obj : gaps) {
    		        	if ( errorExit ){
    	    	    		logger.info("ThreadID[{}] MCU[{}] error occured. break gap",
    	    	    				threadId, mcuDevices.sysId);
    		        		break;
    		        	}
    					HashMap<String,Object> resultMap = (HashMap<String, Object>) obj;
    					String mdsId = (String) resultMap.get("mdsId");
    					if ( errMeters.get(mdsId) != null){
    						logger.debug("ThreadID[{}] MCU[{}] collecting Meter[{}] was failed, skip.", 
    								threadId, mcuDevices.sysId, mdsId);
    						continue;
    					}
    					Meter meter = meterDao.get(mdsId);
    					Map<String,String>result = new HashMap<String,String>();
    		        	waitMcuCommand();
    					result = cmdOperationUtil.doOnDemand(meter, 0, loginId, "", fromDate, toDate);
    					//logger.debug("[{}] call doOnDemand", mdsId);
		        		String resultStr = "";
		        		if ( result.get("result") != null &&  result.get("result") instanceof String ){ 
		        			resultStr = (String)result.get("result");
		        		}
		        		if ( "Success".equalsIgnoreCase(resultStr)){
		        				logger.info("ThreadID[{}] doOnDemand() Success: MCU[{}],Meter[{}],from[{}],to[{}]", 
		        						threadId, mcuDevices.sysId , mdsId, fromDate, toDate );
		        		}
		        		else {
	        				logger.error("ThreadID[{}] doOnDemand() Fail: MCU[{}],Meter[{}],from[{}],to[{}], result[{}]", 
	        						threadId, mcuDevices.sysId , mdsId, fromDate, toDate , resultStr);
	        				if ( resultStr.startsWith("Failure[ Can't connect to DCU")){
	        					errorExit = true;
	        				}
	        				else if ( !resultStr.startsWith("Failure[Read timeout!")) {
	        					errMeters.put(mdsId, "");
	        				}
		        		}
    		        }
    			}
        	} catch (Exception e){
        		logger.info("ThreadID[" + Thread.currentThread().getId() + "] Mcu Recollect Metering thread end. MCU[" + mcuDevices.sysId + "] is failed.");
        	}	
        } 
    }
    /*
     * Send SMS
     */
	public Map sendSmsForCmdServer(Modem modem, String messageType, String commandCode, String commandName, Map<String, String> paramMap) throws Exception {
		long threadId = Thread.currentThread().getId();
		logger.debug("[sendSmsForCmdServer] " + " messageType: " + messageType + " commandCode: " + commandCode + " commandName: " + commandName);

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
			} else {
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
    	long threadId = Thread.currentThread().getId();
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
				logger.error("ThreadID[{}] Meter[{}] ModemPort: {} is not Support", threadId, mdsId, modemPort);
				throw new Exception("ModemPort:" + modemPort + " is not Support");
			}    		
    		
			Map<String,String> paramMap = new HashMap<String,String>();
			if (modemPort==0) {
		    	logger.debug("ThreadID["+ threadId+ " ] cmdGetLoadProfile ["+ mdsId + "][" + modemPort +  "]["  +  fromDate + "][" +toDate +"]");

		    	String obisCode = DataUtil.convertObis(OBIS.ENERGY_LOAD_PROFILE.getCode());
				int classId = DLMS_CLASS.PROFILE_GENERIC.getClazz();
				int attrId = DLMS_CLASS_ATTR.PROFILE_GENERIC_ATTR02.getAttr();
				
				Map<String,String> valueMap = CommonUtil.getParamValueByRange(fromDate,toDate);
				String value = CommonUtil.meterParamMapToJSON(valueMap);
				
				logger.debug("ThreadID[{}] Meter[{}] ObisCode=> {}, classID => {}, attributeId => ", threadId, mdsId, obisCode,classId,attrId);
				//paramGet
    			paramMap.put("paramGet", obisCode+"|"+classId+"|"+attrId+"|null|null|"+value);				
			}
			else {
		    	logger.debug("ThreadID["+ threadId+ " ] cmdGetLoadProfile(Mbus) ["+ mdsId + "][" + modemPort +  "]["  +  fromDate + "][" +toDate +"]");
				
		    	String obisCode = DataUtil.convertObis(OBIS.MBUSMASTER_LOAD_PROFILE.getCode());
				int classId = DLMS_CLASS.PROFILE_GENERIC.getClazz();
				int attrId = DLMS_CLASS_ATTR.PROFILE_GENERIC_ATTR02.getAttr();
				
				Map<String,String> valueMap = CommonUtil.getParamValueByRange(fromDate,toDate);
				String value = CommonUtil.meterParamMapToJSON(valueMap);
				
				logger.debug("ThreadID[{}] Meter[{}] ObisCode=> {}, classID => {}, attributeId => ", threadId, mdsId, obisCode, classId, attrId);
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
						logger.debug("ThreadID[{}] sendSmsForCmdServer() Modem[{}] params[{}]", threadId, mmiu.getDeviceSerial(), paramMap);
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
     		        			logger.error("ThreadID[{}] sendSmsForCmdServer() Success: Modem[{}]",
    		        					threadId, mmiu.getDeviceSerial());
//				                if (meter.getModem() != null) {
//				                	meter.getModem().setCommState(1);
//				                }
				                
						}else{
     		        			logger.error("ThreadID[{}] sendSmsForCmdServer() Fail: Modem[{}] Result[{}]",
    		        					threadId, mmiu.getDeviceSerial() ,"result is null");
								//cmdResult="Failed to get the resopone. See the Async Command History.";
								//cmdResult="Check the Async_Command_History.";								
						}						
						//rtnStr = cmdResult;
	            	}
				}
        		// INSERT START SP-993
        		else {
        			returnMap = cmdOperationUtil.doOnDemand(meter, 0, loginId, "", fromDate, toDate);
    				logger.debug("[{}] call doOnDemand", mdsId);
        		}
        		// INSERT END SP-993
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
    		// INSERT START SP-993
    		else {
    			returnMap = cmdOperationUtil.cmdGetROMRead(meter, 0, loginId, "", fromDate, toDate);
				logger.debug("[{}] call cmdGetROMRead", mdsId);
    			
    		}
    		// INSERT END SP-993
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
	 * 	mvn -e -f $AIMIR_TASK/pom-RecollectMeteringSoria2.xml antrun:run -DtaskName=RecollectMeteringSoria -DdeviceType=DeviceType -DsupplierName=SupplierName -DfromDate=FromYYYYMMDD -DtoDate=ToYYYYMMDD -DmeterType=[EnergyMeter|WaterMeter|GasMeter|...] -Dspring.instrument.path=$MVNREPOSITORY/org/springframework/spring-instrument/4.2.5.RELEASE
	 */
	public static void main(String[] args) {
		logger.info("-----");
		logger.info("-----");
		logger.info("-----");
		logger.info("#### RecollectMeteringSoriaTask2 start. ###");
		
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
		boolean showlist = false;
		boolean mbbWithMcu = false;
		String sysId = null;
		MeterType meterType = MeterType.EnergyMeter;
		
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
			else if ( nextArg.startsWith("-loginId")){
				if ( !"${loginId}".equals(args[i + 1]))
					loginId = new String(args[i + 1]);
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
			else if ( nextArg.startsWith("-meterType")){
				if ( !"${meterType}".equals(args[i + 1])){
					String meterTypeName = new String(args[i + 1]);
					MeterType mt = null;
					for ( MeterType e : MeterType.values()){
						if ( meterTypeName.equals(e.name())) {
							mt = e;
						}
					}
					if ( mt != null)
						meterType = mt;
				}
			}
			else if ( nextArg.startsWith("-sysId")){
				if ( !"${sysId}".equals(args[i + 1]))
					sysId = new String(args[i + 1]);
			}
			else if ( nextArg.startsWith("-showlist")){
				if ( !"${showlist}".equals(args[i + 1])){
					String show = new String(args[i + 1]);
					if ( "true".equals(show))
						showlist = true;
				}
			}
			else if ( nextArg.startsWith("-mbbWithMcu")){
				if ( !"${mbbWithMcu}".equals(args[i + 1])){
					String mbb = new String(args[i + 1]);
					if ( "true".equals(mbb))
						mbbWithMcu = true;
				}
			}
		}
		// dateType is DAILY
		dateType = CommonConstants.DateType.DAILY;

		if (deviceType == null ) {
			deviceType = "Meter";
		}
		if (supplierName == null ){
			supplierName = "";
		}
		logger.info("RecollectMeteringSoriaTask2 . devicetype={} supplierName={} meterType={} meterId={}", 
				deviceType, supplierName, meterType.name(), meterId );

		try {
			ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "spring-RecollectMeteringSoria2.xml" });
			DataUtil.setApplicationContext(ctx);

			RecollectMeteringSoriaTask2 task = (RecollectMeteringSoriaTask2) ctx.getBean(RecollectMeteringSoriaTask2.class);
			
			task.setSupplierName(supplierName);
			task.setLoginId(loginId);
//			task.setFromDate(fromDate);
//			task.setToDate(toDate);
			task.setMeterId(meterId);
			task.setDateType(dateType);
			task.setMeterType(meterType);
			task.setSysId(sysId);
			task.setMbbCollectWithMcu(mbbWithMcu);
			
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

			if (force.equals("true")) {
				task.setForceOption(true);
			}
			else {
				task.setForceOption(false);
			}
			
			if ( showlist ){
				task.setShowList(true);
			}
			task.execute(null);

		} catch (Exception e) {
			logger.error("RecollectMeteringSoriaTask2 excute error - " + e, e);
		} finally {
			logger.info("#### RecollectMeteringSoriaTask2 Task finished. ###");		
			System.exit(0);
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

	private void loadProperties(){
		Properties prop = new Properties();
		try{
			prop.load(getClass().getClassLoader().getResourceAsStream("config/schedule-RecollectMeteringSoria2.properties"));
		}catch(Exception e){
			logger.error("Can't not read property file. -" + e,e);
		}
		_maxThreadWorker = Integer.parseInt(prop.getProperty("recollect.ondemand.maxworker", "5"));

		_maxMcuThreadWorker = Integer.parseInt(prop.getProperty("recollect.ondemand.mcu.maxworker", "5"));
		_maxRecollectMcuDevice = Integer.parseInt(prop.getProperty("recollect.ondemand.mcu.devicemax", "400")); // max devices number to cmdGetMeteringData(),
		_maxRecollectModemDevice = Integer.parseInt(prop.getProperty("recollect.ondemand.modem.devicemax", "5")); // max devices number to cmdDmdNiGetRomReadMulti()
		_deviceMeter = Integer.parseInt(prop.getProperty("recollect.ondemand.device.meter", "0"));
		
		_waitMcu = Integer.parseInt(prop.getProperty("recollect.ondemand.mcu.wait", "2"));
		_timeout = Integer.parseInt(prop.getProperty("recollect.ondemand.timeout", "3600"));
		if (prop.getProperty("recollect.ondemand.useif4", "true").equals("false")) {
			_isUseIF4Command = false;
		}
		_beforeTime = Integer.parseInt(prop.getProperty("recollect.ondemand.beforeTime", "24"));

		_dayBefore = Integer.parseInt(prop.getProperty("recollect.ondemand.beforeDay", "7"));
		_stepDays  = Integer.parseInt(prop.getProperty("recollect.ondemand.stepDays", "1"));
	}
	
	private void getFromToDates(){
		ArrayList<Map<String,String>> fromToDates = new ArrayList<Map<String,String>>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fromDay = "";
		String toDay = "";
		// get end date
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(new Date());
        endCal.add(Calendar.HOUR, -24);
        _endDate =sdf.format(endCal.getTime());
        		
		// get first from date
        Calendar fromDayCalendar = Calendar.getInstance();
        fromDayCalendar.setTime(new Date());
        fromDayCalendar.add(Calendar.DATE, (_dayBefore*-1));
        
        _startDate = sdf.format(fromDayCalendar.getTime());
        
        // get first to date
        Calendar toDayCalendar = (Calendar)fromDayCalendar.clone();
        toDayCalendar.add(Calendar.DATE, _stepDays - 1);
        
        fromDay = _startDate;
        toDay = sdf.format(toDayCalendar.getTime());
        while ( fromDay.compareTo(_endDate) <= 0 ){
        	HashMap<String, String> ftmap = new HashMap<String, String>();

        	ftmap.put("fromDate", fromDay);
        
        	if ( toDay.compareTo(_endDate)<= 0 ){
        		ftmap.put("toDate", toDay);
        	}
        	else {
        		ftmap.put("toDate", _endDate);
        	}
        	fromDayCalendar.add(Calendar.DATE, _stepDays);
        	fromDay = sdf.format(fromDayCalendar.getTime());

        	toDayCalendar.add(Calendar.DATE, _stepDays);
        	toDay = sdf.format(toDayCalendar.getTime());
        	
        	fromToDates.add(ftmap);
        }
        _fromToDateList =  fromToDates;
	}

	String arrayList2String(List<String> arrayList){
		StringBuffer sb = new StringBuffer();
		if ( arrayList != null ){
			for( String e : arrayList){
				if ( sb.length() != 0 ){
					sb.append(",");
				}
				sb.append(e);
			}
		}
		return sb.toString();
	}
}
