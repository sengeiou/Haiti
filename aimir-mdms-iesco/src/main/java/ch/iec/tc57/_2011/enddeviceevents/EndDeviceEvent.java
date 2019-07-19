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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Event detected by a device function associated with end device.
 * 
 * <p>EndDeviceEvent complex type에 대한 Java 클래스입니다.
 * 
 * <p>다음 스키마 단편이 이 클래스에 포함되는 필요한 콘텐츠를 지정합니다.
 * 
 * <pre>
 * &lt;complexType name="EndDeviceEvent">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="mRID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="createdDateTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="issuerID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="issuerTrackingID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="reason" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="severity" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="userID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Assets" type="{http://iec.ch/TC57/2011/EndDeviceEvents#}Asset" minOccurs="0"/>
 *         &lt;element name="EndDeviceEventDetails" type="{http://iec.ch/TC57/2011/EndDeviceEvents#}EndDeviceEventDetail" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="EndDeviceEventType">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="ref" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Names" type="{http://iec.ch/TC57/2011/EndDeviceEvents#}Name" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="status" type="{http://iec.ch/TC57/2011/EndDeviceEvents#}Status" minOccurs="0"/>
 *         &lt;element name="UsagePoint" type="{http://iec.ch/TC57/2011/EndDeviceEvents#}UsagePoint" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EndDeviceEvent", propOrder = {
    "mrid",
    "createdDateTime",
    "issuerID",
    "issuerTrackingID",
    "reason",
    "severity",
    "userID",
    "assets",
    "endDeviceEventDetails",
    "endDeviceEventType",
    "names",
    "status",
    "usagePoint"
})
public class EndDeviceEvent {

    @XmlElement(name = "mRID")
    protected String mrid;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar createdDateTime;
    protected String issuerID;
    protected String issuerTrackingID;
    protected String reason;
    protected String severity;
    protected String userID;
    @XmlElement(name = "Assets")
    protected Asset assets;
    @XmlElement(name = "EndDeviceEventDetails")
    protected List<EndDeviceEventDetail> endDeviceEventDetails;
    @XmlElement(name = "EndDeviceEventType", required = true)
    protected EndDeviceEvent.EndDeviceEventType endDeviceEventType;
    @XmlElement(name = "Names")
    protected List<Name> names;
    protected Status status;
    @XmlElement(name = "UsagePoint")
    protected UsagePoint usagePoint;

    /**
     * mrid 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMRID() {
        return mrid;
    }

    /**
     * mrid 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMRID(String value) {
        this.mrid = value;
    }

    /**
     * createdDateTime 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * createdDateTime 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCreatedDateTime(XMLGregorianCalendar value) {
        this.createdDateTime = value;
    }

    /**
     * issuerID 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIssuerID() {
        return issuerID;
    }

    /**
     * issuerID 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIssuerID(String value) {
        this.issuerID = value;
    }

    /**
     * issuerTrackingID 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIssuerTrackingID() {
        return issuerTrackingID;
    }

    /**
     * issuerTrackingID 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIssuerTrackingID(String value) {
        this.issuerTrackingID = value;
    }

    /**
     * reason 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReason() {
        return reason;
    }

    /**
     * reason 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReason(String value) {
        this.reason = value;
    }

    /**
     * severity 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeverity() {
        return severity;
    }

    /**
     * severity 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeverity(String value) {
        this.severity = value;
    }

    /**
     * userID 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserID() {
        return userID;
    }

    /**
     * userID 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserID(String value) {
        this.userID = value;
    }

    /**
     * assets 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link Asset }
     *     
     */
    public Asset getAssets() {
        return assets;
    }

    /**
     * assets 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link Asset }
     *     
     */
    public void setAssets(Asset value) {
        this.assets = value;
    }

    /**
     * Gets the value of the endDeviceEventDetails property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the endDeviceEventDetails property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEndDeviceEventDetails().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EndDeviceEventDetail }
     * 
     * 
     */
    public List<EndDeviceEventDetail> getEndDeviceEventDetails() {
        if (endDeviceEventDetails == null) {
            endDeviceEventDetails = new ArrayList<EndDeviceEventDetail>();
        }
        return this.endDeviceEventDetails;
    }

    /**
     * endDeviceEventType 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link EndDeviceEvent.EndDeviceEventType }
     *     
     */
    public EndDeviceEvent.EndDeviceEventType getEndDeviceEventType() {
        return endDeviceEventType;
    }

    /**
     * endDeviceEventType 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link EndDeviceEvent.EndDeviceEventType }
     *     
     */
    public void setEndDeviceEventType(EndDeviceEvent.EndDeviceEventType value) {
        this.endDeviceEventType = value;
    }

    /**
     * Gets the value of the names property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the names property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNames().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Name }
     * 
     * 
     */
    public List<Name> getNames() {
        if (names == null) {
            names = new ArrayList<Name>();
        }
        return this.names;
    }

    /**
     * status 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link Status }
     *     
     */
    public Status getStatus() {
        return status;
    }

    /**
     * status 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link Status }
     *     
     */
    public void setStatus(Status value) {
        this.status = value;
    }

    /**
     * usagePoint 속성의 값을 가져옵니다.
     * 
     * @return
     *     possible object is
     *     {@link UsagePoint }
     *     
     */
    public UsagePoint getUsagePoint() {
        return usagePoint;
    }

    /**
     * usagePoint 속성의 값을 설정합니다.
     * 
     * @param value
     *     allowed object is
     *     {@link UsagePoint }
     *     
     */
    public void setUsagePoint(UsagePoint value) {
        this.usagePoint = value;
    }


    /**
     * <p>anonymous complex type에 대한 Java 클래스입니다.
     * 
     * <p>다음 스키마 단편이 이 클래스에 포함되는 필요한 콘텐츠를 지정합니다.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="ref" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class EndDeviceEventType {

        @XmlAttribute(name = "ref")
        protected String ref;

        /**
         * ref 속성의 값을 가져옵니다.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getRef() {
            return ref;
        }

        /**
         * ref 속성의 값을 설정합니다.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setRef(String value) {
            this.ref = value;
        }

    }

}
