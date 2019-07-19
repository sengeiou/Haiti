package com.aimir.mars.integration.multispeak.server;

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
public class AppServer
{
    private static Log log = LogFactory.getLog(AppServer.class);
    
    private String name;
    
    public void init()
    {
        name = System.getProperty("name");
        System.setProperty("name", name);
        
        log.info("\t" + name + " MultiSpeak SERVICE is Ready for Service...\n");
    }
    
    public static void main( String[] args )
    {
        try {
            Properties prop = new Properties();
            prop.load(AppServer.class.getClassLoader()
                    .getResourceAsStream("config/mars.properties"));

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
                log.info("AppServer -port 8089 -httpsPort 8443 -config /config/spring-multispeak.xml");
                return;
            }

            log.info("MultiSpeak SERVICE START "
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
                httpsConnector.setAttribute("keystoreFile", new File(AppServer.class.getClassLoader()
                       .getResource(prop.getProperty("SSL.keystoreFile","config/server_keystore.jks")).toURI()).toPath().toString());
                httpsConnector.setAttribute("clientAuth", "false");
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

            AppServer app = applicationContext.getBean(AppServer.class);
            app.init();

            tomcat.getServer().await();
        }
        catch (Exception e) {
            log.error(e, e);
        }
    }
}
