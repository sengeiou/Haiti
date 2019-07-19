package com.aimir.dao.device.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.MCUVarDao;
import com.aimir.model.device.MCUVar;

@Repository(value = "mcuvarDao")
public class MCUVarDaoImpl extends AbstractHibernateGenericDao<MCUVar, Long> implements MCUVarDao {

	@Autowired
	protected MCUVarDaoImpl(SessionFactory sessionFactory) {
		super(MCUVar.class);
		super.setSessionFactory(sessionFactory);
	}
}
