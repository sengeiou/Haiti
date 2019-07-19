package com.aimir.fep.protocol.smsp.server;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsmpp.bean.BindType;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.SMPPSession;
import org.jsmpp.util.AbsoluteTimeFormatter;
import org.jsmpp.util.TimeFormatter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import com.aimir.fep.protocol.smsp.client.sms.SMPP_Listener;
import com.aimir.fep.protocol.smsp.client.sms.SMPP_Submitter;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;

/** 
 * SMPPAdapter 
 * 
 * @version     1.0  2016.07.23 
 * @author		Sung Han LIM 
 */

@Service
public class SMPPAdapter implements DynamicMBean, MBeanRegistration {
    private static Log logger = LogFactory.getLog(SMPPAdapter.class);
    
    public final static String SERVICE_DOMAIN = "Service";
    public final static String ADAPTER_DOMAIN = "Adapter";
    private String name = "SMPPAdapter";
	final String[] states = { "Stopped", "Stopping", "Starting", "Started", "Failed", "Destroyed" };
	final int STOPPED = 0;
	final int STOPPING = 1;
	final int STARTING = 2;
	final int STARTED = 3;
	final int FAILED = 4;
	final int DESTROYED = 5;
	private int state = STOPPED;
    
    private static SMPPSession session;
    final private static int FRAME_OPTION_LEN = 1;
    private static TimeFormatter timeFormatter = new AbsoluteTimeFormatter();
    final AtomicInteger counter = new AtomicInteger();
    private String eui_Id;
    private ObjectName objectName;
    
    public String sendSMS(HashMap<String, Object> condition) throws Exception {
    	String messageId = null;
		session = getSMPPSession();
        
        // Send Message
        SMPP_Submitter smpp_Submitter = new SMPP_Submitter();
        messageId = smpp_Submitter.sendSMS(session, condition);
        
        return messageId;
    }
    
    public SMPPSession getSMPPSession() {
        return session;
    }

    public void setSMPPSession(SMPPSession session) {
        this.session = session;
    }
    
    public String getEui_Id() {
        return eui_Id;
    }

    public void setEui_Id(String eui_Id) {
        this.eui_Id = eui_Id;
    }
    
    /**
     * MBean 서비스를 생성하고 SMPP 리스너를 실행한다.
     * @throws Exception
     */
    public void startService() throws Exception {
        String smscServer = FMPProperty.getProperty("smpp.hostname");
        String smscPort = FMPProperty.getProperty("smpp.port");
        String smscUserName = FMPProperty.getProperty("smpp.username");
        String smscPassword = FMPProperty.getProperty("smpp.password");
//      String smscServer = "smsc1.com4.no";
//      String smscPort = "9000"; 
//      String smscUserName = "validerams"; 
//      String smscPassword = "U91nDBr"; 
        
        logger.info("smscServer : " + smscServer + ", smscPort : " + smscPort
        			+ ", smscUserName : " + smscUserName + ", smscPassword :" + smscPassword);
        
        objectName = new ObjectName(ADAPTER_DOMAIN+":name="+name);
		state = STARTING;
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		mbs.registerMBean(this, objectName);
		
     	session = new SMPPSession();
        session.setMessageReceiverListener(new SMPP_Listener(this));
        setSMPPSession(session);
        
        try {
            session.connectAndBind(smscServer, Integer.parseInt(smscPort), 
                    new BindParameter(BindType.BIND_TRX, smscUserName, smscPassword, "cp",
                    TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, null));
        } catch (IOException e) {
            logger.error(e, e);
            logger.debug("Failed connect and bind to host");
        }
        
        logger.info("\tSMPP Listener is Ready for Service...\n");
    }    
    
    @Override
    public ObjectName preRegister(MBeanServer server, ObjectName name)
            throws Exception {
        if (objectName == null) 
        {
            objectName = new ObjectName(server.getDefaultDomain() 
                    + ":service=" + this.getClass().getName());
        }
        
        return this.objectName;
    }

    @Override
    public void postRegister(Boolean registrationDone) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void preDeregister() throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void postDeregister() {
        // TODO Auto-generated method stub
        session.unbindAndClose();
    }

    @Override
    public Object getAttribute(String attribute)
            throws AttributeNotFoundException, MBeanException,
            ReflectionException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setAttribute(Attribute attribute)
            throws AttributeNotFoundException, InvalidAttributeValueException,
            MBeanException, ReflectionException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public AttributeList getAttributes(String[] attributes) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AttributeList setAttributes(AttributeList attributes) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object invoke(String actionName, Object[] params, String[] signature)
            throws MBeanException, ReflectionException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MBeanInfo getMBeanInfo() {

        return new MBeanInfo(
                this.getClass().getName(),
                "SMPPAdapterMBean",
                null,
                null,
                null,
                null);
    }
    
    public static void main(String[] args) {
        try {
            ApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[]{"/config/spring-smpp.xml"}); 
            DataUtil.setApplicationContext(applicationContext);
            
            SMPPAdapter adapter = applicationContext.getBean(SMPPAdapter.class);
            adapter.startService();
        }
        catch (Exception e) {
            logger.error(e, e);
        }
    }
}