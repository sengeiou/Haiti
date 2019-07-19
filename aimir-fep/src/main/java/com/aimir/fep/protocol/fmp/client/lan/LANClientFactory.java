package com.aimir.fep.protocol.fmp.client.lan;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.common.LANTarget;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;

/**
 * TCPClient factory
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class LANClientFactory
{
    private static Log log = LogFactory.getLog(LANClientFactory.class);

    /**
     * get TCPClient from client pool
     *
     * @param target <code>TcpTarget</code> tcp packet target
     * @return client <code>TCPClient</code> MCU TCP Client
     * @throws Exception
     */
    public synchronized static LANClient getClient(
            LANTarget target,ProcessorHandler handler) 
        throws Exception
    {
        LANClient client = null;
        String mcuId = target.getTargetId();
        if(mcuId == null || mcuId.length() < 1)
        {
            log.error("target mcuId is null"); 
            throw new Exception("target mcuId is null"); 
        }

        client = new LANClient();
        client.setTarget(target);
        client.setLogProcessor(handler);

        return client;
    }
}
