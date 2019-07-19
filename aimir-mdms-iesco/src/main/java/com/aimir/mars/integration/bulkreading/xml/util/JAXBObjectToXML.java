package com.aimir.mars.integration.bulkreading.xml.util;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;

import com.aimir.mars.integration.bulkreading.xml.cim.MeterReadingsType;
import com.aimir.mars.integration.bulkreading.xml.cim.header.MessageHeaderType;
import com.aimir.mars.integration.bulkreading.xml.cim.meterreading.DeviceListType;
import com.aimir.mars.integration.bulkreading.xml.cim.meterreading.DeviceListType.Device;
import com.aimir.mars.integration.bulkreading.xml.cim.meterreading.DeviceListType.Device.InitialMeasurementDataList;
import com.aimir.mars.integration.bulkreading.xml.cim.meterreading.DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData;
import com.aimir.mars.integration.bulkreading.xml.cim.meterreading.DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData.PreVEE;
import com.aimir.mars.integration.bulkreading.xml.cim.meterreading.DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData.PreVEE.Msrs;
import com.aimir.mars.integration.bulkreading.xml.cim.meterreading.DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData.PreVEE.Msrs.ML;

public class JAXBObjectToXML {

    public static void main(String[] args) {

        MeterReadingsType meterReading = new MeterReadingsType();
        MessageHeaderType header = new MessageHeaderType();
        header.setNoun("created");
        header.setVerb("deviceList");
        header.setContext("PRODUCTION");
        try {
            header.setTimestamp(javax.xml.datatype.DatatypeFactory.newInstance()
                    .newXMLGregorianCalendar("2015-07-22T20:55:12"));
        } catch (DatatypeConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        header.setRevision("1.0");
        header.setAckRequired("false");
        ML ml = new ML();
        ml.setFc("1001");
        try {
            ml.setMeterDt(javax.xml.datatype.DatatypeFactory.newInstance()
                    .newXMLGregorianCalendar("2015-07-22T20:55:12"));
        } catch (DatatypeConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ml.setCaptureDeviceID("1001");
        ml.setCaptureDeviceType("DCU");
        ml.setTs("00.00.00");
        ml.setQ(0.05f);

        Msrs msrs = new Msrs();
        msrs.getML().add(ml);

        DeviceListType deviceList = new DeviceListType();
        Device device = new Device();
        device.setDeviceIdentifierNumber("201203041201");
        device.setHeadEndExternalId("Nuri01");
        InitialMeasurementDataList iml = new InitialMeasurementDataList();
        InitialMeasurementData im = new InitialMeasurementData();
        PreVEE preVEE = new PreVEE();
        try {
            preVEE.setEnDt(javax.xml.datatype.DatatypeFactory.newInstance()
                    .newXMLGregorianCalendar("2015-07-22T20:55:12"));
        } catch (DatatypeConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            preVEE.setStDt(javax.xml.datatype.DatatypeFactory.newInstance()
                    .newXMLGregorianCalendar("2015-07-22T20:55:12"));
        } catch (DatatypeConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        preVEE.setMcIdN("1.0.1.8.0.255");
        preVEE.setMsrs(msrs);

        im.setPreVEE(preVEE);
        device.setInitialMeasurementDataList(iml);
        deviceList.getDevice().add(device);
        meterReading.getDeviceList().add(deviceList);
        meterReading.setHeader(header);

        jaxbObjectToXML("C:\\temp\\test.xml", meterReading);
    }

    public static void jaxbObjectToXML(String fileFullPath, Object obj) {

        try {
            JAXBContext context = JAXBContext.newInstance(obj.getClass());
            Marshaller m = context.createMarshaller();
            // for pretty-print XML in JAXB
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            // m.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, "");
            // Write to File
            m.marshal(obj, new File(fileFullPath));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

}
