package com.aimir.mars.integration.multispeak.service;

import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.WebServiceLogDao;
import com.aimir.fep.util.DataUtil;
import com.aimir.mars.integration.multispeak.client.Service1Soap;
import com.aimir.mars.integration.multispeak.util.MultiSpeakConstants;
import com.aimir.model.system.Code;
import com.aimir.model.system.WebServiceLog;
import com.aimir.service.device.MeterManager;
import com.aimir.util.TimeUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.multispeak.version_4.ArrayOfErrorObject;
import org.multispeak.version_4.ElectricMeter;
import org.multispeak.version_4.ErrorObject;
import org.multispeak.version_4.Meters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional
public class GetAMRSupportedMetersResponse1Service {

    private static Log log = LogFactory.getLog(GetAMRSupportedMetersResponse1Service.class);

    @Resource(name = "transactionManager")
    HibernateTransactionManager txmanager;
    @Autowired
    private CodeDao codeDao;
    @Autowired
    private WebServiceLogDao webServiceLogDao;
    @Autowired
    Service1Soap service1Soap;

    public ArrayOfErrorObject execute(Meters _metersResult) throws Exception{
        log.debug("## GetAMRSupportedMetersResponse1_Service execute..");

        ArrayOfErrorObject resp = new ArrayOfErrorObject();
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(Calendar.getInstance().getTime());

        XMLGregorianCalendar eventTime = DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(c);
        eventTime.setTimezone(DatatypeConstants.FIELD_UNDEFINED);

        //recommended at java8+
        LocalDateTime localDateTime = TimeUtil.toLocalDateTime(c);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddhhmmss");
        String currentDateTime = localDateTime.format(dateTimeFormatter);

        boolean isInvalidParameter = false;
        if(_metersResult.getErrorString() != null){
            //Invalid Parameter 처리
            isInvalidParameter = true;

            ErrorObject obj = new ErrorObject();
            obj.setEventTime(eventTime);
            obj.setNounType("");
            obj.setErrorString(_metersResult.getErrorString());
            resp.getErrorObject().add(obj);
            saveWebServiceLog(currentDateTime, obj.getErrorString(), "-");
            return resp;
        }

        //TODO 데이터를 어떻게 처리할지는 고민중. 현재는 List<Map>으로 처리.
        ArrayList <HashMap<String,String>> resultList = new ArrayList<HashMap<String,String>>();
        //Meters->ElectricMeters
        if(_metersResult.getElectricMeters() != null){
            isInvalidParameter = false;
            List<ElectricMeter> target = new ArrayList<>(_metersResult.getElectricMeters().getElectricMeter());
            //For each meters
            for(ElectricMeter el : target){
                HashMap<String, String> sharedKey = new HashMap<String, String>();
                sharedKey.put("meterNo", el.getMeterNo());
                if(el.getErrorString() != null){
                    //미등록 Meter이거나, 매칭되는 키가 없을 경우
                    String errorString = el.getErrorString();
                    sharedKey.put("Status", "Error: " + errorString);
                    resultList.add(sharedKey);
                    //result확인용 test
                    ErrorObject errorObject = new ErrorObject();
                    errorObject.setEventTime(eventTime);
                    errorObject.setNounType("");
                    errorObject.setErrorString("meterNo: " + el.getMeterNo() + ", error: " + errorString);
                    resp.getErrorObject().add(errorObject);
                    saveWebServiceLog(currentDateTime, errorObject.getErrorString(), el.getMeterNo());
                    continue;
                }
                //MeterKey 꺼내기
                List<String> sealNumberList = new ArrayList<>(el.getSealNumberList().getSealNumber());
                String masterKey = sealNumberList.get(0);
                String unicastKey = sealNumberList.get(1);
                String multicastKey = sealNumberList.get(2);
                String authenticationKey = sealNumberList.get(3);
                //Map에 넣고..
                sharedKey.put("MasterKey", masterKey);
                sharedKey.put("UnicastKey", unicastKey);
                sharedKey.put("MulticastKey", multicastKey);
                sharedKey.put("AuthenticationKey", authenticationKey);
                resultList.add(sharedKey);

                //result확인용 test
                ErrorObject errorObject = new ErrorObject();
                errorObject.setEventTime(eventTime);
                errorObject.setNounType("");
                errorObject.setErrorString("meterNo:"+ el.getMeterNo() + ", [0,MasterKey: " + masterKey + "], [1,UnicastKey: " + unicastKey
                        + "], [2,MulticastKey: " + multicastKey + "], [3,AuthenticationKey: " + authenticationKey + "]");
                resp.getErrorObject().add(errorObject);

                //저장할 곳에 넘기기...
                saveWebServiceLog(currentDateTime, errorObject.getErrorString(), el.getMeterNo());

                //done
            }
        }else{
            //Null Exception
            ErrorObject eo = new ErrorObject();
            eo.setEventTime(eventTime);
            eo.setNounType("");
            eo.setErrorString("Request has no ElectricMeters object.");
            resp.getErrorObject().add(eo);
            saveWebServiceLog(currentDateTime, eo.getErrorString(), "-");
        }

        return resp;
    }


    public boolean saveWebServiceLog(String openTime, String serverRequsteMsg, String objectSerial){
        TransactionStatus txstatus = null;

        try{
            txstatus = txmanager.getTransaction(null);
            Code code = codeDao.getCodeIdByCodeObject("7.13.25"); //meter

            WebServiceLog webServiceLog = new WebServiceLog();
            //fixed
            webServiceLog.setObjectType(code);
            webServiceLog.setWebServiceType("MDMS_SOAP");
            webServiceLog.setSeq(0);
            //param
            webServiceLog.setTrId(openTime+"_"+objectSerial);
            webServiceLog.setOpenTime(openTime);
            webServiceLog.setObjectSerial(objectSerial);
            webServiceLog.setServerRequsteMsg(serverRequsteMsg);

            //add
            webServiceLogDao.add(webServiceLog);
            txmanager.commit(txstatus);
            log.debug("## saveWebServiceLog done.");
            return true;
        }catch (Exception e){
            log.error("## Exception on saveWebServiceLog: [" + e.getMessage() + "]");
            if(txstatus != null){
                txmanager.rollback(txstatus);
            }
            return false;
        }

    }

}
