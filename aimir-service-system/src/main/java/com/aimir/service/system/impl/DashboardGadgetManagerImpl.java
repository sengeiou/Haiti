package com.aimir.service.system.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jws.WebService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.flex.remoting.RemotingDestination;

import com.aimir.dao.system.DashboardDao;
import com.aimir.dao.system.DashboardGadgetDao;
import com.aimir.dao.system.GadgetDao;
import com.aimir.model.system.Dashboard;
import com.aimir.model.system.DashboardGadget;
import com.aimir.model.system.DashboardGadgetPositionVO;
import com.aimir.model.system.DashboardGadgetVO;
import com.aimir.model.system.Gadget;
import com.aimir.service.system.DashboardGadgetManager;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;

@WebService(endpointInterface = "com.aimir.service.system.DashboardGadgetManager")
@Service(value = "dashboardGadgetManager")
@RemotingDestination
@Transactional
public class DashboardGadgetManagerImpl implements DashboardGadgetManager {
    @Autowired
    DashboardGadgetDao dao;
    
    @Autowired
    GadgetDao gadgetDao;
    
    @Autowired
    DashboardDao dashboardDao;
    
    public void add(DashboardGadget dashboardGadget) {
        dao.add(dashboardGadget);
    }

    public void deleteById(Integer dashboardGadgetId) {
        dao.codeDelete(dao.get(dashboardGadgetId));
    }

    public DashboardGadget getDashBoardGadget(Integer DashboardGadgetId) {
        return dao.get(DashboardGadgetId);
    }
    
    public List<DashboardGadget> getDashBoardGadgetsByDashboard(Integer dashboardId) {
    	
    	Set<Condition> set = new HashSet<Condition>();
    	Condition condition = new Condition();
    	condition.setField("dashboard");
    	condition.setRestrict(Restriction.EQ);
    	condition.setValue(new Object[]{dashboardId});
    	set.add(condition);
        return dao.findByConditions(set);
    }

    public List<DashboardGadget> getDashBoardGadgets() {
        return dao.getAll();
    }

    public void update(DashboardGadget dashboardGadget) {
        dao.update(dashboardGadget);
    }

	@SuppressWarnings("unchecked")
	public void add(Integer gadgetId, Integer dashboardId) {
		Gadget gadget = gadgetDao.get(gadgetId);
		Dashboard dashboard = dashboardDao.get(dashboardId);
		
		DashboardGadget dashboardGadget = new DashboardGadget();
		dashboardGadget.setGadget(gadget);
		dashboardGadget.setDashboard(dashboard);
		dashboardGadget.setLayout("fit");
		dashboardGadget.setCollapsible(false);
		
		List result = dao.getGrid(dashboardId);
//		int gridx, gridy;
//		if (result.size() > 0) {
//			Object[] resultData = (Object[]) result.get(result.size() - 1);
//			gridx = Integer.parseInt(resultData[0].toString());
//			gridy = Integer.parseInt(resultData[1].toString());
//		} else {
//			gridx = 0;
//			gridy = 0;
//		}
//		
//		if (gridx == dashboard.getMaxGridX()-1 && gridy < dashboard.getMaxGridY()-1) {
//			dashboardGadget.setGridX(0);
//			dashboardGadget.setGridY(++gridy);
//			dao.codeAdd(dashboardGadget);
//		} else if (gridx < dashboard.getMaxGridX()-1 && gridy <= dashboard.getMaxGridY()-1) {
//			dashboardGadget.setGridX(++gridx);
//			dashboardGadget.setGridY(gridy);
//			dao.codeAdd(dashboardGadget);
//		}
		// 가젯위치 오류 수정
		int gridx = 0;
		int gridy = 0;

		if (result.size() > 0) {
		    Object[] resultData = (Object[]) result.get(result.size() - 1);
		    gridx = Integer.parseInt(resultData[0].toString());
		    gridy = Integer.parseInt(resultData[1].toString());

		    if (gridx == dashboard.getMaxGridX()-1 && gridy < dashboard.getMaxGridY()-1) {
		        dashboardGadget.setGridX(0);
		        dashboardGadget.setGridY(++gridy);
		        dao.codeAdd(dashboardGadget);
		    } else if (gridx < dashboard.getMaxGridX()-1 && gridy <= dashboard.getMaxGridY()-1) {
		        dashboardGadget.setGridX(++gridx);
		        dashboardGadget.setGridY(gridy);
		        dao.codeAdd(dashboardGadget);
		    }
		} else {
            dashboardGadget.setGridX(gridx);
            dashboardGadget.setGridY(gridy);
            dao.codeAdd(dashboardGadget);
		}
	}

	public List<DashboardGadgetVO> getGadgetsByDashboard(Integer dashboardId) {
		return dao.getGadgetsByDashboard(dashboardId);
	}

	public void updatePosition(ArrayList<DashboardGadgetPositionVO> array) {
        for (int i=0; i<array.size(); i++) {
            DashboardGadgetPositionVO dashboardGadget = array.get(i);
            DashboardGadget tmpDashboardGadget = dao.get(Integer.parseInt(dashboardGadget.getUid()));
            tmpDashboardGadget.setGridX(dashboardGadget.getColumnIndex());
            tmpDashboardGadget.setGridY(dashboardGadget.getPosition());
            dao.update(tmpDashboardGadget);
        }
    }
}
