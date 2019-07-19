package com.aimir.service.user.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import com.aimir.model.user.User;
import com.aimir.service.user.UserManager;
import com.aimir.dao.user.UserDao;

import java.util.List;

import javax.jws.WebService;

@WebService(endpointInterface = "com.aimir.service.user.UserManager")
@Service(value = "userManager")
@Transactional
public class UserManagerImpl implements UserManager {
    @Autowired
    UserDao dao;

    public void setUserDao(UserDao dao) {
        this.dao = dao;
    }

    @SuppressWarnings("unchecked")
	public List<User> gets() {
        return dao.getAll();
    }

    public User get(String userId) {
        return dao.get(Long.valueOf(userId));
    }

    public void add(User user) {
        dao.add(user);
    }
    
    public void update(User user) {
        dao.update(user);
    }

    public void delete(String userId) {
        dao.deleteById(Long.valueOf(userId));
    }
}
