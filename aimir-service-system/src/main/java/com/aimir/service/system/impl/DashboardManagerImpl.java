package com.aimir.service.system.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.flex.remoting.RemotingDestination;

import com.aimir.dao.system.DashboardDao;
import com.aimir.model.system.Dashboard;
import com.aimir.service.system.DashboardManager;

import java.util.Iterator;
import java.util.List;

import javax.jws.WebService;

@WebService(endpointInterface = "com.aimir.service.system.DashboardManager")
@Service(value = "dashboardManager")
@RemotingDestination
@Transactional
public class DashboardManagerImpl implements DashboardManager {
    @Autowired
    DashboardDao dao;
    
    public void add(Dashboard dashboard) {
        dao.add(dashboard);
    }

    public void update(Dashboard dashboard) {
        dao.update(dashboard);
    }
    
    public void delete(Integer dashboardId) {
        dao.deleteById(dashboardId);
    }

    public Dashboard getDashboard(Integer dashboardId) {
        return dao.get(dashboardId);
    }

//    public List<Dashboard> getDashboards() {
//        return dao.getAll();
//    }
    
    public List<Dashboard> getDashboardsByOperator(Integer operatorId){
    	return dao.getDashboardsByOperator(operatorId);
    }
    
    public List<Dashboard> getDashboardsByRole(Integer roleId){
    	return dao.getDashboardsByRole(roleId);
    }

    @SuppressWarnings("unchecked")
	public List<Dashboard> getDashboardsByOperatorAndRole(Integer operatorId, Integer roleId) {
    	List<Dashboard> dashboardList = dao.getDashboardsByRole(roleId);
    	List<Dashboard> dashboardTemp = dao.getDashboardsByOperator(operatorId);
    	
    	if (dashboardTemp.size() > 0) {
    		Iterator it = dashboardTemp.iterator();
    		while(it.hasNext()) {
    			Dashboard dashboard = (Dashboard) it.next();
    			dashboardList.add(dashboard);
    		}
    	}
    	return dashboardList;
    }

    public boolean checkDashboardCountByOperator(Integer operatorId) {
		return dao.checkDashboardCountByOperator(operatorId);
	}
}
