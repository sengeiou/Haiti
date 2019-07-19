package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.EventAlertAttrDao;
import com.aimir.model.device.EventAlertAttr;
import com.aimir.util.Condition;

@Repository(value = "eventalertattrDao")
public class EventAlertAttrDaoImpl extends AbstractJpaDao<EventAlertAttr, Integer> implements EventAlertAttrDao {

	public EventAlertAttrDaoImpl() {
		super(EventAlertAttr.class);
	}

    @Override
    public Class<EventAlertAttr> getPersistentClass() {
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
