package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.SupplyTypeLocationDao;
import com.aimir.model.system.SupplyTypeLocation;
import com.aimir.util.Condition;

@Repository(value="supplytypelocationDao")
public class SupplyTypeLocationDaoImpl extends AbstractJpaDao<SupplyTypeLocation, Integer> implements SupplyTypeLocationDao{

	public SupplyTypeLocationDaoImpl() {
		super(SupplyTypeLocation.class);
	}

    @Override
    public boolean checkSupplyType(Integer typeId) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Class<SupplyTypeLocation> getPersistentClass() {
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
