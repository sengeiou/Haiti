package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.WebServiceLogDao;
import com.aimir.model.system.WebServiceLog;
import com.aimir.util.Condition;

@Repository(value="webserviceLogDao")
public class WebServiceLogDaoImpl extends AbstractJpaDao<WebServiceLog, Integer> implements WebServiceLogDao{

	public WebServiceLogDaoImpl() {
		super(WebServiceLog.class);
	}

    @Override
    public Integer count() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<WebServiceLog> getPersistentClass() {
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
