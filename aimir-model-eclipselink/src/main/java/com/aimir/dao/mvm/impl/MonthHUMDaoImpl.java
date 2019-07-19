package com.aimir.dao.mvm.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.MonthHUMDao;
import com.aimir.model.mvm.MonthHUM;
import com.aimir.util.Condition;

@Repository(value = "monthhumDao")
public class MonthHUMDaoImpl extends AbstractJpaDao<MonthHUM, Integer> implements MonthHUMDao {

	private static Log logger = LogFactory.getLog(MonthHUMDaoImpl.class);
    
	public MonthHUMDaoImpl() {
		super(MonthHUM.class);
	}

    @Override
    public List<MonthHUM> getMonthHUMsByListCondition(Set<Condition> list) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMonthHUMsCountByListCondition(Set<Condition> set) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMonthHUMsMaxMinAvg(Set<Condition> conditions,
            String div) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionHumMonitoring(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getUsageChartData(Set<Condition> conditions) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<MonthHUM> getPersistentClass() {
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