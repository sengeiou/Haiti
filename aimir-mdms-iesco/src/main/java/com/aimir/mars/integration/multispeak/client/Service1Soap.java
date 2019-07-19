package com.aimir.mars.integration.multispeak.client;

import org.multispeak.version_4.ArrayOfErrorObject;
import org.multispeak.version_4.Meters;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * 일반적인 Multispeak 구분 외 서비스 Endpoint에 대응하기 위해 별도 작성.
 * 프로젝트별로 임의 수정 가능함.
 */
@WebService(targetNamespace = "http://www.multispeak.org/Version_4.1_Release", name = "Service1Soap")
public interface Service1Soap {

    @WebResult(name = "MeterChangedNotificationResult", targetNamespace = "http://www.multispeak.org/Version_4.1_Release")
    @RequestWrapper(localName = "MeterChangedNotification", targetNamespace = "http://www.multispeak.org/Version_4.1_Release", className = "org.multispeak.version_4.MeterChangedNotification")
    @WebMethod(operationName = "MeterChangedNotification", action = "process")
    @ResponseWrapper(localName = "MeterChangedNotificationResponse", targetNamespace = "http://www.multispeak.org/Version_4.1_Release", className = "org.multispeak.version_4.MeterChangedNotificationResponse")
    public ArrayOfErrorObject MeterChangedNotification (
            @WebParam(name = "changedMeters", targetNamespace = "http://www.multispeak.org/Version_4.1_Release")
                    Meters changedMeters
    );
}
