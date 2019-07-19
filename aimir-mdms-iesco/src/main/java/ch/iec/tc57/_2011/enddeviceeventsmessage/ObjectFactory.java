//
// 이 파일은 JAXB(JavaTM Architecture for XML Binding) 참조 구현 2.2.8-b130911.1802 버전을 통해 생성되었습니다. 
// <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>를 참조하십시오. 
// 이 파일을 수정하면 소스 스키마를 재컴파일할 때 수정 사항이 손실됩니다. 
// 생성 날짜: 2016.03.21 시간 04:26:27 PM CET 
//


package ch.iec.tc57._2011.enddeviceeventsmessage;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ch.iec.tc57._2011.enddeviceeventsmessage package. 
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

    private final static QName _CreatedEndDeviceEvents_QNAME = new QName("http://iec.ch/TC57/2011/EndDeviceEventsMessage", "CreatedEndDeviceEvents");
    private final static QName _ChangeEndDeviceEvents_QNAME = new QName("http://iec.ch/TC57/2011/EndDeviceEventsMessage", "ChangeEndDeviceEvents");
    private final static QName _CreateEndDeviceEvents_QNAME = new QName("http://iec.ch/TC57/2011/EndDeviceEventsMessage", "CreateEndDeviceEvents");
    private final static QName _EndDeviceEventsFaultMessage_QNAME = new QName("http://iec.ch/TC57/2011/EndDeviceEventsMessage", "EndDeviceEventsFaultMessage");
    private final static QName _CancelEndDeviceEvents_QNAME = new QName("http://iec.ch/TC57/2011/EndDeviceEventsMessage", "CancelEndDeviceEvents");
    private final static QName _DeleteEndDeviceEvents_QNAME = new QName("http://iec.ch/TC57/2011/EndDeviceEventsMessage", "DeleteEndDeviceEvents");
    private final static QName _ChangedEndDeviceEvents_QNAME = new QName("http://iec.ch/TC57/2011/EndDeviceEventsMessage", "ChangedEndDeviceEvents");
    private final static QName _ClosedEndDeviceEvents_QNAME = new QName("http://iec.ch/TC57/2011/EndDeviceEventsMessage", "ClosedEndDeviceEvents");
    private final static QName _DeletedEndDeviceEvents_QNAME = new QName("http://iec.ch/TC57/2011/EndDeviceEventsMessage", "DeletedEndDeviceEvents");
    private final static QName _CloseEndDeviceEvents_QNAME = new QName("http://iec.ch/TC57/2011/EndDeviceEventsMessage", "CloseEndDeviceEvents");
    private final static QName _EndDeviceEventsResponseMessage_QNAME = new QName("http://iec.ch/TC57/2011/EndDeviceEventsMessage", "EndDeviceEventsResponseMessage");
    private final static QName _CanceledEndDeviceEvents_QNAME = new QName("http://iec.ch/TC57/2011/EndDeviceEventsMessage", "CanceledEndDeviceEvents");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ch.iec.tc57._2011.enddeviceeventsmessage
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link EndDeviceEventsEventMessageType }
     * 
     */
    public EndDeviceEventsEventMessageType createEndDeviceEventsEventMessageType() {
        return new EndDeviceEventsEventMessageType();
    }

    /**
     * Create an instance of {@link EndDeviceEventsRequestMessageType }
     * 
     */
    public EndDeviceEventsRequestMessageType createEndDeviceEventsRequestMessageType() {
        return new EndDeviceEventsRequestMessageType();
    }

    /**
     * Create an instance of {@link EndDeviceEventsResponseMessageType }
     * 
     */
    public EndDeviceEventsResponseMessageType createEndDeviceEventsResponseMessageType() {
        return new EndDeviceEventsResponseMessageType();
    }

    /**
     * Create an instance of {@link EndDeviceEventsFaultMessageType }
     * 
     */
    public EndDeviceEventsFaultMessageType createEndDeviceEventsFaultMessageType() {
        return new EndDeviceEventsFaultMessageType();
    }

    /**
     * Create an instance of {@link EndDeviceEventsPayloadType }
     * 
     */
    public EndDeviceEventsPayloadType createEndDeviceEventsPayloadType() {
        return new EndDeviceEventsPayloadType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EndDeviceEventsEventMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://iec.ch/TC57/2011/EndDeviceEventsMessage", name = "CreatedEndDeviceEvents")
    public JAXBElement<EndDeviceEventsEventMessageType> createCreatedEndDeviceEvents(EndDeviceEventsEventMessageType value) {
        return new JAXBElement<EndDeviceEventsEventMessageType>(_CreatedEndDeviceEvents_QNAME, EndDeviceEventsEventMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EndDeviceEventsRequestMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://iec.ch/TC57/2011/EndDeviceEventsMessage", name = "ChangeEndDeviceEvents")
    public JAXBElement<EndDeviceEventsRequestMessageType> createChangeEndDeviceEvents(EndDeviceEventsRequestMessageType value) {
        return new JAXBElement<EndDeviceEventsRequestMessageType>(_ChangeEndDeviceEvents_QNAME, EndDeviceEventsRequestMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EndDeviceEventsRequestMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://iec.ch/TC57/2011/EndDeviceEventsMessage", name = "CreateEndDeviceEvents")
    public JAXBElement<EndDeviceEventsRequestMessageType> createCreateEndDeviceEvents(EndDeviceEventsRequestMessageType value) {
        return new JAXBElement<EndDeviceEventsRequestMessageType>(_CreateEndDeviceEvents_QNAME, EndDeviceEventsRequestMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EndDeviceEventsFaultMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://iec.ch/TC57/2011/EndDeviceEventsMessage", name = "EndDeviceEventsFaultMessage")
    public JAXBElement<EndDeviceEventsFaultMessageType> createEndDeviceEventsFaultMessage(EndDeviceEventsFaultMessageType value) {
        return new JAXBElement<EndDeviceEventsFaultMessageType>(_EndDeviceEventsFaultMessage_QNAME, EndDeviceEventsFaultMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EndDeviceEventsRequestMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://iec.ch/TC57/2011/EndDeviceEventsMessage", name = "CancelEndDeviceEvents")
    public JAXBElement<EndDeviceEventsRequestMessageType> createCancelEndDeviceEvents(EndDeviceEventsRequestMessageType value) {
        return new JAXBElement<EndDeviceEventsRequestMessageType>(_CancelEndDeviceEvents_QNAME, EndDeviceEventsRequestMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EndDeviceEventsRequestMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://iec.ch/TC57/2011/EndDeviceEventsMessage", name = "DeleteEndDeviceEvents")
    public JAXBElement<EndDeviceEventsRequestMessageType> createDeleteEndDeviceEvents(EndDeviceEventsRequestMessageType value) {
        return new JAXBElement<EndDeviceEventsRequestMessageType>(_DeleteEndDeviceEvents_QNAME, EndDeviceEventsRequestMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EndDeviceEventsEventMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://iec.ch/TC57/2011/EndDeviceEventsMessage", name = "ChangedEndDeviceEvents")
    public JAXBElement<EndDeviceEventsEventMessageType> createChangedEndDeviceEvents(EndDeviceEventsEventMessageType value) {
        return new JAXBElement<EndDeviceEventsEventMessageType>(_ChangedEndDeviceEvents_QNAME, EndDeviceEventsEventMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EndDeviceEventsEventMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://iec.ch/TC57/2011/EndDeviceEventsMessage", name = "ClosedEndDeviceEvents")
    public JAXBElement<EndDeviceEventsEventMessageType> createClosedEndDeviceEvents(EndDeviceEventsEventMessageType value) {
        return new JAXBElement<EndDeviceEventsEventMessageType>(_ClosedEndDeviceEvents_QNAME, EndDeviceEventsEventMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EndDeviceEventsEventMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://iec.ch/TC57/2011/EndDeviceEventsMessage", name = "DeletedEndDeviceEvents")
    public JAXBElement<EndDeviceEventsEventMessageType> createDeletedEndDeviceEvents(EndDeviceEventsEventMessageType value) {
        return new JAXBElement<EndDeviceEventsEventMessageType>(_DeletedEndDeviceEvents_QNAME, EndDeviceEventsEventMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EndDeviceEventsRequestMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://iec.ch/TC57/2011/EndDeviceEventsMessage", name = "CloseEndDeviceEvents")
    public JAXBElement<EndDeviceEventsRequestMessageType> createCloseEndDeviceEvents(EndDeviceEventsRequestMessageType value) {
        return new JAXBElement<EndDeviceEventsRequestMessageType>(_CloseEndDeviceEvents_QNAME, EndDeviceEventsRequestMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EndDeviceEventsResponseMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://iec.ch/TC57/2011/EndDeviceEventsMessage", name = "EndDeviceEventsResponseMessage")
    public JAXBElement<EndDeviceEventsResponseMessageType> createEndDeviceEventsResponseMessage(EndDeviceEventsResponseMessageType value) {
        return new JAXBElement<EndDeviceEventsResponseMessageType>(_EndDeviceEventsResponseMessage_QNAME, EndDeviceEventsResponseMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EndDeviceEventsEventMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://iec.ch/TC57/2011/EndDeviceEventsMessage", name = "CanceledEndDeviceEvents")
    public JAXBElement<EndDeviceEventsEventMessageType> createCanceledEndDeviceEvents(EndDeviceEventsEventMessageType value) {
        return new JAXBElement<EndDeviceEventsEventMessageType>(_CanceledEndDeviceEvents_QNAME, EndDeviceEventsEventMessageType.class, null, value);
    }

}
