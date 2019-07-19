package com.aimir.mars.integration.event;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;


import ch.iec.tc57._2011.enddeviceevents.Asset;
import ch.iec.tc57._2011.enddeviceevents.EndDeviceEvent;
import ch.iec.tc57._2011.enddeviceevents.EndDeviceEvents;
import ch.iec.tc57._2011.enddeviceeventsmessage.EndDeviceEventsEventMessageType;
import ch.iec.tc57._2011.enddeviceeventsmessage.EndDeviceEventsPayloadType;
import ch.iec.tc57._2011.schema.message.HeaderType;

public class JAXBObjectToXML{
    
	public static void main(String[] args) {
		
		EndDeviceEventsEventMessageType message = new EndDeviceEventsEventMessageType();
		HeaderType header = new HeaderType();
		header.setNoun("created");
		header.setVerb("EndDeviceEvents");

		try {
			header.setTimestamp(javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar("2015-07-22T20:55:12"));
		} catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		header.setRevision("1.0");
		header.setSource("NURI001");
		
		EndDeviceEventsPayloadType payload = new EndDeviceEventsPayloadType();
		
		EndDeviceEvents events = new EndDeviceEvents();
		EndDeviceEvent event = new EndDeviceEvent();
		event.setMRID("3.26.9.185");
		try {
			event.setCreatedDateTime(javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar("2015-07-22T20:55:12"));
		} catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		event.setSeverity("1");
		Asset asset = new Asset();
		asset.setMRID("19023423");
		event.setAssets(asset);
		events.getEndDeviceEvent().add(event);
		payload.setEndDeviceEvents(events);
		message.setHeader(header);
		message.setPayload(payload);
		jaxbObjectToXML("C:\\temp\\event.xml", message);
	} 
 
    public static void jaxbObjectToXML(String fileFullPath, Object obj) {
 
        try {
            JAXBContext context = JAXBContext.newInstance(obj.getClass());
            Marshaller m = context.createMarshaller();
            // for pretty-print XML in JAXB
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            //m.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, "");
            // Write to File
            m.marshal(obj, new File(fileFullPath));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
 
}

