package com.aimir.dao.system.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.LoginLogDao;
import com.aimir.model.system.LoginLog;

@Repository(value = "loginlogDao")
public class LoginLogDaoImpl  extends AbstractHibernateGenericDao<LoginLog, Long> implements LoginLogDao {

	@Autowired
	protected LoginLogDaoImpl(SessionFactory sessionFactory) {
		super(LoginLog.class);
		super.setSessionFactory(sessionFactory);
	}
}