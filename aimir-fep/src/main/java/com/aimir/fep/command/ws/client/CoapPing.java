
package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "coapPing", propOrder = {
    "ipAddress",
    "device",
    "type",
    "protocol",
    "fwVersion"
})

public class CoapPing {
    @XmlElement(name = "IpAddress")
    protected String ipAddress;
    
    @XmlElement(name = "Device")
    protected String device;
    
    @XmlElement(name = "Type")
    protected String type;
    
    @XmlElement(name = "Protocol")
    protected String protocol;
    
    @XmlElement(name = "FwVersion")
    protected String fwVersion;
    
    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getDevice() {
        return ipAddress;
    }

    public void setDevice(String device) {
        this.device = device;
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getFwVersion() {
		return fwVersion;
	}

	public void setFwVersion(String fwVersion) {
		this.fwVersion = fwVersion;
	}

}
