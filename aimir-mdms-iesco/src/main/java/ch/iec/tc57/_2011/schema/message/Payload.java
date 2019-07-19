package ch.iec.tc57._2011.schema.message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;

import com.aimir.mars.integration.bulkreading.xml.cim.MeterReadingsType;

import ch.iec.tc57._2011.enddeviceevents.EndDeviceEvent;


/**
 * Set of values obtained from the meter.
 * 
 * <p>Java class for MeterReading complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Payload">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MeterReadings" type="{http://iec.ch/TC57/2011/MeterReadings#}MeterReadings" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="EndDeviceEvents" type="{http://iec.ch/TC57/2011/EndDeviceEvents#}EndDeviceEvents" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */


@XmlRootElement(name="Payload")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Payload", propOrder = {
	"MeterReadings"
})
public class Payload {
	
	@XmlElement(name = "MeterReadings", namespace="http://iec.ch/TC57/2011/MeterReadings#")
	MeterReadingsType MeterReadings;
	

	public MeterReadingsType getMeterReadings() {
		return MeterReadings;
	}

	public void setMeterReadings(MeterReadingsType meterReadings) {
		MeterReadings = meterReadings;
	}
}
