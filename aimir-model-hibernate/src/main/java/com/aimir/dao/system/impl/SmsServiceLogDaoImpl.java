package com.aimir.dao.system.impl;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.SmsServiceLogDao;
import com.aimir.model.system.SmsServiceLog;

@Repository(value="smsserviceLogDao")
public class SmsServiceLogDaoImpl extends AbstractHibernateGenericDao<SmsServiceLog, Integer> implements SmsServiceLogDao{

	@Autowired
	protected SmsServiceLogDaoImpl(SessionFactory sessionFactory) {
		super(SmsServiceLog.class);
		super.setSessionFactory(sessionFactory);
	}

	public Integer count() {
		Criteria criteria = getSession().createCriteria(SmsServiceLog.class);
		criteria.setProjection(Projections.rowCount());
		return ((Number) criteria.uniqueResult()).intValue();
	}
}
