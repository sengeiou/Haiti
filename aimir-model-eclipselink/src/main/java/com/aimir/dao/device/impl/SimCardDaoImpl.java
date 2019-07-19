package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.SimCardDao;
import com.aimir.model.device.SimCard;
import com.aimir.util.Condition;

@Repository(value = "simCardDao")
public class SimCardDaoImpl extends AbstractJpaDao<SimCard, Long> implements SimCardDao {

	public SimCardDaoImpl() {
		super(SimCard.class);
	}

    @Override
    public Class<SimCard> getPersistentClass() {
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
