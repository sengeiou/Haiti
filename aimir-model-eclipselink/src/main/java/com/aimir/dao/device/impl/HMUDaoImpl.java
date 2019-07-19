package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.HMUDao;
import com.aimir.model.device.HMU;
import com.aimir.util.Condition;

@Repository(value = "hmuDao")
public class HMUDaoImpl extends AbstractJpaDao<HMU, Integer> implements HMUDao {

    Log log = LogFactory.getLog(HMUDaoImpl.class);
    
    public HMUDaoImpl() {
		super(HMU.class);
	}

    @Override
    public Class<HMU> getPersistentClass() {
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