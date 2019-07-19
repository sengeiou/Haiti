package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.FireAlarmMessageLogDao;
import com.aimir.model.system.FireAlarmMessageLog;
import com.aimir.util.Condition;

@Repository(value="firealarmmessagelogDao")
public class FireAlarmMessageLogDaoImpl extends AbstractJpaDao<FireAlarmMessageLog, Number> 
implements FireAlarmMessageLogDao{
    
	public FireAlarmMessageLogDaoImpl() {
		super(FireAlarmMessageLog.class);
	}

    @Override
    public FireAlarmMessageLog[] listNotSended() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<FireAlarmMessageLog> getPersistentClass() {
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
