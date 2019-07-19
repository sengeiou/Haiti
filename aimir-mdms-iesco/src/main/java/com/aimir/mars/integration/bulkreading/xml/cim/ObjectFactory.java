package com.aimir.mars.integration.bulkreading.xml.cim;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.nuritelecom.aimir.com.cim package. 
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

    private final static QName _MeterReadings_QNAME = new QName("http://com.aimir.nuritelecom.com/CIM", "MeterReadings");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.nuritelecom.aimir.com.cim
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link MeterReadingsType }
     * 
     */
    public MeterReadingsType createMeterReadingsType() {
        return new MeterReadingsType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MeterReadingsType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://com.aimir.nuritelecom.com/CIM", name = "MeterReadings")
    public JAXBElement<MeterReadingsType> createMeterReadings(MeterReadingsType value) {
        return new JAXBElement<MeterReadingsType>(_MeterReadings_QNAME, MeterReadingsType.class, null, value);
    }

}
