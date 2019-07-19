package com.aimir.fep.trap.actions.LK;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.dao.device.ChangeLogDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.SNRLogDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.model.device.SNRLog;
import com.aimir.model.system.Location;
import com.aimir.notification.FMPTrap;

/**
 * evtNodeLqi
 */
@Component
public class EV_LK_210_3_0_Action implements EV_Action{
    private static Log log = LogFactory.getLog(EV_LK_210_3_0_Action.class);
    
    @Autowired
    MCUDao dcuDao;
    
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
    
    @Autowired
    SNRLogDao snrLogDao;
    
    /**
     * execute event action
     *
     * @param trap - FMP Trap(DCU Event)
     * @param event - Event Alert Log Data
     * 
     * 1.5	targetShortId	WORD	2		Target Short ID
	 * 1.4	lqi				BYTE	1		Node LQI
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception {
        log.debug("EventName[evtNodeLqi] "+" EventCode[" + trap.getCode()+"] Modem["+trap.getSourceId()+"]");
        String mcuId = trap.getMcuId();
        String ipAddr = event.getEventAttrValue("ntwPppIp") == null ? "" : event.getEventAttrValue("ntwPppIp");
        String modemSerial = trap.getSourceId();  
        String timeStamp = trap.getTimeStamp();
        String srcType = trap.getSourceType();
        // FIXME LQI 파싱과정이 다를경우 수정
		log.debug("mcuId = " + mcuId + ", ipAddr = " + ipAddr + ", modemSeriall = " + modemSerial + ", timeStamp = " + timeStamp + ", srcType = " + srcType);
        int val =  Byte.parseByte(event.getEventAttrValue("byteEntry")) & 0xFF;
        Double lqi = getG3_PLC_SNR(val);

        try{
            SNRLog snrLog = new SNRLog();            
            snrLog.setDcuid(mcuId);
            snrLog.setDeviceId(modemSerial);
            snrLog.setDeviceType(ModemType.ZRU.name());
            snrLog.setYyyymmdd(timeStamp.substring(0,8));
            snrLog.setHhmmss(timeStamp.substring(8,14));
            snrLog.setSnr(lqi);
            snrLogDao.add(snrLog);
			log.debug("SNRLog[Hex=" + Hex.decode(new byte[] { Byte.parseByte(event.getEventAttrValue("byteEntry")) }) + "] = " + snrLog.toString());
        }catch(Exception e){
			log.warn(e, e);
        }
        
        MCU existMcu = dcuDao.get(mcuId);
        if(existMcu != null) {
        	Location location = existMcu.getLocation();
        	event.setLocation(location);
        }
		log.debug("PLC Modem LQI Event Action Compelte");
    }
    
    public Double getG3_PLC_SNR(int val){
    	int min = 0;
    	int min_value = -10;
    	double steps = 0.25;
    	double cal = 0d;
    	
    	cal = (val - min)*steps + min_value;
    	return cal;    	
    }

}
