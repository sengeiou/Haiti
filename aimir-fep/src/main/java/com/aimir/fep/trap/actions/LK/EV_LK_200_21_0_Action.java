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
	200.21	evtLowBattery	OID	3	True	Critical		DCU Low battery detect
	  1.5	batteryADC	WORD	2				Battery ADC value
 * 
 */
@Component
public class EV_LK_200_21_0_Action implements EV_Action {
	private static Log log = LogFactory.getLog(EV_LK_200_21_0_Action.class);

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
	 */
	@Override
	public void execute(FMPTrap trap, EventAlertLog event) throws Exception {
		String currentTime = TimeUtil.getCurrentTime();
		log.debug("EventName[eventLowBattery] " + " EventCode[" + trap.getCode() + "] DCU[" + trap.getMcuId() + "]");
		String ipAddr = event.getEventAttrValue("ntwPppIp") == null ? "" : event.getEventAttrValue("ntwPppIp");
		String sysID = trap.getMcuId();

		log.debug("ipAddr[" + ipAddr + "] is not use.");
		log.debug("sysID[" + sysID + "]");

		String batteryADC = event.getEventAttrValue("wordEntry") == null ? "" : event.getEventAttrValue("wordEntry");
		log.info("batteryADC[" + batteryADC + "]");

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
				throw new Exception("Can't find registerd MCU ID. please check MCU ID = [" + sysID + "]");
			} else {
				log.info("mcu[" + existMcu.getSysID() + "] is existed!! - Location Id:" + existMcu.getLocation().getId() + " Location Name:" + existMcu.getLocation().getName());
				
				existMcu.setLowBatteryFlag(1);
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
			event.getEventAlert().setName("DCU Low battery detect");
			event.append(EventUtil.makeEventAlertAttr("batteryADC", "java.lang.String", batteryADC));
		} catch (Exception e) {
			log.error("evtLowBattery error - " + e.getMessage(), e);
		} finally {
			if (txstatus != null)
				txmanager.commit(txstatus);
		}

		log.debug("DCU Low Battery Action Compelte");
	}
}
