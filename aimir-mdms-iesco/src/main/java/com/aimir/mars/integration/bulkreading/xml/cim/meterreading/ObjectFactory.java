package com.aimir.mars.integration.bulkreading.xml.cim.meterreading;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.nuritelecom.aimir.com.cim.meterreading package. 
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

    private final static QName _DeviceList_QNAME = new QName("http://com.aimir.nuritelecom.com/CIM/MeterReading", "deviceList");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.nuritelecom.aimir.com.cim.meterreading
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DeviceListType }
     * 
     */
    public DeviceListType createDeviceListType() {
        return new DeviceListType();
    }

    /**
     * Create an instance of {@link DeviceListType.Device }
     * 
     */
    public DeviceListType.Device createDeviceListTypeDevice() {
        return new DeviceListType.Device();
    }

    /**
     * Create an instance of {@link DeviceListType.Device.InitialMeasurementDataList }
     * 
     */
    public DeviceListType.Device.InitialMeasurementDataList createDeviceListTypeDeviceInitialMeasurementDataList() {
        return new DeviceListType.Device.InitialMeasurementDataList();
    }

    /**
     * Create an instance of {@link DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData }
     * 
     */
    public DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData createDeviceListTypeDeviceInitialMeasurementDataListInitialMeasurementData() {
        return new DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData();
    }

    /**
     * Create an instance of {@link DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData.PreVEE }
     * 
     */
    public DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData.PreVEE createDeviceListTypeDeviceInitialMeasurementDataListInitialMeasurementDataPreVEE() {
        return new DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData.PreVEE();
    }

    /**
     * Create an instance of {@link DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData.PreVEE.Msrs }
     * 
     */
    public DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData.PreVEE.Msrs createDeviceListTypeDeviceInitialMeasurementDataListInitialMeasurementDataPreVEEMsrs() {
        return new DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData.PreVEE.Msrs();
    }

    /**
     * Create an instance of {@link DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData.PreVEE.Msrs.ML }
     * 
     */
    public DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData.PreVEE.Msrs.ML createDeviceListTypeDeviceInitialMeasurementDataListInitialMeasurementDataPreVEEMsrsML() {
        return new DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData.PreVEE.Msrs.ML();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeviceListType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://com.aimir.nuritelecom.com/CIM/MeterReading", name = "deviceList")
    public JAXBElement<DeviceListType> createDeviceList(DeviceListType value) {
        return new JAXBElement<DeviceListType>(_DeviceList_QNAME, DeviceListType.class, null, value);
    }

}
