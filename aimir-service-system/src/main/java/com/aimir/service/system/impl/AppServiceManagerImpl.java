package com.aimir.service.system.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.jws.WebService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.MeteringDataType;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.meter.saver.ManualMDSaver;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.system.Location;
import com.aimir.service.system.AppServiceManager;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;

@WebService(endpointInterface = "com.aimir.service.system.AppServiceManager")
@Service(value = "appServiceManager")
@Transactional
public class AppServiceManagerImpl implements AppServiceManager {

	private static Logger log = Logger.getLogger(AppServiceManagerImpl.class);
	
    @Autowired
    LocationDao locationDao;
    
    @Autowired
    ModemDao modemDao;
    
    @Autowired
    MeterDao meterDao;
    
    @Autowired
    SupplierDao supplierDao;
    
    // @Autowired 
    ManualMDSaver mMDSaver;


	public List<Location> getLocationList() {
    	return locationDao.getAll();
	}


	public List<Modem> getModemList(Integer supplierId,  Integer locationId) {
		
	    Set<Condition> cond = new HashSet<Condition>();
        cond.add(new Condition("supplier.id", new Object[]{supplierId}, null, Restriction.EQ));
        //cond.add(new Condition("modemType", new Object[]{ModemType.MMIU}, null, Restriction.EQ));

		List<Integer> locations = locationDao.getChildNodesInLocation(locationId, supplierId);
		locations.add(locationId);

		Integer[] locationArrays = new Integer[locations.size()];
		for (int i = 0; i < locations.size(); i++) {
			log.debug("locationid="+locations.get(i));
			locationArrays[i] = locations.get(i);
		}
		
        cond.add(new Condition("location.id", locationArrays, null, Restriction.IN));   
        
        List<Modem> lists = null;
        lists = modemDao.findByConditions(cond);
        if(lists != null &&  lists.size() > 0){
        	log.debug("Return List size="+lists.size());
        }
        
        return lists;
	}


	public List<Meter> getMeterList(Integer supplierId, Integer locationId) {
		
	    Set<Condition> cond = new HashSet<Condition>();
        cond.add(new Condition("supplier.id", new Object[]{supplierId}, null, Restriction.EQ));
        cond.add(new Condition("isManualMeter", new Object[]{Integer.valueOf(1)}, null, Restriction.EQ));
        
		List<Integer> locations = locationDao.getChildNodesInLocation(locationId, supplierId);
		locations.add(locationId);

		Integer[] locationArrays = new Integer[locations.size()];
		for (int i = 0; i < locations.size(); i++) {
			log.debug("locationid="+locations.get(i));
			locationArrays[i] = locations.get(i);
		}
		
        cond.add(new Condition("location.id", locationArrays, null, Restriction.IN));        
 
        List<Meter> lists = null;
        lists = meterDao.findByConditions(cond);
        if(lists != null &&  lists.size() > 0){
        	log.debug("Return List size="+lists.size());
        }
        
        return lists;
	}


	public List<Meter> getMeterListByModem(String modemSerial) {
		
	    Set<Condition> cond = new HashSet<Condition>();
        cond.add(new Condition("modem", new Object[] {"mo"}, null, Restriction.ALIAS));
        cond.add(new Condition("mo.deviceSerial", new Object[]{modemSerial}, null, Restriction.EQ));      
        cond.add(new Condition("isManualMeter", new Object[]{Integer.valueOf(1)}, null, Restriction.EQ));
        return meterDao.findByConditions(cond);
	}

	@Override
	public Boolean[] saveMeterData(String meterSerial, String meteringDate, MeteringDataType mDataType, Double[] meteringValues) throws Exception {
		
		Boolean[] results = new Boolean[meteringValues.length];
		if(meteringValues != null && meteringValues.length > 0){
			for(int i = 0; i < meteringValues.length; i++){
				results[i] = Boolean.FALSE;
			}
		}
		Meter meter = meterDao.get(meterSerial);
		if(meter == null) {
			throw new Exception("meterInvalid ## meter is invalid [" + meterSerial + "]");
		}
		if(meter.getIsManualMeter() == null || meter.getIsManualMeter() != 1) {
			throw new Exception("meterNotManualMeter ## meter is not manual meter [" + meterSerial + "]");
		}
		int i = 0;
		for(Double meteringValue: meteringValues){
			boolean result = mMDSaver.saveBilling(meterSerial, meteringDate, meteringValue, mDataType);
			results[i++] = result;
			log.debug("Write ManualMetering Result >> " + result);
		}

		return results;
	}
}
