package com.aimir.dao.view.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.view.MonthGMViewDao;
import com.aimir.model.view.MonthGMView;
import com.aimir.util.Condition;

@Repository(value="monthgmDaoView")
public class MonthGMViewDaoImpl extends AbstractJpaDao<MonthGMView, Integer> implements MonthGMViewDao {
	private static Log log = LogFactory.getLog(MonthGMViewDaoImpl.class);

    protected MonthGMViewDaoImpl() {
        super(MonthGMView.class);
    }

	@Override
	public List<MonthGMView> getMonthGMsByListCondition(Set<Condition> list) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<MonthGMView> getPersistentClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> getSumFieldByCondition(Set<Condition> conditions, String field, String... groupBy) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MonthGMView> getMonthCustomerBillingGridData(Map<String, Object> conditionMap) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MonthGMView getMonthGMbySupplierId(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}

}
