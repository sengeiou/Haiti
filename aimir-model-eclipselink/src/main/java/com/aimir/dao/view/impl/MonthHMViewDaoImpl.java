package com.aimir.dao.view.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.view.MonthHMViewDao;
import com.aimir.model.view.MonthHMView;
import com.aimir.util.Condition;

@Repository(value="monthhmDaoView")
public class MonthHMViewDaoImpl extends AbstractJpaDao<MonthHMView, Integer> implements MonthHMViewDao {
	private static Log log = LogFactory.getLog(MonthHMViewDaoImpl.class);

    protected MonthHMViewDaoImpl() {
        super(MonthHMView.class);
    }

	@Override
	public List<MonthHMView> getMonthHMsByListCondition(Set<Condition> list) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<MonthHMView> getPersistentClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> getSumFieldByCondition(Set<Condition> conditions, String field, String... groupBy) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MonthHMView> getMonthCustomerBillingGridData(Map<String, Object> conditionMap) {
		// TODO Auto-generated method stub
		return null;
	}

	


}
