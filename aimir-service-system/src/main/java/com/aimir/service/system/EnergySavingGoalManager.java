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

@WSDLDocumentation("Savings target to save")
@WebService(name="EnergySavingGoalService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface EnergySavingGoalManager {
	
	/**
	 * 기준일과 기준일에 대한 절감 목표값 저장.
	 * @param params
	 * @return
	 */

	public Map<String, Object> setEnergySavingGoal(Map<String, Object> params);
	
	@WSDLDocumentationCollection(
		    {
		        @WSDLDocumentation(value = "Savings target to save",	
									placement = WSDLDocumentation.Placement.BINDING_OPERATION),
		        @WSDLDocumentation(value = "Parameters 'params' description \r\n"
							+ " ($key=supplierId,$valueType=String,$description=Supplier.id) \r\n"
							+ " ($key=savingGoal,$valueType=Double,$description=Savings targets) \r\n"
							+ " ($key=savingGoalStartDate,$valueType=String,$description= Application Date yyyyMMdd)",
		                           placement = WSDLDocumentation.Placement.BINDING_OPERATION_INPUT),
		        @WSDLDocumentation(value = "Return Map \r\n"
							+ " ($key=result, $valueType=String, @description='Y' saved, 'E' empty, 'N' failed saving)",
		        					placement = WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)
		    }
	)	
	@WebMethod
	@WebResult(name="EnergySavingGoalMap")
	public Map<String, Object> setEnergySavingGoalByParam(
			@WebParam(name="Supplier.id") String supplierId,
			@WebParam(name="savingGoal") Double savingGoal,
			@WebParam(name="savingGoalStartDate") String savingGoalStartDate
			);
	/**
	 * supplierId , 일/주/월/년 주기별 , 기준일에 따른 에너지 절감 목표 및 비교기간의 평균값 정보를 조회한다.
	 * @param condition
	 * @return
	 */

	public Map<String, Object> getEnergySavingGoalInfo(Map<String, Object> params);
	
	@WSDLDocumentationCollection(
		    {
		        @WSDLDocumentation(value = "Get energy saving goals and average usage by parameters(Period Type: Daily/weekly/monthly/yearly)",	
									placement = WSDLDocumentation.Placement.BINDING_OPERATION),
		        @WSDLDocumentation(value = "Parameters 'params' description \r\n"
							+ " ($key=searchStartDate,$valueType=String,$description=yyyyMMdd) \r\n"
							+ " ($key=searchEndDate,$valueType=String,$description=yyyyMMdd) \r\n"
							+ " ($key=searchDateType,$valueType=String,$description=HOURLY('0'), DAILY('1'),PERIOD('2'),WEEKLY('3'), MONTHLY('4'), MONTHLYPERIOD('5'),WEEKDAILY('6'),SEASONAL('7'),YEARLY('8'),QUARTERLY('9')) \r\n"
							+ " ($key=supplierId,$valueType=String,$description= Supplier.id) \r\n"
							+ " ($key=savingGoal,$valueType=String,$description=Savings targets) \r\n"
							+ " ($key=savingGoalStartDate,$valueType=String,$description=The base date)",
		                           placement = WSDLDocumentation.Placement.BINDING_OPERATION_INPUT),
		        @WSDLDocumentation(value = "Return Map \r\n"
							+ " ($key=info, $valueType=ArrayList<Map<String,Object>>, @description=list of map) \r\n"
							+ " Description of 'info' \r\n"
							+ " ($key=0, $valueType=Map<String, Object>, @description=Average information) \r\n"
							+ " ($key=1, $valueType=Map<String, Object>, @description=Current Energy Information) \r\n"
							+ " ($key=2, $valueType=Map<String, Object>, @description=Previous Energy Information) \r\n"
							+ " ($key=3, $valueType=Map<String, Object>, @description=Previous Year Energy Information) \r\n"
							+ " Description of $key(0~4) data type is same. \r\n"
							+ " ($key=gubun, $valueType=String, @description=Not currently used) \r\n"
							+ " ($key=searchDateType, $valueType=String, @description=HOURLY('0'), DAILY('1'),PERIOD('2'),WEEKLY('3'), MONTHLY('4'), MONTHLYPERIOD('5'),WEEKDAILY('6'),SEASONAL('7'),YEARLY('8'),QUARTERLY('9')) \r\n"
							+ " ($key=yearsSize, $valueType=String, @description=Number of years) \r\n"
							+ " ($key=energyUse, $valueType=String, @description=Energy Consumption with unit) \r\n"
							+ " ($key=co2Use, $valueType=String, @description=Co2 emission with unit) \r\n"
							+ " ($key=energyUseNum, $valueType=String, @description=Energy Consumption ) \r\n"
							+ " ($key=co2UseNum, $valueType=String, @description=Co2 emission) \r\n"
							+ " ($key=saving, $valueType=String, @description=Savings percent) \r\n"
							+ " ($key=savingGoal, $valueType=String, @description=Saving Goal) \r\n"
							+ " ($key=savingGoalStartDate, $valueType=String, @description=yyyyMMdd)",
		        					placement = WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)
   					
		    }
	)	
	@WebMethod
	@WebResult(name="EnergySavingGoalInfoMap")	
	public Map<String, Object> getEnergySavingGoalInfoByParam(
			@WebParam(name="searchStartDate") String searchStartDate,
			@WebParam(name="searchEndDate") String searchEndDate,
			@WebParam(name="searchDateType") String searchDateType,
			@WebParam(name="Supplier.id") String supplierId,
			@WebParam(name="savingGoal") String savingGoal,
			@WebParam(name="savingGoalStartDate") String savingGoalStartDate
	);


	/**
	 * 년도별 검침정보의 합( 전기 , 가스 , 수도 , 총사용량 )
	 * @param params
	 * @return
	 */
	public Map<String, Object> getEnergySavingGoalYearsUsed(Map<String, Object> params);
	
	@WSDLDocumentationCollection(
		    {
		        @WSDLDocumentation(value = "Yearly sum of metering information (electricity, gas, water, Total consumption)",	
									placement = WSDLDocumentation.Placement.BINDING_OPERATION),
		        @WSDLDocumentation(value = "Parameters 'params' description \r\n"
							+ " ($key=searchStartDate,$valueType=String,$description=yyyyMMdd) \r\n"
							+ " ($key=searchEndDate,$valueType=Double,$description=yyyyMMdd) \r\n"
							+ " ($key=supplierId,$valueType=String,$description= Supplier.id)",
		                           placement = WSDLDocumentation.Placement.BINDING_OPERATION_INPUT),
		        @WSDLDocumentation(value = "Return Map \r\n"
							+ " ($key=yearsUsedList, $valueType=List<HashMap<String,Object>>, @description=Yearly sum of metering information) \r\n"
							+ " Description of 'yearsUsedList'  \r\n"
							+ " ($key=year, $valueType=String , @description=yyyy ) \r\n"
							+ " ($key=yearStr, $valueType=String , @description=yyyy ) \r\n"
							+ " ($key=checked, $valueType=Boolean, @description= checked true or false) \r\n"
							+ " ($key=emUsed, $valueType=String , @description= Electric Energy Consumption with unit) \r\n"
							+ " ($key=gmUsed, $valueType=String , @description= Gas Energy Consumption with unit) \r\n"
							+ " ($key=wmUsed, $valueType=String , @description= Water Energy Consumption with unit) \r\n"
							+ " ($key=totalUsed, $valueType=String , @description=Total Energy Consumption with TOE )",
									placement = WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)
   					
		    }
	)
	@WebMethod
	@WebResult(name="EnergySavingYearsUsedMap")
	public Map<String, Object> getEnergySavingGoalYearsUsedByParam(
			@WebParam(name="yyyyMMdd") String searchStartDate,
			@WebParam(name="yyyyMMdd") String searchEndDate,
			@WebParam(name="Supplier.id") String supplierId
			);


	/**
	 * 몇년간 평균사용량
	 * @param params
	 * @return
	 */
	
	public Map<String, Object> getEnergySavingGoalAvgYearsUsed(Map<String, Object> params);

	@WSDLDocumentationCollection(
		    {
		        @WSDLDocumentation(value = "The average annual usage)",	
									placement = WSDLDocumentation.Placement.BINDING_OPERATION),
		        @WSDLDocumentation(value = "Parameters 'params' description \r\n"
							+ " ($key=years,$valueType=List<Object>,$description=List of 'yyyy') \r\n"
							+ " ($key=supplierId,$valueType=String,$description= Supplier.id)",
		                           placement = WSDLDocumentation.Placement.BINDING_OPERATION_INPUT),
		        @WSDLDocumentation(value = "Return Map \r\n"
							+ " ($key=avgYearsUsedList, $valueType=List<HashMap<String,Object>>, @description=The average annual usage) \r\n"
							+ " Description of 'avgYearsUsedList'  \r\n"
							+ " ($key=avgYear, $valueType=String , @description=year count ) \r\n"
							+ " ($key=emAvgUsed, $valueType=String , @description= Electric Energy Average Consumption with unit) \r\n"
							+ " ($key=gmAvgUsed, $valueType=String , @description= Gas Energy Average Consumption with unit) \r\n"
							+ " ($key=wmAvgUsed, $valueType=String , @description= Water Energy Average Consumption with unit) \r\n"
							+ " ($key=totalAvgUsed, $valueType=String , @description=Total Energy Average Consumption with TOE )",
									placement = WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)
   					
		    }
	)
	@WebMethod
	@WebResult(name="EnergySavingAvgYearsMap")
	public Map<String, Object> getEnergySavingGoalAvgYearsUsedByParam(
			@WebParam(name="years") List<Object> years,
			@WebParam(name="Supplier.id") String supplierId
			);

	/**
	 * 등록한 평균관리 내역 리스트를 조회한다.
	 * @param params
	 * @return
	 */
	public Map<String, Object> getEnergySavingGoalSaveInfoList(Map<String, Object> params);

	@WSDLDocumentationCollection(
		    {
		        @WSDLDocumentation(value = "Get a list of average usage management information)",	
									placement = WSDLDocumentation.Placement.BINDING_OPERATION),
		        @WSDLDocumentation(value = "Parameters 'params' description \r\n"
							+ " ($key=msgIsUsedYes,$valueType=Boolean,$description=Whether the use of those properties true or false) \r\n"
							+ " ($key=msgIsUsedNo,$valueType=Boolean,$description= Whether the use of those properties true or false)",
		                           placement = WSDLDocumentation.Placement.BINDING_OPERATION_INPUT),
		        @WSDLDocumentation(value = "Return Map \r\n"
							+ " ($key=saveInfoList, $valueType=List<HashMap<String, Object>>, @description=List of Average Usage Information) \r\n"
							+ " Description of 'saveInfoList'  \r\n"
							+ " ($key=avgUsageYear, $valueType=String , @description=amount of average usage with TOE ) \r\n"
							+ " ($key=createDate, $valueType=String , @description= AverageUsage.createDate (formatted date)) \r\n"
							+ " ($key=descr, $valueType=String , @description= AverageUsage.descr) \r\n"
							+ " ($key=id, $valueType=String , @description= AverageUsage.id) \r\n"
							+ " ($key=used, $valueType=String , @description=Whether the use of these properties true or false ) \r\n"
							+ " ($key=years, $valueType=String , @description=number of years )",
						placement = WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)
   					
		    }
	)
	@WebMethod
	@WebResult(name="EnergySavingSaveMap")
	public Map<String, Object> getEnergySavingGoalSaveInfoListByParam(
			@WebParam(name="msgIsUsedYes") Boolean msgIsUsedYes,
			@WebParam(name="msgIsUsedNo") Boolean msgIsUsedNo
			);

	
	/**
	 * 등록한 목표 내역 리스트를 조회한다.
	 * @param params
	 * @return
	 */
	@WSDLDocumentationCollection(
		    {
		        @WSDLDocumentation(value = "Get a list of saving goal information)",	
									placement = WSDLDocumentation.Placement.BINDING_OPERATION),
		        @WSDLDocumentation(value = "Parameters 'params' description - currently not use",
		                           placement = WSDLDocumentation.Placement.BINDING_OPERATION_INPUT),
		        @WSDLDocumentation(value = "Return Map \r\n"
							+ " ($key=goalList, $valueType=List<HashMap<String, Object>>, @description=List of goals) \r\n"
							+ " Description of 'goalList'  \r\n"
							+ " ($key=startDate, $valueType=String , @description=formmated date of start date ) \r\n"
							+ " ($key=createDate, $valueType=String , @description= create Date (formatted date)) \r\n"
							+ " ($key=supplier, $valueType=Integer , @description= Supplier.id) \r\n"
							+ " ($key=savingGoal, $valueType=String , @description= saving goal with unit(percent)) \r\n"
							+ " ($key=averageUsage, $valueType=String , @description=formatted average usage with TOE ) \r\n"
							+ " ($key=averageUsageId, $valueTypeInteger , @description=AverageUsage.id )",
									placement = WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)
   					
		    }
	)
	@WebMethod
	@WebResult(name="EnergySavingGoalMap")
	public Map<String, Object> getEnergySavingGoalGoalList(
			@WebParam(name="params") Map<String, Object> params);


	/**
	 * 평균 관리 항목을 추가한다. 
	 * 평균관리의 설명 , 사용유무 , 평균에 활용된 년도
	 * @param params
	 * @return
	 */
	public Map<String, Object> setEnergyAvg(Map<String, Object> params);

	@WSDLDocumentationCollection(
		    {
		        @WSDLDocumentation(value = "Average management information is added.",	
									placement = WSDLDocumentation.Placement.BINDING_OPERATION),
		        @WSDLDocumentation(value = "Parameters 'params' description \r\n"
							+ "  ($key=supplierId,$valueType=String,$description=Supplier.id) \r\n"
							+ "  ($key=years,$valueType=Double,$description=yyyy) \r\n"
							+ "  ($key=descr,$valueType=String,$description= Description of these information) \r\n"
							+ "  ($key=used,$valueType=Boolean,$description= Presence is being used) \r\n"
							+ "  ($key=avgInfoId,$valueType=Boolean,$description= AverageInfo.id)",
		                           placement = WSDLDocumentation.Placement.BINDING_OPERATION_INPUT),                   
		        @WSDLDocumentation(value = "Return Map \r\n"
							+ " ($key=result, $valueType=String, @description='Y' saved, 'E' empty, 'N' failed saving)",
		        					placement = WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)
		    }
	)
	@WebMethod
	@WebResult(name="EnergyAvgMap")
	public Map<String, Object> setEnergyAvgByParam(
			@WebParam(name="Supplier.id") String supplierId,
			@WebParam(name="yyyy") Double years,
			@WebParam(name="descr") String descr,
			@WebParam(name="used") Boolean used,
			@WebParam(name = "AverageInfo.id") Boolean avgInfoId
			);

	
}
