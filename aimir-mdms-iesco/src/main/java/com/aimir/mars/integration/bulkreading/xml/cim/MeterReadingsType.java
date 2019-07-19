package com.aimir.mars.integration.bulkreading.xml.cim;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import com.aimir.mars.integration.bulkreading.xml.cim.header.MessageHeaderType;
import com.aimir.mars.integration.bulkreading.xml.cim.meterreading.DeviceListType;


/**
 * <p>MeterReadings_Type complex type에 대한 Java 클래스입니다.
 * 
 * <p>다음 스키마 단편이 이 클래스에 포함되는 필요한 콘텐츠를 지정합니다.
 * 
 * <pre>
 * &lt;complexType name="MeterReadings_Type"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Header" type="{http://com.aimir.nuritelecom.com/CIM/Header}MessageHeader_Type"/&gt;
 *         &lt;element ref="{http://com.aimir.nuritelecom.com/CIM/MeterReading}deviceList" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MeterReadings_Type", propOrder = {
    "header",
    "deviceList"
})
public class MeterReadingsType {

    @XmlElement(name = "Header", required = true)
    protected MessageHeaderType header;
    @XmlElement(namespace = "http://com.aimir.nuritelecom.com/CIM/MeterReading")
    protected List<DeviceListType> deviceList;

    /**
     * header 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link MessageHeaderType }
     *     
     */
    public MessageHeaderType getHeader() {
        return header;
    }

    /**
     * header 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageHeaderType }
     *     
     */
    public void setHeader(MessageHeaderType value) {
        this.header = value;
    }

    /**
     * Gets the value of the deviceList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the deviceList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDeviceList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DeviceListType }
     * 
     * 
     */
    public List<DeviceListType> getDeviceList() {
        if (deviceList == null) {
            deviceList = new ArrayList<DeviceListType>();
        }
        return this.deviceList;
    }

}
