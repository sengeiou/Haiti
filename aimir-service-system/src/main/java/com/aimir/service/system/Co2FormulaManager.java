package com.aimir.service.system;

import org.apache.cxf.annotations.WSDLDocumentation;
import org.apache.cxf.annotations.WSDLDocumentationCollection;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import com.aimir.model.system.Co2Formula;

/**
 * 
 * @author goodjob
 *
 */
@WSDLDocumentation("Co2 Formula")
@WebService(name="Co2FormulaService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface Co2FormulaManager {

    /**
     * method name : getCo2FormulaBySupplyType
     * method Desc : 서비스타입(공급유형, 에너지타입) 코드 아이디를 이용하여 CO2 계산식을 가져온다.
     *
     * @param supplyTypeCodeId 서비스타입(공급유형, 에너지타입) 코드 아이디
     * @return @see com.aimir.model.system.Co2Formula
     */
	@WSDLDocumentationCollection({
			@WSDLDocumentation(value = "서비스타입(공급유형, 에너지타입) 코드 아이디를 이용하여 CO2 계산식을 가져온다.", 
					placement = WSDLDocumentation.Placement.BINDING_OPERATION),
			@WSDLDocumentation(value = "Co2FormulaInstance", 
					placement = WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT) })
	@WebMethod
	@WebResult(name = "Co2FormulaInstance")
	public Co2Formula getCo2FormulaBySupplyType(
			@WebParam(name="supplyTypeCodeId") Integer supplyTypeCodeId);
	
	/**
     * method name : add
     * method Desc : Co2Formula 정보를 추가
     * 
	 * @param co2formula
	 */
	@WSDLDocumentationCollection({
		@WSDLDocumentation(value = "Additional information Co2Formula.", 
				placement = WSDLDocumentation.Placement.BINDING_OPERATION)
	})
	@WebMethod
	public void add (
			@WebParam(name="co2formula") Co2Formula co2formula);
	
	/**
     * method name : update
     * method Desc : Co2Formula 정보를 업데이트
     * 
	 * @param co2formula
	 */
	@WSDLDocumentationCollection({
		@WSDLDocumentation(value = "Updated information Co2Formula", 
				placement = WSDLDocumentation.Placement.BINDING_OPERATION)
	})
	@WebMethod
	public void update(
			@WebParam(name="co2formula") Co2Formula co2formula);
	
	/**
     * method name : calculateCo2emission
     * method Desc :  탄소배출량 계산 : 사용량 * 발생량 탄소 배출량을 리턴 
     * 
	 * @param co2formula
	 * @return
	 */
	@WSDLDocumentationCollection({
		@WSDLDocumentation(value = "Calculation of carbon emissions: emissions of carbon emissions, multiply the return usage", 
				placement = WSDLDocumentation.Placement.BINDING_OPERATION),
		@WSDLDocumentation(value = "calculateCo2e", 
				placement = WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT) })
	@WebMethod
	@WebResult(name = "calculateCo2e")
	public double calculateCo2emission(
			@WebParam(name="co2formula") Co2Formula co2formula);
}
