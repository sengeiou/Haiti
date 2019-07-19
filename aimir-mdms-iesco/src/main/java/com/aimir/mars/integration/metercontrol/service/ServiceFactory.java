package com.aimir.mars.integration.metercontrol.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataUtil;
import com.aimir.mars.integration.metercontrol.server.data.MeterConfigureMessage;

/**
 * Service Factory
 *
 */
public class ServiceFactory
{
    private static Log log = LogFactory.getLog(ServiceFactory.class);
    private static String pkg = null;
    static {
        String clsname = ServiceFactory.class.getName();
        int idx = clsname.lastIndexOf(".");
        pkg = clsname.substring(0,idx+1);
    }

    public static AbstractService getServiceFromFactory(MeterConfigureMessage message)
        throws Exception
    {
    	AbstractService obj = null;
        String clsname = ServiceFactory.class.getName();
        int idx = clsname.lastIndexOf(".");
        pkg = clsname.substring(0,idx+1);
        String service = "Service";
        
        String className = message.getObject().getClass().getSimpleName();

        String serviceName = pkg+className+service;
        log.info("serviceName["+serviceName+"]");
        Class<?> cls = null;

        try{
            cls = Class.forName(serviceName);
            obj = (AbstractService) DataUtil.getBean(cls);

        }catch(Exception ex)
        {
            log.error("getServiceFromFactory failed",ex);
            return null;
        }
        return obj;
    }

}
