package com.aimir.fep.trap.actions.SP;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.protocol.security.OacServerApi;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.EventAlertLog;
import com.aimir.notification.FMPTrap;


/**
 * Event ID : 200.17.0 Processing Class
 *
 * @author 
 * @version $Rev: 1 $, $Date: 20016-05-25 15:59:15 +0900 $,
 */
@Service
public class EV_SP_200_17_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_SP_200_17_0_Action.class);
    
    
    /**
     * execute event action
     * Meter Shared Key
     * @param trap - FMP Trap(Meter Shared Key Event)
     * @param event - Event Data
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
        log.debug("EV_SP_200_17_0_Action : EventCode["+trap.getCode()+"] MCU["+trap.getMcuId()+"]");
        
        // Initialize
        String mcuId = trap.getMcuId();
//        String ipAddr = trap.getIpAddr();
//        MCU 	mcu = mcuDao.get(mcuId);
               
        String modemId = event.getEventAttrValue("stringEntry.hex");
        log.debug("MODEM_ID[" + modemId + "]");
        
        String meterId = event.getEventAttrValue("stringEntry.1");
        log.debug("METER_ID[" + meterId + "]");
         
        int reason = Integer.parseInt(event.getEventAttrValue("uintEntry"));
        String reasonType = null;
        switch (reason) {
        case 0x00 :
        	reasonType = "Unknown";
            break;
        case 0x01 : 
        	reasonType = "No MSK";
            break;
        case 0x02 :
        	reasonType = "Invalid MSK";
            break;
        }
        log.debug("reason[" + reasonType + "]");
      
      
        event.setActivatorId(trap.getMcuId()); 
        event.setActivatorType(TargetClass.DCU);
 
        event.append(EventUtil.makeEventAlertAttr("moSPId", "java.lang.String", modemId));
        event.append(EventUtil.makeEventAlertAttr("meterId", "java.lang.String", meterId));
        event.append(EventUtil.makeEventAlertAttr("reason", "java.lang.String", reasonType));
        event.append(EventUtil.makeEventAlertAttr("message", "java.lang.String", "Meter Shared Key Event"));
        
        // Get Shared Key
        try {
        	OacServerApi api  = new OacServerApi();
	        HashMap<String,String> sharedKey = api.getMeterSharedKey(modemId, meterId);
	        if ( sharedKey != null ){
		        CommandGW gw = new CommandGW();
		        gw.cmdSetMeterSharedKey( mcuId, meterId,
		        		sharedKey.get("MasterKey") == null ? "" : sharedKey.get("MasterKey") ,
		        		sharedKey.get("UnicastKey") == null ? "" :  sharedKey.get("UnicastKey"),
		        		sharedKey.get("MulticastKey") == null ? "" : sharedKey.get("MulticastKey") ,
		        		sharedKey.get("AuthenticationKey") == null ? "" : sharedKey.get("AuthenticationKey") 
		        		);
	        }
	        else {
	        	log.error("getMeterSharedKey fail.");
	        }
	    }
        catch (Exception e) {
            log.warn("getMeterSharedKey fail.", e);
        }
        log.debug("Meter Shared Key Event Action Compelte");
    }
}
