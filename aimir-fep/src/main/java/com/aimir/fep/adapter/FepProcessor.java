
package com.aimir.fep.adapter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.remoting.jaxws.SimpleHttpServerJaxWsServiceExporter;

import com.aimir.constants.CommonConstants;
import com.aimir.fep.logger.snowflake.SnowflakeGeneration;
import com.aimir.fep.util.DataUtil;
import com.aimir.util.ContextUtil;

/**
 * MOA Startup class
 *
 * 2003.11.17
 */
@Service
public class FepProcessor {
    private static Log logger = LogFactory.getLog(FepProcessor.class);

    public final static String SERVICE_DOMAIN = "Service";
    public final static String ADAPTER_DOMAIN = "Adapter";
    
    private String fepName;
    
    public void init()
    {
        fepName = System.getProperty("name");
        System.setProperty("fepName", fepName);
        
        CommonConstants.refreshContractStatus();
        CommonConstants.refreshDataSvc();
        CommonConstants.refreshGasMeterAlarmStatus();
        CommonConstants.refreshGasMeterStatus();
        CommonConstants.refreshHeaderSvc();
        CommonConstants.refreshInterface();
        CommonConstants.refreshMcuType();
        CommonConstants.refreshMeterStatus();
        CommonConstants.refreshMeterType();
        CommonConstants.refreshModemPowerType();
        CommonConstants.refreshModemType();
        CommonConstants.refreshProtocol();
        CommonConstants.refreshSenderReceiver();
        CommonConstants.refreshWaterMeterAlarmStatus();
        CommonConstants.refreshWaterMeterStatus();
        
        logger.info("\t" + fepName + " FEPd is Ready for Service...\n");
    }

    public static void main(String[] args) {
        boolean enableWS = false;
        
        for (int i=0; i < args.length; i+=2) {

            String nextArg = args[i];

            if (nextArg.startsWith("-enableWS")) {
                enableWS = Boolean.parseBoolean(args[i+1]);
            }
        }
        
        DataUtil.setApplicationContext(new ClassPathXmlApplicationContext(new String[]{"/config/spring-fepd.xml"}));
        ContextUtil.setApplicationContext(new ClassPathXmlApplicationContext(new String[]{"/config/spring-fepd.xml"}));
        FepProcessor fep = DataUtil.getBean(FepProcessor.class);
        fep.init();
        
        SnowflakeGeneration.getInstance();
        /*
        if (!enableWS) {
            SimpleHttpServerJaxWsServiceExporter exporter = DataUtil.getBean(SimpleHttpServerJaxWsServiceExporter.class);
            exporter.destroy();
        }
        */
    }
}