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

import org.apache.cxf.annotations.WSDLDocumentation;
import org.apache.cxf.annotations.WSDLDocumentationCollection;

@WSDLDocumentation(value = "AuditLog to obtain information related to the class of service" , placement=WSDLDocumentation.Placement.TOP)
@WebService(name="AuditLogService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface AuditLogManager {

    /**
     * method name : getAuditLogRankingList
     * method Desc : ChangeLog 미니가젯의 AuditLog Ranking 리스트를 조회한다.
     *
     * @param conditionMap
	 * <ul>
	 * <li> startDate : Calendar
	 * <li> endDate : Calendar
	 * <li> page :page number
	 * <li> limit : max limit
	 * </ul>
	 *         
     * @param isCount total count 여부
     * @return List of Map if isCount is true then  return {total, count}
     *                     else return {
     *                      entityName,
     *                      propertyName,
     *                      action, 
     *                      count
     *                     }
     */
	@WSDLDocumentationCollection({
			@WSDLDocumentation(value="AuditLog Ranking list is viewed." , 
					placement=WSDLDocumentation.Placement.BINDING_OPERATION),
			@WSDLDocumentation(value ="AuditLogRankingList object", 
					placement=WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)
		})
	@WebMethod
	@WebResult(name="AuditLogRackingList") 
    public List<Map<String, Object>> getAuditLogRankingList(
    		@WebParam(name="conditionMap") Map<String, Object> conditionMap);
		
    /**
     * method name : getAuditLogRankingListTotalCount
     * method Desc : ChangeLog 미니가젯의 AuditLog Ranking 리스트의 Total Count를 조회한다.
     *
     * @param conditionMap
	 * <ul>
	 * <li> action : action
	 * <li> equipType : Calendar
	 * <li> equipName :page number
	 * <li> propertyName : property Name (Entity's property name)
	 * <li> startDate : yyyymmdd
	 * <li> endDate : yyyymmdd
	 * </ul>
     * @return {total,count}
     */
		
	@WSDLDocumentationCollection({
		@WSDLDocumentation(value="Total Count of AuditLog Ranking list will look up." , 
				placement=WSDLDocumentation.Placement.BINDING_OPERATION),
		@WSDLDocumentation(value ="AuditLogRacking Count", 
		placement=WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)
	})
	@WebMethod
	@WebResult(name="AuditLogRackingCount")
    public Integer getAuditLogRankingListTotalCount(
    		@WebParam(name="conditionMap") Map<String, Object> conditionMap);

    /**
     * method name : getAuditLogList
     * method Desc : ChangeLog 맥스가젯의 AuditLog 리스트를 조회한다.
     *
     * @param conditionMap
	 * <ul>
	 * <li> action : action
	 * <li> equipType : Calendar
	 * <li> equipName :page number
	 * <li> propertyName : property Name (Entity's property name)
	 * <li> startDate : yyyymmdd
	 * <li> endDate : yyyymmdd
	 * <li> page :page number
	 * <li> limit : max limit
	 * </ul>
     * @return List of Map  {
     *                       action,  
     *                       createdDate,  
     *                       entityName,  
     *                       equipName,  
     *                       propertyName, 
     *                       previousState,  
     *                       currentState
     *                      }
     */
	@WSDLDocumentationCollection({
		@WSDLDocumentation(value="AuditLog list will look up." , 
				placement=WSDLDocumentation.Placement.BINDING_OPERATION),
		@WSDLDocumentation(value ="AuditLog List", 
				placement=WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)
	})
	@WebMethod
	@WebResult(name="AuditLogList")
    public List<Map<String, Object>> getAuditLogList(
    		@WebParam(name="conditionMap") Map<String, Object> conditionMap);

    /**
     * method name : getAuditLogListTotalCount
     * method Desc : ChangeLog 맥스가젯의 AuditLog 리스트의 Total Count를 조회한다.
     *
     * @param conditionMap
	 * <ul>
	 * <li> action : action
	 * <li> equipType : Calendar
	 * <li> equipName :page number
	 * <li> propertyName : property Name (Entity's property name)
	 * <li> startDate : yyyymmdd
	 * <li> endDate : yyyymmdd
	 * </ul>
     * @return {total,count}
     */
	@WSDLDocumentationCollection({
		@WSDLDocumentation(value="Total Count of AuditLog list will look up." , 
				placement=WSDLDocumentation.Placement.BINDING_OPERATION),
		@WSDLDocumentation(value ="AuditLog Count", 
				placement=WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)
	})
	@WebMethod
	@WebResult(name="AuditLogCount")
    public Integer getAuditLogListTotalCount(
    		@WebParam(name="conditionMap") Map<String, Object> conditionMap);
}