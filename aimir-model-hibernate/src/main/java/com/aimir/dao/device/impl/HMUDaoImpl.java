package com.aimir.dao.device.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.HMUDao;
import com.aimir.model.device.HMU;

@Repository(value = "hmuDao")
public class HMUDaoImpl extends AbstractHibernateGenericDao<HMU, Integer> implements HMUDao {

    Log log = LogFactory.getLog(HMUDaoImpl.class);
    
	@Autowired
	protected HMUDaoImpl(SessionFactory sessionFactory) {
		super(HMU.class);
		super.setSessionFactory(sessionFactory);
	}

}