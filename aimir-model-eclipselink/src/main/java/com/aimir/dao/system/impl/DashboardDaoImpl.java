package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.DashboardDao;
import com.aimir.model.system.Dashboard;
import com.aimir.util.Condition;

@Repository(value = "dashboardDao")
public class DashboardDaoImpl extends AbstractJpaDao<Dashboard, Integer> implements DashboardDao {

    Log logger = LogFactory.getLog(DashboardDaoImpl.class);

	public DashboardDaoImpl() {
		super(Dashboard.class);
	}

    @Override
    public List<Dashboard> getDashboardsByOperator(Integer operatorId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Dashboard> getDashboardsByRole(Integer roleId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean checkDashboardCountByOperator(Integer operatorId) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<Dashboard> getDashboardByName(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Dashboard> getDashboardByNameOpeatorId(String name,
            int operatorId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<Dashboard> getPersistentClass() {
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