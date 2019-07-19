package com.aimir.dao.system;

import java.util.List;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.Dashboard;

public interface DashboardDao extends GenericDao<Dashboard, Integer> {
	
	/**
     * method name : getDashboardsByOperator
     * method Desc : operatorId 로  해당하는 대시보드 목록을 취득한다. operatorId는 Operator.id 정보가 된다.
     * 
	 * @param operatorId Operator.id
	 * @return List of Dashboard @see com.aimir.model.system.Dashboard
	 */
    public List<Dashboard> getDashboardsByOperator(Integer operatorId);
    
    /**
     * method name : getDashboardsByRole
     * method Desc : roldId 로  해당하는 대시보드 목록을 취득한다. roldId는Role.id 정보가 된다.
     * 
     * @param roleId = Role.id
     * @return List of Dashboard @see com.aimir.model.system.Dashboard
     */
    public List<Dashboard> getDashboardsByRole(Integer roleId);
    
    /**
     * method name : checkDashboardCountByOperator
     * method Desc : operatorId 로  해당하는 대시보드가 있는지 체크한다. 
     * 
	 * @param operatorId Operator.id
     * @return
     */
    public boolean checkDashboardCountByOperator(Integer operatorId);
    
    /**
     * method name : getDashboardByName
     * method Desc : name에 해당하는 대시보드 정보를 취득한다.
     *               
     * @param name 기초데이터 등록시 설정한 대시보드명
     * @return List of Dashboard @see com.aimir.model.system.Dashboard
     */
    public List<Dashboard> getDashboardByName(String name);
    
    /**
     * method name : getDashboardByNameOpeatorId
     * method Desc : 대시보드명과 로그인 유저로 대시보드정보를 취득한다.
     *
     * @param name 대시보드 명
     * @param operatorId 로그인 유저아이디
     * @return List of Dashboard @see com.aimir.model.system.Dashboard
     */
    public List<Dashboard> getDashboardByNameOpeatorId(String name, int operatorId);
}
