package com.oracle.xmlns.ssys.nurieventsproxy.nurieventsproxy;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

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

import ch.iec.tc57._2011.enddeviceevents.EndDeviceEvent;
import ch.iec.tc57._2011.enddeviceevents.EndDeviceEventDetail;
import ch.iec.tc57._2011.enddeviceeventsmessage.EndDeviceEventsEventMessageType;
import ch.iec.tc57._2011.enddeviceeventsmessage.EndDeviceEventsPayloadType;
import ch.iec.tc57._2011.schema.message.HeaderType;

@Service(value = "nuriEventsServer")
public class NuriEventsServerOld implements ExecutePtt {

    private static Log log = LogFactory.getLog(NuriEventsServerOld.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional("transactionManager")
    public XMLGregorianCalendar execute(EndDeviceEventsEventMessageType part1) {
        log.debug(part1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

        String sql = "insert into ip_ev_receive(TS,MRID,ASSETMRID,CREATEDDATETIME,SEVERITY, \n"
                + " ISSUREID, EVENTDETAIL) values(?,?,?,?,?,?,?) ";

        String ts = null;
        HeaderType header = part1.getHeader();
        if (header != null) {
            ts = sdf.format(
                    header.getTimestamp().toGregorianCalendar().getTime());
        }

        EndDeviceEventsPayloadType payload = part1.getPayload();
        if (payload != null && payload.getEndDeviceEvents() != null
                && payload.getEndDeviceEvents().getEndDeviceEvent() != null
                && payload.getEndDeviceEvents().getEndDeviceEvent()
                        .size() > 0) {
            String MRID = "";
            String ASSETMRID = "";
            String CREATEDDATETIME = "";
            String SEVERITY = "";
            String ISSUREID = "";
            String EVENTDETAIL = "";
            for(EndDeviceEvent event : payload.getEndDeviceEvents().getEndDeviceEvent()) {
                CREATEDDATETIME = sdf.format(event.getCreatedDateTime().toGregorianCalendar().getTime());
                SEVERITY = event.getSeverity();
                ISSUREID = event.getIssuerID();
                MRID = event.getMRID();
                ASSETMRID = event.getAssets().getMRID();
                if (event.getEndDeviceEventDetails() != null
                        && event.getEndDeviceEventDetails().size() > 0) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("{ ");
                    for(int i = 0 ; i< event.getEndDeviceEventDetails().size();i++) {
                        EndDeviceEventDetail ed = event.getEndDeviceEventDetails().get(i);
                        sb.append(ed.getName()).append(" : '").append(ed.getValue()).append("' ");
                        if(i != event.getEndDeviceEventDetails().size() -1) {
                            sb.append(", ");
                        }
                    }
                    sb.append(" }");
                    EVENTDETAIL = sb.toString();
                }
                jdbcTemplate.update(sql, new Object[] { ts, MRID, ASSETMRID,
                        CREATEDDATETIME, SEVERITY, ISSUREID, EVENTDETAIL });
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
        log.info("NuriEvent MultiSpeak SERVICE is Ready for Service...\n");
    }

    public static void main( String[] args )
    {
        try {
            
            String servicePort = null;
            String configFile = null;

            if (args.length < 3 ) {
                log.info("Usage:");
                log.info("NuriEventsServer -port CommunicationPort -config /config/spring-ev-integration-ws-test.xml");
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
