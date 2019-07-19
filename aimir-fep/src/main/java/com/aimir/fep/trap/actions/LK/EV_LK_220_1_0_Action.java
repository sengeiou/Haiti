package com.aimir.fep.trap.actions.LK;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.EventStatus;
import com.aimir.constants.CommonConstants.McuType;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.PlcQualityTestDao;
import com.aimir.dao.system.PlcQualityTestDetailDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.EnergyMeter;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.device.PLCIU;
import com.aimir.model.device.ZRU;
import com.aimir.model.system.Code;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.Location;
import com.aimir.model.system.PlcQualityTest;
import com.aimir.model.system.PlcQualityTestDetail;
import com.aimir.model.system.Supplier;
import com.aimir.notification.FMPTrap;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;

/*
 * 220.1	evtInstallMeter	OID	3	True	Major		Install meter
  21.1.1	meterId	STRING	20				Meter ID
  21.1.2	meterModel	STRING	20				Meter model
  21.1.3	meterVendor	STRING	3				Meter Vendor
  21.1.5	meterPhase	BYTE	1				Meter phase (electricity only)
  31.3.3	moG3NodeKind	STRING	20				Modem Node Kind
  31.3.5	moG3FwVer	WORD	2				Modem FW Version (Major , Minor)
  31.3.6	moG3FwBuild	WORD	2				Modem FW Build number
  31.3.7	moG3HwVer	WORD	2				Modem HW Version
 */
@Component
public class EV_LK_220_1_0_Action implements EV_Action {
	private static Log log = LogFactory.getLog(EV_LK_220_1_0_Action.class);

	@Resource(name = "transactionManager")
	JpaTransactionManager txmanager;

	@Autowired
	MCUDao mcuDao;

	@Autowired
	CodeDao codeDao;

	@Autowired
	ModemDao modemDao;

	@Autowired
	MeterDao meterDao;

	@Autowired
	SupplierDao supplierDao;

	@Autowired
	DeviceModelDao deviceModelDao;

	@Autowired
	LocationDao locationDao;

	@Autowired
	PlcQualityTestDao plcQualityTestDao;

	@Autowired
	PlcQualityTestDetailDao plcQualityTestDetailDao;

	public void execute(FMPTrap trap, EventAlertLog event) throws Exception {
		String currentTime = TimeUtil.getCurrentTime();
		log.debug("EventName[evtInstallMeter] " + " EventCode[" + trap.getCode() + "] Modem[" + trap.getSourceId() + "]");

		String mcuId = trap.getMcuId();
		String ipAddr = event.getEventAttrValue("ntwPppIp") == null ? "" : event.getEventAttrValue("ntwPppIp");
		String modemSerial = trap.getSourceId();
		String timeStamp = trap.getTimeStamp();
		String srcType = trap.getSourceType();

		log.debug("mcuId = " + mcuId + ", ipAddr = " + ipAddr + ", modemSeriall = " + modemSerial + ", timeStamp = " + timeStamp + ", srcType = " + srcType);

		String meterId = event.getEventAttrValue("meterId");
		String meterModel = event.getEventAttrValue("meterModel") == null ? "" : event.getEventAttrValue("meterModel");
		String meterVendor = event.getEventAttrValue("meterVendor") == null ? "" : event.getEventAttrValue("meterVendor");
		int meterPhase = Integer.parseInt(event.getEventAttrValue("meterPhase") == null ? "0" : event.getEventAttrValue("meterPhase"));
		String moNodeKind = event.getEventAttrValue("moG3NodeKind") == null ? "" : event.getEventAttrValue("moNodeKind");
		String fwVer = event.getEventAttrValue("moG3FwVer") == null ? "" : event.getEventAttrValue("moFwVer");
		String build = event.getEventAttrValue("moG3FwBuild") == null ? "" : event.getEventAttrValue("moFwBuild");
		String hwVer = event.getEventAttrValue("moG3HwVer") == null ? "" : event.getEventAttrValue("moHwVer");
		String nameSpace = trap.getNameSpace();
		String locationName = FMPProperty.getProperty("loc.default.name","North");
		
		List<Location> locations = locationDao.getLocationByName(locationName);
		Location defaultLocation = null;
		if(locations != null && locations.size() > 0){
			defaultLocation = locations.get(0);
        } else {
        	defaultLocation = locationDao.getAll().get(0);
        }
		
		if (fwVer != null && !"".equals(fwVer)) {
			fwVer = DataUtil.getVersionString(Integer.parseInt(fwVer));
		}
		if (hwVer != null && !"".equals(hwVer)) {
			hwVer = DataUtil.getVersionString(Integer.parseInt(hwVer));
		}

		log.debug("meterId[" + meterId + "]");
		log.debug("meterModel[" + meterModel + "]");
		log.debug("meterVendor[" + meterVendor + "]");
		log.debug("meterPhase[" + meterPhase + "]");
		log.debug("moNodeKind[" + moNodeKind + "]");
		log.debug("moFwVer[" + fwVer + "]");
		log.debug("moFwBuild[" + build + "]");
		log.debug("moHwVer[" + hwVer + "]");

		// Event 저장시 필요한 값
		EventAlertLog eventAlertLog = new EventAlertLog();
		eventAlertLog.setStatus(EventStatus.Open);
		eventAlertLog.setOpenTime(event.getOpenTime());

		/************************
		 * DCU 등록
		 */
		log.info("## 1. DCU Information processing start ##");
		Code mcuStatus = codeDao.getCodeIdByCodeObject("1.1.4.1"); //  <system.Code code="1.1.4.1" name="Normal" descr="Normal" order="1" parent="1.1.4"/>

		MCU mcu = new MCU();
		MCU existMcu = null;
		mcu.setSysID(mcuId);
		mcu.setMcuStatus(mcuStatus);
		mcu.setProtocolType(CommonConstants.getProtocolByName(Protocol.GPRS.name()));

		TransactionStatus txstatus = null;
		try {
			txstatus = txmanager.getTransaction(null);

			existMcu = mcuDao.get(mcu.getSysID());
			if (existMcu == null) {
				log.info("mcu[" + mcu.getSysID() + "] is not existed!!");
				/*
				 * DCU  신규 등록
				 */
				//Set Default Location  
				String defaultLocName = FMPProperty.getProperty("loc.default.name");
				if (defaultLocName != null && !"".equals(defaultLocName)) {
					if (locationDao.getLocationByName(StringUtil.toDB(defaultLocName)) != null && locationDao.getLocationByName(StringUtil.toDB(defaultLocName)).size() > 0) {
						log.info("MCU[" + mcu.getSysID() + "] Set Default Location[" + locationDao.getLocationByName(StringUtil.toDB(defaultLocName)).get(0).getId() + "]");
						mcu.setLocation(locationDao.getLocationByName(StringUtil.toDB(defaultLocName)).get(0));
					} else {
						log.info("MCU[" + mcu.getSysID() + "] Default Location[" + defaultLocName + "] is Not Exist In DB, Set First Location[" + locationDao.getAll().get(0).getId() + "]");
						mcu.setLocation(locationDao.getAll().get(0));
					}
				} else {
					log.info("MCU[" + mcu.getSysID() + "] Default Location is Not Exist In Properties, Set First Location[" + locationDao.getAll().get(0).getId() + "]");
					mcu.setLocation(locationDao.getAll().get(0));
				}

				//Set Default Supplier
				String supplierName = new String(FMPProperty.getProperty("default.supplier.name").getBytes("8859_1"), "UTF-8");
				log.debug("Supplier Name[" + supplierName + "]");
				Supplier supplier = supplierName != null ? supplierDao.getSupplierByName(supplierName) : null;
				if (supplier != null && supplier.getId() != null && mcu.getSupplier() == null) {
					mcu.setSupplier(supplier);
				} else {
					log.info("MCU[" + mcu.getSysID() + "] Default Supplier is Not Exist In Properties, Set First Supplier[" + supplierDao.getAll().get(0).getId() + "]");
					mcu.setSupplier(supplierDao.getAll().get(0));
				}

				mcu.setInstallDate(currentTime);
				mcu.setLastCommDate(currentTime);
				mcu.setNetworkStatus(1);
				mcu.setMcuType(CommonConstants.getMcuTypeByName(McuType.DCU.name()));
				mcu.setSysLocalPort(new Integer(8000));
				mcu.setNameSpace(FMPProperty.getProperty("default.namespace.dcu", "LK"));
				mcu.setProtocolVersion("0102");
				mcu.setSysHwVersion("0.0");
				mcu.setSysSwVersion("0.0");
				mcuDao.add(mcu);

				// Event 저장시 필요한 값
				eventAlertLog.setLocation(mcu.getLocation());
				eventAlertLog.setSupplier(mcu.getSupplier());

				/*
				 * DCU Registration Event save
				 */
				try {
					log.debug("########## DCU[" + mcu.getSysID() + "] Equipment Registration start ##########");
					EventUtil.sendEvent("Equipment Registration", TargetClass.DCU, mcu.getSysID(), currentTime, new String[][] {}, eventAlertLog);
					log.debug("########## DCU[" + mcu.getSysID() + "] Equipment Registration stop  ##########");
				} catch (Exception e) {
					log.error("Equipment Registration save error - " + e.getMessage(), e);
				}
			} else {
				log.info("mcu[" + existMcu.getSysID() + "] is existed!! - Location Id:" + existMcu.getLocation().getId() + " Location Name:" + existMcu.getLocation().getName());

				/*
				 * DCU update
				 */
				/*
				 * 2017.08.28 PPP IP로 저장해달라는 요청으로 수정함.
				 *   - existMcu.setIpAddr(ipAddr);
				 */
				//mcu.setIpAddr(ipAddr);  evtIpChange 이벤트로만 ip변경하도록 수정.
				existMcu.setLastCommDate(currentTime);
				existMcu.setProtocolVersion("0102");
				existMcu.setMcuStatus(mcu.getMcuStatus());
				existMcu.setNameSpace(FMPProperty.getProperty("default.namespace.dcu", "LK"));
				mcuDao.update(existMcu);

				// Event 저장시 필요한 값
				eventAlertLog.setLocation(existMcu.getLocation());
				eventAlertLog.setSupplier(existMcu.getSupplier());
			}
		} catch (Exception e) {
			log.error("DCU add/update error - " + e.getMessage(), e);
		} finally {
			if (txstatus != null)
				txmanager.commit(txstatus);
		}
		log.info("## 1. DCU Information processing finished ##");

		MCU dcu = mcuDao.get(mcu.getSysID());

		/********************************
		 * 모뎀 등록
		 */
		log.info("## 2. Modem Information processing start ##");
		boolean isExistModem = false;
		boolean isRepeater = false;

		txstatus = null;
		try {
			txstatus = txmanager.getTransaction(null);

			// Modem Update
			Modem modem = modemDao.get(modemSerial);
			Supplier supplier = dcu.getSupplier();
			DeviceModel modemModel = deviceModelDao.findByCondition("name", moNodeKind);
			boolean isOldModem = true;

			ModemType modemType = null;
			Protocol modemProtocol = null;
			if(modemModel.getDeviceType().getName().equals("PLCIU")) {
				modemType = ModemType.PLCIU;
				modemProtocol = Protocol.IP;
			}else if(modemModel.getDeviceType().getName().equals("ZRU")) {
				modemType = ModemType.ZRU;
				modemProtocol = Protocol.ZigBee;
			}else {
				modemType = ModemType.Unknown;				
			}
			
			if (modem == null) {
				log.error("modem is null"); // FIXME
				if(modemType == ModemType.PLCIU) {
					modem = new PLCIU();	
				}else if(modemType == ModemType.ZRU) {
					modem = new ZRU();
				}
				
				isOldModem = false;
			}

			if (modemModel != null) {
				modem.setModel(modemModel);
				modem.setProtocolVersion(trap.getProtocolVersion());
				
				log.debug("trap.getSourceType() : " + trap.getSourceType() + ", Modem Protocol = " + modemProtocol);
				modem.setProtocolType(modemProtocol.name());
			}
			if (dcu != null) {
				modem.setSupplier(supplier);
				modem.setMcu(dcu);
				modem.setLocationId((dcu.getLocationId() == null) ? defaultLocation.getId() : dcu.getLocationId());
				modem.setLocation((dcu.getLocation() == null) ? defaultLocation : dcu.getLocation());
			}else {
				modem.setLocationId(defaultLocation.getId());
				modem.setLocation(defaultLocation);
			}
			log.debug("## Modem Information set start ##");
			modem.setProtocolVersion(trap.getProtocolVersion());
			modem.setModemType(modemType.name());
			modem.setFwRevision(build);
			modem.setFwVer(fwVer);
			modem.setHwVer(hwVer);
			modem.setNameSpace(nameSpace);
			log.debug("supplier : " + supplier.toString());
			log.debug("supplier.getTimezone() : " + supplier.getTimezone());
			log.debug("supplier.getTimezone().getName() : " + supplier.getTimezone().getName());
			log.debug("trap.getTimeStamp() : " + trap.getTimeStamp());
			log.debug("DateTimeUtil.getDST() : " + DateTimeUtil.getDST(supplier.getTimezone().getName(), trap.getTimeStamp()));
			modem.setLastLinkTime(DateTimeUtil.getDST(supplier.getTimezone().getName(), trap.getTimeStamp()));

			if (!isOldModem) { // 신규 모뎀 등록
				modem.setDeviceSerial(modemSerial);
				modem.setInstallDate(DateTimeUtil.getDST(supplier.getTimezone().getName(), trap.getTimeStamp()));
				modemDao.add(modem);
				log.info("Add modem=" + modemSerial);

				EventUtil.sendEvent("Equipment Installation", TargetClass.valueOf(modemModel.getDeviceType().getName()), modemSerial, trap.getTimeStamp(), new String[][] {}, event);
			} else {
				modemDao.update(modem);
				log.info("Update modem=" + modemSerial);
				//event.getEventAlert().setName("Equipment Registration");
				//EventUtil.sendEvent("Equipment Registration", TargetClass.valueOf(trap.getProtocolName()), modemSerial, trap.getTimeStamp(), new String[][] {}, event);
			}
		} catch (Exception e) {
			log.error("Modem add/update error - " + e.getMessage(), e);
		} finally {
			if (txstatus != null)
				txmanager.commit(txstatus);
		}
		log.info("## 2. Modem Information processing finished ##");

		/******************
		 * 미터 등록
		 */
		log.info("## 3. Meter Information processing start ##");
		boolean isNewMeter = false;
		if (isRepeater) {
			log.info("This is Repeater. not save meter.");
		} else if (meterId == null || meterId.equals("")) {
			log.warn("Can't find meter id. not save meter.");
		} else {
			Meter meter = null;
			txstatus = null;
			try {
				txstatus = txmanager.getTransaction(null);
				meter = meterDao.get(meterId);
				Modem modem = modemDao.get(modemSerial);

				if (meter == null) { // 신규 미터 등록
					/**
					 * Licence check
					 */
					log.debug("licenceCheck !! " + meterId);
					if (licenceCheck(meterId, modemSerial, dcu.getSupplier())) {
						log.debug("licenceCheck pass - " + meterId);
						meter = new EnergyMeter();
						meter.setMdsId(meterId);
						meter.setInstallDate(currentTime);
						meter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.NewRegistered.name()));
					} else {
						log.warn("########### License violation. DCU=" + dcu.getSysID() + ", Modem=" + modemSerial + ", Meter=" + meterId + " #######");
						return;
					}
					isNewMeter = true;
				} else {
					if (!modem.getDeviceSerial().equals(meter.getModem().getDeviceSerial())) { // 모뎀이 바뀐경우 새로등록
						log.info("###### the modem of [" + meterId + "] meter is changed. from [" + meter.getModem().getDeviceSerial() + "] to [" + modem.getDeviceSerial() + "] ###");
					}
				}

				meter.setModem(modem);
				meter.setMeterType(CommonConstants.getMeterTypeByName(MeterType.EnergyMeter.name()));
				meter.setLocation(dcu.getLocation());
				meter.setSupplier(dcu.getSupplier());
				meter.setLpInterval(60);

				//LSKLIR3410DR-100
				if (meterModel.indexOf("3410CT") >= 0) {
					DeviceModel deviceModel = deviceModelDao.findByCondition("name", "LSIQ-3PCT");
					if (deviceModel != null) {
						meter.setModel(deviceModel);
					}
				}
				if (meterModel.indexOf("3405CP") >= 0) {
					DeviceModel deviceModel = deviceModelDao.findByCondition("name", "LSIQ-3PCV");
					if (deviceModel != null) {
						meter.setModel(deviceModel);
					}
				}
				if (meterModel.indexOf("3410DR") >= 0) {
					DeviceModel deviceModel = deviceModelDao.findByCondition("name", "LSIQ-3P");
					if (deviceModel != null) {
						meter.setModel(deviceModel);
					}
				}
				if (meterModel.indexOf("1210DR") >= 0) {
					DeviceModel deviceModel = deviceModelDao.findByCondition("name", "LSIQ-1P");
					if (deviceModel != null) {
						meter.setModel(deviceModel);
					}
				}

				//미터 등록/업데이트
				meterDao.saveOrUpdate(meter);

				/*
				 * 이벤트 등록
				 */
				log.debug("EventAlertLog => " + eventAlertLog.toJSONString());
				if (isNewMeter) {
					log.debug("########## Meter[" + meterId + "] Equipment Installation start ##########");
					EventUtil.sendEvent("Equipment Installation", TargetClass.valueOf(MeterType.EnergyMeter.name()), meterId, trap.getTimeStamp(), new String[][] {}, eventAlertLog);
					log.debug("########## Meter[" + meterId + "] Equipment Installation stop  ##########");
				} else {
					meterDao.saveOrUpdate(meter);
					log.debug("########## Meter[" + meterId + "] Equipment Registration start ##########");
					EventUtil.sendEvent("Equipment Registration", TargetClass.valueOf(MeterType.EnergyMeter.name()), meterId, trap.getTimeStamp(), new String[][] {}, eventAlertLog);
					log.debug("########## Meter[" + meterId + "] Equipment Registration stop  ##########");
				}

			} catch (Exception e) {
				log.error("Meter add/update error - " + e.getMessage(), e);
			} finally {
				if (txstatus != null)
					txmanager.commit(txstatus);
			}
		}
		log.info("## 3. Meter Information processing finished ##");

		event.setSupplier(dcu.getSupplier());
		event.setLocation(dcu.getLocation());

		log.debug("evtInstallMeter Action Compelte");
	}

	public void plcAssembleTest2(FMPTrap trap, String meterId, String modemSerial, String hwVer, String fwVer, String build) {

		log.debug("meter assemble test start..");
		Set<Condition> condition = new HashSet<Condition>();
		condition.add(new Condition("plcQualityTest", new Object[] { "plcQualityTest" }, null, Restriction.ALIAS));
		condition.add(new Condition("plcQualityTest.zigName", new Object[] { trap.getMcuId() }, null, Restriction.EQ));
		condition.add(new Condition("testStartDate", new Object[] { DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMdd") + "%" }, null, Restriction.LIKE));
		//condition.add(new Condition("testResult", new Object[]{ modemSerial }, null, Restriction.NULL));
		List<PlcQualityTestDetail> details = plcQualityTestDetailDao.findByConditions(condition);

		for (int i = 0; !details.isEmpty() && i < details.size(); i++) {

			String excelString = details.get(i).getMeterSerial() + details.get(i).getModemSerial();
			String testString = meterId + modemSerial;
			if (excelString.equals(testString)) {
				details.get(i).setTestResult(true);
				details.get(i).setHwVer(hwVer);
				details.get(i).setSwVer(fwVer);
				details.get(i).setSwBuild(build);
				details.get(i).setFailReason("");
				details.get(i).setCompleteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
				log.debug("plcQualityTestDao update [zig=" + trap.getMcuId() + "] testResult=[" + details.get(i).getTestResult() + "] meter/modem=" + meterId + "/" + modemSerial);

				PlcQualityTestDetail updateEntity = details.get(i);
				try {
					plcQualityTestDetailDao.update(updateEntity);
				} catch (Exception e) {
					log.error(e, e);
				}
			}

			if (modemSerial.equals(details.get(i).getModemSerial()) && !meterId.equals(details.get(i).getMeterSerial())) {
				details.get(i).setTestResult(false);
				details.get(i).setCompleteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
				details.get(i).setFailReason("Meter Serial Number is not match '" + meterId + "' VS '" + details.get(i).getMeterSerial() + "'");

				PlcQualityTestDetail updateEntity = details.get(i);
				try {
					plcQualityTestDetailDao.update(updateEntity);
				} catch (Exception e) {
					log.error(e, e);
				}
			}

			if (meterId.equals(details.get(i).getMeterSerial()) && !modemSerial.equals(details.get(i).getModemSerial())) {
				details.get(i).setTestResult(false);
				details.get(i).setCompleteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
				details.get(i).setFailReason("Modem Serial Number is not match '" + modemSerial + "' VS '" + details.get(i).getModemSerial() + "'");

				PlcQualityTestDetail updateEntity = details.get(i);
				try {
					plcQualityTestDetailDao.update(updateEntity);
				} catch (Exception e) {
					log.error(e, e);
				}
			}
		}

		condition = new HashSet<Condition>();
		condition.add(new Condition("plcQualityTest", new Object[] { "plcQualityTest" }, null, Restriction.ALIAS));
		condition.add(new Condition("plcQualityTest.zigName", new Object[] { trap.getMcuId() }, null, Restriction.EQ));
		condition.add(new Condition("testResult", new Object[] { Boolean.TRUE }, null, Restriction.EQ));
		List<PlcQualityTestDetail> succ = plcQualityTestDetailDao.findByConditions(condition);
		int succCnt = 0;
		if (succ != null && succ.size() > 0) {
			succCnt = succ.size();
		}

		PlcQualityTest plcQualityTest = plcQualityTestDao.getInfoByZig(trap.getMcuId());
		try {
			plcQualityTest.setCompleteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
			plcQualityTest.setSuccessCount(succCnt);
			plcQualityTestDao.update(plcQualityTest);
			log.info("update test result end ");
		} catch (Exception e) {
			log.error(e, e);
		}
		log.debug("plcQualityTestDao update");

	}

	public void plcAssembleTest(FMPTrap trap, String meterId, String modemSerial, String hwVer, String fwVer, String build) {

		log.debug("meter assemble test start..");
		PlcQualityTest plcQualityTest = plcQualityTestDao.getInfoByZig(trap.getMcuId());
		//if(plcQualityTest != null && plcQualityTest.getTestEnable()!= null && plcQualityTest.getTestEnable()){
		if (plcQualityTest != null) {
			log.debug("plcQualityTestDao test enable");
			List<PlcQualityTestDetail> details = plcQualityTest.getPlcQualityTestDetails();

			for (int i = 0; !details.isEmpty() && i < details.size(); i++) {

				String excelString = details.get(i).getMeterSerial() + details.get(i).getModemSerial();
				String testString = meterId + modemSerial;
				if (excelString.equals(testString)) {
					details.get(i).setTestResult(true);
					details.get(i).setHwVer(hwVer);
					details.get(i).setSwVer(fwVer);
					details.get(i).setSwBuild(build);
					log.debug("plcQualityTestDao update [zig=" + trap.getMcuId() + "] testResult=[" + details.get(i).getTestResult() + "] meter/modem=" + meterId + "/" + modemSerial);

					/*
					PlcQualityTestDetail updateEntity = details.get(i);
					try{
						plcQualityTestDetailDao.update(updateEntity);
					}catch(Exception e){
						log.error(e,e);
					}       
					*/
				}

				if (modemSerial.equals(details.get(i).getModemSerial()) && !meterId.equals(details.get(i).getMeterSerial())) {
					details.get(i).setTestResult(false);
					details.get(i).setFailReason("Meter Serial Number is not match '" + meterId + "' VS '" + details.get(i).getMeterSerial() + "'");
					/*
					PlcQualityTestDetail updateEntity = details.get(i);
					try{
						plcQualityTestDetailDao.update(updateEntity);
					}catch(Exception e){
						log.error(e,e);
					} 
					*/
				}

				if (meterId.equals(details.get(i).getMeterSerial()) && !modemSerial.equals(details.get(i).getModemSerial())) {
					details.get(i).setTestResult(false);
					details.get(i).setFailReason("Modem Serial Number is not match '" + modemSerial + "' VS '" + details.get(i).getModemSerial() + "'");
					/*
					PlcQualityTestDetail updateEntity = details.get(i);
					try{
						plcQualityTestDetailDao.update(updateEntity);
					}catch(Exception e){
						log.error(e,e);
					} 
					*/
				}
			}

			log.info("update test result start ");
			int successCount = 0;
			for (PlcQualityTestDetail detail : details) {
				if (detail.getTestResult() != null && detail.getTestResult()) {
					successCount++;
				}
			}
			try {
				plcQualityTest.setCompleteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
				plcQualityTest.setSuccessCount(successCount);
				plcQualityTestDao.update(plcQualityTest);
				log.info("update test result end ");
			} catch (Exception e) {
				log.error(e, e);
			}
			log.debug("plcQualityTestDao update");
		}
	}

	private boolean licenceCheck(String meterSerial, String modemSerial, Supplier supplier) {
		log.debug("#### meterSerial=" + meterSerial + ", modemSerial=" + modemSerial + ", supplier=" + supplier.getName());
		boolean isPass = true;

		int limitedCount = supplier.getLicenceMeterCount();
		String eventAlertName = "Excessive Number of Device Registration";
		String activatorType = TargetClass.EnergyMeter.name();
		String activatorId = " ";

		if (supplier.getLicenceUse() == 1) {
			int currentMeterCount = meterDao.getTotalMeterCount();
			log.debug("## licenceCheck - LicenceUse=" + supplier.getLicenceUse() + ", currentMeterCoun=" + currentMeterCount);

			if (!(currentMeterCount < limitedCount)) {
				isPass = false;
				EventUtil.sendEvent(eventAlertName, activatorType, activatorId, supplier.getId());

				log.warn("##################################################################################");
				log.warn("Excessive Number of Device Registration. (limited quantity : " + limitedCount + ") - Meter=" + meterSerial + ", Modem=" + modemSerial);
				log.warn("##################################################################################");
			}
		}
		return isPass;
	}

}
