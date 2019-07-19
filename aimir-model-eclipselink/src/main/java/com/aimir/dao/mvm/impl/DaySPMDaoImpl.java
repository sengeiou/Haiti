package com.aimir.dao.mvm.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants.DateType;
import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.DaySPMDao;
import com.aimir.model.mvm.DaySPM;
import com.aimir.util.Condition;

/**
 * 태양열에너지 day 검침 Dao
 * @author Yi Hanghee(javarouka@gmail.com, javarouka@nuritelecom.co.kr)
 *
 */
@Repository(value = "daySPMDao")
public class DaySPMDaoImpl extends AbstractJpaDao<DaySPM, Integer> 
	implements DaySPMDao {
	
    public DaySPMDaoImpl() {
		super(DaySPM.class);
	}
	
	@Override
	public long totalByConditions(Set<Condition> condition) {
		List<Object> ret = findTotalCountByConditions(condition);
		return (Long) ret.get(0);
	}

	@Override
	public List<DaySPM> getDaySPMsByListCondition(Set<Condition> condition) {
		return findByConditions(condition);
	}

	@Override
	public List<Object> getDaySPMsCountByListCondition(Set<Condition> condition) {
		return findTotalCountByConditions(condition);
	}

    @Override
    public double getSumTotalUsageByCondition(Set<Condition> conditions) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Map<String, Double> getSumUsageByCondition(Set<Condition> conditions) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionEmCo2ManualMonitoring(
            Map<String, Object> conditions, DateType weekly) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<DaySPM> getPersistentClass() {
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
