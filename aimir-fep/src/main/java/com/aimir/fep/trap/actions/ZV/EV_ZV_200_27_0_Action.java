package com.aimir.fep.trap.actions.ZV;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.MCUDao;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.EventAlertAttr;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.notification.FMPTrap;
import com.aimir.util.TimeUtil;

/*
 * evtLowBattery(200.21.0)
 */
@Component
public class EV_ZV_200_27_0_Action implements EV_Action{
	private static Log log = LogFactory.getLog(EV_ZV_200_27_0_Action.class);
	
	@Resource(name = "transactionManager")
	JpaTransactionManager txmanager;
	
	@Autowired
	MCUDao mcuDao;
	
	public void execute(FMPTrap trap, EventAlertLog event) throws Exception {
		String mcuId = trap.getMcuId();
		log.debug("EventName[evtBatteryOut] " + " EventCode[" + trap.getCode() + "] DCU[" + mcuId + "]");
		
		String batteryADC =  event.getEventAttrValue("wordEntry") == null ? "" : event.getEventAttrValue("wordEntry");
		log.debug("batteryADC: " + batteryADC);
		
		TransactionStatus txstatus = null;
		try {
			txstatus = txmanager.getTransaction(null);
			event.setActivatorId(mcuId);
			event.setActivatorType(TargetClass.DCU);
			EventAlertAttr ea = EventUtil.makeEventAlertAttr("message",
                    "java.lang.String",
                    "[DCU Battery out detect] " + batteryADC);
			event.append(ea);
			
			MCU mcu = mcuDao.get(mcuId);
			
			if (mcu == null) {
				log.error("does not exist target Mcu");
				return;
			} else {
				mcu.setLastCommDate(TimeUtil.getCurrentTime());
				mcuDao.update(mcu);
			}
			 
			txmanager.commit(txstatus);
		} catch (Exception e) {
			log.error(e, e);
			if (txstatus != null)
				txmanager.rollback(txstatus);
			throw e;
		}
	}
}
