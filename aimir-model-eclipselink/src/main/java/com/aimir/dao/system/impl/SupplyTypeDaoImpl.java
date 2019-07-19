package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.SupplyTypeDao;
import com.aimir.model.system.SupplyType;
import com.aimir.util.Condition;

@Repository(value="supplytypeDao")
public class SupplyTypeDaoImpl extends AbstractJpaDao<SupplyType, Integer> implements SupplyTypeDao {

	public SupplyTypeDaoImpl() {
		super(SupplyType.class);
	}

    @Override
    public List<SupplyType> getSupplyTypeBySupplierId(Integer supplierId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean checkSupplyType(Integer supplierId, Integer typeId) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<SupplyType> getSupplyTypeList(Integer supplierId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<SupplyType> getSupplyTypeBySupplierIdTypeId(Integer supplierId,
            Integer typeId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<SupplyType> getPersistentClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getSumFieldByCondition(Set<Condition> conditions,
            String field, String... groupBy) {
        // TODO Auto-generated method stub
        return null;
    }
}
