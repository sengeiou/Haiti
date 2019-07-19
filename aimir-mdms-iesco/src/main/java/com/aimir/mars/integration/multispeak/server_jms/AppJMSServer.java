package com.aimir.mars.integration.multispeak.server_jms;

import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.Bus;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.aimir.fep.util.DataUtil;

public class AppJMSServer {
    private static Log log = LogFactory.getLog(AppJMSServer.class);
    
    private String name;
    
    public void init()
    {
        name = System.getProperty("name");
        System.setProperty("name", name);
        
        log.info("\t" + name + " MultiSpeak JMS SERVICE is Ready for Service...\n");
    }
    
    public static void main( String[] args )
    {
        try {
            
            String servicePort = null;
            String configFile = null;

            if (args.length < 1 ) {
                log.info("Usage:");
                log.info("AppServer -config /config/spring-multispeak.xml");
                return;
            }

            for (int i=0; i < args.length; i+=2) {

                String nextArg = args[i];

                if (nextArg.startsWith("-configFile")) {
                    configFile = new String(args[i+1]);
                }
            }
            
            log.info("MultiSpeak SERVICE START=SERVICE PORT["+servicePort+"]");
            ApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[]{configFile}); 
            DataUtil.setApplicationContext(applicationContext);            
            AppJMSServer app = new AppJMSServer();
            app.init();
        }
        catch (Exception e) {
            log.error(e, e);
        }
    }
}
