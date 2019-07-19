package ch.iec.tc57._2011.schema.message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.aimir.mars.integration.bulkreading.xml.cim.MeterReadingsType;




@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EventMessage", propOrder = { 
    "header", 
    "payloadList"
})
@XmlRootElement(name="EventMessage")
public class EventMessage {
	
	 	@XmlElement(name = "Header")
		protected Header header ;
	    @XmlElement(name = "Payload")
	    protected List<Payload> payloadList = new ArrayList<Payload>();
	    
	    public EventMessage() {
	    	header = new Header();
		}
	    
	    public void setData(Map<String, Object> map){
	    	
	    	Payload payload = new Payload();
	    	
	    	MeterReadingsType meterReadings = null;
//	    	List<EndDeviceEvents> endDeviceEventList = null;
	    	
	    	if(map.get("class") instanceof MeterReadingsType) {
	    		
	    		meterReadings = (MeterReadingsType)map.get("class");	    	
		    	payload.setMeterReadings(meterReadings);
		    	
	    	} 
//	    	else if(map.get("class") instanceof List) {
//	    		
//	    		endDeviceEventList = (List<EndDeviceEvents>)map.get("class");	    
//	    		
//	    		payload.setEndDeviceEvents(endDeviceEventList);
//	    	}
	    	
	    	payloadList.add(payload);
	    }
		
		public Header getHeader() {
			if(this.header == null ){
				header = new Header();
			}
			return header;
		}
		public void setHeader(Header header) {
			if(this.header == null ){
				this.header = new Header();
			}
			this.header = header;
		}

		public List<Payload> getPayloadList() {
			return payloadList;
		}

		public void setPayloadList(List<Payload> payloadList) {
			this.payloadList = payloadList;
		}
}
