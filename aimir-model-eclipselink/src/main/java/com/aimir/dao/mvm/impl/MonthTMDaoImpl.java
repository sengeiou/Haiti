package com.aimir.dao.mvm.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.MonthTMDao;
import com.aimir.model.mvm.MonthTM;
import com.aimir.util.Condition;

@Repository(value = "monthtmDao")
public class MonthTMDaoImpl extends
		AbstractJpaDao<MonthTM, Integer> implements MonthTMDao {

	private static Log logger = LogFactory.getLog(MonthTMDaoImpl.class);

	public MonthTMDaoImpl() {
		super(MonthTM.class);
	}

    @Override
    public List<MonthTM> getMonthTMsByListCondition(Set<Condition> list) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMonthTMsCountByListCondition(Set<Condition> set) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMonthTMsMaxMinAvg(Set<Condition> conditions,
            String div) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionTmMonitoring(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getUsageChartData(Set<Condition> conditions) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<MonthTM> getPersistentClass() {
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