package com.aimir.mars.integration.multispeak.queue_jms;

import com.aimir.mars.integration.multispeak.data.CBMessage;
import com.aimir.mars.integration.multispeak.service_jms.AbstractService;
import com.aimir.mars.integration.multispeak.service_jms.ServiceFactory;

/**
 * CB Processor
 * 
 * @author goodjob (goodjob@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2015-07-23 15:59:15 +0900 $,
 */
public class CBProcessor extends Processor {

    @Override
    public void processing(Object obj) throws Exception {

        if (obj instanceof CBMessage) {

            CBMessage message = (CBMessage) obj;
            AbstractService service = ServiceFactory.getServiceFromFactory(message);
            service.execute(message);
        }
    }

    @Override
    public void restore() throws Exception {
    }
}
