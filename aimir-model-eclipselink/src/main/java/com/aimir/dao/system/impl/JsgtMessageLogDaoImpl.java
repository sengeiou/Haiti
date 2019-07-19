package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.JsgtMessageLogDao;
import com.aimir.model.system.JsgtMessageLog;
import com.aimir.util.Condition;

@Repository(value="jsgtmessagelogDao")
public class JsgtMessageLogDaoImpl extends AbstractJpaDao<JsgtMessageLog, Integer> implements JsgtMessageLogDao {

	public JsgtMessageLogDaoImpl() {
		super(JsgtMessageLog.class);
	}

    @Override
    public Class<JsgtMessageLog> getPersistentClass() {
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
