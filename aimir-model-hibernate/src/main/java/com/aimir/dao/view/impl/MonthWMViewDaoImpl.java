package com.aimir.dao.view.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.view.MonthWMViewDao;
import com.aimir.model.view.MonthWMView;
import com.aimir.util.Condition;

@Repository(value="monthwmDaoView")
@Transactional
public class MonthWMViewDaoImpl extends AbstractHibernateGenericDao<MonthWMView, Integer> implements MonthWMViewDao {
	private static Log log = LogFactory.getLog(MonthWMViewDaoImpl.class);

	@Autowired
	protected MonthWMViewDaoImpl(SessionFactory sessionFactory) {
		super(MonthWMView.class);
		super.setSessionFactory(sessionFactory);
	}
	
	@Override
	public List<MonthWMView> getMonthWMsByListCondition(Set<Condition> list) {
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
