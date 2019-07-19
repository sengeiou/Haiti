package com.aimir.mars.integration.multispeak.service_jms;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.multispeak.version_4.ArrayOfCDStateChange;
import org.multispeak.version_4.CDState;
import org.multispeak.version_4.CDStateChange;
import org.multispeak.version_4.ExpirationTime;
import org.multispeak.version_4.InitiateCDStateRequest;
import org.multispeak.version_4.LoadActionCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.OperationLogDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.fep.command.conf.DLMSMeta.LOAD_CONTROL_STATUS;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.command.mbean.CommandGW.OnDemandOption;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.sms.SendSMS;
import com.aimir.mars.integration.multispeak.data.MultiSpeakMessage;
import com.aimir.mars.integration.multispeak.util.MultiSpeakConstants.ValidationError;
import com.aimir.model.device.Meter;
import com.aimir.model.device.OperationLog;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Supplier;
import com.aimir.util.DateTimeUtil;

@Service
@Transactional
public class InitiateCDStateRequestService extends AbstractService {

    private static Log log = LogFactory
            .getLog(InitiateCDStateRequestService.class);

    //private CDMessage message = null;

    @Autowired
    private CommandGW command;

    @Autowired
    private MeterDao meterDao;

    @Autowired
    private CodeDao codeDao;

    @Autowired
    private ContractDao contractDao;

    @Autowired
    private OperationLogDao operationLogDao;

    @Autowired
    private JmsTemplate cdInJmsTemplate;

    public void execute(MultiSpeakMessage message) throws Exception {
    	
    	log.debug("InitiateCDStateRequestService execute start..");

        log.debug("InitiateCDStateRequestService execute start..");

        Calendar requestedTime = message.getRequestedTime();
        Object obj = message.getObject();

        Properties prop = new Properties();
        prop.load(getClass().getClassLoader()
                .getResourceAsStream("config/mars.properties"));

        log.debug("Message=" + message.toString());

        InitiateCDStateRequest request = (InitiateCDStateRequest) obj;

        log.debug("Request=" + request.toString());

        ArrayOfCDStateChange stateChanges = new ArrayOfCDStateChange();
        List<CDState> eventList = request.getStates().getCDState();

        log.debug("eventListsize=" + eventList.size());

        String responseURL = request.getResponseURL();
        String transactionID = request.getTransactionID();
        ExpirationTime expirationTime = request.getExpTime();
        Calendar expirationDateTime = Calendar.getInstance();
        expirationDateTime.setTime(requestedTime.getTime());
        
        log.debug("transactionID="+transactionID);
        log.debug("responseURL="+responseURL);
        log.debug("expirationTime="+expirationTime.getValue());
        /*
         * ExpirationTime value가 float 형이다보니 원하는 시간에 실행이 안될수 있다. 소수점 이하가 없는 경우는
         * 원래 방식대로 계산한다. 소수점 있는 경우는 일단 1년(365일) 1달(30일) 기본으로 한다. 초이하 오차는 무시한다.
         */
        switch (expirationTime.getUnits()) {
        case YEARS:
            if (expirationTime.getValue() % (int) expirationTime.getValue() == 0) {
                expirationDateTime.add(Calendar.YEAR,
                        (int) expirationTime.getValue());
            } else {
                expirationDateTime
                        .add(Calendar.SECOND, (int) (expirationTime.getValue()
                                * 365 * 30 * 24 * 60 * 60));
            }
            break;
        case MONTHS:
            if (expirationTime.getValue() % (int) expirationTime.getValue() == 0) {
                expirationDateTime.add(Calendar.MONTH,
                        (int) expirationTime.getValue());
            } else {
                expirationDateTime.add(Calendar.SECOND,
                        (int) (expirationTime.getValue() * 30 * 24 * 60 * 60));
            }
            break;
        case WEEKS:
            if (expirationTime.getValue() % (int) expirationTime.getValue() == 0) {
                expirationDateTime.add(Calendar.DATE,
                        (int) expirationTime.getValue() * 7);
            } else {
                expirationDateTime.add(Calendar.SECOND,
                        (int) (expirationTime.getValue() * 7 * 24 * 60 * 60));
            }
            break;
        case DAYS:
            if (expirationTime.getValue() % (int) expirationTime.getValue() == 0) {
                expirationDateTime.add(Calendar.DATE,
                        (int) expirationTime.getValue());
            } else {
                expirationDateTime.add(Calendar.SECOND,
                        (int) (expirationTime.getValue() * 24 * 60 * 60));
            }
            break;
        case HOURS:
            if (expirationTime.getValue() % (int) expirationTime.getValue() == 0) {
                expirationDateTime.add(Calendar.HOUR,
                        (int) expirationTime.getValue());
            } else {
                expirationDateTime.add(Calendar.SECOND,
                        (int) (expirationTime.getValue() * 60 * 60));
            }
            break;
        case MINUTES:
            if (expirationTime.getValue() % (int) expirationTime.getValue() == 0) {
                expirationDateTime.add(Calendar.MINUTE,
                        (int) expirationTime.getValue());
            } else {
                expirationDateTime.add(Calendar.SECOND,
                        (int) (expirationTime.getValue() * 60));
            }
            break;
        case SECONDS:
            expirationDateTime.add(Calendar.SECOND,
                    (int) expirationTime.getValue());
            break;
        case MILLISECONDS:
            expirationDateTime.add(Calendar.MILLISECOND,
                    (int) expirationTime.getValue());
            break;
        case OTHER:
        default:
            // OTHER 따로 정의 된게 없어 SECOND로 처리한다. 스펙에 디폴트가 정의 되어있다면 변경 해줘야 한다.
            expirationDateTime.add(Calendar.SECOND,
                    (int) expirationTime.getValue());
            break;
        }

        for (CDState event : eventList) {

            log.debug("CDState=" + event.toString());
            String meterId = event.getMeterID().getMeterNo();
            // CDReasonCode cdReasonCode = event.getCDReasonCode();

            log.debug("meterId=" + meterId);
            Meter meter = meterDao.get(meterId);
            // /TODO 집중기 단위로 재 소팅해서 명령 보내야 함
            Map<String, Object> resultMap = new HashMap<String, Object>();

            if (meter != null && event.getMeterID().getMeterNo().startsWith("testp")) {
                String pattern = "\\d+";
                Pattern r = Pattern.compile(pattern);
                Matcher m = r.matcher(event.getMeterID().getMeterNo());
                StringBuilder sbTemp = new StringBuilder();
                while(m.find()) {
                    sbTemp.append(m.group(0));
                }
                if(Integer.parseInt(sbTemp.toString()) % 2 == 0) {
                    resultMap.put("LoadControlStatus", LOAD_CONTROL_STATUS.OPEN);
                } else {
                    resultMap.put("LoadControlStatus", LOAD_CONTROL_STATUS.CLOSE);
                }
            } else {
                try {
                    resultMap = command.cmdOnDemandMeter(meter.getMcu().getSysID(),
                            meterId, OnDemandOption.READ_OPTION_RELAY.getCode());
                } catch (Exception e) {
                    log.error(e, e);
                }
            }
            if (resultMap != null
                    && resultMap.get("LoadControlStatus") != null & resultMap.size() > 0) {
                LOAD_CONTROL_STATUS ctrlStatus = (LOAD_CONTROL_STATUS) resultMap
                        .get("LoadControlStatus");

                if (ctrlStatus == LOAD_CONTROL_STATUS.CLOSE) {
                    updateMeterStatusNormal(meter);
                    CDStateChange cd = new CDStateChange();
                    cd.setErrorString("");
                    cd.setObjectID(event.getObjectID());
                    cd.setMeterID(event.getMeterID());
                    cd.setStateChange(LoadActionCode.CLOSED);

                    stateChanges.getCDStateChange().add(cd);
                } else {
                    //normal case
                    CDStateChange cd = new CDStateChange();
                    cd.setErrorString("");
                    cd.setObjectID(event.getObjectID());
                    cd.setMeterID(event.getMeterID());
                    cd.setStateChange(LoadActionCode.OPENED);

                    stateChanges.getCDStateChange().add(cd);
                }
            }else{
                //normal case
                CDStateChange cd = new CDStateChange();
                cd.setErrorString(ValidationError.COMMUNICATION_FAILURE.getName());
                cd.setObjectID(event.getObjectID());
                cd.setMeterID(event.getMeterID());
                stateChanges.getCDStateChange().add(cd);
            }
            
        }
        Calendar currentTime = Calendar.getInstance();

        if (currentTime.getTimeInMillis() <= expirationDateTime
                .getTimeInMillis() && eventList.size() > 0) {
            // send response
            log.debug("Send InitiateCDStateRequest Response..");

            cdInJmsTemplate.convertAndSend(stateChanges,
                    new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message message)
                        throws JMSException {
                    if (message instanceof BytesMessage) {
                        BytesMessage messageBody = (BytesMessage) message;
                        messageBody.reset();
                        Long length = messageBody.getBodyLength();
                        log.debug("***** MESSAGE LENGTH is " + length
                                + " bytes");
                        byte[] byteMyMessage = new byte[length
                                .intValue()];
                        int red = messageBody.readBytes(byteMyMessage);
                        log.debug("***** SENDING MESSAGE - \n"
                                + "<!-- MSG START -->\n"
                                + new String(byteMyMessage)
                                + "\n<!-- MSG END -->");
                    }
                    return message;
                }
            });
            
            log.debug("InitiateCDStateRequest ACK Response");
        }

        log.debug("InitiateCDStateRequestService execute end..");
    }

    protected void updateMeterStatusNormal(Meter meter) {
        meter.setMeterStatus(codeDao.getCodeIdByCodeObject("1.3.3.1"));
        meterDao.update(meter);

        Contract contract = meter.getContract();
        if (contract != null
                && contract.getStatus().getCode()
                        .equals(CommonConstants.ContractStatus.PAUSE.getCode())) {
            Code normalCode = codeDao
                    .getCodeIdByCodeObject(CommonConstants.ContractStatus.NORMAL
                            .getCode());
            contractDao.updateStatus(contract.getId(), normalCode);
        }
    }

    protected void updateMeterStatusCutOff(Meter meter) {
        meter.setMeterStatus(codeDao.getCodeIdByCodeObject("1.3.3.4"));
        meterDao.update(meter);

        Contract contract = meter.getContract();
        if (contract != null
                && contract
                        .getStatus()
                        .getCode()
                        .equals(CommonConstants.ContractStatus.NORMAL.getCode())) {
            Code pauseCode = codeDao
                    .getCodeIdByCodeObject(CommonConstants.ContractStatus.PAUSE
                            .getCode());
            contractDao.updateStatus(contract.getId(), pauseCode);
        }
    }

    private void saveOperationLog(ResultStatus status, String mdsId,
            Code meterType, Supplier supplier) {
        log.info("save OperationLog start");
        try {
            Code operationCode = codeDao.getCodeIdByCodeObject("8.1.9");
            if (operationCode != null) {
                String currDateTime = DateTimeUtil
                        .getCurrentDateTimeByFormat("yyyyMMddHHmmss");
                OperationLog operationLog = new OperationLog();

                operationLog.setOperatorType(1);// operator
                operationLog.setOperationCommandCode(operationCode);
                operationLog.setYyyymmdd(currDateTime.substring(0, 8));
                operationLog.setHhmmss(currDateTime.substring(8, 14));
                operationLog.setYyyymmddhhmmss(currDateTime);
                operationLog.setDescription("");
                operationLog.setErrorReason(status.name());
                operationLog.setResultSrc("");
                operationLog.setStatus(status.getCode());
                operationLog.setTargetName(mdsId);
                operationLog.setTargetTypeCode(meterType);
                operationLog.setSupplier(supplier);
                operationLogDao.add(operationLog);
            }
            log.info("save OperationLog end");
        } catch (Exception e) {
            log.warn(e, e);
        }
    }

    /**
     * method name : SMSNotification
     */
    private void SMSNotification(Map<String, Object> params) {

        try {
            String mobileNo = (String) params.get("mobileNo");
            Integer contractId = (Integer) params.get("contractId");
            String smsMsg = (String) params.get("smsMsg");

            Properties prop = new Properties();
            prop.load(getClass().getClassLoader().getResourceAsStream(
                    "config/fmp.properties"));

            String smsClassPath = prop.getProperty("smsClassPath");
            SendSMS obj = (SendSMS) Class.forName(smsClassPath).newInstance();

            Method m = obj.getClass().getDeclaredMethod("send", String.class,
                    String.class, Properties.class);
            String messageId = (String) m.invoke(obj, mobileNo, smsMsg, prop);

            if (!"".equals(messageId)) {
                contractDao.updateSmsNumber(contractId, messageId);
            }
        } catch (Exception e) {
            log.warn(e, e);
        }
    }
}
