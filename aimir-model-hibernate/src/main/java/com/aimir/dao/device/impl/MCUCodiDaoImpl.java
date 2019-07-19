package com.aimir.dao.device.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.MCUCodiDao;
import com.aimir.model.device.MCUCodi;

@Repository(value = "mcucodiDao")
public class MCUCodiDaoImpl extends AbstractHibernateGenericDao<MCUCodi, Long> implements MCUCodiDao {

	@Autowired
	protected MCUCodiDaoImpl(SessionFactory sessionFactory) {
		super(MCUCodi.class);
		super.setSessionFactory(sessionFactory);
	}
}
