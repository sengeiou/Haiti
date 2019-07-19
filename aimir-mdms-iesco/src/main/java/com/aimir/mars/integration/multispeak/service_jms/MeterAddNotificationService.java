package com.aimir.mars.integration.multispeak.service_jms;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.mars.integration.multispeak.util.MultiSpeakConstants.ValidationError;
import com.aimir.model.device.EnergyMeter;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Code;
import com.aimir.model.system.Supplier;
import com.aimir.util.DateTimeUtil;

import org.multispeak.version_4.ErrorObject;

@Service
@Transactional
public class MeterAddNotificationService {

    private static Log log = LogFactory
            .getLog(MeterAddNotificationService.class);

    @Autowired
    private MeterDao meterDao;

    @Autowired
    private SupplierDao supplierDao;

    @Autowired
    private JmsTemplate mrInJmsTemplate;

    public ArrayOfErrorObject execute(Meters addMeters) throws Exception {

        log.debug("MeterAddNotificationService execute start..");

        ArrayOfErrorObject resp = new ArrayOfErrorObject();
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(Calendar.getInstance().getTime());

        XMLGregorianCalendar eventTime = DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(c);
        eventTime.setTimezone(DatatypeConstants.FIELD_UNDEFINED);

        List<ElectricMeter> target = new ArrayList<ElectricMeter>(
                addMeters.getElectricMeters().getElectricMeter());
        for (ElectricMeter meter : target) {

            String mdsId = meter.getMeterNo();

            log.debug("MeterNo=" + mdsId);
            // meter.getUtility();
            // meter.getElectricLocationFields().getSubstationCode();
            // meter.getBillingCycle();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String currTime = sdf
                    .format(eventTime.toGregorianCalendar().getTime());

            if (meter.getInstalledDate() != null) {
                currTime = sdf.format(meter.getInstalledDate()
                        .toGregorianCalendar().getTime());
            }

            Meter addMeter = meterDao.get(mdsId);
            ErrorObject obj = new ErrorObject();

            Code deleteStatus = CommonConstants
                    .getMeterStatusByName(MeterStatus.Delete.name());
            if (addMeter != null) {
                if (addMeter.getMeterStatus().getId()
                        .equals(deleteStatus.getId())) {
                    addMeter.setWriteDate(
                            DateTimeUtil.getDateString(new Date()));
                    addMeter.setInstallDate(currTime);
                    addMeter.setMeterStatus(
                            CommonConstants.getMeterStatusByName(
                                    MeterStatus.NewRegistered.name()));

                    // if(meter.getUtility() != null && !"".equals(meter.getUtility())){
                    // supplier = supplierDao.getSupplierByName(meter.getUtility());
                    // }

                    Supplier supplier = supplierDao.getSupplierByName("MOE");
                    if (supplier != null) {
                        addMeter.setSupplier(supplier);
                        addMeter.setSupplierId(supplier.getId());
                    }

                    addMeter.setInstallProperty(meter.getObjectID());
                    try {
                        meterDao.update(addMeter);
                    } catch (Exception e) {
                        obj.setErrorString(
                                ValidationError.SYSTEM_ERROR.getName());
                    }

                    obj.setEventTime(eventTime);
                    obj.setNounType("");
                    obj.setObjectID(meter.getObjectID());
                } else {
                    obj.setErrorString(
                            ValidationError.METER_ALREADY_EXIST.getName()); // Already
                                                                            // exist!
                    obj.setEventTime(eventTime);
                    obj.setNounType("");
                    obj.setObjectID(meter.getObjectID());
                }

            } else {
                addMeter = new EnergyMeter();
                addMeter.setMdsId(mdsId);
                addMeter.setWriteDate(DateTimeUtil.getDateString(new Date()));
                addMeter.setMeterType(CommonConstants
                        .getMeterTypeByName(MeterType.EnergyMeter.name()));
                addMeter.setInstallDate(currTime);
                addMeter.setMeterStatus(CommonConstants.getMeterStatusByName(
                        MeterStatus.NewRegistered.name()));

                Supplier supplier = supplierDao.getSupplierByName("MOE");
                if (supplier != null) {
                    addMeter.setSupplier(supplier);
                    addMeter.setSupplierId(supplier.getId());
                }

                addMeter.setInstallProperty(meter.getObjectID());

                try {
                    meterDao.add(addMeter);
                    obj.setErrorString("");
                } catch (Exception e) {
                    obj.setErrorString(ValidationError.SYSTEM_ERROR.getName());
                }

                obj.setEventTime(eventTime);
                obj.setNounType("");
                obj.setObjectID(meter.getObjectID());
            }

            if (obj.getErrorString() != null
                    && !"".equals(obj.getErrorString())) {
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

        log.debug("MeterAddNotificationService execute end..");

        return resp;
    }
}
