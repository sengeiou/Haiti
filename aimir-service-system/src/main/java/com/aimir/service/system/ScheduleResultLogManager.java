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
@WebService(name="ScheduleResultLogService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface ScheduleResultLogManager {

    @WebMethod
    @WebResult(name="ScheduleResultLogList")
    public List<Map<String, Object>> getScheduleResultLogByJobName(
            @WebParam(name="conditionMap") Map<String, Object> conditionMap);

    @WebMethod
    @WebResult(name="ScheduleResultLog")
    public Integer getScheduleResultLogByJobNameCount(
            @WebParam(name="conditionMap") Map<String, Object> conditionMap);

    @WebMethod
    @WebResult(name="LatestScheduleResultLog")
    @Deprecated
    public Map<String, Object> getLatestScheduleResultLogByTrigger(
            @WebParam(name="triggerName") String triggerName);

    @WebMethod
    @WebResult(name="LatestScheduleResultLogByJobTrigger")
    public Map<String, Object> getLatestScheduleResultLogByJobTrigger(
            @WebParam(name="conditionMap") Map<String, Object> conditionMap);
}