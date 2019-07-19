package com.aimir.dao.device.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.ACDDao;
import com.aimir.model.device.ACD;

@Repository(value = "acdDao")
public class ACDDaoImpl extends AbstractHibernateGenericDao<ACD, Integer> implements ACDDao {

    Log log = LogFactory.getLog(ACDDaoImpl.class);
    
	@Autowired
	protected ACDDaoImpl(SessionFactory sessionFactory) {
		super(ACD.class);
		super.setSessionFactory(sessionFactory);
	}

}