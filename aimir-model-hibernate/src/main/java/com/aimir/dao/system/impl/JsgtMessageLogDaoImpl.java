package com.aimir.dao.system.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.JsgtMessageLogDao;
import com.aimir.model.system.JsgtMessageLog;

@Repository(value="jsgtmessagelogDao")
public class JsgtMessageLogDaoImpl extends AbstractHibernateGenericDao<JsgtMessageLog, Integer> implements JsgtMessageLogDao {

	@Autowired
	protected JsgtMessageLogDaoImpl(SessionFactory sessionFactory) {
		super(JsgtMessageLog.class);
		super.setSessionFactory(sessionFactory);
	}
}
