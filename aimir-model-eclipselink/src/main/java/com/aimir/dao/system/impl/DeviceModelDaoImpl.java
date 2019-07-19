package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.DeviceVendor;
import com.aimir.util.Condition;

@Repository(value="devicemodelDao")
public class DeviceModelDaoImpl extends AbstractJpaDao<DeviceModel, Integer> implements DeviceModelDao {

	public DeviceModelDaoImpl() {
		super(DeviceModel.class);
	}

    @Override
    public List<DeviceModel> getDeviceModels(Integer vendorId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<DeviceModel> getDeviceModels(int vendorId, int deviceTypeId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<DeviceModel> getDeviceModels(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<DeviceModel> getDeviceModelByName(Integer supplierId,
            String name) {
        String sql = "select d from DeviceModel d inner join d.deviceVendor v where d.name = :name and v.supplier.id = :supplierId";
        Query query = em.createQuery(sql, DeviceVendor.class);
        query.setParameter("supplierId", supplierId);
        query.setParameter("name", name);
        return query.getResultList();
    }

    @Override
    public DeviceModel getDeviceModelByCode(Integer supplierId, Integer code,
            Integer deviceTypeCodeId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<DeviceModel> getDeviceModelByTypeId(Integer supplierId,
            Integer typeId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<DeviceModel> getDeviceModelBySupplierId(Integer supplier_id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<DeviceModel> getDeviceModelByTypeIdUnknown(Integer supplier_id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, String>> getMCUDeviceModel(String inCondition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<DeviceModel> getPersistentClass() {
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
    public List<DeviceModel> getDeviceModels(String vendorName) {
        // TODO Auto-generated method stub
        return null;
    }
}
