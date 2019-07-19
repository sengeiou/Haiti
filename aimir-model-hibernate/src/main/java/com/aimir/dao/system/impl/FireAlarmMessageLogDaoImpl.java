package com.aimir.dao.system.impl;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.FireAlarmMessageLogDao;
import com.aimir.model.system.FireAlarmMessageLog;

@Repository(value="firealarmmessagelogDao")
public class FireAlarmMessageLogDaoImpl extends AbstractHibernateGenericDao<FireAlarmMessageLog, Number> 
implements FireAlarmMessageLogDao{
    
	@Autowired
	protected FireAlarmMessageLogDaoImpl(SessionFactory sessionFactory) {
		super(FireAlarmMessageLog.class);
		super.setSessionFactory(sessionFactory);
	}
	
	public Integer count() {
		Criteria criteria = getSession().createCriteria(FireAlarmMessageLog.class);
		criteria.setProjection(Projections.rowCount());
		return ((Number) criteria.uniqueResult()).intValue();
	}
	
	public FireAlarmMessageLog[] listNotSended(){
		//TODO IMPLEMENT
		return null;
	}
}
