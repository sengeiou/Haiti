package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aimir.model.device.MMIU;

/**
 * <p>
 * Java class for cmdFOTA complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType name="cmdFOTA">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;element name="TargetModem" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;element name="FtpUrl" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;element name="FtpPort" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;element name="FtpDirectory" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;element name="TargetFile" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;element name="Username" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;element name="Password" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>       
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdFOTA", propOrder = { "targetModem", "ftpUrl", "ftpPort", "ftpDirectory", "targetFile", "username", "password" })
public class CmdFOTA {
	@XmlElement(name = "TargetModem")
	protected MMIU targetModem;

	@XmlElement(name = "FtpUrl")
	protected String ftpUrl;

	@XmlElement(name = "FtpPort")
	protected String ftpPort;

	@XmlElement(name = "FtpDirectory")
	protected String ftpDirectory;

	@XmlElement(name = "TargetFile")
	protected String targetFile;

	@XmlElement(name = "Username")
	protected String username;

	@XmlElement(name = "Password")
	protected String password;

	public MMIU getTargetModem() {
		return targetModem;
	}

	public String getFtpUrl() {
		return ftpUrl;
	}

	public String getFtpPort() {
		return ftpPort;
	}

	public String getFtpDirectory() {
		return ftpDirectory;
	}

	public String getTargetFile() {
		return targetFile;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

}
