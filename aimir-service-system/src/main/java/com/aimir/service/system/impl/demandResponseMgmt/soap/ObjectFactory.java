
package com.aimir.service.system.impl.demandResponseMgmt.soap;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.openadr.dras.drasclientsoap package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GetEventStates_QNAME = new QName("http://www.openadr.org/DRAS/DRASClientSOAP/", "GetEventStates");
    private final static QName _SetEventStateConfirmation_QNAME = new QName("http://www.openadr.org/DRAS/DRASClientSOAP/", "SetEventStateConfirmation");
    private final static QName _SetEventStateConfirmationResponse_QNAME = new QName("http://www.openadr.org/DRAS/DRASClientSOAP/", "SetEventStateConfirmationResponse");
    private final static QName _GetEventStatesResponse_QNAME = new QName("http://www.openadr.org/DRAS/DRASClientSOAP/", "GetEventStatesResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openadr.dras.drasclientsoap
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SetEventStateConfirmationResponse }
     * 
     */
    public SetEventStateConfirmationResponse createSetEventStateConfirmationResponse() {
        return new SetEventStateConfirmationResponse();
    }

    /**
     * Create an instance of {@link GetEventStatesResponse }
     * 
     */
    public GetEventStatesResponse createGetEventStatesResponse() {
        return new GetEventStatesResponse();
    }

    /**
     * Create an instance of {@link GetEventStates }
     * 
     */
    public GetEventStates createGetEventStates() {
        return new GetEventStates();
    }

    /**
     * Create an instance of {@link SetEventStateConfirmation }
     * 
     */
    public SetEventStateConfirmation createSetEventStateConfirmation() {
        return new SetEventStateConfirmation();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetEventStates }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openadr.org/DRAS/DRASClientSOAP/", name = "GetEventStates")
    public JAXBElement<GetEventStates> createGetEventStates(GetEventStates value) {
        return new JAXBElement<GetEventStates>(_GetEventStates_QNAME, GetEventStates.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetEventStateConfirmation }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openadr.org/DRAS/DRASClientSOAP/", name = "SetEventStateConfirmation")
    public JAXBElement<SetEventStateConfirmation> createSetEventStateConfirmation(SetEventStateConfirmation value) {
        return new JAXBElement<SetEventStateConfirmation>(_SetEventStateConfirmation_QNAME, SetEventStateConfirmation.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetEventStateConfirmationResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openadr.org/DRAS/DRASClientSOAP/", name = "SetEventStateConfirmationResponse")
    public JAXBElement<SetEventStateConfirmationResponse> createSetEventStateConfirmationResponse(SetEventStateConfirmationResponse value) {
        return new JAXBElement<SetEventStateConfirmationResponse>(_SetEventStateConfirmationResponse_QNAME, SetEventStateConfirmationResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetEventStatesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openadr.org/DRAS/DRASClientSOAP/", name = "GetEventStatesResponse")
    public JAXBElement<GetEventStatesResponse> createGetEventStatesResponse(GetEventStatesResponse value) {
        return new JAXBElement<GetEventStatesResponse>(_GetEventStatesResponse_QNAME, GetEventStatesResponse.class, null, value);
    }

}
