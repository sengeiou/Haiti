package com.aimir.dao.device.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.SimCardDao;
import com.aimir.model.device.SimCard;

@Repository(value = "simCardDao")
public class SimCardDaoImpl extends AbstractHibernateGenericDao<SimCard, Long> implements SimCardDao {
	
	@Autowired
	protected SimCardDaoImpl(SessionFactory sessionFactory) {
		super(SimCard.class);
		super.setSessionFactory(sessionFactory);
	}

}
