package com.aimir.dao.device.impl;


import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.HeadendDao;
import com.aimir.model.device.Headend;
import com.aimir.util.Condition;

@Repository(value="headendDao")
public class HeadendDaoImpl extends AbstractJpaDao<Headend, Integer> implements HeadendDao {
	
	public HeadendDaoImpl() {
		super(Headend.class);
	}

    @Override
    public List<Headend> getLastData() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<Headend> getPersistentClass() {
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
