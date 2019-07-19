package com.aimir.mars.integration.multispeak.client;

import org.multispeak.version_4.ArrayOfErrorObject;
import org.multispeak.version_4.ArrayOfServiceLocation1;

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
@WebService(targetNamespace = "http://www.multispeak.org/Version_4.1_Release", name = "Service2Soap")
public interface Service2Soap {

    @WebResult(name = "ServiceLocationChangedNotificationResult", targetNamespace = "http://www.multispeak.org/Version_4.1_Release")
    @RequestWrapper(localName = "ServiceLocationChangedNotification", targetNamespace = "http://www.multispeak.org/Version_4.1_Release", className = "org.multispeak.version_4.ServiceLocationChangedNotification")
    @WebMethod(operationName = "ServiceLocationChangedNotification", action = "process")
    @ResponseWrapper(localName = "ServiceLocationChangedNotificationResponse", targetNamespace = "http://www.multispeak.org/Version_4.1_Release", className = "org.multispeak.version_4.ServiceLocationChangedNotificationResponse")
    public ArrayOfErrorObject ServiceLocationChangedNotification(
            @WebParam(name = "changedServiceLocations", targetNamespace = "http://www.multispeak.org/Version_4.1_Release")
                    ArrayOfServiceLocation1 changedServiceLocations
    );
}
