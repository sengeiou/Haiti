package com.aimir.fep.trap.actions.ZV;

import java.util.Hashtable;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants;
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
import com.aimir.fep.command.conf.DefaultConf;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.model.system.Supplier;
import com.aimir.notification.FMPTrap;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;

/**
 * Event ID : ZV_210.1.0 (PLC Modem Join) <br>
 * Vietnam Zigbee Modem
 *
 */
@Component
public class EV_ZV_210_1_0_Action implements EV_Action {
	private static Log log = LogFactory.getLog(EV_ZV_210_1_0_Action.class);

	@Autowired
	MCUDao dcuDao;

	@Autowired
	MeterDao meterDao;

	@Autowired
	CodeDao codeDao;

	@Autowired
	CommandGW commandGW;

	@Autowired
	ChangeLogDao clDao;

	@Autowired
	DeviceModelDao deviceModelDao;

	@Autowired
	SupplierDao supplierDao;

	@Autowired
	LocationDao locationDao;
	
	@Resource(name = "transactionManager")
	JpaTransactionManager txmanager;

	/**
	 * execute event action
	 *
	 * @param trap
	 *            - FMP Trap(DCU Event)
	 * @param event
	 *            - Event Alert Log Data
	 * 
	 *            31.3.2 moG3ShortId WORD 2 Modem Short ID
	 */
	public void execute(FMPTrap trap, EventAlertLog event) throws Exception {

		log.debug("EventName[eventJoin] " + " EventCode[" + trap.getCode() + "] Modem[" + trap.getSourceId() + "]");
		String mcuId = trap.getMcuId();
		String ipAddr = trap.getIpAddr();
		String modemSerial = trap.getSourceId();

		//int moG3ShortId = Integer.parseInt(event.getEventAttrValue("moG3ShortId") == null ? "0" : event.getEventAttrValue("moG3ShortId"));
		//String shortId = Hex.decode(DataUtil.get2ByteToInt(moG3ShortId));
		//log.info("moG3ShortId["+shortId+"]");      
		
		TransactionStatus txstatus = null;
		
		try { 
			txstatus = txmanager.getTransaction(null);
			MCU existMcu = dcuDao.get(mcuId);
			if(existMcu != null) {
				log.debug("last comm date : " + TimeUtil.getCurrentTime());
				existMcu.setLastCommDate(TimeUtil.getCurrentTime());
				dcuDao.update(existMcu);
			}			
		} finally {
			if (txstatus != null)
				txmanager.commit(txstatus);
		}
		
		

		event.append(EventUtil.makeEventAlertAttr("mcuID", "java.lang.String", mcuId));

		event.setActivatorType(TargetClass.ZRU);
		event.setActivatorId(modemSerial);
		event.append(EventUtil.makeEventAlertAttr("message", "java.lang.String", "Modem Join Network"));

		log.debug("Zigbe Modem Join Event Action Compelte");
	}

}
