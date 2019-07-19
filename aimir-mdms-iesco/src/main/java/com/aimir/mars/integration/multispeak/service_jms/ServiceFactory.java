package com.aimir.mars.integration.multispeak.service_jms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataUtil;
import com.aimir.mars.integration.multispeak.data.MultiSpeakMessage;

/**
 * Service Factory
 *
 * @author goodjob
 * @version $Rev: 1 $, $Date: 2015-07-24 15:59:15 +0900 $,
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

    public static AbstractService getServiceFromFactory(MultiSpeakMessage message)
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
