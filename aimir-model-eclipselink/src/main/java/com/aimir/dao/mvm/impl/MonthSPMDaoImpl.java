package com.aimir.dao.mvm.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants.DateType;
import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.MonthSPMDao;
import com.aimir.model.mvm.MonthSPM;
import com.aimir.util.Condition;

/**
 * 태양열에너지 월별 검침 Dao
 * @author Yi Hanghee(javarouka@gmail.com, javarouka@nuritelecom.co.kr)
 *
 */
@Repository(value = "monthSPMDao")
public class MonthSPMDaoImpl extends AbstractJpaDao<MonthSPM, Integer> 
	implements MonthSPMDao {
	
    public MonthSPMDaoImpl() {
		super(MonthSPM.class);
	}
	
	@Override
	public long totalByConditions(Set<Condition> condition) {
		List<Object> ret = findTotalCountByConditions(condition);
		return (Long) ret.get(0);
	}

	@Override
	public List<MonthSPM> getMonthSPMsByListCondition(Set<Condition> conditions) {
		return findByConditions(conditions);
	}

	@Override
	public List<Object> getMonthSPMsCountByListCondition(Set<Condition> conditions) {
		return findTotalCountByConditions(conditions);
	}

    @Override
    public List<Object> getConsumptionEmCo2ManualMonitoring(
            Map<String, Object> condition, DateType monthly) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<MonthSPM> getPersistentClass() {
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
