package com.oracle.xmlns.ssys.nurimeterdataproxy.nurimeterdata;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.Bus;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.fep.util.DataUtil;
import com.aimir.mars.integration.bulkreading.xml.cim.MeterReadingsType;
import com.aimir.mars.integration.bulkreading.xml.cim.header.MessageHeaderType;
import com.aimir.mars.integration.bulkreading.xml.cim.meterreading.DeviceListType;
import com.aimir.mars.integration.bulkreading.xml.cim.meterreading.DeviceListType.Device;
import com.aimir.mars.integration.bulkreading.xml.cim.meterreading.DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData;
import com.aimir.mars.integration.bulkreading.xml.cim.meterreading.DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData.PreVEE;
import com.aimir.mars.integration.bulkreading.xml.cim.meterreading.DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData.PreVEE.Msrs.ML;

@Service(value = "nuriMeterDataServer")
public class NuriMeterDataServerOld implements ExecutePtt {

    private static Log log = LogFactory.getLog(NuriMeterDataServerOld.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");;

    @Override
    @Transactional("transactionManager")
    public XMLGregorianCalendar execute(MeterReadingsType part1) {
         log.debug(part1);

         String sql = "insert into ip_mv_receive(TS,HEADENDEXTERNALID,DEVICEIDENTIFIERNUMBER,ISSUERID,MCIDN, \n"
                 + " STDT,ENDT,MLTS,MLMETERDT,MLCAPTUREDT,MLCAPTUREDEVID,MLCAPTUREDEVTYPE,MLQ,MLFC) \n "
                 + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";

         String ts = null;
         MessageHeaderType header = part1.getHeader();
         if (header != null) {
             ts = sdf.format(
                     header.getTimestamp().toGregorianCalendar().getTime());
         }
         List<DeviceListType> deviceLists = part1.getDeviceList();
         if (deviceLists != null && deviceLists.size() > 0) {
             for (DeviceListType deviceList : deviceLists) {
                 List<Device> devices = deviceList.getDevice();
                 if (devices != null && devices.size() > 0) {
                     String deviceIdentifierNumber = null;
                     String headEndExternalId = null;
                     String issuerID = null;
                     for(Device device : devices) {
                         deviceIdentifierNumber = device.getDeviceIdentifierNumber();
                         headEndExternalId = device.getHeadEndExternalId();
                         issuerID = device.getIssuerID();
                         if (device.getInitialMeasurementDataList() != null
                                 && device.getInitialMeasurementDataList()
                                         .getInitialMeasurementData() != null
                                 && device.getInitialMeasurementDataList()
                                         .getInitialMeasurementData()
                                         .size() > 0) {
                             String mcIdn = null;
                             String stDt = null;
                             String enDt = null;
                             for(InitialMeasurementData initalMeasurementData : device.getInitialMeasurementDataList().getInitialMeasurementData()) {
                                 if(initalMeasurementData.getPreVEE() != null) {
                                     PreVEE preVee = initalMeasurementData.getPreVEE();
                                     mcIdn = preVee.getMcIdN();
                                     stDt = sdf.format(preVee.getStDt().toGregorianCalendar().getTime());
                                     enDt = sdf.format(preVee.getEnDt().toGregorianCalendar().getTime());

                                     if(preVee.getMsrs() != null && preVee.getMsrs().getML()!=null && preVee.getMsrs().getML().size() > 0) {
                                         String mlts = null;
                                         double mlvalue = 0;
                                         String mlValidCode = "";
                                         String mlMeterDate = "";
                                         String mlCaptureDate = "";
                                         String mlCaptureDevId = "";
                                         String mlCaptureDevType = "";
                                         for(ML ml : preVee.getMsrs().getML()) {
                                             mlts = stDt.substring(0, 8) + ml.getTs().replaceAll(".", "");
                                             mlvalue = ml.getQ();
                                             if(ml.getFc()!=null)
                                                 mlValidCode = ml.getFc();
                                             if(ml.getMeterDt()!=null)
                                                 mlMeterDate = sdf.format(ml.getMeterDt().toGregorianCalendar().getTime());
                                             if(ml.getCaptureDt()!=null)
                                                 mlCaptureDate = sdf.format(ml.getCaptureDt().toGregorianCalendar().getTime());
                                             if(ml.getCaptureDeviceType()!=null)
                                                 mlCaptureDevType = ml.getCaptureDeviceType();
                                             if(ml.getCaptureDeviceID() !=null)
                                                 mlCaptureDevId = ml.getCaptureDeviceID();

                                             jdbcTemplate.update(sql,
                                                     new Object[] { ts,
                                                             headEndExternalId, deviceIdentifierNumber, issuerID, mcIdn,
                                                             stDt, enDt, mlts, mlMeterDate, mlCaptureDate,
                                                             mlCaptureDevId, mlCaptureDevType, mlvalue, mlValidCode });

                                         }
                                     }
                                 }
                             }
                         }
                     }
                 }
             }

         }
         GregorianCalendar cal = new GregorianCalendar();
         cal.setTime(new Date());
         XMLGregorianCalendar date = null;
         try {
             date = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
         } catch (DatatypeConfigurationException e) {
             e.printStackTrace();
         }

         return date;
    }
     public void init()
     {
         log.info("NuriMeterValue SERVICE is Ready for Service...\n");
     }

     public static void main( String[] args )
     {
         try {
             
             String servicePort = null;
             String configFile = null;

             if (args.length < 3 ) {
                 log.info("Usage:");
                 log.info("ExecutePttServer -port CommunicationPort -config /config/spring-mv-integration-ws-test.xml");
                 return;
             }

             for (int i=0; i < args.length; i+=2) {

                 String nextArg = args[i];

                 if (nextArg.startsWith("-port")) {
                     servicePort = new String(args[i+1]);
                 }
                 if (nextArg.startsWith("-configFile")) {
                     configFile = new String(args[i+1]);
                 }
             }
             
             log.info("Event SERVICE START=SERVICE PORT["+servicePort+"]");
             ApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[]{configFile}); 
             DataUtil.setApplicationContext(applicationContext);
             Tomcat tomcat = new Tomcat();
             tomcat.setPort(Integer.parseInt(servicePort));
             Context context = tomcat.addContext("", "");
             Wrapper servletWrap = context.createWrapper();
             servletWrap.setName("cxf");
             CXFNonSpringServlet servlet = new CXFNonSpringServlet();
             // Wire the bus that endpoint uses with the Servlet
             servlet.setBus((Bus)applicationContext.getBean("cxf"));
             servletWrap.setServlet(servlet);
             servletWrap.setLoadOnStartup(1);
             context.addChild(servletWrap);
             context.addServletMapping("/services/*", "cxf");
             File webapps = new File(context.getCatalinaBase().getAbsolutePath() + File.separator + "webapps");
             if (!webapps.exists()) webapps.mkdir();
             tomcat.start();

             tomcat.getServer().await();
         }
         catch (Exception e) {
             log.error(e, e);
         }
     }
}
