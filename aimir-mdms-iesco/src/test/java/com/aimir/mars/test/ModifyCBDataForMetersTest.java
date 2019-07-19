package com.aimir.mars.test;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.multispeak.version_4.ArrayOfElectricMeter;
import org.multispeak.version_4.ElectricMeter;
import org.multispeak.version_4.Meters;
import org.multispeak.version_4.ModifyCBDataForMeters;

import _1_release.cpsm_v4.IDOBJ;

public class ModifyCBDataForMetersTest{
    
	public static void main(String[] args) {
		ModifyCBDataForMeters response = new ModifyCBDataForMeters();

		Meters meters = new Meters();
		ArrayOfElectricMeter emeters = new ArrayOfElectricMeter();
		ElectricMeter electricMeter = new ElectricMeter();
		
		IDOBJ idobj = new IDOBJ();
		idobj.setMRID("18# 0.0.44.0.e.255#5#TRUE");		
		electricMeter.setIdentifiedObject(idobj);
		electricMeter.setMeterNo("19073023");
		electricMeter.setMeterType("ElectricMeter");
		
		emeters.getElectricMeter().add(electricMeter);
		
		meters.setElectricMeters(emeters);
		response.setMeterData(meters);
		
		jaxbObjectToXML("C:\\temp\\ModifyCBDataForMeters.xml", response);
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

