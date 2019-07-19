package com.aimir.service.system.impl;

import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.system.SupplyTypeLocationDao;
import com.aimir.model.system.SupplyTypeLocation;
import com.aimir.service.system.SupplyTypeLocationManager;

@WebService(endpointInterface = "com.aimir.service.system.SupplyTypeLocationManager")
@Service(value="supplyTypeLocationManager")
@Transactional
public class SupplyTypeLocationManagerImpl implements SupplyTypeLocationManager {

    @Autowired
    SupplyTypeLocationDao dao;
    
    public void add(SupplyTypeLocation supplyTypeLocation) {
        dao.add(supplyTypeLocation);
    }

    public void update(SupplyTypeLocation supplyTypeLocation) {
        dao.update(supplyTypeLocation);
    }

    public void delete(Integer supplyTypeLocationId) {
        dao.deleteById(supplyTypeLocationId);
    }

    public SupplyTypeLocation get(Integer supplyTypeLocationId) {
        return dao.get(supplyTypeLocationId);
    }
    
    public boolean checkSupplyType(Integer typeId) {
		return dao.checkSupplyType(typeId);
	}
}
