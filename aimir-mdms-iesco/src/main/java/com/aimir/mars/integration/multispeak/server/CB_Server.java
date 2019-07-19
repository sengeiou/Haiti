package com.aimir.mars.integration.multispeak.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.aimir.mars.integration.multispeak.util.ServiceNameConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.feature.Features;
import org.multispeak.version_4.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.device.MeterDao;
import com.aimir.mars.integration.multispeak.data.CBMessage;
import com.aimir.mars.integration.multispeak.data.MultiSpeakMessage;
import com.aimir.mars.integration.multispeak.queue.QueueHandler;
import com.aimir.mars.integration.multispeak.util.MultiSpeakConstants.ValidationError;
import com.aimir.mars.util.MarsProperty;
import com.aimir.model.device.Meter;
import com.aimir.service.device.MeterManager;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.DeviceModelManager;
import com.aimir.service.system.DeviceVendorManager;
import com.aimir.service.system.ObisCodeManager;
import com.aimir.util.DateTimeUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@WebService(serviceName = "CB_Server", targetNamespace="http://www.multispeak.org/Version_4.1_Release")
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL, parameterStyle = ParameterStyle.WRAPPED)
@Service(value = "cB_Server")
@Features(features = "org.apache.cxf.feature.LoggingFeature")
@Transactional
public class CB_Server {

    protected static Log log = LogFactory.getLog(CB_Server.class);

    @Autowired
    private QueueHandler handler;

    @Autowired
    private MeterManager meterManager;
    @Autowired
    private ObisCodeManager obisCodeManager;
    @Autowired
    private DeviceModelManager deviceModelManager;

    /**
     * CD notifies CB of state change(s) for connect/disconnect device(s). The
     * transactionID calling parameter can be used to link this action with an
     * InitiateConectDisconnect call. If this transaction fails, CB returns
     * information about the failure in an array of errorObject(s).
     * 
     * The message header attribute 'registrationID' should be added to all
     * publish messages to indicate to the subscriber under which registrationID
     * they received this notification data.
     * 
     * CD, MR 등 서비스에 대한 콜백 메세지 내부 테스트 용도로 사용한다.
     */
    @WebMethod(operationName = "CDStatesChangedNotification", action = "CDStatesChangedNotification")
    public @WebResult(name = "CDStatesChangedNotificationResult", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") ArrayOfErrorObject CDStatesChangedNotification(
            @WebParam(name = "stateChanges", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") ArrayOfCDStateChange stateChanges,
            @WebParam(name = "transactionID", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") java.lang.String transactionID)
            throws Exception {

         return new ArrayOfErrorObject();
    }

    /**
     * MR Notifies CB of a change in meter readings by sending the changed
     * meterReading objects.
     * 
     * CB returns information about failed transactions in an array of
     * errorObjects. The transactionID calling parameter links this Initiate
     * request with the published data method call.
     * 
     * The message header attribute 'registrationID' should be added to all
     * publish messages to indicate to the subscriber under which registrationID
     * they received this notification data.
     * 
     * CD, MR 등 서비스에 대한 콜백 메세지 내부 테스트 용도로 사용한다.
     */
    @WebMethod(operationName = "ReadingChangedNotification", action = "ReadingChangedNotification")
    public @WebResult(name = "ReadingChangedNotificationResult", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") ArrayOfErrorObject ReadingChangedNotification(
            @WebParam(name = "changedMeterReads", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") ArrayOfMeterReading1 changedMeterReads,
            @WebParam(name = "transactionID", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") java.lang.String transactionID)
            throws Exception {

        if(Boolean.parseBoolean(MarsProperty.getProperty("HES.TEST", "false"))) {
            if (Boolean.parseBoolean(
                    MarsProperty.getProperty("HES.TEST.EXPORT.JSON", "false"))) {
                GsonBuilder builder = new GsonBuilder();
                builder.setPrettyPrinting().serializeNulls();
                Gson gson = builder.create();
                Writer writer = new FileWriter(
                        "/tmp/ReadingChangdNotification_" + DateTimeUtil
                                .getCurrentDateTimeByFormat("yyyyMMddHHmmssSSS")
                                + "_dump.json");
                gson.toJson(changedMeterReads, writer);
            }
            if (Boolean.parseBoolean(
                    MarsProperty.getProperty("HES.TEST.EXPORT.XML", "false"))) {
                File file = new File(
                        "/tmp/ReadingChangdNotification_" + DateTimeUtil
                                .getCurrentDateTimeByFormat("yyyyMMddHHmmssSSS")
                                + "_dump.xml");
                JAXBContext jaxbContext = JAXBContext
                        .newInstance(ArrayOfMeterReading1.class);
                Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
                jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                        true);
                jaxbMarshaller.marshal(changedMeterReads, file);
            }
        }

        return new ArrayOfErrorObject();
    }    
    
    /**
     * Allow client to Modify CB data for the Meter object.
     * If this transaction fails, CB returns information in a SOAPFault.
     */
    @WebMethod(operationName = "ModifyCBDataForMeters", action = "http://www.multispeak.org/Version_4.1_Release/ModifyCBDataForMeters")
    public @WebResult(name = "ModifyCBDataForMetersResult", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") ArrayOfErrorObject ModifyCBDataForMeters(
            @WebParam(name = "meterData", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") Meters meterData)
            throws Exception {

        GregorianCalendar c = new GregorianCalendar();
        c.setTime(Calendar.getInstance().getTime());
        XMLGregorianCalendar eventTime = DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(c);
        eventTime.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
        eventTime.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);
        int defaultModelId = deviceModelManager.getDeviceModelByName(null, MarsProperty.getProperty("HES.WS.DEFAULTMETERMODEL","CL710K22")).get(0).getId();
        ArrayOfErrorObject resp = null;

        if (meterData == null || (meterData.getElectricMeters() == null
                && meterData.getWaterMeters() == null
                && meterData.getGasMeters() == null
                && meterData.getPropaneMeters() == null)) {
            resp = new ArrayOfErrorObject();

            ErrorObject eo = new ErrorObject();
            eo.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                    + " [meterData] is null or empty.");
            eo.setNounType("");
            eo.setEventTime(eventTime);
            eo.setObjectID("");

            resp.getErrorObject().add(eo);
            return resp;
        }

        String trId = "";
        if(meterData.getObjectID() != null){
            trId = meterData.getObjectID();
        }

        int errExistMeter = 0;
        if (meterData.getElectricMeters() != null && meterData
                .getElectricMeters().getElectricMeter().isEmpty()) {
            errExistMeter = 1;
        } else if (meterData.getGasMeters() != null
                && meterData.getGasMeters().getGasMeter().isEmpty()) {
            errExistMeter = 2;
        } else if (meterData.getWaterMeters() != null
                && meterData.getWaterMeters().getWaterMeter().isEmpty()) {
            errExistMeter = 3;
        } else if (meterData.getPropaneMeters() != null
                && meterData.getPropaneMeters().getPropaneMeter().isEmpty()) {
            errExistMeter = 4;
        }

        if (errExistMeter != 0 && errExistMeter < 5) {
            resp = new ArrayOfErrorObject();
            ErrorObject eo = new ErrorObject();
            eo.setNounType("");
            eo.setEventTime(eventTime);
            eo.setObjectID(trId);
            switch (errExistMeter) {
            case 1:
                eo.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                        + " [electricMeters] is empty.");
                break;
            case 2:
                eo.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                        + " [gasMeters] is empty.");
                break;
            case 3:
                eo.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                        + " [waterMeters] is empty.");
                break;
            case 4:
                eo.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                        + " [propaneMeters] is empty.");
                break;
            }

            resp.getErrorObject().add(eo);
            return resp;
        }
        if (meterData.getGasMeters()!=null && meterData.getGasMeters().getGasMeter().size() > 0) {
            resp = new ArrayOfErrorObject();
            ErrorObject eo = new ErrorObject();
            eo.setNounType("");
            eo.setEventTime(eventTime);
            eo.setErrorString(ValidationError.NOT_SUPPORT.getName() + " gas meters");
            eo.setObjectID(trId);
            resp.getErrorObject().add(eo);
            return resp;
        }
        if (meterData.getWaterMeters()!=null && meterData.getWaterMeters().getWaterMeter().size() > 0) {
            resp = new ArrayOfErrorObject();
            ErrorObject eo = new ErrorObject();
            eo.setNounType("");
            eo.setEventTime(eventTime);
            eo.setErrorString(ValidationError.NOT_SUPPORT.getName() + " water meters");
            eo.setObjectID(trId);
            resp.getErrorObject().add(eo);
            return resp;
        }
        if (meterData.getPropaneMeters()!=null &&  meterData.getPropaneMeters().getPropaneMeter().size() > 0) {
            resp = new ArrayOfErrorObject();
            ErrorObject eo = new ErrorObject();
            eo.setNounType("");
            eo.setEventTime(eventTime);
            eo.setErrorString(ValidationError.NOT_SUPPORT.getName() + " heat meters");
            eo.setObjectID(trId);
            resp.getErrorObject().add(eo);
            return resp;
        }

        resp = new ArrayOfErrorObject();

        if (meterData.getElectricMeters() != null && !meterData
                .getElectricMeters().getElectricMeter().isEmpty()) {
            List<ElectricMeter> target = new ArrayList<ElectricMeter>(
                    meterData.getElectricMeters().getElectricMeter());
            for (ElectricMeter meter : target) {
                if(meter != null && meter.getMeterNo() != null && !"".equals(meter.getMeterNo())){
                    Meter m = meterManager.getMeter(meter.getMeterNo());
                    if (m == null && !meter.getMeterNo().toString().startsWith("testp")) {
                        ErrorObject obj = new ErrorObject();
                        obj.setErrorString(ValidationError.UNREGISTERED_METER.getName());
                        obj.setEventTime(eventTime);
                        obj.setNounType("");
                        obj.setObjectID(meter.getObjectID());
                        resp.getErrorObject().add(obj);
                        meterData.getElectricMeters().getElectricMeter().remove(meter);
                    } else {
                        Integer modelId = defaultModelId;
                        if (m != null && m.getModelId() != null) {
                            modelId = m.getModelId();
                        }
                        if (meter.getExtensionsList() != null
                                && meter.getExtensionsList()
                                        .getExtensionsItem() != null
                                && meter.getExtensionsList().getExtensionsItem()
                                        .size() > 0) {
                            for(ExtensionsItem test: meter.getExtensionsList().getExtensionsItem()) {
                                if(test.getExtName() != null && !test.getExtName().toString().trim().equals("")) {
                                    /*String[] obisCodes = test.getExtName().split("#");
                                    if(obisCodes.length != 3) {
                                        ErrorObject obj = new ErrorObject();
                                        obj.setErrorString(ValidationError.INVALID_PARAMETER.getName());
                                        obj.setEventTime(eventTime);
                                        obj.setNounType("");
                                        obj.setObjectID(meter.getObjectID());
                                        resp.getErrorObject().add(obj);
                                        meterData.getElectricMeters().getElectricMeter().remove(meter);
                                        break;
                                    }
                                    Map<String,Object> condition = new HashMap<String,Object>();
                                    condition.put("modelId", modelId.intValue());
                                    condition.put("obisCode", obisCodes[1]);
                                    condition.put("classId", obisCodes[0]);
                                    condition.put("attributeNo", obisCodes[2]);*/

                                    //IESCO에선 입력Param이 ObisCode에서 ServiceName으로 변경됨.
                                    String serviceName = test.getExtName().toString().trim();
                                    String dlmsClsName = ServiceNameConstants.ServiceNameMapper.getClassName(serviceName);
                                    if(serviceName.equals("") || dlmsClsName.equals("")){
                                        log.debug("Can't find DLMS_ClassName in ServiceNameMapper.");
                                        ErrorObject obj = new ErrorObject();
                                        obj.setErrorString(ValidationError.INVALID_PARAMETER.getName());
                                        obj.setEventTime(eventTime);
                                        obj.setNounType("");
                                        obj.setObjectID(meter.getObjectID());
                                        resp.getErrorObject().add(obj);
                                        meterData.getElectricMeters().getElectricMeter().remove(meter);
                                        break;
                                    }

                                    Map<String,Object> condition = new HashMap<String,Object>();
                                    condition.put("modelId", modelId.intValue());
                                    condition.put("className", dlmsClsName);

                                    List<Map<String,Object>> obsCodeData = obisCodeManager.getObisCodeInfoByName(condition);
                                    if(obsCodeData.size() == 0) {
                                        log.debug("NoData in ObisCode table.");
                                        ErrorObject obj = new ErrorObject();
                                        obj.setErrorString(ValidationError.INVALID_PARAMETER.getName());
                                        obj.setEventTime(eventTime);
                                        obj.setNounType("");
                                        obj.setObjectID(meter.getObjectID());
                                        resp.getErrorObject().add(obj);
                                        meterData.getElectricMeters().getElectricMeter().remove(meter);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }else{
                    ErrorObject obj = new ErrorObject();
                    obj.setErrorString(ValidationError.INVALID_PARAMETER.getName());
                    obj.setEventTime(eventTime);
                    obj.setNounType("");
                    obj.setObjectID(meter.getObjectID());
                    resp.getErrorObject().add(obj);
                    meterData.getElectricMeters().getElectricMeter().remove(meter);
                }
            }
        }
        if (meterData.getWaterMeters() != null && !meterData
                .getWaterMeters().getWaterMeter().isEmpty()) {
            List<WaterMeter> target = new ArrayList<WaterMeter>(
                    meterData.getWaterMeters().getWaterMeter());
            for (WaterMeter meter : target) {
                if(meter != null && meter.getMeterNo() != null && !"".equals(meter.getMeterNo())){
                    Meter m = meterManager.getMeter(meter.getMeterNo());
                    if (m == null && !meter.getMeterNo().toString().startsWith("testp")) {
                        ErrorObject obj = new ErrorObject();
                        obj.setErrorString(ValidationError.UNREGISTERED_METER.getName());
                        obj.setEventTime(eventTime);
                        obj.setNounType("");
                        obj.setObjectID(meter.getObjectID());
                        resp.getErrorObject().add(obj);
                        meterData.getWaterMeters().getWaterMeter().remove(meter);
                    } else {
                        Integer modelId = defaultModelId;
                        if (m.getModelId() != null) {
                            modelId = m.getModelId();
                        }
                        if (meter.getExtensionsList() != null
                                && meter.getExtensionsList()
                                        .getExtensionsItem() != null
                                && meter.getExtensionsList().getExtensionsItem()
                                        .size() > 0) {
                            for(ExtensionsItem test: meter.getExtensionsList().getExtensionsItem()) {
                                if(test.getExtName() != null && !test.getExtName().toString().trim().equals("")) {
                                    /*String[] obisCodes = test.getExtName().split("#");
                                    if(obisCodes.length != 3) {
                                        ErrorObject obj = new ErrorObject();
                                        obj.setErrorString(ValidationError.INVALID_PARAMETER.getName());
                                        obj.setEventTime(eventTime);
                                        obj.setNounType("");
                                        obj.setObjectID(meter.getObjectID());
                                        resp.getErrorObject().add(obj);
                                        meterData.getWaterMeters().getWaterMeter().remove(meter);
                                        break;
                                    }
                                    Map<String,Object> condition = new HashMap<String,Object>();
                                    condition.put("modelId", modelId.intValue());
                                    condition.put("obisCode", obisCodes[1]);
                                    condition.put("classId", obisCodes[0]);
                                    condition.put("attributeNo", obisCodes[2]);*/

                                    //IESCO에선 입력Param이 ObisCode에서 ServiceName으로 변경됨.
                                    String serviceName = test.getExtName().toString().trim();
                                    String dlmsClsName = ServiceNameConstants.ServiceNameMapper.getClassName(serviceName);
                                    if(serviceName.equals("") || dlmsClsName.equals("")){
                                        ErrorObject obj = new ErrorObject();
                                        obj.setErrorString(ValidationError.INVALID_PARAMETER.getName());
                                        obj.setEventTime(eventTime);
                                        obj.setNounType("");
                                        obj.setObjectID(meter.getObjectID());
                                        resp.getErrorObject().add(obj);
                                        meterData.getWaterMeters().getWaterMeter().remove(meter);
                                        break;
                                    }

                                    Map<String,Object> condition = new HashMap<String,Object>();
                                    condition.put("modelId", modelId.intValue());
                                    condition.put("className", dlmsClsName);

                                    List<Map<String,Object>> obsCodeData = obisCodeManager.getObisCodeInfoByName(condition);
                                    if(obsCodeData.size() == 0) {
                                        ErrorObject obj = new ErrorObject();
                                        obj.setErrorString(ValidationError.INVALID_PARAMETER.getName());
                                        obj.setEventTime(eventTime);
                                        obj.setNounType("");
                                        obj.setObjectID(meter.getObjectID());
                                        resp.getErrorObject().add(obj);
                                        meterData.getWaterMeters().getWaterMeter().remove(meter);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }else{
                    ErrorObject obj = new ErrorObject();
                    obj.setErrorString(ValidationError.INVALID_PARAMETER.getName());
                    obj.setEventTime(eventTime);
                    obj.setNounType("");
                    obj.setObjectID(meter.getObjectID());
                    resp.getErrorObject().add(obj);
                    meterData.getWaterMeters().getWaterMeter().remove(meter);
                }
            }
        }
        if (meterData.getGasMeters() != null && !meterData
                .getGasMeters().getGasMeter().isEmpty()) {
            List<GasMeter> target = new ArrayList<GasMeter>(
                    meterData.getGasMeters().getGasMeter());
            for (GasMeter meter : target) {
                if(meter != null && meter.getMeterNo() != null && !"".equals(meter.getMeterNo())){
                    Meter m = meterManager.getMeter(meter.getMeterNo());
                    if (m == null && !meter.getMeterNo().toString().startsWith("testp")) {
                        ErrorObject obj = new ErrorObject();
                        obj.setErrorString(ValidationError.UNREGISTERED_METER.getName());
                        obj.setEventTime(eventTime);
                        obj.setNounType("");
                        obj.setObjectID(meter.getObjectID());
                        resp.getErrorObject().add(obj);
                        meterData.getGasMeters().getGasMeter().remove(meter);
                    } else {
                        Integer modelId = defaultModelId;
                        if (m.getModelId() != null) {
                            modelId = m.getModelId();
                        }
                        if (meter.getExtensionsList() != null
                                && meter.getExtensionsList()
                                        .getExtensionsItem() != null
                                && meter.getExtensionsList().getExtensionsItem()
                                        .size() > 0) {
                            for(ExtensionsItem test: meter.getExtensionsList().getExtensionsItem()) {
                                if(test.getExtName() != null && !test.getExtName().toString().trim().equals("")) {
                                    /*String[] obisCodes = test.getExtName().split("#");
                                    if(obisCodes.length != 3) {
                                        ErrorObject obj = new ErrorObject();
                                        obj.setErrorString(ValidationError.INVALID_PARAMETER.getName());
                                        obj.setEventTime(eventTime);
                                        obj.setNounType("");
                                        obj.setObjectID(meter.getObjectID());
                                        resp.getErrorObject().add(obj);
                                        meterData.getGasMeters().getGasMeter().remove(meter);
                                        break;
                                    }
                                    Map<String,Object> condition = new HashMap<String,Object>();
                                    condition.put("modelId", modelId.intValue());
                                    condition.put("obisCode", obisCodes[1]);
                                    condition.put("classId", obisCodes[0]);
                                    condition.put("attributeNo", obisCodes[2]);*/

                                    //IESCO에선 입력Param이 ObisCode에서 ServiceName으로 변경됨.
                                    String serviceName = test.getExtName().toString().trim();
                                    String dlmsClsName = ServiceNameConstants.ServiceNameMapper.getClassName(serviceName);
                                    if(serviceName.equals("") || dlmsClsName.equals("")){
                                        ErrorObject obj = new ErrorObject();
                                        obj.setErrorString(ValidationError.INVALID_PARAMETER.getName());
                                        obj.setEventTime(eventTime);
                                        obj.setNounType("");
                                        obj.setObjectID(meter.getObjectID());
                                        resp.getErrorObject().add(obj);
                                        meterData.getGasMeters().getGasMeter().remove(meter);
                                        break;
                                    }

                                    Map<String,Object> condition = new HashMap<String,Object>();
                                    condition.put("modelId", modelId.intValue());
                                    condition.put("className", dlmsClsName);

                                    List<Map<String,Object>> obsCodeData = obisCodeManager.getObisCodeInfoByName(condition);
                                    if(obsCodeData.size() == 0) {
                                        ErrorObject obj = new ErrorObject();
                                        obj.setErrorString(ValidationError.INVALID_PARAMETER.getName());
                                        obj.setEventTime(eventTime);
                                        obj.setNounType("");
                                        obj.setObjectID(meter.getObjectID());
                                        resp.getErrorObject().add(obj);
                                        meterData.getGasMeters().getGasMeter().remove(meter);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }else{
                    ErrorObject obj = new ErrorObject();
                    obj.setErrorString(ValidationError.INVALID_PARAMETER.getName());
                    obj.setEventTime(eventTime);
                    obj.setNounType("");
                    obj.setObjectID(meter.getObjectID());
                    resp.getErrorObject().add(obj);
                    meterData.getGasMeters().getGasMeter().remove(meter);
                }
            }
        }
        if (meterData.getPropaneMeters() != null && !meterData
                .getPropaneMeters().getPropaneMeter().isEmpty()) {
            List<PropaneMeter> target = new ArrayList<PropaneMeter>(
                    meterData.getPropaneMeters().getPropaneMeter());
            for (PropaneMeter meter : target) {
                if(meter != null && meter.getMeterNo() != null && !"".equals(meter.getMeterNo())){
                    Meter m = meterManager.getMeter(meter.getMeterNo());
                    if (m == null && !meter.getMeterNo().toString().startsWith("testp")) {
                        ErrorObject obj = new ErrorObject();
                        obj.setErrorString(ValidationError.UNREGISTERED_METER.getName());
                        obj.setEventTime(eventTime);
                        obj.setNounType("");
                        obj.setObjectID(meter.getObjectID());
                        resp.getErrorObject().add(obj);
                        meterData.getPropaneMeters().getPropaneMeter().remove(meter);
                    } else {
                        Integer modelId = defaultModelId;
                        if (m.getModelId() != null) {
                            modelId = m.getModelId();
                        }
                        if (meter.getExtensionsList() != null
                                && meter.getExtensionsList()
                                        .getExtensionsItem() != null
                                && meter.getExtensionsList().getExtensionsItem()
                                        .size() > 0) {
                            for(ExtensionsItem test: meter.getExtensionsList().getExtensionsItem()) {
                                if(test.getExtName() != null && !test.getExtName().toString().trim().equals("")) {
                                    /*String[] obisCodes = test.getExtName().split("#");
                                    if(obisCodes.length != 3) {
                                        ErrorObject obj = new ErrorObject();
                                        obj.setErrorString(ValidationError.INVALID_PARAMETER.getName());
                                        obj.setEventTime(eventTime);
                                        obj.setNounType("");
                                        obj.setObjectID(meter.getObjectID());
                                        resp.getErrorObject().add(obj);
                                        meterData.getPropaneMeters().getPropaneMeter().remove(meter);
                                        break;
                                    }
                                    Map<String,Object> condition = new HashMap<String,Object>();
                                    condition.put("modelId", modelId.intValue());
                                    condition.put("obisCode", obisCodes[1]);
                                    condition.put("classId", obisCodes[0]);
                                    condition.put("attributeNo", obisCodes[2]);*/

                                    //IESCO에선 입력Param이 ObisCode에서 ServiceName으로 변경됨.
                                    String serviceName = test.getExtName().toString().trim();
                                    String dlmsClsName = ServiceNameConstants.ServiceNameMapper.getClassName(serviceName);
                                    if(serviceName.equals("") || dlmsClsName.equals("")){
                                        ErrorObject obj = new ErrorObject();
                                        obj.setErrorString(ValidationError.INVALID_PARAMETER.getName());
                                        obj.setEventTime(eventTime);
                                        obj.setNounType("");
                                        obj.setObjectID(meter.getObjectID());
                                        resp.getErrorObject().add(obj);
                                        meterData.getPropaneMeters().getPropaneMeter().remove(meter);
                                        break;
                                    }

                                    Map<String,Object> condition = new HashMap<String,Object>();
                                    condition.put("modelId", modelId.intValue());
                                    condition.put("className", dlmsClsName);

                                    List<Map<String,Object>> obsCodeData = obisCodeManager.getObisCodeInfoByName(condition);
                                    if(obsCodeData.size() == 0) {
                                        ErrorObject obj = new ErrorObject();
                                        obj.setErrorString(ValidationError.INVALID_PARAMETER.getName());
                                        obj.setEventTime(eventTime);
                                        obj.setNounType("");
                                        obj.setObjectID(meter.getObjectID());
                                        resp.getErrorObject().add(obj);
                                        meterData.getPropaneMeters().getPropaneMeter().remove(meter);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }else{
                    ErrorObject obj = new ErrorObject();
                    obj.setErrorString(ValidationError.INVALID_PARAMETER.getName());
                    obj.setEventTime(eventTime);
                    obj.setNounType("");
                    obj.setObjectID(meter.getObjectID());
                    resp.getErrorObject().add(obj);
                    meterData.getPropaneMeters().getPropaneMeter().remove(meter);
                }
            }
        }
        
        try {

            ModifyCBDataForMeters request = new ModifyCBDataForMeters();
            request.setMeterData(meterData);
            MultiSpeakMessage message = new CBMessage();
            message.setObject(request);
            //message.setMultiSpeakMsgHeader(multiSpeakMsgHeader.value);
            message.setRequestedTime(Calendar.getInstance());
            handler.putServiceData(QueueHandler.CB_MESSAGE, message);

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
    @WebMethod(operationName = "GetLatestReadingByMeterIDAsync", action = "http://www.multispeak.org/Version_4.1_Release/GetLatestReadingByMeterIDAsync")
    public @WebResult(name = "GetLatestReadingByMeterIDAsyncResult", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") ArrayOfErrorObject GetLatestReadingByMeterIDAsync(
            @WebParam(name = "meterReads", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") ArrayOfMeterReading1 meterReads,
            @WebParam(name = "transactionID", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") java.lang.String transactionID)
            throws Exception {

        return new ArrayOfErrorObject();
    }
    */

    @WebMethod(operationName = "GetReadingsByMeterIDAsync", action = "http://www.multispeak.org/Version_4.1_Release/GetReadingsByMeterIDAsync")
    public @WebResult(name = "GetReadingsByMeterIDAsyncResult", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") ArrayOfErrorObject GetReadingsByMeterIDAsync(
            @WebParam(name = "meterReads", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") ArrayOfMeterReading1 meterReads,
            @WebParam(name = "transactionID", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") java.lang.String transactionID)
            throws Exception {

        return new ArrayOfErrorObject();
    }


    @WebMethod(operationName = "ChangeIhdEvent_Send", action = "http://www.multispeak.org/Version_4.1_Release/ChangeIhdEvent_Send")
    public @WebResult(name = "ChangeIhdEvent_SendResult", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") ArrayOfErrorObject ChangeIhdEvent_Send(
            @WebParam(name = "inHomeDisplays", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") ArrayOfInHomeDisplay meterReads)
            throws Exception {

        log.debug("ChangeIhdEvent_Send on CB_Server");
        return new ArrayOfErrorObject();
    }

    /**
     * Allow client to Modify CB data for the Meter object.
     * If this transaction fails, CB returns information in a SOAPFault.
     */
    @WebMethod(operationName = "ModifyCBDataForServiceLocations", action = "http://www.multispeak.org/Version_4.1_Release/ModifyCBDataForServiceLocations")
    public @WebResult(name = "ModifyCBDataForServiceLocationsResult", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") ArrayOfErrorObject ModifyCBDataForServiceLocations(
            @WebParam(name = "serviceLocationData", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") ArrayOfServiceLocation1 serviceLocationData)
            throws Exception {
        log.debug("ModifyCBDataForServiceLocations on CB_Server");

        GregorianCalendar c = new GregorianCalendar();
        c.setTime(Calendar.getInstance().getTime());
        XMLGregorianCalendar eventTime = DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(c);
        eventTime.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
        eventTime.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);
        ArrayOfErrorObject resp = null;

        //Input이 Null이면 바로 종료
        if(serviceLocationData == null
                || serviceLocationData.getServiceLocation().isEmpty()){
            resp = new ArrayOfErrorObject();

            ErrorObject eo = new ErrorObject();
            eo.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                    + " [serviceLocationData] is null or empty.");
            eo.setNounType("");
            eo.setEventTime(eventTime);

            resp.getErrorObject().add(eo);
            return resp;
        }

        //ServiceLocation List item 체크
        resp = new ArrayOfErrorObject();
        List<ServiceLocation> serviceLocationList = new ArrayList<ServiceLocation>(serviceLocationData.getServiceLocation());
        for(ServiceLocation serviceLocation : serviceLocationList) {

            if(serviceLocation.getElectricServiceList() == null
                    || serviceLocation.getElectricServiceList().getElectricService().isEmpty()){
                ErrorObject obj = new ErrorObject();
                obj.setErrorString(ValidationError.NOT_SUPPORT.getName()); //not supported type of service.
                obj.setEventTime(eventTime);
                obj.setNounType("");
                obj.setObjectID(serviceLocation.getObjectID());
                resp.getErrorObject().add(obj);
                serviceLocationData.getServiceLocation().remove(serviceLocation); //지원하지 않는 item제거
            }
            else {
                //ElectricServiceList, meterBase, ...
                //다른 항목은 ModifyCBDataForServiceLocationsService에서 처리.
            }

        } //~List<ServiceLocation>

        //Queue
        try{
            ModifyCBDataForServiceLocations request = new ModifyCBDataForServiceLocations();
            request.setServiceLocationData(serviceLocationData);
            MultiSpeakMessage message = new CBMessage();
            message.setObject(request);
            message.setRequestedTime(Calendar.getInstance());
            handler.putServiceData(QueueHandler.CB_MESSAGE, message);

        }catch(Exception e){
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
