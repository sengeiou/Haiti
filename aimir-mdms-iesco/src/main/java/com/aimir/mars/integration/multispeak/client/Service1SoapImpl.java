package com.aimir.mars.integration.multispeak.client;

import org.multispeak.version_4.ArrayOfErrorObject;
import org.multispeak.version_4.ErrorObject;
import org.multispeak.version_4.Meters;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import java.util.logging.Logger;

@javax.jws.WebService(
        serviceName = "$service.ServiceName",
        targetNamespace = "$service.Namespace",
        wsdlLocation = "http://localhost:8089/services/Service1Soap.wsdl",
        endpointInterface = "Service1Soap")
public class Service1SoapImpl implements Service1Soap {

    private static final Logger LOG = Logger.getLogger(Service1SoapImpl.class.getName());

    @Override
    @WebResult(name = "MeterChangedNotificationResult", targetNamespace = "http://www.multispeak.org/Version_4.1_Release")
    @RequestWrapper(localName = "MeterChangedNotification", targetNamespace = "http://www.multispeak.org/Version_4.1_Release", className = "org.multispeak.version_4.MeterChangedNotification")
    @WebMethod(operationName = "MeterChangedNotification", action = "process")
    @ResponseWrapper(localName = "MeterChangedNotificationResponse", targetNamespace = "http://www.multispeak.org/Version_4.1_Release", className = "org.multispeak.version_4.MeterChangedNotificationResponse")
    public ArrayOfErrorObject MeterChangedNotification(
            @WebParam(name = "changedMeters", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") Meters changedMeters) {
        LOG.info("Executing operation meterChangedNotification");
        System.out.println(changedMeters);
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
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }
}
