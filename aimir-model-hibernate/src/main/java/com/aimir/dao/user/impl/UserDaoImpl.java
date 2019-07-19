package com.aimir.dao.user.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.user.UserDao;
import com.aimir.model.user.User;

@Repository(value = "userDao")
public class UserDaoImpl extends AbstractHibernateGenericDao<User, Long> implements
        UserDao {

    @Autowired
    protected UserDaoImpl(SessionFactory sessionFactory) {
        super(User.class);
        super.setSessionFactory(sessionFactory);
    }
}