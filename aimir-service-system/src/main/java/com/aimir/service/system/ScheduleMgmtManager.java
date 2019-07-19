package com.aimir.service.system;

import java.util.List;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
@WebService(name="ScheduleMgmtService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface ScheduleMgmtManager {
    /**
     * method name : getGroupComboDataByType<b/>
     * method Desc : Task Management 맥스가젯에서 선택한 GroupType 의 Group Combo Data 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
	@WebMethod
	@WebResult(name="GroupComboDataList")
    public List<Map<String, Object>> getGroupComboDataByType(
    		@WebParam(name="conditionMap") Map<String, Object> conditionMap);

    /**
     * method name : getGroupTypeByGroup<b/>
     * method Desc : Task Management 맥스가젯에서 선택한 Job 의 Group Type 을 조회한다.
     * 
     * @param conditionMap
     * @return
     */
	@WebMethod
	@WebResult(name="GroupTypeByGroup")
    public String getGroupTypeByGroup(
    		@WebParam(name="conditionMap") Map<String, Object> conditionMap);
}