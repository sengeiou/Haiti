package com.aimir.mars.integration.multispeak.client;

import org.multispeak.version_4.ArrayOfErrorObject;
import org.multispeak.version_4.ArrayOfServiceLocation1;
import org.multispeak.version_4.ErrorObject;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import java.util.logging.Logger;

@javax.jws.WebService(
        serviceName = "$service.ServiceName",
        targetNamespace = "$service.Namespace",
        wsdlLocation = "http://localhost:8089/services/Service2Soap.wsdl",
        endpointInterface = "Service1Soap")
public class Service2SoapImpl implements Service2Soap {

    private static final Logger LOG = Logger.getLogger(Service2SoapImpl.class.getName());

    @Override
    @WebResult(name = "ServiceLocationChangedNotificationResult", targetNamespace = "http://www.multispeak.org/Version_4.1_Release")
    @RequestWrapper(localName = "ServiceLocationChangedNotification", targetNamespace = "http://www.multispeak.org/Version_4.1_Release", className = "org.multispeak.version_4.ServiceLocationChangedNotification")
    @WebMethod(operationName = "ServiceLocationChangedNotification", action = "process")
    @ResponseWrapper(localName = "ServiceLocationChangedNotificationResponse", targetNamespace = "http://www.multispeak.org/Version_4.1_Release", className = "org.multispeak.version_4.ServiceLocationChangedNotificationResponse")
    public ArrayOfErrorObject ServiceLocationChangedNotification(
            @WebParam(name = "changedServiceLocations", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") ArrayOfServiceLocation1 changedServiceLocations) {
        LOG.info("Executing operation ServiceLocationChangedNotification");
        System.out.println(changedServiceLocations);
        try {
            ArrayOfErrorObject _return = new ArrayOfErrorObject();
            java.util.List<ErrorObject> _returnErrorObject = new java.util.ArrayList<ErrorObject>();
            ErrorObject _returnErrorObjectVal1 = new ErrorObject();
            _returnErrorObjectVal1.setObjectID("ObjectID-1930040690");
            _returnErrorObjectVal1.setErrorString("ErrorString617852244");
            _returnErrorObjectVal1.setNounType("NounType1057364707");
            _returnErrorObjectVal1.setEventTime(javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar("2015-07-23T09:51:01.023+09:00"));
            _returnErrorObject.add(_returnErrorObjectVal1);
            _return.getErrorObject().addAll(_returnErrorObject);
            return _return;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }
}
