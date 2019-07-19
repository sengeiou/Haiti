package com.aimir.mars.integration.multispeak.service_jms;

import java.util.Calendar;
import java.util.Properties;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.multispeak.version_4.ElectricMeter;
import org.multispeak.version_4.Meters;
import org.multispeak.version_4.ModifyCBDataForMeters;
import org.multispeak.version_4.MultiSpeakMsgHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Service;

import com.aimir.dao.device.MeterDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.mars.integration.multispeak.data.MultiSpeakMessage;

import _1_release.cpsm_v4.IdentifiedObject;

@Service
public class ModifyCBDataForMetersService extends AbstractService {

    private static Log log = LogFactory
            .getLog(ModifyCBDataForMetersService.class);

    @Autowired
    private CommandGW command;

    @Autowired
    private MeterDao meterDao;

    @Autowired
    private JmsTemplate mrInJmsTemplate;

    @Override
    public void execute(MultiSpeakMessage message) throws Exception {

        Calendar requestedTime = message.getRequestedTime();
        Object obj = message.getObject();
        MultiSpeakMsgHeader multiSpeakMsgHeader = message
                .getMultiSpeakMsgHeader();
        Properties prop = new Properties();
        prop.load(getClass().getClassLoader()
                .getResourceAsStream("config/multispeak.properties"));

        multiSpeakMsgHeader.setUserID(
                prop.getProperty("multispeak.response.userid", "nuri"));
        multiSpeakMsgHeader.setPwd(
                prop.getProperty("multispeak.response.passwd", "nuri_headend"));

        ModifyCBDataForMeters request = (ModifyCBDataForMeters) obj;
        String responseURL = ""; // TODO SET RESPONSE URL;
        Calendar expirationDateTime = Calendar.getInstance();
        expirationDateTime.setTime(requestedTime.getTime());

        Meters meters = request.getMeterData();

        for (ElectricMeter electricMeter : meters.getElectricMeters()
                .getElectricMeter()) {

            String meterNo = electricMeter.getMeterNo();
            String lpCycle = electricMeter.getBillingCycle();
            IdentifiedObject object = electricMeter.getIdentifiedObject();

            if (lpCycle != null && !"".equals(lpCycle)) {

            }

            if (object != null) {
                String mRID = object.getMRID();
            }

        }

        Meters changedMeters = null;

        Calendar currentTime = Calendar.getInstance();

        // log.info("currentTime="+currentTime+"
        // expirationDateTime="+expirationDateTime);

        mrInJmsTemplate.convertAndSend(changedMeters,
                new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message)
                    throws JMSException {
                if (message instanceof BytesMessage) {
                    BytesMessage messageBody = (BytesMessage) message;
                    messageBody.reset();
                    Long length = messageBody.getBodyLength();
                    log.debug("***** MESSAGE LENGTH is " + length
                            + " bytes");
                    byte[] byteMyMessage = new byte[length
                            .intValue()];
                    int red = messageBody.readBytes(byteMyMessage);
                    log.debug("***** SENDING MESSAGE - \n"
                            + "<!-- MSG START -->\n"
                            + new String(byteMyMessage)
                            + "\n<!-- MSG END -->");
                }
                return message;
            }
        });

        log.debug("Send MeterChangedNotification Response..");

        log.debug("ModifyCBDataForMetersService execute end..");
    }
}
