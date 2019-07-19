package com.aimir.dao.user.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.user.UserDao;
import com.aimir.model.user.User;
import com.aimir.util.Condition;

@Repository(value = "userDao")
public class UserDaoImpl extends AbstractJpaDao<User, Long> implements UserDao {

	public UserDaoImpl() {
		super(User.class);
	}

    @Override
    public Class<User> getPersistentClass() {
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