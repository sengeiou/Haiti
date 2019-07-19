package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.MCUCodiDao;
import com.aimir.model.device.MCUCodi;
import com.aimir.util.Condition;

@Repository(value = "mcucodiDao")
public class MCUCodiDaoImpl extends AbstractJpaDao<MCUCodi, Long> implements MCUCodiDao {
    
	public MCUCodiDaoImpl() {
		super(MCUCodi.class);
	}

    @Override
    public Class<MCUCodi> getPersistentClass() {
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
