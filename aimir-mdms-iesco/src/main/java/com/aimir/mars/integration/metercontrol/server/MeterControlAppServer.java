package com.aimir.mars.integration.metercontrol.server;

import java.io.File;
import java.util.Properties;

import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.Bus;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.aimir.fep.util.DataUtil;

@Service
public class MeterControlAppServer
{
    private static Log log = LogFactory.getLog(MeterControlAppServer.class);
    
    private String name;
    
    public void init()
    {
        name = System.getProperty("name");
        System.setProperty("name", name);
        
        log.info("\t" + name + " MeterConfigure SERVICE is Ready for Service...\n");
    }
    
    public static void main( String[] args )
    {
        try {
            Properties prop = new Properties();
            prop.load(MeterControlAppServer.class.getClassLoader()
                    .getResourceAsStream("config/metercontrol.properties"));

            String servicePort = null;
            String serviceHttpsPort = null;
            String configFile = null;

            for (int i=0; i < args.length; i+=2) {

                String nextArg = args[i];

                if (nextArg.startsWith("-port") && args.length > i+1) {
                    servicePort = new String(args[i + 1]);
                }
                if (nextArg.startsWith("-httpsPort") && args.length > i+1) {
                    serviceHttpsPort = new String(args[i + 1]);
                }
                if (nextArg.startsWith("-configFile") && args.length > i+1) {
                    configFile = new String(args[i+1]);
                }
            }

            if (servicePort == null && serviceHttpsPort == null) {
                log.info("Usage:");
                log.info("AppServer -httpsPort 8450 -config /config/spring-metercontrol-ws.xml");
                return;
            }
            		
            log.info("Web SERVICE START "
                    + (servicePort != null ? "HTTP PORT[" + servicePort + "] " : "")
                    + (serviceHttpsPort != null ? "HTTPS PORT[" + serviceHttpsPort + "]" : ""));
            ApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[]{configFile}); 
            DataUtil.setApplicationContext(applicationContext);
            Tomcat tomcat = new Tomcat();
            if(servicePort!=null) {
                tomcat.setPort(Integer.parseInt(servicePort));
            }
            if(serviceHttpsPort!=null) {
                Connector httpsConnector = new Connector();
                httpsConnector.setPort(Integer.parseInt(serviceHttpsPort));
                httpsConnector.setSecure(true);
                httpsConnector.setScheme("https");
                httpsConnector.setAttribute("keyAlias", prop.getProperty("SSL.keyAlias","aimir"));
                httpsConnector.setAttribute("keystorePass", prop.getProperty("SSL.keystorePass","aimiramm"));
                httpsConnector.setAttribute("keystoreFile", new File(MeterControlAppServer.class.getClassLoader()
                       .getResource(prop.getProperty("SSL.keystoreFile","config/server_keystore.jks")).toURI()).toPath().toString());
                boolean clientAuth = Boolean.parseBoolean(prop.getProperty("SSL.clientAuth","false"));
                if ( clientAuth ) {
                    httpsConnector.setAttribute("truststoreType", prop.getProperty("SSL.truststoreType","JKS"));
                    httpsConnector.setAttribute("truststorePass", prop.getProperty("SSL.truststorePass","aimiramm"));
                    httpsConnector.setAttribute("truststoreFile", new File(MeterControlAppServer.class.getClassLoader()
                           .getResource(prop.getProperty("SSL.truststoreFile","config/server_truststore.jks")).toURI()).toPath().toString());
                }
                httpsConnector.setAttribute("clientAuth", clientAuth);
                String crlFile = prop.getProperty("SSL.crlFile","");
                if ( crlFile.length() > 0 ) {
                	httpsConnector.setAttribute("crlFile",new File(MeterControlAppServer.class.getClassLoader()
                           .getResource(crlFile).toURI()).toPath().toString());
                }
                httpsConnector.setAttribute("SSLEnabled", true);
                org.apache.catalina.Service service = tomcat.getService();
                service.addConnector(httpsConnector);
            }

            Context context = tomcat.addContext("", "");
            Wrapper servletWrap = context.createWrapper();
            servletWrap.setName("cxf");
            CXFNonSpringServlet servlet = new CXFNonSpringServlet();

            // Wire the bus that endpoint uses with the Servlet
            servlet.setBus((Bus) applicationContext.getBean("cxf"));
            servletWrap.setServlet(servlet);
            servletWrap.setLoadOnStartup(1);
            context.addChild(servletWrap);
            context.addServletMapping("/services/*", "cxf");
            System.out.println(context.getCatalinaBase().getAbsolutePath()
                    + File.separator + "webapps");
            File webapps = new File(context.getCatalinaBase().getAbsolutePath()
                    + File.separator + "webapps");
            if (!webapps.exists())
                webapps.mkdir();
            tomcat.start();

            MeterControlAppServer app = applicationContext.getBean(MeterControlAppServer.class);
            app.init();

            tomcat.getServer().await();
        }
        catch (Exception e) {
            log.error(e, e);
        }
    }
}
