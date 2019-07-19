package com.aimir.fep.protocol.smsp.client.sms;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.jsmpp.InvalidResponseException;
import org.jsmpp.PDUException;
import org.jsmpp.bean.BindType;
import org.jsmpp.bean.DataCodings;
import org.jsmpp.bean.ESMClass;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.RegisteredDelivery;
import org.jsmpp.bean.SMSCDeliveryReceipt;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ResponseTimeoutException;
import org.jsmpp.extra.SessionState;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.SMPPSession;
import org.jsmpp.util.AbsoluteTimeFormatter;
import org.jsmpp.util.TimeFormatter;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.ModemIFType;
import com.aimir.constants.CommonConstants.OperatorType;
import com.aimir.constants.CommonConstants.TR_OPTION;
import com.aimir.constants.CommonConstants.TR_STATE;
import com.aimir.dao.device.AsyncCommandLogDao;
import com.aimir.dao.device.AsyncCommandParamDao;
import com.aimir.fep.protocol.smsp.SMSConstants.COMMAND_TYPE;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.AsyncCommandLog;
import com.aimir.model.device.AsyncCommandParam;
import com.aimir.util.TimeUtil;

/** 
 * SMS_Requester 
 * 
 * @version     1.0  2016.07.23 
 * @author		Sung Han LIM 
 */

public class SMS_Requester {
    private static Log logger = LogFactory.getLog(SMS_Requester.class);
    private static TimeFormatter timeFormatter = new AbsoluteTimeFormatter();
    private static final String smscServer = FMPProperty.getProperty("smpp.hostname", "smsc1.com4.no");
    private static final String smscPort = FMPProperty.getProperty("smpp.port", "9000"); 
    private static final String smscUserName = FMPProperty.getProperty("smpp.username", "validerams"); 
    private static final String smscPassword = FMPProperty.getProperty("smpp.password", "U91nDBr"); 
    private static final String hesPhonenumber = FMPProperty.getProperty("smpp.hes.phonenumber", "47580014013024");
    private SMPPSession session = null;

    private static SMS_Requester requester = null;

    private SMS_Requester() throws Exception {
    }

    private void initSession() throws Exception {
        if (session != null && (session.getSessionState() == SessionState.UNBOUND ||
                !session.getSessionState().isBound())) {
            session.unbindAndClose();
            session = null;
        }
        
        if (session == null || session.getSessionState() == SessionState.CLOSED) {
            session = new SMPPSession();
            session.setMessageReceiverListener(new SMS_Receiver());
            
            try {
                session.connectAndBind(smscServer, Integer.parseInt(smscPort), 
                        new BindParameter(BindType.BIND_TRX, smscUserName, smscPassword, "cp",
                        TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, null));
            } catch (IOException e) {
                logger.error(e, e);
                logger.debug("Failed connect and bind to host");
                throw new Exception("FAIL-CONNECT");
            }
        }
    }
    
    public static SMS_Requester newInstance() throws Exception {
        if (requester == null) requester = new SMS_Requester();
        return requester;
    }
    
    public String[] sendSMS(String sequence, String commandName,String commandCode, String euiId[], String MSISDN[],
            byte[] sendMessage, String cmdMap) throws IOException, ParseException, InterruptedException {
        String[] sequences = new String[euiId.length];
        
        for (int i = 0; i < euiId.length; i++) {
        	sequences[i] = sendSMS(sequence, commandName, commandCode, euiId[i], MSISDN[i], sendMessage, cmdMap);
        }
        
        return sequences;
    }
    
    @SuppressWarnings("unused")
	public synchronized String sendSMS(String sequence, String commandName,String commandCode, String euiId, String MSISDN,
            byte[] sendMessage, String cmdMap) throws IOException, ParseException, InterruptedException {
        
        // send Message
        JpaTransactionManager txManager = null;
        TransactionStatus txStatus = null;
        String messageId = null;
        
        try {
            // set RegisteredDelivery
            RegisteredDelivery registeredDelivery = new RegisteredDelivery();
            registeredDelivery.setSMSCDeliveryReceipt(SMSCDeliveryReceipt.SUCCESS_FAILURE);
            String currentTime = TimeUtil.getCurrentTime();
            
            if(!commandName.contains("AsyncChannel")){
            	initSession();
            	
            	// Command Name에 "AsyncChannel" String이 포함되어 있지 않으면 SMS를 보낸다.
	            messageId = session.submitShortMessage("CMT", TypeOfNumber.UNKNOWN,
	                    NumberingPlanIndicator.ISDN, hesPhonenumber, TypeOfNumber.INTERNATIONAL,
	                    NumberingPlanIndicator.ISDN, MSISDN, new ESMClass(), (byte) 0, (byte) 1,
	                    timeFormatter.format(new Date()), null, new RegisteredDelivery(SMSCDeliveryReceipt.SUCCESS_FAILURE),
	                    (byte) 0, DataCodings.ZERO, (byte) 0, sendMessage);
	            
	            // If you need test for the MBB(SMS) function, comment the upper code and uncomment the below one.
	            // messageId = currentTime+"0";
            }else{
            	// Command Name에 "AsyncChannel" String이 포함되어 있으면 SMS Skip하고 CommandName을 원상복구한다. (CommandName에 "AsyncChannel" String추가는 SORIABbbOTARunnable.java에서 이루어짐) 
            	commandName = commandName.replace("AsyncChannel", ""); 
            }
            
            logger.info("====================================");
            logger.info("MSG [" + sequence + "] submitted, ModemId[" + euiId + "], PhoneNumber [" + MSISDN + "]");
            logger.info("====================================");
            
            // SMS 비동기 명령 저장 로직(S)
            txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
            txStatus = txManager.getTransaction(null);

            AsyncCommandLogDao asyncCommandLogDao = DataUtil.getBean(AsyncCommandLogDao.class);
            AsyncCommandLog asyncCommandLog = new AsyncCommandLog();

            asyncCommandLog.setTrId(Long.parseLong(sequence));
            asyncCommandLog.setMcuId(euiId);
            asyncCommandLog.setDeviceType(ModemIFType.MBB.name());
            asyncCommandLog.setDeviceId(euiId);
            asyncCommandLog.setCommand(commandName);
            asyncCommandLog.setTrOption(TR_OPTION.ASYNC_OPT_RETURN_DATA_SAVE.getCode());
            if(commandCode.contains(COMMAND_TYPE.NI.getTypeCode())
                    || commandCode.contains(COMMAND_TYPE.COAP.getTypeCode())
                    || commandCode.contains(COMMAND_TYPE.SNMP.getTypeCode()) ){
                asyncCommandLog.setState(TR_STATE.Waiting.getCode());
            }else{
                asyncCommandLog.setState(TR_STATE.Running.getCode());
            }            
            asyncCommandLog.setOperator(OperatorType.OPERATOR.name());
            asyncCommandLog.setCreateTime(currentTime);
            asyncCommandLog.setRequestTime(currentTime);
            asyncCommandLog.setLastTime(null);
            asyncCommandLogDao.add(asyncCommandLog);
            
            AsyncCommandParam asyncCommandParam = new AsyncCommandParam();
            AsyncCommandParamDao asyncCommandParamDao = DataUtil.getBean(AsyncCommandParamDao.class);
            
            // byte value of sms
            asyncCommandParam.setMcuId(euiId);
            asyncCommandParam.setNum(0);
            asyncCommandParam.setTrId(Long.parseLong(sequence));
            asyncCommandParam.setParamType("byte");
            asyncCommandParam.setParamValue(Hex.decode(sendMessage));
            asyncCommandParam.setTrType("SMS");
            asyncCommandParamDao.add(asyncCommandParam);
            
            // commandcode is used to set the protocol type (NI or COAP or ETC)
            asyncCommandParam = new AsyncCommandParam();
            asyncCommandParam.setMcuId(euiId);
            asyncCommandParam.setNum(1);
            asyncCommandParam.setTrId(Long.parseLong(sequence));
            asyncCommandParam.setParamType("CommandCode");
            asyncCommandParam.setParamValue(commandCode);
            asyncCommandParam.setTrType("CommandCode");
            asyncCommandParamDao.add(asyncCommandParam);
            
            if (cmdMap != null) {
                ObjectMapper cmdMapper = new ObjectMapper();
                Map<String, String> mapForCmd =  cmdMapper.readValue(cmdMap , new TypeReference<Map<String, String>>(){});
                
                if (mapForCmd != null) {
                    Iterator<String> keys = mapForCmd.keySet().iterator();
                    int paramNum = 2;              
                    while (keys.hasNext()) {
                        String key = keys.next();
    
                        asyncCommandParam = new AsyncCommandParam();
                        asyncCommandParam.setTrType("CMD");
                        asyncCommandParam.setParamType(key);
                        asyncCommandParam.setParamValue(mapForCmd.get(key));
                        asyncCommandParam.setMcuId(euiId);
                        asyncCommandParam.setNum(paramNum++);
                        asyncCommandParam.setTrId(Long.parseLong(sequence));
                        
                        asyncCommandParamDao.add(asyncCommandParam);
                    }
                }
            } 
            
            txManager.commit(txStatus);
            
            logger.info("====================================");
            logger.info("MSG [" + sequence + "] Info Save - OK");
            logger.info("====================================");
            // SMS 비동기 명령 저장 로직(E)
            
        } catch (PDUException e) {
            logger.error("FAIL - Invalid PDU parameter", e);
            sequence = "FAIL";
            
            if(txStatus != null) {
            	txManager.rollback(txStatus);
            }            
        } catch (ResponseTimeoutException e) {
            logger.error("FAIL - Response timeout", e);
            sequence = "FAIL";
            
            if(txStatus != null) {
            	txManager.rollback(txStatus);
            }    
        } catch (InvalidResponseException e) {
            logger.error("FAIL - Receive invalid respose", e);
            sequence = "FAIL";
            
            if(txStatus != null) {
            	txManager.rollback(txStatus);
            }    
        } catch (NegativeResponseException e) {
            logger.error("FAIL - Receive negative response", e);
            sequence = "FAIL";
            
            if(txStatus != null) {
            	txManager.rollback(txStatus);
            }    
        } catch (IOException e) {
            logger.error("FAIL - IO error occur", e);
            sequence = "FAIL";
            
            if(txStatus != null) {
            	txManager.rollback(txStatus);
            }    
        } catch (Exception e) { 
            logger.error(e, e);
            sequence = "FAIL";
            
            if(txStatus != null) {
            	txManager.rollback(txStatus);
            }    
        } finally {
        	
        }
        
        return sequence;
    }
}