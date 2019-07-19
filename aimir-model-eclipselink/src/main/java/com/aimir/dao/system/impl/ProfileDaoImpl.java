package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.ProfileDao;
import com.aimir.model.system.Profile;
import com.aimir.util.Condition;

@Repository(value="profileDao")
public class ProfileDaoImpl extends AbstractJpaDao<Profile, Integer> implements ProfileDao {
    
	public ProfileDaoImpl() {
		super(Profile.class);
	}

    @Override
    public Class<Profile> getPersistentClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getSumFieldByCondition(Set<Condition> conditions,
            String field, String... groupBy) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Profile> getProfileByUser(Integer userId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteByUser(Integer userId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean checkProfileByUser(Integer userId) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<String> getMeterEventProfileByUser(Integer userId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int deleteMeterEventProfileByUser(Integer userId) {
        // TODO Auto-generated method stub
        return 0;
    }

}
