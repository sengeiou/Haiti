package com.aimir.dao.device.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.ModemPowerLogDao;
import com.aimir.model.device.ModemPowerLog;

@Repository(value = "modempowerlogDao")
public class ModemPowerLogDaoImpl extends AbstractHibernateGenericDao<ModemPowerLog, Integer> implements ModemPowerLogDao {

    Log log = LogFactory.getLog(ModemPowerLogDaoImpl.class);
    
	@Autowired
	protected ModemPowerLogDaoImpl(SessionFactory sessionFactory) {
		super(ModemPowerLog.class);
		super.setSessionFactory(sessionFactory);
	}

}