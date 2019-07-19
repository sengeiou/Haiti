package com.aimir.mars.integration.bulkreading.xml.service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

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

import com.aimir.mars.integration.bulkreading.xml.cim.MeterReadingsType;
import com.aimir.mars.integration.bulkreading.xml.cim.header.MessageHeaderType;
import com.aimir.mars.integration.bulkreading.xml.cim.meterreading.DeviceListType;
import com.aimir.mars.integration.bulkreading.xml.cim.meterreading.DeviceListType.Device;
import com.aimir.mars.integration.bulkreading.xml.cim.meterreading.DeviceListType.Device.InitialMeasurementDataList;
import com.aimir.mars.integration.bulkreading.xml.cim.meterreading.DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData;
import com.aimir.mars.integration.bulkreading.xml.cim.meterreading.DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData.PreVEE;
import com.aimir.mars.integration.bulkreading.xml.cim.meterreading.DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData.PreVEE.Msrs;
import com.aimir.mars.integration.bulkreading.xml.cim.meterreading.DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData.PreVEE.Msrs.ML;
import com.aimir.mars.util.MarsProperty;
import com.aimir.util.DateTimeUtil;

public class SendMeterValueTasklet implements Tasklet {

    private static Log log = LogFactory.getLog(SendMeterValueTasklet.class);
    final static DecimalFormat dformat = new DecimalFormat("0.###");

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected JmsTemplate jmsTemplate;

    @Autowired
    protected Destination destination;

    protected boolean isDeliverDataEM = false;
    protected boolean isDeliverDataWM = false;
    protected boolean isDeliverDataGM = false;
    protected boolean isDeliverDataHM = false;

    @Override
    public RepeatStatus execute(StepContribution arg0, ChunkContext arg1)
            throws Exception {

        log.info("send metervalue data.");

        List<Map<String, Object>> ipEvOption = jdbcTemplate
                .queryForList(
                        "select attributename, attributevalue from ip_mv_option where codetype=?", "OP");
        for (Map<String, Object> row : ipEvOption) {
            if(row.get("ATTRIBUTENAME").toString().toUpperCase().equals("CNF_DELIVERY_EM")) {
                isDeliverDataEM = Boolean.parseBoolean(row.get("ATTRIBUTEVALUE").toString().toLowerCase());
            } else if(row.get("ATTRIBUTENAME").toString().toUpperCase().equals("CNF_DELIVERY_WM")) {
                    isDeliverDataWM = Boolean.parseBoolean(row.get("ATTRIBUTEVALUE").toString().toLowerCase());
            } else if(row.get("ATTRIBUTENAME").toString().toUpperCase().equals("CNF_DELIVERY_GM")) {
                isDeliverDataGM = Boolean.parseBoolean(row.get("ATTRIBUTEVALUE").toString().toLowerCase());
            } else if(row.get("ATTRIBUTENAME").toString().toUpperCase().equals("CNF_DELIVERY_HM")) {
                isDeliverDataHM = Boolean.parseBoolean(row.get("ATTRIBUTEVALUE").toString().toLowerCase());
            }
        }

        if (isDeliverDataEM) {
            sendData("em");
        }
        if (isDeliverDataWM) {
            sendData("wm");
        }
        if (isDeliverDataGM) {
            sendData("gm");
        }
        if (isDeliverDataHM) {
            sendData("hm");
        }

        return RepeatStatus.FINISHED;
    }

    private void sendData(String tablename) throws Exception {
        List<Map<String, Object>> batch_ids = jdbcTemplate.queryForList(
                "select distinct b.batch_id from ip_mv_outbound_" + tablename
                        + " a, ip_mv_batches b where a.batch_id=b.batch_id and b.batch_status=2");

        String sql = "select mdev_id, mdev_type, yyyymmdd, channel, obis_id, yyyymmddhhmmss, mv_value, cap_date_meter, cap_date_dcu, mv_valid from ip_mv_outbound_"
                + tablename
                + " where batch_id=? order by mdev_id, yyyymmdd, channel, yyyymmddhhmmss ";
        List<Map<String, Object>> list = null;

        for (Map<String, Object> batch_id : batch_ids) {
            long batch_id_val = ((BigDecimal) batch_id.get("BATCH_ID")).longValue();

            MeterReadingsType meterReading = new MeterReadingsType();
            MessageHeaderType header = new MessageHeaderType();
            header.setNoun("deviceList");
            header.setVerb("created");
            header.setContext("PRODUCTION");
            header.setRevision("1.0");
            header.setAckRequired("false");
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

            list = jdbcTemplate.queryForList(sql, batch_id_val);

            DeviceListType deviceList = new DeviceListType();
            Device device = null;
            InitialMeasurementDataList mdList = null;
            InitialMeasurementData md = null;
            PreVEE preVEE = null;
            Msrs mars = null;
            String endDate = null;
            String yyyymmdd = null;

            String mdev_type = null;
            String mdev_id = null;
            String channel = null;
            String channelNew = null;
            String yyyymmddhhmmss = null;
            Double mv_value = null;
            String captureDeviceID = null;

            for (Map<String, Object> row : list) {
                channelNew = ((BigDecimal) row.get("CHANNEL")).toString();
                if (mdev_type == null
                        || !mdev_type.equals((String) row.get("MDEV_TYPE"))
                        || mdev_id == null
                        || !mdev_id.equals((String) row.get("MDEV_ID"))) {

                    if (device != null) {
                        device.setInitialMeasurementDataList(mdList);
                        deviceList.getDevice().add(device);
                    }

                    device = new Device();
                    device.setHeadEndExternalId(
                            MarsProperty.getProperty("HES.ID", "NURI001"));// SET EACH FEP ID (NURI01 ~ 15)
                    device.setDeviceIdentifierNumber(mdev_id);
                    String locationCode = (String)row.get("LOCATION_NAME");
                    if(locationCode != null) {
                        device.setIssuerID(locationCode);
                    }
                    mdList = new InitialMeasurementDataList();
                }

                if (channel == null
                        || !channel.equals(channelNew)
                        || yyyymmdd == null
                        || !yyyymmdd.equals(((String) row.get("YYYYMMDD")))) {

                    if (preVEE != null) {
                        preVEE.setEnDt(convertDateTime(endDate));
                        preVEE.setMsrs(mars);
                    }
                    if (md != null) {
                        md.setPreVEE(preVEE);
                        mdList.getInitialMeasurementData().add(md);
                    }
                    md = new InitialMeasurementData();
                    preVEE = new PreVEE();
                    preVEE.setMcIdN((String) row.get("OBIS_ID"));
                    preVEE.setStDt(convertDateTime((String) row.get("YYYYMMDDHHMMSS")));
                    mars = new Msrs();
                }

                mdev_type = (String) row.get("MDEV_TYPE");
                mdev_id = (String) row.get("MDEV_ID");
                channel = channelNew;
                yyyymmddhhmmss = (String) row.get("YYYYMMDDHHMMSS");
                mv_value = ((BigDecimal) row.get("MV_VALUE")).doubleValue(); 
                captureDeviceID = (String) row.get("CAP_DEVICE_ID");

                ML ml = new ML();
                ml.setTs(extractedTime(yyyymmddhhmmss));
                ml.setMeterDt(convertDateTime((String) row.get("CAP_DATE_METER")));
                ml.setCaptureDt(convertDateTime((String) row.get("CAP_DATE_DCU")));
                ml.setCaptureDeviceID(captureDeviceID);
                ml.setCaptureDeviceType("DCU");
                ml.setQ(mv_value);
                ml.setFc(((BigDecimal) row.get("MV_VALID")).toString());
                mars.getML().add(ml);

                endDate = yyyymmddhhmmss;
            }
            meterReading.getDeviceList().add(deviceList);
            meterReading.setHeader(header);
            jmsTemplate.convertAndSend(destination, meterReading,
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
                    "update ip_mv_batches set batch_status=3 where batch_id=?",
                    new Object[] { batch_id_val });
            if(log.isDebugEnabled()) {
                log.debug(deviceList);
            }
        }
    }

    private XMLGregorianCalendar convertDateTime(String yyyymmddhhmmss) {
        GregorianCalendar gCal = new GregorianCalendar();
        XMLGregorianCalendar dateTimeXML = null;
        try {
            gCal.setTime(DateTimeUtil.getCalendar(yyyymmddhhmmss).getTime());
            dateTimeXML = DatatypeFactory.newInstance()
                    .newXMLGregorianCalendar(gCal);
            dateTimeXML.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
            dateTimeXML.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateTimeXML;
    }
    private String formattedDate(String yyyymmddhhmmss) {
        return yyyymmddhhmmss.substring(0, 4) + "-"
                + yyyymmddhhmmss.substring(4, 6) + "-"
                + yyyymmddhhmmss.substring(6, 8) + "T"
                + yyyymmddhhmmss.substring(8, 10) + ":"
                + yyyymmddhhmmss.substring(10, 12) + ":"
                + yyyymmddhhmmss.substring(12, 14);
    }

    private String extractedDateTime(String yyyymmddhhmmss) {
        return yyyymmddhhmmss.substring(0, 4) + "-"
                + yyyymmddhhmmss.substring(4, 6) + "-"
                + yyyymmddhhmmss.substring(6, 8) + "T"
                + yyyymmddhhmmss.substring(8, 10) + ":"
                + yyyymmddhhmmss.substring(10, 12) + ":"
                + yyyymmddhhmmss.substring(12, 14);
    }

    private String extractedTime(String yyyymmddhhmmss) {
        return yyyymmddhhmmss.substring(8, 10) + "."
                + yyyymmddhhmmss.substring(10, 12) + "."
                + yyyymmddhhmmss.substring(12, 14);
    }
}
