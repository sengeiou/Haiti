package com.aimir.fep.trap.actions.LK;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.ChangeLogDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.model.system.Location;
import com.aimir.notification.FMPTrap;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;

/**
 * evtJoinNode
 */
@Component
public class EV_LK_210_1_0_Action implements EV_Action {
	private static Log log = LogFactory.getLog(EV_LK_210_1_0_Action.class);

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
	 * @param trap - FMP Trap(DCU Event)
	 * @param event - Event Alert Log Data
	 * 
	 * 1.5	targetShortId	WORD 2	Target Short ID
	 */
	public void execute(FMPTrap trap, EventAlertLog event) throws Exception {
		log.debug("EventName[eventJoin] " + " EventCode[" + trap.getCode() + "] Modem[" + trap.getSourceId() + "]");

		String mcuId = trap.getMcuId();
		String ipAddr = event.getEventAttrValue("ntwPppIp") == null ? "" : event.getEventAttrValue("ntwPppIp");
		String modemSerial = trap.getSourceId();
		String timeStamp = trap.getTimeStamp();
		String srcType = trap.getSourceType();

		log.debug("mcuId = " + mcuId + ", ipAddr = " + ipAddr + ", modemSeriall = " + modemSerial + ", timeStamp = " + timeStamp + ", srcType = " + srcType);

		String targetShortId = StringUtil.nullToBlank(event.getEventAttrValue("wordEntry"));
		log.debug("targetShortId => " + targetShortId);
		
		TransactionStatus txstatus = null;
		Location location = null;
		try {
			txstatus = txmanager.getTransaction(null);
			MCU existMcu = dcuDao.get(mcuId);
			if(existMcu != null) {
				log.debug("last comm date : " + TimeUtil.getCurrentTime());
				existMcu.setLastCommDate(TimeUtil.getCurrentTime());
				dcuDao.update(existMcu);
				
				location = existMcu.getLocation();
			}			
		} finally {
			if (txstatus != null)
				txmanager.commit(txstatus);
		}
		
		event.append(EventUtil.makeEventAlertAttr("mcuID", "java.lang.String", mcuId));

		event.setActivatorType(TargetClass.PLCIU);
		event.setActivatorId(modemSerial);
		event.setLocation(location);
		event.append(EventUtil.makeEventAlertAttr("message", "java.lang.String", "Modem Join Network - " + targetShortId));

		log.debug("PLC Modem Join Event Action Compelte");
	}

}
