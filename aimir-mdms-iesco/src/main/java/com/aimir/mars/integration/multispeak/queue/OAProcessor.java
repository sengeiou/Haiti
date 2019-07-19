package com.aimir.mars.integration.multispeak.queue;

import com.aimir.mars.integration.multispeak.data.OAMessage;
import com.aimir.mars.integration.multispeak.service.AbstractService;
import com.aimir.mars.integration.multispeak.service.ServiceFactory;

/**
 * OA Processor
 * 
 * @author goodjob (goodjob@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2015-07-24 15:59:15 +0900 $,
 */
public class OAProcessor extends Processor {
    @Override
    public void processing(Object obj) throws Exception {

        if (obj instanceof OAMessage) {

            OAMessage message = (OAMessage) obj;
            AbstractService service = ServiceFactory.getServiceFromFactory(message);
            service.execute(message);
        }

    }

    @Override
    public void restore() throws Exception {
    }

}
