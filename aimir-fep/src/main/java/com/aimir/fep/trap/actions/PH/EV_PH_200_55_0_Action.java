package com.aimir.fep.trap.actions.PH;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.EventAlertLog;
import com.aimir.notification.FMPTrap;

/**
 * Event ID : 200.55.0
 *
 * @author Elevas Park
 * @version $Rev: 1 $, $Date: 2017-09-27 15:59:15 +0900 $,
 */
@Service
public class EV_PH_200_55_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_PH_200_55_0_Action.class);

    /**
     * execute event action
     *
     * @param trap - FMP Trap(MCU Event)
     * @param event - Event Data
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
        log.debug("EventCode[" + trap.getCode()
                +"] MCU["+trap.getMcuId()+"]");

        // Initialize
        String _reason = event.getEventAttrValue("byteEntry");
        byte reason = (byte)0xFF;
        
        if (_reason != null && !"".equals(_reason))
            reason = Byte.parseByte(_reason);
        
        if (reason == 0x01)
            _reason = "LowBattery";
        else if (reason == 0x02)
            _reason = "User";
        else if (reason == 0x03)
            _reason = "Malfunction";
        else if (reason == 0x04)
            _reason = "Recovery";
        else if (reason == 0x05)
            _reason = "Scheduled";
        else if (reason == 0x06)
        	_reason = "USB Driver Loading Fail(for Mobile)";
        else if (reason == 0x07)
        	_reason = "PPPD Hang";
        else
            _reason = "Unknown";
        
        log.debug("RESON[" + _reason + "]");

        String message = "Reset."; 
        
        //event.append(EventUtil.makeEventAlertAttr("message", "java.lang.String", _reason));
        event.append(EventUtil.makeEventAlertAttr("message", "java.lang.String", message));

        log.debug("Event Action Compelte");
    }
}
