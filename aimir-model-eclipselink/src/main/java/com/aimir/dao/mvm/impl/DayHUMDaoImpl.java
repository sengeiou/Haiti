package com.aimir.dao.mvm.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.DayHUMDao;
import com.aimir.model.mvm.DayHUM;
import com.aimir.util.Condition;

@Repository(value = "dayhumDao")
@SuppressWarnings("unchecked")
public class DayHUMDaoImpl extends AbstractJpaDao<DayHUM, Integer>
		implements DayHUMDao {

	private static Log logger = LogFactory.getLog(DayHUMDaoImpl.class);

	public DayHUMDaoImpl() {
		super(DayHUM.class);
	}

    @Override
    public List<DayHUM> getDayHUMsByMap(Map map) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getDayHUMsCountByListCondition(Set<Condition> set) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<DayHUM> getDayHUMsByList(List<Map> list) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<DayHUM> getDayHUMsByListCondition(Set<Condition> list) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getDayHUMsMaxMinAvg(Set<Condition> conditions,
            String div) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getDayHUMsAvgList(Set<Condition> conditions) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getAvgGroupByListCondition(Set<Condition> conditions) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public List<Object> getConsumptionHumMonitoring(
            Map<String, Object> conditionDay) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<DayHUM> getPersistentClass() {
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