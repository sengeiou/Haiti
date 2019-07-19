package com.aimir.cms.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.cms.model.ServPoint;
import com.aimir.dao.AbstractHibernateGenericDao;

@Repository(value="servPointDao")
public class ServPointDaoImpl extends AbstractHibernateGenericDao<ServPoint, Integer> implements ServPointDao{
    
	@Autowired
	protected ServPointDaoImpl(SessionFactory sessionFactory) {
		super(ServPoint.class);
		super.setSessionFactory(sessionFactory);
	}
}