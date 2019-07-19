package com.aimir.dao.mvm.impl;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.LpTMDao;
import com.aimir.model.mvm.LpTM;
import com.aimir.util.Condition;


@Repository(value="lptmDao")
public class LpTMDaoImpl extends AbstractJpaDao<LpTM, Integer> implements LpTMDao{

	public LpTMDaoImpl() {
	    super(LpTM.class);
	}

    @Override
    public Class<LpTM> getPersistentClass() {
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
