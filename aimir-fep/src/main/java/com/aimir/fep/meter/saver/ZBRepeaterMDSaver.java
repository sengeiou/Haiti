package com.aimir.fep.meter.saver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.BatteryStatus;
import com.aimir.constants.CommonConstants.ModemPowerType;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.parser.ZBRepeater;

@Service
public class ZBRepeaterMDSaver extends AbstractMDSaver {

	@Autowired
	protected ModemDao modemDao;
		
	@Override
	protected boolean save(IMeasurementData md) throws Exception {

        ZBRepeater parser = (ZBRepeater)md.getMeterDataParser();
        log.info("REPEATER[" + parser.getMeter().getModem().getDeviceSerial() + "]");
        
        com.aimir.model.device.ZBRepeater modem = 
                (com.aimir.model.device.ZBRepeater) modemDao.get(parser.getMeter().getModem().getDeviceSerial());
        
        
        if(modem != null){
        	String preTime = modem.getLastLinkTime();
        	
            if(preTime == null || preTime.compareTo(parser.getCurrentTime()) <= 0) {
            	
            	
            	modem.setLastLinkTime(parser.getCurrentTime());
            	
            	modem.setFwVer(parser.getFwVersion());
            	modem.setFwRevision(parser.getFwBuild());
            	modem.setHwVer(parser.getHwVersion());
            	modem.setSwVer(parser.getSwVersion());

            	
            	ModemPowerType powerType = CommonConstants.getModemPowerType(parser.getPowerType());
            	
            	if(powerType != null){
                	modem.setPowerType(powerType.name());
            	}
            	modem.setOperatingDay(parser.getOperatingDay());
            	modem.setActiveTime(parser.getActiveMinute());
            	modem.setBatteryVolt(parser.getBatteryVolt());
            	modem.setTestFlag(parser.getTestFlag() == 0 ? false :true);
            	modem.setCommState(1);

            	modem.setSolarBDCV(parser.getSolarBDCV());
            	modem.setSolarADV(parser.getSolarADV());
            	modem.setSolarChgBV(parser.getSolarChgBV());

/*
                _repeater.addProperty(new MOPROPERTY("resetReason", parser.getResetReason()+""));
                _repeater.addProperty(new MOPROPERTY("permitMode", parser.getPermitMode()+""));
                _repeater.addProperty(new MOPROPERTY("permitState", parser.getPermitState()+""));
                _repeater.addProperty(new MOPROPERTY("alarmFlag", parser.getAlarmFlag()+""));
                _repeater.addProperty(new MOPROPERTY("alarmMask", parser.getAlarmMask()+""));

                _repeater.addProperty(new MOPROPERTY("consumptionCurrent", parser.getBatteryCurrent()+""));
                _repeater.addProperty(new MOPROPERTY("voltOffset", parser.getVoltOffset()+""));
*/


                //"Battery Status( 1: Normal, 0,2: Abnormal, 3: Replacement, 4:Unknown(default))")
                
                if (parser.getPowerType() == ModemPowerType.Battery.getCode()) {
                    if(parser.getBatteryVolt() >= 2.98) {
    	            	modem.setBatteryStatus(BatteryStatus.Normal.name());
    				}
    				else {
    	            	modem.setBatteryStatus(BatteryStatus.Abnormal.name());
    				}
                }
                else if (parser.getPowerType() == ModemPowerType.Solar.getCode()) {
                    if (parser.getSolarADV() >= 2.98) {
    	            	modem.setBatteryStatus(BatteryStatus.Normal.name());
    				}
    				else {
    	            	modem.setBatteryStatus(BatteryStatus.Abnormal.name());
    				}
                }

                
                saveBatteryLog(modem, parser.getBatteryLogs());
            }
        }

	
		return true;
	}

}
