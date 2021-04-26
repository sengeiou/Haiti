package com.aimir.fep.trap.actions;

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
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.notification.FMPTrap;

@Component
public class EV_200_30_0_Action implements EV_Action{
private static Log log = LogFactory.getLog(EV_200_30_0_Action.class);
    
    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;
    
    @Autowired
    MCUDao mcuDao;

	@Override
	public void execute(FMPTrap trap, EventAlertLog event) throws Exception {
		String mcuId = trap.getMcuId();
		log.info("EV_200_30_0_Action : EventCode[" + trap.getCode() +"] MCU["+trap.getMcuId()+"]");
		
		TransactionStatus txstatus = null;
		try {
			txstatus = txmanager.getTransaction(null);
			
			MCU mcu = mcuDao.get(mcuId);
			if(mcu != null) {
				EventUtil.sendEvent("MCU Battery Error", TargetClass.DCU, mcu.getSysID(), trap.getTimeStamp(), new String[][] {}, event);
			}
		}catch(Exception e) {
			if (txstatus != null)
				txmanager.rollback(txstatus);
		}
	}
}
