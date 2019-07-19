package com.aimir.mars.integration.multispeak.server;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;
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
import javax.xml.ws.WebServiceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.feature.Features;
import org.multispeak.version_4.ArrayOfCDState;
import org.multispeak.version_4.ArrayOfConnectDisconnectEvent;
import org.multispeak.version_4.ArrayOfErrorObject;
import org.multispeak.version_4.CDState;
import org.multispeak.version_4.ConnectDisconnectEvent;
import org.multispeak.version_4.ErrorObject;
import org.multispeak.version_4.ExpirationTime;
import org.multispeak.version_4.InitiateCDStateRequest;
import org.multispeak.version_4.InitiateConnectDisconnect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.dao.device.EnergyMeterDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.mars.integration.multispeak.data.CDMessage;
import com.aimir.mars.integration.multispeak.data.MultiSpeakMessage;
import com.aimir.mars.integration.multispeak.queue.QueueHandler;
import com.aimir.mars.integration.multispeak.util.MultiSpeakConstants.ValidationError;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Code;
import com.aimir.model.system.DeviceModel;
import com.aimir.service.device.MeterManager;

@WebService(serviceName = "CD_Server", targetNamespace="http://www.multispeak.org/Version_4.1_Release")
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL, parameterStyle = ParameterStyle.WRAPPED)
@Service(value = "cD_Server")
@Features(features = "org.apache.cxf.feature.LoggingFeature")
@Transactional
public class CD_Server {

    protected static Log log = LogFactory.getLog(CD_Server.class);

    @Resource
    private WebServiceContext wsContext;

    @Autowired
    private QueueHandler handler;

    @Autowired
    private MeterManager meterDao;
    
    @Autowired
    private DeviceModelDao deviceModelDao;

    @Autowired
    private CodeDao codeDao;

    @WebMethod(operationName = "InitiateConnectDisconnect", action = "http://www.multispeak.org/Version_4.1_Release/InitiateConnectDisconnect")
    public @WebResult(name = "InitiateConnectDisconnectResult", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") ArrayOfErrorObject InitiateConnectDisconnect(
            @WebParam(name = "cdEvents", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") ArrayOfConnectDisconnectEvent cdEvents,
            @WebParam(name = "transactionID", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") java.lang.String transactionID,
            @WebParam(name = "expTime", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") ExpirationTime expTime)
            throws Exception {

        ArrayOfErrorObject resp = null;

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(Calendar.getInstance().getTime());
        XMLGregorianCalendar eventTime = DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(cal);
        eventTime.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
        eventTime.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);

        if (cdEvents == null || cdEvents.getConnectDisconnectEvent().isEmpty()) {
            resp = new ArrayOfErrorObject();
            ErrorObject eo = new ErrorObject();
            eo.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                    + " [cdEvents] is null.");
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
            
            List<ConnectDisconnectEvent> target = new ArrayList<ConnectDisconnectEvent>(
                    cdEvents.getConnectDisconnectEvent());
            Code deleteStatus = codeDao.getCodeIdByCodeObject("1.3.3.9");
            Code electricityMeterType = codeDao.getCodeIdByCodeObject("1.3.1.1");
            for (ConnectDisconnectEvent cd : target) {
                if (cd.getMeterID() != null
                        && cd.getMeterID().getMeterNo() != null) {
                    Meter m = meterDao.getMeter(cd.getMeterID().getMeterNo());
                    if(m == null || m.getMeterStatus().getId().equals(deleteStatus.getId())){ //미터가 존재하지 않는 경우 
                        if(!cd.getMeterID().getMeterNo().startsWith("testp")) {
                            ErrorObject obj = new ErrorObject();
                            obj.setErrorString(ValidationError.UNREGISTERED_METER.getName());
                            obj.setEventTime(eventTime);
                            obj.setNounType("");
                            obj.setObjectID(cd.getObjectID());
                            resp.getErrorObject().add(obj);
                            cdEvents.getConnectDisconnectEvent().remove(cd);
                        }
                    } else {
                        if (m != null && cd.getMeterID().getMeterNo().startsWith("testp")) {
                        } else {
                            if(m.getMeterTypeCodeId().equals(electricityMeterType.getId())) {
                                if(m.getModelId() != null && m.getModelId() > 0){ 
                                    DeviceModel model = deviceModelDao.get(m.getModelId());
                                    if(model != null && model.getName().indexOf("MA304T") >= 0){
                                        //ct ct/vt meter is not support relay function
                                        ErrorObject obj = new ErrorObject();
                                        obj.setErrorString(ValidationError.NOT_SUPPORT.getName());
                                        obj.setEventTime(eventTime);
                                        obj.setNounType("");
                                        obj.setObjectID(cd.getObjectID());
                                        resp.getErrorObject().add(obj);
                                        cdEvents.getConnectDisconnectEvent().remove(cd);
                                    }
                                }else{
                                    //meter model is null
                                    ErrorObject obj = new ErrorObject();
                                    obj.setErrorString(ValidationError.COMMUNICATION_FAILURE.getName());
                                    obj.setEventTime(eventTime);
                                    obj.setNounType("");
                                    obj.setObjectID(cd.getObjectID());
                                    resp.getErrorObject().add(obj);
                                    cdEvents.getConnectDisconnectEvent().remove(cd);
                                }
                            } else {
                                // only energy meteer is upport relay function
                                ErrorObject obj = new ErrorObject();
                                obj.setErrorString(ValidationError.NOT_SUPPORT.getName());
                                obj.setEventTime(eventTime);
                                obj.setNounType("");
                                obj.setObjectID(cd.getObjectID());
                                resp.getErrorObject().add(obj);
                                cdEvents.getConnectDisconnectEvent().remove(cd);
                            }
                            /*
                             if(m.getLastReadDate() == null || "".equals(m.getLastReadDate())){ //meter last read date is null
                                ErrorObject obj = new ErrorObject();
                                obj.setErrorString("Meter cannot initiate command.(meter never communicated with comm device)");
                                obj.setEventTime(eventTime);
                                obj.setNounType("");
                                obj.setObjectID(cd.getObjectID());
                                resp.getErrorObject().add(obj);
                                cdEvents.getConnectDisconnectEvent().remove(cd);
                            }
                             */
                        }
                    }
                } else {
                    ErrorObject obj = new ErrorObject();
                    obj.setErrorString(ValidationError.INVALID_PARAMETER.getName());
                    obj.setEventTime(eventTime);
                    obj.setNounType("");
                    obj.setObjectID(cd.getObjectID());
                    resp.getErrorObject().add(obj);
                    cdEvents.getConnectDisconnectEvent().remove(cd);
                }
            }

            InitiateConnectDisconnect request = new InitiateConnectDisconnect();
            request.setCdEvents(cdEvents);
            request.setExpTime(expTime);
            request.setTransactionID(transactionID);

            MultiSpeakMessage message = new CDMessage();
            message.setObject(request);
            //message.setMultiSpeakMsgHeader(multiSpeakMsgHeader.value);
            message.setRequestedTime(Calendar.getInstance());
            handler.putServiceData(QueueHandler.CD_MESSAGE, message);

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

    @WebMethod(operationName = "InitiateCDStateRequest", action = "http://www.multispeak.org/Version_4.1_Release/InitiateCDStateRequest")
    public @WebResult(name = "InitiateCDStateRequestResult", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") ArrayOfErrorObject InitiateCDStateRequest(
            @WebParam(name = "states", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") ArrayOfCDState states,
            @WebParam(name = "transactionID", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") java.lang.String transactionID,
            @WebParam(name = "expTime", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") ExpirationTime expTime)
            throws Exception {

        ArrayOfErrorObject resp = null;

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(Calendar.getInstance().getTime());
        XMLGregorianCalendar eventTime = DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(cal);
        eventTime.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
        eventTime.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);

        if (states == null || states.getCDState().isEmpty()) {
            resp = new ArrayOfErrorObject();
            ErrorObject eo = new ErrorObject();
            eo.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                    + " [states] is null.");
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

            List<CDState> target = new ArrayList<CDState>(
                    states.getCDState());
            Code deleteStatus = codeDao.getCodeIdByCodeObject("1.3.3.9");
            Code electricityMeterType = codeDao.getCodeIdByCodeObject("1.3.1.1");
            for (CDState cd : target) {
                if (cd.getMeterID() != null
                        && cd.getMeterID().getMeterNo() != null) {
                    Meter m = meterDao.getMeter(cd.getMeterID().getMeterNo());
                    if(m == null || m.getMeterStatus().getId().equals(deleteStatus.getId())){ //미터가 존재하지 않는 경우 
                        if(!cd.getMeterID().getMeterNo().startsWith("testp")) {
                            ErrorObject obj = new ErrorObject();
                            obj.setErrorString(ValidationError.UNREGISTERED_METER.getName());
                            obj.setEventTime(eventTime);
                            obj.setNounType("");
                            obj.setObjectID(cd.getObjectID());
                            resp.getErrorObject().add(obj);
                            states.getCDState().remove(cd);
                        }
                    } else {
                        if(m.getMeterTypeCodeId().equals(electricityMeterType.getId())) {
                            if (m != null && cd.getMeterID().getMeterNo().startsWith("testp")) {
                            } else {
                                if(m.getModelId() != null && m.getModelId() > 0){ 
                                    DeviceModel model = deviceModelDao.get(m.getModelId());
                                    if(model != null && model.getName().indexOf("MA304T") >= 0){
                                        //ct ct/vt meter is not support relay function
                                        ErrorObject obj = new ErrorObject();
                                        obj.setErrorString(ValidationError.NOT_SUPPORT.getName());
                                        obj.setEventTime(eventTime);
                                        obj.setNounType("");
                                        obj.setObjectID(cd.getObjectID());
                                        resp.getErrorObject().add(obj);
                                        states.getCDState().remove(cd);
                                    }
                                }else{
                                    //meter model is null
                                    ErrorObject obj = new ErrorObject();
                                    obj.setErrorString(ValidationError.COMMUNICATION_FAILURE.getName());
                                    obj.setEventTime(eventTime);
                                    obj.setNounType("");
                                    obj.setObjectID(cd.getObjectID());
                                    resp.getErrorObject().add(obj);
                                    states.getCDState().remove(cd);
                                }
                            }
                        } else {
                            // only energy meteer is upport relay function
                            ErrorObject obj = new ErrorObject();
                            obj.setErrorString(ValidationError.NOT_SUPPORT.getName());
                            obj.setEventTime(eventTime);
                            obj.setNounType("");
                            obj.setObjectID(cd.getObjectID());
                            resp.getErrorObject().add(obj);
                            states.getCDState().remove(cd);
                        }
                        /*
                         if(m.getLastReadDate() == null || "".equals(m.getLastReadDate())){ //meter last read date is null
                            ErrorObject obj = new ErrorObject();
                            obj.setErrorString("Meter cannot initiate command.(meter never communicated with comm device)");
                            obj.setEventTime(eventTime);
                            obj.setNounType("");
                            obj.setObjectID(cd.getObjectID());
                            resp.getErrorObject().add(obj);
                            states.getCDState().remove(cd);
                        }
                         */
                    }
                } else {
                    ErrorObject obj = new ErrorObject();
                    obj.setErrorString(ValidationError.INVALID_PARAMETER.getName());
                    obj.setEventTime(eventTime);
                    obj.setNounType("");
                    obj.setObjectID(cd.getObjectID());
                    resp.getErrorObject().add(obj);
                    states.getCDState().remove(cd);
                }
            }


            InitiateCDStateRequest request = new InitiateCDStateRequest();
            request.setStates(states);
            request.setExpTime(expTime);
            request.setTransactionID(transactionID);

            MultiSpeakMessage message = new CDMessage();
            message.setObject(request);
            //message.setMultiSpeakMsgHeader(multiSpeakMsgHeader.value);
            message.setRequestedTime(Calendar.getInstance());
            handler.putServiceData(QueueHandler.CD_MESSAGE, message);

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

}
