package com.aimir.mars.integration.multispeak.server_jms;

import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.multispeak.version_4.InitiateMeterReadingsByMeterID;
import org.multispeak.version_4.MeterAddNotification;
import org.multispeak.version_4.MeterChangedNotification;
import org.multispeak.version_4.MeterRemoveNotification;
import org.multispeak.version_4.MeterRetireNotification;
import org.springframework.beans.factory.annotation.Autowired;

import com.aimir.mars.integration.multispeak.data.MRMessage;
import com.aimir.mars.integration.multispeak.data.MultiSpeakMessage;
import com.aimir.mars.integration.multispeak.queue_jms.QueueHandler;

public class MR_JMSListener {

    protected static Log log = LogFactory.getLog(MR_JMSListener.class);

    @Autowired
    private QueueHandler handler;

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

}
