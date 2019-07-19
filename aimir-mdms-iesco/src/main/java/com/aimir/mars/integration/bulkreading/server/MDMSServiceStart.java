package com.aimir.mars.integration.bulkreading.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.mars.util.MarsProperty;

public class MDMSServiceStart {
	
	private static final Logger log = LoggerFactory.getLogger(MDMSServiceStart.class);
	
	public static void main(String[] args) {
		
		try {			
			int port = Integer.parseInt(MarsProperty.getProperty("mdms.web.port","8086"));
			MDMSWebServer server = new MDMSWebServer(port);
			
			server.start();	
			server.join();
			
			log.info("MDMS SERVICE START : PORT " + port);
			
		} catch (InterruptedException e) {

			log.error("[MDMS SERVICE]", e);
			System.exit(1);
		} catch (Exception e){
			System.exit(1); 
		}
	} // end
}
