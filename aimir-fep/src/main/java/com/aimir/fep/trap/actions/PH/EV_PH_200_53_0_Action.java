package com.aimir.fep.trap.actions.PH;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.EventAlertLog;
import com.aimir.notification.FMPTrap;

/**
 * Event ID : EV_PH_200.53.0
 *
 * @author 
 * @version $Rev: 1 $, $Date: 2017-09-27 10:00:00 +0900 $,
 */
@Component
public class EV_PH_200_53_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_PH_200_53_0_Action.class);
  
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

        String message = "Meter Error."; 
        
        event.append(EventUtil.makeEventAlertAttr("message", "java.lang.String", message));
        
        log.debug("Event Action Compelte");
    }
}
