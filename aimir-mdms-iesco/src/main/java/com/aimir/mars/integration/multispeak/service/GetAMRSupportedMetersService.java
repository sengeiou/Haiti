package com.aimir.mars.integration.multispeak.service;

import com.aimir.fep.protocol.security.OacServerApi;
import com.aimir.mars.integration.multispeak.client.Service1Soap;
import com.aimir.mars.integration.multispeak.data.MultiSpeakMessage;
import com.aimir.mars.integration.multispeak.util.MultiSpeakConstants;
import com.aimir.service.device.MeterManager;
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

import java.io.IOException;
import java.util.*;

@Service
@Transactional
public class GetAMRSupportedMetersService {

    private static Log log = LogFactory.getLog(GetAMRSupportedMetersService.class);

    @Autowired
    private MeterManager meterManager;
    @Autowired
    Service1Soap service1Soap;

    private String HES_DEVICE_SERIAL;

    public GetAMRSupportedMetersResponse execute(MultiSpeakMessage message) throws Exception {
        log.debug("## GetAMRSupportedMetersService execute..");
        GetAMRSupportedMetersResponse response = new GetAMRSupportedMetersResponse();
        Meters resMeters = new Meters();

        //Get Object
        Object obj = message.getObject();
        GetAMRSupportedMeters request = (GetAMRSupportedMeters) obj;

        //Set Expiration Time (사용하지 않음)
        Calendar requestedTime = message.getRequestedTime();
        Calendar expirationDateTime = Calendar.getInstance();
        expirationDateTime.setTime(requestedTime.getTime());

        //Get Prop for OACApi
        Properties prop = new Properties();
        try {
            prop.load(getClass().getClassLoader().getResourceAsStream("config/mars.properties"));
            if (prop.containsKey("protocol.security.hes.deviceSerial")) {
                this.HES_DEVICE_SERIAL = prop.getProperty("protocol.security.hes.deviceSerial");
            } else {
                this.HES_DEVICE_SERIAL = "000H000000000003";
            }
        }catch (IOException ie) {
            log.debug("IOException on loading properties.");
            resMeters.setErrorString(MultiSpeakConstants.ValidationError.SYSTEM_ERROR.getName());
            response.setGetAMRSupportedMetersResult(resMeters);
            //IOException 리턴.
            return response;
        }

        //파라미터 처리
        String lastReceived = request.getLastReceived();
        if(lastReceived != null && !lastReceived.equals("")) {
            String[] meterNumbers = lastReceived.split("#");
            if(meterNumbers.length < 1) {
                //split exception
                log.debug("Split result invalid.");
                resMeters.setErrorString(MultiSpeakConstants.ValidationError.INVALID_PARAMETER.getName());
            }else{
                ArrayOfElectricMeter arrayOfElectricMeter = new ArrayOfElectricMeter();
                List<ElectricMeter> electricMeterList = new ArrayList<>();
                OacServerApi oacApi  = new OacServerApi();
                //미터별 sharedKey 조회
                for(String meterNo : meterNumbers) {
                    //ElectricMeter 생성
                    ElectricMeter electricMeter = new ElectricMeter();
                    electricMeter.setMeterNo(meterNo);
                    //sealNumberList 생성
                    ArrayOfSealNumbers arrayOfSealNumbers = new ArrayOfSealNumbers();
                    //미터키 조회
                    HashMap<String, String> sharedKey = oacApi.getPanaMeterSharedKey(HES_DEVICE_SERIAL, meterNo);
                    if(sharedKey != null) {
                        String masterKey = sharedKey.get("MasterKey");
                        String unicastKey = sharedKey.get("UnicastKey");
                        String multicastKey = sharedKey.get("MulticastKey");
                        String authenticationKey = sharedKey.get("AuthenticationKey");

                        //sealNumber 작성
                        arrayOfSealNumbers.getSealNumber().add(0,masterKey);
                        arrayOfSealNumbers.getSealNumber().add(1,unicastKey);
                        arrayOfSealNumbers.getSealNumber().add(2,multicastKey);
                        arrayOfSealNumbers.getSealNumber().add(3,authenticationKey);
                        electricMeter.setSealNumberList(arrayOfSealNumbers);
                    }else{
                        //등록된 미터키 없음
                        electricMeter.setErrorString(MultiSpeakConstants.ValidationError.NO_DATA.getName());
                    }
                    //ArrayOfElectricMeter에 추가
                    arrayOfElectricMeter.getElectricMeter().add(electricMeter);
                }

                //Meters에 추가
                resMeters.setElectricMeters(arrayOfElectricMeter);
            }

        }else{
            //null exception
            log.debug("## lastReceived param is null.");
            resMeters.setErrorString(MultiSpeakConstants.ValidationError.INVALID_PARAMETER.getName());

        }

        response.setGetAMRSupportedMetersResult(resMeters);
        Calendar currentTime = Calendar.getInstance();
        /*Client client = ClientProxy.getClient(service1Soap);
        HTTPConduit http = (HTTPConduit) client.getConduit();
        TLSClientParameters tlsParams = new TLSClientParameters();
        tlsParams.setDisableCNCheck(true); // CN Name check ignore...
        http.setTlsClientParameters(tlsParams);
        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        httpClientPolicy.setAllowChunking(false);
        http.setClient(httpClientPolicy);

        log.debug("Send GetAMRSupportedMetersResponse..");
        ArrayOfErrorObject errorObject = service1Soap.MeterChangedNotification(resMeters);
        log.debug("GetAMRSupportedMetersResponse ACK Response");
        if (errorObject != null && errorObject.getErrorObject().size() > 0) {
            for (ErrorObject error : errorObject.getErrorObject()) {
                log.info("ErrorObject, ObjectID=[" + error.getObjectID()
                        + "], ErrorString=[" + error.getErrorString()
                        + "], EventTime=[" + error.getEventTime() + "]");
            }
        }*/

        log.debug("## GetAMRSupportedMetersService done..");
        return response;
    }
}
