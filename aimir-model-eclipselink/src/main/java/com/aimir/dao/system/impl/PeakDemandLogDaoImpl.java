package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.PeakDemandLogDao;
import com.aimir.model.system.PeakDemandLog;
import com.aimir.util.Condition;

/**
 * 
 * @author Yi Hanghee(javarouka@gmail.com, javarouka@nuritelecom.co.kr)
 *
 */
@Repository(value = "peakDemandLogDao")
public class PeakDemandLogDaoImpl 
	extends AbstractJpaDao<PeakDemandLog, Integer> implements PeakDemandLogDao {
	
	public PeakDemandLogDaoImpl() {
		super(PeakDemandLog.class);
	}

	@Override
	public long totalByConditions(Set<Condition> condition) {
		List<Object> ret = findTotalCountByConditions(condition);
		return (Long) ret.get(0);
	}

    @Override
    public Class<PeakDemandLog> getPersistentClass() {
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