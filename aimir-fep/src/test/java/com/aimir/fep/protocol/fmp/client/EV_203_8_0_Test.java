package com.aimir.fep.protocol.fmp.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.common.GPRSTarget;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.datatype.TIMESTAMP;
import com.aimir.fep.protocol.fmp.frame.ServiceDataConstants;
import com.aimir.fep.protocol.fmp.frame.service.EventData;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.MIBUtil;
import com.aimir.util.TimeUtil;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Send Event Test Class
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class EV_203_8_0_Test
{
    private static Log log = LogFactory.getLog(EV_203_8_0_Test.class);
    static {
        DataUtil.setApplicationContext(new ClassPathXmlApplicationContext(new String[]{"/config/spring.xml"}));
    }
    
    @Test
    public void test_setLastLinkTime()
    {
        try {
            log.info("targetIp="+System.getProperty("targetIp") + ", port=" + System.getProperty("targetPort"));
            
            MIBUtil mibUtil = MIBUtil.getInstance();
            EventData event = new EventData();
            event.setCode(mibUtil.getOid("eventUnknownSensor"));
            event.setSrcType(ServiceDataConstants.E_SRCTYPE_MCU);
            //event.setSrcId(new HEX("76F0AC87DBB7E45C"));
            event.setTimeStamp(new TIMESTAMP(TimeUtil.getCurrentTime()));
            SMIValue smiValue = null;
            smiValue = DataUtil.getSMIValueByObject("sensorID", "000D6F0000234633"); 
            event.append(smiValue);
            log.info("event : "+ event);
            GPRSTarget target = new GPRSTarget(System.getProperty("targetIp"),
                    Integer.parseInt(System.getProperty("targetPort")));
            target.setTargetId("1001");
            Client client = ClientFactory.getClient(target);
            client.sendEvent(event);
        }
        catch (Exception e) {
            log.error(e);
        }
    }
}
