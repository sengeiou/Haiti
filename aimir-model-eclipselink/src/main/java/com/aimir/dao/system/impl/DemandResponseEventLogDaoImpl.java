package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.DemandResponseEventLogDao;
import com.aimir.model.system.DemandResponseEventLog;
import com.aimir.util.Condition;

@Repository(value = "demandResponseEventLogDao")
public class DemandResponseEventLogDaoImpl extends AbstractJpaDao<DemandResponseEventLog, Integer> implements DemandResponseEventLogDao {
	
	public DemandResponseEventLogDaoImpl() {
		super(DemandResponseEventLog.class);
	}

    @Override
    public List<Map<String, Object>> getDemandResponseHistory(String userId,
            String contractNumber, int page, int limit, String fromDate,
            String toDate) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDemandResponseHistoryTotalCount(String userId,
            String contractNumber, String fromDate, String toDate) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<DemandResponseEventLog> getDemandResponseEventLogs(
            DemandResponseEventLog drEventLog) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<DemandResponseEventLog> getPersistentClass() {
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
