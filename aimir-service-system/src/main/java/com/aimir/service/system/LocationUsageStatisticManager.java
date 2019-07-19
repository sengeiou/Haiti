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

@WSDLDocumentation("Regional Statistical Information Service")
@WebService(name="LocationUsageStatisticService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface LocationUsageStatisticManager {
	

	public Map<String,Object> getSearchChartData(Map<String,Object> params);
	
	@WSDLDocumentationCollection(
	{
		        @WSDLDocumentation(value = "Quarterly energy consumption and usage charges and internal and external temperature, humidity",	
									placement = WSDLDocumentation.Placement.BINDING_OPERATION),
		        @WSDLDocumentation(value = "Parameters \r\n"
							+ " ($key=periodType,$valueType=String,$description=HOURLY('0'), DAILY('1'),PERIOD('2'),WEEKLY('3'), MONTHLY('4'), MONTHLYPERIOD('5'),WEEKDAILY('6'),SEASONAL('7'),YEARLY('8'),QUARTERLY('9')) \r\n"
							+ " ($key=supplierId,$valueType=Integer,$description=Supplier ID Supplier.id) \r\n"
							+ " ($key=date,$valueType=String,$description=yyyyMMddHH) \r\n"
							+ " ($key=locationId,$valueType=Integer,$description=Location Id Location.id)",
		                           placement = WSDLDocumentation.Placement.BINDING_OPERATION_INPUT),
		        @WSDLDocumentation(value = "Return Map \r\n"
							+ " ($key=currUsageList, $valueType=List<Map<String, Object>>, @description=current consumption data list) \r\n"
							+ " Description of 'currUsageList' \r\n"
							+ " ($key=xField, $valueType=String, @description=Quater Lable) \r\n"
							+ " ($key=EmUsage, $valueType=String, @description=Electricity Energy Consumption \r\n"
							+ " ($key=EmKGOE, $valueType=String, @description=Electricity Energy Consumption by KGOE) \r\n"
							+ " ($key=GmUsage, $valueType=String, @description=Gas Energy Consumption) \r\n"
							+ " ($key=GmKGOE, $valueType=String, @description=Gas Energy Consumption by KGOE) \r\n"
							+ " ($key=WmUsage, $valueType=String, @description=Water Energy Consumption) \r\n"
							+ " ($key=WmKGOE, $valueType=String, @description=Water Energy Consumption by KGOE) \r\n"
							+ " ($key=EmBillUsage, $valueType=String, @description=Electricity charges) \r\n"
							+ " ($key=GmBillUsage, $valueType=String, @description=Gas charges) \r\n"
							+ " ($key=WmBillUsage, $valueType=String, @description=Water charges) \r\n"
							+ " ($key=TmInMaxUsage, $valueType=String, @description=Max internal temperature) \r\n"
							+ " ($key=TmInMinUsage, $valueType=String, @description=Min internal temperature) \r\n"
							+ " ($key=TmGridInUsage, $valueType=String, @description=Max/Min internal temperature) \r\n"
							+ " ($key=TmOutMaxUsage, $valueType=String, @description=Max external temperature) \r\n"
							+ " ($key=TmOutMinUsage, $valueType=String, @description=Min external temperature) \r\n"
							+ " ($key=TmGridOutUsage, $valueType=String, @description=Max/Min external temperature) \r\n"
							+ " ($key=HumInMaxUsage, $valueType=String, @description=Max internal humidity) \r\n"
							+ " ($key=HumInMinUsage, $valueType=String, @description=Min internal humidity) \r\n"
							+ " ($key=HumGridInUsage, $valueType=String, @description=Max/Min internal humidity) \r\n"
							+ " ($key=HumOutMaxUsage, $valueType=String, @description=Max external humidity) \r\n"
							+ " ($key=HumOutMinUsage, $valueType=String, @description=Min external humidity) \r\n"
							+ " ($key=HumGridOutUsage, $valueType=String, @description=Max/Min external humidity) \r\n"
							+ " ($key=Co2Usage, $valueType=String, @description=Co2 emission) \r\n"
							+ " ($key=totalUsageMap, $valueType=List<Map<String, Object>>, @description=chart data list) \r\n"
							+ " Description of 'totalUsageMap' \r\n"
							+ " ($key=minVal, $valueType=Double, @description=Minimum Consumption Value) \r\n"
							+ " ($key=maxVal, $valueType=Double, @description=Max Consumption Value) \r\n"
							+ " ($key=billMaxVal, $valueType=Double, @description=Max Consumption Charge) \r\n"
							+ " ($key=billMinVal, $valueType=Double, @description=Min Consumption Charge) \r\n"
							+ " ($key=co2MinVal, $valueType=Double, @description=Min co2 emission) \r\n"
							+ " ($key=co2MaxVal, $valueType=Double, @description=Max co2 emission) \r\n"
							+ " ($key=tmMinVal, $valueType=Double, @description=Min temperature) \r\n"
							+ " ($key=tmMaxVal, $valueType=Double, @description=Max temperature) \r\n"
							+ " ($key=humMinVal, $valueType=Double, @description=Min humidity) \r\n"
							+ " ($key=humMaxVal, $valueType=Double, @description=Max humidity)",
        							placement = WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)

		    }
	)
	@WebMethod
	@WebResult(name="SearchChartDataMap")
	public Map<String,Object> getSearchChartDataByParam(
			@WebParam(name="periodType") String periodType,
			@WebParam(name="Supplier.id") Integer supplierId,
			@WebParam(name="yyyyMMddHH") String date,
			@WebParam(name="Location.id") Integer locationId
			);
	
	public Map<String,Object> getSearchCompareChartData(Map<String,Object> params);
	
	@WSDLDocumentationCollection(
	{
		        @WSDLDocumentation(value = "Data Chart - usage charges and internal and external temperature, humidity",	
									placement = WSDLDocumentation.Placement.BINDING_OPERATION),
		        @WSDLDocumentation(value = "Parameters \r\n"
							+ " ($key=periodType,$valueType=String,$description=HOURLY('0'), DAILY('1'),PERIOD('2'),WEEKLY('3'), MONTHLY('4'), MONTHLYPERIOD('5'),WEEKDAILY('6'),SEASONAL('7'),YEARLY('8'),QUARTERLY('9')) \r\n"
							+ " ($key=supplierId,$valueType=Integer,$description=Supplier ID Supplier.id) \r\n"
							+ " ($key=date,$valueType=String,$description=yyyyMMddHH) \r\n"
							+ " ($key=compare,$valueType=Boolean,$description=Whether or not to compare the target) \r\n"
							+ " ($key=locationId,$valueType=Integer,$description=Location Id Location.id)",
		                           placement = WSDLDocumentation.Placement.BINDING_OPERATION_INPUT),
		        @WSDLDocumentation(value = "Return Map \r\n"
							+ " ($key=minVal, $valueType=Double, @description=Minimum Consumption Value) \r\n"
							+ " ($key=maxVal, $valueType=Double, @description=Max Consumption Value) \r\n"
							+ " ($key=billMaxVal, $valueType=Double, @description=Max Consumption Charge) \r\n"
							+ " ($key=billMinVal, $valueType=Double, @description=Min Consumption Charge) \r\n"
							+ " ($key=co2MinVal, $valueType=Double, @description=Min co2 emission) \r\n"
							+ " ($key=co2MaxVal, $valueType=Double, @description=Max co2 emission) \r\n"
							+ " ($key=tmMinVal, $valueType=Double, @description=Min temperature) \r\n"
							+ " ($key=tmMaxVal, $valueType=Double, @description=Max temperature) \r\n"
							+ " ($key=humMinVal, $valueType=Double, @description=Min humidity) \r\n"
							+ " ($key=humMaxVal, $valueType=Double, @description=Max humidity)",
        							placement = WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)
	})
	@WebMethod
	@WebResult(name="SearchCompareChartDataMap")
	public Map<String,Object> getSearchCompareChartDataByParam(
			@WebParam(name="periodType") String periodType,
			@WebParam(name="Supplier.id") Integer supplierId,
			@WebParam(name="yyyyMMddHH") String date,
			@WebParam(name="compare") Boolean compare,
			@WebParam(name="Location.id") Integer locationId
			);
	
	@WebMethod
	@WebResult(name="ExbitionUsageMap")
	Map<String, Object> getExbitionUsage(
			@WebParam(name ="loc")int loc);
	
	@WebMethod
	@WebResult(name="LocationExbitionUsageMap")
	Map<String, Object> getLocationExbitionUsage();
}
