package com.aimir.cms.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.cms.model.CustEnt;
import com.aimir.dao.AbstractHibernateGenericDao;

@Repository(value="custEntDao")
public class CustEntDaoImpl extends AbstractHibernateGenericDao<CustEnt, Integer> implements CustEntDao{
    
	@Autowired
	protected CustEntDaoImpl(SessionFactory sessionFactory) {
		super(CustEnt.class);
		super.setSessionFactory(sessionFactory);
	}
}