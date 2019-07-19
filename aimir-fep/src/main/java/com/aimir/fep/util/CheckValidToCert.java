package com.aimir.fep.util;

import java.io.*;
import java.util.*;

import org.apache.commons.httpclient.util.DateUtil;

import java.security.*;
import java.security.cert.*;


public class CheckValidToCert {
	
	public CheckValidToCert() {}
	
	public static X509Certificate checkValidCert(String filename) {
		
		FileInputStream fr = null;
		X509Certificate certificate = null;
		
		try {
			
			try {
				fr = new FileInputStream(filename);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			CertificateFactory cf;
			
			cf = CertificateFactory.getInstance("X509");
			certificate = (X509Certificate) cf.generateCertificate(fr);
			
			System.out.println("Read in the following certificate:");
			System.out.println("\tCertificate for: " + certificate.getSubjectDN());
			System.out.println("\tCertificate issued by: " + certificate.getIssuerDN());
			System.out.println("\tThe certificate is valid from " + certificate.getNotBefore() + " to " + certificate.getNotAfter());
			
			//check Vaildity
			try {
				certificate.checkValidity();
				System.out.println("\tThe certificate is Valid ");
			}
			catch (CertificateExpiredException cee) {
		        // if the cert is expired we should destroy it and recursively call this function
				System.out.println("\tThe certificate is expired ");
			}
			
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            if (fr != null) try {fr.close();}catch(Exception e) {}
        }
		
		return certificate;
	}
	
	public static void main(String[] args) {
		
		String filename = "/opt/aimir4/firmware-file/cert/dcu/000B12000000030D/000B12000000030D/PANA/000B12000000030D.der";		                   
		CheckValidToCert.checkValidCert(filename);
	}
}
