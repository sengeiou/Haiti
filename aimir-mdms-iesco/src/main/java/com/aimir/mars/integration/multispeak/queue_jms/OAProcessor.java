package com.aimir.mars.integration.multispeak.queue_jms;

import com.aimir.mars.integration.multispeak.data.OAMessage;
import com.aimir.mars.integration.multispeak.service_jms.AbstractService;
import com.aimir.mars.integration.multispeak.service_jms.ServiceFactory;

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
