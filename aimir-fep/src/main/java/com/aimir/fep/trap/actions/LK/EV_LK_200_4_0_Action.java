package com.aimir.fep.trap.actions.LK;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.EventStatus;
import com.aimir.constants.CommonConstants.McuType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.model.system.Code;
import com.aimir.model.system.Supplier;
import com.aimir.notification.FMPTrap;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;

/*
 * 200.4	evtPoweroff	OID	3	True	Major		DCU Power off
     1.4	reason	BYTE	1				Reboot reason (1: LowBattery, 2: User, 3: Malfunction, 4: Recovery, 5: Scheduled, 255: Unknown)
 *  
 */
public class EV_LK_200_4_0_Action implements EV_Action {
	private static Log log = LogFactory.getLog(EV_LK_200_4_0_Action.class);
	
	@Resource(name = "transactionManager")
	JpaTransactionManager txmanager;

	@Autowired
	MCUDao dcuDao;

	@Autowired
	CodeDao codeDao;

	@Autowired
	SupplierDao supplierDao;

	@Autowired
	LocationDao locationDao;

	/**
	 * execute event action
	 *
	 * @param trap - FMP Trap(DCU Event)
	 * @param event - Event Alert Log Data
	 * 
	 * 1.4	reason		BYTE	4		Reboot reason (1: LowBattery, 2: User, 3: Malfunction, 4: Recovery, 5: Scheduled, 255: Unknown)
	 */
	public void execute(FMPTrap trap, EventAlertLog event) throws Exception {
		String currentTime = TimeUtil.getCurrentTime();
		log.debug("EventName[evtPoweroff] " + " EventCode[" + trap.getCode() + "] DCU[" + trap.getMcuId() + "]");
		
		// Get Attributes
		String sysID = trap.getMcuId();
		log.debug("sysID[" + sysID + "]");

		String reason = event.getEventAttrValue("reason") == null ? "" : event.getEventAttrValue("reason");
		log.info("reason[" + reason + "]");

		if (event.getActivatorType() != TargetClass.DCU) {
			log.warn("Activator Type is not MCU");
			return;
		}

		Code mcuStatus = codeDao.getCodeIdByCodeObject("1.1.4.1"); //  <system.Code code="1.1.4.1" name="Normal" descr="Normal" order="1" parent="1.1.4"/>

		MCU mcu = new MCU();
		mcu.setSysID(sysID);
		mcu.setMcuStatus(mcuStatus);
		mcu.setProtocolType(CommonConstants.getProtocolByName(Protocol.GPRS.name()));
		
		TransactionStatus txstatus = null;
		try {
			txstatus = txmanager.getTransaction(null);

			MCU existMcu = dcuDao.get(mcu.getSysID());
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
				dcuDao.add(mcu);

				/*
				 * DCU Registration Event save
				 */
				try {
					EventAlertLog eventAlertLog = new EventAlertLog();
					eventAlertLog.setStatus(EventStatus.Open);
					eventAlertLog.setOpenTime(event.getOpenTime());
					eventAlertLog.setLocation(event.getLocation());
					eventAlertLog.setSupplier(event.getSupplier());
					log.debug("########## DCU[" + mcu.getSysID() + "] Equipment Registration start ##########");
					EventUtil.sendEvent("Equipment Registration", TargetClass.DCU, mcu.getSysID(), currentTime, new String[][] {}, eventAlertLog);
					log.debug("########## DCU[" + mcu.getSysID() + "] Equipment Registration stop  ##########");
				} catch (Exception e) {
					log.error("Equipment Registration save error - " + e.getMessage(), e);
				}
			} else {
				log.info("mcu[" + existMcu.getSysID() + "] is existed!! - Location Id:" + existMcu.getLocation().getId() + " Location Name:" + existMcu.getLocation().getName());

				existMcu.setLastCommDate(currentTime);
				existMcu.setProtocolVersion("0102");
				existMcu.setMcuStatus(mcu.getMcuStatus());
				existMcu.setNameSpace(FMPProperty.getProperty("default.namespace.dcu", "LK"));
				dcuDao.update(existMcu);
			}

			// REASON 분류
			String evtReason = "Undefined";
			if (reason.equals("1")) {
				evtReason = "Low Battery (1)";
			} else if (reason.equals("2")) {
				evtReason = "User (2)";
			} else if (reason.equals("3")) {
				evtReason = "Malfunction (3)";
			} else if (reason.equals("4")) {
				evtReason = "Recovery (4)";
			} else if (reason.equals("5")) {
				evtReason = "Scheduled (5)";
			} else if (reason.equals("255")) {
				evtReason = "Unknown (255)";
			} else {
				evtReason = "Undefined";
			}

			/*
			 * Event 정보 저장
			 */
			if (existMcu != null) {
				event.setActivatorIp(existMcu.getIpAddr());
				event.setSupplier(existMcu.getSupplier());
			} else {
				event.setActivatorIp(mcu.getIpAddr());
				event.setSupplier(mcu.getSupplier());
			}
			//EventAlert MsgPattern => $(event.eventAttrs.evtReason)
			event.append(EventUtil.makeEventAlertAttr("evtReason", "java.lang.String", evtReason));
		} catch (Exception e) {
			log.error("evtPoweroff error - " + e.getMessage(), e);
		} finally {
			if (txstatus != null)
				txmanager.commit(txstatus);
		}

		log.debug("DCU Power off Action Complete");
	}
}
