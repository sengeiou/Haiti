package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.DeviceVendorDao;
import com.aimir.model.system.DeviceVendor;
import com.aimir.util.Condition;

@Repository(value = "devicevendorDao")
public class DeviceVendorDaoImpl extends AbstractJpaDao<DeviceVendor, Integer> implements DeviceVendorDao{

	public DeviceVendorDaoImpl() {
		super(DeviceVendor.class);
	}

    @Override
    public List<Object[]> getDeviceVendorsForTree(Integer supplierId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<DeviceVendor> getDeviceVendorByName(Integer supplierId,
            String name) {
        String sql = "select d from DeviceVendor d where d.supplier.id = :supplierId and d.name = :vendorName";
        Query query = em.createQuery(sql, DeviceVendor.class);
        query.setParameter("supplierId", supplierId);
        query.setParameter("vendorName", name);
        return query.getResultList();
    }

    @Override
    public List<DeviceVendor> getDeviceVendorByCode(Integer supplierId,
            Integer code) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<DeviceVendor> getPersistentClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getSumFieldByCondition(Set<Condition> conditions,
            String field, String... groupBy) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<DeviceVendor> getDeviceVendorsOrderByName() {
        // TODO Auto-generated method stub
        return null;
    }
}
