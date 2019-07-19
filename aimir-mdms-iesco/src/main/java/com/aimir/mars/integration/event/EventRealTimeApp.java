package com.aimir.mars.integration.event;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import com.aimir.mars.util.MarsProperty;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.service.device.MeterManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.oracle.xmlns.ssys.nurieventsproxy.nurieventsproxy.NuriEvents;

import ch.iec.tc57._2011.enddeviceevents.Asset;
import ch.iec.tc57._2011.enddeviceevents.EndDeviceEvent;
import ch.iec.tc57._2011.enddeviceevents.EndDeviceEventDetail;
import ch.iec.tc57._2011.enddeviceevents.EndDeviceEvents;
import ch.iec.tc57._2011.enddeviceevents.EndDeviceEvent.EndDeviceEventType;
import ch.iec.tc57._2011.enddeviceeventsmessage.EndDeviceEventsEventMessageType;
import ch.iec.tc57._2011.enddeviceeventsmessage.EndDeviceEventsPayloadType;
import ch.iec.tc57._2011.schema.message.HeaderType;

public class EventRealTimeApp implements MessageListener {

    private static Log log = LogFactory.getLog(EventRealTimeApp.class);

    @Autowired
    protected NuriEvents webServiceClient;
    @Autowired
    protected MeterManager meterManager;
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    protected boolean isDeliverData = false;
    public void onMessage(Message msg) {
        if (log.isTraceEnabled()) {
            log.trace("onmessage:" + msg);
        }

        List<Map<String, Object>> ipEvOption = jdbcTemplate.queryForList(
                "select attributename, attributevalue from ip_ev_option where codetype=?",
                "OP");
        for (Map<String, Object> row : ipEvOption) {
            if (row.get("ATTRIBUTENAME").toString().toUpperCase()
                    .equals("CNF_DELIVERY")) {
                isDeliverData = Boolean.parseBoolean(
                        row.get("ATTRIBUTEVALUE").toString().toLowerCase());
            }
        }
        if(isDeliverData == false) {
            return;
        }

        try {
            if (msg instanceof TextMessage) {
                TextMessage cbMsg = (TextMessage) msg;
                if (log.isDebugEnabled()) {
                    log.debug("msg=" + cbMsg.getText());
                }

                JsonParser parser = new JsonParser();
                JsonObject eventJson = (JsonObject) parser
                        .parse(cbMsg.getText());

                String eventAlertName = eventJson.get("eventAlertName")
                        .getAsString();
                String eventAlertMessage = null;
                String activatorId = null;
                String openTime = null;
                String closeTime = null;
                String location = null;
                String status = null;

                if (eventAlertName.equals("Power Alarm")
                        || eventAlertName.equals("Cover Alarm")) {
                    eventAlertMessage = eventJson.get("eventMessage")
                            .getAsString();
                    activatorId = eventJson.get("activatorId").getAsString();
                    openTime = eventJson.get("openTime").getAsString();
                    closeTime = eventJson.get("closeTime").getAsString();
                    location = eventJson.get("location").getAsString();
                    status = eventJson.get("status").getAsString();

                    if (eventAlertName.equals("Power Alarm")) {
                        if(eventAlertMessage.equals("Power Down")) {
                            EndDeviceEventsEventMessageType event = makeEndDeviceEventsEventMessageType("3.26.9.185", openTime, activatorId, location);
                            sendWSMessage(event, "Power Alarm Open");
                        } else if(eventAlertMessage.equals("Power Restore")) {
                            EndDeviceEventsEventMessageType event = makeEndDeviceEventsEventMessageType("3.26.9.216", closeTime, activatorId, location);
                            sendWSMessage(event, "Power Alarm Cleared");
                        } else if(eventAlertMessage.indexOf("Missing") > -1) {
                            if(eventAlertMessage.indexOf("L1") > -1) {
                                EndDeviceEventsEventMessageType event = makeEndDeviceEventsEventMessageType("3.26.131.293", openTime, activatorId, location);
                                sendWSMessage(event, "L1 Missing Open");
                            }
                            if(eventAlertMessage.indexOf("L2") > -1) {
                                EndDeviceEventsEventMessageType event = makeEndDeviceEventsEventMessageType("3.26.132.293", openTime, activatorId, location);
                                sendWSMessage(event, "L2 Missing Open");
                            }
                            if(eventAlertMessage.indexOf("L3") > -1) {
                                EndDeviceEventsEventMessageType event = makeEndDeviceEventsEventMessageType("3.26.133.293", openTime, activatorId, location);
                                sendWSMessage(event, "L3 Missing Open");
                            }
                        }
                    } else if (eventAlertName.equals("Cover Alarm")) {
                        if(status.equals("Open")) {
                            EndDeviceEventsEventMessageType event = makeEndDeviceEventsEventMessageType("3.33.1.44", openTime, activatorId, location);
                            sendWSMessage(event, "Cover Alarm Open");
                        } else {
                            EndDeviceEventsEventMessageType event = makeEndDeviceEventsEventMessageType("3.33.1.216", openTime, activatorId, location);
                            sendWSMessage(event, "Cover Alarm Cleared");
                        }
                    }
                }
            } else if (msg instanceof ObjectMessage) {
                ObjectMessage cbMsg = (ObjectMessage) msg;
                if (log.isDebugEnabled()) {
                    Object key = null;
                    for (Enumeration e = cbMsg.getPropertyNames(); e
                            .hasMoreElements();) {
                        key = e.nextElement();
                        log.debug("key=" + key + ", value="
                                + cbMsg.getObjectProperty((String) key));
                    }
                }
            } else {
                log.warn("Message is " + msg.getClass().getName());
            }
        } catch (JMSException e) {
            log.error(e);
        }
    }

    private EndDeviceEventsEventMessageType makeEndDeviceEventsEventMessageType(String eventCIMId, String yyyymmddhhmmss, String tagrgetId, String locationCode) {
        EndDeviceEventsEventMessageType message = new EndDeviceEventsEventMessageType();
        HeaderType header = new HeaderType();
        header.setNoun("EndDeviceEvents");
        header.setVerb("created");
        header.setRevision("1.0");
        header.setSource(MarsProperty.getProperty("HES.ID", "NURI001"));// SET EACH FEP ID (NURI01 ~ 15)
        GregorianCalendar cal = new GregorianCalendar();
        XMLGregorianCalendar date = null;
        try {
            cal.setTime(new Date());
            date = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
            date.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
            date.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        header.setTimestamp(date);

        EndDeviceEventsPayloadType payload = new EndDeviceEventsPayloadType();
        EndDeviceEvents events = new EndDeviceEvents();
        EndDeviceEvent event = new EndDeviceEvent();
        event.setMRID(eventCIMId);

        GregorianCalendar gcal = new GregorianCalendar();
        XMLGregorianCalendar timeStamp = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            Date d = sdf.parse(yyyymmddhhmmss);
            gcal.setTime(d);
            timeStamp = DatatypeFactory.newInstance()
                    .newXMLGregorianCalendar(gcal);
        } catch (Exception e) {
            log.error(e, e);
        }
        timeStamp.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
        timeStamp.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);
        event.setCreatedDateTime(timeStamp);
        event.setSeverity("1");
        Asset asset = new Asset();
        asset.setMRID(tagrgetId);
        
        if(Boolean.parseBoolean(MarsProperty.getProperty("HES.ACTOR.LOCATION", "false"))) {
            event.setIssuerID(locationCode);
        } else {
            event.setIssuerID("HES");
        }
        event.setAssets(asset);
        EndDeviceEventType evt = new EndDeviceEventType();
        event.setEndDeviceEventType(evt);
        if(Boolean.parseBoolean(MarsProperty.getProperty("powel.enableFirmwareInformation","false"))) {
            if(eventCIMId.equals("2.20.7.105") || eventCIMId.equals("3.11.7.213")) {
                Meter met = meterManager.getMeter(tagrgetId);
                Modem mod = met.getModem();

                if (met != null) {
                    EndDeviceEventDetail evd1 = new EndDeviceEventDetail();
                    evd1.setName("firmwareID");
                    evd1.setValue(met.getSwVersion() == null ? ""
                            : met.getSwVersion());
                    event.getEndDeviceEventDetails().add(evd1);
                }
                if (mod != null) {
                    EndDeviceEventDetail evd2 = new EndDeviceEventDetail();
                    evd2.setName("terminalFWVersion");
                    evd2.setValue(mod.getFwVer() == null ? "" : mod.getFwVer());
                    event.getEndDeviceEventDetails().add(evd2);
                    EndDeviceEventDetail evd3 = new EndDeviceEventDetail();
                    evd3.setName("terminalBuildNumber");
                    evd3.setValue(mod.getFwRevision() == null ? "" : mod.getFwRevision());
                    event.getEndDeviceEventDetails().add(evd3);
                    EndDeviceEventDetail evd4 = new EndDeviceEventDetail();
                    evd4.setName("terminalHWVersion");
                    evd4.setValue(mod.getHwVer() == null ? "" : mod.getHwVer());
                    event.getEndDeviceEventDetails().add(evd4);
                }
            }
        }
        events.getEndDeviceEvent().add(event);

        payload.setEndDeviceEvents(events);
        message.setHeader(header);
        message.setPayload(payload);

        return message;
    }

    private boolean checkDeliveryAvailable(String CIMID, String aimireventid) {
        boolean isDeliverData = false;
        boolean isDeliverCIMData = false;
        boolean isDeliverAIMIRIDData = false;
        List<Map<String, Object>> ipEvOption = jdbcTemplate
                .queryForList(
                        "select attributename, attributevalue from ip_ev_option where codetype=? and attributename=? \n" +
                        "union all \n" +
                        "select attributename, attributevalue from ip_ev_option where codetype=? and attributename=? \n" +
                        "union all \n" +
                        "select aimireventid, isuse attribut from ip_ev_eventobis where aimireventid=? ", "OP", "CNF_DELIVERY", "EV", CIMID, aimireventid);
        for (Map<String, Object> row : ipEvOption) {
            if(row.get("ATTRIBUTENAME").toString().toUpperCase().equals("CNF_DELIVERY")) {
                isDeliverData = Boolean.parseBoolean(row.get("ATTRIBUTEVALUE").toString().toLowerCase());
            } else if(row.get("ATTRIBUTENAME").toString().toUpperCase().equals(CIMID)) {
                isDeliverCIMData = Boolean.parseBoolean(row.get("ATTRIBUTEVALUE").toString().toLowerCase());
            } else if(row.get("ATTRIBUTENAME").toString().toUpperCase().equals(aimireventid)) {
                isDeliverAIMIRIDData = Boolean.parseBoolean(row.get("ATTRIBUTEVALUE").toString().toLowerCase());
            }
        }
        return isDeliverData && isDeliverCIMData && isDeliverAIMIRIDData;
    }

    private void sendWSMessage(EndDeviceEventsEventMessageType message, String aimireventid) {
        if(checkDeliveryAvailable(message.getPayload().getEndDeviceEvents().getEndDeviceEvent().get(0).getMRID(), aimireventid)) {
            Client client = ClientProxy.getClient(webServiceClient);
            HTTPConduit http = (HTTPConduit) client.getConduit();
            TLSClientParameters tlsParams = new TLSClientParameters();
            tlsParams.setDisableCNCheck(true); // CN Name check ignore...
            http.setTlsClientParameters(tlsParams);
            HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
            httpClientPolicy.setAllowChunking(false);
            http.setClient(httpClientPolicy);

            XMLGregorianCalendar timestamp =  webServiceClient.nuriEventsBulk(message);
            log.debug("receive timestamp="+timestamp);
        }
    }

    public static void main(String[] args) {

        String configFile = null;

        if (args.length < 1) {
            log.info("Usage:");
            log.info(
                    "App  -configFile config/spring-ev-integration-realtime.xml");
            return;
        }

        for (int i = 0; i < args.length; i += 2) {

            String nextArg = args[i];

            if (nextArg.startsWith("-configFile")) {
                configFile = new String(args[i + 1]);
            }
        }

        final ApplicationContext ctx = new ClassPathXmlApplicationContext(
                new String[] { configFile });
    }
}
