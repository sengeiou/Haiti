package com.aimir.mars.integration.multispeak.client;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

import org.multispeak.version_4.ArrayOfCDStateChange;
import org.multispeak.version_4.ArrayOfErrorObject;
import org.multispeak.version_4.ArrayOfMeterReading1;

/**
 * This class was generated by Apache CXF 2.7.16
 * 2015-07-22T20:55:13.503+09:00
 * Generated source version: 2.7.16
 * 
 */
@WebService(targetNamespace = "http://www.multispeak.org/Version_4.1_Release", name = "CB_ServerSoap")
public interface CBServerSoap {

    /**
     * CD notifies CB of state change(s) for connect/disconnect device(s). 
     * The transactionID calling parameter can be used to link this action with an InitiateConectDisconnect call. 
     * If this transaction fails,
     * CB returns information about the failure in an array of errorObject(s). 
     * The message header attribute 'registrationID' should be added to all publish messages to
     * indicate to the subscriber under which registrationID they received this notification data.
     */
    @WebResult(name = "CDStatesChangedNotificationResult", targetNamespace = "http://www.multispeak.org/Version_4.1_Release")
    @RequestWrapper(localName = "CDStatesChangedNotification", targetNamespace = "http://www.multispeak.org/Version_4.1_Release", className = "org.multispeak.version_4.CDStatesChangedNotification")
    @WebMethod(operationName = "CDStatesChangedNotification", action = "CDStatesChangedNotification")
    @ResponseWrapper(localName = "CDStatesChangedNotificationResponse", targetNamespace = "http://www.multispeak.org/Version_4.1_Release", className = "org.multispeak.version_4.CDStatesChangedNotificationResponse")
    public ArrayOfErrorObject CDStatesChangedNotification(
        @WebParam(name = "stateChanges", targetNamespace = "http://www.multispeak.org/Version_4.1_Release")
        ArrayOfCDStateChange stateChanges,
        @WebParam(name = "transactionID", targetNamespace = "http://www.multispeak.org/Version_4.1_Release")
        java.lang.String transactionID
    );



    @WebResult(name = "GetReadingsByMeterIDAsyncResult", targetNamespace = "http://www.multispeak.org/Version_4.1_Release")
    @RequestWrapper(localName = "GetReadingsByMeterIDAsync", targetNamespace = "http://www.multispeak.org/Version_4.1_Release", className = "org.multispeak.version_4.GetReadingsByMeterIDAsync")
    @WebMethod(operationName = "GetReadingsByMeterIDAsync", action = "http://www.multispeak.org/Version_4.1_Release/GetReadingsByMeterIDAsync")
    @ResponseWrapper(localName = "GetReadingsByMeterIDAsyncResponse", targetNamespace = "http://www.multispeak.org/Version_4.1_Release", className = "org.multispeak.version_4.GetReadingsByMeterIDAsyncResponse")
    public ArrayOfErrorObject GetReadingsByMeterIDAsync(
        @WebParam(name = "meterReads", targetNamespace = "http://www.multispeak.org/Version_4.1_Release")
        ArrayOfMeterReading1 meterReads,
        @WebParam(name = "transactionID", targetNamespace = "http://www.multispeak.org/Version_4.1_Release")
        java.lang.String transactionID
    );
    
}
