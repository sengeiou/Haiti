package com.aimir.mars.integration.bulkreading.xml.service;

import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
import com.aimir.util.TimeUtil;
import com.oracle.xmlns.ssys.nurimeterdataproxy.nurimeterdata.NuriMeterData;

public class SendNetstationMonitoringWSTasklet implements Tasklet {

    private static Log log = LogFactory.getLog(SendNetstationMonitoringWSTasklet.class);
    final static DecimalFormat dformat = new DecimalFormat("0.###");

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected NuriMeterData webServiceClient;

    protected boolean isDeliverDataNM = false;

    @Override
    public RepeatStatus execute(StepContribution arg0, ChunkContext arg1)
            throws Exception {

        log.info("send Netstation-Monitoring  data.");

        List<Map<String, Object>> ipEvOption = jdbcTemplate
                .queryForList(
                        "select attributename, attributevalue from ip_nm_option where codetype=?", "OP");
        for (Map<String, Object> row : ipEvOption) {
            if(row.get("ATTRIBUTENAME").toString().toUpperCase().equals("CNF_DELIVERY_NM")) {
                isDeliverDataNM = Boolean.parseBoolean(row.get("ATTRIBUTEVALUE").toString().toLowerCase());
            } 
        }

        if (isDeliverDataNM) {
            sendData();
        }

        return RepeatStatus.FINISHED;
    }

    private void sendData() throws Exception {
        List<Map<String, Object>> batch_ids = jdbcTemplate.queryForList(
                "select distinct b.batch_id from ip_nm_outbound "
                        + " a, ip_nm_batches b where a.batch_id=b.batch_id and b.batch_status=2");

        String sql = "\n select mdev_id, mdev_type, yyyymmdd, channel, obis_id, yyyymmddhhmmss, mv_value, \n"
                + "\n       cap_date_meter, cap_date_dcu, cap_device_type, cap_device_id, location_name, mv_valid \n"
                + "\n from ip_nm_outbound " 
                + "\n where batch_id=? order by mdev_id, yyyymmdd, channel, yyyymmddhhmmss ";
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
            String yyyymmddhhmmss = null;
            Double mv_value = null;
            String captureDeviceID = null;
            String captureDeviceType = null;

            for (Map<String, Object> row : list) {
                
                //처음 시작..
                if(mdev_id == null) {
                    device = new Device();
                    device.setHeadEndExternalId(
                            MarsProperty.getProperty("HES.ID", "NURI001"));// SET EACH FEP ID (NURI01 ~ 15)
                    device.setDeviceIdentifierNumber((String) row.get("MDEV_ID"));
                    String locationCode = "HES";
                    if(Boolean.parseBoolean(MarsProperty.getProperty("HES.ACTOR.LOCATION", "false"))) {
                        locationCode = (String)row.get("LOCATION_NAME");
                    }
                    if(locationCode != null) {
                        device.setIssuerID(locationCode);
                    }
                    mdList = new InitialMeasurementDataList();
                    md = new InitialMeasurementData();
                    preVEE = new PreVEE();
                    preVEE.setMcIdN((String) row.get("OBIS_ID"));
                    preVEE.setStDt(convertDateTime((String) row.get("YYYYMMDDHHMMSS")));
                    mars = new Msrs();
                }

                //디바이스 정보가 바끼면..
                if ((mdev_type != null && !mdev_type.equals((String) row.get("MDEV_TYPE"))) ||
                    (mdev_id != null && !mdev_id.equals((String) row.get("MDEV_ID")))) {

                    if (preVEE != null) {
                        preVEE.setEnDt(convertDateTime(endDate));
                        preVEE.setMsrs(mars);
                    }

                    if (md != null) {
                        md.setPreVEE(preVEE);
                        mdList.getInitialMeasurementData().add(md);
                    }

                    if (device != null) {
                        device.setInitialMeasurementDataList(mdList);
                        deviceList.getDevice().add(device);
                    }

                    device = new Device();
                    device.setHeadEndExternalId(
                            MarsProperty.getProperty("HES.ID", "NURI001"));// SET EACH FEP ID (NURI01 ~ 15)
                    device.setDeviceIdentifierNumber((String) row.get("MDEV_ID"));
                    String locationCode = "HES";
                    if(Boolean.parseBoolean(MarsProperty.getProperty("HES.ACTOR.LOCATION", "false"))) {
                        locationCode = (String)row.get("LOCATION_NAME");
                    }
                    if(locationCode != null) {
                        device.setIssuerID(locationCode);
                    }
                    mdList = new InitialMeasurementDataList();
                    md = new InitialMeasurementData();
                    preVEE = new PreVEE();
                    preVEE.setMcIdN((String) row.get("OBIS_ID"));
                    preVEE.setStDt(convertDateTime((String) row.get("YYYYMMDDHHMMSS")));
                    mars = new Msrs();
                } else {
                    // 채널이나 날짜가 바끼면..
                    if ((channel != null && !channel.equals(((BigDecimal) row.get("CHANNEL")).toString())) ||
                        (yyyymmdd != null && !yyyymmdd.equals(((String) row.get("YYYYMMDD"))))) {

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
                }

                channel = ((BigDecimal) row.get("CHANNEL")).toString();
                mdev_type = (String) row.get("MDEV_TYPE");
                mdev_id = (String) row.get("MDEV_ID");
                yyyymmddhhmmss = (String) row.get("YYYYMMDDHHMMSS");
                mv_value = ((BigDecimal) row.get("MV_VALUE")).doubleValue(); 
                captureDeviceID = (String) row.get("CAP_DEVICE_ID");
                captureDeviceType = (String) row.get("CAP_DEVICE_TYPE");
                if(captureDeviceType!=null && captureDeviceType.equals("MCU")) {
                    captureDeviceType  = "DCU";
                }

                ML ml = new ML();
                ml.setTs(extractedTime(yyyymmddhhmmss));
                ml.setMeterDt(convertDateTime(TimeUtil.getDateUsingFormat(((BigDecimal) row.get("CAP_DATE_METER")).longValue(), "yyyyMMddHHmmss")));
                ml.setCaptureDt(convertDateTime(TimeUtil.getDateUsingFormat(((BigDecimal) row.get("CAP_DATE_DCU")).longValue(), "yyyyMMddHHmmss")));
                ml.setCaptureDeviceID(captureDeviceID);
                ml.setCaptureDeviceType(captureDeviceType);
                ml.setQ(mv_value);
                ml.setFc(((BigDecimal) row.get("MV_VALID")).toString());
                mars.getML().add(ml);

                endDate = yyyymmddhhmmss;
            }

            if (preVEE != null) {
                preVEE.setEnDt(convertDateTime(endDate));
                preVEE.setMsrs(mars);
            }

            if (md != null) {
                md.setPreVEE(preVEE);
                mdList.getInitialMeasurementData().add(md);
            }

            if (device != null) {
                device.setInitialMeasurementDataList(mdList);
                deviceList.getDevice().add(device);
            }

            meterReading.getDeviceList().add(deviceList);
            meterReading.setHeader(header);

            try {
                if(Boolean.parseBoolean(MarsProperty.getProperty("xmldump.isrunning","false"))) {
                    FileOutputStream fos = null;
                    BufferedOutputStream bos = null;
                    XMLEncoder xmlEncoder = null;
                    
                    try {
                        fos = new FileOutputStream(MarsProperty.getProperty("xmldump.pathandfilename","/tmp/meterreading-") + System.currentTimeMillis() + ".xml");
                        bos = new BufferedOutputStream(fos);
                        xmlEncoder = new XMLEncoder(bos);
                        xmlEncoder.writeObject(meterReading);
                    } catch (Exception e) {
                        log.error("xml dump fail...");
                    } finally {
                        try{
                            if(xmlEncoder != null) { xmlEncoder.close(); }
                        }catch(Exception e) { }
                        try{
                            if(bos != null) { bos.close(); }
                        }catch(Exception e) { }
                        try{
                            if(fos != null) { fos.close(); }
                        }catch(Exception e) { }
                    }
                }

                XMLGregorianCalendar receivedDate = (XMLGregorianCalendar) sendWSMessage(meterReading);
                if(receivedDate != null) {
                    log.debug("batch_id=" + batch_id_val + " size=" + list.size() + " responseDate=" + receivedDate + " success..");
                    java.sql.Date tempdate = new java.sql.Date(receivedDate.toGregorianCalendar().getTime().getTime());

                    jdbcTemplate.update(
                            "update ip_nm_batches set batch_status=?,delivered_date=? where batch_id=?",
                            new Object[] { 3, tempdate, batch_id_val });
                } else {
                    log.debug("batch_id=" + batch_id_val + " size=" + list.size() + " fail.." );
                }
            }catch(Exception e) {
                log.debug("batch_id=" + batch_id_val + " size=" + list.size() + " fail.." );
                log.error(e,e);
            }
        }
        log.info(this.getClass().getSimpleName() + " end.");
    }

    private Object sendWSMessage(MeterReadingsType message) throws Exception {
        Client client = ClientProxy.getClient(webServiceClient);
        HTTPConduit http = (HTTPConduit) client.getConduit();
        TLSClientParameters tlsParams = new TLSClientParameters();
        tlsParams.setDisableCNCheck(true); // CN Name check ignore...
        http.setTlsClientParameters(tlsParams);
        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        httpClientPolicy.setAllowChunking(false);
        http.setClient(httpClientPolicy);
        log.debug(client.getEndpoint());

        return webServiceClient.nuriMeterDataBulk(message);
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
