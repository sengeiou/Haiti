package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.CBLCurvesDao;

import com.aimir.model.system.CBLCurves;
import com.aimir.util.Condition;


@Repository(value="cblcurvesDao")
public class CBLCurvesDaoImpl extends AbstractJpaDao<CBLCurves, Integer> implements CBLCurvesDao{

	public CBLCurvesDaoImpl() {
		super(CBLCurves.class);
	}

    @Override
    public Class<CBLCurves> getPersistentClass() {
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
