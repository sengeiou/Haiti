package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.AverageUsageDao;
import com.aimir.model.system.AverageUsage;
import com.aimir.util.Condition;

@Repository(value = "averageUsageDao")
public class AverageUsageDaoImpl extends AbstractJpaDao<AverageUsage, Integer> implements AverageUsageDao {

	public AverageUsageDaoImpl() {
		super(AverageUsage.class);
	}

    @Override
    public AverageUsage getAverageUsageByUsed() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int usageInitSql(AverageUsage averageUsage) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int updateSql(AverageUsage averageUsage) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Class<AverageUsage> getPersistentClass() {
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
