package com.aimir.dao.mvm.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.LpGMDao;
import com.aimir.model.mvm.LpGM;
import com.aimir.util.Condition;

@Repository(value="lpgmDao")
public class LpGMDaoImpl extends AbstractJpaDao<LpGM, Integer> implements LpGMDao{

    public LpGMDaoImpl() {
		super(LpGM.class);
	}
    
    public List<LpGM> getLpGMsByListCondition(Set<Condition> set) {         
        
        return findByConditions(set);
    }
    
    public List<Object> getLpGMsCountByListCondition(Set<Condition> set) {         
        
        return findTotalCountByConditions(set);
    }

    @Override
    public List<Object> getLpGMsMaxMinSumAvg(Set<Condition> conditions,
            String div) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getLpGMsByNoSended() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getLpGMsByNoSended(String mdevType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateSendedResult(LpGM lpgm) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<Object> getConsumptionGmCo2LpValuesParentId(
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
    public Class<LpGM> getPersistentClass() {
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
