package com.aimir.service.system;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import com.aimir.model.system.Dashboard;

@WebService(name="DashboardService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface DashboardManager {
	
	/**
     * method name : add
     * method Desc : Dashboard 정보를 추가
     * 
	 * @param dashboard
	 */
	@WebMethod
    public void add(
    		@WebParam(name="dashboard") Dashboard dashboard);
    
    /**
     * method name : update
     * method Desc : Dashboard 정보를 업데이트
     * 
     * @param dashboard
     */
	@WebMethod
    public void update(
    		@WebParam(name="dashboard") Dashboard dashboard);
    
    /**
     * method name : delete
     * method Desc : Dashboard 정보를 삭제한다.
     * 
     * @param dashboardId Dashboard.id
     */
	@WebMethod
    public void delete(
    		@WebParam(name="dashboardId") Integer dashboardId);
    
    /**
     * method name : getDashboard
     * method Desc : dashbordId에 해당하는 Dashboard 정보를 리턴한다.
     * 
     * @param dashboarId Dashboard.id
     * @return
     */
	@WebMethod
	@WebResult(name="DashboardInstance")
    public Dashboard getDashboard(
    		@WebParam(name="dashboarId") Integer dashboarId);
    
	/**
     * method name : getDashboardsByOperator
     * method Desc : operatorId 로  해당하는 대시보드 목록을 취득한다. operatorId는 Operator.id 정보가 된다.
     * 
	 * @param operatorId Operator.id
	 * @return List of Dashboard @see com.aimir.model.system.Dashboard
	 */
	@WebMethod
	@WebResult(name="DashboardsList")
    public List<Dashboard> getDashboardsByOperator(
    		@WebParam(name="operatorId") Integer operatorId);
    
    /**
     * method name : getDashboardsByRole
     * method Desc : roldId 로  해당하는 대시보드 목록을 취득한다. roldId는Role.id 정보가 된다.
     * 
     * @param roleId = Role.id
     * @return List of Dashboard @see com.aimir.model.system.Dashboard
     */
	@WebMethod
	@WebResult(name="DashboardsList")
    public List<Dashboard> getDashboardsByRole(
    		@WebParam(name="roleId") Integer roleId);
    
    /**
     * method name : getDashboardsByOperatorAndRole
     * method Desc : OperatorId, roldId 로  해당하는 대시보드 목록을 취득한다.
     * 
     * @param operatorId Operator.id
     * @param roleId Role.id
     * @return List of Dashboard @see com.aimir.model.system.Dashboard
     */
	@WebMethod
	@WebResult(name="DashboardsAndRoleList")
    public List<Dashboard> getDashboardsByOperatorAndRole(
    		@WebParam(name="operatorId") Integer operatorId, 
    		@WebParam(name="roleId") Integer roleId);
    
    /**
     * method name : checkDashboardCountByOperator
     * method Desc : operatorId 로  해당하는 대시보드가 있는지 체크한다. 
     * 
	 * @param operatorId Operator.id
     * @return 중복되는 것이 없으면 true를 리턴한다.
     */
	@WebMethod
	@WebResult(name="checkDashboardCount")
    public boolean checkDashboardCountByOperator(
    		@WebParam(name="operatorId") Integer operatorId);
}
