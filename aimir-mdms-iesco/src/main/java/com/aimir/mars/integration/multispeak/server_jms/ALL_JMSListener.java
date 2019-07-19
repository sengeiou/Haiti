package com.aimir.mars.integration.multispeak.server_jms;

import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.multispeak.version_4.AssessmentLocationChangedNotification;
import org.multispeak.version_4.CDStatesChangedNotification;
import org.multispeak.version_4.InitiateCDStateRequest;
import org.multispeak.version_4.InitiateConnectDisconnect;
import org.multispeak.version_4.InitiateMeterReadingsByMeterID;
import org.multispeak.version_4.MeterAddNotification;
import org.multispeak.version_4.MeterChangedNotification;
import org.multispeak.version_4.MeterRemoveNotification;
import org.multispeak.version_4.MeterRetireNotification;
import org.multispeak.version_4.ModifyCBDataForMeters;
import org.multispeak.version_4.ReadingChangedNotification;
import org.springframework.beans.factory.annotation.Autowired;

import com.aimir.mars.integration.multispeak.data.CBMessage;
import com.aimir.mars.integration.multispeak.data.CDMessage;
import com.aimir.mars.integration.multispeak.data.MRMessage;
import com.aimir.mars.integration.multispeak.data.MultiSpeakMessage;
import com.aimir.mars.integration.multispeak.data.OAMessage;
import com.aimir.mars.integration.multispeak.queue_jms.QueueHandler;

/**
 * 각각의 JMS Listener을 하나로 통합.
 * @author lucky
 *
 */
public class ALL_JMSListener {
    protected static Log log = LogFactory.getLog(ALL_JMSListener.class);

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

    public void handleMessage(InitiateConnectDisconnect request) {
        if (log.isDebugEnabled()) {
            log.debug(request);
        }

        MultiSpeakMessage message = new CDMessage();
        message.setObject(request);
        message.setRequestedTime(Calendar.getInstance());
        try {
            handler.putServiceData(QueueHandler.CD_MESSAGE, message);
        } catch (Exception e) {
            log.error(e,e);
        }
    }

    public void handleMessage(InitiateCDStateRequest request) {
        if (log.isDebugEnabled()) {
            log.debug(request);
        }

        MultiSpeakMessage message = new CDMessage();
        message.setObject(request);
        message.setRequestedTime(Calendar.getInstance());
        try {
            handler.putServiceData(QueueHandler.CD_MESSAGE, message);
        } catch (Exception e) {
            log.error(e,e);
        }
    }

    /**
     * CB requests a new meter reading from MR, on meters selected by meterID.
     * MR returns information about failed transactions using an array of
     * errorObjects.
     * 
     * The meter reading is returned to the CB in the form of a meterReading, an
     * intervalData block, or a formattedBlock, sent to the URL specified in the
     * responseURL. The transactionID calling parameter links this Initiate
     * request with the published data method call. The expiration time
     * parameter indicates the amount of time for which the publisher should try
     * to obtain and publish the data;
     * 
     * if the publisher has been unsuccessful in publishing the data after the
     * expiration time, then the publisher will discard the request and the
     * requester should not expect a response.
     */
    public void handleMessage(InitiateMeterReadingsByMeterID request) {
        if (log.isDebugEnabled()) {
            log.debug(request);
        }

        MultiSpeakMessage message = new MRMessage();
        message.setObject(request);
        message.setRequestedTime(Calendar.getInstance());
        try {
            handler.putServiceData(QueueHandler.MR_MESSAGE, message);
        } catch (Exception e) {
            log.error(e,e);
        }
    }

    /**
     * Publisher notifies MR to Add the associated meter(s). MR returns
     * information about failed transactions using an array of errorObjects.
     * 
     * The message header attribute 'registrationID' should be added to all
     * publish messages to indicate to the subscriber under which registrationID
     * they received this notification data.
     */
    public void handleMessage(MeterAddNotification request) {
        if (log.isDebugEnabled()) {
            log.debug(request);
        }

        MultiSpeakMessage message = new MRMessage();
        message.setObject(request);
        message.setRequestedTime(Calendar.getInstance());
        try {
            handler.putServiceData(QueueHandler.MR_MESSAGE, message);
        } catch (Exception e) {
            log.error(e,e);
        }
    }

    /**
     * Publisher notifies MR to remove the associated meter(s). MR returns
     * information about failed transactions using an array of errorObjects. The
     * message header attribute 'registrationID' should be added to all publish
     * messages to indicate to the subscriber under which registrationID they
     * received this notification data.
     */
    public void handleMessage(MeterRemoveNotification request) {
        if (log.isDebugEnabled()) {
            log.debug(request);
        }

        MultiSpeakMessage message = new MRMessage();
        message.setObject(request);
        message.setRequestedTime(Calendar.getInstance());
        try {
            handler.putServiceData(QueueHandler.MR_MESSAGE, message);
        } catch (Exception e) {
            log.error(e,e);
        }
    }

    /**
     * Publisher notifies MR to retire the associated meter(s). MR returns
     * information about failed transactions using an array of errorObjects. The
     * message header attribute 'registrationID' should be added to all publish
     * messages to indicate to the subscriber under which registrationID they
     * received this notification data.
     */
    public void handleMessage(MeterRetireNotification request) {
        if (log.isDebugEnabled()) {
            log.debug(request);
        }

        MultiSpeakMessage message = new MRMessage();
        message.setObject(request);
        message.setRequestedTime(Calendar.getInstance());
        try {
            handler.putServiceData(QueueHandler.MR_MESSAGE, message);
        } catch (Exception e) {
            log.error(e,e);
        }
    }

    public void handleMessage(MeterChangedNotification request) {
        if (log.isDebugEnabled()) {
            log.debug(request);
        }

        MultiSpeakMessage message = new MRMessage();
        message.setObject(request);
        message.setRequestedTime(Calendar.getInstance());
        try {
            handler.putServiceData(QueueHandler.MR_MESSAGE, message);
        } catch (Exception e) {
            log.error(e,e);
        }
    }

    /**
     * Publisher notifies subscriber that assessmentLocations have been published or updated.
     * Subscriber returns information about failed transactions using an array of errorObjects.
     * The message header attribute 'registrationID' should be added to all publish messages to 
     * indicate to the subscriber under which registrationID they received this notification data.
     */
    public void handleMessage(AssessmentLocationChangedNotification request) {
        if (log.isDebugEnabled()) {
            log.debug(request);
        }

        MultiSpeakMessage message = new OAMessage();
        message.setObject(request);
        message.setRequestedTime(Calendar.getInstance());
        try {
            handler.putServiceData(QueueHandler.OA_MESSAGE, message);
        } catch (Exception e) {
            log.error(e,e);
        }
    }
}
