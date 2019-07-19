package com.aimir.mars.integration.multispeak.service_jms;

import java.text.SimpleDateFormat;
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
import org.multispeak.version_4.Address;
import org.multispeak.version_4.ArrayOfAssessment;
import org.multispeak.version_4.ArrayOfAssessmentLocation;
import org.multispeak.version_4.ArrayOfErrorObject;
import org.multispeak.version_4.Assessment;
import org.multispeak.version_4.AssessmentLocation;
import org.multispeak.version_4.DetailedAddressFields;
import org.multispeak.version_4.ErrorObject;
import org.multispeak.version_4.GpsPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.McuStatus;
import com.aimir.constants.CommonConstants.McuType;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.mars.integration.multispeak.util.MultiSpeakConstants.Category;
import com.aimir.mars.integration.multispeak.util.MultiSpeakConstants.ValidationError;
import com.aimir.model.device.MCU;
import com.aimir.model.system.Code;

@Service
@Transactional
public class AssessmentLocationChangedNotificationService {

    @Autowired
    private MCUDao mcuDao;

    @Autowired
    private DeviceModelDao deviceModelDao;

    @Autowired
    private SupplierDao supplierDao;

    @Autowired
    private CodeDao codeDao;

    @Autowired
    private JmsTemplate oaInJmsTemplate;

    private static Log log = LogFactory
            .getLog(AssessmentLocationChangedNotificationService.class);

    public ArrayOfErrorObject execute(ArrayOfAssessmentLocation locations)
            throws Exception {

        if (log.isDebugEnabled())
            log.debug(
                    "AssessmentLocationChangedNotificationService execute start..");
        if (log.isTraceEnabled())
            log.trace("Parameter : " + locations);

        ArrayOfErrorObject resp = new ArrayOfErrorObject();
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(Calendar.getInstance().getTime());

        XMLGregorianCalendar eventTime = DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(c);
        eventTime.setTimezone(DatatypeConstants.FIELD_UNDEFINED);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Code deleteStatus = codeDao.getCodeIdByCodeObject("1.1.4.2");
        Code normalStatus = codeDao.getCodeIdByCodeObject("1.1.4.1");

        List<AssessmentLocation> target = locations.getAssessmentLocation();

        for (AssessmentLocation location : target) {

            ErrorObject obj = new ErrorObject();
            ArrayOfAssessment assessmentList = location.getAssessmentList();
            GpsPoint gpsPoint = location.getGpsPoint();
            Address address = location.getAddress();
            String gridLocation = location.getGridLocation();
            MCU mcu = null;

            try {
                if (assessmentList != null
                        && assessmentList.getAssessment().size() > 0) {

                    Assessment assessment = assessmentList.getAssessment().get(0);
                    String category = assessment.getCategory();
                    String elementID = assessment.getElementID();
                    XMLGregorianCalendar createOn = assessment.getCreatedOn();
                    // String elementName = assessment.getElementName();
                    String elementType = assessment.getElementType();
                    String comments = assessment.getComments();
                    XMLGregorianCalendar closedOn = assessment.getClosedOn();

                    if (category == null || "".equals(elementID)) {
                        obj.setErrorString(
                                ValidationError.INVALID_PARAMETER.getName());
                        obj.setNounType("");
                        obj.setObjectID(assessment.getObjectID());
                    }
                    if (elementID == null && "".equals(elementID)) {
                        obj.setErrorString(
                                ValidationError.INVALID_PARAMETER.getName());
                        obj.setNounType("");
                        obj.setObjectID(assessment.getObjectID());
                    }
                    if (elementType == null && "".equals(elementType)) {
                        obj.setErrorString(
                                ValidationError.INVALID_PARAMETER.getName());
                        obj.setNounType("");
                        obj.setObjectID(assessment.getObjectID());
                    }
                    mcu = mcuDao.get(elementID);

                    if (category.equals(Category.Add.getName())) {

                        if (mcu != null) {
                            obj.setErrorString(ValidationError.DCU_ALREADY_EXIST
                                    .getName());
                        } else {
                            mcu = new MCU();
                            mcu.setSysID(elementID);
                            McuType mcuTypeEnum = McuType.DCU;

                            if (comments != null && !"".equals(comments)) {
                                // “Default RF Antenna”
                                // “Extension RF Antenna”
                                // “Default MBB Antenna”
                                // “Extension MBB Antenna”
                                mcu.setSysDescr(comments);
                            }
                            mcu.setMcuType(CommonConstants
                                    .getMcuTypeByName(McuType.DCU.name()));
                            mcu.setInstallDate(sdf.format(
                                    createOn.toGregorianCalendar().getTime()));
                            mcu.setLastModifiedDate(sdf.format(
                                    createOn.toGregorianCalendar().getTime()));
                            mcu.setMcuStatus(normalStatus);

                            if (gpsPoint != null) {
                                mcu.setGpioX(gpsPoint.getLatitude());
                                mcu.setGpioY(gpsPoint.getLongitude());
                                mcu.setGpioZ(gpsPoint.getAltitude()==null?0:gpsPoint.getAltitude());
                            }

                            if (address != null) {

                                String locDetail = "";
                                locDetail += address.getAddress1();
                                locDetail += " " + address.getAddress2();
                                locDetail += "," + address.getTownCode();
                                locDetail += "," + address.getCity();
                                locDetail += "," + address.getPostalCode();
                                locDetail += "," + address.getState();
                                locDetail += "," + address.getCountry();
                                DetailedAddressFields addressField = address
                                        .getDetailedAddressFields();

                                if (addressField != null) {
                                    addressField.getAddressGeneral();
                                    addressField.getBuildingNumber();
                                    addressField.getPostOfficeBox();
                                    addressField.getStreetNumber();
                                    addressField.getStreetPrefix();
                                    addressField.getStreetSuffix();
                                    addressField.getStreetType();
                                    addressField.getRegion();
                                    addressField.getSuiteNumber();

                                    mcu.setLocDetail(locDetail);
                                }
                            }
                            if (gridLocation != null
                                    && !"".equals(gridLocation)) {
                                mcu.setSysLocation(gridLocation);
                            }
                            mcuDao.add(mcu);
                        }
                    } else if (category.equals(Category.Remove.getName())) {

                        if (mcu != null && !mcu.getMcuStatus().getId()
                                .equals(deleteStatus.getId())) {
                            mcu.setLastModifiedDate(sdf.format(
                                    closedOn.toGregorianCalendar().getTime()));
                            mcu.setMcuStatus(deleteStatus);
                            mcuDao.update(mcu);
                        } else {
                            obj.setErrorString(
                                    ValidationError.ALREADY_REMOVED.getName());
                            obj.setNounType("");
                            obj.setObjectID(assessment.getObjectID());
                        }

                    } else if (category
                            .equals(Category.Deactivation.getName())) {

                        if (mcu != null && !mcu.getMcuStatus().getId()
                                .equals(deleteStatus.getId())) {
                            mcu.setLastModifiedDate(sdf.format(
                                    closedOn.toGregorianCalendar().getTime()));
                            mcu.setMcuStatus(deleteStatus);
                            mcuDao.update(mcu);
                        } else {
                            obj.setErrorString(
                                    ValidationError.ALREADY_DEACTIVATED
                                            .getName());
                            obj.setNounType("");
                            obj.setObjectID(assessment.getObjectID());
                        }

                    } else {
                        obj.setErrorString(
                                ValidationError.INVALID_PARAMETER.getName());
                        obj.setNounType("");
                        obj.setObjectID(assessment.getObjectID());
                    }
    
                } else {
                    obj.setErrorString(ValidationError.INVALID_PARAMETER.getName());
                    obj.setNounType("");
                    obj.setObjectID(location.getObjectID());
                }
            }catch(Exception e) {
                log.error(e,e);
                obj.setErrorString(ValidationError.SYSTEM_ERROR.getName());
                obj.setNounType("");
                obj.setEventTime(eventTime);
            }

            if (obj.getErrorString() != null
                    && !"".equals(obj.getErrorString())) {
                obj.setEventTime(eventTime);
                resp.getErrorObject().add(obj);
            }
        }

        oaInJmsTemplate.convertAndSend(resp,
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

        if (log.isDebugEnabled())
            log.debug(
                    "AssessmentLocationChangedNotificationService execute end..");

        return resp;
    }

}
