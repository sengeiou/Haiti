package com.aimir.mars.integration.multispeak.service_jms;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.multispeak.version_4.ArrayOfErrorObject;
import org.multispeak.version_4.ElectricMeter;
import org.multispeak.version_4.Meters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.dao.device.MeterDao;
import com.aimir.mars.integration.multispeak.util.MultiSpeakConstants.ValidationError;
import com.aimir.model.device.Meter;
import com.aimir.util.DateTimeUtil;

import org.multispeak.version_4.ErrorObject;

@Service
@Transactional
public class MeterRetireNotificationService {

    private static Log log = LogFactory
            .getLog(MeterRetireNotificationService.class);

    @Autowired
    private MeterDao meterDao;

    @Autowired
    private JmsTemplate mrInJmsTemplate;

    public ArrayOfErrorObject execute(Meters removedMeters) throws Exception {

        log.debug("MeterRetireNotificationService execute start..");

        ArrayOfErrorObject resp = new ArrayOfErrorObject();
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(Calendar.getInstance().getTime());

        XMLGregorianCalendar eventTime = DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(c);
        eventTime.setTimezone(DatatypeConstants.FIELD_UNDEFINED);

        /**
         * TODO 미터 아이디 존재 여부 등 에러 검사. 기타 검증 추가 필요.
         */
        List<ElectricMeter> target = new ArrayList<ElectricMeter>(
                removedMeters.getElectricMeters().getElectricMeter());
        for (ElectricMeter cd : target) {
            if (cd.getMeterNo() != null) {
                Meter removeMeter = meterDao.get(cd.getMeterNo());
                if (removeMeter == null) {
                    ErrorObject obj = new ErrorObject();
                    obj.setErrorString(
                            ValidationError.ALREADY_DEACTIVATED.getName());// already deleted meter
                    obj.setEventTime(eventTime);
                    obj.setNounType("");
                    obj.setObjectID(cd.getObjectID());
                    if (obj.getErrorString() != null
                            && !"".equals(obj.getErrorString())) {
                        resp.getErrorObject().add(obj);
                    }

                } else {

                    ErrorObject obj = new ErrorObject();
                    obj.setErrorString("");
                    obj.setEventTime(eventTime);
                    obj.setNounType("");
                    obj.setObjectID(cd.getObjectID());

                    try {
                        removeMeter.setDeleteDate(DateTimeUtil
                                .getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
                        removeMeter.setMeterStatus(
                                CommonConstants.getMeterStatusByName(
                                        MeterStatus.Deactivation.name()));
                        meterDao.update(removeMeter);
                    } catch (Exception e) {
                        log.error(e, e);
                        obj.setErrorString(
                                ValidationError.SYSTEM_ERROR.getName());
                    }

                    if (obj.getErrorString() != null
                            && !"".equals(obj.getErrorString())) {
                        resp.getErrorObject().add(obj);
                    }
                }
            } else {
                ErrorObject obj = new ErrorObject();
                obj.setErrorString(ValidationError.INVALID_PARAMETER.getName());
                obj.setEventTime(eventTime);
                obj.setNounType("");
                obj.setObjectID(cd.getObjectID());
                resp.getErrorObject().add(obj);
            }
        }

        mrInJmsTemplate.convertAndSend(resp,
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

        log.debug("MeterRetireNotificationService execute End..");

        return resp;
    }
}
