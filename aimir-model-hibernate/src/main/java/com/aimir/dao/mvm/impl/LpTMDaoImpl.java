package com.aimir.dao.mvm.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.mvm.LpTMDao;
import com.aimir.model.mvm.LpTM;


@Repository(value="lptmDao")
public class LpTMDaoImpl extends AbstractHibernateGenericDao<LpTM, Integer> implements LpTMDao{

	@Autowired
	protected LpTMDaoImpl(SessionFactory sessionFactory) {
		super(LpTM.class);
		super.setSessionFactory(sessionFactory);
	}
}
