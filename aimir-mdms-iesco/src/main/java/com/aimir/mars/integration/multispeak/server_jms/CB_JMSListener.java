package com.aimir.mars.integration.multispeak.server_jms;

import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.multispeak.version_4.CDStatesChangedNotification;
import org.multispeak.version_4.ModifyCBDataForMeters;
import org.multispeak.version_4.ReadingChangedNotification;
import org.springframework.beans.factory.annotation.Autowired;

import com.aimir.mars.integration.multispeak.data.CBMessage;
import com.aimir.mars.integration.multispeak.data.MultiSpeakMessage;
import com.aimir.mars.integration.multispeak.queue_jms.QueueHandler;

public class CB_JMSListener {

    protected static Log log = LogFactory.getLog(CB_JMSListener.class);

    @Autowired
    private QueueHandler handler;

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
    public void handleMessage(CDStatesChangedNotification message) {
        if (log.isDebugEnabled()) {
            log.debug(message);
        }

        // String transactionID = message.getTransactionID();
        // ArrayOfCDStateChange stateChanges = message.getStateChanges();

        /**
         * Callback 서비스는 우리가 내부 테스트 용도로 사용하기 때문에 필요없음.
         */
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
    public void handleMessage(ReadingChangedNotification message) {
        if (log.isDebugEnabled()) {
            log.debug(message);
        }

        // String transactionID = message.getTransactionID();
        // ArrayOfMeterReading1 changedMeterReads = message.getChangedMeterReads();

        /**
         * Callback 서비스는 우리가 내부 테스트 용도로 사용하기 때문에 필요없음.
         */
    }

    /**
     * Allow client to Modify CB data for the Meter object.
     * If this transaction fails, CB returns information in a SOAPFault.
     */
    public void handleMessage(ModifyCBDataForMeters request) {
        if (log.isDebugEnabled()) {
            log.debug(request);
        }

        MultiSpeakMessage message = new CBMessage();
        message.setObject(request);
        message.setRequestedTime(Calendar.getInstance());
        try {
            handler.putServiceData(QueueHandler.CB_MESSAGE, message);
        } catch (Exception e) {
            log.error(e,e);
        }
    }
}
