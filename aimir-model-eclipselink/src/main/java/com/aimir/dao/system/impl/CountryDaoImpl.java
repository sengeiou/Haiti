package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.CountryDao;
import com.aimir.model.system.Country;
import com.aimir.util.Condition;

@Repository(value = "countryDao")
public class CountryDaoImpl extends AbstractJpaDao<Country, Integer> implements CountryDao {
			
    Log logger = LogFactory.getLog(CountryDaoImpl.class);
	    
    public CountryDaoImpl() {
        super(Country.class);
    }

    @Override
    public Class<Country> getPersistentClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getSumFieldByCondition(Set<Condition> conditions,
            String field, String... groupBy) {
        // TODO Auto-generated method stub
        return null;
    }
}
