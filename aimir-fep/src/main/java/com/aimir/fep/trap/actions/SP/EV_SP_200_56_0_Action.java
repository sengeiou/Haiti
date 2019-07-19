package com.aimir.fep.trap.actions.SP;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.EventAlertLog;
import com.aimir.notification.FMPTrap;

/**
 * Event ID : 200.56.0
 *
 * @author Elevas Park
 * @version $Rev: 1 $, $Date: 2017-09-27 15:59:15 +0900 $,
 */
@Service
public class EV_SP_200_56_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_SP_200_56_0_Action.class);

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
            _reason = "Init Failure (IPSEC files)";
        else if (reason == 0x02)
            _reason = "Init Failure (TimeSync)";
        else if (reason == 0x03)
            _reason = "Init Failure (3Pass Auth)";
        else if (reason == 0x04)
            _reason = "Init Failure (ListenServer)";
        else if (reason == 0x05)
            _reason = "Init Failure (DCU Info)";
        else if (reason == 0x06)
        	_reason = "Battery Broken (BATT_ADC Under 2.5v)";
        else
            _reason = "Battery Unknown (If PowerFail does not occur, but Low Battery occurs)";
        
        log.debug("RESON[" + _reason + "]");
        
        String message = "Self Test."; 
        
        //event.append(EventUtil.makeEventAlertAttr("message", "java.lang.String", _reason));
        event.append(EventUtil.makeEventAlertAttr("message", "java.lang.String", message));

        log.debug("Event Action Compelte");
    }
}
