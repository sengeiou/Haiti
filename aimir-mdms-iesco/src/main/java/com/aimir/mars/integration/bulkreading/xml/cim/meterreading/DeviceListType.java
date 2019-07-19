package com.aimir.mars.integration.bulkreading.xml.cim.meterreading;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>deviceListType complex type에 대한 Java 클래스입니다.
 * 
 * <p>다음 스키마 단편이 이 클래스에 포함되는 필요한 콘텐츠를 지정합니다.
 * 
 * <pre>
 * &lt;complexType name="deviceListType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="device" maxOccurs="unbounded" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="headEndExternalId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                   &lt;element name="deviceIdentifierNumber" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                   &lt;element name="initialMeasurementDataList"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="initialMeasurementData" maxOccurs="unbounded" minOccurs="0"&gt;
 *                               &lt;complexType&gt;
 *                                 &lt;complexContent&gt;
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                     &lt;sequence&gt;
 *                                       &lt;element name="preVEE"&gt;
 *                                         &lt;complexType&gt;
 *                                           &lt;complexContent&gt;
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                               &lt;sequence&gt;
 *                                                 &lt;element name="mcIdN" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                                                 &lt;element name="stDt" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *                                                 &lt;element name="enDt" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *                                                 &lt;element name="msrs"&gt;
 *                                                   &lt;complexType&gt;
 *                                                     &lt;complexContent&gt;
 *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                                         &lt;sequence&gt;
 *                                                           &lt;element name="mL" maxOccurs="unbounded" minOccurs="0"&gt;
 *                                                             &lt;complexType&gt;
 *                                                               &lt;complexContent&gt;
 *                                                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                                                   &lt;sequence&gt;
 *                                                                     &lt;element name="ts" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                                                                     &lt;element name="meterDt" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *                                                                     &lt;element name="captureDt" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *                                                                     &lt;element name="captureDeviceID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                                                                     &lt;element name="captureDeviceType" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                                                                     &lt;element name="q" type="{http://www.w3.org/2001/XMLSchema}float"/&gt;
 *                                                                     &lt;element name="fc" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                                                                   &lt;/sequence&gt;
 *                                                                 &lt;/restriction&gt;
 *                                                               &lt;/complexContent&gt;
 *                                                             &lt;/complexType&gt;
 *                                                           &lt;/element&gt;
 *                                                         &lt;/sequence&gt;
 *                                                       &lt;/restriction&gt;
 *                                                     &lt;/complexContent&gt;
 *                                                   &lt;/complexType&gt;
 *                                                 &lt;/element&gt;
 *                                               &lt;/sequence&gt;
 *                                             &lt;/restriction&gt;
 *                                           &lt;/complexContent&gt;
 *                                         &lt;/complexType&gt;
 *                                       &lt;/element&gt;
 *                                     &lt;/sequence&gt;
 *                                   &lt;/restriction&gt;
 *                                 &lt;/complexContent&gt;
 *                               &lt;/complexType&gt;
 *                             &lt;/element&gt;
 *                           &lt;/sequence&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "deviceListType", propOrder = {
    "device"
})
public class DeviceListType {

    protected List<DeviceListType.Device> device;

    /**
     * Gets the value of the device property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the device property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDevice().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DeviceListType.Device }
     * 
     * 
     */
    public List<DeviceListType.Device> getDevice() {
        if (device == null) {
            device = new ArrayList<DeviceListType.Device>();
        }
        return this.device;
    }


    /**
     * <p>anonymous complex type에 대한 Java 클래스입니다.
     * 
     * <p>다음 스키마 단편이 이 클래스에 포함되는 필요한 콘텐츠를 지정합니다.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="headEndExternalId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *         &lt;element name="deviceIdentifierNumber" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *         &lt;element name="issuerID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *         &lt;element name="initialMeasurementDataList"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="initialMeasurementData" maxOccurs="unbounded" minOccurs="0"&gt;
     *                     &lt;complexType&gt;
     *                       &lt;complexContent&gt;
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                           &lt;sequence&gt;
     *                             &lt;element name="preVEE"&gt;
     *                               &lt;complexType&gt;
     *                                 &lt;complexContent&gt;
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                                     &lt;sequence&gt;
     *                                       &lt;element name="mcIdN" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *                                       &lt;element name="stDt" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
     *                                       &lt;element name="enDt" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
     *                                       &lt;element name="msrs"&gt;
     *                                         &lt;complexType&gt;
     *                                           &lt;complexContent&gt;
     *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                                               &lt;sequence&gt;
     *                                                 &lt;element name="mL" maxOccurs="unbounded" minOccurs="0"&gt;
     *                                                   &lt;complexType&gt;
     *                                                     &lt;complexContent&gt;
     *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                                                         &lt;sequence&gt;
     *                                                           &lt;element name="ts" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *                                                           &lt;element name="meterDt" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
     *                                                           &lt;element name="captureDt" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
     *                                                           &lt;element name="captureDeviceID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *                                                           &lt;element name="captureDeviceType" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *                                                           &lt;element name="q" type="{http://www.w3.org/2001/XMLSchema}float"/&gt;
     *                                                           &lt;element name="fc" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *                                                         &lt;/sequence&gt;
     *                                                       &lt;/restriction&gt;
     *                                                     &lt;/complexContent&gt;
     *                                                   &lt;/complexType&gt;
     *                                                 &lt;/element&gt;
     *                                               &lt;/sequence&gt;
     *                                             &lt;/restriction&gt;
     *                                           &lt;/complexContent&gt;
     *                                         &lt;/complexType&gt;
     *                                       &lt;/element&gt;
     *                                     &lt;/sequence&gt;
     *                                   &lt;/restriction&gt;
     *                                 &lt;/complexContent&gt;
     *                               &lt;/complexType&gt;
     *                             &lt;/element&gt;
     *                           &lt;/sequence&gt;
     *                         &lt;/restriction&gt;
     *                       &lt;/complexContent&gt;
     *                     &lt;/complexType&gt;
     *                   &lt;/element&gt;
     *                 &lt;/sequence&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "headEndExternalId",
        "deviceIdentifierNumber",
        "issuerID",
        "initialMeasurementDataList"
    })
    public static class Device {

        @XmlElement(required = true)
        protected String headEndExternalId;
        @XmlElement(required = true)
        protected String deviceIdentifierNumber;
        protected String issuerID;
        @XmlElement(required = true)
        protected DeviceListType.Device.InitialMeasurementDataList initialMeasurementDataList;

        /**
         * headEndExternalId 속성의 값을 가져옵니다.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getHeadEndExternalId() {
            return headEndExternalId;
        }

        /**
         * headEndExternalId 속성의 값을 설정합니다.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setHeadEndExternalId(String value) {
            this.headEndExternalId = value;
        }

        /**
         * deviceIdentifierNumber 속성의 값을 가져옵니다.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDeviceIdentifierNumber() {
            return deviceIdentifierNumber;
        }

        /**
         * deviceIdentifierNumber 속성의 값을 설정합니다.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDeviceIdentifierNumber(String value) {
            this.deviceIdentifierNumber = value;
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
         * initialMeasurementDataList 속성의 값을 가져옵니다.
         * 
         * @return
         *     possible object is
         *     {@link DeviceListType.Device.InitialMeasurementDataList }
         *     
         */
        public DeviceListType.Device.InitialMeasurementDataList getInitialMeasurementDataList() {
            return initialMeasurementDataList;
        }

        /**
         * initialMeasurementDataList 속성의 값을 설정합니다.
         * 
         * @param value
         *     allowed object is
         *     {@link DeviceListType.Device.InitialMeasurementDataList }
         *     
         */
        public void setInitialMeasurementDataList(DeviceListType.Device.InitialMeasurementDataList value) {
            this.initialMeasurementDataList = value;
        }


        /**
         * <p>anonymous complex type에 대한 Java 클래스입니다.
         * 
         * <p>다음 스키마 단편이 이 클래스에 포함되는 필요한 콘텐츠를 지정합니다.
         * 
         * <pre>
         * &lt;complexType&gt;
         *   &lt;complexContent&gt;
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *       &lt;sequence&gt;
         *         &lt;element name="initialMeasurementData" maxOccurs="unbounded" minOccurs="0"&gt;
         *           &lt;complexType&gt;
         *             &lt;complexContent&gt;
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                 &lt;sequence&gt;
         *                   &lt;element name="preVEE"&gt;
         *                     &lt;complexType&gt;
         *                       &lt;complexContent&gt;
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                           &lt;sequence&gt;
         *                             &lt;element name="mcIdN" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
         *                             &lt;element name="stDt" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
         *                             &lt;element name="enDt" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
         *                             &lt;element name="msrs"&gt;
         *                               &lt;complexType&gt;
         *                                 &lt;complexContent&gt;
         *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                                     &lt;sequence&gt;
         *                                       &lt;element name="mL" maxOccurs="unbounded" minOccurs="0"&gt;
         *                                         &lt;complexType&gt;
         *                                           &lt;complexContent&gt;
         *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                                               &lt;sequence&gt;
         *                                                 &lt;element name="ts" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
         *                                                 &lt;element name="meterDt" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
         *                                                 &lt;element name="captureDt" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
         *                                                 &lt;element name="captureDeviceID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
         *                                                 &lt;element name="captureDeviceType" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
         *                                                 &lt;element name="q" type="{http://www.w3.org/2001/XMLSchema}float"/&gt;
         *                                                 &lt;element name="fc" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
         *                                               &lt;/sequence&gt;
         *                                             &lt;/restriction&gt;
         *                                           &lt;/complexContent&gt;
         *                                         &lt;/complexType&gt;
         *                                       &lt;/element&gt;
         *                                     &lt;/sequence&gt;
         *                                   &lt;/restriction&gt;
         *                                 &lt;/complexContent&gt;
         *                               &lt;/complexType&gt;
         *                             &lt;/element&gt;
         *                           &lt;/sequence&gt;
         *                         &lt;/restriction&gt;
         *                       &lt;/complexContent&gt;
         *                     &lt;/complexType&gt;
         *                   &lt;/element&gt;
         *                 &lt;/sequence&gt;
         *               &lt;/restriction&gt;
         *             &lt;/complexContent&gt;
         *           &lt;/complexType&gt;
         *         &lt;/element&gt;
         *       &lt;/sequence&gt;
         *     &lt;/restriction&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "initialMeasurementData"
        })
        public static class InitialMeasurementDataList {

            protected List<DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData> initialMeasurementData;

            /**
             * Gets the value of the initialMeasurementData property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the initialMeasurementData property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getInitialMeasurementData().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData }
             * 
             * 
             */
            public List<DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData> getInitialMeasurementData() {
                if (initialMeasurementData == null) {
                    initialMeasurementData = new ArrayList<DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData>();
                }
                return this.initialMeasurementData;
            }


            /**
             * <p>anonymous complex type에 대한 Java 클래스입니다.
             * 
             * <p>다음 스키마 단편이 이 클래스에 포함되는 필요한 콘텐츠를 지정합니다.
             * 
             * <pre>
             * &lt;complexType&gt;
             *   &lt;complexContent&gt;
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
             *       &lt;sequence&gt;
             *         &lt;element name="preVEE"&gt;
             *           &lt;complexType&gt;
             *             &lt;complexContent&gt;
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
             *                 &lt;sequence&gt;
             *                   &lt;element name="mcIdN" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
             *                   &lt;element name="stDt" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
             *                   &lt;element name="enDt" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
             *                   &lt;element name="msrs"&gt;
             *                     &lt;complexType&gt;
             *                       &lt;complexContent&gt;
             *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
             *                           &lt;sequence&gt;
             *                             &lt;element name="mL" maxOccurs="unbounded" minOccurs="0"&gt;
             *                               &lt;complexType&gt;
             *                                 &lt;complexContent&gt;
             *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
             *                                     &lt;sequence&gt;
             *                                       &lt;element name="ts" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
             *                                       &lt;element name="meterDt" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
             *                                       &lt;element name="captureDt" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
             *                                       &lt;element name="captureDeviceID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
             *                                       &lt;element name="captureDeviceType" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
             *                                       &lt;element name="q" type="{http://www.w3.org/2001/XMLSchema}float"/&gt;
             *                                       &lt;element name="fc" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
             *                                     &lt;/sequence&gt;
             *                                   &lt;/restriction&gt;
             *                                 &lt;/complexContent&gt;
             *                               &lt;/complexType&gt;
             *                             &lt;/element&gt;
             *                           &lt;/sequence&gt;
             *                         &lt;/restriction&gt;
             *                       &lt;/complexContent&gt;
             *                     &lt;/complexType&gt;
             *                   &lt;/element&gt;
             *                 &lt;/sequence&gt;
             *               &lt;/restriction&gt;
             *             &lt;/complexContent&gt;
             *           &lt;/complexType&gt;
             *         &lt;/element&gt;
             *       &lt;/sequence&gt;
             *     &lt;/restriction&gt;
             *   &lt;/complexContent&gt;
             * &lt;/complexType&gt;
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "preVEE"
            })
            public static class InitialMeasurementData {

                @XmlElement(required = true)
                protected DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData.PreVEE preVEE;

                /**
                 * preVEE 속성의 값을 가져옵니다.
                 * 
                 * @return
                 *     possible object is
                 *     {@link DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData.PreVEE }
                 *     
                 */
                public DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData.PreVEE getPreVEE() {
                    return preVEE;
                }

                /**
                 * preVEE 속성의 값을 설정합니다.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData.PreVEE }
                 *     
                 */
                public void setPreVEE(DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData.PreVEE value) {
                    this.preVEE = value;
                }


                /**
                 * <p>anonymous complex type에 대한 Java 클래스입니다.
                 * 
                 * <p>다음 스키마 단편이 이 클래스에 포함되는 필요한 콘텐츠를 지정합니다.
                 * 
                 * <pre>
                 * &lt;complexType&gt;
                 *   &lt;complexContent&gt;
                 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
                 *       &lt;sequence&gt;
                 *         &lt;element name="mcIdN" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
                 *         &lt;element name="stDt" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
                 *         &lt;element name="enDt" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
                 *         &lt;element name="msrs"&gt;
                 *           &lt;complexType&gt;
                 *             &lt;complexContent&gt;
                 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
                 *                 &lt;sequence&gt;
                 *                   &lt;element name="mL" maxOccurs="unbounded" minOccurs="0"&gt;
                 *                     &lt;complexType&gt;
                 *                       &lt;complexContent&gt;
                 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
                 *                           &lt;sequence&gt;
                 *                             &lt;element name="ts" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
                 *                             &lt;element name="meterDt" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
                 *                             &lt;element name="captureDt" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
                 *                             &lt;element name="captureDeviceID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
                 *                             &lt;element name="captureDeviceType" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
                 *                             &lt;element name="q" type="{http://www.w3.org/2001/XMLSchema}float"/&gt;
                 *                             &lt;element name="fc" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
                 *                           &lt;/sequence&gt;
                 *                         &lt;/restriction&gt;
                 *                       &lt;/complexContent&gt;
                 *                     &lt;/complexType&gt;
                 *                   &lt;/element&gt;
                 *                 &lt;/sequence&gt;
                 *               &lt;/restriction&gt;
                 *             &lt;/complexContent&gt;
                 *           &lt;/complexType&gt;
                 *         &lt;/element&gt;
                 *       &lt;/sequence&gt;
                 *     &lt;/restriction&gt;
                 *   &lt;/complexContent&gt;
                 * &lt;/complexType&gt;
                 * </pre>
                 * 
                 * 
                 */
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "mcIdN",
                    "stDt",
                    "enDt",
                    "msrs"
                })
                public static class PreVEE {

                    @XmlElement(required = true)
                    protected String mcIdN;
                    @XmlElement(required = true)
                    @XmlSchemaType(name = "dateTime")
                    protected XMLGregorianCalendar stDt;
                    @XmlElement(required = true)
                    @XmlSchemaType(name = "dateTime")
                    protected XMLGregorianCalendar enDt;
                    @XmlElement(required = true)
                    protected DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData.PreVEE.Msrs msrs;

                    /**
                     * mcIdN 속성의 값을 가져옵니다.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getMcIdN() {
                        return mcIdN;
                    }

                    /**
                     * mcIdN 속성의 값을 설정합니다.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setMcIdN(String value) {
                        this.mcIdN = value;
                    }

                    /**
                     * stDt 속성의 값을 가져옵니다.
                     * 
                     * @return
                     *     possible object is
                     *     {@link XMLGregorianCalendar }
                     *     
                     */
                    public XMLGregorianCalendar getStDt() {
                        return stDt;
                    }

                    /**
                     * stDt 속성의 값을 설정합니다.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link XMLGregorianCalendar }
                     *     
                     */
                    public void setStDt(XMLGregorianCalendar value) {
                        this.stDt = value;
                    }

                    /**
                     * enDt 속성의 값을 가져옵니다.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public XMLGregorianCalendar getEnDt() {
                        return enDt;
                    }

                    /**
                     * enDt 속성의 값을 설정합니다.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link XMLGregorianCalendar }
                     *     
                     */
                    public void setEnDt(XMLGregorianCalendar value) {
                        this.enDt = value;
                    }

                    /**
                     * msrs 속성의 값을 가져옵니다.
                     * 
                     * @return
                     *     possible object is
                     *     {@link DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData.PreVEE.Msrs }
                     *     
                     */
                    public DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData.PreVEE.Msrs getMsrs() {
                        return msrs;
                    }

                    /**
                     * msrs 속성의 값을 설정합니다.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData.PreVEE.Msrs }
                     *     
                     */
                    public void setMsrs(DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData.PreVEE.Msrs value) {
                        this.msrs = value;
                    }


                    /**
                     * <p>anonymous complex type에 대한 Java 클래스입니다.
                     * 
                     * <p>다음 스키마 단편이 이 클래스에 포함되는 필요한 콘텐츠를 지정합니다.
                     * 
                     * <pre>
                     * &lt;complexType&gt;
                     *   &lt;complexContent&gt;
                     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
                     *       &lt;sequence&gt;
                     *         &lt;element name="mL" maxOccurs="unbounded" minOccurs="0"&gt;
                     *           &lt;complexType&gt;
                     *             &lt;complexContent&gt;
                     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
                     *                 &lt;sequence&gt;
                     *                   &lt;element name="ts" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
                     *                   &lt;element name="meterDt" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
                     *                   &lt;element name="captureDt" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
                     *                   &lt;element name="captureDeviceID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
                     *                   &lt;element name="captureDeviceType" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
                     *                   &lt;element name="q" type="{http://www.w3.org/2001/XMLSchema}float"/&gt;
                     *                   &lt;element name="fc" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
                     *                 &lt;/sequence&gt;
                     *               &lt;/restriction&gt;
                     *             &lt;/complexContent&gt;
                     *           &lt;/complexType&gt;
                     *         &lt;/element&gt;
                     *       &lt;/sequence&gt;
                     *     &lt;/restriction&gt;
                     *   &lt;/complexContent&gt;
                     * &lt;/complexType&gt;
                     * </pre>
                     * 
                     * 
                     */
                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "ml"
                    })
                    public static class Msrs {

                        @XmlElement(name = "mL")
                        protected List<DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData.PreVEE.Msrs.ML> ml;

                        /**
                         * Gets the value of the ml property.
                         * 
                         * <p>
                         * This accessor method returns a reference to the live list,
                         * not a snapshot. Therefore any modification you make to the
                         * returned list will be present inside the JAXB object.
                         * This is why there is not a <CODE>set</CODE> method for the ml property.
                         * 
                         * <p>
                         * For example, to add a new item, do as follows:
                         * <pre>
                         *    getML().add(newItem);
                         * </pre>
                         * 
                         * 
                         * <p>
                         * Objects of the following type(s) are allowed in the list
                         * {@link DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData.PreVEE.Msrs.ML }
                         * 
                         * 
                         */
                        public List<DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData.PreVEE.Msrs.ML> getML() {
                            if (ml == null) {
                                ml = new ArrayList<DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData.PreVEE.Msrs.ML>();
                            }
                            return this.ml;
                        }


                        /**
                         * <p>anonymous complex type에 대한 Java 클래스입니다.
                         * 
                         * <p>다음 스키마 단편이 이 클래스에 포함되는 필요한 콘텐츠를 지정합니다.
                         * 
                         * <pre>
                         * &lt;complexType&gt;
                         *   &lt;complexContent&gt;
                         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
                         *       &lt;sequence&gt;
                         *         &lt;element name="ts" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
                         *         &lt;element name="meterDt" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
                         *         &lt;element name="captureDt" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
                         *         &lt;element name="captureDeviceID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
                         *         &lt;element name="captureDeviceType" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
                         *         &lt;element name="q" type="{http://www.w3.org/2001/XMLSchema}float"/&gt;
                         *         &lt;element name="fc" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
                         *       &lt;/sequence&gt;
                         *     &lt;/restriction&gt;
                         *   &lt;/complexContent&gt;
                         * &lt;/complexType&gt;
                         * </pre>
                         * 
                         * 
                         */
                        @XmlAccessorType(XmlAccessType.FIELD)
                        @XmlType(name = "", propOrder = {
                            "ts",
                            "meterDt",
                            "captureDt",
                            "captureDeviceID",
                            "captureDeviceType",
                            "q",
                            "fc"
                        })
                        public static class ML {

                            @XmlElement(required = true)
                            protected String ts;
                            @XmlElement(required = true)
                            @XmlSchemaType(name = "dateTime")
                            protected XMLGregorianCalendar meterDt;
                            @XmlElement(required = true)
                            @XmlSchemaType(name = "dateTime")
                            protected XMLGregorianCalendar captureDt;
                            @XmlElement(required = true)
                            protected String captureDeviceID;
                            @XmlElement(required = true)
                            protected String captureDeviceType;
                            protected double q;
                            @XmlElement(required = true)
                            protected String fc;

                            /**
                             * ts 속성의 값을 가져옵니다.
                             * 
                             * @return
                             *     possible object is
                             *     {@link String }
                             *     
                             */
                            public String getTs() {
                                return ts;
                            }

                            /**
                             * ts 속성의 값을 설정합니다.
                             * 
                             * @param value
                             *     allowed object is
                             *     {@link String }
                             *     
                             */
                            public void setTs(String value) {
                                this.ts = value;
                            }

                            /**
                             * meterDt 속성의 값을 가져옵니다.
                             * 
                             * @return
                             *     possible object is
                             *     {@link XMLGregorianCalendar }
                             *     
                             */
                            public XMLGregorianCalendar getMeterDt() {
                                return meterDt;
                            }

                            /**
                             * meterDt 속성의 값을 설정합니다.
                             * 
                             * @param value
                             *     allowed object is
                             *     {@link XMLGregorianCalendar }
                             *     
                             */
                            public void setMeterDt(XMLGregorianCalendar value) {
                                this.meterDt = value;
                            }

                            /**
                             * captureDt 속성의 값을 가져옵니다.
                             * 
                             * @return
                             *     possible object is
                             *     {@link XMLGregorianCalendar }
                             *     
                             */
                            public XMLGregorianCalendar getCaptureDt() {
                                return captureDt;
                            }

                            /**
                             * captureDt 속성의 값을 설정합니다.
                             * 
                             * @param value
                             *     allowed object is
                             *     {@link XMLGregorianCalendar }
                             *     
                             */
                            public void setCaptureDt(XMLGregorianCalendar value) {
                                this.captureDt = value;
                            }

                            /**
                             * captureDeviceID 속성의 값을 가져옵니다.
                             * 
                             * @return
                             *     possible object is
                             *     {@link String }
                             *     
                             */
                            public String getCaptureDeviceID() {
                                return captureDeviceID;
                            }

                            /**
                             * captureDeviceID 속성의 값을 설정합니다.
                             * 
                             * @param value
                             *     allowed object is
                             *     {@link String }
                             *     
                             */
                            public void setCaptureDeviceID(String value) {
                                this.captureDeviceID = value;
                            }

                            /**
                             * captureDeviceType 속성의 값을 가져옵니다.
                             * 
                             * @return
                             *     possible object is
                             *     {@link String }
                             *     
                             */
                            public String getCaptureDeviceType() {
                                return captureDeviceType;
                            }

                            /**
                             * captureDeviceType 속성의 값을 설정합니다.
                             * 
                             * @param value
                             *     allowed object is
                             *     {@link String }
                             *     
                             */
                            public void setCaptureDeviceType(String value) {
                                this.captureDeviceType = value;
                            }

                            /**
                             * q 속성의 값을 가져옵니다.
                             * 
                             */
                            public double getQ() {
                                return q;
                            }

                            /**
                             * q 속성의 값을 설정합니다.
                             * 
                             */
                            public void setQ(double value) {
                                this.q = value;
                            }

                            /**
                             * fc 속성의 값을 가져옵니다.
                             * 
                             * @return
                             *     possible object is
                             *     {@link String }
                             *     
                             */
                            public String getFc() {
                                return fc;
                            }

                            /**
                             * fc 속성의 값을 설정합니다.
                             * 
                             * @param value
                             *     allowed object is
                             *     {@link String }
                             *     
                             */
                            public void setFc(String value) {
                                this.fc = value;
                            }

                        }

                    }

                }

            }

        }

    }

}
