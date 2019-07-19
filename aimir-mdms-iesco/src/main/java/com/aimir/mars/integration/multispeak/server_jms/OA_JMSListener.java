package com.aimir.mars.integration.multispeak.server_jms;

import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.multispeak.version_4.AssessmentLocationChangedNotification;
import org.springframework.beans.factory.annotation.Autowired;

import com.aimir.mars.integration.multispeak.data.MultiSpeakMessage;
import com.aimir.mars.integration.multispeak.data.OAMessage;
import com.aimir.mars.integration.multispeak.queue_jms.QueueHandler;

public class OA_JMSListener {

    protected static Log log = LogFactory.getLog(OA_JMSListener.class);

    @Autowired
    private QueueHandler handler;

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
