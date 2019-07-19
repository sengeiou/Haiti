package com.aimir.dao.mvm.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.MeasurementHistoryDao;
import com.aimir.model.device.MeasurementHistory;
import com.aimir.util.Condition;

@Repository(value="measurementhistoryDao")
public class MeasurementHistoryDaoImpl extends AbstractJpaDao<MeasurementHistory, Long> implements MeasurementHistoryDao{

    public MeasurementHistoryDaoImpl() {
		super(MeasurementHistory.class);
	}

    @Override
    public Class<MeasurementHistory> getPersistentClass() {
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
