/**
 * ReportMgmtManager.java Copyright NuriTelecom Limited 2011
 */
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

/**
 * ReportMgmtManager.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 9. 16.   v1.0       문동규   리포트관리 Service
 *
 */
@WSDLDocumentation("Report File, Create Management")
@WebService(name="ReportMgmtService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface ReportMgmtManager {

    /**
     * method name : getReportResultList
     * method Desc : Report 관리 미니가젯에서 스케줄 실행 결과 리스트의 total count를 조회한다.
     *
     * @param conditionMap
     * @return
     */
	@WebMethod
	@WebResult(name="ReportResultListTotalCount")
    public Integer getReportResultListTotalCount(
    		@WebParam(name ="conditionMap")Map<String, Object> conditionMap);

    /**
     * method name : getReportResultList<br/>
     * method Desc : Report 관리 미니가젯에서 스케줄 실행 결과 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
	@WebMethod
	@WebResult(name="ReportResultList")
    public List<Map<String, Object>> getReportResultList(
    		@WebParam(name ="conditionMap")Map<String, Object> conditionMap);

    /**
     * method name : deleteReportResult
     * method Desc : Report 관리 미니가젯에서 스케줄 실행 결과를 삭제한다.
     *
     * @param conditionMap
     */
	@WebMethod
    public void deleteReportResult(
    		@WebParam(name ="conditionMap")Map<String, Object> conditionMap);

    /**
     * method name : getReportTreeData
     * method Desc : Report 관리 화면에서 Report Tree 데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
	@WebMethod
	@WebResult(name="ReportTreeDataList")
    public List<Map<String, Object>> getReportTreeData(
    		@WebParam(name ="conditionMap")Map<String, Object> conditionMap);

    /**
     * method name : getReportContactsGroupComboData
     * method Desc : Report 관리에서 Email Contacts Group Combo Data 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
	@WebMethod
	@WebResult(name="ReportContactsGroupComboDataList")
    public List<Map<String, Object>> getReportContactsGroupComboData(
    		@WebParam(name ="conditionMap")Map<String, Object> conditionMap);

    /**
     * method name : getReportContactsListTotalCount
     * method Desc : Report 관리에서 Email Contacts 리스트의 total count를 조회한다.
     *
     * @param conditionMap
     * @return
     */
	@WebMethod
	@WebResult(name="ReportContactsListTotalCount")
    public Integer getReportContactsListTotalCount(
    		@WebParam(name ="conditionMap")Map<String, Object> conditionMap);

    /**
     * method name : getReportContactsList
     * method Desc : Report 관리에서 Email Contacts 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
	@WebMethod
	@WebResult(name="ReportContactsList")
    public List<Map<String, Object>> getReportContactsList(
    		@WebParam(name ="conditionMap")Map<String, Object> conditionMap);

    /**
     * method name : insertReportContactsGroup
     * method Desc : Report 관리 화면의 Email Contacts Group 정보를 등록한다.
     *
     * @param conditionMap
     * @return
     */
	@WebMethod
    public void insertReportContactsGroup(
    		@WebParam(name ="conditionMap")Map<String, Object> conditionMap);

    /**
     * method name : updateReportContactsGroup
     * method Desc : Report 관리 미니가젯의 Email Contacts Group 정보를 수정한다.
     *
     * @param conditionMap
     * @return
     */
	@WebMethod
    public void updateReportContactsGroup(
    		@WebParam(name ="conditionMap")Map<String, Object> conditionMap);

    /**
     * method name : deleteReportContactsGroup
     * method Desc : Report 관리 화면의 Email Contacts Group 정보를 삭제한다.
     *
     * @param conditionMap
     */
	@WebMethod
    public void deleteReportContactsGroup(
    		@WebParam(name ="conditionMap")Map<String, Object> conditionMap);

    /**
     * method name : insertReportContactsData
     * method Desc : Report 관리 미니가젯의 Email Contacts 정보를 등록한다.
     *
     * @param conditionMap
     * @return
     */
	@WebMethod
    public void insertReportContactsData(
    		@WebParam(name ="conditionMap")Map<String, Object> conditionMap);

    /**
     * method name : updateReportContactsData
     * method Desc : Report 관리 미니가젯의 Email Contacts 정보를 수정한다.
     *
     * @param conditionMap
     * @return
     */
	@WebMethod
    public void updateReportContactsData(
    		@WebParam(name ="conditionMap")Map<String, Object> conditionMap);

    /**
     * method name : deleteReportContactsData
     * method Desc : Report 관리 미니가젯의 Email Contacts 정보를 삭제한다.
     *
     * @param conditionMap
     * @return
     */
	@WebMethod
	@WebResult(name="ReportContactsData")
    public Integer deleteReportContactsData(
    		@WebParam(name ="conditionMap")Map<String, Object> conditionMap);

    /**
     * method name : insertReportSchedule
     * method Desc : Report 관리 미니가젯에서 Report Schedule 정보를 등록한다.
     *
     * @param conditionMap
     * @return
     */
	@WebMethod
    public void insertReportSchedule(
    		@WebParam(name ="conditionMap")Map<String, Object> conditionMap);

    /**
     * method name : updateReportSchedule
     * method Desc : Report 관리 맥스가젯에서 Report Schedule 정보를 수정한다.
     *
     * @param conditionMap
     * @return
     */
	@WebMethod
    public void updateReportSchedule(
    		@WebParam(name ="conditionMap")Map<String, Object> conditionMap);

    /**
     * method name : deleteReportSchedule
     * method Desc : Report 관리 화면에서 Report Schedule 정보를 삭제한다.
     *
     * @param conditionMap
     */
	@WebMethod
    public void deleteReportSchedule(
    		@WebParam(name ="conditionMap")Map<String, Object> conditionMap);

    /**
     * method name : getReportScheduleListTotalCount
     * method Desc : Report 관리 맥스가젯에서 스케줄 리스트의 total count를 조회한다.
     *
     * @param conditionMap
     * @return
     */
	@WebMethod
	@WebResult(name="ReportScheduleListTotalCount")
    public Integer getReportScheduleListTotalCount(
    		@WebParam(name ="conditionMap")Map<String, Object> conditionMap);

    /**
     * method name : getReportScheduleList
     * method Desc : Report 관리 맥스가젯에서 스케줄 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
	@WebMethod
	@WebResult(name="ReportScheduleList")
    public List<Map<String, Object>> getReportScheduleList(
    		@WebParam(name ="conditionMap")Map<String, Object> conditionMap);

    /**
     * method name : getReportScheduleResultListTotalCount
     * method Desc : Report 관리 미니가젯에서 스케줄 실행 결과 리스트의 total count를 조회한다.
     *
     * @param conditionMap
     * @return
     */
	@WebMethod
	@WebResult(name="ReportScheduleResultListTotalCount")
    public Integer getReportScheduleResultListTotalCount(
    		@WebParam(name ="conditionMap")Map<String, Object> conditionMap);

    /**
     * method name : getReportScheduleResultList
     * method Desc : Report 관리 미니가젯에서 스케줄 실행 결과 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
	@WebMethod
	@WebResult(name="ReportScheduleResultList")
    public List<Map<String, Object>> getReportScheduleResultList(
    		@WebParam(name ="conditionMap")Map<String, Object> conditionMap);

    /************************************************************************************************************************************************
     **** TEST REPORT_RESULT INSERT START ***********************************************************************************************************
     ************************************************************************************************************************************************
    public void testInsertReportScheduleResult(Map<String, Object> conditionMap);
    ************************************************************************************************************************************************
     **** TEST REPORT_RESULT INSERT END *************************************************************************************************************
     ************************************************************************************************************************************************/

}