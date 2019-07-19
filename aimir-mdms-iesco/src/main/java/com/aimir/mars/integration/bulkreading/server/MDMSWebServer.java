package com.aimir.mars.integration.bulkreading.server;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.SimpleInstanceManager;
import org.eclipse.jetty.annotations.ServletContainerInitializersStarter;
import org.eclipse.jetty.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.plus.annotation.ContainerInitializer;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletHolder; 
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebXmlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

public class MDMSWebServer {
	
	private static final Logger log = LoggerFactory.getLogger(MDMSWebServer.class);
	
	public static final String WEB_APP_ROOT = "META-INF/webapp"; 
    
    private int port;
    private Server server;
    
    public MDMSWebServer(int port) {
    	this.port = port;
    }
	
    public void start()  {
    	
    	log.info("## MDMS WEB SERVER IS RUNNING...");
    	
    	try {	    	
    		
    		server = new Server(new QueuedThreadPool(150, 15));
	        
	    	ServerConnector connector = new ServerConnector(server, new HttpConnectionFactory());
	        connector.setPort(this.port);
	        server.addConnector(connector);
	        
	        WebAppContext context = new WebAppContext();
	        context.setContextPath("/");            
	        context.setDescriptor("/WEB-INF/web.xml");
	        context.setAttribute("javax.servlet.context.tempdir", getScratchDir());
	        context.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
	                ".*/[^/]*servlet-api-[^/]*\\.jar$|.*/javax.servlet.jsp.jstl-.*\\.jar$|.*/.*taglibs.*\\.jar$");       
	        
	        context.setResourceBase(getBaseUrl());
	        context.setAttribute("org.eclipse.jetty.containerInitializers", jspInitializers());
	        context.setAttribute(InstanceManager.class.getName(), new SimpleInstanceManager());
	        context.addBean(new ServletContainerInitializersStarter(context), true);
	        context.addServlet(defaultServletHolder(new ClassPathResource(WEB_APP_ROOT).getURI()), "/");
	        
	        context.setParentLoaderPriority(true);
	        context.setClassLoader(Thread.currentThread().getContextClassLoader());
	        context.setConfigurations(new Configuration[] {new WebXmlConfiguration()});
	        
	        server.setHandler(context);
            server.start();
            
        } catch (Exception e) {
        	e.printStackTrace();
            log.error("Failed to start server", e);
            throw new RuntimeException();
        }

        log.info("## MDMS WEB SERVER started");
    }
    
    private List<ContainerInitializer> jspInitializers() {
    	JettyJasperInitializer sci = new JettyJasperInitializer();
    	ContainerInitializer initializer = new ContainerInitializer(sci, null);
    	List<ContainerInitializer> initializers = new ArrayList<ContainerInitializer>();
    	initializers.add(initializer);
    	return initializers;
    }
    
    private ServletHolder defaultServletHolder(URI baseUri)
    {
        ServletHolder holderDefault = new ServletHolder("default", DefaultServlet.class);
        holderDefault.setInitParameter("resourceBase", baseUri.toASCIIString());
        holderDefault.setInitParameter("dirAllowed", "true");
        return holderDefault;
    }
    
    private ClassLoader getUrlClassLoader() {
        ClassLoader jspClassLoader = new URLClassLoader(new URL[0], this.getClass().getClassLoader());
        return jspClassLoader;
    }
    
    private File getScratchDir() throws IOException
    {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        File scratchDir = new File(tempDir.toString(), "embedded-jetty-jsp");
    
        if (!scratchDir.exists())
        {
            if (!scratchDir.mkdirs())
            {
                throw new IOException("Unable to create scratch directory: " + scratchDir);
            }
        }
        return scratchDir;
    }

    public void join() throws InterruptedException {
        server.join();
    }

    private String getBaseUrl() {
        URL webInfUrl = MDMSWebServer.class.getClassLoader().getResource(WEB_APP_ROOT);
        if (webInfUrl == null) {
        	log.error("Failed to find web application root: " + WEB_APP_ROOT);
            throw new RuntimeException("Failed to find web application root: " + WEB_APP_ROOT);
        }
        return webInfUrl.toExternalForm();
    }
}