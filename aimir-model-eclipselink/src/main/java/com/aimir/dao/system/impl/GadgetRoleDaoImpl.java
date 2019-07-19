package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.GadgetRoleDao;
import com.aimir.model.system.GadgetRole;
import com.aimir.util.Condition;

@Repository(value = "gadgetroleDao")
public class GadgetRoleDaoImpl extends AbstractJpaDao<GadgetRole, Integer> implements GadgetRoleDao {

    Log logger = LogFactory.getLog(GadgetRoleDaoImpl.class);
    
	public GadgetRoleDaoImpl() {
		super(GadgetRole.class);
	}

    @Override
    public List<Map<String, Object>> getGadgetRolesList(
            Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getGadgetListByRole(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<GadgetRole> getDelGadgetRoleList(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<GadgetRole> getPersistentClass() {
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