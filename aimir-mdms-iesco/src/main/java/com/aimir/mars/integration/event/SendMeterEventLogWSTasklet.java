package com.aimir.mars.integration.event;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
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
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.mars.util.MarsProperty;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.oracle.xmlns.ssys.nurieventsproxy.nurieventsproxy.NuriEvents;

import ch.iec.tc57._2011.enddeviceevents.Asset;
import ch.iec.tc57._2011.enddeviceevents.EndDeviceEvent;
import ch.iec.tc57._2011.enddeviceevents.EndDeviceEvent.EndDeviceEventType;
import ch.iec.tc57._2011.enddeviceevents.EndDeviceEventDetail;
import ch.iec.tc57._2011.enddeviceevents.EndDeviceEvents;
import ch.iec.tc57._2011.enddeviceeventsmessage.EndDeviceEventsEventMessageType;
import ch.iec.tc57._2011.enddeviceeventsmessage.EndDeviceEventsPayloadType;
import ch.iec.tc57._2011.schema.message.HeaderType;

public class SendMeterEventLogWSTasklet implements Tasklet {

    private static Log log = LogFactory.getLog(SendMeterEventLogWSTasklet.class);

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    @Resource(name = "webServiceClientMeterEvent")
    protected NuriEvents webServiceClientMeterEvent;

    @Autowired
    @Resource(name = "webServiceClientAlert")
    protected NuriEvents webServiceClientAlert;

    @Autowired
    private MeterDao meterDao;

    @Autowired
    private ModemDao modemDao;

    protected boolean isDeliverData = false;

    @Override
    public RepeatStatus execute(StepContribution arg0, ChunkContext arg1)
            throws Exception {
        log.info("send metereventlog data.");

        List<Map<String, Object>> ipEvOption = jdbcTemplate
                .queryForList(
                        "select attributename, attributevalue from ip_ev_option where codetype=?", "OP");
        for (Map<String, Object> row : ipEvOption) {
            if(row.get("ATTRIBUTENAME").toString().toUpperCase().equals("CNF_DELIVERY")) {
                isDeliverData = Boolean.parseBoolean(row.get("ATTRIBUTEVALUE").toString().toLowerCase());
            }
        }
        if (isDeliverData) {
            sendData();
        } else {
            throw new Exception("CNF_DELIVERY is 'FALSE'");
        }

        return RepeatStatus.FINISHED;
    }

    private void sendData() throws Exception {
        List<Map<String, Object>> batch_ids = jdbcTemplate.queryForList(
                "select distinct b.batch_id from ip_ev_outbound a, ip_ev_batches b where a.batch_id=b.batch_id and b.batch_status=2 order by batch_id ");

        String sql = "select EVTYPE,ACTIVATOR_ID,METEREVENT_ID,EVENTOBIS_ID,OPEN_TIME,ACTIVATOR_TYPE,INTEGRATED,MESSAGE,SUPPLIER_ID,LOCATION_NAME,WRITETIME,YYYYMMDD from ip_ev_outbound where batch_id=?";
        List<Map<String, Object>> list = null;

        boolean isMeterEvent = false;
        for (Map<String, Object> batch_id : batch_ids) {
            long batch_id_val = ((BigDecimal) batch_id.get("BATCH_ID")).longValue();

            list = jdbcTemplate.queryForList(sql, batch_id_val);

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

            for (Map<String, Object> row : list) {
                if ( ((String)row.get("EVTYPE")).equals("M") ) {
                    isMeterEvent = true;
                }
                EndDeviceEvent event = new EndDeviceEvent();
                String obis =  (String)row.get("EVENTOBIS_ID");
                event.setMRID(obis);

                GregorianCalendar gcal = new GregorianCalendar();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                Date d = sdf.parse((String)row.get("OPEN_TIME"));
                gcal.setTime(d);
                XMLGregorianCalendar timeStamp = DatatypeFactory.newInstance()
                        .newXMLGregorianCalendar(gcal);
                timeStamp.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
                timeStamp.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);
                event.setCreatedDateTime(timeStamp);
                event.setSeverity("1");  //TODO
                Asset asset = new Asset();
                String meterId = (String)row.get("ACTIVATOR_ID");
                asset.setMRID(meterId);

                String geocode = "HES";
                if(Boolean.parseBoolean(MarsProperty.getProperty("HES.ACTOR.LOCATION", "false"))) {
                    geocode = (String)row.get("LOCATION_NAME");
                }
                if(geocode != null) {
                    event.setIssuerID(geocode);
                }

                event.setAssets(asset);
                EndDeviceEventType evt = new EndDeviceEventType();
                event.setEndDeviceEventType(evt);
                if(Boolean.parseBoolean(MarsProperty.getProperty("powel.enableFirmwareInformation","false"))) {
                    if(obis.equals("2.20.7.105") || obis.equals("3.11.7.213")) {
                        Meter met = meterDao.get(meterId);
                        Modem mod = modemDao.get(met.getModemId());

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
            }
            payload.setEndDeviceEvents(events);
            message.setHeader(header);
            message.setPayload(payload);

            try {
                XMLGregorianCalendar receivedDate = null;
                if(isMeterEvent) {
                    receivedDate = (XMLGregorianCalendar) sendWSMessageMeterEvent(message);
                    log.debug("execute.result=" + receivedDate);
                } else {
                    receivedDate = (XMLGregorianCalendar) sendWSMessageAlert(message);
                    log.debug("execute.result=" + receivedDate);
                }
                if(receivedDate != null) {
                    log.debug("batch_id=" + batch_id_val + " size=" + list.size() + " responseDate=" + receivedDate + " success..");
                    java.sql.Date tempdate = new java.sql.Date(receivedDate.toGregorianCalendar().getTime().getTime());
    
                    jdbcTemplate.update(
                            "update ip_ev_batches set batch_status=?,delivered_date=? where batch_id=?",
                            new Object[] { 3, tempdate, batch_id_val });
                } else {
                    log.debug("batch_id=" + batch_id_val + " size=" + list.size() + " fail.." );
                }
            }catch(Exception e) {
                log.debug("batch_id=" + batch_id_val + " size=" + list.size() + " fail.." );
                log.error(e,e);
            }
        }
    }

    private Object sendWSMessageMeterEvent(EndDeviceEventsEventMessageType message) throws Exception {
        Client client = ClientProxy.getClient(webServiceClientMeterEvent);
        HTTPConduit http = (HTTPConduit) client.getConduit();
        TLSClientParameters tlsParams = new TLSClientParameters();
        tlsParams.setDisableCNCheck(true); // CN Name check ignore...
        http.setTlsClientParameters(tlsParams);
        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        httpClientPolicy.setAllowChunking(false);
        http.setClient(httpClientPolicy);

        return webServiceClientMeterEvent.nuriEventsBulk(message);
    }

    private Object sendWSMessageAlert(EndDeviceEventsEventMessageType message) throws Exception {
        Client client = ClientProxy.getClient(webServiceClientAlert);
        HTTPConduit http = (HTTPConduit) client.getConduit();
        TLSClientParameters tlsParams = new TLSClientParameters();
        tlsParams.setDisableCNCheck(true); // CN Name check ignore...
        http.setTlsClientParameters(tlsParams);
        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        httpClientPolicy.setAllowChunking(false);
        http.setClient(httpClientPolicy);

        return webServiceClientAlert.nuriEventsBulk(message);
    }
}
