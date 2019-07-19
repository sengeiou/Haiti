package com.aimir.cms.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.cms.model.MeterEnt;
import com.aimir.dao.AbstractHibernateGenericDao;

@Repository(value="meterEntDao")
public class MeterEntDaoImpl extends AbstractHibernateGenericDao<MeterEnt, Integer> implements MeterEntDao{
    
	@Autowired
	protected MeterEntDaoImpl(SessionFactory sessionFactory) {
		super(MeterEnt.class);
		super.setSessionFactory(sessionFactory);
	}
}