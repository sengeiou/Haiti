package com.aimir.mars.integration.metercontrol.queue;


import com.aimir.fep.util.DataUtil;
import com.aimir.mars.integration.metercontrol.server.data.MeterConfigureMessage;
import com.aimir.mars.integration.metercontrol.service.AbstractService;
import com.aimir.mars.integration.metercontrol.service.MeterConfigureService;
import com.aimir.mars.integration.metercontrol.service.ServiceFactory;

/**
*
 */
public class MeterConfigureProcessor extends Processor {

    @Override
    public void processing(Object obj) throws Exception {

        if (obj instanceof MeterConfigureMessage) {

            MeterConfigureMessage message = (MeterConfigureMessage) obj;
            AbstractService service = (AbstractService)DataUtil.getBean(MeterConfigureService.class);
            service.execute(message);
        }
    }

    @Override
    public void restore() throws Exception {
    }
}
