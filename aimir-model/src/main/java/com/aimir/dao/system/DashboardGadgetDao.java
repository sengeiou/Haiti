package com.aimir.dao.system;

import java.util.List;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.DashboardGadget;
import com.aimir.model.system.DashboardGadgetVO;

public interface DashboardGadgetDao extends GenericDao<DashboardGadget, Integer> {
	
	/**
	 * method name : getGrid
	 * method Desc : 대시보드 ID로 대시보드에 속한 그리드의 정보들을 취득한다.
	 * 
	 * @param dashboardId Dashboard.id
	 * @return
	 */
	public List<?> getGrid(Integer dashboardId);
	
	/**
	 * method name : getGadgetsByDashboard
	 * method Desc : 대시보드 ID로 대시보드에 속한 가젯의 위치 정보및 가젯 정보들을 취득한다.
	 *
	 * @param dashboardId  Dashboard.id
	 * @return List of DashboardGadgetVO @see com.aimir.model.system.DashboardGadgetVO
	 */
	public List<DashboardGadgetVO> getGadgetsByDashboard(Integer dashboardId);
	
	/**
	 * method name : getDashboardGadgetByDashboardIdGadgetId
	 * method Desc : 대시보드 ID, 가젯 ID로 대시보드가젯 정보를 취득한다.
	 *
	 * @param dashboardId 대시보드 ID Dashboard.id
	 * @param gadgetId 가젯 ID Gadget.id
	 * @return List of DashboardGadget @see com.aimir.model.system.DashboardGadget
	 */
	public List<DashboardGadget> getDashboardGadgetByDashboardIdGadgetId(int dashboardId, int gadgetId);
}
