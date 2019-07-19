package com.aimir.mars.integration.multispeak.service_jms;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.multispeak.version_4.ArrayOfErrorObject;
import org.multispeak.version_4.ArrayOfMeterReading1;
import org.multispeak.version_4.ArrayOfReadingValue;
import org.multispeak.version_4.ErrorObject;
import org.multispeak.version_4.ExpirationTime;
import org.multispeak.version_4.InitiateMeterReadingsByMeterID;
import org.multispeak.version_4.MeterID;
import org.multispeak.version_4.MeterReading;
import org.multispeak.version_4.MultiSpeakMsgHeader;
import org.multispeak.version_4.ReadingStatusCode;
import org.multispeak.version_4.ReadingValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Service;

import com.aimir.dao.device.MeterDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.command.mbean.CommandGW.OnDemandOption;
import com.aimir.fep.meter.data.MeterData;
import com.aimir.mars.integration.multispeak.client.CBServerSoap;
import com.aimir.mars.integration.multispeak.data.MultiSpeakMessage;
import com.aimir.mars.integration.multispeak.util.MultiSpeakConstants.ValidationError;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.util.TimeUtil;

@Service
public class InitiateMeterReadingsByMeterIDService extends AbstractService {

    private static Log log = LogFactory
            .getLog(InitiateMeterReadingsByMeterIDService.class);

    final static DecimalFormat dformat = new DecimalFormat("#0.000000");

    @Autowired
    private CommandGW command;

    @Autowired
    private MeterDao meterDao;

    @Autowired
    private JmsTemplate cbInJmsTemplate;

    @Override
    public void execute(MultiSpeakMessage message) throws Exception {

        log.debug("InitiateMeterReadingsByMeterIDService execute start..");

        Calendar requestedTime = message.getRequestedTime();
        Object obj = message.getObject();
        MultiSpeakMsgHeader multiSpeakMsgHeader = message
                .getMultiSpeakMsgHeader();
        Properties prop = new Properties();
        prop.load(getClass().getClassLoader()
                .getResourceAsStream("config/multispeak.properties"));

        multiSpeakMsgHeader.setUserID(
                prop.getProperty("multispeak.response.userid", "nuri"));
        multiSpeakMsgHeader.setPwd(
                prop.getProperty("multispeak.response.passwd", "nuri_headend"));

        log.debug("Message=" + message.toString());

        InitiateMeterReadingsByMeterID request = (InitiateMeterReadingsByMeterID) obj;

        log.debug("Request=" + request.toString());

        ArrayOfMeterReading1 changedMeterReads = new ArrayOfMeterReading1();
        List<MeterID> meterIDs = request.getMeterIDs().getMeterID();

        log.debug("meterIDs size=" + meterIDs.size());

        String responseURL = request.getResponseURL();
        String transactionID = request.getTransactionID();
        ExpirationTime expirationTime = request.getExpTime();
        Calendar expirationDateTime = Calendar.getInstance();
        expirationDateTime.setTime(requestedTime.getTime());

        log.debug("transactionID=" + transactionID);
        log.debug("responseURL=" + responseURL);
        log.debug("expirationTime=" + expirationTime.getValue());

        /*
         * ExpirationTime value가 float 형이다보니 원하는 시간에 실행이 안될수 있다. 소수점 이하가 없는 경우는
         * 원래 방식대로 계산한다. 소수점 있는 경우는 일단 1년(365일) 1달(30일) 기본으로 한다. 초이하 오차는 무시한다.
         */
        switch (expirationTime.getUnits()) {
        case YEARS:
            if (expirationTime.getValue()
                    % (int) expirationTime.getValue() == 0) {
                expirationDateTime.add(Calendar.YEAR,
                        (int) expirationTime.getValue());
            } else {
                expirationDateTime.add(Calendar.SECOND,
                        (int) (expirationTime.getValue() * 365 * 30 * 24 * 60
                                * 60));
            }
            break;
        case MONTHS:
            if (expirationTime.getValue()
                    % (int) expirationTime.getValue() == 0) {
                expirationDateTime.add(Calendar.MONTH,
                        (int) expirationTime.getValue());
            } else {
                expirationDateTime.add(Calendar.SECOND,
                        (int) (expirationTime.getValue() * 30 * 24 * 60 * 60));
            }
            break;
        case WEEKS:
            if (expirationTime.getValue()
                    % (int) expirationTime.getValue() == 0) {
                expirationDateTime.add(Calendar.DATE,
                        (int) expirationTime.getValue() * 7);
            } else {
                expirationDateTime.add(Calendar.SECOND,
                        (int) (expirationTime.getValue() * 7 * 24 * 60 * 60));
            }
            break;
        case DAYS:
            if (expirationTime.getValue()
                    % (int) expirationTime.getValue() == 0) {
                expirationDateTime.add(Calendar.DATE,
                        (int) expirationTime.getValue());
            } else {
                expirationDateTime.add(Calendar.SECOND,
                        (int) (expirationTime.getValue() * 24 * 60 * 60));
            }
            break;
        case HOURS:
            if (expirationTime.getValue()
                    % (int) expirationTime.getValue() == 0) {
                expirationDateTime.add(Calendar.HOUR,
                        (int) expirationTime.getValue());
            } else {
                expirationDateTime.add(Calendar.SECOND,
                        (int) (expirationTime.getValue() * 60 * 60));
            }
            break;
        case MINUTES:
            if (expirationTime.getValue()
                    % (int) expirationTime.getValue() == 0) {
                expirationDateTime.add(Calendar.MINUTE,
                        (int) expirationTime.getValue());
            } else {
                expirationDateTime.add(Calendar.SECOND,
                        (int) (expirationTime.getValue() * 60));
            }
            break;
        case SECONDS:
            expirationDateTime.add(Calendar.SECOND,
                    (int) expirationTime.getValue());
            break;
        case MILLISECONDS:
            expirationDateTime.add(Calendar.MILLISECOND,
                    (int) expirationTime.getValue());
            break;
        case OTHER:
        default:
            // OTHER 따로 정의 된게 없어 SECOND로 처리한다. 스펙에 디폴트가 정의 되어있다면 변경 해줘야 한다.
            expirationDateTime.add(Calendar.SECOND,
                    (int) expirationTime.getValue());
            break;
        }

        /*
         * Regular("1001"),
         * DST("1002"),
         * LowVoltage("1003"),
         * ReverseEnergyFlow("1004"),
         * NoReadOutage("2001"),
         * NoReadDisconnected("2002"),
         * Missing("3001"),
         * ClockError("3002"),
         * TimeResetOccurred("3003"),
         * ChecksumError("3004"),
         * DeviceFailure("3005"),
         * BadAMIData("3006"),
         * SystemEstimate("4001"),
         * OfficeEstimate("4002")
         */

        for (MeterID meterID : meterIDs) {
            String mdsId = meterID.getMeterNo();
            Meter meter = meterDao.get(mdsId);
            Modem modem = meter.getModem();

            MeterData meterData = null;
            Double meteringValue = null;
            String meteringTime = null;

            try {
                // meterData = command.cmdOnDemandByMeter(meter.getMcu().getSysID(), mdsId,
                // modem.getDeviceSerial(), OnDemandOption.READ_CUMMULATIVE_CONSUMPTION.getCode()+"", TimeUtil.getCurrentDay(),
                // TimeUtil.getCurrentDay()); //for plc only 

               // this method all support(gprs modem, plc meter)
                meterData = command.cmdOnDemandMeter(meter.getMcu().getSysID(),
                        mdsId, modem.getDeviceSerial(),
                        OnDemandOption.READ_CUMMULATIVE_CONSUMPTION.getCode()
                                + "",
                        TimeUtil.getCurrentDay(), TimeUtil.getCurrentDay());
                meteringValue = meterData.getParser().getMeteringValue();
                meteringTime = meterData.getTime();
            } catch (Exception e) {
                log.error(e, e);

                /* 
                 * for testing
                 * meteringValue = 0d; meteringTime =
                 * TimeUtil.getCurrentTimeMilli();
                 */
            }

            if (meteringValue != null && meteringTime != null
                    && !"".equals(meteringTime)) {

                meteringValue = new Double(dformat.format(meteringValue));
                ArrayOfReadingValue arrayOfReadingValue = new ArrayOfReadingValue();
                ReadingValue readingValue = new ReadingValue();

                GregorianCalendar gcal = new GregorianCalendar();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

                if (meteringTime != null && !meteringTime.equals("")) {
                    Date d = sdf.parse(meteringTime);
                    gcal.setTime(d);
                } else {
                    Date d = sdf.parse(TimeUtil.getCurrentTime());
                    gcal.setTime(d);
                }

                XMLGregorianCalendar timeStamp = DatatypeFactory.newInstance()
                        .newXMLGregorianCalendar(gcal);
                timeStamp.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
                readingValue.setTimeStamp(timeStamp);

                // readingValue.setMeasurementPeriod(MeasurementPeriod.CURRENT);
                readingValue.setUnits("kWh");
                readingValue.setValue(meteringValue.toString());

                log.info("Current Consumption=" + meteringValue.toString());
                /** TODO IEC61968-9, Annex D **/
                ReadingStatusCode rStatusCode = new ReadingStatusCode();
                rStatusCode.setValue("1001");
                readingValue.setReadingStatusCode(rStatusCode);

                /** TODO IEC61968-9 Annex D 문서 참고 **/
                // ReadingTypeCode rTypeCode = new ReadingTypeCode();
                // readingValue.setReadingTypeCode(rTypeCode);

                arrayOfReadingValue.getReadingValue().add(readingValue);

                MeterReading meterReading = new MeterReading();
                meterReading.setReadingValues(arrayOfReadingValue);
                meterReading.setMeterID(meterID);
                meterReading.setDeviceID(meterID.getMeterNo());
                changedMeterReads.getMeterReading().add(meterReading);
            } else {
                MeterReading meterReading = new MeterReading();
                meterReading.setMeterID(meterID);
                meterReading.setDeviceID(meterID.getMeterNo());
                meterReading.setErrorString(
                        ValidationError.COMMUNICATION_FAILURE.getName());
                changedMeterReads.getMeterReading().add(meterReading);
            }
        }

        Calendar currentTime = Calendar.getInstance();

        // log.info("currentTime="+currentTime+"
        // expirationDateTime="+expirationDateTime);

        if (currentTime.getTimeInMillis() <= expirationDateTime
                .getTimeInMillis() && meterIDs.size() > 0) {
            cbInJmsTemplate.convertAndSend(changedMeterReads,
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

            log.debug("Send InitiateMeterReadingsByMeterIDService Response..");

        }
        log.debug("InitiateMeterReadingsByMeterIDService execute end..");
    }

}
