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
import com.aimir.dao.device.ChangeLogDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.meter.entry.MeasurementDataEntry;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.model.system.Code;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.Supplier;
import com.aimir.notification.FMPTrap;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;

/**
 * evtDcuInstall
 */
@Component
public class EV_LK_200_1_0_Action implements EV_Action {
	private static Log log = LogFactory.getLog(EV_LK_200_1_0_Action.class);

	@Resource(name = "transactionManager")
	JpaTransactionManager txmanager;

	@Autowired
	MCUDao dcuDao;

	@Autowired
	MeterDao meterDao;

	@Autowired
	CodeDao codeDao;

	@Autowired
	DeviceModelDao deviceModelDao;

	@Autowired
	SupplierDao supplierDao;

	@Autowired
	LocationDao locationDao;

	/*
	 * 200.1	evtDcuInstall	OID	3	True	Critical		Concentrator Registry values
	 * 
	 * 2.1.1	sysID			UINT	4		DCU ID
	 * 2.1.2	sysType			UINT	4		DCU Type (표 4)
	 * 2.1.3	sysName			STRING	64		DCU 명칭 (관리용 명칭)
	 * 2.1.4	sysLocation		STRING	64		DCU 위치
	 * 2.1.5	sysContact		STRING	64		DCU 담당자
	 * 2.1.6	sysModel		STRING	32		DCU HW Model Name
	 * 2.1.7	sysHwVersion	WORD	2		DCU HW Version (상위 Byte : Major, 하위 Byte : Minor)
	 * 2.1.8	sysHwBuild		BYTE	1		DCU HW Build Number
	 * 2.1.9	sysSwVersion	WORD	2		DCU SW Version (상위 Byte : Major, 하위 Byte : Minor)
	 * 2.1.10	sysSwRevision	UINT	4		DCU SW Build Number
	 * 2.1.11	sysPort			UINT	4		DCU Listen port number
	 * 2.5.7    ntwEthPhy 		BYTE	6		Ethernet0 physical address
	 */
	public void execute(FMPTrap trap, EventAlertLog event) throws Exception {
		String currentTime = TimeUtil.getCurrentTime();
		log.debug("EventName[evtDcuInstall] " + " EventCode[" + trap.getCode() + "] DCU[" + trap.getMcuId() + "]");
		MeasurementDataEntry MeasurementDataEntry  = new MeasurementDataEntry();
		
		// Initialize
		String ipAddr = event.getEventAttrValue("ntwPppIp") == null ? "" : event.getEventAttrValue("ntwPppIp");
		String sysID = trap.getMcuId();
		String sysType = event.getEventAttrValue("sysType");
		String sysName = event.getEventAttrValue("sysName");
		String sysLocation = event.getEventAttrValue("sysLocation");
		String sysContact = event.getEventAttrValue("sysContact");
		String sysModel = event.getEventAttrValue("sysModel");
		String sysHwVersion = event.getEventAttrValue("sysHwVersion");
		String sysHwBuild = event.getEventAttrValue("sysHwBuild");
		String sysSwVersion = event.getEventAttrValue("sysSwVersion");
		String sysSwRevision = event.getEventAttrValue("sysSwRevision");
		String sysPort = event.getEventAttrValue("sysPort");
		String ntwEthPhy = event.getEventAttrValue("ntwEthPhy.hex");

		if (sysSwVersion != null && !"".equals(sysSwVersion)) {
			sysSwVersion = DataUtil.getVersionString(Integer.parseInt(sysSwVersion));
		}
		if (sysHwVersion != null && !"".equals(sysHwVersion)) {
			sysHwVersion = DataUtil.getVersionString(Integer.parseInt(sysHwVersion));
		}

		log.debug("ipAddr[" + ipAddr + "] is not use.");
		log.debug("sysID[" + sysID + "]");
		log.debug("sysType[" + sysType + "]");
		log.debug("sysName[" + sysName + "]");
		log.debug("sysLocation[" + sysLocation + "]");
		log.debug("sysContact[" + sysContact + "]");
		log.debug("sysModel[" + sysModel + "]");
		log.debug("sysHwVersion[" + sysHwVersion + "]");
		log.debug("sysHwBuild[" + sysHwBuild + "]");
		log.debug("sysSwVersion[" + sysSwVersion + "]");
		log.debug("sysSwRevision[" + sysSwRevision + "]");
		log.debug("sysPort[" + sysPort + "]");
		log.debug("ntwEthPhy[" + ntwEthPhy + "]");
		
		if (event.getActivatorType() != TargetClass.DCU) {
			log.warn("Activator Type is not MCU");
			return;
		}

		Code mcuStatus = codeDao.getCodeIdByCodeObject("1.1.4.1"); //  <system.Code code="1.1.4.1" name="Normal" descr="Normal" order="1" parent="1.1.4"/>

		/*
		 * Default DCU 정보 세팅
		 */
		MCU mcu = new MCU();
		mcu.setSysID(sysID);
		mcu.setMcuStatus(mcuStatus);
		mcu.setProtocolType(CommonConstants.getProtocolByName(Protocol.GPRS.name()));
		
		if (sysLocation == null || sysLocation.equals("")) {
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
		} else {
			mcu.setSysLocation(sysLocation);
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

		// Model
		DeviceModel model = deviceModelDao.findByCondition("name", sysModel);
		if (model != null) {
			mcu.setDeviceModel(model);
		}

		// MAC Addres
		String a = "";
		String b = "";
		String c = "";
		String d = "";
		String e = "";
		String f = "";
		if (ntwEthPhy != null && !"".equals(ntwEthPhy)) {
			a = ntwEthPhy.substring(0, 2);
			b = ntwEthPhy.substring(2, 4);
			c = ntwEthPhy.substring(4, 6);
			d = ntwEthPhy.substring(6, 8);
			e = ntwEthPhy.substring(8, 10);
			f = ntwEthPhy.substring(10, 12);
		}
		log.info("### MAC Address = " + a + ":" + b + ":" + c + ":" + d + ":" + e + ":" + f);
		mcu.setMacAddr(a + ":" + b + ":" + c + ":" + d + ":" + e + ":" + f);
		mcu.setLastCommDate(currentTime);
		mcu.setNetworkStatus(1);
		mcu.setMcuType(CommonConstants.getMcuTypeByName(McuType.DCU.name()));
		mcu.setSysLocalPort(new Integer(sysPort));
		mcu.setNameSpace(trap.getNameSpace());
		mcu.setProtocolVersion("0102");
		mcu.setSysHwVersion(sysHwVersion);
		mcu.setSysSwVersion(sysSwVersion);
		mcu.setSysName(sysName);
		mcu.setSysModel(sysModel);
		mcu.setSysSwRevision(sysSwRevision);

		TransactionStatus txstatus = null;
		try {
			txstatus = txmanager.getTransaction(null);

			MCU existMcu = dcuDao.get(mcu.getSysID());
			if (existMcu == null) {
				log.info("mcu[" + mcu.getSysID() + "] is not existed!!");

				/*
				 * DCU  신규 등록
				 */
				mcu.setInstallDate(currentTime);
				dcuDao.add(mcu);
			} else {
				log.info("mcu[" + existMcu.getSysID() + "] is existed!! - Location Id:" + existMcu.getLocation().getId() + " Location Name:" + existMcu.getLocation().getName());

				existMcu.setSysName(mcu.getSysName());
				existMcu.setMcuType(mcu.getMcuType());
				existMcu.setDeviceModel(mcu.getDeviceModel());
				existMcu.setSysModel(mcu.getSysModel());
				existMcu.setMacAddr(mcu.getMacAddr());
				existMcu.setSysLocalPort(mcu.getSysLocalPort());
				existMcu.setSysPhoneNumber(mcu.getSysPhoneNumber());
				existMcu.setProtocolType(mcu.getProtocolType());
				existMcu.setSysHwVersion(mcu.getSysHwVersion());
				existMcu.setSysSwVersion(mcu.getSysSwVersion());
				existMcu.setSysSwRevision(mcu.getSysSwRevision());
				existMcu.setLastCommDate(mcu.getLastCommDate());
				existMcu.setProtocolVersion(mcu.getProtocolVersion());
				existMcu.setNameSpace(mcu.getNameSpace());
				dcuDao.update(existMcu);
			}

			/*
			 * Event 정보 저장
			 */
			event.setActivatorIp(mcu.getIpAddr());
			event.setSupplier(mcu.getSupplier());
			event.setLocation(mcu.getLocation());
		} catch (Exception ee) {
			log.error("evtDcuInstall error - " + ee.getMessage(), ee);
		} finally {
			if (txstatus != null)
				txmanager.commit(txstatus);
		}

		log.debug("DCU Install Event Action Compelte");
	}

}
