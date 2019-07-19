package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.TimeZoneDao;
import com.aimir.model.system.TimeZone;
import com.aimir.util.Condition;

@Repository(value = "timezoneDao")
public class TimeZoneDaoImpl extends AbstractJpaDao<TimeZone, Integer> implements TimeZoneDao {
		
    Log logger = LogFactory.getLog(TimeZoneDaoImpl.class);
    
    public TimeZoneDaoImpl() {
        super(TimeZone.class);
    }

    @Override
    public Class<TimeZone> getPersistentClass() {
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
