package com.aimir.fep.trap.actions.SP;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.EventAlertLog;
import com.aimir.notification.FMPTrap;
import com.aimir.util.StringUtil;

/*
 * Event ID : 200.13.0 
 * 
 */
@Component
public class EV_SP_200_13_0_Action implements EV_Action {
	private static Logger log = LoggerFactory.getLogger(EV_SP_200_13_0_Action.class);

	@Override
	public void execute(FMPTrap trap, EventAlertLog event) throws Exception
	{
        log.debug("EventCode[" + trap.getCode()
        +"] MCU["+trap.getMcuId()+"]");

        String sysState = event.getEventAttrValue("sysState");
        log.debug("SYS_STATE" + SYSTEM_STATUS.getItem(Integer.parseInt(sysState)).getDesc() + "]");

        String ntwType = StringUtil.nullToBlank(event.getEventAttrValue("ntwType"));
        byte reason = (byte)0xFF;
        
        if (ntwType != null && !"".equals(ntwType))
            reason = Byte.parseByte(ntwType);
        
        if (reason == 0x00)
        	ntwType = "static";
        else if (reason == 0x01)
        	ntwType = "DHCP";
        else
        	ntwType = "PPP";
        
        log.debug("NTW_TYPE[" + ntwType + "]");

		String ntwApnName = StringUtil.nullToBlank(event.getEventAttrValue("ntwApnName"));
        log.debug("NTW_APN_NAME" + ntwApnName + "]");

        String ntwState = event.getEventAttrValue("ntwState");
        log.debug("NTW_STATE" + NETWORK_STATUS.getItem(Integer.parseInt(ntwState)).getDesc() + "]");

		String message = "Network status change(Down)."; 
        event.append(EventUtil.makeEventAlertAttr("message", "java.lang.String", message));
	}

}
