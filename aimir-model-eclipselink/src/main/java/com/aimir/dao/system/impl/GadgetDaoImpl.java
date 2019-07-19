package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.GadgetDao;
import com.aimir.model.system.Gadget;
import com.aimir.util.Condition;

@Repository(value = "gadgetDao")
public class GadgetDaoImpl extends AbstractJpaDao<Gadget, Integer> implements GadgetDao {

    private static Log logger = LogFactory.getLog(GadgetDaoImpl.class);
    
	public GadgetDaoImpl() {
		super(Gadget.class);
	}

    @Override
    public List<Gadget> searchGadgetList(String gadgetName, Integer roleId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Gadget> getGadgetByGadgetCode(String gadgetCode) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Gadget> getRemainGadgetList(Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Gadget> getAllGadgetList(Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<Gadget> getPersistentClass() {
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