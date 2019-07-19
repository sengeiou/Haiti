package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.SmsServiceLogDao;
import com.aimir.model.system.SmsServiceLog;
import com.aimir.util.Condition;

@Repository(value="smsserviceLogDao")
public class SmsServiceLogDaoImpl extends AbstractJpaDao<SmsServiceLog, Integer> implements SmsServiceLogDao{

	public SmsServiceLogDaoImpl() {
		super(SmsServiceLog.class);
	}

    @Override
    public Integer count() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<SmsServiceLog> getPersistentClass() {
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
