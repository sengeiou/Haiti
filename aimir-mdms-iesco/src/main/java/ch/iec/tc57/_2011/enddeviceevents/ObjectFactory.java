//
// 이 파일은 JAXB(JavaTM Architecture for XML Binding) 참조 구현 2.2.8-b130911.1802 버전을 통해 생성되었습니다. 
// <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>를 참조하십시오. 
// 이 파일을 수정하면 소스 스키마를 재컴파일할 때 수정 사항이 손실됩니다. 
// 생성 날짜: 2016.03.21 시간 04:26:27 PM CET 
//


package ch.iec.tc57._2011.enddeviceevents;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ch.iec.tc57._2011.enddeviceevents_ package. 
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

    private final static QName _EndDeviceEvents_QNAME = new QName("http://iec.ch/TC57/2011/EndDeviceEvents#", "EndDeviceEvents");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ch.iec.tc57._2011.enddeviceevents_
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link EndDeviceEvent }
     * 
     */
    public EndDeviceEvent createEndDeviceEvent() {
        return new EndDeviceEvent();
    }

    /**
     * Create an instance of {@link EndDeviceEvents }
     * 
     */
    public EndDeviceEvents createEndDeviceEvents() {
        return new EndDeviceEvents();
    }

    /**
     * Create an instance of {@link EndDeviceEventDetail }
     * 
     */
    public EndDeviceEventDetail createEndDeviceEventDetail() {
        return new EndDeviceEventDetail();
    }

    /**
     * Create an instance of {@link Status }
     * 
     */
    public Status createStatus() {
        return new Status();
    }

    /**
     * Create an instance of {@link ch.iec.tc57._2011.enddeviceevents_.EndDeviceEventType }
     * 
     */
    public ch.iec.tc57._2011.enddeviceevents.EndDeviceEventType createEndDeviceEventType() {
        return new ch.iec.tc57._2011.enddeviceevents.EndDeviceEventType();
    }

    /**
     * Create an instance of {@link UsagePoint }
     * 
     */
    public UsagePoint createUsagePoint() {
        return new UsagePoint();
    }

    /**
     * Create an instance of {@link NameTypeAuthority }
     * 
     */
    public NameTypeAuthority createNameTypeAuthority() {
        return new NameTypeAuthority();
    }

    /**
     * Create an instance of {@link Asset }
     * 
     */
    public Asset createAsset() {
        return new Asset();
    }

    /**
     * Create an instance of {@link NameType }
     * 
     */
    public NameType createNameType() {
        return new NameType();
    }

    /**
     * Create an instance of {@link Name }
     * 
     */
    public Name createName() {
        return new Name();
    }

    /**
     * Create an instance of {@link EndDeviceEvent.EndDeviceEventType }
     * 
     */
    public EndDeviceEvent.EndDeviceEventType createEndDeviceEventEndDeviceEventType() {
        return new EndDeviceEvent.EndDeviceEventType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EndDeviceEvents }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://iec.ch/TC57/2011/EndDeviceEvents#", name = "EndDeviceEvents")
    public JAXBElement<EndDeviceEvents> createEndDeviceEvents(EndDeviceEvents value) {
        return new JAXBElement<EndDeviceEvents>(_EndDeviceEvents_QNAME, EndDeviceEvents.class, null, value);
    }

}
