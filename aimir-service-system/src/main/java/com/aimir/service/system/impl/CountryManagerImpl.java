package com.aimir.service.system.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.flex.remoting.RemotingDestination;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.system.CountryDao;
import com.aimir.model.system.Country;
import com.aimir.service.system.CountryManager;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;


@WebService(endpointInterface = "com.aimir.service.system.CountryManager")
@Service(value = "countryManager")
@Transactional
@RemotingDestination
public class CountryManagerImpl implements CountryManager {

    Log logger = LogFactory.getLog(CountryManagerImpl.class);
    
    @Autowired
    CountryDao dao;

    public Country get(Integer countryId) {
        return dao.get(countryId);
    }

    public List<Country> getCountries() {
    	Set<Condition> set = new HashSet<Condition>();
        set.add(new Condition("name",null,null,Restriction.ORDERBY));
    	return dao.findByConditions(set);
    }

}
