package com.aimir.mars.integration.metercontrol.queue;

import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Abstract Processor
 *
*/
public abstract class Processor implements MessageListener
{
    // protected static ApplicationContext ctx = null;
    protected Log log = LogFactory.getLog(this.getClass().getName());
    protected String name = null;
    protected String serviceType = null;

    /*
    static {
        if (ctx == null) {
            ctx = new ClassPathXmlApplicationContext(new String[]{"/config/spring-listener.xml"});
        }
    }
    */

    /**
     * processing Service Data
     *
     * @param obj <code>Object</code> Object
     */
    public abstract void processing(Object obj) throws Exception;

    /**
     * restore backup data
     */
    public abstract void restore() throws Exception;

    public void onMessage(Message msg) {
        if (msg instanceof ObjectMessage) {
            try {
                processing(((ObjectMessage) msg).getObject());
            }
            catch (Exception e) {
                log.error(e,e);
            }
        }
        else if(msg instanceof MapMessage){
            try {
                processing(((MapMessage) msg));
            }
            catch (Exception e) {
                log.error(e,e);
            }        	
        }
        else {
            log.warn("Message is not object, check it!!");
        }
    }
}
