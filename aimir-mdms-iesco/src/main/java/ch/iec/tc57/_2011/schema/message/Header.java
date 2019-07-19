package ch.iec.tc57._2011.schema.message;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;


@XmlRootElement(name="Header")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Header", propOrder = {
	"verb",
    "noun",
    "revision",
    "timestamp",
    "source",
    "ackrequired",
    "messageId",
    "correlationId",
    "property"
})

public class Header {
	
	@XmlElement(name = "Verb")
	String verb; 
	
	@XmlElement(name = "Noun")
	String noun; 
	
	@XmlElement(name = "Revision")
	String revision; 	

//	@XmlJavaTypeAdapter(DateFormatterAdapter.class)
	@XmlElement(name = "Timestamp") 
	private XMLGregorianCalendar timestamp;
		
	@XmlElement(name = "Source")
	String source; 
	
	@XmlElement(name = "AckRequired")
	String ackrequired;
	
	@XmlElement(name = "MessageID")
	String messageId; 
		
	@XmlElement(name = "CorrelationID")
	String correlationId;
	
	@XmlElement(name = "Property")
	Property property;
	
	public Header() {
	}
	
	public XMLGregorianCalendar getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(XMLGregorianCalendar timestamp) {
		this.timestamp = timestamp;
	}
	
	private static class DateFormatterAdapter extends XmlAdapter<String, Date> {
        //private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        @Override
        public Date unmarshal(final String v) throws Exception {
            return dateFormat.parse(v);
        }

        @Override
        public String marshal(final Date v) throws Exception {
            return dateFormat.format(v);
        }
    }

	public String getVerb() {
		return verb;
	}

	public void setVerb(String verb) {
		this.verb = verb;
	}

	public String getNoun() {
		return noun;
	}

	public void setNoun(String noun) {
		this.noun = noun;
	}

	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getAckrequired() {
		return ackrequired;
	}

	public void setAckrequired(String ackrequired) {
		this.ackrequired = ackrequired;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	public Property getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		this.property = property;
	}
}
