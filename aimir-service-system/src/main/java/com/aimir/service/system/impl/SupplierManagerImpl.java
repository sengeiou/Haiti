package com.aimir.service.system.impl;

import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.system.SupplierDao;
import com.aimir.model.system.Country;
import com.aimir.model.system.Language;
import com.aimir.model.system.Supplier;
import com.aimir.model.system.TimeZone;
import com.aimir.service.system.SupplierManager;

@WebService(endpointInterface = "com.aimir.service.system.SupplierManager")
@Service(value="supplierManager")
@Transactional
public class SupplierManagerImpl implements SupplierManager {

    @Autowired
    SupplierDao dao;

    public List<Supplier> getSuppliers() {
        return dao.getAll();
    }
    
    public Supplier getSupplier(Integer id) {
        Supplier supplier = dao.get(id);
        return supplier;
    }
    
    public Supplier getSupplierByName(String name) {
        Supplier supplier = dao.getSupplierByName(name);
        return supplier;
    }

    public void add(Supplier supplier) {
        dao.add(supplier);
    }

    public void update(Supplier supplier) {
        dao.update(supplier);
    }
    
    public void delete(Integer supplierId) {
        dao.deleteById(supplierId);
    }
    
    public Integer getCount() {
    	int count = dao.count();
    	return count;
    }

    public Integer getCountryID(Integer supplierID) {
        Supplier supplier = dao.get(supplierID);
        Country country = supplier.getCountry();
        return country.getId();
    }

    public Integer getLanguageID(Integer supplierID) {
        Supplier supplier = dao.get(supplierID);
        Language lang = supplier.getLang();
        return lang.getId();
    }

    public Integer getTimeZoneID(Integer supplierID) {
        Supplier supplier = dao.get(supplierID);
        TimeZone timeZone = supplier.getTimezone();
        return timeZone.getId();
    }
}
