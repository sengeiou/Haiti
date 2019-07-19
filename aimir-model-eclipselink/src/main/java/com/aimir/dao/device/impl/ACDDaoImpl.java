package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.ACDDao;
import com.aimir.model.device.ACD;
import com.aimir.util.Condition;

@Repository(value = "acdDao")
public class ACDDaoImpl extends AbstractJpaDao<ACD, Integer> implements ACDDao {

    Log log = LogFactory.getLog(ACDDaoImpl.class);
    
	public ACDDaoImpl() {
		super(ACD.class);
	}

    @Override
    public Class<ACD> getPersistentClass() {
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