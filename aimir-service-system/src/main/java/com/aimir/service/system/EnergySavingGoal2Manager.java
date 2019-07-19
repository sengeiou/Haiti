package com.aimir.service.system;

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

@WSDLDocumentation("Savings target to save")
@WebService(name="EnergySavingGoal2Service", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface EnergySavingGoal2Manager {
	
	/**
	 * 기준일과 기준일에 대한 절감 목표값 저장.
	 * @param params
	 * @return
	 */

	public Map<String, Object> setEnergySavingGoal2( Map<String, Object> params);
	
	@WSDLDocumentationCollection(
		    {
		        @WSDLDocumentation(value = "Savings target to save",	
									placement = WSDLDocumentation.Placement.BINDING_OPERATION),
		        @WSDLDocumentation(value = "Parameters 'params' description \r\n"
							+ " ($key=supplierId,$valueType=String,$description=Supplier.id) \r\n"
							+ " ($key=energyType,$valueType=String,$description=Energy Type(Electricity:'0',Gas:'1',Water:'2') \r\n"
							+ " ($key=savingGoalDateType,$valueType=String,$description=HOURLY('0'), DAILY('1'),PERIOD('2'),WEEKLY('3'), MONTHLY('4'), MONTHLYPERIOD('5'),WEEKDAILY('6'),SEASONAL('7'),YEARLY('8'),QUARTERLY('9')) \r\n"
							+ " ($key=savingGoalStartDate,$valueType=String,$description= Application Date yyyyMMdd) \r\n"
							+ " ($key=savingGoal,$valueType=Double,$description=Savings targets)",
		                           placement = WSDLDocumentation.Placement.BINDING_OPERATION_INPUT),
		        @WSDLDocumentation(value = "Return Map \r\n"
							+ " ($key=result, $valueType=String, @description='Y' saved, 'E' empty, 'N' failed saving)",
		        					placement = WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)
		    }
	)	
	@WebMethod
	@WebResult(name="EnergySavingGoal2Map")
	public Map<String, Object> setEnergySavingGoal2ByParam(
			@WebParam(name="Supplier.id") String supplierId,
			@WebParam(name="energyType") String energyType,
			@WebParam(name="savingGoalDateType") String savingGoalDateType,
			@WebParam(name="yyyyMMdd") String savingGoalStartDate,
			@WebParam(name="savingGoal") Double savingGoal
			);
	
	
	/**
	 * supplierId , 일/주/월/년 주기별 , 기준일에 따른 에너지 절감 목표 및 비교기간의 평균값 정보를 조회한다.
	 * @param condition
	 * @return
	 */
	@WebMethod
	@WebResult(name="EnergySavingGoal2InfoMap")
	public Map<String, Object> getEnergySavingGoal2Info(
			@WebParam(name="params") Map<String, Object> params);

	/**
	 * 년도별 검침정보의 합( 전기 , 가스 , 수도 , 총사용량 )
	 * @param params
	 * @return
	 */
	@WebMethod
	@WebResult(name="EnergySavingGoal2YearsMap")
	public Map<String, Object> getEnergySavingGoal2YearsUsed(
			@WebParam(name="params") Map<String, Object> params);
	/**
	 * 몇년간 평균사용량
	 * @param params
	 * @return
	 */
	@WebMethod
	@WebResult(name="EnergySavingGoal2AvgYears")
	public Map<String, Object> getEnergySavingGoal2AvgYearsUsed(
			@WebParam(name="params") Map<String, Object> params);

	/**
	 * 등록한 평균관리 내역 리스트를 조회한다.
	 * @param params
	 * @return
	 */
	@WebMethod
	@WebResult(name="EnergySavingGoal2SaveInfoMap")
	public Map<String, Object> getEnergySavingGoal2SaveInfoList(
			@WebParam(name="params") Map<String, Object> params);
	
	/**
	 * 등록한 목표 내역 리스트를 조회한다.
	 * @param params
	 * @return
	 */
	@WebMethod
	@WebResult(name="EnergySavingGoal2GoalMap")
	public Map<String, Object> getEnergySavingGoal2GoalList(
			@WebParam(name="params") Map<String, Object> params);
	
	/**
	 * 평균 관리 항목을 추가한다. 
	 * 평균관리의 설명 , 사용유무 , 평균에 활용된 년도
	 * @param params
	 * @return
	 */
	@WebMethod
	@WebResult(name="EnergyAvg2Map")
	public Map<String, Object> setEnergyAvg2(
			@WebParam(name="params") Map<String, Object> params);
	
	
	/**
	 * 등록한 목표 내역 리스트를 조회한다.
	 * Max 목표관리 탭의 오른쪽 목표 이력 리스트 
	 * @param params
	 * @return
	 */
	@WebMethod
	@WebResult(name="EnergySavingGoal2GoalMap2")
	public Map<String, Object> getEnergySavingGoal2GoalList2(
			@WebParam(name="params") Map<String, Object> params);
	
	
	/**
	 * @param params
	 * @return
	 */
	@WebMethod
	@WebResult(name="DayMonthRangeAvgMap")
	public Map<String, Object> getDayMonthRangeAvg(
			@WebParam(name="condition") Map<String, String> condition);
	
	/**
	 * @param params
	 * @return
	 */
	@WebMethod
	@WebResult(name="DayMonthRangeAvgMap")
	public Map<String, Object> getDayMonthRangeAvgByUsed(
			@WebParam(name="condition") Map<String, String> condition);
	
	/**
	 * method name : getAvgByUsed
	 * method desc : 해당 location에 맞는 전체 사용량을 구해서 각 기간별로 평균을 구한다.
	 * 
	 * @param params
	 * @return
	 */
	@WebMethod
	@WebResult(name="AvgByUsed")
	public Map<String, Object> getAvgByUsed(
			@WebParam(name="condition") Map<String, String> condition);
	
}