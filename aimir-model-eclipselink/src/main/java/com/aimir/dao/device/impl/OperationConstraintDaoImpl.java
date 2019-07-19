package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.OperationConstraintDao;
import com.aimir.model.device.OperationConstraint;
import com.aimir.util.Condition;

@Repository(value = "operationconstraintDao")
public class OperationConstraintDaoImpl extends AbstractJpaDao<OperationConstraint, Integer> implements OperationConstraintDao {

	public OperationConstraintDaoImpl() {
		super(OperationConstraint.class);
	}

    @Override
    public Class<OperationConstraint> getPersistentClass() {
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
