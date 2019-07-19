package com.oracle.xmlns.ssys;

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
import org.springframework.stereotype.Service;

import com.aimir.fep.util.DataUtil;

@Service
public class TestServer {
    private static Log log = LogFactory.getLog(TestServer.class);

    public void init()
    {
        log.info("Test SERVICE is Ready for Service...\n");
    }

    public static void main( String[] args )
    {
        try {
            
            String servicePort = null;
            String configFile = null;

            if (args.length < 3 ) {
                log.info("Usage:");
                log.info("TestServer -port CommunicationPort -config /config/spring-osb-ws-test.xml");
                return;
            }

            for (int i=0; i < args.length; i+=2) {

                String nextArg = args[i];

                if (nextArg.startsWith("-port")) {
                    servicePort = new String(args[i+1]);
                }
                if (nextArg.startsWith("-configFile")) {
                    configFile = new String(args[i+1]);
                }
            }

            log.info("Test SERVICE START=SERVICE PORT["+servicePort+"]");
            ApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[]{configFile}); 
            DataUtil.setApplicationContext(applicationContext);
            Tomcat tomcat = new Tomcat();
            tomcat.setPort(Integer.parseInt(servicePort));
            Context context = tomcat.addContext("", "");
            Wrapper servletWrap = context.createWrapper();
            servletWrap.setName("cxf");
            CXFNonSpringServlet servlet = new CXFNonSpringServlet();
            // Wire the bus that endpoint uses with the Servlet
            servlet.setBus((Bus)applicationContext.getBean("cxf"));
            servletWrap.setServlet(servlet);
            servletWrap.setLoadOnStartup(1);
            context.addChild(servletWrap);
            context.addServletMapping("/services/*", "cxf");
            File webapps = new File(context.getCatalinaBase().getAbsolutePath() + File.separator + "webapps");
            if (!webapps.exists()) webapps.mkdir();
            tomcat.start();

//            TestServer app = applicationContext.getBean(TestServer.class);
//            app.init();
            for(String name : applicationContext.getBeanDefinitionNames()) {
                log.debug(name);
            }

            tomcat.getServer().await();
        }
        catch (Exception e) {
            log.error(e, e);
        }
    }
}
