package com.aimir.dao.system.impl;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.LoginLogDao;
import com.aimir.model.system.LoginLog;
import com.aimir.util.Condition;

@Repository(value = "loginlogDao")
public class LoginLogDaoImpl  extends AbstractJpaDao<LoginLog, Long> implements LoginLogDao {

	public LoginLogDaoImpl() {
		super(LoginLog.class);
	}

    @Override
    public Class<LoginLog> getPersistentClass() {
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