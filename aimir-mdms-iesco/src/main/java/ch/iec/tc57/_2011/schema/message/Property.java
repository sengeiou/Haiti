package ch.iec.tc57._2011.schema.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="Property")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Property", propOrder = {
	"name",
    "value"
})

public class Property {
	
	@XmlElement(name = "Name")
	String name; 
	
	@XmlElement(name = "Value")
	String value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}	
}