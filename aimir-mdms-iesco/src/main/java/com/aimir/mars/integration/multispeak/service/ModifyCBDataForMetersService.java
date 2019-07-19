package com.aimir.mars.integration.multispeak.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.json.JsonException;

import com.aimir.mars.integration.multispeak.client.Service1Soap;
import com.aimir.mars.integration.multispeak.util.ServiceNameConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.multispeak.version_4.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.device.MeterDao;
import com.aimir.mars.integration.multispeak.client.MRServerSoap;
import com.aimir.mars.integration.multispeak.data.MultiSpeakMessage;
import com.aimir.mars.integration.multispeak.util.MultiSpeakConstants.ValidationError;
import com.aimir.mars.util.CmdController;
import com.aimir.mars.util.MarsProperty;
import com.aimir.model.device.Meter;
import com.aimir.service.device.MeterManager;
import com.aimir.service.system.ObisCodeManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
@Transactional
public class ModifyCBDataForMetersService extends AbstractService {

    private static Log log = LogFactory
            .getLog(ModifyCBDataForMetersService.class);

    @Autowired
    private CmdController cmdController;
    @Autowired
    private MeterDao meterDao;
    @Autowired
    private MeterManager meterManager;
    @Autowired
    private ObisCodeManager obisCodeManager;

    @Autowired
    MRServerSoap mrServerSoap;
    @Autowired
    Service1Soap service1Soap;

    @Override
    public void execute(MultiSpeakMessage message) throws Exception {

        Calendar requestedTime = message.getRequestedTime();
        Object obj = message.getObject();

        ModifyCBDataForMeters request = (ModifyCBDataForMeters) obj;
        String responseURL = MarsProperty.getProperty("HES.WS.RESPONSE.MR",
                "http://172.31.120.46:7003/ssys/services/v1_1/NuriProxy/MR/proxy");
        Calendar expirationDateTime = Calendar.getInstance();
        expirationDateTime.setTime(requestedTime.getTime());

        Meters meters = request.getMeterData();

        for (ElectricMeter electricMeter : meters.getElectricMeters()
                .getElectricMeter()) {
            String meterNo = electricMeter.getMeterNo();

            //연동테스트용 분기
            if(meterNo != null && !meterNo.equals("") && meterNo.startsWith("testp")) {
                log.debug("ModifyCBDataForMetersService demo service [" + meterNo + "]");
                Meter meter = meterManager.getMeter("001");
                String modelName = meter.getModel().getName();
                Integer modelId = meter.getModelId();

                ArrayOfExtensionsItem arrayExtensionItem = electricMeter.getExtensionsList();
                if(arrayExtensionItem != null && arrayExtensionItem.getExtensionsItem().size() != 0){
                    List<ExtensionsItem> listExtensionItem = arrayExtensionItem.getExtensionsItem();
                    List<ExtensionsItem> listExtensionItemForResponse = new ArrayList<ExtensionsItem>();
                    for( ExtensionsItem item : listExtensionItem ){
                        if(item.getExtName() == null || "".equals(item.getExtName().trim())) {
                            log.debug("getExtName is null : invalid_parameter");
                            item.getExtValue().setValue(ValidationError.INVALID_PARAMETER.getName());
                            continue;
                        }
                        String serviceName = item.getExtName().toString().trim();
                        String dlmsClsName = ServiceNameConstants.ServiceNameMapper.getClassName(serviceName);
                        Map<String,Object> condition = new HashMap<String,Object>();
                        condition.put("modelId", modelId.intValue());
                        condition.put("className", dlmsClsName);
                        List<Map<String,Object>> obsCodeData = obisCodeManager.getObisCodeInfoByName(condition);
                        if(obsCodeData.size() == 0) {
                            log.debug("testp: obsCodeData.size() is 0");
                            item.getExtValue().setValue(ValidationError.NOT_SUPPORT.getName());
                            continue;
                        }

                        ExtensionsItem e = new ExtensionsItem();
                        e.setExtName(item.getExtName() + " : " + obsCodeData.get(0).get("OBISCODE").toString());
                        e.setExtType(item.getExtType());
                        e.setExtValue(item.getExtValue());
                        listExtensionItemForResponse.add(e);

                    } //for(ext item)
                    electricMeter.getExtensionsList().getExtensionsItem().clear();
                    for(ExtensionsItem e : listExtensionItemForResponse ) {
                        electricMeter.getExtensionsList().getExtensionsItem().add(e);
                    }
                }else{
                    log.debug("ArrayOfExtensionsItem is empty");
                }

            } else { //실제서비스 로직
                Meter meter = meterManager.getMeter(meterNo);
                String modelName = meter.getModel().getName();
                Integer modelId = meter.getModelId();
                String billingCycle = electricMeter.getBillingCycle();
                if(billingCycle!=null && !billingCycle.trim().equals("")) {
                    if(Integer.parseInt(billingCycle) >0 && Integer.parseInt(billingCycle) <= 60) {
                        setLpInterval(electricMeter, meterNo, modelId, modelName, "[{\"value\":\""+Integer.parseInt(billingCycle)*60+"\"}]");
                    } else {
                        log.debug("BillingCycle params are invalid.");
                        electricMeter.setErrorString(ValidationError.INVALID_PARAMETER.getName() + " [billingCycle]");
                    }
                }
                ArrayOfExtensionsItem arrayExtensionItem = electricMeter.getExtensionsList();
                if(arrayExtensionItem != null && arrayExtensionItem.getExtensionsItem().size() != 0) {
                    List<ExtensionsItem> listExtensionItem = arrayExtensionItem.getExtensionsItem();
                    List<ExtensionsItem> listExtensionItemForResponse = new ArrayList<ExtensionsItem>();
                    for(ExtensionsItem item : listExtensionItem ) {
                        if(item.getExtName() == null || "".equals(item.getExtName().trim())) {
                            log.debug("getExtName is null : invalid_parameter");
                            item.getExtValue().setValue(ValidationError.INVALID_PARAMETER.getName());
                            continue;
                        }
                    /*String[] obisCodes = item.getExtName().split("#");  // ClassID#Obiscode#AttributeNo
                    if(obisCodes.length != 3) {
                        item.getExtValue().setValue(ValidationError.INVALID_PARAMETER.getName());
                        continue;
                    }*/

                        boolean isSetCommand = true;
                        if(item.getExtValue().getValue() == null || "".equals(item.getExtValue().getValue().trim())) {
                            isSetCommand = false;
                        }

                        //IESCO에선 입력Param이 ObisCode에서 ServiceName으로 변경됨.
                        String serviceName = item.getExtName().toString().trim();
                        String dlmsClsName = ServiceNameConstants.ServiceNameMapper.getClassName(serviceName);

                        Map<String,Object> condition = new HashMap<String,Object>();
                        condition.put("modelId", modelId.intValue());
                        condition.put("className", dlmsClsName);
                    /*condition.put("obisCode", obisCodes[1]);
                    condition.put("classId", obisCodes[0]);
                    condition.put("attributeNo", obisCodes[2]);*/

                        List<Map<String,Object>> obsCodeData = obisCodeManager.getObisCodeInfoByName(condition);
                        if(obsCodeData.size() == 0) {
                            log.debug("obsCodeData.size() is 0");
                            item.getExtValue().setValue(ValidationError.NOT_SUPPORT.getName());
                            continue;
                        }
                        JsonArray jsonArr = new JsonArray();
                        JsonObject jsonObj = new JsonObject();
                        jsonObj.addProperty("ID", obsCodeData.get(0).get("ID").toString());
                        jsonObj.addProperty("OBISCODE", obsCodeData.get(0).get("OBISCODE").toString());
                        jsonObj.addProperty("CLASSNAME", obsCodeData.get(0).get("CLASSNAME").toString());
                        jsonObj.addProperty("CLASSID", obsCodeData.get(0).get("CLASSID").toString());
                        jsonObj.addProperty("ATTRIBUTENO", obsCodeData.get(0).get("ATTRIBUTENO").toString());
                        jsonObj.addProperty("ATTRIBUTENAME", obsCodeData.get(0).get("ATTRIBUTENAME").toString());
                        jsonObj.addProperty("DATATYPE", obsCodeData.get(0).get("DATATYPE").toString());
                        jsonObj.addProperty("ACCESSRIGHT", obsCodeData.get(0).get("ACCESSRIGHT").toString());
                        JsonParser jp = new JsonParser();
                        if(isSetCommand) {
                            if(jp.parse(item.getExtValue().getValue()).isJsonArray()) {
                                jsonObj.add("VALUE", jp.parse(item.getExtValue().getValue()));
                            } else if(jp.parse(item.getExtValue().getValue()).isJsonObject()) {
                                jsonObj.add("VALUE", jp.parse("[" + item.getExtValue().getValue() + "]"));
                            } else {
                                if(item.getExtName().equals("7#1.0.99.1.0.255#3")) {
                                    /**
                                     * [
                                     *  {"paramType":"Channel 1","paramValue":"ClassId[8], ObisCode[0000010000FF], AttributeNo[2]"},
                                     *  {"paramType":"Channel 2","paramValue":"ClassId[1], ObisCode[0000600A02FF], AttributeNo[2]"},
                                     *  {"paramType":"Channel 3","paramValue":"ClassId[3], ObisCode[0100010800FF], AttributeNo[2]"},
                                     *  {"paramType":"Channel 4","paramValue":"ClassId[3], ObisCode[0100020800FF], AttributeNo[2]"},
                                     *  {"paramType":"Channel 5","paramValue":"ClassId[3], ObisCode[0100030800FF], AttributeNo[2]"},
                                     *  {"paramType":"Channel 6","paramValue":"ClassId[3], ObisCode[0100040800FF], AttributeNo[2]"}
                                     * ]
                                     * 7#1.0.1.8.0.255#3,7#1.0.2.8.0.255#3,7#1.0.3.8.0.255#3,7#1.0.4.8.0.255#3
                                     */
                                    String[] values = item.getExtValue().getValue().split(",");
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("[");
                                    sb.append("{\"paramType\":\"Channel 1\",\"paramValue\":\"8#0.0.1.0.0.255#2\"},");
                                    sb.append("{\"paramType\":\"Channel 2\",\"paramValue\":\"7#0.0.96.10.2.255#2\"},");
                                    for (int i = 0; i < values.length; i++) {
                                        sb.append("{\"paramType\":\"Channel ").append(2+i).append("\",\"paramValue\":\"").append(values[i]).append("\"}");
                                        if(i < values.length -1) {
                                            sb.append(",");
                                        }
                                    }
                                    sb.append("]");
                                    jsonObj.add("VALUE", jp.parse("[{\"value\" : \"" + sb.toString() + "\"}]"));
                                } else {
                                    jsonObj.add("VALUE", jp.parse("[{\"value\" : \"" + item.getExtValue().getValue() + "\"}]"));
                                }
                            }
                        } else {
                            jsonObj.add("VALUE", jp.parse("[{\"value\" : \"\"}]"));
                        }

                        jsonArr.add(jsonObj);

                        String parameter = jsonArr.toString();
                        Map<String, Object> result = null;
                        try {
                            if(isSetCommand) {
                                result = cmdController.dlmsGetSet("cmdMeterParamSet", parameter, meterNo, modelName);
                            } else {
                                result = cmdController.dlmsGetSet("cmdMeterParamGet", parameter, meterNo, modelName);
                            }
                            log.debug(result);
                            if(result != null) {
                                List<Map<String, Object>> rtnStrList = (ArrayList<Map<String, Object>>) result.get("rtnStrList");
                                if(rtnStrList == null || rtnStrList.size()==0) {
                                    if(listExtensionItem!=null && listExtensionItem.size() == 1) {
                                        electricMeter.setErrorString(ValidationError.COMMUNICATION_FAILURE.getName());
                                    }
                                    continue;
                                } else {
                                    for(Map<String, Object> rtnStrMap : rtnStrList) {
                                        if(rtnStrMap == null || rtnStrMap.get("rtnStr") == null || "".equals(((String)rtnStrMap.get("rtnStr")).trim())) {
                                            if(listExtensionItem!=null && listExtensionItem.size() == 1) {
                                                electricMeter.setErrorString(ValidationError.COMMUNICATION_FAILURE.getName());
                                            }
                                            continue;
                                        }
                                        String rtnStr = (String) rtnStrMap.get("rtnStr");
                                        if(rtnStr.toUpperCase().startsWith("FAIL")) {
                                            if(listExtensionItem!=null && listExtensionItem.size() == 1) {
                                                electricMeter.setErrorString(ValidationError.COMMUNICATION_FAILURE.getName());
                                            }
                                            continue;
                                        } else if(!isSetCommand){ //Get Result
                                            List<Map<String, Object>> viewMsg = (List<Map<String, Object>>) rtnStrMap.get("viewMsg");
                                            if(viewMsg!=null) {
                                                JsonArray jsa = new JsonArray();
                                                for(Map<String, Object> iv: viewMsg) {
                                                    JsonObject jso = new JsonObject();
                                                    jso.addProperty(iv.get("paramType").toString(), iv.get("paramValue").toString());
                                                    jsa.add(jso);
                                                }
                                                //String value = parsingValue(viewMsg);
                                                String value = jsa.toString();
                                                if(item.getExtValue() == null) {
                                                    ExtValue ev = new ExtValue();
                                                    ev.setValue(value);
                                                } else {
                                                    item.getExtValue().setValue(value);
                                                }
                                                listExtensionItemForResponse.add(item);
                                            }
                                        } else { //Set Result
                                            ExtensionsItem e = new ExtensionsItem();
                                            e.setExtName(item.getExtName());
                                            e.setExtType(item.getExtType());
                                            e.setExtValue(item.getExtValue());
                                            listExtensionItemForResponse.add(e);
                                        }
                                    }
                                }
                            } else {
                                if(listExtensionItem!=null && listExtensionItem.size() == 1) {
                                    electricMeter.setErrorString(ValidationError.COMMUNICATION_FAILURE.getName());
                                }
                            }
                        } catch(Exception e) {
                            if(listExtensionItem!=null && listExtensionItem.size() == 1) {
                                electricMeter.setErrorString(ValidationError.SYSTEM_ERROR.getName());
                            }
                        }
                    }
                    electricMeter.getExtensionsList().getExtensionsItem().clear();
                    for(ExtensionsItem e : listExtensionItemForResponse ) {
                        electricMeter.getExtensionsList().getExtensionsItem().add(e);
                    }
                } else {
                    if(billingCycle==null || billingCycle.trim().equals("")) {
                        electricMeter.setErrorString(ValidationError.INVALID_PARAMETER.getName());
                    }
                }
            } //if(testp)-else

        } //for(electric meters)

        Meters changedMeters = meters;

        Calendar currentTime = Calendar.getInstance();

        Client client = ClientProxy.getClient(service1Soap);
        HTTPConduit http = (HTTPConduit) client.getConduit();
        TLSClientParameters tlsParams = new TLSClientParameters();
        tlsParams.setDisableCNCheck(true); // CN Name check ignore...
        http.setTlsClientParameters(tlsParams);
        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        httpClientPolicy.setAllowChunking(false);
        http.setClient(httpClientPolicy);
        log.debug("Send MeterChangedNotification Response..");

        ArrayOfErrorObject response = service1Soap
                .MeterChangedNotification(changedMeters);
        log.debug("MeterChangedNotification ACK Response");
        if (response != null && response.getErrorObject().size() > 0) {
            for (ErrorObject error : response.getErrorObject()) {
                log.info("ErrorObject, ObjectID=[" + error.getObjectID()
                        + "], ErrorString=[" + error.getErrorString()
                        + "], EventTime=[" + error.getEventTime() + "]");
            }
        }

        log.debug("ModifyCBDataForMetersService execute end..");
    }

    private String parsingValue(List<Map<String, Object>> viewMsg) {
        String value = null;
        String valueDate = null;
        String valueTime = null;
        for(Map<String, Object> item: viewMsg) {
            if(item.get("paramType").toString().toUpperCase().equals("VALUE")) {
                value = (String) item.get("paramValue");
            }
            if(item.get("paramType").toString().toUpperCase().equals("DATE(YYYY/MM/DD)")) {
                valueDate = (String) item.get("paramValue");
            }
            if(item.get("paramType").toString().toUpperCase().equals("TIME(HH:MM:SS)")) {
                valueTime = (String) item.get("paramValue");
            }
            if(item.get("paramType").toString().toUpperCase().equals("DATA_SIZE")) {
                
            }
        }

        if (value != null) {
            return value;
        } else if(valueDate != null || valueTime != null) {
            if(valueDate != null && valueTime != null) {
                return valueDate + " " + valueTime;
            } else if(valueDate != null && valueTime == null) {
                return valueDate;
            } else if(valueDate == null && valueTime != null) {
                return valueTime;
            }
        }
        return "";
    }

    private void setLpInterval(ElectricMeter electricMeter, String meterNo, Integer modelId, String modelName, String value) {
        String item = "7#1.0.99.1.0.255#4";
        String[] obisCodes = item.split("#");  // ClassID#Obiscode#AttributeNo
        Map<String,Object> condition = new HashMap<String,Object>();
        condition.put("modelId", modelId.intValue());
        condition.put("obisCode", obisCodes[1]);
        condition.put("classId", obisCodes[0]);
        condition.put("attributeNo", obisCodes[2]);

        List<Map<String,Object>> obsCodeData = obisCodeManager.getObisCodeInfo(condition);

        JsonArray jsonArr = new JsonArray();
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("ID", obsCodeData.get(0).get("ID").toString());
        jsonObj.addProperty("OBISCODE", obsCodeData.get(0).get("OBISCODE").toString());
        jsonObj.addProperty("CLASSNAME", obsCodeData.get(0).get("CLASSNAME").toString());
        jsonObj.addProperty("CLASSID", obsCodeData.get(0).get("CLASSID").toString());
        jsonObj.addProperty("ATTRIBUTENO", obsCodeData.get(0).get("ATTRIBUTENO").toString());
        jsonObj.addProperty("ATTRIBUTENAME", obsCodeData.get(0).get("ATTRIBUTENAME").toString());
        jsonObj.addProperty("DATATYPE", obsCodeData.get(0).get("DATATYPE").toString());
        jsonObj.addProperty("ACCESSRIGHT", obsCodeData.get(0).get("ACCESSRIGHT").toString());
        JsonParser jp = new JsonParser();
        try {
            jsonObj.add("VALUE", jp.parse(value));
        } catch(Exception e) {
            electricMeter.setErrorString(ValidationError.INVALID_PARAMETER.getName());
            return;
        }
        jsonArr.add(jsonObj);

        
        String parameter = jsonArr.toString();
        Map<String, Object> result = null;
        try {
            result = cmdController.dlmsGetSet("cmdMeterParamSet", parameter, meterNo, modelName);
            log.debug(result);
            if(result != null) {
                List<Map<String, Object>> rtnStrList = (ArrayList<Map<String, Object>>) result.get("rtnStrList");
                if(rtnStrList == null || rtnStrList.size()==0) {
                    electricMeter.setErrorString(ValidationError.COMMUNICATION_FAILURE.getName());
                } else {
                    for(Map<String, Object> rtnStrMap : rtnStrList) {
                        if(rtnStrMap == null || rtnStrMap.get("rtnStr") == null || "".equals(((String)rtnStrMap.get("rtnStr")).trim())) {
                            electricMeter.setErrorString(ValidationError.COMMUNICATION_FAILURE.getName());
                            continue;
                        }
                        String rtnStr = (String) rtnStrMap.get("rtnStr");
                        if(rtnStr.startsWith("FAIL")) {
                            electricMeter.setErrorString(ValidationError.COMMUNICATION_FAILURE.getName());
                            continue;
                        }
                    }
                }
            } else {
                electricMeter.setErrorString(ValidationError.COMMUNICATION_FAILURE.getName());
            }
        } catch(Exception e) {
            electricMeter.setErrorString(ValidationError.SYSTEM_ERROR.getName());
        }
    }
}
