package com.aimir.mars.test;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;

import org.multispeak.version_4.ArrayOfErrorObject;
import org.multispeak.version_4.AssessmentLocationChangedNotificationResponse;
import org.multispeak.version_4.ErrorObject;


import ch.iec.tc57._2011.enddeviceevents.Asset;
import ch.iec.tc57._2011.enddeviceevents.EndDeviceEvent;
import ch.iec.tc57._2011.enddeviceevents.EndDeviceEvents;
import ch.iec.tc57._2011.enddeviceeventsmessage.EndDeviceEventsEventMessageType;
import ch.iec.tc57._2011.enddeviceeventsmessage.EndDeviceEventsPayloadType;
import ch.iec.tc57._2011.schema.message.HeaderType;

public class AssessmentLocationResponseTest{
    
	public static void main(String[] args) {
		
		AssessmentLocationChangedNotificationResponse response = new AssessmentLocationChangedNotificationResponse();
		ArrayOfErrorObject errList = new ArrayOfErrorObject();
		
		ErrorObject errobj = new ErrorObject();
		errobj.setObjectID("1001");
		try {
			errobj.setEventTime(javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar("2015-07-22T20:55:12"));
		} catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		errobj.setErrorString("");
		
		
		ErrorObject errobj2 = new ErrorObject();
		errobj2.setObjectID("1002");
		try {
			errobj2.setEventTime(javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar("2015-07-22T20:55:12"));
		} catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		errobj2.setErrorString("");
		
		errList.getErrorObject().add(errobj);
		errList.getErrorObject().add(errobj2);
		response.setAssessmentLocationChangedNotificationResult(errList);
		
		jaxbObjectToXML("C:\\temp\\AssessmentLocationChangedNotificationResponse.xml", response);
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

