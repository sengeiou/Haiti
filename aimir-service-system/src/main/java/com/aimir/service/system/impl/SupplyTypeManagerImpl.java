package com.aimir.service.system.impl;

import java.util.List;

import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.system.SupplyTypeDao;
import com.aimir.model.system.SupplyType;
import com.aimir.service.system.SupplyTypeManager;

@WebService(endpointInterface = "com.aimir.service.system.SupplyTypeManager")
@Service(value="supplyTypeManager")
@Transactional
public class SupplyTypeManagerImpl implements SupplyTypeManager {

    @Autowired
    SupplyTypeDao dao;
    
    public SupplyType getSupplyType(Integer supplyTypeId) {
        SupplyType type = dao.get(supplyTypeId);
        return type;
    }
    
    public void add(SupplyType supplyType) {
        SupplyType type = dao.add(supplyType);
    }

    public void delete(Integer supplyTypeId) {
        dao.deleteById(supplyTypeId);
    }

    public void update(SupplyType SupplyType) {
        dao.update(SupplyType);
    }

	public List<SupplyType> getSupplyTypeBySupplierId(Integer supplierId) {
		return dao.getSupplyTypeBySupplierId(supplierId);
	}

	public boolean checkSupplyType(Integer supplierId, Integer typeId) {
		return dao.checkSupplyType(supplierId, typeId);
	}

	public List<SupplyType> getSupplyTypeList(int supplier) {
		return dao.getSupplyTypeList(supplier);
	}

	//BEMS 에서 사용
	public List<SupplyType> getSupplyTypeList() {
		return dao.getAll();
	}
}
