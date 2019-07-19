package com.aimir.dao.device.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.GasMeterDao;
import com.aimir.model.device.GasMeter;

@Repository(value = "gasmeterDao")
public class GasMeterDaoImpl extends AbstractHibernateGenericDao<GasMeter, Integer> implements GasMeterDao {

    Log logger = LogFactory.getLog(MeterDaoImpl.class);
    
	@Autowired
	protected GasMeterDaoImpl(SessionFactory sessionFactory) {
		super(GasMeter.class);
		super.setSessionFactory(sessionFactory);
	}
}