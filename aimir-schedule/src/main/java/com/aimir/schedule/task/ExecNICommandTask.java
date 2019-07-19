package com.aimir.schedule.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.aimir.fep.protocol.smsp.SMSConstants;
import com.aimir.fep.protocol.smsp.SMSConstants.COMMAND_TYPE;
import com.aimir.fep.protocol.smsp.command.frame.sms.ResponseFrame;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.AsyncCommandResult;
import com.aimir.model.device.MMIU;
import com.aimir.model.device.Modem;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.device.OperationLogDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.OperatorDao;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.constants.CommonConstants.TR_STATE;
import com.aimir.dao.device.AsyncCommandLogDao;
import com.aimir.dao.device.AsyncCommandResultDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MMIUDao;
import com.aimir.schedule.command.CmdOperationUtil;

@Service
public class ExecNICommandTask extends ScheduleTask 
{
    private static Log log = LogFactory.getLog(ExecNICommandTask.class);
    
    @Autowired
    ModemDao modemDao;

    @Autowired
    MCUDao mcuDao;
 
	@Autowired
	MMIUDao mmiuDao;

	@Autowired
	CodeDao codeDao;

	@Autowired
	OperatorDao 	operatorDao;

	@Autowired
	CmdOperationUtil cmdOperationUtil;

	@Autowired
	OperationLogDao operationLogDao;

	@Autowired
	AsyncCommandLogDao asyncCommandLogDao;

	@Autowired
	AsyncCommandResultDao resultDao;    
    
    @Resource(name="transactionManager")
    HibernateTransactionManager txmanager;

    private static String _modemId;
    private static String _requestType;
    private static String _attrId;
    private static String _attrParam;;
    private static String _direct;
    private static String _dev;
    
    public static void main(String[] args) {

		log.debug("ARG_0[" + args[0] + "] ARG_1[" + args[1] + "] ARG_2[" + args[2] + "] ARG_3[" + args[3] + 
				"] ARG_4[" + args[4] + "] ARG_5[" + args[5] + "]");		
		
		_modemId = args[0];
		_requestType = args[1];
		_attrId = args[2];
		_attrParam = args[3];
		_dev = args[4];                
		_direct = args[5];

		if (_dev.equals("DEV")) {
	        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"spring-schedule-task-dev.xml"}); 
	        DataUtil.setApplicationContext(ctx);
	        ExecNICommandTask task = ctx.getBean(ExecNICommandTask.class);
	        task.execute(null);
			
		} else {
	        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"spring-schedule-task.xml"}); 
	        DataUtil.setApplicationContext(ctx);
	        ExecNICommandTask task = ctx.getBean(ExecNICommandTask.class);
	        task.execute(null);
		}
		
        System.exit(0);
    }
    
	@Override
	public void execute(JobExecutionContext context) {

        TransactionStatus txstatus = null;
        txstatus = txmanager.getTransaction(null);

        try {
        	Modem modem = modemDao.get(_modemId);
        	//Modem modem = modemDao.get(Integer.parseInt(_modemId));
        	
        	if( ((modem instanceof MMIU) && (modem.getProtocolType() == Protocol.SMS)) ){
        		Map<String, String> result = new HashMap<String, String>();
        		result = cmdExecNIMBB(String.valueOf(modem.getId()), _requestType, _attrId, _attrParam);
    			for (String key : result.keySet()) {
    				log.debug(key + " : " + result.get(key).toString());
    			}
    		}else if( ((modem instanceof MMIU) && 
    				(modem.getProtocolType() == Protocol.IP || modem.getProtocolType() == Protocol.GPRS)) ||
        			  (_direct.equals("DIRECT"))) {
        		Map<String, Object> result = new HashMap<String, Object>();
        		result = cmdOperationUtil.cmdGeneralNiCommand(String.valueOf(modem.getId()), _requestType, _attrId, _attrParam);
    			for (String key : result.keySet()) {
    				log.debug(key + " : " + result.get(key).toString());
    			}
        	} else {
        		Map<String, Object> result = new HashMap<String, Object>();
        		result = cmdOperationUtil.cmdExecDmdNiCommand(String.valueOf(modem.getId()), _requestType, _attrId, _attrParam);
    			for (String key : result.keySet()) {
    				log.debug(key + " : " + result.get(key).toString());
    			}
        	}            
        }
        catch (Exception e) {
            if (txstatus != null) txmanager.rollback(txstatus);
            log.error(e, e);
            return;
        }
        if (txstatus != null) txmanager.commit(txstatus);
        
        log.info("End Check over threshold count. ");
    }    

    private Map<String, String> cmdExecNIMBB(String modem_Id, String requestType, String attrID, String attrParam) {
    	
    	ResultStatus status = ResultStatus.FAIL;
    	String cmd = "cmdExecNI";

    	Map<String, String> returnMap = null;
    	
    	Modem modem = modemDao.get(Integer.parseInt(modem_Id));

    	try {
	    	if ( modem == null ){
				throw new Exception("Target modem is NULL");
	    	}

    		if( ((modem.getModemType() == ModemType.MMIU) && (modem.getProtocolType() == Protocol.SMS)) ){
    			Map<String, String> asyncResult = new HashMap<String, String>();
    			Map<String, String> paramMap = new HashMap<String, String>();
    			
    			paramMap.put("modemId", modem_Id);
				paramMap.put("requestType", requestType);
				paramMap.put("attrID", attrID);
				paramMap.put("attrParam", attrParam);
       			asyncResult = sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), cmd, paramMap);	

    			if(asyncResult != null){
    				status = ResultStatus.SUCCESS;
					returnMap = new HashMap<String,String>();
					returnMap.putAll(asyncResult);
					returnMap.put("result", "Success");
    			}else{
    				log.debug("[" + modem_Id + "] SMS Fail");
    			}

    		}
    	}catch(Exception e){
    		log.error("[" + modem_Id + "] cmdExecNIMBB  excute error - " + e,e);
    	}
    	return returnMap;
    }	
	
	
    /*
     * Send SMS
     */
	public Map sendSmsForCmdServer(Modem modem, String messageType, String commandCode, String commandName, Map<String, String> paramMap) throws Exception {
		log.debug("[sendSmsForCmdServer] " + " messageType: " + messageType + " commandCode: " + commandCode + " commandName: " + commandName);

		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> condition = new HashMap<String, Object>();
		String mobliePhNum = null;
		String euiId = null;

		if (modem.getModemType().equals(ModemType.MMIU)) {
			//MMIU mmiuModem = (MMIU) modem;
			MMIU mmiuModem = mmiuDao.get(modem.getId());
			mobliePhNum = mmiuModem.getPhoneNumber();
			euiId = modem.getDeviceSerial();

			condition.put("messageType", messageType);
			condition.put("mobliePhNum", mobliePhNum);
			condition.put("euiId", euiId);
			condition.put("commandCode", commandCode);
			condition.put("commandName", commandName);

			List<String> paramListForSMS = new ArrayList<String>();
			Properties prop = new Properties();
            try{
        		if (_dev.equals("DEV")) {
        			prop.load(getClass().getClassLoader().getResourceAsStream("config/command.properties.dev"));
        		} else {
        			prop.load(getClass().getClassLoader().getResourceAsStream("config/command.properties"));
        		}
            }catch(Exception e){
            	log.error("Can't not read property file. -" + e,e);
            }

			String serverIp = prop.getProperty("smpp.hes.fep.server") == null ? "" : prop.getProperty("smpp.hes.fep.server").trim();
			String serverPort = prop.getProperty("soria.modem.tls.port") == null ? "" : prop.getProperty("soria.modem.tls.port").trim();
			String authPort = prop.getProperty("smpp.auth.port") == null ? "" : prop.getProperty("smpp.auth.port").trim();
			paramListForSMS.add(serverIp);
			paramListForSMS.add(serverPort);
			paramListForSMS.add(authPort);

			// modem이 Fep에 붙었을 때 실행할 command의 param들을 json String으로 넘겨줌
			String cmdMap = null;
			ObjectMapper om = new ObjectMapper();
			if (paramMap != null)
				cmdMap = om.writeValueAsString(paramMap);

			log.debug("Send SMS euiId: " + euiId + ", mobliePhNum: " + mobliePhNum + ", commandName: " + commandName + ", cmdMap " + cmdMap);
			resultMap = sendSms(condition, paramListForSMS, cmdMap); // Send SMS!
			//String response_messageType = resultMap.get("messageType").toString();
			String response_messageId = resultMap.get("messageId") == null ? "F" : resultMap.get("messageId").toString();
			/*
			 * 결과 처리
			 */
			if (response_messageId.equals("F") || response_messageId.equals("CF")) { // Fail
				log.debug(response_messageId);
				return null;
			} else {
			    int loopCount = 0;
                Integer lastStatus = null;
                while(loopCount < 6) {
                    lastStatus = asyncCommandLogDao.getCmdStatus(modem.getDeviceSerial(), commandName);
                    if (TR_STATE.Success.getCode() == lastStatus) {
                        break;
                    }
                    loopCount++;
                    Thread.sleep(10000);
                }
				if (TR_STATE.Success.getCode() != lastStatus) {
					log.debug("FAIL : Communication Error but Send SMS Success.  " + euiId + "  " + commandName);
					return null;
				} else {
					ObjectMapper mapper = new ObjectMapper();
					//List<AsyncCommandResult> asyncResult = asyncCommandLogManager.getCmdResults(modem.getDeviceSerial(), Long.parseLong(response_messageId),commandName); //ASYNC_COMMAND_RESULT에서 결과 값을 가져옴
					List<AsyncCommandResult> asyncResult = resultDao.getCmdResults(modem.getDeviceSerial(), Long.parseLong(response_messageId), commandName); //ASYNC_COMMAND_RESULT에서 결과 값을 가져옴
					if (asyncResult == null || asyncResult.size() <= 0) {
						log.debug("FAIL : Send SMS but fail to execute " + euiId + "  " + commandName);
						return null;
					} else { // Success
						String resultStr = "";
						for (int i = 0; i < asyncResult.size(); i++) {
							resultStr += asyncResult.get(i).getResultValue();
						}
						Map<String, String> map = mapper.readValue(resultStr, new TypeReference<Map<String, String>>() {
						});
						log.debug("Success get result");
						return map; // 맴 형식으로 결과 리턴
					}
				}
			}
		} else {
			log.error("Type Missmatch. this modem is not MMIU Type modem.");
			return null;
		}
	}


	public Map<String, Object> sendSms(Map<String, Object> condition, List<String> paramList, String cmdMap) throws Exception {

		Map<String, Object> resultMap = new HashMap<String, Object>();
		String euiId = condition.get("euiId").toString();
		String messageId = cmdOperationUtil.sendSMS(condition, paramList, cmdMap);
		String commandCode = condition.get("commandCode").toString();

		// 결과처리 로직 (S)
		String rtnMessage = null;
		// MBB Modem으로 전송하는 SMS 명령이
		// 55(set up environment For NI),56(~~CoAP),57(~~SNMP)일 경우
		// Async_command_Result 조회를 하지않고, message id만 55, 56, 57 명령 처리 로직으로 넘겨준다.
		if (commandCode.equals(COMMAND_TYPE.NI.getTypeCode()) || commandCode.equals(COMMAND_TYPE.COAP.getTypeCode()) || commandCode.equals(COMMAND_TYPE.SNMP.getTypeCode())) {
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
				try {
					int time = 0;
					int interver = 5000;		// 5 second
					int period  = 20000;		// 20 second
					while ( time != period ){
						Thread.sleep(interver);
						time += interver;
						
						if (rtnMessage != null) {
							break;
						} else {
							try {
								rtnMessage = resultDao.getCmdResults(euiId, Long.parseLong(messageId));
							} catch (Exception e) {
								rtnMessage = null;
							}
						}
					}

				} catch (Exception e) {
                    log.error("SendSMS excute error - " + e, e);
					resultMap.put("messageType", "F");
					return resultMap;
				}
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
	
}
