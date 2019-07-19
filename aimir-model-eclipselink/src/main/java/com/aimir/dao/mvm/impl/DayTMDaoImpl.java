package com.aimir.dao.mvm.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.DayTMDao;
import com.aimir.model.mvm.DayTM;
import com.aimir.util.Condition;

@Repository(value = "daytmDao")
@SuppressWarnings("unchecked")
public class DayTMDaoImpl extends AbstractJpaDao<DayTM, Integer>
		implements DayTMDao {

	private static Log logger = LogFactory.getLog(DayTMDaoImpl.class);

	public DayTMDaoImpl() {
		super(DayTM.class);
	}

    @Override
    public List<DayTM> getDayTMsByMap(Map map) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getDayTMsCountByListCondition(Set<Condition> set) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<DayTM> getDayTMsByList(List<Map> list) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<DayTM> getDayTMsByListCondition(Set<Condition> list) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getDayTMsMaxMinAvg(Set<Condition> conditions, String div) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getDayTMsAvgList(Set<Condition> conditions) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getAvgGroupByListCondition(Set<Condition> conditions) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public List<Object> getConsumptionTmMonitoring(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<DayTM> getPersistentClass() {
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