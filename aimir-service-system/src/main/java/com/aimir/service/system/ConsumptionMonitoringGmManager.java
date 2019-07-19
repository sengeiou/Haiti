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

@WSDLDocumentation("Gas Energy Consumption Monitoring Service")
@WebService(name="ConsumptionMonitoringGmService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface ConsumptionMonitoringGmManager {

	public Map<String, Object> getBuildingLookUpGm(Map<String, Object> condition);

	@WSDLDocumentationCollection(
			{
					@WSDLDocumentation(value = "Building energy usage information",	
										placement = WSDLDocumentation.Placement.BINDING_OPERATION),
				    @WSDLDocumentation(value = "Parameter Map \r\n"
									+ " ($key=searchDateType, $valueType=String, @description=HOURLY('0'), DAILY('1'),PERIOD('2'),WEEKLY('3'), MONTHLY('4'), MONTHLYPERIOD('5'),WEEKDAILY('6'),SEASONAL('7'),YEARLY('8'),QUARTERLY('9')) \r\n"
									+ " ($key=supplierId, $valueType=String, @description=Supplier.id) \r\n"
									+ " ($key=detailLocationId, $valueType=Integer, @description=Location ID on the floor or room number (Location.id))",
			            				placement = WSDLDocumentation.Placement.BINDING_OPERATION_INPUT),  
				    @WSDLDocumentation(value = "Return Map \r\n"
									+ " ($key=TM_MAX, $valueType=int, @description=Temperature Max) \r\n"
									+ " ($key=TM_MIN, $valueType=int, @description=Temperature Min) \r\n"
									+ " ($key=currentDateTime, $valueType=String, @description=yyyy.MM.dd HH:mm:ss) \r\n"
									+ " ($key=grid, $valueType=List<Map<String, Object>>, @description=Energy Consumption of Location / Sum of carbon.) \r\n"
									+ " Description of 'grid' \r\n"
									+ " ($key=LOCATION_ID, $valueType=String, @description=Location.id) \r\n"
									+ " ($key=NAME, $valueType=String, @description=Location.name) \r\n"
									+ " ($key=MDS_ID, $valueType=String, @description=Meter.mdsId) \r\n"
									+ " ($key=TOTAL, $valueType=Double, @description=TOTAL Energy Consumption) \r\n"
									+ " ($key=sumTHGrid, $valueType=List<HashMap<String, Object>>, @description=Building full-power temperature / humidity) \r\n"
									+ " Description of 'sumGrid' \r\n"
									+ " ($key=hour '0' ~'23', $valueType=HashMap<String, Object>, @description=Number('0' ~ '23')) \r\n"
									+ " ($key='MDATE', $valueType=String, @description=metering date time) \r\n"
									+ " ($key='TM', $valueType=Double, @description=Temperature Value) \r\n"
									+ " ($key='TM_MAX', $valueType=Double, @description=Temperature Max Value) \r\n"
									+ " ($key='TM_MIN', $valueType=Double, @description=Temperature Min Value) \r\n"
									+ " ($key='HUM', $valueType=Double, @description=Humidity Value) \r\n"
									+ " ($key='HUM_MAX', $valueType=Double, @description=Humidity Max Value) \r\n"
									+ " ($key='HUM_MIN', $valueType=Double, @description=Humidity Min Value) \r\n"
									+ " ($key=returnLocation, $valueType=List<Location>, @description=Location List)",
			            				placement = WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)
			}	)
			@WebMethod
			@WebResult(name="BuildingLookUpGmMap")
	public Map<String, Object> getBuildingLookUpGmByParam(
			@WebParam(name="searchDateType")String searchDateType,
			@WebParam(name="Supplier.id")String supplierId,
			@WebParam(name ="Location.id") Integer detailLocationId
	);

	public Map<String, Object> getBuildingLookUpMaxGm(Map<String, Object> condition);
	
	@WSDLDocumentationCollection(
			{
			        @WSDLDocumentation(value = "Building energy usage detailed information",	
										placement = WSDLDocumentation.Placement.BINDING_OPERATION),
			        @WSDLDocumentation(value = "Parameter Map \r\n"
									+ " ($key=searchDateType, $valueType=String, @description=HOURLY('0'), DAILY('1'),PERIOD('2'),WEEKLY('3'), MONTHLY('4'), MONTHLYPERIOD('5'),WEEKDAILY('6'),SEASONAL('7'),YEARLY('8'),QUARTERLY('9')) \r\n"
									+ " ($key=supplierId, $valueType=String, @description=Supplier.id) \r\n"
									+ " ($key=detailLocationId, $valueType=Integer, @description=Location ID on the floor or room number (Location.id))",
										placement = WSDLDocumentation.Placement.BINDING_OPERATION_INPUT), 
			        @WSDLDocumentation(value = "Return Map \r\n"
									+ " ($key=currentDateTime, $valueType=String, @description=yyyy.MM.dd HH:mm:ss) \r\n"
									+ " ($key=myChartDataLocation, $valueType=List<Map<String, Object>>, @description=The total amount of energy by Location) \r\n"
									+ " Description of 'myChartDataLocation' \r\n"
									+ " ($key=NAME, $valueType=String, @description=Location.name) \r\n"
									+ " ($key=TOTAL, $valueType=Double, @description=TOTAL Energy Consumption) \r\n"
									+ " ($key=myChartDataDay, $valueType=List<HashMap<String, Object>>, @description=Daily energy consumption min/max/co2) \r\n"
									+ " ($key=myChartDataDayInfo, $valueType=List<HashMap<String, Object>>, @description=Daily energy consumption total) \r\n"
									+ " ($key=myChartDataWeek, $valueType=List<HashMap<String, Object>>, @description=Weekly energy consumption min/max/co2) \r\n"
									+ " ($key=myChartDataWeekInfo, $valueType=List<HashMap<String, Object>>, @description=Weekly energy consumption total) \r\n"
									+ " ($key=myChartDataMonth, $valueType=List<HashMap<String, Object>>, @description==Monthly energy consumption min/max/co2) \r\n"
									+ " ($key=myChartDataMonthInfo, $valueType=List<HashMap<String, Object>>, @description=Monthly energy consumption total) \r\n"
									+ " ($key=myChartDataQuarter, $valueType=List<HashMap<String, Object>>, @description=Quater energy consumption min/max/co2) \r\n"
									+ " ($key=myChartDataQuarterInfo, $valueType=List<HashMap<String, Object>>, @description=Quater energy consumption total) \r\n"
									+ " Description of 'myChartDataXXX' \r\n"
									+ " ($key=sequence('0' ~ 'XX'), $valueType=HashMap<String, Object>, @description=Number('0' ~ 'XX')) \r\n"
									+ " ($key=MYDATE, $valueType=String, @description=metering date time) \r\n"
									+ " ($key=GMMIN, $valueType=Double, @description=Energy Consumption Min) \r\n"
									+ " ($key=GMMAX, $valueType=Double, @description=Energy Consumption Max) \r\n"
									+ " ($key=GMSUM, $valueType=Double, @description=Energy Consumption Summation) \r\n"
									+ " ($key=CO2MIN, $valueType=Double, @description=co2 emission Min) \r\n"
									+ " ($key=CO2MAX, $valueType=Double, @description=co2 emission Max) \r\n"
									+ " ($key=CO2SUM, $valueType=Double, @description=co2 emission Summation) \r\n"
									+ " Description of 'myChartDataXXXInfo' \r\n"
									+ " ($key=INFOTOTAL, $valueType=String, @description=Total Value with unit) \r\n"
									+ " ($key=INFOCO2TOTAL, $valueType=String, @description=Co2 Total Value with unit ) \r\n"
									+ " ($key=INFOCAVGCHARGE, $valueType=String, @description=Average usage rate) \r\n"
									+ " ($key=INFOMAXUSETIME, $valueType=String, @description=During peak time) \r\n"
									+ " ($key=INFOMAXUSE, $valueType=String, @description=During peak usage with unit) \r\n"
									+ " ($key=INFOMINUSETIME, $valueType=String, @description=During off-peak time) \r\n"
									+ " ($key=INFOMINUSE, $valueType=String, @description=During off-peak usage with unit) \r\n"
									+ " ($key=returnLocation, $valueType=List<Location>, @description=Location List)",
		            					placement = WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)
			    }
			)
			@WebMethod
			@WebResult(name="BuildingLookUpMaxGmMap")
	public Map<String, Object> getBuildingLookUpMaxGmByParam(
			@WebParam(name="searchDateType")String searchDateType,
			@WebParam(name="Supplier.id")String supplierId,
			@WebParam(name="Location.id") Integer detailLocationId			
			);

	@WebMethod
	@WebResult(name="RootLocationId")
	public Map<String, Object> getRootLocationId(
			@WebParam(name="condition") Map<String, Object> condition);	
	
	public Map<String, Object> getTotalUseOfSearchType(Map<String, String> condition);

	@WSDLDocumentationCollection(
			{
			        @WSDLDocumentation(value = "Returns current usage and carbon emissions by parameters(supplierId and period) .",	
										placement = WSDLDocumentation.Placement.BINDING_OPERATION),
			        @WSDLDocumentation(value = "Parameter Map \r\n"
									+ " ($key=searchDateType, $valueType=String, @description=HOURLY('0'), DAILY('1'),PERIOD('2'),WEEKLY('3'), MONTHLY('4'), MONTHLYPERIOD('5'),WEEKDAILY('6'),SEASONAL('7'),YEARLY('8'),QUARTERLY('9')) \r\n"
									+ " ($key=supplierId, $valueType=String, @description=Supplier.id) \r\n"
									+ " ($key=locationId, $valueType=Integer, @description=Location ID on the floor or room number (Location.id))",
										placement = WSDLDocumentation.Placement.BINDING_OPERATION_INPUT), 
			        @WSDLDocumentation(value = "Return Map \r\n"
									+ " ($key=totalUse, $valueType=Double, @description= Total Usage) \r\n"
									+ " ($key=totalCo2Use, $valueType=Double, @description= Total co2 emission) \r\n"
									+ " ($key=averageUsage, $valueType=Double, @description=Average Usage) \r\n"
									+ " ($key=averageCo2Usage, $valueType=Double, @description=Average co2 emission)",
		            					placement = WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)
			}	)
			@WebMethod
			@WebResult(name="TotalUseOfSearchType")
	public Map<String, Object> getTotalUseOfSearchTypeByParam(
			@WebParam(name="searchDateType") String searchDateType,
			@WebParam(name="Supplier.id") String supplierId,
			@WebParam(name="Location.id") Integer locationId	
			);
}
