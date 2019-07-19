package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for cmdForceUpload complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmdForceUpload">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="McuId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="serverName" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="dataCount" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="fromDate" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="toDate" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdForceUpload", propOrder = {
    "mcuId",
    "serverName",
    "dataCount",
    "fromDate",
    "toDate"
})
public class CmdForceUpload {

    @XmlElement(name = "McuId")
    protected String mcuId;
    @XmlElement(name = "serverName")
    protected String serverName;
    @XmlElement(name = "dataCount")
    protected int dataCount;
    @XmlElement(name = "fromDate")
    protected String fromDate;
    @XmlElement(name = "toDate")
    protected String toDate;
    /**
     * Gets the value of the mcuId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMcuId() {
        return mcuId;
    }

    /**
     * Sets the value of the mcuId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMcuId(String value) {
        this.mcuId = value;
    }

    /**
     * Gets the value of the serverName property.
     * 
     */
    public String getserverName() {
        return serverName;
    }

    /**
     * Sets the value of the serverName property.
     * 
     */
    public void setserverName(String serverName) {
        this.serverName = serverName;
    }  

    /**
     * Gets the value of the dataCount property.
     * 
     */
    public int getdataCount() {
        return dataCount;
    }

    /**
     * Sets the value of the dataCount property.
     * 
     */
    public void setdataCount(int dataCount) {
        this.dataCount = dataCount;
    } 
    
    /**
     * Gets the value of the fromDate property.
     * 
     */
    public String getfromDate() {
        return fromDate;
    }

    /**
     * Sets the value of the fromDate property.
     * 
     */
    public void setfromDate(String fromDate) {
        this.fromDate = fromDate;
    } 

    /**
     * Gets the value of the toDate property.
     * 
     */
    public String gettoDate() {
        return toDate;
    }

    /**
     * Sets the value of the toDate property.
     * 
     */
    public void settoDate(String toDate) {
        this.toDate = toDate;
    } 
}
