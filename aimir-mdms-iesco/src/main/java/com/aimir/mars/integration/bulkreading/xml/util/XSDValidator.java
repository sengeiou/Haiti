package com.aimir.mars.integration.bulkreading.xml.util;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

public class XSDValidator {
	
	private static Log log = LogFactory.getLog(XSDValidator.class);	

	public static void main(String[] args) {

		SchemaFactory factory = SchemaFactory
				.newInstance("http://www.w3.org/2001/XMLSchema");

		String xsd = "C:\\xsd\\movie1.xsd";
		String xml = "C:\\xsd\\movies.xml"; 

		File schemaLocation;
		Schema schema;
		Validator validator;
		try {

			schemaLocation = new File(xsd);
			schema = factory.newSchema(schemaLocation);
			validator = schema.newValidator();

			Source source = new StreamSource(xml);

			validator.validate(source);

			System.out.println(xml + " is valid."); 

		} catch (IOException e) {
			System.out.println(xml + " is not found "); 
			System.out.println(e.getMessage());
		} catch (SAXException e) {
			System.out.println(xml + " is not valid because ");
			System.out.println(e.getMessage());
		}
	}
	
	
	public static boolean check(String xmlPath){

		SchemaFactory factory = SchemaFactory
				.newInstance("http://www.w3.org/2001/XMLSchema");
		
		String meterDataXSD = "xsd/moe_integration/MeterDataXMLSchema.xsd";
		String eventDataXSD = "xsd/moe_integration/EventDataXMLSchema.xsd";

		Schema schema;
		Validator validator;

		try {
			if(xmlPath.indexOf("input_read") >= 0){
				schema = factory.newSchema(XSDValidator.class.getResource(meterDataXSD));
			}else if(xmlPath.indexOf("input_event") >= 0){
				schema = factory.newSchema(XSDValidator.class.getResource(eventDataXSD));
			}else{
				log.error("xml path is wrong");
				return false;
			}
			
			validator = schema.newValidator();
			Source source = new StreamSource(xmlPath);
			validator.validate(source);
			log.debug(xmlPath + " is valid."); 			
			return true;

		} catch (IOException e) {
			log.error(xmlPath + " is not found "); 
			log.error(e,e);
		} catch (SAXException e) {
			log.error(xmlPath + " is not valid because ");
			log.error(e,e);
		}
		return false;
		
	}
}

