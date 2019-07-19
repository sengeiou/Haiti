package com.aimir.dao.system.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.TimeZoneDao;
import com.aimir.model.system.TimeZone;

	@Repository(value = "timezoneDao")
	public class TimeZoneDaoImpl extends AbstractHibernateGenericDao<TimeZone, Integer> implements TimeZoneDao {
			
	    Log logger = LogFactory.getLog(TimeZoneDaoImpl.class);
	    
	    @Autowired
	    protected TimeZoneDaoImpl(SessionFactory sessionFactory) {
	        super(TimeZone.class);
	        super.setSessionFactory(sessionFactory);
	    }
 
		
}
