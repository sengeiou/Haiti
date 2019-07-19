package com.aimir.fep.trap.actions.PH;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.EventAlertLog;
import com.aimir.notification.FMPTrap;
import com.aimir.util.StringUtil;

/*
 * Event ID : 200.12.0 
 * 200.12	evtIpChange
 * 2.2.5	sysState	WORD	2					System status (표 4)
   2.5.1	ntwType	BYTE	1					Network type (0: static, 1: DHCP, 3: PPP)
   2.5.3	ntwState	WORD	2					Network status (표 6)
   2.5.4	ntwGateway	UINT	4					Default gateway
   2.5.5	ntwEthIp	UINT	4					Ethernet IP address
   2.5.6	ntwEthSubnetMask	UINT	4					Ethernet subnet mask
   2.5.9	ntwPppIp	UINT	4					PPP IP address
   2.5.10	ntwPppSubnetMask	UINT	4					PPP subnet mask
 */
@Component
public class EV_PH_200_12_0_Action implements EV_Action {
	private static Logger log = LoggerFactory.getLogger(EV_PH_200_12_0_Action.class);

	@Override
	public void execute(FMPTrap trap, EventAlertLog event) throws Exception
	{
        log.debug("EventCode[" + trap.getCode()
        +"] MCU["+trap.getMcuId()+"]");

        String sysState = event.getEventAttrValue("sysState");
        log.debug("SYS_STATE [" + SYSTEM_STATUS.getItem(Integer.parseInt(sysState)).getDesc() + "]");

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
        
        log.debug("NTW_TYPE [" + ntwType + "]");

        String ntwState = event.getEventAttrValue("ntwState");
        log.debug("NTW_STATE [" + NETWORK_STATUS.getItem(Integer.parseInt(ntwState)).getDesc() + "]");

        String ntwGateway = event.getEventAttrValue("ntwGateway");
        log.debug("NTW_GATEWAY [" + ntwGateway + "]");

        String ntwEthIp = event.getEventAttrValue("ntwEthIp");
        log.debug("NTW_ETH_IP [" + ntwEthIp + "]");

        String ntwEthSubnetMask = event.getEventAttrValue("ntwEthSubnetMask");
        log.debug("NTW_ETH_SUBNETMASK [" + ntwEthSubnetMask + "]");

        String ntwPppIp = event.getEventAttrValue("ntwPppIp");
        log.debug("NTW_PPP_IP [" + ntwPppIp + "]");

        String ntwPppSubnetMask = event.getEventAttrValue("ntwPppSubnetMask");
        log.debug("NTW_PPP_SUBNETMASK [" + ntwPppSubnetMask + "]");

        String message = "Network status change(Down)."; 
        event.append(EventUtil.makeEventAlertAttr("message", "java.lang.String", message));
	}

}
