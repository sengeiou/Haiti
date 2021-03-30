package com.aimir.dao.system;

import com.aimir.model.system.FixedVariable;

public interface FixedVariableDao {

	public FixedVariable getFixedVariableDao(String name, Integer tariffId, String applydate);
}
