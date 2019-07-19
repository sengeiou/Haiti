package com.aimir.schedule.command;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.DataConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.ConnectionType;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;

import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.fep.command.ws.client.CommandWS;
import com.aimir.fep.command.ws.client.CommandWS_Service;

public class CmdManager {
    private static Log log = LogFactory.getLog(CmdManager.class);
    
    private static DataConfiguration config;
    
    static {
        try {
            config = new DataConfiguration(new PropertiesConfiguration("command.properties"));
        } catch (ConfigurationException e) {
            try {
                config = new DataConfiguration(new PropertiesConfiguration("config/command.properties"));
            }
            catch (ConfigurationException ee) {
                log.error(e, e);
            }
        }
    }
    
    public static CommandWS getCommandWS(String protocolName) throws IOException {
        return getCommandWS(Protocol.valueOf(protocolName));
    }
    
    /**
     * This function call getCommandWS(Protocol protocol, String timeout) after parameter "String protocolName" convert to Protocol Enum.<br>
     * The parameter <b>"String timeout"</b> MUST be able to convert <b>"Long"</b> type<br>
     * Related JIRA issue <a href="http://tauran.nuritelecom.com:9393/browse/SP-863">SP-863</a>
     *  
     * @author wll27471297(JiwoongPark)
     * @param protocolName The protocol name of WebSocket
     * @param timeout The time to expire WebSocket (ms)
     * @return getCommandWS(Protocol protocol, String timeout)
     * @throws IOException
     * @see <a href="http://tauran.nuritelecom.com:9393/browse/SP-863">SP-863</a>
     */
    public static CommandWS getCommandWS(String protocolName, String timeout) throws IOException {
        return getCommandWS(Protocol.valueOf(protocolName), timeout);
    }
    
    /**
     * This function call getCommandWS(Protocol protocol, String timeout) after parameter "int timeout" convert to String and parameter "String protocolName" convert to Protocol Enum.<br>
     * The parameter <b>"String timeout"</b> MUST be able to convert <b>"Long"</b> type<br>
     * Related JIRA issue <a href="http://tauran.nuritelecom.com:9393/browse/SP-863">SP-863</a>
     *  
     * @author wll27471297(JiwoongPark)
     * @param protocolName The protocol name of WebSocket
     * @param timeout The time to expire WebSocket (ms)
     * @return getCommandWS(Protocol protocol, String timeout)
     * @throws IOException
     * @see <a href="http://tauran.nuritelecom.com:9393/browse/SP-863">SP-863</a>
     */
    public static CommandWS getCommandWS(String protocolName, int timeout) throws IOException {
        return getCommandWS(Protocol.valueOf(protocolName), String.valueOf(timeout));
    }
    
    /**
     * This function call getCommandWS(Protocol protocol, String timeout) after parameter "int timeout" convert to String.<br>
     * Related JIRA issue <a href="http://tauran.nuritelecom.com:9393/browse/SP-863">SP-863</a>
     *  
     * @author wll27471297(JiwoongPark)
     * @param protocolName The protocol of WebSocket
     * @param timeout The time to expire WebSocket (ms)
     * @return getCommandWS(Protocol protocol, String timeout)
     * @throws IOException
     * @see <a href="http://tauran.nuritelecom.com:9393/browse/SP-863">SP-863</a>
     */
    public static CommandWS getCommandWS(Protocol protocol, int timeout) throws IOException {
        return getCommandWS(protocol, String.valueOf(timeout));
    }
    
    public static CommandWS getCommandWS(Protocol protocol) throws IOException {
        CommandWS port = null;
        try {
            CommandWS_Service ss = new CommandWS_Service(getURL(protocol));
            port = ss.getCommandWSPort();
        }
        catch(Exception e){
        	log.error("################ " + e.getMessage(), e);
        }
        finally {
            if (port != null) {
                Client client = ClientProxy.getClient(port);
                HTTPConduit http = (HTTPConduit)client.getConduit();
                HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
                if(config.getProperty("fep.ws.timeout")!=null && !config.getProperty("fep.ws.timeout").equals("")) {
                    httpClientPolicy.setReceiveTimeout(Long.parseLong((String)config.getProperty("fep.ws.timeout")));
                    log.info("CommandWS Timeout Set : " + (String)config.getProperty("fep.ws.timeout") + " ms");
                } else {
                    httpClientPolicy.setReceiveTimeout(180000);
                    log.info("CommandWS Timeout Set : 180000 ms");
                }
                httpClientPolicy.setAllowChunking(false);
                httpClientPolicy.setConnection(ConnectionType.CLOSE);
                http.setClient(httpClientPolicy);
            }
        }
        
        return port;
    }

    /**
     * This function return WebSocket Object set with protocol and timeout.<br>
     * The parameter <b>"String timeout"</b> MUST be able to convert <b>"Long"</b> type<br>
     * Related JIRA issue <a href="http://tauran.nuritelecom.com:9393/browse/SP-863">SP-863</a>
     * 
     * @author wll27471297(JiwoongPark)
     * @param protocol The protocol of WebSocket
     * @param timeout The time to expire WebSocket (ms)
     * @return CommandWS WebSocket Object
     * @throws IOException
     * @see <a href="http://tauran.nuritelecom.com:9393/browse/SP-863">SP-863</a>
     */
    public static CommandWS getCommandWS(Protocol protocol, String timeout) throws IOException {
        CommandWS port = null;
        try {
            CommandWS_Service ss = new CommandWS_Service(getURL(protocol));
            port = ss.getCommandWSPort();
        }
        catch(Exception e){
        	log.error("################ " + e.getMessage(), e);
        }
        finally {
            if (port != null) {
                Client client = ClientProxy.getClient(port);
                HTTPConduit http = (HTTPConduit)client.getConduit();
                HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
                log.info("CommandWS Timeout Set : " + timeout + " ms");
                httpClientPolicy.setReceiveTimeout(Long.parseLong(timeout));
                httpClientPolicy.setAllowChunking(false);
                httpClientPolicy.setConnection(ConnectionType.CLOSE);
                http.setClient(httpClientPolicy);
            }
        }
        
        return port;
    }
    
    private static URL getURL(Protocol protocol) throws MalformedURLException {
        String url = null;
        if (protocol != null)
            url = (String)config.getProperty("fep.ws." +protocol.name());
        
        if (url == null || "".equals(url))
            url = (String)config.getProperty("fep.ws");
        
        return new URL(url);
    }
}
