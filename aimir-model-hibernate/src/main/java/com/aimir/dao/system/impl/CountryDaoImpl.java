package com.aimir.dao.system.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.CountryDao;
import com.aimir.model.system.Country;

@Repository(value = "countryDao")
public class CountryDaoImpl extends AbstractHibernateGenericDao<Country, Integer> implements CountryDao {
			
    Log logger = LogFactory.getLog(CountryDaoImpl.class);
	    
    @Autowired
    protected CountryDaoImpl(SessionFactory sessionFactory) {
        super(Country.class);
        super.setSessionFactory(sessionFactory);
    }
}
