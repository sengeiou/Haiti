//
// 이 파일은 JAXB(JavaTM Architecture for XML Binding) 참조 구현 2.2.8-b130911.1802 버전을 통해 생성되었습니다. 
// <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>를 참조하십시오. 
// 이 파일을 수정하면 소스 스키마를 재컴파일할 때 수정 사항이 손실됩니다. 
// 생성 날짜: 2016.03.21 시간 04:26:27 PM CET 
//


package ch.iec.tc57._2011.enddeviceevents;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>EndDeviceEvents complex type에 대한 Java 클래스입니다.
 * 
 * <p>다음 스키마 단편이 이 클래스에 포함되는 필요한 콘텐츠를 지정합니다.
 * 
 * <pre>
 * &lt;complexType name="EndDeviceEvents">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="EndDeviceEvent" type="{http://iec.ch/TC57/2011/EndDeviceEvents#}EndDeviceEvent" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="EndDeviceEventType" type="{http://iec.ch/TC57/2011/EndDeviceEvents#}EndDeviceEventType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EndDeviceEvents", propOrder = {
    "endDeviceEvent",
    "endDeviceEventType"
})
public class EndDeviceEvents {

    @XmlElement(name = "EndDeviceEvent")
    protected List<EndDeviceEvent> endDeviceEvent;
    @XmlElement(name = "EndDeviceEventType")
    protected List<EndDeviceEventType> endDeviceEventType;

    /**
     * Gets the value of the endDeviceEvent property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the endDeviceEvent property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEndDeviceEvent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EndDeviceEvent }
     * 
     * 
     */
    public List<EndDeviceEvent> getEndDeviceEvent() {
        if (endDeviceEvent == null) {
            endDeviceEvent = new ArrayList<EndDeviceEvent>();
        }
        return this.endDeviceEvent;
    }

    /**
     * Gets the value of the endDeviceEventType property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the endDeviceEventType property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEndDeviceEventType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EndDeviceEventType }
     * 
     * 
     */
    public List<EndDeviceEventType> getEndDeviceEventType() {
        if (endDeviceEventType == null) {
            endDeviceEventType = new ArrayList<EndDeviceEventType>();
        }
        return this.endDeviceEventType;
    }

}
