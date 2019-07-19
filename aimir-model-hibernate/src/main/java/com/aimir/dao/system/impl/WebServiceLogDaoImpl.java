package com.aimir.dao.system.impl;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.WebServiceLogDao;
import com.aimir.model.system.AimirGroup;
import com.aimir.model.system.WebServiceLog;

@Repository(value="webserviceLogDao")
public class WebServiceLogDaoImpl extends AbstractHibernateGenericDao<WebServiceLog, Integer> implements WebServiceLogDao{

	@Autowired
	protected WebServiceLogDaoImpl(SessionFactory sessionFactory) {
		super(WebServiceLog.class);
		super.setSessionFactory(sessionFactory);
	}

	public Integer count() {
		Criteria criteria = getSession().createCriteria(WebServiceLog.class);
		criteria.setProjection(Projections.rowCount());
		return ((Number) criteria.uniqueResult()).intValue();
	}
}
