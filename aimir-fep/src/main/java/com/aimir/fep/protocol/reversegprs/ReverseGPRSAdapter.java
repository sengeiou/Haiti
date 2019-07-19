package com.aimir.fep.protocol.reversegprs;

import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;

import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.fep.bypass.actions.CommandAction_GD;
import com.aimir.fep.protocol.fmp.frame.service.CommandData;
import com.aimir.fep.util.DataUtil;

/**
 * (<b>Entry point</b>) GPRSModemAdapter server which processing event from GPRS Modem
 *
 * @author goodjob (goodjob@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2015-02-26 15:59:15 +0900 $,
 */
@Service
public class ReverseGPRSAdapter implements ReverseGPRSAdapterMBean, MBeanRegistration
{
    public final static String SERVICE_DOMAIN = "Service";
    public final static String ADAPTER_DOMAIN = "Adapter";
    private static Log log = LogFactory.getLog(ReverseGPRSAdapter.class);
	private ObjectName objectName = null;
	final String[] states = { "Stopped", "Stopping", "Starting", "Started", "Failed",
	"Destroyed" };
	final int STOPPED = 0;
	final int STOPPING = 1;
	final int STARTING = 2;
	final int STARTED = 3;
	final int FAILED = 4;
	final int DESTROYED = 5;
	private int state = STOPPED;
    private String name = "ReverseGPRSAdapter";
    private int PORT = 9000;
    private IoAcceptor acceptor = null;

    SessionCache cache;
    @Autowired
    private CommHandlerAdapter handler;
    
    public ReverseGPRSAdapter()
    {
        //int maxPoolSize = Integer.parseInt(FMPProperty.getProperty("executor.max.pool.size","100"));
        //System.setProperty("actors.maxPoolSize", ""+maxPoolSize);        
        //ExecutorService executor = Executors.newCachedThreadPool();
        //acceptor = new NioSocketAcceptor(executor, new NioProcessor(executor));
        acceptor = new NioSocketAcceptor();
    }

    /**
     * set FMPTrapAdapter Name
     *
     * @param name <code>String</code> name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * set listen port
     *
     * @param port <code>int</code>
     */
    public void setPort(int port)
    {
        this.PORT = port;
    }

    /**
     * start FMPTrapAdapter
     *
     * @throws Exception
     */
    @Override
    public void start() throws Exception
    {
        try {
            objectName = new ObjectName(
                    ADAPTER_DOMAIN+":name="+name);
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            mbs.registerMBean(this, objectName);
            log.debug(this.objectName  + " start");
            state=STARTING;
            handler = DataUtil.getBean(CommHandlerAdapter.class);
            cache = SessionCache.getInstance();
            handler.setSessionCache(cache);
            acceptor.setDefaultLocalAddress(new InetSocketAddress(PORT));
            acceptor.setHandler(handler);
    		acceptor.getFilterChain().addLast(this.name,
                    new ProtocolCodecFilter(new ProtocolCodecFactory()
                    {
                        public ProtocolDecoder getDecoder(IoSession session) throws Exception
                        {
                            return new CommDecoder();
                        }

                        public ProtocolEncoder getEncoder(IoSession session) throws Exception
                        {
                            return new CommEncoder();
                        }
                    }
                    ));
    		acceptor.bind();
            log.info( "ReverseGPRSAdapter Listening on port " + PORT );
            state=STARTED;
            
        }catch(Exception ex)
        {
            log.error("objectName["+this.objectName 
                    +"] start failed");
            state = STOPPED;
            throw ex;
        }
    }

    /**
     * stop FMPTrapAdapter
     *
     * unbind adapter service
     */
    public void stop()
    {
        log.debug(this.objectName + " stop");
        state=STOPPING;
        acceptor.unbind();
        state=STOPPED;
    }

    public static void main( String[] args ) throws Exception
    {
        try {
            ReverseGPRSAdapter adaptor =
                new ReverseGPRSAdapter();
            adaptor.setPort(8888);
            adaptor.start();
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
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
	}

	@Override
	public void preDeregister() throws Exception {

	}

	@Override
	public void postDeregister() {

	}

    @Override
    public String getName() {
        return this.objectName.getCanonicalName();
    }

	@Override
	public int getPort() {
		return this.PORT;
	}

	@Override
	public String getState()
    {
        return states[state];
    }

	@Override
	public CommandData cmdExecute(String targetId, CommandData command) throws Exception{

        long stime = System.currentTimeMillis();
        long ctime = System.currentTimeMillis();
        long responseTimeout = 90000;
        
		CommandData response = null;
		IoSession cmdSession = cache.getActiveSession(targetId);
        if(cmdSession != null && cmdSession.isConnected()){
        	log.info("GetClientSession from =["+targetId+","+cmdSession.getRemoteAddress()+"]");        	
        	CommandAction_GD action = new CommandAction_GD();
        	action.executeReverseGPRSCommand(cmdSession, command);
        	Thread.sleep(500);
        	while(((ctime - stime)) < responseTimeout){
            	response = (CommandData)cmdSession.getAttribute("response");
            	if(response != null){
            		cmdSession.removeAttribute("response");
            		return response;
            	}
            	Thread.sleep(500);
            	ctime = System.currentTimeMillis();
        	}

        }else{
        	throw new Exception("No session for target=["+targetId+"]"+" from ReverseGPRSAdapter");
        }
        return response;        
	}
}