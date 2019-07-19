package com.aimir.dao.device.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.MCUCodiNeighborDao;
import com.aimir.model.device.MCUCodiNeighbor;

@Repository(value = "mcucodineighborDao")
public class MCUCodiNeighborDaoImpl extends AbstractHibernateGenericDao<MCUCodiNeighbor, Long> implements MCUCodiNeighborDao {

	@Autowired
	protected MCUCodiNeighborDaoImpl(SessionFactory sessionFactory) {
		super(MCUCodiNeighbor.class);
		super.setSessionFactory(sessionFactory);
	}
}
