package com.aimir.fep.trap.actions.LK;

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
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.DeviceModelDao;
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
	220.2	evtUninstallMeter	OID	3	True	Major		Uninstall meter

	21.1.1	meterId	STRING	20				Meter ID
	21.1.2	meterModel	STRING	20				Meter model
	21.1.3	meterVendor	STRING	3				Meter Vendor
	21.1.5	meterPhase	BYTE	1				Meter phase (electricity only)
 */
@Component
public class EV_LK_220_2_0_Action implements EV_Action {
	private static Log log = LogFactory.getLog(EV_LK_220_2_0_Action.class);

	@Resource(name = "transactionManager")
	JpaTransactionManager txmanager;

	@Autowired
	MCUDao dcuDao;

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

	/**
	 * execute event action
	 *
	 */
	@Override
	public void execute(FMPTrap trap, EventAlertLog event) throws Exception {
		String currentTime = TimeUtil.getCurrentTime();
		log.debug("EventName[evtUninstallMeter] " + " EventCode[" + trap.getCode() + "] DCU[" + trap.getMcuId() + "]");

		String sysID = trap.getMcuId();
		log.debug("sysID[" + sysID + "]");

		String meterId = event.getEventAttrValue("meterId");
		String meterModel = event.getEventAttrValue("meterModel") == null ? "" : event.getEventAttrValue("meterModel");
		String meterVendor = event.getEventAttrValue("meterVendor") == null ? "" : event.getEventAttrValue("meterVendor");
		int meterPhase = Integer.parseInt(event.getEventAttrValue("meterPhase") == null ? "0" : event.getEventAttrValue("meterPhase"));

		log.debug("meterId[" + meterId + "]");
		log.debug("meterModel[" + meterModel + "]");
		log.debug("meterVendor[" + meterVendor + "]");
		log.debug("meterPhase[" + meterPhase + "]");

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

			event.getEventAlert().setName("Equipment UnInstallation");
			event.append(EventUtil.makeEventAlertAttr("message", "java.lang.String", "Meter ID = " + meterId));
		} catch (Exception e) {
			log.error("evtBatteryOut error - " + e.getMessage(), e);
		} finally {
			if (txstatus != null)
				txmanager.commit(txstatus);
		}

		log.debug("evtUninstallMeter Action Compelte");
	}

}
