package com.aimir.test.fep.pattern.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.aimir.fep.protocol.fmp.client.lan.LANClient;
import com.aimir.fep.protocol.fmp.common.LANTarget;
import com.aimir.fep.protocol.fmp.datatype.HEX;
import com.aimir.fep.protocol.fmp.datatype.OID;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.datatype.TIMESTAMP;
import com.aimir.fep.protocol.fmp.frame.ServiceDataConstants;
import com.aimir.fep.protocol.fmp.frame.service.EventData;
import com.aimir.fep.util.DataUtil;
import com.aimir.test.fep.generator.ValueGenerator;
import com.aimir.test.fep.util.TimeUtil;

public class TestModemMeterInstall {

	private static Log log = LogFactory.getLog(TestModemMeterInstall.class);
    
	private String mcuId = null;

	
	public static void main(String[] args) {
		
		TestModemMeterInstall test = new TestModemMeterInstall();
		test.test_EquipmentRegistration();
	}
	
	
    public void test_EquipmentRegistration() {

		int dcuCount = 1000;
		int nodeCount = 300;
		
    	try{
    		
        	for(int k = 0; k < dcuCount; k++){
        		
        		String dcuid = ValueGenerator.randombcdString(k, 5);
        		
                for(int i = 0; i < nodeCount; i++){

                    String eui = "000B12"+dcuid+ValueGenerator.randomTextString(i, 5);
                    
                    String value = dcuid+ValueGenerator.randombcdString(i, 8);
                    //byte[] code = value.getBytes();
                    //String meterid = Hex.decode(code)+"00000000000000";
                    String meterid = value;
                    //meterid = new String(Hex.encode(meterid));

            		LANTarget target = new LANTarget("187.1.10.28",8000);
            		target.setTargetId(dcuid);
            		LANClient client = new LANClient(target);  

            		EventData event = new EventData();
            		event.setCode(new OID("203.105.0"));
            		event.setSrcId(new HEX(eui));
            		event.setMcuId(dcuid);
            		event.setSvc((byte) 1);
            		event.setSrcType(ServiceDataConstants.E_SRCTYPE_ZRU);
            		event.setTimeStamp(new TIMESTAMP(TimeUtil.getCurrentTime()));
            		SMIValue smiValue = null;
            		smiValue = (SMIValue) DataUtil.getFMPVariable("","sensorID",eui); 
            		event.append(smiValue);
            		smiValue = (SMIValue) DataUtil.getFMPVariable("","sensorType", "1"); 
            		event.append(smiValue);
            		smiValue = (SMIValue) DataUtil.getFMPVariable("","mlpMid",meterid); 
            		event.append(smiValue);
            		smiValue = (SMIValue) DataUtil.getFMPVariable("","sensorServiceType","1"); 
            		event.append(smiValue);

            		client.sendEvent(event);
            	}
    			
    		}

        }catch(Exception ex) { ex.printStackTrace(); }
    }

}
