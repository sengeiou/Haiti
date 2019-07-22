package com.aimir.dao.view.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.view.MonthTMViewDao;
import com.aimir.model.view.MonthTMView;

@Repository(value="monthtmDaoView")
@Transactional
public class MonthTMViewDaoImpl extends AbstractHibernateGenericDao<MonthTMView, Integer> implements MonthTMViewDao {
	private static Log log = LogFactory.getLog(MonthTMViewDaoImpl.class);

	@Autowired
	protected MonthTMViewDaoImpl(SessionFactory sessionFactory) {
		super(MonthTMView.class);
		super.setSessionFactory(sessionFactory);
	}

	@Override
	public List<MonthTMView> getMonthCustomerBillingGridData(Map<String, Object> conditionMap) {
		// TODO Auto-generated method stub
		return null;
	}
    

}
