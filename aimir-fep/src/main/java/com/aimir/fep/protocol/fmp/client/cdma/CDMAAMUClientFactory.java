package com.aimir.fep.protocol.fmp.client.cdma;

import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.common.CDMATarget;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;

/**
 * CDMAAMUClient factory
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 2. 22. 오후 2:05:53$
 */
public class CDMAAMUClientFactory {

	private static Hashtable clients = new Hashtable();
    private static Log log = LogFactory.getLog(CDMAAMUClientFactory.class);

    /**
     * get CDMAClient from client pool
     *
     * @param target <code>CDMATarget</code> CDMA packet target
     * @return client <code>CDMAClient</code> MCU CDMA Client
     * @throws Exception
     */
    public synchronized static CDMAAMUClient getClient(CDMATarget target, ProcessorHandler handler) 
        throws Exception
    {
        CDMAAMUClient client = null;
        String mcuId = target.getTargetId();
        if(mcuId == null || mcuId.length() < 1)
        {
            log.error("CDMAAMUClient target mcuId is null"); 
            throw new Exception("CDMAAMUClient target mcuId is null"); 
        }

        if(clients.containsKey(mcuId))
        {
            client = (CDMAAMUClient)clients.get(mcuId);
            client.setTarget(target);
            return client;
        }
        client = new CDMAAMUClient();
        client.setTarget(target);
        client.setLogProcessor(handler);
        clients.put(mcuId,client);

        return client;
    }

    /**
     * remove specified CDMAAMUClient from client pool
     *
     * @param mcuId <code>String</code> target mcuId
     */
    public static void removeCDMAAMUClient(String mcuId)
    {
        CDMAAMUClient client = (CDMAAMUClient)clients.remove(mcuId);
        if(client != null)
            client.close();
    }
}


