package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.HeadendCtrlDao;
import com.aimir.model.device.HeadendCtrl;
import com.aimir.util.Condition;

@Repository(value="headendCtrlDao")
public class HeadendCtrlDaoImpl extends AbstractJpaDao<HeadendCtrl, Integer> implements HeadendCtrlDao {

	public HeadendCtrlDaoImpl() {
		super(HeadendCtrl.class);
	}

    @Override
    public List<HeadendCtrl> getHeadendCtrlLastData(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void insert(HeadendCtrl headendCtrl) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Class<HeadendCtrl> getPersistentClass() {
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
