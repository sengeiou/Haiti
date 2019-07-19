package com.aimir.mars.test;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.multispeak.version_4.ArrayOfElectricMeter;
import org.multispeak.version_4.ElectricMeter;
import org.multispeak.version_4.MeterChangedNotification;
import org.multispeak.version_4.Meters;

public class MeterChangedNotificationTest{
    
	public static void main(String[] args) {
		MeterChangedNotification response = new MeterChangedNotification();

		Meters meters = new Meters();
		ArrayOfElectricMeter emeters = new ArrayOfElectricMeter();
		ElectricMeter electricMeter = new ElectricMeter();
		electricMeter.setMeterNo("19073023");
		electricMeter.setMeterType("ElectricMeter");
		electricMeter.setErrorString("");
		
		emeters.getElectricMeter().add(electricMeter);
		
		meters.setElectricMeters(emeters);
		response.setChangedMeters(meters);
		
		jaxbObjectToXML("C:\\temp\\MeterChangedNotification.xml", response);
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

