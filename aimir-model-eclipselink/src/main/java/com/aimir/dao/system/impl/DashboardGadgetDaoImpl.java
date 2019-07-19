package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.DashboardGadgetDao;
import com.aimir.model.system.DashboardGadget;
import com.aimir.model.system.DashboardGadgetVO;
import com.aimir.util.Condition;

@Repository(value = "dashboardgadgetDao")
public class DashboardGadgetDaoImpl extends AbstractJpaDao<DashboardGadget, Integer> implements DashboardGadgetDao {

    Log logger = LogFactory.getLog(DashboardGadgetDaoImpl.class);
    
	public DashboardGadgetDaoImpl() {
		super(DashboardGadget.class);
	}

    @Override
    public List<?> getGrid(Integer dashboardId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<DashboardGadgetVO> getGadgetsByDashboard(Integer dashboardId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<DashboardGadget> getDashboardGadgetByDashboardIdGadgetId(
            int dashboardId, int gadgetId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<DashboardGadget> getPersistentClass() {
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