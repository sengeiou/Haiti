package com.aimir.dao.device.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.MeterTimeSyncLogDao;
import com.aimir.model.device.MeterTimeSyncLog;

@Repository(value = "metertimesynclogDao")
public class MeterTimeSyncLogDaoImpl extends AbstractHibernateGenericDao<MeterTimeSyncLog, Long> implements MeterTimeSyncLogDao {
	
    Log logger = LogFactory.getLog(MeterTimeSyncLogDaoImpl.class);
    
	@Autowired
	protected MeterTimeSyncLogDaoImpl(SessionFactory sessionFactory) {
		super(MeterTimeSyncLog.class);
		super.setSessionFactory(sessionFactory);
	}

}


