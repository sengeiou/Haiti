package com.aimir.cms.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.cms.model.TariffEnt;
import com.aimir.dao.AbstractHibernateGenericDao;

@Repository(value="tariffEntDao")
public class TariffEntDaoImpl extends AbstractHibernateGenericDao<TariffEnt, Integer> implements TariffEntDao{
    
	@Autowired
	protected TariffEntDaoImpl(SessionFactory sessionFactory) {
		super(TariffEnt.class);
		super.setSessionFactory(sessionFactory);
	}
}