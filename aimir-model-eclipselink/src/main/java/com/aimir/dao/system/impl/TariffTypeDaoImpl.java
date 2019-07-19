package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.TariffTypeDao;
import com.aimir.model.system.Code;
import com.aimir.model.system.Supplier;
import com.aimir.model.system.TariffType;
import com.aimir.util.Condition;

@Repository(value = "tarifftypeDao")
	public class TariffTypeDaoImpl extends AbstractJpaDao<TariffType, Integer> implements TariffTypeDao {
			
    Log logger = LogFactory.getLog(TariffTypeDaoImpl.class);
    
    public TariffTypeDaoImpl() {
        super(TariffType.class);
    }

    @Override
    public List<TariffType> getTariffTypeBySupplier(String serviceType,
            Integer supplierId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<TariffType> getTariffTypeList(Integer supplierId,
            Integer serviceType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<TariffType> getTariffTypeByName(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int updateData(TariffType tariffType) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Integer getLastCode() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TariffType addTariffType(String tariffTypeName, Code serviceType,
            Supplier supplier) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<TariffType> getPersistentClass() {
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
