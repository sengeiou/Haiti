package com.aimir.mars.integration.multispeak.queue;

import com.aimir.mars.integration.multispeak.data.CDMessage;
import com.aimir.mars.integration.multispeak.service.AbstractService;
import com.aimir.mars.integration.multispeak.service.ServiceFactory;

/**
 * CD Processor
 * 
 * @author goodjob (goodjob@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2015-07-24 15:59:15 +0900 $,
 */
public class CDProcessor extends Processor {
    @Override
    public void processing(Object obj) throws Exception {

        if (obj instanceof CDMessage) {

            CDMessage message = (CDMessage) obj;
            AbstractService service = ServiceFactory.getServiceFromFactory(message);
            service.execute(message);

        }
    }

    @Override
    public void restore() throws Exception {
    }

}
