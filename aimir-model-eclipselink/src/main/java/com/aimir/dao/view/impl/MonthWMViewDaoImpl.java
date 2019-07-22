package com.aimir.dao.view.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.view.MonthWMViewDao;
import com.aimir.model.view.MonthWMView;
import com.aimir.util.Condition;

@Repository(value="monthwmDaoView")
public class MonthWMViewDaoImpl extends AbstractJpaDao<MonthWMView, Integer> implements MonthWMViewDao {
	private static Log log = LogFactory.getLog(MonthWMViewDaoImpl.class);

    protected MonthWMViewDaoImpl() {
        super(MonthWMView.class);
    }

	@Override
	public List<MonthWMView> getMonthWMsByListCondition(Set<Condition> list) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<MonthWMView> getPersistentClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> getSumFieldByCondition(Set<Condition> conditions, String field, String... groupBy) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MonthWMView> getMonthCustomerBillingGridData(Map<String, Object> conditionMap) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MonthWMView getMonthWMbySupplierId(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
