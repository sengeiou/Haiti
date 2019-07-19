package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.MCUVarDao;
import com.aimir.model.device.MCUVar;
import com.aimir.util.Condition;

@Repository(value = "mcuvarDao")
public class MCUVarDaoImpl extends AbstractJpaDao<MCUVar, Long> implements MCUVarDao {

	public MCUVarDaoImpl() {
		super(MCUVar.class);
	}

    @Override
    public Class<MCUVar> getPersistentClass() {
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
