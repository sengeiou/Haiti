package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.SupplyCapacityLogDao;
import com.aimir.model.system.SupplyCapacityLog;
import com.aimir.util.Condition;

@Repository(value="supplycapacitylogDao")
public class SupplyCapacityLogDaoImpl extends AbstractJpaDao<SupplyCapacityLog, Long> implements SupplyCapacityLogDao{
	
    public SupplyCapacityLogDaoImpl() {
		super(SupplyCapacityLog.class);
	}

    @Override
    public void supplyCapacityLogDelete(int supplyTypeId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<SupplyCapacityLog> getSupplyCapacityLogs(int page, int count) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<SupplyCapacityLog> getPersistentClass() {
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
