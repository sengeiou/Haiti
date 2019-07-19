package com.aimir.dao.mvm.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.LpWMDao;
import com.aimir.model.mvm.LpWM;
import com.aimir.util.Condition;

@Repository(value="lpwmDao")
public class LpWMDaoImpl extends AbstractJpaDao<LpWM, Integer> implements LpWMDao{

    public LpWMDaoImpl() {
		super(LpWM.class);
	}

    
    public List<LpWM> getLpWMsByListCondition(Set<Condition> set) {         
        
        return findByConditions(set);
    }
    
    public List<Object> getLpWMsCountByListCondition(Set<Condition> set) {         
        
        return findTotalCountByConditions(set);
    }


    @Override
    public List<Object> getLpWMsMaxMinSumAvg(Set<Condition> conditions,
            String div) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public List<Object> getLpWMsByNoSended() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public List<Object> getLpWMsByNoSended(String mdevType) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void updateSendedResult(LpWM lpwm) {
        // TODO Auto-generated method stub
        
    }


    @Override
    public List<Object> getConsumptionWmCo2LpValuesParentId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public int getLpInterval(String mdevId) {
        // TODO Auto-generated method stub
        return 0;
    }


    @Override
    public Class<LpWM> getPersistentClass() {
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
