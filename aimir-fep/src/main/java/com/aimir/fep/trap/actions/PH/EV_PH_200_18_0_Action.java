package com.aimir.fep.trap.actions.PH;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.EventAlertLog;
import com.aimir.notification.FMPTrap;

/**
 * Event ID : 200.18.0
 *
 * @author Elevas Park
 * @version $Rev: 1 $, $Date: 2017-09-27 15:59:15 +0900 $,
 */
@Service
public class EV_PH_200_18_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_PH_200_18_0_Action.class);
   
   /**
     * execute event action
     *
     * @param trap - FMP Trap(Communication Restore Alarm Event)
     * @param event - Event Alert Log Data
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
        log.debug("EventCode[" + trap.getCode()
        +"] MCU["+trap.getMcuId()+"]");

        String sysSerialNumber = event.getEventAttrValue("sysSerialNumber");
        log.debug("SYS_SERIALNUMBER[" + sysSerialNumber + "]");

        String message = "KMS Network Key."; 
        
        event.append(EventUtil.makeEventAlertAttr("message", "java.lang.String", message));
        
        log.debug("Event Action Compelte");
    }
}

