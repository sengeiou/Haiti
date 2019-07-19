package com.aimir.fep.protocol.fmp.client.cdma;

import com.aimir.fep.protocol.fmp.common.CDMATarget;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * CDMAClient factory
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class CDMAClientFactory
{
    private static Log log = LogFactory.getLog(
            CDMAClientFactory.class);

    /**
     * get CDMAClient from client pool
     *
     * @param target <code>CDMATarget</code> CDMA packet target
     * @return client <code>CDMAClient</code> MCU CDMA Client
     * @throws Exception
     */
    public synchronized static CDMAClient getClient(
            CDMATarget target,ProcessorHandler handler) 
        throws Exception
    {
        CDMAClient client = null;
        String mcuId = target.getTargetId();
        if(mcuId == null || mcuId.length() < 1)
        {
            log.error("target mcuId is null"); 
            throw new Exception("target mcuId is null"); 
        }

        client = new CDMAClient();
        client.setTarget(target);
        client.setLogProcessor(handler);

        return client;
    }
}
