package com.aimir.dao.mvm.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.LpSPMDao;
import com.aimir.model.mvm.LpSPM;
import com.aimir.util.Condition;

/**
 * 태양열에너지 LP 검침 Dao
 * @author Yi Hanghee(javarouka@gmail.com, javarouka@nuritelecom.co.kr)
 *
 */
@Repository(value = "lpSPMDao")
public class LpSPMDaoImpl extends AbstractJpaDao<LpSPM, Integer> 
	implements LpSPMDao {

    public LpSPMDaoImpl() {
		super(LpSPM.class);
	}
	
	@Override
	public long totalByConditions(Set<Condition> condition) {
		List<Object> ret = findTotalCountByConditions(condition);
		return (Long) ret.get(0);
	}
	
	@Override
	public List<LpSPM> getLpSPMsByListCondition(Set<Condition> set) {
        return findByConditions(set);
    }

	@Override
	public List<Object> getLpSPMsCountByListCondition(Set<Condition> conditions) {
		return findTotalCountByConditions(conditions);
	}

    @Override
    public Class<LpSPM> getPersistentClass() {
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
