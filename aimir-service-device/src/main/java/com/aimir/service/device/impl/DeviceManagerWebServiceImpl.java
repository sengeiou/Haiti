package com.aimir.service.device.impl;

import java.util.List;

import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.model.device.EnergyMeter;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.service.device.DeviceManagerWebService;

@WebService(endpointInterface="com.aimir.service.device.DeviceManagerWebService")
@Service(value="deviceManagerWebService")
@Transactional(readOnly=false)
public class DeviceManagerWebServiceImpl implements DeviceManagerWebService {
	
	@Autowired
	MeterDao meterDao; 
	
	@Autowired
    MCUDao mcuDao;
	
	@Autowired
	ModemDao modemDao;
	
	public Meter getMeter(Integer meterId){
		Meter rtnMeter = meterDao.get(meterId);
		
		if(rtnMeter.getModem() == null)
			rtnMeter.setModem(null);
		
		return rtnMeter;
	}
	
	public Meter getMeter(String mdsId) {
	    return meterDao.findByCondition("mdsId", mdsId);
	}
	
	public MCU getMCU(Integer mcuId) {
        
        MCU mcu = mcuDao.get(mcuId);
        
        return mcu;
    }
    
    public MCU getMCU(String name) {
        MCU mcu = mcuDao.get(name);
        return mcu;
    }
    
    public Modem getModem(Integer modemId) {
        Modem resultModem = new Modem();

        resultModem = modemDao.get(modemId);

        return resultModem;
    }

    public Modem getModem(String deviceSerial) {
        return modemDao.findByCondition("deviceSerial", deviceSerial);
    }
    
    public EnergyMeter[] getAllMeter() {
        List<Meter> list = meterDao.getAll();
        return list.toArray(new EnergyMeter[0]);
    }
    
    public MCU[] getAllMcu() {
        List<MCU> list = mcuDao.getAll();
        return list.toArray(new MCU[0]);
    }
    
    public Modem[] getAllModem() {
        List<Modem> list = modemDao.getAll();
        return list.toArray(new Modem[0]);
    }
}
