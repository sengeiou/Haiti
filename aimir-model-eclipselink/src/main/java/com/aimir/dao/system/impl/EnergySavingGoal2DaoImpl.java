package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.EnergySavingGoal2Dao;
import com.aimir.model.system.EnergySavingGoal2;
import com.aimir.model.system.EnergySavingGoalPk2;
import com.aimir.util.Condition;

@Repository(value = "energySavingGoal2Dao")
public class EnergySavingGoal2DaoImpl extends AbstractJpaDao<EnergySavingGoal2, EnergySavingGoalPk2> implements EnergySavingGoal2Dao {

	public EnergySavingGoal2DaoImpl() {
		super(EnergySavingGoal2.class);
	}

    @Override
    public List<EnergySavingGoal2> getEnergySavingGoal2ListByStartDate(
            String searchDateType, String energyType, String startDate,
            Integer supplierId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<EnergySavingGoal2> getEnergySavingGoal2ListByAverageUsage(
            String searchDateType, String energyType, String startDate,
            Integer supplierId, Integer averageUsageId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<EnergySavingGoal2> getEnergySavingGoal2ListByAvg(
            String supplierId, String energyType, String avgInfoId,
            String allView) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<EnergySavingGoal2> getPersistentClass() {
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
