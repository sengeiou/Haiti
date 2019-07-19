package com.aimir.fep.protocol.nip.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.CharacterCodingException;
import java.util.concurrent.Executors;

import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.DatagramSessionConfig;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;

import com.aimir.constants.CommonConstants;
import com.aimir.fep.protocol.fmp.server.FMPSslContextFactory;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.system.Code;
import com.aimir.fep.protocol.nip.server.wssl.*;

@Deprecated
public class NiUdpDtlsAdapter implements NiUdpDtlsAdapterMBean, MBeanRegistration {
    private static Log log = LogFactory.getLog(NiUdpDtlsAdapter.class);
    public int PORT = 8002;
    public Integer protocolType = 0;
    private ObjectName objectName = null;
    private WsslServer server = null;
    
    public NiUdpDtlsAdapter() throws Exception, IOException, MalformedObjectNameException{
        //objectName = new ObjectName("NiUdpDtlsAdapter");
       log.debug("NiUdpDtlsAdapter");
        server = new WsslServer();
        server.setPort(PORT);
 
    }
    
    public String getName() {
        return objectName.toString();
    }

    public int getPort() {
        return PORT;
    }

    public void setPort(int port) {
        this.PORT = port;
        if ( server != null ){
        	server.setPort(PORT);
        }
    }
       

    public void start() throws Exception{
    	server.start();
    };  
 

    public void stop() {
    	server.stop();
    }

    public String getState() {
        // TODO Auto-generated method stub
        return null;
    }

	public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception {
	       log.debug("NiUdpDtlsAdapter.preRegister");
        if (name == null) 
        {
            name = new ObjectName(server.getDefaultDomain() 
                    + ":service=" + this.getClass().getName());
        }

        this.objectName = name;
        return this.objectName;
	}

	public void postRegister(Boolean registrationDone) {
		// TODO Auto-generated method stub
		
	}

	public void preDeregister() throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void postDeregister() {
		// TODO Auto-generated method stub
		
	}

	public Integer getProtocolType() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getProtocolTypeString() {
		// TODO Auto-generated method stub
		return null;
	}
}
