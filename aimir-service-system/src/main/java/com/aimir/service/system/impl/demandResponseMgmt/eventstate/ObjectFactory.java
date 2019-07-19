
package com.aimir.service.system.impl.demandResponseMgmt.eventstate;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.openadr.dras.eventstate package. 
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

    private final static QName _EventState_QNAME = new QName("http://www.openadr.org/DRAS/EventState", "eventState");
    private final static QName _EventStatus_QNAME = new QName("http://www.openadr.org/DRAS/EventState", "EventStatus");
    private final static QName _OperationModeValue_QNAME = new QName("http://www.openadr.org/DRAS/EventState", "OperationModeValue");
    private final static QName _SimpleClientEventDataOperationModeSchedule_QNAME = new QName("http://www.openadr.org/DRAS/EventState", "operationModeSchedule");
    private final static QName _EventStateDrEventData_QNAME = new QName("http://www.openadr.org/DRAS/EventState", "drEventData");
    private final static QName _EventStateCustomData_QNAME = new QName("http://www.openadr.org/DRAS/EventState", "customData");
    private static final QName _ListOfEventState_QNAME = new QName("http://www.openadr.org/DRAS/EventState", "listOfEventState");
    
    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openadr.dras.eventstate
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link org.openadr.dras.eventstate.CustomData }
     * 
     */
    public CustomData createCustomData() {
        return new CustomData();
    }

    /**
     * Create an instance of {@link SimpleClientEventData.OperationModeSchedule }
     * 
     */
    public SimpleClientEventData.OperationModeSchedule createSimpleClientEventDataOperationModeSchedule() {
        return new SimpleClientEventData.OperationModeSchedule();
    }

    /**
     * Create an instance of {@link EventState }
     * 
     */
    public EventState createEventState() {
        return new EventState();
    }

    /**
     * Create an instance of {@link EventInfoInstance }
     * 
     */
    public EventInfoInstance createEventInfoInstance() {
        return new EventInfoInstance();
    }

    /**
     * Create an instance of {@link EventInfoTypeID }
     * 
     */
    public EventInfoTypeID createEventInfoTypeID() {
        return new EventInfoTypeID();
    }

    /**
     * Create an instance of {@link EventInfoValue }
     * 
     */
    public EventInfoValue createEventInfoValue() {
        return new EventInfoValue();
    }

    /**
     * Create an instance of {@link EventStateConfirmation }
     * 
     */
    public EventStateConfirmation createEventStateConfirmation() {
        return new EventStateConfirmation();
    }

    /**
     * Create an instance of {@link GeneralInfoValue }
     * 
     */
    public GeneralInfoValue createGeneralInfoValue() {
        return new GeneralInfoValue();
    }

    /**
     * Create an instance of {@link SimpleClientEventData }
     * 
     */
    public SimpleClientEventData createSimpleClientEventData() {
        return new SimpleClientEventData();
    }

    /**
     * Create an instance of {@link GeneralInfoInstance }
     * 
     */
    public GeneralInfoInstance createGeneralInfoInstance() {
        return new GeneralInfoInstance();
    }

    /**
     * Create an instance of {@link SmartClientDREventData }
     * 
     */
    public SmartClientDREventData createSmartClientDREventData() {
        return new SmartClientDREventData();
    }

    /**
     * Create an instance of {@link ListOfEventStates }
     * 
     */
    public ListOfEventStates createListOfEventStates() {
        return new ListOfEventStates();
    }

    /**
     * Create an instance of {@link EventState.CustomData }
     * 
     */
    public EventState.CustomData createEventStateCustomData() {
        return new EventState.CustomData();
    }

    /**
     * Create an instance of {@link OperationState }
     * 
     */
    public OperationState createOperationState() {
        return new OperationState();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EventState }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openadr.org/DRAS/EventState", name = "eventState")
    public JAXBElement<EventState> createEventState(EventState value) {
        return new JAXBElement<EventState>(_EventState_QNAME, EventState.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openadr.org/DRAS/EventState", name = "EventStatus")
    public JAXBElement<String> createEventStatus(String value) {
        return new JAXBElement<String>(_EventStatus_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openadr.org/DRAS/EventState", name = "OperationModeValue")
    public JAXBElement<String> createOperationModeValue(String value) {
        return new JAXBElement<String>(_OperationModeValue_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleClientEventData.OperationModeSchedule }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openadr.org/DRAS/EventState", name = "operationModeSchedule", scope = SimpleClientEventData.class)
    public JAXBElement<SimpleClientEventData.OperationModeSchedule> createSimpleClientEventDataOperationModeSchedule(SimpleClientEventData.OperationModeSchedule value) {
        return new JAXBElement<SimpleClientEventData.OperationModeSchedule>(_SimpleClientEventDataOperationModeSchedule_QNAME, SimpleClientEventData.OperationModeSchedule.class, SimpleClientEventData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SmartClientDREventData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openadr.org/DRAS/EventState", name = "drEventData", scope = EventState.class)
    public JAXBElement<SmartClientDREventData> createEventStateDrEventData(SmartClientDREventData value) {
        return new JAXBElement<SmartClientDREventData>(_EventStateDrEventData_QNAME, SmartClientDREventData.class, EventState.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EventState.CustomData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openadr.org/DRAS/EventState", name = "customData", scope = EventState.class)
    public JAXBElement<EventState.CustomData> createEventStateCustomData(EventState.CustomData value) {
        return new JAXBElement<EventState.CustomData>(_EventStateCustomData_QNAME, EventState.CustomData.class, EventState.class, value);
    }
    
    @XmlElementDecl(namespace = "http://www.openadr.org/DRAS/EventState", name = "listOfEventStates", scope = ListOfEventStates.class)
    public JAXBElement<ListOfEventStates> createListOfEventState(ListOfEventStates value)
    {
        return new JAXBElement<ListOfEventStates>(_ListOfEventState_QNAME, ListOfEventStates.class, null, value);
    }

}
