package com.aimir.mars.integration.multispeak.queue_jms;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.aimir.mars.util.MarsProperty;

/**
 * Queue Handler
 *
 * @author goodjob (goodjob@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2015-07-23 15:59:15 +0900 $,
 */
@Component
public class QueueHandler
{
    private static Log log = LogFactory.getLog(QueueHandler.class);
    public static String CB_MESSAGE = MarsProperty.getProperty("MultiSpeak.CB_MESSAGE","MultiSpeak.CB_MESSAGE?consumer.prefetchSize=100");
    public static String CD_MESSAGE = MarsProperty.getProperty("MultiSpeak.CD_MESSAGE","MultiSpeak.CD_MESSAGE?consumer.prefetchSize=100");
    public static String MR_MESSAGE = MarsProperty.getProperty("MultiSpeak.MR_MESSAGE","MultiSpeak.MR_MESSAGE?consumer.prefetchSize=100"); 
    public static String OA_MESSAGE = MarsProperty.getProperty("MultiSpeak.OA_MESSAGE","MultiSpeak.OA_MESSAGE?consumer.prefetchSize=100");

    @Autowired
    private JmsTemplate jmsTemplate;
    
    /**
     * put Data to Queue
     *
     * @param data <code>Object</code> Data
     */
    public void putServiceData(String serviceType, final Serializable data) throws Exception
    {
        log.debug("Put Data to Queue ==> [" + serviceType + "][" + data.getClass().getName() + "]");
        jmsTemplate.send(serviceType, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                return session.createObjectMessage(data);
            }
        });
    }
}
