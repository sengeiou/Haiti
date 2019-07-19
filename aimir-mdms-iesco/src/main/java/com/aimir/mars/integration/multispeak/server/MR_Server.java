package com.aimir.mars.integration.multispeak.server;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.aimir.mars.integration.multispeak.service.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.feature.Features;
import org.multispeak.version_4.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.fep.util.DataUtil;
import com.aimir.mars.integration.multispeak.data.MRMessage;
import com.aimir.mars.integration.multispeak.data.MultiSpeakMessage;
import com.aimir.mars.integration.multispeak.queue.QueueHandler;
import com.aimir.mars.integration.multispeak.util.MultiSpeakConstants.ValidationError;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Code;
import com.aimir.model.system.Location;
import com.aimir.util.TimeUtil;

@WebService(serviceName = "MR_Server", targetNamespace = "http://www.multispeak.org/Version_4.1_Release")
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL, parameterStyle = ParameterStyle.WRAPPED)
@Service(value = "mR_Server")
@Features(features = "org.apache.cxf.feature.LoggingFeature")
@Transactional
public class MR_Server {

    protected static Log log = LogFactory.getLog(MR_Server.class);

    @Autowired
    private QueueHandler handler;

    @Autowired
    private MeterDao meterDao;

    @Autowired
    private DeviceModelDao deviceModelDao;

    @Autowired
    private LocationDao locationDao;

    @Autowired
    private CodeDao codeDao;

    /**
     * CB requests a new meter reading from MR, on meters selected by meterID.
     * MR returns information about failed transactions using an array of
     * errorObjects.
     * 
     * The meter reading is returned to the CB in the form of a meterReading, an
     * intervalData block, or a formattedBlock, sent to the URL specified in the
     * responseURL. The transactionID calling parameter links this Initiate
     * request with the published data method call. The expiration time
     * parameter indicates the amount of time for which the publisher should try
     * to obtain and publish the data;
     * 
     * if the publisher has been unsuccessful in publishing the data after the
     * expiration time, then the publisher will discard the request and the
     * requester should not expect a response.
     */
    @WebMethod(operationName = "InitiateMeterReadingsByMeterID", action = "http://www.multispeak.org/Version_4.1_Release/InitiateMeterReadingsByMeterID")
    public @WebResult(name = "InitiateMeterReadingsByMeterIDResult", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") ArrayOfErrorObject InitiateMeterReadingsByMeterID(
            @WebParam(name = "meterIDs", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") ArrayOfMeterIDNillable meterIDs,
            @WebParam(name = "responseURL", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") java.lang.String responseURL,
            @WebParam(name = "transactionID", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") java.lang.String transactionID,
            @WebParam(name = "expTime", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") ExpirationTime expTime)
            throws Exception {

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(Calendar.getInstance().getTime());
        XMLGregorianCalendar eventTime = DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(cal);
        eventTime.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
        eventTime.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);

        ArrayOfErrorObject resp = null;

        if (meterIDs == null || meterIDs.getMeterID().isEmpty()) {
            resp = new ArrayOfErrorObject();

            ErrorObject eo = new ErrorObject();
            eo.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                    + " [meterIDs] is null or empty.");
            eo.setNounType("");
            eo.setEventTime(eventTime);

            resp.getErrorObject().add(eo);
            return resp;
        }

        if (transactionID == null || "".equals(transactionID)) {
            resp = new ArrayOfErrorObject();

            ErrorObject eo = new ErrorObject();
            eo.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                    + " [transactionID] is null.");
            eo.setNounType("");
            eo.setEventTime(eventTime);

            resp.getErrorObject().add(eo);
            return resp;
        }

        if (expTime == null) {
            resp = new ArrayOfErrorObject();

            ErrorObject eo = new ErrorObject();
            eo.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                    + " [expTime] is null.");
            eo.setNounType("");
            eo.setEventTime(eventTime);

            resp.getErrorObject().add(eo);
            return resp;
        }

        try {
            resp = new ArrayOfErrorObject();

            List<MeterID> target = new ArrayList<MeterID>(
                    meterIDs.getMeterID());
            Code deleteStatus = codeDao.getCodeIdByCodeObject("1.3.3.9");
            for (MeterID cd : target) {
                if (cd.getMeterNo() != null
                        && !cd.getMeterNo().trim().equals("")) {
                    try {
                        Meter m = meterDao.get(cd.getMeterNo());

                        String geocode = cd.getUtility();
                        Location meterLocation = null;
                        if (geocode != null) {
                            meterLocation = locationDao.findByCondition("geocode", geocode);
                            if (meterLocation == null
                                    || (meterLocation != null && m != null
                                            && m.getLocationId()
                                                    .intValue() != meterLocation
                                                            .getId().intValue())) {
                                m = null;
                            }
                        }

//                        if (m != null && cd.getMeterNo().startsWith("testp")) {
                        if (cd.getMeterNo().startsWith("testp")) {
                            log.debug("Request of testp meter: [" + cd.getMeterNo() + "]");
                        } else {
                            if (m == null || (m != null && m.getMeterStatus() != null
                                    && m.getMeterStatus().getId()
                                            .equals(deleteStatus.getId()))) { // 미터가 존재하지 않는 경우
                                ErrorObject obj = new ErrorObject();
                                obj.setErrorString(
                                        ValidationError.UNREGISTERED_METER.getName());
                                obj.setEventTime(eventTime);
                                obj.setNounType("");
                                obj.setObjectID(cd.getObjectID());
                                resp.getErrorObject().add(obj);
                                meterIDs.getMeterID().remove(cd);
                            } else {
                                if (m.getModelId() == null) {
                                    ErrorObject obj = new ErrorObject();
                                    obj.setErrorString(
                                            ValidationError.COMMUNICATION_FAILURE
                                                    .getName());
                                    obj.setEventTime(eventTime);
                                    obj.setNounType("");
                                    obj.setObjectID(cd.getObjectID());
                                    resp.getErrorObject().add(obj);
                                    meterIDs.getMeterID().remove(cd);
                                }
                            }
                        }
                    }catch(Exception e) {
                        log.error(e, e);
                        ErrorObject obj = new ErrorObject();
                        obj.setErrorString(
                                ValidationError.SYSTEM_ERROR
                                        .getName());
                        obj.setEventTime(eventTime);
                        obj.setNounType("");
                        obj.setObjectID(cd.getObjectID());
                        resp.getErrorObject().add(obj);
                        meterIDs.getMeterID().remove(cd);
                    }
                } else {
                    ErrorObject obj = new ErrorObject();
                    obj.setErrorString(
                            ValidationError.INVALID_PARAMETER.getName()
                                    + " [MeterNo] is null.");
                    obj.setEventTime(eventTime);
                    obj.setNounType("");
                    obj.setObjectID(cd.getObjectID());
                    resp.getErrorObject().add(obj);
                    meterIDs.getMeterID().remove(cd);
                }
            }

            InitiateMeterReadingsByMeterID request = new InitiateMeterReadingsByMeterID();
            request.setMeterIDs(meterIDs);
            request.setResponseURL(responseURL);
            request.setTransactionID(transactionID);
            request.setExpTime(expTime);

            MultiSpeakMessage message = new MRMessage();
            // message.setMultiSpeakMsgHeader(multiSpeakMsgHeader.value);
            message.setObject(request);
            message.setRequestedTime(Calendar.getInstance());

            handler.putServiceData(QueueHandler.MR_MESSAGE, message);

        } catch (Exception e) {
            log.error(e, e);
            resp = new ArrayOfErrorObject();

            ErrorObject eo = new ErrorObject();
            eo.setErrorString(ValidationError.SYSTEM_ERROR.getName());
            eo.setNounType("");
            eo.setEventTime(eventTime);

            resp.getErrorObject().add(eo);
        }
        return resp;
    }

    /**
     * Returns the most recent meter reading data for a given MeterID.
     */
    /*
    @WebMethod(operationName = "GetLatestReadingByMeterID", action = "http://www.multispeak.org/Version_4.1_Release/GetLatestReadingByMeterID")
    public @WebResult(name = "GetLatestReadingByMeterIDResult", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") ArrayOfErrorObject GetLatestReadingByMeterID(
            @WebParam(name = "meterID", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") MeterID meterID,
            @WebParam(name = "transactionID", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") java.lang.String transactionID)
            throws Exception {

        ArrayOfErrorObject resp = null;
        if (meterID == null || meterID.getMeterNo() == null
                || meterID.getMeterNo().trim().equals("")) {
            throw new Exception("Parameter [meterID] is null");
        }
        if (transactionID == null) {
            throw new Exception("Parameter [transactionID] is null");
        }
        resp = new ArrayOfErrorObject();
        try {
            // GetLatestReadingByMeterIDService service =
            // DataUtil.getBean(GetLatestReadingByMeterIDService.class);
            // resp = service.execute(meterID);

            GetLatestReadingByMeterID request = new GetLatestReadingByMeterID();
            request.setMeterID(meterID);
            request.setTransactionID(transactionID);

            MultiSpeakMessage message = new MRMessage();
            // message.setMultiSpeakMsgHeader(multiSpeakMsgHeader.value);
            message.setObject(request);
            message.setRequestedTime(Calendar.getInstance());
            // send Queue;
            handler.putServiceData(QueueHandler.MR_MESSAGE, message);
        } catch (Exception e) {
                       log.error(e, e);
            resp = new ArrayOfErrorObject();

            ErrorObject eo = new ErrorObject();
            eo.setErrorString(ValidationError.SYSTEM_ERROR.getName());
            eo.setNounType("");
            eo.setEventTime(eventTime);

            resp.getErrorObject().add(eo);
        }

        return resp;
    }
    */

    /**
     * Returns meter reading data for a given MeterID and date range.
     */
    @WebMethod(operationName = "GetReadingsByMeterID", action = "http://www.multispeak.org/Version_4.1_Release/GetReadingsByMeterID")
    public @WebResult(name = "GetReadingsByMeterIDResult", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") ArrayOfErrorObject GetReadingsByMeterID(
            @WebParam(name = "meterID", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") MeterID meterID,
            @WebParam(name = "startDate") XMLGregorianCalendar startDate,
            @WebParam(name = "endDate") XMLGregorianCalendar endDate,
            @WebParam(name = "transactionID", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") java.lang.String transactionID)
            throws Exception {

        GregorianCalendar c = new GregorianCalendar();
        c.setTime(Calendar.getInstance().getTime());
        XMLGregorianCalendar eventTime = DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(c);
        eventTime.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
        eventTime.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);

        ArrayOfErrorObject resp = null;
        
        if (meterID == null || meterID.getMeterNo() == null
                || meterID.getMeterNo().trim().equals("")) {
            resp = new ArrayOfErrorObject();

            ErrorObject eo = new ErrorObject();
            eo.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                    + " [meterID] is null.");
            eo.setNounType("");
            eo.setEventTime(eventTime);

            resp.getErrorObject().add(eo);
            return resp;
        }
        if (startDate == null) {
            resp = new ArrayOfErrorObject();

            ErrorObject eo = new ErrorObject();
            eo.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                    + " [startDate] is null.");
            eo.setNounType("");
            eo.setEventTime(eventTime);

            resp.getErrorObject().add(eo);
            return resp;
        }
        if (endDate == null) {
            resp = new ArrayOfErrorObject();

            ErrorObject eo = new ErrorObject();
            eo.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                    + " [endDate] is null.");
            eo.setNounType("");
            eo.setEventTime(eventTime);

            resp.getErrorObject().add(eo);
            return resp;
        }
        if (transactionID == null) {
            resp = new ArrayOfErrorObject();

            ErrorObject eo = new ErrorObject();
            eo.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                    + " [transactionID] is null.");
            eo.setNounType("");
            eo.setEventTime(eventTime);

            resp.getErrorObject().add(eo);
            return resp;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String startDateStr = sdf
                .format(startDate.toGregorianCalendar().getTime());
        String endDateStr = sdf.format(endDate.toGregorianCalendar().getTime());

        if (Long.parseLong(startDateStr) > Long
                .parseLong(TimeUtil.getCurrentDay())) {
            resp = new ArrayOfErrorObject();

            ErrorObject eo = new ErrorObject();
            eo.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                    + " [startDate] is invalid.");
            eo.setNounType("");
            eo.setEventTime(eventTime);

            resp.getErrorObject().add(eo);
            return resp;
        }
        if (Long.parseLong(endDateStr) > Long
                .parseLong(TimeUtil.getCurrentDay())) {
            resp = new ArrayOfErrorObject();

            ErrorObject eo = new ErrorObject();
            eo.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                    + " [endDate] is invalid.");
            eo.setNounType("");
            eo.setEventTime(eventTime);

            resp.getErrorObject().add(eo);
            return resp;
        }
        if (Long.parseLong(endDateStr) < Long.parseLong(startDateStr)) {
            resp = new ArrayOfErrorObject();

            ErrorObject eo = new ErrorObject();
            eo.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                    + " [startDate, endDate] is invalid.");
            eo.setNounType("");
            eo.setEventTime(eventTime);

            resp.getErrorObject().add(eo);
            return resp;
        }

        try {
            resp = new ArrayOfErrorObject();

            Code deleteStatus = codeDao.getCodeIdByCodeObject("1.3.3.9");

            if (meterID.getMeterNo() != null
                    && !meterID.getMeterNo().trim().equals("")) {
                try {
                    Meter m = meterDao.get(meterID.getMeterNo());
                    String geocode = meterID.getUtility();
                    Location meterLocation = null;
                    if (geocode != null) {
                        meterLocation = locationDao.findByCondition("geocode", geocode);
                        if (meterLocation == null
                                || (meterLocation != null && m != null
                                        && m.getLocationId()
                                                .intValue() != meterLocation
                                                        .getId().intValue())) {
                            m = null;
                        }
                    }
                    if (m != null && meterID.getMeterNo().startsWith("testp")) {

                    } else {
                        if (m == null || (m != null && m.getMeterStatus() != null
                                && m.getMeterStatus().getId()
                                        .equals(deleteStatus.getId()))) { // 미터가 존재하지 않는 경우
                            ErrorObject obj = new ErrorObject();
                            obj.setErrorString(
                                    ValidationError.UNREGISTERED_METER.getName());
                            obj.setEventTime(eventTime);
                            obj.setNounType("");
                            obj.setObjectID(meterID.getObjectID());
                            resp.getErrorObject().add(obj);
                        } else {
                            if (m.getModelId() == null) {
                                ErrorObject obj = new ErrorObject();
                                obj.setErrorString(
                                        ValidationError.COMMUNICATION_FAILURE
                                                .getName());
                                obj.setEventTime(eventTime);
                                obj.setNounType("");
                                obj.setObjectID(meterID.getObjectID());
                                resp.getErrorObject().add(obj);
                            }
                        }
                    }
                }catch(Exception e) {
                    log.error(e,e);
                    ErrorObject obj = new ErrorObject();
                    obj.setErrorString(
                            ValidationError.SYSTEM_ERROR
                                    .getName());
                    obj.setEventTime(eventTime);
                    obj.setNounType("");
                    obj.setObjectID(meterID.getObjectID());
                    resp.getErrorObject().add(obj);
                }
            } else {
                ErrorObject obj = new ErrorObject();
                obj.setErrorString(
                        ValidationError.INVALID_PARAMETER.getName()
                                + " [MeterNo] is null.");
                obj.setEventTime(eventTime);
                obj.setNounType("");
                obj.setObjectID(meterID.getObjectID());
                resp.getErrorObject().add(obj);
            }

            
            GetReadingsByMeterID request = new GetReadingsByMeterID();
            request.setMeterID(meterID);
            request.setStartDate(startDate);
            request.setEndDate(endDate);
            request.setTransactionID(transactionID);

            MultiSpeakMessage message = new MRMessage();
            // message.setMultiSpeakMsgHeader(multiSpeakMsgHeader.value);
            message.setObject(request);
            message.setRequestedTime(Calendar.getInstance());
            // send Queue;
            handler.putServiceData(QueueHandler.MR_MESSAGE, message);
        } catch (Exception e) {
            log.error(e, e);
            resp = new ArrayOfErrorObject();

            ErrorObject eo = new ErrorObject();
            eo.setErrorString(ValidationError.SYSTEM_ERROR.getName());
            eo.setNounType("");
            eo.setEventTime(eventTime);

            resp.getErrorObject().add(eo);
        }

        return resp;
    }

    /**
     * Publisher notifies MR to Add the associated meter(s). MR returns
     * information about failed transactions using an array of errorObjects.
     * 
     * The message header attribute 'registrationID' should be added to all
     * publish messages to indicate to the subscriber under which registrationID
     * they received this notification data.
     */
    @WebMethod(operationName = "MeterAddNotification", action = "http://www.multispeak.org/Version_4.1_Release/MeterAddNotification")
    public @WebResult(name = "MeterAddNotificationResult", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") ArrayOfErrorObject MeterAddNotification(
            @WebParam(name = "addedMeters", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") Meters addedMeters)
            throws Exception {

        ArrayOfErrorObject resp = null;

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(Calendar.getInstance().getTime());
        XMLGregorianCalendar eventTime = DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(cal);
        eventTime.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
        eventTime.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);

        if (addedMeters == null || (addedMeters.getElectricMeters() == null
                && addedMeters.getWaterMeters() == null
                && addedMeters.getGasMeters() == null
                && addedMeters.getPropaneMeters() == null)) {
            resp = new ArrayOfErrorObject();
            ErrorObject eo = new ErrorObject();
            eo.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                    + " [addedMeters] is null or empty.");
            eo.setNounType("");
            eo.setEventTime(eventTime);
            resp.getErrorObject().add(eo);
            return resp;
        }

        int errExistMeter = 0;
        if (addedMeters.getElectricMeters() != null && addedMeters
                .getElectricMeters().getElectricMeter().isEmpty()) {
            errExistMeter = 1;
        } else if (addedMeters.getGasMeters() != null
                && addedMeters.getGasMeters().getGasMeter().isEmpty()) {
            errExistMeter = 2;
        } else if (addedMeters.getWaterMeters() != null
                && addedMeters.getWaterMeters().getWaterMeter().isEmpty()) {
            errExistMeter = 3;
        } else if (addedMeters.getPropaneMeters() != null
                && addedMeters.getPropaneMeters().getPropaneMeter().isEmpty()) {
            errExistMeter = 4;
        }

        if (errExistMeter != 0 && errExistMeter < 5) {
            resp = new ArrayOfErrorObject();
            ErrorObject eo = new ErrorObject();
            eo.setNounType("");
            eo.setEventTime(eventTime);
            switch (errExistMeter) {
            case 1:
                eo.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                        + " [electricMeters] is null.");
                break;
            case 2:
                eo.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                        + " [gasMeters] is null.");
                break;
            case 3:
                eo.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                        + " [waterMeters] is null.");
                break;
            case 4:
                eo.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                        + " [propaneMeters] is null.");
                break;
            }

            resp.getErrorObject().add(eo);
            return resp;
        }

        try {
            resp = new ArrayOfErrorObject();

            MeterAddNotificationService service = DataUtil
                    .getBean(MeterAddNotificationService.class);
            resp = service.execute(addedMeters);

        } catch (Exception e) {
            log.error(e, e);
            resp = new ArrayOfErrorObject();

            ErrorObject eo = new ErrorObject();
            eo.setErrorString(ValidationError.SYSTEM_ERROR.getName());
            eo.setNounType("");
            eo.setEventTime(eventTime);

            resp.getErrorObject().add(eo);
        }
        return resp;
    }

    /**
     * Publisher notifies MR to remove the associated meter(s). MR returns
     * information about failed transactions using an array of errorObjects. The
     * message header attribute 'registrationID' should be added to all publish
     * messages to indicate to the subscriber under which registrationID they
     * received this notification data.
     */
    @WebMethod(operationName = "MeterRemoveNotification", action = "http://www.multispeak.org/Version_4.1_Release/MeterRemoveNotification")
    public @WebResult(name = "MeterRemoveNotificationResult", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") ArrayOfErrorObject MeterRemoveNotification(
            @WebParam(name = "removedMeters", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") Meters removedMeters)
            throws Exception {

        ArrayOfErrorObject resp = null;

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(Calendar.getInstance().getTime());
        XMLGregorianCalendar eventTime = DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(cal);
        eventTime.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
        eventTime.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);

        if (removedMeters == null || (removedMeters.getElectricMeters() == null
                && removedMeters.getWaterMeters() == null
                && removedMeters.getGasMeters() == null
                && removedMeters.getPropaneMeters() == null)) {
            resp = new ArrayOfErrorObject();
            ErrorObject eo = new ErrorObject();
            eo.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                    + " [removedMeters] is null or empty.");
            eo.setNounType("");
            eo.setEventTime(eventTime);
            resp.getErrorObject().add(eo);
            return resp;
        }


        int errExistMeter = 0;
        if (removedMeters.getElectricMeters() != null && removedMeters
                .getElectricMeters().getElectricMeter().isEmpty()) {
            errExistMeter = 1;
        } else if (removedMeters.getGasMeters() != null
                && removedMeters.getGasMeters().getGasMeter().isEmpty()) {
            errExistMeter = 2;
        } else if (removedMeters.getWaterMeters() != null
                && removedMeters.getWaterMeters().getWaterMeter().isEmpty()) {
            errExistMeter = 3;
        } else if (removedMeters.getPropaneMeters() != null
                && removedMeters.getPropaneMeters().getPropaneMeter().isEmpty()) {
            errExistMeter = 4;
        }

        if (errExistMeter != 0 && errExistMeter < 5) {
            resp = new ArrayOfErrorObject();
            ErrorObject eo = new ErrorObject();
            eo.setNounType("");
            eo.setEventTime(eventTime);
            switch (errExistMeter) {
            case 1:
                eo.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                        + " [electricMeters] is null.");
                break;
            case 2:
                eo.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                        + " [gasMeters] is null.");
                break;
            case 3:
                eo.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                        + " [waterMeters] is null.");
                break;
            case 4:
                eo.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                        + " [propaneMeters] is null.");
                break;
            }

            resp.getErrorObject().add(eo);
            return resp;
        }

        try {

            MeterRemoveNotificationService service = DataUtil
                    .getBean(MeterRemoveNotificationService.class);
            resp = service.execute(removedMeters);

        } catch (Exception e) {
            log.error(e, e);
            resp = new ArrayOfErrorObject();

            ErrorObject eo = new ErrorObject();
            eo.setErrorString(ValidationError.SYSTEM_ERROR.getName());
            eo.setNounType("");
            eo.setEventTime(eventTime);

            resp.getErrorObject().add(eo);
        }
        return resp;
    }

    /**
     * Publisher notifies MR to retire the associated meter(s). MR returns
     * information about failed transactions using an array of errorObjects. The
     * message header attribute 'registrationID' should be added to all publish
     * messages to indicate to the subscriber under which registrationID they
     * received this notification data.
     */
    @WebMethod(operationName = "MeterRetireNotification", action = "http://www.multispeak.org/Version_4.1_Release/MeterRetireNotification")
    public @WebResult(name = "MeterRetireNotificationResult", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") ArrayOfErrorObject MeterRetireNotification(
            @WebParam(name = "retiredMeters", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") Meters retiredMeters,
            @WebParam(name = "transactionID", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") java.lang.String transactionID,
            @WebParam(mode = WebParam.Mode.INOUT, name = "MultiSpeakMsgHeader", targetNamespace = "http://www.multispeak.org/Version_4.1_Release", header = true) javax.xml.ws.Holder<MultiSpeakMsgHeader> multiSpeakMsgHeader)
            throws Exception {

        ArrayOfErrorObject resp = null;

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(Calendar.getInstance().getTime());
        XMLGregorianCalendar eventTime = DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(cal);
        eventTime.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
        eventTime.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);

        if (retiredMeters == null || (retiredMeters.getElectricMeters() == null
                && retiredMeters.getWaterMeters() == null
                && retiredMeters.getGasMeters() == null
                && retiredMeters.getPropaneMeters() == null)) {
            resp = new ArrayOfErrorObject();
            ErrorObject eo = new ErrorObject();
            eo.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                    + " [retiredMeters] is null or empty.");
            eo.setNounType("");
            eo.setEventTime(eventTime);
            resp.getErrorObject().add(eo);
            return resp;
        }


        int errExistMeter = 0;
        if (retiredMeters.getElectricMeters() != null && retiredMeters
                .getElectricMeters().getElectricMeter().isEmpty()) {
            errExistMeter = 1;
        } else if (retiredMeters.getGasMeters() != null
                && retiredMeters.getGasMeters().getGasMeter().isEmpty()) {
            errExistMeter = 2;
        } else if (retiredMeters.getWaterMeters() != null
                && retiredMeters.getWaterMeters().getWaterMeter().isEmpty()) {
            errExistMeter = 3;
        } else if (retiredMeters.getPropaneMeters() != null
                && retiredMeters.getPropaneMeters().getPropaneMeter().isEmpty()) {
            errExistMeter = 4;
        }

        if (errExistMeter != 0 && errExistMeter < 5) {
            resp = new ArrayOfErrorObject();
            ErrorObject eo = new ErrorObject();
            eo.setNounType("");
            eo.setEventTime(eventTime);
            switch (errExistMeter) {
            case 1:
                eo.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                        + " [electricMeters] is null.");
                break;
            case 2:
                eo.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                        + " [gasMeters] is null.");
                break;
            case 3:
                eo.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                        + " [waterMeters] is null.");
                break;
            case 4:
                eo.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                        + " [propaneMeters] is null.");
                break;
            }

            resp.getErrorObject().add(eo);
            return resp;
        }

        try {

            MeterRetireNotificationService service = DataUtil
                    .getBean(MeterRetireNotificationService.class);
            resp = service.execute(retiredMeters);

        } catch (Exception e) {
            log.error(e, e);
            resp = new ArrayOfErrorObject();

            ErrorObject eo = new ErrorObject();
            eo.setErrorString(ValidationError.SYSTEM_ERROR.getName());
            eo.setNounType("");
            eo.setEventTime(eventTime);

            resp.getErrorObject().add(eo);

        }
        return resp;
    }

    @WebMethod(operationName = "MeterChangedNotification", action = "http://www.multispeak.org/Version_4.1_Release/MeterChangedNotification")
    public @WebResult(name = "MeterChangedNotificationResult", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") ArrayOfErrorObject MeterChangedNotification(
            @WebParam(name = "changedMeters", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") Meters changedMeters)
            throws Exception {

        return new ArrayOfErrorObject();
    }

    @WebMethod(operationName = "GetAMRSupportedMeters", action = "http://www.multispeak.org/Version_4.1_Release/GetAMRSupportedMeters")
    public @WebResult(name = "GetAMRSupportedMetersResponse", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") GetAMRSupportedMetersResponse GetAMRSupportedMeters(
            @WebParam(name = "lastReceived", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") java.lang.String lastReceived)
            throws Exception {

        log.debug("GetAMRSupportedMeters at MR_Server");
        //TODO errorObject or errorString 처리
        GetAMRSupportedMeters request = new GetAMRSupportedMeters();
        request.setLastReceived(lastReceived);

        MultiSpeakMessage message = new MRMessage();
        message.setObject(request);
        message.setRequestedTime(Calendar.getInstance());

        //queue (Async)
        //handler.putServiceData(QueueHandler.MR_MESSAGE, message);

        //바로 리턴하는 경우
        GetAMRSupportedMetersService service = DataUtil.getBean(GetAMRSupportedMetersService.class);
        GetAMRSupportedMetersResponse metersResponse = service.execute(message);

        return metersResponse;
    }

    @WebMethod(operationName = "GetAMRSupportedMetersResponse1", action = "http://www.multispeak.org/Version_4.1_Release/GetAMRSupportedMetersResponse")
    public @WebResult(name = "GetAMRSupportedMetersResponseResult", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") ArrayOfErrorObject GetAMRSupportedMetersResponse(
            @WebParam(name = "GetAMRSupportedMetersResult", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") Meters getAMRSupportedMetersResult)
            throws Exception {
        log.debug("GetAMRSupportedMetersResponse1 at MR_Server");

        ArrayOfErrorObject resp = null;

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(Calendar.getInstance().getTime());
        XMLGregorianCalendar eventTime = DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(cal);
        eventTime.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
        eventTime.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);

        if(getAMRSupportedMetersResult == null){
            //하위 미터 데이터는 없을수도 있음.
            resp = new ArrayOfErrorObject();
            ErrorObject eo = new ErrorObject();
            eo.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                    + ": [MetersResult] is null.");
            eo.setNounType("");
            eo.setEventTime(eventTime);
            resp.getErrorObject().add(eo);
            return resp;
        }

        try{
            //Async로 하는 경우.
            GetAMRSupportedMetersResponse request = new GetAMRSupportedMetersResponse();
            request.setGetAMRSupportedMetersResult(getAMRSupportedMetersResult);

            MultiSpeakMessage message = new MRMessage();
            message.setObject(request);
            message.setRequestedTime(Calendar.getInstance());
            //handler.putServiceData(QueueHandler.MR_MESSAGE, message);

            //Synchronous 처리인 경우.
            GetAMRSupportedMetersResponse1Service service = DataUtil
                    .getBean(GetAMRSupportedMetersResponse1Service.class);
            resp = service.execute(getAMRSupportedMetersResult);
        } catch (Exception e) {
            log.error(e,e);
            resp = new ArrayOfErrorObject();

            ErrorObject eo = new ErrorObject();
            eo.setErrorString(ValidationError.SYSTEM_ERROR.getName());
            eo.setNounType("");
            eo.setEventTime(eventTime);

            resp.getErrorObject().add(eo);
        }

        return resp;
    }

}
