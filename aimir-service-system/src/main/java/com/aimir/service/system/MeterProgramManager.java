/**
 * MeterProgramManager.java Copyright NuriTelecom Limited 2011
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
 * MeterProgramManager.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2012. 1. 30.  v1.0        문동규   TOU Profile Service
 * 2012. 2. 24.  v1.0        문동규   Meter Program 으로 이름 변경         
 * </pre>
 */
@WSDLDocumentation("Meter Program Setting, Program Management")
@WebService(name="MeterProgramService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface MeterProgramManager {
    
    /**
     * method name : getMeterProgramLogListTotalCount<b/>
     * method Desc : Vendor Model 맥스가젯의 Meter Program 탭에서 Meter Program Log 리스트의 total count 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
	@WebMethod
	@WebResult(name="MeterProgramLogListTotalCount")
	@Deprecated
    public Integer getMeterProgramLogListTotalCount(
    		@WebParam(name ="conditionMap")Map<String, Object> conditionMap);

    /**
     * method name : getMeterProgramLogList<b/>
     * method Desc : Vendor Model 맥스가젯의 Meter Program 탭에서 Meter Program Log 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
	@WebMethod
	@WebResult(name="MeterProgramLogList")
	@Deprecated
    public List<Map<String, Object>> getMeterProgramLogList(
    		@WebParam(name ="conditionMap")Map<String, Object> conditionMap);

    /**
     * method name : getMeterProgramLogListTotalCountRenew<b/>
     * method Desc : Vendor Model 맥스가젯의 Meter Program 탭에서 Meter Program Log 리스트의 total count 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @WebMethod
    @WebResult(name="MeterProgramLogListTotalCountRenew")
    public Integer getMeterProgramLogListTotalCountRenew(
            @WebParam(name ="conditionMap")Map<String, Object> conditionMap);

    /**
     * method name : getMeterProgramLogListRenew<b/>
     * method Desc : Vendor Model 맥스가젯의 Meter Program 탭에서 Meter Program Log 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @WebMethod
    @WebResult(name="MeterProgramLogListRenew")
    public List<Map<String, Object>> getMeterProgramLogListRenew(
            @WebParam(name ="conditionMap")Map<String, Object> conditionMap);

    /**
     * method name : getMeterProgramSettingsData<b/>
     * method Desc : Vendor Model 맥스가젯의 Meter Program 탭에서 Settings 값을 조회한다.
     *
     * @param conditionMap
     * @return
     */
	@WebMethod
	@WebResult(name="MeterProgramSettingsData")
    public String getMeterProgramSettingsData(
    		@WebParam(name ="conditionMap")Map<String, Object> conditionMap);

    /**
     * method name : saveMeterProgram<b/>
     * method Desc : Vendor Model 맥스가젯의 Meter Program 탭에서 Meter Program 정보를 저장한다.
     *
     * @param conditionMap
     * @throws Exception 
     */
	@WebMethod
    public void saveMeterProgram(
    		@WebParam(name ="conditionMap")Map<String, Object> conditionMap) throws Exception;
}