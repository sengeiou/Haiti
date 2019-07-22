package com.aimir.dao.view.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.view.MonthTMViewDao;
import com.aimir.model.view.MonthTMView;
import com.aimir.util.Condition;

@Repository(value="monthtmDaoView")
public class MonthTMViewDaoImpl extends AbstractJpaDao<MonthTMView, Integer> implements MonthTMViewDao {
	private static Log log = LogFactory.getLog(MonthTMViewDaoImpl.class);

    protected MonthTMViewDaoImpl() {
        super(MonthTMView.class);
    }

	@Override
	public Class<MonthTMView> getPersistentClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> getSumFieldByCondition(Set<Condition> conditions, String field, String... groupBy) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MonthTMView> getMonthCustomerBillingGridData(Map<String, Object> conditionMap) {
		// TODO Auto-generated method stub
		return null;
	}


}
