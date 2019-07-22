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
import com.aimir.dao.view.MonthHMViewDao;
import com.aimir.model.view.MonthHMView;
import com.aimir.util.Condition;

@Repository(value="monthhmDaoView")
@Transactional
public class MonthHMViewDaoImpl extends AbstractHibernateGenericDao<MonthHMView, Integer> implements MonthHMViewDao {
	private static Log log = LogFactory.getLog(MonthHMViewDaoImpl.class);

	@Autowired
	protected MonthHMViewDaoImpl(SessionFactory sessionFactory) {
		super(MonthHMView.class);
		super.setSessionFactory(sessionFactory);
	}

	@Override
	public List<MonthHMView> getMonthHMsByListCondition(Set<Condition> list) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<MonthHMView> getMonthCustomerBillingGridData(Map<String, Object> conditionMap) {
		// TODO Auto-generated method stub
		return null;
	}
    

}
