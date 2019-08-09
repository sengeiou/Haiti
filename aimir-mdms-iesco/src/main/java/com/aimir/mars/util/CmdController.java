package com.aimir.mars.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.CircuitBreakerCondition;
import com.aimir.constants.CommonConstants.CircuitBreakerStatus;
import com.aimir.constants.CommonConstants.CommandType;
import com.aimir.constants.CommonConstants.GroupType;
import com.aimir.constants.CommonConstants.McuType;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.OperatorType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.constants.CommonConstants.TR_OPTION;
import com.aimir.constants.CommonConstants.TR_STATE;
import com.aimir.dao.device.MMIUDao;
import com.aimir.fep.meter.data.MeterData;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.DLMS_CLASS;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.DLMS_CLASS_ATTR;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.OBIS;
import com.aimir.fep.protocol.smsp.SMSConstants;
import com.aimir.fep.protocol.smsp.SMSConstants.COMMAND_TYPE;
import com.aimir.fep.protocol.smsp.command.frame.sms.ResponseFrame;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.AsyncCommandLog;
import com.aimir.model.device.AsyncCommandParam;
import com.aimir.model.device.AsyncCommandResult;
import com.aimir.model.device.MMIU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Operator;
import com.aimir.model.system.Role;
import com.aimir.model.system.Supplier;
import com.aimir.schedule.command.CmdOperationUtil;
import com.aimir.service.device.AsyncCommandLogManager;
import com.aimir.service.device.MeterManager;
import com.aimir.service.device.ModemManager;
import com.aimir.service.device.OperationLogManager;
import com.aimir.service.system.CircuitBreakerManager;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.ContractManager;
import com.aimir.service.system.OperatorManager;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.sf.json.JSONArray;

@Service
@Transactional
public class CmdController {
    private static Log log = LogFactory.getLog(CmdController.class);
    private static String loginId = "integration";
    private Object resMonitor = new Object();

    @Autowired
    MMIUDao mmiuDao;
    @Autowired
    CodeManager codeManager;
    @Autowired
    MeterManager meterManager;
    @Autowired
    ModemManager modemManager;
    @Autowired
    ContractManager contractManager;
    @Autowired
    OperationLogManager operationLogManager;
    @Autowired
    AsyncCommandLogManager asyncCommandLogManager;
    @Autowired
    CircuitBreakerManager circuitBreakerManager;
    @Autowired
    private OperatorManager operatorManager;
    @Autowired
    private CmdOperationUtil cmdOperationUtil;
    @Autowired
    HibernateTransactionManager transactionManager;

    protected boolean commandAuthCheck(String pLoginId, CommandType cmdType,
            String command) {

        Operator operator = operatorManager.getOperatorByLoginId(pLoginId);
        if (operator == null) {
            return false; // wrong id
        }

        Role role = operator.getRole();
        Set<Code> commands = role.getCommands();
        Code codeCommand = null;
        if (role.getCustomerRole() != null && role.getCustomerRole()) {
            return false; // 고객 권한이면
        }

//      DELETE START SP-198
//      if (role.getMtrAuthority().equals("c")) {
//          return true; // 관리자면
//      }
//      DELETE END   SP-198
        
        for (Iterator<Code> i = commands.iterator(); i.hasNext();) {
            codeCommand = (Code) i.next();
            if (codeCommand.getCode().equals(command))
                return true; // 관리자가 아니라도 명령에 대한 권한이 있으면
        }

        return false;
    }

    public Map<String, Object> cmdOnDemand(String target, String fromDate,
            String toDate, String type) {
        ResultStatus status = ResultStatus.FAIL;
        Map<String, Object> mav = new HashMap<String, Object>();

        String checkCode = null;
        if ("MCU".equals(type)) {
            checkCode = "8.1.1";
        } else if ("MODEM".equals(type)) {
            checkCode = "8.1.2";
        } else if ("METER".equals(type)) {
            checkCode = "8.1.3";
        } else {
            status = ResultStatus.INVALID_PARAMETER;
            mav.put("rtnStr", "Invalid Type!");
            mav.put("detail", null);
            mav.put("rawMap", null);
            return mav;
        }

        if (!commandAuthCheck(loginId, CommandType.DeviceRead, checkCode)) {
            mav.put("rtnStr", "No permission");
            mav.put("detail", null);
            mav.put("rawMap", null);
            return mav;
        }

        if (target == null || "".equals(target)) {
            status = ResultStatus.INVALID_PARAMETER;
            mav.put("rtnStr", "Target ID null!");
            mav.put("detail", null);
            mav.put("rawMap", null);
            return mav;
        }

        Map<?, ?> result = null;
        String rtnStr = "";
        String detail = "";
        MeterData.Map rawMap = null;
        Meter meter = meterManager.getMeter(target);
        //Modem modem = modemManager.getModem(meter.getModemId());
        Modem modem = meter.getModem();
        Supplier supplier = meter.getSupplier();
        if (supplier == null) {
            Operator operator = operatorManager.getOperatorByLoginId(loginId);
            supplier = operator.getSupplier();
        }

        boolean isSMSModem = isSMSModem(modem);

        try {
            // 프로토콜확인
            String nOption = "";
            if (modem != null) {
                //modem = modemManager.getModem(modem.getId());
                if((modem.getModemType() == ModemType.MMIU) && (modem.getProtocolType() == Protocol.SMS)) {
                //if( (modem instanceof MMIU) && (modem.getProtocolType() == Protocol.SMS) ) {
                    if (fromDate != null && toDate != null) {
                        if (type == null ||  "METER".equals(type) ) {
                            mav = onDemandMeterBypassMBB(meter.getMdsId(), fromDate, toDate);
                            log.info("send sms command : onDemandMeterBypassMBB");
                        }
                        else if ( "MODEM".equals(type)){
                            mav = romReadBypassMBB(meter.getMdsId(), fromDate, toDate);
                            log.info("send sms command : romReadBypassMBB");
                        }
                    } else {
                        mav = onDemandMeterBypassMBB(meter.getMdsId(), fromDate, toDate);
                        log.info("send sms command : onDemandMeterBypassMBB");
                    }

                    return mav;
                } else {
                    if (fromDate != null && toDate != null) {
                        // UPDATE SP-179
                        //result = cmdOperationUtil.doOnDemand(meter, 0, loginId, nOption, fromDate, toDate);
                        if (type == null ||  "METER".equals(type) ) {
                            result = cmdOperationUtil.doOnDemand(meter, 0, loginId, nOption, fromDate, toDate);
                        } else if ("MCU".equals(type)){
                            // UPDATE START SP-633
                            //result = cmdOperationUtil.cmdGetMeteringData(meter, 0, loginId, nOption, fromDate, toDate);
                            result = cmdOperationUtil.cmdGetMeteringData(meter, 0, loginId, nOption, fromDate, toDate, null);
                            // UPDATE END SP-633
                        }else if ("MODEM".equals(type)){
                            // UPDATE START SP-759
                            if (modem.getModemType() == ModemType.MMIU && 
                                    (modem.getProtocolType() == Protocol.IP || modem.getProtocolType() == Protocol.GPRS)){
                                result = cmdOperationUtil.cmdGetROMRead(meter, 0, loginId, nOption, fromDate, toDate);
                            }else{
                                // UPDATE START SP-632
                                //result = cmdOperationUtil.cmdGetROMRead(meter, 0, "admin", nOption, fromDate, toDate);
                                result = cmdOperationUtil.cmdDmdNiGetRomRead(meter, 0, loginId, fromDate, toDate);
                                // UPDATE END SP-632
                            }
                         // UPDATE END SP-759
                        }
                    } else {
                        result = cmdOperationUtil.doOnDemand(meter, 0, loginId, nOption, "", "");
                    }
                }
            }else{
                log.info("no modem object");
                detail = "No Modem Object";
            }

            if (result != null && !isSMSModem) {
                rtnStr = (String) result.get("result");
                detail = (String) result.get("detail");
                if(rtnStr.equals("Success")) status = ResultStatus.SUCCESS; // SP-792
                else status = ResultStatus.FAIL;
                rawMap = (MeterData.Map) result.get("rawMap");
            }

            Code operationCode = null;
            if ("MCU".equals(type)) {
                operationCode = codeManager.getCodeByCode("8.1.1");
            } else if ("MODEM".equals(type)) {
                operationCode = codeManager.getCodeByCode("8.1.2");
            } else if ("METER".equals(type)) {
                operationCode = codeManager.getCodeByCode("8.1.3");
            } else {
                operationCode = codeManager.getCodeByCode("8.1.1");
            }
            
            if (operationCode != null) {
                operationLogManager.saveOperationLog(supplier, meter.getMeterType(),
                        meter.getMdsId(), loginId, operationCode,
                        status.getCode(), rtnStr);
            }
        } catch (Exception e) {
            rtnStr = e.getMessage();
        }
        mav.put("status", status.name());
        mav.put("meterId", meter.getMdsId());
        mav.put("rtnStr", rtnStr);
        mav.put("detail", detail);
        mav.put("rawMap", rawMap);
        return mav;
    }

    private Map onDemandMeterBypassMBB(String mdsId, String fromDate, String toDate) {
        Map<String, Object> mav = new HashMap<String, Object>();
        ResultStatus status = ResultStatus.FAIL;
        Meter meter = null;
        String cmd = "cmdMeterParamGet";
        String detailInfo = "";
        String rtnStr = "";

        JSONArray jsonArr = null;
        try{
            if (loginId != null ){
                if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.1.10")) {
                    throw new Exception("No permission");
                }
            }

            meter = meterManager.getMeter(mdsId);
            int modemPort = 0;
            if ( meter.getModemPort() != null ){
                modemPort = meter.getModemPort().intValue();
            }
            if ( modemPort > 5){
                throw new Exception("ModemPort:" + modemPort + " is not Support");
            }

            Map<String,String> paramMap = new HashMap<String,String>();
            if (modemPort==0) {
                log.debug("cmdGetLoadProfile ["+ mdsId + "][" + modemPort +  "]["  +  fromDate + "][" +toDate +"]");

                String obisCode = this.convertObis(OBIS.ENERGY_LOAD_PROFILE.getCode());
                int classId = DLMS_CLASS.PROFILE_GENERIC.getClazz();
                int attrId = DLMS_CLASS_ATTR.PROFILE_GENERIC_ATTR02.getAttr();

                Map<String,String> valueMap = eventLogValueByRange(fromDate,toDate);
                String value = meterParamMapToJSON(valueMap);

                log.debug("obisCode => " + obisCode + ", classId => " + classId + ", attributeId => " + attrId);

                //paramGet
                paramMap.put("paramGet", obisCode+"|"+classId+"|"+attrId+"|null|null|"+value);              
            }
            else {
                log.debug("cmdGetLoadProfile ["+ mdsId + "][" + modemPort +  "]["  +  fromDate + "][" +toDate +"]");

                String obisCode = this.convertObis(OBIS.MBUSMASTER_LOAD_PROFILE.getCode());
                int classId = DLMS_CLASS.PROFILE_GENERIC.getClazz();
                int attrId = DLMS_CLASS_ATTR.PROFILE_GENERIC_ATTR02.getAttr();

                Map<String,String> valueMap = eventLogValueByRange(fromDate,toDate);
                String value = meterParamMapToJSON(valueMap);

                log.debug("obisCode => " + obisCode + ", classId => " + classId + ", attributeId => " + attrId);

                //paramGet
                paramMap.put("paramGet", obisCode+"|"+classId+"|"+attrId+"|null|null|"+value);
            }
            paramMap.put("option", "ondemandmbb");

            Map<String,Object> map = new HashMap<String,Object>();
            try{
                if(meter != null && meter.getModem() != null) {
                    Modem modem = meter.getModem();
                    if(modem.getModemType() == ModemType.MMIU && (modem.getProtocolType() == Protocol.SMS 
                            || modem.getProtocolType() == Protocol.IP 
                            || modem.getProtocolType() == Protocol.GPRS)) {
                        Map<String,Object> condition = new HashMap<String,Object>();
                        condition.put("modemId", meter.getModemId());
                        condition.put("modemType", ModemType.MMIU.toString());
                        MMIU mmiu = (MMIU)modemManager.getModemByType(condition);

                        map.put("meterId", mdsId);
                        map.put("modemType", meter.getModem().getModemType().name());
                        map.put("protocolType", meter.getModem().getProtocolType());
                        map.put("modem", mmiu);
                    }
                } else {
                    rtnStr = "FAIL : Target ID null!";
                }
            }catch(Exception e) {
                log.warn(e,e);
                rtnStr = "FAIL : Target ID null!";
            }

            try{
                if(map.get("modemType") == ModemType.MMIU.name() && map.get("protocolType") == Protocol.SMS) {
                    MMIU mmiu = (MMIU)map.get("modem");

                    String mobileNo = mmiu.getPhoneNumber();
                    if (mobileNo == null || "".equals(mobileNo)) {
                        log.warn(String.format("[" + cmd + "] Phone number is empty"));
                        rtnStr = "FAIL : Phone number is empty!";
                    }
                    else if (!Protocol.SMS.equals(mmiu.getProtocolType())) {
                        log.warn(String.format("[" + cmd + "] Invalid ProtocolType"));
                        rtnStr = "FAIL : Invalid ProtocolType!";
                    }
                    else {
                        Long trId = System.currentTimeMillis();
                        Map<String, String> result;
                        String cmdResult = "";

                        result = sendSmsForCmdServer(mmiu, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), cmd, paramMap);
                        if (result != null) {
                            cmdResult = "Success";

                            status = ResultStatus.SUCCESS;
                            detailInfo = result.get("detail").toString();

                            Map<String, String> tmpMap = parseDetailMessageForMBB(
                                    detailInfo);
                            mav.put("rawMap", tmpMap);

                            if (meter.getModem() != null) {
                                meter.getModem().setCommState(1);
                            }
                        } else {
                            log.debug("SMS Fail");
                            //cmdResult="Failed to get the resopone. See the Async Command History.";
                            cmdResult = "Check the Async_Command_History.";
                        }

                        rtnStr = cmdResult;
                    }
                } 
            } catch (Exception e) {
                log.error(e, e);
                rtnStr = "Check the Async_Command_History";
            }

        } catch (Exception e) {
            log.error(e, e);
            rtnStr = "FAIL : " + e.getMessage();
        }

        if ( meter != null ){
            Code operationCode = codeManager.getCodeByCode("8.1.10");
            if (operationCode != null) {
                operationLogManager.saveOperationLog(meter.getSupplier(), 
                        meter.getMeterType(), meter.getMdsId(), loginId,
                        operationCode, status.getCode(), status.name());
            }

        }

        mav.put("status", status.name());
        mav.put("meterId", meter.getMdsId());
        mav.put("detail", detailInfo);        
        mav.put("rtnStr", rtnStr);
        return mav;
    }

    private Map<String,String> parseDetailMessageForMBB(String detail) {
        Map<String,String> map = new LinkedHashMap<String,String>();
        BufferedReader br = new BufferedReader(new StringReader(detail));
        String b = null;
        try {
            b = br.readLine();
            while(b!=null) {
                System.out.println(b);
                if(!b.trim().equals("")) {
                    String[] c = b.split(":");
                    if(c.length>1) {
                        String [] d = b.split(" ");
                        String tempKey = d[0] +" " + d[1] + " " + d[2] + " " + d[3] + " " + d[4];
                        String tempValue = b.replaceAll(tempKey, "").trim();
                        map.put(tempKey,tempValue);
                    } else {
                        if(b.startsWith("Cumulative active energy -import")) {
                            map.put("Cumulative active energy -import", b.replaceAll("Cumulative active energy -import", "").trim());
                        } else if(b.startsWith("Cumulative active energy -export")) {
                            map.put("Cumulative active energy -export", b.replaceAll("Cumulative active energy -export", "").trim());
                        } else if(b.startsWith("Cumulative reactive energy -import")) {
                            map.put("Cumulative reactive energy -import", b.replaceAll("Cumulative reactive energy -import", "").trim());
                        } else if(b.startsWith("Cumulative reactive energy -export")) {
                            map.put("Cumulative reactive energy -export", b.replaceAll("Cumulative reactive energy -export", "").trim());
                        }
                    }
                }
                b = br.readLine();
            }
        } catch (IOException e) {
            log.error(e,e);
        }
        return map;
    }

    private Map<String,String> eventLogValueByRange(String fromDate, String toDate) throws Exception {
        Map<String,String> valueMap = new HashMap<String,String>();

        String clockObis = this.convertObis(OBIS.CLOCK.getCode());
        String option="1";  //option 0 is offset, option 1 is range_descriptor(date). but not yet implement offset.

        valueMap.put("clockObis", clockObis);
        valueMap.put("option", option);
        Calendar fromCal = null;
        if (fromDate != null && !fromDate.equals("")) {
            fromCal = DateTimeUtil.getCalendar(fromDate);
        } else {
            fromCal = Calendar.getInstance();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        fromDate = formatter.format(fromCal.getTime());
        valueMap.put("fYear", fromDate.substring(0,4));
        valueMap.put("fMonth", fromDate.substring(4,6));
        valueMap.put("fDayOfMonth", fromDate.substring(6,8));
        valueMap.put("fDayOfWeek", String.valueOf(fromCal.get(Calendar.DAY_OF_WEEK)));
        valueMap.put("fHh", fromDate.substring(8,10));
        valueMap.put("fMm", fromDate.substring(10,12));
        valueMap.put("fSs", fromDate.substring(12,14));
        
        Calendar toCal = null;
        if (toDate != null && !toDate.equals("")) {
            toCal = DateTimeUtil.getCalendar(toDate);
        } else {
            toCal = Calendar.getInstance();
        }
        toDate = formatter.format(toCal.getTime());
        
        valueMap.put("tYear", toDate.substring(0,4));
        valueMap.put("tMonth", toDate.substring(4,6));
        valueMap.put("tDayOfMonth", toDate.substring(6,8));
        valueMap.put("tDayOfWeek", String.valueOf(toCal.get(Calendar.DAY_OF_WEEK)));
        valueMap.put("tHh", toDate.substring(8,10));
        valueMap.put("tMm", toDate.substring(10,12));
        valueMap.put("tSs", toDate.substring(12,14));

        return valueMap;
    }

    private String meterParamMapToJSON(Map map) {
        StringBuffer rStr = new StringBuffer();
        Iterator<String> keys = map.keySet().iterator();
        String keyVal = null;
        rStr.append("[{");
        while (keys.hasNext()) {
            keyVal = (String) keys.next();
            rStr.append("\""+keyVal+"\":");
            rStr.append("\""+map.get(keyVal)+"\"");
            if (keys.hasNext()) {
                rStr.append(",");
            }
        }
        rStr.append("}]");
        return rStr.toString();
    }

    private Map romReadBypassMBB(String mdsId, String fromDate, String toDate) {
        Map<String, Object> mav = new HashMap<String, Object>();
        ResultStatus status = ResultStatus.FAIL;
        Meter meter = null;
        String cmd = "cmdGetROMRead";

        meter = meterManager.getMeter(mdsId);
        Modem modem = meter.getModem();

        if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.1.10")) {
            mav.put("rtnStr", "No permission");
            mav.put("status", status.name());
            mav.put("detail", "");
            return mav;
        }

        if ( modem == null ){
            mav.put("rtnStr", "Target ID null!");
            mav.put("status", status.name());
            mav.put("detail", "");
            return mav;
        }

        try{
            if( ((modem.getModemType() == ModemType.MMIU) && (modem.getProtocolType() == Protocol.SMS)) ){
                Map<String, String> asyncResult = new HashMap<String, String>();
                Map<String, String> paramMap = new HashMap<String, String>();

                fromDate = (fromDate == null || "".equals(fromDate)) ? TimeUtil
                        .getCurrentDay()
                        : fromDate;
                toDate = (toDate == null || "".equals(toDate)) ? TimeUtil
                        .getCurrentDay()
                        : toDate;

                paramMap.put("meterId", mdsId);
                paramMap.put("fromDate",fromDate);
                paramMap.put("toDate", toDate);

                asyncResult = sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), cmd, paramMap);

                if(asyncResult != null){
                    status = ResultStatus.SUCCESS;
                }else{
                    log.debug("SMS Fail");
                    mav.put("rtnStr", "Check the results in Async History tab");
                    mav.put("status", status.name());
                    mav.put("detail", "");
                    return mav;
                }
                // SUCCESS
                for (String key : asyncResult.keySet()) {
                    if ( key.equals("detail") ){
                        //// Convert escape char
                        String value = asyncResult.get(key).toString() ;
                        ObjectMapper mapper = new ObjectMapper();
                        String json = "{\"key\":\""+ value + "\"}";
                        Map<String, Object> tmpMap = new HashMap<String, Object>();
                        tmpMap = mapper.readValue(json, new TypeReference<Map<String, String>>(){});
                        log.debug("detail[" +(String)tmpMap.get("key") +"]" );
                        mav.put(key, (String)tmpMap.get("key"));
                    }
                    else {
                        mav.put(key, asyncResult.get(key).toString());
                    }
                }
                mav.put("status", status.name());
                return mav;
            }
            else {
                mav.put("rtnStr", "Invalid Type!");
                mav.put("status", status.name());
                mav.put("detail", "");
                return mav;
            }
        }catch(Exception e){
            log.debug(e,e);
            mav.put("rtnStr", "Check the results in Async History tab");
            mav.put("status", status.name());
            return mav;
        }
    }

    public Map<String, Object> cmdRemoteGetStatus(String target, String mcuId) {

        ResultStatus status = ResultStatus.FAIL;
        Map<String, Object> mav = new HashMap<String, Object>();

        if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.1.4")) {
            mav.put("rtnStr", "No permission");
            return mav;
        }
        if (target == null || "".equals(target)) {
            status = ResultStatus.INVALID_PARAMETER;
            mav.put("rtnStr", "Target ID null!");
            return mav;
        }
        String rtnStr = "";

        Meter meter = meterManager.getMeter(target);
        Modem modem = meter.getModem();
        Supplier supplier = meter.getSupplier();
        if (supplier == null) {
            Operator operator = operatorManager.getOperatorByLoginId(loginId);
            supplier = operator.getSupplier();
        }

        boolean isSMSModem = isSMSModem(modem);

        try {
            status = ResultStatus.SUCCESS;

            Map<String, Object> resultMap = null;
            // MBB(SMS)
            // -> UPDATE START 2016/09/14 SP-117
            //if( ((modem instanceof MMIU) && (modem.getProtocolType() == Protocol.SMS)) ){
            if( ((modem.getModemType() == ModemType.MMIU) && (modem.getProtocolType() == Protocol.SMS)) ){
                String loadControlStatusString = "";
                String loadControlModeString = "";
                String relayStatusString = "";
                String failReasonString = "";

                // <- UPDATE END   2016/09/14 SP-117
                // Map<String, String> asyncResult = new HashMap<String, String>();
                rtnStr = "Check the results in Async History tab"; // INSERT 2016/09/21 SP-117
                mav.put("jsonString", rtnStr); // INSERT 2016/09/21 SP-117
                try{ 
                    Map<String, String> paramMap = new HashMap<String, String>();
                    paramMap.put("mcuId", mcuId);
                    paramMap.put("meterId", meter.getMdsId());

                    // -> UPDATE START 2016/09/12 SP-117
                    // resultMap = sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), "relayValveStatus", paramMap);
                    // Map<String, Object> resultMap2 = null; // DELETE 2016/09/20 SP-117
                    String param    = null;
                    String obisCode = this.convertObis(OBIS.RELAY_STATUS.getCode());
                    int    classId  = DLMS_CLASS.RELAY_CLASS.getClazz();
                    int    attrId   = DLMS_CLASS_ATTR.REGISTER_ATTR02.getAttr();
                    param           = obisCode+"|"+classId+"|"+attrId+"|RO|Boolean|";

                    paramMap.put("paramGet", param);
                    paramMap.put("option"  , "relaystatusall");
                    // -> UPDATE START 2016/09/20 SP-117
                    // resultMap2 = sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), "cmdMeterParamGet", paramMap);

                    // if( (Boolean)resultMap2.get("RelayStatus") == true ) {
                    //    resultMap.put( "Relay Status", RELAY_STATUS_KAIFA.Connected );
                    //    updateMeterStatusNormal(meter);
                    // }
                    // else {
                    //    resultMap.put( "Relay Status", RELAY_STATUS_KAIFA.Disconnected );
                    //    updateMeterStatusCutOff(meter);
                    // }
                
                    // Set [LoadControlStatus]
                    // if( (Integer)resultMap2.get("LoadControlStatus") == CONTROL_STATE.Disconnected.ordinal() ) {
                    //    // updateMeterStatusCutOff(meter);
                    //  resultMap.put( "LoadControlStatus", CONTROL_STATE.Disconnected );
                    // }
                    // else if( (Integer)resultMap2.get("LoadControlStatus") == CONTROL_STATE.Connected.ordinal() ) {
                    //    // updateMeterStatusNormal(meter);
                    //    resultMap.put( "LoadControlStatus", CONTROL_STATE.Connected );
                    // }
                    // else if( (Integer)resultMap2.get("LoadControlStatus") == CONTROL_STATE.ReadyForReconnection.ordinal() ) {
                    //  resultMap.put( "LoadControlStatus", CONTROL_STATE.ReadyForReconnection );
                    // }
                    
                    // // Set [LoadControlMode]
                    // resultMap.put( "LoadControlMode", resultMap2.get("LoadControlMode") );
                    resultMap = sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), "cmdMeterParamGet", paramMap);
                    // -> UPDATE START 2016/09/20 SP-117
                    // <- UPDATE START 2016/09/12 SP-117

                    log.debug("resultMap : " + resultMap);
                    if(resultMap != null){
                        status = ResultStatus.SUCCESS;
                        relayStatusString = (String) resultMap.get("Relay Status");
                        loadControlStatusString = (String) resultMap.get("LoadControlStatus");
                        loadControlModeString = (String) resultMap.get("LoadControlMode");
                    }else{
                        log.debug("SMS Fail");
                        status = ResultStatus.FAIL;
                    }
                }catch(Exception e){
                    log.debug("SMS Fail", e);
                    status = ResultStatus.FAIL;
                }

                if (status != ResultStatus.FAIL) {
                    rtnStr = "Relay Status = " + relayStatusString
                            + ", Load Control Status = " + loadControlStatusString
                            + ", Load Control Mode = " + loadControlModeString;
                    mav.put("rtnStr", rtnStr);
                }

                mav.put("RelayStatus", relayStatusString);
                mav.put("LoadControlStatus", loadControlStatusString);
                mav.put("LoadControlMode", loadControlModeString);

            } else { 
                // RF or Ethernet   

                resultMap = cmdOperationUtil
                        .relayValveStatus(mcuId, meter.getMdsId());

                String loadControlStatusString = "";
                String loadControlModeString = "";
                String relayStatusString = "";
                String failReasonString = "";
                try {
                    String responseJson = (String) resultMap.get("Response");
                    log.debug("Reponse:" + responseJson);
                    JsonParser jsonParser = new JsonParser();
                    JsonElement element = jsonParser.parse(responseJson);
                    if (element.isJsonArray()) {
                        for (JsonElement e : element.getAsJsonArray()) {
                            JsonObject jobj = e.getAsJsonObject();
                            if (jobj.get("name") != null) {
                                String name = jobj.get("name").getAsString();
                                if ("failReason".equals(name)) {
                                    failReasonString = jobj.get("value")
                                            .getAsString();
                                } else if ("Relay Status".equals(name)) {
                                    relayStatusString = jobj.get("value")
                                            .getAsString();
                                } else if ("LoadControlStatus".equals(name)) {
                                    loadControlStatusString = jobj.get("value")
                                            .getAsString();
                                } else if ("LoadControlMode".equals(name)) {
                                    loadControlModeString = jobj.get("value")
                                            .getAsString();
                                }
                            }
                        }
                    } else {
                        log.debug(element.getClass());
                    }
                } catch (Exception e) {
                    log.debug(e, e);
                }
                Object[] values = resultMap.values().toArray(new Object[0]);

                for (Object o : values) {
                    log.debug((String) o);
                    // rtnStr += (String) o + " \n";
                    rtnStr = loadControlStatusString;

                    if (((String) o).contains("failReason")) {
                        status = ResultStatus.FAIL;
                        rtnStr = "FAIL : " + failReasonString;
                        break;
                    }

                    if (isSMSModem) {
                        if (((String) o).contains("SUCCESS")) {
                            // 상태가 바뀌는 시간을 기다려주기 위해 60초 sleep
                            Thread.sleep(60000);
                            Integer lastStatus = asyncCommandLogManager.getCmdStatus(
                                    modem.getDeviceSerial(), "cmdRelayStatus");
                            if (TR_STATE.Success.getCode() != lastStatus) {
                                status = ResultStatus.FAIL;
                                rtnStr = "FAIL : Communication Error(RelayStatus)\n";
                            } else {
                                status = ResultStatus.SUCCESS;

                                /**
                                 * Transaction ID가 FEP에서 생성되는 방식이어서 정확한 Transaction
                                 * ID를 알수가 없다. 그래서 마지막 Transaction번호의 Parameter를
                                 * 가져오는 방식으로 구현되었으며 이는 다른 값을 가져올 확률이 있다.
                                 */
                                List<AsyncCommandParam> acplist = asyncCommandLogManager
                                        .getCmdParamsByTrnxId(
                                                modem.getDeviceSerial(), null);
                                if (acplist == null || acplist.size() <= 0) {
                                    rtnStr = "RESULT_STATUS=Empty~!!";
                                } else {
                                    rtnStr += "Result = ";
                                    for (AsyncCommandParam param : acplist) {
                                        rtnStr += param.getParamType()
                                                .equals("RESULT_STATUS")
                                                        ? param.getParamValue()
                                                        : "" + "\n";
                                    }
                                }
                                log.debug(
                                        "cmdRelayStatus returnValue =>> " + rtnStr);
                            }
                            break;
                        } else {
                            status = ResultStatus.FAIL;
                        }
                    }
                }

                if (meter.getModel() != null
                        && meter.getModel().getName().indexOf("LS") >= 0) {
                    String open = "[" + "{\"name\":\"" + "LoadControlStatus"
                            + "\",\"value\":\"" + "OPEN" + "\"}" + "]";
                    String close = "[" + "{\"name\":\"" + "LoadControlStatus"
                            + "\",\"value\":\"" + "CLOSE" + "\"}" + "]";
                    if (rtnStr.indexOf(open) >= 0) {
                        rtnStr = "Internal relay is OPEN.";
                    } else if (rtnStr.indexOf(close) >= 0) {
                        rtnStr = "Internal relay is CLOSED.";
                    }
                } else if (status != ResultStatus.FAIL) {
                    rtnStr = "Relay Status = " + relayStatusString
                            + ", Load Control Status = " + loadControlStatusString
                            + ", Load Control Mode = " + loadControlModeString;
                }

                mav.put("RelayStatus", relayStatusString);
                mav.put("LoadControlStatus", loadControlStatusString);
                mav.put("LoadControlMode", loadControlModeString);
            }

        } catch (Exception e) {
            log.error(e, e);
            rtnStr = e.getMessage();
            status = ResultStatus.FAIL;
        }

        /**
         * 2014.07.03 simhanger Relay Status : Relay Switch로 남겨지던 로그를 Relay
         * Status로 남겨지도록 수정함
         * 
         * Code operationCode = codeManager.getCodeByCode("8.1.4");
         */
        Code operationCode = codeManager.getCodeByCode("8.1.4");

        Code meterTypeCode = codeManager.getCode(meter.getMeterTypeCodeId());
        if (operationCode != null) {
            operationLogManager.saveOperationLog(supplier, meterTypeCode, meter.getMdsId(), loginId,
                    operationCode, status.getCode(), status.name());
        }
        // meterDao.updateMeter(meter);
        mav.put("status", status.name());
        mav.put("meterId", meter.getMdsId());
        mav.put("rtnStr", rtnStr);
        return mav;
    }

    public Map<String, Object> cmdRemotePowerOff(String target, String mcuId) {

        ResultStatus status = ResultStatus.FAIL;
        Map<String, Object> mav = new HashMap<String, Object>();

        if (!commandAuthCheck(loginId, CommandType.DeviceWrite, "8.1.6")) {
            mav.put("rtnStr", "No permission");
            return mav;
        }
        if (target == null || "".equals(target)) {
            status = ResultStatus.INVALID_PARAMETER;
            mav.put("rtnStr", "Target ID null!");
            return mav;
        }
        String rtnStr = "";
        Meter meter = meterManager.getMeter(target);
        //Modem modem = modemManager.getModem(meter.getModemId());
        Modem modem = meter.getModem();
        Supplier supplier = meter.getSupplier();
        if (supplier == null) {
            Operator operator = operatorManager.getOperatorByLoginId(loginId);
            supplier = operator.getSupplier();
        }
 
        boolean isSMSModem = isSMSModem(modem);

        try {
            status = ResultStatus.SUCCESS;
            Map<String, Object> resultMap = null;

            String loadControlStatusString = "";
            String relayStatusString = "";
            String failReasonString = "";
            String cmdStatusString = "";

            // MBB(SMS)
            // -> UPDATE START 2016/09/14 SP-117
            //if( ((modem instanceof MMIU) && (modem.getProtocolType() == Protocol.SMS)) ){
            if( ((modem.getModemType() == ModemType.MMIU) && (modem.getProtocolType() == Protocol.SMS)) ){
                // <- UPDATE END   2016/09/14 SP-117
                // Map<String, String> asyncResult = new HashMap<String, String>();
                rtnStr = "Check the results in Async History tab"; // INSERT 2016/09/21 SP-117
                mav.put("jsonString", rtnStr); // INSERT 2016/09/21 SP-117
                try{ 
                    Map<String, String> paramMap = new HashMap<String, String>();
                    paramMap.put("mcuId", mcuId);
                    paramMap.put("meterId", meter.getMdsId());

                    // -> UPDATE START 2016/09/12 SP-117
                    // resultMap = sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), "relayValveOff", paramMap);
                    String param    = null;
                    String obisCode = this.convertObis(OBIS.RELAY_STATUS.getCode());
                    int    classId  = DLMS_CLASS.RELAY_CLASS.getClazz();
                    int    attrId   = DLMS_CLASS_ATTR.REGISTER_ATTR02.getAttr();
                    param           = obisCode+"|"+classId+"|"+attrId+"|ACTION|Boolean|false";

                    paramMap.put("paramAct", param);
                    resultMap = sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), "cmdMeterParamAct", paramMap);
                    // <- UPDATE START 2016/09/12 SP-117

                    if(resultMap != null){
                        // -> DELETE START 2016/09/20 SP-117
                        // // -> INSERT START 2016/09/12 SP-117
                        // if( (Boolean)resultMap.get("value") == true ) {
                        //    resultMap.put( "Relay Status", RELAY_STATUS_KAIFA.Connected );
                        // }
                        // else {
                        //    resultMap.put( "Relay Status", RELAY_STATUS_KAIFA.Disconnected );
                        //    updateMeterStatusCutOff( meter );
                        // }
                        // // <- INSERT END   2016/09/12 SP-117
                        // <- DELETE END   2016/09/20 SP-117
                        status = ResultStatus.SUCCESS;
                    }else{
                        log.debug("SMS Fail");
                        // mav.addObject("rtnStr", "Check the results in Async History tab"); // DELETE 2016/09/21 SP-117
                        status = ResultStatus.FAIL;
                    }
                }catch(Exception e){
                    log.debug("SMS Fail");
                    mav.put("rtnStr", "Check the results in Async History tab");
                    status = ResultStatus.FAIL;
                }

            // RF or Ethernet   
            }else{ 

                resultMap = cmdOperationUtil.relayValveOff(mcuId, meter.getMdsId());

                try {
                    String responseJson = (String)resultMap.get("Response");
                    log.debug("Reponse:" + responseJson);
                    JsonParser jsonParser = new JsonParser();
                    JsonElement element =  jsonParser.parse(responseJson);
                    if ( element.isJsonArray()){
                        for (JsonElement e : element.getAsJsonArray()) {
                            JsonObject jobj = e.getAsJsonObject();
                            if ( jobj.get("name") != null  ){
                                String name = jobj.get("name").getAsString();
                                if ( "failReason".equals(name)){
                                    failReasonString = jobj.get("value").getAsString();
                                }
                                else if ( "Relay Status".equals(name)){
                                    relayStatusString = jobj.get("value").getAsString();
                                }
                                else if ( "LoadControlStatus".equals(name)){
                                    loadControlStatusString = jobj.get("value").getAsString();
                                }
                                else if ( "RESULT_VALUE".equals(name)){
                                    cmdStatusString = jobj.get("value").getAsString();
                                }
                            }
                        }
                    }
                    else {
                        log.debug(element.getClass());
                    }
                }catch (Exception e){
                    log.debug(e,e);
                }

                Object[] values = resultMap.values().toArray(new Object[0]);
                for (Object o : values) {
                    log.debug((String)o);
                    //rtnStr += (String) o + " \n";
                    rtnStr = loadControlStatusString;

                    if (((String) o).contains("failReason")) {
                        status = ResultStatus.FAIL;
                        rtnStr = "FAIL : " + failReasonString;
                        break;
                    }

                    if (isSMSModem) {
                        if (((String) o).contains("SUCCESS")) {
                            // 상태가 바뀌는 시간을 기다려주기 위해 60초 sleep
                            Thread.sleep(60000);
                            Integer lastStatus = asyncCommandLogManager.getCmdStatus(modem.getDeviceSerial(),
                                    "cmdRelayDisconnect");
                            if (TR_STATE.Success.getCode() != lastStatus) {
                                status = ResultStatus.FAIL;
                                rtnStr = "FAIL : Communication Error(RelayOff)\n";
                            } else {
                                status = ResultStatus.SUCCESS;

                                /**
                                 * Transaction ID가 FEP에서 생성되는 방식이어서 정확한 Transaction
                                 * ID를 알수가 없다. 그래서 마지막 Transaction번호의 Parameter를
                                 * 가져오는 방식으로 구현되었으며 이는 다른 값을 가져올 확률이 있다.
                                 */
                                List<AsyncCommandParam> acplist = asyncCommandLogManager
                                        .getCmdParamsByTrnxId(modem.getDeviceSerial(), null);
                                if (acplist == null || acplist.size() <= 0) {
                                    rtnStr = "RESULT_DISCONNECT=Empty~!!";
                                } else {
                                    rtnStr += "Result = ";
                                    for (AsyncCommandParam param : acplist) {
                                        rtnStr += param.getParamType().equals("RESULT_DISCONNECT") ? param.getParamValue()
                                                : "" + "\n";
                                    }
                                }
                                log.debug("cmdRelayDisconnect returnValue =>> " + rtnStr);
                            }
                            break;
                        } else {
                            status = ResultStatus.FAIL;
                        }
                    }
                }

                if (meter.getModel() != null && meter.getModel().getName().indexOf("LS") >= 0) {
                    String open = "{\"name\":\"" + "LoadControlStatus" + "\",\"value\":\"" + "OPEN" + "\"}";
                    String close = "{\"name\":\"" + "LoadControlStatus" + "\",\"value\":\"" + "CLOSE" + "\"}";
                    if (rtnStr.indexOf(open) >= 0) {
                        rtnStr = "Energy supply to the customer is INTERRUPTED!";
                    } else if (rtnStr.indexOf(close) >= 0) {
                        rtnStr = "Internal relay is CLOSED.";
                    }
                }else if(status != ResultStatus.FAIL) {
                    rtnStr = "Command Status = " + cmdStatusString + ", Relay Status = " + relayStatusString ;
                }
            }

            mav.put("RelayStatus", relayStatusString);
            mav.put("LoadControlStatus", loadControlStatusString);
            mav.put("FailReason", failReasonString);

        } catch (Exception e) {
            rtnStr = e.getMessage();
            status = ResultStatus.FAIL;
            log.error(e, e);
        }

        /**
         * 2014.07.03 simhanger Relay Off : Relay Switch로 남겨지던 로그를 Relay Off로
         * 남겨지도록 수정함
         * 
         * Code operationCode = codeManager.getCodeByCode("8.1.4");
         */
        Code operationCode = codeManager.getCodeByCode("8.1.6");
        Code meterTypeCode = codeManager.getCode(meter.getMeterTypeCodeId());
        if (operationCode != null) {
            operationLogManager.saveOperationLog(supplier, meterTypeCode, meter.getMdsId(), loginId, operationCode,
                    status.getCode(), status.name());
        }

        try {
            //Code meterType = codeDao.getCodeByName("EnergyMeter");
            Code meterType = codeManager.getCodeByCode("1.3.1.1");
            if (meter.getMeterType() == meterType && status == ResultStatus.SUCCESS) {
                circuitBreakerManager.saveSupplyCapacity(CircuitBreakerStatus.Deactivation, GroupType.Meter.name(),
                        CircuitBreakerCondition.Emergency, meter.getId());
            }
        } catch (Exception e) {
            log.warn(e, e);
        }

        // meterDao.updateMeter(meter);
        mav.put("status", status.name());
        mav.put("meterId", meter.getMdsId());
        mav.put("rtnStr", rtnStr);
        return mav;
    }

    public Map<String, Object> cmdRemotePowerOn(String target, String mcuId) {

        ResultStatus status = ResultStatus.FAIL;
        Map<String, Object> mav = new HashMap<String, Object>();

        if (!commandAuthCheck(loginId, CommandType.DeviceWrite, "8.1.5")) {
            mav.put("rtnStr", "No permission");
            return mav;
        }
        if (target == null || "".equals(target)) {
            status = ResultStatus.INVALID_PARAMETER;
            mav.put("rtnStr", "Target ID null!");
            return mav;
        }
        Map<?, ?> result = null;
        String rtnStr = "";
        String relayStatus = "";
        Meter meter = meterManager.getMeter(target);
        //Modem modem = modemManager.getModem(meter.getModemId());
        Modem modem = meter.getModem();
        Contract contract = contractManager.getContractByMeterId(meter.getId());
        Supplier supplier = meter.getSupplier();
        if (supplier == null) {
            Operator operator = operatorManager.getOperatorByLoginId(loginId);
            supplier = operator.getSupplier();
        }

        boolean isSMSModem = isSMSModem(modem);

        String loadControlStatusString = "";
        String relayStatusString = "";
        String failReasonString = "";
        String cmdStatusString = "";

        try {
            status = ResultStatus.SUCCESS;
            Map<String, Object> resultMap = null;
            // MBB(SMS)
            // -> UPDATE START 2016/09/14 SP-117
            //if( ((modem instanceof MMIU) && (modem.getProtocolType() == Protocol.SMS)) ){
            if( ((modem.getModemType() == ModemType.MMIU) && (modem.getProtocolType() == Protocol.SMS)) ){
                // <- UPDATE END   2016/09/14 SP-117
                // Map<String, String> asyncResult = new HashMap<String, String>();
                rtnStr = "Check the results in Async History tab"; // INSERT 2016/09/21 SP-117
                mav.put("jsonString", rtnStr); // INSERT 2016/09/21 SP-117
                try{ 
                    Map<String, String> paramMap = new HashMap<String, String>();
                    paramMap.put("mcuId", mcuId);
                    paramMap.put("meterId", meter.getMdsId());

                    // -> UPDATE START 2016/09/12 SP-117
                    // resultMap = sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), "relayValveOn", paramMap);
                    String param    = null;
                    String obisCode = this.convertObis(OBIS.RELAY_STATUS.getCode());
                    int    classId  = DLMS_CLASS.RELAY_CLASS.getClazz();
                    int    attrId   = DLMS_CLASS_ATTR.REGISTER_ATTR02.getAttr();
                    param           = obisCode+"|"+classId+"|"+attrId+"|ACTION|Boolean|true";

                    paramMap.put("paramAct", param);
                    resultMap = sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), "cmdMeterParamAct", paramMap);
                    // <- UPDATE START 2016/09/12 SP-117

                    log.debug("resultMap : " + resultMap);
                    if(resultMap != null){
                        // <- INSERT START 2016/09/20 SP-117
                        // // -> INSERT START 2016/09/12 SP-117
                        // if( (Boolean)resultMap.get("value") == true ) {
                        //     resultMap.put( "Relay Status", RELAY_STATUS_KAIFA.Connected );
                        //     updateMeterStatusNormal( meter );
                        // }
                        // else {
                        //     resultMap.put( "Relay Status", RELAY_STATUS_KAIFA.Disconnected );
                        // }
                        // // <- INSERT END   2016/09/12 SP-117
                        // -> INSERT END   2016/09/20 SP-117
                        status = ResultStatus.SUCCESS;
                    }else{
                        log.debug("SMS Fail");
                        // mav.addObject("rtnStr", "Check the results in Async History tab"); // DELETE 2016/09/21 SP-117
                        status = ResultStatus.FAIL;
                    }
                }catch(Exception e){
                    log.debug("SMS Fail");
                    mav.put("rtnStr", "Check the results in Async History tab");
                    status = ResultStatus.FAIL;
                }
            // RF or Ethernet   
            }else{ 
                resultMap = cmdOperationUtil.relayValveOn(mcuId, meter.getMdsId());

                try {
                    String responseJson = (String)resultMap.get("Response");
                    log.debug("Reponse:" + responseJson);
                    JsonParser jsonParser = new JsonParser();
                    JsonElement element =  jsonParser.parse(responseJson);
                    if ( element.isJsonArray()){
                        for (JsonElement e : element.getAsJsonArray()) {
                            JsonObject jobj = e.getAsJsonObject();
                            if ( jobj.get("name") != null  ){
                                String name = jobj.get("name").getAsString();
                                if ( "failReason".equals(name)){
                                    failReasonString = jobj.get("value").getAsString();
                                }
                                else if ( "Relay Status".equals(name)){
                                    relayStatusString = jobj.get("value").getAsString();
                                }
                                else if ( "LoadControlStatus".equals(name)){
                                    loadControlStatusString = jobj.get("value").getAsString();
                                }
                                else if ( "RESULT_VALUE".equals(name)){
                                    cmdStatusString  = jobj.get("value").getAsString();
                                }
                            }
                        }
                    }
                    else {
                        log.debug(element.getClass());
                    }
                }catch (Exception e){
                    log.debug(e,e);
                }
            }
            Object[] values = resultMap.values().toArray(new Object[0]);

            for (Object o : values) {
                log.debug((String)o);
                //rtnStr += (String) o + " \n";
                rtnStr = loadControlStatusString;

                if (((String) o).contains("failReason")) {
                    status = ResultStatus.FAIL;
                    rtnStr = "FAIL : " + failReasonString;
                    break;
                }

                if (isSMSModem) {
                    if (((String) o).contains("SUCCESS")) {
                        // 상태가 바뀌는 시간을 기다려주기 위해 60초 sleep
                        Thread.sleep(60000);
                        Integer lastStatus = asyncCommandLogManager.getCmdStatus(modem.getDeviceSerial(),
                                "cmdRelayReconnect");
                        if (TR_STATE.Success.getCode() != lastStatus) {
                            status = ResultStatus.FAIL;
                            rtnStr = "FAIL : Communication Error(RelayOn)\n";
                        } else {
                            status = ResultStatus.SUCCESS;

                            /**
                             * Transaction ID가 FEP에서 생성되는 방식이어서 정확한 Transaction
                             * ID를 알수가 없다. 그래서 마지막 Transaction번호의 Parameter를
                             * 가져오는 방식으로 구현되었으며 이는 다른 값을 가져올 확률이 있다.
                             */
                            List<AsyncCommandParam> acplist = asyncCommandLogManager
                                    .getCmdParamsByTrnxId(modem.getDeviceSerial(), null);
                            if (acplist == null || acplist.size() <= 0) {
                                rtnStr = "RESULT_RECONNECT=Empty~!!";
                            } else {
                                rtnStr += "Result = ";
                                for (AsyncCommandParam param : acplist) {
                                    rtnStr += param.getParamType().equals("RESULT_RECONNECT") ? param.getParamValue()
                                            : "" + "\n";
                                }
                            }
                            log.debug("cmdRelayReconnect returnValue =>> " + rtnStr);
                        }
                        break;
                    } else {
                        status = ResultStatus.FAIL;
                    }
                }
            }

            if (meter.getModel() != null && meter.getModel().getName().indexOf("LS") >= 0) {
                String open = "{\"name\":\"" + "LoadControlStatus" + "\",\"value\":\"" + "OPEN" + "\"}";
                String close = "{\"name\":\"" + "LoadControlStatus" + "\",\"value\":\"" + "CLOSE" + "\"}";
                if (rtnStr.indexOf(open) >= 0) {
                    rtnStr = "Internal relay is OPEN.";
                } else if (rtnStr.indexOf(close) >= 0) {
                    rtnStr = "Energy supply to the customer is RESUMED!";
                }
            }else if(status != ResultStatus.FAIL) {
                rtnStr = "Command Status = " + cmdStatusString + ", Relay Status = " + relayStatusString ;
            }

            mav.put("RelayStatus", relayStatusString);
            mav.put("LoadControlStatus", loadControlStatusString);
            mav.put("FailReason", failReasonString);

        } catch (Exception e) {
            log.error(e, e);
            rtnStr = e.getMessage();
            status = ResultStatus.FAIL;
        }

        /**
         * 2014.07.03 simhanger Relay On : Relay Switch로 남겨지던 로그를 Relay On으로
         * 남겨지도록 수정함
         * 
         * Code operationCode = codeManager.getCodeByCode("8.1.4");
         */
        Code operationCode = codeManager.getCodeByCode("8.1.5");
        Code meterTypeCode = codeManager.getCode(meter.getMeterTypeCodeId());
        if (operationCode != null) {
            operationLogManager.saveOperationLog(supplier, meterTypeCode, meter.getMdsId(), loginId, operationCode,
                    status.getCode(), status.name());
        }
        meterManager.updateMeter(meter);
        mav.put("status", status.name());
        mav.put("meterId", meter.getMdsId());
        mav.put("rtnStr", rtnStr);
        return mav;
    }

    public Map<String, Object> dlmsGetSet(String cmd, String parameter, String mdsId, String modelName) {
        Map<String, Object> mav = new HashMap<String, Object>();
        List<Map<String, Object>> modemTempList = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> modemList = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> rtnStrList = new ArrayList<Map<String, Object>>();
        ResultStatus status = ResultStatus.FAIL;
        Meter meter = null;

        JSONArray jsonArr = null;
        try{
            if (loginId != null ){
                if ( ("cmdMeterParamSet".equals(cmd) || "cmdMeterParamAct".equals(cmd)) &&
                        !commandAuthCheck(loginId, CommandType.DeviceWrite, "8.1.11")) {
                    throw new Exception("No permission");
                }
                else if ("cmdMeterParamGet".equals(cmd) && 
                        !commandAuthCheck(loginId, CommandType.DeviceRead, "8.1.10")) {
                    throw new Exception("No permission");
                }
            }
            if(parameter == null || parameter.isEmpty()) {
                jsonArr = new JSONArray();
            } else {
                jsonArr = JSONArray.fromObject(parameter);
            }
            
            List<Map<String,String>> paramList = new ArrayList<Map<String,String>>();
            Map<String,String> paramMap = new HashMap<String,String>();
            if(jsonArr.size() > 0) {
                Object[] tempJson = jsonArr.toArray();
                for (int i = 0; i < tempJson.length; i++) {
                    Map<String,Object> jsonMap = (Map<String,Object>)tempJson[i];
                    String obisCode = jsonMap.get("OBISCODE").toString();
                    String classId = jsonMap.get("CLASSID").toString();
                    String attributeNo = jsonMap.get("ATTRIBUTENO") == null ? "" : jsonMap.get("ATTRIBUTENO").toString();
                    String dataType = jsonMap.get("DATATYPE") == null ? "" : jsonMap.get("DATATYPE").toString();
                    String accessRight = jsonMap.get("ACCESSRIGHT") == null ? "" : jsonMap.get("ACCESSRIGHT").toString();
                    String value = jsonMap.get("VALUE") == null ? "" : jsonMap.get("VALUE").toString();

                    //cmd종류: cmdMeterParamGet, cmdMeterParamSet
                    String paramType = "";
                    if(i==0) {
                        paramType = cmd.replace("cmdMeterP", "p");
                    }else {
                        paramType = cmd.replace("cmdMeterP", "p")+i;
                    }
                    paramMap.put(paramType, obisCode+"|"+classId+"|"+attributeNo+"|"+accessRight+"|"+dataType+"|"+value);
                    paramList.add(paramMap);
                }
            }

            try{
                meter = meterManager.getMeter(mdsId);
                if(meter != null && meter.getModem() != null) {
                    if(meter.getModel() != null && !meter.getModel().getName().equals(modelName)) {
                        Map<String,Object> tempMap = new HashMap<String,Object>();
                        tempMap.put("meterId", mdsId);
                        tempMap.put("rtnStr", "FAIL : Model is Not " + modelName);
                        rtnStrList.add(tempMap);
                    } else {
                        Modem modem = meter.getModem();
                        if(modem.getModemType() == ModemType.MMIU && (modem.getProtocolType() == Protocol.SMS 
                                || modem.getProtocolType() == Protocol.IP 
                                || modem.getProtocolType() == Protocol.GPRS)) {
                            Map<String,Object> condition = new HashMap<String,Object>();
                            condition.put("modemId", meter.getModemId());
                            condition.put("modemType", ModemType.MMIU.toString());
                            MMIU mmiu = (MMIU)modemManager.getModemByType(condition);

                            Map<String,Object> tempMap = new HashMap<String,Object>();
                            tempMap.put("meterId", mdsId);
                            tempMap.put("modemType", meter.getModem().getModemType().name());
                            tempMap.put("protocolType", meter.getModem().getProtocolType());
                            tempMap.put("modem", mmiu);
                            modemTempList.add(tempMap);
                        } else if(modem.getModemType() == ModemType.SubGiga && modem.getProtocolType() == Protocol.IP) {
                            Map<String,Object> tempMap = new HashMap<String,Object>();
                            tempMap.put("meterId", mdsId);
                            tempMap.put("modemType", meter.getModem().getModemType());
                            tempMap.put("protocolType", meter.getModem().getProtocolType());
                            tempMap.put("modem", meter.getModem());
                            modemTempList.add(tempMap);
                        } else {
                            Map<String,Object> tempMap = new HashMap<String,Object>();
                            tempMap.put("meterId", mdsId);
                            tempMap.put("rtnStr", "FAIL : Target ID null!");
                            rtnStrList.add(tempMap);
                        }
                    }
                } else {
                    Map<String,Object> tempMap = new HashMap<String,Object>();
                    tempMap.put("meterId", mdsId);
                    tempMap.put("rtnStr", "FAIL : Target ID null!");
                    rtnStrList.add(tempMap);
                }
            }catch(Exception e) {
                log.warn(e,e);
                Map<String,Object> tempMap = new HashMap<String,Object>();
                tempMap.put("meterId", mdsId);
                tempMap.put("rtnStr", "FAIL : Target ID null!");
                rtnStrList.add(tempMap);
            }

            for (int j = 0; j < modemTempList.size(); j++) {
                Map<String,Object> map = null;
                try{
                    map = modemTempList.get(j);

                    if(map.get("modemType") == ModemType.MMIU.name() && map.get("protocolType") == Protocol.SMS) {
                        MMIU mmiu = (MMIU)map.get("modem");

                        String mobileNo = mmiu.getPhoneNumber();
                        if (mobileNo == null || "".equals(mobileNo)) {
                            log.warn(String.format("[" + cmd + "] Phone number is empty"));
                            Map<String,Object> tempMap = new HashMap<String,Object>();
                            tempMap.put("meterId", map.get("meterId"));
                            tempMap.put("rtnStr", "FAIL : Phone number is empty!");
                            rtnStrList.add(tempMap);
                            continue;
                        }

                        if (!Protocol.SMS.equals(mmiu.getProtocolType())) {
                            log.warn(String.format("[" + cmd + "] Invalid ProtocolType"));
                            Map<String,Object> tempMap = new HashMap<String,Object>();
                            tempMap.put("meterId", map.get("meterId"));
                            tempMap.put("rtnStr", "FAIL : Invalid ProtocolType!");
                            rtnStrList.add(tempMap);
                            continue;
                        }

                         // 서버에서 모뎀으로 SMS를 보낸뒤 모뎀이 서버에 접속하여 수행해야할 Command가 무엇인지
                         //* 구분할 방법이 따로 없기 때문에 Transaction ID를 사용하여 구분하도록 한다.

                        Long trId = System.currentTimeMillis();
                        Map<String, String> result;
                        String cmdResult = "";
                        
                        //* 비동기 명령 저장 : SMS발송보다 먼저 저장하려 했으나 변경-> 메시지발송결과를 받으면 명령을 저장.                        
                        //saveAsyncCommandList(mmiu.getDeviceSerial(), trId, cmd, paramList, TimeUtil.getCurrentTime());
                        //map.put("trId", trId);
                        modemList.add(map);

                        //String messageId = sendSMSForMOE("244.0.0", trId + "", mmiu.getDeviceSerial(), cmd);
                        result = sendSmsForCmdServer(mmiu, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), cmd, paramMap); 

                        if(result != null){
                            cmdResult =  result.get("RESULT").toString();
                            //check result 0911
                            log.debug("### MMIU - CMDRESULT : " + cmdResult);                       
                        }else{
                            log.debug("SMS Fail");
                            //cmdResult="Failed to get the resopone. See the Async Command History.";
                            cmdResult="Check the Async_Command_History.";
                        }

                        // MMIU모뎀의 결과값은 나중에 한번에 가져온다.
                        Map<String, Object> tempMap = new HashMap<String, Object>();
                        tempMap.put("meterId", map.get("meterId"));
                        tempMap.put("rtnStr", cmdResult);
                        //tempMap.put("trId", trId);
                        rtnStrList.add(tempMap);
                    } else {
                        List<Map<String, Object>> result = null;
                        Modem modem = (Modem) map.get("modem");
                        if ("cmdMeterParamGet".equals(cmd)) {
                            result = cmdOperationUtil.cmdMeterParamGet(modem.getDeviceSerial(),
                                    paramList.get(0).get("paramGet"), modem.getProtocolType());
                        } else if ("cmdMeterParamSet".equals(cmd))  {
                            result = cmdOperationUtil.cmdMeterParamSet(modem.getDeviceSerial(),
                                    paramList.get(0).get("paramSet"), modem.getProtocolType());
                        } else if ("cmdMeterParamAct".equals(cmd))  {
                            result = cmdOperationUtil.cmdMeterParamAct(modem.getDeviceSerial(),
                                    paramList.get(0).get("paramAct"), modem.getProtocolType());
                        }

                        if (result != null) {
                            Map<String, Object> tempMap = new HashMap<String, Object>();
                            tempMap.put("meterId", map.get("meterId"));
                            tempMap.put("rtnStr", "DONE! ");
                            tempMap.put("viewMsg", result);
                            rtnStrList.add(tempMap);
                            status = ResultStatus.SUCCESS;
                        }
                    }

                } catch (Exception e) {
                    log.error(e, e);
                    Map<String, Object> tempMap = new HashMap<String, Object>();
                    tempMap.put("meterId", map.get("meterId"));
                    //tempMap.put("rtnStr", "FAIL : " + e.getMessage());
                    tempMap.put("rtnStr", "Check the Async_Command_History");
                    rtnStrList.add(tempMap);
                    continue;
                }
            }

        } catch (Exception e) {
            log.error(e, e);
            Map<String, Object> tempMap = new HashMap<String, Object>();
            tempMap.put("meterId", "Unknow Error");
            tempMap.put("rtnStr", "FAIL : " + e.getMessage());
            rtnStrList.add(tempMap);
        }

        if ( meter != null ){
            Code operationCode = "cmdMeterParamSet".equals(cmd) ? 
                    codeManager.getCodeByCode("8.1.11") : codeManager.getCodeByCode("8.1.10");
            if (operationCode != null) {
                operationLogManager.saveOperationLog(meter.getSupplier(), 
                        meter.getMeterType(), meter.getMdsId(), loginId,
                        operationCode, status.getCode(), status.name());
            }

        }
        mav.put("rtnStrList", rtnStrList);
        return mav;
    }

    public Map<String,Object> sendSms(Map<String, Object> condition, List<String> paramList, String cmdMap) throws Exception {
        Map<String, Object> resultMap = new HashMap<String,Object>();
        String euiId = condition.get("euiId").toString();
        String messageId = cmdOperationUtil.sendSMS(condition, paramList, cmdMap);
        String commandCode =  condition.get("commandCode").toString();
        
        // 결과처리 로직 (S)
        String rtnMessage = null;
        // MBB Modem으로 전송하는 SMS 명령이
        // 55(set up environment For NI),56(~~CoAP),57(~~SNMP)일 경우
        // Async_command_Result 조회를 하지않고, message id만 55, 56, 57 명령 처리 로직으로 넘겨준다.
        if (    commandCode.equals(COMMAND_TYPE.NI.getTypeCode())
                || commandCode.equals(COMMAND_TYPE.COAP.getTypeCode())
                || commandCode.equals(COMMAND_TYPE.SNMP.getTypeCode()) ) {
            if (messageId.equals("FAIL")) {
                resultMap.put("messageId", "F");
            } else if (messageId.equals("FAIL-CONNECT")) {
                resultMap.put("messageId", "CF");
            } else {
                resultMap.put("messageId", messageId);
            }
        } else {
            if (messageId.equals("FAIL")) {
                resultMap.put("messageType", "F");
            } else if (messageId.equals("FAIL-CONNECT")) {
                resultMap.put("messageType", "CF");
            } else {
                // 5초 간격 20초 동안 AsyncCommandResult 테이블 조회 로직 (S)
                int time = 0;
                int interver = 5000;        // 5 second
                int period  = 20000;        // 20 second

                while (time != period) {
                    waitResponse(interver);
                    time += interver;
                    
                    if (rtnMessage != null) {
                        break;
                    } else {
                        try {
                            rtnMessage = asyncCommandLogManager.getCmdResults(euiId, Long.parseLong(messageId));
                        } catch (Exception e) {
                            rtnMessage = null;
                        }
                    }
                }
                // 5초 간격 20초 동안 AsyncCommandResult 테이블 조회 로직 (E)
                
                if (rtnMessage == null) {
                    resultMap.put("messageType", "F");
                    return resultMap;
                }
                
                ResponseFrame responseFrame = new ResponseFrame();
                resultMap = responseFrame.decode(rtnMessage);

            }
        }
        // 결과처리 로직 (E)

        return resultMap;
    }
    public void waitResponse(int waitTime) {
        synchronized (resMonitor) {
            try {
                resMonitor.wait(waitTime);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }

    /*
     * SMS를 보내고  Async테이블에서 결과를 가져오는 함수 입니다.
     */
    public Map sendSmsForCmdServer(Modem modem, String messageType, String commandCode
            ,String commandName, Map<String, String> paramMap) throws Exception{
        log.debug("[sendSmsAndGetResult] " + " messageType: " + messageType + 
                " commandCode: " + commandCode + " commandName: " + commandName);

            /*
            * 서버에서 모뎀으로 SMS를 보낸뒤 모뎀이 서버에 접속하여 수행해야할 Command가 무엇인지
            * 구분할 방법이 따로 없기 때문에 Transaction ID를 사용하여 구분하도록 한다.
            */
            /*Long maxTrId = asyncCommandLogManager.getMaxTrId(modem.getDeviceSerial());
            String trnxId;
            if (maxTrId != null) {
            trnxId = String.format("%08d", maxTrId.intValue() + 1);
            } else {
            trnxId = "00000001";
            }*/

            /*
            * 비동기 명령 저장 : SMS발송보다 먼저 저장함.
            */
            //saveAsyncCommandForSORIA(modem.getDeviceSerial(), Long.parseLong(trnxId), commandName, paramMap, TimeUtil.getCurrentTime());
 
            /*
            * SMS 발송
            */
            Map<String, Object> resultMap = new HashMap<String,Object>();
            Map<String, Object> condition = new HashMap<String, Object>();
            String mobliePhNum = null;
            String euiId = null;

            if(modem.getModemType().equals(ModemType.MMIU)){            
            //MMIU mmiuModem = (MMIU) modem;
            MMIU mmiuModem = mmiuDao.get(modem.getId());
            mobliePhNum = mmiuModem.getPhoneNumber();
            euiId = modem.getDeviceSerial();

            condition.put("messageType", messageType);
            condition.put("mobliePhNum", mobliePhNum);
            condition.put("euiId", euiId);
            condition.put("commandCode", commandCode);
            condition.put("commandName", commandName);

            List<String> paramListForSMS  = new ArrayList<String>();
            Properties prop = new Properties();
            prop.load(getClass().getClassLoader().getResourceAsStream("command.properties"));
            String serverIp = prop.getProperty("smpp.hes.fep.server") == null ? "" : prop.getProperty("smpp.hes.fep.server").trim();
            String serverPort = prop.getProperty("soria.modem.tls.port") == null ? "" : prop.getProperty("soria.modem.tls.port").trim();
            String authPort = prop.getProperty("smpp.auth.port") == null ? "" : prop.getProperty("smpp.auth.port").trim();

            int loopCountMax = Integer.parseInt(prop.getProperty("smpp.command.response.timeout.loopcount", "6"));
            int loopSleep = Integer.parseInt(prop.getProperty("smpp.command.response.timeout.loopsleep.sec", "10")) * 1000;

            log.debug("Send SMS Param Properties. ServerIp="+serverIp+", ServerPort="+serverPort+", AuthPort="+authPort+", LoopCountMax="+loopCountMax+", loopSleep=" + loopSleep);

            paramListForSMS.add(serverIp);
            paramListForSMS.add(serverPort);
            paramListForSMS.add(authPort);

            // modem이 Fep에 붙었을 때 실행할 command의 param들을 json String으로 넘겨줌
            String cmdMap = null;
            ObjectMapper om = new ObjectMapper();
            if (paramMap != null)
                cmdMap = om.writeValueAsString(paramMap);

            log.debug("Send SMS euiId: " + euiId + ", mobliePhNum: " +mobliePhNum+ ", commandName: " + commandName + ", cmdMap " + cmdMap);
            resultMap = sendSms(condition, paramListForSMS, cmdMap); // Send SMS!
            //String response_messageType = resultMap.get("messageType").toString();
            String response_messageId = resultMap.get("messageId") == null ? "F" : resultMap.get("messageId").toString();
            /*
             * 결과 처리
             */
            if (response_messageId.equals("F")
                    || response_messageId.equals("CF")) { // Fail
                log.debug(response_messageId);
                return null;
            } else {
                int loopCount = 0;
                Integer lastStatus = null;
                while(loopCount < loopCountMax) {
                    lastStatus = asyncCommandLogManager.getCmdStatus(modem.getDeviceSerial(), commandName);
                    log.debug("#### Response Result Status Check ~!! ==> " + TR_STATE.valueOf(lastStatus).name());
                    if (TR_STATE.Success.getCode() == lastStatus) {
                        break;
                    }
                    loopCount++;
                    Thread.sleep(loopSleep);
                }

                if (TR_STATE.Success.getCode() != lastStatus) {
                    log.debug("FAIL : Communication Error but Send SMS Success.  "+ euiId + "  " + commandName);
                    return null;
                } else { 
                    ObjectMapper mapper = new ObjectMapper();
                    List<AsyncCommandResult> asyncResult = asyncCommandLogManager.getCmdResults(modem.getDeviceSerial(), Long.parseLong(response_messageId),commandName); //ASYNC_COMMAND_RESULT에서 결과 값을 가져옴
                    if (asyncResult == null || asyncResult.size() <= 0) {
                        log.debug("#### Response Fail. Send SMS but fail to execute. EUI_ID=" + euiId + ", CommandName=" + commandName);
                        return null;
                    } else { // Success
                        String resultStr="";
                        for(int i = 0 ; i < asyncResult.size() ; i ++){
                            resultStr += asyncResult.get(i).getResultValue();
                        }
                        Map<String, String> map = mapper.readValue(resultStr , new TypeReference<Map<String, String>>(){});
                        log.debug("#### Response Success. Result ==> " + map.toString());
                        
                        return map; // 맴 형식으로 결과 리턴
                    }
                }
            }
        } else {
            log.error("Type Missmatch. this modem is not MMIU Type modem.");
            throw new Exception("Type Missmatch. this modem is not MMIU Type modem.");
        }

    }

    private Boolean isSMSModem(Modem modem) {
        boolean isSMSModem = false;

        if (modem != null && ModemType.MMIU.equals(modem.getModemType()) && Protocol.SMS.equals(modem.getProtocolType())
                && "0102".equals(modem.getProtocolVersion())) {
            isSMSModem = true;
        }

        return isSMSModem;
    }

    private String convertObis(String obisCode) {
        String returnData = "";
        if(obisCode.length() == 12) {
            byte[] obisCodeArr = Hex.encode(obisCode);
            obisCode="";
            for (int i = 0; i < obisCodeArr.length; i++) {
                if(i == 0) {
                    obisCode += DataUtil.getIntToByte(obisCodeArr[i]);
                } else {
                    obisCode += "."+DataUtil.getIntToByte(obisCodeArr[i]);
                }
            }
            returnData = obisCode;
        } else {
            returnData = "Wrong Obis";
        }
        
        return returnData;
    }   

    private void saveAsyncCommandList(String deviceSerial, long trId, String cmd, List<Map<String, String>> paramList,
            String currentTime) {
        AsyncCommandLog asyncCommandLog = new AsyncCommandLog();
        asyncCommandLog.setTrId(trId);
        asyncCommandLog.setMcuId(deviceSerial);
        asyncCommandLog.setDeviceType(McuType.MMIU.name());
        asyncCommandLog.setDeviceId(deviceSerial);
        asyncCommandLog.setCommand(cmd);
        asyncCommandLog.setTrOption(TR_OPTION.ASYNC_OPT_RETURN_DATA_SAVE.getCode());
        asyncCommandLog.setState(TR_STATE.Waiting.getCode());
        asyncCommandLog.setOperator(OperatorType.OPERATOR.name());
        asyncCommandLog.setCreateTime(currentTime);
        asyncCommandLog.setRequestTime(currentTime);
        asyncCommandLog.setLastTime(null);
        asyncCommandLogManager.add(asyncCommandLog);
        Integer num = 0;
        if (paramList != null && paramList.size() > 0) {
            // parameter가 존재할 경우.
            Integer maxNum = asyncCommandLogManager.getParamMaxNum(deviceSerial, trId);
            if (maxNum != null)
                num = maxNum + 1;

            for (int i = 0; i < paramList.size(); i++) {
                Map<String, String> param = paramList.get(i);
                Iterator<String> iter = param.keySet().iterator();
                while (iter.hasNext()) {
                    String key = iter.next();

                    AsyncCommandParam asyncCommandParam = new AsyncCommandParam();
                    asyncCommandParam.setMcuId(deviceSerial);
                    asyncCommandParam.setNum(num);
                    asyncCommandParam.setParamType(key);
                    asyncCommandParam.setParamValue((String) param.get(key));
                    asyncCommandParam.setTrId(trId);

                    asyncCommandLogManager.addParam(asyncCommandParam);
                    num += 1;
                }
            }
        }
    }

}
