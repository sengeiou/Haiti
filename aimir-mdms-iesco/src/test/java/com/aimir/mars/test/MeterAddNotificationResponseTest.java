package com.aimir.mars.test;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;

import org.multispeak.version_4.ArrayOfErrorObject;
import org.multispeak.version_4.ErrorObject;
import org.multispeak.version_4.MeterAddNotificationResponse;

public class MeterAddNotificationResponseTest{
    
	public static void main(String[] args) {
		
		MeterAddNotificationResponse response = new MeterAddNotificationResponse();
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
		response.setMeterAddNotificationResult(errList);
		
		jaxbObjectToXML("C:\\temp\\MeterAddNotificationResponse.xml", response);
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

