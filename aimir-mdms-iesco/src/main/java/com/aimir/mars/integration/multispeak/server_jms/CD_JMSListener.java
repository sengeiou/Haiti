package com.aimir.mars.integration.multispeak.server_jms;

import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.multispeak.version_4.InitiateCDStateRequest;
import org.multispeak.version_4.InitiateConnectDisconnect;
import org.springframework.beans.factory.annotation.Autowired;

import com.aimir.mars.integration.multispeak.data.CDMessage;
import com.aimir.mars.integration.multispeak.data.MultiSpeakMessage;
import com.aimir.mars.integration.multispeak.queue_jms.QueueHandler;

public class CD_JMSListener {

    protected static Log log = LogFactory.getLog(CD_JMSListener.class);

    @Autowired
    private QueueHandler handler;

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
}
