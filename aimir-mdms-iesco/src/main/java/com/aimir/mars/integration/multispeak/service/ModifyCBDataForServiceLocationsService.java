package com.aimir.mars.integration.multispeak.service;

import com.aimir.mars.integration.multispeak.client.MRServerSoap;
import com.aimir.mars.integration.multispeak.client.Service2Soap;
import com.aimir.mars.integration.multispeak.data.MultiSpeakMessage;
import com.aimir.mars.integration.multispeak.util.MultiSpeakConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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

import java.util.Calendar;
import java.util.List;

@Service
@Transactional
public class ModifyCBDataForServiceLocationsService extends AbstractService {

    private static Log log = LogFactory.getLog(ModifyCBDataForServiceLocationsService.class);

    @Autowired
    MRServerSoap mrServerSoap;
    @Autowired
    Service2Soap service2Soap;

    @Override
    public void execute(MultiSpeakMessage message) throws Exception {
        log.debug("## ModifyCBDataForServiceLocationsService execute..");

        //Get Object
        Object obj = message.getObject();
        ModifyCBDataForServiceLocations request = (ModifyCBDataForServiceLocations) obj;

        //Set Expiration Time (사용하지 않음)
        Calendar requestedTime = message.getRequestedTime();
        Calendar expirationDateTime = Calendar.getInstance();
        expirationDateTime.setTime(requestedTime.getTime());

        ArrayOfServiceLocation1 arrayOfServiceLocation1 = request.getServiceLocationData();
        List<ServiceLocation> serviceLocationList = arrayOfServiceLocation1.getServiceLocation();


        if(serviceLocationList.isEmpty() || serviceLocationList.size() < 1){
            log.debug("No data in arrayOfServiceLocation1.");
            //CB_SERVER에서 처리
        }
        else{
            //List<ServiceLocation>이 null이 아니면.
            for(ServiceLocation serviceLocation : serviceLocationList){

                if(serviceLocation.getElectricServiceList() == null
                        || serviceLocation.getElectricServiceList().getElectricService().isEmpty()){
                    log.debug("No ElectricServiceList in ServiceLocation.");
                    serviceLocation.setErrorString(MultiSpeakConstants.ValidationError.INVALID_PARAMETER.getName());

                }else{
                    //List<ElectricService>가 null이 아니면.
                    List<ElectricService> electricServiceList = serviceLocation.getElectricServiceList().getElectricService();
                    for(ElectricService electricService : electricServiceList){

                        if(electricService.getMeterBase() == null){
                            log.debug("No MeterBase in ElectricService.");
                            electricService.setErrorString(MultiSpeakConstants.ValidationError.INVALID_PARAMETER.getName());

                        }else{
                            //MeterBase가 null이 아니면.
                            MeterBase meterBase = electricService.getMeterBase();
                            if(meterBase.getInHomeDisplays() == null
                                    || meterBase.getInHomeDisplays().getInHomeDisplay().isEmpty()){
                                log.debug("No InHomeDisplayList in MeterBase.");
                                meterBase.setErrorString(MultiSpeakConstants.ValidationError.INVALID_PARAMETER.getName());

                            }else{
                                //InHomeDisplay List 처리.
                                List<InHomeDisplay> inHomeDisplayList = meterBase.getInHomeDisplays().getInHomeDisplay();
                                for(InHomeDisplay ihd : inHomeDisplayList){
                                    String serialNumber = ihd.getSerialNumber();
                                    String deviceType = ihd.getDeviceType();
                                    //Message to JSON
                                    JsonArray jsonArr_ihdMsg = new JsonArray();
                                    JsonObject jsonObj_ihdMsg = new JsonObject();
                                    jsonObj_ihdMsg.addProperty("SerialNumber", serialNumber);
                                    jsonObj_ihdMsg.addProperty("DeviceType", deviceType);
                                    if(ihd.getInHomeDisplayMessageList() == null
                                            || ihd.getInHomeDisplayMessageList().getInHomeDisplayMessage().isEmpty()){
                                        log.debug("No InHomeDisplayMessage in InHomeDisplay.");
                                        ihd.setErrorString(MultiSpeakConstants.ValidationError.INVALID_PARAMETER.getName());

                                    }else{
                                        List<InHomeDisplayMessage> inHomeDisplayMessageList = ihd.getInHomeDisplayMessageList().getInHomeDisplayMessage();
                                        for(InHomeDisplayMessage ihdMsg : inHomeDisplayMessageList){
                                            String ihdId = ihdMsg.getInHomeDisplayID();
                                            Duration duration = ihdMsg.getDuration();
                                            String durationVal = String.valueOf(duration.getValue());
                                            String timeUnits = duration.getUnits().value();
                                            String alertLevel = ihdMsg.getAlertLevel();
                                            String priority = ihdMsg.getPriorityOrder().toString();
                                            String reason = ihdMsg.getReason();

                                            //json형태로 바꾸자..
                                            JsonArray jsonArr_msgLineList = new JsonArray();
                                            JsonObject jsonObj_msgLineList = new JsonObject();
                                            jsonObj_msgLineList.addProperty("ihdId", ihdId);
                                            jsonObj_msgLineList.addProperty("duration", durationVal);
                                            jsonObj_msgLineList.addProperty("timeUnits", timeUnits);
                                            jsonObj_msgLineList.addProperty("alertLevel", alertLevel);
                                            jsonObj_msgLineList.addProperty("priorityOrder", priority);
                                            jsonObj_msgLineList.addProperty("reason", reason);
                                            if(ihdMsg.getMsgLineList() == null
                                                    || ihdMsg.getMsgLineList().getMsgLine().isEmpty()) {
                                                log.debug("No MsgLine in InHomeDisplayMessage.");
                                                ihdMsg.setErrorString(MultiSpeakConstants.ValidationError.INVALID_PARAMETER.getName());
                                            }else{
                                                //마지막 텍스트.
                                                List<MsgLine> msgLineList = ihdMsg.getMsgLineList().getMsgLine();
                                                for(MsgLine msgLine : msgLineList){
                                                    String seq = msgLine.getSequenceNumber().toString();
                                                    String msgText = msgLine.getText();
                                                    //put text to json
                                                    JsonObject json_msg = new JsonObject();
                                                    json_msg.addProperty("SequenceNumber", seq);
                                                    json_msg.addProperty("Text", msgText);
                                                    jsonArr_msgLineList.add(json_msg);
                                                } //~MsgLineList
                                            }
                                            jsonObj_msgLineList.add("msgLineList", jsonArr_msgLineList);
                                            jsonArr_ihdMsg.add(jsonObj_msgLineList);

                                        } //~inHomeDisplayMessageList

                                        jsonObj_ihdMsg.add("ihdMsgList", jsonArr_ihdMsg);
                                        //json 출력테스트
                                        log.debug("JSON OUT: " + jsonObj_ihdMsg.toString());

                                    }
                                } //~inHomeDisplayList
                            }
                        }
                    } //~electricServiceList
                }
            } //~serviceLocationList


        } //if(serviceLocationList.isEmpty()

        ArrayOfServiceLocation1 changedServiceLocations = arrayOfServiceLocation1;
        Calendar currentTime = Calendar.getInstance();

        Client client = ClientProxy.getClient(service2Soap);
        HTTPConduit http = (HTTPConduit) client.getConduit();
        TLSClientParameters tlsParams = new TLSClientParameters();
        tlsParams.setDisableCNCheck(true); // CN Name check ignore...
        http.setTlsClientParameters(tlsParams);
        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        httpClientPolicy.setAllowChunking(false);
        http.setClient(httpClientPolicy);
        log.debug("Send ServiceLocationChangedNotification Response..");

        ArrayOfErrorObject response = service2Soap.ServiceLocationChangedNotification(changedServiceLocations);

        log.debug("ServiceLocationChangedNotification ACK Response");
        if (response != null && response.getErrorObject().size() > 0) {
            for (ErrorObject error : response.getErrorObject()) {
                log.info("ErrorObject, ObjectID=[" + error.getObjectID()
                        + "], ErrorString=[" + error.getErrorString()
                        + "], EventTime=[" + error.getEventTime() + "]");
            }
        }

        log.debug("ModifyCBDataForServiceLocationsService - execute end..");
    }
}
