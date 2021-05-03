package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.FixedVariableDao;
import com.aimir.model.system.FixedVariable;
import com.aimir.util.Condition;

@Repository(value = "fixedVariableDao")
public class FixedVariableDaoImpl extends AbstractJpaDao<FixedVariable, Integer> implements FixedVariableDao {

	public FixedVariableDaoImpl() {
		super(FixedVariable.class);
	}

	@Override
	public Class<FixedVariable> getPersistentClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> getSumFieldByCondition(Set<Condition> conditions, String field, String... groupBy) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FixedVariable getFixedVariableDao(String name, Integer tariffId, String applydate) {
		// TODO Auto-generated method stub
		return null;
	}


}
