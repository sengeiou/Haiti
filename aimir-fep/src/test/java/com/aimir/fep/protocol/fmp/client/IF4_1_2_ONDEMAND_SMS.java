package com.aimir.fep.protocol.fmp.client;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.orm.jpa.JpaTransactionManager;

import com.aimir.fep.BaseTestCase;
import com.aimir.fep.bypass.BypassRegister;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.protocol.fmp.client.sms.SMSClient;
import com.aimir.fep.protocol.mrp.command.frame.sms.RequestFrame;
import com.aimir.fep.util.DataUtil;

public class IF4_1_2_ONDEMAND_SMS extends BaseTestCase{
	
    private static Log log = LogFactory.getLog(IF4_1_2_ONDEMAND_SMS.class);
    
    @Test
    public void testOndemandMetering() {
    	
        String modemSerial = "";

        JpaTransactionManager txManager = DataUtil.getBean(JpaTransactionManager.class);
        CommandGW cgw = DataUtil.getBean(CommandGW.class);
        
        try {
    	    Object result = cgw.cmdSendSMS(
    	    		modemSerial,
                    RequestFrame.CMD_GPRSCONNECT_OID,
                    String.valueOf(SMSClient.getSEQ()),
                    8900+"");//BYPASS PORT    	    
    	    
			// relay control log(명령) 등록	
    	    String[][] params = new String[2][2];
    	    params[0] = new String[]{"107.1.2", 0+""};//offset
    	    params[1] = new String[]{"107.1.3", 48+""};//count
    	    
    	    Map<String, String[][]> command = new HashMap<String, String[][]>();
    	    command.put("cmdOndemandMetering", params);	    
			BypassRegister bs = BypassRegister.getInstance();
			bs.add(modemSerial, command);

            log.info("Modem[" + modemSerial + "] SEND SMS End");
        }
        catch (Exception e) {}
        return;
    }

}
