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

@WSDLDocumentation("Energy Consumption(Peak Demand) Data Service ")
@WebService(name="ContractEnergyPeakDemandService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface ContractEnergyPeakDemandManager {

	@WSDLDocumentationCollection(
		    {
		        @WSDLDocumentation(value = "ContractCapacity regional mapping information is returned.",	
									placement = WSDLDocumentation.Placement.BINDING_OPERATION),
		        @WSDLDocumentation(value = "List Of Object {CONTRACTCAPACITY_ID as id,loc.name as name }",
		                           placement = WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)
		    }
	)	
	@WebMethod
	@WebResult(name="EnergyPeakDemandCombo")
	public List<Map<String,Object>> getEnergyPeakDemandCombo();

	public Map<String, Object> getEnergyPeakDemand(Map<String, Object> condition);

	@WSDLDocumentationCollection(
	{
		        @WSDLDocumentation(value = "Energy Peak Demand Information(Gauge Chart, Column Data,etc).",	
									placement = WSDLDocumentation.Placement.BINDING_OPERATION),
		        @WSDLDocumentation(value = "Parameters \r\n"
							+ " ($key=contractCapacityId,$valueType=String,$description=ContractCapacity.id) \r\n"
							+ " ($key=startDate,$valueType=String,$description=yyyyMMddHH) \r\n"
							+ " ($key=endDate,$valueType=String,$description=yyyyMMddHH)",
		                           placement = WSDLDocumentation.Placement.BINDING_OPERATION_INPUT),
		        @WSDLDocumentation(value = "Return Map \r\n"
							+ " ($key=currentDateTime, $valueType=String, @description=yyyy.MM.dd HH:mm:ss) \r\n"
							+ " ($key=gauge, $valueType=Map<String, Object>, @description=gauge chart data map) \r\n"
							+ " ($key=column, $valueType=List<Map<String, Object>>, @description=chart data list) \r\n"
							+ " Description of 'gauge' \r\n"
							+ " ($key=threshold1, $valueType=Double, @description=threshold1) \r\n"
							+ " ($key=threshold2, $valueType=Double, @description=threshold2) \r\n"
							+ " ($key=threshold3, $valueType=Double, @description=threshold3) \r\n"
							+ " ($key=lastAmount, $valueType=Double, @description=Last amount) \r\n"
							+ " ($key=lastPercent, $valueType=Double, @description=Last percent) \r\n"
							+ " ($key=useAmount, $valueType=Double, @description=use amount) \r\n"
							+ " ($key=capacity, $valueType=Doouble, @description=capacity) \r\n"
							+ " ($key=maxPercent, $valueType=Double, @description=max percent) \r\n"
							+ " Description of 'column' List of Map  \r\n"
							+ " ($key=hh, $valueType=String, @description=hour('00' ~'23') ) \r\n"
							+ " ($key=amount, $valueType=String, @description=amount ) \r\n"
							+ " ($key=percent, $valueType=String, @description=percent )",
    								placement = WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)
		    }
	)	
	@WebMethod
	@WebResult(name="EnergyPeakDemandMap")
	public Map<String, Object> getEnergyPeakDemandByParam(
			@WebParam(name="ContractCapacity.id") String contractCapacityId,
			@WebParam(name="yyyyMMddHH") String startDate,
			@WebParam(name="yyyyMMddHH") String endDate
			);
	
	public void updateThreshold(Map<String, Object> condition);

	@WSDLDocumentationCollection(
		    {
		        @WSDLDocumentation(value = "Updating Peak Demand Threshold.",	
									placement = WSDLDocumentation.Placement.BINDING_OPERATION),
		        @WSDLDocumentation(value = "Parameters \r\n"
							+ " ($key=contractCapacityId,$valueType=String,$description=ContractCapacity.id) \r\n"
							+ " ($key=threshold1,$valueType=String,$description=[ex] 0.000) \r\n"
							+ " ($key=threshold2,$valueType=String,$description=[ex] 0.000) \r\n"
							+ " ($key=threshold3,$valueType=String,$description=[ex] 0.000) }",
		                           placement = WSDLDocumentation.Placement.BINDING_OPERATION_INPUT)
		    }
	)
	@WebMethod
	public void updateThresholdByParam(
			@WebParam(name="ContractCapacity.id") String contractCapacityId,
			@WebParam(name="threshold1") String threshold1,
			@WebParam(name="threshold2") String threshold2,
			@WebParam(name="threshold3") String threshold3
			);
}
