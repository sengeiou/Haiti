package com.aimir.mars.integration.multispeak.client;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

import org.multispeak.version_4.*;

/**
 * This class was generated by Apache CXF 2.7.16
 * 2015-07-23T09:51:01.101+09:00
 * Generated source version: 2.7.16
 * 
 */
@WebService(targetNamespace = "http://www.multispeak.org/Version_4.1_Release", name = "MR_ServerSoap")
@XmlSeeAlso({_1_release.cpsm_v4.ObjectFactory.class, _1_release.gml_v4.ObjectFactory.class, org.w3._1999.xlink.ObjectFactory.class})
public interface MRServerSoap {

    /**
     * CB requests a new meter reading from MR, on meters selected by meterID.
     * MR returns information about failed transactions using an array of errorObjects.
     * The meter reading is returned to the CB in the form of a meterReading, an intervalData block, or a formattedBlock,
     * sent to the URL specified in the responseURL.
     * The transactionID calling parameter links this Initiate request with the published data method call.
     * The expiration time parameter indicates the amount of time for which the publisher should try to obtain and publish the data;
     * if the publisher has been unsuccessful in publishing the data after the expiration time,
     * then the publisher will discard the request and the requestor should not expect a response.
     */
    @WebResult(name = "InitiateMeterReadingsByMeterIDResult", targetNamespace = "http://www.multispeak.org/Version_4.1_Release")
    @RequestWrapper(localName = "InitiateMeterReadingsByMeterID", targetNamespace = "http://www.multispeak.org/Version_4.1_Release", className = "org.multispeak.version_4.InitiateMeterReadingsByMeterID")
    @WebMethod(operationName = "InitiateMeterReadingsByMeterID", action = "http://www.multispeak.org/Version_4.1_Release/InitiateMeterReadingsByMeterID")
    @ResponseWrapper(localName = "InitiateMeterReadingsByMeterIDResponse", targetNamespace = "http://www.multispeak.org/Version_4.1_Release", className = "org.multispeak.version_4.InitiateMeterReadingsByMeterIDResponse")
    public ArrayOfErrorObject InitiateMeterReadingsByMeterID(
        @WebParam(name = "meterIDs", targetNamespace = "http://www.multispeak.org/Version_4.1_Release")
        ArrayOfMeterIDNillable meterIDs,
        @WebParam(name = "responseURL", targetNamespace = "http://www.multispeak.org/Version_4.1_Release")
        java.lang.String responseURL,
        @WebParam(name = "transactionID", targetNamespace = "http://www.multispeak.org/Version_4.1_Release")
        java.lang.String transactionID,
        @WebParam(name = "expTime", targetNamespace = "http://www.multispeak.org/Version_4.1_Release")
        ExpirationTime expTime
    );

    /**
     * Publisher notifies MR to Add the associated meter(s).
     * MR returns information about failed transactions using an array of error Objects.
     * The message header attribute'registrationID'should be added to all publish messages to
     * indicate to the subscriber under which registrationID they received this notification data.
     */
    @WebResult(name = "MeterAddNotificationResult", targetNamespace = "http://www.multispeak.org/Version_4.1_Release")
    @RequestWrapper(localName = "MeterAddNotification", targetNamespace = "http://www.multispeak.org/Version_4.1_Release", className = "org.multispeak.version_4.MeterAddNotification")
    @WebMethod(operationName = "MeterAddNotification", action = "http://www.multispeak.org/Version_4.1_Release/MeterAddNotification")
    @ResponseWrapper(localName = "MeterAddNotificationResponse", targetNamespace = "http://www.multispeak.org/Version_4.1_Release", className = "org.multispeak.version_4.MeterAddNotificationResponse")
    public ArrayOfErrorObject MeterAddNotification(
        @WebParam(name = "addedMeters", targetNamespace = "http://www.multispeak.org/Version_4.1_Release")
        Meters addedMeters
    );

    /**
     * Publisher notifies MR to remove the associated meter(s).
     * MR returns information about failed transactions using an array of errorObjects.
     * The message header attribute 'registrationID' should be added to all publish messages to
     * indicate to the subscriber under which registrationID they received this notification data.
     */
    @WebResult(name = "MeterRemoveNotificationResult", targetNamespace = "http://www.multispeak.org/Version_4.1_Release")
    @RequestWrapper(localName = "MeterRemoveNotification", targetNamespace = "http://www.multispeak.org/Version_4.1_Release", className = "org.multispeak.version_4.MeterRemoveNotification")
    @WebMethod(operationName = "MeterRemoveNotification", action = "http://www.multispeak.org/Version_4.1_Release/MeterRemoveNotification")
    @ResponseWrapper(localName = "MeterRemoveNotificationResponse", targetNamespace = "http://www.multispeak.org/Version_4.1_Release", className = "org.multispeak.version_4.MeterRemoveNotificationResponse")
    public ArrayOfErrorObject MeterRemoveNotification(
        @WebParam(name = "removedMeters", targetNamespace = "http://www.multispeak.org/Version_4.1_Release")
        Meters removedMeters
    );    
    
    @WebResult(name = "MeterChangedNotificationResult", targetNamespace = "http://www.multispeak.org/Version_4.1_Release")
    @RequestWrapper(localName = "MeterChangedNotification", targetNamespace = "http://www.multispeak.org/Version_4.1_Release", className = "org.multispeak.version_4.MeterChangedNotification")    
    @WebMethod(operationName = "MeterChangedNotification", action = "http://www.multispeak.org/Version_4.1_Release/MeterChangedNotification")
    @ResponseWrapper(localName = "MeterChangedNotificationResponse", targetNamespace = "http://www.multispeak.org/Version_4.1_Release", className = "org.multispeak.version_4.MeterChangedNotificationResponse")
    public ArrayOfErrorObject MeterChangedNotification (
            @WebParam(name = "changedMeters", targetNamespace = "http://www.multispeak.org/Version_4.1_Release")
            Meters changedMeters
    );

    /**
     * MR Notifies CB of a change in meter readings by sending the changed meterReading objects.
     * CB returns information about failed transactions in an array of errorObjects.
     * The transactionID calling parameter links this Initiate request with the published data method call.
     * The message header attribute 'registrationID' should be added to all publish messages to
     * indicate to the subscriber under which registrationID they received this notification data.
     */
    @WebResult(name = "ReadingChangedNotificationResult", targetNamespace = "http://www.multispeak.org/Version_4.1_Release")
    @RequestWrapper(localName = "ReadingChangedNotification", targetNamespace = "http://www.multispeak.org/Version_4.1_Release", className = "org.multispeak.version_4.ReadingChangedNotification")
    @WebMethod(operationName = "ReadingChangedNotification", action = "ReadingChangedNotification")
    @ResponseWrapper(localName = "ReadingChangedNotificationResponse", targetNamespace = "http://www.multispeak.org/Version_4.1_Release", className = "org.multispeak.version_4.ReadingChangedNotificationResponse")
    public ArrayOfErrorObject ReadingChangedNotification(
            @WebParam(name = "changedMeterReads", targetNamespace = "http://www.multispeak.org/Version_4.1_Release")
                    ArrayOfMeterReading1 changedMeterReads,
            @WebParam(name = "transactionID", targetNamespace = "http://www.multispeak.org/Version_4.1_Release")
                    java.lang.String transactionID
    );
}
