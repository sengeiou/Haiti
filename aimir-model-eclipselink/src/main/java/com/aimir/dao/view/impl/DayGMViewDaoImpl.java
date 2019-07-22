package com.aimir.dao.view.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.view.DayGMViewDao;
import com.aimir.model.view.DayGMView;
import com.aimir.util.Condition;

@Repository(value="daygmDaoView")
public class DayGMViewDaoImpl extends AbstractJpaDao<DayGMView, Integer> implements DayGMViewDao {
	private static Log log = LogFactory.getLog(DayGMViewDaoImpl.class);
	
    protected DayGMViewDaoImpl() {
        super(DayGMView.class);
    }

	@Override
	public List<DayGMView> getDayGMsByListCondition(Set<Condition> list) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DayGMView> getDayCustomerBillingGridData(Map<String, Object> conditionMap) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DayGMView getDayGMbySupplierId(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<DayGMView> getPersistentClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> getSumFieldByCondition(Set<Condition> conditions, String field, String... groupBy) {
		// TODO Auto-generated method stub
		return null;
	}
    
}
