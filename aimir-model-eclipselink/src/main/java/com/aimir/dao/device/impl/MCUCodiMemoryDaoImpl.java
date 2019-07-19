package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.MCUCodiMemoryDao;
import com.aimir.model.device.MCUCodiMemory;
import com.aimir.util.Condition;

@Repository(value = "mcucodimemoryDao")
public class MCUCodiMemoryDaoImpl extends AbstractJpaDao<MCUCodiMemory, Long> implements MCUCodiMemoryDao {

	public MCUCodiMemoryDaoImpl() {
		super(MCUCodiMemory.class);
	}

    @Override
    public Class<MCUCodiMemory> getPersistentClass() {
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
