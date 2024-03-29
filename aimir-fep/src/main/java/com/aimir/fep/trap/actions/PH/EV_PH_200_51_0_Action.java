package com.aimir.fep.trap.actions.PH;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.EventAlertAttr;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.model.system.Code;
import com.aimir.notification.FMPTrap;
import com.aimir.util.TimeUtil;

/**
 * Event ID : EV_PH_200_51_0 Processing Class
 *
 * @author Tatsumi
 * @version $Rev: 1 $, $Date: 2016-05-17 15:59:15 +0900 $,
 */
@Component
public class EV_PH_200_51_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_PH_200_51_0_Action.class);
    
    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;
    
    @Autowired
    MCUDao mcuDao;
    
    @Autowired
    CodeDao codeDao;
    
    /**
     * execute event action
     *
     * @param trap - FMP Trap(MCU Event)
     * @param event - Event Data
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
        log.debug("EV_PH_200_51_0_Action : EventCode[" + trap.getCode()
                +"] MCU["+trap.getMcuId()+"]");
        
        TransactionStatus txstatus = null;
        try {
            txstatus = txmanager.getTransaction(null);
            
            String mcuId = trap.getMcuId();
            MCU mcu = mcuDao.get(mcuId);
            log.debug("EV_PH_200_51_0_Action : mcuId[" + mcuId + "]");
    
            if(mcu != null)
            {
                mcu.setLastCommDate(TimeUtil.getCurrentTime());
                mcu.setNetworkStatus(0);
                mcu.setPowerState(1);
                
                Code powerDown = codeDao.findByCondition("code", "1.1.4.3");
                if (powerDown != null)
                    mcu.setMcuStatus(powerDown);
                
                log.debug("MCU Power Down Action Started");
                EventAlertLog alert = EventUtil.findOpenAlert(event);
                
//                if (alert != null)
//                {
//                    log.debug("MCU Power Down Action issue int the past");
//	    
//	                Integer t = alert.getOccurCnt();
//	                int ti = t.intValue()+1;
//	                alert.setOccurCnt(new Integer(ti));
//	                
//	                 event = alert;
//                }
                 event.setActivatorId(trap.getSourceId());
                 event.setActivatorType(TargetClass.DCU);
                 EventAlertAttr ea = EventUtil.makeEventAlertAttr("message",
                         "java.lang.String",
                         "MCU Power Down");
                 event.append(ea);

            } 
            else
            {
                log.debug("MCU Power Down Action failed : Unknown MCU");
            }
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }
    }
}
