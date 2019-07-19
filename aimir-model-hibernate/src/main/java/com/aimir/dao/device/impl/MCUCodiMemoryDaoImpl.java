package com.aimir.dao.device.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.MCUCodiMemoryDao;
import com.aimir.model.device.MCUCodiMemory;

@Repository(value = "mcucodimemoryDao")
public class MCUCodiMemoryDaoImpl extends AbstractHibernateGenericDao<MCUCodiMemory, Long> implements MCUCodiMemoryDao {

	@Autowired
	protected MCUCodiMemoryDaoImpl(SessionFactory sessionFactory) {
		super(MCUCodiMemory.class);
		super.setSessionFactory(sessionFactory);
	}
}
