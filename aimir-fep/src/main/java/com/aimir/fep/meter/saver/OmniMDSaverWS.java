package com.aimir.fep.meter.saver;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import javax.jws.soap.SOAPBinding.ParameterStyle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@WebService(serviceName="OmniMDSaver")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
@Service
public class OmniMDSaverWS {
    
    @Autowired
    private OmniMDSaver saver;
    
    @WebMethod
    public @WebResult(name="response") String save(@WebParam(name="meterSerialNumber") String meterNo, 
            @WebParam(name="dcuNumber") String dcuNo, @WebParam(name="modemNumber") String modemNo,
            @WebParam(name="meteringTime") String meteringTime, @WebParam(name="meteringValueM3") double meteringValue,
            @WebParam(name="lpInterval") int lpInterval, @WebParam(name="lpValueM3") double[] lplist)
    throws Exception
    {
        return saver.save(meterNo, dcuNo, modemNo, meteringTime, meteringValue, lpInterval, lplist);
    }
    
    /**
     * base 값을 가져오는 저장 로직
     * @param meterNo
     * @param dcuNo
     * @param modemNo
     * @param meteringTime
     * @param meteringValue
     * @param lpInterval
     * @param lplist
     * @return
     * @throws Exception
     */
    @WebMethod
    public @WebResult(name="response") String save1(@WebParam(name="meterSerialNumber") String meterNo, 
            @WebParam(name="dcuNumber") String dcuNo, @WebParam(name="modemNumber") String modemNo,
            @WebParam(name="meteringTime") String meteringTime, @WebParam(name="meteringValueM3") double meteringValue,
            @WebParam(name="lpInterval") int lpInterval, @WebParam(name="lpValueM3") double[] lplist)
    throws Exception
    {
        return saver.save1(meterNo, dcuNo, modemNo, meteringTime, meteringValue, lpInterval, lplist);
    }
}
