package com.aimir.service.system;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import com.aimir.model.system.DashboardGadget;
import com.aimir.model.system.DashboardGadgetPositionVO;
import com.aimir.model.system.DashboardGadgetVO;

/**
 * 
 * @author 최은정(ej8486)
 *
 */
@WebService(name="DashboardGadgetService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface DashboardGadgetManager {
	
	/**
	 * method name : getDashBoardGadgets
	 * method Desc : 전체 대시보드가젯 목록을 리턴한다.
	 * 
	 * @return List of DashboardGadget @see com.aimir.model.system.DashboardGadget
	 */
	@WebMethod
	@WebResult(name="DashBoardGadgetsList")
    public List<DashboardGadget> getDashBoardGadgets();
    
    /**
	 * method name : getDashBoardGadget
	 * method Desc : 대시보드 가젯 ID로 대시보드에 속한 가젯의 위치 정보및 가젯 정보들을 취득한다.
	 * 
     * @param DashboardGadgetId DashboardGadget.id
     * @return @see com.aimir.model.system.DashboardGadget
     */
	@WebMethod
	@WebResult(name="DashBoardGadgetInstance")
    public DashboardGadget getDashBoardGadget(
    		@WebParam(name="customerId") Integer DashboardGadgetId);
    
    /**
	 * method name : getDashBoardGadgetsByDashboard
	 * method Desc : 대시보드 ID로 대시보드에 속한 가젯의 위치 정보및 가젯 정보들을 취득한다.
	 * 
     * @param dashboardId Dashboard.id
     * @return List of DashboardGadget @see com.aimir.model.system.DashboardGadget
     */
	@WebMethod
	@WebResult(name="DashBoardGadgetsList")
    public List<DashboardGadget> getDashBoardGadgetsByDashboard(
    		@WebParam(name="dashboardId") Integer dashboardId);
    
	/**
	 * method name : getGadgetsByDashboard
	 * method Desc : 대시보드 ID로 대시보드에 속한 가젯의 위치 정보및 가젯 정보들을 취득한다.
	 *
	 * @param dashboardId  Dashboard.id
	 * @return List of DashboardGadgetVO @see com.aimir.model.system.DashboardGadgetVO
	 */
	@WebMethod
	@WebResult(name="GadgetsByDashboardList")
    public List<DashboardGadgetVO> getGadgetsByDashboard(
    		@WebParam(name="dashboardId") Integer dashboardId);
    
    /**
	 * method name : add
	 * method Desc : dashboard에 가젯을 신규 추가한다. 좌표 위치는 자동으로 설정하게끔 로직에서 처리한다.
	 *               새로운 관계가 생성된다. 즉 DashboardGadget entity가 추가된다.
	 * 
     * @param gadgetId Gadget.id
     * @param dashboardId Dashboard.id
     */
	@WebMethod(operationName ="addGadget")
    public void add(
    		@WebParam(name="gadgetId") Integer gadgetId, 
    		@WebParam(name="dashboardId")Integer dashboardId);
    
    /**
	 * method name : add
	 * method Desc : dashboard에 가젯을 신규 추가한다. 좌표 위치는 파라미터에서 입력한 정보대로 생성된다.
	 *                즉 DashboardGadget entity가 추가된다.
	 *               
     * @param dashboardGadget
     */
	@WebMethod(operationName ="addDashboardGadget")
    public void add(
    		@WebParam(name="dashboardGadget") DashboardGadget dashboardGadget);
    
    /**
	 * method name : update
	 * method Desc : DashboardGadget entity를 갱신한다.
	 *                
     * @param dashboardGadget
     */
	@WebMethod
    public void update(
    		@WebParam(name="dashboardGadget") DashboardGadget dashboardGadget);
    
    /**
	 * method name : deleteById
	 * method Desc : DashboardGadget 을 삭제한다. 즉 Dashboard에 가젯이 삭제되므로 UI상에서 Gadget이 안보이게 된다.
	 * 
     * @param dashboardGadgetId DashboardGadget.id
     */
	@WebMethod
    public void deleteById(
    		@WebParam(name="dashboardGadgetId") Integer dashboardGadgetId);
    
    /**
	 * method name : updatePosition
	 * method Desc : 대시보드상에 가젯들의 좌표 위치를 다시 정리하여 업데이트한다.
	 * 
     * @param array
     */
	@WebMethod
    public void updatePosition(
    		@WebParam(name="array") ArrayList<DashboardGadgetPositionVO> array);
}