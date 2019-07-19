package com.aimir.service.device;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;


import com.aimir.model.device.EnergyMeter;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;

@WebService(name="DeviceService", targetNamespace="http://aimir.com/services")
public interface DeviceManagerWebService {
	
    @WebMethod
	public @WebResult(name="Meter") Meter getMeter(@WebParam(name="MeterIdInteger") Integer meterId);
    
    @WebMethod(operationName="getMeterBySerial")
	public @WebResult(name="Meter") Meter getMeter(@WebParam(name="MeterSerialString") String meterSerial);
    
    @WebMethod
    public @WebResult(name="MCU") MCU getMCU(@WebParam(name="McuIdInteger") Integer mcuId);
    
    @WebMethod (operationName="getMCUBySysId")
    public @WebResult(name="MCU") MCU getMCU(@WebParam(name="SysIdString") String name);
    
    @WebMethod
    public @WebResult(name="Modem") Modem getModem(@WebParam(name ="modemId")Integer modemId);
    
    @WebMethod(operationName="getModemByDeviceSerial")
    public @WebResult(name="Modem") Modem getModem(@WebParam(name ="deviceSerial")String deviceSerial);
    
    @WebMethod
    public @WebResult(name="MeterList") EnergyMeter[] getAllMeter();
    
    @WebMethod
    public @WebResult(name="McuList") MCU[] getAllMcu();
    
    @WebMethod
    public @WebResult(name="ModemList") Modem[] getAllModem();
}
