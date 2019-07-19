package com.aimir.fep.protocol.smsp.client.sms;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsmpp.bean.AlertNotification;
import org.jsmpp.bean.DataSm;
import org.jsmpp.bean.DeliverSm;
import org.jsmpp.bean.DeliveryReceipt;
import org.jsmpp.bean.MessageType;
import org.jsmpp.extra.ProcessRequestException;
import org.jsmpp.session.DataSmResult;
import org.jsmpp.session.MessageReceiverListener;
import org.jsmpp.session.Session;
import org.jsmpp.util.InvalidDeliveryReceiptException;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionStatus;

import com.aimir.dao.device.AsyncCommandResultDao;
import com.aimir.fep.protocol.smsp.command.frame.sms.ResponseFrame;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.AsyncCommandResult;

/** 
 * SMS_Receiver 
 * 
 * @version     1.0  2016.07.23 
 * @author		Sung Han LIM 
 */

public class SMS_Receiver implements MessageReceiverListener {
	private static Log logger = LogFactory.getLog(SMS_Receiver.class);
	
	public SMS_Receiver() {
	}
	
    public void onAcceptDeliverSm(DeliverSm deliverSm) throws ProcessRequestException {
        logger.debug(deliverSm.toString());
        if (MessageType.SMSC_DEL_RECEIPT.containedIn(deliverSm.getEsmClass())) { 
            try {
                // JASMIN Gateway SMPP Server API 사용하면 sub/dlr 값이 없어서 무조건 exception 발생한다.
                // HTTP API를 사용하고 dlr_level 2 or 3를 사용하면 문제 없다.
                DeliveryReceipt delReceipt = deliverSm.getShortMessageAsDeliveryReceipt();
                
                // long id = Long.parseLong(delReceipt.getId()) & 0xffffffff;
                // String messageId = null; 
                // messageId = Long.toString(id, 16).toUpperCase();
                
                logger.debug("Receiving delivery receipt for message from "
                        + deliverSm.getSourceAddr() + " to "
                        + deliverSm.getDestAddress() + " : " + delReceipt);
            } catch (InvalidDeliveryReceiptException e) {}
        } else {
        	String result = new String(deliverSm.getShortMessage());
        	Map<String, Object> resultMap = new HashMap<String,Object>();
        	ResponseFrame responseFrame = new ResponseFrame();
        	String euiId = null;
        	String sequence = null; 
        	
        	try {
				resultMap = responseFrame.decode(result);
				euiId = resultMap.get("euiId").toString();
				sequence = resultMap.get("sequence").toString();
			} catch (Exception e) {
				logger.error("SMS_Receiver produce -" + e, e);
			}
        	
			// SMS 응답받은 결과 저장 로직 (S)
			if (result != null) {
				try {
					saveAsyncCommandResult(euiId, Long.parseLong(sequence), result);

					logger.info("====================================");
					logger.info("Received MSG [" + sequence + "] Result Save - OK");
					logger.info("Received Result [" + result + "]");
					logger.info("====================================");
				} catch (Exception e) {
					logger.error("SMS_Receiver produce -" + e, e);
					logger.error(e, e);

					try {
						saveAsyncCommandResult(euiId, Long.parseLong(sequence), null);

						logger.info("====================================");
						logger.info("Received MSG [" + sequence + "] Result Save - NULL");
						logger.info("====================================");
					} catch (Exception ignore) {}

				}
			}
			// SMS 응답받은 결과 저장 로직 (E)
		}
	}
    
    private void saveAsyncCommandResult(String mcuId, long trId, String result) throws Exception {
        JpaTransactionManager txManager = null;
        TransactionStatus txStatus = null;
        
        try {
            txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
            txStatus = txManager.getTransaction(null);
            
            AsyncCommandResult asyncCommandResult = new AsyncCommandResult();
            AsyncCommandResultDao commandResultDao = DataUtil.getBean(AsyncCommandResultDao.class);
            asyncCommandResult.setTrId(trId);
            asyncCommandResult.setMcuId(mcuId);
            asyncCommandResult.setNum(0);
            asyncCommandResult.setResultType("SMS_Response");
            asyncCommandResult.setResultValue(result);
            asyncCommandResult.setTrType("SMS");
            commandResultDao.add(asyncCommandResult);
            txManager.commit(txStatus);
            
        } catch (Exception e) {
            logger.error("SMS_Receiver produce -" + e, e);
            if(txStatus != null){
                txManager.rollback(txStatus);
            }
            throw e;
        }
    }
    
    public void onAcceptAlertNotification(AlertNotification alertNotification) {
    }
    
    public DataSmResult onAcceptDataSm(DataSm dataSm, Session source) throws ProcessRequestException {
        return null;
    }
}