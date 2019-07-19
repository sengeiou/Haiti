package com.aimir.fep.trap.actions.LK;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aimir.dao.device.ChangeLogDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.model.device.EventAlertAttr;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.model.system.Location;
import com.aimir.notification.FMPTrap;

/**
 * evtPathDiscovery
 */
@Component
public class EV_LK_210_2_0_Action implements EV_Action{
    private static Log log = LogFactory.getLog(EV_LK_210_2_0_Action.class);
    
    @Autowired
    MCUDao dcuDao;
    
    @Autowired
    MeterDao meterDao;
    
    @Autowired
    CodeDao codeDao;
    
    @Autowired
    CommandGW commandGW;
    
    @Autowired
    ChangeLogDao clDao;
    
    @Autowired
    DeviceModelDao deviceModelDao;
    
    @Autowired
    SupplierDao supplierDao;
    
    @Autowired
    LocationDao locationDao;
    
    /**
     * execute event action
     *
     * @param trap - FMP Trap(DCU Event)
     * @param event - Event Alert Log Data
     * 
     * 1.5	targetShortId		WORD	2				Target Short ID
	 * 1.5	resultStatus		WORD	2				Discovery result (0x0000 이 아니면 Fail)
	 * 1.4	noOfForwardPath		BYTE	1				Forward path count
	 * 1.14	pathId				EUI64	8				Forward path EUI64 ID
	 * 1.5	pathShortId			WORD	2				Forward path Short ID
	 * 1.4	pathLinkCost		BYTE	1				Forward path Link cost
	 * 1.4	noOfReversePath		BYTE	1				Forward path count
	 * 1.14	pathId				EUI64	8				Reverse path EUI64 ID
	 * 1.5	pathShortId			WORD	2				Reverse path Short ID
	 * 1.4	pathLinkCost		BYTE	1				Reverse path Link cost
	 * 
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception{
        log.debug("EventName[evtPathDiscovery] "+" EventCode[" + trap.getCode()+"] Modem["+trap.getSourceId()+"]");
        
        // Initialize
        String mcuId = trap.getMcuId();
        String ipAddr = event.getEventAttrValue("ntwPppIp") == null ? "" : event.getEventAttrValue("ntwPppIp");
        String modemSerial = trap.getSourceId();
        String timeStamp = trap.getTimeStamp();
		String srcType = trap.getSourceType();
		
        log.debug("mcuId = " + mcuId + ", ipAddr = " + ipAddr + ", modemSeriall = " + modemSerial + ", timeStamp = " + timeStamp + ", srcType = " + srcType);

        // Get attribute from event.
        EventAlertAttr[] eventAttr = event.getEventAlertAttrs().toArray(new EventAlertAttr[0]);
        
        int i = 0;
        String targetShortID = eventAttr[i++].getValue();
        String result = "Success";
        int resultStatus = Integer.parseInt(eventAttr[i++].getValue());
        if(resultStatus != 0){
        	result = "Fail";
        }
        
        log.info("Target Short ID=["+targetShortID+"] Result Status=["+resultStatus+"]");
       
        int noOfForwardPath = Integer.parseInt(eventAttr[i++].getValue());
        
        for(int p = 0; p < noOfForwardPath; p++){
        	String pathId = eventAttr[i++].getValue();
        	String pathShortId = eventAttr[i++].getValue();
        	int pathLinkCost = Integer.parseInt(eventAttr[i++].getValue());        	
        	log.info("Forward Path, PathId=["+pathId+"] Path Short Id=["+pathShortId+"] Path Link Cost=["+pathLinkCost+"]");
        }

        int noOfReversePath = Integer.parseInt(eventAttr[i++].getValue());
        
        for(int r = 0; r < noOfReversePath; r++){
        	String pathId = eventAttr[i++].getValue();
        	String pathShortId = eventAttr[i++].getValue();
        	int pathLinkCost = Integer.parseInt(eventAttr[i++].getValue());
        	log.info("Reverse Path, PathId=["+pathId+"] Path Short Id=["+pathShortId+"] Path Link Cost=["+pathLinkCost+"]");
        }
        
        MCU existMcu = dcuDao.get(mcuId);
        if(existMcu != null) {
        	Location location = existMcu.getLocation();
        	event.setLocation(location);
        }
		log.debug("PLC Modem Path Discovery Action Compelte");
    }

}
