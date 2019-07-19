package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.EnergySavingGoalDao;
import com.aimir.model.system.EnergySavingGoal;
import com.aimir.model.system.EnergySavingGoalPk;
import com.aimir.util.Condition;

@Repository(value = "energySavingGoalDao")
public class EnergySavingGoalDaoImpl extends AbstractJpaDao<EnergySavingGoal, EnergySavingGoalPk> implements EnergySavingGoalDao {

	public EnergySavingGoalDaoImpl() {
		super(EnergySavingGoal.class);
	}

    @Override
    public List<EnergySavingGoal> getEnergySavingGoalListBystartDate(
            String startDate, Integer supplierId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<EnergySavingGoal> getPersistentClass() {
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
