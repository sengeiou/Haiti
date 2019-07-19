package com.aimir.dao.mvm.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.LpHMDao;
import com.aimir.model.mvm.LpHM;
import com.aimir.util.Condition;

@Repository(value="lphmDao")
public class LpHMDaoImpl extends AbstractJpaDao<LpHM, Integer> implements LpHMDao{

    public LpHMDaoImpl() {
		super(LpHM.class);
	}

    
    public List<LpHM> getLpHMsByListCondition(Set<Condition> set) {         
        
        return findByConditions(set);
    }
    
    public List<Object> getLpHMsCountByListCondition(Set<Condition> set) {         
        
        return findTotalCountByConditions(set);
    }


    @Override
    public List<Object> getLpHMsMaxMinSumAvg(Set<Condition> conditions,
            String div) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public List<Object> getLpHMsByNoSended() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public List<Object> getLpHMsByNoSended(String mdevType) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void updateSendedResult(LpHM lphm) {
        // TODO Auto-generated method stub
        
    }


    @Override
    public Class<LpHM> getPersistentClass() {
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
