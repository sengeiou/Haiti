package com.aimir.mars.integration.event;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;

import com.aimir.mars.util.MarsProperty;

import ch.iec.tc57._2011.enddeviceevents.Asset;
import ch.iec.tc57._2011.enddeviceevents.EndDeviceEvent;
import ch.iec.tc57._2011.enddeviceevents.EndDeviceEvents;
import ch.iec.tc57._2011.enddeviceeventsmessage.EndDeviceEventsEventMessageType;
import ch.iec.tc57._2011.enddeviceeventsmessage.EndDeviceEventsPayloadType;
import ch.iec.tc57._2011.schema.message.HeaderType;

public class SendMeterEventLogTasklet implements Tasklet {

    private static Log log = LogFactory.getLog(SendMeterEventLogTasklet.class);

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected JmsTemplate jmsTemplate;

    @Autowired
    private Destination destination;

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
                "select distinct b.batch_id from ip_ev_outbound a, ip_ev_batches b where a.batch_id=b.batch_id and b.batch_status=2");

        String sql = "select ACTIVATOR_ID,METEREVENT_ID,EVENTOBIS_ID,OPEN_TIME,ACTIVATOR_TYPE,INTEGRATED,MESSAGE,SUPPLIER_ID,WRITETIME,YYYYMMDD from ip_ev_outbound where batch_id=?";
        List<Map<String, Object>> list = null;

        for (Map<String, Object> batch_id : batch_ids) {
            long batch_id_val = ((BigDecimal) batch_id.get("BATCH_ID")).longValue();

            list = jdbcTemplate.queryForList(sql, batch_id_val);

            EndDeviceEventsEventMessageType message = new EndDeviceEventsEventMessageType();
            HeaderType header = new HeaderType();
            header.setNoun("EndDeviceEvents");
            header.setVerb("created");

            try {
                header.setTimestamp(javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar());
            } catch (DatatypeConfigurationException e) {
                e.printStackTrace();
            }
            header.setRevision("1.0");
            header.setSource(MarsProperty.getProperty("HES.ID", "NURI001"));// SET EACH FEP ID (NURI01 ~ 15)
            
            EndDeviceEventsPayloadType payload = new EndDeviceEventsPayloadType();
            EndDeviceEvents events = new EndDeviceEvents();

            for (Map<String, Object> row : list) {
                EndDeviceEvent event = new EndDeviceEvent();
                event.setMRID((String)row.get("EVENTOBIS_ID"));

                GregorianCalendar gcal = new GregorianCalendar();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                Date d = sdf.parse((String)row.get("OPEN_TIME"));
                gcal.setTime(d);
                event.setCreatedDateTime(DatatypeFactory.newInstance()
                        .newXMLGregorianCalendar(gcal));
                event.setSeverity("1");  //TODO
                Asset asset = new Asset();
                asset.setMRID((String)row.get("ACTIVATOR_ID"));
                event.setAssets(asset);
                events.getEndDeviceEvent().add(event);
            }
            payload.setEndDeviceEvents(events);
            message.setHeader(header);
            message.setPayload(payload);

            jmsTemplate.convertAndSend(destination, message,
                    new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message message)
                        throws JMSException {
                    if (message instanceof BytesMessage) {
                        BytesMessage messageBody = (BytesMessage) message;
                        messageBody.reset();
                        Long length = messageBody.getBodyLength();
                        log.debug("***** MESSAGE LENGTH is " + length
                                + " bytes");
                        byte[] byteMyMessage = new byte[length
                                .intValue()];
                        int red = messageBody.readBytes(byteMyMessage);
                        log.debug("***** SENDING MESSAGE - \n"
                                + "<!-- MSG START -->\n"
                                + new String(byteMyMessage)
                                + "\n<!-- MSG END -->");
                    }
                    return message;
                }
            });

            jdbcTemplate.update(
                    "update ip_ev_batches set batch_status=3 where batch_id=?",
                    new Object[] { batch_id_val });
            if(log.isDebugEnabled()) {
                log.debug(null);
            }
        }
    }
}
