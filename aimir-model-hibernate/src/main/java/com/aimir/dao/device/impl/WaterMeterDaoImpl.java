package com.aimir.dao.device.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.WaterMeterDao;
import com.aimir.model.device.WaterMeter;

@Repository(value = "watermeterDao")
public class WaterMeterDaoImpl extends AbstractHibernateGenericDao<WaterMeter, Integer> implements WaterMeterDao {

    Log logger = LogFactory.getLog(WaterMeterDaoImpl.class);
    
	@Autowired
	protected WaterMeterDaoImpl(SessionFactory sessionFactory) {
		super(WaterMeter.class);
		super.setSessionFactory(sessionFactory);
	}
}