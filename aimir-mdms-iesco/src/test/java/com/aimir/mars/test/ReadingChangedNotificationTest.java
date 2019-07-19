package com.aimir.mars.test;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.datatype.DatatypeConfigurationException;

import org.multispeak.version_4.ArrayOfErrorObject;
import org.multispeak.version_4.ArrayOfMeterReading1;
import org.multispeak.version_4.ArrayOfReadingValue;
import org.multispeak.version_4.ErrorObject;
import org.multispeak.version_4.MeterAddNotificationResponse;
import org.multispeak.version_4.MeterID;
import org.multispeak.version_4.MeterReading;
import org.multispeak.version_4.ReadingChangedNotification;
import org.multispeak.version_4.ReadingStatusCode;
import org.multispeak.version_4.ReadingValue;
import org.multispeak.version_4.ServiceType;

public class ReadingChangedNotificationTest{
    
	public static void main(String[] args) {
		ReadingChangedNotification response = new ReadingChangedNotification();

		ArrayOfMeterReading1 changedMeterReads = new ArrayOfMeterReading1();
		MeterReading meterReading = new MeterReading();
		
		MeterID meterID = new MeterID();
		meterID.setMeterNo("19023423");
		meterID.setObjectID("");
		meterID.setServiceType(ServiceType.ELECTRIC);
		meterID.setUtility("AKL");
		meterReading.setMeterID(meterID);
		meterReading.setErrorString("");
		meterReading.setObjectID("1001");
		
		
		
		ArrayOfReadingValue readingValues = new ArrayOfReadingValue();
		
		ReadingStatusCode code = new ReadingStatusCode();
		code.setValue("1001");
		
		ReadingValue readingValue1 = new ReadingValue();
		readingValue1.setValue("300");
		readingValue1.setUnits("kWh");
		readingValue1.setFieldName("1.0.1.8.0.255");
		readingValue1.setReadingStatusCode(code);
		try {
			readingValue1.setTimeStamp(javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar("2016-02-17T09:30:47"));
		} catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		
		ReadingValue readingValue2 = new ReadingValue();
		readingValue2.setValue("300");
		readingValue2.setUnits("kWh");
		readingValue2.setFieldName("1.0.1.8.0.255");
		readingValue2.setReadingStatusCode(code);
		try {
			readingValue2.setTimeStamp(javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar("2016-02-17T09:30:47"));
		} catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ReadingValue readingValue3 = new ReadingValue();
		readingValue3.setValue("10");
		readingValue3.setUnits("kVarh");
		readingValue3.setFieldName("1.0.3.8.0.255");
		readingValue3.setReadingStatusCode(code);
		try {
			readingValue3.setTimeStamp(javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar("2016-02-17T09:30:47"));
		} catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ReadingValue readingValue4 = new ReadingValue();
		readingValue4.setValue("0");
		readingValue4.setUnits("kVarh");
		readingValue4.setFieldName("1.0.4.8.0.255");
		readingValue4.setReadingStatusCode(code);
		try {
			readingValue4.setTimeStamp(javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar("2016-02-17T09:30:47"));
		} catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		readingValues.getReadingValue().add(readingValue1);		
		readingValues.getReadingValue().add(readingValue2);
		readingValues.getReadingValue().add(readingValue3);
		readingValues.getReadingValue().add(readingValue4);
		meterReading.setReadingValues(readingValues);		
		changedMeterReads.getMeterReading().add(meterReading);		
		response.setTransactionID("12001");
		response.setChangedMeterReads(changedMeterReads);
		
		jaxbObjectToXML("C:\\temp\\ReadingChangedNotificationResponse.xml", response);
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

