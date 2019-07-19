package com.aimir.fep.meter.saver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants.ModemPowerType;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.parser.PLCRepeater;

@Service
public class PLCRepeaterMDSaver extends AbstractMDSaver {

	@Autowired
	protected ModemDao modemDao;
		
	@Override
	protected boolean save(IMeasurementData md) throws Exception {

        PLCRepeater parser = (PLCRepeater)md.getMeterDataParser();
        log.info("REPEATER[" + parser.getMeter().getModem().getDeviceSerial() + "]");
        
        com.aimir.model.device.ZBRepeater modem = 
                (com.aimir.model.device.ZBRepeater) modemDao.get(parser.getMeter().getModem().getDeviceSerial());
        
		String modemTime = parser.getMeteringTime();
		Double val = parser.getLQISNRValue();
		if(val != null && modemTime != null && !"".equals(modemTime) && modemTime.length() == 14){
			saveSNRLog(parser.getDeviceId(), modemTime.substring(0,8), modemTime.substring(8,14), parser.getMeter().getModem(), val);
		}
        if(modem != null){
        	String preTime = modem.getLastLinkTime();
        	
            if(preTime == null || preTime.compareTo(parser.getMeteringTime()) <= 0) {           	
            	
            	modem.setLastLinkTime(parser.getMeteringTime());   	
            	ModemPowerType powerType = ModemPowerType.Line;
            	
            	if(powerType != null){
                	modem.setPowerType(powerType.name());
            	}
            	modem.setCommState(1);
            }
        }
	
		return true;
	}

}
