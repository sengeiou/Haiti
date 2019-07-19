package com.aimir.fep.trap.actions.SP;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.protocol.security.OacServerApi;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.Util;
import com.aimir.model.device.EventAlertAttr;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Modem;
import com.aimir.model.device.SubGiga;
import com.aimir.notification.FMPTrap;
import com.google.gson.JsonObject;

/**
 * Event ID : 200.16.0
 *
 * @author Elevas Park
 * @version $Rev: 1 $, $Date: 2017-09-27 15:59:15 +0900 $,
 */
@Service
public class EV_SP_200_16_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_SP_200_16_0_Action.class);
    @Autowired
    MCUDao mcuDao;
    
    @Autowired
    ModemDao modemDao;

   
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

        String modemId = event.getEventAttrValue("moSPId");
        log.debug("MODEM_ID[" + modemId + "]");

        MCU mcu = mcuDao.get(trap.getMcuId());
        
        event.setActivatorIp(mcu.getIpv6Addr() != null ? mcu.getIpv6Addr():mcu.getIpAddr());
        event.setActivatorId(mcu.getSysID());
        event.setActivatorType(TargetClass.DCU);
        event.setSupplier(mcu.getSupplier());
        event.setLocation(mcu.getLocation());
        
        String message = "Modem Leave from DCU. "; 
        Modem modem = modemDao.get(modemId);
        
        if (modem != null) {
            message += "Modem[" + modemId + "]";
        }
        else {
            message += "FEP";
        }
        
        event.append(EventUtil.makeEventAlertAttr("moSPId", "java.lang.String", modemId));
        event.append(EventUtil.makeEventAlertAttr("message", "java.lang.String", message));
        
        log.debug("moSPId=[" + modemId +"], message=["+message+"]");          
        log.debug("Event Action Compelte");
    }
}

