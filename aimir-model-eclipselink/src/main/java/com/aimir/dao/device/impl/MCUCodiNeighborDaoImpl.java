package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.MCUCodiNeighborDao;
import com.aimir.model.device.MCUCodiNeighbor;
import com.aimir.util.Condition;

@Repository(value = "mcucodineighborDao")
public class MCUCodiNeighborDaoImpl extends AbstractJpaDao<MCUCodiNeighbor, Long> implements MCUCodiNeighborDao {

	public MCUCodiNeighborDaoImpl() {
		super(MCUCodiNeighbor.class);
	}

    @Override
    public Class<MCUCodiNeighbor> getPersistentClass() {
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
