package com.aimir.dao.mvm.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.LpEmExternalDao;
import com.aimir.model.mvm.LpEmExternal;
import com.aimir.model.mvm.LpExPk;
import com.aimir.util.Condition;

@Repository(value="lpemexternalDao")
public class LpEmExternalDaoImpl extends AbstractJpaDao<LpEmExternal, LpExPk> implements LpEmExternalDao{

	public LpEmExternalDaoImpl() {
        super(LpEmExternal.class);
    }

	@Override
	public Class<LpEmExternal> getPersistentClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> getSumFieldByCondition(Set<Condition> conditions, String field, String... groupBy) {
		// TODO Auto-generated method stub
		return null;
	}

}
