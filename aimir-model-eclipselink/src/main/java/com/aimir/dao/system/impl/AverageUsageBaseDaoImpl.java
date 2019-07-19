package com.aimir.dao.system.impl;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.AverageUsageBaseDao;
import com.aimir.model.system.AverageUsageBase;
import com.aimir.model.system.AverageUsageBasePk;
import com.aimir.util.Condition;

@Repository(value = "averageUsageBaseDao")
public class AverageUsageBaseDaoImpl extends AbstractJpaDao<AverageUsageBase, AverageUsageBasePk> implements AverageUsageBaseDao {

	public AverageUsageBaseDaoImpl() {
		super(AverageUsageBase.class);
	}

    @Override
    public Class<AverageUsageBase> getPersistentClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getSumFieldByCondition(Set<Condition> conditions,
            String field, String... groupBy) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<AverageUsageBase> getAverageUsageBaseListBystartDate(
            Integer avgUsageId, Integer supplyType, String UsageYear) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int deleteAvgUsageId(AverageUsageBase averageUsageBase) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public List<AverageUsageBase> getSetYearsbyId(Integer avgUsageId) {
        // TODO Auto-generated method stub
        return null;
    }

}
